package io.github.thecsdev.betterstats.client.gui.stats.tabs;

import static io.github.thecsdev.betterstats.api.client.gui.stats.widget.PlayerBadgeStatWidget.SIZE;
import static io.github.thecsdev.betterstats.api.util.stats.SUPlayerBadgeStat.getPlayerBadgeStatsByModGroups;
import static io.github.thecsdev.betterstats.client.gui.stats.panel.StatFiltersPanel.FILTER_ID_SEARCH;
import static io.github.thecsdev.tcdcommons.api.client.gui.config.TConfigPanelBuilder.nextPanelBottomY;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.betterstats.api.client.gui.stats.widget.PlayerBadgeStatWidget;
import io.github.thecsdev.betterstats.api.client.registry.BSStatsTabs;
import io.github.thecsdev.betterstats.api.client.util.StatFilterSettings;
import io.github.thecsdev.betterstats.api.util.stats.SUPlayerBadgeStat;
import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.util.TUtils;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.text.Text;

public final class PlayerBadgeStatsTab extends BSStatsTab<SUPlayerBadgeStat>
{
	// ==================================================
	public @Virtual @Override Text getName() { return translatable("tcdcommons.api.badge.playerbadge.plural"); }
	// --------------------------------------------------
	public final @Override boolean isAvailable() { return TCDCommons.getInstance().getConfig().enablePlayerBadges; }
	// ==================================================
	public final @Override void initStats(StatsInitContext initContext)
	{
		final var panel = initContext.getStatsPanel();
		final var stats = initContext.getStatsProvider();
		final var statGroups = getPlayerBadgeStatsByModGroups(stats, getPredicate(initContext.getFilterSettings()));
		
		for(final var statGroup : statGroups.entrySet())
		{
			BSStatsTab.init_groupLabel(panel, literal(TUtils.getModName(statGroup.getKey())));
			init_stats(panel, statGroup.getValue(), widget ->
			{
				if(widget.getStat().value > 0) widget.setOutlineColor(BSStatsTabs.COLOR_SPECIAL);
				else if(!widget.getStat().isEmpty()) widget.setOutlineColor(TPanelElement.COLOR_OUTLINE);
			});
		}
	}
	// --------------------------------------------------
	protected @Virtual Predicate<SUPlayerBadgeStat> getPredicate(StatFilterSettings filterSettings)
	{
		final String sq = filterSettings.getPropertyOrDefault(FILTER_ID_SEARCH, "");
		return stat -> stat.matchesSearchQuery(sq);
	}
	// ==================================================
	public static @Internal void init_stats
	(TPanelElement panel, Collection<SUPlayerBadgeStat> stats, Consumer<PlayerBadgeStatWidget> processWidget)
	{
		final int wmp = panel.getWidth() - (panel.getScrollPadding() * 2); //width minus padding
		int nextX = panel.getScrollPadding();
		int nextY = nextPanelBottomY(panel) - panel.getY();
		
		for(final SUPlayerBadgeStat stat : stats)
		{
			final var statElement = new PlayerBadgeStatWidget(nextX, nextY, stat);
			panel.addChild(statElement, true);
			if(processWidget != null)
				processWidget.accept(statElement);
			
			nextX += SIZE + GAP;
			if(nextX + SIZE >= wmp)
			{
				nextX = panel.getScrollPadding();
				nextY = (nextPanelBottomY(panel) - panel.getY()) + GAP;
			}
		}
	}
	// ==================================================
}