package io.github.fourlastor.game.intro.state;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Tile {
    public abstract ElementType type();

    public static Tile create(ElementType type) {
        return new AutoValue_Tile(type);
    }
}
