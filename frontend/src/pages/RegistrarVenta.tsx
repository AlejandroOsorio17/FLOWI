import { useMemo, useState } from "react"
import { PageHeading, SearchIcon, StoreIcon } from "../components/SprintUI"
import { formatCurrency, useInventory } from "../data/InventoryContext"
import { useStoreSelection } from "../data/StoreSelectionContext"

function CartIcon() {
  return <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M3 4h2l2.2 10.2a2 2 0 0 0 2 1.6h7.9a2 2 0 0 0 2-1.6L20.5 8H6.3" /><circle cx="10" cy="20" r="1" /><circle cx="18" cy="20" r="1" /></svg>
}

export default function RegistrarVenta() {
  const { products, registerSale } = useInventory()
  const { selectedStore } = useStoreSelection()
  const available = products.filter((product) => product.quantity > 0)
  const [selectedId, setSelectedId] = useState(available[0]?.id ?? "")
  const [quantity, setQuantity] = useState(1)
  const [query, setQuery] = useState("")
  const [notice, setNotice] = useState<{ type: "success" | "error"; message: string } | null>(null)
  const selected = products.find((product) => product.id === selectedId)
  const visibleProducts = useMemo(() => products.filter((product) =>
    `${product.name} ${product.sku}`.toLowerCase().includes(query.toLowerCase()),
  ), [products, query])
  const total = (selected?.price ?? 0) * quantity

  const confirmSale = async () => {
    const result = await registerSale(selectedId, quantity)
    setNotice({ type: result.ok ? "success" : "error", message: result.message })
    if (result.ok) setQuantity(1)
    setTimeout(() => setNotice(null), 5000)
  }

  return (
    <div className="min-h-full bg-slate-50 px-5 py-7 sm:px-8 lg:px-12">
      <div className="mx-auto max-w-6xl">
        <PageHeading
          eyebrow={<span className="text-slate-500">Ventas <span className="mx-2">›</span> <span className="text-slate-800">Nueva venta</span></span>}
          title="Registrar venta"
          subtitle="Selecciona el producto y confirma la cantidad vendida."
          action={<div className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white px-4 py-2 shadow-sm"><StoreIcon className="h-5 w-5 text-slate-500" /><div><div className="text-xs font-semibold text-slate-800">{selectedStore?.name ?? "Tienda no seleccionada"}</div><div className="text-[11px] text-slate-500">{selectedStore?.code ?? "-"}</div></div></div>}
        />

        {notice && <div className={`mt-5 rounded-xl border px-4 py-3 text-sm font-medium ${notice.type === "success" ? "border-emerald-200 bg-emerald-50 text-emerald-800" : "border-rose-200 bg-rose-50 text-rose-800"}`}>{notice.message}</div>}

        <div className="grid gap-6 py-7 lg:grid-cols-[minmax(0,1fr)_340px] lg:items-start">
          <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
            <div className="p-5">
              <Step number="1" title="Selecciona un producto" subtitle="Consulta la disponibilidad actual en tienda." />
              <label className="relative mt-5 block">
                <SearchIcon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar por nombre o SKU" className="h-11 w-full rounded-lg border border-slate-200 pl-10 pr-3 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100" />
              </label>
            </div>
            <div className="divide-y divide-slate-100 border-y border-slate-100">
              {visibleProducts.map((product) => {
                const isSelected = product.id === selectedId
                const disabled = product.quantity === 0
                return (
                  <button
                    key={product.id}
                    disabled={disabled}
                    onClick={() => { setSelectedId(product.id); setQuantity(1); setNotice(null) }}
                    className={`grid w-full grid-cols-[24px_48px_minmax(0,1fr)_auto] items-center gap-3 px-5 py-4 text-left transition ${isSelected ? "bg-blue-50" : "hover:bg-slate-50"} ${disabled ? "cursor-not-allowed opacity-50" : ""}`}
                  >
                    <span className={`flex h-5 w-5 items-center justify-center rounded-full border ${isSelected ? "border-blue-600 bg-blue-600 text-white" : "border-slate-300"}`}>{isSelected && <span className="text-xs">✓</span>}</span>
                    <span className="flex h-12 w-12 items-center justify-center rounded-xl bg-slate-100 text-slate-500"><CartIcon /></span>
                    <span><span className="block font-semibold text-slate-900">{product.name}</span><span className="mt-0.5 block text-xs text-slate-500">{product.sku} · {product.detail}</span></span>
                    <span className="text-right"><span className="block font-bold text-slate-900">{formatCurrency(product.price)}</span><span className={`mt-1 block text-xs font-medium ${product.quantity === 0 ? "text-rose-600" : product.quantity <= product.minimum ? "text-amber-600" : "text-emerald-600"}`}>{product.quantity} disponibles</span></span>
                  </button>
                )
              })}
            </div>
            <div className="p-5">
              <Step number="2" title="Indica la cantidad" subtitle="Unidades vendidas de la referencia seleccionada." />
              <div className="mt-5 flex flex-wrap items-end gap-4">
                <div><label className="mb-2 block text-xs font-semibold text-slate-700">Cantidad vendida</label><div className="flex h-11 overflow-hidden rounded-lg border border-slate-200"><button onClick={() => setQuantity((value) => Math.max(1, value - 1))} className="w-11 text-slate-500 transition hover:bg-slate-50">−</button><div className="flex w-16 items-center justify-center border-x border-slate-200 font-semibold">{quantity}</div><button onClick={() => setQuantity((value) => value + 1)} className="w-11 text-slate-500 transition hover:bg-slate-50">+</button></div></div>
                <div className="pb-3 text-sm text-slate-500">Stock disponible: <strong className="text-slate-800">{selected?.quantity ?? 0} unidades</strong></div>
              </div>
            </div>
          </section>

          <aside className="rounded-2xl border border-slate-200 bg-white shadow-sm lg:sticky lg:top-6">
            <div className="border-b border-slate-100 p-5"><h2 className="font-bold text-slate-900">Resumen de la venta</h2><p className="mt-1 text-xs text-slate-500">Revisa la información antes de confirmar.</p></div>
            <div className="p-5">
              {selected ? <div className="rounded-xl bg-slate-50 p-4"><div className="font-semibold text-slate-900">{selected.name}</div><div className="mt-1 text-xs text-slate-500">{selected.sku} · {selected.detail}</div><div className="my-4 border-t border-slate-200" /><div className="flex justify-between text-sm text-slate-500"><span>{quantity} × {formatCurrency(selected.price)}</span><strong className="text-slate-800">{formatCurrency(total)}</strong></div></div> : <div className="rounded-xl bg-slate-50 p-4 text-sm text-slate-500">Selecciona un producto.</div>}
              <div className="space-y-3 border-b border-slate-100 py-5 text-sm"><div className="flex justify-between text-slate-500"><span>Subtotal</span><span className="font-medium text-slate-700">{formatCurrency(total)}</span></div><div className="flex justify-between text-slate-500"><span>Impuestos</span><span className="font-medium text-slate-700">Incluidos</span></div></div>
              <div className="flex items-center justify-between py-5"><span className="font-semibold text-slate-600">Total</span><strong className="text-2xl text-slate-950">{formatCurrency(total)}</strong></div>
              <button onClick={confirmSale} disabled={!selected} className="h-11 w-full rounded-lg bg-blue-600 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-slate-300">✓ &nbsp; Confirmar venta</button>
              <p className="mt-3 text-center text-[11px] text-slate-400">Al confirmar, el inventario se actualizará automáticamente.</p>
            </div>
          </aside>
        </div>
      </div>
    </div>
  )
}

function Step({ number, title, subtitle }: { number: string; title: string; subtitle: string }) {
  return <div className="flex gap-3"><span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-sm font-bold text-blue-600">{number}</span><div><h2 className="font-bold text-slate-900">{title}</h2><p className="mt-0.5 text-sm text-slate-500">{subtitle}</p></div></div>
}
