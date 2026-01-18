package io.github.catpire.PowerUp;

import io.github.catpire.InGame;
import io.github.catpire.Player;
import com.badlogic.gdx.Gdx;

public class PowerUp_EnergyBall implements PowerUpEffect {
    @Override
    public void apply(InGame game, Player player) {
        // ¡Aquí está la magia!
        // Le decimos a InGame que añada una nueva bola
        game.spawnElectroballProjectile(player);

        Gdx.app.log("PowerUp_ElectroBall", "¡ElectroBall activada!");
    }

    public String getTexturePath() {
        return "electroball.png"; // <-- ¡Aquí definimos su icono!
    }
}
