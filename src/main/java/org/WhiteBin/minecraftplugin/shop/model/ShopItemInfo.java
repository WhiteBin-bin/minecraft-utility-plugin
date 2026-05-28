package org.WhiteBin.minecraftplugin.shop.model;

import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

/**
 * 상점에 등록된 상품 정보를 표현하는 값 객체입니다.
 *
 * @param slot 상품 슬롯
 * @param itemStack 상품 아이템
 * @param buyPrice 상품 구매가
 * @param sellPrice 상품 판매가
 */
public record ShopItemInfo(int slot, ItemStack itemStack, BigDecimal buyPrice, BigDecimal sellPrice) {

    /**
     * 기존 단일 가격 호출부 호환을 위한 구매가 반환 메서드입니다.
     *
     * @return 상품 구매가
     */
    public BigDecimal price() {
        return buyPrice;
    }
}
