const TOKEN_KEY = "flowi-token"
const ROLE_KEY = "flowi-role"

function getApiBaseUrl() {
  const viteEnv = (import.meta as any).env as Record<string, string | undefined> | undefined
  const base = (viteEnv?.VITE_API_BASE_URL ?? "").trim().replace(/\/+$/, "")
  if (!base) {
    return "http://localhost:8080"
  }
  return base
}

export const API_BASE_URL = getApiBaseUrl()

function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY) ?? sessionStorage.getItem(TOKEN_KEY)
}

function clearStoredAuth() {
  localStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ROLE_KEY)
  sessionStorage.removeItem(ROLE_KEY)
  localStorage.removeItem("flowi-authenticated")
  sessionStorage.removeItem("flowi-authenticated")
}

export class ApiError extends Error {
  status: number
  constructor(message: string, status: number) {
    super(message)
    this.status = status
    this.name = "ApiError"
  }
}

async function readErrorMessage(response: Response): Promise<string> {
  try {
    const contentType = response.headers.get("content-type") ?? ""
    if (contentType.includes("application/json")) {
      const data = (await response.json()) as unknown
      if (data && typeof data === "object") {
        const anyData = data as any
        if (typeof anyData.message === "string" && anyData.message) return anyData.message
        if (typeof anyData.error === "string" && anyData.error) return anyData.error
      }
      return "Ocurrió un error inesperado. Intenta nuevamente."
    }
    const text = await response.text()
    return text || "Ocurrió un error inesperado. Intenta nuevamente."
  } catch {
    return "Ocurrió un error inesperado. Intenta nuevamente."
  }
}

/**
 * Helper centralizado para llamadas a /api/*.
 * - Agrega Authorization: Bearer <token> cuando hay sesión.
 * - Si la respuesta es 401, limpia la sesión (el llamador debe redirigir a /login).
 * - Si es 403, lanza un ApiError con mensaje "No tienes permiso para esta acción".
 * - Para otros errores, usa body.message, body.error, o un mensaje por defecto.
 */
export async function apiFetch(path: string, options: RequestInit = {}): Promise<Response> {
  const token = getStoredToken()
  const headers = new Headers(options.headers)
  if (token) {
    headers.set("Authorization", `Bearer ${token}`)
  }
  if (options.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json")
  }

  let response: Response
  try {
    response = await fetch(path.startsWith("/api") ? path : `/api${path}`, {
      ...options,
      headers,
    })
  } catch {
    throw new ApiError("No se pudo conectar con el servidor. Verifica tu conexión e inténtalo de nuevo.", 0)
  }

  if (response.status === 401) {
    clearStoredAuth()
    if (typeof window !== "undefined" && window.location.pathname !== "/login") {
      window.location.href = "/login"
    }
    throw new ApiError("Tu sesión expiró. Inicia sesión de nuevo.", 401)
  }

  if (response.status === 403) {
    throw new ApiError("No tienes permiso para esta acción.", 403)
  }

  return response
}

/**
 * Helper que además parsea el JSON y lanza ApiError si la respuesta no es ok.
 */
export async function apiFetchJson<T = unknown>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await apiFetch(path, options)
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    const message = await extractMessageFromBody(body, response)
    throw new ApiError(message, response.status)
  }
  return body as T
}

async function extractMessageFromBody(body: unknown, response: Response): Promise<string> {
  if (body && typeof body === "object") {
    const anyBody = body as any
    if (typeof anyBody.message === "string" && anyBody.message) return anyBody.message
    if (typeof anyBody.error === "string" && anyBody.error) return anyBody.error
  }
  return `Ocurrió un error inesperado (HTTP ${response.status}).`
}

export { readErrorMessage }
