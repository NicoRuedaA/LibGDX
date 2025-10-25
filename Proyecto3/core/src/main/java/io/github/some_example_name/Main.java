package io.github.some_example_name;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

    public class Main extends ApplicationAdapter {

        private SpriteBatch batch;
        private ShapeRenderer shapeRenderer;

        private Level level;

        private Player player;
        private List<Projectile> projectiles;
        private EnemyManager enemyManager;
        private CameraController camController;

        private boolean debugHitboxes = false;

        private float spawnTimer;
        private float spawnInterval = 0.5f; // Generar un enemigo cada 1 segundo

        private UIManager uiManager;

        @Override
        public void create() {

            spawnTimer = 0f;

            batch = new SpriteBatch();
            shapeRenderer = new ShapeRenderer();

            // Player
            player = new Player(new Vector2(Level.WORLD_WIDTH / 2f, Level.WORLD_HEIGHT / 2f));

            // Lista de proyectiles
            projectiles = new ArrayList<>();

            // Fondo
            level = new Level();

            // Enemigos
            enemyManager = new EnemyManager((int)Level.WORLD_WIDTH, (int)Level.WORLD_HEIGHT);

            // Cámara
            camController = new CameraController(800, 600, Level.WORLD_WIDTH, Level.WORLD_HEIGHT);

            uiManager = new UIManager();
        }

        @Override
        public void render() {
            float delta = Gdx.graphics.getDeltaTime();

            spawnTimer += delta;
            if (spawnTimer >= spawnInterval) {
                SpawnEnemy();
                spawnTimer = 0f; // Reiniciar temporizador
            }

            // Alternar hitboxes
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                debugHitboxes = !debugHitboxes;
            }

            // Actualizar jugador
            player.update(delta);

            // Actualizar cámara
            camController.follow(player);

            // Manejar disparos
            handleInput();

            // Actualizar proyectiles
            Iterator<Projectile> iter = projectiles.iterator();
            while (iter.hasNext()) {
                Projectile p = iter.next();
                p.update(delta);
                if (p.isOutOfScreen(Level.WORLD_WIDTH, Level.WORLD_HEIGHT)) {
                    p.dispose();
                    iter.remove();
                }
            }

            // Actualizar enemigos
            enemyManager.update(delta, player.getPosition());

            // Limpiar pantalla
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // 🔹 Dibujar fondo repetido
            level.renderBackground(batch, camController.getCamera());

            // 🔹 Renderizar mundo con cámara
            batch.setProjectionMatrix(camController.getCamera().combined);
            batch.begin();
            player.render(batch);
            enemyManager.render(batch);
            for (Projectile p : projectiles) {
                p.render(batch);
            }
            batch.end();

            // 🔹 Colisiones proyectiles - enemigos
            handleProjectileCollisions();

            // 🔹 Colisiones jugador - enemigos
            handlePlayerCollisions();

            // 🔹 Hitboxes (opcional)
            if (debugHitboxes) {
                shapeRenderer.setProjectionMatrix(camController.getCamera().combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.RED);

                // Hitbox jugador
                Rectangle playerRect = player.getBounds();
                shapeRenderer.rect(playerRect.x, playerRect.y, playerRect.width, playerRect.height);

                // Hitboxes enemigos
                for (Enemy e : enemyManager.getEnemies()) {
                    Rectangle eRect = e.getBounds();
                    shapeRenderer.rect(eRect.x, eRect.y, eRect.width, eRect.height);
                }

                // Hitboxes proyectiles
                for (Projectile p : projectiles) {
                    Rectangle pRect = p.getBounds();
                    shapeRenderer.rect(pRect.x, pRect.y, pRect.width, pRect.height);
                }

                shapeRenderer.end();
            }

            batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // 2. Dibujamos la UI
            batch.begin();
            uiManager.render(batch, player.getHealth());
            batch.end();

        }

        private void handleInput() {
            player.shoot(projectiles, camController);
        }

        private void handleProjectileCollisions() {
            Iterator<Projectile> projectileIter = projectiles.iterator();
            while (projectileIter.hasNext()) {
                Projectile p = projectileIter.next();
                Iterator<Enemy> enemyIter = enemyManager.getEnemies().iterator();
                while (enemyIter.hasNext()) {
                    Enemy e = enemyIter.next();
                    if (p.getBounds().overlaps(e.getBounds())) {

                        // --- AÑADIR ESTA LÍNEA ---
                        p.dispose(); // <-- ¡Faltaba esto!
                        // --- FIN DE LÍNEA A AÑADIR ---

                        projectileIter.remove();
                        enemyIter.remove();
                        break;
                    }
                }
            }
        }

        private void handlePlayerCollisions() {
            Rectangle playerRect = player.getBounds();
            for (Enemy e : enemyManager.getEnemies()) {
                if (playerRect.overlaps(e.getBounds())) {
                    restartGame();
                    break;
                }
            }
        }

        private void restartGame() {
            // (Usa la variable de Level)
            player = new Player(new Vector2(Level.WORLD_WIDTH / 2f, Level.WORLD_HEIGHT / 2f));
            for (Projectile p : projectiles) {
                p.dispose();
            }
            projectiles.clear();
            enemyManager.getEnemies().clear();
            camController.follow(player);
        }

        @Override
        public void dispose() {
            batch.dispose();
            shapeRenderer.dispose();
            level.dispose();
            player.dispose();
            enemyManager.dispose();
            uiManager.dispose();
            for (Projectile p : projectiles) {
                p.dispose();
            }
        }

        public void SpawnEnemy(){
            enemyManager.spawnEnemy();
        }

    }
