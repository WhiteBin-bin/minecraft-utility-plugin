package org.WhiteBin.minecraftplugin.economy.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link EconomyTabCompletion}의 경제 명령어 자동완성 후보 생성을 검증합니다.
 */
class EconomyTabCompletionTest {

    private final EconomyTabCompletion economyTabCompletion = new EconomyTabCompletion();

    /**
     * 첫 번째 인자 위치에서 현재 서버에 접속 중인 플레이어 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsOnlinePlayerNames() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = economyTabCompletion.complete(false, false, new String[]{""}, onlinePlayerNames);

        // then
        assertEquals(List.of("pay", "WhiteBin", "Steve"), completions);
    }

    /**
     * 첫 번째 인자 자동완성 후보를 입력 중인 접두어로 필터링하는지 검증합니다.
     */
    @Test
    void completeFiltersPlayerNamesByPrefix() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = economyTabCompletion.complete(false, false, new String[]{"W"}, onlinePlayerNames);

        // then
        assertEquals(List.of("WhiteBin"), completions);
    }

    /**
     * 두 번째 인자 이후에는 자동완성 후보를 반환하지 않는지 검증합니다.
     */
    @Test
    void completeReturnsEmptyAfterFirstArgument() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = economyTabCompletion.complete(false, false, new String[]{"WhiteBin", ""}, onlinePlayerNames);

        // then
        assertTrue(completions.isEmpty());
    }

    /**
     * OP가 영어 명령어를 입력할 때 잔액 관리 하위 명령어를 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsEnglishManagementCommandsForOp() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin");

        // when
        List<String> completions = economyTabCompletion.complete(true, false, new String[]{""}, onlinePlayerNames);

        // then
        assertEquals(List.of("pay", "give", "take", "set", "WhiteBin"), completions);
    }

    /**
     * OP가 한글 명령어를 입력할 때 한글 잔액 관리 하위 명령어를 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsKoreanManagementCommandsForOp() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin");

        // when
        List<String> completions = economyTabCompletion.complete(true, true, new String[]{""}, onlinePlayerNames);

        // then
        assertEquals(List.of("보내기", "지급", "차감", "설정", "WhiteBin"), completions);
    }

    /**
     * 잔액 관리 하위 명령어의 플레이어 인자 위치에서 플레이어 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsPlayerNamesForManagementCommand() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = economyTabCompletion.complete(true, false, new String[]{"give", "S"}, onlinePlayerNames);

        // then
        assertEquals(List.of("Steve"), completions);
    }

    /**
     * 송금 하위 명령어의 플레이어 인자 위치에서 플레이어 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsPlayerNamesForTransferCommand() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = economyTabCompletion.complete(false, false, new String[]{"pay", "S"}, onlinePlayerNames);

        // then
        assertEquals(List.of("Steve"), completions);
    }
}
