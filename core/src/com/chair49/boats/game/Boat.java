package com.chair49.boats.game;


import com.badlogic.gdx.math.Vector2;

public class Boat {
    static final int TYPE_CHEAP = 0;
    static final int TYPE_SCOUT = 500;
    static final int TYPE_WAR = 5000;
    static final int TYPE_TRANSPORT = 1000;
    static final int TYPE_SPEED = 3000;
    static final int TYPE_GOD = 5;
    public CauseOfDeath causeOfDeath = CauseOfDeath.NONE;
    public String typename;

    public void sail(Input in, float delta) {
        if (in.isLeft()) {
            this.velocity.rotate(45 * this.agility * delta);
        } else if (in.isRight()) {
            this.velocity.rotate(-45 * this.agility * delta);
        }
        if (in.isSpeed()) {
            this.velocity.setLength(this.speed + this.agility);
        } else if (in.isSlow()) {
            this.velocity.setLength(this.speed - this.agility);
        } else {
            this.velocity.setLength(this.speed);
        }
    }


    public enum CauseOfDeath{
        SEAMONSTERS,CRASHING,NONE
    }


    Vector2 velocity;
    Vector2 position;
    Vector2 size;
    Vector2 person_size = new Vector2(0.2f, 0.25f);

    float speed;
    float agility;
    public boolean isStopped = false;
    public boolean grounded = false;
    public boolean alive = true;

    Boat(Vector2 position, float rotation, int type) {
        this.position = position;
        this.velocity = new Vector2(1, 0);
        this.velocity.setAngle(rotation);
        changeType(type);
    }

    void move(float delta) {
        position.add(velocity.cpy().scl(delta));

    }


    public void changeType(int type) {
        switch (type) {
            case TYPE_CHEAP:
                speed = 4f;
                agility = 1f;
                size = new Vector2(0.6f, 0.2f);
                typename = "Isaac's";
                break;
            case TYPE_SCOUT:
                speed = 6f;
                agility = 4f;
                size = new Vector2(0.3f, 0.14f);
                typename = "Scout";
                break;
            case TYPE_SPEED:
                speed = 15f;
                agility = 4f;
                size = new Vector2(0.3f, 0.14f);
                typename = "Speed";
                break;
            case TYPE_TRANSPORT:
                speed = 8f;
                agility = 1f;
                size = new Vector2(0.8f, 0.4f);
                typename = "Transport";
                break;
            case TYPE_WAR:
                speed = 9f;
                agility = 6f;
                size = new Vector2(0.5f, 0.2f);
                typename = "War";

                break;
            case TYPE_GOD:
                speed = 18f;
                agility = 12f;
                size = new Vector2(0.5f, 0.2f);
                typename = "Jacob's";
                break;

        }
    }
}
