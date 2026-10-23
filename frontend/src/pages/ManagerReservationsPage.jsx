import { useEffect, useState } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Empty, ErrorMessage, Loading, PageHeader } from '../components/Feedback';

export default function ManagerReservationsPage() {
  const { session } = useAuth(); const [items, setItems] = useState(null); const [error, setError] = useState('');
  async function load() { try { setItems(await api.managerReservations(session.auth)); } catch (e) { setError(e.message); } }
  useEffect(() => {
    load();
  }, [session.auth]);
  async function update(id, status) { try { await api.updateReservationStatus(id, status, session.auth); await load(); } catch (e) { setError(e.message); } }
  return <section className="page"><PageHeader eyebrow="Manager tools" title="Reservations" />{error && <ErrorMessage message={error} />}{items === null && !error ? <Loading /> : items?.length === 0 ? <Empty title="No reservations" text="Reservations will appear here." /> : <div className="overflow-x-auto rounded-2xl border border-white/10"><table><thead><tr><th>ID</th><th>Customer</th><th>Table</th><th>Date</th><th>Time</th><th>Status</th><th /></tr></thead><tbody>{items?.map((item) => <tr key={item.id}><td>#{item.id}</td><td>{item.customerId}</td><td>{item.restaurantTableId}</td><td>{item.reservationDate}</td><td>{item.startTime}–{item.endTime}</td><td><span className="badge">{item.status}</span></td><td><select value={item.status} onChange={(e) => update(item.id, e.target.value)}><option value="CONFIRMED">CONFIRMED</option><option value="CANCELLED">CANCELLED</option></select></td></tr>)}</tbody></table></div>}</section>;
}
