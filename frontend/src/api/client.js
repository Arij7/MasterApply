import axios from 'axios';

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api' });

// Ajoute le jeton JWT à chaque requête
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Si le jeton est refusé (expiré ou invalide), on déconnecte l'utilisateur
api.interceptors.response.use(
  (reponse) => reponse,
  (erreur) => {
    const url = erreur.config?.url || '';
    if (erreur.response?.status === 401 && !url.startsWith('/auth/')) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(erreur);
  }
);

// Extrait le message JSON renvoyé par le backend
export function messageErreur(erreur) {
  if (erreur.response?.data?.message) return erreur.response.data.message;
  if (erreur.request) {
    return 'Impossible de joindre le serveur. Vérifiez que le backend est démarré.';
  }
  return "Une erreur inattendue s'est produite.";
}

// Télécharge un fichier protégé (reçu PDF, document) en envoyant le jeton JWT
export async function telechargerFichier(url, nom) {
  const { data } = await api.get(url, { responseType: 'blob' });
  const lienFichier = URL.createObjectURL(data);
  const lien = document.createElement('a');
  lien.href = lienFichier;
  lien.download = nom;
  document.body.appendChild(lien);
  lien.click();
  lien.remove();
  URL.revokeObjectURL(lienFichier);
}

export default api;