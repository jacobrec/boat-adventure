package com.chair49.boats;

import com.badlogic.gdx.Game;
import com.chair49.boats.game.GameScreen;

public class MyGame extends Game {

	@Override
	public void create () {
		this.setScreen(new GameScreen(this));
	}

}
