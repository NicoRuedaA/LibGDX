package io.github.some_example_name;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraController {
    private OrthographicCamera camera;
    private float worldWidth, worldHeight;

    public CameraController(float viewportWidth, float viewportHeight, float worldWidth, float worldHeight) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.update();
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void follow(Player player) {
        // Centramos la cámara en la posición actual del jugador
        camera.position.set(player.position.x, player.position.y, 0);

        // Limitar dentro del mundo
        float halfW = camera.viewportWidth / 2;
        float halfH = camera.viewportHeight / 2;
        camera.position.x = clamp(camera.position.x, halfW, worldWidth - halfW);
        camera.position.y = clamp(camera.position.y, halfH, worldHeight - halfH);

        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Vector3 screenToWorld(int screenX, int screenY) {
        Vector3 mousePos = new Vector3(screenX, screenY, 0);
        camera.unproject(mousePos);
        return mousePos;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
