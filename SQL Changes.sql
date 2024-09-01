-- -------------------------------------------------------------------------------------------------------------------------------------
--                  SCRIPT CON EL CÓDIGO DE CREACIÓN DE LA BASE DE DATOS DE EXERGAMES CON SUS RESPECTIVAS TABLAS
--                  GESTOR USADO: PostgreSQL (sqlShell y pgAdmin 4)
--                  AUTOR: Mario Ventura Burgos
--                  FECHA: 04-2024 - Curso escolar 2023-2024
--                  PROYECTO: Trabajo de Fin de Grado: Exergames para rehabilitación cervical
-- -------------------------------------------------------------------------------------------------------------------------------------

-- Usuario por defecto 'postgres' con contraseña 'root'

-- CREAR BASE DE DATOS
CREATE DATABASE exergames;

-- CONEXIÓN A LA BASE DE DATOS
\c exergames;

-- CREAR TABLAS
-- INSERTS PARA TABLA USUARIO GENERADOS CON SCRIPT 'INSERT_USER_Exergames.py'
CREATE TABLE USUARIO(
    username VARCHAR(30) NOT NULL,          -- Nombre de usuario
    name VARCHAR(30) NOT NULL,              -- Nombre
    lastname VARCHAR(30) NOT NULL,          -- Apellido
    password VARCHAR(25) NOT NULL,          -- Contraseña
    xp INT NOT NULL,                        -- Puntos de XP
    level INT,                              -- Nivel
    CONSTRAINT PK_USUARIO PRIMARY KEY (username),
    CONSTRAINT CHECK_XP CHECK (xp >= 0 AND xp <= 1000)
);

-- INSERTS PARA USUARIO GENERADOS CON EL SCRIPT 'INSERT_USER_Exergames.py'
CREATE TABLE PROBLEMA_CERVICAL(
    idCerv SERIAL NOT NULL,                 -- ID del problema cervical
    name VARCHAR(60) NOT NULL,              -- Nombre del problema cervical
    CONSTRAINT PK_PROBLEMA_CERVICAL PRIMARY KEY (idCerv)
);

-- INSERTS PARA LA TABLA JUEGO GENERADOS CON EL SCRIPT 'INSERT_USER_Exergames.py'
CREATE TABLE JUEGO(
    idGame SERIAL NOT NULL,                 -- ID del juego
    name VARCHAR(30) NOT NULL,              -- Nombre del juego
    description VARCHAR(300),               -- Descripcion (opcional)
    instructions VARCHAR(300),              -- Instrucciones del juego
    difficulty FLOAT NOT NULL,              -- Dificultad del juego (numero del 1 al 10)
    idCerv INT NOT NULL,                    -- ID del problema cervical asociado al juego
    CONSTRAINT PK_JUEGO PRIMARY KEY (idGame),
    CONSTRAINT FK_JUEGO_PC FOREIGN KEY (idCerv) REFERENCES PROBLEMA_CERVICAL(idCerv),
    CONSTRAINT DIFFICULTY_OK CHECK(difficulty >= 0 AND difficulty <= 5)
);

-- INSERTS DE PUNTOS GENERADOS CON EL SCRIPT 'INSERT_USER_Exergames.py'
CREATE TABLE PUNTOS(
    numPoints INT NOT NULL,                         -- Numero de puntos de un usuario concreto en un juego concreto
    idGame INT NOT NULL,                            -- ID del Juego en el que el usuario tiene los puntos
    username VARCHAR(50) NOT NULL,                  -- username del usuario que tiene los puntos
    fechaHora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- fecha en la que se obtuvieron esos puntos
    CONSTRAINT PK_PUNTOS PRIMARY KEY (idGame, username, fechaHora),
    CONSTRAINT FK_PUNTOS_JUEGO FOREIGN KEY (idGame) REFERENCES JUEGO(idGame),
    CONSTRAINT FK_PUNTOS_USUARIO FOREIGN KEY (username) REFERENCES USUARIO(username)
);

