package org.WhiteBin.minecraftplugin.shop.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * 상점 GUI의 상점 이름을 보관하는 {@link InventoryHolder} 구현체입니다.
 */
@Getter
@RequiredArgsConstructor
public class ShopInventoryHolder implements InventoryHolder {

    private final String shopName;
    @Setter
    private Inventory inventory;

    /**
     * 이 홀더와 연결된 상점 GUI 인벤토리를 반환합니다.
     *
     * @return 연결된 상점 GUI 인벤토리
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
