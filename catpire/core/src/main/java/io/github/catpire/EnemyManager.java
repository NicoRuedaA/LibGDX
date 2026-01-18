package io.github.catpire;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

public class EnemyManager {

    public enum EnemyType {
        GRUNT,  // El normal
        SCOUT,
        TYPE3,
        TYPE4// Uno más rápido pero débil
        // (Puedes añadir más aquí: TANK, SHOOTER, etc.)
    }

    EnemyType[] allTypes = EnemyType.values();
    private List<Enemy> enemies;
    private static final float MIN_SPAWN_DISTANCE = 300f;
    private Vector2 tempSpawnPosition = new Vector2();

    private int screenWidth, screenHeight;

    public EnemyManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        enemies = new ArrayList<>();
    }

    public void spawnEnemy(Vector2 playerPosition) {
        float x = 0;
        float y = 0;
        float distance = 0;
        do {
            // a. Elige una posición aleatoria (como antes)
            x = MathUtils.random(0, screenWidth);
            y = MathUtils.random(0, screenHeight);

            // b. Ponemos la posición en nuestro vector temporal
            tempSpawnPosition.set(x, y);

            // c. Calculamos la distancia desde el spawn al jugador
            //    .dst() es la abreviatura de "distance" (distancia)
            distance = tempSpawnPosition.dst(playerPosition);

            // d. Si la distancia es MENOR que la segura,
            //    el bucle se repite (vuelve a tirar).
        } while (distance < MIN_SPAWN_DISTANCE);


        EnemyType typeToSpawn;
        int newRandom = MathUtils.random(0, allTypes.length - 1);
        typeToSpawn = allTypes[newRandom];

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

    public void clearAll() {
        // Primero, liberamos la memoria de cada enemigo
        for (Enemy e : enemies) {
            e.dispose();
        }
        // Luego, vaciamos la lista
        enemies.clear();
    }

    public void dispose() {
        for (Enemy e : enemies) {
            e.dispose();
        }
    }
}
