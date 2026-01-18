package io.github.catpire.PowerUp; // (O tu paquete)

import io.github.catpire.InGame;
import io.github.catpire.Player;
import com.badlogic.gdx.Gdx;

public class PowerUp_ArcShot implements PowerUpEffect {

    @Override
    public void apply(InGame game, Player player) {
        // Le decimos al Player que mejore su habilidad de arco
        player.applyArcShotUpgrade();

        Gdx.app.log("PowerUp_ArcShot", "¡Mejora de Arco activada!");
    }

    public String getTexturePath() {
        return "fireball1.png"; // <-- ¡Aquí definimos su icono!
    }



}
