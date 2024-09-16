package io.github.fourlastor.game.intro.state;

import com.badlogic.gdx.graphics.Color;

public enum ElementType {
    FIRE(Color.CORAL),
    WATER(Color.CYAN),
    EARTH(Color.BROWN),
    AIR(Color.WHITE);

    public final Color color;

    ElementType(Color color) {
        this.color = color;
    }
}
