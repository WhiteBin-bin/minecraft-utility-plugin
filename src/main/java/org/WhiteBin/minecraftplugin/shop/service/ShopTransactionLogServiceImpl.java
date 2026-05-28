package org.WhiteBin.minecraftplugin.shop.service;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopTransactionLog;
import org.WhiteBin.minecraftplugin.shop.model.ShopTransactionType;
import org.WhiteBin.minecraftplugin.shop.repository.ShopTransactionLogRepository;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 상점 거래 로그 기록과 조회를 처리하는 {@link ShopTransactionLogService} 구현체입니다.
 */
@RequiredArgsConstructor
public class ShopTransactionLogServiceImpl implements ShopTransactionLogService {

    private final ShopTransactionLogRepository shopTransactionLogRepository;

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
    @Override
    public void record(ShopTransactionType type, String shopName, ShopItemInfo itemInfo, Player player, int quantity, BigDecimal unitPrice) {
        shopTransactionLogRepository.save(new ShopTransactionLog(
                LocalDateTime.now(),
                type,
                shopName,
                itemInfo.slot(),
                itemInfo.itemStack().getType().name(),
                player.getUniqueId(),
                player.getName(),
                quantity,
                unitPrice,
                unitPrice.multiply(BigDecimal.valueOf(quantity))
        ));
    }

    /**
     * 상점 이름 기준 거래 로그를 조회합니다.
     *
     * @param shopName 상점 이름
     * @return 거래 로그 목록
     */
    @Override
    public List<ShopTransactionLog> findByShopName(String shopName) {
        return shopTransactionLogRepository.findByShopName(shopName);
    }

    /**
     * 플레이어 UUID 기준 거래 로그를 조회합니다.
     *
     * @param playerUuid 플레이어 UUID
     * @return 거래 로그 목록
     */
    @Override
    public List<ShopTransactionLog> findByPlayerUuid(UUID playerUuid) {
        return shopTransactionLogRepository.findByPlayerUuid(playerUuid);
    }
}
