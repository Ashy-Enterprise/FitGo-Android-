// FitGo dashboard interactivity

const CIRCUMFERENCE = 88;

const defaultProfile = {
  height: 175,
  weight: 78,
  age: 30,
  sex: 'male',
  activity: 'moderate',
  goal: 'lose_fat',
  targetAreas: ['abdomen', 'arms', 'legs'],
  equipment: 'bodyweight',
  time: 30,
};

const areaExercises = {
  abdomen: [
    { name: 'Crunches', reps: '15 reps' },
    { name: 'Leg raises', reps: '12 reps' },
    { name: 'Plank', reps: '45 sec' },
    { name: 'Russian twists', reps: '20 reps' },
    { name: 'Mountain climbers', reps: '30 sec' },
  ],
  arms: [
    { name: 'Push-ups', reps: '12 reps' },
    { name: 'Tricep dips', reps: '12 reps' },
    { name: 'Arm circles', reps: '30 sec' },
    { name: 'Bicep curls', reps: '12 reps', equipment: 'dumbbell' },
    { name: 'Shoulder press', reps: '10 reps', equipment: 'dumbbell' },
  ],
  legs: [
    { name: 'Squats', reps: '15 reps' },
    { name: 'Lunges', reps: '12 reps/leg' },
    { name: 'Calf raises', reps: '20 reps' },
    { name: 'Jump squats', reps: '12 reps' },
    { name: 'Wall sit', reps: '45 sec' },
  ],
  glutes: [
    { name: 'Glute bridges', reps: '15 reps' },
    { name: 'Donkey kicks', reps: '15 reps/leg' },
    { name: 'Fire hydrants', reps: '15 reps/leg' },
    { name: 'Hip thrusts', reps: '12 reps', equipment: 'dumbbell' },
  ],
  chest: [
    { name: 'Push-ups', reps: '12 reps' },
    { name: 'Chest dips', reps: '10 reps' },
    { name: 'Incline push-ups', reps: '12 reps' },
  ],
  back: [
    { name: 'Superman', reps: '12 reps' },
    { name: 'Reverse snow angels', reps: '10 reps' },
    { name: 'Pull-ups', reps: '8 reps', equipment: 'bar' },
    { name: 'Rows', reps: '12 reps', equipment: 'dumbbell' },
  ],
};

const areaLabels = {
  abdomen: 'Core',
  arms: 'Arms',
  legs: 'Legs',
  glutes: 'Glutes',
  chest: 'Chest',
  back: 'Back',
};

function loadState() {
  const saved = localStorage.getItem('fitgo_state');
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      if (parsed && parsed.profile) {
        parsed.targets = computeTargets(parsed.profile);
        return parsed;
      }
    } catch (e) {}
  }
  return createInitialState();
}

function createInitialState() {
  const profile = { ...defaultProfile };
  const targets = computeTargets(profile);
  return {
    profile,
    targets,
    today: {
      calories: 1240,
      protein: 94,
      carbs: 128,
      fat: 42,
      water: 1250,
      steps: 6842,
      distance: 4.8,
    },
    week: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      steps: [5200, 7100, 4800, 9200, 6842, 8100, 3500],
      calories: [2100, 2300, 1950, 2400, 2240, 2180, 1900],
    },
    workout: null,
    streak: 12,
  };
}

function saveState() {
  localStorage.setItem('fitgo_state', JSON.stringify(state));
}

function computeTargets(profile) {
  const { height, weight, age, sex, activity, goal } = profile;
  const s = sex === 'female' ? -161 : 5;
  const bmr = 10 * weight + 6.25 * height - 5 * age + s;
  const multipliers = {
    sedentary: 1.2,
    light: 1.375,
    moderate: 1.55,
    active: 1.725,
    very: 1.9,
  };
  const tdee = Math.round(bmr * (multipliers[activity] || 1.55));
  const goalAdjust = {
    lose_fat: -500,
    build_muscle: 300,
    maintain: 0,
    tone: -300,
  };
  const target = tdee + (goalAdjust[goal] || 0);
  const protein = Math.round((goal === 'build_muscle' ? weight * 2.2 : weight * 2.0) * 4);
  const fat = Math.round(target * 0.30);
  const carbs = Math.max(50, Math.round((target - protein - fat) / 4));
  return { bmr: Math.round(bmr), tdee, calories: target, protein: protein / 4, carbs, fat: fat / 9 };
}

