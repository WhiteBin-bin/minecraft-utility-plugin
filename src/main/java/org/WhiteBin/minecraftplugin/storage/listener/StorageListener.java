package org.WhiteBin.minecraftplugin.storage.listener;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.service.StorageInventoryHolder;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

@RequiredArgsConstructor
public class StorageListener implements Listener {

    private final StorageService storageService;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!storageService.isStorageInventory(event.getInventory())) {
            return;
        }

        StorageInventoryHolder holder = storageService.getStorageHolder(event.getInventory());

        if (holder == null) {
            return;
        }

        storageService.saveStorage(player, holder);
    }
}
