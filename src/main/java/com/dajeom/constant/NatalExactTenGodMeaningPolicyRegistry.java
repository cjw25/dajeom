package com.dajeom.constant;

import com.dajeom.model.NatalExactTenGodMeaningPolicy;
import com.dajeom.type.StructureTenGodType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정확한 10십성 사용자 Meaning Registry.
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * 비견과 겁재를 합치지 않는다.
 * 식신과 상관을 합치지 않는다.
 * 정재와 편재를 합치지 않는다.
 * 정관과 편관을 합치지 않는다.
 * 정인과 편인을 합치지 않는다.
 *
 * 각각 독립적으로 해석한다.
 *
 * 또한 십성 하나만으로:
 *
 * - 결혼
 * - 이별
 * - 승진
 * - 퇴사
 * - 큰돈
 * - 질병
 * - 사고
 *
 * 같은 사건을 만들지 않는다.
 */
public final class NatalExactTenGodMeaningPolicyRegistry {

    private static final Map<
            StructureTenGodType,
            NatalExactTenGodMeaningPolicy
            > POLICIES =
            createPolicies();

    private NatalExactTenGodMeaningPolicyRegistry() {
    }

    public static NatalExactTenGodMeaningPolicy require(
            StructureTenGodType tenGodType
    ) {

        Objects.requireNonNull(
                tenGodType,
                "정확한 십성 종류는 필수입니다."
        );

        NatalExactTenGodMeaningPolicy policy =
                POLICIES.get(
                        tenGodType
                );

        if (
                policy == null
        ) {

            throw new IllegalStateException(
                    "Exact TenGod Meaning Policy가 없습니다: "
                            + tenGodType
            );
        }

        return policy;
    }

    public static Map<
            StructureTenGodType,
            NatalExactTenGodMeaningPolicy
            >
    all() {

        return POLICIES;
    }

