package org.WhiteBin.minecraftplugin.storage.policy;

import org.WhiteBin.minecraftplugin.storage.service.TestItemStack;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link StorageSortPolicy}의 창고 아이템 정렬 정책을 검증하는 테스트 클래스입니다.
 * <p>
 * 빈 슬롯 제거, 같은 아이템 병합, 창고 크기 유지가 의도한 규칙대로 동작하는지 확인합니다.
 */
class StorageSortPolicyTest {

    private final StorageSortPolicy storageSortPolicy = new StorageSortPolicy();

    /**
     * 빈 슬롯이 제거되고 아이템이 앞쪽 슬롯부터 배치되는지 검증합니다.
     */
    @Test
    void sortMovesItemsToFront() {
        // given
        ItemStack[] contents = new ItemStack[5];
        contents[2] = new TestItemStack("stone", 1);
        contents[4] = new TestItemStack("dirt", 1);

        // when
        ItemStack[] sortedContents = storageSortPolicy.sort(contents);

        // then
        assertEquals("stone", ((TestItemStack) sortedContents[0]).getItemKey());
        assertEquals("dirt", ((TestItemStack) sortedContents[1]).getItemKey());
        assertNull(sortedContents[2]);
        assertNull(sortedContents[3]);
        assertNull(sortedContents[4]);
    }

    /**
     * 같은 아이템이 최대 스택 크기까지 병합되는지 검증합니다.
     */
    @Test
    void sortMergesSimilarItemsUpToMaxStackSize() {
        // given
        ItemStack[] contents = new ItemStack[5];
        contents[0] = new TestItemStack("stone", 40);
        contents[2] = new TestItemStack("stone", 30);

        // when
        ItemStack[] sortedContents = storageSortPolicy.sort(contents);

        // then
        assertEquals("stone", ((TestItemStack) sortedContents[0]).getItemKey());
        assertEquals(64, sortedContents[0].getAmount());
        assertEquals("stone", ((TestItemStack) sortedContents[1]).getItemKey());
        assertEquals(6, sortedContents[1].getAmount());
        assertNull(sortedContents[2]);
    }

    /**
     * 정렬 전후 창고 배열 크기가 유지되는지 검증합니다.
     */
    @Test
    void sortKeepsStorageSize() {
        // given
        ItemStack[] contents = new ItemStack[9];
        contents[8] = new TestItemStack("oak_log", 3);

        // when
        ItemStack[] sortedContents = storageSortPolicy.sort(contents);

        // then
        assertEquals(9, sortedContents.length);
        assertEquals("oak_log", ((TestItemStack) sortedContents[0]).getItemKey());
        assertEquals(3, sortedContents[0].getAmount());
    }
}
