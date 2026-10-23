import { motion, useReducedMotion } from 'motion/react';

export function Loading({ label = 'Loading...' }) {
  const reduceMotion = useReducedMotion();
  return <motion.div initial={reduceMotion ? false : { opacity: 0 }} animate={{ opacity: 1 }} className="card flex min-h-28 items-center justify-center gap-3 text-sm text-slate-400"><span className="spinner" role="status" aria-label="Loading" />{label}</motion.div>;
}

export function Empty({ title, text }) {
  return <div className="card border-dashed py-12 text-center"><div className="mx-auto grid h-10 w-10 place-items-center rounded-full bg-amber-300/10 text-lg text-amber-300">+</div><h3 className="mt-4 font-semibold text-slate-100">{title}</h3><p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-400">{text}</p></div>;
}

export function ErrorMessage({ message }) {
  return <div role="alert" className="notice-error flex items-start gap-3"><span className="mt-0.5 text-rose-300" aria-hidden="true">!</span><span>{message}</span></div>;
}

export function PageHeader({ eyebrow, title, children }) {
  return <div className="mb-8 flex flex-col justify-between gap-4 border-b border-white/10 pb-6 sm:flex-row sm:items-end"><div><p className="eyebrow">{eyebrow}</p><h1 className="mt-2 text-3xl font-semibold tracking-tight text-white sm:text-4xl">{title}</h1></div>{children}</div>;
}
