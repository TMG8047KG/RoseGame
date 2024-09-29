package Engine.Terrain;

import Engine.Objects.MeshData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TerrainGenerator {
    public static final int CHUNK_SIZE = 64; // Size of each chunk
    private static final float SCALE = 500.0f;
    private static final float MAX_HEIGHT = 300.0f;
    private static final int OCTAVES = 6; // For noise generation

    // HashMap to store generated terrain chunks by their coordinates
    private Map<Vector2f, MeshData> chunkCache = new HashMap<>();

    public MeshData getChunk(Vector2f chunkPos) {
        if (!chunkCache.containsKey(chunkPos)) {
            // Generate the chunk if it's not in the cache
            MeshData chunk = generateChunk(chunkPos);
            chunkCache.put(chunkPos, chunk);
        }
        return chunkCache.get(chunkPos);
    }

    private MeshData generateChunk(Vector2f chunkPos) {
        Random rnd = new Random(123);
        int vertexCount = CHUNK_SIZE * CHUNK_SIZE;
        float[] positions = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] tangents = new float[vertexCount * 3];
        float[] bitangents = new float[vertexCount * 3];
        float[] textCoords = new float[vertexCount * 2];
        int[] indices = new int[(CHUNK_SIZE - 1) * (CHUNK_SIZE - 1) * 6];
        int[] boneIndices = new int[vertexCount * 4];
        float[] weights = new float[vertexCount * 4];

        // Generate terrain vertices for the chunk
        for (int z = 0; z < CHUNK_SIZE; z++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                int index = (z * CHUNK_SIZE + x) * 3;

                // Ensure worldX and worldZ are aligned at chunk borders
                float worldX = (chunkPos.x * (CHUNK_SIZE - 1)) + x;
                float worldZ = (chunkPos.y * (CHUNK_SIZE - 1)) + z;
                float worldY = (CHUNK_SIZE - 1.2f);

                float height = generateHeight(worldX, worldY, worldZ, 8004);
                positions[index] = worldX;
                positions[index + 1] = height;
                positions[index + 2] = worldZ;

                int texIndex = (z * CHUNK_SIZE + x) * 2;
                textCoords[texIndex] = (float) x / CHUNK_SIZE;
                textCoords[texIndex + 1] = (float) z / CHUNK_SIZE;
            }
        }

        // Generate indices for chunk faces
        generateIndices(indices);

        // Calculate normals, tangents, bitangents
        calculateNormalsTangentsBitangents(positions, normals, tangents, bitangents, textCoords, indices);

        // Return the generated mesh data for the chunk
        Vector3f aabbMin = new Vector3f(chunkPos.x * CHUNK_SIZE, 0, chunkPos.y * CHUNK_SIZE);
        Vector3f aabbMax = new Vector3f((chunkPos.x + 1) * CHUNK_SIZE, MAX_HEIGHT, (chunkPos.y + 1) * CHUNK_SIZE);
        return new MeshData(positions, normals, tangents, bitangents, textCoords, indices, boneIndices, weights, aabbMin, aabbMax);
    }


    private float generateHeight(float x, float y, float z, long seed) {
        float height = 0.0f;
        float frequency = 1.0f;
        float amplitude = 1.2f;
        float persistence = 0.5f;
        float lacunarity = 2.0f;

        for (int i = 0; i < OCTAVES; i++) {
            height += OpenSimplex2S.noise3_ImproveXZ(seed, x * frequency / SCALE, y, z * frequency / SCALE) * amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }

        float ridge = 1.0f - Math.abs(height);
        height = ridge * height;
        height = (float) Math.pow(height, 3);
        return height * MAX_HEIGHT;
    }

    private static void generateIndices(int[] indices) {
        int pointer = 0;
        for (int z = 0; z < CHUNK_SIZE - 1; z++) {
            for (int x = 0; x < CHUNK_SIZE - 1; x++) {
                int topLeft = (z * CHUNK_SIZE) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * CHUNK_SIZE) + x;
                int bottomRight = bottomLeft + 1;

                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;

                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
    }

    /**
     * Calculates normals, tangents, and bitangents for the mesh.
     */
    private static void calculateNormalsTangentsBitangents(
            float[] positions, float[] normals, float[] tangents, float[] bitangents,
            float[] texCoords, int[] indices) {
        for (int i = 0; i < indices.length; i += 3) {
            int idx0 = indices[i] * 3;
            int idx1 = indices[i + 1] * 3;
            int idx2 = indices[i + 2] * 3;

            Vector3f v0 = new Vector3f(positions[idx0], positions[idx0 + 1], positions[idx0 + 2]);
            Vector3f v1 = new Vector3f(positions[idx1], positions[idx1 + 1], positions[idx1 + 2]);
            Vector3f v2 = new Vector3f(positions[idx2], positions[idx2 + 1], positions[idx2 + 2]);

            // Calculate edges
            Vector3f edge1 = new Vector3f(v1).sub(v0);
            Vector3f edge2 = new Vector3f(v2).sub(v0);

            // Calculate normal
            Vector3f normal = new Vector3f(edge1).cross(edge2).normalize();

            // Add the normal to each vertex
            addToVector(normals, idx0, normal);
            addToVector(normals, idx1, normal);
            addToVector(normals, idx2, normal);

            // Calculate tangent and bitangent
            int idx0Tex = indices[i] * 2;
            int idx1Tex = indices[i + 1] * 2;
            int idx2Tex = indices[i + 2] * 2;

            Vector2f tex0 = new Vector2f(texCoords[idx0Tex], texCoords[idx0Tex + 1]);
            Vector2f tex1 = new Vector2f(texCoords[idx1Tex], texCoords[idx1Tex + 1]);
            Vector2f tex2 = new Vector2f(texCoords[idx2Tex], texCoords[idx2Tex + 1]);

            Vector2f deltaTex1 = new Vector2f(tex1).sub(tex0);
            Vector2f deltaTex2 = new Vector2f(tex2).sub(tex0);

            float r = (deltaTex1.x * deltaTex2.y - deltaTex1.y * deltaTex2.x);
            if (r == 0.0f) r = 1.0f; // Prevent division by zero
            r = 1.0f / r;

            Vector3f tangent = new Vector3f(edge1).mul(deltaTex2.y).sub(new Vector3f(edge2).mul(deltaTex1.y)).mul(r).normalize();
            Vector3f bitangent = new Vector3f(edge2).mul(deltaTex1.x).sub(new Vector3f(edge1).mul(deltaTex2.x)).mul(r).normalize();

            // Add tangent and bitangent to each vertex
            addToVector(tangents, idx0, tangent);
            addToVector(tangents, idx1, tangent);
            addToVector(tangents, idx2, tangent);

            addToVector(bitangents, idx0, bitangent);
            addToVector(bitangents, idx1, bitangent);
            addToVector(bitangents, idx2, bitangent);
        }

        // Normalize normals, tangents, and bitangents
        normalizeVectors(normals);
        normalizeVectors(tangents);
        normalizeVectors(bitangents);
    }

    /**
     * Adds a vector to the existing vector in the array.
     */
    private static void addToVector(float[] array, int index, Vector3f vec) {
        array[index] += vec.x;
        array[index + 1] += vec.y;
        array[index + 2] += vec.z;
    }

    /**
     * Normalizes all vectors in the array.
     */
    private static void normalizeVectors(float[] array) {
        for (int i = 0; i < array.length; i += 3) {
            Vector3f vec = new Vector3f(array[i], array[i + 1], array[i + 2]);
            vec.normalize();
            array[i] = vec.x;
            array[i + 1] = vec.y;
            array[i + 2] = vec.z;
        }
    }
}
