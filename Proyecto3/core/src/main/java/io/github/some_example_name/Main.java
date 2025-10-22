package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Player player;

    private Texture projectileTexture;
    private List<Projectile> projectiles;

    private Texture enemyTexture;
    private EnemyManager enemyManager;

    @Override
    public void create() {

        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        player = new Player(new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));
        projectiles = new ArrayList<>();

        // Cargar textura de enemigo y crear EnemyManager
        enemyTexture = new Texture("enemy.png"); // Asegúrate de tener enemy.png en assets
        enemyManager = new EnemyManager(enemyTexture, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        projectileTexture = new Texture("projectile.png");
    }

    @Override
    public void render() {
        handleInput();
        float delta = Gdx.graphics.getDeltaTime();

        // Actualizar proyectiles
        Iterator<Projectile> iter = projectiles.iterator();
        while (iter.hasNext()) {
            Projectile p = iter.next();
            p.update(delta);
            if (p.isOutOfScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
                iter.remove();
            }
        }

        // Actualizar enemigos (hacia el jugador)
        enemyManager.update(delta, player.getPosition());

        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);






        // Dibujar enemigos con SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        enemyManager.render(batch);
        for (Projectile p : projectiles) {
            p.render(batch, projectileTexture);
        }
        batch.end();

        // ---------------- Colisiones ----------------
        Iterator<Projectile> projectileIter = projectiles.iterator();
        while (projectileIter.hasNext()) {
            Projectile p = projectileIter.next();
            Iterator<Enemy> enemyIter = enemyManager.getEnemies().iterator();

            while (enemyIter.hasNext()) {
                Enemy e = enemyIter.next();

                // Si el proyectil toca al enemigo, eliminamos ambos
                if (p.getBounds().overlaps(e.getBounds())) {
                    projectileIter.remove();
                    enemyIter.remove();
                    break; // un proyectil solo destruye un enemigo
                }
            }
        }

// Colisión enemigo - jugador
        for (Enemy e : enemyManager.getEnemies()) {
            Rectangle playerRect = new Rectangle(player.getPosition().x - 16, player.getPosition().y - 16, 32, 32);
            if (e.getBounds().overlaps(playerRect)) {
                // Reiniciar juego
                restartGame();
                break;
            }
        }
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta);

        // Disparar proyectil
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            projectiles.add(new Projectile(new Vector2(player.getPosition()), mousePos));
        }
    }

    private void restartGame() {
        // Reiniciamos jugador
        player = new Player(new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));

        // Limpiamos proyectiles y enemigos
        projectiles.clear();
        enemyManager.getEnemies().clear();
    }

    @Override
    public void dispose() {
        batch.dispose();
        enemyTexture.dispose();
    }
}
