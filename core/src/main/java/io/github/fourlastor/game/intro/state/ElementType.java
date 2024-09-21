package io.github.fourlastor.game.intro.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.fourlastor.game.intro.ElementTextures;
import java.util.function.Function;

public enum ElementType {
    FIRE(new Color(0xa83848ff), it -> it.fireTile),
    WATER(new Color(0x6888b8ff), it -> it.waterTile),
    EARTH(new Color(0x884848ff), it -> it.earthTile),
    AIR(new Color(0xd8d8d8ff), it -> it.airTile);

    public final Color color;
    public final Function<ElementTextures, TextureRegion> tileSelector;

    ElementType(Color color, Function<ElementTextures, TextureRegion> tileSelector) {
        this.color = color;
        this.tileSelector = tileSelector;
    }
}
