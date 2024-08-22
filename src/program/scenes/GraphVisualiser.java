package program.scenes;

import com.raylib.java.Raylib;
import com.raylib.java.raymath.Raymath;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.text.Font;
import program.Window;
import program.components.Network;
import program.components.Node;
import program.components.NodeConnection;
import program.util.HashMapUtils;

import java.util.HashMap;
import java.util.List;

import static com.raylib.java.core.Color.*;
import static com.raylib.java.text.rText.FontType.FONT_DEFAULT;

public class GraphVisualiser {
    static final Raylib rl = Window.getWindow();
    Network currentNetwork;
    HashMap<Node, Vector2> nodePositions = new HashMap<>();
    HashMap<Node, List<NodeConnection>> nodeEdges;

    public void draw() {
        rl.core.ClearBackground(WHITE);
        for (Node node : nodePositions.keySet()) {
            Vector2 position = nodePositions.get(node);
            rl.shapes.DrawCircleV(position, 30, BLACK);
            int x = (int) position.x;
            int y = (int) position.y;
            //if (node.equals(currentNetwork.positionYInput)) rl.text.DrawText("PositionY", x, y, 10, RED);
            //else if (node.equals(currentNetwork.velocityYInput)) rl.text.DrawText("VelocityY", x, y, 10, RED);
            //else if (node.equals(currentNetwork.thrustOutput)) rl.text.DrawText("Thrust", x, y, 10, RED);
            rl.text.DrawText("" + node.getOutput(), x, y, 15, RED);
        }

        for (Node sourceNode : nodeEdges.keySet()) {
            for (NodeConnection nodeConnection : nodeEdges.get(sourceNode)) {
                Vector2 sourcePosition = nodePositions.get(sourceNode);
                Vector2 destPosition = nodePositions.get(nodeConnection.getDestinationNode());
                rl.shapes.DrawLineV(sourcePosition, destPosition, BLACK);
                float middle = Raymath.Vector2Distance(sourcePosition, destPosition) / 2f;
                Vector2 middlePoint = Raymath.Vector2MoveTowards(sourcePosition, destPosition, middle);
                rl.text.DrawText("" + nodeConnection.getWeight(), (int) middlePoint.getX(), (int) middlePoint.getY(), 15, RED);
            }
        }
    }

    public void setCurrentNetwork(Network network) {
        currentNetwork = network;
        redraw();
    }

    public void redraw() {
        nodePositions.clear();

        HashMap<Integer, List<Node>> layeredNodes = currentNetwork.getLayerSortedNodes();
        for (Integer layer : layeredNodes.keySet()) {
            float x = 50f + (layer * 200);
            List<Node> nodes = layeredNodes.get(layer);
            for (int i = 0; i < nodes.size(); i++) {
                Vector2 vector2 = new Vector2(x, 50f + (i * 100));
                nodePositions.put(nodes.get(i), vector2);
            }
        }

        nodeEdges = currentNetwork.getOutgoingNodeConnections();
    }
}
