package io.github.some_example_name.PowerUp;

import io.github.some_example_name.InGame;
import io.github.some_example_name.Player;

public class PowerUp_Shield implements PowerUpEffect{
    @Override
    public void apply(InGame game, Player player) {
        // Activa un booleano en el player y quizás añade un efecto visual en InGame
        player.activateShield();
        // game.spawnParticleEffect(player.getPosition(), "shield_aura");
    }

    public String getTexturePath() {
        return "shield.png"; // <-- ¡Aquí definimos su icono!
    }
}
