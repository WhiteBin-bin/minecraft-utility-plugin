package org.WhiteBin.minecraftplugin.economy.command;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 경제 명령어 자동완성 후보를 생성합니다.
 */
class EconomyTabCompletion {

    /**
     * 입력 중인 명령어 인자에 맞는 자동완성 후보를 반환합니다.
     *
     * @param args 현재 입력된 명령어 인자
     * @param onlinePlayerNames 현재 서버에 접속 중인 플레이어 이름 목록
     * @return 자동완성 후보 목록
     */
    List<String> complete(String[] args, Collection<String> onlinePlayerNames) {
        if (args.length == 0) {
            return onlinePlayerNames.stream().toList();
        }

        if (args.length == 1) {
            return filterByPrefix(onlinePlayerNames, args[0]);
        }

        return List.of();
    }

    private List<String> filterByPrefix(Collection<String> candidates, String prefix) {
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);

        return candidates.stream()
                .filter(candidate -> candidate.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix))
                .toList();
    }
}
