package com.dajeom.type;

/**
 * 오행 사이의 기본 작용.
 *
 * GENERATING
 * 상생 관계.
 *
 * 木生火
 * 火生土
 * 土生金
 * 金生水
 * 水生木
 *
 * CONTROLLING
 * 상극 관계.
 *
 * 木克土
 * 土克水
 * 水克火
 * 火克金
 * 金克木
 */
public enum ElementInteractionType {

    GENERATING(
            "상생",
            "生"
    ),

    CONTROLLING(
            "상극",
            "克"
    );

    private final String koreanName;

    private final String chineseName;

    ElementInteractionType(
            String koreanName,
            String chineseName
    ) {

        this.koreanName =
                koreanName;

        this.chineseName =
                chineseName;
    }

    public String koreanName() {

        return koreanName;
    }

    public String chineseName() {

        return chineseName;
    }

    public boolean generating() {

        return this == GENERATING;
    }

    public boolean controlling() {

        return this == CONTROLLING;
    }
}