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
import java.util.List; // <-- Importante

import io.github.some_example_name.PowerUp.PowerUpEffect;



public class LevelUpUI {
    public static class PowerUpOption {
        public PowerUpEffect effect;
        public Runnable action;

        public PowerUpOption(PowerUpEffect effect, Runnable action) {
            this.effect = effect;
            this.action = action;
        }
    }


    private Stage stage;
    private Skin skin;


    /**
     * --- CAMBIO 1: AÑADIR EL 4º RUNNABLE ---
     * El constructor ahora debe aceptar 4 "recetas" (Runnables), no 3.
     */
    public LevelUpUI(List<PowerUpOption> options) {
        stage = new Stage(new ScreenViewport());

        // 1. Cargar la Skin
        try {
            skin = new Skin(Gdx.files.internal("uiskin.json"));
        } catch (Exception e) {
            skin = new Skin();
        }

        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle bgStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        bgStyle.background = skin.newDrawable("default-rect", new Color(0, 0, 0, 0.7f));
        table.setBackground(bgStyle.background);

        // Título: El colspan se adapta al número de opciones
        int columnCount = options.size();
        table.add(new Label("¡NIVEL ALCANZADO!", skin)).colspan(columnCount).padBottom(40);
        table.row();
        for (PowerUpOption option : options) {

            // A. Obtener la textura usando el path del efecto
            String texturePath = option.effect.getTexturePath();
            Texture texture = resourceManager.getInstance().getTexture(texturePath);

            if (texture == null) {
                Gdx.app.error("LevelUpUI", "Textura no encontrada: " + texturePath);
                continue;
            }

            // B. Crear el botón
            ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));

            // C. Añadir la lógica (el Runnable)
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    option.action.run();
                }
            });

            // D. Añadir a la tabla
            table.add(button).pad(20);
        }

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
        if (skin != null) skin.dispose();

        // ¡YA NO HACEMOS DISPOSE DE TEXTURAS AQUÍ!
        // El ResourceManager se encargará de eso al cerrar el juego.
    }
}
