import { useEffect, useState } from 'react';
import api, { messageErreur, telechargerFichier } from '../../api/client';

export default function AdminPaiements() {
  const [paiements, setPaiements] = useState([]);
  const [erreur, setErreur] = useState('');

  useEffect(() => {
    api.get('/paiements')
      .then(({ data }) => setPaiements(data))
      .catch((e) => setErreur(messageErreur(e)));
  }, []);

  return (
    <>
      <p className="avertissement">Tous les paiements sont simulés : aucune transaction bancaire réelle.</p>
      {erreur && <p className="erreur">{erreur}</p>}
      <table className="tableau">
        <thead>
          <tr><th>N°</th><th>Candidature</th><th>Montant</th><th>Date</th><th>Référence</th><th>Statut</th><th>Reçu</th></tr>
        </thead>
        <tbody>
          {paiements.length === 0 && <tr><td colSpan={7} className="discret">Aucun paiement.</td></tr>}
          {paiements.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.candidatureId}</td>
              <td>{p.montant} TND</td>
              <td>{new Date(p.datePaiement).toLocaleString('fr-FR')}</td>
              <td>{p.referenceTransaction}</td>
              <td>{p.statut === 'REUSSI' ? 'Réussi' : p.statut === 'ECHOUE' ? 'Échoué' : 'En attente'}</td>
              <td>
                {p.recuId && (
                  <button className="lien" onClick={() => telechargerFichier(`/recus/${p.recuId}/pdf`, `recu-${p.recuId}.pdf`)}>
                    PDF
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}