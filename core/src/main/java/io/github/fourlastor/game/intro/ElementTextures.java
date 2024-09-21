package io.github.fourlastor.game.intro;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ElementTextures {
    public final TextureRegion fireElement;
    public final TextureRegion fireTile;
    public final TextureRegion waterElement;
    public final TextureRegion waterTile;
    public final TextureRegion earthElement;
    public final TextureRegion earthTile;
    public final TextureRegion airElement;
    public final TextureRegion airTile;

    public ElementTextures(
            TextureRegion fireElement,
            TextureRegion fireTile,
            TextureRegion waterElement,
            TextureRegion waterTile,
            TextureRegion earthElement,
            TextureRegion earthTile,
            TextureRegion airElement,
            TextureRegion airTile) {
        this.fireElement = fireElement;
        this.fireTile = fireTile;
        this.waterElement = waterElement;
        this.waterTile = waterTile;
        this.earthElement = earthElement;
        this.earthTile = earthTile;
        this.airElement = airElement;
        this.airTile = airTile;
    }
}
