package io.github.catpire.PowerUp;

import io.github.catpire.InGame;
import io.github.catpire.Player;

public class PowerUp_MoreHealth implements PowerUpEffect {

    // Solo añade 'InGame game' al método
    @Override
    public void apply(InGame game, Player player) {
        player.applyHealthUpgrade();
    }

    public String getTexturePath() {
        return "heart.png"; // <-- ¡Aquí definimos su icono!
    }
}
