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
 * CAREER 현재 대운 Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_DECADE_CONTEXT
 *
 * 결과를 ConsultationCategory.CAREER의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * CareerDecadeContextRule은
 * DecadeLuckPeriodContextAnalysis에서:
 *
 * - DIRECT_OFFICER
 * - SEVEN_KILLINGS
 *
 * 와 연결된 실제 Decade Context Signal을 조회한다.
 *
 * 해당 Signal은 단순 TenGod Axis뿐 아니라
 * upstream에서 이미 계산된:
 *
 * - USEFUL_GOD
 * - STRUCTURE
 * - RELATION
 * - TRANSFORMATION
 *
 * 등의 Context를 포함할 수 있다.
 *
 * =================================================
 * Upstream Status 계산
 * =================================================
 *
 * CareerDecadeContextRule은 이미:
 *
 * SUPPORTED
 * QUALIFIED
 * WEAKENED
 * CONFLICTED
 *
 * 상태를 실제 Signal 기준으로 집계하여
 * LifeDomainSignalStatus를 확정한다.
 *
 * 따라서 이 Category Rule에서는
 * 그 Status를 다시 계산하지 않는다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * CAREER_DECADE_CONTEXT
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CAREER Category Match 하나
 *
 * 로 그대로 보존한다.
 *
 * =================================================
 * 시간층 분리
 * =================================================
 *
 * 대운:
 *
 * CAREER_DECADE_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 세운:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * ->
 *
 * CareerCurrentStructuralFlowRule
 *
 * -------------------------------------------------
 *
 * 월운:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * ->
 *
 * 별도의 Monthly deterministic Rule
 *
 * =================================================
 * 세운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * 를 조회하지 않는다.
 *
 * 따라서:
 *
 * DECADE SUPPORTIVE
 * +
 * ANNUAL PRESSURE
 * =
 * MIXED
 *
 * 같은 계산을 하지 않는다.
 *
 * =================================================
 * 월운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_MONTHLY_CONTEXT
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * 을 사용하지 않는다.
 *
 * =================================================
 * 원국과의 분리
 * =================================================
 *
 * 다음 Source도 사용하지 않는다.
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 원국 구조와 대운 Context를
 * 하나의 Category Status로 합성하지 않는다.
 *
 * =================================================
 * 실제 Java 계산 보존
 * =================================================
 *
 * CareerDecadeContextRule 내부에서
 * 실제 DecadeLuckContextSignal에 의해 계산된:
 *
 * - 지원
 * - 한정
 * - 약화
 * - 상충
 *
 * 상태와 그 최종 LifeDomainSignalStatus는
 * 유효한 upstream deterministic 계산이다.
 *
 * 이 Category Rule은 해당 계산을
 * 그대로 보존한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음 의미를 새로 생성하지 않는다.
 *
 * - 대운 때문에 세운 직업 상태가 달라진다
 * - 대운의 책임 구조 위에서 세운이 작동한다
 * - 대운이 원국 관살을 강화한다
 * - 원국 관살 때문에 대운 부담이 커진다
 * - 대운이 월운을 보완한다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 취업
 * != 승진
 * != 직위 획득
 *
 * PRESSURE
 * != 퇴사
 * != 해고
 * != 직장 갈등
 *
 * MIXED
 * != 이직
 * != 직업 변동
 *
 * CONTEXTUAL
 * != 직업운 없음
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * DIRECT_OFFICER
 * != 공무원
 *
 * SEVEN_KILLINGS
 * != 군인
 * != 경찰
 * != 법조인
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각각의 Segment에서 실제 존재하는
 * CAREER_DECADE_CONTEXT를 각각 보존한다.
 *
 * 하나를 임의 선택하지 않는다.
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
 * 는 현재 Segment 좌표를 그대로 보존한다.
 *
 * 이는 대운 자체를 연운 또는 월운으로
 * 재해석한다는 뜻이 아니다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 대운 Signal 재계산
 * - 관살 재계산
 * - 용신 재계산
 * - 격국 재계산
 * - 관계 재계산
 * - 합화 재계산
 * - 신강신약 재계산
 * - 대운 × 세운 Status 합성
 * - 대운 × 월운 Status 합성
 * - 원국 × 대운 Status 합성
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - Match 개수 점수화
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 실제 직업 사건 생성
 */
