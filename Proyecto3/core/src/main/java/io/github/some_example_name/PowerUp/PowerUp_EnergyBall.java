package io.github.some_example_name.PowerUp;

import io.github.some_example_name.InGame;
import io.github.some_example_name.Player;
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
