package io.github.catpire.PowerUp;

import io.github.catpire.InGame;
import io.github.catpire.Player;

public class PowerUp_MoreProjectiles implements PowerUpEffect {

    // Solo añade 'InGame game' al método
    @Override
    public void apply(InGame game, Player player) {
        player.applyProjectileUpgrade();
    }

    public String getTexturePath() {
        return "fireball3.png"; // <-- ¡Aquí definimos su icono!
    }
}
