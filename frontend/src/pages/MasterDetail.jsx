import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api, { messageErreur } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function MasterDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const [master, setMaster] = useState(null);
  const [erreur, setErreur] = useState('');

  useEffect(() => {
    api.get(`/masters/${id}`)
      .then(({ data }) => setMaster(data))
      .catch((e) => setErreur(messageErreur(e)));
  }, [id]);

  if (erreur) return <p className="erreur">{erreur}</p>;
  if (!master) return <p>Chargement...</p>;

  return (
    <section className="panneau">
      <Link to="/">← Retour aux Masters</Link>
      <h1>{master.intitule}</h1>

      <h3>Description</h3>
      <p className="texte-long">{master.description || 'Non renseignée.'}</p>

      <h3>Conditions de candidature</h3>
      <p className="texte-long">{master.conditions || 'Non renseignées.'}</p>

      <p><strong>Capacité :</strong> {master.capacite} places</p>
      <p><strong>Frais d'inscription :</strong> {master.frais} TND</p>
      <p><strong>Documents obligatoires :</strong> CV et relevé de notes</p>

      <div className="actions">
        {user?.role === 'CANDIDAT' && (
          <Link className="btn" to={`/candidat/nouvelle?master=${master.id}`}>Postuler</Link>
        )}
        {!user && <Link to="/login">Connectez-vous pour postuler</Link>}
      </div>
    </section>
  );
}