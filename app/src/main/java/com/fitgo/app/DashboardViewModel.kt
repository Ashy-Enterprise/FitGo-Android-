package com.fitgo.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    var profile by mutableStateOf(
        UserProfile(
            height = 175,
            weight = 78,
            age = 30,
            sex = "male",
            activity = "moderate",
            goal = "lose_fat",
            targetAreas = listOf("Core", "Arms", "Legs"),
            equipment = "bodyweight",
            time = 30
        )
    )
        private set

    var caloriesConsumed by mutableIntStateOf(1240)
        private set
    var protein by mutableIntStateOf(94)
        private set
    var carbs by mutableIntStateOf(128)
        private set
    var fat by mutableIntStateOf(42)
        private set
    var waterMl by mutableIntStateOf(1250)
        private set
    var steps by mutableIntStateOf(6842)
        private set
    var streak by mutableIntStateOf(12)
        private set

    val workout = mutableStateListOf<Exercise>()

    init {
        workout.addAll(WorkoutGenerator.generate(profile.targetAreas, profile.time))
    }

    val macros get() = Triple(protein, carbs, fat)

    val targets: Targets
        get() = computeTargets(profile)

    fun addWater(amount: Int = 250) {
        waterMl += amount
    }

    fun logFood(name: String, cals: Int) {
        caloriesConsumed += cals
        protein += (cals * 0.25 / 4).toInt()
        carbs += (cals * 0.45 / 4).toInt()
        fat += (cals * 0.30 / 9).toInt()
    }

    fun addSteps(extra: Int = 1000) {
        steps += extra
    }

    fun toggleExercise(index: Int) {
        if (index in workout.indices) {
            workout[index] = workout[index].copy(completed = !workout[index].completed)
        }
    }

    fun swapExercise(index: Int) {
        if (index in workout.indices) {
            val area = workout[index].area
            val list = WorkoutGenerator.generate(listOf(area), 10)
            val replacement = list.firstOrNull { it.name != workout[index].name } ?: list.first()
            workout[index] = replacement
        }
    }

    fun regenerateWorkout() {
        workout.clear()
        workout.addAll(WorkoutGenerator.generate(profile.targetAreas, profile.time))
    }

    fun toggleArea(area: String) {
        val current = profile.targetAreas.toMutableList()
        if (current.contains(area)) {
            current.remove(area)
        } else {
            current.add(area)
        }
        profile = profile.copy(targetAreas = current)
        regenerateWorkout()
    }

    companion object {
        fun computeTargets(profile: UserProfile): Targets {
            val s = if (profile.sex == "female") -161 else 5
            val bmr = (10 * profile.weight + 6.25 * profile.height - 5 * profile.age + s).toInt()
            val multiplier = when (profile.activity) {
                "sedentary" -> 1.2
                "light" -> 1.375
                "moderate" -> 1.55
                "active" -> 1.725
                "very" -> 1.9
                else -> 1.55
            }
            val tdee = (bmr * multiplier).toInt()
            val adjustment = when (profile.goal) {
                "lose_fat" -> -500
                "build_muscle" -> 300
                "maintain" -> 0
                "tone" -> -300
                else -> -500
            }
            val target = tdee + adjustment
            val proteinGrams = if (profile.goal == "build_muscle") profile.weight * 2.2 else profile.weight * 2.0
            val protein = proteinGrams.toInt()
            val fat = (target * 0.30 / 9).toInt()
            val carbs = ((target - protein * 4 - fat * 9) / 4).coerceAtLeast(50)
            return Targets(bmr, tdee, target, protein, carbs, fat)
        }
    }
}

data class UserProfile(
    val height: Int,
    val weight: Int,
    val age: Int,
    val sex: String,
    val activity: String,
    val goal: String,
    val targetAreas: List<String>,
    val equipment: String,
    val time: Int
)

data class Targets(
    val bmr: Int,
    val tdee: Int,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)
