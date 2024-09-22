package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class AssetsModule {

    private static final String PATH_TEXTURE_ATLAS = "images/packed/images.pack.atlas";
    public static final String WHITE_PIXEL = "white-pixel";
    public static final String MUSIC_PATH = "audio/music/thesatyrsdance.mp3";
    public static final String FIRE_PATH = "audio/sounds/fire.ogg";
    public static final String WATER_PATH = "audio/sounds/water.ogg";
    public static final String EARTH_PATH = "audio/sounds/earth.ogg";
    public static final String AIR_PATH = "audio/sounds/air.ogg";
    public static final String DELETE_PATH = "audio/sounds/delete.ogg";
    public static final String CONNECT_PATH = "audio/sounds/connect.ogg";
    public static final String CLICK_PATH = "audio/sounds/click.ogg";
    public static final String ABORT_PATH = "audio/sounds/abort.ogg";

    @Provides
    @Singleton
    public AssetManager assetManager() {
        AssetManager assetManager = new AssetManager();
        assetManager.load(PATH_TEXTURE_ATLAS, TextureAtlas.class);
        assetManager.load(FIRE_PATH, Sound.class);
        assetManager.load(WATER_PATH, Sound.class);
        assetManager.load(EARTH_PATH, Sound.class);
        assetManager.load(AIR_PATH, Sound.class);
        assetManager.load(DELETE_PATH, Sound.class);
        assetManager.load(CONNECT_PATH, Sound.class);
        assetManager.load(CLICK_PATH, Sound.class);
        assetManager.load(ABORT_PATH, Sound.class);
        assetManager.finishLoading();
        return assetManager;
    }

    @Provides
    @Singleton
    public TextureAtlas textureAtlas(AssetManager assetManager) {
        return assetManager.get(PATH_TEXTURE_ATLAS, TextureAtlas.class);
    }

    @Provides
    @Singleton
    @Named(WHITE_PIXEL)
    public TextureRegion whitePixel(TextureAtlas atlas) {
        return atlas.findRegion("whitePixel");
    }
}
