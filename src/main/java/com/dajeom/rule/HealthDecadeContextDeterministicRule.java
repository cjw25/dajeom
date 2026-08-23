package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * HEALTH_TENDENCY 현재 대운 Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.HEALTH_LIFESTYLE에서 이미 계산된:
 *
 * HEALTH_DECADE_CONTEXT
 *
 * 결과를 ConsultationCategory.HEALTH_TENDENCY의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * HealthDecadeContextRule은
 * DecadeLuckPeriodContextAnalysis의:
 *
 * DecadeLuckContextAxis.USEFUL_GOD
 *
 * Signal을 사용한다.
 *
 * 그리고 upstream Java에서 이미:
 *
 * SUPPORTED
 * QUALIFIED
 * WEAKENED
 * CONFLICTED
 *
 * Signal들을 평가하여 최종:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * LifeDomainSignalStatus를 계산한다.
 *
 * 이 Category Rule에서는
 * 해당 상태를 다시 계산하지 않는다.
 *
 * =================================================
 * 실제 upstream 합성 보존
 * =================================================
 *
 * HealthDecadeContextRule 내부의:
 *
 * CONFLICTED 존재
 * -> MIXED
 *
 * SUPPORT 계열 + WEAKENED
 * -> MIXED
 *
 * WEAKENED
 * -> PRESSURE
 *
 * SUPPORTED
 * -> SUPPORTIVE
 *
 * QUALIFIED only
 * -> CONTEXTUAL
 *
 * 계산은 실제 하나의
 * DecadeLuckPeriodContextAnalysis 안에서
 * Java가 명시적으로 수행한 공식 계산이다.
 *
 * 따라서 그대로 보존한다.
 *
 * =================================================
 * Category 단계 경계
 * =================================================
 *
 * 이 Rule에서는 다음을 하지 않는다.
 *
 * HEALTH_DECADE_CONTEXT
 * +
 * HEALTH_ANNUAL_CONTEXT
 *
 * HEALTH_DECADE_CONTEXT
 * +
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * HEALTH_DECADE_CONTEXT
 * +
 * 원국 HEALTH Match
 *
 * 등의 Status 합성.
 *
 * =================================================
 * Status
 * =================================================
 *
 * LifeDomainRuleMatch의 공식 Status를
 * Category Status로 1:1 변환한다.
 *
 * SUPPORTIVE
 * -> SUPPORTIVE
 *
 * PRESSURE
 * -> PRESSURE
 *
 * MIXED
 * -> MIXED
 *
 * CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * 의미 경계
 * =================================================
 *
 * 여기서 SUPPORTIVE / PRESSURE / MIXED는
 * 명리적인 생활균형 조정축의 상태다.
 *
 * 의료적:
 *
 * - 건강
 * - 질병
 * - 발병
 * - 회복
 * - 치료
 *
 * 상태가 아니다.
 *
 * =================================================
 * 의료 변환 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 건강이 좋아짐
 * != 회복
 * != 치유
 *
 * PRESSURE
 * != 질병
 * != 발병
 * != 악화
 *
 * MIXED
 * != 건강 불안정
 * != 만성질환
 *
 * CONTEXTUAL
 * != 건강함
 * != 질병 없음
 *
 * =================================================
 * 조후·병약·통관 재생성 금지
 * =================================================
 *
 * DecadeLuckContextAxis에는:
 *
 * CLIMATE
 * PATHOLOGY
 * REMEDY
 * PASSAGE
 *
 * 전용 Axis가 없다.
 *
 * 따라서 Category에서도
 * 대운에서:
 *
 * - 조후
 * - 명리적 병약
 * - 통관
 *
 * 을 새로 계산하거나
 * 존재한다고 추론하지 않는다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * HEALTH_DECADE_CONTEXT
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * HEALTH_CURRENT_DECADE_CONTEXT
 * Category Match 하나
 *
 * 로 보존한다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * 다음을 새로 만들지 않는다.
 *
 * - 대운 용신 상태 때문에 건강 부담이 생긴다
 * - 대운이 원국 건강 구조를 강화한다
 * - 대운이 병약을 해소한다
 * - 대운이 세운보다 먼저 작용한다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * Timeline Segment가 여러 개 가능하면
 * 각 Segment의 실제 HEALTH_DECADE_CONTEXT를
 * 각각 보존한다.
 *
 * 하나를 대표 대운으로 임의 선택하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - DecadeLuckPeriodContextAnalysis 재계산
 * - USEFUL_GOD Signal 재계산
 * - upstream Status 재계산
 * - 다른 HEALTH Source와 Status 합성
 * - Annual Context와 합성
 * - Annual Functional과 합성
 * - Monthly Functional과 합성
 * - 원국 HEALTH 결과와 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 오행-장기 연결
 * - 특정 질환 생성
 * - 발병 예측
 * - 회복 예측
 * - 수명 예측
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class HealthDecadeContextDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "HEALTH_CURRENT_DECADE_CONTEXT";

    private static final String
            HEALTH_DECADE_CONTEXT_RULE_CODE =
            "HEALTH_DECADE_CONTEXT";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.HEALTH_TENDENCY;
    }


    @Override
    public String code() {

        return RULE_CODE;
    }


    @Override
    public List<ConsultationCategoryDeterministicMatch>
    evaluate(
            ConsultationCategoryEvidenceView view
    ) {

        Objects.requireNonNull(
                view,
                "HealthDecadeContextDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.HEALTH_TENDENCY
        ) {

            throw new IllegalArgumentException(
                    "HealthDecadeContextDeterministicRule은 HEALTH_TENDENCY View만 평가할 수 있습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Current Timeline
         * =================================================
         */

        if (
                !view.hasSegmentEvidences()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        /*
         * =================================================
         * Segment별 독립 처리
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "HEALTH Decade Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 공식 Source
             * =================================================
             */

            List<LifeDomainRuleMatch> decadeMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            HEALTH_DECADE_CONTEXT_RULE_CODE
                    );

            if (
                    decadeMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Match 1개 -> Category Match 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch decadeMatch
                    : decadeMatches
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                decadeMatch,
                                "HEALTH_DECADE_CONTEXT Match는 null일 수 없습니다."
                        );

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.HEALTH_TENDENCY,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                status,

                                normalizedSegment.sajuYear(),

                                normalizedSegment.monthIndex(),

                                List.of(),

                                summaryOf(
                                        status
                                ),

                                List.of(
                                        normalizedMatch
                                ),

                                buildReasons(
                                        normalizedSegment,
                                        normalizedMatch
                                )
                        )
                );
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Segment Source 조회
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    findSegmentMatches(

            ConsultationCategorySegmentEvidence segment,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                segment,
                "ConsultationCategorySegmentEvidence가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "HEALTH Decade Context sourceRuleCode가 필요합니다."
                );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : segment.matches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "HEALTH Segment Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.HEALTH_LIFESTYLE
            ) {

                continue;
            }

            if (
                    !normalizedRuleCode.equals(
                            normalized.ruleCode()
                    )
            ) {

                continue;
            }

            result.add(
                    normalized
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "HEALTH Decade Context에는 LifeDomainSignalStatus가 필요합니다."
        );

        return switch (
                sourceStatus
                ) {

            case SUPPORTIVE ->
                    ConsultationCategoryDeterministicStatus
                            .SUPPORTIVE;

            case PRESSURE ->
                    ConsultationCategoryDeterministicStatus
                            .PRESSURE;

            case MIXED ->
                    ConsultationCategoryDeterministicStatus
                            .MIXED;

            case CONTEXTUAL ->
                    ConsultationCategoryDeterministicStatus
                            .CONTEXTUAL;
        };
    }


    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            ConsultationCategorySegmentEvidence segment,

            LifeDomainRuleMatch sourceMatch

    ) {

        Objects.requireNonNull(
                segment,
                "HEALTH Decade Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "HEALTH Decade Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_DECADE_CONTEXT에서 이미 계산된 하나의 HEALTH_LIFESTYLE LifeDomain Match를 HEALTH_TENDENCY의 독립 대운 Context로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 HEALTH_DECADE_CONTEXT의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 HEALTH Source와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "HealthDecadeContextRule이 DecadeLuckPeriodContextAnalysis의 USEFUL_GOD Axis Signal을 이용해 이미 계산한 SUPPORTED·QUALIFIED·WEAKENED·CONFLICTED 상태와 최종 Status를 다시 계산하지 않았습니다."
        );

        reasons.add(
                "upstream HealthDecadeContextRule 내부에서 실제 Signal 조합을 확인하여 생성한 MIXED 상태는 공식 Java 계산 결과이므로 그대로 보존합니다."
        );

        reasons.add(
                "HEALTH_ANNUAL_CONTEXT, HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL, HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL 또는 원국 HEALTH Match를 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "DecadeLuckContextAxis에 조후·병약·통관 전용 Axis가 없으므로 대운에서 해당 상태를 새로 생성하지 않았습니다."
        );

        reasons.add(
                "이 Status는 명리적 생활균형 조정축의 대운 상태이며 실제 질병·발병·악화·회복·치료 상태로 변환하지 않습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 용신 오행, 조후, 병약, 통관 또는 의료적 의미를 재구성하지 않았습니다."
        );

        reasons.add(
                "다른 HEALTH Evidence와 함께 존재한다는 사실만으로 새로운 강화·약화·보완·인과·순서 또는 종합 Status를 생성하지 않았습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 HEALTH 대운 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 다른 가능한 Segment가 존재하면 각각의 HEALTH_DECADE_CONTEXT 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.beforeFirstDecadeLuck()
        ) {

            reasons.add(
                    "현재 Segment가 첫 대운 시작 전이라면 실제 HEALTH_DECADE_CONTEXT Match가 존재하는 경우에만 Category Match를 생성합니다."
            );
        }

        if (
                segment.afterGeneratedDecadeRange()
        ) {

            reasons.add(
                    "현재 Segment가 생성된 대운 범위 이후라면 실제 HEALTH_DECADE_CONTEXT Match가 존재하는 경우에만 결과를 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태라면 Annual 또는 Monthly HEALTH 결과를 대운 대신 사용하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 대운 자체를 세운 또는 월운 계산으로 재해석하지 않습니다."
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Internal Summary
     * =================================================
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "HEALTH Decade Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운의 명리적 생활균형 조정 Context가 SUPPORTIVE 상태로 계산되었으며 의료적 건강 상태로 확대하지 않습니다.";

            case PRESSURE ->
                    "현재 대운의 명리적 생활균형 조정 Context가 PRESSURE 상태로 계산되었으며 질병이나 발병 상태를 의미하지 않습니다.";

            case MIXED ->
                    "현재 대운의 명리적 생활균형 조정 Context 자체에서 MIXED 상태가 계산되었으며 해당 upstream 결과를 그대로 보존합니다.";

            case CONTEXTUAL ->
                    "현재 대운의 명리적 생활균형 조정 Context가 CONTEXTUAL 상태로 남아 있으며 다른 HEALTH 결과와 합성하지 않습니다.";
        };
    }


    /*
     * =================================================
     * Text
     * =================================================
     */

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}