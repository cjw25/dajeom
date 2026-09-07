package com.dajeom.domain;

/**
 * Java 명리 규칙 엔진이 확정한 구조를
 * 실제 삶의 의미 단위로 변환한 코드.
 *
 * AI는 이 코드를 기반으로 새로운 명리 판정을 하지 않고
 * 별도의 NarrativeDirective를 통해 자연어 문장화만 담당한다.
 */
public enum LifeClaimCode {

    /*
     * =================================================
     * 기본 십성 family 구조
     * =================================================
     */

    /**
     * 준비·검토·학습·안정 기반이
     * 삶의 운영에서 중요한 자원이 될 수 있음.
     */
    PREPARATION_AND_REFLECTION_SUPPORT_STABILITY,

    /**
     * 생각이나 능력을 외부의 표현·생산·소통으로
     * 적절하게 사용하는 것이 중요.
     */
    EXPRESSION_NEEDS_A_CONSTRUCTIVE_OUTLET,

    /**
     * 시간·돈·기회·노력 같은 현실 자원의
     * 배분과 관리가 반복적으로 중요한 문제.
     */
    PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME,

    /**
     * 책임·기대·규칙·역할이
     * 중요한 선택 기준으로 작동할 수 있음.
     */
    RESPONSIBILITY_AND_EXPECTATIONS_SHAPE_CHOICES,

    /**
     * 사람과 함께 움직이더라도
     * 자신의 선택권과 역할 경계를 유지하는 것이 중요.
     */
    AUTONOMY_AND_PEER_BOUNDARIES_MATTER,

    /**
     * 자신의 활동 패턴에 맞는 지속 가능한
     * 생활 루틴과 페이스 설정이 중요.
     */
    SUSTAINABLE_ROUTINE_SHOULD_MATCH_ENERGY_PATTERN,

    /*
     * =================================================
     * 日主 기본 강약
     * =================================================
     */

    /**
     * 자기 기준과 방향을 비교적 지속하기 쉬움.
     */
    SELF_DIRECTION_IS_EASIER_TO_SUSTAIN,

    /**
     * 지속적인 성과를 위해
     * 활동량과 지원·회복 자원의 균형이 중요.
     */
    PACING_AND_SUPPORT_MATTER_FOR_SUSTAINED_OUTPUT,

    /**
     * 일반 강약만으로 설명하기 어려운
     * 특수 구조적 맥락이 함께 작동.
     */
    BASELINE_TENDENCIES_REQUIRE_CONTEXTUAL_ADJUSTMENT,

    /**
     * 자기 방식만 밀기보다
     * 지배적인 환경과 흐름을 읽는 것이 중요.
     */
    DOMINANT_CONTEXT_STRONGLY_SHAPES_DECISIONS,

    /**
     * 일반적인 강약과 다른 방식으로
     * 힘의 사용 방향이 재편될 수 있음.
     */
    STRUCTURAL_ALIGNMENT_CHANGES_HOW_STRENGTH_IS_EXPRESSED,

    /*
     * =================================================
     * 食傷 / 활동·회복
     * =================================================
     */

    /**
     * 외부 활동을 확대하기 전에
     * 회복 자원을 확보하는 것이 중요.
     */
    RECOVERY_BEFORE_OVEREXTENSION,

    /**
     * 표현·생산·결과 산출에
     * 자기 자원을 많이 사용할 수 있음.
     */
    OUTPUT_CAN_CONSUME_PERSONAL_RESOURCES,

    /**
     * 준비와 통제가 과해지면
     * 실제 표현이나 실행을 지나치게 누를 수 있음.
     */
    OVERCONTROL_CAN_SUPPRESS_EXPRESSION,

    /*
     * =================================================
     * 印
     * =================================================
     */

    /**
     * 지원·보호·준비가 지나치게 많아지면
     * 실제 행동이 늦어질 수 있음.
     */
    TOO_MUCH_SUPPORT_CAN_DELAY_ACTION,

    /**
     * 안정 기반과 현실적 성과를
     * 함께 운영하는 균형이 중요.
     */
    BALANCE_STABILITY_AND_MATERIAL_GOALS,

    /*
     * =================================================
     * 財
     * =================================================
     */

    /**
     * 현실적인 결과와 자원 확보가
     * 중요한 행동 동기로 작동하기 쉬움.
     */
    MATERIAL_RESULTS_ARE_IMPORTANT_MOTIVATORS,

