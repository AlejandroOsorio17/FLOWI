import { useState, type FormEvent } from "react"
import { Navigate, useNavigate } from "react-router"
import { useAuth } from "../auth/AuthContext"

function FlowiMark() {
  return (
    <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-blue-600 text-white shadow-lg shadow-blue-950/20">
      <svg viewBox="0 0 24 24" className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
        <path d="M7 7h10M7 12h7M7 17h4" />
      </svg>
    </span>
  )
}

export default function Login() {
  const { isAuthenticated, login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [remember, setRemember] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [errors, setErrors] = useState<{ email?: string; password?: string; form?: string }>({})

  if (isAuthenticated) return <Navigate to="/" replace />

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    const nextErrors: typeof errors = {}
    if (!email.trim()) nextErrors.email = "Ingresa tu correo electrónico."
    if (!password) nextErrors.password = "Ingresa tu contraseña."
    if (Object.keys(nextErrors).length) {
      setErrors(nextErrors)
      return
    }
    if (!login(email, password, remember)) {
      setErrors({ form: "El correo electrónico o la contraseña no son válidos." })
      return
    }
    navigate("/", { replace: true })
  }

  return (
    <main className="grid min-h-screen bg-slate-50 lg:grid-cols-[1.05fr_0.95fr]">
      <section className="relative hidden overflow-hidden bg-slate-950 px-16 py-14 text-white lg:flex lg:flex-col xl:px-20">
        <div className="pointer-events-none absolute -right-32 -top-24 h-96 w-96 rounded-full border border-white/10" />
        <div className="pointer-events-none absolute -right-20 -top-14 h-72 w-72 rounded-full border border-white/10" />
        <div className="flex items-center gap-3">
          <FlowiMark />
          <span className="text-2xl font-bold tracking-tight">Flowi</span>
        </div>
        <div className="my-auto max-w-xl">
          <div className="mb-8 inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300">
            <span className="h-2 w-2 rounded-full bg-blue-400" />
            Gestión inteligente de operaciones
          </div>
          <h1 className="text-5xl font-bold leading-tight tracking-tight xl:text-6xl">
            Tu cadena de suministro,<br />siempre en movimiento.
          </h1>
          <p className="mt-7 max-w-lg text-lg leading-8 text-slate-400">
            Centraliza productos, tiendas y centros de distribución en un espacio seguro, simple y conectado.
          </p>
        </div>
        <p className="text-sm text-slate-500">Supply chain, simplified.</p>
      </section>

      <section className="flex min-h-screen items-center justify-center px-5 py-10 sm:px-10">
        <div className="w-full max-w-md">
          <div className="mb-10 flex items-center gap-3 lg:hidden">
            <FlowiMark />
            <span className="text-2xl font-bold tracking-tight text-slate-950">Flowi</span>
          </div>
          <p className="text-sm font-semibold text-blue-600">Bienvenido de nuevo</p>
          <h1 className="mt-2 text-4xl font-bold tracking-tight text-slate-950">Inicia sesión en Flowi</h1>
          <p className="mt-3 text-sm text-slate-500">Usa un perfil de prueba para validar el acceso según su rol.</p>

          <form onSubmit={handleSubmit} className="mt-9" noValidate>
            {errors.form && (
              <div role="alert" className="mb-5 rounded-lg border border-rose-200 bg-rose-50 px-4 py-3 text-sm font-medium text-rose-700">
                {errors.form}
              </div>
            )}
            <label className="block">
              <span className="text-sm font-semibold text-slate-700">Correo electrónico</span>
              <input
                type="email"
                value={email}
                onChange={(event) => { setEmail(event.target.value); setErrors((current) => ({ ...current, email: undefined, form: undefined })) }}
                placeholder="nombre@empresa.com"
                autoComplete="email"
                className={`mt-2 h-12 w-full rounded-lg border bg-white px-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:ring-2 ${errors.email ? "border-rose-400 focus:border-rose-500 focus:ring-rose-100" : "border-slate-300 focus:border-blue-500 focus:ring-blue-100"}`}
              />
              {errors.email && <span className="mt-1.5 block text-xs font-medium text-rose-600">{errors.email}</span>}
            </label>

            <label className="mt-5 block">
              <span className="text-sm font-semibold text-slate-700">Contraseña</span>
              <span className="relative mt-2 block">
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(event) => { setPassword(event.target.value); setErrors((current) => ({ ...current, password: undefined, form: undefined })) }}
                  placeholder="Ingresa tu contraseña"
                  autoComplete="current-password"
                  className={`h-12 w-full rounded-lg border bg-white px-4 pr-12 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:ring-2 ${errors.password ? "border-rose-400 focus:border-rose-500 focus:ring-rose-100" : "border-slate-300 focus:border-blue-500 focus:ring-blue-100"}`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((current) => !current)}
                  aria-label={showPassword ? "Ocultar contraseña" : "Mostrar contraseña"}
                  className="absolute right-3 top-1/2 -translate-y-1/2 rounded p-1 text-slate-400 transition hover:text-slate-700"
                >
                  <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z" /><circle cx="12" cy="12" r="2.5" /></svg>
                </button>
              </span>
              {errors.password && <span className="mt-1.5 block text-xs font-medium text-rose-600">{errors.password}</span>}
            </label>

            <label className="mt-5 flex w-fit cursor-pointer items-center gap-2 text-sm text-slate-600">
              <input type="checkbox" checked={remember} onChange={(event) => setRemember(event.target.checked)} className="h-4 w-4 rounded border-slate-300 accent-blue-600" />
              Mantener mi sesión iniciada
            </label>

            <button type="submit" className="mt-7 h-12 w-full rounded-lg bg-blue-600 text-sm font-semibold text-white shadow-lg shadow-blue-600/20 transition hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300">
              Validar acceso
            </button>
          </form>

          <div className="mt-7 border-t border-slate-200 pt-6">
            <div className="text-[11px] font-bold uppercase tracking-wider text-slate-400">Perfiles de prueba</div>
            <div className="mt-3 grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => { setEmail("admin@flowi.co"); setPassword("Flowi2025"); setErrors({}) }}
                className={`rounded-lg border bg-white p-3 text-left transition hover:border-blue-400 hover:bg-blue-50 ${email === "admin@flowi.co" ? "border-blue-500 ring-2 ring-blue-100" : "border-slate-200"}`}
              >
                <span className="block text-sm font-semibold text-slate-900">Administrador</span>
                <span className="mt-1 block text-xs text-slate-500">Acceso completo</span>
              </button>
              <button
                type="button"
                onClick={() => { setEmail("operador@flowi.co"); setPassword("Flowi2025"); setErrors({}) }}
                className={`rounded-lg border bg-white p-3 text-left transition hover:border-blue-400 hover:bg-blue-50 ${email === "operador@flowi.co" ? "border-blue-500 ring-2 ring-blue-100" : "border-slate-200"}`}
              >
                <span className="block text-sm font-semibold text-slate-900">Operador</span>
                <span className="mt-1 block text-xs text-slate-500">Acceso limitado</span>
              </button>
            </div>
            <p className="mt-3 text-xs text-slate-400">Contraseña para ambos perfiles: Flowi2025</p>
          </div>
          <p className="mt-8 text-center text-xs text-slate-400">Prototipo de validación de acceso según rol</p>
        </div>
      </section>
    </main>
  )
}
