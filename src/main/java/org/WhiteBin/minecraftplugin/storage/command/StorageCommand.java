package org.WhiteBin.minecraftplugin.storage.command;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 개인 창고 명령어 처리를 담당하는 {@link CommandExecutor} 구현체입니다.
 * <p>
 * 플레이어의 개인 창고 열기, OP의 다른 유저 창고 열기,
 * 개인 창고 확장 및 축소 명령어를 처리합니다.
 */
@RequiredArgsConstructor
public class StorageCommand implements CommandExecutor {

    private final StorageService storageService;

    /**
     * {@code /storage} 및 {@code /창고} 명령어 실행 요청을 처리합니다.
     * <p>
     * 인자가 없으면 자신의 창고를 열고, {@code expand}, {@code shrink}, {@code 확장}, {@code 축소} 인자가 있으면 OP 전용 창고 크기 변경을 수행하며,
     * 그 외 인자는 본인 창고 또는 OP 전용 다른 유저 창고 열기로 처리합니다.
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

        if (isExpandCommand(args[0])) {
            if (!player.isOp()) {
                player.sendMessage("창고 확장은 OP만 할 수 있습니다.");
                return true;
            }

            handleExpandCommand(player, args);
            return true;
        }

        if (isShrinkCommand(args[0])) {
            if (!player.isOp()) {
                player.sendMessage("창고 축소는 OP만 할 수 있습니다.");
                return true;
            }

            handleShrinkCommand(player, args);
            return true;
        }

        OfflinePlayer target = findTarget(player, args[0]);

        if (target == null) {
            return true;
        }

        if (!isSamePlayer(player, target) && !player.isOp()) {
            player.sendMessage("다른 유저의 창고는 OP만 열 수 있습니다.");
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        storageService.openStorage(player, target.getUniqueId(), targetName);
        return true;
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
     * 전달된 인자가 창고 확장 명령어인지 확인합니다.
     *
     * @param argument 확인할 명령어 인자
     * @return {@code expand} 또는 {@code 확장}이면 {@code true}
     */
    private boolean isExpandCommand(String argument) {
        return argument.equalsIgnoreCase("expand") || argument.equals("확장");
    }

    /**
     * 전달된 인자가 창고 축소 명령어인지 확인합니다.
     *
     * @param argument 확인할 명령어 인자
     * @return {@code shrink} 또는 {@code 축소}이면 {@code true}
     */
    private boolean isShrinkCommand(String argument) {
        return argument.equalsIgnoreCase("shrink") || argument.equals("축소");
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
