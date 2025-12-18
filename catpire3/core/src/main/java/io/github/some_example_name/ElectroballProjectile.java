package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class ElectroballProjectile {

    private Character target; // El jugador al que seguirá
    private Texture texture;

    private Vector2 position; // La posición de la bola
    private float width = 35f;
    private float height = 35f;

    // --- Lógica de Giro ---
    private float angle; // El ángulo actual en grados
    private float distance = 75f; // A qué distancia del jugador girará
    private float rotationSpeed = 200f; // Grados por segundo

    private int damage = 1;
    private float hitCooldown = 0.5f; // Solo puede golpear a un enemigo cada 0.5s
    private float hitTimer;

    public ElectroballProjectile(Character target) {
        this.target = target;
        this.position = new Vector2();
        this.angle = MathUtils.random(0, 360);

        this.hitTimer = 0f; // <-- Inicializa el temporizador

        try {
            texture = new Texture(Gdx.files.internal("electroball.png"));
        } catch (Exception e) {
            Gdx.app.error("ElectroballProjectile", "No se pudo cargar la textura");
        }
    }

    /**
     * Actualiza la posición de la bola para que gire alrededor del objetivo.
     */
    public void update(float delta) {
        // 1. Avanza el ángulo
        angle += rotationSpeed * delta;
        if (angle > 360) {
            angle -= 360;
        }

        // 2. Obtiene la posición del centro (el jugador)
        Vector2 targetPos = target.getPosition();

        // 3. Calcula la nueva posición X e Y usando trigonometría
        // Math.cos y Math.sin usan radianes, así que convertimos el ángulo
        position.x = targetPos.x + (distance * MathUtils.cosDeg(angle));
        position.y = targetPos.y + (distance * MathUtils.sinDeg(angle));

        hitTimer += delta;
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x - width / 2, position.y - height / 2, width, height);
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x - width / 2, position.y - height / 2, width, height);
    }

    public int getDamage() {
        return damage;
    }

    public boolean isReadyToHit() {
        return hitTimer >= hitCooldown;
    }

    public void resetHitTimer() {
        this.hitTimer = 0f;
    }

}
