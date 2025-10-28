package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Clase base abstracta para todas las entidades "vivas" del juego.
 * Contiene la lógica común de posición, salud y colisiones.
 */
public abstract class Character {
    // 'protected' significa que esta clase y sus hijos (Player, Enemy) pueden verlas.
    protected Vector2 position;
    protected Vector2 startPosition; // Posición de reaparición
    protected float speed;
    protected int health;
    protected float width;
    protected float height;

    //constructor
    public Character(Vector2 startPos) {
        // Hacemos copias (.cpy()) para que el vector original no se modifique
        this.position = startPos.cpy();
        this.startPosition = startPos.cpy();
    }

    /**
     * Devuelve la Hitbox (rectángulo de colisión) centrada.
     */
    public Rectangle getBounds() {
        return new Rectangle(position.x - width / 2, position.y - height / 2, width, height);
    }

    /**
     * Aplica una cantidad de daño al personaje.
     */
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    /**
     * Comprueba si el personaje sigue vivo.
     */
    public boolean isAlive() {
        return this.health > 0;
    }

    /**
     * Devuelve al personaje a su posición inicial.
     */
    public void resetPosition() {
        // 'set' copia los valores de startPosition en position
        this.position.set(startPosition);
    }

    /**
     * Devuelve una copia segura de la posición actual.
     */
    public Vector2 getPosition() {
        return position.cpy();
    }

    // --- Métodos Comunes (Abstractos) ---
    // Métodos que Player y Enemy DEBEN implementar, pero lo hacen de forma diferente.

    /**
     * Actualiza la lógica del personaje (movimiento, IA, etc.).
     * @param delta Tiempo desde el último fotograma.
     */
    public abstract void update(float delta);

    /**
     * Dibuja el personaje en la pantalla.
     * @param batch El SpriteBatch para dibujar.
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Libera los recursos (como texturas de animación).
     */
    public abstract void dispose();
}
