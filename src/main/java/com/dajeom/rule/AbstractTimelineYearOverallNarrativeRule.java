package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicDependencyReference;
import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * PAST_LUCK / FUTURE_LUCK
 * TIMELINE_YEAR OVERALL_NARRATIVE
 * 공통 deterministic Synthesis Rule.
 *
 * =================================================
 * Phase
 * =================================================
 *
 * phase = 200
 *
 * =================================================
 * 입력
 * =================================================
 *
 * phase 200 시작 전 이미 검증된:
 *
 * BIG_FLOW
 *
 * PRIMARY_ACTIVE_DOMAIN
 *
 * PRIMARY_PRESSURE_DOMAIN
 *
 * CROSS_DOMAIN_PATTERN
 *
 * TURNING_POINT
 *
 * 결과를 사용한다.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * OpenAI가 위 결과들을 직접 보고:
 *
 * - 어느 것을 더 중요하게 볼지 임의 결정
 * - PRESSURE를 전체 흉운으로 확대
 * - PRIMARY_ACTIVE를 좋은 영역으로 오해
 * - PRIMARY_PRESSURE를 BIG_FLOW보다 우선
 * - CROSS_DOMAIN_PATTERN을 전체 상태로 덮어쓰기
 * - TURNING_POINT를 사건 발생으로 확대
 *
 * 하는 것을 막는다.
 *
 * =================================================
 * 최종 해석 우선순위
 * =================================================
 *
 * 1.
 * BIG_FLOW
 *
 * 해당 명리연도의
 * 전체 deterministic 방향 Anchor.
 *
 * 최종 OVERALL_NARRATIVE status는
 * 반드시 BIG_FLOW status를 그대로 사용한다.
 *
 * =================================================
 *
 * 2.
 * PRIMARY_ACTIVE_DOMAIN
 *
 * 해당 연도에서 시간적 작동 자격이
 * 상대적으로 가장 분명한 생활영역.
 *
 * BIG_FLOW를 덮어쓰지 않는다.
 *
 * 예:
 *
 * BIG_FLOW = MIXED
 * PRIMARY_ACTIVE = CAREER / PRESSURE
 *
 * ->
 *
 * 전체는 MIXED.
 *
 * CAREER는 그 MIXED 구조 안에서
 * 주된 작동축 중 하나.
 *
 * 전체 PRESSURE로 변경하지 않는다.
 *
 * =================================================
 *
 * 3.
 * PRIMARY_PRESSURE_DOMAIN
 *
 * 명시적 PRESSURE 후보들 중
 * 구조적으로 주된 부담축을
 * 식별할 수 있을 때만 존재한다.
 *
 * 이것 역시 BIG_FLOW를 덮어쓰지 않는다.
 *
 * =================================================
 *
 * 4.
 * CROSS_DOMAIN_PATTERN
 *
 * 서로 다른 LifeDomain 방향들이
 * 어떻게 공존하는지를 설명한다.
 *
 * 전체 방향을 새로 계산하지 않는다.
 *
 * =================================================
 *
 * 5.
 * TURNING_POINT
 *
 * 전년도 / 해당연도 / 다음연도 비교에서
 * 구조 변화가 검출되었음을 뜻한다.
 *
 * TURNING_POINT 존재 자체는:
 *
 * - 좋음
 * - 나쁨
 * - 사건 발생
 *
 * 을 의미하지 않는다.
 *
 * TURNING_POINT status도 앞 Rule에서
 * 현재 BIG_FLOW status를 사용하도록
 * 설계되어 있다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * OVERALL_NARRATIVE는
 * 앞 단계 deterministic 결과를
 * 다시 계산하지 않는다.
 *
 * =================================================
 *
 * 예:
 *
 * BIG_FLOW Rule의 raw LifeDomain Match를
 * 다시 읽어 MIXED인지 PRESSURE인지
 * 계산하지 않는다.
 *
 * 이미 계산된 BIG_FLOW 결과를
 * 공식 Anchor로 사용한다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * summary나 reasons 안의 한국어 문장을
 * 파싱하지 않는다.
 *
 * PRIMARY_ACTIVE_DOMAIN과
 * PRIMARY_PRESSURE_DOMAIN은
 * 해당 Match의 sourceDomains() 구조로
 * Domain을 확인한다.
 *
 * =================================================
 * 최종 Status
 * =================================================
 *
 * 항상:
 *
 * OVERALL_NARRATIVE.status
 * =
 * BIG_FLOW.status
 *
 * =================================================
 *
 * PRIMARY_ACTIVE / PRIMARY_PRESSURE /
 * CROSS_DOMAIN / TURNING_POINT의 status를
 * 평균하거나 다수결하지 않는다.
 *
 * =================================================
 * Dependency
 * =================================================
 *
 * 이 결과는 실제 사용한:
 *
 * BIG_FLOW
 *
 * PRIMARY_ACTIVE_DOMAIN
 *
 * PRIMARY_PRESSURE_DOMAIN
 *
 * CROSS_DOMAIN_PATTERN
 *
 * TURNING_POINT
 *
 * Match들을 dependency로 보존한다.
 *
 * =================================================
 *
 * 따라서 최종 결과에서:
 *
 * 왜 이 연도 narrative가 이렇게 구성됐는지
 * deterministic provenance를 추적할 수 있다.
 *
 * =================================================
 * Raw Source
 * =================================================
 *
 * sourceMatches에는
 * 사용된 현재 명리연도 component들의
 * 실제 LifeDomainRuleMatch를 합집합으로 넣는다.
 *
 * 다른 연도의 raw LifeDomainRuleMatch는
 * 넣지 않는다.
 *
 * TURNING_POINT의 전년도/다음연도 dependency는
 * TURNING_POINT Match 자체의 dependency에
 * 이미 구조적으로 보존되어 있다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 새로운 사주 계산
 * - Match 개수 점수화
 * - Rule 개수 점수화
 * - Source 개수 점수화
 * - SUPPORTIVE/PRESSURE 상쇄
 * - 사건 생성
 * - 실제 과거 사건 추정
 * - 미래 사건 예언
 * - summary 문자열 파싱
 */
