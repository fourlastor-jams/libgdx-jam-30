package io.github.fourlastor.game.intro.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import io.github.fourlastor.game.intro.Config;
import io.github.fourlastor.game.intro.state.Element;
import io.github.fourlastor.game.intro.state.ElementType;
import io.github.fourlastor.game.intro.state.State;

public class Board extends WidgetGroup {

    private final Image fireStart;
    private final Image fireEnd;
    private final Image waterStart;
    private final Image waterEnd;
    private final Image earthStart;
    private final Image earthEnd;
    private final Image airStart;
    private final Image airEnd;

    public Board(TextureRegion element, TextureRegion tile) {
        setFillParent(true);
        fireStart = createButton(element, ElementType.FIRE);
        fireEnd = createButton(tile, ElementType.FIRE);
        waterStart = createButton(element, ElementType.WATER);
        waterEnd = createButton(tile, ElementType.WATER);
        earthStart = createButton(element, ElementType.EARTH);
        earthEnd = createButton(tile, ElementType.EARTH);
        airStart = createButton(element, ElementType.AIR);
        airEnd = createButton(tile, ElementType.AIR);
        addActor(fireStart);
        addActor(fireEnd);
        addActor(waterStart);
        addActor(waterEnd);
        addActor(earthStart);
        addActor(earthEnd);
        addActor(airStart);
        addActor(airEnd);
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
    }

    private void updateElement(Element element, Image elementImage) {
        if (element.visible()) {
            elementImage.setVisible(true);
            elementImage.setPosition(element.position().x * Config.TILE_SIZE, element.position().y * Config.TILE_SIZE);
        } else {
            elementImage.setVisible(false);
        }
    }

    private Image createButton(TextureRegion element, ElementType type) {
        final Image button;
        button = new Image(element);
        button.setColor(type.color);
        button.setVisible(false);
        return button;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
