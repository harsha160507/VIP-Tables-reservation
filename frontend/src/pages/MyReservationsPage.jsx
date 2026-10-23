import { useEffect, useState } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Empty, ErrorMessage, Loading, PageHeader } from '../components/Feedback';

export default function MyReservationsPage() {
  const { user, session } = useAuth(); const [items, setItems] = useState(null); const [error, setError] = useState(''); const [message, setMessage] = useState('');
  function load() { api.reservations(user.id, session.auth).then(setItems).catch((e) => setError(e.message)); }
  useEffect(() => {
    load();
  }, [user.id, session.auth]);
  async function cancel(id) { setError(''); setMessage(''); try { await api.cancelReservation(id, user.id, session.auth); setMessage('Reservation cancelled.'); load(); } catch (e) { setError(e.message); } }
  return <section className="page"><PageHeader eyebrow="Your plans" title="My reservations" />{error && <ErrorMessage message={error} />}{message && <p className="notice-success mb-4">{message}</p>}{items === null && !error ? <Loading /> : items?.length === 0 ? <Empty title="No reservations yet" text="Your next memorable meal is waiting." /> : <div className="space-y-3">{items?.map((item) => <div className="card flex flex-col justify-between gap-4 sm:flex-row sm:items-center" key={item.id}><div><p className="font-semibold">Table {item.restaurantTableId} · {item.reservationDate}</p><p className="mt-1 text-sm text-slate-400">{item.startTime} – {item.endTime} · <span className={item.status === 'CONFIRMED' ? 'text-emerald-300' : 'text-slate-500'}>{item.status}</span></p></div>{item.status === 'CONFIRMED' && <button className="button-danger" onClick={() => cancel(item.id)}>Cancel</button>}</div>)}</div>}</section>;
}
