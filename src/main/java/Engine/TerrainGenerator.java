package Engine;

import Engine.Objects.MeshData;
import org.joml.SimplexNoise;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Random;

public class TerrainGenerator {
    private static final int SIZE = 64; // Size of the terrain
    private static final int SCALE = 10; // Scale of the noise

    public static MeshData generateTerrain() {
        float[] positions = new float[SIZE * SIZE * 3];
        float[] normals = new float[SIZE * SIZE * 3];
        float[] tangents = new float[SIZE * SIZE * 3];
        float[] bitangents = new float[SIZE * SIZE * 3];
        float[] textCoords = new float[SIZE * SIZE * 3];
        int[] indices = new int[(SIZE - 1) * (SIZE - 1) * 6];
        int[] boneIndices = new int[SIZE * SIZE * 4];
        float[] weights = new float[SIZE * SIZE * 4];

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

        calculateNormalsTangentsBitangents(positions, normals, tangents, bitangents, textCoords, indices);

        Vector3f aabbMin = new Vector3f(0, 0, 0);
        Vector3f aabbMax = new Vector3f(SIZE, SIZE, SIZE);
        return new MeshData(positions, normals, tangents, bitangents, textCoords, indices, boneIndices, weights, aabbMin, aabbMax);
    }

    private static float noise(float x, float y) {
        Random random = new Random();
        return SimplexNoise.noise(x, y);// Replace with a proper noise function
    }

    private static void calculateNormalsTangentsBitangents(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] texCoords, int[] indices) {
        for (int i = 0; i < indices.length; i += 3) {
            int idx0 = indices[i] * 3;
            int idx1 = indices[i + 1] * 3;
            int idx2 = indices[i + 2] * 3;

            Vector3f v0 = new Vector3f(positions[idx0], positions[idx0 + 1], positions[idx0 + 2]);
            Vector3f v1 = new Vector3f(positions[idx1], positions[idx1 + 1], positions[idx1 + 2]);
            Vector3f v2 = new Vector3f(positions[idx2], positions[idx2 + 1], positions[idx2 + 2]);

            // Calculate normal
            Vector3f edge1 = v1.sub(v0);
            Vector3f edge2 = v2.sub(v0);
            Vector3f normal = edge1.cross(edge2).normalize();

            // Add the normal to each vertex
            for (int idx : new int[] {idx0, idx1, idx2}) {
                normals[idx] += normal.x;
                normals[idx + 1] += normal.y;
                normals[idx + 2] += normal.z;
            }

            // Calculate tangent and bitangent
            int idx0Tex = indices[i] * 2;
            int idx1Tex = indices[i + 1] * 2;
            int idx2Tex = indices[i + 2] * 2;

            Vector2f tex0 = new Vector2f(texCoords[idx0Tex], texCoords[idx0Tex + 1]);
            Vector2f tex1 = new Vector2f(texCoords[idx1Tex], texCoords[idx1Tex + 1]);
            Vector2f tex2 = new Vector2f(texCoords[idx2Tex], texCoords[idx2Tex + 1]);

            Vector2f deltaTex1 = tex1.sub(tex0);
            Vector2f deltaTex2 = tex2.sub(tex0);

            float r = 1.0f / (deltaTex1.x * deltaTex2.y - deltaTex1.y * deltaTex2.x);
            Vector3f tangent = edge1.mul(deltaTex2.y).sub(edge2.mul(deltaTex1.y)).mul(r).normalize();
            Vector3f bitangent = edge2.mul(deltaTex1.x).sub(edge1.mul(deltaTex2.x)).mul(r).normalize();

            // Add tangent and bitangent to each vertex
            for (int idx : new int[] {idx0, idx1, idx2}) {
                tangents[idx] += tangent.x;
                tangents[idx + 1] += tangent.y;
                tangents[idx + 2] += tangent.z;

                bitangents[idx] += bitangent.x;
                bitangents[idx + 1] += bitangent.y;
                bitangents[idx + 2] += bitangent.z;
            }
        }

        // Normalize normals, tangents, and bitangents
        for (int i = 0; i < normals.length; i += 3) {
            Vector3f normal = new Vector3f(normals[i], normals[i + 1], normals[i + 2]).normalize();
            normals[i] = normal.x;
            normals[i + 1] = normal.y;
            normals[i + 2] = normal.z;

            Vector3f tangent = new Vector3f(tangents[i], tangents[i + 1], tangents[i + 2]).normalize();
            tangents[i] = tangent.x;
            tangents[i + 1] = tangent.y;
            tangents[i + 2] = tangent.z;

            Vector3f bitangent = new Vector3f(bitangents[i], bitangents[i + 1], bitangents[i + 2]).normalize();
            bitangents[i] = bitangent.x;
            bitangents[i + 1] = bitangent.y;
            bitangents[i + 2] = bitangent.z;
        }
    }
}
