package io.github.some_example_name.PowerUp;

import io.github.some_example_name.InGame;
import io.github.some_example_name.Player;

public class PowerUp_Speed implements PowerUpEffect {
    @Override
    public void apply(InGame game, Player player) {
        // El PowerUp solo sabe QUE debe subir la velocidad
        // El Player sabe COMO subirla
        player.increaseSpeed(50);
        System.out.println("¡Velocidad aumentada!");
    }

    public String getTexturePath() {
        return "sneakers.png"; // <-- ¡Aquí definimos su icono!
    }
}
