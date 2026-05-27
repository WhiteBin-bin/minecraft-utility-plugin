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

    /**
     * 플레이어 잔액을 증가시킵니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param amount 증가시킬 금액
     * @return 증가 후 잔액
     */
    BigDecimal deposit(UUID uuid, String name, BigDecimal amount);

    /**
     * 플레이어 잔액을 감소시킵니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param amount 감소시킬 금액
     * @return 감소에 성공했으면 {@code true}
     */
    boolean withdraw(UUID uuid, String name, BigDecimal amount);

    /**
     * 보내는 플레이어 잔액을 차감하고 받는 플레이어 잔액을 증가시킵니다.
     *
     * @param fromUuid 보내는 플레이어 UUID
     * @param fromName 보내는 플레이어 이름
     * @param toUuid 받는 플레이어 UUID
     * @param toName 받는 플레이어 이름
     * @param amount 송금할 금액
     * @return 송금에 성공했으면 {@code true}
     */
    boolean transfer(UUID fromUuid, String fromName, UUID toUuid, String toName, BigDecimal amount);

    /**
     * 플레이어 잔액이 지정한 금액 이상인지 확인합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param amount 확인할 금액
     * @return 잔액이 충분하면 {@code true}
     */
    boolean hasEnough(UUID uuid, String name, BigDecimal amount);
}
