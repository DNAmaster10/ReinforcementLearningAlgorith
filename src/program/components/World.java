package program.components;

import com.raylib.java.Raylib;
import program.Window;

public class World {
    private static final Raylib rl = Window.getWindow();

    Network network = new Network();

    float positionY = 250f;
    float velocityY = 0f;
    float gravity = 0.1f;

    float score = 0f;

    public void tick() {
        network.reset();
        network.setInputs(positionY, velocityY);
        float accelerationBoolean = network.execute();
        if (accelerationBoolean > 0.5f) velocityY += 0.2f;
        velocityY -= gravity;

        positionY += velocityY;

        float dy = Math.abs(0f - positionY);
        score = 0 - dy;
    }

    public float getScore() {
        return score;
    }

    public void mutateNetwork() {
        network.mutate();
    }

    public void reset() {
        network.reset();
        score = 0f;
        positionY = 0f;
        velocityY = 0f;
    }

    public float getPositionY() {
        return this.positionY;
    }

    public Network getNetwork() {
        return this.network;
    }
}
