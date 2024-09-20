package Engine.GUI;

import Engine.IGuiInstance;
import Engine.Objects.*;
import Engine.Rendering.Lighting.*;
import Engine.Rendering.MouseInput;
import Engine.Rendering.Scene.Scene;
import Engine.Rendering.Scene.Window;
import Engine.Terrain.TerrainGenerator;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Random;

public class TerrainSettings implements IGuiInstance {

    private int[] size;
    private float[] scale;
    private float[] mheight;
    private ImInt seed;
    protected Scene scene;

    public TerrainSettings(Scene scene) {
        size = new int[]{200};
        scale = new float[]{200f};
        mheight = new float[]{1000f};
        seed = new ImInt(0);
        this.scene = scene;
    }

    @Override
    public void drawGui() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(450, 400, ImGuiCond.FirstUseEver);

        ImGui.begin("Terrain Settings");
        ImGui.sliderInt("Size", size, 1, 10000);
        ImGui.sliderFloat("Scale", scale, 1, 10000);
        ImGui.sliderFloat("Max Height", mheight, 1, 10000);
        ImGui.beginGroup();
        ImGui.inputInt("Seed", seed);
        if(ImGui.button("Random Seed")){
            Random rand = new Random();
            seed = new ImInt(rand.nextInt(10000));
        }
        ImGui.endGroup();
        if(ImGui.button("Generate Terrain")){
           //
        }

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
            //Nothing
        }
        return false;
    }
}
