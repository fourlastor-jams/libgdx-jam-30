package io.github.fourlastor.game.intro;

import com.badlogic.gdx.math.GridPoint2;
import io.github.fourlastor.game.intro.state.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;

public class LevelGenerator {

    private final Random random;

    @Inject
    public LevelGenerator(Random random) {
        this.random = random;
    }

    public State manually() {
        return State.game(
                new GridPoint2(0, 4), // fs
                new GridPoint2(3, 0), // fe
                new GridPoint2(1, 2), // ws
                new GridPoint2(3, 3), // we
                new GridPoint2(1, 1), // es
                new GridPoint2(4, 0), // ee
                new GridPoint2(1, 4), // as
                new GridPoint2(2, 2) // ae
                );
    }

    public State generateLevel() {
        List<GridPoint2> generated = new ArrayList<>(16);
        for (int i = 0; i < 8; i++) {
            generated.add(pickRandom(generated, i));
        }
        return State.game(
                generated.get(0),
                generated.get(1),
                generated.get(2),
                generated.get(3),
                generated.get(4),
                generated.get(5),
                generated.get(6),
                generated.get(7));
    }

    private GridPoint2 pickRandom(List<GridPoint2> existing, int counter) {
        while (true) {
            GridPoint2 point = new GridPoint2(random.nextInt(Config.TILE_COUNT), random.nextInt(Config.TILE_COUNT));
            if (existing.contains(point) || elementOnEdge(counter, point) || elementHasAdjacent(point, existing)) {
                continue;
            }
            return point;
        }
    }

    private boolean elementHasAdjacent(GridPoint2 point, List<GridPoint2> existing) {
        GridPoint2 needle = point.cpy();
        return existing.stream().anyMatch(it -> it.dst(needle) == 1);
    }

    private boolean elementOnEdge(int counter, GridPoint2 point) {
        return counter % 2 == 0
                && (point.x == 0
                        || point.x == Config.TILE_COUNT - 1
                        || point.y == 0
                        || point.y == Config.TILE_COUNT - 1);
    }
}
