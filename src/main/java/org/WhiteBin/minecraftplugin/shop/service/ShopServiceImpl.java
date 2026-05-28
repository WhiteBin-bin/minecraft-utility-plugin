package org.WhiteBin.minecraftplugin.shop.service;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.economy.service.EconomyService;
import org.WhiteBin.minecraftplugin.shop.model.ShopInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopPurchaseResult;
import org.WhiteBin.minecraftplugin.shop.repository.ShopRepository;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

/**
 * GUI 상점 생성, 상품 등록, 구매 기능을 처리하는 {@link ShopService} 구현체입니다.
 */
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private static final int MAX_SHOP_SIZE = 54;

    private final ShopRepository shopRepository;
    private final EconomyService economyService;

    /**
     * 상점을 생성합니다.
     *
     * @param shopName 생성할 상점 이름
     * @return 새로 생성했으면 {@code true}
     */
    @Override
    public boolean createShop(String shopName) {
        if (shopRepository.exists(shopName)) {
            return false;
        }

        shopRepository.create(shopName);
        return true;
    }

    /**
     * 상점을 삭제합니다.
     *
     * @param shopName 삭제할 상점 이름
     * @return 삭제했으면 {@code true}
     */
    @Override
    public boolean deleteShop(String shopName) {
        if (!shopRepository.exists(shopName)) {
            return false;
        }

        shopRepository.delete(shopName);
        return true;
    }

    /**
     * 상점 이름 목록을 반환합니다.
     *
     * @return 상점 이름 목록
     */
    @Override
    public List<String> getShopNames() {
        return shopRepository.findShopNames();
    }

    /**
     * 상점 정보를 반환합니다.
     *
     * @param shopName 상점 이름
     * @return 상점 정보
     */
    @Override
    public ShopInfo getShop(String shopName) {
        return shopRepository.load(shopName);
    }

    /**
     * 상점에 상품을 등록합니다.
     *
     * @param shopName 상점 이름
     * @param itemStack 등록할 아이템
     * @param price 상품 개당 가격
     * @return 등록된 상품 정보 또는 등록할 수 없으면 {@code null}
     */
    @Override
    public ShopItemInfo addItem(String shopName, ItemStack itemStack, BigDecimal price) {
        return addItem(shopName, itemStack, price, price);
    }

    /**
     * 상점에 상품을 등록합니다.
     *
     * @param shopName 상점 이름
     * @param itemStack 등록할 아이템
     * @param buyPrice 상품 구매가
     * @param sellPrice 상품 판매가
     * @return 등록된 상품 정보 또는 등록할 수 없으면 {@code null}
     */
    @Override
    public ShopItemInfo addItem(String shopName, ItemStack itemStack, BigDecimal buyPrice, BigDecimal sellPrice) {
        ShopInfo shopInfo = shopRepository.load(shopName);

        if (shopInfo == null || invalidPrice(buyPrice) || invalidPrice(sellPrice)) {
            return null;
        }

        int slot = findEmptySlot(shopInfo);

        if (slot < 0) {
            return null;
        }

        ShopItemInfo itemInfo = new ShopItemInfo(slot, normalizeItemStack(itemStack), buyPrice, sellPrice);
        shopRepository.saveItem(shopName, itemInfo);
        return itemInfo;
    }

    /**
     * 상점의 특정 슬롯에 상품을 등록하거나 교체합니다.
     *
     * @param shopName 상점 이름
     * @param slot 등록할 슬롯
     * @param itemStack 등록할 아이템
     * @param unitPrice 상품 개당 가격
     * @return 등록된 상품 정보 또는 등록할 수 없으면 {@code null}
     */
    @Override
    public ShopItemInfo setItem(String shopName, int slot, ItemStack itemStack, BigDecimal unitPrice) {
        return setItem(shopName, slot, itemStack, unitPrice, unitPrice);
    }

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
    @Override
    public ShopItemInfo setItem(String shopName, int slot, ItemStack itemStack, BigDecimal buyPrice, BigDecimal sellPrice) {
        ShopInfo shopInfo = shopRepository.load(shopName);

        if (shopInfo == null
                || slot < 0
                || slot >= shopInfo.size()
                || itemStack.isEmpty()
                || invalidPrice(buyPrice)
                || invalidPrice(sellPrice)) {
            return null;
        }

        ShopItemInfo itemInfo = new ShopItemInfo(slot, normalizeItemStack(itemStack), buyPrice, sellPrice);
        shopRepository.saveItem(shopName, itemInfo);
        return itemInfo;
    }

    /**
     * 상점 상품을 제거합니다.
     *
     * @param shopName 상점 이름
     * @param slot 제거할 상품 슬롯
     * @return 제거했으면 {@code true}
     */
    @Override
    public boolean removeItem(String shopName, int slot) {
        ShopInfo shopInfo = shopRepository.load(shopName);

        if (shopInfo == null || findItem(shopInfo, slot) == null) {
            return false;
        }

        shopRepository.removeItem(shopName, slot);
        return true;
    }

    /**
     * 상점 상품 위치를 이동합니다.
     *
     * @param shopName 상점 이름
     * @param fromSlot 기존 슬롯
     * @param toSlot 이동할 슬롯
     * @return 이동했으면 {@code true}
     */
    @Override
    public boolean moveItem(String shopName, int fromSlot, int toSlot) {
        ShopInfo shopInfo = shopRepository.load(shopName);

        if (shopInfo == null || fromSlot == toSlot || toSlot < 0 || toSlot >= shopInfo.size()) {
            return false;
        }

        ShopItemInfo itemInfo = findItem(shopInfo, fromSlot);

        if (itemInfo == null || findItem(shopInfo, toSlot) != null) {
            return false;
        }

        shopRepository.removeItem(shopName, fromSlot);
        shopRepository.saveItem(shopName, new ShopItemInfo(toSlot, itemInfo.itemStack(), itemInfo.buyPrice(), itemInfo.sellPrice()));
        return true;
    }

    /**
     * 플레이어가 상점 상품을 구매합니다.
     *
     * @param player 구매할 플레이어
     * @param shopName 상점 이름
     * @param slot 구매할 상품 슬롯
     * @param quantity 구매할 수량
     * @return 구매 처리 결과
     */
    @Override
    public ShopPurchaseResult purchase(Player player, String shopName, int slot, int quantity) {
        ShopInfo shopInfo = shopRepository.load(shopName);

        if (shopInfo == null) {
            return ShopPurchaseResult.SHOP_NOT_FOUND;
        }

        ShopItemInfo itemInfo = findItem(shopInfo, slot);

        if (itemInfo == null) {
            return ShopPurchaseResult.ITEM_NOT_FOUND;
        }

        BigDecimal totalPrice = itemInfo.buyPrice().multiply(BigDecimal.valueOf(quantity));

        if (quantity <= 0 || !economyService.hasEnough(player.getUniqueId(), player.getName(), totalPrice)) {
            return ShopPurchaseResult.NOT_ENOUGH_MONEY;
        }

        if (!hasInventorySpace(player, itemInfo.itemStack(), quantity)) {
            return ShopPurchaseResult.INVENTORY_FULL;
        }

        economyService.withdraw(player.getUniqueId(), player.getName(), totalPrice);
        giveItem(player, itemInfo.itemStack(), quantity);
        return ShopPurchaseResult.SUCCESS;
    }

    /**
     * 플레이어가 상점에 상품을 판매합니다.
     *
     * @param player 판매할 플레이어
     * @param shopName 상점 이름
     * @param slot 판매할 상품 슬롯
     * @param quantity 판매할 수량
     * @return 판매 처리 결과
     */
    @Override
    public ShopPurchaseResult sell(Player player, String shopName, int slot, int quantity) {
        ShopInfo shopInfo = shopRepository.load(shopName);

        if (shopInfo == null) {
            return ShopPurchaseResult.SHOP_NOT_FOUND;
        }

        ShopItemInfo itemInfo = findItem(shopInfo, slot);

        if (itemInfo == null) {
            return ShopPurchaseResult.ITEM_NOT_FOUND;
        }

        if (quantity <= 0 || countSimilarItems(player, itemInfo.itemStack()) < quantity) {
            return ShopPurchaseResult.NOT_ENOUGH_ITEM;
        }

        removeItems(player, itemInfo.itemStack(), quantity);
        economyService.deposit(player.getUniqueId(), player.getName(), itemInfo.sellPrice().multiply(BigDecimal.valueOf(quantity)));
        return ShopPurchaseResult.SUCCESS;
    }

    private boolean invalidPrice(BigDecimal price) {
        return price == null || price.compareTo(BigDecimal.ZERO) <= 0;
    }

    private ItemStack normalizeItemStack(ItemStack itemStack) {
        ItemStack normalizedItemStack = itemStack.clone();

        normalizedItemStack.setAmount(1);
        return normalizedItemStack;
    }

    private boolean hasInventorySpace(Player player, ItemStack itemStack, int quantity) {
        int remaining = quantity;

        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null || content.getType().isAir()) {
                remaining -= itemStack.getMaxStackSize();
            } else if (content.isSimilar(itemStack)) {
                remaining -= Math.max(0, content.getMaxStackSize() - content.getAmount());
            }

            if (remaining <= 0) {
                return true;
            }
        }

        return false;
    }

    private void giveItem(Player player, ItemStack itemStack, int quantity) {
        int remaining = quantity;

        while (remaining > 0) {
            int amount = Math.min(itemStack.getMaxStackSize(), remaining);
            ItemStack purchasedItem = itemStack.clone();

            purchasedItem.setAmount(amount);
            player.getInventory().addItem(purchasedItem);
            remaining -= amount;
        }
    }

    private int countSimilarItems(Player player, ItemStack itemStack) {
        int count = 0;

        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content != null && content.isSimilar(itemStack)) {
                count += content.getAmount();
            }
        }

        return count;
    }

    private void removeItems(Player player, ItemStack itemStack, int quantity) {
        int remaining = quantity;
        ItemStack[] contents = player.getInventory().getStorageContents();

        for (int index = 0; index < contents.length && remaining > 0; index++) {
            ItemStack content = contents[index];

            if (content == null || !content.isSimilar(itemStack)) {
                continue;
            }

            int removeAmount = Math.min(content.getAmount(), remaining);
            content.setAmount(content.getAmount() - removeAmount);
            remaining -= removeAmount;

            if (content.getAmount() <= 0) {
                contents[index] = null;
            }
        }

        player.getInventory().setStorageContents(contents);
    }

    private int findEmptySlot(ShopInfo shopInfo) {
        for (int slot = 0; slot < MAX_SHOP_SIZE; slot++) {
            int currentSlot = slot;
            boolean used = shopInfo.items().stream()
                    .anyMatch(itemInfo -> itemInfo.slot() == currentSlot);

            if (!used) {
                return slot;
            }
        }

        return -1;
    }

    private ShopItemInfo findItem(ShopInfo shopInfo, int slot) {
        return shopInfo.items().stream()
                .filter(itemInfo -> itemInfo.slot() == slot)
                .findFirst()
                .orElse(null);
    }
}
