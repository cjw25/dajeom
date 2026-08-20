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
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * PAST_LUCK / FUTURE_LUCK
 * TIMELINE_YEAR TURNING_POINT
 * 공통 Synthesis Rule.
 *
 * =================================================
 * Phase
 * =================================================
 *
 * phase = 100
 *
 * 입력으로 사용하는 기존 계산:
 *
 * BIG_FLOW
 *
 * PRIMARY_ACTIVE_DOMAIN
 *
 * PRIMARY_PRESSURE_DOMAIN
 *
 * CROSS_DOMAIN_PATTERN
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * TURNING_POINT는:
 *
 * 한 해의 상태가 MIXED라는 이유로
 * 생성하지 않는다.
 *
 * 반드시:
 *
 * 이전 명리연도
 * 현재 명리연도
 * 다음 명리연도
 *
 * 세 연도의 구조를 비교한다.
 *
 * =================================================
 * 구조 Component
 * =================================================
 *
 * 1.
 * BIG_FLOW
 *
 * 2.
 * PRIMARY_ACTIVE_DOMAIN
 *
 * 3.
 * PRIMARY_PRESSURE_DOMAIN
 *
 * 4.
 * CROSS_DOMAIN_PATTERN
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * summary/reasons의 텍스트를 파싱해서
 * Domain이나 Pattern을 추출하지 않는다.
 *
 * 각 Deterministic Match의:
 *
 * - status
 * - source Domain
 * - source LifeDomain status
 *
 * 를 구조적으로 비교한다.
 *
 * =================================================
 * Major Structural Change
 * =================================================
 *
 * 이전 -> 현재 변화가:
 *
 * A.
 * BIG_FLOW 자체가 변경
 *
 * 또는
 *
 * B.
 * 4개 Component 중
 * 최소 2개가 변경
 *
 * 된 경우에만
 * Major Change로 본다.
 *
 * =================================================
 * TURNING_POINT 유형
 * =================================================
 *
 * 1.
 * TRANSITION_ENTRY
 *
 * 이전 -> 현재에서 Major Change
 *
 * +
 *
 * 현재 구조가 다음 연도까지
 * 의미 있게 이어짐.
 *
 * ->
 *
 * 새로운 구조 구간으로 들어가는
 * 전환 진입점.
 *
 * =================================================
 *
 * 2.
 * TEMPORARY_PIVOT
 *
 * 이전 -> 현재 Major Change
 *
 * +
 *
 * 현재 -> 다음도 Major Change
 *
 * +
 *
 * 이전과 다음 구조가
 * 의미 있게 다시 연결됨.
 *
 * ->
 *
 * 현재 연도만 별도 구조로 튀는
 * 일시적 Pivot.
 *
 * =================================================
 * Continuity
 * =================================================
 *
 * BIG_FLOW가 같으면서
 * 다른 Component 하나 이상도 같으면
 * continuity 인정.
 *
 * 또는:
 *
 * BIG_FLOW가 달라도
 * 나머지 Component 중
 * 최소 2개가 같으면
 * continuity 인정.
 *
 * =================================================
 *
 * 모두 없는 Optional Component끼리는
 * continuity 근거로 세지 않는다.
 *
 * =================================================
 * Range Edge
 * =================================================
 *
 * 첫 연도와 마지막 연도는
 * 이전/다음 양쪽 비교가 불가능하므로
 * TURNING_POINT를 생성하지 않는다.
 *
 * 임의로 Range 바깥 구조를 추정하지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * TURNING_POINT의 status는
 * "전환점 강도"가 아니다.
 *
 * 현재 연도의 BIG_FLOW status를
 * 그대로 보존한다.
 *
 * 따라서:
 *
 * SUPPORTIVE 전환점
 * PRESSURE 전환점
 * MIXED 전환점
 * CONTEXTUAL 전환점
 *
 * 모두 가능하다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 사건 발생 예측
 * - 인생이 바뀐다고 단정
 * - 이직/결혼/손실 등 사건 생성
 * - Match 개수 비교
 * - 문자열 summary 파싱
 * - Range 밖 연도 추정
 */
