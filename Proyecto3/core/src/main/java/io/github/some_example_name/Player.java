package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;

public class Player {

    public Vector2 position;        // Posición del jugador (centro del sprite)
    private float speed = 300f;     // Velocidad en píxeles por segundo
    private float width = 35f;  // O el ancho real de tu sprite
    private float height = 50f;
    private float shootDelay = 0.75f; // 0.2 segundos (5 disparos por segundo)
    private float shootTimer;

    public PlayerAnimation animation;

    private int health;

    public Player(Vector2 startPos) {
        position = startPos;
        animation = new PlayerAnimation("player_spritesheet.png", 5, 4);
        this.shootTimer = 0f;
        this.health = 3;

    }

    public void update(float delta) {
        boolean moved = false;

        float minX = width / 2f;
        float maxX = Level.WORLD_WIDTH - (width / 2f);
        float minY = height / 2f;
        float maxY = Level.WORLD_HEIGHT - (height / 2f);

        position.x = MathUtils.clamp(position.x, minX, maxX);
        position.y = MathUtils.clamp(position.y, minY, maxY);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * delta;
            animation.setDirection("UP");
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * delta;
            animation.setDirection("DOWN");
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= speed * delta;
            animation.setDirection("LEFT");
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += speed * delta;
            animation.setDirection("RIGHT");
            moved = true;
        }

        // Solo actualizar animación si se está moviendo
        if (moved) {
            animation.update(delta);
        }

        shootTimer += delta;
    }

    public void render(SpriteBatch batch) {

        animation.render(batch, position.x, position.y);


    }

    public Rectangle getBounds() {
        // Centrado sobre el jugador
        /*float x = position.x - hitboxWidth / 2f;
        float y = position.y - hitboxHeight / 2f;
        return new Rectangle(x, y, hitboxWidth, hitboxHeight);*/
        return new Rectangle(position.x - width/2, position.y - height/2, width, height);
    }

    public void shoot(List<Projectile> projectiles, CameraController camera) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
// 2. ¡Añade esta comprobación!
            // ¿Ha pasado suficiente tiempo desde el último disparo?
            if (shootTimer >= shootDelay) {

                // --- (Este es tu código de disparo de antes) ---
                Vector3 mouseWorld = camera.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                Vector2 mousePos = new Vector2(mouseWorld.x, mouseWorld.y);
                Vector2 startPos = this.position.cpy();
                Vector2 direction = mousePos.sub(startPos).nor();

                projectiles.add(new Projectile(startPos, direction));
                // --- (Fin del código de disparo) ---

                // 3. ¡Resetea el temporizador!
                shootTimer = 0f;
            }
            // Si el 'shootTimer' es menor que 'shootDelay', no hará nada
            // y el jugador tendrá que esperar.
        }
    }

    public void dispose() {
        animation.dispose();
    }

    public Vector2 getPosition() {
        return position.cpy(); // devuelve una copia segura de la posición
    }

    public int getHealth() {
        return this.health;
    }

}
