package io.github.fourlastor.game.intro.ui;

import static io.github.fourlastor.game.intro.Config.TILE_SIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.fourlastor.game.intro.ElementTextures;
import io.github.fourlastor.game.intro.state.Element;
import io.github.fourlastor.game.intro.state.ElementType;
import io.github.fourlastor.game.intro.state.State;
import java.util.ArrayList;
import java.util.List;

public class Board extends WidgetGroup {

    private final Image fireStart;
    private final Image fireEnd;
    private Image fireCurrent;
    private final List<Image> firePreviews = new ArrayList<>();
    private final Image waterStart;
    private final Image waterEnd;
    private Image waterCurrent;
    private final List<Image> waterPreviews = new ArrayList<>();
    private final Image earthStart;
    private final Image earthEnd;
    private Image earthCurrent;
    private final List<Image> earthPreviews = new ArrayList<>();
    private final Image airStart;
    private final Image airEnd;
    private Image airCurrent;
    private final List<Image> airPreviews = new ArrayList<>();
    private final List<Image> visibleTiles = new ArrayList<>();
    private final StateMachine stateMachine;
    private final ElementTextures textures;
    private final TextureRegion tile;
    private final Listener listener;

    public Board(ElementTextures textures, TextureRegion tile, Listener listener) {
        this.textures = textures;
        this.tile = tile;
        this.listener = listener;
        setFillParent(true);
        fireStart = createButton(textures.fireElement);
        fireEnd = createButton(textures.fireTile);
        fireCurrent = fireEnd;
        waterStart = createButton(textures.waterElement);
        waterEnd = createButton(textures.waterTile);
        waterCurrent = waterEnd;
        earthStart = createButton(textures.earthElement);
        earthEnd = createButton(textures.earthTile);
        earthCurrent = earthEnd;
        airStart = createButton(textures.airElement);
        airEnd = createButton(textures.airTile);
        airCurrent = airEnd;
        addActor(fireStart);
        addActor(fireEnd);
        addActor(waterStart);
        addActor(waterEnd);
        addActor(earthStart);
        addActor(earthEnd);
        addActor(airStart);
        addActor(airEnd);
        stateMachine = new StateMachine(this);
        stateMachine.changeState(new Selection());
    }

    public void update(State state) {
        updateElement(state.fireStart(), fireStart);
        updateElement(state.fireEnd(), fireEnd);
        updateElement(state.waterStart(), waterStart);
        updateElement(state.waterEnd(), waterEnd);
        updateElement(state.earthStart(), earthStart);
        updateElement(state.earthEnd(), earthEnd);
        updateElement(state.airStart(), airStart);
        updateElement(state.airEnd(), airEnd);
        addStartingPositions(firePreviews, state.fireStartingPositions(), ElementType.FIRE);
        addStartingPositions(waterPreviews, state.waterStartingPositions(), ElementType.WATER);
        addStartingPositions(earthPreviews, state.earthStartingPositions(), ElementType.EARTH);
        addStartingPositions(airPreviews, state.airStartingPositions(), ElementType.AIR);
        for (Image visibleTile : visibleTiles) {
            visibleTile.remove();
        }
        visibleTiles.clear();
        state.tiles().forEach((position, current) -> {
            Image image = new Image(current.type().tileSelector.apply(textures));
            image.setPosition(position.x * TILE_SIZE, position.y * TILE_SIZE);
            addActor(image);
            visibleTiles.add(image);
            if (state.fireLast() == position) {
                fireCurrent = image;
            }
            if (state.waterLast() == position) {
                waterCurrent = image;
            }
            if (state.earthLast() == position) {
                earthCurrent = image;
            }
            if (state.airLast() == position) {
                airCurrent = image;
            }
        });
    }

