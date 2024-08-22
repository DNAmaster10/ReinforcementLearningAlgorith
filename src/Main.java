import com.raylib.java.Raylib;
import program.Window;
import program.scenes.SimulationVisualiser;

import static com.raylib.java.core.Color.BLACK;

public class Main {
    public static void main(String[] args) {
        Raylib rl = new Raylib(1000, 1000, "Test!");
        Window.setRl(rl);

        rl.core.SetTargetFPS(60);

        SimulationVisualiser simulationVisualiser = new SimulationVisualiser();

        rl.core.BeginDrawing();
        rl.core.ClearBackground(BLACK);
        rl.core.EndDrawing();

        while (!rl.core.WindowShouldClose()) {
            simulationVisualiser.tick();
            rl.core.BeginDrawing();
            simulationVisualiser.draw();
            rl.core.EndDrawing();
        }
    }
}