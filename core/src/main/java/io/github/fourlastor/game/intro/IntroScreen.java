package io.github.fourlastor.game.intro;

import static io.github.fourlastor.game.di.modules.AssetsModule.WHITE_PIXEL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.intro.state.ElementType;
import io.github.fourlastor.game.intro.state.State;
import io.github.fourlastor.game.intro.ui.Board;
import io.github.fourlastor.game.route.Router;
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
    private final Router router;
    private final StateContainer<State> stateContainer;

    @Inject
    public IntroScreen(
            @Named(WHITE_PIXEL) TextureRegion whitePixel,
            TextureAtlas atlas,
            InputMultiplexer multiplexer,
            Router router,
            LevelGenerator generator) {
        this.multiplexer = multiplexer;
        this.router = router;
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
                atlas.findRegion("elements/air-tile"));
        TextureAtlas.AtlasRegion tile = atlas.findRegion("elements/tile");
        stateContainer = new StateContainer<>(generator.generateLevel());
        Board board = new Board(elementTextures, tile, new Board.Listener() {
            @Override
            public void onElementPlaced(ElementType type, GridPoint2 position) {
                stateContainer.update(it -> it.add(type, position));
            }

            @Override
            public void onElementRemoved(GridPoint2 position) {
                stateContainer.update(it -> it.remove(position));
            }
        });
        TextureAtlas.AtlasRegion winTexture = atlas.findRegion("win_condition_text");
        Image winConditionText = new Image(winTexture);
        float scale = ((float) Config.TILE_COUNT * Config.TILE_SIZE) / winTexture.getRegionWidth();
        winConditionText.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2, Align.center);
        winConditionText.setOrigin(Align.center);
        winConditionText.setScale(scale);
        Color winColor = new Color(Color.WHITE);
        winColor.a = 0;
        winConditionText.setColor(winColor);
        winConditionText.setTouchable(Touchable.disabled);
        Image winOverlay = new Image(atlas.findRegion("whitePixel"));
        Color overlayColor = new Color(Color.BLACK);
        overlayColor.a = 0;
        winOverlay.setColor(overlayColor);
        winOverlay.setSize(stage.getWidth(), stage.getHeight());
        winOverlay.setTouchable(Touchable.disabled);
        stage.addActor(board);
        stage.addActor(winOverlay);
        stage.addActor(winConditionText);
        stateContainer.listen(board::update);
        stateContainer.distinct(State::gameWon).listen(state -> {
            if (state.gameWon()) {
                board.setTouchable(Touchable.disabled);
                winConditionText.addAction(Actions.fadeIn(1));
                winOverlay.addAction(Actions.alpha(0.5f, 1));
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            router.goToIntro();
        }
    }

    @Override
    public void hide() {
        multiplexer.removeProcessor(stage);
        super.hide();
    }
}
