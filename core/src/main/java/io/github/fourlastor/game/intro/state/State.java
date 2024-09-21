package io.github.fourlastor.game.intro.state;

import com.badlogic.gdx.math.GridPoint2;
import com.google.auto.value.AutoValue;
import io.github.fourlastor.game.intro.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoValue
public abstract class State {

    public abstract Builder builder();

    public abstract Element fireStart();

    public abstract Element fireEnd();

    public abstract GridPoint2 fireLast();

    public abstract Element waterStart();

    public abstract Element waterEnd();

    public abstract GridPoint2 waterLast();

    public abstract Element earthStart();

    public abstract Element earthEnd();

    public abstract GridPoint2 earthLast();

    public abstract Element airStart();

    public abstract Element airEnd();

    public abstract GridPoint2 airLast();

    public abstract Map<GridPoint2, Tile> tiles();

    public List<GridPoint2> fireStartingPositions() {
        return startingPositions(fireStart(), fireEnd(), fireLast());
    }

    public List<GridPoint2> waterStartingPositions() {
        return startingPositions(waterStart(), waterEnd(), waterLast());
    }

    public List<GridPoint2> earthStartingPositions() {
        return startingPositions(earthStart(), earthEnd(), earthLast());
    }

    public List<GridPoint2> airStartingPositions() {
        return startingPositions(airStart(), airEnd(), airLast());
    }

    private List<GridPoint2> startingPositions(Element start, Element end, GridPoint2 last) {
        List<GridPoint2> results = new ArrayList<>(16);
        addPositions(start, end, last, results);
        return results;
    }

    public boolean gameWon() {
        return areAdjacent(fireStart(), fireLast())
                && areAdjacent(waterStart(), waterLast())
                && areAdjacent(earthStart(), earthLast())
                && areAdjacent(airStart(), airLast());
    }

    private boolean areAdjacent(Element startElement, GridPoint2 last) {
        int x = last.x;
        int y = last.y;
        GridPoint2 start = startElement.position();
        return (start.x == x && start.y == y - 1
                || start.x == x && start.y == y + 1
                || start.x == x - 1 && start.y == y
                || start.x == x + 1 && start.y == y);
    }

    private void addPositions(Element start, Element end, GridPoint2 last, List<GridPoint2> results) {
        if (!start.visible() || !end.visible()) {
            return;
        }
        GridPoint2 currentCandidate = new GridPoint2(last);
        int x = currentCandidate.x;
        int y = currentCandidate.y;
        if (spotFree(currentCandidate.set(x - 1, y))) {
            results.add(new GridPoint2(currentCandidate));
        }
        if (spotFree(currentCandidate.set(x + 1, y))) {
            results.add(new GridPoint2(currentCandidate));
        }
        if (spotFree(currentCandidate.set(x, y - 1))) {
            results.add(new GridPoint2(currentCandidate));
        }
        if (spotFree(currentCandidate.set(x, y + 1))) {
            results.add(new GridPoint2(currentCandidate));
        }
    }

    private boolean spotFree(GridPoint2 candidate) {
        return candidate.x >= 0
                && candidate.x < Config.TILE_COUNT
                && candidate.y >= 0
                && candidate.y < Config.TILE_COUNT
                && !fireStart().position().equals(candidate)
                && !fireEnd().position().equals(candidate)
                && !waterStart().position().equals(candidate)
                && !waterEnd().position().equals(candidate)
                && !earthStart().position().equals(candidate)
                && !earthEnd().position().equals(candidate)
                && !airStart().position().equals(candidate)
                && !airEnd().position().equals(candidate)
                && !tiles().containsKey(candidate);
    }

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
                            .fireLast(position)
                            .build();
                } else {
                    HashMap<GridPoint2, Tile> newTiles = new HashMap<>(tiles());
                    newTiles.put(position, Tile.create(type));
                    return builder().tiles(newTiles).fireLast(position).build();
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
                            .waterLast(position)
                            .build();
                } else {
                    HashMap<GridPoint2, Tile> newTiles = new HashMap<>(tiles());
                    newTiles.put(position, Tile.create(type));
                    return builder().tiles(newTiles).waterLast(position).build();
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
                            .earthLast(position)
                            .build();
                } else {
                    HashMap<GridPoint2, Tile> newTiles = new HashMap<>(tiles());
                    newTiles.put(position, Tile.create(type));
                    return builder().tiles(newTiles).earthLast(position).build();
                }
            case AIR:
                if (!airStart().visible()) {
                    return builder()
                            .airStart(airStart().builder().position(position).build())
                            .build();
                } else if (!airEnd().visible()) {
                    return builder()
                            .airEnd(airEnd().builder().position(position).build())
                            .airLast(position)
                            .build();
                } else {
                    HashMap<GridPoint2, Tile> newTiles = new HashMap<>(tiles());
                    newTiles.put(position, Tile.create(type));
                    return builder().tiles(newTiles).airLast(position).build();
                }
            default:
                return this;
        }
    }

    public static State game(
            GridPoint2 fireStart,
            GridPoint2 fireEnd,
            GridPoint2 waterStart,
            GridPoint2 waterEnd,
            GridPoint2 earthStart,
            GridPoint2 earthEnd,
            GridPoint2 airStart,
            GridPoint2 airEnd) {
        return initial()
                .add(ElementType.FIRE, fireStart)
                .add(ElementType.FIRE, fireEnd)
                .add(ElementType.WATER, waterStart)
                .add(ElementType.WATER, waterEnd)
                .add(ElementType.EARTH, earthStart)
                .add(ElementType.EARTH, earthEnd)
                .add(ElementType.AIR, airStart)
                .add(ElementType.AIR, airEnd);
    }

    public static State initial() {
        return new AutoValue_State.Builder()
                .fireStart(Element.initial())
                .fireEnd(Element.initial())
                .fireLast(new GridPoint2())
                .waterStart(Element.initial())
                .waterEnd(Element.initial())
                .waterLast(new GridPoint2())
                .earthStart(Element.initial())
                .earthEnd(Element.initial())
                .earthLast(new GridPoint2())
                .airStart(Element.initial())
                .airEnd(Element.initial())
                .airLast(new GridPoint2())
                .tiles(new HashMap<>())
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder fireStart(Element value);

        public abstract Builder fireEnd(Element value);

        public abstract Builder fireLast(GridPoint2 value);

        public abstract Builder waterStart(Element value);

        public abstract Builder waterEnd(Element value);

        public abstract Builder waterLast(GridPoint2 value);

        public abstract Builder earthStart(Element value);

        public abstract Builder earthEnd(Element value);

        public abstract Builder earthLast(GridPoint2 value);

        public abstract Builder airStart(Element value);

        public abstract Builder airEnd(Element value);

        public abstract Builder airLast(GridPoint2 value);

        public abstract Builder tiles(Map<GridPoint2, Tile> value);

        public abstract State build();
    }
}
