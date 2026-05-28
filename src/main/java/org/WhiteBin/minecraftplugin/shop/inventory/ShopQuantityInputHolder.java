package org.WhiteBin.minecraftplugin.shop.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.math.BigDecimal;

/**
 * 상점 상품 구매 수량 입력 GUI의 소유자 정보입니다.
 */
@Getter
@RequiredArgsConstructor
public class ShopQuantityInputHolder implements InventoryHolder {

    private final String shopName;
    private final int slot;
    private final BigDecimal unitPrice;
    private final boolean selling;
    private final String input;

    @Setter
    private Inventory inventory;
}
