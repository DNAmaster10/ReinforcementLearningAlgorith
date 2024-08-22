package program;

import com.raylib.java.Raylib;

public class Window {
    private static Raylib rl;

    public static void setRl(Raylib raylib) {
        rl = raylib;
    }

    public static Raylib getWindow() {
        return rl;
    }
}
