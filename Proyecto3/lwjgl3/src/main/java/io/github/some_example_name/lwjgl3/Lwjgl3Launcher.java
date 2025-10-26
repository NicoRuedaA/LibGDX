package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
// import io.github.some_example_name.GameScreen; // 👈 Ya no se necesita aquí
import io.github.some_example_name.Proyecto3Game; // 👈 Esta es la que importa

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support

        // 1. DEJA SOLO ESTA LÍNEA EN EL MAIN
        //    El único trabajo de main es llamar a createApplication()
        createApplication();
    }

    // 2. ESTE ES EL MÉTODO QUE LANZA EL JUEGO
    private static Lwjgl3Application createApplication() {
        // 3. CAMBIA GameScreen() POR Proyecto3Game()
        //    Proyecto3Game es ahora el punto de entrada que mostrará el menú.
        return new Lwjgl3Application(new Proyecto3Game(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        // (Este método está perfecto, no se toca)
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Proyecto3");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(1920, 720);
        configuration.setDecorated(true);
        configuration.setResizable(true);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}
