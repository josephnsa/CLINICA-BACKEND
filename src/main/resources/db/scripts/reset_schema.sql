-- Ejecutar MANUALMENTE en PostgreSQL cuando quieras dejar la BD limpia
-- para que Flyway vuelva a ejecutar V1, V2, V3, V4 desde cero.
--
-- Ejemplo: psql -U postgres -d postgres -f src/main/resources/db/scripts/reset_schema.sql
-- O pegar este contenido en pgAdmin / DBeaver y ejecutar.
--
-- IMPORTANTE: Esto borra TODO el esquema public (tablas, datos, y la tabla flyway_schema_history).
-- Después de ejecutarlo, inicia la aplicación para que Flyway aplique las migraciones.

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

-- Opcional: restablecer permisos por defecto del esquema
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
