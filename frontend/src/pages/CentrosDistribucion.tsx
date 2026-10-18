import { useState } from "react"
import { Badge, Field, EmptyState, Toast, Header, FormPanel, type View } from "../components/SharedUI"

interface Centro {
  id: string
  nombre: string
  codigo: string
  ciudad: string
  pais: string
  tipo: string
  capacidadM3: number
}

const seedCentros: Centro[] = [
  { id: "CD001", nombre: "Centro Distribución Medellín", codigo: "CD-MED-01", ciudad: "Medellín", pais: "Colombia", tipo: "Principal", capacidadM3: 95000 },
  { id: "CD002", nombre: "Centro Distribución Rionegro", codigo: "CD-RNG-01", ciudad: "Rionegro", pais: "Colombia", tipo: "Regional", capacidadM3: 48000 },
  { id: "CD003", nombre: "Centro Distribución Oriente Antioqueño", codigo: "CD-ORI-01", ciudad: "El Carmen de Viboral", pais: "Colombia", tipo: "Satélite", capacidadM3: 18000 },
]

const tipoColor: Record<string, string> = {
  Principal: "blue",
  Regional: "green",
  "Satélite": "amber",
  "Cross-Docking": "rose",
}

export default function CentrosDistribucion() {
  const [view, setView] = useState<View>("list")
  const [centros, setCentros] = useState<Centro[]>(seedCentros)
  const [toast, setToast] = useState<{ msg: string; type: "success" | "error" } | null>(null)
  const [errors, setErrors] = useState<Record<string, boolean>>({})
  const [form, setForm] = useState({ nombre: "", codigo: "", ciudad: "", pais: "", tipo: "", capacidadM3: "" })
  const tipos = ["Principal", "Regional", "Satélite", "Cross-Docking"]

  const showToast = (msg: string, type: "success" | "error") => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3000)
  }

  const handleSubmit = () => {
    const required = ["nombre", "codigo", "ciudad", "pais", "tipo"]
    const errs: Record<string, boolean> = {}
    required.forEach((k) => { if (!form[k as keyof typeof form]) errs[k] = true })
    if (Object.keys(errs).length) { setErrors(errs); showToast("Completa los campos requeridos.", "error"); return }
    if (centros.some((c) => c.codigo === form.codigo)) { showToast("Ya existe un centro con ese código.", "error"); return }
    const id = `CD${String(centros.length + 1).padStart(3, "0")}`
    setCentros([...centros, { ...form, id, capacidadM3: +form.capacidadM3 || 0 }])
    setForm({ nombre: "", codigo: "", ciudad: "", pais: "", tipo: "", capacidadM3: "" })
    setErrors({})
    setView("list")
    showToast("Centro de distribución registrado.", "success")
  }

  const f = (k: keyof typeof form) => ({
    value: form[k],
    onChange: (v: string) => { setForm({ ...form, [k]: v }); setErrors({ ...errors, [k]: false }) },
  })

  return (
    <div className="flex h-full flex-col bg-white">
      {toast && <Toast message={toast.msg} type={toast.type} />}
      <Header
        title="Centros de Distribución"
        subtitle={`${centros.length} centros registrados`}
        view={view}
        onToggle={() => setView(view === "list" ? "register" : "list")}
      />

      {view === "list" ? (
        centros.length === 0 ? (
          <EmptyState
            icon="⬡"
            title="No existen centros de distribución registrados"
            subtitle="Registra el primer centro de la red logística."
            onAction={() => setView("register")}
            actionLabel="Registrar centro"
          />
        ) : (
          <div className="overflow-auto">
            <table className="w-full text-sm border-collapse">
              <thead>
                <tr className="border-b" style={{ borderColor: "#d4c9b0" }}>
                  {["ID", "Nombre", "Código", "Ciudad", "País", "Tipo", "Capacidad (m³)"].map((h) => (
                    <th key={h} className="text-left text-[10px] font-semibold tracking-widest uppercase px-6 py-3" style={{ color: "#8a7a60" }}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {centros.map((c, i) => (
                  <tr
                    key={c.id}
                    className="border-b transition-all duration-150"
                    style={{
                      borderColor: "#d4c9b0",
                      backgroundColor: i % 2 === 0 ? "transparent" : "#ede8df",
                    }}
                    onMouseEnter={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#ddd4c0" }}
                    onMouseLeave={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = i % 2 === 0 ? "transparent" : "#ede8df" }}
                  >
                    <td className="px-6 py-3 text-xs font-medium" style={{ color: "#5a4a30" }}>{c.id}</td>
                    <td className="px-6 py-3 font-semibold" style={{ color: "#3d2e14" }}>{c.nombre}</td>
                    <td className="px-6 py-3 text-xs" style={{ color: "#8a7a60" }}>{c.codigo}</td>
                    <td className="px-6 py-3" style={{ color: "#3d2e14" }}>{c.ciudad}</td>
                    <td className="px-6 py-3 text-sm" style={{ color: "#6b5a3e" }}>{c.pais}</td>
                    <td className="px-6 py-3"><Badge color={tipoColor[c.tipo] ?? "blue"}>{c.tipo}</Badge></td>
                    <td className="px-6 py-3 text-xs font-medium" style={{ color: "#3a6b47" }}>{c.capacidadM3.toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      ) : (
        <FormPanel title="Nuevo Centro de Distribución" onCancel={() => setView("list")} onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="sm:col-span-2">
              <Field label="Nombre del centro" placeholder="Ej. Centro Distribución Zaragoza" required {...f("nombre")} />
              {errors.nombre && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Código del centro" placeholder="Ej. CD-ZAR-01" required {...f("codigo")} />
              {errors.codigo && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Tipo" required select options={tipos} {...f("tipo")} />
              {errors.tipo && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="País" placeholder="Ej. España" required {...f("pais")} />
              {errors.pais && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div>
              <Field label="Ciudad" placeholder="Ej. Zaragoza" required {...f("ciudad")} />
              {errors.ciudad && <p className="text-[10px] mt-1" style={{ color: "#7a3a3a" }}>Campo requerido</p>}
            </div>
            <div className="sm:col-span-2">
              <Field label="Capacidad (m³)" type="number" placeholder="0" {...f("capacidadM3")} />
            </div>
          </div>
        </FormPanel>
      )}
    </div>
  )
}
