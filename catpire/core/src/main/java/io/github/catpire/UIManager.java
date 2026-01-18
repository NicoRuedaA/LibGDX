package io.github.catpire;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class UIManager {

    private Texture heartTexture;
    private float heartSize = 32f;
    private float heartSpacing = 5f;
    private float margin = 15f;        // Un poco más de margen para que no toque el borde
    private BitmapFont font;

    public UIManager() {
        try {
            // Asegúrate de que el nombre del archivo sea exacto (case-sensitive en Web)
            heartTexture = new Texture(Gdx.files.internal("heart.png"));
        } catch (Exception e) {
            Gdx.app.error("UIManager", "No se pudo cargar 'heart.png'. Revisa el nombre del archivo.", e);
        }
        font = new BitmapFont();
        // Opcional: Hacer la fuente un poco más grande si se ve pequeña en 800x450
        font.getData().setScale(1.2f);
    }

    /**
     * Dibuja la UI usando coordenadas virtuales (0-800, 0-450).
     */
    public void render(SpriteBatch batch, int currentHealth, int currentExp, int expToLevelUp) {
        if (heartTexture == null) return;

        // --- CAMBIO CLAVE ---
        // Usamos InGame.VIRTUAL_HEIGHT en lugar de Gdx.graphics.getHeight()
        // Esto garantiza que la UI siempre esté arriba aunque la ventana sea gigante.
        float topY = InGame.VIRTUAL_HEIGHT - margin;
        float heartY = topY - heartSize;

        // Dibuja los corazones
        for (int i = 0; i < currentHealth; i++) {
            float x = margin + i * (heartSize + heartSpacing);
            batch.draw(heartTexture, x, heartY, heartSize, heartSize);
        }

        // Dibuja el texto de experiencia
        String expText = "EXP: " + currentExp + " / " + expToLevelUp;
        // Posicionamos el texto justo debajo de los corazones
        float textY = heartY - 10f;

        if (font != null) {
            font.draw(batch, expText, margin, textY);
        }
    }

    public void dispose() {
        if (heartTexture != null) heartTexture.dispose();
        if (font != null) font.dispose();
    }
}
