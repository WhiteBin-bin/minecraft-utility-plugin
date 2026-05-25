# Minecraft Plugin

마인크래프트 편의 기능 플러그인입니다.

## 기능

- 개인 창고 명령어
- 개인 창고 확장
- 개인 창고 축소
- OP의 다른 유저 창고 접근
- 플레이어 UUID 기준 창고 데이터 저장

## 명령어

| 명령어 | 별칭 | 설명                        |
| --- | --- |---------------------------|
| `/storage` | `/창고` | 자신의 개인 창고를 엽니다.           |
| `/storage <player>` | `/창고 <player>` | 다른 유저의 창고를 엽니다.(OP만 사용가능) |
| `/storage expand` | `/창고 확장` | 자신의 개인 창고를 9칸 확장합니다.(OP만 사용가능) |
| `/storage expand <player>` | `/창고 확장 <player>` | 다른 유저의 개인 창고를 9칸 확장합니다.(OP만 사용가능) |
| `/storage shrink` | `/창고 축소` | 자신의 개인 창고를 9칸 축소합니다.(OP만 사용가능) |
| `/storage shrink <player>` | `/창고 축소 <player>` | 다른 유저의 개인 창고를 9칸 축소합니다.(OP만 사용가능) |

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
