package io.github.catpire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array; // Importante: el Array de LibGDX

public class FireProjectile_Animation {

    // Almacenamos las dos texturas para poder liberarlas (dispose)
    private Texture frame1;
    private Texture frame2;

    private Animation<TextureRegion> animation;
    private float stateTime;

    // El constructor ahora está vacío, hace todo el trabajo internamente
    public FireProjectile_Animation() {

        // 1. Cargar las dos texturas por separado
        frame1 = new Texture(Gdx.files.internal("fireball1.png"));
        frame2 = new Texture(Gdx.files.internal("fireball2.png"));

        // 2. Crear un array de TextureRegions
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(frame1)); // Frame 1
        frames.add(new TextureRegion(frame2)); // Frame 2

        // 3. Crear la Animación
        // 0.1f es la velocidad (10 frames por segundo). Cámbialo si quieres.
        animation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        stateTime = 0f;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    // El renderizado centrado sigue siendo igual
    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
    }

    // ¡IMPORTANTE! Debemos liberar AMBAS texturas
    public void dispose() {
        frame1.dispose();
        frame2.dispose();
    }
}