    private static Map<
            StructureTenGodType,
            NatalExactTenGodMeaningPolicy
            >
    createPolicies() {

        Map<
                StructureTenGodType,
                NatalExactTenGodMeaningPolicy
                > result =
                new EnumMap<>(
                        StructureTenGodType.class
                );

        /*
         * =================================================
         * 비견
         * =================================================
         */

        put(
                result,

                StructureTenGodType.COMPANION,

                "자기 기준과 독립성을 유지하면서 자신의 몫과 판단을 스스로 지키려는 경향",

                List.of(
                        "비슷한 위치의 사람과도 지나치게 기대기보다 대등하게 서려는 태도",
                        "남이 정해 주기보다 자신이 직접 선택하고 결정할 때 편한 쪽"
                ),

                List.of(
                        "형제자매 문제나 재산 다툼이 생긴다고 단정하지 말 것.",
                        "사람과 반드시 경쟁하거나 관계가 끊어진다고 단정하지 말 것."
                )
        );

        /*
         * =================================================
         * 겁재
         * =================================================
         */

        put(
                result,

                StructureTenGodType.ROB_WEALTH,

                "경쟁이나 몫의 배분이 걸린 상황에서 자기 몫을 적극적으로 확보하고 주도적으로 움직이려는 경향",

                List.of(
                        "공동의 기회나 자원을 나누는 상황에서 상대의 움직임을 빠르게 의식하는 편",
                        "필요하다고 판단하면 망설이기보다 먼저 움직여 판을 잡으려는 쪽"
                ),

                List.of(
                        "친구나 동료에게 돈을 빼앗기거나 배신당한다고 단정하지 말 것.",
                        "투자 손실이나 재산 손실을 자동으로 연결하지 말 것."
                )
        );

        /*
         * =================================================
         * 식신
         * =================================================
         */

        put(
                result,

                StructureTenGodType.EATING_GOD,

                "자신의 생각과 능력을 꾸준히 결과물로 만들어 밖으로 드러내려는 경향",

                List.of(
                        "급하게 밀어붙이기보다 익숙한 방식으로 일정하게 만들어 내는 쪽",
                        "말이나 행동뿐 아니라 실제 결과물로 표현이 이어질 때 편한 편"
                ),

                List.of(
                        "먹을복이나 자녀복이 좋다고 단정하지 말 것.",
                        "창작이나 사업이 반드시 성공한다고 사건화하지 말 것."
                )
        );

        /*
         * =================================================
         * 상관
         * =================================================
         */

        put(
                result,

                StructureTenGodType.HURTING_OFFICER,

                "자기 판단과 표현을 분명하게 드러내고 기존 기준의 답답함이나 문제점을 짚으려는 경향",

                List.of(
                        "형식 자체보다 실제로 납득되는지와 결과가 맞는지를 중요하게 보는 쪽",
                        "제약이 강하다고 느낄수록 자신의 의견이나 표현이 더 분명해질 수 있는 편"
                ),

                List.of(
                        "직장 상사와 반드시 충돌하거나 퇴사한다고 단정하지 말 것.",
                        "말실수, 관재, 이별 같은 사건을 자동으로 연결하지 말 것."
                )
        );

        /*
         * =================================================
         * 편재
         * =================================================
         */

        put(
                result,

                StructureTenGodType.INDIRECT_WEALTH,

                "여러 현실 기회와 자원의 흐름을 넓게 보고 상황 변화에 빠르게 대응하려는 경향",

                List.of(
                        "한 가지 방식에만 고정되기보다 여러 가능성을 함께 살피는 편",
                        "사람, 정보, 자원 사이의 흐름을 빠르게 파악해 활용하려는 쪽"
                ),

                List.of(
                        "큰돈을 벌거나 사업이 성공한다고 단정하지 말 것.",
                        "연애가 많거나 바람기가 있다고 자동으로 연결하지 말 것."
                )
        );

        /*
         * =================================================
         * 정재
         * =================================================
         */

        put(
                result,

                StructureTenGodType.DIRECT_WEALTH,

                "현실 자원과 결과를 안정적으로 관리하고 예측 가능한 방식으로 쌓고 유지하려는 경향",

                List.of(
                        "돈, 시간, 물건처럼 구체적으로 관리할 수 있는 자원을 계획적으로 다루려는 편",
                        "불확실성을 줄이고 지속 가능한 방식으로 유지되는지를 중요하게 보는 쪽"
                ),

                List.of(
                        "부자가 되거나 월급이 안정된다고 단정하지 말 것.",
                        "배우자운이나 결혼 사건을 자동으로 연결하지 말 것."
                )
        );

        /*
         * =================================================
         * 편관 / 칠살
         * =================================================
         */

        put(
                result,

                StructureTenGodType.SEVEN_KILLINGS,

                "압박과 경쟁, 강한 요구가 주어졌을 때 긴장을 감수하고 빠르게 대응하거나 결단하려는 경향",

                List.of(
                        "상황의 기준이 분명해지면 행동과 결정을 빠르게 가져가려는 쪽",
                        "강한 외부 요구나 긴장 변화에 민감하게 반응하는 편"
                ),

                List.of(
                        "폭력성, 사고, 질병, 처벌을 자동으로 연결하지 말 것.",
                        "군인, 경찰, 권력 직업을 자동으로 추천하거나 단정하지 말 것."
                )
        );

        /*
         * =================================================
         * 정관
         * =================================================
         */

        put(
                result,

                StructureTenGodType.DIRECT_OFFICER,

                "역할과 규칙, 책임의 기준을 의식하고 정해진 질서 안에서 자신을 조절하려는 경향",

                List.of(
                        "기준과 절차가 분명할수록 자신의 역할을 안정적으로 수행하려는 편",
                        "약속이나 책임의 범위를 명확하게 두고 지키는 것을 중요하게 보는 쪽"
                ),

                List.of(
                        "공무원이나 조직생활에 반드시 적합하다고 단정하지 말 것.",
                        "승진, 취업, 좋은 배우자 같은 사건을 자동으로 연결하지 말 것."
                )
        );

        /*
         * =================================================
         * 편인
         * =================================================
         */

        put(
                result,

                StructureTenGodType.INDIRECT_RESOURCE,

                "익숙한 방식에만 기대기보다 다른 관점이나 비정형 정보를 받아들여 자기 방식으로 해석하려는 경향",

                List.of(
                        "낯선 정보나 서로 다른 단서를 독자적으로 연결해 보는 편",
                        "반복된 방식보다 자신이 납득한 별도의 방법을 찾으려는 쪽"
                ),

                List.of(
                        "천재성이나 영적 능력이 있다고 단정하지 말 것.",
                        "의심이 많거나 인간관계가 고립된다고 자동으로 연결하지 말 것."
                )
        );

        /*
         * =================================================
         * 정인
         * =================================================
         */

        put(
                result,

                StructureTenGodType.DIRECT_RESOURCE,

                "정보와 경험을 차분히 받아들이고 이해와 학습을 통해 안정된 판단 틀을 만들려는 경향",

                List.of(
                        "충분히 이해하고 정리한 다음 움직일 때 안정감을 느끼는 편",
                        "지식이나 경험을 축적해 판단의 근거를 만들어 가는 쪽"
                ),

                List.of(
                        "학벌이 좋거나 시험에 합격한다고 단정하지 말 것.",
                        "부모복이나 보호를 반드시 많이 받는다고 사건화하지 말 것."
                )
        );

        if (
                result.size()
                        != StructureTenGodType.values().length
        ) {

            throw new IllegalStateException(
                    "정확한 10십성 Meaning Policy가 모두 등록되지 않았습니다."
            );
        }

        return Map.copyOf(
                result
        );
    }

    private static void put(

            Map<
                    StructureTenGodType,
                    NatalExactTenGodMeaningPolicy
                    > target,

            StructureTenGodType tenGodType,

            String coreMeaning,

            List<String> allowedDetails,

            List<String> forbiddenExtensions

    ) {

        NatalExactTenGodMeaningPolicy previous =
                target.put(
                        tenGodType,

                        new NatalExactTenGodMeaningPolicy(
                                tenGodType,
                                coreMeaning,
                                allowedDetails,
                                forbiddenExtensions
                        )
                );

        if (
                previous != null
        ) {

            throw new IllegalStateException(
                    "Exact TenGod Meaning Policy가 중복되었습니다: "
                            + tenGodType
            );
        }
    }
}