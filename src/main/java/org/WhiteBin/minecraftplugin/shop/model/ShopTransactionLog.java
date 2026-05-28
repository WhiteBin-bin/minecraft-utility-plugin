package org.WhiteBin.minecraftplugin.shop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 상점 구매와 판매 거래 로그입니다.
 *
 * @param occurredAt 거래 시각
 * @param type 거래 타입
 * @param shopName 상점 이름
 * @param slot 상품 슬롯
 * @param itemName 상품 이름
 * @param playerUuid 플레이어 UUID
 * @param playerName 플레이어 이름
 * @param quantity 거래 수량
 * @param unitPrice 개당 가격
 * @param totalPrice 총 거래 금액
 */
public record ShopTransactionLog(
        LocalDateTime occurredAt,
        ShopTransactionType type,
        String shopName,
        int slot,
        String itemName,
        UUID playerUuid,
        String playerName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
