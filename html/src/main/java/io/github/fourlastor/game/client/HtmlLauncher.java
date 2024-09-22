package io.github.fourlastor.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import io.github.fourlastor.game.GdxGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
                config.padHorizontal = 0;
                config.padVertical = 0;
                return config;
                // Fixed size application:
                //return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public Preloader.PreloaderCallback getPreloaderCallback() {
                return createPreloaderPanel(GWT.getHostPageBaseURL() + "preloadlogo.png");
        }

        @Override
        protected void adjustMeterPanel(Panel meterPanel, Style meterStyle) {
                meterPanel.setStyleName("gdx-meter");
                meterPanel.addStyleName("nostripes");
                meterStyle.setProperty("backgroundColor", "#281828");
                meterStyle.setProperty("backgroundImage", "none");
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return GdxGame.createGame();
        }
}
