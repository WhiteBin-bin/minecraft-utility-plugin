package org.WhiteBin.minecraftplugin;

import org.WhiteBin.minecraftplugin.storage.command.StorageCommand;
import org.WhiteBin.minecraftplugin.storage.listener.StorageListener;
import org.WhiteBin.minecraftplugin.storage.repository.StorageRepository;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class minecraftPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        StorageRepository storageRepository = new StorageRepository(this);
        StorageService storageService = new StorageService(storageRepository);

        getCommand("storage").setExecutor(new StorageCommand(storageService));
        Bukkit.getPluginManager().registerEvents(new StorageListener(storageService), this);

        getLogger().info("창고 플러그인이 켜졌습니다.");
    }

    @Override
    public void onDisable() {
        getLogger().info("창고 플러그인이 꺼졌습니다.");
    }
}
