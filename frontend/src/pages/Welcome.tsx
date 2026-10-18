import { useNavigate } from "react-router"

const buttons = [
  { label: "Tabla de productos", to: "/productos" },
  { label: "Tiendas", to: "/tiendas" },
  { label: "Centros de distribución", to: "/centros" },
]

export default function Welcome() {
  const navigate = useNavigate()

  return (
    <div className="flex h-full flex-col items-center justify-center bg-white">
      {/* Card container */}
      <div
        className="flex flex-col items-center gap-10"
        style={{
          backgroundColor: "#ffffff",
          borderRadius: "12px",
          padding: "48px",
          boxShadow: "0px 4px 16px rgba(0,0,0,0.08)",
          minWidth: "480px",
        }}
      >
        {/* Title + tagline */}
        <div className="flex flex-col items-center gap-2 text-center">
          <h1
            style={{
              fontSize: "48px",
              fontWeight: 700,
              color: "#1E293B",
              fontFamily: "'Inter', sans-serif",
              lineHeight: 1.1,
            }}
          >
            Flowi
          </h1>
          <p style={{ fontSize: "18px", fontWeight: 400, color: "#64748B", fontFamily: "'Inter', sans-serif" }}>
            Supply chain, simplified
          </p>
        </div>

        {/* Buttons */}
        <div className="flex gap-3 flex-wrap justify-center">
          {buttons.map(({ label, to }) => (
            <button
              key={to}
              onClick={() => navigate(to)}
              style={{
                backgroundColor: "#2563EB",
                color: "#ffffff",
                fontSize: "14px",
                fontWeight: 600,
                fontFamily: "'Inter', sans-serif",
                borderRadius: "8px",
                padding: "10px 20px",
                border: "none",
                cursor: "pointer",
                boxShadow: "0px 2px 8px rgba(0,0,0,0.1)",
                transition: "background-color 0.15s",
              }}
              onMouseEnter={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#1D4ED8" }}
              onMouseLeave={(e) => { ;(e.currentTarget as HTMLElement).style.backgroundColor = "#2563EB" }}
            >
              {label}
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}
