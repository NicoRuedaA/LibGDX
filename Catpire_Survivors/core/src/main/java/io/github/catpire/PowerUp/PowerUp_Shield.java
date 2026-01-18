package io.github.catpire.PowerUp;

import io.github.catpire.InGame;
import io.github.catpire.Player;

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
