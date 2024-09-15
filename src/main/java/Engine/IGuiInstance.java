package Engine;

import Engine.Rendering.Scene.Scene;
import Engine.Rendering.Scene.Window;

public interface IGuiInstance {
    void drawGui();

    boolean handleGuiInput(Scene scene, Window window);
}