    private void addStartingPositions(List<Image> previews, List<GridPoint2> startingPositions, ElementType type) {
        for (Image preview : previews) {
            preview.remove();
        }
        previews.clear();
        for (GridPoint2 position : startingPositions) {
            Image image = new Image(tile);
            image.setVisible(false);
            Color color = new Color(type.color);
            color.a = 0.5f;
            image.setColor(color);
            image.setPosition(position.x * TILE_SIZE, position.y * TILE_SIZE);
            addActor(image);
            previews.add(image);
        }
    }

    private void updateElement(Element element, Image elementImage) {
        if (element.visible()) {
            elementImage.setVisible(true);
            elementImage.setPosition(element.position().x * TILE_SIZE, element.position().y * TILE_SIZE);
        } else {
            elementImage.setVisible(false);
        }
    }

    private Image createButton(TextureRegion element) {
        final Image button;
        button = new Image(element);
        button.setVisible(false);
        return button;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateMachine.update();
    }

    private abstract static class BaseState implements com.badlogic.gdx.ai.fsm.State<Board> {

        @Override
        public void update(Board board) {}

        @Override
        public boolean onMessage(Board board, Telegram telegram) {
            return false;
        }
    }

    private static class Selection extends BaseState {

        private ClickListener fireListener;
        private ClickListener waterListener;
        private ClickListener earthListener;
        private ClickListener airListener;

        @Override
        public void enter(Board board) {
            fireListener = createListener(board, ElementType.FIRE);
            board.fireCurrent.addListener(fireListener);
            waterListener = createListener(board, ElementType.WATER);
            board.waterCurrent.addListener(waterListener);
            earthListener = createListener(board, ElementType.EARTH);
            board.earthCurrent.addListener(earthListener);
            airListener = createListener(board, ElementType.AIR);
            board.airCurrent.addListener(airListener);
        }

        private ClickListener createListener(Board board, final ElementType type) {
            return new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    board.stateMachine.changeState(new PlaceTile(type));
                }
            };
        }

        @Override
        public void exit(Board board) {
            board.fireCurrent.removeListener(fireListener);
            board.waterCurrent.removeListener(waterListener);
            board.earthCurrent.removeListener(earthListener);
            board.airCurrent.removeListener(airListener);
        }
    }

    private static class PlaceTile extends BaseState {

        private final Vector2 screenCoords = new Vector2();
        private final ElementType type;
        private ClickListener listener;

        public PlaceTile(ElementType type) {
            super();
            this.type = type;
        }

        @Override
        public void enter(Board board) {
            listener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getButton() != Input.Buttons.LEFT) {
                        return;
                    }
                    Actor target = event.getTarget();
                    Vector2 positionOnBoard = ElementPosition.positionOnBoard(
                            board.getStage().getViewport(), screenCoords.set(target.getX(), target.getY()));
                    board.listener.onElementPlaced(
                            type, new GridPoint2((int) positionOnBoard.x, (int) positionOnBoard.y));
                    goToSelection(board);
                }
            };
            for (Image preview : previews(board)) {
                preview.setVisible(true);
                preview.addListener(listener);
            }
        }

        private void goToSelection(Board board) {
            board.stateMachine.changeState(new Selection());
        }

        private List<Image> previews(Board board) {
            switch (type) {
                case FIRE:
                    return board.firePreviews;
                case WATER:
                    return board.waterPreviews;
                case EARTH:
                    return board.earthPreviews;
                case AIR:
                    return board.airPreviews;
                default:
                    throw new IllegalStateException("Element type unrecognized " + type);
            }
        }

        @Override
        public void update(Board board) {
            super.update(board);
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                goToSelection(board);
            }
        }

        @Override
        public void exit(Board board) {
            for (Image preview : previews(board)) {
                preview.setVisible(false);
                preview.removeListener(listener);
            }
        }
    }

    private static class StateMachine extends DefaultStateMachine<Board, BaseState> {
        public StateMachine(Board owner) {
            super(owner);
        }
    }

    public interface Listener {
        void onElementPlaced(ElementType type, GridPoint2 position);
    }
}
