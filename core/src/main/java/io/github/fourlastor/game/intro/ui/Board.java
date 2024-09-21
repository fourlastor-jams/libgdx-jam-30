package io.github.fourlastor.game.intro.ui;

import static io.github.fourlastor.game.intro.Config.TILE_SIZE;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
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
        fireEnd.setOrigin(Align.center);
        fireCurrent = fireEnd;
        waterStart = createButton(textures.waterElement);
        waterEnd = createButton(textures.waterTile);
        waterEnd.setOrigin(Align.center);
        waterCurrent = waterEnd;
        earthStart = createButton(textures.earthElement);
        earthEnd = createButton(textures.earthTile);
        earthEnd.setOrigin(Align.center);
        earthCurrent = earthEnd;
        airStart = createButton(textures.airElement);
        airEnd = createButton(textures.airTile);
        airEnd.setOrigin(Align.center);
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

    private static Action highlightAction() {
        return Actions.forever(Actions.sequence(
                Actions.scaleTo(1.05f, 1.05f, 0.2f, Interpolation.circleOut),
                Actions.delay(0.3f),
                Actions.scaleTo(1, 1, 0.1f, Interpolation.circleIn),
                Actions.delay(0.3f)));
    }

    public void update(State state) {
        fireCurrent.clearActions();
        waterCurrent.clearActions();
        earthCurrent.clearActions();
        airCurrent.clearActions();
        updateElement(state.fireStart(), fireStart);
        updateElement(state.fireEnd(), fireEnd);
        if (state.fireEnd().position().equals(state.fireLast())) {
            fireCurrent = fireEnd;
        }
        updateElement(state.waterStart(), waterStart);
        updateElement(state.waterEnd(), waterEnd);
        if (state.waterEnd().position().equals(state.waterLast())) {
            waterCurrent = waterEnd;
        }
        updateElement(state.earthStart(), earthStart);
        updateElement(state.earthEnd(), earthEnd);
        if (state.earthEnd().position().equals(state.earthLast())) {
            earthCurrent = earthEnd;
        }
        updateElement(state.airStart(), airStart);
        updateElement(state.airEnd(), airEnd);
        if (state.airEnd().position().equals(state.airLast())) {
            airCurrent = airEnd;
        }
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
            image.setOrigin(Align.center);
            addActor(image);
            visibleTiles.add(image);
            if (state.fireLast().equals(position)) {
                fireCurrent = image;
            }
            if (state.waterLast().equals(position)) {
                waterCurrent = image;
            }
            if (state.earthLast().equals(position)) {
                earthCurrent = image;
            }
            if (state.airLast().equals(position)) {
                airCurrent = image;
            }
        });
        fireCurrent.addAction(highlightAction());
        waterCurrent.addAction(highlightAction());
        earthCurrent.addAction(highlightAction());
        airCurrent.addAction(highlightAction());
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

        private ClickListener fireDeleteListener;
        private ClickListener waterDeleteListener;
        private ClickListener earthDeleteListener;
        private ClickListener airDeleteListener;
        private final Vector2 screenCoords = new Vector2();

        @Override
        public void enter(Board board) {
            fireListener = createListener(board, ElementType.FIRE);
            board.fireCurrent.addListener(fireListener);
            fireDeleteListener = deleteListener(board);
            board.fireCurrent.addListener(fireDeleteListener);
            waterListener = createListener(board, ElementType.WATER);
            board.waterCurrent.addListener(waterListener);
            waterDeleteListener = deleteListener(board);
            board.waterCurrent.addListener(waterDeleteListener);
            earthListener = createListener(board, ElementType.EARTH);
            board.earthCurrent.addListener(earthListener);
            earthDeleteListener = deleteListener(board);
            board.earthCurrent.addListener(earthDeleteListener);
            airListener = createListener(board, ElementType.AIR);
            board.airCurrent.addListener(airListener);
            airDeleteListener = deleteListener(board);
            board.airCurrent.addListener(airDeleteListener);
        }

        private ClickListener createListener(Board board, final ElementType type) {
            return new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    board.stateMachine.changeState(new PlaceTile(type));
                }
            };
        }

        private ClickListener deleteListener(Board board) {
            return new ClickListener(Input.Buttons.RIGHT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Actor target = event.getTarget();
                    Vector2 positionOnBoard = ElementPosition.positionOnBoard(
                            board.getStage().getViewport(), screenCoords.set(target.getX(), target.getY()));
                    board.listener.onElementRemoved(new GridPoint2((int) positionOnBoard.x, (int) positionOnBoard.y));
                    board.stateMachine.changeState(new Selection());
                }
            };
        }

        @Override
        public void exit(Board board) {
            board.fireCurrent.removeListener(fireListener);
            board.fireCurrent.removeListener(fireDeleteListener);
            board.waterCurrent.removeListener(waterListener);
            board.waterCurrent.removeListener(waterDeleteListener);
            board.earthCurrent.removeListener(earthListener);
            board.earthCurrent.removeListener(earthDeleteListener);
            board.airCurrent.removeListener(airListener);
            board.airCurrent.removeListener(airDeleteListener);
        }
    }

    private static class PlaceTile extends BaseState {

        private final Vector2 screenCoords = new Vector2();
        private final ElementType type;
        private ClickListener listener;
        private ClickListener cancelListener;

        public PlaceTile(ElementType type) {
            super();
            this.type = type;
        }

        @Override
        public void enter(Board board) {
            listener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
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
            cancelListener = new ClickListener(Input.Buttons.RIGHT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    goToSelection(board);
                }
            };
            board.addListener(cancelListener);
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
        public void exit(Board board) {
            for (Image preview : previews(board)) {
                preview.setVisible(false);
                preview.removeListener(listener);
            }
            board.removeListener(cancelListener);
        }
    }

    private static class StateMachine extends DefaultStateMachine<Board, BaseState> {
        public StateMachine(Board owner) {
            super(owner);
        }
    }

    public interface Listener {
        void onElementPlaced(ElementType type, GridPoint2 position);

        void onElementRemoved(GridPoint2 position);
    }
}
