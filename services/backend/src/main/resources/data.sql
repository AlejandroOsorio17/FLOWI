INSERT INTO products (code, name, description, price)
SELECT 'CAM-001', 'Basic Cotton T-Shirt', 'White organic cotton t-shirt', 89900.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'CAM-001');

INSERT INTO products (code, name, description, price)
SELECT 'JEA-001', 'Slim Fit Jeans', 'Dark blue slim fit denim jeans', 189900.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'JEA-001');

INSERT INTO products (code, name, description, price)
SELECT 'VES-001', 'Flowing Midi Dress', 'Black flowing midi dress', 229900.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'VES-001');

INSERT INTO products (code, name, description, price)
SELECT 'CHA-001', 'Leather Ankle Boots', 'Black leather ankle boots', 349900.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'CHA-001');

INSERT INTO products (code, name, description, price)
SELECT 'SUD-001', 'Oversized Hoodie', 'Beige oversized cotton hoodie', 159900.00
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'SUD-001');

INSERT INTO stores (code, name, address, city)
SELECT 'STR-BOG-001', 'Zara Andino', 'Carrera 11 No. 82-71', 'Bogota'
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE code = 'STR-BOG-001');

INSERT INTO stores (code, name, address, city)
SELECT 'STR-MDE-001', 'Zara El Tesoro', 'Carrera 25A No. 1A Sur-45', 'Medellin'
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE code = 'STR-MDE-001');

INSERT INTO stores (code, name, address, city)
SELECT 'STR-CLO-001', 'Zara Chipichape', 'Calle 38 Norte No. 6N-45', 'Cali'
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE code = 'STR-CLO-001');

INSERT INTO distribution_centers (code, name, address, city)
SELECT 'DC-BOG-001', 'Bogota Distribution Center', 'Calle 80 No. 100-20', 'Bogota'
WHERE NOT EXISTS (SELECT 1 FROM distribution_centers WHERE code = 'DC-BOG-001');

INSERT INTO distribution_centers (code, name, address, city)
SELECT 'DC-MDE-001', 'Medellin Distribution Center', 'Carrera 50 No. 14-20', 'Medellin'
WHERE NOT EXISTS (SELECT 1 FROM distribution_centers WHERE code = 'DC-MDE-001');

-- Inventario de prueba: relaciona cada tienda con cada producto, cantidad aleatoria (10-100).
-- Idempotente: solo inserta combinaciones store_id+product_id que no existan todavía,
-- así no sobreescribe cantidades ya ajustadas manualmente ni se duplica en cada arranque.
INSERT INTO inventario (store_id, product_id, cantidad)
SELECT s.id, p.id, floor(random() * 91 + 10)::int
FROM stores s
CROSS JOIN products p
WHERE NOT EXISTS (
    SELECT 1 FROM inventario i
    WHERE i.store_id = s.id AND i.product_id = p.id
);
