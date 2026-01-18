package io.github.catpire.PowerUp;

import io.github.catpire.InGame;
import io.github.catpire.Player;

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
