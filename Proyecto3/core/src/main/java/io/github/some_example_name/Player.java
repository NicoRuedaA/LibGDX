package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;

public class Player extends Character {

    public PlayerAnimation animation;
    private InputManager inputManager;

    //variables de nivel
    private int currentExp;
    private int expToLevelUp;
    private int expIncreaseAmount;

    // Variables para los límites del mundo
    private float minX, maxX, minY, maxY;

    private boolean moved;
    private boolean isBursting;
    private boolean energyBallActive;

    //varaibles del disparo
    private float shootDelay = 0.75f;   // delay entre disparos
    private float shootTimer;           // contador de shootDelay
    private int projectilesPerShot;     // balas por ráfaga (empezará en 1)
    private float burstDelay;           // delay entre balas de la misma rafaga
    private float burstTimer;           // Temporizador para el burstDelay
    private int projectilesFiredInBurst; // cuántas balas disparamos por rafaga

    private Vector2 direction;

    public Player(Vector2 startPos, InputManager inputManager) {
        super(startPos);

        this.animation = new PlayerAnimation("player_spritesheet.png", 5, 4);
        this.inputManager = inputManager;

        this.shootTimer = 0f;
        this.speed = 250f;
        this.currentExp = 0;
        this.expToLevelUp = 10;
        this.expIncreaseAmount = 1;

        this.shootTimer = 0f;
        this.projectilesPerShot = 1; // Empieza disparando 1 bala
        this.burstDelay = 0.1f;      // 0.1 segundos entre balas
        this.burstTimer = 0f;
        this.projectilesFiredInBurst = 0;

        this.width = 40f;
        this.height = 50f;
        minX = width / 2f;
        maxX = Background.WORLD_WIDTH - (width / 2f);
        minY = height / 2f;
        maxY = Background.WORLD_HEIGHT - (height / 2f);


        this.health = 1;

        this.direction = new Vector2(0, 0);

        this.moved = false;
        this.isBursting = false;
        this.energyBallActive = false;
    }

    @Override
    public void update(float delta) {
        moved = inputManager.hasMoved();
        direction = inputManager.getMovementDirection();

        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;

        //impedimos que el jugador se salga de los limites del mundo
        position.x = MathUtils.clamp(position.x, minX, maxX);
        position.y = MathUtils.clamp(position.y, minY, maxY);

        // Solo actualizar animación si se está moviendo
        if (moved) {
            animation.setDirection(inputManager.getAnimationDirection());
            animation.update(delta);
        }

        if(!isBursting) shootTimer += delta;
        burstTimer += delta;
    }

    public void updateShooting(boolean justClicked, List<FireProjectile> fireProjectiles, CameraController camera) {

        // --- A. ¿El jugador acaba de hacer clic? ---
        //    (Y no estamos ya en una ráfaga, Y el cooldown principal está listo)
        if (justClicked && !isBursting && shootTimer >= shootDelay) {

            // ¡Empezamos una nueva ráfaga!
            isBursting = true;
            projectilesFiredInBurst = 0;
            burstTimer = burstDelay; // ¡Listo para disparar la primera bala YA!
        }

        // --- B. ¿Estamos en medio de una ráfaga? ---
        //    (Esta lógica ahora se ejecuta sola, sin necesidad de input)
        if (isBursting) {

            // ¿Ha pasado el tiempo de 'burstDelay'?
            if (burstTimer >= burstDelay) {

                // 1. Dispara una bala
                projectilesFiredInBurst++;
                burstTimer = 0f; // Reinicia el timer de la ráfaga
                fireOneProjectile(fireProjectiles, camera); // Llama a tu método que crea la bala

                // 2. ¿Hemos terminado la ráfaga?
                if (projectilesFiredInBurst >= projectilesPerShot) {
                    // ¡Sí! Ráfaga terminada.
                    isBursting = false;
                    projectilesFiredInBurst = 0;

                    // 3. *** ¡AQUÍ ESTÁ TU REQUISITO! ***
                    //    El cooldown principal (shootTimer) empieza a contar AHORA.
                    shootTimer = 0f;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        animation.render(batch, position.x, position.y);
    }

    /*public void shoot(boolean isShooting, List<Projectile> projectiles, CameraController camera) {
        //if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
        //cooldown principal entre disparos

        if(isShooting){
            if (shootTimer >= shootDelay) {
                // ¡Sí! Iniciar una nueva ráfaga
                shootTimer = 0f; // Reiniciar cooldown principal
                projectilesFiredInBurst = 0; // Empezar ráfaga desde 0
                burstTimer = burstDelay; // ¡Importante! Pone el timer listo para disparar la primera bala INMEDIATAMENTE
            }
            // ¿Estamos en medio de una ráfaga?
            // (se completa aunque el jugador suelte el botón)
            if (projectilesFiredInBurst < projectilesPerShot && burstTimer >= burstDelay) {
                // disparamos la siguiente bala de la rafaga
                projectilesFiredInBurst++; // Contamos esta bala
                burstTimer = 0f; // Reiniciamos el timer de la ráfaga
                // Llamamos a la función que crea la bala
                fireOneProjectile(projectiles, camera);
            }
        }

    }*/

    private void fireOneProjectile(List<FireProjectile> fireProjectiles, CameraController camera) {
        Vector3 mouseWorld = camera.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePos = new Vector2(mouseWorld.x, mouseWorld.y);
        Vector2 startPos = this.position.cpy();
        Vector2 direction = mousePos.sub(startPos).nor();

        fireProjectiles.add(new FireProjectile(startPos, direction));
    }

    public boolean addExp(int amount) {
        this.currentExp += amount;

        while (this.currentExp >= this.expToLevelUp) {
            int spilloverExp = this.currentExp - this.expToLevelUp;
            this.expToLevelUp += this.expIncreaseAmount;
            this.currentExp = spilloverExp;
            return true; // Ha subido de nivel
        }

        return false; // No ha subido de nivel
    }

    /**
     * Aplica la mejora de vida (llamado por GameScreen).
     */
    public void applyHealthUpgrade() {
        this.health += 1;
    }
    /**
     * Aplica la mejora de proyectil (llamado por GameScreen).
     */
    public void applyProjectileUpgrade() {
        this.projectilesPerShot++; // Ahora disparará una más
    }

    public void activateEnergyBall() {
        this.energyBallActive = true;
        // (En el futuro, aquí podrías crear el objeto 'new EnergyBall()')
    }

    public boolean getEnergyBall() {
        return this.energyBallActive;
    }

    public int getCurrentExp() {
        return this.currentExp;
    }

    public int getHealth() {
        return this.health;
    }

    public int getExpToLevelUp() {
        return this.expToLevelUp;
    }

    @Override
    public void dispose() {
        animation.dispose();
    }
}
