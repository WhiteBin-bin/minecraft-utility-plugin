package org.WhiteBin.minecraftplugin.economy.sidebar;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.math.BigDecimal;

/**
 * 플레이어 오른쪽 사이드바에 경제 잔액을 표시합니다.
 */
public class EconomySidebar {

    private static final String BALANCE_OBJECTIVE_NAME = "balance";
    private static final String BALANCE_SCORE_NAME = " ";

    /**
     * 플레이어 오른쪽 사이드바에 전달된 잔액을 표시합니다.
     *
     * @param player 사이드바를 표시할 플레이어
     * @param balance 표시할 잔액
     */
    public void showBalance(Player player, BigDecimal balance) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(BALANCE_OBJECTIVE_NAME, Criteria.DUMMY, Component.text("돈"));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore(BALANCE_SCORE_NAME).setScore(balance.intValue());
        player.setScoreboard(scoreboard);
    }
}
