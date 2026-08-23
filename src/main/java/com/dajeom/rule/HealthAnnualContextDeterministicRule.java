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
 * HEALTH_TENDENCY 현재 세운 Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.HEALTH_LIFESTYLE에서 이미 계산된:
 *
 * HEALTH_ANNUAL_CONTEXT
 *
 * 결과를 ConsultationCategory.HEALTH_TENDENCY의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * HealthAnnualContextRule은
 * 14-6 AnnualLuckContextAnalysis의:
 *
 * AnnualLuckContextAxis.USEFUL_GOD
 *
 * Signal을 사용한다.
 *
 * 해당 Signal은 이미 upstream Java에서:
 *
 * - 원국 용신·희신·기신 판단
 * - 현재 세운
 * - 가능한 경우 현재 대운 Segment Context
 *
 * 등을 실제 계산한 뒤 생성된
 * Annual Context 결과다.
 *
 * 이 Category Rule에서는
 * 해당 명리 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * Upstream Status 계산
 * =================================================
 *
 * HealthAnnualContextRule은
 * USEFUL_GOD Axis Signal에 대해:
 *
 * SUPPORTED
 * QUALIFIED
 * WEAKENED
 * CONFLICTED
 *
 * 상태를 집계한다.
 *
 * 그리고:
 *
 * CONFLICTED 존재
 * ->
 * MIXED
 *
 * -------------------------------------------------
 *
 * SUPPORTED 또는 QUALIFIED
 * +
 * WEAKENED
 * ->
 * MIXED
 *
 * -------------------------------------------------
 *
 * WEAKENED
 * ->
 * PRESSURE
 *
 * -------------------------------------------------
 *
 * SUPPORTED
 * ->
 * SUPPORTIVE
 *
 * -------------------------------------------------
 *
 * QUALIFIED only
 * ->
 * CONTEXTUAL
 *
 * 로 하나의 공식
 * LifeDomainSignalStatus를 확정한다.
 *
 * =================================================
 * 실제 Java 계산 보존
 * =================================================
 *
 * 위 Status 계산은
 * Category 단계에서 서로 다른 Evidence를
 * 임의로 합친 것이 아니다.
 *
 * 하나의 AnnualLuckContextAnalysis 안의
 * USEFUL_GOD Axis Signal들을 대상으로
 * HealthAnnualContextRule이 실제 Java에서
 * 계산한 공식 Annual 상태다.
 *
 * 따라서 그대로 보존한다.
 *
 * =================================================
 * Category 단계
 * =================================================
 *
 * 이 Rule에서는:
 *
 * HEALTH_ANNUAL_CONTEXT
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * HEALTH_CURRENT_ANNUAL_CONTEXT
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 1:1 보존한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 원본 LifeDomainSignalStatus를
 * Category Status로 그대로 변환한다.
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
 * 대운과 분리
 * =================================================
 *
 * HEALTH_DECADE_CONTEXT
 *
 * ->
 *
 * HealthDecadeContextDeterministicRule
 *
 * -------------------------------------------------
 *
 * HEALTH_ANNUAL_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 두 시간층을 Category 단계에서
 * 다시 결합하지 않는다.
 *
 * =================================================
 * 대운 × 세운 재합성 금지
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * HEALTH_DECADE_CONTEXT SUPPORTIVE
 * +
 * HEALTH_ANNUAL_CONTEXT SUPPORTIVE
 * =
 * 더 강한 SUPPORTIVE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * HEALTH_DECADE_CONTEXT PRESSURE
 * +
 * HEALTH_ANNUAL_CONTEXT SUPPORTIVE
 * =
 * MIXED
 *
 * 금지.
 *
 * =================================================
 * Upstream 대운 Context와의 구분
 * =================================================
 *
 * AnnualLuckContextAnalysis 자체가
 * 현재 대운 Segment Context를
 * 실제 Java 계산에 반영한 경우는
 * 공식 upstream 계산이므로 인정한다.
 *
 * 하지만 Category에서 별도의:
 *
 * HEALTH_DECADE_CONTEXT
 *
 * Match를 다시 가져와
 * HEALTH_ANNUAL_CONTEXT와 결합하지 않는다.
 *
 * =================================================
 * Annual Functional과 분리
 * =================================================
 *
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * 은 별도의 Functional 계산이다.
 *
 * 따라서:
 *
 * HEALTH_ANNUAL_CONTEXT
 * +
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * 을 이 Rule에서 합치지 않는다.
 *
 * =================================================
 * Monthly Functional과 분리
 * =================================================
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * 역시 별도 계산이다.
 *
 * 이 Rule에서는 월운 Functional 결과를
 * 조회하거나 합성하지 않는다.
 *
 * =================================================
 * 원국 HEALTH와 분리
 * =================================================
 *
 * 다음 원국 Category 결과도
 * 이 Rule의 Status 계산에 사용하지 않는다.
 *
 * HEALTH_BASELINE_BALANCE_CONTEXT
 * HEALTH_CLIMATE_DISEASE_FLOW_CONTEXT
 * HEALTH_ADJUSTMENT_DIRECTION_CONTEXT
 *
 * =================================================
 * 조후·병약·통관 경계
 * =================================================
 *
 * AnnualLuckContextAxis에는:
 *
 * CLIMATE
 * PATHOLOGY
 * REMEDY
 * PASSAGE
 *
 * 전용 Axis가 존재하지 않는다.
 *
 * 따라서 이 Rule에서도:
 *
 * - 세운 조후
 * - 세운 병약
 * - 세운 통관
 *
 * 을 새로 생성하지 않는다.
 *
 * 원국에서 계산된:
 *
 * ClimateHealthAnalysis
 *
 * 결과는 별도 원국 HEALTH Match에서
 * 그대로 보존된다.
 *
 * =================================================
 * 의료적 의미 경계
 * =================================================
 *
 * 이 Status는:
 *
 * 생활 균형의 명리적 조정축에 대한
 * 세운 Context 상태다.
 *
 * 실제 의료적 건강 상태가 아니다.
 *
 * =================================================
 * 의료 변환 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 건강 회복
 * != 건강이 좋아짐
 * != 질병 예방
 *
 * PRESSURE
 * != 질병
 * != 발병
 * != 악화
 *
 * MIXED
 * != 건강 불안정
 * != 만성 질환
 *
 * CONTEXTUAL
 * != 건강함
 * != 질병 없음
 *
 * =================================================
 * UsefulGod 경계
 * =================================================
 *
 * 용신 세운 지원
 * != 건강이 좋아진다
 *
 * 희신 세운 지원
 * != 회복된다
 *
 * 기신 관련 약화
 * != 병이 생긴다
 *
 * 특정 오행
 * != 특정 장기
 *
 * 로 변환하지 않는다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * Category 단계에서 다음을
 * 새로 생성하지 않는다.
 *
 * - 세운이 원국 건강 구조를 강화한다
 * - 세운 때문에 건강 부담이 생긴다
 * - 대운 때문에 세운 건강 상태가 바뀐다
 * - 원국 조후 부담이 세운 Pressure로 이어진다
 * - 용신 세운이 병약을 해소한다
 * - 먼저 대운이 작동하고 이후 세운이 작동한다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * 실제 Java 관계와의 구분
 * =================================================
 *
 * AnnualLuckContextAnalysis에서 이미
 * 실제 Java로 계산된:
 *
 * - 원국 × 세운 Context
 * - 가능한 대운 Segment Context
 * - UsefulGod 관련 Annual Signal
 *
 * 자체는 그대로 유효하다.
 *
 * 금지하는 것은
 * 별도의 Category Match들을 보고
 * 새로운 관계를 추가하는 것이다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * annualContext.decadeTimingUncertain() 또는
 * 현재 Timeline Segment 자체에
 * 불확실성이 존재할 수 있다.
 *
 * 이 경우 가능한 Segment 중 하나를
 * Category에서 임의 선택하지 않는다.
 *
 * 각 Segment에 실제 생성된:
 *
 * HEALTH_ANNUAL_CONTEXT
 *
 * Match를 각각 그대로 보존한다.
 *
 * =================================================
 * decadeContextAvailable 경계
 * =================================================
 *
 * HealthAnnualContextRule은
 * 대운 Context를 사용할 수 없는 경우에도
 * 원국 × 세운만으로 확정 가능한
 * annualSignals는 유지한다.
 *
 * Category에서도:
 *
 * decadeUnavailable
 *
 * 상태라고 해서 실제 존재하는
 * HEALTH_ANNUAL_CONTEXT를 삭제하지 않는다.
 *
 * 반대로 다른 대운 Match를
 * 대신 끼워 넣지도 않는다.
 *
 * =================================================
 * 좌표
 * =================================================
 *
 * 이 Match는 CURRENT_TIMELINE Layer이므로:
 *
 * - sajuYear
 * - monthIndex
 *
 * 를 현재 Segment 좌표 그대로 보존한다.
 *
 * monthIndex가 존재한다고 해서
 * HEALTH_ANNUAL_CONTEXT를
 * 월운 계산으로 재해석하지 않는다.
 *
 * monthIndex == 0도
 * 유효한 Timeline 좌표다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * HEALTH_ANNUAL_CONTEXT Match가
 * 여러 Timeline Segment에서 존재한다고 해서:
 *
 * - 건강 위험도가 높다
 * - 건강운이 강하다
 * - 발병 가능성이 높다
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * LifeDomainRuleMatch의:
 *
 * - summary
 * - reasons
 *
 * 를 파싱하여:
 *
 * - 용신 오행
 * - 희신 오행
 * - 기신 오행
 * - Signal 종류
 * - 조후
 * - 병약
 * - 통관
 *
 * 을 Category에서 다시 판정하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - AnnualLuckContextAnalysis 재계산
 * - USEFUL_GOD Signal 재계산
 * - SignalCounts 재계산
 * - upstream Status 재계산
 * - 대운 × 세운 Status Matrix 생성
 * - HEALTH_DECADE_CONTEXT와 Status 합성
 * - HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL과 Status 합성
 * - HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL과 Status 합성
 * - 원국 HEALTH Match와 Status 합성
 * - 조후 재계산
 * - 병약 재계산
 * - 통관 재계산
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 오행-장기 직접 대응
 * - 특정 질환 생성
 * - 발병 예측
 * - 악화 예측
 * - 회복 예측
 * - 치료 예측
 * - 수명 예측
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class HealthAnnualContextDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "HEALTH_CURRENT_ANNUAL_CONTEXT";

    private static final String
            HEALTH_ANNUAL_CONTEXT_RULE_CODE =
            "HEALTH_ANNUAL_CONTEXT";


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
                "HealthAnnualContextDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.HEALTH_TENDENCY
        ) {

            throw new IllegalArgumentException(
                    "HealthAnnualContextDeterministicRule은 HEALTH_TENDENCY View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

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
                            "HEALTH Annual Segment는 null일 수 없습니다."
                    );

            List<LifeDomainRuleMatch> annualMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            HEALTH_ANNUAL_CONTEXT_RULE_CODE
                    );

            if (
                    annualMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Match 1개 -> Category Match 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch annualMatch
                    : annualMatches
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                annualMatch,
                                "HEALTH_ANNUAL_CONTEXT Match는 null일 수 없습니다."
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
     *
     * 다음만 사용한다.
     *
     * domain =
     * HEALTH_LIFESTYLE
     *
     * ruleCode =
     * HEALTH_ANNUAL_CONTEXT
     *
     * summary / reasons 문자열을
     * Source 판별에 사용하지 않는다.
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
                        "HEALTH Annual Context sourceRuleCode가 필요합니다."
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
     *
     * HealthAnnualContextRule이 이미 계산한
     * 공식 LifeDomainSignalStatus만 사용한다.
     *
     * 다른 HEALTH Match의 상태는
     * 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "HEALTH Annual Context에는 LifeDomainSignalStatus가 필요합니다."
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
     *
     * 최종 사용자 의료 해석이 아니라
     * deterministic provenance 기록이다.
     */

    private List<String> buildReasons(

            ConsultationCategorySegmentEvidence segment,

            LifeDomainRuleMatch sourceMatch

    ) {

        Objects.requireNonNull(
                segment,
                "HEALTH Annual Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "HEALTH Annual Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_ANNUAL_CONTEXT에서 이미 계산된 하나의 HEALTH_LIFESTYLE LifeDomain Match를 HEALTH_TENDENCY의 독립 세운 Context로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 HEALTH_ANNUAL_CONTEXT의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 HEALTH Source와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "HealthAnnualContextRule이 AnnualLuckContextAnalysis의 USEFUL_GOD Axis Signal을 이용해 이미 계산한 SUPPORTED·QUALIFIED·WEAKENED·CONFLICTED 상태와 최종 LifeDomainSignalStatus를 다시 계산하지 않았습니다."
        );

        reasons.add(
                "CONFLICTED 또는 지원측 Signal과 WEAKENED Signal의 공존으로 upstream HealthAnnualContextRule 자체가 MIXED를 계산한 경우 해당 결과는 공식 Java 계산이므로 그대로 보존합니다."
        );

        reasons.add(
                "HEALTH_DECADE_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 대운×세운 Category Status Matrix를 생성하지 않았습니다."
        );

        reasons.add(
                "AnnualLuckContextAnalysis 내부에 정확한 대운 Segment Context가 실제 반영되어 있는 경우 그 upstream 계산은 그대로 인정하지만 별도 HEALTH_DECADE_CONTEXT Match를 다시 결합하지 않습니다."
        );

        reasons.add(
                "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL, HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL 및 원국 HEALTH Match를 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "AnnualLuckContextAxis에는 조후·병약·통관 전용 Axis가 없으므로 세운 단계에서 해당 구조를 새로 생성하지 않았습니다."
        );

        reasons.add(
                "이 Status는 생활 균형의 명리적 조정 방향에 대한 세운 Context이며 실제 질병·발병·악화·회복·치료 여부로 변환하지 않습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 용신 오행·희신 오행·기신 오행·조후·병약·통관 또는 의료적 의미를 재구성하지 않았습니다."
        );

        reasons.add(
                "다른 HEALTH Evidence가 같은 Segment에 존재한다는 사실만으로 새로운 강화·약화·보완·인과·순서 또는 종합 Status를 생성하지 않았습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 HEALTH 세운 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 좌표의 다른 가능한 Segment가 존재하면 각각의 HEALTH_ANNUAL_CONTEXT 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment에서 대운 Context를 사용할 수 없더라도 upstream HealthAnnualContextRule에서 실제 생성된 Annual Match는 그대로 보존하며 다른 대운 Match를 대신 추가하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 monthIndex 존재를 이유로 HEALTH_ANNUAL_CONTEXT를 월운 HEALTH 계산으로 재해석하지 않습니다."
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "HEALTH Annual Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 세운의 명리적 생활균형 조정 Context가 SUPPORTIVE 상태로 계산되었으며 다른 HEALTH 시간층과 합성하지 않습니다.";

            case PRESSURE ->
                    "현재 세운의 명리적 생활균형 조정 Context가 PRESSURE 상태로 계산되었으며 질병이나 발병 상태를 의미하지 않습니다.";

            case MIXED ->
                    "현재 세운의 명리적 생활균형 조정 Context 자체에서 MIXED 상태가 계산되었으며 해당 upstream 결과를 그대로 보존합니다.";

            case CONTEXTUAL ->
                    "현재 세운의 명리적 생활균형 조정 Context가 CONTEXTUAL 상태로 남아 있으며 다른 HEALTH 결과와 합성하지 않습니다.";
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