let state = loadState();
if (!state.workout) {
  state.workout = generateWorkout(state.profile);
  saveState();
}

function generateWorkout(profile) {
  const { targetAreas, equipment, time } = profile;
  const areas = targetAreas.length ? targetAreas : ['abdomen', 'arms', 'legs', 'glutes'];
  const count = time < 20 ? 4 : time < 40 ? 5 : 6;
  const workout = [];
  for (let i = 0; i < count; i++) {
    const area = areas[i % areas.length];
    const candidates = areaExercises[area].filter(ex => !ex.equipment || ex.equipment === equipment || equipment === 'dumbbell');
    const pool = candidates.length ? candidates : areaExercises[area];
    const pick = pool[Math.floor(Math.random() * pool.length)];
    workout.push({
      id: i,
      name: pick.name,
      area,
      reps: pick.reps,
      sets: 3,
      rest: '45s',
      completed: false,
    });
  }
  return workout;
}

function format(n) {
  return n.toLocaleString();
}

function renderHeader() {
  const hour = new Date().getHours();
  const greeting = hour < 12 ? 'Good morning' : hour < 18 ? 'Good afternoon' : 'Good evening';
  document.getElementById('greeting').textContent = `${greeting}, Alex`;
}

function renderCalories() {
  const target = state.targets.calories;
  const eaten = state.today.calories;
  const remaining = Math.max(0, target - eaten);
  const pct = Math.min(100, Math.round((eaten / target) * 100));
  document.getElementById('calRemaining').textContent = format(remaining);
  document.getElementById('calSummary').textContent = `${format(eaten)} eaten / ${format(target)} target`;
  document.getElementById('calPercent').textContent = `${pct}%`;
  const ring = document.getElementById('calRing');
  const offset = CIRCUMFERENCE - (pct / 100) * CIRCUMFERENCE;
  ring.style.strokeDashoffset = offset;
}

function renderMacros() {
  const t = state.targets;
  const d = state.today;
  const set = (val, target, barId, textId) => {
    const pct = Math.min(100, Math.round((val / target) * 100));
    document.getElementById(barId).style.width = `${pct}%`;
    document.getElementById(textId).textContent = `${Math.round(val)} / ${Math.round(target)}g`;
  };
  set(d.protein, t.protein, 'proteinBar', 'proteinVal');
  set(d.carbs, t.carbs, 'carbBar', 'carbVal');
  set(d.fat, t.fat, 'fatBar', 'fatVal');
}

function renderSteps() {
  const goal = 10000;
  const steps = state.today.steps;
  const pct = Math.min(100, Math.round((steps / goal) * 100));
  document.getElementById('stepsVal').textContent = format(steps);
  document.getElementById('stepsGoal').textContent = format(goal);
  document.getElementById('stepsBar').style.width = `${pct}%`;
}

function renderWater() {
  const goal = 2500;
  const water = state.today.water;
  const pct = Math.min(100, Math.round((water / goal) * 100));
  document.getElementById('waterVal').textContent = `${format(water)} ml`;
  document.getElementById('waterBar').style.width = `${pct}%`;
}

function renderMetrics() {
  document.getElementById('bmrVal').textContent = format(state.targets.bmr);
  document.getElementById('tdeeVal').textContent = format(state.targets.tdee);
  const bmi = (state.profile.weight / Math.pow(state.profile.height / 100, 2)).toFixed(1);
  document.getElementById('bmiVal').textContent = bmi;
  document.getElementById('targetText').textContent = `Calorie target: ${format(state.targets.calories)} kcal/day for ${state.profile.goal.replace('_', ' ')}.`;
}

