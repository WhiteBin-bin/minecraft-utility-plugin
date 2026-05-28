package org.WhiteBin.minecraftplugin.shop.listener;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.economy.service.EconomyService;
import org.WhiteBin.minecraftplugin.economy.sidebar.EconomySidebar;
import org.WhiteBin.minecraftplugin.shop.inventory.ShopEditInventoryHolder;
import org.WhiteBin.minecraftplugin.shop.inventory.ShopGuiFactory;
import org.WhiteBin.minecraftplugin.shop.inventory.ShopInventoryHolder;
import org.WhiteBin.minecraftplugin.shop.inventory.ShopPriceInputHolder;
import org.WhiteBin.minecraftplugin.shop.inventory.ShopQuantityInputHolder;
import org.WhiteBin.minecraftplugin.shop.model.ShopInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopPurchaseResult;
import org.WhiteBin.minecraftplugin.shop.service.ShopService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

/**
 * 상점 GUI 클릭 이벤트를 처리하는 리스너입니다.
 */
@RequiredArgsConstructor
public class ShopListener implements Listener {

    private final ShopService shopService;
    private final EconomyService economyService;
    private final EconomySidebar economySidebar = new EconomySidebar();
    private final ShopGuiFactory shopGuiFactory = new ShopGuiFactory();

