package Engine.GUI;

import Engine.IGuiInstance;
import Engine.Rendering.MouseInput;
import Engine.Rendering.Scene.Window;
import Engine.Rendering.Fog;
import Engine.Rendering.Scene.Scene;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FogControl implements IGuiInstance{

//    private Fog fog;
    private boolean active;
    private float[] color;
    private float[] density;
    private float[] distance;

    public FogControl() {
        this.color = new float[]{0.5f, 0.5f, 0.5f};
        this.density = new float[]{0.015f};
        this.distance = new float[]{0.0f, 0.0f, 0.0f};
    }

    @Override
    public void drawGui() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(450, 400);

        ImGui.begin("Fog");
        active = !ImGui.button("Enabled");
        ImGui.sliderFloat("Density", density, 0.001f, 1f);
        ImGui.colorEdit3("Color", color);
        ImGui.separator();
        ImGui.text("Distance");
        ImGui.sliderFloat3("Distance", distance, 0.0f, 1f);

        ImGui.end();
        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean handleGuiInput(Scene scene, Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPos();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        imGuiIO.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());

        boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
        if (consumed) {
            Fog fog = scene.getFog();
            fog.setActive(active);
            fog.setDensity(density[0]);
            fog.setColor(new Vector3f(color[0], color[1], color[2]));
            scene.setFog(fog);
        }
        return consumed;
    }
}
