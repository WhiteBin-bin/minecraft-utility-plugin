package org.WhiteBin.minecraftplugin.storage.repository;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 플레이어 개인 창고 데이터를 파일에 저장하고 불러오는 저장소입니다.
 * <p>
 * 플레이어 UUID별 YAML 파일을 사용하여 창고 아이템 목록과 창고 크기를 관리합니다.
 */
@RequiredArgsConstructor
public class StorageRepository {

    private final JavaPlugin plugin;

    /**
     * 플레이어 UUID에 해당하는 창고 아이템 목록과 창고 크기를 저장합니다.
     * <p>
     * 인벤토리의 현재 슬롯 구성과 크기를 YAML 파일에 기록합니다.
     *
     * @param uuid 창고 소유자 UUID
     * @param inventory 저장할 창고 인벤토리
     */
    public void save(UUID uuid, Inventory inventory) {
        File file = getStorageFile(uuid);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<ItemStack> items = Arrays.asList(inventory.getContents());
        config.set("items", items);
        config.set("size", inventory.getSize());

        saveConfig(uuid, file, config);
    }

    /**
     * 플레이어 UUID에 해당하는 창고 크기만 저장합니다.
     * <p>
     * 창고 확장 시 기존 아이템 데이터는 유지하고 크기 값만 갱신합니다.
     *
     * @param uuid 창고 소유자 UUID
     * @param size 저장할 창고 크기
     */
    public void saveSize(UUID uuid, int size) {
        File file = getStorageFile(uuid);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("size", size);
        saveConfig(uuid, file, config);
    }

    /**
     * 플레이어 UUID에 저장된 창고 크기를 불러옵니다.
     * <p>
     * 저장 파일이 없거나 크기 값이 없으면 전달받은 기본 크기를 반환합니다.
     *
     * @param uuid 창고 소유자 UUID
     * @param defaultSize 저장된 크기가 없을 때 사용할 기본 크기
     * @return 저장된 창고 크기 또는 기본 창고 크기
     */
    public int loadSize(UUID uuid, int defaultSize) {
        File file = getStorageFile(uuid);

        if (!file.exists()) {
            return defaultSize;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("size", defaultSize);
    }

    /**
     * 지정한 슬롯 범위에 저장된 아이템이 있는지 확인합니다.
     * <p>
     * 창고 축소 시 제거될 슬롯 범위에 아이템이 남아 있는 경우 축소를 막기 위해 사용합니다.
     *
     * @param uuid 창고 소유자 UUID
     * @param startSlot 확인을 시작할 슬롯 번호
     * @param endSlot 확인을 끝낼 슬롯 번호
     * @return 지정한 범위에 아이템이 있으면 {@code true}
     */
    public boolean hasItemsInRange(UUID uuid, int startSlot, int endSlot) {
        File file = getStorageFile(uuid);

        if (!file.exists()) {
            return false;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<?> items = config.getList("items");

        if (items == null) {
            return false;
        }

        for (int i = startSlot; i < endSlot && i < items.size(); i++) {
            Object item = items.get(i);

            if (item instanceof ItemStack itemStack && !itemStack.getType().isAir() && itemStack.getAmount() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * YAML 설정 파일을 디스크에 저장합니다.
     * <p>
     * 저장 중 오류가 발생하면 플러그인 로그에 경고를 남깁니다.
     *
     * @param uuid 저장 대상 창고 소유자 UUID
     * @param file 저장할 파일
     * @param config 저장할 YAML 설정
     */
    private void saveConfig(UUID uuid, File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("창고 저장 중 오류 발생: " + uuid);
            e.printStackTrace();
        }
    }

    /**
     * 플레이어 UUID에 저장된 창고 아이템 목록을 인벤토리에 로드합니다.
     * <p>
     * 저장된 아이템 개수가 현재 인벤토리 크기보다 큰 경우 현재 인벤토리에 들어갈 수 있는 범위까지만 로드합니다.
     *
     * @param uuid 창고 소유자 UUID
     * @param inventory 아이템을 채워 넣을 창고 인벤토리
     */
    public void load(UUID uuid, Inventory inventory) {
        File file = getStorageFile(uuid);

        if (!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<?> items = config.getList("items");

        if (items == null) {
            return;
        }

        for (int i = 0; i < items.size() && i < inventory.getSize(); i++) {
            Object item = items.get(i);

            if (item instanceof ItemStack itemStack) {
                inventory.setItem(i, itemStack);
            }
        }
    }

    /**
     * 플레이어 UUID에 해당하는 창고 저장 파일을 반환합니다.
     * <p>
     * 창고 저장 폴더가 존재하지 않으면 새로 생성합니다.
     *
     * @param uuid 창고 소유자 UUID
     * @return 창고 데이터가 저장될 YAML 파일
     */
    private File getStorageFile(UUID uuid) {
        File storageFolder = new File(plugin.getDataFolder(), "storages");

        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }

        return new File(storageFolder, uuid + ".yml");
    }
}
