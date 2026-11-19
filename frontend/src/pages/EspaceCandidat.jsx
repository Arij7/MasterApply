import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api, { messageErreur, telechargerFichier } from '../api/client';
import CandidatureCard from '../components/CandidatureCard';
import { useAuth } from '../context/AuthContext';

export default function EspaceCandidat() {
  const { user } = useAuth();
  const [candidatures, setCandidatures] = useState([]);
  const [inscription, setInscription] = useState(null);
  const [recus, setRecus] = useState([]);
  const [erreur, setErreur] = useState('');
  const [chargement, setChargement] = useState(true);

  const charger = useCallback(async () => {
    try {
      const [c, i, r] = await Promise.all([
        api.get('/candidatures'),
        api.get('/inscriptions'),
        api.get('/recus'),
      ]);
      setCandidatures(c.data);
      setInscription(i.data[0] ?? null);
      setRecus(r.data);
      setErreur('');
    } catch (e) {
      setErreur(messageErreur(e));
    } finally {
      setChargement(false);
    }
  }, []);

  useEffect(() => {
    charger();
  }, [charger]);

  const soumises = candidatures.filter((c) => c.statut !== 'BROUILLON').length;

  if (chargement) return <p>Chargement...</p>;

  return (
    <>
      <div className="titre-page">
        <h1>Bonjour {user.prenom}</h1>
        <Link className="btn" to="/candidat/nouvelle">Nouvelle candidature</Link>
      </div>

      {erreur && <p className="erreur">{erreur}</p>}

      {inscription?.statut === 'CONFIRMEE' && (
        <p className="succes">
          Inscription définitive confirmée : {inscription.masterIntitule}
          {' '}(le {new Date(inscription.dateInscription).toLocaleDateString('fr-FR')}).
        </p>
      )}

      <p className="discret">
        Candidatures soumises : {soumises} / 3 pour cette campagne (les brouillons ne comptent pas).
      </p>

      <h2>Mes candidatures</h2>
      {candidatures.length === 0 && (
        <p>Vous n'avez pas encore de candidature. Choisissez un Master pour commencer.</p>
      )}
      <div className="grille">
        {candidatures.map((c) => (
          <CandidatureCard key={c.id} c={c} inscription={inscription} onChange={charger} />
        ))}
      </div>

      <h2>Mes reçus</h2>
      {recus.length === 0 && (
        <p className="discret">Aucun reçu pour le moment. Un reçu est généré après un paiement réussi.</p>
      )}
      {recus.length > 0 && (
        <table className="tableau">
          <thead>
            <tr><th>Numéro</th><th>Master</th><th>Montant</th><th>Date</th><th>PDF</th></tr>
          </thead>
          <tbody>
            {recus.map((r) => (
              <tr key={r.id}>
                <td>{r.numero}</td>
                <td>{r.masterIntitule}</td>
                <td>{r.montant} TND</td>
                <td>{new Date(r.dateGeneration).toLocaleDateString('fr-FR')}</td>
                <td>
                  <button
                    className="lien"
                    onClick={() => telechargerFichier(`/recus/${r.id}/pdf`, `${r.numero}.pdf`)}
                  >
                    Télécharger
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  );
}