import { useState } from 'react';
import AdminCandidatures from './admin/AdminCandidatures';
import AdminDashboard from './admin/AdminDashboard';
import AdminInscriptions from './admin/AdminInscriptions';
import AdminMasters from './admin/AdminMasters';
import AdminPaiements from './admin/AdminPaiements';

const ONGLETS = [
  { cle: 'dashboard', libelle: 'Dashboard' },
  { cle: 'candidatures', libelle: 'Candidatures' },
  { cle: 'masters', libelle: 'Masters' },
  { cle: 'paiements', libelle: 'Paiements' },
  { cle: 'inscriptions', libelle: 'Inscriptions' },
];

export default function EspaceAdmin() {
  const [onglet, setOnglet] = useState('dashboard');

  return (
    <>
      <h1>Espace administrateur</h1>
      <div className="onglets">
        {ONGLETS.map((o) => (
          <button
            key={o.cle}
            className={`onglet ${onglet === o.cle ? 'actif' : ''}`}
            onClick={() => setOnglet(o.cle)}
          >
            {o.libelle}
          </button>
        ))}
      </div>

      {onglet === 'dashboard' && <AdminDashboard />}
      {onglet === 'candidatures' && <AdminCandidatures />}
      {onglet === 'masters' && <AdminMasters />}
      {onglet === 'paiements' && <AdminPaiements />}
      {onglet === 'inscriptions' && <AdminInscriptions />}
    </>
  );
}