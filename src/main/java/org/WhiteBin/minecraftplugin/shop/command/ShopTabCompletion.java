package org.WhiteBin.minecraftplugin.shop.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 상점 명령어 자동완성 후보를 생성합니다.
 */
class ShopTabCompletion {

    private static final List<String> ENGLISH_OP_COMMANDS = List.of("create", "delete", "add", "remove", "logs");
    private static final List<String> KOREAN_OP_COMMANDS = List.of("생성", "삭제", "추가", "제거", "로그");
    private static final String ENGLISH_LIST_COMMAND = "list";
    private static final String KOREAN_LIST_COMMAND = "목록";

    /**
     * 입력 중인 명령어 인자에 맞는 자동완성 후보를 반환합니다.
     *
     * @param op 명령어 실행자의 OP 여부
     * @param koreanCommand 한글 명령어 라벨 사용 여부
     * @param args 현재 입력된 명령어 인자
     * @param shopNames 상점 이름 목록
     * @return 자동완성 후보 목록
     */
    List<String> complete(boolean op, boolean koreanCommand, String[] args, Collection<String> shopNames) {
        if (args.length == 0) {
            return topLevelCandidates(op, koreanCommand, shopNames);
        }

        if (args.length == 1) {
            return filterByPrefix(topLevelCandidates(op, koreanCommand, shopNames), args[0]);
        }

        if (args.length == 2 && isShopNameArgumentCommand(args[0])) {
            return filterByPrefix(shopNames, args[1]);
        }

        if (args.length == 2 && isLogCommand(args[0])) {
            List<String> candidates = new ArrayList<>(shopNames);
            candidates.add(isKoreanLogCommand(args[0]) ? "유저" : "player");
            return filterByPrefix(candidates, args[1]);
        }

        return List.of();
    }

    private List<String> topLevelCandidates(boolean op, boolean koreanCommand, Collection<String> shopNames) {
        List<String> candidates = new ArrayList<>();
        candidates.add(koreanCommand ? KOREAN_LIST_COMMAND : ENGLISH_LIST_COMMAND);

        if (op) {
            candidates.addAll(koreanCommand ? KOREAN_OP_COMMANDS : ENGLISH_OP_COMMANDS);
        }

        candidates.addAll(shopNames);
        return candidates;
    }

    private boolean isShopNameArgumentCommand(String command) {
        String normalizedCommand = command.toLowerCase(Locale.ROOT);
        return normalizedCommand.equals("delete")
                || normalizedCommand.equals("add")
                || normalizedCommand.equals("remove")
                || command.equals("삭제")
                || command.equals("추가")
                || command.equals("제거");
    }

    private boolean isLogCommand(String command) {
        return command.equalsIgnoreCase("logs") || isKoreanLogCommand(command);
    }

    private boolean isKoreanLogCommand(String command) {
        return command.equals("로그");
    }

    private List<String> filterByPrefix(Collection<String> candidates, String prefix) {
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);

        return candidates.stream()
                .filter(candidate -> candidate.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix))
                .toList();
    }
}