function renderWorkout() {
  const chips = document.getElementById('targetChips');
  const list = document.getElementById('workoutList');
  chips.innerHTML = '';
  list.innerHTML = '';

  Object.keys(areaLabels).forEach(area => {
    const active = state.profile.targetAreas.includes(area);
    const btn = document.createElement('button');
    btn.className = `rounded-full px-3 py-1 text-xs font-semibold border transition ${active ? 'bg-emerald-500/10 border-emerald-500/40 text-emerald-300' : 'border-slate-700 text-slate-400 hover:border-slate-500'}`;
    btn.textContent = areaLabels[area];
    btn.addEventListener('click', () => toggleArea(area));
    chips.appendChild(btn);
  });

  const completed = state.workout.filter(e => e.completed).length;
  document.getElementById('workoutMeta').textContent = `${state.workout.length} exercises • ${state.profile.time} min • ${completed}/${state.workout.length} completed`;

  state.workout.forEach(ex => {
    const item = document.createElement('div');
    item.className = `flex items-center justify-between rounded-xl border p-4 transition ${ex.completed ? 'bg-emerald-500/10 border-emerald-500/30' : 'bg-slate-900/40 border-slate-800'}`;
    item.innerHTML = `
      <div class="flex items-center gap-4">
        <button class="swapBtn flex h-8 w-8 items-center justify-center rounded-lg border border-slate-700 text-slate-400 hover:border-emerald-500/50 hover:text-emerald-400" title="Swap" data-id="${ex.id}">
          <i data-lucide="shuffle" class="h-4 w-4"></i>
        </button>
        <div>
          <p class="font-semibold ${ex.completed ? 'line-through text-slate-500' : 'text-slate-100'}">${ex.name}</p>
          <p class="text-xs text-slate-400">${ex.sets} sets • ${ex.reps} • ${ex.rest} rest • ${areaLabels[ex.area]}</p>
        </div>
      </div>
      <button class="doneBtn rounded-lg px-3 py-2 text-xs font-bold transition ${ex.completed ? 'bg-emerald-500 text-slate-950' : 'border border-slate-700 text-slate-300 hover:border-emerald-500/50'}" data-id="${ex.id}">
        ${ex.completed ? 'Done' : 'Mark'}
      </button>
    `;
    list.appendChild(item);
  });

  list.querySelectorAll('.doneBtn').forEach(btn => {
    btn.addEventListener('click', () => toggleExercise(parseInt(btn.dataset.id)));
  });
  list.querySelectorAll('.swapBtn').forEach(btn => {
    btn.addEventListener('click', () => swapExercise(parseInt(btn.dataset.id)));
  });

  lucide.createIcons();
}

function toggleArea(area) {
  const areas = state.profile.targetAreas;
  if (areas.includes(area)) {
    state.profile.targetAreas = areas.filter(a => a !== area);
  } else {
    state.profile.targetAreas = [...areas, area];
  }
  state.workout = generateWorkout(state.profile);
  saveState();
  renderWorkout();
}

function toggleExercise(id) {
  const ex = state.workout.find(e => e.id === id);
  if (ex) {
    ex.completed = !ex.completed;
    saveState();
    renderWorkout();
  }
}

function swapExercise(id) {
  const ex = state.workout.find(e => e.id === id);
  if (!ex) return;
  const pool = areaExercises[ex.area].filter(p => p.name !== ex.name && (!p.equipment || p.equipment === state.profile.equipment || state.profile.equipment === 'dumbbell'));
  const pick = pool.length ? pool[Math.floor(Math.random() * pool.length)] : areaExercises[ex.area][0];
  ex.name = pick.name;
  ex.reps = pick.reps;
  ex.completed = false;
  saveState();
  renderWorkout();
}

function renderRecentLogs() {
  const container = document.getElementById('recentLogs');
  container.innerHTML = '';
  const logs = state.recentLogs || [];
  if (!logs.length) {
    container.innerHTML = '<p class="text-xs text-slate-500">No recent entries.</p>';
    return;
  }
  logs.slice(-4).reverse().forEach(log => {
    const row = document.createElement('div');
    row.className = 'flex items-center justify-between rounded-lg bg-slate-900/40 px-3 py-2 text-sm';
    row.innerHTML = `<span class="text-slate-300">${log.name}</span><span class="font-semibold text-emerald-400">+${log.calories}</span>`;
    container.appendChild(row);
  });
}

