package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen implements Screen {
    private static final float SPLASH_DURATION = 5f; // duração do splash em segundos
    private final Game game;
    private final GameScreen gameScreen;
    private SpriteBatch spriteBatch;
    private Texture splashImage;
    private float elapsedTime = 0;
    private SplashScreenListener listener;

    public SplashScreen(Game game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }
    public void setListener(SplashScreenListener listener) {
        this.listener = listener;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        splashImage = new Texture(Gdx.files.internal("splash.png"));
        Gdx.app.log("SplashScreen", "Splash screen displayed.");
    }

    private static final float MIN_RENDER_INTERVAL = 1 / 60f; // intervalo mínimo entre as chamadas do método render

    @Override
    public void render(float delta) {
        // Verificando se já se passou tempo suficiente desde a última chamada do método render
        if (delta < MIN_RENDER_INTERVAL) {
            return;
        }

        // Limpando a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenhando a imagem de splash screen
        spriteBatch.begin();
        spriteBatch.draw(splashImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        // Adicionando o tempo que passou desde a última chamada do método render a elapsedTime
        elapsedTime += delta;

        // Adicionando o tempo total
        float totalTime = elapsedTime;

        // Verificando se o tempo de exibição da tela de splash screen acabou
        if (totalTime >= SPLASH_DURATION) {
            // Mudando para a próxima tela
            game.setScreen(gameScreen);

            // Dispose da tela de splash screen
            dispose();
        }
    }
    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    private void notifySplashScreenFinished() {
        if (listener != null) {
            listener.onSplashScreenFinished();
        }
    }
    public interface SplashScreenListener {
        void onSplashScreenFinished();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        splashImage.dispose();
        notifySplashScreenFinished();
    }
}
