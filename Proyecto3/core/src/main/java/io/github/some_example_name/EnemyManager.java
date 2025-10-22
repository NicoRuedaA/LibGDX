package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Random;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private float spawnTimer;
    private float spawnInterval = 1.5f; // 3 segundos
    private Texture enemyTexture;
    private Random random;

    private float screenWidth;
    private float screenHeight;

    public EnemyManager(Texture texture, float screenWidth, float screenHeight) {
        enemies = new ArrayList<>();
        spawnTimer = 0;
        enemyTexture = texture;
        random = new Random();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(float deltaTime, Vector2 playerPosition) {
        // Actualizar timer y crear enemigo si corresponde
        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            spawnEnemy();
        }

        // Actualizar enemigos
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, playerPosition);
        }
    }

    public void render(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }

    private void spawnEnemy() {
        float x = 0, y = 0;

        // Elegir una esquina aleatoria
        int corner = random.nextInt(4);
        switch (corner) {
            case 0: x = 0; y = 0; break; // esquina inferior izquierda
            case 1: x = screenWidth - enemyTexture.getWidth(); y = 0; break; // esquina inferior derecha
            case 2: x = 0; y = screenHeight - enemyTexture.getHeight(); break; // esquina superior izquierda
            case 3: x = screenWidth - enemyTexture.getWidth(); y = screenHeight - enemyTexture.getHeight(); break; // esquina superior derecha
        }

        enemies.add(new Enemy(x, y, 100f, enemyTexture)); // velocidad 100
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}
