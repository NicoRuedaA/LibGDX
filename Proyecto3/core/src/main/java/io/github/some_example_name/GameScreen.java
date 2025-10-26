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

/**
 * Esta es la pantalla principal del juego.
 * Implementa 'Screen' y es controlada por 'Proyecto3Game'.
 */
public class GameScreen implements Screen {

    // --- Referencias de Juego ---
    private Proyecto3Game game;

    // --- Herramientas de Renderizado ---
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // --- Entidades del Mundo ---
    private Level level;
    private Player player;
    private List<Projectile> projectiles;
    private EnemyManager enemyManager;
    private CameraController camController;

    // --- UI ---
    private UIManager uiManager;
    private LevelUpUI levelUpUI; // La UI para subir de nivel

    // --- Estado del Juego ---
    private boolean isGameOver = false; // Flag para volver al menú
    private boolean debugHitboxes = false;
    private float spawnTimer;
    private float spawnInterval = 0.5f;



    /**
     * Constructor de la pantalla de juego.
     * Aquí se inicializa todo lo necesario para una partida.
     */
    public GameScreen(Proyecto3Game game) {
        this.game = game; // Guarda la referencia al gestor de pantallas

        // Reinicia el GameManager al empezar una nueva partida
        GameManager.getInstance().reset();

        // --- Inicialización de Herramientas ---
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // --- Inicialización del Mundo ---
        level = new Level();

        // --- Inicialización de Entidades ---
        player = new Player(new Vector2(Level.WORLD_WIDTH / 2f, Level.WORLD_HEIGHT / 2f));
        projectiles = new ArrayList<>();
        enemyManager = new EnemyManager((int)Level.WORLD_WIDTH, (int)Level.WORLD_HEIGHT);

        // --- Inicialización de Cámara y UI ---
        camController = new CameraController(800, 600, Level.WORLD_WIDTH, Level.WORLD_HEIGHT);
        uiManager = new UIManager();

        // Temporizador de spawn
        spawnTimer = 0f;

        // --- Configuración de la UI de Level Up ---

        // Qué hacer si se elige "Vida"
        Runnable onHealthSelected = () -> {
            player.applyHealthUpgrade();     // Aplica la mejora
            GameManager.getInstance().resumeGame(); // Reanuda el juego
            Gdx.input.setInputProcessor(null); // Devuelve el input al juego
        };

        // Qué hacer si se elige "Proyectil"
        Runnable onProjectileSelected = () -> {
            player.applyProjectileUpgrade();  // Aplica la mejora
            GameManager.getInstance().resumeGame(); // Reanuda el juego
            Gdx.input.setInputProcessor(null); // Devuelve el input al juego
        };

        // Instancia la UI pasándole las acciones
        levelUpUI = new LevelUpUI(onHealthSelected, onProjectileSelected);
    }

    /**
     * El bucle principal de renderizado.
     */
    @Override
    public void render(float delta) {

        // 1. --- LÓGICA DE JUEGO (UPDATES) ---
        //    Solo se ejecuta si el GameManager dice que el juego está "CORRIENDO"
        if (GameManager.getInstance().isRunning()) {

            // Lógica de Spawn
            spawnTimer += delta;
            if (spawnTimer >= spawnInterval) {
                SpawnEnemy();
                spawnTimer = 0f;
            }

            // Input de Debug
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                debugHitboxes = !debugHitboxes;
            }

            // Actualización de Entidades
            player.update(delta);
            camController.follow(player);
            handleInput(); // Disparos

            // Actualizar Proyectiles
            Iterator<Projectile> iter = projectiles.iterator();
            while (iter.hasNext()) {
                Projectile p = iter.next();
                p.update(delta);
                if (p.isOutOfScreen(Level.WORLD_WIDTH, Level.WORLD_HEIGHT)) {
                    p.dispose();
                    iter.remove();
                }
            }

            // Actualizar Enemigos
            enemyManager.update(delta, player.getPosition());

            // Comprobación de Colisiones
            handleProjectileCollisions();
            handlePlayerCollisions();

        } // --- Fin del bloque 'if (GameManager.getInstance().isRunning())' ---


        // 2. --- LÓGICA DE DIBUJADO (RENDER) ---
        //    Esto se ejecuta siempre, para mostrar el juego congelado si está pausado

        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar el fondo
        level.renderBackground(batch, camController.getCamera());

        // Dibujar el mundo (con cámara)
        batch.setProjectionMatrix(camController.getCamera().combined);
        batch.begin();
        player.render(batch);
        enemyManager.render(batch);
        for (Projectile p : projectiles) {
            p.render(batch);
        }
        batch.end();

        // Dibujar Hitboxes (opcional)
        if (debugHitboxes) {
            shapeRenderer.setProjectionMatrix(camController.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            Rectangle playerRect = player.getBounds();
            shapeRenderer.rect(playerRect.x, playerRect.y, playerRect.width, playerRect.height);

            for (Enemy e : enemyManager.getEnemies()) {
                Rectangle eRect = e.getBounds();
                shapeRenderer.rect(eRect.x, eRect.y, eRect.width, eRect.height);
            }
            for (Projectile p : projectiles) {
                Rectangle pRect = p.getBounds();
                shapeRenderer.rect(pRect.x, pRect.y, pRect.width, pRect.height);
            }
            shapeRenderer.end();
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
            player.shoot(projectiles, camController);
        }
    }

    /**
     * Gestiona colisiones entre proyectiles y enemigos.
     */
    private void handleProjectileCollisions() {
        Iterator<Projectile> projectileIter = projectiles.iterator();
        while (projectileIter.hasNext()) {
            Projectile p = projectileIter.next();
            Iterator<Enemy> enemyIter = enemyManager.getEnemies().iterator();
            while (enemyIter.hasNext()) {
                Enemy e = enemyIter.next();
                if (p.getBounds().overlaps(e.getBounds())) {

                    p.dispose();
                    projectileIter.remove();
                    enemyIter.remove(); // El enemigo muere

                    // Comprueba si el jugador sube de nivel
                    if (player.addExp(1)) {
                        // Pausa el juego y da el control a la UI de level up
                        GameManager.getInstance().pauseForLevelUp();
                        Gdx.input.setInputProcessor(levelUpUI.getStage());
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
                } else {
                    // Muerto: activa el Game Over
                    restartGame();
                }

                break; // Solo nos golpea un enemigo a la vez
            }
        }
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
        enemyManager.spawnEnemy();
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
        level.dispose();
        player.dispose();
        enemyManager.dispose();
        uiManager.dispose();
        levelUpUI.dispose();

        // Libera proyectiles restantes
        for (Projectile p : projectiles) {
            p.dispose();
        }
    }
}
