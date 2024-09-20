package io.github.fourlastor.game.intro.ui;

import static io.github.fourlastor.game.intro.Config.TILE_SIZE;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.fourlastor.game.intro.state.ElementType;

public class Palette extends WidgetGroup {

    private final Image fireButton;
    private final Image waterButton;
    private final Image earthButton;
    private final Image airButton;
    private final Image elementPreview;
    private final StateMachine stateMachine;
    private final Listener listener;

    public Palette(TextureRegion element, Listener listener) {
        this.listener = listener;
        setFillParent(true);
        Table table = new Table();
        table.setFillParent(true);
        fireButton = createButton(element, ElementType.FIRE);
        waterButton = createButton(element, ElementType.WATER);
        earthButton = createButton(element, ElementType.EARTH);
        airButton = createButton(element, ElementType.AIR);
        table.padTop(15);
        table.add(fireButton).expand().top();
        table.add(waterButton).expand().top();
        table.add(earthButton).expand().top();
        table.add(airButton).expand().top();
        elementPreview = new Image(element);
        elementPreview.setVisible(false);
        addActor(table);
        addActor(elementPreview);
        stateMachine = new StateMachine(this);
        stateMachine.changeState(new Selection());
    }

    private Image createButton(TextureRegion element, ElementType type) {
        final Image button;
        button = new Image(element);
        button.setColor(type.color);
        return button;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateMachine.update();
    }

    private abstract static class State implements com.badlogic.gdx.ai.fsm.State<Palette> {

        @Override
        public void update(Palette palette) {}

        @Override
        public boolean onMessage(Palette palette, Telegram telegram) {
            return false;
        }
    }

    private static class Selection extends Palette.State {

        private ClickListener fireListener;
        private ClickListener waterListener;
        private ClickListener earthListener;
        private ClickListener airListener;

        @Override
        public void enter(Palette palette) {
            palette.setTouchable(Touchable.childrenOnly);
            fireListener = createListener(palette, ElementType.FIRE);
            palette.fireButton.addListener(fireListener);
            waterListener = createListener(palette, ElementType.WATER);
            palette.waterButton.addListener(waterListener);
            earthListener = createListener(palette, ElementType.EARTH);
            palette.earthButton.addListener(earthListener);
            airListener = createListener(palette, ElementType.AIR);
            palette.airButton.addListener(airListener);
        }

        private ClickListener createListener(Palette palette, final ElementType type) {
            return new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    palette.stateMachine.changeState(new PlaceElement(type));
                }
            };
        }

        @Override
        public void exit(Palette palette) {
            palette.fireButton.removeListener(fireListener);
            palette.waterButton.removeListener(waterListener);
            palette.earthButton.removeListener(earthListener);
            palette.airButton.removeListener(airListener);
        }
    }

    private static class PlaceElement extends Palette.State {

        private final Vector2 screenCoords = new Vector2();
        private final Color color;
        private final ElementType type;
        private ClickListener listener;

        public PlaceElement(ElementType type) {
            super();
            this.type = type;
            this.color = new Color(type.color);
            this.color.a = 0.5f;
        }

        @Override
        public void enter(Palette palette) {
            palette.setTouchable(Touchable.enabled);
            Image elementPreview = palette.elementPreview;
            elementPreview.setColor(color);
            elementPreview.setVisible(true);
            listener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    palette.stateMachine.changeState(new Selection());
                    Vector2 positionOnBoard =
                            ElementPosition.positionOnBoard(palette.getStage().getViewport(), screenCoords);
                    palette.listener.onElementPlaced(
                            type, new GridPoint2((int) positionOnBoard.x, (int) positionOnBoard.y));
                }
            };
            palette.addListener(listener);
        }

        @Override
        public void update(Palette palette) {
            Vector2 unprojected = ElementPosition.positionOnBoard(
                            palette.getStage().getViewport(), screenCoords)
                    .scl(TILE_SIZE);
            palette.elementPreview.setPosition(unprojected.x, unprojected.y);
        }

        @Override
        public void exit(Palette palette) {
            palette.removeListener(listener);
            palette.elementPreview.setVisible(false);
        }
    }

    private static class StateMachine extends DefaultStateMachine<Palette, Palette.State> {
        public StateMachine(Palette owner) {
            super(owner);
        }
    }

    public interface Listener {
        void onElementPlaced(ElementType type, GridPoint2 position);
    }
}
