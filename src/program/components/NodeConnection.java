package program.components;

public class NodeConnection {
    private float weight;
    private Node sourceNode;
    private Node destinationNode;

    public NodeConnection(float weight, Node sourceNode, Node destinationNode) {
        this.weight = weight;

        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
    }

    public void apply() {
        destinationNode.addInput(sourceNode.getOutput() * weight);
    }

    public Node getDestinationNode() {
        return this.destinationNode;
    }

    public void addWeight(float weight) {
        this.weight += weight;
    }

    public float getWeight() {
        return this.weight;
    }
}
