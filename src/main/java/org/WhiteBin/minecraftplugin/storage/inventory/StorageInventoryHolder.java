package org.WhiteBin.minecraftplugin.storage.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 개인 창고 인벤토리의 소유자 정보를 보관하는 {@link InventoryHolder} 구현체입니다.
 * <p>
 * 인벤토리를 닫을 때 실제 창고 소유자의 UUID와 이름을 확인하여
 * 창고를 연 플레이어가 아닌 소유자 기준으로 저장할 수 있게 합니다.
 */
@Getter
@RequiredArgsConstructor
public class StorageInventoryHolder implements InventoryHolder {

    private final UUID ownerUuid;
    private final String ownerName;
    @Setter
    private Inventory inventory;

    /**
     * 이 홀더와 연결된 인벤토리를 반환합니다.
     *
     * @return 연결된 창고 인벤토리
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
