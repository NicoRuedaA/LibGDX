package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

public class EnemyManager {

    protected static final int NUMBER_OF_DIFFERENT_ENEMIES = 4;

    public enum EnemyType {
        GRUNT,  // El normal
        SCOUT,
        TYPE3,
        TYPE4// Uno más rápido pero débil
        // (Puedes añadir más aquí: TANK, SHOOTER, etc.)
    }

    private List<Enemy> enemies;
    private int screenWidth, screenHeight;

    public EnemyManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        enemies = new ArrayList<>();
    }

    public void spawnEnemy() {
        // 1. Elige una posición aleatoria (esto ya lo tenías)
        float x = MathUtils.random(0, screenWidth);
        float y = MathUtils.random(0, screenHeight);

        // --- 2. ¡NUEVA LÓGICA! Elige un tipo de enemigo al azar ---
        EnemyType typeToSpawn;

        int newRandom = MathUtils.random(0, 3);

        switch (newRandom) {
            case 0:
            default:
                typeToSpawn = EnemyType.GRUNT;
                break;
            case 1:
                typeToSpawn = EnemyType.SCOUT;
                break;
            case 2:
                typeToSpawn = EnemyType.TYPE3;
                break;
            case 3:
                typeToSpawn = EnemyType.TYPE4;
                break;

        }

        // --- FIN DE LA LÓGICA DE SPAWN ---

        // 3. Llama al nuevo constructor de Enemigo
        enemies.add(new Enemy(x, y, typeToSpawn));
    }

    public void update(float delta, Vector2 playerPosition) {
        for (Enemy e : enemies) {
            // 1. Llama al update() base (para la animación)
            e.update(delta);

            // 2. Llama al update() de IA (para el movimiento)
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
