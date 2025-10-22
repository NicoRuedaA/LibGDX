package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 500f;


    public Projectile(Vector2 startPos, Vector2 targetPos) {
        this.position = new Vector2(startPos);
        Vector2 direction = targetPos.cpy().sub(startPos).nor();
        this.velocity = direction.scl(speed);
    }

    public void update(float delta) {
        position.add(velocity.cpy().scl(delta));
    }

    public void render(SpriteBatch batch, Texture texture) {
        batch.draw(texture, position.x - 25, position.y - 25, 50, 50 );
    }


    public boolean isOutOfScreen(float width, float height) {
        return position.x < 0 || position.x > width || position.y < 0 || position.y > height;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x - 4, position.y - 4, 50, 50);
    }
}