public abstract class AbstractTimelineYearOverallNarrativeRule
        implements ConsultationCategoryDeterministicSynthesisRule {

    /*
     * =================================================
     * Metadata
     * =================================================
     */

    @Override
    public abstract ConsultationCategory category();


    @Override
    public abstract String code();


    protected abstract String flowLabel();


    protected abstract void appendTemporalReason(
            List<String> reasons
    );


    @Override
    public final int phase() {

        return 200;
    }


    /*
     * =================================================
     * Upstream Rule Codes
     * =================================================
     */

    private String bigFlowRuleCode() {

        return flowLabel()
                + "_TIMELINE_YEAR_BIG_FLOW";
    }


    private String primaryActiveRuleCode() {

        return flowLabel()
                + "_TIMELINE_YEAR_PRIMARY_ACTIVE_DOMAIN";
    }


    private String primaryPressureRuleCode() {

        return flowLabel()
                + "_TIMELINE_YEAR_PRIMARY_PRESSURE_DOMAIN";
    }


    private String crossDomainRuleCode() {

        return flowLabel()
                + "_TIMELINE_YEAR_CROSS_DOMAIN_PATTERN";
    }


    private String turningPointRuleCode() {

        return flowLabel()
                + "_TIMELINE_YEAR_TURNING_POINT";
    }


    /*
     * =================================================
     * Evaluate
     * =================================================
     */

    @Override
    public final List<ConsultationCategoryDeterministicMatch> evaluate(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    priorMatches

    ) {

        Objects.requireNonNull(
                view,
                flowLabel()
                        + " OVERALL_NARRATIVE에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        Objects.requireNonNull(
                priorMatches,
                flowLabel()
                        + " OVERALL_NARRATIVE에는 prior deterministic matches가 필요합니다."
        );

        if (
                view.category()
                        != category()
        ) {

            throw new IllegalArgumentException(
                    flowLabel()
                            + " OVERALL_NARRATIVE Rule은 "
                            + category()
                            + " View만 평가할 수 있습니다. actual="
                            + view.category()
            );
        }

        if (
                view.unavailable()
                        || !view.hasTimelineYearEvidences()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                ConsultationCategoryTimelineYearEvidence yearEvidence
                : view.timelineYearEvidences()
        ) {

            ConsultationCategoryDeterministicMatch match =
                    evaluateYear(
                            yearEvidence,
                            priorMatches
                    );

            if (
                    match != null
            ) {

                result.add(
                        match
                );
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * One Year
     * =================================================
     */

    private ConsultationCategoryDeterministicMatch evaluateYear(

            ConsultationCategoryTimelineYearEvidence yearEvidence,

            List<ConsultationCategoryDeterministicMatch>
                    priorMatches

    ) {

        int sajuYear =
                yearEvidence.sajuYear();

        ConsultationCategoryDeterministicMatch bigFlow =
                findUnique(
                        priorMatches,
                        bigFlowRuleCode(),
                        sajuYear
                )
                        .orElse(
                                null
                        );

        /*
         * =================================================
         * BIG_FLOW 필수
         * =================================================
         *
         * 최종 연도 narrative의 공식 Anchor다.
         */

        if (
                bigFlow == null
        ) {

            return null;
        }

        ConsultationCategoryDeterministicMatch primaryActive =
                findUnique(
                        priorMatches,
                        primaryActiveRuleCode(),
                        sajuYear
                )
                        .orElse(
                                null
                        );

        ConsultationCategoryDeterministicMatch primaryPressure =
                findUnique(
                        priorMatches,
                        primaryPressureRuleCode(),
                        sajuYear
                )
                        .orElse(
                                null
                        );

        ConsultationCategoryDeterministicMatch crossDomain =
                findUnique(
                        priorMatches,
                        crossDomainRuleCode(),
                        sajuYear
                )
                        .orElse(
                                null
                        );

        ConsultationCategoryDeterministicMatch turningPoint =
                findUnique(
                        priorMatches,
                        turningPointRuleCode(),
                        sajuYear
                )
                        .orElse(
                                null
                        );

        /*
         * =================================================
         * 구조 검증
         * =================================================
         */

        LifeDomain primaryActiveDomain =
                primaryActive == null
                        ? null
                        : requireSingleSourceDomain(
                        primaryActive,
                        "PRIMARY_ACTIVE_DOMAIN"
                );

        LifeDomain primaryPressureDomain =
                primaryPressure == null
                        ? null
                        : requireSingleSourceDomain(
                        primaryPressure,
                        "PRIMARY_PRESSURE_DOMAIN"
                );

        if (
                primaryPressure != null
                        && primaryPressure.status()
                        != ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            throw new IllegalStateException(
                    flowLabel()
                            + " PRIMARY_PRESSURE_DOMAIN 결과의 status가 PRESSURE가 아닙니다. year="
                            + sajuYear
                            + ", actual="
                            + primaryPressure.status()
            );
        }

        /*
         * TURNING_POINT status는
         * 현재 BIG_FLOW status와 동일해야 한다.
         */

        if (
                turningPoint != null
                        && turningPoint.status()
                        != bigFlow.status()
        ) {

            throw new IllegalStateException(
                    flowLabel()
                            + " TURNING_POINT status가 해당 연도 BIG_FLOW와 일치하지 않습니다. year="
                            + sajuYear
                            + ", bigFlow="
                            + bigFlow.status()
                            + ", turningPoint="
                            + turningPoint.status()
            );
        }

        List<ConsultationCategoryDeterministicMatch> components =
                components(
                        bigFlow,
                        primaryActive,
                        primaryPressure,
                        crossDomain,
                        turningPoint
                );

        List<LifeDomainRuleMatch> sourceMatches =
                collectCurrentYearSources(
                        components
                );

        if (
                sourceMatches.isEmpty()
        ) {

            throw new IllegalStateException(
                    flowLabel()
                            + " OVERALL_NARRATIVE를 생성했지만 현재 명리연도의 raw sourceMatches가 없습니다. year="
                            + sajuYear
            );
        }

        List<ConsultationCategoryDeterministicDependencyReference>
                dependencies =
                components
                        .stream()
                        .map(
                                ConsultationCategoryDeterministicDependencyReference
                                        ::of
                        )
                        .toList();

        List<String> reasons =
                buildReasons(
                        yearEvidence,
                        bigFlow,
                        primaryActive,
                        primaryActiveDomain,
                        primaryPressure,
                        primaryPressureDomain,
                        crossDomain,
                        turningPoint
                );

        /*
         * =================================================
         * 최종 Status
         * =================================================
         *
         * 절대 재계산하지 않는다.
         *
         * BIG_FLOW가 공식 Anchor다.
         */

        return new ConsultationCategoryDeterministicMatch(
                category(),
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                code(),
                bigFlow.status(),
                sajuYear,
                null,
                yearEvidence.monthIndices(),
                summaryOf(
                        sajuYear,
                        bigFlow,
                        primaryActive,
                        primaryActiveDomain,
                        primaryPressureDomain,
                        crossDomain,
                        turningPoint
                ),
                sourceMatches,
                reasons,
                dependencies
        );
    }


    /*
     * =================================================
     * Unique Match
     * =================================================
     */

    private Optional<ConsultationCategoryDeterministicMatch>
    findUnique(

            List<ConsultationCategoryDeterministicMatch>
                    priorMatches,

            String ruleCode,

            int sajuYear

    ) {

        ConsultationCategoryDeterministicMatch found =
                null;

        for (
                ConsultationCategoryDeterministicMatch match
                : priorMatches
        ) {

            Objects.requireNonNull(
                    match,
                    flowLabel()
                            + " OVERALL_NARRATIVE priorMatch는 null일 수 없습니다."
            );

            if (
                    match.category()
                            != category()
            ) {

                continue;
            }

            if (
                    match.layer()
                            != ConsultationCategoryDeterministicLayer.TIMELINE_YEAR
            ) {

                continue;
            }

            if (
                    !match.ruleCode()
                            .equals(
                                    ruleCode
                            )
            ) {

                continue;
            }

            if (
                    match.sajuYear() == null
                            || match.sajuYear()
                            != sajuYear
            ) {

                continue;
            }

            if (
                    found != null
            ) {

                throw new IllegalStateException(
                        flowLabel()
                                + " OVERALL_NARRATIVE 계산에서 동일 Rule/Year deterministic 결과가 중복되었습니다. rule="
                                + ruleCode
                                + ", year="
                                + sajuYear
                );
            }

            found =
                    match;
        }

        return Optional.ofNullable(
                found
        );
    }


    /*
     * =================================================
     * Single Domain
     * =================================================
     *
     * PRIMARY_ACTIVE_DOMAIN /
     * PRIMARY_PRESSURE_DOMAIN은
     * 하나의 Domain만 가리켜야 한다.
     *
     * summary 문자열을 파싱하지 않고
     * sourceDomains() 구조를 사용한다.
     */

    private LifeDomain requireSingleSourceDomain(

            ConsultationCategoryDeterministicMatch match,

            String componentName

    ) {

        Set<LifeDomain> domains =
                match.sourceDomains();

        if (
                domains.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    flowLabel()
                            + " "
                            + componentName
                            + "은 정확히 하나의 source LifeDomain을 가져야 합니다. year="
                            + match.sajuYear()
                            + ", domains="
                            + domains
            );
        }

        return domains
                .iterator()
                .next();
    }


    /*
     * =================================================
     * Components
     * =================================================
     */

    private List<ConsultationCategoryDeterministicMatch>
    components(

            ConsultationCategoryDeterministicMatch bigFlow,

            ConsultationCategoryDeterministicMatch primaryActive,

            ConsultationCategoryDeterministicMatch primaryPressure,

            ConsultationCategoryDeterministicMatch crossDomain,

            ConsultationCategoryDeterministicMatch turningPoint

    ) {

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        result.add(
                Objects.requireNonNull(
                        bigFlow
                )
        );

        if (
                primaryActive != null
        ) {

            result.add(
                    primaryActive
            );
        }

        if (
                primaryPressure != null
        ) {

            result.add(
                    primaryPressure
            );
        }

        if (
                crossDomain != null
        ) {

            result.add(
                    crossDomain
            );
        }

        if (
                turningPoint != null
        ) {

            result.add(
                    turningPoint
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Raw Current-Year Sources
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    collectCurrentYearSources(

            List<ConsultationCategoryDeterministicMatch>
                    components

    ) {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                ConsultationCategoryDeterministicMatch component
                : components
        ) {

            for (
                    LifeDomainRuleMatch sourceMatch
                    : component.sourceMatches()
            ) {

                LifeDomainRuleMatch normalized =
                        Objects.requireNonNull(
                                sourceMatch,
                                "OVERALL_NARRATIVE component sourceMatch는 null일 수 없습니다."
                        );

                if (
                        !result.contains(
                                normalized
                        )
                ) {

                    result.add(
                            normalized
                    );
                }
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            ConsultationCategoryTimelineYearEvidence yearEvidence,

            ConsultationCategoryDeterministicMatch bigFlow,

            ConsultationCategoryDeterministicMatch primaryActive,

            LifeDomain primaryActiveDomain,

            ConsultationCategoryDeterministicMatch primaryPressure,

            LifeDomain primaryPressureDomain,

            ConsultationCategoryDeterministicMatch crossDomain,

            ConsultationCategoryDeterministicMatch turningPoint

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + " OVERALL_NARRATIVE는 LifeDomainRuleMatch를 다시 합산해 새로운 전체 상태를 계산하지 않고 이미 검증된 BIG_FLOW를 해당 명리연도의 최종 방향 Anchor로 사용합니다."
        );

        reasons.add(
                "OVERALL_NARRATIVE의 최종 status는 BIG_FLOW status "
                        + bigFlow.status()
                        + "를 그대로 사용하며 PRIMARY_ACTIVE_DOMAIN, PRIMARY_PRESSURE_DOMAIN, CROSS_DOMAIN_PATTERN, TURNING_POINT가 이를 덮어쓰지 않습니다."
        );

        /*
         * =================================================
         * Primary Active
         * =================================================
         */

        if (
                primaryActive != null
        ) {

            reasons.add(
                    "PRIMARY_ACTIVE_DOMAIN은 "
                            + primaryActiveDomain
                            + "이며 해당 Domain의 deterministic 방향은 "
                            + primaryActive.status()
                            + "입니다."
            );

            reasons.add(
                    "PRIMARY_ACTIVE_DOMAIN은 가장 좋은 영역이나 가장 나쁜 영역을 뜻하지 않고 해당 연도에서 시간적 작동 자격이 상대적으로 가장 분명한 생활영역을 뜻합니다."
            );

        } else {

            reasons.add(
                    "해당 명리연도에는 다른 모든 후보를 구조적으로 지배하는 단일 PRIMARY_ACTIVE_DOMAIN이 없어 임의 대표 생활영역을 생성하지 않습니다."
            );
        }

        /*
         * =================================================
         * Primary Pressure
         * =================================================
         */

        if (
                primaryPressure != null
        ) {

            reasons.add(
                    "PRIMARY_PRESSURE_DOMAIN은 "
                            + primaryPressureDomain
                            + "입니다."
            );

            reasons.add(
                    "PRIMARY_PRESSURE_DOMAIN은 명시적 PRESSURE 후보 중 하나의 주된 부담축을 구조적으로 식별할 수 있다는 뜻이며 이것만으로 연도 전체 status를 PRESSURE로 변경하지 않습니다."
            );

        } else {

            reasons.add(
                    "해당 명리연도에는 단일 PRIMARY_PRESSURE_DOMAIN을 확정할 자격이 없거나 명시적 PRESSURE Domain 자체가 없어 임의 부담축을 생성하지 않습니다."
            );
        }

        /*
         * =================================================
         * Cross Domain
         * =================================================
         */

        if (
                crossDomain != null
        ) {

            reasons.add(
                    "CROSS_DOMAIN_PATTERN이 존재하며 deterministic status는 "
                            + crossDomain.status()
                            + ", 참여 sourceDomains="
                            + crossDomain.sourceDomains()
                            + "입니다."
            );

            reasons.add(
                    "CROSS_DOMAIN_PATTERN은 여러 생활영역의 방향 공존 구조를 설명하며 BIG_FLOW의 최종 방향을 다시 계산하지 않습니다."
            );

        } else {

            reasons.add(
                    "해당 명리연도에는 별도의 CROSS_DOMAIN_PATTERN을 구성할 수 있는 충분한 서로 다른 Temporal LifeDomain 구조가 확인되지 않았습니다."
            );
        }

        /*
         * =================================================
         * Turning Point
         * =================================================
         */

        if (
                turningPoint != null
        ) {

            reasons.add(
                    "해당 명리연도에는 전후 명리연도와의 구조 비교에서 TURNING_POINT가 확인되었습니다."
            );

            reasons.add(
                    "TURNING_POINT의 status "
                            + turningPoint.status()
                            + "는 전환점의 좋고 나쁨 점수가 아니라 현재 명리연도 BIG_FLOW의 방향을 그대로 보존한 값입니다."
            );

            reasons.add(
                    "TURNING_POINT가 참조한 전년도·현재연도·다음연도의 deterministic 구조는 TURNING_POINT Match 자체의 dependency provenance에 보존되어 있습니다."
            );

        } else {

            reasons.add(
                    "해당 명리연도에는 현재 Range와 공식 TURNING_POINT 조건에서 구조적 전환점이 생성되지 않았습니다."
            );
        }

        /*
         * =================================================
         * Timeline
         * =================================================
         */

        reasons.add(
                "OVERALL_NARRATIVE는 Timeline Range가 실제 계산한 monthIndex 목록 "
                        + yearEvidence.monthIndices()
                        + "을 그대로 보존하며 대표 월을 임의 선택하지 않습니다."
        );

        if (
                yearEvidence
                        .timelineUncertaintyExists()
        ) {

            reasons.add(
                    "해당 명리연도에는 Timeline uncertainty가 존재하며 최종 deterministic narrative를 실제 사건이나 확정된 시간 결과로 변환하지 않습니다."
            );
        }

        /*
         * =================================================
         * AI Contract
         * =================================================
         */

        reasons.add(
                "최종 상담 문장에서는 BIG_FLOW를 연도 전체 방향으로 사용하고 PRIMARY_ACTIVE_DOMAIN은 주된 작동축, PRIMARY_PRESSURE_DOMAIN은 제한된 부담축, CROSS_DOMAIN_PATTERN은 영역 간 공존 구조, TURNING_POINT는 전후 연도 구조 변화로만 설명해야 합니다."
        );

        reasons.add(
                "OpenAI는 이 OVERALL_NARRATIVE에서 결정된 구조를 다시 계산하거나 하나의 하위 Component를 연도 전체 결론으로 승격해서는 안 됩니다."
        );

        appendTemporalReason(
                reasons
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOf(

            int sajuYear,

            ConsultationCategoryDeterministicMatch bigFlow,

            ConsultationCategoryDeterministicMatch primaryActive,

            LifeDomain primaryActiveDomain,

            LifeDomain primaryPressureDomain,

            ConsultationCategoryDeterministicMatch crossDomain,

            ConsultationCategoryDeterministicMatch turningPoint

    ) {

        StringBuilder builder =
                new StringBuilder();

        builder
                .append(
                        sajuYear
                )
                .append(
                        "년의 최종 deterministic 종합 방향은 "
                )
                .append(
                        bigFlow.status()
                )
                .append(
                        "입니다."
                );

        if (
                primaryActive != null
        ) {

            builder
                    .append(
                            " PRIMARY_ACTIVE_DOMAIN="
                    )
                    .append(
                            primaryActiveDomain
                    )
                    .append(
                            "("
                    )
                    .append(
                            primaryActive.status()
                    )
                    .append(
                            ")."
                    );
        }

        if (
                primaryPressureDomain != null
        ) {

            builder
                    .append(
                            " PRIMARY_PRESSURE_DOMAIN="
                    )
                    .append(
                            primaryPressureDomain
                    )
                    .append(
                            "."
                    );
        }

        if (
                crossDomain != null
        ) {

            builder
                    .append(
                            " CROSS_DOMAIN_STATUS="
                    )
                    .append(
                            crossDomain.status()
                    )
                    .append(
                            ", CROSS_DOMAIN_PARTICIPANTS="
                    )
                    .append(
                            crossDomain.sourceDomains()
                    )
                    .append(
                            "."
                    );
        }

        if (
                turningPoint != null
        ) {

            builder
                    .append(
                            " TURNING_POINT=true."
                    );
        }

        return builder
                .toString();
    }
}