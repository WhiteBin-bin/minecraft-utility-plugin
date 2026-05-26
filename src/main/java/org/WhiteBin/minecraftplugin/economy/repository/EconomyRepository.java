package org.WhiteBin.minecraftplugin.economy.repository;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.economy.model.EconomyAccount;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 플레이어 경제 계좌 데이터를 YAML 파일에 저장하고 불러오는 저장소입니다.
 */
@RequiredArgsConstructor
public class EconomyRepository {

    private final JavaPlugin plugin;

    /**
     * 플레이어 UUID에 저장된 경제 계좌를 불러옵니다.
     *
     * @param uuid 계좌 소유자 UUID
     * @param fallbackName 저장된 이름이 없을 때 사용할 이름
     * @param defaultBalance 저장된 잔액이 없을 때 사용할 기본 잔액
     * @return 저장된 경제 계좌
     */
    public EconomyAccount loadAccount(UUID uuid, String fallbackName, BigDecimal defaultBalance) {
        File file = getEconomyFile(uuid);

        if (!file.exists()) {
            return new EconomyAccount(uuid, fallbackName, defaultBalance);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String balance = config.getString("balance", defaultBalance.toPlainString());

        return new EconomyAccount(uuid, config.getString("name", fallbackName), new BigDecimal(balance));
    }

    /**
     * 플레이어 UUID에 해당하는 경제 계좌를 저장합니다.
     *
     * @param account 저장할 경제 계좌
     */
    public void saveAccount(EconomyAccount account) {
        File file = getEconomyFile(account.uuid());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", account.name());
        config.set("balance", account.balance().toPlainString());
        saveConfig(account.uuid(), file, config);
    }

    private void saveConfig(UUID uuid, File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("잔액 저장 중 오류 발생: " + uuid);
            e.printStackTrace();
        }
    }

    private File getEconomyFile(UUID uuid) {
        return new File(getEconomyFolder(), uuid + ".yml");
    }

    private File getEconomyFolder() {
        File economyFolder = new File(plugin.getDataFolder(), "economy");

        if (!economyFolder.exists()) {
            economyFolder.mkdirs();
        }

        return economyFolder;
    }
}
