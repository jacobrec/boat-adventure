package com.chair49.boats.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import static com.chair49.boats.game.Boat.*;

public class World {
    Boat player;
    int gold = 0;

    int size = (int) Math.pow(2, 9);
    public int[][] map;
    public int[][] treasuremap;

    List<Vector2> crew;
    String[] messages;

    Message currentMessage;

    boolean isPlaying = true;
    List<Cannonball> cannonballs;
    long timeoflastshot = 0;

    List<EnemyAI> enemies;

    Button buyScout;
    Button buyTransporter;
    Button buyWarShip;
    Button buySpeedBoat;


    World() {
        FileHandle file = Gdx.files.internal("messages.txt");
        messages = file.readString().split("\n");

        this.player = new Boat(new Vector2(size / 2, size / 2), 0, Boat.TYPE_CHEAP);
        this.map = WorldGenerator.generatePatch(size, size, 8);
        crew = new ArrayList<Vector2>();
        cannonballs = new ArrayList<Cannonball>();
        treasuremap = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                treasuremap[r][c] = 0;
                if (this.map[r][c] == 171) {
                    if (crew.size() < 20)
                        crew.add(new Vector2(r, c));
                } else if (this.map[r][c] == 67) {
                    treasuremap[r][c] = 8 + (int) (Math.random() * 5);
                } else if (this.map[r][c] == 49) {
                    treasuremap[r][c] = 80 + (int) (Math.random() * 50);
                }

            }
        }

        buyScout = new Button((Gdx.graphics.getWidth() - 1000) / 2, 300, 100, 1000, "Scout " + Boat.TYPE_SCOUT + " Gold");
        buyTransporter = new Button((Gdx.graphics.getWidth() - 1000) / 2, 450, 100, 1000, "Transporter " + Boat.TYPE_TRANSPORT + " Gold");
        buySpeedBoat = new Button((Gdx.graphics.getWidth() - 1000) / 2, 600, 100, 1000, "Speedboat " + Boat.TYPE_SPEED + " Gold");
        buyWarShip = new Button((Gdx.graphics.getWidth() - 1000) / 2, 750, 100, 1000, "Warship " + Boat.TYPE_WAR + " Gold");

    }

    void update(float delta, Input input) {
        if (input.isFire())
            isPlaying = true;
        if (isPlaying) {
            controlPlayer(player, input, delta);
            if (currentMessage != null)
                currentMessage.type -= delta;
        }
        for (int i = cannonballs.size() - 1; i >= 0; i--) {
            if (cannonballs.get(i).explode) {
                cannonballs.remove(i);
                continue;
            }
            cannonballs.get(i).update(delta);
        }
        if (player.isStopped) {
            if (Gdx.input.isTouched()) {
                if (buyScout.isPointInBounds(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                    this.buyShip(Boat.TYPE_SCOUT);
                }
                if (buyTransporter.isPointInBounds(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                    this.buyShip(Boat.TYPE_TRANSPORT);
                }
                if (buySpeedBoat.isPointInBounds(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                    this.buyShip(Boat.TYPE_SPEED);
                }
                if (buyWarShip.isPointInBounds(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                    this.buyShip(Boat.TYPE_WAR);
                }
            }
        }
//        for (EnemyAI e : enemies) {
//            e.wander(getWorldSlice(e.position));
//        }

    }

    private int[][] getWorldSlice(Vector2 position) {
        int pr = Math.round(position.y);
        int pc = Math.round(position.x);
        int[][] heights = new int[100][100];
        for (int r = pr - 49; r < pr + 50; r++) {
            for (int c = pc - 49; c < pc + 50; c++) {
                int val = -1;
                if(Math.sqrt(Math.pow(c-pc,2)+Math.pow(r-pr,2))<50){
                    try{
                        val = map[Math.round(player.position.x)][Math.round(player.position.y)];
                    }catch (ArrayIndexOutOfBoundsException e){
                        //this should be empty, when the array is out of bounds val should be -1
                    }
                }
                heights[r-(pr - 49)][c-(pc - 49)] = val;
            }
        }
        return heights;
    }

    private void controlPlayer(Boat boat, Input in, float delta) {
        if (!boat.isStopped) {
            if (boat.grounded) {
                boat.velocity.setZero();
                if (in.isSpeed()) {
                    boat.velocity.y = 1f;
                } else if (in.isSlow()) {
                    boat.velocity.y = -1f;
                }
                if (in.isLeft()) {
                    boat.velocity.x = -1f;
                } else if (in.isRight()) {
                    boat.velocity.x = 1f;
                }
            } else {
                player.sail(in, delta);

            }
            if (in.isFire()) {
                if (System.currentTimeMillis() > timeoflastshot + 750) {
                    cannonballs.add(new Cannonball(player.position.cpy(), player.velocity.cpy().scl(2)));
                    timeoflastshot = System.currentTimeMillis();
                }
            }
        } else {
            if (!(player.velocity.len() < 1e-15)) {//XXX: Fixes bugs where upon un-pausing would either not let the player move send its velocity and position to nan, nan and be eaten by sea monsters
                boat.velocity.scl(0.9f);
            }

        }
        if (in.isStop())
            boat.isStopped = true;
        if (in.isSpeed()) {
            boat.isStopped = false;
            buylock = true;
            if (currentMessage != null)
                currentMessage.fullscreen = false;
        }

        try {
            if ((map[Math.round(player.position.x)][Math.round(player.position.y)] > 137) && !boat.grounded) {
                if (!in.isSlow()) {
                    player.alive = false;
                    player.causeOfDeath = Boat.CauseOfDeath.CRASHING;
                }
                in.clear();
                currentMessage = new Message("On the Ground", Message.SUBTLE_SHORT);
                boat.grounded = true;

            }
            if (!(map[Math.round(player.position.x)][Math.round(player.position.y)] > 130) && boat.grounded) {
                in.clear();
                currentMessage = new Message("In the Water", Message.SUBTLE_SHORT);
                boat.grounded = false;
            }

            if (treasuremap[Math.round(player.position.x)][Math.round(player.position.y)] != 0) {
                gold += treasuremap[Math.round(player.position.x)][Math.round(player.position.y)];
                currentMessage = new Message("You found " + treasuremap[Math.round(player.position.x)][Math.round(player.position.y)] + " gold!", Message.SUBTLE_SHORT);
                treasuremap[Math.round(player.position.x)][Math.round(player.position.y)] = 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            player.alive = false;
            player.causeOfDeath = Boat.CauseOfDeath.SEAMONSTERS;
        }
        if (currentMessage == null || !currentMessage.fullscreen)
            player.move(delta);
        for (int i = crew.size() - 1; i >= 0; i--) {
            if (crew.get(i).x == Math.round(player.position.x) && crew.get(i).y == Math.round(player.position.y)) {
                crew.remove(i);
                currentMessage = new Message(messages[crew.size()], Message.FULLSCREEN);
                in.clear();
            }
        }
    }

    boolean buylock = true;

    void buyShip(int type) {
        if (buylock) {
            switch (type) {
                case TYPE_CHEAP:
                    break;
                case TYPE_SCOUT:
                    if (gold < TYPE_SCOUT)
                        currentMessage = new Message("Sorry, you can't afford this", Message.SUBTLE_SHORT);
                    else {
                        gold -= TYPE_SCOUT;
                        player.changeType(TYPE_SCOUT);
                        currentMessage = new Message("You bought a scout boat!!", Message.SUBTLE_SHORT);
                        buylock = false;
                    }
                    break;
                case TYPE_SPEED:
                    if (gold < TYPE_SPEED)
                        currentMessage = new Message("Sorry, you can't afford this", Message.SUBTLE_SHORT);
                    else {
                        gold -= TYPE_SPEED;
                        player.changeType(TYPE_SPEED);
                        currentMessage = new Message("You bought a speed boat!!", Message.SUBTLE_SHORT);
                        buylock = false;
                    }
                    break;
                case TYPE_TRANSPORT:
                    if (gold < TYPE_TRANSPORT)
                        currentMessage = new Message("Sorry, you can't afford this", Message.SUBTLE_SHORT);
                    else {
                        gold -= TYPE_TRANSPORT;
                        player.changeType(TYPE_TRANSPORT);
                        currentMessage = new Message("You bought a transport boat!!", Message.SUBTLE_SHORT);
                        buylock = false;
                    }
                    break;
                case TYPE_WAR:
                    if (gold < TYPE_WAR)
                        currentMessage = new Message("Sorry, you can't afford this", Message.SUBTLE_SHORT);
                    else {
                        gold -= TYPE_WAR;
                        player.changeType(TYPE_WAR);
                        currentMessage = new Message("You bought a warship!!", Message.SUBTLE_SHORT);
                        buylock = false;
                    }
                    break;
                case TYPE_GOD:
                    break;

            }
        }
    }
}

class Message {
    String message;
    float type;
    public final static int FULLSCREEN = -99;
    public final static int SUBTLE_SHORT = 3;
    public final static int SUBTLE_LONG = 5;
    public boolean fullscreen;

    public Message(String message, float type) {
        this.message = message;
        this.type = type;
        fullscreen = type == FULLSCREEN;
    }
}

class Cannonball {
    Vector3 pos;
    Vector3 vel;
    boolean explode;

    public Cannonball(Vector2 pos, Vector2 vel) {
        this.pos = new Vector3(pos.cpy(), 0);
        this.vel = new Vector3(vel.cpy(), 1);
        this.explode = false;
    }

    public void update(float delta) {
        this.vel.z -= 2 * delta;
        this.pos.add(vel.cpy().scl(delta));
        explode = this.pos.z <= 0;
    }

}

