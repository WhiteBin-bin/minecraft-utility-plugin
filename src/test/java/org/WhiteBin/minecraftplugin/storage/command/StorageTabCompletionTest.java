package org.WhiteBin.minecraftplugin.storage.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link StorageTabCompletion}의 개인 창고 명령어 자동완성 후보 생성을 검증합니다.
 */
class StorageTabCompletionTest {

    private final StorageTabCompletion storageTabCompletion = new StorageTabCompletion();

    /**
     * OP가 영어 명령어를 입력할 때 OP 전용 하위 명령어와 온라인 플레이어 후보를 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsEnglishOpCommandsAndPlayersForOp() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = storageTabCompletion.complete(true, false, new String[]{""}, onlinePlayerNames);

        // then
        assertEquals(List.of("expand", "shrink", "sort", "clear", "share", "unshare", "shares", "shared", "logs", "WhiteBin", "Steve"), completions);
    }

    /**
     * 일반 유저가 영어 명령어를 입력할 때 OP 전용 하위 명령어를 반환하지 않는지 검증합니다.
     */
    @Test
    void completeDoesNotReturnEnglishOpCommandsForNonOp() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin");

        // when
        List<String> completions = storageTabCompletion.complete(false, false, new String[]{""}, onlinePlayerNames);

        // then
        assertFalse(completions.contains("expand"));
        assertFalse(completions.contains("shrink"));
        assertFalse(completions.contains("logs"));
        assertTrue(completions.contains("sort"));
        assertTrue(completions.contains("share"));
        assertTrue(completions.contains("WhiteBin"));
    }

    /**
     * OP가 한글 명령어를 입력할 때 한글 하위 명령어 후보를 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsKoreanCommandsForKoreanCommand() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin");

        // when
        List<String> completions = storageTabCompletion.complete(true, true, new String[]{""}, onlinePlayerNames);

        // then
        assertEquals(List.of("확장", "축소", "정렬", "초기화", "공유", "공유해제", "공유목록", "공유받은목록", "로그", "WhiteBin"), completions);
    }

    /**
     * 첫 번째 인자 자동완성 후보를 입력 중인 접두어로 필터링하는지 검증합니다.
     */
    @Test
    void completeFiltersTopLevelCandidatesByPrefix() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = storageTabCompletion.complete(true, false, new String[]{"sh"}, onlinePlayerNames);

        // then
        assertEquals(List.of("shrink", "share", "shares", "shared"), completions);
    }

    /**
     * 플레이어 인자가 필요한 영어 하위 명령어에서 온라인 플레이어 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsPlayerNamesForEnglishPlayerArgument() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = storageTabCompletion.complete(true, false, new String[]{"logs", "W"}, onlinePlayerNames);

        // then
        assertEquals(List.of("WhiteBin"), completions);
    }

    /**
     * 플레이어 인자가 필요한 한글 하위 명령어에서 온라인 플레이어 이름을 반환하는지 검증합니다.
     */
    @Test
    void completeReturnsPlayerNamesForKoreanPlayerArgument() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = storageTabCompletion.complete(true, true, new String[]{"로그", "S"}, onlinePlayerNames);

        // then
        assertEquals(List.of("Steve"), completions);
    }

    /**
     * 일반 유저가 OP 전용 하위 명령어의 플레이어 인자 후보를 받을 수 없는지 검증합니다.
     */
    @Test
    void completeDoesNotReturnPlayerNamesForNonOpOpCommand() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = storageTabCompletion.complete(false, false, new String[]{"logs", ""}, onlinePlayerNames);

        // then
        assertTrue(completions.isEmpty());
    }
}
