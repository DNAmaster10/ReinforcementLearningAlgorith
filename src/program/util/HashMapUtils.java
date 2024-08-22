package program.util;

import program.components.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapUtils {
    public static HashMap<Node, List<Node>> copy(HashMap<Node, List<Node>> original) {
        HashMap<Node, List<Node>> copy = new HashMap<>();
        for (Map.Entry<Node, List<Node>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }
}
