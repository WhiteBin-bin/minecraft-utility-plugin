package org.WhiteBin.minecraftplugin.storage.service;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.repository.StorageRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

/**
 * 개인 창고의 열기, 저장, 확장 기능을 처리하는 서비스입니다.
 * <p>
 * 창고 데이터를 저장소에서 불러와 인벤토리를 생성하고,
 * 창고 크기 정책에 따라 플레이어별 창고 확장을 수행합니다.
 */
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageSizePolicy storageSizePolicy = new StorageSizePolicy();

    /**
     * 플레이어 자신의 개인 창고를 엽니다.
     *
     * @param player 창고를 열 플레이어
     */
    public void openStorage(Player player) {
        openStorage(player, player.getUniqueId(), player.getName());
    }

    /**
     * 특정 소유자의 개인 창고를 지정한 플레이어에게 엽니다.
     * <p>
     * 저장된 창고 크기를 기준으로 인벤토리를 생성하고,
     * 저장된 아이템을 로드한 뒤 플레이어에게 표시합니다.
     *
     * @param viewer 창고를 열어 볼 플레이어
     * @param ownerUuid 창고 소유자 UUID
     * @param ownerName 창고 소유자 이름
     */
    public void openStorage(Player viewer, UUID ownerUuid, String ownerName) {
        StorageInventoryHolder holder = new StorageInventoryHolder(ownerUuid, ownerName);
        int storageSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);
        Inventory inventory = Bukkit.createInventory(holder, storageSize, ownerName + "의 창고");
        holder.setInventory(inventory);

        storageRepository.load(ownerUuid, inventory);

        viewer.openInventory(inventory);
    }

    /**
     * 특정 소유자의 개인 창고 크기를 한 단계 확장합니다.
     * <p>
     * 이미 최대 크기인 경우 크기를 변경하지 않고 현재 크기를 반환합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 확장 후 창고 크기 또는 현재 창고 크기
     */
    public int expandStorage(UUID ownerUuid) {
        int currentSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);

        if (!storageSizePolicy.canExpand(currentSize)) {
            return currentSize;
        }

        int expandedSize = storageSizePolicy.expand(currentSize);
        storageRepository.saveSize(ownerUuid, expandedSize);
        return expandedSize;
    }

    /**
     * 특정 소유자의 개인 창고가 확장 가능한 상태인지 확인합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 최대 크기보다 작으면 {@code true}
     */
    public boolean canExpand(UUID ownerUuid) {
        int currentSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);
        return storageSizePolicy.canExpand(currentSize);
    }

    /**
     * 개인 창고 인벤토리 내용을 창고 소유자 UUID 기준으로 저장합니다.
     *
     * @param player 창고를 닫은 플레이어
     * @param holder 창고 소유자 정보가 담긴 인벤토리 홀더
     */
    public void saveStorage(Player player, StorageInventoryHolder holder) {
        storageRepository.save(holder.getOwnerUuid(), holder.getInventory());
    }

    /**
     * 전달된 인벤토리가 개인 창고 인벤토리인지 확인합니다.
     *
     * @param inventory 확인할 인벤토리
     * @return 개인 창고 홀더를 가진 인벤토리면 {@code true}
     */
    public boolean isStorageInventory(Inventory inventory) {
        return inventory.getHolder() instanceof StorageInventoryHolder;
    }

    /**
     * 전달된 인벤토리에서 개인 창고 홀더를 반환합니다.
     *
     * @param inventory 확인할 인벤토리
     * @return 개인 창고 홀더가 있으면 해당 홀더, 없으면 {@code null}
     */
    public StorageInventoryHolder getStorageHolder(Inventory inventory) {
        if (inventory.getHolder() instanceof StorageInventoryHolder holder) {
            return holder;
        }

        return null;
    }
}
