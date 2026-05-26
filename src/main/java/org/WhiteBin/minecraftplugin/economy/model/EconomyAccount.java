package org.WhiteBin.minecraftplugin.economy.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 플레이어 경제 계좌 정보를 표현하는 값 객체입니다.
 *
 * @param uuid 계좌 소유자 UUID
 * @param name 계좌 소유자 이름
 * @param balance 계좌 잔액
 */
public record EconomyAccount(UUID uuid, String name, BigDecimal balance) {
}
