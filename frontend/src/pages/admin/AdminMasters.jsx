import { useCallback, useEffect, useState } from 'react';
import api, { messageErreur } from '../../api/client';

const VIDE = { intitule: '', description: '', conditions: '', capacite: '', frais: '' };

export default function AdminMasters() {
  const [masters, setMasters] = useState([]);
  const [form, setForm] = useState(VIDE);
  const [editionId, setEditionId] = useState(null);
  const [message, setMessage] = useState('');
  const [erreur, setErreur] = useState('');
  const [occupe, setOccupe] = useState(false);

  const charger = useCallback(async () => {
    try {
      const { data } = await api.get('/masters');
      setMasters(data);
    } catch (e) {
      setErreur(messageErreur(e));
    }
  }, []);

  useEffect(() => {
    charger();
  }, [charger]);

  const modifierChamp = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const annuler = () => {
    setForm(VIDE);
    setEditionId(null);
  };

  const modifier = (m) => {
    setEditionId(m.id);
    setForm({
      intitule: m.intitule,
      description: m.description ?? '',
      conditions: m.conditions ?? '',
      capacite: m.capacite,
      frais: m.frais,
    });
    setMessage('');
    setErreur('');
  };

  const enregistrer = async (e) => {
    e.preventDefault();
    setErreur('');
    setMessage('');
    setOccupe(true);
    const corps = { ...form, capacite: Number(form.capacite), frais: Number(form.frais) };
    try {
      if (editionId) {
        await api.put(`/masters/${editionId}`, corps);
        setMessage('Master modifié.');
      } else {
        await api.post('/masters', corps);
        setMessage('Master ajouté.');
      }
      annuler();
      await charger();
    } catch (err) {
      setErreur(messageErreur(err));
    } finally {
      setOccupe(false);
    }
  };

  const supprimer = async (m) => {
    if (!window.confirm(`Supprimer le Master « ${m.intitule} » ?`)) return;
    setErreur('');
    setMessage('');
    try {
      await api.delete(`/masters/${m.id}`);
      setMessage('Master supprimé.');
      await charger();
    } catch (err) {
      setErreur(messageErreur(err));
    }
  };

  return (
    <>
      <section className="panneau">
        <h2>{editionId ? `Modifier le Master n° ${editionId}` : 'Ajouter un Master'}</h2>
        <form className="formulaire-admin" onSubmit={enregistrer}>
          <label>Intitulé
            <input name="intitule" value={form.intitule} onChange={modifierChamp} required />
          </label>
          <label>Description
            <textarea name="description" rows={2} maxLength={2000} value={form.description} onChange={modifierChamp} />
          </label>
          <label>Conditions de candidature
            <textarea name="conditions" rows={2} maxLength={2000} value={form.conditions} onChange={modifierChamp} />
          </label>
          <label>Capacité (places)
            <input name="capacite" type="number" min="1" value={form.capacite} onChange={modifierChamp} required />
          </label>
          <label>Frais (TND)
            <input name="frais" type="number" min="0" step="0.01" value={form.frais} onChange={modifierChamp} required />
          </label>
          <div className="actions">
            <button className="btn" type="submit" disabled={occupe}>{editionId ? 'Enregistrer' : 'Ajouter'}</button>
            {editionId && <button type="button" className="btn btn-gris" onClick={annuler}>Annuler</button>}
          </div>
        </form>
        {message && <p className="succes">{message}</p>}
        {erreur && <p className="erreur">{erreur}</p>}
      </section>

      <table className="tableau">
        <thead>
          <tr><th>N°</th><th>Intitulé</th><th>Capacité</th><th>Frais</th><th></th></tr>
        </thead>
        <tbody>
          {masters.length === 0 && <tr><td colSpan={5} className="discret">Aucun Master.</td></tr>}
          {masters.map((m) => (
            <tr key={m.id}>
              <td>{m.id}</td>
              <td>{m.intitule}</td>
              <td>{m.capacite}</td>
              <td>{m.frais} TND</td>
              <td>
                <button className="lien" onClick={() => modifier(m)}>Modifier</button>{' · '}
                <button className="lien lien-danger" onClick={() => supprimer(m)}>Supprimer</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
