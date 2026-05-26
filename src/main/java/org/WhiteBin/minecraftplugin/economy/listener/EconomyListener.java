package org.WhiteBin.minecraftplugin.economy.listener;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.WhiteBin.minecraftplugin.economy.service.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.math.BigDecimal;

/**
 * 플레이어 접속 시 경제 계좌를 준비하고 사이드바에 잔액을 표시하는 리스너입니다.
 */
@RequiredArgsConstructor
public class EconomyListener implements Listener {

    private static final String BALANCE_OBJECTIVE_NAME = "balance";
    private static final String BALANCE_SCORE_NAME = " ";

    private final EconomyService economyService;

    /**
     * 플레이어 접속 시 잔액을 저장하고 사이드바에 표시합니다.
     *
     * @param event 플레이어 접속 이벤트
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BigDecimal balance = economyService.getBalance(player.getUniqueId(), player.getName());

        economyService.setBalance(player.getUniqueId(), player.getName(), balance);
        showBalanceSidebar(player, balance);
    }

    private void showBalanceSidebar(Player player, BigDecimal balance) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(BALANCE_OBJECTIVE_NAME, Criteria.DUMMY, Component.text("돈"));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore(BALANCE_SCORE_NAME).setScore(balance.intValue());
        player.setScoreboard(scoreboard);
    }
}
