package com.dajeom.constant;

import com.dajeom.model.NatalPersonalityFormalPatternMeaningPolicy;
import com.dajeom.type.TenGodCorePatternType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 전통 십성 Formal Pattern의
 * 사용자 Meaning Policy Registry.
 *
 * =================================================
 * 등록 원칙
 * =================================================
 *
 * TenGodCorePatternType에 이미 존재하는
 * 11개 Pattern만 등록한다.
 *
 * 새로운 조합 Pattern을 추가하지 않는다.
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * Pattern이 성립했다는 것과
 * 현실 사건이 확정된다는 것은 다르다.
 *
 * 따라서:
 *
 * 식신제살 = 무조건 성공
 * 상관견관 = 상사와 충돌
 * 재생관 = 승진
 * 비겁쟁재 = 돈을 잃음
 * 관살혼재 = 이성관계 문제
 *
 * 같은 자동 사건화를 금지한다.
 *
 * =================================================
 * 사용자 출력 경계
 * =================================================
 *
 * userMeaning은 AI가 없어도 그대로
 * deterministic fallback 문장으로 사용할 수 있어야 한다.
 *
 * 따라서 userMeaning 자체에는:
 *
 * - 십성 전문용어
 * - 신강·신약 전문용어
 * - 일간 같은 내부 명리 용어
 *
 * 를 직접 노출하지 않는다.
 *
 * 정확한 Pattern Type과 provenance는
 * Java 내부 Contract에 그대로 보존한다.
 */
public final class NatalPersonalityFormalPatternMeaningPolicyRegistry {

    private static final Map<
            TenGodCorePatternType,
            NatalPersonalityFormalPatternMeaningPolicy
            > POLICIES =
            createPolicies();

    private NatalPersonalityFormalPatternMeaningPolicyRegistry() {
    }

    public static NatalPersonalityFormalPatternMeaningPolicy require(
            TenGodCorePatternType patternType
    ) {

        Objects.requireNonNull(
                patternType,
                "Formal Pattern Type은 필수입니다."
        );

        NatalPersonalityFormalPatternMeaningPolicy policy =
                POLICIES.get(
                        patternType
                );

        if (
                policy == null
        ) {

            throw new IllegalArgumentException(
                    "등록되지 않은 Formal Pattern Meaning Policy입니다: "
                            + patternType
            );
        }

        return policy;
    }

    public static List<NatalPersonalityFormalPatternMeaningPolicy> all() {

        return List.copyOf(
                POLICIES.values()
        );
    }

