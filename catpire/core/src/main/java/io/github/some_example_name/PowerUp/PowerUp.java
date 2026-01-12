package io.github.some_example_name.PowerUp;

import io.github.some_example_name.Player;
import io.github.some_example_name.InGame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PowerUp {

    protected Vector2 position;
    protected Texture texture;
    protected PowerUpEffect effect; // <-- Guarda la "receta" (la Estrategia)

    private float width = 32f;
    private float height = 32f;
    private float lifeTimer = 10f; // El powerup desaparece después de 10s

    public PowerUp(Vector2 position, Texture texture, PowerUpEffect effect) {
        this.position = position;
        this.texture = texture;
        this.effect = effect;
    }

    public void update(float delta) {
        // El powerup desaparece con el tiempo
        lifeTimer -= delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - width/2, position.y - height/2, width, height);
    }

    /**
     * Le dice al powerup que aplique su efecto (que está guardado).
     */
    public void applyEffect(InGame game, Player player) {

        // --- 2. MODIFICA ESTA LÍNEA (Tu Línea 39) ---
        // this.effect.apply(player);
        this.effect.apply(game, player);
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x - width/2, position.y - height/2, width, height);
    }

    public boolean isExpired() {
        return lifeTimer <= 0;
    }
}
