# ============================================================================================================
#           -----< EXERGAMES PARA REHABILITACIÓN CERVICAL >-----
#
#           TRABAJO: Trabajo de fin de grado de Ingeniería Informática
#           AUTOR: Mario Ventura Burgos
#           FECHA: 05/2024
#           FICHERO: Script de creación de inserts para la base de datos de exergames
# ============================================================================================================

from faker import Faker
import random

def generate_inserts(num_users, filename):
    faker = Faker()
    with open(filename, 'w', encoding='utf-8') as file:

        # =======================================
        # ====== INSERTS PARA PROBLEMA_CERVICAL
        # =======================================
        # Limitaciones de movimiento horizontal
        insert_command = f"INSERT INTO public.problema_cervical(idCerv, name) VALUES ({1}, 'Limitaciones de movimiento horizontal') ON CONFLICT (idCerv) DO NOTHING;\n"
        file.write(insert_command)
        # Limitaciones de movimiento vertical
        insert_command = f"INSERT INTO public.problema_cervical(idCerv, name) VALUES ({2}, 'Limitaciones de movimiento vertical') ON CONFLICT (idCerv) DO NOTHING;\n"
        file.write(insert_command)
        # Limitaciones de movimiento en ambos sentidos
        insert_command = f"INSERT INTO public.problema_cervical(idCerv, name) VALUES ({3}, 'Limitaciones de movimiento horizontal y vertical') ON CONFLICT (idCerv) DO NOTHING;\n"
        file.write(insert_command)

        # =======================================
        # ====== INSERTS DE LA TABLA JUEGO ======
        # =======================================
        games = []
        # Bricks Breaker
        insert_command = f"INSERT INTO public.juego(idGame, name, description, instructions, difficulty, idCerv) VALUES ({1}, 'Bricks Breaker', 'Un adictivo juego clásico donde debes romper ladrillos usando una bola que rebota en tu paleta. Dirige la bola para destruir todos los ladrillos y avanza a través de niveles desafiantes. ¡No dejes caer la bola o perderás!', 'Haz movimientos horizontales con la cabeza para mover la pala y hacer que la pelota rebote contra los ladrillos.', {3.0}, {1}) ON CONFLICT (idGame) DO NOTHING;\n"
        file.write(insert_command)
        games.append(1)
        # Pacman
        insert_command = f"INSERT INTO public.juego(idGame, name, description, instructions, difficulty, idCerv) VALUES ({2}, 'Pacman', 'Un icónico juego de arcade donde controlas a Pacman en un laberinto lleno de puntos y fantasmas. Come todos los puntos y frutas para ganar puntos, y usa power-ups para hacer que los fantasmas se vuelvan vulnerables. ¡Cuidado con los fantasmas!', 'Mueve la cabeza vertical y horizontalmente para mover a pacman a través del laberinto. Evita movimientos bruscos que puedan hacerte daño o marearte.', {4.5}, {3}) ON CONFLICT (idGame) DO NOTHING;\n"
        file.write(insert_command)
        games.append(2)
        # Flappy Bird
        insert_command = f"INSERT INTO public.juego(idGame, name, description, instructions, difficulty, idCerv) VALUES ({3}, 'Flappy Bird', 'Un desafiante juego de habilidad donde controlas a un pájaro que debe volar entre tuberías sin tocarlas. Toca la pantalla para mantener al pájaro en vuelo y trata de superar la mayor cantidad de obstáculos posible. ¡Un solo choque y es el fin!', 'Mueve la cabeza haciendo movimientos verticales para guiar al pájaro a través de las tuberías.', {3.5}, {2}) ON CONFLICT (idGame) DO NOTHING;\n"
        file.write(insert_command)
        games.append(3)
        # Snake Game
        insert_command = f"INSERT INTO public.juego(idGame, name, description, instructions, difficulty, idCerv) VALUES ({4}, 'Snake Game', 'Controla una serpiente que crece al comer comida esparcida por el campo. Guía a la serpiente con cuidado para evitar chocar contra las paredes o su propio cuerpo mientras intentas crecer lo más posible. ¡Cada bocado hace que el juego sea más desafiante!', 'Haz movimientos horizontales y verticales con la cabeza para hacer que la serpiente se mueva y coma fruta. Evita chocarte con las paredes o contigo mismo, e intenta no marearte o hacer movimientos bruscos.', {3.0}, {3}) ON CONFLICT (idGame) DO NOTHING;\n"
        file.write(insert_command)
        games.append(4)
        
        # =======================================
        # ====== INSERTS DE TABLA USUARIO =======
        # =======================================
        # Usuarios "reales" para pruebas
        usernames = []
        insert_command = f"INSERT INTO public.usuario(username, name, lastname, password, level, xp) VALUES ('test', 'test', 'test', 'test', {9999}, {100}) ON CONFLICT (username) DO NOTHING;\n"
        file.write(insert_command)
        usernames.append('test')
        insert_command = f"INSERT INTO public.usuario(username, name, lastname, password, level, xp) VALUES ('xisca.roig', 'Xisca', 'Roig', '1234', {9999}, {100}) ON CONFLICT (username) DO NOTHING;\n"
        file.write(insert_command)
        usernames.append('xisca.roig')
        insert_command = f"INSERT INTO public.usuario(username, name, lastname, password, level, xp) VALUES ('mario.ventura', 'Mario', 'Ventura', '8522', {9999}, {100}) ON CONFLICT (username) DO NOTHING;\n"
        file.write(insert_command)
        usernames.append('mario.ventura')
        # Usuarios aleatorios para rellenar y dar credibilidad a la base de datos
        for _ in range(num_users):
            name = faker.first_name()
            lastname = faker.last_name()
            username = f"{name}.{lastname}".lower().replace(' ', '')
            usernames.append(username)
            password = faker.password(length=8, special_chars=False, digits=True, upper_case=True, lower_case=True)
            level = random.randint(1, 1000)
            xp = random.uniform(1, 1000)
            insert_command = f"INSERT INTO public.usuario(username, name, lastname, password, level, xp) VALUES ('{username}', '{name}', '{lastname}', '{password}', {level}, {xp}) ON CONFLICT (username) DO NOTHING;\n"
            file.write(insert_command)
        
        # =======================================
        # ====== INSERTS DE TABLA PUNTOS ========
        # =======================================
        # Generar inserts para la tabla PUNTOS
        for username in usernames:
            for idGame in games:
                numPoints = random.randint(1, 150) * 100
                # fechaHora = faker.date_time_this_year().strftime('%Y-%m-%d %H:%M:%S')
                insert_command = f"INSERT INTO public.puntos(numPoints, idGame, username) VALUES ({numPoints}, {idGame}, '{username}') ON CONFLICT (idGame, username, fechaHora) DO NOTHING;\n"
                file.write(insert_command)
        
        # =======================================
        # ====== INSERTS PARA LOS FOLLOWERS =====
        # =======================================
        # Generar inserts para la tabla R_USUARIO_USUARIO
        for username1 in usernames:
            username2 = random.choice([u for u in usernames if u != username1])
            insert_command = f"INSERT INTO public.r_usuario_usuario(username1, username2) VALUES ('{username1}', '{username2}') ON CONFLICT (username1, username2) DO NOTHING;\n"
            file.write(insert_command)

if __name__ == "__main__":
    generate_inserts(10000, r'C:/Users/Usuario/Desktop/Exergames para rehabilitacion cervical/Database/Inserts/InsertsGenerales.txt')
    

