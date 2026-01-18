package io.github.catpire.PowerUp;

import io.github.catpire.Player;
import io.github.catpire.InGame;

public interface PowerUpEffect {
    void apply(InGame game, Player player);

    String getTexturePath();
}
