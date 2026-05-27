package org.WhiteBin.minecraftplugin.economy.command;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 경제 명령어 자동완성 후보를 생성합니다.
 */
class EconomyTabCompletion {

    private static final List<String> ENGLISH_OP_COMMANDS = List.of("give", "take", "set");
    private static final List<String> KOREAN_OP_COMMANDS = List.of("지급", "차감", "설정");

    /**
     * 입력 중인 명령어 인자에 맞는 자동완성 후보를 반환합니다.
     *
     * @param op 명령어 실행자의 OP 여부
     * @param koreanCommand 한글 명령어 라벨 사용 여부
     * @param args 현재 입력된 명령어 인자
     * @param onlinePlayerNames 현재 서버에 접속 중인 플레이어 이름 목록
     * @return 자동완성 후보 목록
     */
    List<String> complete(boolean op, boolean koreanCommand, String[] args, Collection<String> onlinePlayerNames) {
        if (args.length == 0) {
            return topLevelCandidates(op, koreanCommand, onlinePlayerNames);
        }

        if (args.length == 1) {
            return filterByPrefix(topLevelCandidates(op, koreanCommand, onlinePlayerNames), args[0]);
        }

        if (args.length == 2 && isOpCommand(args[0])) {
            return filterByPrefix(onlinePlayerNames, args[1]);
        }

        return List.of();
    }

    private List<String> topLevelCandidates(boolean op, boolean koreanCommand, Collection<String> onlinePlayerNames) {
        List<String> candidates = new ArrayList<>();

        if (op) {
            candidates.addAll(koreanCommand ? KOREAN_OP_COMMANDS : ENGLISH_OP_COMMANDS);
        }

        candidates.addAll(onlinePlayerNames);
        return candidates;
    }

    private boolean isOpCommand(String command) {
        String normalizedCommand = command.toLowerCase(Locale.ROOT);
        return ENGLISH_OP_COMMANDS.contains(normalizedCommand) || KOREAN_OP_COMMANDS.contains(command);
    }

    private List<String> filterByPrefix(Collection<String> candidates, String prefix) {
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);

        return candidates.stream()
                .filter(candidate -> candidate.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix))
                .toList();
    }
}
