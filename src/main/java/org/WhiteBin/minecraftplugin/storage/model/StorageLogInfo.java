package org.WhiteBin.minecraftplugin.storage.model;

import java.util.UUID;

/**
 * 개인 창고에서 발생한 작업 로그 정보를 표현하는 값 객체입니다.
 * <p>
 * 작업 시각, 작업 타입, 실행자, 창고 소유자, 상세 메시지를 함께 보관하여
 * 관리자 로그 조회 명령어에서 출력할 수 있게 합니다.
 *
 * @param createdAt 로그가 생성된 시각
 * @param type 창고 작업 타입
 * @param actorUuid 작업을 실행한 플레이어 UUID
 * @param actorName 작업을 실행한 플레이어 이름
 * @param ownerUuid 작업 대상 창고 소유자 UUID
 * @param ownerName 작업 대상 창고 소유자 이름
 * @param detail 작업 상세 메시지
 */
public record StorageLogInfo(
        String createdAt,
        StorageLogType type,
        UUID actorUuid,
        String actorName,
        UUID ownerUuid,
        String ownerName,
        String detail
) {
}
