package org.WhiteBin.minecraftplugin.shop.inventory;

import net.kyori.adventure.text.Component;
import org.WhiteBin.minecraftplugin.shop.model.ShopInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 상점 상품을 표시하는 GUI 인벤토리를 생성합니다.
 */
public class ShopGuiFactory {

    public static final int PRICE_CONFIRM_SLOT = 53;
    public static final int PRICE_BACKSPACE_SLOT = 49;
    public static final int PRICE_CANCEL_SLOT = 45;
    public static final int PRICE_DISPLAY_SLOT = 4;

    private static final int[] DIGIT_SLOTS = {40, 12, 13, 14, 21, 22, 23, 30, 31, 32};
    private static final int[] KEYPAD_BORDER_SLOTS = {2, 3, 5, 6, 11, 15, 20, 24, 29, 33, 38, 39, 41, 42, 47, 48, 50, 51};
    private static final int PRICE_GUI_SIZE = 54;

    /**
     * 상점 GUI 인벤토리를 생성합니다.
     *
     * @param shopInfo 상점 정보
     * @return 상점 GUI 인벤토리
     */
    public Inventory createShopInventory(ShopInfo shopInfo) {
        ShopInventoryHolder holder = new ShopInventoryHolder(shopInfo.name());
        Inventory inventory = Bukkit.createInventory(holder, shopInfo.size(), Component.text(shopInfo.name() + " 상점"));

        holder.setInventory(inventory);
        shopInfo.items().forEach(itemInfo -> inventory.setItem(itemInfo.slot(), createDisplayItem(itemInfo)));
        return inventory;
    }

    /**
     * 상점 편집 GUI 인벤토리를 생성합니다.
     *
     * @param shopInfo 상점 정보
     * @return 상점 편집 GUI 인벤토리
     */
    public Inventory createShopEditInventory(ShopInfo shopInfo) {
        ShopEditInventoryHolder holder = new ShopEditInventoryHolder(shopInfo.name());
        Inventory inventory = Bukkit.createInventory(holder, shopInfo.size(), Component.text(shopInfo.name() + " 상점 편집"));

        holder.setInventory(inventory);
        shopInfo.items().forEach(itemInfo -> inventory.setItem(itemInfo.slot(), createDisplayItem(itemInfo)));
        return inventory;
    }

    /**
     * 상품 가격 입력 GUI 인벤토리를 생성합니다.
     *
     * @param shopName 상점 이름
     * @param slot 상품 슬롯
     * @param itemStack 등록할 아이템
     * @param input 현재 입력값
     * @return 상품 가격 입력 GUI 인벤토리
     */
    public Inventory createPriceInputInventory(String shopName, int slot, ItemStack itemStack, BigDecimal buyPrice, String input) {
        ShopPriceInputHolder holder = new ShopPriceInputHolder(shopName, slot, itemStack.clone(), buyPrice, input);
        Inventory inventory = Bukkit.createInventory(holder, PRICE_GUI_SIZE, Component.text(buyPrice == null ? "구매가 입력" : "판매가 입력"));

        holder.setInventory(inventory);
        for (int borderSlot : KEYPAD_BORDER_SLOTS) {
            inventory.setItem(borderSlot, createButton(Material.BLACK_STAINED_GLASS_PANE, " ", List.of()));
        }

        inventory.setItem(PRICE_DISPLAY_SLOT, createButton(Material.PAPER, "입력값", List.of(
                Component.text(input.isBlank() ? "0원" : input + "원"),
                Component.text(buyPrice == null ? "구매가를 입력하세요." : "판매가를 입력하세요.")
        )));

        fillKeypad(inventory, "등록");
        return inventory;
    }

    /**
     * 구매 수량 입력 GUI 인벤토리를 생성합니다.
     *
     * @param shopName 상점 이름
     * @param itemInfo 구매할 상품 정보
     * @param selling 판매 모드 여부
     * @param input 현재 입력값
     * @return 구매 수량 입력 GUI 인벤토리
     */
    public Inventory createQuantityInputInventory(String shopName, ShopItemInfo itemInfo, boolean selling, String input) {
        BigDecimal unitPrice = selling ? itemInfo.sellPrice() : itemInfo.buyPrice();
        ShopQuantityInputHolder holder = new ShopQuantityInputHolder(shopName, itemInfo.slot(), unitPrice, selling, input);
        Inventory inventory = Bukkit.createInventory(holder, PRICE_GUI_SIZE, Component.text(selling ? "판매 수량 입력" : "구매 수량 입력"));
        String quantity = input.isBlank() ? "0" : input;
        String totalPrice = unitPrice
                .multiply(new BigDecimal(quantity))
                .stripTrailingZeros()
                .toPlainString();

        holder.setInventory(inventory);
        for (int borderSlot : KEYPAD_BORDER_SLOTS) {
            inventory.setItem(borderSlot, createButton(Material.BLACK_STAINED_GLASS_PANE, " ", List.of()));
        }

        inventory.setItem(PRICE_DISPLAY_SLOT, createButton(Material.PAPER, "입력값", List.of(
                Component.text("수량: " + quantity + "개"),
                Component.text((selling ? "개당 판매가: " : "개당 구매가: ") + unitPrice.stripTrailingZeros().toPlainString() + "원"),
                Component.text("총 가격: " + totalPrice + "원")
        )));

        fillKeypad(inventory, selling ? "판매" : "구매");
        return inventory;
    }

    /**
     * 가격 입력 GUI의 숫자 슬롯이면 숫자를 반환합니다.
     *
     * @param slot 클릭한 슬롯
     * @return 숫자 또는 숫자 슬롯이 아니면 {@code null}
     */
    public Integer getDigit(int slot) {
        for (int digit = 0; digit < DIGIT_SLOTS.length; digit++) {
            if (DIGIT_SLOTS[digit] == slot) {
                return digit;
            }
        }

        return null;
    }

    private ItemStack createDisplayItem(ShopItemInfo itemInfo) {
        ItemStack itemStack = itemInfo.itemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> lore = itemMeta.lore() == null ? new ArrayList<>() : new ArrayList<>(itemMeta.lore());
        String buyPrice = itemInfo.buyPrice().stripTrailingZeros().toPlainString();
        String sellPrice = itemInfo.sellPrice().stripTrailingZeros().toPlainString();

        lore.add(Component.text("개당 구매가: " + buyPrice + "원"));
        lore.add(Component.text("개당 판매가: " + sellPrice + "원"));
        lore.add(Component.text("좌클릭: 구매"));
        lore.add(Component.text("우클릭: 판매"));
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void fillKeypad(Inventory inventory, String confirmName) {
        for (int digit = 0; digit < DIGIT_SLOTS.length; digit++) {
            inventory.setItem(DIGIT_SLOTS[digit], createDigitButton(digit));
        }

        inventory.setItem(PRICE_CANCEL_SLOT, createButton(Material.RED_DYE, "취소", List.of()));
        inventory.setItem(PRICE_BACKSPACE_SLOT, createButton(Material.GRAY_DYE, "지우기", List.of()));
        inventory.setItem(PRICE_CONFIRM_SLOT, createButton(Material.LIME_DYE, confirmName, List.of()));
    }

    private ItemStack createButton(Material material, String name, List<Component> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text(name));
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack createDigitButton(int digit) {
        ItemStack itemStack = new ItemStack(digit == 0 ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE, Math.max(1, digit));
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("숫자 " + digit));
        itemMeta.lore(List.of(Component.text("클릭해서 " + digit + " 입력")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
