package com.example.demo1.domain.replenishment;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Elige el centro de distribución de origen para un producto.
 * Solo considera centros con stock > 0. Prefiere el de la misma ciudad que la tienda
 * (sin distinguir mayúsculas, tildes ni espacios sobrantes); si no hay, el de mayor stock.
 * Desempate: id de centro menor.
 */
public final class OriginCenterSelector {

    private static final Comparator<CenterStockLevel> BY_STOCK_DESC_THEN_ID =
            Comparator.comparingInt(CenterStockLevel::stock).reversed()
                    .thenComparing(CenterStockLevel::centerId);

    private OriginCenterSelector() {
    }

    public static Optional<CenterStockLevel> select(String storeCity, List<CenterStockLevel> centers) {
        if (centers == null || centers.isEmpty()) {
            return Optional.empty();
        }
        List<CenterStockLevel> available = centers.stream()
                .filter(c -> c.stock() > 0)
                .toList();
        if (available.isEmpty()) {
            return Optional.empty();
        }
        String normalizedStoreCity = normalize(storeCity);
        if (!normalizedStoreCity.isEmpty()) {
            Optional<CenterStockLevel> sameCity = available.stream()
                    .filter(c -> normalizedStoreCity.equals(normalize(c.city())))
                    .min(BY_STOCK_DESC_THEN_ID);
            if (sameCity.isPresent()) {
                return sameCity;
            }
        }
        return available.stream().min(BY_STOCK_DESC_THEN_ID);
    }

    static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String decomposed = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}+", "")
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
