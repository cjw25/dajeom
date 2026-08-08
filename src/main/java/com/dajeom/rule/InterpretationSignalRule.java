package com.dajeom.rule;

import com.dajeom.model.InterpretationEvidence;
import com.dajeom.model.InterpretationSignal;
import com.dajeom.type.BranchRelationType;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationIntensity;
import com.dajeom.type.InterpretationPolarity;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.TenGod;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Interpretation Signal V1 Rule.
 *
 * 이 Rule은 명리 계산 자체를 하지 않는다.
 *
 * 이미 계산된 명리 결과를:
 *
 * - 성향
 * - 재물
 * - 직업
 * - 관계
 * - 현재 흐름
 *
 * 등의 상담 분야에 연결하는 역할을 한다.
 *
 * 사용자 상담 문장 생성은 담당하지 않는다.
 */
public final class InterpretationSignalRule {

    public static final String RULE_VERSION =
            "DAJEOM_INTERPRETATION_SIGNAL_V1";

    private InterpretationSignalRule() {

        throw new IllegalStateException(
                "InterpretationSignalRule은 생성할 수 없습니다."
        );
    }

    /**
     * 십성이 영향을 주는 상담 분야와
     * 내부 가중치를 반환한다.
     *
     * 특정 십성을 무조건 길/흉으로 보지 않고
     * 지원과 주의 값을 동시에 사용할 수 있다.
     */
    public static List<CategoryWeight>
    weightsForTenGod(
            TenGod tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "십성은 필수입니다."
        );

        return switch (tenGod) {

            case BI_GYEON ->
                    List.of(
                            weight(
                                    ConsultationCategory
                                            .SOCIAL_RELATIONSHIP,
                                    0.4,
                                    0.3
                            ),
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.4,
                                    0.2
                            )
                    );

