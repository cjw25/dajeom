package com.dajeom.domain;

/**
 * Evidence가 어느 계산 계층에서
 * 발생했는지를 나타낸다.
 *
 * 이 값 자체가 독립 Evidence 여부를 뜻하지는 않는다.
 *
 * 실제 독립성 판정은 Evidence.provenanceKey를
 * 기준으로 한다.
 */
public enum EvidenceSource {

    PILLAR,

    DAY_MASTER,

    MONTH_BRANCH,

    FIVE_ELEMENT_DISTRIBUTION,

    YIN_YANG_DISTRIBUTION,

    TEN_GOD,

    HIDDEN_STEM,

    TWELVE_STAGE,

    STEM_RELATION,

    BRANCH_RELATION,

    BRANCH_GROUP_RELATION,

    VOID,

    NAEUM,

    NOBLEMAN,

    TWELVE_SINSAL,

    SPECIAL_STAR,

    DAEWOON,

    SEWOON,

    WOLWOON,

    /**
     * 여러 계산값을 하나의 명시적 Java 규칙으로
     * 조합해서 만들어낸 Evidence.
     *
     * AI 합성이라는 뜻이 아니다.
     */
    COMPOSITE_RULE
}