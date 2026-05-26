package org.WhiteBin.minecraftplugin.storage.policy;

/**
 * 개인 창고 조회 GUI의 크기와 표시 범위를 계산합니다.
 */
public class StorageGuiLayoutPolicy {

    public static final int MAX_GUI_SIZE = 54;
    public static final int MIN_GUI_SIZE = 9;
    public static final int CONTENT_SIZE = 45;
    public static final int PREVIOUS_PAGE_SLOT = 45;
    public static final int FILTER_SLOT = 49;
    public static final int NEXT_PAGE_SLOT = 53;
    public static final int FILTER_OPTION_START_SLOT = 10;

    /**
     * 표시할 항목 수에 맞는 GUI 인벤토리 크기를 반환합니다.
     *
     * @param itemCount 표시할 항목 수
     * @return 9칸 단위 GUI 크기
     */
    public int inventorySize(int itemCount) {
        int requiredSize = Math.max(itemCount, 1);
        int rows = (int) Math.ceil(requiredSize / 9.0);
        return Math.min(MAX_GUI_SIZE, Math.max(MIN_GUI_SIZE, rows * 9));
    }

    /**
     * 최신 항목을 최대 GUI 크기만큼 표시하기 위한 시작 인덱스를 반환합니다.
     *
     * @param itemCount 전체 항목 수
     * @return 표시 시작 인덱스
     */
    public int visibleStartIndex(int itemCount) {
        return Math.max(0, itemCount - MAX_GUI_SIZE);
    }

    /**
     * GUI에 표시할 수 있는 항목 수를 반환합니다.
     *
     * @param itemCount 전체 항목 수
     * @return GUI에 표시할 항목 수
     */
    public int visibleItemCount(int itemCount) {
        return Math.min(itemCount, MAX_GUI_SIZE);
    }

    /**
     * GUI 페이지 수를 반환합니다.
     *
     * @param itemCount 전체 항목 수
     * @return 전체 페이지 수
     */
    public int totalPages(int itemCount) {
        return Math.max(1, (int) Math.ceil(itemCount / (double) CONTENT_SIZE));
    }

    /**
     * 현재 페이지에서 표시를 시작할 항목 인덱스를 반환합니다.
     *
     * @param page 현재 페이지
     * @return 표시 시작 인덱스
     */
    public int pageStartIndex(int page) {
        return Math.max(0, page) * CONTENT_SIZE;
    }

    /**
     * 현재 페이지에서 표시를 끝낼 항목 인덱스를 반환합니다.
     *
     * @param itemCount 전체 항목 수
     * @param page 현재 페이지
     * @return 표시 종료 인덱스
     */
    public int pageEndIndex(int itemCount, int page) {
        return Math.min(itemCount, pageStartIndex(page) + CONTENT_SIZE);
    }

    /**
     * 전체 페이지 범위 안으로 보정된 페이지 번호를 반환합니다.
     *
     * @param page 요청 페이지
     * @param itemCount 전체 항목 수
     * @return 보정된 페이지 번호
     */
    public int normalizePage(int page, int itemCount) {
        return Math.min(Math.max(0, page), totalPages(itemCount) - 1);
    }
}
