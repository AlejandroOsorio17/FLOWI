import { createContext, useContext, useState, type ReactNode } from "react"

export interface SelectedStore {
  id: number
  code: string
  name: string
  city: string
}

const STORAGE_KEY = "flowi-selected-store"

interface StoreSelectionContextValue {
  selectedStore: SelectedStore | null
  selectStore: (store: SelectedStore) => void
  clearStore: () => void
}

const StoreSelectionContext = createContext<StoreSelectionContextValue | null>(null)

function readStoredStore(): SelectedStore | null {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw) as SelectedStore
    if (
      parsed &&
      typeof parsed.id === "number" &&
      typeof parsed.code === "string" &&
      typeof parsed.name === "string" &&
      typeof parsed.city === "string"
    ) {
      return parsed
    }
    return null
  } catch {
    return null
  }
}

export function StoreSelectionProvider({ children }: { children: ReactNode }) {
  const [selectedStore, setSelectedStore] = useState<SelectedStore | null>(() => readStoredStore())

  const selectStore = (store: SelectedStore) => {
    setSelectedStore(store)
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(store))
  }

  const clearStore = () => {
    setSelectedStore(null)
    sessionStorage.removeItem(STORAGE_KEY)
  }

  return (
    <StoreSelectionContext.Provider value={{ selectedStore, selectStore, clearStore }}>
      {children}
    </StoreSelectionContext.Provider>
  )
}

export function useStoreSelection() {
  const context = useContext(StoreSelectionContext)
  if (!context) throw new Error("useStoreSelection must be used inside StoreSelectionProvider")
  return context
}
