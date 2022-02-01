INSERT INTO "user" (surname, login, password)
VALUES ('', 'admin', 'admin')
ON CONFLICT (login) DO NOTHING ;

INSERT INTO user_role (user_id, role)
VALUES (1, 'SYS_ADMIN')
ON CONFLICT (user_id, role) DO NOTHING;

