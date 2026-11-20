import { useCallback, useEffect, useState } from 'react';
import api, { messageErreur, telechargerFichier } from '../api/client';

const TYPES = [
  { valeur: 'CV', libelle: 'CV' },
  { valeur: 'RELEVE_NOTES', libelle: 'Relevé de notes' },
  { valeur: 'DIPLOME', libelle: 'Diplôme' },
  { valeur: 'PIECE_IDENTITE', libelle: "Pièce d'identité" },
  { valeur: 'LETTRE_MOTIVATION', libelle: 'Lettre de motivation' },
];

const LIBELLES = {
  BROUILLON: 'Brouillon',
  SOUMISE: 'Soumise',
  EN_EXAMEN: 'En examen',
  ACCEPTEE: 'Acceptée',
  REFUSEE: 'Refusée',
};

export default function CandidatureCard({ c, inscription, onChange }) {
  const [documents, setDocuments] = useState([]);
  const [paiements, setPaiements] = useState([]);
  const [type, setType] = useState('CV');
  const [fichier, setFichier] = useState(null);
  const [message, setMessage] = useState('');
  const [erreur, setErreur] = useState('');
  const [occupe, setOccupe] = useState(false);

  const charger = useCallback(async () => {
    try {
      if (c.statut === 'BROUILLON') {
        const { data } = await api.get(`/candidatures/${c.id}/documents`);
        setDocuments(data);
      }
      if (c.statut === 'ACCEPTEE') {
        const { data } = await api.get(`/candidatures/${c.id}/paiements`);
        setPaiements(data);
      }
    } catch (e) {
      setErreur(messageErreur(e));
    }
  }, [c.id, c.statut]);

  useEffect(() => {
    charger();
  }, [charger]);

  const executer = async (action, succes) => {
    setErreur('');
    setMessage('');
    setOccupe(true);
    try {
      await action();
      if (succes) setMessage(succes);
      await charger();
      await onChange();
    } catch (e) {
      setErreur(messageErreur(e));
    } finally {
      setOccupe(false);
    }
  };

  const envoyerDocument = (e) => {
    e.preventDefault();
    if (!fichier) {
      setErreur('Choisissez un fichier (PDF, JPG ou PNG).');
      return;
    }
    const form = new FormData();
    form.append('type', type);
    form.append('file', fichier);
    e.target.reset();
    setFichier(null);
    executer(() => api.post(`/candidatures/${c.id}/documents`, form), 'Document enregistré.');
  };

  const soumettre = () =>
    executer(() => api.patch(`/candidatures/${c.id}/soumettre`), 'Candidature soumise.');

  const supprimer = async () => {
    if (!window.confirm('Supprimer ce brouillon et ses documents ?')) return;
    setErreur('');
    setOccupe(true);
    try {
      await api.delete(`/candidatures/${c.id}`);
      await onChange();
    } catch (e) {
      setErreur(messageErreur(e));
      setOccupe(false);
    }
  };

  const payer = (forcerEchec) => {
    const texte = "Ce paiement est SIMULÉ : aucune transaction bancaire réelle n'est effectuée. Continuer ?";
    if (!window.confirm(texte)) return;
    executer(async () => {
      const { data } = await api.post(`/candidatures/${c.id}/paiements/simulation`, { forcerEchec });
      setMessage(data.statut === 'REUSSI'
        ? `Paiement réussi (référence ${data.referenceTransaction}).`
        : 'Le paiement a échoué. Vous pouvez réessayer.');
    });
  };

  const confirmerInscription = () => {
    if (!window.confirm('Confirmer définitivement votre inscription dans ce Master ?')) return;
    executer(() => api.post(`/candidatures/${c.id}/inscription`), 'Inscription confirmée.');
  };

  const dejaInscrit = inscription?.statut === 'CONFIRMEE';
  const inscritIci = dejaInscrit && inscription.candidatureId === c.id;
  const paye = paiements.some((p) => p.statut === 'REUSSI');

  return (
    <article className="carte">
      <div className="carte-entete">
        <h2>{c.masterIntitule}</h2>
        <span className={`badge badge-${c.statut}`}>{LIBELLES[c.statut]}</span>
      </div>
      <p className="discret">
        {c.campagneLibelle} · créée le {new Date(c.dateCreation).toLocaleDateString('fr-FR')}
      </p>

      {inscritIci && <p className="succes">Vous êtes inscrit(e) dans ce Master.</p>}

      {c.statut === 'BROUILLON' && (
        <>
          <h3>Documents</h3>
          {documents.length === 0 && (
            <p className="discret">Aucun document envoyé. Obligatoires : CV et relevé de notes.</p>
          )}
          <ul className="liste">
            {documents.map((d) => (
              <li key={d.id}>
                {TYPES.find((t) => t.valeur === d.type)?.libelle} : {d.nomFichier}
              </li>
            ))}
          </ul>
          <form className="ligne-formulaire" onSubmit={envoyerDocument}>
            <select value={type} onChange={(e) => setType(e.target.value)}>
              {TYPES.map((t) => <option key={t.valeur} value={t.valeur}>{t.libelle}</option>)}
            </select>
            <input
              type="file"
              accept=".pdf,.jpg,.jpeg,.png"
              onChange={(e) => setFichier(e.target.files[0] || null)}
            />
            <button className="btn" type="submit" disabled={occupe}>Envoyer</button>
          </form>
          <div className="actions">
            <button className="btn btn-vert" onClick={soumettre} disabled={occupe}>
              Soumettre la candidature
            </button>
            <button className="btn btn-rouge" onClick={supprimer} disabled={occupe}>
              Supprimer le brouillon
            </button>
          </div>
        </>
      )}

      {(c.statut === 'SOUMISE' || c.statut === 'EN_EXAMEN') && (
        <p>Votre dossier est transmis à l'administration. Vous serez informé(e) de la décision ici.</p>
      )}

      {(c.statut === 'ACCEPTEE' || c.statut === 'REFUSEE') && (
        <p>
          {c.noteDossier != null && <><strong>Note du dossier :</strong> {c.noteDossier}/20<br /></>}
          {c.commentaireAdmin && <><strong>Commentaire :</strong> {c.commentaireAdmin}</>}
        </p>
      )}

      {c.statut === 'ACCEPTEE' && !dejaInscrit && (
        <>
          <h3>Paiement des frais</h3>
          <p className="avertissement">Paiement simulé : démonstration uniquement.</p>
          {paiements.length > 0 && (
            <table className="tableau">
              <thead>
                <tr><th>Date</th><th>Montant</th><th>Statut</th><th>Reçu</th></tr>
              </thead>
              <tbody>
                {paiements.map((p) => (
                  <tr key={p.id}>
                    <td>{new Date(p.datePaiement).toLocaleString('fr-FR')}</td>
                    <td>{p.montant} TND</td>
                    <td>{p.statut === 'REUSSI' ? 'Réussi' : 'Échoué'}</td>
                    <td>
                      {p.recuId && (
                        <button
                          className="lien"
                          onClick={() => telechargerFichier(`/recus/${p.recuId}/pdf`, `recu-${p.recuId}.pdf`)}
                        >
                          Télécharger
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
          <div className="actions">
            {!paye && (
              <>
                <button className="btn" onClick={() => payer(false)} disabled={occupe}>Payer les frais</button>
                <button className="btn btn-gris" onClick={() => payer(true)} disabled={occupe}>
                  Simuler un échec (démo)
                </button>
              </>
            )}
            {paye && (
              <button className="btn btn-vert" onClick={confirmerInscription} disabled={occupe}>
                Confirmer mon inscription
              </button>
            )}
          </div>
        </>
      )}

      {c.statut === 'ACCEPTEE' && dejaInscrit && !inscritIci && (
        <p className="discret">
          Vous avez déjà une inscription définitive dans un autre Master : cette acceptation ne donne
          pas lieu à une seconde inscription.
        </p>
      )}

      {message && <p className="succes">{message}</p>}
      {erreur && <p className="erreur">{erreur}</p>}
    </article>
  );
}
