package io.github.haykam821.sculkprison.game.map;

import io.github.haykam821.sculkprison.Main;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;

public class SculkPrisonMapBuilder {
	static final Identifier START_ID = new Identifier(Main.MOD_ID, "start");

	public SculkPrisonMap create(MinecraftServer server) {
		StructureTemplate template = server.getStructureTemplateManager().getTemplate(START_ID).orElseThrow();
		return new SculkPrisonMap(template);
	}
}
