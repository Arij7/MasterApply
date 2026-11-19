const racine = document.getElementById('root');

function afficher(erreur) {
  racine.innerHTML = '';
  const bloc = document.createElement('pre');
  bloc.style.cssText = 'color:red;padding:20px;white-space:pre-wrap;font-size:14px';
  bloc.textContent = String((erreur && erreur.stack) || erreur);
  racine.appendChild(bloc);
}

window.addEventListener('error', (e) => afficher(e.error || e.message));
window.addEventListener('unhandledrejection', (e) => afficher(e.reason));

import('./demarrage.jsx').catch(afficher);