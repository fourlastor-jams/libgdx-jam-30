package io.github.fourlastor.game.intro.ui;

import static io.github.fourlastor.game.intro.Config.TILE_SIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.fourlastor.game.intro.Config;

public class Palette extends WidgetGroup {

    private final Image fireButton;
    private final Image waterButton;
    private final Image earthButton;
    private final Image airButton;
    private final Image elementPreview;
    private final StateMachine stateMachine;

    public Palette(TextureRegion element) {
        setFillParent(true);
        Table table = new Table();
        table.setFillParent(true);
        fireButton = createButton(element, Color.CORAL);
        waterButton = createButton(element, Color.CYAN);
        earthButton = createButton(element, Color.BROWN);
        airButton = createButton(element, Color.WHITE);
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

    private Image createButton(TextureRegion element, Color color) {
        final Image button;
        button = new Image(element);
        button.setColor(color);
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
            fireListener = createListener(palette, Color.CORAL);
            palette.fireButton.addListener(fireListener);
            waterListener = createListener(palette, Color.CYAN);
            palette.waterButton.addListener(waterListener);
            earthListener = createListener(palette, Color.BROWN);
            palette.earthButton.addListener(earthListener);
            airListener = createListener(palette, Color.WHITE);
            palette.airButton.addListener(airListener);
        }

        private ClickListener createListener(Palette palette, final Color color) {
            return new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    palette.stateMachine.changeState(new PlaceElement(color));
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
        private ClickListener listener;

        public PlaceElement(Color color) {
            super();
            this.color = new Color(color);
            this.color.a = 0.5f;
        }

        @Override
        public void enter(Palette palette) {
            Image elementPreview = palette.elementPreview;
            elementPreview.setColor(color);
            elementPreview.setVisible(true);
            listener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    palette.stateMachine.changeState(new Selection());
                }
            };
            palette.addListener(listener);
        }

        @Override
        public void update(Palette palette) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            Vector2 unprojected = palette.getStage()
                    .getViewport()
                    .unproject(screenCoords.set(x, y))
                    .scl(1f / TILE_SIZE);
            unprojected
                    .set(
                            MathUtils.clamp(MathUtils.floor(unprojected.x), 0f, Config.TILE_COUNT - 1),
                            MathUtils.clamp(MathUtils.floor(unprojected.y), 0, Config.TILE_COUNT - 1))
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
}
