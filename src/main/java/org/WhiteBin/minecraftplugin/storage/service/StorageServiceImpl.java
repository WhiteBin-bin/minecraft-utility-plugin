package org.WhiteBin.minecraftplugin.storage.service;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.policy.StorageSizePolicy;
import org.WhiteBin.minecraftplugin.storage.policy.StorageSortPolicy;
import org.WhiteBin.minecraftplugin.storage.repository.StorageRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * 개인 창고의 열기, 저장, 확장, 축소, 정렬, 초기화, 공유 기능을 처리하는 {@link StorageService} 구현체입니다.
 * <p>
 * 창고 데이터를 저장소에서 불러와 인벤토리를 생성하고,
 * 창고 크기 정책, 정렬 정책, 공유 관계에 따라 플레이어별 창고 데이터를 변경합니다.
 */
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final StorageSizePolicy storageSizePolicy = new StorageSizePolicy();
    private final StorageSortPolicy storageSortPolicy = new StorageSortPolicy();

    /**
     * 플레이어 자신의 개인 창고를 엽니다.
     *
     * @param player 창고를 열 플레이어
     */
    @Override
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
    @Override
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
    @Override
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
     * 특정 소유자의 개인 창고 크기를 한 단계 축소합니다.
     * <p>
     * 이미 최소 크기이거나 축소될 슬롯 범위에 아이템이 있는 경우 크기를 변경하지 않고 현재 크기를 반환합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 축소 후 창고 크기 또는 현재 창고 크기
     */
    @Override
    public int shrinkStorage(UUID ownerUuid) {
        int currentSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);

        if (!storageSizePolicy.canShrink(currentSize)) {
            return currentSize;
        }

        int shrinkSize = storageSizePolicy.shrink(currentSize);

        if (storageRepository.hasItemsInRange(ownerUuid, shrinkSize, currentSize)) {
            return currentSize;
        }

        storageRepository.saveSize(ownerUuid, shrinkSize);
        return shrinkSize;
    }

    /**
     * 특정 소유자의 개인 창고가 확장 가능한 상태인지 확인합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 최대 크기보다 작으면 {@code true}
     */
    @Override
    public boolean canExpand(UUID ownerUuid) {
        int currentSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);
        return storageSizePolicy.canExpand(currentSize);
    }

    /**
     * 특정 소유자의 개인 창고가 축소 가능한 상태인지 확인합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 최소 크기보다 크면 {@code true}
     */
    @Override
    public boolean canShrink(UUID ownerUuid) {
        int currentSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);
        return storageSizePolicy.canShrink(currentSize);
    }

    /**
     * 특정 소유자의 개인 창고에서 축소될 슬롯 범위에 아이템이 있는지 확인합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 축소될 슬롯 범위에 아이템이 있으면 {@code true}
     */
    @Override
    public boolean hasItemsInShrinkRange(UUID ownerUuid) {
        int currentSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);
        int shrinkSize = storageSizePolicy.shrink(currentSize);
        return storageRepository.hasItemsInRange(ownerUuid, shrinkSize, currentSize);
    }

    /**
     * 특정 소유자의 개인 창고 아이템을 정렬하고 저장합니다.
     * <p>
     * 창고 크기는 유지하며 빈 슬롯을 뒤로 보내고 같은 아이템을 가능한 범위에서 합칩니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     */
    @Override
    public void sortStorage(UUID ownerUuid) {
        int storageSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);
        storageRepository.saveItems(ownerUuid, storageSortPolicy.sort(storageRepository.loadItems(ownerUuid, storageSize)), storageSize);
    }

    /**
     * 특정 소유자의 개인 창고 아이템을 모두 비우고 저장합니다.
     * <p>
     * 창고 크기는 기존 저장 크기를 유지합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     */
    @Override
    public void clearStorage(UUID ownerUuid) {
        int storageSize = storageRepository.loadSize(ownerUuid, StorageSizePolicy.DEFAULT_SIZE);
        storageRepository.saveItems(ownerUuid, new ItemStack[storageSize], storageSize);
    }

    /**
     * 개인 창고를 특정 플레이어에게 공유합니다.
     * <p>
     * 이미 공유된 대상이거나 자기 자신에게 공유하는 경우 저장하지 않습니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @param ownerName 창고 소유자 이름
     * @param targetUuid 공유받을 플레이어 UUID
     * @param targetName 공유받을 플레이어 이름
     * @return 새 공유 관계가 저장되었으면 {@code true}
     */
    @Override
    public boolean shareStorage(UUID ownerUuid, String ownerName, UUID targetUuid, String targetName) {
        if (ownerUuid.equals(targetUuid) || storageRepository.isSharedWith(ownerUuid, targetUuid)) {
            return false;
        }

        storageRepository.saveShare(ownerUuid, ownerName, targetUuid, targetName);
        return true;
    }

    /**
     * 개인 창고 공유를 해제합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @param targetUuid 공유 해제 대상 플레이어 UUID
     * @return 기존 공유 관계가 제거되었으면 {@code true}
     */
    @Override
    public boolean unshareStorage(UUID ownerUuid, UUID targetUuid) {
        if (!storageRepository.isSharedWith(ownerUuid, targetUuid)) {
            return false;
        }

        storageRepository.removeShare(ownerUuid, targetUuid);
        return true;
    }

    /**
     * 특정 플레이어가 대상 창고를 열 수 있는지 확인합니다.
     * <p>
     * 자기 자신의 창고이거나 공유받은 창고인 경우 접근을 허용합니다.
     *
     * @param viewerUuid 창고를 열어 볼 플레이어 UUID
     * @param ownerUuid 창고 소유자 UUID
     * @return 접근 가능한 공유 관계이면 {@code true}
     */
    @Override
    public boolean canAccessStorage(UUID viewerUuid, UUID ownerUuid) {
        return viewerUuid.equals(ownerUuid) || storageRepository.isSharedWith(ownerUuid, viewerUuid);
    }

    /**
     * 특정 소유자가 공유 중인 플레이어 목록을 반환합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 공유받은 플레이어 목록
     */
    @Override
    public List<StorageShareInfo> getSharedUsers(UUID ownerUuid) {
        return storageRepository.loadSharedUsers(ownerUuid);
    }

    /**
     * 특정 플레이어가 공유받은 창고 목록을 반환합니다.
     *
     * @param targetUuid 공유받은 플레이어 UUID
     * @return 공유해준 창고 소유자 목록
     */
    @Override
    public List<StorageShareInfo> getSharedStorages(UUID targetUuid) {
        return storageRepository.loadSharedStorages(targetUuid);
    }

    /**
     * 개인 창고 인벤토리 내용을 창고 소유자 UUID 기준으로 저장합니다.
     *
     * @param player 창고를 닫은 플레이어
     * @param holder 창고 소유자 정보가 담긴 인벤토리 홀더
     */
    @Override
    public void saveStorage(Player player, StorageInventoryHolder holder) {
        storageRepository.save(holder.getOwnerUuid(), holder.getInventory());
    }

    /**
     * 전달된 인벤토리가 개인 창고 인벤토리인지 확인합니다.
     *
     * @param inventory 확인할 인벤토리
     * @return 개인 창고 홀더를 가진 인벤토리면 {@code true}
     */
    @Override
    public boolean isStorageInventory(Inventory inventory) {
        return inventory.getHolder() instanceof StorageInventoryHolder;
    }

    /**
     * 전달된 인벤토리에서 개인 창고 홀더를 반환합니다.
     *
     * @param inventory 확인할 인벤토리
     * @return 개인 창고 홀더가 있으면 해당 홀더, 없으면 {@code null}
     */
    @Override
    public StorageInventoryHolder getStorageHolder(Inventory inventory) {
        if (inventory.getHolder() instanceof StorageInventoryHolder holder) {
            return holder;
        }

        return null;
    }
}
