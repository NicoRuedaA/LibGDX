package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.Player.PlayerState;

public class PlayerAnimation {

    // --- AHORA SOLO 6 ANIMACIONES ---
    private Animation<TextureRegion> walkDown, walkLeft, walkUp;
    private Animation<TextureRegion> attackDown, attackLeft, attackUp;

    private Texture sheet;

    // --- DEFINICIONES DE COLUMNAS ACTUALIZADAS ---
    private final int[] WALK_COLS = {0, 1};
    private final int[] ATTACK_COLS = {2, 3};

    // --- FILAS (SIN ROW_RIGHT) ---
    private final int ROW_DOWN = 0;
    private final int ROW_LEFT = 1;
    private final int ROW_UP = 2;
    // private final int ROW_RIGHT = 3; // <-- Eliminada

    public PlayerAnimation(String filePath, int frameCols, int frameRows) {
        sheet = new Texture(Gdx.files.internal(filePath));
        TextureRegion[][] tmp = TextureRegion.split(sheet,
            sheet.getWidth() / frameCols,
            sheet.getHeight() / frameRows);

        // --- Cargar animaciones de caminar (en bucle) ---
        walkDown   = createAnimation(tmp, ROW_DOWN,  WALK_COLS, 0.2f, Animation.PlayMode.LOOP);
        walkLeft   = createAnimation(tmp, ROW_LEFT,  WALK_COLS, 0.2f, Animation.PlayMode.LOOP);
        walkUp     = createAnimation(tmp, ROW_UP,    WALK_COLS, 0.2f, Animation.PlayMode.LOOP);
        // walkRight  = ...; // <-- Eliminada

        // --- Cargar animaciones de ataque (NO en bucle) ---
        attackDown = createAnimation(tmp, ROW_DOWN,  ATTACK_COLS, 0.1f, Animation.PlayMode.NORMAL);
        attackLeft = createAnimation(tmp, ROW_LEFT,  ATTACK_COLS, 0.1f, Animation.PlayMode.NORMAL);
        attackUp   = createAnimation(tmp, ROW_UP,    ATTACK_COLS, 0.1f, Animation.PlayMode.NORMAL);
        // attackRight= ...; // <-- Eliminada
    }

    private Animation<TextureRegion> createAnimation(TextureRegion[][] tmp, int row, int[] cols, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>();
        for (int col : cols) {
            frames.add(tmp[row][col]);
        }
        return new Animation<>(frameDuration, frames, playMode);
    }

    /**
     * El método principal.
     * --- MODIFICADO: "RIGHT" ahora devuelve la animación "LEFT" ---
     */
    public TextureRegion getFrame(PlayerState state, String direction, float stateTime) {
        Animation<TextureRegion> anim = null;

        switch (state) {
            case ATTACKING:
                switch (direction) {
                    case "UP":    anim = attackUp;    break;
                    case "LEFT":  anim = attackLeft;  break;
                    case "RIGHT": anim = attackLeft;  break; // <-- CAMBIO
                    default:      anim = attackDown;  break;
                }
                break;

            case WALKING:
                switch (direction) {
                    case "UP":    anim = walkUp;    break;
                    case "LEFT":  anim = walkLeft;  break;
                    case "RIGHT": anim = walkLeft;  break; // <-- CAMBIO
                    default:      anim = walkDown;  break;
                }
                break;

            case IDLE:
            default:
                switch (direction) {
                    case "UP":    anim = walkUp;    break;
                    case "LEFT":  anim = walkLeft;  break;
                    case "RIGHT": anim = walkLeft;  break; // <-- CAMBIO
                    default:      anim = walkDown;  break;
                }
                return anim.getKeyFrame(0, false);
        }
        return anim.getKeyFrame(stateTime, (state == PlayerState.WALKING));
    }

    /**
     * Devuelve la duración total de la animación de ataque.
     */
    public float getAttackAnimationDuration() {
        // Usamos attackLeft (o cualquier otra) ya que asumimos que todas duran lo mismo
        return attackLeft.getAnimationDuration();
    }

    public void dispose() {
        sheet.dispose();
    }
}
