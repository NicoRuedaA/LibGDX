package io.github.catpire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

public class resourceManager {

    private static resourceManager instance;

    // Un mapa para guardar las texturas cargadas: "nombre.png" -> Textura
    private Map<String, Texture> textures;

    private resourceManager() {
        textures = new HashMap<>();
    }

    public static resourceManager getInstance() {
        if (instance == null) instance = new resourceManager();
        return instance;
    }

    /**
     * Devuelve la textura. Si no está cargada, la carga primero.
     */
    public Texture getTexture(String path) {
        // 1. Si ya la tenemos, la devolvemos
        if (textures.containsKey(path)) {
            return textures.get(path);
        }

        // 2. Si no, la cargamos
        try {
            Texture tex = new Texture(Gdx.files.internal(path));
            textures.put(path, tex); // La guardamos en el mapa
            return tex;
        } catch (Exception e) {
            Gdx.app.error("ResourceManager", "Error cargando textura: " + path);
            return null; // O devuelve una textura de error por defecto
        }
    }

    /**
     * Libera TODAS las texturas al cerrar el juego.
     */
    public void dispose() {
        for (Texture tex : textures.values()) {
            tex.dispose();
        }
        textures.clear();
    }
}
