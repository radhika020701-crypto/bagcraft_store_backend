-- Sample seed data. Runs automatically on startup only if you set
-- spring.sql.init.mode=always (ddl-auto=update won't re-run this after first success).
-- Safer: run these manually once via a SQL client after the app has created the tables.

INSERT INTO CATEGORY (name, slug, description) VALUES ('Bags', 'bags', 'Handbags, totes and clutches');
INSERT INTO CATEGORY (name, slug, description) VALUES ('Jewelry', 'jewelry', 'Necklaces, earrings and bangles');
INSERT INTO CATEGORY (name, slug, description) VALUES ('Sarees', 'sarees', 'Silk, cotton and designer sarees');
