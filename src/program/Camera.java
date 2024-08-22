package program;

import com.raylib.java.Raylib;
import com.raylib.java.core.rcamera.Camera2D;
import com.raylib.java.raymath.Vector2;

public class Camera {
    private static final Raylib rl = Window.getWindow();
    Camera2D camera = new Camera2D();
    float velocity = 0f;
    float maxZoomOut = 0.5f;
    float maxZoomIn = 5f;

    public Camera() {
        camera.setOffset(new Vector2(0f, 0f));
        camera.setZoom(0f);
    }

    public void tick() {
        if (rl.core.GetMouseWheelMove() > 0f) {
            velocity = 0.2f;
            camera.setTarget(rl.core.GetScreenToWorld2D(rl.core.GetMousePosition(), camera));
            camera.setOffset(rl.core.GetMousePosition());
        } else if (rl.core.GetMouseWheelMove() < 0f) {
            velocity = -0.2f;
            camera.setTarget(rl.core.GetScreenToWorld2D(rl.core.GetMousePosition(), camera));
            camera.setOffset(rl.core.GetMousePosition());
        }
        camera.setZoom(camera.getZoom() + velocity);
        velocity = velocity / 1.2f;
        if (camera.getZoom() >= maxZoomIn) {
            velocity = 0f;
            camera.setZoom(maxZoomIn);
        } else if (camera.getZoom() < maxZoomOut) {
            velocity = 0f;
            camera.setZoom(maxZoomOut);
        }
    }

    public Camera2D getCamera() {
        return this.camera;
    }
}
