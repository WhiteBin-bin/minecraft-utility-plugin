package org.WhiteBin.minecraftplugin.storage.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link StorageSizePolicy}의 창고 크기 확장 정책을 검증하는 테스트 클래스입니다.
 * <p>
 * 기본 확장 단위, 최대 크기 제한, 확장 가능 여부가 의도한 규칙대로 동작하는지 확인합니다.
 */
class StorageSizePolicyTest {

    private final StorageSizePolicy storageSizePolicy = new StorageSizePolicy();

    /**
     * 현재 창고 크기가 기본 크기일 때 9칸 증가하는지 검증합니다.
     */
    @Test
    void expandIncreasesByNineSlots() {
        // given
        int currentSize = 27;

        // when
        int expandedSize = storageSizePolicy.expand(currentSize);

        // then
        assertEquals(36, expandedSize);
    }

    /**
     * 확장 결과가 최대 창고 크기인 54칸을 초과하지 않는지 검증합니다.
     */
    @Test
    void expandDoesNotExceedMaxSize() {
        // given
        int expandableSize = 45;
        int maxSize = 54;

        // when
        int expandedFromExpandableSize = storageSizePolicy.expand(expandableSize);
        int expandedFromMaxSize = storageSizePolicy.expand(maxSize);

        // then
        assertEquals(54, expandedFromExpandableSize);
        assertEquals(54, expandedFromMaxSize);
    }

    /**
     * 최대 창고 크기보다 작은 경우에만 확장 가능 상태로 판단하는지 검증합니다.
     */
    @Test
    void canExpandReturnsFalseAtMaxSize() {
        // given
        int expandableSize = 45;
        int maxSize = 54;

        // when
        boolean canExpandExpandableSize = storageSizePolicy.canExpand(expandableSize);
        boolean canExpandMaxSize = storageSizePolicy.canExpand(maxSize);

        // then
        assertTrue(canExpandExpandableSize);
        assertFalse(canExpandMaxSize);
    }
}
