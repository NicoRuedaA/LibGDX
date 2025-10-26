package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

/**
 * Esta clase maneja la UI que se muestra al subir de nivel.
 * "Pausa" el juego y muestra las opciones de mejora.
 */
public class LevelUpUI {

    private Stage stage;
    private Skin skin; // La skin 'uiskin.json' que ya tienes

    // Texturas para los botones
    private Texture heartTexture;
    private Texture projectileTexture;

    /**
     * Constructor que crea la UI.
     * @param onHealthSelected Callback (función) que se ejecuta al elegir "Vida".
     * @param onProjectileSelected Callback (función) que se ejecuta al elegir "Proyectil".
     */
    public LevelUpUI(Runnable onHealthSelected, Runnable onProjectileSelected) {
        stage = new Stage(new ScreenViewport());

        // 1. Cargar la Skin (para la fuente y el fondo)
        try {
            skin = new Skin(Gdx.files.internal("uiskin.json"));
        } catch (Exception e) {
            Gdx.app.error("LevelUpUI", "No se pudo cargar 'uiskin.json'", e);
            // Cargamos una skin por defecto si falla
            skin = new Skin();
        }

        // 2. Cargar las texturas de los botones
        try {
            heartTexture = new Texture(Gdx.files.internal("heart.png")); // O 'heart.png'
            projectileTexture = new Texture(Gdx.files.internal("fireball1.png"));
        } catch (Exception e) {
            Gdx.app.error("LevelUpUI", "No se pudieron cargar las texturas de los botones");
            return;
        }

        // 3. Crear los Botones con Imagen
        ImageButton healthButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(heartTexture)));
        ImageButton projectileButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(projectileTexture)));

        // 4. Añadir Listeners (qué pasa al hacer clic)
        healthButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onHealthSelected.run(); // Ejecuta la función que nos pasó GameScreen
            }
        });

        projectileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onProjectileSelected.run(); // Ejecuta la función que nos pasó GameScreen
            }
        });

        // 5. Crear la Tabla para organizar la UI
        Table table = new Table();
        table.setFillParent(true);
        // table.setDebug(true); // Descomenta esto para ver los bordes de la tabla

        // Añadimos un fondo semitransparente (opcional, pero queda bien)
        Label.LabelStyle bgStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        bgStyle.background = skin.newDrawable("default-rect", new Color(0, 0, 0, 0.7f));
        table.setBackground(bgStyle.background);

        // Añadir Título
        table.add(new Label("¡NIVEL ALCANZADO!", skin)).colspan(2).padBottom(40);
        table.row();

        // Añadir Botones
        table.add(healthButton).pad(20);
        table.add(projectileButton).pad(20);

        stage.addActor(table);
    }

    /**
     * Dibuja la UI.
     */
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    /**
     * Devuelve el 'Stage' para que GameScreen le pueda dar el control del input.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Libera la memoria.
     */
// En LevelUpUI.java

    public void dispose() {
        stage.dispose();

        // (Añadimos un null-check para la skin, por si acaso)
        if (skin != null) {
            skin.dispose();
        }

        // --- ¡ESTA ES LA CORRECCIÓN! ---
        // Comprueba si la textura NO es null antes de llamar a dispose()
        if (heartTexture != null) {
            heartTexture.dispose();
        }
        if (projectileTexture != null) {
            projectileTexture.dispose();
        }
        // --- FIN DE LA CORRECCIÓN ---
    }
}
