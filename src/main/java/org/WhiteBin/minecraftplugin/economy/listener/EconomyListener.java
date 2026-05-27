package org.WhiteBin.minecraftplugin.economy.listener;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.economy.sidebar.EconomySidebar;
import org.WhiteBin.minecraftplugin.economy.service.EconomyService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.math.BigDecimal;

/**
 * 플레이어 접속 시 경제 계좌를 준비하고 사이드바에 잔액을 표시하는 리스너입니다.
 */
@RequiredArgsConstructor
public class EconomyListener implements Listener {

    private final EconomyService economyService;
    private final EconomySidebar economySidebar = new EconomySidebar();

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
        economySidebar.showBalance(player, balance);
    }
}
