// FitGo landing page interactions

lucide.createIcons();

const menuBtn = document.getElementById('menuBtn');
const mobileMenu = document.getElementById('mobileMenu');

if (menuBtn && mobileMenu) {
  menuBtn.addEventListener('click', () => {
    mobileMenu.classList.toggle('hidden');
  });
  mobileMenu.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => mobileMenu.classList.add('hidden'));
  });
}

// Pricing toggle
const monthlyBtn = document.getElementById('monthlyBtn');
const annualBtn = document.getElementById('annualBtn');
const monthlyPrice = document.getElementById('monthlyPrice');
const annualPrice = document.getElementById('annualPrice');

if (monthlyBtn && annualBtn) {
  monthlyBtn.addEventListener('click', () => {
    monthlyBtn.classList.add('bg-emerald-500', 'text-slate-950');
    monthlyBtn.classList.remove('text-slate-300');
    annualBtn.classList.remove('bg-emerald-500', 'text-slate-950');
    annualBtn.classList.add('text-slate-300');
    if (monthlyPrice) monthlyPrice.textContent = '9.99';
    if (annualPrice) annualPrice.textContent = '79.99';
  });
  annualBtn.addEventListener('click', () => {
    annualBtn.classList.add('bg-emerald-500', 'text-slate-950');
    annualBtn.classList.remove('text-slate-300');
    monthlyBtn.classList.remove('bg-emerald-500', 'text-slate-950');
    monthlyBtn.classList.add('text-slate-300');
    if (monthlyPrice) monthlyPrice.textContent = '6.67';
    if (annualPrice) annualPrice.textContent = '79.99';
  });
}

// Reveal animations on scroll
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) entry.target.classList.add('visible');
  });
}, { threshold: 0.1 });

document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
