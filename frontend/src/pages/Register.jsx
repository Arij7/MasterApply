import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { messageErreur } from '../api/client';
import { useAuth } from '../context/AuthContext';

const INITIAL = {
  nom: '', prenom: '', email: '', motDePasse: '',
  dateNaissance: '', telephone: '', nationalite: '', moyenneGenerale: '',
};

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState(INITIAL);
  const [erreur, setErreur] = useState('');
  const [envoi, setEnvoi] = useState(false);

  const modifier = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const soumettre = async (e) => {
    e.preventDefault();
    setErreur('');
    setEnvoi(true);
    try {
      await register({
        ...form,
        dateNaissance: form.dateNaissance || null,
        telephone: form.telephone || null,
        nationalite: form.nationalite || null,
        moyenneGenerale: form.moyenneGenerale === '' ? null : Number(form.moyenneGenerale),
      });
      navigate('/candidat');
    } catch (err) {
      setErreur(messageErreur(err));
    } finally {
      setEnvoi(false);
    }
  };

  return (
    <div className="formulaire">
      <h1>Créer un compte candidat</h1>
      <form onSubmit={soumettre}>
        <label>Nom
          <input name="nom" value={form.nom} onChange={modifier} required />
        </label>
        <label>Prénom
          <input name="prenom" value={form.prenom} onChange={modifier} required />
        </label>
        <label>Email
          <input name="email" type="email" value={form.email} onChange={modifier} required />
        </label>
        <label>Mot de passe (8 caractères minimum)
          <input name="motDePasse" type="password" minLength={8} value={form.motDePasse} onChange={modifier} required />
        </label>
        <label>Date de naissance
          <input name="dateNaissance" type="date" value={form.dateNaissance} onChange={modifier} />
        </label>
        <label>Téléphone
          <input name="telephone" value={form.telephone} onChange={modifier} />
        </label>
        <label>Nationalité
          <input name="nationalite" value={form.nationalite} onChange={modifier} />
        </label>
        <label>Moyenne générale (0 à 20)
          <input name="moyenneGenerale" type="number" step="0.01" min="0" max="20" value={form.moyenneGenerale} onChange={modifier} />
        </label>
        {erreur && <p className="erreur">{erreur}</p>}
        <button className="btn" type="submit" disabled={envoi}>
          {envoi ? 'Création...' : "S'inscrire"}
        </button>
      </form>
      <p>Déjà un compte ? <Link to="/login">Se connecter</Link></p>
    </div>
  );
}