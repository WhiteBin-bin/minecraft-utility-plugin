package org.WhiteBin.minecraftplugin.economy.service;

import org.WhiteBin.minecraftplugin.economy.model.EconomyAccount;
import org.WhiteBin.minecraftplugin.economy.repository.EconomyRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link EconomyService}의 플레이어 잔액 저장과 조회 흐름을 검증합니다.
 */
class EconomyServiceTest {

    /**
     * 저장된 계좌가 없을 때 기본 잔액 0을 반환하는지 검증합니다.
     */
    @Test
    void getBalanceReturnsZeroWhenAccountDoesNotExist() {
        // given
        UUID uuid = UUID.randomUUID();
        FakeEconomyRepository economyRepository = new FakeEconomyRepository(null);
        EconomyService economyService = new EconomyServiceImpl(economyRepository);

        // when
        BigDecimal balance = economyService.getBalance(uuid, "WhiteBin");

        // then
        assertEquals(BigDecimal.ZERO, balance);
    }

    /**
     * 저장된 계좌가 있을 때 저장소의 잔액을 반환하는지 검증합니다.
     */
    @Test
    void getBalanceReturnsSavedBalance() {
        // given
        UUID uuid = UUID.randomUUID();
        EconomyAccount savedAccount = new EconomyAccount(uuid, "WhiteBin", new BigDecimal("1500"));
        FakeEconomyRepository economyRepository = new FakeEconomyRepository(savedAccount);
        EconomyService economyService = new EconomyServiceImpl(economyRepository);

        // when
        BigDecimal balance = economyService.getBalance(uuid, "WhiteBin");

        // then
        assertEquals(new BigDecimal("1500"), balance);
    }

    /**
     * 전달된 잔액으로 플레이어 계좌를 저장하는지 검증합니다.
     */
    @Test
    void setBalanceSavesAccount() {
        // given
        UUID uuid = UUID.randomUUID();
        FakeEconomyRepository economyRepository = new FakeEconomyRepository(null);
        EconomyService economyService = new EconomyServiceImpl(economyRepository);

        // when
        economyService.setBalance(uuid, "WhiteBin", new BigDecimal("3000"));

        // then
        assertEquals(uuid, economyRepository.savedAccount.uuid());
        assertEquals("WhiteBin", economyRepository.savedAccount.name());
        assertEquals(new BigDecimal("3000"), economyRepository.savedAccount.balance());
    }

    /**
     * 입금 시 기존 잔액에 금액을 더해 저장하는지 검증합니다.
     */
    @Test
    void depositAddsAmountToBalance() {
        // given
        UUID uuid = UUID.randomUUID();
        EconomyAccount savedAccount = new EconomyAccount(uuid, "WhiteBin", new BigDecimal("1000"));
        FakeEconomyRepository economyRepository = new FakeEconomyRepository(savedAccount);
        EconomyService economyService = new EconomyServiceImpl(economyRepository);

        // when
        BigDecimal balance = economyService.deposit(uuid, "WhiteBin", new BigDecimal("500"));

        // then
        assertEquals(new BigDecimal("1500"), balance);
        assertEquals(new BigDecimal("1500"), economyRepository.savedAccount.balance());
    }

    /**
     * 잔액이 충분할 때 출금 후 잔액을 저장하는지 검증합니다.
     */
    @Test
    void withdrawSubtractsAmountWhenEnoughBalance() {
        // given
        UUID uuid = UUID.randomUUID();
        EconomyAccount savedAccount = new EconomyAccount(uuid, "WhiteBin", new BigDecimal("1000"));
        FakeEconomyRepository economyRepository = new FakeEconomyRepository(savedAccount);
        EconomyService economyService = new EconomyServiceImpl(economyRepository);

        // when
        boolean withdrawn = economyService.withdraw(uuid, "WhiteBin", new BigDecimal("400"));

        // then
        assertTrue(withdrawn);
        assertEquals(new BigDecimal("600"), economyRepository.savedAccount.balance());
    }

    /**
     * 잔액이 부족할 때 출금하지 않는지 검증합니다.
     */
    @Test
    void withdrawDoesNotSubtractWhenNotEnoughBalance() {
        // given
        UUID uuid = UUID.randomUUID();
        EconomyAccount savedAccount = new EconomyAccount(uuid, "WhiteBin", new BigDecimal("100"));
        FakeEconomyRepository economyRepository = new FakeEconomyRepository(savedAccount);
        EconomyService economyService = new EconomyServiceImpl(economyRepository);

        // when
        boolean withdrawn = economyService.withdraw(uuid, "WhiteBin", new BigDecimal("400"));

        // then
        assertFalse(withdrawn);
        assertEquals(null, economyRepository.savedAccount);
    }

    /**
     * 잔액이 지정 금액 이상인지 반환하는지 검증합니다.
     */
    @Test
    void hasEnoughReturnsBalanceComparisonResult() {
        // given
        UUID uuid = UUID.randomUUID();
        EconomyAccount savedAccount = new EconomyAccount(uuid, "WhiteBin", new BigDecimal("1000"));
        FakeEconomyRepository economyRepository = new FakeEconomyRepository(savedAccount);
        EconomyService economyService = new EconomyServiceImpl(economyRepository);

        // when
        boolean enough = economyService.hasEnough(uuid, "WhiteBin", new BigDecimal("1000"));
        boolean notEnough = economyService.hasEnough(uuid, "WhiteBin", new BigDecimal("1001"));

        // then
        assertTrue(enough);
        assertFalse(notEnough);
    }

    /**
     * {@link EconomyService} 테스트에서 파일 시스템 접근 없이 계좌 저장을 검증하기 위한 저장소입니다.
     */
    private static class FakeEconomyRepository extends EconomyRepository {

        private final EconomyAccount loadedAccount;
        private EconomyAccount savedAccount;

        /**
         * 테스트에서 불러올 계좌 정보를 지정하여 저장소를 생성합니다.
         *
         * @param loadedAccount 조회 시 반환할 계좌 정보
         */
        private FakeEconomyRepository(EconomyAccount loadedAccount) {
            super(null);
            this.loadedAccount = loadedAccount;
        }

        /**
         * 테스트용 계좌 정보를 반환합니다.
         *
         * @param uuid 계좌 소유자 UUID
         * @param fallbackName 저장된 이름이 없을 때 사용할 이름
         * @param defaultBalance 저장된 잔액이 없을 때 사용할 기본 잔액
         * @return 테스트용 계좌 정보
         */
        @Override
        public EconomyAccount loadAccount(UUID uuid, String fallbackName, BigDecimal defaultBalance) {
            if (loadedAccount == null) {
                return new EconomyAccount(uuid, fallbackName, defaultBalance);
            }

            return loadedAccount;
        }

        /**
         * 저장 요청된 계좌 정보를 메모리에 보관합니다.
         *
         * @param account 저장할 경제 계좌
         */
        @Override
        public void saveAccount(EconomyAccount account) {
            this.savedAccount = account;
        }
    }
}