    /**
     * 현실적 성과와 안정 기반이
     * 서로 충돌하지 않고 병행 가능.
     */
    MATERIAL_GOALS_AND_STABILITY_CAN_COEXIST,

    /**
     * 성과 압박이 지나치면
     * 안정과 회복 기반을 침범할 수 있음.
     */
    MATERIAL_PRESSURE_CAN_DISRUPT_STABILITY,

    /**
     * 구체적인 현실 목표가
     * 과도한 준비와 보호 구조를 적절히 움직일 수 있음.
     */
    PRACTICAL_GOALS_CAN_CORRECT_OVERPROTECTION,

    /**
     * 압박 요소를 줄이면서도
     * 현실적인 성과 구조를 보존할 수 있음.
     */
    PRESSURE_CAN_BE_REDUCED_WITHOUT_LOSING_PRACTICAL_GOALS,

    /**
     * 과도한 현실 압박이 감소하면
     * 안정과 회복 기반을 보존하기 쉬움.
     */
    STABILITY_CAN_BE_PRESERVED_WHEN_MATERIAL_PRESSURE_IS_REDUCED,

    /*
     * =================================================
     * 官殺 / 책임·경쟁
     * =================================================
     */

    /**
     * 여러 종류의 책임과 평가 기준이
     * 동시에 작동하면 혼선이 커질 수 있음.
     */
    COMPLEX_AUTHORITY_EXPECTATIONS_REQUIRE_CLARITY,

    /**
     * 복잡한 책임과 평가 압력을
     * 한 방향으로 정리할 수 있는 구조.
     */
    AUTHORITY_COMPLEXITY_CAN_BE_CHANNELLED,

    /**
     * 경쟁과 긴장을 추진력으로 사용하되
     * 방향 설정이 중요.
     */
    COMPETITIVE_DRIVE_NEEDS_DIRECTION,

    /*
     * =================================================
     * 陽刃
     * =================================================
     */

    /**
     * 독립성과 결단성이 강하게 나타날 수 있음.
     */
    DECISIVE_INDEPENDENT_DRIVE,

    /**
     * 강한 추진력이 명확한 책임과 규칙을 만날 때
     * 생산적으로 정리되기 쉬움.
     */
    STRONG_DRIVE_BENEFITS_FROM_CLEAR_RESPONSIBILITY,

    /*
     * =================================================
     * 天干合
     * =================================================
     */

    /**
     * 사람·역할·목표 사이의 강한 결속이
     * 자신의 우선순위를 바꾸는 요인이 될 수 있음.
     */
    STRONG_BINDING_DYNAMICS_CAN_REDIRECT_PRIORITIES,

    /*
     * =================================================
     * 月令 / 원국 안정성
     * =================================================
     */

    /**
     * 핵심 생활 패턴 안에
     * 충돌이나 변화 요인이 존재.
     */
    CORE_LIFE_PATTERN_HAS_INTERNAL_FRICTION,

    /**
     * 내부 충돌이 있어도
     * 이를 조정할 구조가 함께 존재.
     */
    INTERNAL_FRICTION_HAS_A_RESOLUTION_ROUTE,

    /*
     * =================================================
     * 格局
     * =================================================
     */

    /**
     * 원국 중심 구조가 비교적
     * 일관된 방향을 가짐.
     */
    LIFE_DIRECTION_HAS_A_COHERENT_STRUCTURE,

    /**
     * 중심 구조는 있으나
     * 관리해야 하는 결함이 함께 존재.
     */
    CORE_STRENGTH_COMES_WITH_A_MANAGEABLE_DEFECT,

    /**
     * 중심 구조의 충돌 때문에
     * 운영 방식을 적극적으로 재조정할 필요가 있음.
     */
    CORE_STRUCTURE_REQUIRES_ACTIVE_REORGANIZATION,

    /**
     * 구조적인 문제를
     * 다른 요소가 보완하는 구응 경로가 있음.
     */
    DIFFICULTY_CAN_BE_CONVERTED_THROUGH_COMPENSATION,

    /**
     * 하나의 방향으로 단정하기 어려운
     * 복수 구조가 존재.
     */
    MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,

    /**
     * 월령만으로 완성된 방향보다
     * 실제 선택하는 환경과 역할의 영향이 큼.
     */
    LIFE_DIRECTION_DEPENDS_MORE_ON_CHOSEN_STRUCTURE
}