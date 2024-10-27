package io.github.haykam821.sculkprison.game.map;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.sculkprison.Main;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import xyz.nucleoid.fantasy.mixin.MinecraftServerAccess;
import xyz.nucleoid.fantasy.mixin.MinecraftServerMixin;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

public final class SculkPrisonChunkGenerator extends GameChunkGenerator {
	private static final BlockPos ORIGIN = new BlockPos(0, 64, 0);
	private static final OneDirectionRandom RANDOM = new OneDirectionRandom();
	private static final Identifier PRISON_STARTS_ID = new Identifier(Main.MOD_ID, "prison_starts");
	private static final int MAX_DEPTH = 16;

	private final StructureTemplateManager structureTemplateManager;
	private final DynamicRegistryManager registryManager;

	private final Long2ObjectMap<List<StructurePiece>> piecesByChunk = new Long2ObjectOpenHashMap<>();

	/**
	 * Constructor for SculkPrisonChunkGenerator.
	 *
	 * @param server           The Minecraft server instance.
	 * @param initialStructure The initial StructureTemplate (can be null if not used).
	 */
	public SculkPrisonChunkGenerator(MinecraftServer server, StructureTemplate initialStructure) {
		super(server);

		this.registryManager = server.getRegistryManager();

		// Initialize StructureTemplateManager
		this.structureTemplateManager = new StructureTemplateManager(
				server.getResourceManager(),
				((MinecraftServerAccess) server).getSession(),
				server.getDataFixer(),
				Registries.BLOCK.getReadOnlyWrapper()
		);

		List<StructurePiece> pieces = new ArrayList<>();

		// Define StructurePiecesHolder with a custom getIntersecting implementation
		StructurePiecesHolder holder = new StructurePiecesHolder() {
			@Override
			public void addPiece(StructurePiece piece) {
				pieces.add(piece);
			}

			@Override
			public StructurePiece getIntersecting(BlockBox box) {
				// Custom implementation to check for intersecting pieces
				for (StructurePiece piece : pieces) {
					if (piece.getBoundingBox().intersects(box)) {
						return piece;
					}
				}
				return null;
			}
		};

		RANDOM.enforceOneDirectionNext();

		// Generate structures using StructurePoolBasedGenerator
		StructurePoolBasedGenerator.generate(
				this.registryManager,
				config,
				PoolStructurePiece::new,
				this.structureTemplateManager, // Updated to use StructureTemplateManager
				ORIGIN,
				holder,
				RANDOM,
				false,
				false,
				new HeightLimitView() {
					@Override
					public int getBottomY() {
						return 0; // Actual bottom Y level
					}

					@Override
					public int getHeight() {
						return 384; // Updated to actual world height in 1.20.1
					}
				}
		);

		this.addStructurePieces(pieces);
	}

	/**
	 * Adds structure pieces to the chunks they occupy.
	 *
	 * @param pieces List of StructurePiece to add.
	 */
	private void addStructurePieces(List<StructurePiece> pieces) {
		for (StructurePiece piece : pieces) {
			BlockBox box = piece.getBoundingBox();
			int minChunkX = box.getMinX() >> 4;
			int minChunkZ = box.getMinZ() >> 4;
			int maxChunkX = box.getMaxX() >> 4;
			int maxChunkZ = box.getMaxZ() >> 4;

			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
					long chunkPosLong = ChunkPos.toLong(chunkX, chunkZ);
					List<StructurePiece> piecesByChunkList = this.piecesByChunk.computeIfAbsent(chunkPosLong, p -> new ArrayList<>());
					piecesByChunkList.add(piece);
				}
			}
		}
	}

	/**
	 * Generates features for the given chunk.
	 *
	 * @param world      The world access.
	 * @param chunk      The chunk being generated.
	 * @param structures The structure accessor.
	 */
	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structures) {
		if (this.piecesByChunk.isEmpty()) {
			return;
		}

		ChunkPos chunkPos = chunk.getPos();
		long chunkPosLong = chunkPos.toLong();
		List<StructurePiece> pieces = this.piecesByChunk.remove(chunkPosLong);

		if (pieces != null) {
			BlockBox chunkBox = new BlockBox(
					chunkPos.getStartX(),
					0,
					chunkPos.getStartZ(),
					chunkPos.getEndX(),
					384, // Updated to actual world height
					chunkPos.getEndZ()
			);
			for (StructurePiece piece : pieces) {
				piece.generate(world, structures, this, RANDOM, chunkBox, chunkPos, ORIGIN);
			}
		}
	}
}
