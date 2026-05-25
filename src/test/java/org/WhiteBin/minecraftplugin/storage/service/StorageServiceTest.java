package org.WhiteBin.minecraftplugin.storage.service;

import org.WhiteBin.minecraftplugin.storage.repository.StorageRepository;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link StorageService}의 개인 창고 확장 흐름을 검증하는 테스트 클래스입니다.
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
        StorageService storageService = new StorageService(storageRepository);

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
        StorageService storageService = new StorageService(storageRepository);

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
        StorageService expandableStorageService = new StorageService(expandableRepository);
        StorageService maxStorageService = new StorageService(maxRepository);

        // when
        boolean canExpandCurrentSize = expandableStorageService.canExpand(ownerUuid);
        boolean canExpandMaxSize = maxStorageService.canExpand(ownerUuid);

        // then
        assertTrue(canExpandCurrentSize);
        assertFalse(canExpandMaxSize);
    }

    /**
     * {@link StorageService} 테스트에서 파일 시스템 접근 없이 창고 크기 저장을 검증하기 위한 저장소입니다.
     * <p>
     * 창고 크기 조회와 저장만 메모리 값으로 대체합니다.
     */
    private static class FakeStorageRepository extends StorageRepository {

        private final int loadedSize;
        private UUID savedUuid;
        private int savedSize;
        private int saveCount;

        /**
         * 테스트에서 불러올 창고 크기를 지정하여 저장소를 생성합니다.
         *
         * @param loadedSize 조회 시 반환할 창고 크기
         */
        private FakeStorageRepository(int loadedSize) {
            super(null);
            this.loadedSize = loadedSize;
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
         * 창고 아이템 저장은 이 테스트의 검증 대상이 아니므로 수행하지 않습니다.
         *
         * @param uuid 창고 소유자 UUID
         * @param inventory 저장할 창고 인벤토리
         */
        @Override
        public void save(UUID uuid, Inventory inventory) {
        }
    }
}
