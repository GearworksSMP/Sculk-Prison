package io.github.haykam821.sculkprison;

import io.github.haykam821.sculkprison.game.SculkPrisonConfig;
import io.github.haykam821.sculkprison.game.phase.SculkPrisonWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.GameType;

public class Main implements ModInitializer {
	public static final String MOD_ID = "sculkprison";

	private static final Identifier SCULK_PRISON_ID = new Identifier(MOD_ID, "sculk_prison");
	public static final GameType<SculkPrisonConfig> SCULK_PRISON_TYPE = GameType.register(SCULK_PRISON_ID, SculkPrisonConfig.CODEC, SculkPrisonWaitingPhase::open);

	private static final Identifier WARDEN_CAGE_BREAK_BLOCKS_ID = new Identifier(MOD_ID, "warden_cage_break_blocks");
	public static final TagKey<Block> WARDEN_CAGE_BREAK_BLOCKS = TagKey.of(Registries.BLOCK.getKey(), WARDEN_CAGE_BREAK_BLOCKS_ID);

	@Override
	public void onInitialize() {
		return;
	}
}
