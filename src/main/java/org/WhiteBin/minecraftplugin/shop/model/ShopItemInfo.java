package org.WhiteBin.minecraftplugin.shop.model;

import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

/**
 * 상점에 등록된 상품 정보를 표현하는 값 객체입니다.
 *
 * @param slot 상품 슬롯
 * @param itemStack 상품 아이템
 * @param price 상품 가격
 */
public record ShopItemInfo(int slot, ItemStack itemStack, BigDecimal price) {
}
