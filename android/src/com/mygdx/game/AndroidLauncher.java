package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	private GameScreen gameScreen;
	private SplashScreen splashScreen;
	private MyGdxGame myGdxGame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;

		myGdxGame = new MyGdxGame();
		gameScreen = new GameScreen(myGdxGame);
		splashScreen = new SplashScreen(myGdxGame, gameScreen);

		//myGdxGame.showSplashScreen();
		initialize(myGdxGame, config);


	}
}
