import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api, { messageErreur } from '../api/client';

export default function NouvelleCandidature() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const [masters, setMasters] = useState([]);
  const [masterId, setMasterId] = useState(params.get('master') || '');
  const [motivation, setMotivation] = useState('');
  const [erreur, setErreur] = useState('');
  const [envoi, setEnvoi] = useState(false);

  useEffect(() => {
    api.get('/masters')
      .then(({ data }) => setMasters(data))
      .catch((e) => setErreur(messageErreur(e)));
  }, []);

  const soumettre = async (e) => {
    e.preventDefault();
    setErreur('');
    setEnvoi(true);
    try {
      await api.post('/candidatures', { masterId: Number(masterId), motivation });
      navigate('/candidat');
    } catch (err) {
      setErreur(messageErreur(err));
    } finally {
      setEnvoi(false);
    }
  };

  return (
    <div className="formulaire">
      <h1>Nouvelle candidature</h1>
      <p className="discret">
        Le dossier est créé en brouillon. Vous ajouterez les documents puis vous le soumettrez depuis votre espace.
      </p>
      <form onSubmit={soumettre}>
        <label>Master
          <select value={masterId} onChange={(e) => setMasterId(e.target.value)} required>
            <option value="">Choisir un Master</option>
            {masters.map((m) => <option key={m.id} value={m.id}>{m.intitule}</option>)}
          </select>
        </label>
        <label>Lettre de motivation
          <textarea rows={6} maxLength={2000} value={motivation} onChange={(e) => setMotivation(e.target.value)} />
        </label>
        {erreur && <p className="erreur">{erreur}</p>}
        <button className="btn" type="submit" disabled={envoi}>
          {envoi ? 'Création...' : 'Créer le brouillon'}
        </button>
      </form>
    </div>
  );
}