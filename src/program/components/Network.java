package program.components;

import program.util.HashMapUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Network {
    //Define input nodes
    public Node positionYInput = new Node();
    public Node velocityYInput = new Node();

    //Define output nodes
    public Node thrustOutput = new Node();

    //State variables
    HashMap<Node, List<NodeConnection>> outgoingNodeConnections = new HashMap<>();
    HashMap<Node, List<Node>> incomingConnections = new HashMap<>();
    HashMap<Node, List<Node>> outgoingConnections = new HashMap<>();
    List<Node> unsortedNodes = new ArrayList<>();
    List<Node> topologicalSort = new ArrayList<>();

    public Network() {
        Node mNode = new Node();
        addConnection(positionYInput, mNode);
        addConnection(velocityYInput, mNode);
        addConnection(mNode, thrustOutput);
        initialize();
    }

    public List<Node> getTopologicalSort() {
        return this.topologicalSort;
    }

    public void addConnection(Node sourceNode, Node destNode) {
        //Adds a new node connection with a random weight
        if (incomingConnections.containsKey(destNode)) {
            //Check that the connection does not already exist, if it does abort
            if (incomingConnections.get(destNode).contains(sourceNode)) return;
        } else {
            incomingConnections.put(destNode, new ArrayList<>());
        }
        if (!outgoingConnections.containsKey(sourceNode)) outgoingConnections.put(sourceNode, new ArrayList<>());
        if (!outgoingNodeConnections.containsKey(sourceNode)) outgoingNodeConnections.put(sourceNode, new ArrayList<>());

        float weight = ThreadLocalRandom.current().nextFloat(-1f, 1f);
        NodeConnection nodeConnection = new NodeConnection(weight, sourceNode, destNode);

        outgoingNodeConnections.get(sourceNode).add(nodeConnection);
        incomingConnections.get(destNode).add(sourceNode);
        outgoingConnections.get(sourceNode).add(destNode);

        if (!unsortedNodes.contains(sourceNode)) unsortedNodes.add(sourceNode);
        if (!unsortedNodes.contains(destNode)) unsortedNodes.add(destNode);
    }

    public void removeConnection(Node sourceNode, Node destNode) {
        if (!incomingConnections.containsKey(destNode)) return;
        if (!incomingConnections.get(destNode).contains(sourceNode)) return;

        incomingConnections.get(destNode).remove(sourceNode);
        outgoingConnections.get(sourceNode).remove(destNode);
        outgoingNodeConnections.get(sourceNode).removeIf(e -> e.getDestinationNode().equals(destNode));

        if (incomingConnections.get(destNode).isEmpty()) incomingConnections.remove(destNode);
        if (outgoingConnections.get(sourceNode).isEmpty()) outgoingConnections.remove(sourceNode);
        if (outgoingNodeConnections.get(sourceNode).isEmpty()) outgoingNodeConnections.remove(sourceNode);
    }

    private boolean checkConnection(Node sourceNode, Node destNode) {
        if (!outgoingConnections.containsKey(sourceNode)) return false;
        return outgoingConnections.get(sourceNode).contains(destNode);
    }

    public void initialize() {
        //Calculates the correct node execution order using a topological sort
        topologicalSort.clear();

        HashMap<Integer, List<Node>> layeredNodes = getLayerSortedNodes();

        for (int currentLayer = 0; currentLayer < layeredNodes.size(); currentLayer++) {
            topologicalSort.addAll(layeredNodes.get(currentLayer));
        }
    }

    public HashMap<Integer, List<Node>> getLayerSortedNodes() {
        //Calculates the correct node execution order using a topological sort
        //Copying values here is done using a "Deep Copy". This is essential to preserve the original hash map.
        HashMap<Node, List<Node>> tempMap = HashMapUtils.copy(incomingConnections);
        List<Node> unsortedNodes = new ArrayList<>(this.unsortedNodes);

        HashMap<Integer, List<Node>> layeredNodes = new HashMap<>();
        Integer currentLayer = 0;

        while (!unsortedNodes.isEmpty()) {
            //Calculate in degree for remaining nodes
            HashMap<Integer, List<Node>> inDegrees = new HashMap<>();
            for (Node node : unsortedNodes) {
                Integer inDegree = 0;
                if (tempMap.containsKey(node)) inDegree = tempMap.get(node).size();
                if (!inDegrees.containsKey(inDegree)) inDegrees.put(inDegree, new ArrayList<>());
                inDegrees.get(inDegree).add(node);
            }

            List<Node> zeroInNodes = inDegrees.get(0);
            layeredNodes.put(currentLayer, zeroInNodes);
            inDegrees.remove(0);

            //Remove nodes from temporary connection list
            for (Node node : zeroInNodes) {
                tempMap.remove(node);
                unsortedNodes.remove(node);
                if (!outgoingConnections.containsKey(node)) continue;
                List<Node> destinationNodes = outgoingConnections.get(node);
                for (Node destinationNode : destinationNodes) {
                    if (!tempMap.containsKey(destinationNode)) continue;
                    tempMap.get(destinationNode).remove(node);
                }
            }
            currentLayer++;
        }
        return layeredNodes;
    }

    public float execute() {
        for (Node node : topologicalSort) {
            if (outgoingNodeConnections.containsKey(node)) {
                for (NodeConnection connection : outgoingNodeConnections.get(node)) {
                    connection.apply();
                }
            }
        }
        return thrustOutput.getOutput();
    }

    public void setInputs(float positionY, float velocityY) {
        positionYInput.addInput(positionY);
        velocityYInput.addInput(velocityY);
    }

    public void reset() {
        for (Node node : unsortedNodes) {
            node.reset();
        }
    }

    public void mutate() {
        //Randomly mutates network
        int mutationType = ThreadLocalRandom.current().nextInt(0, 4);

        if (mutationType == 1) mutateNewConnection();
        else if (mutationType == 2) mutateNewNode();
        else if (mutationType == 3) mutateWeight();
    }

    public void mutateNewConnection() {
        //Select two nodes to add a connection between. This cannot be between the input nodes and cannot be circular
        int destNodeIndex = ThreadLocalRandom.current().nextInt(2, topologicalSort.size());
        int sourceNodeIndex = ThreadLocalRandom.current().nextInt(0, destNodeIndex);
        Node destNode = topologicalSort.get(destNodeIndex);
        Node sourceNode = topologicalSort.get(sourceNodeIndex);

        if (checkConnection(sourceNode, destNode)) return;
        addConnection(sourceNode, destNode);

        //Re-initialize the graph
        initialize();
    }

    public void mutateNewNode() {
        //Select a random node connection
        Node[] nodes = outgoingNodeConnections.keySet().toArray(Node[]::new);
        Node randomSourceNode = nodes[ThreadLocalRandom.current().nextInt(nodes.length)];

        //Get random connection
        NodeConnection[] connections = outgoingNodeConnections.get(randomSourceNode).toArray(NodeConnection[]::new);
        Node randomDestNode = connections[ThreadLocalRandom.current().nextInt(connections.length)].getDestinationNode();

        //Remove the existing connection
        removeConnection(randomSourceNode, randomDestNode);

        //Create the new node to be inserted, and insert it between the two nodes
        Node newNode = new Node();
        addConnection(randomSourceNode, newNode);
        addConnection(newNode, randomDestNode);

        initialize();
    }

    public void mutateWeight() {
        //Get random node
        Node[] nodes = outgoingNodeConnections.keySet().toArray(Node[]::new);
        Node randomNode = nodes[ThreadLocalRandom.current().nextInt(nodes.length)];

        //Get random connection
        NodeConnection[] connections = outgoingNodeConnections.get(randomNode).toArray(NodeConnection[]::new);
        NodeConnection randomConnection = connections[ThreadLocalRandom.current().nextInt(connections.length)];

        //Mutate weight randomly
        float mutationAmount = ThreadLocalRandom.current().nextFloat(-1f, 1f);
        randomConnection.addWeight(mutationAmount);
    }

    public HashMap<Node, List<NodeConnection>> getOutgoingNodeConnections() {
        return this.outgoingNodeConnections;
    }
}
