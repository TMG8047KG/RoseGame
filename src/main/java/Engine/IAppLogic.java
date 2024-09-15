package Engine;


import Engine.Rendering.Render;
import Engine.Rendering.Scene.Scene;
import Engine.Rendering.Scene.Window;

public interface IAppLogic {

    void cleanup();

    void init(Window window, Scene scene, Render render);

    void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed);

    void update(Window window, Scene scene, long diffTimeMillis);
}