    /**
     * 상점 GUI 클릭 시 아이템 이동을 막고 상품 구매를 처리합니다.
     *
     * @param event 인벤토리 클릭 이벤트
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getInventory().getHolder() instanceof ShopInventoryHolder holder) {
            handleShopClick(event, player, holder);
            return;
        }

        if (event.getInventory().getHolder() instanceof ShopEditInventoryHolder holder) {
            handleEditClick(event, player, holder);
            return;
        }

        if (event.getInventory().getHolder() instanceof ShopPriceInputHolder holder) {
            handlePriceInputClick(event, player, holder);
            return;
        }

        if (event.getInventory().getHolder() instanceof ShopQuantityInputHolder holder) {
            handleQuantityInputClick(event, player, holder);
        }
    }

    /**
     * 상점 편집 GUI 드래그 이벤트를 처리합니다.
     *
     * @param event 인벤토리 드래그 이벤트
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)
                || !(event.getInventory().getHolder() instanceof ShopEditInventoryHolder holder)) {
            return;
        }

        Integer topSlot = event.getRawSlots().stream()
                .filter(rawSlot -> rawSlot >= 0 && rawSlot < event.getInventory().getSize())
                .findFirst()
                .orElse(null);

        if (topSlot == null || isEmpty(event.getOldCursor())) {
            return;
        }

        event.setCancelled(true);
        player.openInventory(shopGuiFactory.createPriceInputInventory(
                holder.getShopName(),
                topSlot,
                event.getOldCursor().clone(),
                null,
                ""
        ));
    }

    private void handleShopClick(InventoryClickEvent event, Player player, ShopInventoryHolder holder) {
        event.setCancelled(true);

        if (!isTopInventorySlot(event)) {
            return;
        }

        if (isEmpty(event.getCurrentItem())) {
            return;
        }

        ShopItemInfo itemInfo = findStoredItemInfo(holder.getShopName(), event.getRawSlot());

        if (itemInfo == null) {
            player.sendMessage("상품을 찾을 수 없습니다.");
            return;
        }

        player.openInventory(shopGuiFactory.createQuantityInputInventory(holder.getShopName(), itemInfo, event.isRightClick(), ""));
    }

    private void handleEditClick(InventoryClickEvent event, Player player, ShopEditInventoryHolder holder) {
        if (event.isShiftClick()) {
            handleEditShiftClick(event, player, holder);
            return;
        }

        if (!isTopInventorySlot(event)) {
            return;
        }

        event.setCancelled(true);

        if (event.isRightClick() && !isEmpty(event.getCurrentItem())) {
            removeItem(player, holder.getShopName(), event.getRawSlot());
            return;
        }

        ItemStack targetItem = isEmpty(event.getCursor())
                ? findStoredItem(holder.getShopName(), event.getRawSlot())
                : event.getCursor();

        if (isEmpty(targetItem)) {
            return;
        }

        player.openInventory(shopGuiFactory.createPriceInputInventory(
                holder.getShopName(),
                event.getRawSlot(),
                targetItem.clone(),
                null,
                ""
        ));
    }

    private void handleEditShiftClick(InventoryClickEvent event, Player player, ShopEditInventoryHolder holder) {
        event.setCancelled(true);

        if (isTopInventorySlot(event)) {
            return;
        }

        if (isEmpty(event.getCurrentItem())) {
            return;
        }

        Integer emptySlot = findEmptySlot(holder.getShopName());

        if (emptySlot == null) {
            player.sendMessage("상점이 가득 찼습니다.");
            return;
        }

        player.openInventory(shopGuiFactory.createPriceInputInventory(
                holder.getShopName(),
                emptySlot,
                event.getCurrentItem().clone(),
                null,
                ""
        ));
    }

    private void handlePriceInputClick(InventoryClickEvent event, Player player, ShopPriceInputHolder holder) {
        event.setCancelled(true);

        if (!isTopInventorySlot(event)) {
            return;
        }

        Integer digit = shopGuiFactory.getDigit(event.getRawSlot());

        if (digit != null) {
            openPriceInput(player, holder, holder.getInput() + digit);
            return;
        }

        if (event.getRawSlot() == ShopGuiFactory.PRICE_BACKSPACE_SLOT) {
            String input = holder.getInput();
            openPriceInput(player, holder, input.isEmpty() ? "" : input.substring(0, input.length() - 1));
            return;
        }

        if (event.getRawSlot() == ShopGuiFactory.PRICE_CANCEL_SLOT) {
            openEditInventory(player, holder.getShopName());
            return;
        }

        if (event.getRawSlot() == ShopGuiFactory.PRICE_CONFIRM_SLOT) {
            registerItem(player, holder);
        }
    }

    private void handleQuantityInputClick(InventoryClickEvent event, Player player, ShopQuantityInputHolder holder) {
        event.setCancelled(true);

        if (!isTopInventorySlot(event)) {
            return;
        }

        Integer digit = shopGuiFactory.getDigit(event.getRawSlot());

        if (digit != null) {
            openQuantityInput(player, holder, holder.getInput() + digit);
            return;
        }

        if (event.getRawSlot() == ShopGuiFactory.PRICE_BACKSPACE_SLOT) {
            String input = holder.getInput();
            openQuantityInput(player, holder, input.isEmpty() ? "" : input.substring(0, input.length() - 1));
            return;
        }

        if (event.getRawSlot() == ShopGuiFactory.PRICE_CANCEL_SLOT) {
            openShopInventory(player, holder.getShopName());
            return;
        }

        if (event.getRawSlot() == ShopGuiFactory.PRICE_CONFIRM_SLOT) {
            purchaseItem(player, holder);
        }
    }

    private void registerItem(Player player, ShopPriceInputHolder holder) {
        BigDecimal price = parseUnitPrice(player, holder.getInput());

        if (price == null) {
            openPriceInput(player, holder, holder.getInput());
            return;
        }

        if (holder.getBuyPrice() == null) {
            player.openInventory(shopGuiFactory.createPriceInputInventory(
                    holder.getShopName(),
                    holder.getSlot(),
                    holder.getItemStack(),
                    price,
                    ""
            ));
            return;
        }

        ShopItemInfo itemInfo = shopService.setItem(holder.getShopName(), holder.getSlot(), holder.getItemStack(), holder.getBuyPrice(), price);

        if (itemInfo == null) {
            player.sendMessage("상품을 등록할 수 없습니다.");
            openEditInventory(player, holder.getShopName());
            return;
        }

        player.sendMessage(holder.getShopName() + " 상점 " + itemInfo.slot() + "번 슬롯에 상품을 등록했습니다.");
        openEditInventory(player, holder.getShopName());
    }

    private void removeItem(Player player, String shopName, int slot) {
        if (!shopService.removeItem(shopName, slot)) {
            player.sendMessage("제거할 상품을 찾을 수 없습니다.");
            return;
        }

        player.sendMessage(shopName + " 상점 " + slot + "번 슬롯의 상품을 제거했습니다.");
        openEditInventory(player, shopName);
    }

    private void openPriceInput(Player player, ShopPriceInputHolder holder, String input) {
        player.openInventory(shopGuiFactory.createPriceInputInventory(
                holder.getShopName(),
                holder.getSlot(),
                holder.getItemStack(),
                holder.getBuyPrice(),
                input
        ));
    }

    private void openQuantityInput(Player player, ShopQuantityInputHolder holder, String input) {
        ShopItemInfo itemInfo = findStoredItemInfo(holder.getShopName(), holder.getSlot());

        if (itemInfo == null) {
            player.sendMessage("상품을 찾을 수 없습니다.");
            openShopInventory(player, holder.getShopName());
            return;
        }

        player.openInventory(shopGuiFactory.createQuantityInputInventory(
                holder.getShopName(),
                itemInfo,
                holder.isSelling(),
                input
        ));
    }

    private void openShopInventory(Player player, String shopName) {
        ShopInfo shopInfo = shopService.getShop(shopName);

        if (shopInfo == null) {
            player.closeInventory();
            player.sendMessage("상점을 찾을 수 없습니다.");
            return;
        }

        player.openInventory(shopGuiFactory.createShopInventory(shopInfo));
    }

    private void openEditInventory(Player player, String shopName) {
        ShopInfo shopInfo = shopService.getShop(shopName);

        if (shopInfo == null) {
            player.closeInventory();
            player.sendMessage("상점을 찾을 수 없습니다.");
            return;
        }

        player.openInventory(shopGuiFactory.createShopEditInventory(shopInfo));
    }

    private ItemStack findStoredItem(String shopName, int slot) {
        ShopItemInfo itemInfo = findStoredItemInfo(shopName, slot);

        if (itemInfo == null) {
            return null;
        }

        return itemInfo.itemStack();
    }

    private ShopItemInfo findStoredItemInfo(String shopName, int slot) {
        ShopInfo shopInfo = shopService.getShop(shopName);

        if (shopInfo == null) {
            return null;
        }

        return shopInfo.items().stream()
                .filter(itemInfo -> itemInfo.slot() == slot)
                .findFirst()
                .orElse(null);
    }

    private Integer findEmptySlot(String shopName) {
        ShopInfo shopInfo = shopService.getShop(shopName);

        if (shopInfo == null) {
            return null;
        }

        for (int slot = 0; slot < shopInfo.size(); slot++) {
            int currentSlot = slot;
            boolean used = shopInfo.items().stream()
                    .anyMatch(itemInfo -> itemInfo.slot() == currentSlot);

            if (!used) {
                return slot;
            }
        }

        return null;
    }


    private BigDecimal parseUnitPrice(Player player, String input) {
        try {
            BigDecimal unitPrice = new BigDecimal(input);

            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                player.sendMessage("개당 가격은 0보다 커야 합니다.");
                return null;
            }

            return unitPrice;
        } catch (NumberFormatException e) {
            player.sendMessage("개당 가격은 숫자로 입력해야 합니다.");
            return null;
        }
    }

    private void purchaseItem(Player player, ShopQuantityInputHolder holder) {
        Integer quantity = parseQuantity(player, holder.getInput());

        if (quantity == null) {
            openQuantityInput(player, holder, holder.getInput());
            return;
        }

        ShopPurchaseResult result = holder.isSelling()
                ? shopService.sell(player, holder.getShopName(), holder.getSlot(), quantity)
                : shopService.purchase(player, holder.getShopName(), holder.getSlot(), quantity);
        handlePurchaseResult(player, result, holder.isSelling());

        if (result == ShopPurchaseResult.SUCCESS) {
            openShopInventory(player, holder.getShopName());
        }
    }

    private Integer parseQuantity(Player player, String input) {
        try {
            int quantity = Integer.parseInt(input);

            if (quantity <= 0) {
                player.sendMessage("구매 수량은 1개 이상이어야 합니다.");
                return null;
            }

            return quantity;
        } catch (NumberFormatException e) {
            player.sendMessage("구매 수량은 숫자로 입력해야 합니다.");
            return null;
        }
    }

    private boolean isTopInventorySlot(InventoryClickEvent event) {
        return event.getRawSlot() >= 0 && event.getRawSlot() < event.getInventory().getSize();
    }

    private boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0;
    }

    private void handlePurchaseResult(Player player, ShopPurchaseResult result, boolean selling) {
        switch (result) {
            case SUCCESS -> {
                economySidebar.showBalance(player, economyService.getBalance(player.getUniqueId(), player.getName()));
                player.sendMessage(selling ? "상품을 판매했습니다." : "상품을 구매했습니다.");
            }
            case SHOP_NOT_FOUND -> player.sendMessage("상점을 찾을 수 없습니다.");
            case ITEM_NOT_FOUND -> player.sendMessage("상품을 찾을 수 없습니다.");
            case NOT_ENOUGH_MONEY -> player.sendMessage("돈이 부족합니다.");
            case NOT_ENOUGH_ITEM -> player.sendMessage("판매할 아이템이 부족합니다.");
            case INVENTORY_FULL -> player.sendMessage("인벤토리 공간이 부족합니다.");
        }
    }
}
