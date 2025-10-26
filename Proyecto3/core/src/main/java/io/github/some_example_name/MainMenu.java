package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenu implements Screen {

    private Proyecto3Game game;
    private Stage stage;
    private Skin skin;

    public MainMenu(Proyecto3Game game) {
        this.game = game;

        // El 'Stage' es como un lienzo para la UI
        stage = new Stage(new ScreenViewport());

        // Cargamos la skin que descargaste
        try {
            skin = new Skin(Gdx.files.internal("uiskin.json"));
        } catch (Exception e) {
            Gdx.app.error("MainMenu", "No se pudo cargar 'uiskin.json'. Asegúrate de que esté en 'assets'.");
            return;
        }

        // 'Table' nos ayuda a organizar la UI (como en HTML)
        Table table = new Table();
        table.setFillParent(true); // La tabla ocupa toda la pantalla
        stage.addActor(table); // Añadimos la tabla al lienzo

        // 1. Creamos el botón "Play"
        TextButton playButton = new TextButton("Play", skin);

        // 2. Definimos qué pasa al hacer clic
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Cambiamos a la pantalla del juego
                game.setScreen(new GameScreen(game));
            }
        });

        // 3. Añadimos el botón a la tabla
        table.add(playButton).width(200).height(50);

        // Hacemos que la UI (el 'Stage') reciba los clics
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Limpiamos la pantalla
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizamos y dibujamos el lienzo de la UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        // Liberamos los recursos del menú
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void show() {
        // Asegurarnos de que el 'Stage' maneje los clics al mostrar esta pantalla
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        // Al salir de esta pantalla, quitamos el procesador de input
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
