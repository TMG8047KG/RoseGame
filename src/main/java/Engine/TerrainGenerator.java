package Engine;

import Engine.Objects.MeshData;
import org.joml.Vector3f;

import java.util.Random;

public class TerrainGenerator {
    private static final int SIZE = 128; // Size of the terrain
    private static final int SCALE = 10; // Scale of the noise

    public static MeshData generateTerrain() {
        float[] positions = new float[SIZE * SIZE * 3];
        float[] normals = new float[SIZE * SIZE * 3];
        float[] tangents = new float[SIZE * SIZE * 3]; // Placeholder for tangents
        float[] bitangents = new float[SIZE * SIZE * 3]; // Placeholder for bitangents
        float[] textCoords = new float[SIZE * SIZE * 2];
        int[] indices = new int[(SIZE - 1) * (SIZE - 1) * 6];
        int[] boneIndices = new int[SIZE * SIZE * 4]; // Placeholder for bone indices
        float[] weights = new float[SIZE * SIZE * 4]; // Placeholder for weights

        // Generate terrain vertices
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                float height = noise(x * SCALE, y * SCALE); // Replace with a noise function
                positions[(y * SIZE + x) * 3] = x;
                positions[(y * SIZE + x) * 3 + 1] = height;
                positions[(y * SIZE + x) * 3 + 2] = y;

                textCoords[(y * SIZE + x) * 2] = (float) x / SIZE;
                textCoords[(y * SIZE + x) * 2 + 1] = (float) y / SIZE;
            }
        }

        // Generate indices
        int offset = 0;
        for (int y = 0; y < SIZE - 1; y++) {
            for (int x = 0; x < SIZE - 1; x++) {
                int topLeft = y * SIZE + x;
                int topRight = topLeft + 1;
                int bottomLeft = (y + 1) * SIZE + x;
                int bottomRight = bottomLeft + 1;

                indices[offset++] = topLeft;
                indices[offset++] = bottomLeft;
                indices[offset++] = topRight;

                indices[offset++] = topRight;
                indices[offset++] = bottomLeft;
                indices[offset++] = bottomRight;
            }
        }

        Vector3f aabbMin = new Vector3f(0, 0, 0);
        Vector3f aabbMax = new Vector3f(SIZE, SIZE, SIZE);
        return new MeshData(positions, normals, tangents, bitangents, textCoords, indices, boneIndices, weights, aabbMin, aabbMax);
    }

    private static float noise(float x, float y) {
        Random random = new Random();
        return (float) random.nextGaussian(); // Replace with a proper noise function
    }
}