            case GEOP_JAE ->
                    List.of(
                            weight(
                                    ConsultationCategory
                                            .SOCIAL_RELATIONSHIP,
                                    0.3,
                                    0.7
                            ),
                            weight(
                                    ConsultationCategory.WEALTH,
                                    0.2,
                                    0.9
                            ),
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.3,
                                    0.5
                            )
                    );

            case SIK_SIN ->
                    List.of(
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.8,
                                    0.1
                            ),
                            weight(
                                    ConsultationCategory.WEALTH,
                                    0.7,
                                    0.1
                            ),
                            weight(
                                    ConsultationCategory.LOVE_STYLE,
                                    0.5,
                                    0.1
                            )
                    );

            case SANG_GWAN ->
                    List.of(
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.7,
                                    0.5
                            ),
                            weight(
                                    ConsultationCategory
                                            .SOCIAL_RELATIONSHIP,
                                    0.4,
                                    0.6
                            ),
                            weight(
                                    ConsultationCategory.LEGAL_RISK,
                                    0.1,
                                    0.8
                            )
                    );

            case PYEON_JAE ->
                    List.of(
                            weight(
                                    ConsultationCategory.WEALTH,
                                    0.8,
                                    0.3
                            ),
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.6,
                                    0.2
                            ),
                            weight(
                                    ConsultationCategory
                                            .SOCIAL_RELATIONSHIP,
                                    0.5,
                                    0.2
                            )
                    );

            case JEONG_JAE ->
                    List.of(
                            weight(
                                    ConsultationCategory.WEALTH,
                                    0.8,
                                    0.1
                            ),
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.6,
                                    0.1
                            )
                    );

            case PYEON_GWAN ->
                    List.of(
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.5,
                                    0.7
                            ),
                            weight(
                                    ConsultationCategory.LEGAL_RISK,
                                    0.2,
                                    0.8
                            )
                    );

            case JEONG_GWAN ->
                    List.of(
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.8,
                                    0.2
                            ),
                            weight(
                                    ConsultationCategory.LEGAL_RISK,
                                    0.3,
                                    0.3
                            )
                    );

            case PYEON_IN ->
                    List.of(
                            weight(
                                    ConsultationCategory.PERSONALITY,
                                    0.4,
                                    0.3
                            ),
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.4,
                                    0.2
                            )
                    );

            case JEONG_IN ->
                    List.of(
                            weight(
                                    ConsultationCategory.PERSONALITY,
                                    0.5,
                                    0.1
                            ),
                            weight(
                                    ConsultationCategory.CAREER,
                                    0.5,
                                    0.1
                            )
                    );
        };
    }

    /**
     * 원국의 어느 위치에서 관계가 발생했는지에 따라
     * 상담 분야를 연결한다.
     *
     * YEAR:
     * 지나온 흐름 / 주변관계
     *
     * MONTH:
     * 사회생활 / 직업 / 대인관계
     *
     * DAY:
     * 연애 / 인연
     *
     * HOUR:
     * 앞으로의 흐름
     */
    public static List<ConsultationCategory>
    categoriesForPositions(
            List<PillarPosition> positions
    ) {

        Objects.requireNonNull(
                positions,
                "기둥 위치 목록은 필수입니다."
        );

        Set<ConsultationCategory> result =
                new LinkedHashSet<>();

        for (
                PillarPosition position
                : positions
        ) {

            switch (position) {

                case YEAR -> {

                    result.add(
                            ConsultationCategory.PAST_LUCK
                    );

                    result.add(
                            ConsultationCategory
                                    .SOCIAL_RELATIONSHIP
                    );
                }

                case MONTH -> {

                    result.add(
                            ConsultationCategory.CAREER
                    );

                    result.add(
                            ConsultationCategory
                                    .SOCIAL_RELATIONSHIP
                    );
                }

                case DAY -> {

                    result.add(
                            ConsultationCategory.LOVE_STYLE
                    );

                    result.add(
                            ConsultationCategory.PARTNER_LUCK
                    );
                }

                case HOUR ->

                        result.add(
                                ConsultationCategory.FUTURE_LUCK
                        );
            }
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 지지 관계 자체의 내부 가중치.
     *
     * 합도 무조건 좋다고 판단하지 않고,
     * 충도 사건 발생을 확정하지 않는다.
     */
    public static RelationWeight relationWeight(
            BranchRelationType type
    ) {

        Objects.requireNonNull(
                type,
                "지지 관계는 필수입니다."
        );

        return switch (type) {

            case SIX_COMBINATION ->
                    new RelationWeight(
                            0.6,
                            0.2
                    );

            case THREE_HARMONY ->
                    new RelationWeight(
                            0.8,
                            0.2
                    );

            case CLASH ->
                    new RelationWeight(
                            0.2,
                            1.0
                    );

            case PUNISHMENT ->
                    new RelationWeight(
                            0.1,
                            0.9
                    );

            case SELF_PUNISHMENT ->
                    new RelationWeight(
                            0.1,
                            0.9
                    );

            case HARM ->
                    new RelationWeight(
                            0.1,
                            0.8
                    );

            case BREAK ->
                    new RelationWeight(
                            0.1,
                            0.7
                    );
        };
    }

    /**
     * 분야별 Evidence를 하나의 Signal로 합산한다.
     */
    public static InterpretationSignal createSignal(
            ConsultationCategory category,
            List<InterpretationEvidence> evidences
    ) {

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        Objects.requireNonNull(
                evidences,
                "근거 목록은 필수입니다."
        );

        List<InterpretationEvidence> filtered =
                evidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence.category()
                                                == category
                        )
                        .toList();

        double supportScore =
                filtered
                        .stream()
                        .mapToDouble(
                                InterpretationEvidence
                                        ::supportWeight
                        )
                        .sum();

        double cautionScore =
                filtered
                        .stream()
                        .mapToDouble(
                                InterpretationEvidence
                                        ::cautionWeight
                        )
                        .sum();

        InterpretationPolarity polarity =
                classifyPolarity(
                        supportScore,
                        cautionScore
                );

        InterpretationIntensity intensity =
                classifyIntensity(
                        supportScore,
                        cautionScore
                );

        return new InterpretationSignal(
                category,
                supportScore,
                cautionScore,
                polarity,
                intensity,
                filtered
        );
    }

    public static InterpretationPolarity
    classifyPolarity(
            double supportScore,
            double cautionScore
    ) {

        if (
                supportScore <= 0.0
                        && cautionScore <= 0.0
        ) {

            return InterpretationPolarity.NEUTRAL;
        }

        /*
         * 두 방향이 모두 의미 있게 존재하면서
         * 차이가 크지 않다면 복합 신호로 본다.
         */
        if (
                supportScore > 0.0
                        && cautionScore > 0.0
                        && Math.abs(
                        supportScore
                                - cautionScore
                ) < 0.75
        ) {

            return InterpretationPolarity.MIXED;
        }

        if (
                supportScore
                        > cautionScore
        ) {

            return InterpretationPolarity.SUPPORTIVE;
        }

        if (
                cautionScore
                        > supportScore
        ) {

            return InterpretationPolarity.CAUTION;
        }

        return InterpretationPolarity.MIXED;
    }

    public static InterpretationIntensity
    classifyIntensity(
            double supportScore,
            double cautionScore
    ) {

        double strongest =
                Math.max(
                        supportScore,
                        cautionScore
                );

        if (strongest <= 0.0) {

            return InterpretationIntensity.NONE;
        }

        if (strongest < 1.5) {

            return InterpretationIntensity.LOW;
        }

        if (strongest < 3.0) {

            return InterpretationIntensity.MEDIUM;
        }

        return InterpretationIntensity.HIGH;
    }

    private static CategoryWeight weight(
            ConsultationCategory category,
            double supportWeight,
            double cautionWeight
    ) {

        return new CategoryWeight(
                category,
                supportWeight,
                cautionWeight
        );
    }

    public record CategoryWeight(

            ConsultationCategory category,

            double supportWeight,

            double cautionWeight

    ) {

        public CategoryWeight {

            Objects.requireNonNull(
                    category,
                    "상담 분야는 필수입니다."
            );

            if (
                    supportWeight < 0.0
                            || cautionWeight < 0.0
            ) {

                throw new IllegalArgumentException(
                        "가중치는 음수일 수 없습니다."
                );
            }
        }
    }

    public record RelationWeight(

            double supportWeight,

            double cautionWeight

    ) {

        public RelationWeight {

            if (
                    supportWeight < 0.0
                            || cautionWeight < 0.0
            ) {

                throw new IllegalArgumentException(
                        "관계 가중치는 음수일 수 없습니다."
                );
            }
        }
    }
}