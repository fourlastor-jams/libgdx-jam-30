package io.github.fourlastor.game.intro;

import static io.github.fourlastor.game.di.modules.AssetsModule.WHITE_PIXEL;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import javax.inject.Inject;
import javax.inject.Named;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public class IntroScreen extends ScreenAdapter {

    private static final Color CLEAR_COLOR = new Color(0x222222ff);
    private static final int TILE_SIZE = 32;
    private static final int TILE_COUNT = 10;

    private final Stage stage;
    private final Viewport viewport;

    @Inject
    public IntroScreen(@Named(WHITE_PIXEL) TextureRegion whitePixel) {
        viewport = new FitViewport(TILE_SIZE * TILE_COUNT, TILE_SIZE * (TILE_COUNT + 3));
        stage = new Stage(viewport);
        ShapeDrawer shapeDrawer = new ShapeDrawer(stage.getBatch(), whitePixel);
        Image bg = new Image(whitePixel);
        bg.setSize(stage.getWidth(), stage.getHeight());
        bg.setColor(new Color(0x333333ff));
        stage.addActor(bg);
        Image image = new Image(new ShapeDrawerDrawable(shapeDrawer) {
            @Override
            public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
                int count = TILE_COUNT;
                float squareWidth = width / count;
                float squareHeight = height / count;
                boolean dark = true;
                for (int column = 0; column < count; column++) {
                    for (int row = 0; row < count; row++) {
                        float rX = squareWidth * row + x;
                        float rY = squareHeight * column + y;
                        Color color = dark ? Color.LIGHT_GRAY : Color.LIME;
                        shapeDrawer.filledRectangle(rX, rY, squareWidth, squareHeight, color);
                        dark = !dark;
                    }
                    dark = !dark;
                }
            }
        });
        image.setSize(TILE_SIZE * TILE_COUNT, TILE_SIZE * TILE_COUNT);
        image.setPosition(stage.getWidth() / 2, 0, Align.center | Align.bottom);
        stage.addActor(image);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR, true);
        stage.act(delta);
        stage.draw();
    }
}
