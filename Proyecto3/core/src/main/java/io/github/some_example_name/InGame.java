    package io.github.some_example_name;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.Screen; // Importante
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.GL20;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
    import com.badlogic.gdx.math.Rectangle;
    import com.badlogic.gdx.math.Vector2;

    import java.util.ArrayList;
    import java.util.Iterator;
    import java.util.List;

    import io.github.some_example_name.PowerUp.*;
    import io.github.some_example_name.ElectroballProjectile;

    /**
     * Esta es la pantalla principal del juego.
     * Implementa 'Screen' y es controlada por 'Proyecto3Game'.
     */
    public class InGame implements Screen {

        // --- Referencias de Juego ---
        private Proyecto3Game game;

        private PowerUpManager powerupManager;

        // --- Herramientas de Renderizado ---
        private SpriteBatch batch;
        private ShapeRenderer shapeRenderer;

        // --- Entidades del Mundo ---
        private Background background;
        private Player player;
        private List<FireProjectile> fireProjectiles;
        private EnemyManager enemyManager;
        private CameraController camController;
        private InputManager inputManager;

        // --- UI ---
        private UIManager uiManager;
        private LevelUpUI levelUpUI; // La UI para subir de nivel

        // --- Estado del Juego ---
        private boolean isGameOver = false; // Flag para volver al menú
        private boolean debugHitboxes = false;
        private float spawnTimer;
        private float spawnInterval = 0.5f;

        private PowerUpEffect healthUpgrade;
        private PowerUpEffect projectileUpgrade;
        private PowerUpEffect energyBallUpgrade;

        private List<ElectroballProjectile> activeElectroballs;


        /**
         * Constructor de la pantalla de juego.
         * Aquí se inicializa todo lo necesario para una partida.
         */
        public InGame(Proyecto3Game game) {
            this.game = game; // Guarda la referencia al gestor de pantallas

            // Reinicia el GameManager al empezar una nueva partida
            GameManager.getInstance().reset();

            // --- Inicialización de Herramientas ---
            batch = new SpriteBatch();
            shapeRenderer = new ShapeRenderer();
            // --- Inicialización del Mundo ---
            background = new Background();

            // --- Inicialización de Entidades ---
            inputManager = new InputManager();
            player = new Player(new Vector2(Background.WORLD_WIDTH / 2f, Background.WORLD_HEIGHT / 2f), inputManager);
            fireProjectiles = new ArrayList<>();
            enemyManager = new EnemyManager((int) Background.WORLD_WIDTH, (int) Background.WORLD_HEIGHT);
            powerupManager = new PowerUpManager();

            // --- Inicialización de Cámara y UI ---
            camController = new CameraController(800, 600, Background.WORLD_WIDTH, Background.WORLD_HEIGHT);
            uiManager = new UIManager();

            // Temporizador de spawn
            spawnTimer = 0f;

            healthUpgrade = new PowerUp_MoreHealth();
            projectileUpgrade = new PowerUp_MoreProjectiles();
            energyBallUpgrade = new PowerUp_EnergyBall();

            // Qué hacer si se elige "Vida"
            Runnable onHealthSelected = () -> {
                OnHealthSelected();
            };

            // Qué hacer si se elige "Proyectil"
            Runnable onProjectileSelected = () -> {
                OnProjectileSelected();
            };

            Runnable onEnergyBallSelected = () -> { // 👈 Crea la nueva receta
                OnEnergyBallSelected();
            };

            // Instancia la UI pasándole las acciones
            levelUpUI = new LevelUpUI(onHealthSelected, onProjectileSelected, onEnergyBallSelected); // (Asumo que ya tienes 3)
            activeElectroballs = new ArrayList<>();
        }

        /**
         * El bucle principal de renderizado.
         */
        @Override
        public void render(float delta) {

            // 1. --- LÓGICA DE JUEGO (UPDATES) ---
            //    Solo se ejecuta si el GameManager dice que el juego está "CORRIENDO"
            if (GameManager.getInstance().isRunning()) {

                inputManager.update();

                if (inputManager.didPressLevelUpDebug()) {
                    // Pausa el juego y muestra la UI de nivel
                    GameManager.getInstance().pauseForLevelUp();
                    Gdx.input.setInputProcessor(levelUpUI.getStage());
                }

                // Lógica de Spawn
                spawnTimer += delta;
                if (spawnTimer >= spawnInterval) {
                    SpawnEnemy();
                    spawnTimer = 0f;
                }



                for (ElectroballProjectile ball : activeElectroballs) {
                    ball.update(delta);
                }

                // Input de Debug
                if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                    debugHitboxes = !debugHitboxes;
                }

                // Actualización de Entidades
                player.update(delta);
                camController.follow(player);

                handleInput();

                // Actualizar Proyectiles
                Iterator<FireProjectile> iter = fireProjectiles.iterator();
                while (iter.hasNext()) {
                    FireProjectile p = iter.next();
                    p.update(delta, player);
                    if (p.isOutOfScreen(Background.WORLD_WIDTH, Background.WORLD_HEIGHT)) {
                        p.dispose();
                        iter.remove();
                    }
                }

                // Actualizar Enemigos
                enemyManager.update(delta, player.getPosition());
                // Actualizar mejoras
                powerupManager.update(delta);


                // Comprobación de Colisiones
                handleProjectileCollisions();
                handlePlayerCollisions();
                handleElectroballCollisions();

            } // --- Fin del bloque 'if (GameManager.getInstance().isRunning())' ---


            // 2. --- LÓGICA DE DIBUJADO (RENDER) ---
            //    Esto se ejecuta siempre, para mostrar el juego congelado si está pausado

            // Limpiar pantalla
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Dibujar el fondo
            background.renderBackground(batch, camController.getCamera());

            // Dibujar el mundo (con cámara)
            batch.setProjectionMatrix(camController.getCamera().combined);
            batch.begin();
            player.render(batch);
            enemyManager.render(batch);

            //dibuja las bolas que giran
            for (ElectroballProjectile ball : activeElectroballs) {
                ball.render(batch);
            }
            //dibuja fuego proyectiles
            for (FireProjectile p : fireProjectiles) {
                p.render(batch);
            }
            batch.end();

            // Dibujar Hitboxes (opcional)
            if (debugHitboxes) {
                DebugHitboxes();
            }

            // Dibujar UI principal (corazones, exp) - (sin cámara)
            batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.begin();
            uiManager.render(batch, player.getHealth(), player.getCurrentExp(), player.getExpToLevelUp());
            batch.end();

            // Dibujar UI de Level Up (si está pausado)
            if (GameManager.getInstance().isPausedForLevelUp()) {
                levelUpUI.render(delta); // Dibuja la UI de elección
            }

            // Comprobación de Game Over (al final de todo)
            if (isGameOver) {
                Gdx.input.setInputProcessor(null); // Limpia el input
                game.setScreen(new MainMenu(game)); // Vuelve al menú
                return; // Sale del render
            }
        }

        /**
         * Llama al método 'shoot' del jugador.
         */
        private void handleInput() {
            // Solo permite disparar si el juego está corriendo
            if (GameManager.getInstance().isRunning()) {

                // --- 1. LEE EL ESTADO DEL INPUT MANAGER ---
                boolean isShooting = inputManager.isShooting();

                // --- 2. PASA EL ESTADO AL JUGADOR ---
                player.updateShooting(isShooting, fireProjectiles, camController);
                //player.shoot(isShooting, projectiles, camController);
            }
        }

        /**
         * Gestiona colisiones entre proyectiles y enemigos.
         */
        private void handleProjectileCollisions() {
            Iterator<FireProjectile> projectileIter = fireProjectiles.iterator();
            while (projectileIter.hasNext()) {
                FireProjectile p = projectileIter.next();
                Iterator<Enemy> enemyIter = enemyManager.getEnemies().iterator();
                while (enemyIter.hasNext()) {
                    Enemy e = enemyIter.next();
                    if (p.getBounds().overlaps(e.getBounds())) {

                        p.dispose();
                        projectileIter.remove();
                        //enemyIter.remove(); // El enemigo muere
                        e.takeDamage(1);
                        if (!e.isAlive()) {

                            // 4. El GameScreen lo ELIMINA de la lista
                            enemyIter.remove(); // <-- ¡ESTA ES LA ELIMINACIÓN CORRECTA!
                            // Comprueba si el jugador sube de nivel
                            if (player.addExp(1)) {
                                // Pausa el juego y da el control a la UI de level up
                                GameManager.getInstance().pauseForLevelUp();
                                Gdx.input.setInputProcessor(levelUpUI.getStage());
                            }
                        }

                        break; // El proyectil solo puede golpear a un enemigo
                    }
                }
            }
        }

        /**
         * Gestiona colisiones entre el jugador y los enemigos.
         */
        private void handlePlayerCollisions() {
            Rectangle playerRect = player.getBounds();
            for (Enemy e : enemyManager.getEnemies()) {
                if (playerRect.overlaps(e.getBounds())) {

                    player.takeDamage(1); // El jugador pierde vida

                    if (player.isAlive()) {
                        // Sigue vivo: Reinicia la posición y limpia enemigos
                        player.resetPosition();
                        enemyManager.clearAll(); // Limpia enemigos Y libera memoria
                        powerupManager.clearAll();
                        for(ElectroballProjectile ball : activeElectroballs) { ball.dispose(); }
                        activeElectroballs.clear();
                    } else {
                        // Muerto: activa el Game Over
                        restartGame();
                    }

                    break; // Solo nos golpea un enemigo a la vez
                }
            }
        }

        private void handleElectroballCollisions() {
            // Itera por cada bola activa
            for (ElectroballProjectile ball : activeElectroballs) {

                // Si la bola no está lista para golpear (en cooldown),
                // sáltatela y comprueba la siguiente bola.
                if (!ball.isReadyToHit()) {
                    continue;
                }

                Rectangle ballRect = ball.getBounds();

                // Itera por cada enemigo
                Iterator<Enemy> enemyIter = enemyManager.getEnemies().iterator();
                while (enemyIter.hasNext()) {
                    Enemy e = enemyIter.next();

                    // Comprueba si la bola golpea al enemigo
                    if (ballRect.overlaps(e.getBounds())) {

                        // ¡Colisión!

                        // 1. Reinicia el timer de la bola (para que no golpee 60/s)
                        ball.resetHitTimer();

                        // 2. El enemigo recibe daño
                        e.takeDamage(ball.getDamage());

                        // 3. Comprueba si el enemigo murió
                        if (!e.isAlive()) {
                            enemyIter.remove(); // Elimina al enemigo

                            // Otorga EXP (¡la bola también da exp!)
                            if (player.addExp(1)) {
                                GameManager.getInstance().pauseForLevelUp();
                                Gdx.input.setInputProcessor(levelUpUI.getStage());
                            }
                        }

                        // NOTA: A diferencia del proyectil de fuego,
                        // NO rompemos el bucle 'while' aquí.
                        // Esto permite que una bola golpee a MÚLTIPLES
                        // enemigos que estén apilados.
                    }
                }
            }
        }

        private void OnHealthSelected() {
            healthUpgrade.apply(this, player); // Pasa 'this' (InGame)
            resumeGameFromLevelUp();
        }

        private void OnProjectileSelected() {
            projectileUpgrade.apply(this, player); // Pasa 'this' (InGame)
            resumeGameFromLevelUp();
        }

        private void OnEnergyBallSelected() {
            // 1. Aplica la lógica de la MEJORA
            energyBallUpgrade.apply(this, player); // Pasa 'this' (InGame)
            resumeGameFromLevelUp();
        }

        public void spawnElectroballProjectile(Character target) {
            activeElectroballs.add(new ElectroballProjectile(target));
        }

        private void resumeGameFromLevelUp() {
            GameManager.getInstance().resumeGame();
            Gdx.input.setInputProcessor(null);
        }

        private void DebugHitboxes() {
            shapeRenderer.setProjectionMatrix(camController.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            Rectangle playerRect = player.getBounds();
            shapeRenderer.rect(playerRect.x, playerRect.y, playerRect.width, playerRect.height);

            for (Enemy e : enemyManager.getEnemies()) {
                Rectangle eRect = e.getBounds();
                shapeRenderer.rect(eRect.x, eRect.y, eRect.width, eRect.height);
            }
            for (FireProjectile p : fireProjectiles) {
                Rectangle pRect = p.getBounds();
                shapeRenderer.rect(pRect.x, pRect.y, pRect.width, pRect.height);
            }

            for (ElectroballProjectile ball : activeElectroballs) {
                Rectangle bRect = ball.getBounds();
                shapeRenderer.rect(bRect.x, bRect.y, bRect.width, bRect.height);
            }

            shapeRenderer.end();
        }

        /**
         * Pone el flag de 'isGameOver' a true.
         * El bucle render se encargará de cambiar de pantalla.
         */
        private void restartGame() {
            this.isGameOver = true;
        }

        /**
         * Llama al 'spawnEnemy' del manager.
         */
        public void SpawnEnemy(){
            Vector2 playerPos = player.getPosition();
            // 2. Se la pasamos al manager
            enemyManager.spawnEnemy(playerPos);
        }

        // --- Métodos de la interfaz Screen ---

        @Override
        public void show() {
            // Se llama cuando esta pantalla se vuelve la activa
            // (Podríamos reiniciar el input aquí, pero ya lo hacemos al reanudar)
        }

        @Override
        public void resize(int width, int height) {
            // Actualiza la cámara y la UI si la ventana cambia de tamaño
            camController.resize(width, height);
            levelUpUI.getStage().getViewport().update(width, height, true);
        }

        @Override
        public void pause() {
            // (No lo usamos, nuestro GameManager maneja la pausa)
        }

        @Override
        public void resume() {
            // (No lo usamos)
        }

        @Override
        public void hide() {
            // Se llama cuando cambiamos a otra pantalla (ej: volvemos al menú)
            // ¡Liberamos la memoria de esta pantalla!
            dispose();
        }

        @Override
        public void dispose() {
            // Libera todos los recursos
            batch.dispose();
            shapeRenderer.dispose();
            background.dispose();
            player.dispose();
            enemyManager.dispose();
            uiManager.dispose();
            levelUpUI.dispose();
            powerupManager.dispose();

            // Libera proyectiles restantes
            for (FireProjectile p : fireProjectiles) {
                p.dispose();
            }

            for(ElectroballProjectile ball : activeElectroballs) { ball.dispose(); }
        }
    }
