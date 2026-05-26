package org.WhiteBin.minecraftplugin.storage.inventory;

import net.kyori.adventure.text.Component;
import org.WhiteBin.minecraftplugin.storage.model.StorageLogInfo;
import org.WhiteBin.minecraftplugin.storage.model.StorageLogType;
import org.WhiteBin.minecraftplugin.storage.model.StorageShareInfo;
import org.WhiteBin.minecraftplugin.storage.policy.StorageGuiLayoutPolicy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 개인 창고 로그와 공유 목록을 표시하는 GUI 인벤토리를 생성합니다.
 */
public class StorageGuiFactory {

    public static final String ALL_FILTER = "ALL";
    private static final String ONLINE_FILTER = "ONLINE";
    private static final String OFFLINE_FILTER = "OFFLINE";

    private final StorageGuiLayoutPolicy storageGuiLayoutPolicy = new StorageGuiLayoutPolicy();

    /**
     * 창고 로그 목록 GUI를 생성합니다.
     *
     * @param ownerUuid 로그를 조회할 창고 소유자 UUID
     * @param ownerName 로그를 조회할 창고 소유자 이름
     * @param logs 창고 로그 목록
     * @param page 현재 페이지
     * @param filter 로그 타입 필터
     * @return 창고 로그 GUI 인벤토리
     */
    public Inventory createLogsInventory(UUID ownerUuid, String ownerName, List<StorageLogInfo> logs, int page, String filter) {
        List<StorageLogInfo> filteredLogs = filterLogs(logs, filter);
        int currentPage = storageGuiLayoutPolicy.normalizePage(page, filteredLogs.size());
        Inventory inventory = createInventory(StorageGuiType.LOGS, ownerUuid, ownerName, currentPage, filter, ownerName + "님의 창고 로그");

        if (filteredLogs.isEmpty()) {
            inventory.setItem(0, createItem(Material.BARRIER, "창고 로그가 없습니다.", List.of(ownerName + "님의 창고 로그가 없습니다.")));
        } else {
            List<StorageLogInfo> pageLogs = filteredLogs.subList(
                    storageGuiLayoutPolicy.pageStartIndex(currentPage),
                    storageGuiLayoutPolicy.pageEndIndex(filteredLogs.size(), currentPage)
            );

            for (int index = 0; index < pageLogs.size(); index++) {
                inventory.setItem(index, createLogItem(pageLogs.get(index)));
            }
        }

        setNavigationItems(inventory, currentPage, storageGuiLayoutPolicy.totalPages(filteredLogs.size()), filter);
        return inventory;
    }

    /**
     * 내가 공유 중인 플레이어 목록 GUI를 생성합니다.
     *
     * @param ownerUuid 창고 소유자 UUID
     * @param ownerName 창고 소유자 이름
     * @param shares 공유받은 플레이어 목록
     * @param page 현재 페이지
     * @return 공유 목록 GUI 인벤토리
     */
    public Inventory createSharesInventory(UUID ownerUuid, String ownerName, List<StorageShareInfo> shares, int page, String filter) {
        List<StorageShareInfo> filteredShares = filterShares(shares, filter);
        int currentPage = storageGuiLayoutPolicy.normalizePage(page, filteredShares.size());
        Inventory inventory = createInventory(StorageGuiType.SHARES, ownerUuid, ownerName, currentPage, filter, "내가 공유 중인 창고");

        if (filteredShares.isEmpty()) {
            inventory.setItem(0, createItem(Material.BARRIER, "공유 중인 유저가 없습니다.", List.of("내 창고를 공유받은 유저가 없습니다.")));
        } else {
            List<StorageShareInfo> pageShares = filteredShares.subList(
                    storageGuiLayoutPolicy.pageStartIndex(currentPage),
                    storageGuiLayoutPolicy.pageEndIndex(filteredShares.size(), currentPage)
            );

            for (int index = 0; index < pageShares.size(); index++) {
                StorageShareInfo share = pageShares.get(index);
                inventory.setItem(index, createItem(Material.PLAYER_HEAD, share.name(), List.of("공유받은 유저", share.uuid().toString())));
            }
        }

        setNavigationItems(inventory, currentPage, storageGuiLayoutPolicy.totalPages(filteredShares.size()), filter);
        return inventory;
    }

