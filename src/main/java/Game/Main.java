package Game;

import Engine.Engine;
import Engine.Objects.Entity;
import Engine.Objects.Model;
import Engine.Objects.ModelLoader;
import Engine.Rendering.*;
import Engine.Rendering.Lighting.*;
import Engine.Rendering.Scene.Camera;
import Engine.Rendering.Scene.Scene;
import Engine.Rendering.Scene.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import Engine.IAppLogic;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IAppLogic {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;

    private AnimationData animationData1;
    private AnimationData animationData2;
    private Entity cubeEntity1;
    private Entity cubeEntity2;
    private float lightAngle;
    private float rotation;

    public static void main(String[] args) {
        Main main = new Main();
        Window.WindowOptions opts = new Window.WindowOptions();
        opts.antiAliasing = true;
        Engine engine = new Engine("RoseEngine", opts, main);
        engine.start();
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        String terrainModelId = "terrain";
        Model terrainModel = ModelLoader.loadModel(terrainModelId, "resources/models/terrain/terrain.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(terrainModel);
        Entity terrainEntity = new Entity("terrainEntity", terrainModelId);
        terrainEntity.setScale(100.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);

        String treeModelId = "tree";
        Model treeModel = ModelLoader.loadModel(treeModelId, "resources/models/tree/Lowpoly_tree_sample.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(treeModel);
        Entity treeEntity = new Entity("treeEntity", treeModelId);
        treeEntity.setScale(0.5f);
        treeEntity.updateModelMatrix();
        scene.addEntity(treeEntity);

        render.setupData(scene);

        SceneLights sceneLights = new SceneLights();
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        ambientLight.setIntensity(1.0f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);

        DirectLight dirLight = sceneLights.getDirectLight();
        dirLight.setPosition(0, 1, 0);
        dirLight.setIntensity(1.0f);
        scene.setSceneLights(sceneLights);

        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache(),
                scene.getMaterialCache());
        skyBox.getSkyBoxEntity().setScale(100);
        skyBox.getSkyBoxEntity().updateModelMatrix();
        scene.setSkyBox(skyBox);

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.005f));

        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));

        lightAngle = 45.001f;
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
            camera.moveDown(move);
        }
        if(window.isKeyPressed(GLFW_KEY_SPACE)){
            camera.moveUp(move);
        }
        if (window.isKeyPressed(GLFW_KEY_COMMA)) {
            lightAngle -= 2.5f;
            if (lightAngle < -90) {
                lightAngle = -90;
            }
        } else if (window.isKeyPressed(GLFW_KEY_PERIOD)) {
            lightAngle += 2.5f;
            if (lightAngle > 90) {
                lightAngle = 90;
            }
        }

        MouseInput mouseInput = window.getMouseInput();
        if (mouseInput.isRightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }

        SceneLights sceneLights = scene.getSceneLights();
        DirectLight dirLight = sceneLights.getDirectLight();
        double angRad = Math.toRadians(lightAngle);
        dirLight.getDirection().z = (float) Math.sin(angRad);
        dirLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
    }
}
