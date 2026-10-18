import { createContext, useContext, useMemo, useState, type ReactNode } from "react"

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

const initialInventory: InventoryProduct[] = [
  { id: "P001", name: "Camiseta Algodón Slim Fit", sku: "CAM-SLM-001", category: "Tops", detail: "Blanco · Talla M", price: 89900, quantity: 42, minimum: 20 },
  { id: "P002", name: "Pantalón Chino Beige", sku: "PAN-CHN-002", category: "Bottoms", detail: "Beige · Talla 28", price: 169900, quantity: 8, minimum: 10 },
  { id: "P003", name: "Vestido Floral Midi", sku: "VES-FLR-003", category: "Vestidos", detail: "Estampado · Talla S", price: 219900, quantity: 21, minimum: 15 },
  { id: "P004", name: "Chaqueta Cuero Sintético", sku: "CHA-CUE-004", category: "Abrigos", detail: "Negro · Talla M", price: 239900, quantity: 0, minimum: 10 },
  { id: "P005", name: "Sneakers Canvas Blanco", sku: "SNK-CAN-005", category: "Calzado", detail: "Blanco · Talla 38", price: 129900, quantity: 5, minimum: 8 },
  { id: "P006", name: "Bolso Tote Lona", sku: "BOL-TOT-006", category: "Accesorios", detail: "Natural · Única", price: 119900, quantity: 18, minimum: 12 },
]

interface InventoryContextValue {
  products: InventoryProduct[]
  registerSale: (productId: string, quantity: number) => { ok: boolean; message: string }
}

const InventoryContext = createContext<InventoryContextValue | null>(null)

export function InventoryProvider({ children }: { children: ReactNode }) {
  const [products, setProducts] = useState(initialInventory)

  const value = useMemo<InventoryContextValue>(() => ({
    products,
    registerSale(productId, quantity) {
      const product = products.find((item) => item.id === productId)
      if (!product) return { ok: false, message: "Selecciona un producto para continuar." }
      if (quantity < 1) return { ok: false, message: "La cantidad debe ser mayor que cero." }
      if (quantity > product.quantity) {
        return { ok: false, message: `Solo hay ${product.quantity} unidades disponibles de este producto.` }
      }

      setProducts((current) =>
        current.map((item) =>
          item.id === productId ? { ...item, quantity: item.quantity - quantity } : item,
        ),
      )
      return { ok: true, message: "Venta registrada. El inventario fue actualizado correctamente." }
    },
  }), [products])

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
