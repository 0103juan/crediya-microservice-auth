INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_ADMIN', 'Administrador del sistema'),
(2, 'ROLE_CLIENTE', 'Cliente de la aplicación'),
(3, 'ROLE_ASESOR', 'Asesor de crédito')
ON CONFLICT (id) DO NOTHING;


-- Inserta el usuario administrador con contraseña encriptada
-- La contraseña es 'admin123'
INSERT INTO users (first_name, last_name, email, password, id_number, phone, base_salary, birth_date, address, id_role)
VALUES (
    'Admin',
    'Crediya',
    'admin@crediya.com',
    '$2a$12$pLu9zSUlgBwWWEw4HFdeS.lCfOFIK7ayowp22NgMpXx9BPZiDrkGG',
    '00000000',
    '3001234567',
    5000000.00,
    '1990-01-01',
    'Oficina Principal',
    1
) ON CONFLICT (email) DO NOTHING;