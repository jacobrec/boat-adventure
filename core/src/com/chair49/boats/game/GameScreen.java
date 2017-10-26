package com.chair49.boats.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.chair49.boats.MyGame;

public class GameScreen implements Screen, InputProcessor {
    MyGame gdx;

    World world;
    GameRenderer renderer;
    Camera cam;

    Input input;
    Music music;

    public GameScreen(MyGame gdx) {
        this.gdx = gdx;
        Gdx.input.setInputProcessor(this);
        cam = new OrthographicCamera(16, 9);
        this.world = new World();
        this.renderer = new GameRenderer(cam, world);
        this.input = new Input();

    }

    @Override
    public void show() {
        music = Gdx.audio.newMusic(Gdx.files.internal("pirateCCbySA.ogg"));
        music.setLooping(true);
        music.play();

    }

    @Override
    public void render(float delta) {
        world.update(delta, input);
        renderer.render(world);
        if (!world.player.alive){
            if(input.isFire()){
                this.world = new World();
                input.clear();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return input.key(keycode, true);
    }

    @Override
    public boolean keyUp(int keycode) {
        return input.key(keycode, false);
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

class Input {
    private boolean left, right, speed, slow, fire, stop;

    public Input() {
        clear();
    }

    public boolean isLeft() {
        return left && !right;
    }

    public boolean isRight() {
        return right && !left;
    }

    public boolean isSpeed() {
        return speed && !slow;
    }

    public boolean isSlow() {
        return slow && !speed;
    }

    public boolean isFire() {
        return fire;
    }

    public boolean isStop() {
        return stop;
    }


    public boolean key(int keycode, boolean isKeyDown) {
        switch (keycode) {
            case Keys.S:
                this.stop = isKeyDown;
                break;
            case Keys.SPACE:
                this.fire = isKeyDown;
                return true;
            case Keys.LEFT:
                this.left = isKeyDown;
                return true;
            case Keys.RIGHT:
                this.right = isKeyDown;
                return true;
            case Keys.UP:
                this.speed = isKeyDown;
                return true;
            case Keys.DOWN:
                this.slow = isKeyDown;
                return true;

        }
        return false;
    }

    public void clear() {
        this.left = false;
        this.right = false;
        this.speed = false;
        this.slow = false;
        this.fire = false;
        this.stop = false;
    }
}