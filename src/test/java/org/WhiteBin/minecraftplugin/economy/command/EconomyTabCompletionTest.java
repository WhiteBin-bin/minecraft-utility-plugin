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
        List<String> completions = economyTabCompletion.complete(new String[]{""}, onlinePlayerNames);

        // then
        assertEquals(onlinePlayerNames, completions);
    }

    /**
     * 첫 번째 인자 자동완성 후보를 입력 중인 접두어로 필터링하는지 검증합니다.
     */
    @Test
    void completeFiltersPlayerNamesByPrefix() {
        // given
        List<String> onlinePlayerNames = List.of("WhiteBin", "Steve");

        // when
        List<String> completions = economyTabCompletion.complete(new String[]{"W"}, onlinePlayerNames);

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
        List<String> completions = economyTabCompletion.complete(new String[]{"WhiteBin", ""}, onlinePlayerNames);

        // then
        assertTrue(completions.isEmpty());
    }
}
