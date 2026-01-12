package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class UIManager {

    private Texture heartTexture;
    private float heartSize = 32f;     // Tamaño del corazón en píxeles
    private float heartSpacing = 5f; // Espacio entre corazones
    private float margin = 10f;        // Margen desde la esquina de la pantalla
    private BitmapFont font;
    private float heartY = 0;


    public UIManager() {
        // Cargar la textura del corazón
        try {
            heartTexture = new Texture(Gdx.files.internal("heart.png")); // ¡Ojo! pusiste 'hearth', si es 'heart' cámbialo
        } catch (Exception e) {
            Gdx.app.error("UIManager", "No se pudo cargar 'hearth.png'", e);
        }
        font = new BitmapFont();
    }

    /**
     * Dibuja los corazones de vida en la esquina superior izquierda.
     * @param batch El SpriteBatch para dibujar.
     * @param currentHealth La vida actual del jugador.
     */
    public void render(SpriteBatch batch, int currentHealth, int currentExp, int expToLevelUp) {
        if (heartTexture == null) return; // No dibujar si la textura falló al cargar

        heartY = Gdx.graphics.getHeight() - heartSize - margin;

        // Dibuja un corazón por cada punto de vida
        for (int i = 0; i < currentHealth; i++) {

            // Calculamos la posición X para este corazón
            // i = 0 -> margin
            // i = 1 -> margin + heartSize + heartSpacing
            // i = 2 -> margin + 2 * (heartSize + heartSpacing)
            float x = margin + i * (heartSize + heartSpacing);

            // Calculamos la posición Y (fija)
            // Gdx.graphics.getHeight() nos da el alto de la VENTANA
            // Restamos para ir hacia abajo desde la esquina superior

            //float y = heartY;

            //batch.draw(heartTexture, x, y, heartSize, heartSize);
            batch.draw(heartTexture, x, heartY, heartSize, heartSize);
        }

        String expText = "EXP: " + currentExp + " / " + expToLevelUp;
        float textY = heartY - heartSpacing;

        if (font != null) {
            font.draw(batch, expText, margin, textY);
        }
    }

    /**
     * Libera la memoria de la textura.
     */
    public void dispose() {
        if (heartTexture != null) {
            heartTexture.dispose();
        }
        if (font != null) {
            font.dispose(); // <-- ¡Importante liberar la fuente!
        }
    }
}
