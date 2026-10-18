import { createContext, useContext, useState, type ReactNode } from "react"

const AUTH_KEY = "flowi-authenticated"
const ROLE_KEY = "flowi-role"

export type UserRole = "admin" | "operator"

interface AuthContextValue {
  isAuthenticated: boolean
  role: UserRole | null
  login: (email: string, password: string, remember: boolean) => boolean
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(
    () => localStorage.getItem(AUTH_KEY) === "true" || sessionStorage.getItem(AUTH_KEY) === "true",
  )
  const [role, setRole] = useState<UserRole | null>(() => {
    const storedRole = localStorage.getItem(ROLE_KEY) || sessionStorage.getItem(ROLE_KEY)
    if (storedRole === "operator") return "operator"
    if (storedRole === "admin") return "admin"
    const hasLegacySession = localStorage.getItem(AUTH_KEY) === "true" || sessionStorage.getItem(AUTH_KEY) === "true"
    return hasLegacySession ? "admin" : null
  })

  const login = (email: string, password: string, remember: boolean) => {
    const normalizedEmail = email.trim().toLowerCase()
    const nextRole: UserRole | null =
      normalizedEmail === "admin@flowi.co"
        ? "admin"
        : normalizedEmail === "operador@flowi.co"
          ? "operator"
          : null
    if (!nextRole || password !== "Flowi2025") return false

    localStorage.removeItem(AUTH_KEY)
    sessionStorage.removeItem(AUTH_KEY)
    localStorage.removeItem(ROLE_KEY)
    sessionStorage.removeItem(ROLE_KEY)
    const storage = remember ? localStorage : sessionStorage
    storage.setItem(AUTH_KEY, "true")
    storage.setItem(ROLE_KEY, nextRole)
    setRole(nextRole)
    setIsAuthenticated(true)
    return true
  }

  const logout = () => {
    localStorage.removeItem(AUTH_KEY)
    sessionStorage.removeItem(AUTH_KEY)
    localStorage.removeItem(ROLE_KEY)
    sessionStorage.removeItem(ROLE_KEY)
    setRole(null)
    setIsAuthenticated(false)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, role, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error("useAuth must be used inside AuthProvider")
  return context
}
