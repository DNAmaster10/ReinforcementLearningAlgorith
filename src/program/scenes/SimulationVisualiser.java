package program.scenes;

import com.raylib.java.Raylib;
import com.raylib.java.raymath.Vector2;
import program.Camera;
import program.Window;
import program.components.Simulator;
import program.components.World;

import static com.raylib.java.core.Color.*;
import static com.raylib.java.core.input.Keyboard.*;

public class SimulationVisualiser implements Scene {
    private static final Raylib rl = Window.getWindow();

    Simulator simulator = new Simulator();
    World currentWorld = simulator.getBestWorld();
    int generation = 0;
    boolean drawGraph = false;
    boolean isPaused = false;
    GraphVisualiser graphVisualiser = new GraphVisualiser();

    Camera camera = new Camera();
    @Override
    public void tick() {
        camera.tick();
        if (rl.core.IsKeyPressed(KEY_S)) {
            simulator.iterateOnce();
            currentWorld = simulator.getBestWorld();
            drawGraph = false;
            generation++;
        }
        if (rl.core.IsKeyPressed(KEY_W)) {
            currentWorld = simulator.getWorstWorld();
            graphVisualiser.setCurrentNetwork(currentWorld.getNetwork());
        } else if (rl.core.IsKeyPressed(KEY_B)) {
            currentWorld = simulator.getBestWorld();
            graphVisualiser.setCurrentNetwork(currentWorld.getNetwork());
        }
        if (rl.core.IsKeyPressed(KEY_SPACE)) {
            if (!drawGraph) {
                drawGraph = true;
                graphVisualiser.setCurrentNetwork(currentWorld.getNetwork());
            } else {
                drawGraph = false;
            }
        }
        if (rl.core.IsKeyPressed(KEY_P)) {
            isPaused = !isPaused;
        }

        if (!isPaused) {
            currentWorld.tick();
        }
    }

    @Override
    public void draw() {
        if (drawGraph) {
            rl.core.BeginMode2D(camera.getCamera());
            graphVisualiser.draw();
            rl.core.EndMode2D();
        } else {
            float y = currentWorld.getPositionY();
            Vector2 position = new Vector2(500f, y + 500f);
            rl.core.ClearBackground(BLACK);
            rl.shapes.DrawCircleV(position, 50, RED);
            rl.text.DrawFPS(10, 10, WHITE);
            rl.text.DrawText("Generation: " + generation, 10, 50, 50, WHITE);
            rl.text.DrawText("Avg: " + simulator.getAverageScore(), 10, 100, 50, WHITE);
        }
    }
}