@Component
public class CareerDecadeContextDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_CURRENT_DECADE_CONTEXT";

    private static final String
            DECADE_CONTEXT_RULE_CODE =
            "CAREER_DECADE_CONTEXT";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CAREER;
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
                "CareerDecadeContextDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerDecadeContextDeterministicRule은 CAREER View만 평가할 수 있습니다."
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
         * Current Timeline 필요
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
                            "CAREER Decade Context Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 실제 CAREER_DECADE_CONTEXT 조회
             * =================================================
             */

            List<LifeDomainRuleMatch> decadeMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            DECADE_CONTEXT_RULE_CODE
                    );

            /*
             * 현재 Segment에 활성 대운 CAREER Context가 없으면
             * 다른 시간층을 대신 사용하지 않는다.
             */

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
                                "CAREER_DECADE_CONTEXT Match는 null일 수 없습니다."
                        );

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                List<String> reasons =
                        buildReasons(
                                normalizedSegment
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.CAREER,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                status,

                                normalizedSegment
                                        .sajuYear(),

                                normalizedSegment
                                        .monthIndex(),

                                List.of(),

                                summaryOf(
                                        status
                                ),

                                List.of(
                                        normalizedMatch
                                ),

                                reasons
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
     * Source 판별에는:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * Source 판별 조건으로 사용하지 않는다.
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
                        "CAREER Decade Context sourceRuleCode가 필요합니다."
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
                            "CAREER Segment Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.CAREER
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
     * Status 1:1 변환
     * =================================================
     *
     * CareerDecadeContextRule이 이미 계산한
     * LifeDomainSignalStatus를 그대로 보존한다.
     *
     * 다른 Source나 시간층을 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CAREER Decade Context에는 LifeDomainSignalStatus가 필요합니다."
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
     * 최종 사용자 Story가 아니라
     * deterministic provenance 기록이다.
     */

    private List<String> buildReasons(
            ConsultationCategorySegmentEvidence segment
    ) {

        Objects.requireNonNull(
                segment,
                "CAREER Decade Context Segment가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "CAREER_DECADE_CONTEXT에서 이미 계산된 하나의 LifeDomain Match를 독립적인 CAREER Category Match로 보존했습니다."
        );

        reasons.add(
                "CareerDecadeContextRule이 DecadeLuckPeriodContextAnalysis의 정관·칠살 관련 Signal을 이용해 확정한 공식 LifeDomainSignalStatus를 그대로 1:1 보존했습니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER_MONTHLY_CONTEXT와 CAREER_MONTHLY_FUNCTIONAL을 이 Rule에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER_PRIMARY_OFFICER_STRUCTURE, CAREER_OFFICER_STRUCTURE, CAREER_CROSS_VALIDATED_PATTERN 같은 원국 Source와도 Status를 합성하지 않았습니다."
        );

        reasons.add(
                "대운과 세운·월운·원국 결과 사이의 강화·약화·상쇄·충돌·보완·전환 관계를 새로 생성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과를 취업·승진·합격·이직·퇴사·해고·직위 획득 같은 실제 직업 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Decade Context 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 다른 가능한 Segment가 존재하면 각 CAREER_DECADE_CONTEXT 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.beforeFirstDecadeLuck()
        ) {

            reasons.add(
                    "현재 Segment는 첫 대운 시작 전 상태이며 실제 CAREER_DECADE_CONTEXT Match가 존재하는 경우에만 이 결과가 생성됩니다."
            );
        }

        if (
                segment.afterGeneratedDecadeRange()
        ) {

            reasons.add(
                    "현재 Segment가 생성된 대운 범위 이후로 표시되더라도 실제 CAREER_DECADE_CONTEXT Match가 존재하는 경우에만 이 결과가 생성됩니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태라면 다른 시간층을 대운 대신 사용하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 이를 대운 내부의 연·월 진행 순서로 재해석하지 않습니다."
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * 세운·월운·원국과 연결하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER Decade Context Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운의 CAREER 관살 Context 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 대운의 CAREER 관살 Context 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 대운의 CAREER 관살 Context 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 대운의 CAREER 관살 Context 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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