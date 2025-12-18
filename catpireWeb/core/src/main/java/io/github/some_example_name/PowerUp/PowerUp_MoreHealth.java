package io.github.some_example_name.PowerUp;

import io.github.some_example_name.InGame;
import io.github.some_example_name.Player;

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
