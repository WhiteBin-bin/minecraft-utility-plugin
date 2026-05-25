package org.WhiteBin.minecraftplugin.storage.service;

import org.WhiteBin.minecraftplugin.storage.repository.StorageRepository;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link StorageService}의 개인 창고 크기 변경 흐름을 검증하는 테스트 클래스입니다.
 * <p>
 * Bukkit 서버가 필요한 인벤토리 열기 동작은 제외하고,
 * 저장소에 기록되는 창고 크기 변경 로직을 중심으로 확인합니다.
 */
class StorageServiceTest {

    /**
     * 확장 가능한 창고 크기일 때 9칸 증가한 크기가 저장되는지 검증합니다.
     */
    @Test
    void expandStorageSavesExpandedSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository storageRepository = new FakeStorageRepository(27);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        int expandedSize = storageService.expandStorage(ownerUuid);

        // then
        assertEquals(36, expandedSize);
        assertEquals(36, storageRepository.savedSize);
        assertEquals(ownerUuid, storageRepository.savedUuid);
    }

    /**
     * 창고가 이미 최대 크기일 때 크기를 저장하지 않고 현재 크기를 반환하는지 검증합니다.
     */
    @Test
    void expandStorageDoesNotSaveWhenAlreadyMaxSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository storageRepository = new FakeStorageRepository(54);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        int expandedSize = storageService.expandStorage(ownerUuid);

        // then
        assertEquals(54, expandedSize);
        assertEquals(0, storageRepository.saveCount);
    }

    /**
     * 저장된 창고 크기를 기준으로 확장 가능 여부를 반환하는지 검증합니다.
     */
    @Test
    void canExpandUsesSavedStorageSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository expandableRepository = new FakeStorageRepository(45);
        FakeStorageRepository maxRepository = new FakeStorageRepository(54);
        StorageService expandableStorageService = new StorageServiceImpl(expandableRepository);
        StorageService maxStorageService = new StorageServiceImpl(maxRepository);

        // when
        boolean canExpandCurrentSize = expandableStorageService.canExpand(ownerUuid);
        boolean canExpandMaxSize = maxStorageService.canExpand(ownerUuid);

        // then
        assertTrue(canExpandCurrentSize);
        assertFalse(canExpandMaxSize);
    }

    /**
     * 축소 가능한 창고 크기일 때 9칸 감소한 크기가 저장되는지 검증합니다.
     */
    @Test
    void shrinkStorageSavesShrinkSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository storageRepository = new FakeStorageRepository(27);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        int shrinkSize = storageService.shrinkStorage(ownerUuid);

        // then
        assertEquals(18, shrinkSize);
        assertEquals(18, storageRepository.savedSize);
        assertEquals(ownerUuid, storageRepository.savedUuid);
    }

    /**
     * 창고가 이미 최소 크기일 때 크기를 저장하지 않고 현재 크기를 반환하는지 검증합니다.
     */
    @Test
    void shrinkStorageDoesNotSaveWhenAlreadyMinSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository storageRepository = new FakeStorageRepository(9);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        int shrinkSize = storageService.shrinkStorage(ownerUuid);

        // then
        assertEquals(9, shrinkSize);
        assertEquals(0, storageRepository.saveCount);
    }

    /**
     * 축소될 슬롯 범위에 아이템이 있을 때 크기를 저장하지 않고 현재 크기를 반환하는지 검증합니다.
     */
    @Test
    void shrinkStorageDoesNotSaveWhenShrinkRangeHasItems() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository storageRepository = new FakeStorageRepository(27, true);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        int shrinkSize = storageService.shrinkStorage(ownerUuid);

        // then
        assertEquals(27, shrinkSize);
        assertEquals(0, storageRepository.saveCount);
    }

    /**
     * 저장된 창고 크기를 기준으로 축소 가능 여부를 반환하는지 검증합니다.
     */
    @Test
    void canShrinkUsesSavedStorageSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository shrinkableRepository = new FakeStorageRepository(18);
        FakeStorageRepository minRepository = new FakeStorageRepository(9);
        StorageService shrinkableStorageService = new StorageServiceImpl(shrinkableRepository);
        StorageService minStorageService = new StorageServiceImpl(minRepository);

        // when
        boolean canShrinkCurrentSize = shrinkableStorageService.canShrink(ownerUuid);
        boolean canShrinkMinSize = minStorageService.canShrink(ownerUuid);

        // then
        assertTrue(canShrinkCurrentSize);
        assertFalse(canShrinkMinSize);
    }

    /**
     * 축소될 슬롯 범위의 아이템 존재 여부를 저장소 기준으로 반환하는지 검증합니다.
     */
    @Test
    void hasItemsInShrinkRangeUsesRepositoryResult() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        FakeStorageRepository storageRepository = new FakeStorageRepository(27, true);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        boolean hasItemsInShrinkRange = storageService.hasItemsInShrinkRange(ownerUuid);

        // then
        assertTrue(hasItemsInShrinkRange);
        assertEquals(18, storageRepository.checkedStartSlot);
        assertEquals(27, storageRepository.checkedEndSlot);
    }

    /**
     * 창고 정렬 결과가 기존 창고 크기와 함께 저장되는지 검증합니다.
     */
    @Test
    void sortStorageSavesSortedItemsWithStorageSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        ItemStack[] loadedItems = new ItemStack[27];
        loadedItems[5] = new TestItemStack("stone", 40);
        loadedItems[8] = new TestItemStack("stone", 30);
        FakeStorageRepository storageRepository = new FakeStorageRepository(27, false, loadedItems);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        storageService.sortStorage(ownerUuid);

        // then
        assertEquals(ownerUuid, storageRepository.savedItemsUuid);
        assertEquals(27, storageRepository.savedItemsSize);
        assertEquals("stone", ((TestItemStack) storageRepository.savedItems[0]).getItemKey());
        assertEquals(64, storageRepository.savedItems[0].getAmount());
        assertEquals("stone", ((TestItemStack) storageRepository.savedItems[1]).getItemKey());
        assertEquals(6, storageRepository.savedItems[1].getAmount());
    }

    /**
     * 창고 초기화 결과가 기존 창고 크기와 빈 아이템 배열로 저장되는지 검증합니다.
     */
    @Test
    void clearStorageSavesEmptyItemsWithStorageSize() {
        // given
        UUID ownerUuid = UUID.randomUUID();
        ItemStack[] loadedItems = new ItemStack[27];
        loadedItems[0] = new TestItemStack("stone", 10);
        FakeStorageRepository storageRepository = new FakeStorageRepository(27, false, loadedItems);
        StorageService storageService = new StorageServiceImpl(storageRepository);

        // when
        storageService.clearStorage(ownerUuid);

        // then
        assertEquals(ownerUuid, storageRepository.savedItemsUuid);
        assertEquals(27, storageRepository.savedItemsSize);
        assertEquals(27, storageRepository.savedItems.length);
        assertTrue(isEmpty(storageRepository.savedItems));
    }

    /**
     * {@link StorageService} 테스트에서 파일 시스템 접근 없이 창고 크기 저장을 검증하기 위한 저장소입니다.
     * <p>
     * 창고 크기 조회와 저장만 메모리 값으로 대체합니다.
     */
    private static class FakeStorageRepository extends StorageRepository {

        private final int loadedSize;
        private final boolean hasItemsInRange;
        private final ItemStack[] loadedItems;
        private UUID savedUuid;
        private int savedSize;
        private int saveCount;
        private int checkedStartSlot;
        private int checkedEndSlot;
        private UUID savedItemsUuid;
        private ItemStack[] savedItems;
        private int savedItemsSize;

        /**
         * 테스트에서 불러올 창고 크기를 지정하여 저장소를 생성합니다.
         *
         * @param loadedSize 조회 시 반환할 창고 크기
         */
        private FakeStorageRepository(int loadedSize) {
            super(null);
            this.loadedSize = loadedSize;
            this.hasItemsInRange = false;
            this.loadedItems = new ItemStack[loadedSize];
        }

        /**
         * 테스트에서 불러올 창고 크기와 아이템 존재 여부를 지정하여 저장소를 생성합니다.
         *
         * @param loadedSize 조회 시 반환할 창고 크기
         * @param hasItemsInRange 지정한 슬롯 범위의 아이템 존재 여부
         */
        private FakeStorageRepository(int loadedSize, boolean hasItemsInRange) {
            super(null);
            this.loadedSize = loadedSize;
            this.hasItemsInRange = hasItemsInRange;
            this.loadedItems = new ItemStack[loadedSize];
        }

        /**
         * 테스트에서 불러올 창고 크기, 아이템 존재 여부, 아이템 목록을 지정하여 저장소를 생성합니다.
         *
         * @param loadedSize 조회 시 반환할 창고 크기
         * @param hasItemsInRange 지정한 슬롯 범위의 아이템 존재 여부
         * @param loadedItems 조회 시 반환할 창고 아이템 배열
         */
        private FakeStorageRepository(int loadedSize, boolean hasItemsInRange, ItemStack[] loadedItems) {
            super(null);
            this.loadedSize = loadedSize;
            this.hasItemsInRange = hasItemsInRange;
            this.loadedItems = loadedItems;
        }

        /**
         * 테스트에서 지정한 창고 크기를 반환합니다.
         *
         * @param uuid 창고 소유자 UUID
         * @param defaultSize 기본 창고 크기
         * @return 테스트용 창고 크기
         */
        @Override
        public int loadSize(UUID uuid, int defaultSize) {
            return loadedSize;
        }

        /**
         * 저장 요청으로 전달된 창고 소유자 UUID와 창고 크기를 메모리에 기록합니다.
         *
         * @param uuid 창고 소유자 UUID
         * @param size 저장할 창고 크기
         */
        @Override
        public void saveSize(UUID uuid, int size) {
            savedUuid = uuid;
            savedSize = size;
            saveCount++;
        }

        /**
         * 저장소에 지정된 아이템 존재 여부를 반환하고 검사한 슬롯 범위를 기록합니다.
         *
         * @param uuid 창고 소유자 UUID
         * @param startSlot 확인을 시작할 슬롯 번호
         * @param endSlot 확인을 끝낼 슬롯 번호
         * @return 테스트용 아이템 존재 여부
         */
        @Override
        public boolean hasItemsInRange(UUID uuid, int startSlot, int endSlot) {
            checkedStartSlot = startSlot;
            checkedEndSlot = endSlot;
            return hasItemsInRange;
        }

        /**
         * 테스트에서 지정한 창고 아이템 배열을 반환합니다.
         *
         * @param uuid 창고 소유자 UUID
         * @param size 불러올 창고 크기
         * @return 테스트용 창고 아이템 배열
         */
        @Override
        public ItemStack[] loadItems(UUID uuid, int size) {
            return loadedItems;
        }

        /**
         * 저장 요청으로 전달된 창고 소유자 UUID, 아이템 배열, 창고 크기를 메모리에 기록합니다.
         *
         * @param uuid 창고 소유자 UUID
         * @param contents 저장할 창고 아이템 배열
         * @param size 저장할 창고 크기
         */
        @Override
        public void saveItems(UUID uuid, ItemStack[] contents, int size) {
            savedItemsUuid = uuid;
            savedItems = contents;
            savedItemsSize = size;
        }

        /**
         * 창고 아이템 저장은 이 테스트의 검증 대상이 아니므로 수행하지 않습니다.
         *
         * @param uuid 창고 소유자 UUID
         * @param inventory 저장할 창고 인벤토리
         */
        @Override
        public void save(UUID uuid, Inventory inventory) {
        }
    }

    /**
     * 전달된 아이템 배열이 모두 빈 슬롯인지 확인합니다.
     *
     * @param contents 확인할 아이템 배열
     * @return 모든 슬롯이 비어 있으면 {@code true}
     */
    private boolean isEmpty(ItemStack[] contents) {
        for (ItemStack content : contents) {
            if (content != null) {
                return false;
            }
        }

        return true;
    }
}
