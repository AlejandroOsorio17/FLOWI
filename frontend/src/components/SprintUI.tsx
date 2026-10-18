import type { ReactNode, SVGProps } from "react"

export function SearchIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <circle cx="11" cy="11" r="7" />
      <path d="m20 20-3.5-3.5" />
    </svg>
  )
}

export function BoxIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path d="m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3Z" />
      <path d="m4.5 7.7 7.5 4.2 7.5-4.2M12 12v9" />
    </svg>
  )
}

export function AlertIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path d="M10.3 4.2 2.7 17.5A2 2 0 0 0 4.4 20h15.2a2 2 0 0 0 1.7-2.5L13.7 4.2a2 2 0 0 0-3.4 0Z" />
      <path d="M12 9v4M12 17h.01" />
    </svg>
  )
}

export function StoreIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path d="M4 10v10h16V10M3 10l2-6h14l2 6" />
      <path d="M3 10a3 3 0 0 0 4.5 2.6A3 3 0 0 0 12 12.6a3 3 0 0 0 4.5 0A3 3 0 0 0 21 10M9 20v-5h6v5" />
    </svg>
  )
}

export function PageHeading({ title, subtitle, eyebrow, action }: {
  title: string
  subtitle: string
  eyebrow?: ReactNode
  action?: ReactNode
}) {
  return (
    <div className="flex flex-col gap-4 border-b border-slate-200 pb-7 sm:flex-row sm:items-end sm:justify-between">
      <div>
        {eyebrow && <div className="mb-2 text-sm font-semibold text-blue-600">{eyebrow}</div>}
        <h1 className="text-3xl font-bold tracking-tight text-slate-950">{title}</h1>
        <p className="mt-1.5 text-sm text-slate-500">{subtitle}</p>
      </div>
      {action}
    </div>
  )
}

export function StatusPill({ status }: { status: "Disponible" | "Stock bajo" | "Agotado" }) {
  const styles = {
    Disponible: "border-emerald-200 bg-emerald-50 text-emerald-700",
    "Stock bajo": "border-amber-200 bg-amber-50 text-amber-700",
    Agotado: "border-rose-200 bg-rose-50 text-rose-700",
  }
  return (
    <span className={`inline-flex items-center gap-1.5 rounded-full border px-2.5 py-1 text-xs font-semibold ${styles[status]}`}>
      <span className="h-1.5 w-1.5 rounded-full bg-current" />
      {status}
    </span>
  )
}
