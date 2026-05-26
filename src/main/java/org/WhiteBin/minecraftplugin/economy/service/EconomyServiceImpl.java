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
}
