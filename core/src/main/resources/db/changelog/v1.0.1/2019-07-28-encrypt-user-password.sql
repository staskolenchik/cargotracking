ALTER TABLE "user" ALTER COLUMN password TYPE VARCHAR(72);

UPDATE "user" SET password = crypt(password, gen_salt('bf', 10));
