import { createContext, useContext, useState, type ReactNode } from "react"
import { API_BASE_URL } from "../api"

const TOKEN_KEY = "flowi-token"
const ROLE_KEY = "flowi-role"

export type UserRole = "ADMIN" | "VENDEDOR" | "BODEGUERO"

interface AuthResponse {
  token: string
  role: UserRole
}

async function readErrorMessage(response: Response) {
  try {
    const contentType = response.headers.get("content-type") ?? ""
    if (contentType.includes("application/json")) {
      const data = (await response.json()) as unknown
      if (data && typeof data === "object" && "message" in data && typeof (data as any).message === "string") {
        return (data as any).message as string
      }
      return JSON.stringify(data)
    }
    const text = await response.text()
    return text || `HTTP ${response.status}`
  } catch {
    return `HTTP ${response.status}`
  }
}

interface AuthContextValue {
  isAuthenticated: boolean
  role: UserRole | null
  login: (username: string, password: string, remember: boolean) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

function getStoredValue(key: string) {
  return localStorage.getItem(key) ?? sessionStorage.getItem(key)
}

function clearStoredAuth() {
  localStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ROLE_KEY)
  sessionStorage.removeItem(ROLE_KEY)
  localStorage.removeItem("flowi-authenticated")
  sessionStorage.removeItem("flowi-authenticated")
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState(() => getStoredValue(TOKEN_KEY))
  const [role, setRole] = useState<UserRole | null>(() => {
    const storedRole = getStoredValue(ROLE_KEY)
    return storedRole === "ADMIN" || storedRole === "VENDEDOR" || storedRole === "BODEGUERO"
      ? storedRole
      : null
  })

  const login = async (username: string, password: string, remember: boolean) => {
    const trimmedUsername = username.trim()
    if (!trimmedUsername || !password) {
      throw new Error("Debes ingresar usuario y contraseña.")
    }

    let response: Response
    try {
      response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: trimmedUsername, password }),
      })
    } catch {
      throw new Error("No se pudo conectar con el servidor. Verifica la URL del backend e inténtalo de nuevo.")
    }

    if (response.status === 401) {
      throw new Error("El usuario o la contraseña no son válidos.")
    }
    if (!response.ok) {
      const message = await readErrorMessage(response)
      throw new Error(`Error del servidor al iniciar sesión: ${message}`)
    }

    const result = (await response.json()) as AuthResponse
    if (!result?.token || !["ADMIN", "VENDEDOR", "BODEGUERO"].includes(result.role)) {
      throw new Error("El servidor devolvió una respuesta de inicio de sesión inválida.")
    }

    clearStoredAuth()
    const storage = remember ? localStorage : sessionStorage
    storage.setItem(TOKEN_KEY, result.token)
    storage.setItem(ROLE_KEY, result.role)
    setToken(result.token)
    setRole(result.role)
  }

  const logout = async () => {
    const currentToken = getStoredValue(TOKEN_KEY)
    try {
      // Logout debe ser “best effort”: si el backend falla igual limpiamos estado local.
      if (currentToken) {
        const response = await fetch(`${API_BASE_URL}/api/auth/logout`, {
          method: "POST",
          headers: { Authorization: `Bearer ${currentToken}` },
        })
        if (!response.ok) {
          // No bloquea el logout del usuario, pero deja un error útil en consola.
          const message = await readErrorMessage(response)
          console.warn(`El backend no pudo revocar la sesión: ${message}`)
        }
      }
    } finally {
      clearStoredAuth()
      setToken(null)
      setRole(null)
    }
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated: token !== null, role, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error("useAuth must be used inside AuthProvider")
  return context
}
