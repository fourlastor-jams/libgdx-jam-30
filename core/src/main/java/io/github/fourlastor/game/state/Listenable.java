package io.github.fourlastor.game.state;

import java.util.function.Consumer;

public interface Listenable<State> {
    void listen(Consumer<State> consumer);
}
