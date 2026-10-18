import { createBrowserRouter, Navigate } from "react-router"
import Layout from "./Layout"
import Welcome from "./pages/Welcome"
import Productos from "./pages/Productos"
import Tiendas from "./pages/Tiendas"
import CentrosDistribucion from "./pages/CentrosDistribucion"
import TiendasVacio from "./pages/TiendasVacio"
import Login from "./pages/Login"
import { useAuth } from "./auth/AuthContext"
import AccessDenied from "./pages/AccessDenied"

function ProtectedLayout() {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <Layout /> : <Navigate to="/login" replace />
}

function CentrosByRole() {
  const { role } = useAuth()
  return role === "admin" ? <CentrosDistribucion /> : <AccessDenied />
}

export const router = createBrowserRouter([
  { path: "/login", Component: Login },
  {
    path: "/",
    Component: ProtectedLayout,
    children: [
      { index: true, Component: Welcome },
      { path: "productos", Component: Productos },
      { path: "tiendas", Component: Tiendas },
      { path: "tiendas/vacio", Component: TiendasVacio },
      { path: "centros", Component: CentrosByRole },
    ],
  },
])
