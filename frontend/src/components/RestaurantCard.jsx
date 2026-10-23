import { Link } from 'react-router-dom';
import { motion } from 'motion/react';

export default function RestaurantCard({ restaurant }) {
  return (
    <motion.div whileHover={{ y: -4 }} transition={{ duration: 0.2 }}>
      <Link to={`/restaurants/${restaurant.id}`} className="group card block overflow-hidden p-0 hover:border-amber-300/50">
        <div className="restaurant-image">{restaurant.imageUrl ? <img src={restaurant.imageUrl} alt="" /> : <span>{restaurant.cuisine?.slice(0, 1)}</span>}<span className="image-badge">Explore tables</span></div>
        <div className="p-5 sm:p-6"><p className="eyebrow">{restaurant.cuisine}</p><h3 className="mt-2 text-xl font-semibold text-white">{restaurant.name}</h3><p className="mt-2 line-clamp-2 min-h-12 text-sm leading-6 text-slate-400">{restaurant.description || 'A memorable table awaits.'}</p><p className="mt-5 flex items-center justify-between gap-3 border-t border-white/10 pt-4 text-sm text-amber-300"><span className="truncate text-slate-400">{restaurant.address}</span><span className="shrink-0">View details <span aria-hidden="true">→</span></span></p></div>
      </Link>
    </motion.div>
  );
}
