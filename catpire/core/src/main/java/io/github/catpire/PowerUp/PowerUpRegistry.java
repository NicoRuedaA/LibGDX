package io.github.catpire.PowerUp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PowerUpRegistry {
    private static List<PowerUpEffect> allPowerups = new ArrayList<>();

    static {
        allPowerups.add(new PowerUp_EnergyBall());
        allPowerups.add(new PowerUp_MoreHealth());
        allPowerups.add(new PowerUp_Shield());
        allPowerups.add(new PowerUp_MoreHealth());
        allPowerups.add(new PowerUp_Speed());
        allPowerups.add(new PowerUp_ArcShot());
        allPowerups.add(new PowerUp_MoreProjectiles());
        // ... añade aquí los que quieras
    }

    public static List<PowerUpEffect> getRandomPowerups(int amount) {
        List<PowerUpEffect> selection = new ArrayList<>(allPowerups);
        Collections.shuffle(selection); // Mezcla la baraja

        // Devuelve los primeros 'amount' (ej. 3)
        if (amount > selection.size()) return selection;
        return selection.subList(0, amount);
    }

    public static List<PowerUpEffect> getAllEffects() {
        return new ArrayList<>(allPowerups);
    }
}
