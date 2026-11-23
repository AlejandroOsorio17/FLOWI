import { Outlet } from "react-router"
import Sidebar from "./Sidebar"

export default function Layout() {
  return (
    <div className="flex h-screen w-screen overflow-hidden" style={{ backgroundColor: "#F8FAFC" }}>
      <Sidebar />
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}
