INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_ADMIN', 'Administrador del sistema'),
(2, 'ROLE_CLIENTE', 'Cliente de la aplicación'),
(3, 'ROLE_ASESOR', 'Asesor de crédito');


-- Inserta el usuario administrador con contraseña encriptada
-- La contraseña es 'admin123'
INSERT INTO users (first_name, last_name, email, password, id_number, phone, base_salary, birth_date, address, id_role)
VALUES (
    'Admin',
    'Crediya',
    'admin@crediya.com',
    '$2a$10$2.G.9u.R.R.B.h.r.Z.Y.e.U.L.s.o.X.U.X.I.A.D.C.W.E.N.G.U.E', -- Corresponde a 'admin123'
    '00000000',
    3001234567,
    5000000.00,
    '1990-01-01',
    'Oficina Principal',
    1 -- ID para ROLE_ADMIN según tu enumeración
) ON CONFLICT (email) DO NOTHING;