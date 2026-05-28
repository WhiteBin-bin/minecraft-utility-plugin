package org.WhiteBin.minecraftplugin.shop.service;

import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopTransactionLog;
import org.WhiteBin.minecraftplugin.shop.model.ShopTransactionType;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 상점 거래 로그 기능에서 제공해야 하는 서비스 계약입니다.
 */
public interface ShopTransactionLogService {

    /**
     * 상점 거래 로그를 기록합니다.
     *
     * @param type 거래 타입
     * @param shopName 상점 이름
     * @param itemInfo 상품 정보
     * @param player 거래 플레이어
     * @param quantity 거래 수량
     * @param unitPrice 개당 가격
     */
    void record(ShopTransactionType type, String shopName, ShopItemInfo itemInfo, Player player, int quantity, BigDecimal unitPrice);

    /**
     * 상점 이름 기준 거래 로그를 조회합니다.
     *
     * @param shopName 상점 이름
     * @return 거래 로그 목록
     */
    List<ShopTransactionLog> findByShopName(String shopName);

    /**
     * 플레이어 UUID 기준 거래 로그를 조회합니다.
     *
     * @param playerUuid 플레이어 UUID
     * @return 거래 로그 목록
     */
    List<ShopTransactionLog> findByPlayerUuid(UUID playerUuid);
}
