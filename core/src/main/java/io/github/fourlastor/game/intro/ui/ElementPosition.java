package io.github.fourlastor.game.intro.ui;

import static io.github.fourlastor.game.intro.Config.TILE_SIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.intro.Config;

public class ElementPosition {

    public static Vector2 positionOnBoard(Viewport viewport, Vector2 output) {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        Vector2 unprojected = viewport.unproject(output.set(x, y)).scl(1f / TILE_SIZE);
        return unprojected.set(
                MathUtils.clamp(MathUtils.floor(unprojected.x), 0f, Config.TILE_COUNT - 1),
                MathUtils.clamp(MathUtils.floor(unprojected.y), 0, Config.TILE_COUNT - 1));
    }
}
