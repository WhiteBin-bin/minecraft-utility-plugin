package org.WhiteBin.minecraftplugin.shop.repository;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.shop.model.ShopInfo;
import org.WhiteBin.minecraftplugin.shop.model.ShopItemInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * 상점 데이터를 YAML 파일에 저장하고 불러오는 저장소입니다.
 */
@RequiredArgsConstructor
public class ShopRepository {

    private static final int DEFAULT_SHOP_SIZE = 54;

    private final JavaPlugin plugin;

    /**
     * 상점이 존재하는지 확인합니다.
     *
     * @param shopName 상점 이름
     * @return 상점 파일이 존재하면 {@code true}
     */
    public boolean exists(String shopName) {
        return getShopFile(shopName).exists();
    }

    /**
     * 상점을 생성합니다.
     *
     * @param shopName 생성할 상점 이름
     */
    public void create(String shopName) {
        File file = getShopFile(shopName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", shopName);
        config.set("size", DEFAULT_SHOP_SIZE);
        saveConfig(shopName, file, config);
    }

    /**
     * 상점을 삭제합니다.
     *
     * @param shopName 삭제할 상점 이름
     */
    public void delete(String shopName) {
        getShopFile(shopName).delete();
    }

    /**
     * 상점 이름 목록을 반환합니다.
     *
     * @return 상점 이름 목록
     */
    public List<String> findShopNames() {
        File[] files = getShopFolder().listFiles((folder, name) -> name.endsWith(".yml"));

        if (files == null) {
            return List.of();
        }

        return List.of(files).stream()
                .map(file -> file.getName().replace(".yml", ""))
                .sorted()
                .toList();
    }

    /**
     * 상점 정보를 불러옵니다.
     *
     * @param shopName 상점 이름
     * @return 상점 정보
     */
    public ShopInfo load(String shopName) {
        File file = getShopFile(shopName);

        if (!file.exists()) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return new ShopInfo(shopName, config.getInt("size", DEFAULT_SHOP_SIZE), loadItems(config));
    }

    /**
     * 상점 상품을 저장합니다.
     *
     * @param shopName 상점 이름
     * @param itemInfo 저장할 상품 정보
     */
    public void saveItem(String shopName, ShopItemInfo itemInfo) {
        File file = getShopFile(shopName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String itemPath = "items." + itemInfo.slot();

        config.set(itemPath + ".price", itemInfo.price().toPlainString());
        config.set(itemPath + ".item", itemInfo.itemStack());
        saveConfig(shopName, file, config);
    }

    /**
     * 상점 상품을 제거합니다.
     *
     * @param shopName 상점 이름
     * @param slot 제거할 상품 슬롯
     */
    public void removeItem(String shopName, int slot) {
        File file = getShopFile(shopName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("items." + slot, null);
        saveConfig(shopName, file, config);
    }

    private List<ShopItemInfo> loadItems(YamlConfiguration config) {
        ConfigurationSection items = config.getConfigurationSection("items");

        if (items == null) {
            return List.of();
        }

        return items.getKeys(false).stream()
                .map(slot -> toShopItemInfo(items, slot))
                .sorted(Comparator.comparingInt(ShopItemInfo::slot))
                .toList();
    }

    private ShopItemInfo toShopItemInfo(ConfigurationSection items, String slot) {
        return new ShopItemInfo(
                Integer.parseInt(slot),
                items.getItemStack(slot + ".item"),
                new BigDecimal(items.getString(slot + ".price", "0"))
        );
    }

    private void saveConfig(String shopName, File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("상점 저장 중 오류 발생: " + shopName);
            e.printStackTrace();
        }
    }

    private File getShopFile(String shopName) {
        return new File(getShopFolder(), shopName + ".yml");
    }

    private File getShopFolder() {
        File shopFolder = new File(plugin.getDataFolder(), "shops");

        if (!shopFolder.exists()) {
            shopFolder.mkdirs();
        }

        return shopFolder;
    }
}
