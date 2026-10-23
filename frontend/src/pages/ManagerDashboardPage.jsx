import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { motion } from 'motion/react';
import { ErrorMessage, Loading, PageHeader } from '../components/Feedback';

export default function ManagerDashboardPage() {
  const { session } = useAuth(); const [stats, setStats] = useState(null); const [error, setError] = useState('');
  useEffect(() => { api.managerDashboard(session.auth).then(setStats).catch((e) => setError(e.message)); }, [session.auth]);
  return <section className="page"><PageHeader eyebrow="Operations" title="Manager dashboard"><motion.div whileTap={{ scale: 0.97 }}><Link to="/manager/restaurants" className="button-primary">Manage restaurants</Link></motion.div></PageHeader>{error && <ErrorMessage message={error} />}{!stats && !error ? <Loading /> : stats && <><motion.div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4" initial="hidden" animate="show" variants={{ hidden: {}, show: { transition: { staggerChildren: 0.08 } } }}>{[['Restaurants', stats.restaurantCount], ['Active tables', stats.activeTableCount], ['Confirmed', stats.confirmedReservationCount], ['Cancelled', stats.cancelledReservationCount]].map(([label, value]) => <motion.div className="stat-card" key={label} variants={{ hidden: { opacity: 0, y: 10 }, show: { opacity: 1, y: 0 } }}><p className="text-sm text-slate-400">{label}</p><p className="mt-3 text-4xl font-semibold">{value}</p></motion.div>)}</motion.div><div className="mt-6 grid gap-5 md:grid-cols-3"><Link className="card card-link" to="/manager/restaurants">Restaurant management →</Link><Link className="card card-link" to="/manager/tables">Table management →</Link><Link className="card card-link" to="/manager/reservations">Reservation management →</Link></div></>}</section>;
}
