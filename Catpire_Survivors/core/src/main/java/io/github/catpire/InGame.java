package io.github.catpire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.catpire.PowerUp.*;
import com.badlogic.gdx.audio.Music;

public class InGame implements Screen {

    private Music bgMusic;

    private Main game;
    private PowerUpManager powerupManager;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Background background;
    private Player player;
    private List<FireProjectile> fireProjectiles;
    private EnemyManager enemyManager;
    private CameraController camController;
    private InputManager inputManager;

    private UIManager uiManager;
    private LevelUpUI levelUpUI;

    private boolean isGameOver = false;
    private boolean debugHitboxes = false;
    private float spawnTimer;
    private float spawnInterval = 0.5f;

    private List<ElectroballProjectile> activeElectroballs;

    // --- NUEVAS CONSTANTES PARA 16:9 ---
    public static final float VIRTUAL_WIDTH = 800f;
    public static final float VIRTUAL_HEIGHT = 450f;

    public InGame(Main game) {
        this.game = game;
        GameManager.getInstance().reset();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        background = new Background();

        inputManager = new InputManager();

        // El CameraController ahora maneja el FitViewport de 16:9
        camController = new CameraController(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Background.WORLD_WIDTH, Background.WORLD_HEIGHT);

        player = new Player(new Vector2(Background.WORLD_WIDTH / 2f, Background.WORLD_HEIGHT / 2f), inputManager);
        fireProjectiles = new ArrayList<>();
        enemyManager = new EnemyManager((int) Background.WORLD_WIDTH, (int) Background.WORLD_HEIGHT);
        powerupManager = new PowerUpManager();
        uiManager = new UIManager();

        spawnTimer = 0f;
        activeElectroballs = new ArrayList<>();

        // Cargar el archivo desde assets/music/bso.mp3
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/bso.mp3"));

        // Configuración inicial
        bgMusic.setLooping(true); // Para que se repita infinitamente
        bgMusic.setVolume(0.5f);  // Volumen al 50%
    }

    @Override
    public void render(float delta) {
        // 1. --- LÓGICA ---
        if (GameManager.getInstance().isRunning()) {
            updateGameLogic(delta);
        }

        // 2. --- RENDERIZADO ---

        // Limpiar con negro (será el color de las barras negras)
        ScreenUtils.clear(0, 0, 0, 1);

        // ¡IMPORTANTE! Aplicar el viewport antes de dibujar el mundo
        camController.getViewport().apply();
        batch.setProjectionMatrix(camController.getCamera().combined);

        // A. Dibujar Fondo (Dentro del sistema de cámara)
        background.renderBackground(batch, camController.getCamera());

        // B. Dibujar Entidades
        batch.begin();
        player.render(batch);
        enemyManager.render(batch);
        for (ElectroballProjectile ball : activeElectroballs) ball.render(batch);
        for (FireProjectile p : fireProjectiles) p.render(batch);
        batch.end();

        // C. Dibujar Hitboxes (Usa la misma cámara)
        if (debugHitboxes) {
            DebugHitboxes();
        }

        // D. Dibujar UI (Independiente del movimiento de cámara, pero dentro del viewport)
        // Usamos la matriz de proyección del viewport para que la UI no se salga a las barras negras
        batch.setProjectionMatrix(camController.getViewport().getCamera().combined);
        // Opcional: Si tu UIManager usa coordenadas de pantalla puras (0 a 800),
        // puedes usar una matriz ortográfica fija aquí:
        batch.getProjectionMatrix().setToOrtho2D(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        batch.begin();
        uiManager.render(batch, player.getHealth(), player.getCurrentExp(), player.getExpToLevelUp());
        batch.end();

        // E. UI de Level Up
        if (GameManager.getInstance().isPausedForLevelUp()) {
            levelUpUI.render(delta);
        }

        if (isGameOver) {
            Gdx.input.setInputProcessor(null);
            game.setScreen(new MainMenu(game));
        }
    }

    private void updateGameLogic(float delta) {
        inputManager.update();

        if (inputManager.didPressLevelUpDebug()) {
            showLevelUpMenu();
        }

        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            SpawnEnemy();
            spawnTimer = 0f;
        }

        for (ElectroballProjectile ball : activeElectroballs) ball.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) debugHitboxes = !debugHitboxes;

        player.update(delta);
        camController.follow(player);
        handleInput();

        updateProjectiles(delta);
        enemyManager.update(delta, player.getPosition());
        powerupManager.update(delta);

