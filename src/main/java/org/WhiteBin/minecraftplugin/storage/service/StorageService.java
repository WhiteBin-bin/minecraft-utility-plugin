package org.WhiteBin.minecraftplugin.storage.service;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.repository.StorageRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

@RequiredArgsConstructor
public class StorageService {

    private static final String STORAGE_TITLE = "개인 창고";
    private static final int STORAGE_SIZE = 54;

    private final StorageRepository storageRepository;

    public void openStorage(Player player) {
        openStorage(player, player.getUniqueId(), player.getName());
    }

    public void openStorage(Player viewer, UUID ownerUuid, String ownerName) {
        StorageInventoryHolder holder = new StorageInventoryHolder(ownerUuid, ownerName);
        Inventory inventory = Bukkit.createInventory(holder, STORAGE_SIZE, ownerName + "님의 창고");
        holder.setInventory(inventory);

        storageRepository.load(ownerUuid, inventory);

        viewer.openInventory(inventory);
        viewer.sendMessage(ownerName + "님의 창고를 열었습니다.");
    }

    public void saveStorage(Player player, StorageInventoryHolder holder) {
        storageRepository.save(holder.getOwnerUuid(), holder.getInventory());
        player.sendMessage(holder.getOwnerName() + "님의 창고가 저장되었습니다.");
    }

    public boolean isStorageInventory(Inventory inventory) {
        return inventory.getHolder() instanceof StorageInventoryHolder;
    }

    public StorageInventoryHolder getStorageHolder(Inventory inventory) {
        if (inventory.getHolder() instanceof StorageInventoryHolder holder) {
            return holder;
        }

        return null;
    }
}
