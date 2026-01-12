package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Clase base ABSTRACTA para todos los proyectiles.
 * Define las propiedades comunes (posición, hitbox) y los métodos
 * que todos los proyectiles DEBEN implementar (update, render, dispose).
 */
public abstract class Projectile {

    protected Vector2 position;
    protected float width;
    protected float height;

    public Projectile(Vector2 startPos) {
        this.position = startPos.cpy();
    }

    // --- Métodos Comunes (Concretos) ---

    public Rectangle getBounds() {
        return new Rectangle(position.x - width / 2, position.y - height / 2, width, height);
    }

    public boolean isOutOfScreen(float worldWidth, float worldHeight) {
        return position.x < 0 || position.x > worldWidth ||
            position.y < 0 || position.y > worldHeight;
    }

    public Vector2 getPosition() {
        return position.cpy();
    }

    // --- Métodos Abstractos (Obligatorios para las clases hijas) ---

    /**
     * Actualiza la lógica del proyectil (movimiento, etc.).
     * Pasamos 'player' por si el proyectil necesita saber dónde está (como la Electroball).
     */
    public abstract void update(float delta, Player player);

    public abstract void render(SpriteBatch batch);

    public abstract void dispose();
}
