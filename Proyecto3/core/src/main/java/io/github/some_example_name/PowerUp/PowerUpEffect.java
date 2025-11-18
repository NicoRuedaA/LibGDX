package io.github.some_example_name.PowerUp;

import io.github.some_example_name.Player;
import io.github.some_example_name.InGame;

public interface PowerUpEffect {
    void apply(InGame game, Player player);

    String getTexturePath();
}
