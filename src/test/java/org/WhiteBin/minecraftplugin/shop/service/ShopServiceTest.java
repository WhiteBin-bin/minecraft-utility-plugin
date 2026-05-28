package org.WhiteBin.minecraftplugin.shop.service;

import org.WhiteBin.minecraftplugin.economy.model.EconomyAccount;
import org.WhiteBin.minecraftplugin.economy.service.EconomyService;
import org.WhiteBin.minecraftplugin.shop.model.ShopInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.WhiteBin.minecraftplugin.shop.repository.ShopRepository;
import org.WhiteBin.minecraftplugin.storage.service.TestItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ShopService}의 상점 생성과 상품 등록 흐름을 검증합니다.
 */
class ShopServiceTest {

    /**
     * 존재하지 않는 상점이면 새 상점을 생성하는지 검증합니다.
     */
    @Test
    void createShopCreatesWhenNotExists() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        boolean created = shopService.createShop("food");

        // then
        assertTrue(created);
        assertTrue(shopRepository.exists("food"));
    }

    /**
     * 이미 존재하는 상점이면 새로 생성하지 않는지 검증합니다.
     */
    @Test
    void createShopDoesNotCreateWhenAlreadyExists() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        boolean created = shopService.createShop("food");

        // then
        assertFalse(created);
    }

    /**
     * 상점에 빈 슬롯이 있으면 상품을 등록하는지 검증합니다.
     */
    @Test
    void addItemSavesItemToEmptySlot() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());
        ItemStack itemStack = new TestItemStack("bread", 16);

        // when
        ShopItemInfo itemInfo = shopService.addItem("food", itemStack, new BigDecimal("100"));

        // then
        assertEquals(0, itemInfo.slot());
        assertEquals(new BigDecimal("100"), itemInfo.price());
        assertEquals(1, shopRepository.load("food").items().size());
    }

    /**
     * 존재하지 않는 상점에는 상품을 등록하지 않는지 검증합니다.
     */
    @Test
    void addItemReturnsNullWhenShopDoesNotExist() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        ShopItemInfo itemInfo = shopService.addItem("food", new TestItemStack("bread", 16), new BigDecimal("100"));

        // then
        assertNull(itemInfo);
    }

    /**
     * 상점 상품 등록 시 구매가와 판매가를 따로 저장하는지 검증합니다.
     */
    @Test
    void addItemSavesBuyPriceAndSellPrice() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        ShopItemInfo itemInfo = shopService.addItem(
                "food",
                new TestItemStack("bread", 16),
                new BigDecimal("100"),
                new BigDecimal("60")
        );

        // then
        assertEquals(new BigDecimal("100"), itemInfo.buyPrice());
        assertEquals(new BigDecimal("60"), itemInfo.sellPrice());
    }

    /**
     * 상점의 지정 슬롯에 상품을 등록하는지 검증합니다.
     */
    @Test
    void setItemSavesItemToSelectedSlot() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());
        ItemStack itemStack = new TestItemStack("bread", 64);

        // when
        ShopItemInfo itemInfo = shopService.setItem("food", 7, itemStack, new BigDecimal("15"));

        // then
        assertEquals(7, itemInfo.slot());
        assertEquals(new BigDecimal("15"), itemInfo.price());
        assertEquals(1, itemInfo.itemStack().getAmount());
    }

    /**
     * 지정 슬롯 상품 등록 시 잘못된 가격이면 등록하지 않는지 검증합니다.
     */
    @Test
    void setItemReturnsNullWhenPriceIsNotPositive() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        ShopItemInfo itemInfo = shopService.setItem("food", 7, new TestItemStack("bread", 64), BigDecimal.ZERO);

        // then
        assertNull(itemInfo);
    }

    /**
     * 상점에 등록된 상품을 제거하는지 검증합니다.
     */
    @Test
    void removeItemRemovesSavedItem() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        shopRepository.saveItem("food", new ShopItemInfo(0, new TestItemStack("bread", 16), new BigDecimal("100"), new BigDecimal("50")));
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        boolean removed = shopService.removeItem("food", 0);

        // then
        assertTrue(removed);
        assertTrue(shopRepository.load("food").items().isEmpty());
    }

    /**
     * 상점에 등록된 상품 위치를 빈 슬롯으로 이동하는지 검증합니다.
     */
    @Test
    void moveItemMovesSavedItemToEmptySlot() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        shopRepository.saveItem("food", new ShopItemInfo(0, new TestItemStack("bread", 1), new BigDecimal("100"), new BigDecimal("50")));
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        boolean moved = shopService.moveItem("food", 0, 8);

        // then
        assertTrue(moved);
        assertEquals(8, shopRepository.load("food").items().getFirst().slot());
    }

    /**
     * 이미 상품이 있는 슬롯으로는 상품 위치를 이동하지 않는지 검증합니다.
     */
    @Test
    void moveItemReturnsFalseWhenTargetSlotIsOccupied() {
        // given
        FakeShopRepository shopRepository = new FakeShopRepository();
        shopRepository.create("food");
        shopRepository.saveItem("food", new ShopItemInfo(0, new TestItemStack("bread", 1), new BigDecimal("100"), new BigDecimal("50")));
        shopRepository.saveItem("food", new ShopItemInfo(8, new TestItemStack("apple", 1), new BigDecimal("30"), new BigDecimal("10")));
        ShopService shopService = new ShopServiceImpl(shopRepository, new FakeEconomyService());

        // when
        boolean moved = shopService.moveItem("food", 0, 8);

        // then
        assertFalse(moved);
    }

    /**
     * 테스트에서 파일 시스템 접근 없이 상점 저장을 검증하기 위한 저장소입니다.
     */
    private static class FakeShopRepository extends ShopRepository {

        private final List<ShopInfo> shops = new ArrayList<>();

        /**
         * 테스트용 상점 저장소를 생성합니다.
         */
        private FakeShopRepository() {
            super(null);
        }

        /**
         * 테스트용 상점 존재 여부를 반환합니다.
         *
         * @param shopName 상점 이름
         * @return 상점이 있으면 {@code true}
         */
        @Override
        public boolean exists(String shopName) {
            return load(shopName) != null;
        }

        /**
         * 테스트용 상점을 생성합니다.
         *
         * @param shopName 생성할 상점 이름
         */
        @Override
        public void create(String shopName) {
            shops.add(new ShopInfo(shopName, 54, new ArrayList<>()));
        }

        /**
         * 테스트용 상점을 삭제합니다.
         *
         * @param shopName 삭제할 상점 이름
         */
        @Override
        public void delete(String shopName) {
            shops.removeIf(shopInfo -> shopInfo.name().equals(shopName));
        }

        /**
         * 테스트용 상점 이름 목록을 반환합니다.
         *
         * @return 상점 이름 목록
         */
        @Override
        public List<String> findShopNames() {
            return shops.stream()
                    .map(ShopInfo::name)
                    .toList();
        }

        /**
         * 테스트용 상점 정보를 반환합니다.
         *
         * @param shopName 상점 이름
         * @return 상점 정보
         */
        @Override
        public ShopInfo load(String shopName) {
            return shops.stream()
                    .filter(shopInfo -> shopInfo.name().equals(shopName))
                    .findFirst()
                    .orElse(null);
        }

        /**
         * 테스트용 상점 상품을 저장합니다.
         *
         * @param shopName 상점 이름
         * @param itemInfo 저장할 상품 정보
         */
        @Override
        public void saveItem(String shopName, ShopItemInfo itemInfo) {
            ShopInfo shopInfo = load(shopName);
            List<ShopItemInfo> items = new ArrayList<>(shopInfo.items());

            items.removeIf(savedItem -> savedItem.slot() == itemInfo.slot());
            items.add(itemInfo);
            shops.remove(shopInfo);
            shops.add(new ShopInfo(shopName, shopInfo.size(), items));
        }

        /**
         * 테스트용 상점 상품을 제거합니다.
         *
         * @param shopName 상점 이름
         * @param slot 제거할 상품 슬롯
         */
        @Override
        public void removeItem(String shopName, int slot) {
            ShopInfo shopInfo = load(shopName);
            List<ShopItemInfo> items = new ArrayList<>(shopInfo.items());

            items.removeIf(savedItem -> savedItem.slot() == slot);
            shops.remove(shopInfo);
            shops.add(new ShopInfo(shopName, shopInfo.size(), items));
        }
    }

    /**
     * 테스트에서 사용하지 않는 경제 기능을 대체하는 서비스입니다.
     */
    private static class FakeEconomyService implements EconomyService {

        @Override
        public EconomyAccount getAccount(UUID uuid, String name) {
            return new EconomyAccount(uuid, name, BigDecimal.ZERO);
        }

        @Override
        public BigDecimal getBalance(UUID uuid, String name) {
            return BigDecimal.ZERO;
        }

        @Override
        public void setBalance(UUID uuid, String name, BigDecimal balance) {
        }

        @Override
        public BigDecimal deposit(UUID uuid, String name, BigDecimal amount) {
            return BigDecimal.ZERO;
        }

        @Override
        public boolean withdraw(UUID uuid, String name, BigDecimal amount) {
            return false;
        }

        @Override
        public boolean transfer(UUID fromUuid, String fromName, UUID toUuid, String toName, BigDecimal amount) {
            return false;
        }

        @Override
        public boolean hasEnough(UUID uuid, String name, BigDecimal amount) {
            return false;
        }
    }
}
