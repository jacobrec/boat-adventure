package com.chair49.boats.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

public class GameRenderer {
    SpriteBatch batch;
    SpriteBatch hudbatch;
    SpriteBatch pixelbatch;

    ShapeRenderer shapes;

    Camera cam;

    TextureRegion background;
    TextureRegion boat;
    TextureRegion explosion;
    TextureRegion person;
    TextureRegion blackpixel;
    TextureRegion cannonball;


    Pixmap black;

    float drawsize = 128;
    float minimapsize = 32;

    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("pix.ttf"));
    BitmapFont font12;



    public GameRenderer(Camera cam, World world) {
        font12 = generator.generateFont(new FreeTypeFontGenerator.FreeTypeFontParameter() {{
            this.size = 45;
        }});
        generator.dispose();

        this.cam = cam;
        this.batch = new SpriteBatch();
        this.hudbatch = new SpriteBatch();
        this.pixelbatch = new SpriteBatch();
        this.shapes = new ShapeRenderer();

        this.hudbatch.setProjectionMatrix(cam.combined);
        this.batch.setProjectionMatrix(cam.combined);

        boat = new TextureRegion(new Texture(Gdx.files.internal("boat.png")));
        explosion = new TextureRegion(new Texture(Gdx.files.internal("explosion.png")));
        person = new TextureRegion(new Texture(Gdx.files.internal("person.png")));
        blackpixel = new TextureRegion(new Texture(Gdx.files.internal("blackpixel.png")));
        cannonball = new TextureRegion(new Texture(Gdx.files.internal("ball.png")));

        background = new TextureRegion(new Texture(WorldGenerator.getPixmap(world.map)));
        black = new Pixmap((int) minimapsize, (int) minimapsize, Pixmap.Format.RGBA8888);
        black.setColor(Color.BLACK);
        black.setBlending(Pixmap.Blending.None);
        black.fill();



    }

    void render(World world) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (world.player.alive) {
            cam.position.set(MathUtils.clamp(world.player.position.cpy().x * (drawsize / world.size), 8, drawsize - 8), MathUtils.clamp(world.player.position.cpy().y * (drawsize / world.size), 4.5f, drawsize - 4.5f), 0);
            cam.update();
            batch.setProjectionMatrix(cam.combined);

            black.setColor(Color.CLEAR);
            black.fillCircle((int) ((world.player.position.x - 8) * minimapsize / world.size), (int) ((world.size - (world.player.position.y + 8)) * minimapsize / world.size), (int) (8 * minimapsize / world.size));

            batch.begin();
            batch.draw(background, 0, 0, drawsize, drawsize);
            for (int i = 0; i < world.crew.size(); i++) {
                batch.draw(person, world.crew.get(i).x * drawsize / world.size, world.crew.get(i).y * drawsize / world.size - drawsize / world.size, drawsize / world.size, drawsize / world.size);
            }
            batch.draw(world.player.grounded ? person : boat, world.player.position.x * drawsize / world.size, world.player.position.y * drawsize / world.size - drawsize / world.size, (world.player.grounded ? world.player.person_size.x : world.player.size.x) / 2, (world.player.grounded ? world.player.person_size.y : world.player.size.y) / 2, world.player.grounded ? world.player.person_size.x : world.player.size.x, world.player.grounded ? world.player.person_size.y : world.player.size.y, 1, 1, world.player.grounded ? 90 : world.player.velocity.angle(), true);
            for(Cannonball ball : world.cannonballs){
                batch.draw(ball.explode?explosion:cannonball, ball.pos.x * drawsize / world.size+world.player.size.x/2, ball.pos.y * drawsize / world.size - drawsize / world.size, (world.player.size.x) / 2, (world.player.size.y) / 2, ball.explode?0.3f:0.1f, ball.explode?0.3f:0.1f, 1, 1, 0, true);
            }
            batch.end();

            hudbatch.begin();
            hudbatch.draw(background, -8, 2.5f, 2, 2);
            hudbatch.draw(new TextureRegion(new Texture(black)), -8, 2.5f, 2, 2);
            hudbatch.draw(explosion, -8 + world.player.position.x / (world.size) * 2 - 0.05f, 2.5f + world.player.position.y / (world.size) * 2, 0.1f, 0.1f);

            hudbatch.end();

            int t = 0;
            float n = 0;
            for (int r = 0; r < minimapsize; r++) {
                for (int c = 0; c < minimapsize; c++) {
                    if (black.getPixel(r, c) == Color.CLEAR.toIntBits())
                        n++;
                    t++;
                }
            }

            pixelbatch.begin();
            font12.draw(pixelbatch, "Map completed: " + (int) Math.floor(n / t * 100) + "%", Gdx.graphics.getWidth() * 2 / 3, Gdx.graphics.getHeight());
            font12.draw(pixelbatch, "Crew Members Remaining: " + world.crew.size(), Gdx.graphics.getWidth() * 1 / 7, Gdx.graphics.getHeight());
            font12.draw(pixelbatch, "Gold: " + world.gold, Gdx.graphics.getWidth() * 5 / 7, Gdx.graphics.getHeight() - 45);

            if (world.currentMessage != null) {
                if (world.currentMessage.fullscreen) {
                    pixelbatch.draw(blackpixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    GlyphLayout gl = new GlyphLayout();
                    gl.setText(font12, world.currentMessage.message, Color.WHITE, Gdx.graphics.getWidth(), Align.center, true);
                    font12.draw(pixelbatch, gl, 0, Gdx.graphics.getHeight() / 2);
                } else {
                    if (world.currentMessage.type > 0)
                        font12.draw(pixelbatch, world.currentMessage.message, 0, 45);
                }
            }
            pixelbatch.end();

            if (world.player.isStopped) {
                GlyphLayout gl = new GlyphLayout();
                gl.setText(font12, "Buy a boat : Currently using " + world.player.typename + " boat", Color.WHITE, 1000, Align.center, false);
                pixelbatch.begin();
                font12.draw(pixelbatch, gl, (Gdx.graphics.getWidth() - 1000) / 2, 950);
                pixelbatch.end();

                world.buyScout.draw(shapes, font12, pixelbatch);
                world.buyTransporter.draw(shapes, font12, pixelbatch);
                world.buySpeedBoat.draw(shapes, font12, pixelbatch);
                world.buyWarShip.draw(shapes, font12, pixelbatch);

            }

        } else {
            String message = "";
            switch (world.player.causeOfDeath) {
                case CRASHING:
                    message = "You crashed into a rock!!";
                    break;
                case SEAMONSTERS:
                    message = "You were eaten by sea monsters!!";
                    break;
            }
            pixelbatch.begin();
            pixelbatch.draw(blackpixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            GlyphLayout gl = new GlyphLayout();
            gl.setText(font12, message, Color.WHITE, Gdx.graphics.getWidth(), Align.center, false);
            font12.draw(pixelbatch, gl, 0, Gdx.graphics.getHeight() / 2);
            pixelbatch.end();
            black.setColor(Color.BLACK);
            black.fill();
        }
    }
}

class Button {
    float x, y, height, width;
    String text;

    public Button(float x, float y, float height, float width, String text) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.text = text;
    }

    public void draw(ShapeRenderer sr, BitmapFont font, Batch batch) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.rect(x, y, width, height);
        sr.end();
        batch.begin();
        GlyphLayout gl = new GlyphLayout();
        gl.setText(font, text, Color.WHITE, width, Align.center, false);
        font.draw(batch, gl, (Gdx.graphics.getWidth() - 1000) / 2, y + height / 2);
        batch.end();
    }

    public boolean isPointInBounds(float x, float y) {
        return (x > this.x && x < this.x + this.width) && (y > this.y && y < this.y + this.height);
    }


}
