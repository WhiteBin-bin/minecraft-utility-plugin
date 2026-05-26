package org.WhiteBin.minecraftplugin.storage.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 개인 창고 조회 GUI의 종류와 페이지 상태를 보관하는 {@link InventoryHolder} 구현체입니다.
 */
@Getter
@RequiredArgsConstructor
public class StorageGuiHolder implements InventoryHolder {

    private final StorageGuiType type;
    private final UUID ownerUuid;
    private final String ownerName;
    private final int page;
    private final String filter;
    @Setter
    private Inventory inventory;

    /**
     * 이 홀더와 연결된 GUI 인벤토리를 반환합니다.
     *
     * @return 연결된 GUI 인벤토리
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
