package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;

public class Player extends Character {

    private float shootDelay = 0.75f; // 0.2 segundos (5 disparos por segundo)
    private float shootTimer;

    public PlayerAnimation animation;


    private int starterHealth = 1;

    private int currentExp;
    private int expToLevelUp;
    private int expIncreaseAmount;

    private int projectilesPerShot;     // Cuántas balas por ráfaga (empezará en 1)
    private float burstDelay;           // El delay que pediste (0.1s entre balas)
    private float burstTimer;           // Temporizador para el burstDelay
    private int projectilesFiredInBurst; // Contador de cuántas balas hemos disparado en la ráfaga actual

    public Player(Vector2 startPos) {
        super(startPos);
        animation = new PlayerAnimation("player_spritesheet.png", 5, 4);
        this.shootTimer = 0f;
        this.speed = 250f;
        this.currentExp = 0;
        this.expToLevelUp = 10;
        this.expIncreaseAmount = 1;

        this.shootTimer = 0f;
        this.projectilesPerShot = 1; // Empieza disparando 1 bala
        this.burstDelay = 0.1f;      // 0.1 segundos entre balas (puedes ajustarlo)
        this.burstTimer = 0f;
        this.projectilesFiredInBurst = 0;
    }

    @Override
    public void update(float delta) {
        boolean moved = false;

        float minX = width / 2f;
        float maxX = Level.WORLD_WIDTH - (width / 2f);
        float minY = height / 2f;
        float maxY = Level.WORLD_HEIGHT - (height / 2f);

        position.x = MathUtils.clamp(position.x, minX, maxX);
        position.y = MathUtils.clamp(position.y, minY, maxY);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * delta;
            animation.setDirection("UP");
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * delta;
            animation.setDirection("DOWN");
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= speed * delta;
            animation.setDirection("LEFT");
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += speed * delta;
            animation.setDirection("RIGHT");
            moved = true;
        }

        // Solo actualizar animación si se está moviendo
        if (moved) {
            animation.update(delta);
        }

        shootTimer += delta;
        burstTimer += delta;


    }

    @Override
    public void render(SpriteBatch batch) {

        animation.render(batch, position.x, position.y);


    }

    public void shoot(List<Projectile> projectiles, CameraController camera) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

            // 2. ¿Está listo el cooldown principal?
            if (shootTimer >= shootDelay) {
                // ¡Sí! Iniciar una nueva ráfaga
                shootTimer = 0f; // Reiniciar cooldown principal
                projectilesFiredInBurst = 0; // Empezar ráfaga desde 0
                burstTimer = burstDelay; // ¡Importante! Pone el timer listo para disparar la primera bala INMEDIATAMENTE
            }
        }

        // 3. ¿Estamos EN MEDIO de una ráfaga?
        //    (Esto se comprueba fuera del 'if (isButtonPressed)' para que la ráfaga
        //    se complete aunque el jugador suelte el botón)
        if (projectilesFiredInBurst < projectilesPerShot && burstTimer >= burstDelay) {

            // ¡Sí! Disparar la siguiente bala de la ráfaga
            projectilesFiredInBurst++; // Contamos esta bala
            burstTimer = 0f; // Reiniciamos el timer de la ráfaga

            // Llamamos a la función que crea la bala
            fireOneProjectile(projectiles, camera);
        }
    }

    private void fireOneProjectile(List<Projectile> projectiles, CameraController camera) {
        Vector3 mouseWorld = camera.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePos = new Vector2(mouseWorld.x, mouseWorld.y);
        Vector2 startPos = this.position.cpy();
        Vector2 direction = mousePos.sub(startPos).nor();

        projectiles.add(new Projectile(startPos, direction));
    }

    @Override
    public void dispose() {
        animation.dispose();
    }

    public int getHealth() {
        return this.health;
    }

    public boolean addExp(int amount) {
        this.currentExp += amount;

        while (this.currentExp >= this.expToLevelUp) {
            // ¡LEVEL UP!
            int spilloverExp = this.currentExp - this.expToLevelUp;
            this.expToLevelUp += this.expIncreaseAmount;
            this.currentExp = spilloverExp;

            // --- ¡CAMBIO IMPORTANTE! ---
            // Ya no añadimos vida aquí.
            // this.health += 1;  // <-- BORRA ESTA LÍNEA

            return true; // <-- AÑADE ESTO: Notifica a GameScreen
        }

        return false; // No ha subido de nivel
    }

    public int getCurrentExp() {
        return this.currentExp;
    }

    public int getExpToLevelUp() {
        return this.expToLevelUp;
    }

    public void applyHealthUpgrade() {
        this.health += 1;
    }

    /**
     * Aplica la mejora de proyectil (llamado por GameScreen).
     */
    public void applyProjectileUpgrade() {
        // ¡Aquí está tu nueva funcionalidad!
        this.projectilesPerShot++; // Ahora disparará 2 (o 3, o 4...)
        Gdx.app.log("Player", "Mejora de Proyectil! Disparando " + projectilesPerShot + " balas.");
    }



}
