package com.dajeom.type;

/**
 * 격국 후보의 출처.
 *
 * STANDARD
 * 월령 정기·중기·여기에서 생성된
 * 일반 내격 후보.
 *
 * SPECIAL
 * 건록·양인·종격 등
 * 5-8 특수구조 후보.
 */
public enum StructureCandidateKind {

    STANDARD(
            "일반격"
    ),

    SPECIAL(
            "특수격"
    );

    private final String koreanName;

    StructureCandidateKind(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean standard() {

        return this == STANDARD;
    }

    public boolean special() {

        return this == SPECIAL;
    }
}