    /**
     * 내가 공유받은 창고 목록 GUI를 생성합니다.
     *
     * @param ownerUuid 공유받은 플레이어 UUID
     * @param ownerName 공유받은 플레이어 이름
     * @param sharedStorages 공유해준 창고 소유자 목록
     * @param page 현재 페이지
     * @return 공유받은 창고 목록 GUI 인벤토리
     */
    public Inventory createSharedStoragesInventory(UUID ownerUuid, String ownerName, List<StorageShareInfo> sharedStorages, int page, String filter) {
        List<StorageShareInfo> filteredSharedStorages = filterShares(sharedStorages, filter);
        int currentPage = storageGuiLayoutPolicy.normalizePage(page, filteredSharedStorages.size());
        Inventory inventory = createInventory(StorageGuiType.SHARED, ownerUuid, ownerName, currentPage, filter, "내가 공유받은 창고");

        if (filteredSharedStorages.isEmpty()) {
            inventory.setItem(0, createItem(Material.BARRIER, "공유받은 창고가 없습니다.", List.of("나에게 공유된 창고가 없습니다.")));
        } else {
            List<StorageShareInfo> pageSharedStorages = filteredSharedStorages.subList(
                    storageGuiLayoutPolicy.pageStartIndex(currentPage),
                    storageGuiLayoutPolicy.pageEndIndex(filteredSharedStorages.size(), currentPage)
            );

            for (int index = 0; index < pageSharedStorages.size(); index++) {
                StorageShareInfo sharedStorage = pageSharedStorages.get(index);
                inventory.setItem(index, createItem(Material.CHEST, sharedStorage.name() + "의 창고", List.of("창고 소유자", sharedStorage.uuid().toString())));
            }
        }

        setNavigationItems(inventory, currentPage, storageGuiLayoutPolicy.totalPages(filteredSharedStorages.size()), filter);
        return inventory;
    }

    /**
     * 창고 로그 필터 선택 GUI를 생성합니다.
     *
     * @param ownerUuid 로그를 조회할 창고 소유자 UUID
     * @param ownerName 로그를 조회할 창고 소유자 이름
     * @param currentFilter 현재 필터
     * @return 창고 로그 필터 선택 GUI 인벤토리
     */
    public Inventory createLogFilterInventory(UUID ownerUuid, String ownerName, String currentFilter) {
        Inventory inventory = createInventory(StorageGuiType.LOG_FILTER, ownerUuid, ownerName, 0, currentFilter, "창고 로그 필터 선택");
        List<String> filters = logFilters();

        for (int index = 0; index < filters.size(); index++) {
            String filter = filters.get(index);
            inventory.setItem(StorageGuiLayoutPolicy.FILTER_OPTION_START_SLOT + index, createFilterItem(filter, currentFilter));
        }

        return inventory;
    }

    /**
     * 공유 목록 필터 선택 GUI를 생성합니다.
     *
     * @param type 필터를 적용할 공유 목록 GUI 종류
     * @param ownerUuid 공유 목록 기준 플레이어 UUID
     * @param ownerName 공유 목록 기준 플레이어 이름
     * @param currentFilter 현재 필터
     * @return 공유 목록 필터 선택 GUI 인벤토리
     */
    public Inventory createShareFilterInventory(StorageGuiType type, UUID ownerUuid, String ownerName, String currentFilter) {
        StorageGuiType filterType = type == StorageGuiType.SHARES ? StorageGuiType.SHARES_FILTER : StorageGuiType.SHARED_FILTER;
        Inventory inventory = createInventory(filterType, ownerUuid, ownerName, 0, currentFilter, "공유 목록 필터 선택");
        List<String> filters = shareFilters();

        for (int index = 0; index < filters.size(); index++) {
            String filter = filters.get(index);
            inventory.setItem(StorageGuiLayoutPolicy.FILTER_OPTION_START_SLOT + index, createFilterItem(filter, currentFilter));
        }

        return inventory;
    }

    /**
     * 선택한 슬롯에 해당하는 로그 필터 값을 반환합니다.
     *
     * @param slot 클릭한 슬롯
     * @return 선택한 로그 필터 값 또는 {@code null}
     */
    public String getLogFilterBySlot(int slot) {
        return getFilterBySlot(logFilters(), slot);
    }

