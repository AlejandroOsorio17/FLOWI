import { useEffect, useMemo, useState } from "react"
import { PageHeading, StoreIcon } from "../components/SprintUI"
import { apiFetch, ApiError } from "../api"
import { useInventory } from "../data/InventoryContext"
import { useStoreSelection } from "../data/StoreSelectionContext"

type ProductoApi = {
  id: number
  code: string
  name: string
}

export default function IngresarStock() {
  const { selectedStore } = useStoreSelection()
  const { reload } = useInventory()
  const [productos, setProductos] = useState<ProductoApi[]>([])
  const [loadingProductos, setLoadingProductos] = useState(false)
  const [productId, setProductId] = useState<string>("")
  const [quantity, setQuantity] = useState(1)
  const [notice, setNotice] = useState<{ type: "success" | "error"; message: string } | null>(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    let cancelled = false
    async function load() {
      setLoadingProductos(true)
      try {
        const res = await apiFetch("/api/products")
        const body = await res.json().catch(() => ({}))
        if (!res.ok) throw new Error(body?.message || "Error consultando productos")
        if (!cancelled) {
          const list = Array.isArray(body) ? (body as ProductoApi[]) : []
          setProductos(list)
          if (list.length > 0) setProductId(String(list[0].id))
        }
      } catch {
        if (!cancelled) setProductos([])
      } finally {
        if (!cancelled) setLoadingProductos(false)
      }
    }
    load()
    return () => {
      cancelled = true
    }
  }, [])

  const selectedProduct = useMemo(
    () => productos.find((p) => String(p.id) === productId),
    [productos, productId],
  )

  const showNotice = (type: "success" | "error", message: string) => {
    setNotice({ type, message })
    setTimeout(() => setNotice(null), 5000)
  }

  const handleSubmit = async () => {
    if (!selectedStore) {
      showNotice("error", "No hay una tienda seleccionada.")
      return
    }
    if (!productId) {
      showNotice("error", "Selecciona un producto.")
      return
    }
    if (!Number.isInteger(quantity) || quantity < 1) {
      showNotice("error", "La cantidad debe ser un número entero mayor o igual a 1.")
      return
    }

    setSubmitting(true)
    try {
      const res = await apiFetch("/api/inventory", {
        method: "POST",
        body: JSON.stringify({
          storeId: selectedStore.id,
          productId: Number(productId),
          quantity,
        }),
      })
      const body = await res.json().catch(() => ({}))
      if (!res.ok) {
        if (res.status === 404) throw new Error(body?.message || "No se encontró la tienda o el producto indicado.")
        if (res.status === 400) throw new Error(body?.message || "Datos inválidos. Revisa la cantidad ingresada.")
        throw new Error(body?.message || body?.error || "Error ingresando stock")
      }

      showNotice("success", `Stock actualizado: ${body.productName} ahora tiene ${body.quantity} unidades`)
      setQuantity(1)
      await reload(selectedStore.id)
    } catch (e) {
      const message = e instanceof ApiError ? e.message : e instanceof Error ? e.message : "Error ingresando stock"
      showNotice("error", message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="min-h-full bg-slate-50 px-5 py-7 sm:px-8 lg:px-12">
      <div className="mx-auto max-w-3xl">
        <PageHeading
          eyebrow={
            <span className="inline-flex items-center gap-2">
              <StoreIcon className="h-4 w-4" />
              {selectedStore ? `${selectedStore.name} (${selectedStore.code})` : "Tienda no seleccionada"}
            </span>
          }
          title="Ingresar stock"
          subtitle="Suma unidades al inventario de la tienda seleccionada."
        />

        {notice && (
          <div
            className={`mt-5 rounded-xl border px-4 py-3 text-sm font-medium ${
              notice.type === "success"
                ? "border-emerald-200 bg-emerald-50 text-emerald-800"
                : "border-rose-200 bg-rose-50 text-rose-800"
            }`}
          >
            {notice.message}
          </div>
        )}

        <section className="mt-7 overflow-hidden rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          {loadingProductos ? (
            <div className="text-sm text-slate-600">Cargando productos...</div>
          ) : productos.length === 0 ? (
            <div className="text-sm text-slate-600">No hay productos; crea uno primero.</div>
          ) : (
            <div className="space-y-5">
              <div>
                <label className="mb-2 block text-xs font-semibold text-slate-700">Producto</label>
                <select
                  value={productId}
                  onChange={(event) => setProductId(event.target.value)}
                  className="h-11 w-full rounded-lg border border-slate-200 px-3 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                >
                  {productos.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name} ({p.code})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="mb-2 block text-xs font-semibold text-slate-700">Cantidad</label>
                <div className="flex h-11 w-40 overflow-hidden rounded-lg border border-slate-200">
                  <button
                    onClick={() => setQuantity((value) => Math.max(1, value - 1))}
                    className="w-11 text-slate-500 transition hover:bg-slate-50"
                  >
                    −
                  </button>
                  <input
                    type="number"
                    min={1}
                    step={1}
                    value={quantity}
                    onChange={(event) => setQuantity(Math.max(1, parseInt(event.target.value, 10) || 1))}
                    className="w-full border-x border-slate-200 text-center font-semibold outline-none"
                  />
                  <button
                    onClick={() => setQuantity((value) => value + 1)}
                    className="w-11 text-slate-500 transition hover:bg-slate-50"
                  >
                    +
                  </button>
                </div>
              </div>

              <button
                onClick={handleSubmit}
                disabled={submitting || !selectedProduct}
                className="h-11 w-full rounded-lg bg-blue-600 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-slate-300"
              >
                {submitting ? "Guardando..." : "✓  Ingresar stock"}
              </button>
            </div>
          )}
        </section>
      </div>
    </div>
  )
}
