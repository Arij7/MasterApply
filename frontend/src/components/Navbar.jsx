import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const deconnecter = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="navbar">
      <Link to="/" className="logo">MasterApply</Link>
      <nav>
        <Link to="/">Masters</Link>
        {!user && <Link to="/login">Connexion</Link>}
        {!user && <Link to="/register">Inscription</Link>}
        {user?.role === 'CANDIDAT' && <Link to="/candidat">Mon espace</Link>}
        {user?.role === 'ADMIN' && <Link to="/admin">Administration</Link>}
        {user && (
          <>
            <span className="utilisateur">{user.prenom} {user.nom}</span>
            <button className="btn btn-secondaire" onClick={deconnecter}>Déconnexion</button>
          </>
        )}
      </nav>
    </header>
  );
}