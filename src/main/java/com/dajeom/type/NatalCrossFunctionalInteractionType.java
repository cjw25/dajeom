package com.dajeom.type;

/**
 * 원국 교차 기능 Pattern의
 * 구조적 상호작용 종류.
 *
 * =================================================
 *
 * 주의:
 *
 * 이 값은 길흉 등급이 아니다.
 *
 * GENERATION = 무조건 좋음
 * CONTROL = 무조건 나쁨
 *
 * 같은 의미가 아니다.
 *
 * 실제 Pattern의 성립/약화/충돌 상태는
 * 기존 10-9 TenGodCrossValidationStatus를
 * 그대로 사용한다.
 *
 * =================================================
 *
 * GENERATION
 *
 * 한 기능이 다른 기능을 생하는 구조.
 *
 * 예:
 *
 * 재생관
 * 관인상생
 * 살인상생
 *
 * =================================================
 *
 * CONTROL
 *
 * 한 기능이 다른 기능을
 * 제어·극하는 구조.
 *
 * 예:
 *
 * 식신제살
 * 상관패인
 * 편인도식
 * 재극인
 *
 * =================================================
 *
 * CONFLICT
 *
 * 서로 다른 기능이
 * 직접적으로 충돌하는 구조.
 *
 * 예:
 *
 * 상관견관
 *
 * =================================================
 *
 * COMPETITION
 *
 * 기능 사이에 경합이 발생하는 구조.
 *
 * 예:
 *
 * 비겁쟁재
 *
 * =================================================
 *
 * OVERLOAD
 *
 * 특정 기능이
 * 일간 자기기반의 수용 범위를
 * 압박하는 구조.
 *
 * 예:
 *
 * 재다신약
 *
 * =================================================
 *
 * MIXTURE
 *
 * 동일 기능 Family 내부에서
 * 서로 다른 작동이 혼재하는 구조.
 *
 * 예:
 *
 * 관살혼재
 */
public enum NatalCrossFunctionalInteractionType {

    GENERATION(
            "생"
    ),

    CONTROL(
            "제어·극"
    ),

    CONFLICT(
            "충돌"
    ),

    COMPETITION(
            "경합"
    ),

    OVERLOAD(
            "과중"
    ),

    MIXTURE(
            "혼재"
    );

    private final String koreanName;

    NatalCrossFunctionalInteractionType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}