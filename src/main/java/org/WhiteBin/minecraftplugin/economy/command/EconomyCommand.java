package org.WhiteBin.minecraftplugin.economy.command;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.economy.sidebar.EconomySidebar;
import org.WhiteBin.minecraftplugin.economy.service.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.List;

/**
 * 플레이어 잔액 조회 명령어를 처리하는 {@link CommandExecutor} 구현체입니다.
 */
@RequiredArgsConstructor
public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final EconomyService economyService;
    private final EconomyTabCompletion economyTabCompletion = new EconomyTabCompletion();
    private final EconomySidebar economySidebar = new EconomySidebar();

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

        if (isTransferCommand(args[0])) {
            handleTransferCommand(player, args);
            return true;
        }

        if (isManagementCommand(args[0])) {
            handleManagementCommand(player, args);
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

        return economyTabCompletion.complete(sender.isOp(), label.equalsIgnoreCase("돈"), args, onlinePlayerNames);
    }

    private void handleTransferCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("사용법: /money " + args[0] + " <player> <amount>");
            return;
        }

        OfflinePlayer target = findTarget(player, args[1]);

        if (target == null) {
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage("자기 자신에게는 송금할 수 없습니다.");
            return;
        }

        BigDecimal amount = parsePositiveAmount(player, args[2]);

        if (amount == null) {
            return;
        }

        String targetName = getTargetName(target, args[1]);
        boolean transferred = economyService.transfer(player.getUniqueId(), player.getName(), target.getUniqueId(), targetName, amount);

        if (!transferred) {
            player.sendMessage("돈이 부족합니다.");
            return;
        }

        BigDecimal senderBalance = economyService.getBalance(player.getUniqueId(), player.getName());
        BigDecimal targetBalance = economyService.getBalance(target.getUniqueId(), targetName);

        economySidebar.showBalance(player, senderBalance);
        updateTargetSidebar(target, targetBalance);
        player.sendMessage(targetName + "님에게 " + formatBalance(amount) + "원을 보냈습니다. 현재 돈: " + formatBalance(senderBalance));

        if (target instanceof Player onlineTarget) {
            onlineTarget.sendMessage(player.getName() + "님에게서 " + formatBalance(amount) + "원을 받았습니다. 현재 돈: " + formatBalance(targetBalance));
        }
    }

    private void handleManagementCommand(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("잔액 관리는 OP만 할 수 있습니다.");
            return;
        }

        if (args.length < 3) {
            player.sendMessage("사용법: /money " + args[0] + " <player> <amount>");
            return;
        }

        OfflinePlayer target = findTarget(player, args[1]);

        if (target == null) {
            return;
        }

        BigDecimal amount = parsePositiveAmount(player, args[2]);

        if (amount == null) {
            return;
        }

        String targetName = getTargetName(target, args[1]);

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "give", "지급" -> handleGiveCommand(player, target, targetName, amount);
            case "take", "차감" -> handleTakeCommand(player, target, targetName, amount);
            case "set", "설정" -> handleSetCommand(player, target, targetName, amount);
            default -> {
            }
        }
    }

    private void handleGiveCommand(Player player, OfflinePlayer target, String targetName, BigDecimal amount) {
        BigDecimal balance = economyService.deposit(target.getUniqueId(), targetName, amount);

        updateTargetSidebar(target, balance);
        player.sendMessage(targetName + "님에게 " + formatBalance(amount) + "원을 지급했습니다. 현재 돈: " + formatBalance(balance));
    }

    private void handleTakeCommand(Player player, OfflinePlayer target, String targetName, BigDecimal amount) {
        boolean withdrawn = economyService.withdraw(target.getUniqueId(), targetName, amount);

        if (!withdrawn) {
            player.sendMessage(targetName + "님의 돈이 부족합니다.");
            return;
        }

        BigDecimal balance = economyService.getBalance(target.getUniqueId(), targetName);

        updateTargetSidebar(target, balance);
        player.sendMessage(targetName + "님에게서 " + formatBalance(amount) + "원을 차감했습니다. 현재 돈: " + formatBalance(balance));
    }

    private void handleSetCommand(Player player, OfflinePlayer target, String targetName, BigDecimal amount) {
        economyService.setBalance(target.getUniqueId(), targetName, amount);
        updateTargetSidebar(target, amount);
        player.sendMessage(targetName + "님의 돈을 " + formatBalance(amount) + "원으로 설정했습니다.");
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
        String targetName = getTargetName(target, fallbackName);
        BigDecimal balance = economyService.getBalance(target.getUniqueId(), targetName);

        viewer.sendMessage(targetName + "님의 돈: " + formatBalance(balance));
    }

    private void updateTargetSidebar(OfflinePlayer target, BigDecimal balance) {
        if (target instanceof Player onlineTarget) {
            economySidebar.showBalance(onlineTarget, balance);
        }
    }

    private BigDecimal parsePositiveAmount(Player player, String amountArgument) {
        try {
            BigDecimal amount = new BigDecimal(amountArgument);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                player.sendMessage("금액은 0보다 커야 합니다.");
                return null;
            }

            return amount;
        } catch (NumberFormatException e) {
            player.sendMessage("금액은 숫자로 입력해야 합니다.");
            return null;
        }
    }

    private String formatBalance(BigDecimal balance) {
        return balance.stripTrailingZeros().toPlainString();
    }

    private String getTargetName(OfflinePlayer target, String fallbackName) {
        return target.getName() == null ? fallbackName : target.getName();
    }

    private boolean isManagementCommand(String command) {
        return switch (command.toLowerCase(Locale.ROOT)) {
            case "give", "take", "set", "지급", "차감", "설정" -> true;
            default -> false;
        };
    }

    private boolean isTransferCommand(String command) {
        return switch (command.toLowerCase(Locale.ROOT)) {
            case "pay", "보내기" -> true;
            default -> false;
        };
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
