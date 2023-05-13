package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen {
    private final Game game;

    public GameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Inicialize os objetos do jogo aqui
    }

    @Override
    public void render(float delta) {
        // Desenhe o jogo aqui
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

    // Outros métodos da interface Screen
}
