import { RouterProvider } from "react-router"
import { router } from "./routes"
import { InventoryProvider } from "./data/InventoryContext"
import { AuthProvider } from "./auth/AuthContext"

export default function App() {
  return (
    <AuthProvider>
      <InventoryProvider>
        <RouterProvider router={router} />
      </InventoryProvider>
    </AuthProvider>
  )
}
