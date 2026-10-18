import { useState } from "react"
import { NavLink, useNavigate } from "react-router"
import { useAuth } from "./auth/AuthContext"

const HomeIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
    <polyline points="9 22 9 12 15 12 15 22" />
  </svg>
)

const BoxIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
    <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
    <line x1="12" y1="22.08" x2="12" y2="12" />
  </svg>
)

const StoreIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M3 9l1-6h16l1 6" />
    <path d="M3 9a2 2 0 0 0 2 2 2 2 0 0 0 2-2 2 2 0 0 0 2 2 2 2 0 0 0 2-2 2 2 0 0 0 2 2 2 2 0 0 0 2-2" />
    <path d="M5 11v9h14v-9" />
    <rect x="9" y="15" width="6" height="5" />
  </svg>
)

const WarehouseIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M22 8.35V20a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V8.35A2 2 0 0 1 3.26 6.5l8-3.2a2 2 0 0 1 1.48 0l8 3.2A2 2 0 0 1 22 8.35z" />
    <path d="M6 18h12" />
    <path d="M6 14h12" />
    <rect x="8" y="14" width="8" height="8" />
  </svg>
)

const MenuIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
    <line x1="3" y1="6" x2="21" y2="6" />
    <line x1="3" y1="12" x2="21" y2="12" />
    <line x1="3" y1="18" x2="21" y2="18" />
  </svg>
)

const navItems = [
  { label: "Inicio", to: "/", icon: HomeIcon, exact: true, adminOnly: false },
  { label: "Productos", to: "/productos", icon: BoxIcon, exact: false, adminOnly: false },
  { label: "Tiendas", to: "/tiendas", icon: StoreIcon, exact: false, adminOnly: false },
  { label: "Centros de distribución", to: "/centros", icon: WarehouseIcon, exact: false, adminOnly: true },
]

export default function Sidebar() {
  const [open, setOpen] = useState(true)
  const { logout, role } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate("/login", { replace: true })
  }

  return (
    <>
      {/* Toggle button — floats outside sidebar */}
      <button
        onClick={() => setOpen((v) => !v)}
        className="fixed z-20 flex items-center justify-center w-7 h-7 rounded-lg transition-all duration-300"
        style={{
          top: "14px",
          left: open ? "172px" : "12px",
          backgroundColor: open ? "#2563EB" : "#1E293B",
          color: "#ffffff",
          border: "none",
          transition: "left 0.3s, background-color 0.15s",
          boxShadow: "0 2px 8px rgba(0,0,0,0.18)",
        }}
        title={open ? "Collapse" : "Expand"}
      >
        <MenuIcon />
      </button>

      <aside
        className="flex flex-col h-full shrink-0 transition-all duration-300 overflow-hidden"
        style={{
          width: open ? "200px" : "0px",
          backgroundColor: "#1E293B",
          borderRight: "none",
        }}
      >
        {/* Logo */}
        <div className="px-5 pt-5 pb-4" style={{ borderBottom: "1px solid rgba(255,255,255,0.08)" }}>
          <div className="flex items-center gap-2.5">
            <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-600 text-sm font-bold text-white">F</span>
            <div>
              <span className="block text-xl font-bold tracking-tight text-white">Flowi</span>
              <span className="block text-[9px] text-slate-400">Supply chain, simplified</span>
            </div>
          </div>
        </div>

        {/* Nav items */}
        <nav className="flex-1 px-3 py-4 flex flex-col gap-1">
          {navItems.filter((item) => !item.adminOnly || role === "admin").map(({ label, to, icon: Icon, exact }) => (
            <NavLink
              key={to}
              to={to}
              end={exact}
              className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150"
              style={({ isActive }) => ({
                backgroundColor: isActive ? "#2563EB" : "transparent",
                color: isActive ? "#ffffff" : "rgba(255,255,255,0.65)",
              })}
              onMouseEnter={(e) => {
                const el = e.currentTarget as HTMLElement
                if (el.getAttribute("aria-current") !== "page") {
                  el.style.backgroundColor = "rgba(255,255,255,0.08)"
                  el.style.color = "#ffffff"
                }
              }}
              onMouseLeave={(e) => {
                const el = e.currentTarget as HTMLElement
                if (el.getAttribute("aria-current") !== "page") {
                  el.style.backgroundColor = "transparent"
                  el.style.color = "rgba(255,255,255,0.65)"
                }
              }}
            >
              <span className="mt-0.5 shrink-0"><Icon /></span>
              <span className="min-w-0 whitespace-normal leading-4">{label}</span>
            </NavLink>
          ))}

        </nav>
        <div className="m-3 rounded-xl border border-white/10 p-3">
          <div className="flex items-center gap-2.5">
            <span className="flex h-8 w-8 items-center justify-center rounded-full bg-slate-700 text-[10px] font-bold text-white">{role === "admin" ? "AD" : "OP"}</span>
            <div className="min-w-0">
              <div className="truncate text-xs font-semibold text-white">{role === "admin" ? "Administrador" : "Operador"}</div>
              <div className="truncate text-[10px] text-slate-400">{role === "admin" ? "Acceso completo" : "Acceso limitado"}</div>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="mt-3 flex w-full items-center justify-center gap-2 rounded-lg bg-white/10 px-3 py-2 text-xs font-semibold text-white transition hover:bg-white/15"
          >
            <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><path d="M10 17l5-5-5-5M15 12H3M14 3h5a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-5" /></svg>
            Cerrar sesión
          </button>
        </div>
      </aside>
    </>
  )
}
