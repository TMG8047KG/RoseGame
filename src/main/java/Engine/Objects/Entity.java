package Engine.Objects;

import Engine.Rendering.AnimationData;
import Engine.Objects.Model;
import org.joml.*;

public class Entity {
    private final String id;
    private Model model; // Change from modelId to Model reference
    private AnimationData animationData;
    private Matrix4f modelMatrix;
    private Vector3f position;
    private Quaternionf rotation;
    private float scale;

    public Entity(String id, Model model) {
        this.id = id;
        this.model = model; // Store the Model object directly
        modelMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1;
    }

    public AnimationData getAnimationData() {
        return animationData;
    }

    public String getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model; // Allow updating the Model
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    public final void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(float x, float y, float z, float angle) {
        this.rotation.fromAxisAngleRad(x, y, z, angle);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void updateModelMatrix() {
        modelMatrix.translationRotateScale(position, rotation, scale);
    }
}
