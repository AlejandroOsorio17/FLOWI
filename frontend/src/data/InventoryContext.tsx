import { createContext, useContext, useMemo, useState, type ReactNode } from "react"
import { apiFetch } from "../api"

export interface InventoryProduct {
  id: string
  name: string
  sku: string
  category: string
  detail: string
  price: number
  quantity: number
  minimum: number
}

type InventoryItemApi = {
  id: number
  name: string
  sku: string
  category: string
  detail: string
  price: number
  quantity: number
  minimum: number
}

interface InventoryContextValue {
  products: InventoryProduct[]
  loading: boolean
  error: string | null
  reload: (storeId?: number) => Promise<void>
  registerSale: (productId: string, quantity: number) => Promise<{ ok: boolean; message: string }>
}

const InventoryContext = createContext<InventoryContextValue | null>(null)

function toUi(item: InventoryItemApi): InventoryProduct {
  return {
    id: String(item.id),
    name: item.name,
    sku: item.sku,
    category: item.category,
    detail: item.detail,
    price: item.price,
    quantity: item.quantity,
    minimum: item.minimum,
  }
}

function extractSaleErrorMessage(status: number, body: any): string {
  if (status === 409) {
    const disponible = body?.disponible
    const solicitado = body?.solicitado
    if (disponible !== undefined && solicitado !== undefined) {
      return `Stock insuficiente: disponibles ${disponible}, solicitados ${solicitado}`
    }
    return body?.message || "Stock insuficiente"
  }
  if (status === 403) {
    return "No tienes permiso"
  }
  if (status === 400) {
    const campos = body?.campos
    if (campos && typeof campos === "object") {
      const detalle = Object.entries(campos)
        .map(([campo, msg]) => `${campo}: ${msg}`)
        .join(" · ")
      if (detalle) return detalle
    }
    return body?.message || "Datos inválidos"
  }
  return body?.message || body?.error || "Error registrando venta"
}

export function InventoryProvider({ children }: { children: ReactNode }) {
  const [products, setProducts] = useState<InventoryProduct[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [storeId, setStoreId] = useState<number | null>(null)

  async function reload(nextStoreId?: number) {
    const sid = nextStoreId ?? storeId
    if (sid == null) return
    setStoreId(sid)
    setLoading(true)
    setError(null)
    try {
      const res = await apiFetch(`/api/inventory/${sid}/enriched`)
      const body = await res.json().catch(() => ({}))
      if (!res.ok) throw new Error(body?.message || "Error consultando inventario")
      setProducts(Array.isArray(body) ? (body as InventoryItemApi[]).map(toUi) : [])
    } catch (e) {
      setError(e instanceof Error ? e.message : "Error consultando inventario")
      setProducts([])
    } finally {
      setLoading(false)
    }
  }

  const value = useMemo<InventoryContextValue>(
    () => ({
      products,
      loading,
      error,
      reload,
      async registerSale(productId, quantity) {
        if (storeId == null) {
          return { ok: false, message: "No hay una tienda seleccionada." }
        }
        try {
          const res = await apiFetch("/api/sales", {
            method: "POST",
            body: JSON.stringify({
              storeId,
              items: [{ productId: Number(productId), cantidad: quantity }],
            }),
          })
          const body = await res.json().catch(() => ({}))
          if (!res.ok) {
            const message = extractSaleErrorMessage(res.status, body)
            return { ok: false, message }
          }

          await reload(storeId)
          return { ok: true, message: "Venta registrada. El inventario fue actualizado correctamente." }
        } catch (e) {
          return { ok: false, message: e instanceof Error ? e.message : "Error registrando venta" }
        }
      },
    }),
    [products, loading, error, storeId],
  )

  return <InventoryContext.Provider value={value}>{children}</InventoryContext.Provider>
}

export function useInventory() {
  const context = useContext(InventoryContext)
  if (!context) throw new Error("useInventory must be used inside InventoryProvider")
  return context
}

export function getInventoryStatus(product: InventoryProduct) {
  if (product.quantity === 0) return "Agotado"
  if (product.quantity <= product.minimum) return "Stock bajo"
  return "Disponible"
}

export function formatCurrency(value: number) {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    maximumFractionDigits: 0,
  }).format(value).replace("COP", "$")
}
