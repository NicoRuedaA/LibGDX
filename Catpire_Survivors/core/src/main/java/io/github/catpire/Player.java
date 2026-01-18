package io.github.catpire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; // <-- ¡Asegúrate de importar esto!
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;

public class Player extends Character {

    // --- 1. ESTADO DEL JUGADOR ---
    // (Puedes poner esto en su propio archivo PlayerState.java si quieres)
    public enum PlayerState {
        IDLE,
        WALKING,
        ATTACKING
    }

    private PlayerState currentState;
    private String currentDirection; // Almacena la última dirección (para IDLE/ATTACK)
    private float stateTime; // Temporizador para la animación del estado actual

    public PlayerAnimation animation;
    private InputManager inputManager;

    //variables de nivel
    private int currentExp;
    private int expToLevelUp;
    private int expIncreaseAmount;

    // Variables para los límites del mundo
    private float minX, maxX, minY, maxY;

    // --- VARIABLES ELIMINADAS ---
    // private boolean moved; // (Ahora se gestiona localmente en update)
    // private Vector2 direction; // (Ahora se gestiona localmente en update)

    private boolean isBursting;
    private boolean energyBallActive;

    //varaibles del disparo
    private float shootDelay = 0.75f;
    private float shootTimer;
    private int projectilesPerShot;
    private float burstDelay;
    private float burstTimer;
    private int projectilesFiredInBurst;

    //variables de disparo en arco
    private int spreadLevel;
    private float angleBetweenShots = 15f;


    public Player(Vector2 startPos, InputManager inputManager) {
        super(startPos);

        this.animation = new PlayerAnimation("player_spritesheet.png", 5, 4); // (Asegúrate de que esta clase esté actualizada al "Plan B")
        this.inputManager = inputManager;

        // Stats heredados
        this.speed = 250f;
        this.width = 100f;
        this.height = 100f;
        this.hitboxWidth = 40f;   // 👈 EDITA ESTO
        this.hitboxHeight = 40f;
        //this.size = 50f;
        this.health = 1;

        // Stats de nivel
        this.currentExp = 0;
        this.expToLevelUp = 10;
        this.expIncreaseAmount = 1;

        // Stats de disparo
        this.shootTimer = 0f;
        this.projectilesPerShot = 1;
        this.burstDelay = 0.1f;
        this.burstTimer = 0f;
        this.projectilesFiredInBurst = 0;
        this.spreadLevel = 1;

        // Stats de estado
        this.isBursting = false;
        this.energyBallActive = false;

        // --- 2. INICIALIZACIÓN DE ESTADO ---
        this.currentState = PlayerState.IDLE;
        this.currentDirection = "DOWN"; // Por defecto
        this.stateTime = 0f;

        // Límites del mundo
        minX = width / 2f;
        maxX = Background.WORLD_WIDTH - (width / 2f);
        minY = height / 2f;
        maxY = Background.WORLD_HEIGHT - (height / 2f);
    }

    @Override
    public void update(float delta) {
        // Acumulamos el tiempo que llevamos en el estado actual
        stateTime += delta;

        boolean moved = inputManager.hasMoved();
        Vector2 direction = inputManager.getMovementDirection();

        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;

        // --- 3. LÓGICA DE LA MÁQUINA DE ESTADOS ---

        // El estado de ataque tiene prioridad y bloquea el movimiento
        if (currentState == PlayerState.ATTACKING) {

            // Comprueba si la animación de ataque (que no es en bucle) ha terminado
            if (animation.getAttackAnimationDuration() <= stateTime) {
                currentState = PlayerState.IDLE; // Vuelve al estado IDLE
            }

        } else {
            // Si no estamos atacando (estamos IDLE o WALKING), procesamos el movimiento





            // Actualiza el estado y la dirección (IDLE o WALKING)
            if (moved) {
                currentState = PlayerState.WALKING;
                currentDirection = inputManager.getAnimationDirection(); // Actualiza la dirección
            } else {
                currentState = PlayerState.IDLE;
            }
        }

        // --- FIN LÓGICA DE ESTADO ---

        // El 'clamping' (límites) se aplica siempre, sin importar el estado
        position.x = MathUtils.clamp(position.x, minX, maxX);
        position.y = MathUtils.clamp(position.y, minY, maxY);

        // Los timers de disparo también se actualizan siempre
        if(!isBursting) shootTimer += delta;
        burstTimer += delta;
    }