    private static Map<
            TenGodCorePatternType,
            NatalPersonalityFormalPatternMeaningPolicy
            >
    createPolicies() {

        EnumMap<
                TenGodCorePatternType,
                NatalPersonalityFormalPatternMeaningPolicy
                > result =
                new EnumMap<>(
                        TenGodCorePatternType.class
                );

        /*
         * =================================================
         * 식신제살
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.EATING_GOD_CONTROLS_SEVEN_KILLINGS,
                        "강한 압박이나 요구를 표현과 실행, 산출의 작용으로 제어하는 구조",
                        List.of(
                                "압박이 있으면 반드시 성공한다고 확대하지 말 것.",
                                "권력직이나 특정 직업에 적합하다고 자동 연결하지 말 것.",
                                "사고, 폭력, 처벌 같은 현실 사건으로 확대하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 상관견관
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.HURTING_OFFICER_MEETS_DIRECT_OFFICER,
                        "강한 자기 표현의 작용과 규칙·역할·외부 기준의 요구가 서로 부딪히는 구조",
                        List.of(
                                "상사와 반드시 충돌한다고 단정하지 말 것.",
                                "퇴사, 해고, 소송, 처벌로 자동 사건화하지 말 것.",
                                "결혼이나 배우자 문제로 자동 확대하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 상관패인
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.HURTING_OFFICER_WITH_RESOURCE,
                        "강한 표현과 판단의 작용을 이해·학습·내부 정리의 작용이 조절하는 구조",
                        List.of(
                                "천재성이나 높은 학력을 보장한다고 확대하지 말 것.",
                                "시험 합격이나 자격 취득을 자동 예측하지 말 것.",
                                "표현력이 항상 좋은 방향으로 작동한다고 단정하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 편인도식 / 효신탈식
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.INDIRECT_RESOURCE_SEIZES_EATING_GOD,
                        "비정형적인 해석과 내부 검토의 작용이 자연스러운 산출과 표현을 억제하는 구조",
                        List.of(
                                "질병이나 정신질환으로 확대하지 말 것.",
                                "음식 문제나 자녀 문제를 자동 사건화하지 말 것.",
                                "창작이나 생산 능력이 없다고 단정하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 재생관
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.WEALTH_GENERATES_DIRECT_OFFICER,
                        "현실 자원과 결과의 작용이 역할·책임·규범의 작동을 뒷받침하는 구조",
                        List.of(
                                "돈이 많아진다고 자동 해석하지 말 것.",
                                "취업이나 승진을 자동 사건화하지 말 것.",
                                "결혼이나 좋은 배우자를 자동 예측하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 관인상생
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.DIRECT_OFFICER_GENERATES_RESOURCE,
                        "역할과 책임, 외부 기준의 작용이 이해·학습·수용의 작동으로 이어지는 구조",
                        List.of(
                                "공무원이나 조직생활에 반드시 적합하다고 단정하지 말 것.",
                                "승진이나 시험 합격을 자동 예측하지 말 것.",
                                "학력이나 후원자 운으로 자동 확대하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 살인상생
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.SEVEN_KILLINGS_GENERATES_RESOURCE,
                        "강한 압박과 요구의 작용이 이해·학습·내부 정리의 작동으로 연결되는 구조",
                        List.of(
                                "위기에서 반드시 성공한다고 단정하지 말 것.",
                                "군인, 경찰, 권력직 같은 특정 직업으로 자동 연결하지 말 것.",
                                "사고나 위험 사건으로 자동 확대하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 재극인
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.WEALTH_CONTROLS_RESOURCE,
                        "현실성과 실제 결과의 요구가 내부 이해·검토·수용의 작동을 제어하는 구조",
                        List.of(
                                "학업 실패를 자동 예측하지 말 것.",
                                "부모와의 갈등을 자동 사건화하지 말 것.",
                                "재산이나 건강 문제로 자동 확대하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 비겁쟁재
         * =================================================
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.COMPANION_CONTESTS_WEALTH,
                        "자기 몫을 확보하고 경쟁하는 작용과 현실 자원의 배분이 서로 경합하는 구조",
                        List.of(
                                "돈을 반드시 잃는다고 단정하지 말 것.",
                                "친구나 동업자의 배신을 자동 예측하지 말 것.",
                                "이혼이나 관계 단절로 자동 확대하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 재다신약
         * =================================================
         *
         * 내부 Pattern은 그대로
         * WEALTH_HEAVY_WEAK_DAY_MASTER를 보존한다.
         *
         * 단 사용자 Meaning에서는
         * "일간" 같은 내부 명리 용어를 노출하지 않는다.
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.WEALTH_HEAVY_WEAK_DAY_MASTER,
                        "현실 자원과 성과를 감당해야 하는 요구가 기본적인 자기 수용 여력에 비해 과중한 구조",
                        List.of(
                                "가난이나 채무를 자동 예측하지 말 것.",
                                "사업 실패나 재산 손실을 자동 사건화하지 말 것.",
                                "과로, 질병, 번아웃을 확정하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * 관살혼재
         * =================================================
         *
         * 내부 Pattern은 정확히
         * 정관 + 편관/칠살 혼재를 보존한다.
         *
         * 사용자 Meaning에서는 십성 명칭을 직접 노출하지 않고
         * 두 요구 방식의 기능적 차이만 표현한다.
         */

        register(
                result,
                new NatalPersonalityFormalPatternMeaningPolicy(
                        TenGodCorePatternType.MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS,
                        "책임과 외부 기준의 영역 안에서 안정적인 규범 요구와 강한 압박 요구가 함께 작동하는 구조",
                        List.of(
                                "이성이 두 명 생긴다는 식으로 해석하지 말 것.",
                                "외도나 삼각관계를 자동 예측하지 말 것.",
                                "직장 이동, 법적 문제, 권력 충돌을 자동 사건화하지 말 것."
                        )
                )
        );

        /*
         * =================================================
         * Registry 완전성
         * =================================================
         */

        if (
                result.size()
                        != TenGodCorePatternType.values().length
        ) {

            throw new IllegalStateException(
                    "Formal Pattern Meaning Policy가 "
                            + "TenGodCorePatternType 전체를 등록하지 않았습니다. expected="
                            + TenGodCorePatternType.values().length
                            + ", actual="
                            + result.size()
            );
        }

        return Map.copyOf(
                result
        );
    }

    private static void register(

            Map<
                    TenGodCorePatternType,
                    NatalPersonalityFormalPatternMeaningPolicy
                    > target,

            NatalPersonalityFormalPatternMeaningPolicy policy

    ) {

        NatalPersonalityFormalPatternMeaningPolicy previous =
                target.put(
                        policy.patternType(),
                        policy
                );

        if (
                previous != null
        ) {

            throw new IllegalStateException(
                    "Formal Pattern Meaning Policy가 중복 등록되었습니다: "
                            + policy.patternType()
            );
        }
    }
}