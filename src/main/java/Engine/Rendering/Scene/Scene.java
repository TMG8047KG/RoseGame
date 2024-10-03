package Engine.Rendering.Scene;

import Engine.IGuiInstance;
import Engine.Objects.Entity;
import Engine.Objects.MaterialCache;
import Engine.Objects.Model;
import Engine.Rendering.Projection;
import Engine.Rendering.Fog;
import Engine.Rendering.Lighting.SceneLights;
import Engine.Rendering.SkyBox;
import Engine.Rendering.TextureCache;

import java.util.HashMap;
import java.util.Map;

public class Scene {
    private Camera camera;
    private Fog fog;
    private IGuiInstance guiInstance;
    private MaterialCache materialCache;
    private Map<String, Model> modelMap;
    private Projection projection;
    private SceneLights sceneLights;
    private SkyBox skyBox;
    private TextureCache textureCache;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        materialCache = new MaterialCache();
        camera = new Camera();
        fog = new Fog();
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModel().getId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public void removeEntity(Entity entity){
        modelMap.remove(entity.getModel().getId());
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void removeModel(Model model) {
        modelMap.remove(model.getId());
    }

    public Camera getCamera() {
        return camera;
    }

    public Fog getFog() {
        return fog;
    }

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public MaterialCache getMaterialCache() {
        return materialCache;
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }


    public Projection getProjection() {
        return projection;
    }

    public SceneLights getSceneLights() {
        return sceneLights;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public void setSceneLights(SceneLights sceneLights) {
        this.sceneLights = sceneLights;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }
}
