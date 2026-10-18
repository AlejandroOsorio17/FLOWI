import { createContext, useContext, useState, type ReactNode } from "react"

const TOKEN_KEY = "flowi-token"
const ROLE_KEY = "flowi-role"
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? "").replace(/\/+$/, "")

export type UserRole = "ADMIN" | "VENDEDOR" | "BODEGUERO"

interface AuthResponse {
  token: string
  role: UserRole
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
    let response: Response
    try {
      response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username.trim(), password }),
      })
    } catch {
      throw new Error("No se pudo conectar con el servidor. Verifica la URL del backend e inténtalo de nuevo.")
    }

    if (response.status === 401) {
      throw new Error("El usuario o la contraseña no son válidos.")
    }
    if (!response.ok) {
      throw new Error(`Error del servidor al iniciar sesión (HTTP ${response.status}).`)
    }

    const result = (await response.json()) as AuthResponse
    if (!result.token || !["ADMIN", "VENDEDOR", "BODEGUERO"].includes(result.role)) {
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
      if (currentToken) {
        const response = await fetch(`${API_BASE_URL}/api/auth/logout`, {
          method: "POST",
          headers: { Authorization: `Bearer ${currentToken}` },
        })
        if (!response.ok) {
          throw new Error(`El backend no pudo revocar la sesión (HTTP ${response.status}).`)
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