-- INSERTS PARA FOLLOWERS GENERADOS CON EL SCRIPT 'INSERT_USER_Exergames.py'
CREATE TABLE R_USUARIO_USUARIO(
    username1 VARCHAR(50) NOT NULL,         -- Nombre de usuario 1
    username2 VARCHAR(50) NOT NULL,         -- Nombre de usuario 2
    CONSTRAINT PK_R_USUARIO_USUARIO PRIMARY KEY (username1, username2),
    CONSTRAINT FK_R_USUARIO1_FOLLOWS FOREIGN KEY (username2) REFERENCES USUARIO(username),
    CONSTRAINT FK_R_USUARIO2_FOLLOWS FOREIGN KEY (username1) REFERENCES USUARIO(username)
);

-- -------------------------------------------------------------------------------------------------------------------------------------
--                          GENERAR INSERTS CON LLAMADA AL SCRIPT DE PYTHON 'INSERTS_Exergames.py
--                          comando:    python INSERTS_Exergames.py
--                          Copiar inserts generados en .txt en la consola de SQL Shell pa insertar datos
-- -------------------------------------------------------------------------------------------------------------------------------------

python INSERTS_Exergames.py

-- -------------------------------------------------------------------------------------------------------------------------------------
--                          FUNCIONES QUE SERAN LLAMADAS DESDE LA APP DE ANDROID STUDIO (cada una tiene su propio .sql)
-- -------------------------------------------------------------------------------------------------------------------------------------
-- Comprobar si existe username
CREATE OR REPLACE FUNCTION public.findUsername(username VARCHAR)
RETURNS SETOF usuario AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM usuario
    WHERE usuario.username = $1;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;


-- Funcion que crea una cuenta (usuario)
CREATE OR REPLACE FUNCTION public.create_user(
    p_username VARCHAR,
    p_name VARCHAR,
    p_lastname VARCHAR,
    p_password VARCHAR,
    p_level INT DEFAULT 0,
    p_xp FLOAT DEFAULT 0.0  -- valor por defecto en caso de que no se especifique
)
RETURNS BOOLEAN AS $$
BEGIN
    -- Intentar insertar un nuevo usuario en la tabla
    INSERT INTO usuario (username, name, lastname, password, level, xp)
    VALUES (p_username, p_name, p_lastname, p_password, p_level, p_xp);

    -- Si la inserción es exitosa, retornar verdadero
    RETURN TRUE;
EXCEPTION WHEN UNIQUE_VIOLATION THEN
    -- Si se viola la restricción de unicidad, retornar falso
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;


-- Log in en una cuenta ya existente
CREATE OR REPLACE FUNCTION public.login(username VARCHAR, password VARCHAR)
RETURNS SETOF usuario AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM usuario
    WHERE usuario.username = $1 AND usuario.password = $2;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;


-- Obtiene el numero de seguidores de un usuario
CREATE OR REPLACE FUNCTION get_followers_count(p_username VARCHAR)
RETURNS INTEGER AS $$
DECLARE
    follower_count INTEGER;
BEGIN
    SELECT COUNT(username1) INTO follower_count
    FROM public.r_usuario_usuario
    WHERE username2 = p_username
    GROUP BY username2;

    RETURN follower_count;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT get_followers_count('mario.ventura');"


-- Funcion para hacer un update de un usuario en profile detail
CREATE OR REPLACE FUNCTION update_user_profile(
    p_username VARCHAR,
    p_name VARCHAR,
    p_lastname VARCHAR,
    p_password VARCHAR,
    p_xp INT,
    p_level INT
)
RETURNS BOOLEAN AS $$
DECLARE
    row_count INT;
BEGIN
    UPDATE USUARIO
    SET 
        name = p_name,
        lastname = p_lastname,
        password = p_password,
        xp = p_xp,
        level = p_level
    WHERE 
        username = p_username;

    GET DIAGNOSTICS row_count = ROW_COUNT;

    IF row_count > 0 THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT update_user_profile('mario.ventura', 'Mario', 'Ventura', '8522', 99, 9999);"


-- Funcion para seguir a un usuario
CREATE OR REPLACE FUNCTION follow_user(
    p_username1 VARCHAR,
    p_username2 VARCHAR
)
RETURNS VOID AS $$
BEGIN
    INSERT INTO R_USUARIO_USUARIO (username1, username2)
    VALUES (p_username1, p_username2)
    ON CONFLICT DO NOTHING;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT follow_user('john_doe', 'jane_smith');"


