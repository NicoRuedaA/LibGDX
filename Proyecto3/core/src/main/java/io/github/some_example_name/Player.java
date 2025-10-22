package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;

public class Player {
    public Vector2 position;
    private float speed = 300f; // píxeles por segundo
    private float size = 50f;
    private Texture texture;

    public Player(Vector2 startPos) {
        position = startPos;
        texture = new Texture("player.png");
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += speed * delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 50, position.y - 50, 100, 100);
    }

    public Vector2 getPosition() {
        return position.cpy(); // devolvemos copia para evitar que se modifique externamente
    }

    public void dispose() {
        texture.dispose();
    }
}

