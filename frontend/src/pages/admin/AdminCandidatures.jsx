import { useCallback, useEffect, useState } from 'react';
import api, { messageErreur, telechargerFichier } from '../../api/client';

const LIBELLES = {
  SOUMISE: 'Soumise',
  EN_EXAMEN: 'En examen',
  ACCEPTEE: 'Acceptée',
  REFUSEE: 'Refusée',
};

const TYPES = {
  CV: 'CV',
  RELEVE_NOTES: 'Relevé de notes',
  DIPLOME: 'Diplôme',
  PIECE_IDENTITE: "Pièce d'identité",
  LETTRE_MOTIVATION: 'Lettre de motivation',
};

function Dossier({ c, onChange, onFermer }) {
  const [documents, setDocuments] = useState([]);
  const [note, setNote] = useState(c.noteDossier ?? '');
  const [commentaire, setCommentaire] = useState(c.commentaireAdmin ?? '');
  const [erreur, setErreur] = useState('');
  const [occupe, setOccupe] = useState(false);

  useEffect(() => {
    api.get(`/candidatures/${c.id}/documents`)
      .then(({ data }) => setDocuments(data))
      .catch((e) => setErreur(messageErreur(e)));
  }, [c.id]);

  const executer = async (action) => {
    setErreur('');
    setOccupe(true);
    try {
      await action();
      await onChange();
    } catch (e) {
      setErreur(messageErreur(e));
    } finally {
      setOccupe(false);
    }
  };

  const examiner = () => executer(() => api.patch(`/candidatures/${c.id}/examen`));

  const decider = (decision) => {
    const verbe = decision === 'ACCEPTEE' ? 'Accepter' : 'Refuser';
    if (!window.confirm(`${verbe} la candidature de ${c.candidatNom} ?`)) return;
    executer(() => api.patch(`/candidatures/${c.id}/decision`, {
      decision,
      noteDossier: note === '' ? null : Number(note),
      commentaire: commentaire || null,
    }));
  };

  const modifiable = c.statut === 'SOUMISE' || c.statut === 'EN_EXAMEN';

  return (
    <section className="panneau">
      <div className="carte-entete">
        <h2>Dossier n° {c.id} : {c.candidatNom}</h2>
        <button className="lien" onClick={onFermer}>Fermer</button>
      </div>
      <p><strong>Master :</strong> {c.masterIntitule} · <strong>Campagne :</strong> {c.campagneLibelle}</p>
      <p>
        <strong>Statut :</strong> <span className={`badge badge-${c.statut}`}>{LIBELLES[c.statut]}</span>
        {c.dateSoumission && <> · soumis le {new Date(c.dateSoumission).toLocaleDateString('fr-FR')}</>}
      </p>

      <h3>Lettre de motivation</h3>
      <p className="texte-long">{c.motivation || <span className="discret">Non renseignée.</span>}</p>

      <h3>Documents</h3>
      {documents.length === 0 && <p className="discret">Aucun document.</p>}
      <ul className="liste">
        {documents.map((d) => (
          <li key={d.id}>
            {TYPES[d.type] || d.type} :{' '}
            <button
              className="lien"
              onClick={() => telechargerFichier(`/candidatures/${c.id}/documents/${d.id}/fichier`, d.nomFichier)}
            >
              {d.nomFichier}
            </button>
          </li>
        ))}
      </ul>

      {c.statut === 'SOUMISE' && (
        <div className="actions">
          <button className="btn" onClick={examiner} disabled={occupe}>Mettre en examen</button>
        </div>
      )}

      {modifiable && (
        <>
          <h3>Décision</h3>
          <div className="formulaire-admin">
            <label>Note du dossier (0 à 20)
              <input type="number" min="0" max="20" step="0.5" value={note} onChange={(e) => setNote(e.target.value)} />
            </label>
            <label>Commentaire
              <textarea rows={3} maxLength={1000} value={commentaire} onChange={(e) => setCommentaire(e.target.value)} />
            </label>
          </div>
          <div className="actions">
            <button className="btn btn-vert" onClick={() => decider('ACCEPTEE')} disabled={occupe}>Accepter</button>
            <button className="btn btn-rouge" onClick={() => decider('REFUSEE')} disabled={occupe}>Refuser</button>
          </div>
        </>
      )}

      {!modifiable && (
        <p>
          <strong>Décision :</strong> {LIBELLES[c.statut]}
          {c.noteDossier != null && <> · note {c.noteDossier}/20</>}
          {c.commentaireAdmin && <> · « {c.commentaireAdmin} »</>}
        </p>
      )}

      {erreur && <p className="erreur">{erreur}</p>}
    </section>
  );
}

export default function AdminCandidatures() {
  const [liste, setListe] = useState([]);
  const [filtre, setFiltre] = useState('A_TRAITER');
  const [choisieId, setChoisieId] = useState(null);
  const [erreur, setErreur] = useState('');
  const [chargement, setChargement] = useState(true);

  const charger = useCallback(async () => {
    try {
      const { data } = await api.get('/candidatures');
      setListe(data.filter((c) => c.statut !== 'BROUILLON'));
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

  const visibles = liste.filter((c) => {
    if (filtre === 'A_TRAITER') return c.statut === 'SOUMISE' || c.statut === 'EN_EXAMEN';
    if (filtre === 'TOUTES') return true;
    return c.statut === filtre;
  });

  const choisie = liste.find((c) => c.id === choisieId);

  if (chargement) return <p>Chargement...</p>;

  return (
    <>
      <div className="ligne-formulaire">
        <label>Afficher :{' '}
          <select value={filtre} onChange={(e) => setFiltre(e.target.value)}>
            <option value="A_TRAITER">À traiter (soumises et en examen)</option>
            <option value="ACCEPTEE">Acceptées</option>
            <option value="REFUSEE">Refusées</option>
            <option value="TOUTES">Toutes</option>
          </select>
        </label>
      </div>
      {erreur && <p className="erreur">{erreur}</p>}

      <table className="tableau">
        <thead>
          <tr><th>N°</th><th>Candidat</th><th>Master</th><th>Soumise le</th><th>Docs</th><th>Statut</th><th></th></tr>
        </thead>
        <tbody>
          {visibles.length === 0 && (
            <tr><td colSpan={7} className="discret">Aucune candidature dans cette catégorie.</td></tr>
          )}
          {visibles.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.candidatNom}</td>
              <td>{c.masterIntitule}</td>
              <td>{c.dateSoumission ? new Date(c.dateSoumission).toLocaleDateString('fr-FR') : ''}</td>
              <td>{c.nombreDocuments}</td>
              <td><span className={`badge badge-${c.statut}`}>{LIBELLES[c.statut]}</span></td>
              <td><button className="lien" onClick={() => setChoisieId(c.id)}>Examiner</button></td>
            </tr>
          ))}
        </tbody>
      </table>

      {choisie && <Dossier key={choisie.id} c={choisie} onChange={charger} onFermer={() => setChoisieId(null)} />}
    </>
  );
}
