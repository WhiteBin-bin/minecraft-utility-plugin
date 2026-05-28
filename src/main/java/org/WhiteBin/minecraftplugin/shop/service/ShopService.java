package org.WhiteBin.minecraftplugin.shop.service;

import org.WhiteBin.minecraftplugin.shop.model.ShopInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopPurchaseResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

/**
 * GUI 상점 기능에서 제공해야 하는 서비스 계약입니다.
 */
public interface ShopService {

    /**
     * 상점을 생성합니다.
     *
     * @param shopName 생성할 상점 이름
     * @return 새로 생성했으면 {@code true}
     */
    boolean createShop(String shopName);

    /**
     * 상점을 삭제합니다.
     *
     * @param shopName 삭제할 상점 이름
     * @return 삭제했으면 {@code true}
     */
    boolean deleteShop(String shopName);

    /**
     * 상점 이름 목록을 반환합니다.
     *
     * @return 상점 이름 목록
     */
    List<String> getShopNames();

    /**
     * 상점 정보를 반환합니다.
     *
     * @param shopName 상점 이름
     * @return 상점 정보
     */
    ShopInfo getShop(String shopName);

    /**
     * 상점에 상품을 등록합니다.
     *
     * @param shopName 상점 이름
     * @param itemStack 등록할 아이템
     * @param price 상품 개당 가격
     * @return 등록된 상품 정보 또는 등록할 수 없으면 {@code null}
     */
    ShopItemInfo addItem(String shopName, ItemStack itemStack, BigDecimal price);

    /**
     * 상점에 상품을 등록합니다.
     *
     * @param shopName 상점 이름
     * @param itemStack 등록할 아이템
     * @param buyPrice 상품 구매가
     * @param sellPrice 상품 판매가
     * @return 등록된 상품 정보 또는 등록할 수 없으면 {@code null}
     */
    ShopItemInfo addItem(String shopName, ItemStack itemStack, BigDecimal buyPrice, BigDecimal sellPrice);

    /**
     * 상점의 특정 슬롯에 상품을 등록하거나 교체합니다.
     *
     * @param shopName 상점 이름
     * @param slot 등록할 슬롯
     * @param itemStack 등록할 아이템
     * @param unitPrice 상품 개당 가격
     * @return 등록된 상품 정보 또는 등록할 수 없으면 {@code null}
     */
    ShopItemInfo setItem(String shopName, int slot, ItemStack itemStack, BigDecimal unitPrice);

    /**
     * 상점의 특정 슬롯에 상품을 등록하거나 교체합니다.
     *
     * @param shopName 상점 이름
     * @param slot 등록할 슬롯
     * @param itemStack 등록할 아이템
     * @param buyPrice 상품 구매가
     * @param sellPrice 상품 판매가
     * @return 등록된 상품 정보 또는 등록할 수 없으면 {@code null}
     */
    ShopItemInfo setItem(String shopName, int slot, ItemStack itemStack, BigDecimal buyPrice, BigDecimal sellPrice);

    /**
     * 상점 상품을 제거합니다.
     *
     * @param shopName 상점 이름
     * @param slot 제거할 상품 슬롯
     * @return 제거했으면 {@code true}
     */
    boolean removeItem(String shopName, int slot);

    /**
     * 플레이어가 상점 상품을 구매합니다.
     *
     * @param player 구매할 플레이어
     * @param shopName 상점 이름
     * @param slot 구매할 상품 슬롯
     * @param quantity 구매할 수량
     * @return 구매 처리 결과
     */
    ShopPurchaseResult purchase(Player player, String shopName, int slot, int quantity);

    /**
     * 플레이어가 상점에 상품을 판매합니다.
     *
     * @param player 판매할 플레이어
     * @param shopName 상점 이름
     * @param slot 판매할 상품 슬롯
     * @param quantity 판매할 수량
     * @return 판매 처리 결과
     */
    ShopPurchaseResult sell(Player player, String shopName, int slot, int quantity);
}
