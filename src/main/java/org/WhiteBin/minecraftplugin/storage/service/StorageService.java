package org.WhiteBin.minecraftplugin.storage.service;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

/**
 * 개인 창고 기능에서 제공해야 하는 서비스 계약입니다.
 * <p>
 * 창고 열기, 저장, 크기 변경, 아이템 정렬, 창고 인벤토리 판별 기능을 정의합니다.
 */
public interface StorageService {

    /**
     * 플레이어 자신의 개인 창고를 엽니다.
     *
     * @param player 창고를 열 플레이어
     */
    void openStorage(Player player);

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
    void openStorage(Player viewer, UUID ownerUuid, String ownerName);

    /**
     * 특정 소유자의 개인 창고 크기를 한 단계 확장합니다.
     * <p>
     * 이미 최대 크기인 경우 크기를 변경하지 않고 현재 크기를 반환합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 확장 후 창고 크기 또는 현재 창고 크기
     */
    int expandStorage(UUID ownerUuid);

    /**
     * 특정 소유자의 개인 창고 크기를 한 단계 축소합니다.
     * <p>
     * 이미 최소 크기이거나 축소될 슬롯 범위에 아이템이 있는 경우 크기를 변경하지 않고 현재 크기를 반환합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 축소 후 창고 크기 또는 현재 창고 크기
     */
    int shrinkStorage(UUID ownerUuid);

    /**
     * 특정 소유자의 개인 창고가 확장 가능한 상태인지 확인합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 최대 크기보다 작으면 {@code true}
     */
    boolean canExpand(UUID ownerUuid);

    /**
     * 특정 소유자의 개인 창고가 축소 가능한 상태인지 확인합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 최소 크기보다 크면 {@code true}
     */
    boolean canShrink(UUID ownerUuid);

    /**
     * 특정 소유자의 개인 창고에서 축소될 슬롯 범위에 아이템이 있는지 확인합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @return 축소될 슬롯 범위에 아이템이 있으면 {@code true}
     */
    boolean hasItemsInShrinkRange(UUID ownerUuid);

    /**
     * 특정 소유자의 개인 창고 아이템을 정렬하고 저장합니다.
     * <p>
     * 창고 크기는 유지하며 빈 슬롯을 뒤로 보내고 같은 아이템을 가능한 범위에서 합칩니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     */
    void sortStorage(UUID ownerUuid);

    /**
     * 개인 창고 인벤토리 내용을 창고 소유자 UUID 기준으로 저장합니다.
     *
     * @param player 창고를 닫은 플레이어
     * @param holder 창고 소유자 정보가 담긴 인벤토리 홀더
     */
    void saveStorage(Player player, StorageInventoryHolder holder);

    /**
     * 전달된 인벤토리가 개인 창고 인벤토리인지 확인합니다.
     *
     * @param inventory 확인할 인벤토리
     * @return 개인 창고 홀더를 가진 인벤토리면 {@code true}
     */
    boolean isStorageInventory(Inventory inventory);

    /**
     * 전달된 인벤토리에서 개인 창고 홀더를 반환합니다.
     *
     * @param inventory 확인할 인벤토리
     * @return 개인 창고 홀더가 있으면 해당 홀더, 없으면 {@code null}
     */
    StorageInventoryHolder getStorageHolder(Inventory inventory);
}