let chartInstance = null;

function initChart() {
  const ctx = document.getElementById('activityChart').getContext('2d');
  const metric = document.getElementById('chartMetric').value || 'steps';
  const data = state.week[metric];
  const color = metric === 'steps' ? '#34d399' : '#fb923c';

  chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: state.week.labels,
      datasets: [{
        label: metric === 'steps' ? 'Steps' : 'Calories',
        data,
        borderColor: color,
        backgroundColor: color + '20',
        fill: true,
        tension: 0.4,
        pointRadius: 4,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false, drawBorder: false }, ticks: { color: '#94a3b8' } },
        y: { grid: { color: '#1e293b' }, ticks: { color: '#94a3b8' } },
      },
    },
  });

  document.getElementById('chartMetric').addEventListener('change', () => {
    const m = document.getElementById('chartMetric').value;
    chartInstance.data.datasets[0].label = m === 'steps' ? 'Steps' : 'Calories';
    chartInstance.data.datasets[0].data = state.week[m];
    chartInstance.data.datasets[0].borderColor = m === 'steps' ? '#34d399' : '#fb923c';
    chartInstance.data.datasets[0].backgroundColor = (m === 'steps' ? '#34d399' : '#fb923c') + '20';
    chartInstance.update();
  });
}

function updateChartToday() {
  const lastIdx = state.week.steps.length - 1;
  state.week.steps[lastIdx] = state.today.steps;
  state.week.calories[lastIdx] = state.today.calories;
  if (chartInstance) {
    const m = document.getElementById('chartMetric').value;
    chartInstance.data.datasets[0].data = state.week[m];
    chartInstance.update();
  }
}

function bindEvents() {
  document.getElementById('addWater').addEventListener('click', () => {
    state.today.water = Math.min(5000, state.today.water + 250);
    saveState();
    renderWater();
  });

  document.getElementById('regenerateBtn').addEventListener('click', () => {
    state.workout = generateWorkout(state.profile);
    saveState();
    renderWorkout();
  });

  document.getElementById('completeAllBtn').addEventListener('click', () => {
    const allDone = state.workout.every(e => e.completed);
    state.workout.forEach(e => e.completed = !allDone);
    saveState();
    renderWorkout();
  });

  document.getElementById('logForm').addEventListener('submit', e => {
    e.preventDefault();
    const name = document.getElementById('foodName').value.trim();
    const cals = parseInt(document.getElementById('foodCal').value, 10);
    if (!name || !cals) return;
    state.today.calories += cals;
    // default macro split: 25% protein, 45% carbs, 30% fat
    state.today.protein += (cals * 0.25) / 4;
    state.today.carbs += (cals * 0.45) / 4;
    state.today.fat += (cals * 0.30) / 9;
    state.recentLogs = state.recentLogs || [];
    state.recentLogs.push({ name, calories: cals, time: Date.now() });
    document.getElementById('logForm').reset();
    saveState();
    renderCalories();
    renderMacros();
    renderRecentLogs();
    updateChartToday();
  });

  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('sidebarOverlay');
  const toggle = document.getElementById('sidebarToggle');
  toggle.addEventListener('click', () => {
    sidebar.classList.toggle('-translate-x-full');
    sidebar.classList.toggle('translate-x-0');
    overlay.classList.toggle('hidden');
  });
  overlay.addEventListener('click', () => {
    sidebar.classList.add('-translate-x-full');
    sidebar.classList.remove('translate-x-0');
    overlay.classList.add('hidden');
  });
}

function init() {
  lucide.createIcons();
  renderHeader();
  renderCalories();
  renderMacros();
  renderSteps();
  renderWater();
  renderMetrics();
  renderWorkout();
  renderRecentLogs();
  initChart();
  bindEvents();
}

init();
