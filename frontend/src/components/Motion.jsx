import { motion, useReducedMotion } from 'motion/react';

export function Reveal({ children, className = '', delay = 0 }) {
  const reduceMotion = useReducedMotion();
  return (
    <motion.div
      className={className}
      initial={reduceMotion ? false : { opacity: 0, y: 12 }}
      whileInView={reduceMotion ? undefined : { opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.15 }}
      transition={reduceMotion ? undefined : { duration: 0.3, delay, ease: 'easeOut' }}
    >
      {children}
    </motion.div>
  );
}

export function TapButton({ children, className, ...props }) {
  const reduceMotion = useReducedMotion();
  return (
    <motion.button
      className={className}
      whileTap={reduceMotion ? undefined : { scale: 0.97 }}
      whileHover={reduceMotion ? undefined : { y: -1 }}
      transition={{ duration: 0.12 }}
      {...props}
    >
      {children}
    </motion.button>
  );
}
