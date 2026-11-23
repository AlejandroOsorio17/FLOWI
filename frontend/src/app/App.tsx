import { RouterProvider } from "react-router"
import { router } from "./routes"
import { InventoryProvider } from "../context/InventoryContext"
import { AuthProvider } from "../context/AuthContext"
import { StoreSelectionProvider } from "../context/StoreSelectionContext"

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
