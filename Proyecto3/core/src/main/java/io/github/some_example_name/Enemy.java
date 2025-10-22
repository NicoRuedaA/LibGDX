package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Enemy {
    private Vector2 position;
    private float speed;
    private Texture texture;

    public Enemy(float x, float y, float speed, Texture texture) {
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.texture = texture;
    }

    public void update(float deltaTime, Vector2 playerPosition) {
        // Vector dirección hacia el jugador
        Vector2 direction = playerPosition.cpy().sub(position).nor();
        // Mover enemigo hacia jugador
        position.add(direction.scl(speed * deltaTime));
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, 100, 100);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, 100, 100);
    }
}
