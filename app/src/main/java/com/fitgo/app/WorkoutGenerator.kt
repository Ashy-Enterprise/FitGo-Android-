package com.fitgo.app

import kotlin.random.Random

data class Exercise(
    val name: String,
    val area: String,
    val sets: Int,
    val reps: String,
    val rest: String,
    var completed: Boolean = false
)

object WorkoutGenerator {

    private val exercises = mapOf(
        "Core" to listOf(
            Pair("Crunches", "15 reps"),
            Pair("Leg raises", "12 reps"),
            Pair("Plank", "45 sec"),
            Pair("Russian twists", "20 reps"),
            Pair("Mountain climbers", "30 sec")
        ),
        "Arms" to listOf(
            Pair("Push-ups", "12 reps"),
            Pair("Tricep dips", "12 reps"),
            Pair("Arm circles", "30 sec")
        ),
        "Legs" to listOf(
            Pair("Squats", "15 reps"),
            Pair("Lunges", "12 reps/leg"),
            Pair("Calf raises", "20 reps"),
            Pair("Jump squats", "12 reps"),
            Pair("Wall sit", "45 sec")
        ),
        "Glutes" to listOf(
            Pair("Glute bridges", "15 reps"),
            Pair("Donkey kicks", "15 reps/leg"),
            Pair("Fire hydrants", "15 reps/leg")
        ),
        "Chest" to listOf(
            Pair("Push-ups", "12 reps"),
            Pair("Incline push-ups", "12 reps")
        ),
        "Back" to listOf(
            Pair("Superman", "12 reps"),
            Pair("Reverse snow angels", "10 reps")
        )
    )

    fun generate(targetAreas: List<String>, durationMinutes: Int): List<Exercise> {
        val areas = targetAreas.ifEmpty { listOf("Core", "Arms", "Legs") }
        val count = when {
            durationMinutes < 20 -> 4
            durationMinutes < 40 -> 5
            else -> 6
        }
        val result = mutableListOf<Exercise>()
        repeat(count) { index ->
            val area = areas[index % areas.size]
            val pool = exercises[area] ?: exercises["Core"]!!
            val pick = pool[Random.nextInt(pool.size)]
            result.add(
                Exercise(
                    name = pick.first,
                    area = area,
                    sets = 3,
                    reps = pick.second,
                    rest = "45s"
                )
            )
        }
        return result
    }
}
