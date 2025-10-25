package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private Vector2 position;
    private float speed;
    public EnemyAnimation animation;

    private float size = 100f;

    private float width = 35f;   // <- AQUÍ (Ancho de la hitbox)
    private float height = 50f;  // <- Y AQUÍ (Alto de la hitbox)

    public Enemy(float x, float y, float speed) {
        this.position = new Vector2(x, y);
        this.speed = speed;
        animation = new EnemyAnimation("enemy_spritesheet.png", 5, 4);
    }

    public void update(float deltaTime, Vector2 playerPosition) {
        Vector2 direction = playerPosition.cpy().sub(position).nor();
        position.add(direction.scl(speed * deltaTime));

        animation.update(deltaTime);
    }

    public void render(SpriteBatch batch) {
        // ANTES (Centrado incorrecto)
        // animation.render(batch, position.x - size/2, position.y - size/2, size, size);

        // DESPUÉS (Correcto: Pasa el centro directamente)
        animation.render(batch, position.x, position.y, size, size);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x - width/2, position.y - height/2, width, height);
    }

    public void dispose() {
        animation.dispose();
    }
}
