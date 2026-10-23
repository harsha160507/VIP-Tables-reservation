import { Link } from 'react-router-dom';
import { PageHeader } from '../components/Feedback';
import { useAuth } from '../context/AuthContext';
import { motion } from 'motion/react';

export default function CustomerDashboardPage() {
  const { user } = useAuth();
  return <section className="page"><PageHeader eyebrow="Your dining desk" title={`Good to see you, ${user.fullName.split(' ')[0]}.`} /><motion.div className="grid gap-5 md:grid-cols-2" initial="hidden" animate="show" variants={{ hidden: {}, show: { transition: { staggerChildren: 0.1 } } }}><motion.div variants={{ hidden: { opacity: 0, y: 12 }, show: { opacity: 1, y: 0 } }}><Link to="/restaurants" className="card card-link"><p className="eyebrow">Discover</p><h2 className="mt-2 text-2xl font-semibold">Find a restaurant</h2><p className="mt-2 text-sm text-slate-400">Browse the live collection and choose your next table.</p></Link></motion.div><motion.div variants={{ hidden: { opacity: 0, y: 12 }, show: { opacity: 1, y: 0 } }}><Link to="/my-reservations" className="card card-link"><p className="eyebrow">Your plans</p><h2 className="mt-2 text-2xl font-semibold">My reservations</h2><p className="mt-2 text-sm text-slate-400">Review or cancel your upcoming reservations.</p></Link></motion.div></motion.div></section>;
}
