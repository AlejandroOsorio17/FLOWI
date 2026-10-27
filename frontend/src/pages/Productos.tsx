import { useEffect, useState } from "react"
import { Badge, Field, EmptyState, Toast, Header, FormPanel, type View } from "../components/SharedUI"
import { apiFetch, ApiError } from "../api"
import { formatCurrency } from "../data/InventoryContext"
import Disponibilidad from "./Disponibilidad"

interface ProductoApi {
  id: number
  code: string
  name: string
  description: string
  price: number
  category: string | null
  unit: string | null
  minimumStock: number | null
}

export default function Productos() {
  const [view, setView] = useState<View>("list")
  const [showAvailability, setShowAvailability] = useState(false)
  const [productos, setProductos] = useState<ProductoApi[]>([])
  const [loading, setLoading] = useState(false)
  const [loadError, setLoadError] = useState<string | null>(null)
  const [toast, setToast] = useState<{ msg: string; type: "success" | "error" } | null>(null)
  const [errors, setErrors] = useState<Record<string, boolean>>({})
  const [form, setForm] = useState({
    nombre: "",
    sku: "",
    descripcion: "",
    categoria: "",
    unidad: "",
    precioBase: "",
    stockMinimo: "",
  })

  const categorias = ["Tops", "Bottoms", "Dresses", "Outerwear", "Calzado", "Accesorios"]
  const unidades = ["unidad", "par", "caja", "kg"]

  async function loadProducts() {
    setLoading(true)
    setLoadError(null)
    try {
      const res = await apiFetch("/api/products")
      const body = await res.json().catch(() => ({}))
      if (!res.ok) throw new Error(body?.message || body?.error || "Error consultando productos")
      setProductos(Array.isArray(body) ? (body as ProductoApi[]) : [])
    } catch (e) {
      setLoadError(e instanceof ApiError ? e.message : e instanceof Error ? e.message : "Error consultando productos")
      setProductos([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadProducts()
  }, [])

  const showToast = (msg: string, type: "success" | "error") => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 5000)
  }

  const handleSubmit = async () => {
    const required = ["nombre", "sku", "descripcion", "precioBase"]
    const errs: Record<string, boolean> = {}
    required.forEach((k) => { if (!form[k as keyof typeof form]) errs[k] = true })
    if (Object.keys(errs).length) { setErrors(errs); showToast("Completa los campos requeridos.", "error"); return }

    try {
      const res = await apiFetch("/api/products", {
        method: "POST",
        body: JSON.stringify({
          code: form.sku,
          name: form.nombre,
          description: form.descripcion,
          price: Number(form.precioBase),
          category: form.categoria || null,
          unit: form.unidad || null,
          minimumStock: form.stockMinimo ? Number(form.stockMinimo) : null,
        }),
      })
      const body = await res.json().catch(() => ({}))
      if (!res.ok) {
        if (res.status === 409) throw new Error(body?.message || "Ya existe un producto con ese código.")
        if (res.status === 422) throw new Error(body?.message || "Datos inválidos. Revisa los campos del formulario.")
        throw new Error(body?.message || body?.error || "Error registrando producto")
      }

      setForm({ nombre: "", sku: "", descripcion: "", categoria: "", unidad: "", precioBase: "", stockMinimo: "" })
      setErrors({})
      setView("list")
      showToast("Producto registrado exitosamente.", "success")
      await loadProducts()
    } catch (e) {
      const message = e instanceof ApiError ? e.message : e instanceof Error ? e.message : "Error registrando producto"
      showToast(message, "error")
    }
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
        subtitle={loading ? "Cargando..." : loadError ? "Error cargando productos" : `${productos.length} productos registrados`}
        view={view}
        onToggle={() => setView(view === "list" ? "register" : "list")}
      />

      {view === "list" ? (
        loading ? (
          <div className="px-6 py-10 text-sm" style={{ color: "#6b5a3e" }}>
            Cargando productos...
          </div>
        ) : loadError ? (
          <div className="px-6 py-10 text-sm" style={{ color: "#7a3a3a" }}>
            {loadError}
          </div>
        ) : productos.length === 0 ? (
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
                    <td className="px-6 py-3 font-semibold" style={{ color: "#3d2e14" }}>{p.name}</td>
                    <td className="px-6 py-3 text-xs" style={{ color: "#8a7a60" }}>{p.code}</td>
                    <td className="px-6 py-3"><Badge color="blue">{p.category ?? "N/A"}</Badge></td>
                    <td className="px-6 py-3 text-sm" style={{ color: "#6b5a3e" }}>{p.unit ?? "-"}</td>
                    <td className="px-6 py-3 text-sm font-semibold" style={{ color: "#3a6b47" }}>{formatCurrency(p.price)}</td>
                    <td className="px-6 py-3 text-xs" style={{ color: "#8a7a60" }}>{p.minimumStock ?? "-"}</td>
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
            <div className="sm:col-span-2">
              <Field label="Descripción" placeholder="Ej. Camiseta de algodón 100%, corte slim fit" required {...f("descripcion")} />
              {errors.descripcion && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Categoría" select options={categorias} {...f("categoria")} />
            </div>
            <div>
              <Field label="Unidad de medida" select options={unidades} {...f("unidad")} />
            </div>
            <div>
              <Field label="Precio base (COP)" type="number" placeholder="0" required {...f("precioBase")} />
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
