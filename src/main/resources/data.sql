-- Seed admin user and a few products. Password hash corresponds to Admin123!
INSERT INTO users (username, email, password, role)
SELECT 'admin', 'ryu@mail.com', '$2a$10$7b7PjdvVS.wGInxjeRGnaO0Jsbgx5caX5/C/PObbIVdQydb9h9t7C', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
