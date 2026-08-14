package com.dajeom.type;

/**
 * 정밀화 10차-8.
 *
 * 핵심 십성 복합 Pattern.
 *
 * =================================================
 *
 * 여기에서 Pattern 존재 여부와
 * 구조적 성립도를 판정한다.
 *
 * 다만 길흉·용희기신·격국상의 성패는
 * 아직 최종 확정하지 않는다.
 *
 * 해당 교차검증은 10-9의 책임이다.
 */
public enum TenGodCorePatternType {

    EATING_GOD_CONTROLS_SEVEN_KILLINGS(
            "식신제살"
    ),

    HURTING_OFFICER_MEETS_DIRECT_OFFICER(
            "상관견관"
    ),

    HURTING_OFFICER_WITH_RESOURCE(
            "상관패인"
    ),

    INDIRECT_RESOURCE_SEIZES_EATING_GOD(
            "편인도식·효신탈식"
    ),

    WEALTH_GENERATES_DIRECT_OFFICER(
            "재생관"
    ),

    DIRECT_OFFICER_GENERATES_RESOURCE(
            "관인상생"
    ),

    SEVEN_KILLINGS_GENERATES_RESOURCE(
            "살인상생"
    ),

    WEALTH_CONTROLS_RESOURCE(
            "재극인"
    ),

    COMPANION_CONTESTS_WEALTH(
            "비겁쟁재"
    ),

    WEALTH_HEAVY_WEAK_DAY_MASTER(
            "재다신약"
    ),

    MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS(
            "관살혼재"
    );

    private final String koreanName;

    TenGodCorePatternType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean eatingGodControlsSevenKillings() {

        return this
                == EATING_GOD_CONTROLS_SEVEN_KILLINGS;
    }

    public boolean hurtingOfficerMeetsDirectOfficer() {

        return this
                == HURTING_OFFICER_MEETS_DIRECT_OFFICER;
    }

    public boolean hurtingOfficerWithResource() {

        return this
                == HURTING_OFFICER_WITH_RESOURCE;
    }

    public boolean indirectResourceSeizesEatingGod() {

        return this
                == INDIRECT_RESOURCE_SEIZES_EATING_GOD;
    }

    public boolean wealthGeneratesDirectOfficer() {

        return this
                == WEALTH_GENERATES_DIRECT_OFFICER;
    }

    public boolean directOfficerGeneratesResource() {

        return this
                == DIRECT_OFFICER_GENERATES_RESOURCE;
    }

    public boolean sevenKillingsGeneratesResource() {

        return this
                == SEVEN_KILLINGS_GENERATES_RESOURCE;
    }

    public boolean wealthControlsResource() {

        return this
                == WEALTH_CONTROLS_RESOURCE;
    }

    public boolean companionContestsWealth() {

        return this
                == COMPANION_CONTESTS_WEALTH;
    }

    public boolean wealthHeavyWeakDayMaster() {

        return this
                == WEALTH_HEAVY_WEAK_DAY_MASTER;
    }

    public boolean mixedOfficerKillings() {

        return this
                == MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS;
    }
}