import { useEffect, useMemo, useState } from "react"
import { Link } from "react-router"
import Inventario from "./Inventario"
import RegistrarVenta from "./RegistrarVenta"
import IngresarStock from "./IngresarStock"
import { apiFetch } from "../api"
import { useInventory } from "../data/InventoryContext"
import { useStoreSelection, type SelectedStore } from "../data/StoreSelectionContext"
import { useAuth } from "../auth/AuthContext"

type StoreView = "list" | "inventory" | "sale" | "stock"

type StoreDto = {
  id: number
  code: string
  name: string
  address: string
  city: string
}

export default function Tiendas() {
  const [view, setView] = useState<StoreView>("list")
  const [stores, setStores] = useState<StoreDto[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { reload } = useInventory()
  const { selectStore } = useStoreSelection()
  const { role } = useAuth()
  const canManageStock = role === "ADMIN" || role === "BODEGUERO"

  useEffect(() => {
    let cancelled = false
    async function load() {
      setLoading(true)
      setError(null)
      try {
        const res = await apiFetch("/api/stores")
        const data = await res.json().catch(() => ({}))
        if (!res.ok) throw new Error(data?.message || "Error consultando tiendas")
        if (!cancelled) setStores(Array.isArray(data) ? (data as StoreDto[]) : [])
      } catch (e) {
        if (!cancelled) setError(e instanceof Error ? e.message : "Error consultando tiendas")
      } finally {
        if (!cancelled) setLoading(false)
      }
    }
    load()
    return () => {
      cancelled = true
    }
  }, [])

  const storesUi = useMemo(
    () =>
      stores.map((s) => ({
        key: s.id,
        id: s.id,
        code: s.code,
        name: s.name,
        city: s.city,
        nombre: s.name,
        ubicacion: `${s.city} · ${s.address}`,
        estado: "Activa" as const,
      })),
    [stores],
  )

  const goTo = (nextView: StoreView, store: SelectedStore) => {
    selectStore(store)
    reload(store.id).catch(() => {})
    setView(nextView)
  }

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
        {view === "inventory" ? <Inventario /> : view === "sale" ? <RegistrarVenta /> : <IngresarStock />}
      </div>
    )
  }

  return (
    <div className="flex h-full flex-col bg-white">
      <header className="flex items-center justify-between border-b border-slate-200 bg-white px-8 py-5">
        <div>
          <h1 className="text-xl font-bold text-slate-950">Tiendas</h1>
          <p className="mt-1 text-xs text-slate-500">Selecciona una tienda para gestionar sus operaciones.</p>
        </div>
        <button className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-600 transition hover:border-slate-300 hover:text-slate-900">
          Buscar tienda
        </button>
      </header>

      <div className="flex flex-col gap-3 px-8 py-6">
        {loading && (
          <div className="rounded-xl border border-slate-200 bg-white px-5 py-6 text-sm text-slate-600">
            Cargando tiendas...
          </div>
        )}
        {!loading && error && (
          <div className="rounded-xl border border-rose-200 bg-rose-50 px-5 py-6 text-sm text-rose-700">
            {error}
          </div>
        )}
        {!loading && !error && storesUi.length === 0 && (
          <div className="rounded-xl border border-slate-200 bg-white px-5 py-6 text-sm text-slate-600">
            No hay tiendas registradas.
          </div>
        )}

        {storesUi.map(({ key, id, code, name, city, nombre, ubicacion, estado }) => (
          <div
            key={key}
            className="flex flex-col gap-4 rounded-xl border border-slate-200 bg-white px-5 py-4 shadow-sm transition hover:border-slate-300 sm:flex-row sm:items-center sm:justify-between"
          >
            <div className="flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-50 text-blue-600">
                <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8">
                  <path d="M4 10v10h16V10M3 10l2-6h14l2 6" />
                  <path d="M3 10a3 3 0 0 0 4.5 2.6A3 3 0 0 0 12 12.6a3 3 0 0 0 4.5 0A3 3 0 0 0 21 10" />
                </svg>
              </span>
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-sm font-semibold text-slate-900">{nombre}</span>
                  <span
                    className={`rounded-full px-2 py-0.5 text-[10px] font-semibold ${
                      estado === "Activa" ? "bg-emerald-50 text-emerald-700" : "bg-rose-50 text-rose-700"
                    }`}
                  >
                    {estado}
                  </span>
                </div>
                <span className="mt-1 block text-xs text-slate-500">{ubicacion}</span>
              </div>
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => goTo("inventory", { id, code, name, city })}
                className="rounded-lg border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-600 transition hover:border-blue-300 hover:text-blue-600"
              >
                Ver inventario
              </button>
              <button
                disabled={estado !== "Activa"}
                onClick={() => goTo("sale", { id, code, name, city })}
                className="rounded-lg bg-blue-600 px-3 py-2 text-xs font-semibold text-white transition hover:bg-blue-700 disabled:bg-slate-300"
              >
                Registrar venta
              </button>
              {canManageStock && (
                <button
                  disabled={estado !== "Activa"}
                  onClick={() => goTo("stock", { id, code, name, city })}
                  className="rounded-lg border border-emerald-300 bg-emerald-50 px-3 py-2 text-xs font-semibold text-emerald-700 transition hover:bg-emerald-100 disabled:bg-slate-300 disabled:text-slate-500"
                >
                  Ingresar stock
                </button>
              )}
            </div>
          </div>
        ))}
      </div>

      <div className="mt-auto border-t border-slate-200 px-8 py-6">
        <Link to="/tiendas/vacio" className="text-sm text-slate-500 transition hover:text-slate-900">
          Ver vista sin tiendas disponibles →
        </Link>
      </div>
    </div>
  )
}
