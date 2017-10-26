package com.chair49.boats.game;


import com.badlogic.gdx.math.Vector2;

public class EnemyAI extends Boat {

    EnemyAI(Vector2 position, float rotation, int type) {
        super(position, rotation, type);
    }

    public Input wander(int[][] heights) {

        return new Input();
    }
}
