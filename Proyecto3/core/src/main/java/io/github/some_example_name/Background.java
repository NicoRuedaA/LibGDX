package io.github.some_example_name;

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
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        // Offset en UV según la posición de la cámara
        float offsetX = camera.position.x - camera.viewportWidth / 2;
        float offsetY = camera.position.y - camera.viewportHeight / 2;

        // UVs de repetición
        float u = offsetX / background.getWidth();
        float v = offsetY / background.getHeight();
        float u2 = u + (float) screenWidth / background.getWidth();
        float v2 = v + (float) screenHeight / background.getHeight();

        batch.draw(
            background,
            offsetX, offsetY,         // posición inferior izquierda en mundo
            screenWidth, screenHeight, // tamaño visible
            u, v, u2, v2              // UVs con offset → se repite
        );

        batch.end();
    }

    public void dispose() {
        background.dispose();
    }
}
