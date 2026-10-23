import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="border-t border-white/10 bg-black/20">
      <div className="mx-auto flex max-w-7xl flex-col gap-4 px-5 py-8 text-sm text-slate-500 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <Link to="/" className="font-semibold text-slate-300">VIPTables</Link>
          <p className="mt-1">Make room for the good stuff.</p>
        </div>
        <p>Thoughtful dining, simply reserved.</p>
      </div>
    </footer>
  );
}
