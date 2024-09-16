package io.github.fourlastor.game.intro.state;

import com.badlogic.gdx.math.GridPoint2;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Element {

    private static final GridPoint2 NO_VALUE = new GridPoint2(-1, -1);

    public abstract Builder builder();

    public abstract GridPoint2 position();

    public boolean visible() {
        return !NO_VALUE.equals(position());
    }

    public static Element initial() {
        return new AutoValue_Element.Builder().position(NO_VALUE).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder position(GridPoint2 value);

        public abstract Element build();
    }
}
