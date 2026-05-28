package org.WhiteBin.minecraftplugin.shop.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * 상점 상품 가격 입력 GUI의 소유자 정보입니다.
 */
@Getter
@RequiredArgsConstructor
public class ShopPriceInputHolder implements InventoryHolder {

    private final String shopName;
    private final int slot;
    private final ItemStack itemStack;
    private final String input;

    @Setter
    private Inventory inventory;
}
