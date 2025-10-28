package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Representa un enemigo que hereda de Character.
 * Su lógica de movimiento es controlada por el EnemyManager (Opción 1).
 */
public class Enemy extends Character {




    public EnemyAnimation animation;

    // 'size' es solo para el renderizado (qué tan grande se ve el sprite)
    private float size = 100f;

    /**
     * Constructor para un nuevo Enemigo.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public Enemy(float x, float y, EnemyManager.EnemyType type) {
        // 1. Llama al constructor de Character para guardar la posición
        super(new Vector2(x, y));

        // 2. Establece las variables 'protected' heredadas de Character
        this.speed = speed;
        this.health = 1;      // Damos 1 vida por defecto
        this.width = 50f;     // Hitbox de 50px (diferente al 'size' de render)
        this.height = 50f;    // Hitbox de 50px

        // 3. Inicializa la animación
        switch (type) {
            case GRUNT:
            default:
                // El enemigo normal
                this.speed = 100f;
                this.health = 2;
                this.width = 50f;
                this.height = 50f;
                this.size = 100f;
                animation = new EnemyAnimation("enemy_spritesheet.png", 5, 4);
                break;

            case SCOUT:
                // El enemigo rápido
                this.speed = 250f; // Más rápido
                this.health = 1;   // Más débil
                this.width = 50f;  // Más pequeño
                this.height = 50f;
                this.size = 100f;
                // Asumimos que usa una hoja de sprites diferente
                animation = new EnemyAnimation("enemy2_spritesheet.png", 5, 4);
                break;

            case TYPE3:
                // El enemigo normal
                this.speed = 100f;
                this.health = 2;
                this.width = 50f;
                this.height = 50f;
                this.size = 100f;
                animation = new EnemyAnimation("enemy3_spritesheet.png", 5, 4);
                break;
            case TYPE4:
                // El enemigo normal
                this.speed = 100f;
                this.health = 2;
                this.width = 50f;
                this.height = 50f;
                this.size = 100f;
                animation = new EnemyAnimation("enemy4_spritesheet.png", 5, 4);
                break;
        }
    }

    /**
     * Este método cumple con la herencia de 'Character'.
     * Lo usamos para actualizar cosas que solo dependen del tiempo, como la animación.
     */
    @Override
    public void update(float delta) {
        animation.update(delta);
    }

    /**
     * Este es el método que llama EnemyManager (Opción 1).
     * Contiene la lógica de movimiento (IA) porque necesita la posición del jugador.
     */
    public void update(float delta, Vector2 playerPosition) {
        // 1. Calcula la dirección hacia el jugador
        //    (Arreglado de tu código: 'playerPosition' es el objetivo)
        Vector2 direction = playerPosition.cpy().sub(position).nor();

        // 2. Mueve la 'position' (heredada) hacia esa dirección
        position.add(direction.scl(speed * delta));
    }

    /**
     * Dibuja al enemigo (exigido por Character).
     */
    @Override
    public void render(SpriteBatch batch) {
        // Pasa el centro (position) y el TAMAÑO DE DIBUJO (size)
        // La clase Animation se encarga de centrarlo
        animation.render(batch, position.x, position.y, size, size);
    }

    /**
     * Libera la memoria de la animación (exigido por Character).
     */
    @Override
    public void dispose() {
        animation.dispose();
    }

    // --- NOTA ---
    // ¡No necesitas getBounds(), takeDamage(), isAlive(), getPosition(), etc.
    // Todos esos métodos ya se heredan automáticamente de la clase Character.
}
