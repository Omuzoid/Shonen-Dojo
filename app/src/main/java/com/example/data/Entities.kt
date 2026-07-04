package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val xp: Int = 0,
    val gold: Int = 12450, // High starting balance like the screenshot
    val statPoints: Int = 0,
    val strength: Int = 78,
    val speed: Int = 84,
    val stamina: Int = 92,
    val intelligence: Int = 65,
    val vitality: Int = 88,
    val agility: Int = 90,
    val selectedMentor: String = "SUNG JIN-WOO",
    val equippedAura: String = "None",
    val purchasedAurasJson: String = "[\"None\"]",
    val companionName: String = "FLAME CUB",
    val companionLevel: Int = 12,
    val companionXp: Int = 1240,
    val activeRoadmap: String = "1-Week Induction",
    val activeRoadmapDay: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val calories: Int,
    val timestamp: Long = System.currentTimeMillis()
)