        handleProjectileCollisions();
        handlePlayerCollisions();
        handleElectroballCollisions();
    }

    private void updateProjectiles(float delta) {
        Iterator<FireProjectile> iter = fireProjectiles.iterator();
        while (iter.hasNext()) {
            FireProjectile p = iter.next();
            p.update(delta, player);
            if (p.isOutOfScreen(Background.WORLD_WIDTH, Background.WORLD_HEIGHT)) {
                p.dispose();
                iter.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Actualiza el Viewport de la cámara y de la UI
        camController.resize(width, height);
        if (levelUpUI != null) {
            levelUpUI.getStage().getViewport().update(width, height, true);
        }
    }

    private void showLevelUpMenu() {
        if (levelUpUI != null) levelUpUI.dispose();
        List<LevelUpUI.PowerUpOption> options = new ArrayList<>();
        List<PowerUpEffect> effectsToShow = PowerUpRegistry.getRandomPowerups(3);

        for (PowerUpEffect effect : effectsToShow) {
            Runnable action = () -> {
                effect.apply(this, player);
                resumeGameFromLevelUp();
            };
            options.add(new LevelUpUI.PowerUpOption(effect, action));
        }

        levelUpUI = new LevelUpUI(options);
        // IMPORTANTE: Actualizar el viewport de la nueva UI al crearla
        levelUpUI.getStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        GameManager.getInstance().pauseForLevelUp();
        Gdx.input.setInputProcessor(levelUpUI.getStage());
    }

    private void handleInput() {
        if (GameManager.getInstance().isRunning()) {
            player.updateShooting(inputManager.isShooting(), fireProjectiles, camController);
        }
    }

    // ... (Tus otros métodos handleCollisions, SpawnEnemy, etc. se mantienen igual) ...

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
                    e.takeDamage(1);
                    if (!e.isAlive()) {
                        enemyIter.remove();
                        if (player.addExp(1)) showLevelUpMenu();
                    }
                    break;
                }
            }
        }
    }

    private void handlePlayerCollisions() {
        Rectangle playerRect = player.getBounds();
        for (Enemy e : enemyManager.getEnemies()) {
            if (playerRect.overlaps(e.getBounds())) {
                player.takeDamage(1);
                if (player.isAlive()) {
                    player.resetPosition();
                    enemyManager.clearAll();
                    powerupManager.clearAll();
                    for(ElectroballProjectile ball : activeElectroballs) ball.dispose();
                    activeElectroballs.clear();
                } else {
                    restartGame();
                }
                break;
            }
        }
    }

    private void handleElectroballCollisions() {
        for (ElectroballProjectile ball : activeElectroballs) {
            if (!ball.isReadyToHit()) continue;
            Rectangle ballRect = ball.getBounds();
            Iterator<Enemy> enemyIter = enemyManager.getEnemies().iterator();
            while (enemyIter.hasNext()) {
                Enemy e = enemyIter.next();
                if (ballRect.overlaps(e.getBounds())) {
                    ball.resetHitTimer();
                    e.takeDamage(ball.getDamage());
                    if (!e.isAlive()) {
                        enemyIter.remove();
                        if (player.addExp(1)) showLevelUpMenu();
                    }
                }
            }
        }
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
        shapeRenderer.rect(player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);
        for (Enemy e : enemyManager.getEnemies()) shapeRenderer.rect(e.getBounds().x, e.getBounds().y, e.getBounds().width, e.getBounds().height);
        for (FireProjectile p : fireProjectiles) shapeRenderer.rect(p.getBounds().x, p.getBounds().y, p.getBounds().width, p.getBounds().height);
        for (ElectroballProjectile ball : activeElectroballs) shapeRenderer.rect(ball.getBounds().x, ball.getBounds().y, ball.getBounds().width, ball.getBounds().height);
        shapeRenderer.end();
    }

    private void restartGame() {
        if (bgMusic != null) {
            bgMusic.stop();
        }
        this.isGameOver = true; }

    public void SpawnEnemy(){ enemyManager.spawnEnemy(player.getPosition()); }

    @Override
    public void show() {
        // Se ejecuta al entrar a la pantalla InGame
        if (bgMusic != null) {
            bgMusic.play();
        }
    }
    @Override public void pause() {}
    @Override public void resume() {}
 public void hide() {  }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
        player.dispose();
        enemyManager.dispose();
        uiManager.dispose();
        if (levelUpUI != null) levelUpUI.dispose();
        powerupManager.dispose();
        for (FireProjectile p : fireProjectiles) p.dispose();
        for(ElectroballProjectile ball : activeElectroballs) ball.dispose();
        if (bgMusic != null) {
            bgMusic.dispose(); // Libera la memoria (Muy importante)
        }
    }
}
