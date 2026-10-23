import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ErrorMessage } from '../components/Feedback';

export default function LoginPage() {
  const { user, login } = useAuth(); const navigate = useNavigate(); const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' }); const [error, setError] = useState(''); const [busy, setBusy] = useState(false);
  if (user) return <Navigate to={user.role === 'MANAGER' ? '/manager' : '/customer'} replace />;
  async function submit(event) { event.preventDefault(); setBusy(true); setError(''); try { const next = await login(form.email, form.password); navigate(next.role === 'MANAGER' ? '/manager' : (location.state?.from || '/customer')); } catch (e) { setError(e.message); } finally { setBusy(false); } }
  return <section className="form-page"><div className="form-card"><p className="eyebrow">Welcome back</p><h1 className="mt-2 text-3xl font-semibold">Sign in to VIPTables</h1><p className="mt-2 text-sm text-slate-400">Your reservations, one thoughtful place.</p>{error && <div className="mt-5"><ErrorMessage message={error} /></div>}<form className="mt-7 space-y-4" onSubmit={submit}><label>Email<input required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label><label>Password<input required type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label><button className="button-primary w-full" disabled={busy}>{busy ? 'Signing in...' : 'Sign in'}</button></form><p className="mt-6 text-sm text-slate-400">New here? <Link className="text-amber-300" to="/register">Create an account</Link></p></div></section>;
}
