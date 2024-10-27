package io.github.haykam821.sculkprison.game;

import io.github.haykam821.sculkprison.game.phase.SculkPrisonActivePhase;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.widget.BossBarWidget;

public class SculkPrisonBar {
	private static final Text CAGE_LOCKED_TEXT = Text.translatable("text.sculkprison.cage_locked");
	private static final Text CAGE_UNLOCKED_TEXT = Text.translatable("text.sculkprison.cage_unlocked");

	private final BossBarWidget bar;

	public SculkPrisonBar(GlobalWidgets widgets) {
		this.bar = widgets.addBossBar(CAGE_LOCKED_TEXT, BossBar.Color.BLUE, BossBar.Style.PROGRESS);
	}

	public void tick(SculkPrisonActivePhase phase) {
		this.bar.setProgress(phase.getBarProgress());
	}

	public void changeToSurvive() {
		this.bar.setTitle(CAGE_UNLOCKED_TEXT);
		this.bar.setStyle(BossBar.Color.GREEN, BossBar.Style.PROGRESS);
	}
}
