package org.WhiteBin.minecraftplugin.storage.command;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.WhiteBin.minecraftplugin.storage.service.StorageShareInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

/**
 * 개인 창고 명령어 처리를 담당하는 {@link CommandExecutor} 구현체입니다.
 * <p>
 * 플레이어의 개인 창고 열기, OP의 다른 유저 창고 열기,
 * 개인 창고 확장, 축소, 정렬, 초기화, 공유 명령어를 처리합니다.
 */
@RequiredArgsConstructor
public class StorageCommand implements CommandExecutor {

    private final StorageService storageService;

    /**
     * {@code /storage} 및 {@code /창고} 명령어 실행 요청을 처리합니다.
     * <p>
     * 인자가 없으면 자신의 창고를 열고, 창고 관리 하위 명령어를 처리하며,
     * 그 외 인자는 본인 창고, OP 전용 다른 유저 창고 열기, 공유받은 창고 열기로 처리합니다.
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
            storageService.openStorage(player);
            return true;
        }

        handleSubCommand(player, args);
        return true;
    }

    /**
     * 첫 번째 인자를 기준으로 창고 하위 명령어를 분기합니다.
     * <p>
     * 등록된 하위 명령어가 아니면 대상 플레이어의 창고 열기 요청으로 처리합니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param args 명령어 인자 목록
     */
    private void handleSubCommand(Player player, String[] args) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "expand", "확장" -> {
                if (hasOpPermission(player, "창고 확장은 OP만 할 수 있습니다.")) {
                    handleExpandCommand(player, args);
                }
            }
            case "shrink", "축소" -> {
                if (hasOpPermission(player, "창고 축소는 OP만 할 수 있습니다.")) {
                    handleShrinkCommand(player, args);
                }
            }
            case "sort", "정렬" -> handleSortCommand(player, args);
            case "clear", "초기화" -> handleClearCommand(player, args);
            case "share", "공유" -> handleShareCommand(player, args);
            case "unshare", "공유해제" -> handleUnshareCommand(player, args);
            case "shares", "공유목록" -> handleMySharesCommand(player);
            case "shared", "공유받은목록" -> handleSharedStoragesCommand(player);
            default -> handleTargetStorageCommand(player, args[0]);
        }
    }

    /**
     * 대상 플레이어의 개인 창고 열기 명령어를 처리합니다.
     * <p>
     * 본인, OP, 공유받은 유저만 대상 플레이어의 창고를 열 수 있습니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param targetArgument 창고 소유자로 입력된 플레이어 이름
     */
    private void handleTargetStorageCommand(Player player, String targetArgument) {
        OfflinePlayer target = findTarget(player, targetArgument);

        if (target == null) {
            return;
        }

        if (!player.isOp() && !storageService.canAccessStorage(player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage("공유받은 창고만 열 수 있습니다.");
            return;
        }

        String targetName = getTargetName(target, targetArgument);
        storageService.openStorage(player, target.getUniqueId(), targetName);
    }

    /**
     * 창고 확장 명령어를 처리합니다.
     * <p>
     * 대상 유저 인자가 없으면 본인 창고를 확장하고,
     * 대상 유저 인자가 있으면 해당 유저의 창고를 확장합니다.
     * 이 메서드는 OP 권한 검사를 통과한 뒤 호출됩니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param args 명령어 인자 목록
     */
    private void handleExpandCommand(Player player, String[] args) {
        OfflinePlayer target;
        String fallbackName;

        if (args.length == 1) {
            target = player;
            fallbackName = player.getName();
        } else {
            target = findTarget(player, args[1]);
            fallbackName = args[1];

            if (target == null) {
                return;
            }
        }

        String targetName = getTargetName(target, fallbackName);

        if (!storageService.canExpand(target.getUniqueId())) {
            player.sendMessage(targetName + "님의 창고는 이미 최대 크기입니다.");
            return;
        }

        int expandedSize = storageService.expandStorage(target.getUniqueId());
        player.sendMessage(targetName + "님의 창고가 " + expandedSize + "칸으로 확장되었습니다.");
    }

    /**
     * 창고 축소 명령어를 처리합니다.
     * <p>
     * 대상 유저 인자가 없으면 본인 창고를 축소하고,
     * 대상 유저 인자가 있으면 해당 유저의 창고를 축소합니다.
     * 이 메서드는 OP 권한 검사를 통과한 뒤 호출됩니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param args 명령어 인자 목록
     */
    private void handleShrinkCommand(Player player, String[] args) {
        OfflinePlayer target;
        String fallbackName;

        if (args.length == 1) {
            target = player;
            fallbackName = player.getName();
        } else {
            target = findTarget(player, args[1]);
            fallbackName = args[1];

            if (target == null) {
                return;
            }
        }

        String targetName = getTargetName(target, fallbackName);

        if (!storageService.canShrink(target.getUniqueId())) {
            player.sendMessage(targetName + "님의 창고는 이미 최소 크기입니다.");
            return;
        }

        if (storageService.hasItemsInShrinkRange(target.getUniqueId())) {
            player.sendMessage("축소될 칸에 아이템이 있어 창고를 줄일 수 없습니다.");
            return;
        }

        int shrinkSize = storageService.shrinkStorage(target.getUniqueId());
        player.sendMessage(targetName + "님의 창고가 " + shrinkSize + "칸으로 축소되었습니다.");
    }

    /**
     * 창고 정렬 명령어를 처리합니다.
     * <p>
     * 대상 유저 인자가 없으면 본인 창고를 정렬하고,
     * 대상 유저 인자가 있으면 본인 또는 OP 권한이 있는 경우에만 해당 유저의 창고를 정렬합니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param args 명령어 인자 목록
     */
    private void handleSortCommand(Player player, String[] args) {
        OfflinePlayer target;
        String fallbackName;

        if (args.length == 1) {
            target = player;
            fallbackName = player.getName();
        } else {
            target = findTarget(player, args[1]);
            fallbackName = args[1];

            if (target == null) {
                return;
            }

            if (!isSamePlayer(player, target) && !player.isOp()) {
                player.sendMessage("다른 유저의 창고 정렬은 OP만 할 수 있습니다.");
                return;
            }
        }

        String targetName = getTargetName(target, fallbackName);
        storageService.sortStorage(target.getUniqueId());
        player.sendMessage(targetName + "님의 창고를 정렬했습니다.");
    }

    /**
     * 창고 초기화 명령어를 처리합니다.
     * <p>
     * 대상 유저 인자가 없으면 본인 창고를 초기화하고,
     * 대상 유저 인자가 있으면 본인 또는 OP 권한이 있는 경우에만 해당 유저의 창고를 초기화합니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param args 명령어 인자 목록
     */
    private void handleClearCommand(Player player, String[] args) {
        OfflinePlayer target;
        String fallbackName;

        if (args.length == 1) {
            target = player;
            fallbackName = player.getName();
        } else {
            target = findTarget(player, args[1]);
            fallbackName = args[1];

            if (target == null) {
                return;
            }

            if (!isSamePlayer(player, target) && !player.isOp()) {
                player.sendMessage("다른 유저의 창고 초기화는 OP만 할 수 있습니다.");
                return;
            }
        }

        String targetName = getTargetName(target, fallbackName);
        storageService.clearStorage(target.getUniqueId());
        player.sendMessage(targetName + "님의 창고를 초기화했습니다.");
    }

    /**
     * 창고 공유 명령어를 처리합니다.
     * <p>
     * 본인의 개인 창고를 대상 플레이어에게 공유합니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param args 명령어 인자 목록
     */
    private void handleShareCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("사용법: /storage share <player>");
            return;
        }

        OfflinePlayer target = findTarget(player, args[1]);

        if (target == null) {
            return;
        }

        if (isSamePlayer(player, target)) {
            player.sendMessage("자기 자신에게는 창고를 공유할 수 없습니다.");
            return;
        }

        String targetName = getTargetName(target, args[1]);
        boolean shared = storageService.shareStorage(player.getUniqueId(), player.getName(), target.getUniqueId(), targetName);

        if (!shared) {
            player.sendMessage(targetName + "님에게 이미 창고를 공유하고 있습니다.");
            return;
        }

        player.sendMessage(targetName + "님에게 창고를 공유했습니다.");
    }

    /**
     * 창고 공유 해제 명령어를 처리합니다.
     * <p>
     * 본인의 개인 창고를 대상 플레이어가 더 이상 열 수 없도록 공유를 해제합니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param args 명령어 인자 목록
     */
    private void handleUnshareCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("사용법: /storage unshare <player>");
            return;
        }

        OfflinePlayer target = findTarget(player, args[1]);

        if (target == null) {
            return;
        }

        String targetName = getTargetName(target, args[1]);
        boolean unshared = storageService.unshareStorage(player.getUniqueId(), target.getUniqueId());

        if (!unshared) {
            player.sendMessage(targetName + "님에게 공유 중인 창고가 없습니다.");
            return;
        }

        player.sendMessage(targetName + "님에 대한 창고 공유를 해제했습니다.");
    }

    /**
     * 내가 공유 중인 플레이어 목록을 출력합니다.
     *
     * @param player 명령어를 실행한 플레이어
     */
    private void handleMySharesCommand(Player player) {
        List<StorageShareInfo> sharedUsers = storageService.getSharedUsers(player.getUniqueId());

        if (sharedUsers.isEmpty()) {
            player.sendMessage("공유 중인 유저가 없습니다.");
            return;
        }

        player.sendMessage("[내가 공유 중인 창고]");
        sharedUsers.forEach(sharedUser -> player.sendMessage("- " + sharedUser.name()));
    }

    /**
     * 내가 공유받은 창고 목록을 출력합니다.
     *
     * @param player 명령어를 실행한 플레이어
     */
    private void handleSharedStoragesCommand(Player player) {
        List<StorageShareInfo> sharedStorages = storageService.getSharedStorages(player.getUniqueId());

        if (sharedStorages.isEmpty()) {
            player.sendMessage("공유받은 창고가 없습니다.");
            return;
        }

        player.sendMessage("[내가 공유받은 창고]");
        sharedStorages.forEach(sharedStorage -> player.sendMessage("- " + sharedStorage.name() + "의 창고"));
    }

    /**
     * 명령어 실행자와 대상 유저가 같은 플레이어인지 확인합니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param target 명령어 대상으로 지정된 유저
     * @return UUID가 같으면 {@code true}
     */
    private boolean isSamePlayer(Player player, OfflinePlayer target) {
        return player.getUniqueId().equals(target.getUniqueId());
    }

    /**
     * 플레이어가 OP 권한을 가지고 있는지 확인합니다.
     * <p>
     * 권한이 없으면 전달된 메시지를 플레이어에게 전송합니다.
     *
     * @param player 권한을 확인할 플레이어
     * @param message 권한이 없을 때 전송할 메시지
     * @return OP 권한이 있으면 {@code true}
     */
    private boolean hasOpPermission(Player player, String message) {
        if (player.isOp()) {
            return true;
        }

        player.sendMessage(message);
        return false;
    }

    /**
     * 대상 유저 이름을 반환합니다.
     * <p>
     * 오프라인 유저 이름을 가져올 수 없는 경우 명령어에서 입력된 이름을 대신 사용합니다.
     *
     * @param target 이름을 확인할 대상 유저
     * @param fallbackName 대상 이름이 없을 때 사용할 대체 이름
     * @return 대상 유저 이름 또는 대체 이름
     */
    private String getTargetName(OfflinePlayer target, String fallbackName) {
        return target.getName() == null ? fallbackName : target.getName();
    }

    /**
     * 명령어 인자로 입력된 이름에 해당하는 대상 유저를 찾습니다.
     * <p>
     * 서버에 접속한 적이 없고 현재 온라인 상태도 아닌 유저는 찾을 수 없는 대상으로 처리합니다.
     *
     * @param player 명령어를 실행한 플레이어
     * @param targetName 찾을 대상 유저 이름
     * @return 찾은 대상 유저 또는 {@code null}
     */
    private OfflinePlayer findTarget(Player player, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage("해당 유저를 찾을 수 없습니다.");
            return null;
        }

        return target;
    }
}
