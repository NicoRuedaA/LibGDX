package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import java.util.List;

public class EnemyManager {

    private List<Enemy> enemies;
    private int screenWidth, screenHeight;

    public EnemyManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        enemies = new ArrayList<>();
    }

    public void spawnEnemy() {
        float x = MathUtils.random(0, screenWidth);
        float y = MathUtils.random(0, screenHeight);
        float speed = MathUtils.random(50, 150);
        enemies.add(new Enemy(x, y, speed));
    }

    public void update(float delta, Vector2 playerPosition) {
        for (Enemy e : enemies) {
            e.update(delta, playerPosition);
        }
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        for (Enemy e : enemies) {
            e.render(batch);
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void dispose() {
        for (Enemy e : enemies) {
            e.dispose();
        }
    }

    public void clearAll() {
        // Primero, liberamos la memoria de cada enemigo
        for (Enemy e : enemies) {
            e.dispose();
        }
        // Luego, vaciamos la lista
        enemies.clear();
    }
}
