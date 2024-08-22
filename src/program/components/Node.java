package program.components;

public class Node {
    private float currentValue = 0f;

    public void reset() {
        this.currentValue = 0f;
    }

    public void addInput(float input) {
        this.currentValue += input;
    }

    public float getOutput() {
        return this.currentValue;
    }
}
