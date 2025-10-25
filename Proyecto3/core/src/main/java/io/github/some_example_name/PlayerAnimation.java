package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Gdx;

public class PlayerAnimation {

    private Texture sheet;
    private Animation<TextureRegion> walkUp, walkDown, walkSide;
    private TextureRegion currentFrame;
    private float stateTime;

    private String direction = "DOWN"; // Dirección inicial

    private int frameCols = 5; // columnas en el spritesheet
    private int frameRows = 4; // filas en el spritesheet

    public PlayerAnimation(String filePath, int frameCols, int frameRows) {
        this.frameCols = frameCols;
        this.frameRows = frameRows;
        sheet = new Texture(Gdx.files.internal(filePath));

        // Dividir el spritesheet en una matriz 2D de frames
        TextureRegion[][] tmp = TextureRegion.split(
            sheet,
            sheet.getWidth() / frameCols,
            sheet.getHeight() / frameRows
        );

        // 🔹 Crear animaciones usando SOLO los 2 primeros frames de cada fila relevante
        walkDown = createAnimationFromRow(tmp, 0, 2); // Fila 0 → abajo
        walkSide = createAnimationFromRow(tmp, 1, 2); // Fila 1 → izquierda/derecha
        walkUp   = createAnimationFromRow(tmp, 2, 2); // Fila 2 → arriba

        stateTime = 0f;
        currentFrame = tmp[0][0]; // frame inicial
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

        switch (direction) {
            case "UP":
                currentFrame = walkUp.getKeyFrame(stateTime, true);
                break;
            case "DOWN":
                currentFrame = walkDown.getKeyFrame(stateTime, true);
                break;
            case "LEFT":
                currentFrame = walkSide.getKeyFrame(stateTime, true);
                break;
            case "RIGHT":
                // Mismo sprite que LEFT pero volteado horizontalmente
                currentFrame = new TextureRegion(walkSide.getKeyFrame(stateTime, true));
                currentFrame.flip(true, false);
                break;
        }
    }

    public void render(SpriteBatch batch, float x, float y) {
        float width = 100;
        float height = 100;
        batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
    }

    public void setDirection(String dir) {
        if (!dir.equals(direction)) {
            direction = dir;
            stateTime = 0f; // reinicia animación al cambiar de dirección
        }
    }

    public void dispose() {
        sheet.dispose();
    }
}
