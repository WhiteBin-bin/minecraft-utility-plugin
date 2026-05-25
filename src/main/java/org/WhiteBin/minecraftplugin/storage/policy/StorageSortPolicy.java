package org.WhiteBin.minecraftplugin.storage.policy;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 개인 창고 아이템 정렬 정책을 정의하는 클래스입니다.
 * <p>
 * 빈 슬롯을 제거하고 같은 아이템을 가능한 범위에서 합친 뒤,
 * 창고 앞쪽 슬롯부터 아이템이 배치되도록 정렬합니다.
 */
public class StorageSortPolicy {

    /**
     * 전달된 창고 아이템 배열을 정렬한 새 배열을 반환합니다.
     * <p>
     * 원본 배열의 크기는 유지하며, 빈 슬롯은 뒤쪽으로 이동합니다.
     *
     * @param contents 정렬할 창고 아이템 배열
     * @return 정렬된 창고 아이템 배열
     */
    public ItemStack[] sort(ItemStack[] contents) {
        List<ItemStack> sortedItems = mergeSimilarItems(contents);
        ItemStack[] sortedContents = new ItemStack[contents.length];

        for (int i = 0; i < sortedItems.size() && i < sortedContents.length; i++) {
            sortedContents[i] = sortedItems.get(i);
        }

        return sortedContents;
    }

    /**
     * 빈 아이템을 제외하고 같은 아이템을 최대 스택 크기까지 합칩니다.
     *
     * @param contents 병합할 창고 아이템 배열
     * @return 병합된 아이템 목록
     */
    private List<ItemStack> mergeSimilarItems(ItemStack[] contents) {
        List<ItemStack> mergedItems = new ArrayList<>();

        for (ItemStack item : contents) {
            if (isEmpty(item)) {
                continue;
            }

            mergeItem(mergedItems, item.clone());
        }

        return mergedItems;
    }

    /**
     * 대상 아이템을 기존 병합 목록에 합치거나 새 스택으로 추가합니다.
     *
     * @param mergedItems 병합된 아이템 목록
     * @param item 병합할 아이템
     */
    private void mergeItem(List<ItemStack> mergedItems, ItemStack item) {
        int remainingAmount = item.getAmount();

        for (ItemStack mergedItem : mergedItems) {
            if (!mergedItem.isSimilar(item)) {
                continue;
            }

            int maxStackSize = mergedItem.getMaxStackSize();
            int availableAmount = maxStackSize - mergedItem.getAmount();

            if (availableAmount <= 0) {
                continue;
            }

            int mergeAmount = Math.min(availableAmount, remainingAmount);
            mergedItem.setAmount(mergedItem.getAmount() + mergeAmount);
            remainingAmount -= mergeAmount;

            if (remainingAmount <= 0) {
                return;
            }
        }

        addRemainingItemStacks(mergedItems, item, remainingAmount);
    }

    /**
     * 남은 아이템 수량을 최대 스택 크기에 맞춰 새 스택으로 추가합니다.
     *
     * @param mergedItems 병합된 아이템 목록
     * @param item 추가할 아이템 기준 스택
     * @param remainingAmount 추가해야 할 남은 수량
     */
    private void addRemainingItemStacks(List<ItemStack> mergedItems, ItemStack item, int remainingAmount) {
        int maxStackSize = item.getMaxStackSize();

        while (remainingAmount > 0) {
            int stackAmount = Math.min(maxStackSize, remainingAmount);
            ItemStack stack = item.clone();
            stack.setAmount(stackAmount);
            mergedItems.add(stack);
            remainingAmount -= stackAmount;
        }
    }

    /**
     * 전달된 아이템이 빈 슬롯으로 취급되는지 확인합니다.
     *
     * @param item 확인할 아이템
     * @return 아이템이 없거나 공기 아이템이면 {@code true}
     */
    private boolean isEmpty(ItemStack item) {
        return item == null || item.isEmpty() || item.getAmount() <= 0;
    }
}
