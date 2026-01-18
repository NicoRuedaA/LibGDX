package io.github.catpire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * Gestiona la entrada del teclado para el movimiento del jugador.
 * Lee las teclas una vez por fotograma y almacena el resultado.
 */
public class InputManager {

    private Vector2 movementDirection;
    private boolean moving, shooting;

    private String animationDirection;

    private boolean levelUpDebugPressed;

    public InputManager() {
        this.movementDirection = new Vector2(0, 0);
        this.moving = false;
        this.animationDirection = null;
        this.shooting = false;
        this.levelUpDebugPressed = false;
    }

    /**
     * Este método debe ser llamado una vez por fotograma (desde GameScreen).
     * Lee las teclas y actualiza las variables internas.
     */
    public void update() {
        // Resetea el estado al inicio de cada fotograma
        movementDirection.set(0, 0);
        moving = false;
        animationDirection = null;
        shooting = false;

        if (GameManager.getInstance().isRunning()) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                movementDirection.y = 1;
                animationDirection = "UP";
                moving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                movementDirection.y = -1;
                animationDirection = "DOWN";
                moving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                movementDirection.x = -1;
                animationDirection = "LEFT";
                moving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                movementDirection.x = 1;
                animationDirection = "RIGHT";
                moving = true;
            }
            shooting = (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT));
            levelUpDebugPressed = Gdx.input.isKeyJustPressed(Input.Keys.O);
        }
    }

    // --- Getters ---
    public boolean hasMoved() {
        return moving;
    }

    public Vector2 getMovementDirection() {
        return movementDirection;
    }

    public String getAnimationDirection() {
        return animationDirection;
    }

    public boolean isShooting() {
        return shooting;
    }

    public boolean didPressLevelUpDebug() {
        return levelUpDebugPressed;
    }
}
