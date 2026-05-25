package org.WhiteBin.minecraftplugin.storage.model;

/**
 * 개인 창고에서 로그로 기록할 작업 타입입니다.
 * <p>
 * 창고 열기, 저장, 크기 변경, 정렬, 초기화, 공유 작업을 구분하기 위해 사용합니다.
 */
public enum StorageLogType {

    /**
     * 개인 창고 열기 작업입니다.
     */
    OPEN,

    /**
     * 개인 창고 저장 작업입니다.
     */
    SAVE,

    /**
     * 개인 창고 확장 작업입니다.
     */
    EXPAND,

    /**
     * 개인 창고 축소 작업입니다.
     */
    SHRINK,

    /**
     * 개인 창고 정렬 작업입니다.
     */
    SORT,

    /**
     * 개인 창고 초기화 작업입니다.
     */
    CLEAR,

    /**
     * 개인 창고 공유 작업입니다.
     */
    SHARE,

    /**
     * 개인 창고 공유 해제 작업입니다.
     */
    UNSHARE
}
