package Game;

import Engine.Engine;
import Engine.GUI.TerrainSettings;
import Engine.Objects.*;
import Engine.Rendering.*;
import Engine.Rendering.Lighting.*;
import Engine.Rendering.Scene.Camera;
import Engine.Rendering.Scene.Scene;
import Engine.Rendering.Scene.Window;
import Engine.Terrain.TerrainGenerator;
import Engine.Terrain.TerrainManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import Engine.IAppLogic;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IAppLogic {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static float MOVEMENT_SPEED = 0.01f;

    TerrainManager terrainManager = new TerrainManager();
    Model terrainModel;
    Entity terrainEntity;
    Entity treeEntity;
    private float lightAngle;
    private Vector2f lastCameraChunkPos = new Vector2f();

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
        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));

        Material terrainMaterial = new Material();
        terrainMaterial.setAmbientColor(new Vector4f(0.2f, 0.3f, 0.1f, 1.0f));
        terrainMaterial.setDiffuseColor(new Vector4f(0.2f, 0.4f, 0.1f, 1.0f));
        terrainMaterial.setSpecularColor(new Vector4f(0.3f, 0.6f, 0.2f, 1.0f));
        terrainMaterial.setReflectance(0.01f);
        MaterialCache.addMaterial(terrainMaterial);

        terrainModel = new Model("terrain", terrainManager.getVisibleChunks(camera.getPosition()), List.of());
        scene.addModel(terrainModel);
        terrainEntity = new Entity("terrainEntity", terrainModel);
        terrainEntity.setScale(1.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);

        String treeModelId = "tree";
        Model treeModel = ModelLoader.loadModel(treeModelId, "resources/models/tree/Lowpoly_tree_sample.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(treeModel);
        treeEntity = new Entity("treeEntity", treeModel);
        treeEntity.setPosition(2, 2,1);
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

        lightAngle = 45.001f;
        lastCameraChunkPos = getChunkPosition(camera.getPosition());
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
        if(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)){
            camera.moveDown(move);
        }
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
            MOVEMENT_SPEED = 0.05f;
        }
        if(window.isKeyPressed(GLFW_KEY_SPACE)){
            camera.moveUp(move);
        }
        if(window.isKeyPressed(GLFW_KEY_H)){
            scene.removeModel(treeEntity.getModel());

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
        Camera camera = scene.getCamera();
        Vector2f currentCameraChunkPos = getChunkPosition(camera.getPosition());

        // Check if the camera moved to a new chunk
        if (!currentCameraChunkPos.equals(lastCameraChunkPos)) {
            // Update the terrain chunks
            scene.removeModel(terrainEntity.getModel());
            scene.removeEntity(terrainEntity);

            List<MeshData> visibleChunks = terrainManager.getVisibleChunks(camera.getPosition());
            terrainModel = new Model("terrain", visibleChunks, List.of());
            System.out.println("Number of visible chunks: " + visibleChunks.size());
            scene.addModel(terrainModel);

            // Update the entity's model with the new terrain model
            terrainEntity.setModel(terrainModel);

            // Update the entity's model matrix
            terrainEntity.updateModelMatrix();
            scene.addEntity(terrainEntity);
            // Update the last known camera chunk position
            lastCameraChunkPos.set(currentCameraChunkPos);
            System.out.println("Camera position: " + camera.getPosition());
            System.out.println("Current chunk position: " + currentCameraChunkPos);
        }
    }

    // Helper method to get the chunk position based on camera position
    private Vector2f getChunkPosition(Vector3f cameraPos) {
        int chunkX = (int) Math.floor(cameraPos.x / TerrainGenerator.CHUNK_SIZE);
        int chunkZ = (int) Math.floor(cameraPos.z / TerrainGenerator.CHUNK_SIZE);
        return new Vector2f(chunkX, chunkZ);
    }
}
