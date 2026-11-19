import { useEffect, useState } from 'react';
import api, { messageErreur } from '../../api/client';

export default function AdminInscriptions() {
  const [inscriptions, setInscriptions] = useState([]);
  const [erreur, setErreur] = useState('');

  useEffect(() => {
    api.get('/inscriptions')
      .then(({ data }) => setInscriptions(data))
      .catch((e) => setErreur(messageErreur(e)));
  }, []);

  return (
    <>
      {erreur && <p className="erreur">{erreur}</p>}
      <table className="tableau">
        <thead>
          <tr><th>N°</th><th>Candidat</th><th>Master</th><th>Date</th><th>Statut</th></tr>
        </thead>
        <tbody>
          {inscriptions.length === 0 && <tr><td colSpan={5} className="discret">Aucune inscription.</td></tr>}
          {inscriptions.map((i) => (
            <tr key={i.id}>
              <td>{i.id}</td>
              <td>{i.candidatNom}</td>
              <td>{i.masterIntitule}</td>
              <td>{new Date(i.dateInscription).toLocaleDateString('fr-FR')}</td>
              <td>{i.statut === 'CONFIRMEE' ? 'Confirmée' : i.statut}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}