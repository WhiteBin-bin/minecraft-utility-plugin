package org.WhiteBin.minecraftplugin.storage.model;

import java.util.UUID;

/**
 * 개인 창고 공유 대상 또는 공유받은 창고 정보를 표현하는 값 객체입니다.
 * <p>
 * UUID를 기준으로 공유 관계를 식별하고,
 * 명령어 목록 출력에는 저장된 플레이어 이름을 사용합니다.
 *
 * @param uuid 공유 관계에 포함된 플레이어 UUID
 * @param name 목록 출력에 사용할 플레이어 이름
 */
public record StorageShareInfo(UUID uuid, String name) {
}
