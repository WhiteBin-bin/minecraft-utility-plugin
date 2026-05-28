package org.WhiteBin.minecraftplugin.shop.command;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.shop.inventory.ShopGuiFactory;
import org.WhiteBin.minecraftplugin.shop.model.ShopInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopTransactionLog;
import org.WhiteBin.minecraftplugin.shop.service.ShopService;
import org.WhiteBin.minecraftplugin.shop.service.ShopTransactionLogService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * GUI 상점 명령어를 처리하는 {@link CommandExecutor} 구현체입니다.
 */
@RequiredArgsConstructor
public class ShopCommand implements CommandExecutor, TabCompleter {

    private static final DateTimeFormatter LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_LOG_DISPLAY_COUNT = 10;

    private final ShopService shopService;
    private final ShopTransactionLogService shopTransactionLogService;
    private final ShopGuiFactory shopGuiFactory = new ShopGuiFactory();
    private final ShopTabCompletion shopTabCompletion = new ShopTabCompletion();

    /**
     * {@code /shop} 및 {@code /상점} 명령어 실행 요청을 처리합니다.
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
            sendShopList(player);
            return true;
        }

        handleSubCommand(player, args);
        return true;
    }

    /**
     * {@code /shop} 및 {@code /상점} 명령어의 자동완성 후보를 반환합니다.
     *
     * @param sender 명령어를 입력 중인 주체
     * @param command 자동완성 중인 명령어 객체
     * @param label 사용자가 입력한 명령어 라벨
     * @param args 현재 입력된 명령어 인자 목록
     * @return 자동완성 후보 목록
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return shopTabCompletion.complete(sender.isOp(), label.equalsIgnoreCase("상점"), args, shopService.getShopNames());
    }

    private void handleSubCommand(Player player, String[] args) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create", "생성" -> handleCreateCommand(player, args);
            case "delete", "삭제" -> handleDeleteCommand(player, args);
            case "add", "추가" -> handleAddCommand(player, args);
            case "remove", "제거" -> handleRemoveCommand(player, args);
            case "logs", "로그" -> handleLogsCommand(player, args);
            case "list", "목록" -> sendShopList(player);
            default -> openShop(player, args[0]);
        }
    }

    private void handleCreateCommand(Player player, String[] args) {
        if (!hasOpPermission(player)) {
            return;
        }

        if (args.length < 2) {
            player.sendMessage("사용법: /shop create <shop>");
            return;
        }

        if (!shopService.createShop(args[1])) {
            player.sendMessage("이미 존재하는 상점입니다.");
            return;
        }

        player.sendMessage(args[1] + " 상점을 생성했습니다.");
    }

    private void handleDeleteCommand(Player player, String[] args) {
        if (!hasOpPermission(player)) {
            return;
        }

        if (args.length < 2) {
            player.sendMessage("사용법: /shop delete <shop>");
            return;
        }

        if (!shopService.deleteShop(args[1])) {
            player.sendMessage("상점을 찾을 수 없습니다.");
            return;
        }

        player.sendMessage(args[1] + " 상점을 삭제했습니다.");
    }

    private void handleAddCommand(Player player, String[] args) {
        if (!hasOpPermission(player)) {
            return;
        }

        if (args.length < 3) {
            openShopEditor(player, args);
            return;
        }

        BigDecimal buyPrice = parsePositiveAmount(player, args[2]);

        if (buyPrice == null) {
            return;
        }

        BigDecimal sellPrice = args.length >= 4 ? parsePositiveAmount(player, args[3]) : buyPrice;

        if (sellPrice == null) {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().isAir() || itemStack.getAmount() <= 0) {
            player.sendMessage("손에 든 아이템이 없습니다.");
            return;
        }

        ShopItemInfo itemInfo = shopService.addItem(args[1], itemStack, buyPrice, sellPrice);

        if (itemInfo == null) {
            player.sendMessage("상점을 찾을 수 없거나 상점이 가득 찼습니다.");
            return;
        }

        player.sendMessage(args[1] + " 상점 " + itemInfo.slot() + "번 슬롯에 상품을 추가했습니다.");
    }

    private void handleRemoveCommand(Player player, String[] args) {
        if (!hasOpPermission(player)) {
            return;
        }

        if (args.length < 3) {
            player.sendMessage("사용법: /shop remove <shop> <slot>");
            return;
        }

        Integer slot = parseSlot(player, args[2]);

        if (slot == null) {
            return;
        }

        if (!shopService.removeItem(args[1], slot)) {
            player.sendMessage("상점 또는 상품을 찾을 수 없습니다.");
            return;
        }

        player.sendMessage(args[1] + " 상점 " + slot + "번 슬롯의 상품을 제거했습니다.");
    }

    private void openShop(Player player, String shopName) {
        ShopInfo shopInfo = shopService.getShop(shopName);

        if (shopInfo == null) {
            player.sendMessage("상점을 찾을 수 없습니다.");
            return;
        }

        player.openInventory(shopGuiFactory.createShopInventory(shopInfo));
    }

    private void handleLogsCommand(Player player, String[] args) {
        if (!hasOpPermission(player)) {
            return;
        }

        if (args.length < 2) {
            player.sendMessage("사용법: /shop logs <shop> 또는 /shop logs player <player>");
            return;
        }

        if (isPlayerLogArgument(args[1])) {
            sendPlayerLogs(player, args);
            return;
        }

        sendShopLogs(player, args[1]);
    }

    private void sendShopLogs(Player player, String shopName) {
        List<ShopTransactionLog> logs = shopTransactionLogService.findByShopName(shopName);

        if (logs.isEmpty()) {
            player.sendMessage("상점 거래 로그가 없습니다.");
            return;
        }

        player.sendMessage("[상점 거래 로그: " + shopName + "]");
        logs.stream()
                .limit(MAX_LOG_DISPLAY_COUNT)
                .forEach(log -> player.sendMessage(formatLog(log)));
    }

    private void sendPlayerLogs(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("사용법: /shop logs player <player>");
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[2]);
        List<ShopTransactionLog> logs = shopTransactionLogService.findByPlayerUuid(targetPlayer.getUniqueId());

        if (logs.isEmpty()) {
            player.sendMessage("플레이어 상점 거래 로그가 없습니다.");
            return;
        }

        player.sendMessage("[플레이어 상점 거래 로그: " + args[2] + "]");
        logs.stream()
                .limit(MAX_LOG_DISPLAY_COUNT)
                .forEach(log -> player.sendMessage(formatLog(log)));
    }

    private boolean isPlayerLogArgument(String argument) {
        return argument.equalsIgnoreCase("player") || argument.equals("유저");
    }

    private String formatLog(ShopTransactionLog log) {
        return "- "
                + log.occurredAt().format(LOG_TIME_FORMATTER)
                + " "
                + log.type()
                + " "
                + log.shopName()
                + " "
                + log.itemName()
                + " x"
                + log.quantity()
                + " 개당 "
                + log.unitPrice().stripTrailingZeros().toPlainString()
                + "원 총 "
                + log.totalPrice().stripTrailingZeros().toPlainString()
                + "원 "
                + log.playerName();
    }

    private void openShopEditor(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("사용법: /shop add <shop>");
            return;
        }

        ShopInfo shopInfo = shopService.getShop(args[1]);

        if (shopInfo == null) {
            player.sendMessage("상점을 찾을 수 없습니다.");
            return;
        }

        player.openInventory(shopGuiFactory.createShopEditInventory(shopInfo));
    }

    private void sendShopList(Player player) {
        List<String> shopNames = shopService.getShopNames();

        if (shopNames.isEmpty()) {
            player.sendMessage("생성된 상점이 없습니다.");
            return;
        }

        player.sendMessage("[상점 목록]");
        shopNames.forEach(shopName -> player.sendMessage("- " + shopName));
    }

    private boolean hasOpPermission(Player player) {
        if (player.isOp()) {
            return true;
        }

        player.sendMessage("상점 관리는 OP만 할 수 있습니다.");
        return false;
    }

    private BigDecimal parsePositiveAmount(Player player, String amountArgument) {
        try {
            BigDecimal amount = new BigDecimal(amountArgument);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                player.sendMessage("가격은 0보다 커야 합니다.");
                return null;
            }

            return amount;
        } catch (NumberFormatException e) {
            player.sendMessage("가격은 숫자로 입력해야 합니다.");
            return null;
        }
    }

    private Integer parseSlot(Player player, String slotArgument) {
        try {
            int slot = Integer.parseInt(slotArgument);

            if (slot < 0 || slot >= 54) {
                player.sendMessage("슬롯은 0부터 53까지 입력할 수 있습니다.");
                return null;
            }

            return slot;
        } catch (NumberFormatException e) {
            player.sendMessage("슬롯은 숫자로 입력해야 합니다.");
            return null;
        }
    }
}
