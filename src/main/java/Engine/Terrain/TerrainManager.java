package Engine.Terrain;

import Engine.Objects.MeshData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static Engine.Terrain.TerrainGenerator.CHUNK_SIZE;

public class TerrainManager {
    private TerrainGenerator terrainGenerator;
    private int renderDistance = 3; // Render distance in chunks

    public TerrainManager() {
        terrainGenerator = new TerrainGenerator();
    }

    public List<MeshData> getVisibleChunks(Vector3f cameraPos) {
        List<MeshData> visibleChunks = new ArrayList<>();
        int cameraChunkX = (int) Math.floor(cameraPos.x / CHUNK_SIZE);
        int cameraChunkZ = (int) Math.floor(cameraPos.z / CHUNK_SIZE);

        // Load chunks around the camera within the render distance
        for (int z = -renderDistance; z <= renderDistance; z++) {
            for (int x = -renderDistance; x <= renderDistance; x++) {
                Vector2f chunkPos = new Vector2f(cameraChunkX + x, cameraChunkZ + z);
                visibleChunks.add(terrainGenerator.getChunk(chunkPos));
                visibleChunks.getLast().setMaterialIdx(4);
            }
        }
        return visibleChunks;
    }
}
