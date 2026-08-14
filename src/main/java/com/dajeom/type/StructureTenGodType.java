package com.dajeom.type;

/**
 * 격국 분석에서 사용하는 정확한 십성.
 *
 * 5차에서는 월령 지장간과 일간의 관계를
 * 정확한 음양까지 포함해 판정해야 하므로
 * 비겁/인성/식상/재성/관살의 큰 그룹보다
 * 한 단계 더 세분화된 십성을 사용한다.
 *
 * COMPANION
 * 비견
 *
 * ROB_WEALTH
 * 겁재
 *
 * EATING_GOD
 * 식신
 *
 * HURTING_OFFICER
 * 상관
 *
 * INDIRECT_WEALTH
 * 편재
 *
 * DIRECT_WEALTH
 * 정재
 *
 * SEVEN_KILLINGS
 * 편관 / 칠살
 *
 * DIRECT_OFFICER
 * 정관
 *
 * INDIRECT_RESOURCE
 * 편인
 *
 * DIRECT_RESOURCE
 * 정인
 */
public enum StructureTenGodType {

    COMPANION(
            "비견",
            "비겁"
    ),

    ROB_WEALTH(
            "겁재",
            "비겁"
    ),

    EATING_GOD(
            "식신",
            "식상"
    ),

    HURTING_OFFICER(
            "상관",
            "식상"
    ),

    INDIRECT_WEALTH(
            "편재",
            "재성"
    ),

    DIRECT_WEALTH(
            "정재",
            "재성"
    ),

    SEVEN_KILLINGS(
            "편관",
            "관살"
    ),

    DIRECT_OFFICER(
            "정관",
            "관살"
    ),

    INDIRECT_RESOURCE(
            "편인",
            "인성"
    ),

    DIRECT_RESOURCE(
            "정인",
            "인성"
    );

    private final String koreanName;

    private final String groupKoreanName;

    StructureTenGodType(
            String koreanName,
            String groupKoreanName
    ) {

        this.koreanName =
                koreanName;

        this.groupKoreanName =
                groupKoreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public String groupKoreanName() {

        return groupKoreanName;
    }

    public boolean companion() {

        return this == COMPANION;
    }

    public boolean robWealth() {

        return this == ROB_WEALTH;
    }

    public boolean eatingGod() {

        return this == EATING_GOD;
    }

    public boolean hurtingOfficer() {

        return this == HURTING_OFFICER;
    }

    public boolean indirectWealth() {

        return this == INDIRECT_WEALTH;
    }

    public boolean directWealth() {

        return this == DIRECT_WEALTH;
    }

    public boolean sevenKillings() {

        return this == SEVEN_KILLINGS;
    }

    public boolean directOfficer() {

        return this == DIRECT_OFFICER;
    }

    public boolean indirectResource() {

        return this == INDIRECT_RESOURCE;
    }

    public boolean directResource() {

        return this == DIRECT_RESOURCE;
    }

    public boolean companionGroup() {

        return companion()
                || robWealth();
    }

    public boolean outputGroup() {

        return eatingGod()
                || hurtingOfficer();
    }

    public boolean wealthGroup() {

        return indirectWealth()
                || directWealth();
    }

    public boolean officerGroup() {

        return sevenKillings()
                || directOfficer();
    }

    public boolean resourceGroup() {

        return indirectResource()
                || directResource();
    }
}