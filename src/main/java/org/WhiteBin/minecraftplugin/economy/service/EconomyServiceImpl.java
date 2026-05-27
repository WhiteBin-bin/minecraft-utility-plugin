package org.WhiteBin.minecraftplugin.economy.service;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.economy.model.EconomyAccount;
import org.WhiteBin.minecraftplugin.economy.repository.EconomyRepository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 플레이어 경제 계좌 저장과 조회를 처리하는 {@link EconomyService} 구현체입니다.
 */
@RequiredArgsConstructor
public class EconomyServiceImpl implements EconomyService {

    private static final BigDecimal DEFAULT_BALANCE = BigDecimal.ZERO;

    private final EconomyRepository economyRepository;

    /**
     * 플레이어 경제 계좌를 조회합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @return 경제 계좌 정보
     */
    @Override
    public EconomyAccount getAccount(UUID uuid, String name) {
        return economyRepository.loadAccount(uuid, name, DEFAULT_BALANCE);
    }

    /**
     * 플레이어 잔액을 조회합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @return 플레이어 잔액
     */
    @Override
    public BigDecimal getBalance(UUID uuid, String name) {
        return getAccount(uuid, name).balance();
    }

    /**
     * 플레이어 잔액을 저장합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param balance 저장할 잔액
     */
    @Override
    public void setBalance(UUID uuid, String name, BigDecimal balance) {
        economyRepository.saveAccount(new EconomyAccount(uuid, name, balance));
    }

    /**
     * 플레이어 잔액을 증가시킵니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param amount 증가시킬 금액
     * @return 증가 후 잔액
     */
    @Override
    public BigDecimal deposit(UUID uuid, String name, BigDecimal amount) {
        BigDecimal balance = getBalance(uuid, name).add(amount);

        setBalance(uuid, name, balance);
        return balance;
    }

    /**
     * 플레이어 잔액을 감소시킵니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param amount 감소시킬 금액
     * @return 감소에 성공했으면 {@code true}
     */
    @Override
    public boolean withdraw(UUID uuid, String name, BigDecimal amount) {
        if (!hasEnough(uuid, name, amount)) {
            return false;
        }

        setBalance(uuid, name, getBalance(uuid, name).subtract(amount));
        return true;
    }

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
    @Override
    public boolean transfer(UUID fromUuid, String fromName, UUID toUuid, String toName, BigDecimal amount) {
        if (!hasEnough(fromUuid, fromName, amount)) {
            return false;
        }

        setBalance(fromUuid, fromName, getBalance(fromUuid, fromName).subtract(amount));
        deposit(toUuid, toName, amount);
        return true;
    }

    /**
     * 플레이어 잔액이 지정한 금액 이상인지 확인합니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param name 계좌 소유자 이름
     * @param amount 확인할 금액
     * @return 잔액이 충분하면 {@code true}
     */
    @Override
    public boolean hasEnough(UUID uuid, String name, BigDecimal amount) {
        return getBalance(uuid, name).compareTo(amount) >= 0;
    }
}
