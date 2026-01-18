package io.github.catpire;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FireProjectile extends Projectile {

    private Vector2 velocity;
    private FireProjectile_Animation animation; // Usa la clase renombrada
    private float speed = 500f;

    public FireProjectile(Vector2 startPos, Vector2 direction) {
        // 1. Llama al constructor base
        super(startPos);

        // 2. Configura las variables de esta clase
        this.velocity = direction.cpy().scl(speed);
        this.animation = new FireProjectile_Animation(); // Asumo que este es tu constructor

        // 3. Configura las variables heredadas
        this.width = 30f;
        this.height = 30f;
    }

    /**
     * El update del FireProjectile ignora al 'player'.
     * Simplemente se mueve en línea recta.
     */
    @Override
    public void update(float delta, Player player) {
        position.add(velocity.cpy().scl(delta));
        animation.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        animation.render(batch, position.x, position.y, width, height);
    }

    @Override
    public void dispose() {
        animation.dispose();
    }
}
