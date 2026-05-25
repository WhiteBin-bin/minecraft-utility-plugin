package org.WhiteBin.minecraftplugin.storage.command;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class StorageCommand implements CommandExecutor {

    private final StorageService storageService;

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

        if (!player.isOp()) {
            player.sendMessage("다른 유저의 창고는 OP만 열 수 있습니다.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage("해당 유저를 찾을 수 없습니다.");
            return true;
        }

        String targetName = target.getName() == null ? args[0] : target.getName();
        storageService.openStorage(player, target.getUniqueId(), targetName);
        return true;
    }
}
