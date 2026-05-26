package org.WhiteBin.minecraftplugin.economy.command;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.economy.service.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

/**
 * 플레이어 잔액 조회 명령어를 처리하는 {@link CommandExecutor} 구현체입니다.
 */
@RequiredArgsConstructor
public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final EconomyService economyService;
    private final EconomyTabCompletion economyTabCompletion = new EconomyTabCompletion();

    /**
     * {@code /money} 및 {@code /돈} 명령어 실행 요청을 처리합니다.
     *
     * @param sender 명령어를 실행한 주체
     * @param command 실행된 명령어 객체
     * @param label 사용자가 입력한 명령어 라벨
     * @param args 명령어 인자 목록
     * @return 명령어 처리가 완료되었으면 {@code true}
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            sendBalanceMessage(player, player, player.getName());
            return true;
        }

        handleTargetBalanceCommand(player, args[0]);
        return true;
    }

    /**
     * {@code /money} 및 {@code /돈} 명령어의 자동완성 후보를 반환합니다.
     *
     * @param sender 명령어를 입력 중인 주체
     * @param command 자동완성 중인 명령어 객체
     * @param label 사용자가 입력한 명령어 라벨
     * @param args 현재 입력된 명령어 인자 목록
     * @return 자동완성 후보 목록
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> onlinePlayerNames = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();

        return economyTabCompletion.complete(args, onlinePlayerNames);
    }

    private void handleTargetBalanceCommand(Player player, String targetArgument) {
        OfflinePlayer target = findTarget(player, targetArgument);

        if (target == null) {
            return;
        }

        if (!player.getUniqueId().equals(target.getUniqueId()) && !player.isOp()) {
            player.sendMessage("다른 유저의 잔액 조회는 OP만 할 수 있습니다.");
            return;
        }

        sendBalanceMessage(player, target, targetArgument);
    }

    private void sendBalanceMessage(Player viewer, OfflinePlayer target, String fallbackName) {
        String targetName = target.getName() == null ? fallbackName : target.getName();
        BigDecimal balance = economyService.getBalance(target.getUniqueId(), targetName);

        viewer.sendMessage(targetName + "님의 돈: " + formatBalance(balance));
    }

    private String formatBalance(BigDecimal balance) {
        return balance.stripTrailingZeros().toPlainString();
    }

    private OfflinePlayer findTarget(Player player, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage("해당 유저를 찾을 수 없습니다.");
            return null;
        }

        return target;
    }
}
