package io.github.catpire;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraController {
    private OrthographicCamera camera;
    private Viewport viewport;
    private float worldWidth, worldHeight;

    /**
     * @param virtualWidth Ancho virtual (ej: 800)
     * @param virtualHeight Alto virtual (ej: 450 para 16:9)
     * @param worldWidth Límite total del mapa (ej: 2000)
     * @param worldHeight Límite total del mapa (ej: 2000)
     */
    public CameraController(float virtualWidth, float virtualHeight, float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        // 1. Inicializamos la cámara
        camera = new OrthographicCamera();

        // 2. Inicializamos el Viewport.
        // FitViewport añadirá barras negras si la ventana no es de la proporción exacta.
        viewport = new com.badlogic.gdx.utils.viewport.ExtendViewport(virtualWidth, virtualHeight, camera);

        viewport.apply();
        camera.update();
    }

    public void follow(Player player) {
        // Centramos la cámara en el jugador
        camera.position.set(player.position.x, player.position.y, 0);

        // Limitar la cámara para que no se salga de los bordes del mapa (WORLD_WIDTH/HEIGHT)
        // Usamos getWorldWidth() del viewport porque representa nuestras unidades de juego
        float halfW = viewport.getWorldWidth() / 2f;
        float halfH = viewport.getWorldHeight() / 2f;

        camera.position.x = clamp(camera.position.x, halfW, worldWidth - halfW);
        camera.position.y = clamp(camera.position.y, halfH, worldHeight - halfH);

        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Convierte coordenadas de pantalla (píxeles) a coordenadas del mundo (unidades)
     * Es vital usar el viewport aquí para que funcione con las barras negras.
     */
    public Vector3 screenToWorld(int screenX, int screenY) {
        Vector3 mousePos = new Vector3(screenX, screenY, 0);
        viewport.unproject(mousePos); // El viewport sabe dónde están las barras negras
        return mousePos;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Se llama desde el método resize(w, h) de la Screen principal.
     */
    public void resize(int width, int height) {
        // El 'true' final centra la cámara automáticamente
        viewport.update(width, height, true);
    }
}
