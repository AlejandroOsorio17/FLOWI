import React from "react"

export type View = "list" | "register"

export function Badge({ color, children }: { color: string; children: React.ReactNode }) {
  const map: Record<string, string> = {
    blue: "bg-[#dce8f5] text-[#2a5a8a] border border-[#b8d0e8]",
    green: "bg-[#d4edda] text-[#3a6b47] border border-[#b8ddc4]",
    amber: "bg-[#f5ecd4] text-[#7a5a20] border border-[#e0d0a0]",
    rose: "bg-[#f5d4d4] text-[#7a3a3a] border border-[#e0b0b0]",
  }
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-[10px] font-medium tracking-wide ${map[color] ?? map.blue}`}>
      {children}
    </span>
  )
}

export function Field({
  label, type = "text", placeholder, value, onChange, required, select, options,
}: {
  label: string
  type?: string
  placeholder?: string
  value: string
  onChange: (v: string) => void
  required?: boolean
  select?: boolean
  options?: string[]
}) {
  const base =
    "w-full bg-[#f5f0e8] border border-[#d4c9b0] text-[#3d2e14] rounded px-3 py-2 text-sm placeholder:text-[#a09070] focus:outline-none focus:border-[#8a7a60] transition-all"
  return (
    <div className="flex flex-col gap-1.5">
      <label className="text-xs font-medium tracking-wide" style={{ color: "#6b5a3e" }}>
        {label}
        {required && <span className="ml-1" style={{ color: "#7a3a3a" }}>*</span>}
      </label>
      {select ? (
        <select className={base} value={value} onChange={(e) => onChange(e.target.value)}>
          <option value="">Seleccionar…</option>
          {options?.map((o) => <option key={o} value={o}>{o}</option>)}
        </select>
      ) : (
        <input
          type={type}
          className={base}
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
      )}
    </div>
  )
}

export function EmptyState({ icon, title, subtitle, onAction, actionLabel }: {
  icon: string; title: string; subtitle: string; onAction: () => void; actionLabel: string
}) {
  return (
    <div className="flex flex-col items-center justify-center py-24 gap-5">
      <div className="w-16 h-16 rounded-2xl flex items-center justify-center text-3xl"
        style={{ backgroundColor: "#e8e0d0", border: "1px solid #d4c9b0" }}>
        {icon}
      </div>
      <div className="text-center">
        <p className="font-semibold text-base" style={{ color: "#3d2e14" }}>{title}</p>
        <p className="text-sm mt-1" style={{ color: "#8a7a60" }}>{subtitle}</p>
      </div>
      <button
        onClick={onAction}
        className="mt-2 px-5 py-2 text-sm font-semibold rounded transition-all duration-150"
        style={{ backgroundColor: "#e8e0d0", color: "#3d2e14", border: "1px solid #d4c9b0" }}
        onMouseEnter={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#ddd4c0" }}
        onMouseLeave={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#e8e0d0" }}
      >
        {actionLabel}
      </button>
    </div>
  )
}

export function Toast({ message, type }: { message: string; type: "success" | "error" }) {
  return (
    <div
      className="fixed bottom-6 right-6 z-50 flex items-center gap-3 px-4 py-3 rounded text-sm font-medium shadow-lg"
      style={{
        backgroundColor: type === "success" ? "#d4edda" : "#f5d4d4",
        border: `1px solid ${type === "success" ? "#b8ddc4" : "#e0b0b0"}`,
        color: type === "success" ? "#3a6b47" : "#7a3a3a",
      }}
    >
      <span>{type === "success" ? "✓" : "✗"}</span>
      {message}
    </div>
  )
}

export function Header({ title, subtitle, view, onToggle }: {
  title: string; subtitle: string; view: View; onToggle: () => void
}) {
  return (
    <div className="flex items-center justify-between px-8 py-5 border-b" style={{ borderColor: "#d4c9b0" }}>
      <div>
        <h1 className="text-xl font-semibold" style={{ color: "#3d2e14" }}>{title}</h1>
        <p className="text-xs mt-0.5" style={{ color: "#8a7a60" }}>{subtitle}</p>
      </div>
      <button
        onClick={onToggle}
        className="px-4 py-2 text-sm font-medium rounded transition-all duration-150"
        style={{ backgroundColor: "#e8e0d0", color: "#5a4a30", border: "1px solid #d4c9b0" }}
        onMouseEnter={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#ddd4c0" }}
        onMouseLeave={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#e8e0d0" }}
      >
        {view === "list" ? "+ Registrar" : "← Ver listado"}
      </button>
    </div>
  )
}

export function FormPanel({ title, onCancel, onSubmit, children }: {
  title: string; onCancel: () => void; onSubmit: () => void; children: React.ReactNode
}) {
  return (
    <div className="p-8 max-w-2xl">
      <div className="rounded p-6" style={{ backgroundColor: "#e8e0d0", border: "1px solid #d4c9b0" }}>
        <h2 className="text-sm font-semibold mb-5 flex items-center gap-2" style={{ color: "#3d2e14" }}>
          <span className="w-1 h-4 rounded-full inline-block" style={{ backgroundColor: "#6b5a3e" }}></span>
          {title}
        </h2>
        {children}
        <div className="flex gap-3 mt-6 pt-5 border-t" style={{ borderColor: "#d4c9b0" }}>
          <button
            onClick={onSubmit}
            className="px-5 py-2 text-sm font-semibold rounded transition-all duration-150"
            style={{ backgroundColor: "#ddd4c0", color: "#3d2e14", border: "1px solid #c4b898" }}
            onMouseEnter={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#ccc0a8" }}
            onMouseLeave={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#ddd4c0" }}
          >
            Guardar registro
          </button>
          <button
            onClick={onCancel}
            className="px-5 py-2 text-sm font-medium rounded transition-all duration-150"
            style={{ backgroundColor: "transparent", color: "#8a7a60", border: "1px solid #d4c9b0" }}
            onMouseEnter={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#ddd4c0" }}
            onMouseLeave={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "transparent" }}
          >
            Cancelar
          </button>
        </div>
      </div>
    </div>
  )
}
