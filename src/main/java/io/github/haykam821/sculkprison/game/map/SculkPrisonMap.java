package io.github.haykam821.sculkprison.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;

public final class SculkPrisonMap {
	public static final Vec3d SPAWN = new Vec3d(7, 65.06250, 4);
	public static final Vec3d WARDEN_SPAWN = new Vec3d(7, 66, 10);
	public static final BlockBounds WARDEN_CAGE = BlockBounds.of(8, 65, 8, 5, 69, 11);

	private final StructureTemplate structure;

	public SculkPrisonMap(StructureTemplate structure) {
		this.structure = structure;
	}
	
	public StructureTemplate getStructure() {
		return this.structure;
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new SculkPrisonChunkGenerator(server, this.structure);
	}
}