-- Funcion para saber si un usuario sigue a otro
CREATE OR REPLACE FUNCTION is_following(
    p_username1 VARCHAR,
    p_username2 VARCHAR
)
RETURNS BOOLEAN AS $$
DECLARE
    v_exists BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM R_USUARIO_USUARIO
        WHERE username1 = p_username1 AND username2 = p_username2
    ) INTO v_exists;

    RETURN v_exists;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT * FROM is_following('john_doe', 'jane_smith');  -- Retorna TRUE o FALSE"


-- Funcion para dejar de seguir a un usuario
CREATE OR REPLACE FUNCTION unfollow_user(
    p_username1 VARCHAR,
    p_username2 VARCHAR
)
RETURNS VOID AS $$
BEGIN
    DELETE FROM R_USUARIO_USUARIO
    WHERE username1 = p_username1 AND username2 = p_username2;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT unfollow_user('john_doe', 'jane_smith');"


-- Funcion para obtener los detalles de un juego
CREATE OR REPLACE FUNCTION get_game_details(p_idGame INT)
RETURNS TABLE(idGame INT, name VARCHAR, description VARCHAR, instructions VARCHAR, difficulty FLOAT, idCerv INT) AS $$
BEGIN
    RETURN QUERY
    SELECT J.idGame, J.name, J.description, J.instructions, J.difficulty, J.idCerv
    FROM JUEGO J
    WHERE J.idGame = p_idGame;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT * FROM get_game_details(1);  -- Reemplaza '1' con el idGame deseado"


-- Funcion para buscar usuario con la barra de busqueda
CREATE OR REPLACE FUNCTION search_user(
    p_search_term VARCHAR
)
RETURNS TABLE(username VARCHAR, name VARCHAR, lastname VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT U.username, U.name, U.lastname
    FROM USUARIO U
    WHERE U.username ILIKE '%' || p_search_term || '%' 
       OR U.name ILIKE '%' || p_search_term || '%'
       OR U.lastname ILIKE '%' || p_search_term || '%';
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT * FROM search_user('john');  -- Reemplaza 'john' con el término de búsqueda deseado"


-- Funcion para ver los puntos totales de un jugador en un juego
CREATE OR REPLACE FUNCTION get_total_points(
    p_username VARCHAR,
    p_idGame INT
)
RETURNS INT AS $$
DECLARE
    v_total_points INT;
BEGIN
    SELECT COALESCE(SUM(numPoints), 0)
    INTO v_total_points
    FROM PUNTOS
    WHERE username = p_username AND idGame = p_idGame;

    RETURN v_total_points;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT get_total_points('john_doe', 1);  -- Reemplaza 'john_doe' con el username y '1' con el idGame deseado"


-- Function para obtener todos los problemas cervicales asociados con un juego en concreto
CREATE OR REPLACE FUNCTION get_cervical_problems_for_game(p_idGame INT)
RETURNS TABLE(idCerv INT, name VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT PC.idCerv, PC.name
    FROM PROBLEMA_CERVICAL PC
    JOIN JUEGO J ON PC.idCerv = J.idCerv
    WHERE J.idGame = p_idGame;
END;
$$ LANGUAGE plpgsql;
-- Llamada con "SELECT * FROM get_cervical_problems_for_game(?);""


-- Fucntion para guardar el progreso de un usuario en un juego
CREATE OR REPLACE FUNCTION insert_puntos(
    p_username VARCHAR(30),
    p_idGame INT,
    p_numPoints INT,
    p_fechaHora TIMESTAMP
) RETURNS VOID AS $$
BEGIN
    INSERT INTO PUNTOS (numPoints, idGame, username, fechaHora)
    VALUES (p_numPoints, p_idGame, p_username, p_fechaHora);
EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION 'El idGame % o username % no existen en las tablas relacionadas.', p_idGame, p_username;
    WHEN unique_violation THEN
        RAISE NOTICE 'Ya existe un registro para idGame %, username % y fechaHora %. Registro no insertado.', p_idGame, p_username, p_fechaHora;
    WHEN others THEN
        RAISE EXCEPTION 'Ocurrió un error inesperado: %', SQLERRM;
END;
$$ LANGUAGE plpgsql;
-- Ejemplo de uso de la función insert_puntos
-- SELECT insert_puntos('mario.ventura', 2, 1500, '2024-01-05 05:34:48');
