package io.github.fourlastor.game.intro.state;

import com.badlogic.gdx.math.GridPoint2;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class State {

    public abstract Builder builder();

    public abstract Element fireStart();

    public abstract Element fireEnd();

    public abstract Element waterStart();

    public abstract Element waterEnd();

    public abstract Element earthStart();

    public abstract Element earthEnd();

    public abstract Element airStart();

    public abstract Element airEnd();

    public State add(ElementType type, GridPoint2 position) {
        switch (type) {
            case FIRE:
                if (!fireStart().visible()) {
                    return builder()
                            .fireStart(fireStart().builder().position(position).build())
                            .build();
                } else if (!fireEnd().visible()) {
                    return builder()
                            .fireEnd(fireEnd().builder().position(position).build())
                            .build();
                } else {
                    return this;
                }
            case WATER:
                if (!waterStart().visible()) {
                    return builder()
                            .waterStart(
                                    waterStart().builder().position(position).build())
                            .build();
                } else if (!waterEnd().visible()) {
                    return builder()
                            .waterEnd(waterEnd().builder().position(position).build())
                            .build();
                } else {
                    return this;
                }
            case EARTH:
                if (!earthStart().visible()) {
                    return builder()
                            .earthStart(
                                    earthStart().builder().position(position).build())
                            .build();
                } else if (!earthEnd().visible()) {
                    return builder()
                            .earthEnd(earthEnd().builder().position(position).build())
                            .build();
                } else {
                    return this;
                }
            case AIR:
                if (!airStart().visible()) {
                    return builder()
                            .airStart(airStart().builder().position(position).build())
                            .build();
                } else if (!airEnd().visible()) {
                    return builder()
                            .airEnd(airEnd().builder().position(position).build())
                            .build();
                } else {
                    return this;
                }
            default:
                return this;
        }
    }

    public static State initial() {
        return new AutoValue_State.Builder()
                .fireStart(Element.initial())
                .fireEnd(Element.initial())
                .waterStart(Element.initial())
                .waterEnd(Element.initial())
                .earthStart(Element.initial())
                .earthEnd(Element.initial())
                .airStart(Element.initial())
                .airEnd(Element.initial())
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder fireStart(Element value);

        public abstract Builder fireEnd(Element value);

        public abstract Builder waterStart(Element value);

        public abstract Builder waterEnd(Element value);

        public abstract Builder earthStart(Element value);

        public abstract Builder earthEnd(Element value);

        public abstract Builder airStart(Element value);

        public abstract Builder airEnd(Element value);

        public abstract State build();
    }
}
