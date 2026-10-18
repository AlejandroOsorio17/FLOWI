import { useMemo, useState } from "react"
import { AlertIcon, PageHeading, SearchIcon } from "../components/SprintUI"
import { getInventoryStatus, useInventory } from "../data/InventoryContext"

export default function Disponibilidad() {
  const { products } = useInventory()
  const [query, setQuery] = useState("")
  const [onlyLow, setOnlyLow] = useState(true)
  const lowProducts = products.filter((product) => getInventoryStatus(product) === "Stock bajo")
  const displayed = useMemo(() => products.filter((product) => {
    const matches = `${product.name} ${product.sku}`.toLowerCase().includes(query.toLowerCase())
    return matches && (!onlyLow || getInventoryStatus(product) === "Stock bajo")
  }), [onlyLow, products, query])

  return (
    <div className="min-h-full bg-slate-50">
      <div className="border-b border-slate-200 bg-white px-5 py-6 sm:px-8">
        <div className="mx-auto max-w-5xl">
          <PageHeading title="Disponibilidad de productos" subtitle="Identifica los productos que requieren atención para evitar faltantes." />
        </div>
      </div>
      <div className="mx-auto max-w-5xl px-5 py-8 sm:px-8">
        {lowProducts.length > 0 ? (
          <div className="mb-6 flex items-center justify-between rounded-xl border border-amber-300 bg-amber-50 px-5 py-4">
            <div><div className="text-sm font-semibold text-amber-700">Estado del inventario</div><div className="mt-1 font-bold text-amber-950">{lowProducts.length} productos requieren atención</div></div>
            <div className="rounded-lg bg-amber-100 px-4 py-2 text-amber-700"><strong className="text-2xl">{lowProducts.length}</strong> <span className="text-sm">alertas</span></div>
          </div>
        ) : (
          <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 px-5 py-5 text-emerald-800">
            <div className="font-bold">Inventario en niveles adecuados</div>
            <div className="mt-1 text-sm">No existen productos con bajo inventario.</div>
          </div>
        )}

        <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
          <div className="flex flex-col gap-4 border-b border-slate-200 px-5 py-5 lg:flex-row lg:items-center lg:justify-between">
            <div><h2 className="font-bold text-slate-900">Inventario disponible</h2><p className="mt-1 text-sm text-slate-500">El nivel mínimo determina cuándo un producto necesita reposición.</p></div>
            <div className="flex flex-col gap-2 sm:flex-row">
              <label className="relative">
                <SearchIcon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar producto o SKU" className="h-10 w-full rounded-lg border border-slate-200 pl-10 pr-3 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 sm:w-64" />
              </label>
              <div className="flex rounded-lg bg-slate-100 p-1 text-sm font-semibold">
                <button onClick={() => setOnlyLow(true)} className={`rounded-md px-3 py-1.5 transition ${onlyLow ? "bg-white text-amber-700 shadow-sm" : "text-slate-500"}`}>Bajo inventario</button>
                <button onClick={() => setOnlyLow(false)} className={`rounded-md px-3 py-1.5 transition ${!onlyLow ? "bg-white text-blue-600 shadow-sm" : "text-slate-500"}`}>Todos</button>
              </div>
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full min-w-[820px] text-left text-sm">
              <thead className="bg-slate-50 text-[11px] uppercase tracking-wider text-slate-500"><tr><th className="px-5 py-3.5">Producto</th><th className="px-5 py-3.5">SKU</th><th className="px-5 py-3.5">Inventario actual</th><th className="px-5 py-3.5">Nivel mínimo</th><th className="px-5 py-3.5">Unidades faltantes</th><th className="px-5 py-3.5">Estado</th></tr></thead>
              <tbody className="divide-y divide-slate-100">
                {displayed.map((product) => {
                  const low = getInventoryStatus(product) === "Stock bajo"
                  return (
                    <tr key={product.id} className={low ? "bg-amber-50/40" : "hover:bg-slate-50"}>
                      <td className="px-5 py-4"><div className="font-semibold text-slate-900">{product.name}</div><div className="mt-0.5 text-xs text-slate-400">{product.id}</div></td>
                      <td className="px-5 py-4 text-slate-500">{product.sku}</td>
                      <td className={`px-5 py-4 font-bold ${low ? "text-amber-700" : "text-slate-900"}`}>{product.quantity}</td>
                      <td className="px-5 py-4 text-slate-500">{product.minimum}</td>
                      <td className="px-5 py-4 font-semibold text-slate-700">{Math.max(product.minimum - product.quantity, 0)}</td>
                      <td className="px-5 py-4">{low ? <span className="inline-flex rounded-full border border-amber-300 bg-white px-2.5 py-1 text-xs font-semibold text-amber-700">Bajo inventario</span> : <span className="text-xs text-slate-500">{getInventoryStatus(product)}</span>}</td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
            {displayed.length === 0 && (
              <div className="flex flex-col items-center px-5 py-12 text-center"><AlertIcon className="mb-3 h-8 w-8 text-emerald-500" /><div className="font-semibold text-slate-800">No hay productos con bajo inventario</div><div className="mt-1 text-sm text-slate-500">Todos los productos se encuentran en niveles adecuados.</div></div>
            )}
          </div>
        </section>
      </div>
    </div>
  )
}
