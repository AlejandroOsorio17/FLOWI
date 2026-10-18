import { useMemo, useState } from "react"
import { AlertIcon, BoxIcon, PageHeading, SearchIcon, StatusPill, StoreIcon } from "../components/SprintUI"
import { getInventoryStatus, useInventory } from "../data/InventoryContext"

type Filter = "Todos" | "Disponibles" | "Stock bajo" | "Agotados"

export default function Inventario() {
  const { products } = useInventory()
  const [query, setQuery] = useState("")
  const [filter, setFilter] = useState<Filter>("Todos")
  const total = products.reduce((sum, product) => sum + product.quantity, 0)
  const low = products.filter((product) => getInventoryStatus(product) === "Stock bajo").length
  const empty = products.filter((product) => getInventoryStatus(product) === "Agotado").length

  const filtered = useMemo(() => products.filter((product) => {
    const status = getInventoryStatus(product)
    const matchesQuery = `${product.name} ${product.sku}`.toLowerCase().includes(query.toLowerCase())
    const matchesFilter =
      filter === "Todos" ||
      (filter === "Disponibles" && status === "Disponible") ||
      (filter === "Stock bajo" && status === "Stock bajo") ||
      (filter === "Agotados" && status === "Agotado")
    return matchesQuery && matchesFilter
  }), [filter, products, query])

  return (
    <div className="min-h-full bg-slate-50 px-5 py-7 sm:px-8 lg:px-12">
      <div className="mx-auto max-w-6xl">
        <PageHeading
          eyebrow={<span className="inline-flex items-center gap-2"><StoreIcon className="h-4 w-4" /> Tienda Medellín Poblado</span>}
          title="Inventario de tienda"
          subtitle="Consulta las existencias disponibles e identifica los productos que necesitan reposición."
          action={
            <div className="inline-flex items-center gap-2 rounded-lg border border-slate-200 bg-white px-3 py-2 text-xs text-slate-500 shadow-sm">
              <span className="h-2 w-2 rounded-full bg-emerald-500" />
              Inventario actualizado ahora
            </div>
          }
        />

        <div className="grid gap-4 py-7 md:grid-cols-3">
          <Summary title="Unidades disponibles" value={total} note={`${products.length} productos registrados`} tone="blue"><BoxIcon className="h-5 w-5" /></Summary>
          <Summary title="Con stock bajo" value={low} note="Requieren reposición pronto" tone="amber"><AlertIcon className="h-5 w-5" /></Summary>
          <Summary title="Productos agotados" value={empty} note="Sin unidades disponibles" tone="rose"><AlertIcon className="h-5 w-5" /></Summary>
        </div>

        <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
          <div className="flex flex-col gap-4 border-b border-slate-200 px-5 py-5 md:flex-row md:items-center md:justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900">Productos en inventario</h2>
              <p className="mt-1 text-sm text-slate-500">{filtered.length} de {products.length} productos</p>
            </div>
            <div className="flex flex-col gap-2 sm:flex-row">
              <label className="relative block">
                <SearchIcon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                <input
                  value={query}
                  onChange={(event) => setQuery(event.target.value)}
                  placeholder="Buscar por nombre o SKU"
                  className="h-10 w-full rounded-lg border border-slate-200 bg-white pl-10 pr-3 text-sm text-slate-700 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 sm:w-72"
                />
              </label>
              <select
                value={filter}
                onChange={(event) => setFilter(event.target.value as Filter)}
                className="h-10 rounded-lg border border-slate-200 bg-white px-3 text-sm text-slate-700 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 sm:w-40"
              >
                <option>Todos</option>
                <option>Disponibles</option>
                <option>Stock bajo</option>
                <option>Agotados</option>
              </select>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full min-w-[680px] text-left">
              <thead className="bg-slate-50 text-[11px] uppercase tracking-wider text-slate-500">
                <tr><th className="px-5 py-3.5">Producto</th><th className="px-5 py-3.5">Categoría</th><th className="px-5 py-3.5 text-right">Cantidad</th><th className="px-5 py-3.5">Estado</th></tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filtered.map((product) => {
                  const status = getInventoryStatus(product)
                  return (
                    <tr key={product.id} className="transition hover:bg-slate-50">
                      <td className="px-5 py-4"><div className="font-semibold text-slate-900">{product.name}</div><div className="mt-0.5 text-xs text-slate-500">{product.sku}</div></td>
                      <td className="px-5 py-4 text-sm text-slate-600">{product.category}</td>
                      <td className={`px-5 py-4 text-right font-bold ${status === "Agotado" ? "text-rose-600" : "text-slate-900"}`}>{product.quantity} <span className="text-xs font-normal text-slate-400">uds.</span></td>
                      <td className="px-5 py-4"><StatusPill status={status} /></td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
            {filtered.length === 0 && <div className="px-5 py-12 text-center text-sm text-slate-500">No hay productos que coincidan con la búsqueda.</div>}
          </div>
        </section>
      </div>
    </div>
  )
}

function Summary({ title, value, note, tone, children }: {
  title: string
  value: number
  note: string
  tone: "blue" | "amber" | "rose"
  children: React.ReactNode
}) {
  const toneClass = {
    blue: "bg-blue-50 text-blue-600",
    amber: "bg-amber-50 text-amber-600",
    rose: "bg-rose-50 text-rose-600",
  }[tone]
  return (
    <div className="flex items-start justify-between rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <div><div className="text-sm text-slate-500">{title}</div><div className="mt-1 text-3xl font-bold text-slate-950">{value}</div><div className="mt-1 text-xs text-slate-500">{note}</div></div>
      <div className={`rounded-xl p-3 ${toneClass}`}>{children}</div>
    </div>
  )
}
