package org.WhiteBin.minecraftplugin.storage.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * 창고 정렬 테스트에서 Bukkit 서버 없이 사용할 수 있는 테스트용 아이템 스택입니다.
 * <p>
 * 아이템 종류 식별자와 수량만으로 유사성, 복제, 스택 크기 계산을 검증할 수 있게 합니다.
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class TestItemStack extends ItemStack {

    @Getter
    private final String itemKey;
    private int amount;

    /**
     * 아이템 수량을 반환합니다.
     *
     * @return 아이템 수량
     */
    @Override
    public int getAmount() {
        return amount;
    }

    /**
     * 아이템 수량을 설정합니다.
     *
     * @param amount 설정할 아이템 수량
     */
    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * 테스트용 최대 스택 크기를 반환합니다.
     *
     * @return 최대 스택 크기
     */
    @Override
    public int getMaxStackSize() {
        return 64;
    }

    /**
     * 테스트용 아이템 종류 식별자가 같은지 확인합니다.
     *
     * @param stack 비교할 아이템 스택
     * @return 같은 테스트용 아이템 종류면 {@code true}
     */
    @Override
    public boolean isSimilar(ItemStack stack) {
        return stack instanceof TestItemStack testItemStack && itemKey.equals(testItemStack.itemKey);
    }

    /**
     * 현재 테스트용 아이템 스택을 복제합니다.
     *
     * @return 복제된 테스트용 아이템 스택
     */
    @Override
    public ItemStack clone() {
        return new TestItemStack(itemKey, amount);
    }

    /**
     * 현재 테스트용 아이템 스택이 비어 있는지 확인합니다.
     *
     * @return 아이템 수량이 0 이하이면 {@code true}
     */
    @Override
    public boolean isEmpty() {
        return amount <= 0;
    }
}