    /**
     * 선택한 슬롯에 해당하는 공유 목록 필터 값을 반환합니다.
     *
     * @param slot 클릭한 슬롯
     * @return 선택한 공유 목록 필터 값 또는 {@code null}
     */
    public String getShareFilterBySlot(int slot) {
        return getFilterBySlot(shareFilters(), slot);
    }

    private Inventory createInventory(StorageGuiType type, UUID ownerUuid, String ownerName, int page, String filter, String title) {
        StorageGuiHolder holder = new StorageGuiHolder(type, ownerUuid, ownerName, page, filter);
        Inventory inventory = Bukkit.createInventory(holder, StorageGuiLayoutPolicy.MAX_GUI_SIZE, Component.text(title));
        holder.setInventory(inventory);
        return inventory;
    }

    private List<StorageLogInfo> filterLogs(List<StorageLogInfo> logs, String filter) {
        if (filter == null || filter.equals(ALL_FILTER)) {
            return logs;
        }

        return logs.stream()
                .filter(log -> log.type().name().equals(filter))
                .toList();
    }

    private List<StorageShareInfo> filterShares(List<StorageShareInfo> shares, String filter) {
        if (filter == null || filter.equals(ALL_FILTER)) {
            return shares;
        }

        return shares.stream()
                .filter(share -> shareMatchesFilter(share, filter))
                .toList();
    }

    private boolean shareMatchesFilter(StorageShareInfo share, String filter) {
        boolean online = Bukkit.getPlayer(share.uuid()) != null;
        return (filter.equals(ONLINE_FILTER) && online) || (filter.equals(OFFLINE_FILTER) && !online);
    }

    private void setNavigationItems(Inventory inventory, int page, int totalPages, String filter) {
        if (page > 0) {
            inventory.setItem(StorageGuiLayoutPolicy.PREVIOUS_PAGE_SLOT, createItem(Material.ARROW, "이전 페이지", List.of((page) + " / " + totalPages)));
        }

        inventory.setItem(StorageGuiLayoutPolicy.FILTER_SLOT, createItem(Material.HOPPER, "필터 선택: " + filterName(filter), List.of("클릭하면 필터 목록을 엽니다.", "페이지 " + (page + 1) + " / " + totalPages)));

        if (page < totalPages - 1) {
            inventory.setItem(StorageGuiLayoutPolicy.NEXT_PAGE_SLOT, createItem(Material.ARROW, "다음 페이지", List.of((page + 2) + " / " + totalPages)));
        }
    }

    private String filterName(String filter) {
        if (filter == null || filter.equals(ALL_FILTER)) {
            return "전체";
        }

        if (filter.equals(ONLINE_FILTER)) {
            return "접속 중";
        }

        if (filter.equals(OFFLINE_FILTER)) {
            return "미접속";
        }

        return filter;
    }

    private List<String> logFilters() {
        List<String> filters = new ArrayList<>();
        filters.add(ALL_FILTER);
        filters.addAll(Arrays.stream(StorageLogType.values())
                .map(Enum::name)
                .toList());
        return filters;
    }

    private List<String> shareFilters() {
        return List.of(ALL_FILTER, ONLINE_FILTER, OFFLINE_FILTER);
    }

    private ItemStack createFilterItem(String filter, String currentFilter) {
        Material material = filter.equals(currentFilter) ? Material.LIME_DYE : Material.GRAY_DYE;
        return createItem(material, filterName(filter), List.of("클릭하면 이 필터를 적용합니다.", "필터 값: " + filter));
    }

    private String getFilterBySlot(List<String> filters, int slot) {
        int index = slot - StorageGuiLayoutPolicy.FILTER_OPTION_START_SLOT;

        if (index < 0 || index >= filters.size()) {
            return null;
        }

        return filters.get(index);
    }

    private ItemStack createLogItem(StorageLogInfo log) {
        List<String> lore = new ArrayList<>();
        lore.add("작업 시각: " + log.createdAt());
        lore.add("실행자: " + log.actorName());
        lore.add("창고 소유자: " + log.ownerName());
        lore.add("상세: " + log.detail());
        return createItem(Material.PAPER, log.type().name(), lore);
    }

    private ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(displayName));
        itemMeta.lore(lore.stream()
                .map(Component::text)
                .toList());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
