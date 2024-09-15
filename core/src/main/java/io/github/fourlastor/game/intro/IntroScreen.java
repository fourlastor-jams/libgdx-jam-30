package io.github.fourlastor.game.intro;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

import javax.inject.Inject;
import javax.inject.Named;

import static io.github.fourlastor.game.di.modules.AssetsModule.WHITE_PIXEL;

public class IntroScreen extends ScreenAdapter {

    public static Color CLEAR_COLOR = new Color(0x333333ff);

    private final Stage stage;
    private final Viewport viewport;

    @Inject
    public IntroScreen(@Named(WHITE_PIXEL) TextureRegion whitePixel) {
        viewport = new FitViewport(256, 144);
        stage = new Stage(viewport);
        ShapeDrawer shapeDrawer = new ShapeDrawer(stage.getBatch(), whitePixel);
        Image image = new Image(new ShapeDrawerDrawable(shapeDrawer) {
            @Override
            public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
                int count = 12;
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
        image.setSize(144, 144);
        image.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
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
