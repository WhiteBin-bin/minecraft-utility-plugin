package org.WhiteBin.minecraftplugin;

import org.WhiteBin.minecraftplugin.storage.command.StorageCommand;
import org.WhiteBin.minecraftplugin.storage.listener.StorageListener;
import org.WhiteBin.minecraftplugin.storage.repository.StorageRepository;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.WhiteBin.minecraftplugin.storage.service.StorageServiceImpl;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 마인크래프트 편의 기능 플러그인의 메인 클래스입니다.
 * <p>
 * 플러그인 활성화 시 개인 창고 기능에 필요한 명령어와 이벤트 리스너를 등록하며,
 * 비활성화 시 플러그인 종료 로그를 출력합니다.
 */
public final class minecraftPlugin extends JavaPlugin {

    /**
     * 플러그인이 활성화될 때 개인 창고 기능에 필요한 객체를 생성하고 등록합니다.
     */
    @Override
    public void onEnable() {
        StorageRepository storageRepository = new StorageRepository(this);
        StorageService storageService = new StorageServiceImpl(storageRepository);
        StorageCommand storageCommandExecutor = new StorageCommand(storageService);
        PluginCommand storageCommand = getCommand("storage");

        storageCommand.setExecutor(storageCommandExecutor);
        storageCommand.setTabCompleter(storageCommandExecutor);
        Bukkit.getPluginManager().registerEvents(new StorageListener(storageService), this);

        getLogger().info("창고 플러그인이 켜졌습니다.");
    }

    /**
     * 플러그인이 비활성화될 때 종료 로그를 출력합니다.
     */
    @Override
    public void onDisable() {
        getLogger().info("창고 플러그인이 꺼졌습니다.");
    }
}
