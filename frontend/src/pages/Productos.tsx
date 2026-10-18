import { useState } from "react"
import { Badge, Field, EmptyState, Toast, Header, FormPanel, type View } from "../components/SharedUI"
import Disponibilidad from "./Disponibilidad"

interface Producto {
  id: string
  nombre: string
  sku: string
  categoria: string
  unidad: string
  precioBase: number
  stockMinimo: number
}

const seedProductos: Producto[] = [
  { id: "P001", nombre: "Camiseta Algodón Slim Fit", sku: "CAM-SLM-001", categoria: "Tops", unidad: "unidad", precioBase: 29.9, stockMinimo: 50 },
  { id: "P002", nombre: "Pantalón Chino Beige", sku: "PAN-CHN-002", categoria: "Bottoms", unidad: "unidad", precioBase: 49.9, stockMinimo: 30 },
  { id: "P003", nombre: "Vestido Floral Midi", sku: "VES-FLR-003", categoria: "Dresses", unidad: "unidad", precioBase: 64.9, stockMinimo: 25 },
  { id: "P004", nombre: "Chaqueta Cuero Sintético", sku: "CHA-CUE-004", categoria: "Outerwear", unidad: "unidad", precioBase: 89.9, stockMinimo: 20 },
  { id: "P005", nombre: "Sneakers Canvas Blanco", sku: "SNK-CAN-005", categoria: "Calzado", unidad: "par", precioBase: 39.9, stockMinimo: 40 },
]

export default function Productos() {
  const [view, setView] = useState<View>("list")
  const [showAvailability, setShowAvailability] = useState(false)
  const [productos, setProductos] = useState<Producto[]>(seedProductos)
  const [toast, setToast] = useState<{ msg: string; type: "success" | "error" } | null>(null)
  const [errors, setErrors] = useState<Record<string, boolean>>({})
  const [form, setForm] = useState({ nombre: "", sku: "", categoria: "", unidad: "", precioBase: "", stockMinimo: "" })

  const categorias = ["Tops", "Bottoms", "Dresses", "Outerwear", "Calzado", "Accesorios"]
  const unidades = ["unidad", "par", "caja", "kg"]

  const showToast = (msg: string, type: "success" | "error") => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3000)
  }

  const handleSubmit = () => {
    const required = ["nombre", "sku", "categoria", "unidad", "precioBase"]
    const errs: Record<string, boolean> = {}
    required.forEach((k) => { if (!form[k as keyof typeof form]) errs[k] = true })
    if (Object.keys(errs).length) { setErrors(errs); showToast("Completa los campos requeridos.", "error"); return }
    if (productos.some((p) => p.sku === form.sku)) { showToast("Ya existe un producto con ese SKU.", "error"); return }
    const id = `P${String(productos.length + 1).padStart(3, "0")}`
    setProductos([...productos, { ...form, id, precioBase: +form.precioBase, stockMinimo: +form.stockMinimo || 0 }])
    setForm({ nombre: "", sku: "", categoria: "", unidad: "", precioBase: "", stockMinimo: "" })
    setErrors({})
    setView("list")
    showToast("Producto registrado exitosamente.", "success")
  }

  const f = (k: keyof typeof form) => ({
    value: form[k],
    onChange: (v: string) => { setForm({ ...form, [k]: v }); setErrors({ ...errors, [k]: false }) },
  })

  if (showAvailability) {
    return (
      <div className="min-h-full bg-white">
        <div className="border-b border-slate-200 bg-white px-5 py-3 sm:px-8">
          <button
            onClick={() => setShowAvailability(false)}
            className="rounded-lg px-2 py-1.5 text-sm font-semibold text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
          >
            ← Volver al catálogo
          </button>
        </div>
        <Disponibilidad />
      </div>
    )
  }

  return (
    <div className="flex h-full flex-col bg-white">
      {toast && <Toast message={toast.msg} type={toast.type} />}
      <Header
        title="Productos"
        subtitle={`${productos.length} productos registrados`}
        view={view}
        onToggle={() => setView(view === "list" ? "register" : "list")}
      />

      {view === "list" ? (
        productos.length === 0 ? (
          <EmptyState
            icon="▦"
            title="No existen productos registrados"
            subtitle="Comienza registrando tu primer producto en el catálogo."
            onAction={() => setView("register")}
            actionLabel="Registrar producto"
          />
        ) : (
          <div className="overflow-auto">
            <div className="flex items-center justify-between border-b border-amber-200 bg-amber-50 px-6 py-4">
              <div>
                <div className="text-sm font-semibold text-amber-900">Disponibilidad del catálogo</div>
                <div className="mt-0.5 text-xs text-amber-700">Consulta qué referencias necesitan reposición.</div>
              </div>
              <button onClick={() => setShowAvailability(true)} className="rounded-lg border border-amber-300 bg-white px-3 py-2 text-xs font-semibold text-amber-700 transition hover:bg-amber-100">
                Revisar inventario
              </button>
            </div>
            <table className="w-full text-sm border-collapse">
              <thead>
                <tr className="border-b" style={{ borderColor: "#d4c9b0" }}>
                  {["ID", "Nombre", "SKU", "Categoría", "Unidad", "Precio Base", "Stock Mín."].map((h) => (
                    <th key={h} className="text-left text-[10px] font-semibold tracking-widest uppercase px-6 py-3" style={{ color: "#8a7a60" }}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {productos.map((p, i) => (
                  <tr
                    key={p.id}
                    className="border-b transition-all duration-150"
                    style={{
                      borderColor: "#d4c9b0",
                      backgroundColor: i % 2 === 0 ? "transparent" : "#ede8df",
                    }}
                    onMouseEnter={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#ddd4c0" }}
                    onMouseLeave={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = i % 2 === 0 ? "transparent" : "#ede8df" }}
                  >
                    <td className="px-6 py-3 text-xs font-medium" style={{ color: "#5a4a30" }}>{p.id}</td>
                    <td className="px-6 py-3 font-semibold" style={{ color: "#3d2e14" }}>{p.nombre}</td>
                    <td className="px-6 py-3 text-xs" style={{ color: "#8a7a60" }}>{p.sku}</td>
                    <td className="px-6 py-3"><Badge color="blue">{p.categoria}</Badge></td>
                    <td className="px-6 py-3 text-sm" style={{ color: "#6b5a3e" }}>{p.unidad}</td>
                    <td className="px-6 py-3 text-sm font-semibold" style={{ color: "#3a6b47" }}>${p.precioBase.toFixed(2)}</td>
                    <td className="px-6 py-3 text-xs" style={{ color: "#8a7a60" }}>{p.stockMinimo}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      ) : (
        <FormPanel title="Nuevo Producto" onCancel={() => setView("list")} onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="sm:col-span-2">
              <Field label="Nombre del producto" placeholder="Ej. Camiseta Algodón Slim Fit" required {...f("nombre")} />
              {errors.nombre && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="SKU" placeholder="Ej. CAM-SLM-001" required {...f("sku")} />
              {errors.sku && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Categoría" required select options={categorias} {...f("categoria")} />
              {errors.categoria && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Unidad de medida" required select options={unidades} {...f("unidad")} />
              {errors.unidad && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Precio base (USD)" type="number" placeholder="0.00" required {...f("precioBase")} />
              {errors.precioBase && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Stock mínimo" type="number" placeholder="0" {...f("stockMinimo")} />
            </div>
          </div>
        </FormPanel>
      )}
    </div>
  )
}
