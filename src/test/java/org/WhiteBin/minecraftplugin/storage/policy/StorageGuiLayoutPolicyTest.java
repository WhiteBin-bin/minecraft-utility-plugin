package org.WhiteBin.minecraftplugin.storage.policy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link StorageGuiLayoutPolicy}의 GUI 크기와 표시 범위 계산을 검증합니다.
 */
class StorageGuiLayoutPolicyTest {

    private final StorageGuiLayoutPolicy storageGuiLayoutPolicy = new StorageGuiLayoutPolicy();

    /**
     * 항목이 없을 때 최소 GUI 크기인 9칸을 반환하는지 검증합니다.
     */
    @Test
    void inventorySizeReturnsMinimumSizeWhenEmpty() {
        // given
        int itemCount = 0;

        // when
        int inventorySize = storageGuiLayoutPolicy.inventorySize(itemCount);

        // then
        assertEquals(9, inventorySize);
    }

    /**
     * 항목 수에 따라 9칸 단위로 GUI 크기를 반환하는지 검증합니다.
     */
    @Test
    void inventorySizeReturnsNineSlotUnitSize() {
        // given
        int itemCount = 10;

        // when
        int inventorySize = storageGuiLayoutPolicy.inventorySize(itemCount);

        // then
        assertEquals(18, inventorySize);
    }

    /**
     * 항목 수가 많을 때 최대 GUI 크기인 54칸을 초과하지 않는지 검증합니다.
     */
    @Test
    void inventorySizeDoesNotExceedMaxSize() {
        // given
        int itemCount = 100;

        // when
        int inventorySize = storageGuiLayoutPolicy.inventorySize(itemCount);

        // then
        assertEquals(54, inventorySize);
    }

    /**
     * 항목 수가 최대 GUI 크기보다 많을 때 최신 54개를 표시하는 시작 인덱스를 반환하는지 검증합니다.
     */
    @Test
    void visibleStartIndexReturnsLatestItemStartIndex() {
        // given
        int itemCount = 60;

        // when
        int visibleStartIndex = storageGuiLayoutPolicy.visibleStartIndex(itemCount);

        // then
        assertEquals(6, visibleStartIndex);
    }

    /**
     * GUI에 표시할 항목 수가 최대 GUI 크기를 초과하지 않는지 검증합니다.
     */
    @Test
    void visibleItemCountDoesNotExceedMaxSize() {
        // given
        int itemCount = 60;

        // when
        int visibleItemCount = storageGuiLayoutPolicy.visibleItemCount(itemCount);

        // then
        assertEquals(54, visibleItemCount);
    }

    /**
     * 항목 수를 45칸 콘텐츠 영역 기준으로 나누어 전체 페이지 수를 반환하는지 검증합니다.
     */
    @Test
    void totalPagesUsesContentSize() {
        // given
        int itemCount = 46;

        // when
        int totalPages = storageGuiLayoutPolicy.totalPages(itemCount);

        // then
        assertEquals(2, totalPages);
    }

    /**
     * 현재 페이지의 시작 인덱스를 반환하는지 검증합니다.
     */
    @Test
    void pageStartIndexUsesPageAndContentSize() {
        // given
        int page = 1;

        // when
        int pageStartIndex = storageGuiLayoutPolicy.pageStartIndex(page);

        // then
        assertEquals(45, pageStartIndex);
    }

    /**
     * 현재 페이지의 종료 인덱스를 전체 항목 수 범위 안으로 반환하는지 검증합니다.
     */
    @Test
    void pageEndIndexDoesNotExceedItemCount() {
        // given
        int itemCount = 46;
        int page = 1;

        // when
        int pageEndIndex = storageGuiLayoutPolicy.pageEndIndex(itemCount, page);

        // then
        assertEquals(46, pageEndIndex);
    }

    /**
     * 요청 페이지가 전체 페이지 범위를 벗어나면 마지막 페이지로 보정하는지 검증합니다.
     */
    @Test
    void normalizePageDoesNotExceedLastPage() {
        // given
        int page = 10;
        int itemCount = 46;

        // when
        int normalizedPage = storageGuiLayoutPolicy.normalizePage(page, itemCount);

        // then
        assertEquals(1, normalizedPage);
    }
}
