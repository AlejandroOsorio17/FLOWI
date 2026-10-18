import { useState } from "react"
import { Link } from "react-router"
import Inventario from "./Inventario"
import RegistrarVenta from "./RegistrarVenta"

type StoreView = "list" | "inventory" | "sale"

export default function Tiendas() {
  const [view, setView] = useState<StoreView>("list")

  if (view !== "list") {
    return (
      <div className="min-h-full bg-white">
        <div className="border-b border-slate-200 bg-white px-5 py-3 sm:px-8">
          <button
            onClick={() => setView("list")}
            className="rounded-lg px-2 py-1.5 text-sm font-semibold text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
          >
            ← Volver a tiendas
          </button>
        </div>
        {view === "inventory" ? <Inventario /> : <RegistrarVenta />}
      </div>
    )
  }

  return (
    <div className="flex h-full flex-col bg-white">
      <header
        className="flex items-center justify-between border-b border-slate-200 bg-white px-8 py-5"
      >
        <div>
          <h1 className="text-xl font-bold text-slate-950">Tiendas</h1>
          <p className="mt-1 text-xs text-slate-500">Selecciona una tienda para gestionar sus operaciones.</p>
        </div>
        <button
          className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-600 transition hover:border-slate-300 hover:text-slate-900"
        >
          Buscar tienda
        </button>
      </header>

      <div className="flex flex-col gap-3 px-8 py-6">
        {[
          { nombre: "Tienda Medellín Poblado", ubicacion: "Medellín, Antioquia", estado: "Activa" },
          { nombre: "Tienda Rionegro", ubicacion: "Rionegro, Antioquia", estado: "Activa" },
          { nombre: "Tienda El Carmen de Viboral", ubicacion: "El Carmen, Antioquia", estado: "Inactiva" },
        ].map(({ nombre, ubicacion, estado }) => (
          <div
            key={nombre}
            className="flex flex-col gap-4 rounded-xl border border-slate-200 bg-white px-5 py-4 shadow-sm transition hover:border-slate-300 sm:flex-row sm:items-center sm:justify-between"
          >
            <div className="flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-50 text-blue-600">
                <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M4 10v10h16V10M3 10l2-6h14l2 6" /><path d="M3 10a3 3 0 0 0 4.5 2.6A3 3 0 0 0 12 12.6a3 3 0 0 0 4.5 0A3 3 0 0 0 21 10" /></svg>
              </span>
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-sm font-semibold text-slate-900">{nombre}</span>
                  <span className={`rounded-full px-2 py-0.5 text-[10px] font-semibold ${estado === "Activa" ? "bg-emerald-50 text-emerald-700" : "bg-rose-50 text-rose-700"}`}>{estado}</span>
                </div>
                <span className="mt-1 block text-xs text-slate-500">{ubicacion}</span>
              </div>
            </div>
            <div className="flex gap-2">
              <button onClick={() => setView("inventory")} className="rounded-lg border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-600 transition hover:border-blue-300 hover:text-blue-600">Ver inventario</button>
              <button disabled={estado !== "Activa"} onClick={() => setView("sale")} className="rounded-lg bg-blue-600 px-3 py-2 text-xs font-semibold text-white transition hover:bg-blue-700 disabled:bg-slate-300">Registrar venta</button>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-auto border-t border-slate-200 px-8 py-6">
        <Link
          to="/tiendas/vacio"
          className="text-sm text-slate-500 transition hover:text-slate-900"
        >
          Ver vista sin tiendas disponibles →
        </Link>
      </div>
    </div>
  )
}
