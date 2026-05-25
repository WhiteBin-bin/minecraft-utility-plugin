package org.WhiteBin.minecraftplugin.storage.repository;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.*;


@RequiredArgsConstructor
public class StorageRepository {

    private final JavaPlugin plugin;

    public void save(UUID uuid, Inventory inventory) {
        File file = getStorageFile(uuid);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<ItemStack> items = Arrays.asList(inventory.getContents());
        config.set("items", items);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("창고 저장 중 오류 발생: " + uuid);
            e.printStackTrace();
        }
    }

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

    private File getStorageFile(UUID uuid) {
        File storageFolder = new File(plugin.getDataFolder(), "storages");

        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }

        return new File(storageFolder, uuid + ".yml");
    }
}