    public void updateShooting(boolean justClicked, List<FireProjectile> fireProjectiles, CameraController camera) {

        // --- A. ¿El jugador acaba de hacer clic? ---
        // ¡NUEVA CONDICIÓN! Solo podemos disparar si no estamos ya atacando.
        if (justClicked && !isBursting && shootTimer >= shootDelay && currentState != PlayerState.ATTACKING) {

            // --- 4. ACTIVAR EL ESTADO DE ATAQUE ---
            currentState = PlayerState.ATTACKING;
            stateTime = 0f; // ¡Reinicia el reloj del estado!

            // Iniciar ráfaga
            isBursting = true;
            projectilesFiredInBurst = 0;
            burstTimer = burstDelay;
        }

        // --- B. ¿Estamos en medio de una ráfaga? ---
        if (isBursting) {
            if (burstTimer >= burstDelay) {
                projectilesFiredInBurst++;
                burstTimer = 0f;
                fireShot(fireProjectiles, camera);

                if (projectilesFiredInBurst >= projectilesPerShot) {
                    isBursting = false;
                    projectilesFiredInBurst = 0;
                    shootTimer = 0f;
                }
            }
        }
    }

    private void fireShot(List<FireProjectile> projectiles, CameraController camera) {
        // 1. Obtiene la dirección central (hacia el ratón)
        Vector3 mouseWorld = camera.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePos = new Vector2(mouseWorld.x, mouseWorld.y);
        Vector2 startPos = this.position.cpy();
        Vector2 baseDirection = mousePos.sub(startPos).nor();
        float baseAngle = baseDirection.angleDeg();

        // 2. Itera 'spreadLevel' veces
        for (int i = 0; i < spreadLevel; i++) {
            float angleOffset = (i - (spreadLevel - 1) / 2.0f) * angleBetweenShots;
            float finalAngle = baseAngle + angleOffset;
            Vector2 shotDirection = new Vector2(1, 0).setAngleDeg(finalAngle);
            projectiles.add(new FireProjectile(startPos, shotDirection));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // --- 5. RENDERIZADO BASADO EN ESTADO ---
        // Pide a la animación el frame correcto basado en el estado,
        // la dirección y el tiempo que llevamos en ese estado.
        TextureRegion frame = animation.getFrame(currentState, currentDirection, stateTime);

        if (currentDirection.equals("RIGHT") && !frame.isFlipX()) {
            // Si miramos a la derecha Y el frame NO está flipeado,
            // entonces lo flipeamos (invertimos).
            frame.flip(true, false);
        }
        // Comprueba si debemos mirar a la IZQUIERDA (o arriba/abajo)
        else if (!currentDirection.equals("RIGHT") && frame.isFlipX()) {
            // Si NO miramos a la derecha (ej. "LEFT", "UP", "DOWN")
            // Y el frame SÍ estaba flipeado,
            // lo volvemos a flipear para dejarlo original.
            frame.flip(true, false);
        }

        // Dibuja el frame centrado
        batch.draw(frame, position.x - width / 2, position.y - height / 2, width, height);
    }

    // --- MÉTODO ANTIGUO ELIMINADO ---
    // private void fireOneProjectile(...) { ... }


    // --- MÉTODOS DE MEJORA Y STATS (Sin cambios) ---

    public void applyArcShotUpgrade() {
        this.spreadLevel++;
        Gdx.app.log("Player", "Mejora de Arco! Nivel de arco: " + spreadLevel);
    }

    public void increaseSpeed(int x){
        this.speed += x;
    }

    public void activateShield(){

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

    public void applyHealthUpgrade() {
        this.health += 1;
    }

    public void applyProjectileUpgrade() {
        this.projectilesPerShot++;
    }

    public void activateEnergyBall() {
        this.energyBallActive = true;
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
