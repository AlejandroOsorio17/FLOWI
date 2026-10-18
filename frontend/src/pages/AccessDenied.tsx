import { useNavigate } from "react-router"

export default function AccessDenied() {
  const navigate = useNavigate()

  return (
    <div className="flex min-h-full items-center justify-center bg-slate-50 px-6 py-12">
      <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
        <span className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-amber-50 text-amber-600">
          <svg viewBox="0 0 24 24" className="h-7 w-7" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="4" y="10" width="16" height="11" rx="2" /><path d="M8 10V7a4 4 0 0 1 8 0v3M12 14v3" /></svg>
        </span>
        <h1 className="mt-5 text-2xl font-bold text-slate-950">Acceso no autorizado</h1>
        <p className="mt-2 text-sm leading-6 text-slate-500">
          Tu perfil no cuenta con los permisos necesarios para acceder a esta funcionalidad.
        </p>
        <button onClick={() => navigate("/", { replace: true })} className="mt-6 h-10 rounded-lg bg-blue-600 px-5 text-sm font-semibold text-white transition hover:bg-blue-700">
          Volver al inicio
        </button>
      </div>
    </div>
  )
}
