# Minecraft Plugin

마인크래프트 편의 기능 플러그인입니다.

## 기능

- 개인 창고 명령어
- 개인 창고 확장
- 개인 창고 축소
- 개인 창고 정렬
- 개인 창고 초기화
- 개인 창고 공유
- OP의 다른 유저 창고 접근
- 플레이어 UUID 기준 창고 데이터 저장

## 명령어

| 명령어 | 별칭 | 설명                        |
| --- | --- |---------------------------|
| `/storage` | `/창고` | 자신의 개인 창고를 엽니다.           |
| `/storage <player>` | `/창고 <player>` | 공유받은 유저 또는 OP가 다른 유저의 창고를 엽니다. |
| `/storage expand` | `/창고 확장` | 자신의 개인 창고를 9칸 확장합니다.(OP만 사용가능) |
| `/storage expand <player>` | `/창고 확장 <player>` | 다른 유저의 개인 창고를 9칸 확장합니다.(OP만 사용가능) |
| `/storage shrink` | `/창고 축소` | 자신의 개인 창고를 9칸 축소합니다.(OP만 사용가능) |
| `/storage shrink <player>` | `/창고 축소 <player>` | 다른 유저의 개인 창고를 9칸 축소합니다.(OP만 사용가능) |
| `/storage sort` | `/창고 정렬` | 자신의 개인 창고 아이템을 정렬합니다. |
| `/storage sort <player>` | `/창고 정렬 <player>` | 다른 유저의 개인 창고 아이템을 정렬합니다.(OP만 사용가능) |
| `/storage clear` | `/창고 초기화` | 자신의 개인 창고 아이템을 초기화합니다. |
| `/storage clear <player>` | `/창고 초기화 <player>` | 다른 유저의 개인 창고 아이템을 초기화합니다.(OP만 사용가능) |
| `/storage share <player>` | `/창고 공유 <player>` | 자신의 개인 창고를 다른 유저에게 공유합니다. |
| `/storage unshare <player>` | `/창고 공유해제 <player>` | 다른 유저에게 공유한 개인 창고를 해제합니다. |
| `/storage shares` | `/창고 공유목록` | 내가 공유 중인 유저 목록을 확인합니다. |
| `/storage shared` | `/창고 공유받은목록` | 내가 공유받은 창고 목록을 확인합니다. |

## 요구사항

- Java 21
- Paper 1.21.11

## 빌드

```bash
./gradlew build
```

플러그인 jar 파일은 아래 경로에 생성됩니다.

```text
build/libs/
```

## 개발

로컬 Paper 서버를 실행하려면 아래 명령어를 사용합니다.

```bash
./gradlew runServer
```

## 컨벤션

- [커밋 컨벤션](.github/COMMIT_CONVENTION.md)
