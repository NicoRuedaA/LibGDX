package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EnemyAnimation {

    private Texture sheet;
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion currentFrame;
    private float stateTime;

    private float size = 100f;

    private int frameCols = 5; // columnas del spritesheet
    private int frameRows = 1; // filas del spritesheet

    public EnemyAnimation(String filePath, int frameCols, int frameRows) {
        this.frameCols = frameCols;
        this.frameRows = frameRows;

        sheet = new Texture(Gdx.files.internal(filePath));
        TextureRegion[][] tmp = TextureRegion.split(
            sheet,
            sheet.getWidth() / frameCols,
            sheet.getHeight() / frameRows
        );

        // Usaremos todos los frames de la primera fila para la animación de caminar
        walkAnimation = createAnimationFromRow(tmp, 0, 2);

        stateTime = 0f;
        currentFrame = tmp[0][0]; // primer frame
    }

    private Animation<TextureRegion> createAnimationFromRow(TextureRegion[][] tmp, int row, int usedCols) {
        TextureRegion[] frames = new TextureRegion[usedCols];
        for (int i = 0; i < usedCols; i++) {
            frames[i] = tmp[row][i];
        }
        return new Animation<>(0.2f, frames); // duración de frame ajustable
    }

    public void update(float delta) {
        stateTime += delta;
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);
    }

    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
    }

    public void dispose() {
        sheet.dispose();
    }
}
