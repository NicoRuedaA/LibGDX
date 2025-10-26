package io.github.some_example_name;

/**
 * Gestiona el estado global del juego (pausa, corriendo, etc.).
 * Es un Singleton, por lo que solo hay una instancia de él.
 */
public class GameManager {

    // 1. El enum de estado que antes estaba en GameScreen
    public enum GameState {
        RUNNING,
        PAUSED_LEVELUP
        // En el futuro: PAUSED_BY_USER, GAME_OVER, etc.
    }

    // 2. La instancia única (Singleton)
    private static GameManager instance;

    // 3. El estado actual
    private GameState currentState;

    /**
     * El constructor es privado para que nadie más pueda crearlo.
     */
    private GameManager() {
        this.currentState = GameState.RUNNING; // El juego empieza corriendo
    }

    /**
     * El método estático para obtener la única instancia.
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // --- MÉTODOS PÚBLICOS (La "API" de tu manager) ---

    /**
     * Comprueba si el juego debe estar actualizándose.
     */
    public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }

    /**
     * Comprueba si el juego está pausado para la subida de nivel.
     */
    public boolean isPausedForLevelUp() {
        return currentState == GameState.PAUSED_LEVELUP;
    }

    /**
     * Pausa el juego para la subida de nivel.
     */
    public void pauseForLevelUp() {
        this.currentState = GameState.PAUSED_LEVELUP;
    }

    /**
     * Reanuda el juego.
     */
    public void resumeGame() {
        this.currentState = GameState.RUNNING;
    }

    /**
     * Reinicia el estado para una nueva partida.
     */
    public void reset() {
        this.currentState = GameState.RUNNING;
    }
}
