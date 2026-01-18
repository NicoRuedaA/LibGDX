package io.github.catpire.PowerUp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//gestor de colisiones con mejoras en el suelo
public class PowerUpManager {

    // Lista de powerups activos en el mapa
    private List<PowerUp> activePowerups;

    // Almacenamos las texturas aquí para no cargarlas mil veces
    private Texture heartTexture;
    private Texture projectileTexture;

    private float spawnChance = 0.25f; // 25% de probabilidad de que un enemigo suelte un powerup

    public PowerUpManager() {
        activePowerups = new ArrayList<>();

        // Carga las texturas que usarán los powerups
        try {
            heartTexture = new Texture(Gdx.files.internal("heart.png")); // (o heart.png)
            projectileTexture = new Texture(Gdx.files.internal("fireball1.png")); // (o la que uses)
        } catch (Exception e) {
            Gdx.app.error("PowerupManager", "No se pudieron cargar las texturas de powerups");
        }
    }

    /**
     * Dibuja todos los powerups activos.
     */
    public void render(SpriteBatch batch) {
        for (PowerUp p : activePowerups) {
            p.render(batch);
        }
    }

    /**
     * Actualiza el temporizador de vida de los powerups.
     */
    public void update(float delta) {
        Iterator<PowerUp> iter = activePowerups.iterator();
        while (iter.hasNext()) {
            PowerUp p = iter.next();
            p.update(delta);
            if (p.isExpired()) {
                iter.remove();
            }
        }
    }

    /**
     * Este método es llamado por GameScreen cuando un enemigo muere.
     * Decide SI soltar un powerup y CUÁL.
     * @param position La posición donde murió el enemigo.
     */
    public void trySpawnPowerup(Vector2 position) {
        // 1. ¿Tenemos suerte? (25% de probabilidad)
        if (MathUtils.random() > spawnChance) {
            return; // No, no hay suerte
        }

        // 2. Sí, hay suerte. ¿Qué powerup soltamos?
        Texture textureToUse;
        PowerUpEffect effectToUse;

        if (MathUtils.randomBoolean()) { // 50/50
            // --- Powerup de Vida ---
            textureToUse = heartTexture;
            effectToUse = new PowerUp_MoreHealth();
        } else {
            // --- Powerup de Proyectil ---
            textureToUse = projectileTexture;
            effectToUse = new PowerUp_MoreProjectiles();
        }

        // 3. Crea y añade el powerup a la lista
        activePowerups.add(new PowerUp(position, textureToUse, effectToUse));
    }

    public List<PowerUp> getActivePowerups() {
        return activePowerups;
    }

    public void dispose() {
        if (heartTexture != null) {
            heartTexture.dispose();
        }
        if (projectileTexture != null) {
            projectileTexture.dispose();
        }
        activePowerups.clear();
    }

    public void clearAll() {
        activePowerups.clear();
    }
}
