package Engine.Rendering;

import Engine.Objects.*;

public class SkyBox {
    private Material material;
    private Mesh mesh;
    private Entity skyBoxEntity;
    private Model skyBoxModel;

    public SkyBox(String skyBoxModelPath, TextureCache textureCache, MaterialCache materialCache) {
        skyBoxModel = ModelLoader.loadModel("skybox-model", skyBoxModelPath, textureCache, materialCache, false);
        MeshData meshData = skyBoxModel.getMeshDataList().getFirst();
        material = materialCache.getMaterial(meshData.getMaterialIdx());
        mesh = new Mesh(meshData);
        skyBoxModel.getMeshDataList().clear();
        skyBoxEntity = new Entity("skyBoxEntity-entity", skyBoxModel);
    }

    public void cleanuo() {
        mesh.cleanup();
    }

    public Material getMaterial() {
        return material;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Entity getSkyBoxEntity() {
        return skyBoxEntity;
    }

    public Model getSkyBoxModel() {
        return skyBoxModel;
    }
}
