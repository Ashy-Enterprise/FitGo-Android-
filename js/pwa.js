// Register FitGo service worker for offline/PWA support
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/service-worker.js')
      .then(reg => console.log('FitGo SW registered:', reg.scope))
      .catch(err => console.error('FitGo SW registration failed:', err));
  });
}
