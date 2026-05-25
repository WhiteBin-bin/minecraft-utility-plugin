package org.WhiteBin.minecraftplugin.storage.listener;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.inventory.StorageInventoryHolder;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * 개인 창고 인벤토리 이벤트를 처리하는 리스너입니다.
 * <p>
 * 플레이어가 개인 창고 인벤토리를 닫을 때 창고 소유자 UUID를 기준으로
 * 현재 인벤토리 내용을 저장합니다.
 */
@RequiredArgsConstructor
public class StorageListener implements Listener {

    private final StorageService storageService;

    /**
     * 인벤토리 닫기 이벤트 발생 시 개인 창고 여부를 확인하고 저장을 수행합니다.
     * <p>
     * 커스텀 창고 홀더가 연결된 인벤토리만 저장 대상으로 처리합니다.
     *
     * @param event 인벤토리 닫기 이벤트
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!storageService.isStorageInventory(event.getInventory())) {
            return;
        }

        StorageInventoryHolder holder = storageService.getStorageHolder(event.getInventory());

        if (holder == null) {
            return;
        }

        storageService.saveStorage(player, holder);
    }
}