public abstract class AbstractTimelineYearTurningPointRule
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

        return 100;
    }


    /*
     * =================================================
     * Rule Code
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
                        + " TURNING_POINT에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        Objects.requireNonNull(
                priorMatches,
                flowLabel()
                        + " TURNING_POINT에는 prior deterministic matches가 필요합니다."
        );

        if (
                view.category()
                        != category()
        ) {

            throw new IllegalArgumentException(
                    flowLabel()
                            + " TURNING_POINT Rule은 "
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

        Map<Integer, YearStructure> structures =
                buildYearStructures(
                        view,
                        priorMatches
                );

        if (
                structures.size()
                        < 3
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                ConsultationCategoryTimelineYearEvidence yearEvidence
                : view.timelineYearEvidences()
        ) {

            int currentYear =
                    yearEvidence.sajuYear();

            YearStructure previous =
                    structures.get(
                            currentYear - 1
                    );

            YearStructure current =
                    structures.get(
                            currentYear
                    );

            YearStructure next =
                    structures.get(
                            currentYear + 1
                    );

            /*
             * Range Edge 또는
             * BIG_FLOW가 없는 연도는 추정하지 않는다.
             */

            if (
                    previous == null
                            || current == null
                            || next == null
            ) {

                continue;
            }

            TurningPointType turningPointType =
                    classifyTurningPoint(
                            previous,
                            current,
                            next
                    );

            if (
                    turningPointType == null
            ) {

                continue;
            }

            result.add(
                    createMatch(
                            previous,
                            current,
                            next,
                            turningPointType
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Year Structures
     * =================================================
     */

    private Map<Integer, YearStructure> buildYearStructures(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    priorMatches

    ) {

        Map<Integer, YearStructure> result =
                new LinkedHashMap<>();

        for (
                ConsultationCategoryTimelineYearEvidence yearEvidence
                : view.timelineYearEvidences()
        ) {

            int year =
                    yearEvidence.sajuYear();

            ConsultationCategoryDeterministicMatch bigFlow =
                    findUnique(
                            priorMatches,
                            bigFlowRuleCode(),
                            year
                    )
                            .orElse(
                                    null
                            );

            /*
             * BIG_FLOW가 없으면
             * 그 연도 구조를 TURNING_POINT 비교에
             * 사용하지 않는다.
             */

            if (
                    bigFlow == null
            ) {

                continue;
            }

            ConsultationCategoryDeterministicMatch primaryActive =
                    findUnique(
                            priorMatches,
                            primaryActiveRuleCode(),
                            year
                    )
                            .orElse(
                                    null
                            );

            ConsultationCategoryDeterministicMatch primaryPressure =
                    findUnique(
                            priorMatches,
                            primaryPressureRuleCode(),
                            year
                    )
                            .orElse(
                                    null
                            );

            ConsultationCategoryDeterministicMatch crossDomain =
                    findUnique(
                            priorMatches,
                            crossDomainRuleCode(),
                            year
                    )
                            .orElse(
                                    null
                            );

            result.put(
                    year,
                    new YearStructure(
                            yearEvidence,
                            bigFlow,
                            primaryActive,
                            primaryPressure,
                            crossDomain
                    )
            );
        }

        return Map.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Unique Result
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
                                + " TURNING_POINT 계산에서 동일 Rule/Year deterministic 결과가 중복되었습니다. rule="
                                + ruleCode
                                + ", sajuYear="
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
     * Turning Point Classification
     * =================================================
     */

    private TurningPointType classifyTurningPoint(

            YearStructure previous,

            YearStructure current,

            YearStructure next

    ) {

        StructuralChange previousToCurrent =
                compare(
                        previous,
                        current
                );

        if (
                !previousToCurrent.majorChange()
        ) {

            return null;
        }

        /*
         * =================================================
         * 새로운 구조로 진입
         * =================================================
         */

        if (
                continuity(
                        current,
                        next
                )
        ) {

            return TurningPointType
                    .TRANSITION_ENTRY;
        }

        StructuralChange currentToNext =
                compare(
                        current,
                        next
                );

        /*
         * =================================================
         * 일시적 Pivot
         * =================================================
         *
         * 이전 -> 현재 큰 변화
         *
         * 현재 -> 다음도 큰 변화
         *
         * 이전과 다음은 다시 연결.
         */

        if (
                currentToNext.majorChange()
                        && continuity(
                        previous,
                        next
                )
        ) {

            return TurningPointType
                    .TEMPORARY_PIVOT;
        }

        return null;
    }


    /*
     * =================================================
     * Structural Compare
     * =================================================
     */

    private StructuralChange compare(

            YearStructure first,

            YearStructure second

    ) {

        List<String> changedComponents =
                new ArrayList<>();

        if (
                componentChanged(
                        first.bigFlow(),
                        second.bigFlow()
                )
        ) {

            changedComponents.add(
                    "BIG_FLOW"
            );
        }

        if (
                componentChanged(
                        first.primaryActive(),
                        second.primaryActive()
                )
        ) {

            changedComponents.add(
                    "PRIMARY_ACTIVE_DOMAIN"
            );
        }

        if (
                componentChanged(
                        first.primaryPressure(),
                        second.primaryPressure()
                )
        ) {

            changedComponents.add(
                    "PRIMARY_PRESSURE_DOMAIN"
            );
        }

        if (
                componentChanged(
                        first.crossDomain(),
                        second.crossDomain()
                )
        ) {

            changedComponents.add(
                    "CROSS_DOMAIN_PATTERN"
            );
        }

        boolean bigFlowChanged =
                changedComponents.contains(
                        "BIG_FLOW"
                );

        boolean majorChange =
                bigFlowChanged
                        || changedComponents.size() >= 2;

        return new StructuralChange(
                List.copyOf(
                        changedComponents
                ),
                bigFlowChanged,
                majorChange
        );
    }


    /*
     * =================================================
     * Continuity
     * =================================================
     */

    private boolean continuity(

            YearStructure first,

            YearStructure second

    ) {

        boolean bigFlowEqual =
                componentMeaningfullyEqual(
                        first.bigFlow(),
                        second.bigFlow()
                );

        int otherEqualCount =
                0;

        if (
                componentMeaningfullyEqual(
                        first.primaryActive(),
                        second.primaryActive()
                )
        ) {

            otherEqualCount++;
        }

        if (
                componentMeaningfullyEqual(
                        first.primaryPressure(),
                        second.primaryPressure()
                )
        ) {

            otherEqualCount++;
        }

        if (
                componentMeaningfullyEqual(
                        first.crossDomain(),
                        second.crossDomain()
                )
        ) {

            otherEqualCount++;
        }

        /*
         * BIG_FLOW가 같더라도
         * 다른 구조 하나 이상이 같이 이어져야
         * 새로운 구조의 continuity로 본다.
         */

        if (
                bigFlowEqual
                        && otherEqualCount >= 1
        ) {

            return true;
        }

        /*
         * BIG_FLOW는 달라도
         * 나머지 구조 둘 이상이 동일하면
         * structural continuity로 인정한다.
         */

        return otherEqualCount
                >= 2;
    }


    /*
     * =================================================
     * Component
     * =================================================
     */

    private boolean componentChanged(

            ConsultationCategoryDeterministicMatch first,

            ConsultationCategoryDeterministicMatch second

    ) {

        if (
                first == null
                        && second == null
        ) {

            return false;
        }

        if (
                first == null
                        || second == null
        ) {

            return true;
        }

        return !signatureOf(
                first
        )
                .equals(
                        signatureOf(
                                second
                        )
                );
    }


    /*
     * =================================================
     * Meaningful Equality
     * =================================================
     *
     * 둘 다 absent인 것은
     * continuity 근거가 아니다.
     */

    private boolean componentMeaningfullyEqual(

            ConsultationCategoryDeterministicMatch first,

            ConsultationCategoryDeterministicMatch second

    ) {

        if (
                first == null
                        || second == null
        ) {

            return false;
        }

        return signatureOf(
                first
        )
                .equals(
                        signatureOf(
                                second
                        )
                );
    }


    /*
     * =================================================
     * Structured Signature
     * =================================================
     *
     * summary 문자열을 사용하지 않는다.
     */

    private ComponentSignature signatureOf(

            ConsultationCategoryDeterministicMatch match

    ) {

        ConsultationCategoryDeterministicDependencyReference reference =
                ConsultationCategoryDeterministicDependencyReference
                        .of(
                                match
                        );

        return new ComponentSignature(
                match.status(),
                reference.sourceDomainStatuses()
        );
    }


    /*
     * =================================================
     * Create Match
     * =================================================
     */

    private ConsultationCategoryDeterministicMatch createMatch(

            YearStructure previous,

            YearStructure current,

            YearStructure next,

            TurningPointType turningPointType

    ) {

        List<LifeDomainRuleMatch> sourceMatches =
                currentSourceMatches(
                        current
                );

        if (
                sourceMatches.isEmpty()
        ) {

            throw new IllegalStateException(
                    flowLabel()
                            + " TURNING_POINT 현재 연도에 보존할 LifeDomain sourceMatch가 없습니다. year="
                            + current.sajuYear()
            );
        }

        List<ConsultationCategoryDeterministicDependencyReference>
                dependencies =
                dependenciesOf(
                        previous,
                        current,
                        next
                );

        StructuralChange previousToCurrent =
                compare(
                        previous,
                        current
                );

        StructuralChange currentToNext =
                compare(
                        current,
                        next
                );

        List<String> reasons =
                buildReasons(
                        previous,
                        current,
                        next,
                        turningPointType,
                        previousToCurrent,
                        currentToNext
                );

        return new ConsultationCategoryDeterministicMatch(
                category(),
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                code(),
                current.bigFlow().status(),
                current.sajuYear(),
                null,
                current.yearEvidence().monthIndices(),
                summaryOf(
                        current,
                        turningPointType
                ),
                sourceMatches,
                reasons,
                dependencies
        );
    }


    /*
     * =================================================
     * Current Raw Sources
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    currentSourceMatches(

            YearStructure current

    ) {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                ConsultationCategoryDeterministicMatch component
                : current.components()
        ) {

            for (
                    LifeDomainRuleMatch sourceMatch
                    : component.sourceMatches()
            ) {

                if (
                        !result.contains(
                                sourceMatch
                        )
                ) {

                    result.add(
                            sourceMatch
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
     * Dependencies
     * =================================================
     */

    private List<ConsultationCategoryDeterministicDependencyReference>
    dependenciesOf(

            YearStructure previous,

            YearStructure current,

            YearStructure next

    ) {

        List<ConsultationCategoryDeterministicDependencyReference>
                result =
                new ArrayList<>();

        appendDependencies(
                result,
                previous
        );

        appendDependencies(
                result,
                current
        );

        appendDependencies(
                result,
                next
        );

        return List.copyOf(
                result
        );
    }


    private void appendDependencies(

            List<ConsultationCategoryDeterministicDependencyReference>
                    result,

            YearStructure structure

    ) {

        for (
                ConsultationCategoryDeterministicMatch component
                : structure.components()
        ) {

            ConsultationCategoryDeterministicDependencyReference dependency =
                    ConsultationCategoryDeterministicDependencyReference
                            .of(
                                    component
                            );

            if (
                    !result.contains(
                            dependency
                    )
            ) {

                result.add(
                        dependency
                );
            }
        }
    }


    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            YearStructure previous,

            YearStructure current,

            YearStructure next,

            TurningPointType turningPointType,

            StructuralChange previousToCurrent,

            StructuralChange currentToNext

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + " TURNING_POINT는 단일연도의 MIXED 여부나 Match 개수로 생성하지 않고 전년도·해당연도·다음연도의 BIG_FLOW, PRIMARY_ACTIVE_DOMAIN, PRIMARY_PRESSURE_DOMAIN, CROSS_DOMAIN_PATTERN 구조를 비교합니다."
        );

        reasons.add(
                "TURNING_POINT 비교에는 summary/reasons 문자열을 파싱하지 않고 각 deterministic Match의 status와 실제 source LifeDomain/Status 구조만 사용합니다."
        );

        reasons.add(
                previous.sajuYear()
                        + " -> "
                        + current.sajuYear()
                        + " 변경 Component="
                        + previousToCurrent.changedComponents()
                        + ", bigFlowChanged="
                        + previousToCurrent.bigFlowChanged()
                        + ", majorChange="
                        + previousToCurrent.majorChange()
                        + "입니다."
        );

        reasons.add(
                current.sajuYear()
                        + " -> "
                        + next.sajuYear()
                        + " 변경 Component="
                        + currentToNext.changedComponents()
                        + ", bigFlowChanged="
                        + currentToNext.bigFlowChanged()
                        + ", majorChange="
                        + currentToNext.majorChange()
                        + "입니다."
        );

        switch (
                turningPointType
        ) {

            case TRANSITION_ENTRY ->
                    reasons.add(
                            "전년도에서 해당연도로 Major Structural Change가 발생하고 해당연도의 구조가 다음 명리연도까지 의미 있게 이어져 TRANSITION_ENTRY 유형의 TURNING_POINT로 분류합니다."
                    );

            case TEMPORARY_PIVOT ->
                    reasons.add(
                            "전년도에서 해당연도로 Major Structural Change가 발생하고 다음연도에 다시 큰 구조 변화가 나타나며 전년도와 다음연도의 구조적 continuity가 확인되어 TEMPORARY_PIVOT 유형의 TURNING_POINT로 분류합니다."
                    );
        }

        reasons.add(
                "TURNING_POINT의 status는 전환점 자체의 좋고 나쁨 점수가 아니라 해당 명리연도 BIG_FLOW의 공식 deterministic status "
                        + current.bigFlow().status()
                        + "를 그대로 사용합니다."
        );

        reasons.add(
                "첫 연도와 마지막 연도는 이전·다음 양쪽 비교가 불가능하므로 Range 밖 구조를 추정해 TURNING_POINT를 생성하지 않습니다."
        );

        if (
                previous.yearEvidence().timelineUncertaintyExists()
                        || current.yearEvidence().timelineUncertaintyExists()
                        || next.yearEvidence().timelineUncertaintyExists()
        ) {

            reasons.add(
                    "비교에 사용된 세 명리연도 중 하나 이상에 Timeline uncertainty가 존재하며 TURNING_POINT를 특정 사건 발생 또는 확정된 인생 변화로 해석하지 않습니다."
            );
        }

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

            YearStructure current,

            TurningPointType type

    ) {

        return switch (
                type
                ) {

            case TRANSITION_ENTRY ->
                    current.sajuYear()
                            + "년은 전년도와 비교해 주요 운 구조가 변경되고 그 변화가 다음 명리연도까지 이어지는 구조적 전환 진입점입니다.";

            case TEMPORARY_PIVOT ->
                    current.sajuYear()
                            + "년은 전후 명리연도와 구별되는 구조가 일시적으로 형성되는 전환성 Pivot입니다.";
        };
    }


    /*
     * =================================================
     * Component Signature
     * =================================================
     */

    private record ComponentSignature(

            ConsultationCategoryDeterministicStatus status,

            Map<
                    LifeDomain,
                    Set<LifeDomainSignalStatus>
                    > sourceDomainStatuses

    ) {

        private ComponentSignature {

            Objects.requireNonNull(
                    status
            );

            sourceDomainStatuses =
                    sourceDomainStatuses == null
                            ? Map.of()
                            : Map.copyOf(
                            sourceDomainStatuses
                    );
        }
    }


    /*
     * =================================================
     * Structural Change
     * =================================================
     */

    private record StructuralChange(

            List<String> changedComponents,

            boolean bigFlowChanged,

            boolean majorChange

    ) {

        private StructuralChange {

            changedComponents =
                    changedComponents == null
                            ? List.of()
                            : List.copyOf(
                            changedComponents
                    );
        }
    }


    /*
     * =================================================
     * Year Structure
     * =================================================
     */

    private record YearStructure(

            ConsultationCategoryTimelineYearEvidence
            yearEvidence,

            ConsultationCategoryDeterministicMatch
            bigFlow,

            ConsultationCategoryDeterministicMatch
            primaryActive,

            ConsultationCategoryDeterministicMatch
            primaryPressure,

            ConsultationCategoryDeterministicMatch
            crossDomain

    ) {

        private YearStructure {

            Objects.requireNonNull(
                    yearEvidence,
                    "TURNING_POINT YearStructure에는 yearEvidence가 필요합니다."
            );

            Objects.requireNonNull(
                    bigFlow,
                    "TURNING_POINT YearStructure에는 BIG_FLOW가 필요합니다."
            );
        }


        private int sajuYear() {

            return yearEvidence
                    .sajuYear();
        }


        private List<ConsultationCategoryDeterministicMatch>
        components() {

            List<ConsultationCategoryDeterministicMatch> result =
                    new ArrayList<>();

            result.add(
                    bigFlow
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

            return List.copyOf(
                    result
            );
        }
    }


    /*
     * =================================================
     * Turning Point Type
     * =================================================
     */

    private enum TurningPointType {

        TRANSITION_ENTRY,

        TEMPORARY_PIVOT
    }
}