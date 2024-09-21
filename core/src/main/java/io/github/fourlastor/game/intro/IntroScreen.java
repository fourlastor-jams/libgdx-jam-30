package io.github.fourlastor.game.intro;

import static io.github.fourlastor.game.di.modules.AssetsModule.WHITE_PIXEL;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.intro.state.State;
import io.github.fourlastor.game.intro.ui.Board;
import io.github.fourlastor.game.state.StateContainer;
import javax.inject.Inject;
import javax.inject.Named;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public class IntroScreen extends ScreenAdapter {

    private static final Color CLEAR_COLOR = new Color(0x222222ff);

    private final Stage stage;
    private final Viewport viewport;
    private final InputMultiplexer multiplexer;
    private final StateContainer<State> stateContainer = new StateContainer<>(State.game(
            new GridPoint2(0,4), // fs
            new GridPoint2(3,0), // fe
            new GridPoint2(1,2), // ws
            new GridPoint2(3,3), // we
            new GridPoint2(1,1), // es
            new GridPoint2(4,0), // ee
            new GridPoint2(1,4), // as
            new GridPoint2(2,2)  // ae
    ));

    @Inject
    public IntroScreen(@Named(WHITE_PIXEL) TextureRegion whitePixel, TextureAtlas atlas, InputMultiplexer multiplexer) {
        this.multiplexer = multiplexer;
        viewport = new FitViewport(Config.TILE_SIZE * Config.TILE_COUNT, Config.TILE_SIZE * Config.TILE_COUNT);
        stage = new Stage(viewport);
        ShapeDrawer shapeDrawer = new ShapeDrawer(stage.getBatch(), whitePixel);
        Image bg = new Image(whitePixel);
        bg.setSize(stage.getWidth(), stage.getHeight());
        bg.setColor(new Color(0x333333ff));
        stage.addActor(bg);
        Image image = createGrid(shapeDrawer);
        image.setPosition(stage.getWidth() / 2, 0, Align.center | Align.bottom);
        stage.addActor(image);
        ElementTextures elementTextures = new ElementTextures(
                atlas.findRegion("elements/fire-element"),
                atlas.findRegion("elements/fire-tile"),
                atlas.findRegion("elements/water-element"),
                atlas.findRegion("elements/water-tile"),
                atlas.findRegion("elements/earth-element"),
                atlas.findRegion("elements/earth-tile"),
                atlas.findRegion("elements/air-element"),
                atlas.findRegion("elements/air-tile")
        );
        TextureAtlas.AtlasRegion tile = atlas.findRegion("elements/tile");
        Board board =
                new Board(elementTextures, tile, ((type, position) -> stateContainer.update(it -> it.add(type, position))));
        stage.addActor(board);
        stateContainer.listen(board::update);
        stateContainer.distinct(State::gameWon).listen(state -> {
            if (state.gameWon()) {
                System.out.println("YAY GAME WON");
            }
        });
    }

    private static Image createGrid(ShapeDrawer shapeDrawer) {
        Image image = new Image(new ShapeDrawerDrawable(shapeDrawer) {
            @Override
            public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
                int count = Config.TILE_COUNT;
                float squareWidth = width / count;
                float squareHeight = height / count;
                boolean light = true;
                for (int column = 0; column < count; column++) {
                    for (int row = 0; row < count; row++) {
                        float rX = squareWidth * row + x;
                        float rY = squareHeight * column + y;
                        Color color = light ? Config.LIGHT_TILE : Config.DARK_TILE;
                        shapeDrawer.filledRectangle(rX, rY, squareWidth, squareHeight, color);
                        light = !light;
                    }
                    if (count % 2 == 0) {
                        light = !light;
                    }
                }
            }
        });
        image.setSize(Config.TILE_SIZE * Config.TILE_COUNT, Config.TILE_SIZE * Config.TILE_COUNT);
        return image;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        super.show();
        multiplexer.addProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR, true);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        multiplexer.removeProcessor(stage);
        super.hide();
    }
}
