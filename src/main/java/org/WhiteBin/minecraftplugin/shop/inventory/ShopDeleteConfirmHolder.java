package org.WhiteBin.minecraftplugin.shop.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 상점 상품 삭제 확인 GUI의 소유자 정보입니다.
 */
@Getter
@RequiredArgsConstructor
public class ShopDeleteConfirmHolder implements InventoryHolder {

    private final String shopName;
    private final int slot;

    @Setter
    private Inventory inventory;
}
