package io.github.some_example_name;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        // Al iniciar el juego, mostramos la pantalla del menú principal
        this.setScreen(new MainMenu(this));
    }
}
