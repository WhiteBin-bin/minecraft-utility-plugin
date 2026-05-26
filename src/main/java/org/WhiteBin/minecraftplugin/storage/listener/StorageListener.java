package org.WhiteBin.minecraftplugin.storage.listener;

import lombok.RequiredArgsConstructor;
import org.WhiteBin.minecraftplugin.storage.inventory.StorageGuiFactory;
import org.WhiteBin.minecraftplugin.storage.inventory.StorageGuiHolder;
import org.WhiteBin.minecraftplugin.storage.inventory.StorageGuiType;
import org.WhiteBin.minecraftplugin.storage.inventory.StorageInventoryHolder;
import org.WhiteBin.minecraftplugin.storage.policy.StorageGuiLayoutPolicy;
import org.WhiteBin.minecraftplugin.storage.service.StorageService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
    private final StorageGuiFactory storageGuiFactory = new StorageGuiFactory();

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

    /**
     * 개인 창고 조회 GUI에서 아이템 이동을 차단합니다.
     *
     * @param event 인벤토리 클릭 이벤트
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof StorageGuiHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        switch (event.getRawSlot()) {
            case StorageGuiLayoutPolicy.PREVIOUS_PAGE_SLOT -> openGuiPage(player, holder, holder.getPage() - 1, holder.getFilter());
            case StorageGuiLayoutPolicy.NEXT_PAGE_SLOT -> openGuiPage(player, holder, holder.getPage() + 1, holder.getFilter());
            case StorageGuiLayoutPolicy.FILTER_SLOT -> openFilterGui(player, holder);
            default -> applyFilter(player, holder, event.getRawSlot());
        }
    }

    private void openGuiPage(Player player, StorageGuiHolder holder, int page, String filter) {
        switch (holder.getType()) {
            case LOGS -> player.openInventory(storageGuiFactory.createLogsInventory(
                    holder.getOwnerUuid(),
                    holder.getOwnerName(),
                    storageService.getStorageLogs(holder.getOwnerUuid()),
                    page,
                    filter
            ));
            case SHARES -> player.openInventory(storageGuiFactory.createSharesInventory(
                    holder.getOwnerUuid(),
                    holder.getOwnerName(),
                    storageService.getSharedUsers(holder.getOwnerUuid()),
                    page,
                    filter
            ));
            case SHARED -> player.openInventory(storageGuiFactory.createSharedStoragesInventory(
                    holder.getOwnerUuid(),
                    holder.getOwnerName(),
                    storageService.getSharedStorages(holder.getOwnerUuid()),
                    page,
                    filter
            ));
            case LOG_FILTER, SHARES_FILTER, SHARED_FILTER -> {
            }
        }
    }

    private void openFilterGui(Player player, StorageGuiHolder holder) {
        switch (holder.getType()) {
            case LOGS -> player.openInventory(storageGuiFactory.createLogFilterInventory(
                    holder.getOwnerUuid(),
                    holder.getOwnerName(),
                    holder.getFilter()
            ));
            case SHARES, SHARED -> player.openInventory(storageGuiFactory.createShareFilterInventory(
                    holder.getType(),
                    holder.getOwnerUuid(),
                    holder.getOwnerName(),
                    holder.getFilter()
            ));
            case LOG_FILTER, SHARES_FILTER, SHARED_FILTER -> {
            }
        }
    }

    private void applyFilter(Player player, StorageGuiHolder holder, int slot) {
        switch (holder.getType()) {
            case LOG_FILTER -> {
                String filter = storageGuiFactory.getLogFilterBySlot(slot);

                if (filter != null) {
                    player.openInventory(storageGuiFactory.createLogsInventory(
                            holder.getOwnerUuid(),
                            holder.getOwnerName(),
                            storageService.getStorageLogs(holder.getOwnerUuid()),
                            0,
                            filter
                    ));
                }
            }
            case SHARES_FILTER -> {
                String filter = storageGuiFactory.getShareFilterBySlot(slot);

                if (filter != null) {
                    player.openInventory(storageGuiFactory.createSharesInventory(
                            holder.getOwnerUuid(),
                            holder.getOwnerName(),
                            storageService.getSharedUsers(holder.getOwnerUuid()),
                            0,
                            filter
                    ));
                }
            }
            case SHARED_FILTER -> {
                String filter = storageGuiFactory.getShareFilterBySlot(slot);

                if (filter != null) {
                    player.openInventory(storageGuiFactory.createSharedStoragesInventory(
                            holder.getOwnerUuid(),
                            holder.getOwnerName(),
                            storageService.getSharedStorages(holder.getOwnerUuid()),
                            0,
                            filter
                    ));
                }
            }
            case LOGS, SHARES, SHARED -> {
            }
        }
    }
}
