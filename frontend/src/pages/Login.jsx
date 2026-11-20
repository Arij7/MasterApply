import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { messageErreur } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [motDePasse, setMotDePasse] = useState('');
  const [erreur, setErreur] = useState('');
  const [envoi, setEnvoi] = useState(false);

  const soumettre = async (e) => {
    e.preventDefault();
    setErreur('');
    setEnvoi(true);
    try {
      const user = await login(email, motDePasse);
      navigate(user.role === 'ADMIN' ? '/admin' : '/candidat');
    } catch (err) {
      setErreur(messageErreur(err));
    } finally {
      setEnvoi(false);
    }
  };

  return (
    <div className="formulaire">
      <h1>Connexion</h1>
      <form onSubmit={soumettre}>
        <label>Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>Mot de passe
          <input type="password" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} required />
        </label>
        {erreur && <p className="erreur">{erreur}</p>}
        <button className="btn" type="submit" disabled={envoi}>
          {envoi ? 'Connexion...' : 'Se connecter'}
        </button>
      </form>
      <p>Pas de compte ? <Link to="/register">Créer un compte</Link></p>
    </div>
  );
}
