package org.WhiteBin.minecraftplugin.shop.repository;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.shop.model.ShopTransactionLog;
import org.WhiteBin.minecraftplugin.shop.model.ShopTransactionType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * 상점 거래 로그를 YAML 파일에 저장하고 불러오는 저장소입니다.
 */
@RequiredArgsConstructor
public class ShopTransactionLogRepository {

    private final JavaPlugin plugin;

    /**
     * 상점 거래 로그를 저장합니다.
     *
     * @param log 저장할 거래 로그
     */
    public void save(ShopTransactionLog log) {
        saveLog(getShopLogFile(log.shopName()), log);
        saveLog(getPlayerLogFile(log.playerUuid()), log);
    }

    /**
     * 상점 이름 기준 거래 로그를 조회합니다.
     *
     * @param shopName 상점 이름
     * @return 거래 로그 목록
     */
    public List<ShopTransactionLog> findByShopName(String shopName) {
        return loadLogs(getShopLogFile(shopName));
    }

    /**
     * 플레이어 UUID 기준 거래 로그를 조회합니다.
     *
     * @param playerUuid 플레이어 UUID
     * @return 거래 로그 목록
     */
    public List<ShopTransactionLog> findByPlayerUuid(UUID playerUuid) {
        return loadLogs(getPlayerLogFile(playerUuid));
    }

    private void saveLog(File file, ShopTransactionLog log) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String key = "logs." + System.currentTimeMillis() + "-" + UUID.randomUUID();

        config.set(key + ".occurred-at", log.occurredAt().toString());
        config.set(key + ".type", log.type().name());
        config.set(key + ".shop-name", log.shopName());
        config.set(key + ".slot", log.slot());
        config.set(key + ".item-name", log.itemName());
        config.set(key + ".player-uuid", log.playerUuid().toString());
        config.set(key + ".player-name", log.playerName());
        config.set(key + ".quantity", log.quantity());
        config.set(key + ".unit-price", log.unitPrice().toPlainString());
        config.set(key + ".total-price", log.totalPrice().toPlainString());
        saveConfig(file, config);
    }

    private List<ShopTransactionLog> loadLogs(File file) {
        if (!file.exists()) {
            return List.of();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection logs = config.getConfigurationSection("logs");

        if (logs == null) {
            return List.of();
        }

        return logs.getKeys(false).stream()
                .map(key -> toLog(logs.getConfigurationSection(key)))
                .sorted(Comparator.comparing(ShopTransactionLog::occurredAt).reversed())
                .toList();
    }

    private ShopTransactionLog toLog(ConfigurationSection section) {
        return new ShopTransactionLog(
                LocalDateTime.parse(section.getString("occurred-at")),
                ShopTransactionType.valueOf(section.getString("type")),
                section.getString("shop-name"),
                section.getInt("slot"),
                section.getString("item-name"),
                UUID.fromString(section.getString("player-uuid")),
                section.getString("player-name"),
                section.getInt("quantity"),
                new BigDecimal(section.getString("unit-price")),
                new BigDecimal(section.getString("total-price"))
        );
    }

    private void saveConfig(File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("상점 거래 로그 저장 중 오류 발생: " + file.getName());
            e.printStackTrace();
        }
    }

    private File getShopLogFile(String shopName) {
        return new File(getShopLogFolder(), shopName + ".yml");
    }

    private File getPlayerLogFile(UUID playerUuid) {
        return new File(getPlayerLogFolder(), playerUuid + ".yml");
    }

    private File getShopLogFolder() {
        File folder = new File(plugin.getDataFolder(), "shop-logs/shops");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }

    private File getPlayerLogFolder() {
        File folder = new File(plugin.getDataFolder(), "shop-logs/players");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }
}
