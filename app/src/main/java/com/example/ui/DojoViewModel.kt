package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SynthAudioPlayer
import com.example.data.DojoDatabase
import com.example.data.UserStats
import com.example.data.WorkoutLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DojoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DojoDatabase.getDatabase(application)
    private val dao = db.dojoDao()
    private val audioPlayer = SynthAudioPlayer()

    // Screen navigation state
    var activeTab by mutableStateOf("Dojo") // Dojo, Roadmap, Train, Companion, Store
    var isDayMode by mutableStateOf(false) // default is majestic dark theme night mode
    
    // Vocal subtitle state
    var activeMentorQuote by mutableStateOf("Welcome to the Dojo. Prepare to exceed your absolute limits.")
    var activeWordRange by mutableStateOf(Pair(-1, -1)) // (startChar, endChar) for karaoke highlighter
    var isSpeaking by mutableStateOf(false)

    // Active workout tracking
    var isWorkoutActive by mutableStateOf(false)
    var currentExerciseName by mutableStateOf("Push-Ups")
    var currentExerciseSet by mutableStateOf(1)
    var totalSetsRequired by mutableStateOf(4)
    var repsCompleted by mutableStateOf(0)
    var repsRequired by mutableStateOf(20)
    var caloriesBurned by mutableStateOf(0)
    var restTimerSeconds by mutableStateOf(0)
    var isResting by mutableStateOf(false)

    // TTS Engine
    private var tts: TextToSpeech? = null

    // UI flows from Room Database
    val userStats: StateFlow<UserStats?> = dao.getUserStatsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val workoutLogs: StateFlow<List<WorkoutLog>> = dao.getAllWorkoutLogsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Initialize default user stats row if it doesn't exist
        viewModelScope.launch {
            val stats = dao.getUserStatsDirect()
            if (stats == null) {
                dao.insertOrUpdateUserStats(UserStats())
            }
            
            // Check and populate historical logs if empty
            val existingLogs = dao.getAllWorkoutLogsFlow().first()
            if (existingLogs.isEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val workouts = listOf(
                    "Push-Ups" to Pair(4, 20),
                    "Squats" to Pair(4, 20),
                    "Sit-Ups" to Pair(3, 15),
                    "Plank" to Pair(3, 60),
                    "Running" to Pair(1, 15)
                )
                
                for (offset in 1..6) {
                    val cal = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -offset)
                    }
                    val dateStr = sdf.format(cal.time)
                    val timestamp = cal.timeInMillis
                    
                    val w1 = workouts[offset % workouts.size]
                    val w2 = workouts[(offset + 2) % workouts.size]
                    
                    dao.insertWorkoutLog(
                        WorkoutLog(
                            date = dateStr,
                            exerciseName = w1.first,
                            sets = w1.second.first,
                            reps = w1.second.second,
                            calories = w1.second.first * w1.second.second,
                            timestamp = timestamp
                        )
                    )
                    
                    if (offset % 2 == 0) {
                        dao.insertWorkoutLog(
                            WorkoutLog(
                                date = dateStr,
                                exerciseName = w2.first,
                                sets = w2.second.first,
                                reps = w2.second.second,
                                calories = w2.second.first * w2.second.second,
                                timestamp = timestamp + 1000
                            )
                        )
                    }
                }
            }
        }

        // Initialize TextToSpeech
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(0.95f) // slightly slower for anime dramatic effect
                setupTtsListener()
            }
        }
    }

    private fun setupTtsListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                activeWordRange = Pair(-1, -1)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isSpeaking = false
                activeWordRange = Pair(-1, -1)
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                activeWordRange = Pair(start, end)
            }
        })
    }

    fun playClickSound() {
        audioPlayer.playClick()
    }

    fun playLevelUpSound() {
        audioPlayer.playLevelUp()
    }

    fun triggerPenaltyEvent() {
        audioPlayer.playPenaltyAlarm()
        speakPhrase("Warning! Penalty quest has been activated. Complete your exercises now or face the dungeon!")
    }

    fun speakPhrase(phrase: String) {
        activeMentorQuote = phrase
        activeWordRange = Pair(-1, -1)
        val params = android.os.Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "mentor_vocal_cue")
        }
        tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, params, "mentor_vocal_cue")
    }

    fun setMentor(mentorName: String) {
        viewModelScope.launch {
            val current = dao.getUserStatsDirect() ?: UserStats()
            dao.insertOrUpdateUserStats(current.copy(selectedMentor = mentorName))
            playClickSound()
            val introQuote = when (mentorName) {
                "SUNG JIN-WOO" -> "I will protect those who are weak. Walk with me into the shadows."
                "GOKU" -> "Alright! Let's get training! I'm excited to see how strong you get!"
                "SAITAMA" -> "Just do it. 100 push-ups and squats. Everyday. Don't quit."
                "ALL MIGHT" -> "Do not fear, young trainee, because I AM HERE! PLUS ULTRA!"
                "ASH KETCHUM" -> "We can level up together! I choose you, let's go!"
                else -> "Prepare for extreme transformation."
            }
            speakPhrase(introQuote)
        }
    }

    // Allocate Stat Points
    fun allocateStatPoint(statType: String) {
        viewModelScope.launch {
            val current = dao.getUserStatsDirect() ?: return@launch
            if (current.statPoints > 0) {
                val updated = when (statType) {
                    "STRENGTH" -> current.copy(strength = current.strength + 1, statPoints = current.statPoints - 1)
                    "SPEED" -> current.copy(speed = current.speed + 1, statPoints = current.statPoints - 1)
                    "STAMINA" -> current.copy(stamina = current.stamina + 1, statPoints = current.statPoints - 1)
                    "INTELLIGENCE" -> current.copy(intelligence = current.intelligence + 1, statPoints = current.statPoints - 1)
                    "VITALITY" -> current.copy(vitality = current.vitality + 1, statPoints = current.statPoints - 1)
                    "AGILITY" -> current.copy(agility = current.agility + 1, statPoints = current.statPoints - 1)
                    else -> current
                }
                dao.insertOrUpdateUserStats(updated)
                playClickSound()
            }
        }
    }

    // Purchase Aura from Store
    fun purchaseAura(auraName: String, price: Int) {
        viewModelScope.launch {
            val current = dao.getUserStatsDirect() ?: return@launch
            val purchasedArray = JSONArray(current.purchasedAurasJson)
            
            // Check if already purchased
            var alreadyPurchased = false
            for (i in 0 until purchasedArray.length()) {
                if (purchasedArray.getString(i) == auraName) {
                    alreadyPurchased = true
                    break
                }
            }

            if (!alreadyPurchased && current.gold >= price) {
                purchasedArray.put(auraName)
                val updated = current.copy(
                    gold = current.gold - price,
                    purchasedAurasJson = purchasedArray.toString(),
                    equippedAura = auraName
                )
                dao.insertOrUpdateUserStats(updated)
                playLevelUpSound()
                speakPhrase("Excellent choice! You purchased the legendary $auraName. It is now equipped.")
            } else if (alreadyPurchased) {
                // Toggle Equip
                val updated = current.copy(equippedAura = auraName)
                dao.insertOrUpdateUserStats(updated)
                playClickSound()
                speakPhrase("$auraName equipped.")
            } else {
                speakPhrase("Inadequate gold! Complete more dungeon runs to afford this aura.")
            }
        }
    }

    // Select/Change Roadmaps
    fun selectRoadmap(roadmapName: String) {
        viewModelScope.launch {
            val current = dao.getUserStatsDirect() ?: return@launch
            val updated = current.copy(activeRoadmap = roadmapName, activeRoadmapDay = 1)
            dao.insertOrUpdateUserStats(updated)
            playClickSound()
            val roadmapQuote = when (roadmapName) {
                "1-Week Induction" -> "Foundation conditioning selected. Let's build your baseline."
                "4-Week Awakening" -> "Awakening hypertrophy track selected. High density training ahead."
                "12-Week Dungeon Raid" -> "Dungeon Raid selected! This is where limits are shattered."
                "100-Day System Protocol" -> "The Saitama absolute protocol selected. Do not fail a single day!"
                "365-Day Monarch Ascent" -> "Year-long Monarch Ascent selected. Consistency is the true power."
                else -> "Track updated."
            }
            speakPhrase(roadmapQuote)
            activeTab = "Dojo" // bring them back to Dojo to start
        }
    }

    // Active Workout Flow Controls
    fun startWorkout(exerciseName: String) {
        playClickSound()
        currentExerciseName = exerciseName
        isWorkoutActive = true
        currentExerciseSet = 1
        totalSetsRequired = 4
        repsCompleted = 0
        repsRequired = when (exerciseName) {
            "Push-Ups" -> 20
            "Squats" -> 20
            "Sit-Ups" -> 20
            "Plank" -> 60 // seconds
            "Running" -> 1 // 1 min simulation
            else -> 15
        }
        caloriesBurned = 0
        isResting = false
        activeTab = "Train" // navigate to training center tab

        val mentorQuote = when (userStats.value?.selectedMentor) {
            "SUNG JIN-WOO" -> "Begin $exerciseName. Show me your focus."
            "GOKU" -> "Let's go! Let's power through this first set of $exerciseName!"
            "SAITAMA" -> "Start $exerciseName. Keep good form and don't stop."
            "ALL MIGHT" -> "Now is the time! Give your $exerciseName everything you've got! Plus Ultra!"
            "ASH KETCHUM" -> "Alright, $exerciseName! Let's show everyone our strength!"
            else -> "Begin training."
        }
        speakPhrase(mentorQuote)
    }

    fun completeActiveRep() {
        if (!isWorkoutActive || isResting) return
        
        if (currentExerciseName == "Plank" || currentExerciseName == "Running") {
            // Instant complete set for timed exercises
            completeActiveSet()
            return
        }

        repsCompleted++
        caloriesBurned += 1 // 1 calorie per rep for simpler visual tracking
        playClickSound()

        if (repsCompleted >= repsRequired) {
            completeActiveSet()
        }
    }

    private fun completeActiveSet() {
        if (currentExerciseSet < totalSetsRequired) {
            isResting = true
            restTimerSeconds = 15
            viewModelScope.launch {
                speakPhrase("Set $currentExerciseSet complete! Take a brief 15 second rest.")
                while (restTimerSeconds > 0 && isResting) {
                    delay(1000)
                    restTimerSeconds--
                }
                if (isResting) {
                    isResting = false
                    currentExerciseSet++
                    repsCompleted = 0
                    speakPhrase("Rest finished. Begin Set $currentExerciseSet!")
                }
            }
        } else {
            // Workout finished completely!
            finishWorkout()
        }
    }

    fun skipRest() {
        if (isResting) {
            isResting = false
            currentExerciseSet++
            repsCompleted = 0
            playClickSound()
            speakPhrase("Rest skipped. Begin Set $currentExerciseSet!")
        }
    }

    private fun finishWorkout() {
        isWorkoutActive = false
        val earnedXp = 320
        val earnedGold = 150
        
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val log = WorkoutLog(
                date = dateStr,
                exerciseName = currentExerciseName,
                sets = totalSetsRequired,
                reps = repsRequired,
                calories = totalSetsRequired * repsRequired
            )
            dao.insertWorkoutLog(log)

            val current = dao.getUserStatsDirect() ?: return@launch
            
            // Add rewards and compute XP level up
            var newXp = current.xp + earnedXp
            var newLevel = current.level
            var totalXpRequired = current.level * 2500 // XP curve
            var leveledUp = false
            var addedStatPoints = current.statPoints

            while (newXp >= totalXpRequired) {
                newXp -= totalXpRequired
                newLevel++
                addedStatPoints += 5 // 5 stat points per level!
                totalXpRequired = newLevel * 2500
                leveledUp = true
            }

            // Companion XP gains
            var compLevel = current.companionLevel
            var compXp = current.companionXp + 450
            val compXpRequired = compLevel * 180
            var compLeveledUp = false
            while (compXp >= compXpRequired) {
                compXp -= compXpRequired
                compLevel++
                compLeveledUp = true
            }

            // Move active roadmap day forward
            val currentDay = current.activeRoadmapDay
            val updatedDay = currentDay + 1

            val updated = current.copy(
                level = newLevel,
                xp = newXp,
                gold = current.gold + earnedGold,
                statPoints = addedStatPoints,
                companionLevel = compLevel,
                companionXp = compXp,
                activeRoadmapDay = updatedDay
            )
            dao.insertOrUpdateUserStats(updated)

            // Audio cues
            if (leveledUp) {
                playLevelUpSound()
                speakPhrase("CONGRATULATIONS! You leveled up to Level $newLevel! You earned 5 Stat Points. Allocate them in your Shadow Stats panel.")
            } else if (compLeveledUp) {
                playLevelUpSound()
                speakPhrase("Your companion companion leveled up! Flame Cub is growing stronger.")
            } else {
                playClickSound()
                speakPhrase("Workout complete! Dungeon raid successful. Earned $earnedXp XP and $earnedGold Gold Coins.")
            }
            activeTab = "Dojo" // go back to Dojo
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}
