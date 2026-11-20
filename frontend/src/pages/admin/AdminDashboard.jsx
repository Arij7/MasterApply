import { useEffect, useState } from 'react';
import api, { messageErreur } from '../../api/client';

function Carte({ titre, valeur, couleur }) {
  return (
    <div className="stat" style={{ borderTopColor: couleur }}>
      <span className="stat-valeur">{valeur}</span>
      <span className="stat-titre">{titre}</span>
    </div>
  );
}

function Camembert({ parts }) {
  const total = parts.reduce((s, p) => s + p.valeur, 0);
  let cumul = 0;
  const segments = parts.map((p) => {
    const debut = cumul;
    cumul += total ? (p.valeur / total) * 100 : 0;
    return `${p.couleur} ${debut}% ${cumul}%`;
  });
  const fond = total ? `conic-gradient(${segments.join(', ')})` : '#e5e7eb';

  return (
    <div className="camembert-zone">
      <div className="camembert" style={{ background: fond }} />
      <ul className="legende">
        {parts.map((p) => (
          <li key={p.label}>
            <span className="pastille" style={{ background: p.couleur }} />
            {p.label} : <strong>{p.valeur}</strong>
          </li>
        ))}
      </ul>
    </div>
  );
}

function Barres({ donnees }) {
  if (donnees.length === 0) return <p className="discret">Aucune candidature soumise pour le moment.</p>;
  const max = Math.max(...donnees.map((d) => d.total), 1);
  return (
    <div className="barres">
      {donnees.map((d) => (
        <div key={d.master} className="barre-ligne">
          <span className="barre-nom">{d.master}</span>
          <div className="barre-piste">
            <div className="barre" style={{ width: `${(d.total / max) * 100}%` }} />
          </div>
          <strong>{d.total}</strong>
        </div>
      ))}
    </div>
  );
}

function Courbe({ donnees }) {
  if (donnees.length === 0) return <p className="discret">Aucune candidature soumise pour le moment.</p>;
  const L = 440;
  const H = 170;
  const m = 28;
  const max = Math.max(...donnees.map((d) => d.total), 1);
  const x = (i) => (donnees.length === 1 ? L / 2 : m + (i * (L - 2 * m)) / (donnees.length - 1));
  const y = (v) => H - m - (v / max) * (H - 2 * m);
  const points = donnees.map((d, i) => `${x(i)},${y(d.total)}`).join(' ');
  const format = (d) => new Date(d).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });

  return (
    <svg viewBox={`0 0 ${L} ${H}`} className="courbe">
      <line x1={m} y1={H - m} x2={L - m} y2={H - m} stroke="#cbd5e1" />
      <polyline points={points} fill="none" stroke="#2563eb" strokeWidth="2" />
      {donnees.map((d, i) => (
        <g key={d.date}>
          <circle cx={x(i)} cy={y(d.total)} r="4" fill="#2563eb" />
          <text x={x(i)} y={y(d.total) - 8} textAnchor="middle" fontSize="11" fill="#374151">{d.total}</text>
        </g>
      ))}
      <text x={x(0)} y={H - 8} textAnchor="middle" fontSize="11" fill="#6b7280">{format(donnees[0].date)}</text>
      {donnees.length > 1 && (
        <text x={x(donnees.length - 1)} y={H - 8} textAnchor="middle" fontSize="11" fill="#6b7280">
          {format(donnees[donnees.length - 1].date)}
        </text>
      )}
    </svg>
  );
}

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [erreur, setErreur] = useState('');

  useEffect(() => {
    api.get('/dashboard/statistiques')
      .then(({ data }) => setStats(data))
      .catch((e) => setErreur(messageErreur(e)));
  }, []);

  if (erreur) return <p className="erreur">{erreur}</p>;
  if (!stats) return <p>Chargement...</p>;

  return (
    <>
      <div className="cartes-stats">
        <Carte titre="Candidatures" valeur={stats.totalCandidatures} couleur="#1e3a8a" />
        <Carte titre="En attente" valeur={stats.enAttente} couleur="#2563eb" />
        <Carte titre="Acceptées" valeur={stats.acceptees} couleur="#16a34a" />
        <Carte titre="Refusées" valeur={stats.refusees} couleur="#dc2626" />
        <Carte titre="Paiements réussis" valeur={stats.paiementsReussis} couleur="#d97706" />
        <Carte titre="Inscriptions confirmées" valeur={stats.inscriptionsConfirmees} couleur="#7c3aed" />
      </div>

      <div className="grille">
        <section className="carte">
          <h2>Décisions</h2>
          <Camembert
            parts={[
              { label: 'Acceptées', valeur: stats.acceptees, couleur: '#16a34a' },
              { label: 'Refusées', valeur: stats.refusees, couleur: '#dc2626' },
              { label: 'En attente', valeur: stats.enAttente, couleur: '#2563eb' },
            ]}
          />
        </section>
        <section className="carte">
          <h2>Candidatures par Master</h2>
          <Barres donnees={stats.parMaster} />
        </section>
        <section className="carte">
          <h2>Soumissions par jour</h2>
          <Courbe donnees={stats.parJour} />
        </section>
      </div>
    </>
  );
}
