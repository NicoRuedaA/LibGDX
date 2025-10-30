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

public class LevelUpUI {

    private Stage stage;
    private Skin skin;

    // Texturas para los botones
    private Texture heartTexture;
    private Texture projectileTexture;
    private Texture energyBallTexture;
    private Texture arcShotTexture;

    /**
     * --- CAMBIO 1: AÑADIR EL 4º RUNNABLE ---
     * El constructor ahora debe aceptar 4 "recetas" (Runnables), no 3.
     */
    public LevelUpUI(Runnable onHealthSelected, Runnable onProjectileSelected, Runnable onEnergyBallSelected, Runnable onArcShotSelected) {
        stage = new Stage(new ScreenViewport());

        // 1. Cargar la Skin
        try {
            skin = new Skin(Gdx.files.internal("uiskin.json"));
        } catch (Exception e) {
            Gdx.app.error("LevelUpUI", "No se pudo cargar 'uiskin.json'", e);
            skin = new Skin();
        }

        // 2. Cargar las texturas de los botones
        try {
            heartTexture = new Texture(Gdx.files.internal("heart.png"));
            projectileTexture = new Texture(Gdx.files.internal("fireball1.png"));
            energyBallTexture = new Texture(Gdx.files.internal("electroball.png"));
            arcShotTexture = new Texture(Gdx.files.internal("fireball1.png")); // (Puedes cambiar este icono)
        } catch (Exception e) {
            Gdx.app.error("LevelUpUI", "No se pudieron cargar las texturas de los botones");
            return;
        }

        // 3. Crear los 4 Botones con Imagen
        ImageButton healthButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(heartTexture)));
        ImageButton projectileButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(projectileTexture)));
        ImageButton energyBallButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(energyBallTexture)));
        ImageButton arcShotButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(arcShotTexture))); // (Corregí el nombre de la variable)


        // 4. Añadir Listeners (qué pasa al hacer clic)
        healthButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onHealthSelected.run();
            }
        });

        projectileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onProjectileSelected.run();
            }
        });

        energyBallButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                onEnergyBallSelected.run();
            }
        });

        /**
         * --- CAMBIO 2: AÑADIR EL LISTENER PARA EL 4º BOTÓN ---
         * El listener debe usar el 4º Runnable que pasamos al constructor.
         */
        arcShotButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                onArcShotSelected.run(); // <-- Usar el nuevo Runnable
            }
        });


        // 5. Crear la Tabla para organizar la UI
        Table table = new Table();
        table.setFillParent(true);

        // Fondo semitransparente
        Label.LabelStyle bgStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        bgStyle.background = skin.newDrawable("default-rect", new Color(0, 0, 0, 0.7f));
        table.setBackground(bgStyle.background);

        // Añadir Título (colspan 4 está correcto)
        table.add(new Label("¡NIVEL ALCANZADO!", skin)).colspan(4).padBottom(40);
        table.row();

        // Añadir Botones
        table.add(healthButton).pad(20);
        table.add(projectileButton).pad(20);
        table.add(energyBallButton).pad(20);

        /**
         * --- CAMBIO 3: AÑADIR EL 4º BOTÓN A LA TABLA ---
         * Olvidaste añadir el botón a la tabla para que se vea.
         */
        table.add(arcShotButton).pad(20); // <-- AÑADIR ESTO

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
    public void dispose() {
        stage.dispose();

        if (skin != null) {
            skin.dispose();
        }

        if (heartTexture != null) {
            heartTexture.dispose();
        }
        if (projectileTexture != null) {
            projectileTexture.dispose();
        }
        if (energyBallTexture != null) {
            energyBallTexture.dispose();
        }

        /**
         * --- CAMBIO 4: AÑADIR EL DISPOSE PARA LA NUEVA TEXTURA ---
         * Tienes que liberar la memoria de la 4ª textura.
         */
        if (arcShotTexture != null) {
            arcShotTexture.dispose(); // <-- AÑADIR ESTO
        }
    }
}
