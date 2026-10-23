import { useEffect, useState } from 'react';
import { api } from '../services/api';
import RestaurantCard from '../components/RestaurantCard';
import { Empty, ErrorMessage, Loading, PageHeader } from '../components/Feedback';
import { motion } from 'motion/react';

export default function RestaurantsPage() {
  const [restaurants, setRestaurants] = useState(null); const [error, setError] = useState('');
  useEffect(() => { api.restaurants().then(setRestaurants).catch((e) => setError(e.message)); }, []);
  return <section className="page"><PageHeader eyebrow="The collection" title="Find your next table"><p className="max-w-md text-sm text-slate-400">Browse live restaurant data from the VIPTables network.</p></PageHeader>{error && <ErrorMessage message={error} />}{restaurants === null && !error ? <Loading label="Loading restaurants..." /> : restaurants?.length === 0 ? <Empty title="No restaurants yet" text="Check back soon for new openings." /> : <motion.div className="grid gap-5 md:grid-cols-2" initial="hidden" animate="show" variants={{ hidden: {}, show: { transition: { staggerChildren: 0.08 } } }}>{restaurants?.map((restaurant) => <motion.div key={restaurant.id} variants={{ hidden: { opacity: 0, y: 10 }, show: { opacity: 1, y: 0 } }}><RestaurantCard restaurant={restaurant} /></motion.div>)}</motion.div>}</section>;
}
