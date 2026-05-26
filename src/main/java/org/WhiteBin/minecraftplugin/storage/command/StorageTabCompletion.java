package org.WhiteBin.minecraftplugin.storage.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 개인 창고 명령어 자동완성 후보를 생성합니다.
 */
class StorageTabCompletion {

    private static final List<String> ENGLISH_PLAYER_COMMANDS = List.of(
            "expand",
            "shrink",
            "sort",
            "clear",
            "share",
            "unshare",
            "logs"
    );

    private static final List<String> KOREAN_PLAYER_COMMANDS = List.of(
            "확장",
            "축소",
            "정렬",
            "초기화",
            "공유",
            "공유해제",
            "로그"
    );

    /**
     * 입력 중인 명령어 인자에 맞는 자동완성 후보를 반환합니다.
     *
     * @param op 명령어 실행자의 OP 여부
     * @param koreanCommand 한글 명령어 라벨 사용 여부
     * @param args 현재 입력된 명령어 인자
     * @param onlinePlayerNames 온라인 플레이어 이름 목록
     * @return 자동완성 후보 목록
     */
    List<String> complete(boolean op, boolean koreanCommand, String[] args, Collection<String> onlinePlayerNames) {
        if (args.length == 0) {
            return topLevelCandidates(op, koreanCommand, onlinePlayerNames);
        }

        if (args.length == 1) {
            return filterByPrefix(topLevelCandidates(op, koreanCommand, onlinePlayerNames), args[0]);
        }

        if (args.length == 2 && requiresPlayerArgument(args[0], op)) {
            return filterByPrefix(onlinePlayerNames, args[1]);
        }

        return List.of();
    }

    private List<String> topLevelCandidates(boolean op, boolean koreanCommand, Collection<String> onlinePlayerNames) {
        List<String> candidates = new ArrayList<>();

        if (koreanCommand) {
            if (op) {
                candidates.add("확장");
                candidates.add("축소");
            }

            candidates.add("정렬");
            candidates.add("초기화");
            candidates.add("공유");
            candidates.add("공유해제");
            candidates.add("공유목록");
            candidates.add("공유받은목록");

            if (op) {
                candidates.add("로그");
            }
        } else {
            if (op) {
                candidates.add("expand");
                candidates.add("shrink");
            }

            candidates.add("sort");
            candidates.add("clear");
            candidates.add("share");
            candidates.add("unshare");
            candidates.add("shares");
            candidates.add("shared");

            if (op) {
                candidates.add("logs");
            }
        }

        candidates.addAll(onlinePlayerNames);
        return candidates;
    }

    private boolean requiresPlayerArgument(String command, boolean op) {
        String normalizedCommand = command.toLowerCase(Locale.ROOT);

        if (normalizedCommand.equals("share")
                || normalizedCommand.equals("unshare")
                || normalizedCommand.equals("공유")
                || normalizedCommand.equals("공유해제")) {
            return true;
        }

        return op && (ENGLISH_PLAYER_COMMANDS.contains(normalizedCommand) || KOREAN_PLAYER_COMMANDS.contains(command));
    }

    private List<String> filterByPrefix(Collection<String> candidates, String prefix) {
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);

        return candidates.stream()
                .filter(candidate -> candidate.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix))
                .toList();
    }
}
