package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile {

    // --- Propiedades ---
    private Vector2 position;
    private Vector2 velocity;

    // --- Animación y Colisión ---
    private ProjectileAnimation animation; // Se encarga de la parte visual
    private float width = 15f;  // Ancho para la hitbox y el sprite
    private float height = 15f; // Alto para la hitbox y el sprite

    // Velocidad de la bala
    private float speed = 500f;

    /**
     * Constructor para un nuevo proyectil.
     * @param spawnPos Posición (central) donde se crea la bala.
     * @param direction Vector de dirección (normalizado) hacia donde se mueve.
     */
    public Projectile(Vector2 spawnPos, Vector2 direction) {
        // Hacemos copias (.cpy()) para evitar modificar los vectores originales
        this.position = spawnPos.cpy();
        this.velocity = direction.cpy().scl(speed); // Multiplica la dirección por la velocidad

        // Crea la animación (que cargará "fireball1.png" y "fireball2.png")
        animation = new ProjectileAnimation();
    }

    /**
     * Actualiza la posición y la animación del proyectil.
     */
    public void update(float delta) {
        // Mueve la posición basado en la velocidad y el tiempo delta
        position.add(velocity.cpy().scl(delta));

        // Avanza el fotograma de la animación
        animation.update(delta);
    }

    /**
     * Dibuja el proyectil en la pantalla.
     * La lógica de centrado la maneja la clase ProjectileAnimation.
     */
    public void render(SpriteBatch batch) {
        animation.render(batch, position.x, position.y, 30, 30);
    }

    /**
     * Devuelve el rectángulo de colisión (hitbox) centrado.
     */
    public Rectangle getBounds() {
        // Calcula la esquina inferior izquierda desde el centro (position)
        return new Rectangle(position.x - width/2, position.y - height/2, width, height);
    }

    /**
     * Comprueba si el proyectil ha salido de los límites del mundo.
     */
    public boolean isOutOfScreen(float worldWidth, float worldHeight) {
        return position.x < 0 || position.x > worldWidth ||
            position.y < 0 || position.y > worldHeight;
    }

    /**
     * Libera la memoria de las texturas usadas por la animación.
     * Es CRÍTICO llamar a esto cuando la bala se destruye.
     */
    public void dispose() {
        animation.dispose();
    }
}
