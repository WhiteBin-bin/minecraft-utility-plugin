package org.WhiteBin.minecraftplugin.economy.service;

import org.WhiteBin.minecraftplugin.economy.model.EconomyAccount;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 플레이어 경제 계좌 기능에서 제공해야 하는 서비스 계약입니다.
 */
public interface EconomyService {

    /**
     * 플레이어 경제 계좌를 조회합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @return 경제 계좌 정보
     */
    EconomyAccount getAccount(UUID uuid, String name);

    /**
     * 플레이어 잔액을 조회합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @return 플레이어 잔액
     */
    BigDecimal getBalance(UUID uuid, String name);

    /**
     * 플레이어 잔액을 저장합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param balance 저장할 잔액
     */
    void setBalance(UUID uuid, String name, BigDecimal balance);
}
