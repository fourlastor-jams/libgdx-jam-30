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
        return fireComplete() && waterComplete() && earthComplete() && airComplete();
    }

    public boolean fireComplete() {
        return areAdjacent(fireStart(), fireLast());
    }

    public boolean waterComplete() {
        return areAdjacent(waterStart(), waterLast());
    }

    public boolean earthComplete() {
        return areAdjacent(earthStart(), earthLast());
    }

    public boolean airComplete() {
        return areAdjacent(airStart(), airLast());
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
        for (int cx = x - 1; spotFree(currentCandidate.set(cx, y)); cx--) {
            results.add(new GridPoint2(currentCandidate));
        }
        for (int cx = x + 1; spotFree(currentCandidate.set(cx, y)); cx++) {
            results.add(new GridPoint2(currentCandidate));
        }
        for (int cy = y - 1; spotFree(currentCandidate.set(x, cy)); cy--) {
            results.add(new GridPoint2(currentCandidate));
        }
        for (int cy = y + 1; spotFree(currentCandidate.set(x, cy)); cy++) {
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

    public State remove(GridPoint2 position) {
        Tile tile = tiles().get(position);
        if (tile == null) {
            return this;
        }
        GridPoint2 adjPosition = position.cpy();
        ElementType type = tile.type();
        Tile candidate = adj(adjPosition.set(position.x, position.y - 1), type);
        HashMap<GridPoint2, Tile> newTiles = new HashMap<>(tiles());
        newTiles.remove(position);
        Builder builder = builder().tiles(newTiles);
        if (candidate != null) {
            return updateLastPosition(type, adjPosition, builder);
        }
        candidate = adj(adjPosition.set(position.x, position.y + 1), type);
        if (candidate != null) {
            return updateLastPosition(type, adjPosition, builder);
        }
        candidate = adj(adjPosition.set(position.x - 1, position.y), type);
        if (candidate != null) {
            return updateLastPosition(type, adjPosition, builder);
        }
        candidate = adj(adjPosition.set(position.x + 1, position.y), type);
        if (candidate != null) {
            return updateLastPosition(type, adjPosition, builder);
        }
        switch (type) {
            case FIRE:
                return builder.fireLast(fireEnd().position()).build();
            case WATER:
                return builder.waterLast(waterEnd().position()).build();
            case EARTH:
                return builder.earthLast(earthEnd().position()).build();
            case AIR:
                return builder.airLast(airEnd().position()).build();
        }
        return this;
    }

    private State updateLastPosition(ElementType type, GridPoint2 adjPosition, State.Builder builder) {
        switch (type) {
            case FIRE:
                return builder.fireLast(adjPosition).build();
            case WATER:
                return builder.waterLast(adjPosition).build();
            case EARTH:
                return builder.earthLast(adjPosition).build();
            case AIR:
                return builder.airLast(adjPosition).build();
            default:
                throw new IllegalStateException("Invalid tile type");
        }
    }

    private Tile adj(GridPoint2 adjPosition, ElementType type) {
        Tile candidate = tiles().get(adjPosition);
        if (candidate == null || !candidate.type().equals(type)) {
            return null;
        }
        return candidate;
    }

    public State add(ElementType type, GridPoint2 position) {
        HashMap<GridPoint2, Tile> newTiles;
        switch (type) {
            case FIRE:
                newTiles = addTilesTo(fireLast(), position, type);
                return builder().tiles(newTiles).fireLast(position).build();
            case WATER:
                newTiles = addTilesTo(waterLast(), position, type);
                return builder().tiles(newTiles).waterLast(position).build();
            case EARTH:
                newTiles = addTilesTo(earthLast(), position, type);
                return builder().tiles(newTiles).earthLast(position).build();
            case AIR:
                newTiles = addTilesTo(airLast(), position, type);
                return builder().tiles(newTiles).airLast(position).build();
            default:
                return this;
        }
    }

    private HashMap<GridPoint2, Tile> addTilesTo(GridPoint2 initial, GridPoint2 position, ElementType type) {
        HashMap<GridPoint2, Tile> newTiles;
        newTiles = new HashMap<>(tiles());
        int dX = Integer.signum(position.x - initial.x);
        for (int x = initial.x + dX; x != position.x; x += dX) {
            newTiles.put(new GridPoint2(x, position.y), Tile.create(type));
        }
        int dY = Integer.signum(position.y - initial.y);
        for (int y = initial.y + dY; y != position.y; y += dY) {
            newTiles.put(new GridPoint2(position.x, y), Tile.create(type));
        }
        newTiles.put(position, Tile.create(type));
        return newTiles;
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
        return new AutoValue_State.Builder()
                .fireStart(Element.on(fireStart))
                .fireEnd(Element.on(fireEnd))
                .fireLast(fireEnd)
                .waterStart(Element.on(waterStart))
                .waterEnd(Element.on(waterEnd))
                .waterLast(waterEnd)
                .earthStart(Element.on(earthStart))
                .earthEnd(Element.on(earthEnd))
                .earthLast(earthEnd)
                .airStart(Element.on(airStart))
                .airEnd(Element.on(airEnd))
                .airLast(airEnd)
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
