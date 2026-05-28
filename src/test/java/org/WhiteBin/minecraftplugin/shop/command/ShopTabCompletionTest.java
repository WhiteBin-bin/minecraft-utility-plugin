package org.WhiteBin.minecraftplugin.shop.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ShopTabCompletion}의 상점 명령어 자동완성 후보 생성을 검증합니다.
 */
class ShopTabCompletionTest {

    private final ShopTabCompletion shopTabCompletion = new ShopTabCompletion();

    /**
     * OP가 영어 명령어를 입력할 때 상점 관리 명령어와 상점 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsEnglishCommandsAndShopNamesForOp() {
        // given
        List<String> shopNames = List.of("food");

        // when
        List<String> completions = shopTabCompletion.complete(true, false, new String[]{""}, shopNames);

        // then
        assertEquals(List.of("list", "create", "delete", "add", "remove", "logs", "food"), completions);
    }

    /**
     * 일반 유저가 영어 명령어를 입력할 때 관리 명령어를 반환하지 않는지 검증합니다.
     */
    @Test
    void completeDoesNotReturnManagementCommandsForNonOp() {
        // given
        List<String> shopNames = List.of("food");

        // when
        List<String> completions = shopTabCompletion.complete(false, false, new String[]{""}, shopNames);

        // then
        assertEquals(List.of("list", "food"), completions);
    }

    /**
     * OP가 한글 명령어를 입력할 때 한글 관리 명령어와 상점 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsKoreanCommandsAndShopNamesForOp() {
        // given
        List<String> shopNames = List.of("food");

        // when
        List<String> completions = shopTabCompletion.complete(true, true, new String[]{""}, shopNames);

        // then
        assertEquals(List.of("목록", "생성", "삭제", "추가", "제거", "로그", "food"), completions);
    }

    /**
     * 상점 이름 인자 위치에서 상점 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsShopNamesForShopNameArgument() {
        // given
        List<String> shopNames = List.of("food", "ore");

        // when
        List<String> completions = shopTabCompletion.complete(true, false, new String[]{"add", "f"}, shopNames);

        // then
        assertEquals(List.of("food"), completions);
    }

    /**
     * 세 번째 인자 이후에는 자동완성 후보를 반환하지 않는지 검증합니다.
     */
    @Test
    void completeReturnsEmptyAfterShopNameArgument() {
        // given
        List<String> shopNames = List.of("food", "ore");

        // when
        List<String> completions = shopTabCompletion.complete(true, false, new String[]{"add", "food", ""}, shopNames);

        // then
        assertTrue(completions.isEmpty());
    }
}
