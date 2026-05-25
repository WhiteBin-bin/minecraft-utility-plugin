package org.WhiteBin.minecraftplugin.storage.service;

/**
 * 개인 창고 크기 확장 정책을 정의하는 클래스입니다.
 * <p>
 * 기본 창고 크기, 최대 창고 크기, 확장 단위를 관리하며
 * 현재 창고 크기를 기준으로 확장 가능 여부와 확장 후 크기를 계산합니다.
 */
public class StorageSizePolicy {

    /**
     * 새 플레이어에게 적용되는 기본 창고 크기입니다.
     */
    public static final int DEFAULT_SIZE = 27;

    /**
     * 개인 창고가 확장될 수 있는 최대 크기입니다.
     */
    public static final int MAX_SIZE = 54;

    /**
     * 창고 확장 명령어 한 번으로 증가하는 슬롯 수입니다.
     */
    public static final int EXPAND_SIZE = 9;

    /**
     * 현재 창고 크기를 확장 단위만큼 증가시킨 값을 반환합니다.
     * <p>
     * 확장 결과가 최대 창고 크기를 초과하는 경우 최대 창고 크기를 반환합니다.
     *
     * @param currentSize 현재 창고 크기
     * @return 확장 후 창고 크기
     */
    public int expand(int currentSize) {
        return Math.min(currentSize + EXPAND_SIZE, MAX_SIZE);
    }

    /**
     * 현재 창고 크기가 확장 가능한 상태인지 확인합니다.
     *
     * @param currentSize 현재 창고 크기
     * @return 현재 창고 크기가 최대 창고 크기보다 작으면 {@code true}
     */
    public boolean canExpand(int currentSize) {
        return currentSize < MAX_SIZE;
    }
}
