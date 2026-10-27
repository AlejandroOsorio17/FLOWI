import { RouterProvider } from "react-router"
import { router } from "./routes"
import { InventoryProvider } from "./data/InventoryContext"
import { AuthProvider } from "./auth/AuthContext"
import { StoreSelectionProvider } from "./data/StoreSelectionContext"

export default function App() {
  return (
    <AuthProvider>
      <StoreSelectionProvider>
        <InventoryProvider>
          <RouterProvider router={router} />
        </InventoryProvider>
      </StoreSelectionProvider>
    </AuthProvider>
  )
}
