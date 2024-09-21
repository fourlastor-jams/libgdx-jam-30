package io.github.fourlastor.game.di.modules;

import dagger.Module;
import io.github.fourlastor.game.intro.IntroComponent;

@Module(
        subcomponents = {
            IntroComponent.class,
        })
public class ScreensModule {}
