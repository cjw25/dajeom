package com.dajeom.type;

/**
 * 정밀화 10차-4.
 *
 * 편재·정재 복합구조 유형.
 *
 * =================================================
 *
 * 하나의 명식에서 여러 Pattern이
 * 동시에 존재할 수 있다.
 *
 * 예:
 *
 * 재성 발달
 * +
 * 정재 중심
 * +
 * 신약 재성 부담
 *
 * 을 동시에 보존할 수 있다.
 *
 * =================================================
 *
 * 주의:
 *
 * 이번 단계에서는 다음 고전 Pattern을
 * 최종 확정하지 않는다.
 *
 * - 재다신약
 * - 비겁쟁재
 * - 재생관
 * - 재극인
 *
 * 해당 구조는 10-7~10-9에서
 * 다른 십성·격국·용신과 교차검증한다.
 */
public enum WealthCompositePatternType {

    WEALTH_DEVELOPED(
            "재성 발달"
    ),

    DIRECT_WEALTH_CENTERED(
            "정재 중심"
    ),

    INDIRECT_WEALTH_CENTERED(
            "편재 중심"
    ),

    MIXED_WEALTH(
            "정재·편재 혼재"
    ),

    WEAK_DAY_MASTER_WEALTH_BURDEN(
            "신약 재성 부담"
    ),

    COMPANION_PRESSURED_WEALTH(
            "비겁 압박 재성"
    );

    private final String koreanName;

    WealthCompositePatternType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean developed() {

        return this
                == WEALTH_DEVELOPED;
    }

    public boolean directWealthCentered() {

        return this
                == DIRECT_WEALTH_CENTERED;
    }

    public boolean indirectWealthCentered() {

        return this
                == INDIRECT_WEALTH_CENTERED;
    }

    public boolean mixed() {

        return this
                == MIXED_WEALTH;
    }

    public boolean weakDayMasterWealthBurden() {

        return this
                == WEAK_DAY_MASTER_WEALTH_BURDEN;
    }

    public boolean companionPressured() {

        return this
                == COMPANION_PRESSURED_WEALTH;
    }
}