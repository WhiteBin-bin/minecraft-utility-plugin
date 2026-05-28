package org.WhiteBin.minecraftplugin.shop.model;

import java.util.List;

/**
 * 상점 정보를 표현하는 값 객체입니다.
 *
 * @param name 상점 이름
 * @param size 상점 GUI 크기
 * @param items 상점 상품 목록
 */
public record ShopInfo(String name, int size, List<ShopItemInfo> items) {
}
