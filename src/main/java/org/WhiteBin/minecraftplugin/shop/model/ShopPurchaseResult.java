package org.WhiteBin.minecraftplugin.shop.model;

/**
 * 상점 상품 구매 처리 결과입니다.
 */
public enum ShopPurchaseResult {

    SUCCESS,
    SHOP_NOT_FOUND,
    ITEM_NOT_FOUND,
    NOT_ENOUGH_MONEY,
    INVENTORY_FULL
}
