package io.github.catpire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {

    private Texture background;
    public static final float WORLD_WIDTH = 2000f;
    public static final float WORLD_HEIGHT = 2000f;

    public Background() {
        // Cargar textura de fondo y repetir
        background = new Texture("background.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);


    }

    /**
     * Renderiza fondo tileado que se mueve según la cámara
     */
    public void renderBackground(SpriteBatch batch, OrthographicCamera camera) {
        // 1. Protección: Si la textura no ha cargado, no dibujes nada
        if (background.getWidth() <= 1) return;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 2. USA EL VIEWPORT DE LA CÁMARA, NO Gdx.graphics
        // Esto asegura que el fondo escale igual que el resto del juego
        float width = camera.viewportWidth;
        float height = camera.viewportHeight;

        float offsetX = camera.position.x - width / 2;
        float offsetY = camera.position.y - height / 2;

        float u = offsetX / background.getWidth();
        float v = offsetY / background.getHeight();

        // Usamos el ancho del viewport para calcular cuánto debe repetirse
        float u2 = u + width / background.getWidth();
        float v2 = v + height / background.getHeight();

        batch.draw(
            background,
            offsetX, offsetY,
            width, height,
            u, v, u2, v2
        );

        batch.end();
    }

    public void dispose() {
        background.dispose();
    }
}
