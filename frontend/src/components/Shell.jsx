import { useState } from 'react';
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { motion, useReducedMotion } from 'motion/react';
import { useAuth } from '../context/AuthContext';
import Footer from './Footer';

export default function Shell() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const reduceMotion = useReducedMotion();
  const [menuOpen, setMenuOpen] = useState(false);
  const linkClass = ({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`;
  const closeMenu = () => setMenuOpen(false);
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <header className="sticky top-0 z-30 border-b border-white/10 bg-slate-950/90 shadow-lg shadow-black/10 backdrop-blur-xl">
        <nav className="mx-auto flex max-w-7xl items-center justify-between gap-5 px-5 py-3 sm:px-7 lg:px-8">
          <Link to="/" onClick={closeMenu} className="flex items-center gap-3 text-lg font-semibold tracking-tight text-white">
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-amber-300 font-black text-slate-950 shadow-lg shadow-amber-300/10">V</span>
            <span>VIP<span className="text-amber-300">Tables</span></span>
          </Link>
          <div className="hidden items-center gap-1 md:flex">
            <NavLink to="/restaurants" onClick={closeMenu} className={linkClass}>Restaurants</NavLink>
            {user?.role === 'CUSTOMER' && <NavLink to="/my-reservations" onClick={closeMenu} className={linkClass}>My reservations</NavLink>}
            {user?.role === 'MANAGER' && <NavLink to="/manager" onClick={closeMenu} className={linkClass}>Manager dashboard</NavLink>}
          </div>
          <div className="flex items-center gap-2">
            {user ? (
              <>
                <span className="hidden text-sm text-slate-400 sm:block">{user.fullName}</span>
                <button className="button-secondary" onClick={() => { logout(); navigate('/'); }}>Sign out</button>
              </>
            ) : (
              <><Link className="button-secondary" to="/login">Sign in</Link><Link className="button-primary" to="/register">Join us</Link></>
            )}
            <button className="nav-menu-button md:hidden" aria-label="Toggle navigation" aria-expanded={menuOpen} onClick={() => setMenuOpen(!menuOpen)}>
              <span /><span /><span />
            </button>
          </div>
        </nav>
        {menuOpen && <div className="border-t border-white/10 px-5 py-3 md:hidden">
          <div className="mx-auto flex max-w-7xl flex-col gap-1">
            <NavLink to="/restaurants" onClick={closeMenu} className={linkClass}>Restaurants</NavLink>
            {user?.role === 'CUSTOMER' && <NavLink to="/my-reservations" onClick={closeMenu} className={linkClass}>My reservations</NavLink>}
            {user?.role === 'MANAGER' && <NavLink to="/manager" onClick={closeMenu} className={linkClass}>Manager dashboard</NavLink>}
          </div>
        </div>}
      </header>
      <motion.main key={location.pathname} initial={reduceMotion ? false : { opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={reduceMotion ? undefined : { duration: 0.25 }}>
        <Outlet />
      </motion.main>
      <Footer />
    </div>
  );
}
