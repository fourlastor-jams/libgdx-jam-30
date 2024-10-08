package io.github.fourlastor.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.di.GameComponent;
import io.github.fourlastor.game.di.modules.AssetsModule;
import io.github.fourlastor.game.intro.IntroComponent;
import io.github.fourlastor.game.route.Router;
import io.github.fourlastor.harlequin.Harlequin;
import io.github.fourlastor.perceptual.Perceptual;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GdxGame extends Game implements Router {

    private final InputMultiplexer multiplexer;

    private final IntroComponent.Builder introScreenFactory;
    private Screen pendingScreen = null;

    @Inject
    public GdxGame(InputMultiplexer multiplexer, IntroComponent.Builder introScreenFactory) {
        this.multiplexer = multiplexer;
        this.introScreenFactory = introScreenFactory;
        Harlequin.LIST_CREATOR = new Harlequin.ListCreator() {
            @Override
            public <T> List<T> newList() {
                return new ObjectList<>();
            }

            @Override
            public <T> List<T> newList(int size) {
                return new ObjectList<>(size);
            }
        };
    }

    @Override
    public void create() {
        //        if (Gdx.app.getType() != Application.ApplicationType.Android) {
        //
        //            Cursor customCursor =
        //                    Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("images/included/whitePixel.png")),
        // 0, 0);
        //            Gdx.graphics.setCursor(customCursor);
        //        }
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Music music = Gdx.audio.newMusic(Gdx.files.internal(AssetsModule.MUSIC_PATH));
        music.setVolume(Perceptual.perceptualToAmplitude(0.4f));
        music.setLooping(true);
        music.play();
        goToIntro();
    }

    @Override
    public void render() {
        if (pendingScreen != null) {
            setScreen(pendingScreen);
            pendingScreen = null;
        }
        super.render();
    }

    public static GdxGame createGame() {
        return GameComponent.component().game();
    }

    @Override
    public void goToIntro() {
        pendingScreen = introScreenFactory.router(this).build().screen();
    }
}
