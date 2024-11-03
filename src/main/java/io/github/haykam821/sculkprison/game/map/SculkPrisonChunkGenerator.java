package io.github.haykam821.sculkprison.game.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.haykam821.sculkprison.Main;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structure.StructurePosition;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

public final class SculkPrisonChunkGenerator extends GameChunkGenerator {
	private static final BlockPos ORIGIN = new BlockPos(0, 64, 0);

	private static final int MAX_DEPTH = 16;
	private static final int MAX_DISTANCE_FROM_CENTER = 1024;

	private static final Identifier PRISON_STARTS_ID = new Identifier(Main.MOD_ID, "prison_starts");
	private static final RegistryKey<StructurePool> PRISON_STARTS = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, PRISON_STARTS_ID);

	private final Long2ObjectMap<List<StructurePiece>> piecesByChunk = new Long2ObjectOpenHashMap<>();

	public SculkPrisonChunkGenerator(MinecraftServer server) {
		super(server);

		DynamicRegistryManager registryManager = server.getRegistryManager();
		StructureTemplateManager structureManager = server.getStructureTemplateManager();

		NoiseConfig noiseConfig = null;

		long seed = RandomSeed.getSeed();
		ChunkRandom random = ForcedBlockRotationRandom.of(seed, BlockRotation.NONE);

		ChunkPos chunkPos = new ChunkPos(ORIGIN);
		HeightLimitView heightLimitView = new ChunkGeneratorHeightLimitView(this);

		Structure.Context context = new Structure.Context(registryManager, this, this.getBiomeSource(), noiseConfig, structureManager, random, seed, chunkPos, heightLimitView, biome -> true);
		RegistryEntry<StructurePool> structurePool = registryManager.get(RegistryKeys.TEMPLATE_POOL).getEntry(PRISON_STARTS).orElseThrow();

		StructurePosition structurePosition = StructurePoolBasedGenerator.generate(context, structurePool, Optional.empty(), MAX_DEPTH, ORIGIN, false, Optional.empty(), MAX_DISTANCE_FROM_CENTER).orElseThrow();
		this.addStructurePieces(structurePosition.generate().toList().pieces());
	}

	private void addStructurePieces(List<StructurePiece> pieces) {
		for (StructurePiece piece : pieces) {
			BlockBox box = piece.getBoundingBox();

			int minChunkX = ChunkSectionPos.getSectionCoord(box.getMinX());
			int minChunkZ = ChunkSectionPos.getSectionCoord(box.getMinZ());

			int maxChunkX = ChunkSectionPos.getSectionCoord(box.getMaxX());
			int maxChunkZ = ChunkSectionPos.getSectionCoord(box.getMaxZ());

			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ += 1) {
				for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX += 1) {
					long chunkPos = ChunkPos.toLong(chunkX, chunkZ);

					List<StructurePiece> piecesByChunk = this.piecesByChunk.computeIfAbsent(chunkPos, p -> new ArrayList<>());
					piecesByChunk.add(piece);
				}
			}
		}
	}

	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structures) {
		if (this.piecesByChunk.isEmpty()) {
			return;
		}

		ChunkPos chunkPos = chunk.getPos();
		List<StructurePiece> pieces = this.piecesByChunk.remove(chunkPos.toLong());

		if (pieces != null) {
			BlockBox chunkBox = getChunkBox(chunk);

			for (StructurePiece piece : pieces) {
				piece.generate(world, structures, this, world.getRandom(), chunkBox, chunkPos, ORIGIN);
			}
		}
	}

	private static BlockBox getChunkBox(Chunk chunk) {
		ChunkPos pos = chunk.getPos();

		int minX = pos.getStartX();
		int minY = chunk.getBottomY();
		int minZ = pos.getStartZ();

		int maxX = pos.getEndX();
		int maxY = chunk.getTopY();
		int maxZ = pos.getEndZ();

		return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
