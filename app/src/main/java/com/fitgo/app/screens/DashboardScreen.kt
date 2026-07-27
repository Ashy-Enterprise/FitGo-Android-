@file:OptIn(ExperimentalMaterial3Api::class)

package com.fitgo.app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitgo.app.DashboardViewModel
import com.fitgo.app.Exercise
import com.fitgo.app.ui.theme.Cyan400
import com.fitgo.app.ui.theme.Emerald300
import com.fitgo.app.ui.theme.Emerald500
import com.fitgo.app.ui.theme.Orange400
import com.fitgo.app.ui.theme.Slate700
import com.fitgo.app.ui.theme.Slate800
import com.fitgo.app.ui.theme.Slate900
import com.fitgo.app.ui.theme.Slate950
import com.fitgo.app.ui.theme.Teal500
import com.fitgo.app.ui.theme.White
import kotlin.math.min

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val scrollState = rememberScrollState()
    val targets = viewModel.targets

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Good afternoon, Alex", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Slate950,
                    titleContentColor = White
                ),
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                }
            )
        },
        containerColor = Slate950
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCards(viewModel, targets)

            MacrosCard(viewModel, targets)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StepsCard(viewModel, modifier = Modifier.weight(1f))
                WaterCard(viewModel, modifier = Modifier.weight(1f))
            }

            WorkoutPlanCard(viewModel)

            ActivityChartCard()

            QuickLogCard(viewModel)

            BodyMetricsCard(viewModel, targets)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryCards(viewModel: DashboardViewModel, targets: com.fitgo.app.Targets) {
    val percent = min(1f, viewModel.caloriesConsumed.toFloat() / targets.calories)
    val remaining = (targets.calories - viewModel.caloriesConsumed).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Calories remaining", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$remaining", color = White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${viewModel.caloriesConsumed} eaten / ${targets.calories} target", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 10.dp.toPx()
                    val diameter = min(size.width, size.height) - stroke
                    val topLeft = Offset(stroke / 2, stroke / 2)
                    val arcSize = Size(diameter, diameter)
                    drawArc(
                        color = Slate700,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke)
                    )
                    drawArc(
                        color = Emerald500,
                        startAngle = -90f,
                        sweepAngle = 360f * percent,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                Text("${(percent * 100).toInt()}%", color = Emerald500, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MacrosCard(viewModel: DashboardViewModel, targets: com.fitgo.app.Targets) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Macros today", color = White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            MacroBar("Protein", viewModel.protein, targets.protein, Emerald500)
            MacroBar("Carbs", viewModel.carbs, targets.carbs, Emerald300)
            MacroBar("Fat", viewModel.fat, targets.fat, Orange400)
        }
    }
}

@Composable
private fun MacroBar(label: String, value: Int, target: Int, color: Color) {
    val fraction = min(1f, value.toFloat() / target)
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = White, fontSize = 13.sp)
            Text("$value / ${target}g", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Slate800)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun StepsCard(viewModel: DashboardViewModel, modifier: Modifier) {
    val goal = 10000
    val fraction = min(1f, viewModel.steps.toFloat() / goal)
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Steps", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${viewModel.steps}", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Slate800)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Emerald500)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Goal: $goal", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
private fun WaterCard(viewModel: DashboardViewModel, modifier: Modifier) {
    val goal = 2500
    val fraction = min(1f, viewModel.waterMl.toFloat() / goal)
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Water", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${viewModel.waterMl} ml", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Goal: $goal", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            IconButton(
                onClick = { viewModel.addWater() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Cyan400.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add water", tint = Cyan400)
            }
        }
    }
}

@Composable
private fun WorkoutPlanCard(viewModel: DashboardViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Today's workout", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    val completed = viewModel.workout.count { it.completed }
                    Text(
                        "${viewModel.workout.size} exercises • ${viewModel.profile.time} min • $completed/${viewModel.workout.size} completed",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                Row {
                    IconButton(onClick = { viewModel.regenerateWorkout() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = White)
                    }
                    IconButton(onClick = {
                        viewModel.workout.forEachIndexed { i, ex ->
                            viewModel.workout[i] = ex.copy(completed = !viewModel.workout.all { it.completed })
                        }
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Toggle all", tint = White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val areas = listOf("Core", "Arms", "Legs", "Glutes", "Chest", "Back")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                areas.forEach { area ->
                    FilterChip(
                        selected = viewModel.profile.targetAreas.contains(area),
                        onClick = { viewModel.toggleArea(area) },
                        label = { Text(area, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500.copy(alpha = 0.2f),
                            selectedLabelColor = Emerald500,
                            containerColor = Slate800,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            viewModel.workout.forEachIndexed { index, exercise ->
                ExerciseRow(
                    exercise = exercise,
                    onToggle = { viewModel.toggleExercise(index) },
                    onSwap = { viewModel.swapExercise(index) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseRow(exercise: Exercise, onToggle: () -> Unit, onSwap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (exercise.completed) Emerald500.copy(alpha = 0.12f) else Slate800)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onSwap, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Shuffle, contentDescription = "Swap", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column {
                Text(
                    text = exercise.name,
                    color = if (exercise.completed) MaterialTheme.colorScheme.onSurfaceVariant else White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${exercise.sets} sets • ${exercise.reps} • ${exercise.rest} rest • ${exercise.area}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
        Button(
            onClick = onToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (exercise.completed) Emerald500 else Slate700,
                contentColor = if (exercise.completed) Slate950 else White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(if (exercise.completed) "Done" else "Mark", fontSize = 12.sp)
        }
    }
}

@Composable
private fun ActivityChartCard() {
    val stepsData = listOf(5200f, 7100f, 4800f, 9200f, 6842f, 8100f, 3500f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Activity", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val max = stepsData.maxOrNull() ?: 1f
                    val min = stepsData.minOrNull() ?: 0f
                    val range = max - min
                    val stepX = size.width / (stepsData.size - 1)
                    val points = stepsData.mapIndexed { i, v ->
                        val x = i * stepX
                        val y = size.height - ((v - min) / range) * (size.height - 40) - 20
                        Offset(x, y)
                    }

                    for (i in 0 until points.lastIndex) {
                        drawLine(
                            color = Emerald300,
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    points.forEach { point ->
                        drawCircle(color = Emerald500, radius = 5.dp.toPx(), center = point)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                    Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun QuickLogCard(viewModel: DashboardViewModel) {
    var name by remember { mutableStateOf("") }
    var cals by remember { mutableStateOf("") }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Quick log", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Add a food or drink entry.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food name") },
                    modifier = Modifier.weight(2f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = cals,
                    onValueChange = { cals = it },
                    label = { Text("Cal") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        val calInt = cals.toIntOrNull()
                        if (name.isNotBlank() && calInt != null) {
                            viewModel.logFood(name, calInt)
                            name = ""
                            cals = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Log", color = Slate950, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BodyMetricsCard(viewModel: DashboardViewModel, targets: com.fitgo.app.Targets) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Body metrics", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("BMR & TDEE from your profile.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricTile("BMR", targets.bmr.toString(), Modifier.weight(1f))
                MetricTile("TDEE", targets.tdee.toString(), Modifier.weight(1f))
                MetricTile("BMI", viewModel.bmi(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MetricTile(label: String, value: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Slate800)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

private fun DashboardViewModel.bmi(): String {
    val hM = profile.height / 100.0
    return String.format("%.1f", profile.weight / (hM * hM))
}
