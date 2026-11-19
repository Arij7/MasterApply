import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api, { messageErreur } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function Masters() {
  const { user } = useAuth();
  const [masters, setMasters] = useState([]);
  const [erreur, setErreur] = useState('');
  const [chargement, setChargement] = useState(true);

  useEffect(() => {
    api.get('/masters')
      .then(({ data }) => setMasters(data))
      .catch((e) => setErreur(messageErreur(e)))
      .finally(() => setChargement(false));
  }, []);

  if (chargement) return <p>Chargement...</p>;
  if (erreur) return <p className="erreur">{erreur}</p>;

  return (
    <>
      <h1>Masters disponibles</h1>
      {masters.length === 0 && <p>Aucun Master disponible pour le moment.</p>}
      <div className="grille">
        {masters.map((m) => (
          <article key={m.id} className="carte">
            <h2>{m.intitule}</h2>
            <p>{m.description}</p>
            <p><strong>Conditions :</strong> {m.conditions}</p>
            <p><strong>Capacité :</strong> {m.capacite} places</p>
            <p><strong>Frais :</strong> {m.frais} TND</p>
            <div className="actions">
              <Link to={`/masters/${m.id}`}>Voir le détail</Link>
              {user?.role === 'CANDIDAT' && (
                <Link className="btn" to={`/candidat/nouvelle?master=${m.id}`}>Postuler</Link>
              )}
              {!user && <Link to="/login">Connectez-vous pour postuler</Link>}
            </div>
          </article>
        ))}
      </div>
    </>
  );
}