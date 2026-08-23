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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * PAST_LUCK / FUTURE_LUCK
 * TIMELINE_YEAR TURNING_POINT
 * deterministic Synthesis Rule.
 *
 * =================================================
 * Phase
 * =================================================
 *
 * phase = 100
 *
 * 이 Rule은 앞 단계에서 Java가 이미 계산한:
 *
 * - BIG_FLOW
 * - PRIMARY_ACTIVE_DOMAIN
 * - PRIMARY_PRESSURE_DOMAIN
 * - CROSS_DOMAIN_PATTERN
 *
 * 을 입력으로 사용한다.
 *
 * =================================================
 * TURNING_POINT의 의미
 * =================================================
 *
 * TURNING_POINT는
 * 현재 연도의 Status가 MIXED라는 이유만으로
 * 생성되지 않는다.
 *
 * 반드시:
 *
 * 이전 명리연도
 * 현재 명리연도
 * 다음 명리연도
 *
 * 세 연도의 deterministic 구조를
 * Java에서 직접 비교한다.
 *
 * =================================================
 * 비교 Component
 * =================================================
 *
 * 1. BIG_FLOW
 *
 * 2. PRIMARY_ACTIVE_DOMAIN
 *
 * 3. PRIMARY_PRESSURE_DOMAIN
 *
 * 4. CROSS_DOMAIN_PATTERN
 *
 * =================================================
 * 문자열 해석 금지
 * =================================================
 *
 * summary 또는 reasons 문자열을 파싱해서
 * Domain이나 Pattern을 추론하지 않는다.
 *
 * 각 deterministic Match의:
 *
 * - status
 * - sourceDomainStatuses
 *
 * 를 구조적으로 비교한다.
 *
 * =================================================
 * Major Structural Change
 * =================================================
 *
 * 첫 번째 연도에서 두 번째 연도로:
 *
 * A.
 * BIG_FLOW 자체가 변경되었거나
 *
 * 또는
 *
 * B.
 * 네 Component 가운데
 * 최소 2개가 변경된 경우
 *
 * Major Structural Change로 본다.
 *
 * =================================================
 * TURNING_POINT 유형
 * =================================================
 *
 * 1. TRANSITION_ENTRY
 *
 * 이전 -> 현재에서
 * Major Structural Change가 존재하고
 *
 * 현재 구조와 다음 연도 구조 사이에
 * structural continuity가 확인되는 경우.
 *
 * 즉:
 *
 * 현재 연도에서 새롭게 형성된 구조가
 * 다음 연도에도 일정 수준 유지되는
 * 구조적 전환 진입이다.
 *
 * -------------------------------------------------
 *
 * 2. TEMPORARY_PIVOT
 *
 * 이전 -> 현재에서
 * Major Structural Change가 존재하고
 *
 * 현재 -> 다음에서도
 * 다시 Major Structural Change가 존재하며
 *
 * 이전 구조와 다음 구조 사이에는
 * structural continuity가 확인되는 경우.
 *
 * 즉:
 *
 * 현재 연도가 앞뒤 연도와 구분되는
 * 일시적인 구조적 Pivot이다.
 *
 * =================================================
 * Continuity
 * =================================================
 *
 * BIG_FLOW가 동일하고
 * 나머지 Component 중 최소 하나가 동일하면
 * continuity를 인정한다.
 *
 * 또는:
 *
 * BIG_FLOW가 다르더라도
 * 나머지 Component 가운데
 * 최소 두 개가 동일하면
 * structural continuity를 인정한다.
 *
 * -------------------------------------------------
 *
 * 양쪽 모두 존재하지 않는 Optional Component는
 * continuity 근거로 사용하지 않는다.
 *
 * =================================================
 * Range Edge
 * =================================================
 *
 * 첫 연도 또는 마지막 연도처럼
 * 이전/다음 연도를 모두 비교할 수 없는 경우
 * TURNING_POINT를 생성하지 않는다.
 *
 * Range 밖의 연도 구조를
 * 임의로 추정하지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * TURNING_POINT의 status는
 * "전환의 강도"를 의미하지 않는다.
 *
 * 해당 현재 명리연도의
 * BIG_FLOW 공식 status를 그대로 보존한다.
 *
 * 따라서:
 *
 * SUPPORTIVE TURNING_POINT
 * PRESSURE TURNING_POINT
 * MIXED TURNING_POINT
 * CONTEXTUAL TURNING_POINT
 *
 * 모두 가능하다.
 *
 * =================================================
 * 허용되는 연도 간 관계
 * =================================================
 *
 * 이 Rule의:
 *
 * - 이전 -> 현재 구조 변경
 * - 현재 -> 다음 구조 유지
 * - 현재 -> 다음 구조 재변경
 * - 이전과 다음 구조의 continuity
 *
 * 는 Java가 직접 계산한 공식 관계다.
 *
 * 따라서 이 계산 결과 범위 안에서만
 * "구조가 전환되었다",
 * "다음 연도까지 구조가 유지된다",
 * "현재 연도만 구조적으로 구분된다"
 * 같은 표현을 사용할 수 있다.
 *
 * =================================================
 * 금지
 * =================================================
 *
 * 이 Rule은 다음을 생성하지 않는다.
 *
 * - 실제 사건 발생 예측
 * - 인생이 바뀐다고 단정
 * - 이직/결혼/손실 등 사건 생성
 * - 연도가 좋아진다/나빠진다라는 새 판정
 * - Match 개수 기반 강도 계산
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - Range 밖 연도 추정
 * - 계산되지 않은 강화/약화/상쇄 관계
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
     * Component Rule Codes
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

            List<ConsultationCategoryDeterministicMatch> priorMatches

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

        /*
         * TURNING_POINT는
         * 이전 / 현재 / 다음
         * 세 연도가 모두 필요하다.
         */

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

            ConsultationCategoryTimelineYearEvidence normalizedYearEvidence =
                    Objects.requireNonNull(
                            yearEvidence,
                            flowLabel()
                                    + " TURNING_POINT TimelineYearEvidence는 null일 수 없습니다."
                    );

            int currentYear =
                    normalizedYearEvidence
                            .sajuYear();

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
             * BIG_FLOW가 없어 구조가 만들어지지 않은 연도는
             * TURNING_POINT 비교에 사용하지 않는다.
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

            List<ConsultationCategoryDeterministicMatch> priorMatches

    ) {

        Objects.requireNonNull(
                view,
                flowLabel()
                        + " TURNING_POINT YearStructure 생성에는 View가 필요합니다."
        );

        Objects.requireNonNull(
                priorMatches,
                flowLabel()
                        + " TURNING_POINT YearStructure 생성에는 priorMatches가 필요합니다."
        );

        Map<Integer, YearStructure> result =
                new LinkedHashMap<>();

        for (
                ConsultationCategoryTimelineYearEvidence yearEvidence
                : view.timelineYearEvidences()
        ) {

            ConsultationCategoryTimelineYearEvidence normalizedYearEvidence =
                    Objects.requireNonNull(
                            yearEvidence,
                            flowLabel()
                                    + " TURNING_POINT TimelineYearEvidence는 null일 수 없습니다."
                    );

            int year =
                    normalizedYearEvidence
                            .sajuYear();

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
             * BIG_FLOW가 없는 연도는
             * 공식 YearStructure를 구성하지 않는다.
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

            YearStructure previous =
                    result.put(
                            year,
                            new YearStructure(
                                    normalizedYearEvidence,
                                    bigFlow,
                                    primaryActive,
                                    primaryPressure,
                                    crossDomain
                            )
                    );

            if (
                    previous != null
            ) {

                throw new IllegalStateException(
                        flowLabel()
                                + " TURNING_POINT YearStructure의 sajuYear가 중복되었습니다. year="
                                + year
                );
            }
        }

        return Map.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Unique Prior Result
     * =================================================
     */

    private Optional<ConsultationCategoryDeterministicMatch>
    findUnique(

            List<ConsultationCategoryDeterministicMatch> priorMatches,

            String ruleCode,

            int sajuYear

    ) {

        Objects.requireNonNull(
                priorMatches,
                flowLabel()
                        + " TURNING_POINT priorMatches가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        ruleCode,
                        flowLabel()
                                + " TURNING_POINT component ruleCode가 필요합니다."
                );

        ConsultationCategoryDeterministicMatch found =
                null;

        for (
                ConsultationCategoryDeterministicMatch match
                : priorMatches
        ) {

            ConsultationCategoryDeterministicMatch normalized =
                    Objects.requireNonNull(
                            match,
                            flowLabel()
                                    + " TURNING_POINT priorMatch는 null일 수 없습니다."
                    );

            if (
                    normalized.category()
                            != category()
            ) {

                continue;
            }

            if (
                    normalized.layer()
                            != ConsultationCategoryDeterministicLayer.TIMELINE_YEAR
            ) {

                continue;
            }

            if (
                    !normalized.ruleCode()
                            .equals(
                                    normalizedRuleCode
                            )
            ) {

                continue;
            }

            if (
                    normalized.sajuYear() == null
                            || normalized.sajuYear()
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
                                + normalizedRuleCode
                                + ", sajuYear="
                                + sajuYear
                );
            }

            found =
                    normalized;
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

        Objects.requireNonNull(
                previous,
                "TURNING_POINT previous YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                current,
                "TURNING_POINT current YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                next,
                "TURNING_POINT next YearStructure가 필요합니다."
        );

        StructuralChange previousToCurrent =
                compare(
                        previous,
                        current
                );

        /*
         * 이전 -> 현재에서
         * Major Structural Change가 없으면
         * 현재 연도를 Turning Point로 보지 않는다.
         */

        if (
                !previousToCurrent
                        .majorChange()
        ) {

            return null;
        }

        /*
         * =================================================
         * TRANSITION_ENTRY
         * =================================================
         *
         * 이전 -> 현재:
         * Major Structural Change
         *
         * 현재 -> 다음:
         * Structural continuity
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
         * TEMPORARY_PIVOT
         * =================================================
         *
         * 이전 -> 현재:
         * Major Structural Change
         *
         * 현재 -> 다음:
         * 다시 Major Structural Change
         *
         * 이전 <-> 다음:
         * Structural continuity
         */

        if (
                currentToNext
                        .majorChange()
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

        Objects.requireNonNull(
                first,
                "TURNING_POINT 비교의 first YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "TURNING_POINT 비교의 second YearStructure가 필요합니다."
        );

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
                changedComponents
                        .contains(
                                "BIG_FLOW"
                        );

        /*
         * 공식 Major Change 기준:
         *
         * BIG_FLOW 자체가 바뀌거나
         * 전체 Component 중 2개 이상이 바뀐다.
         */

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
     * Structural Continuity
     * =================================================
     */

    private boolean continuity(

            YearStructure first,

            YearStructure second

    ) {

        Objects.requireNonNull(
                first,
                "TURNING_POINT continuity first YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "TURNING_POINT continuity second YearStructure가 필요합니다."
        );

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
         * 나머지 구조 중 최소 하나가 같이 유지되어야
         * structural continuity로 인정한다.
         */

        if (
                bigFlowEqual
                        && otherEqualCount >= 1
        ) {

            return true;
        }

        /*
         * BIG_FLOW가 다르더라도
         * 나머지 구조 중 최소 두 개가 동일하면
         * structural continuity로 인정한다.
         */

        return otherEqualCount
                >= 2;
    }


    /*
     * =================================================
     * Component Change
     * =================================================
     */

    private boolean componentChanged(

            ConsultationCategoryDeterministicMatch first,

            ConsultationCategoryDeterministicMatch second

    ) {

        /*
         * 양쪽 모두 없는 Optional Component는
         * 변경으로 보지 않는다.
         */

        if (
                first == null
                        && second == null
        ) {

            return false;
        }

        /*
         * 한쪽만 존재하면
         * 구조의 등장 또는 소멸이므로
         * 변경으로 본다.
         */

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
     * 양쪽 모두 absent인 것은
     * continuity 근거로 사용하지 않는다.
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
     * summary / reasons 문자열을 사용하지 않는다.
     *
     * 공식 구조:
     *
     * - Category deterministic status
     * - sourceDomainStatuses
     *
     * 만 비교한다.
     */

    private ComponentSignature signatureOf(
            ConsultationCategoryDeterministicMatch match
    ) {

        Objects.requireNonNull(
                match,
                "TURNING_POINT ComponentSignature 생성에는 deterministic Match가 필요합니다."
        );

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

        Objects.requireNonNull(
                previous,
                "TURNING_POINT Match 생성에는 previous YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                current,
                "TURNING_POINT Match 생성에는 current YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                next,
                "TURNING_POINT Match 생성에는 next YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                turningPointType,
                "TURNING_POINT Match 생성에는 TurningPointType이 필요합니다."
        );

        List<LifeDomainRuleMatch> sourceMatches =
                currentSourceMatches(
                        current
                );

        /*
         * TURNING_POINT의 raw sourceMatches는
         * 현재 연도 구조의 실제 LifeDomain Source를 보존한다.
         *
         * 전년도/다음년도 구조는
         * dependencies에 구조적으로 보존한다.
         */

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

        /*
         * TURNING_POINT 자체의 status는
         * 전환 강도나 길흉을 새로 계산하지 않는다.
         *
         * 현재 연도 BIG_FLOW status를 그대로 보존한다.
         */

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

        Objects.requireNonNull(
                current,
                "TURNING_POINT currentSourceMatches에는 YearStructure가 필요합니다."
        );

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

                LifeDomainRuleMatch normalized =
                        Objects.requireNonNull(
                                sourceMatch,
                                "TURNING_POINT current sourceMatch는 null일 수 없습니다."
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
     * Dependencies
     * =================================================
     *
     * 전년도 / 현재년도 / 다음년도에서
     * TURNING_POINT 계산에 실제 사용한
     * deterministic Component를 보존한다.
     */

    private List<ConsultationCategoryDeterministicDependencyReference>
    dependenciesOf(

            YearStructure previous,

            YearStructure current,

            YearStructure next

    ) {

        Objects.requireNonNull(
                previous,
                "TURNING_POINT dependencies에는 previous YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                current,
                "TURNING_POINT dependencies에는 current YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                next,
                "TURNING_POINT dependencies에는 next YearStructure가 필요합니다."
        );

        List<ConsultationCategoryDeterministicDependencyReference> result =
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

            List<ConsultationCategoryDeterministicDependencyReference> result,

            YearStructure structure

    ) {

        Objects.requireNonNull(
                result,
                "TURNING_POINT dependency 결과 목록이 필요합니다."
        );

        Objects.requireNonNull(
                structure,
                "TURNING_POINT dependency YearStructure가 필요합니다."
        );

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

        Objects.requireNonNull(
                previous,
                "TURNING_POINT reason 생성에는 previous YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                current,
                "TURNING_POINT reason 생성에는 current YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                next,
                "TURNING_POINT reason 생성에는 next YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                turningPointType,
                "TURNING_POINT reason 생성에는 TurningPointType이 필요합니다."
        );

        Objects.requireNonNull(
                previousToCurrent,
                "TURNING_POINT reason 생성에는 previousToCurrent 구조 변화가 필요합니다."
        );

        Objects.requireNonNull(
                currentToNext,
                "TURNING_POINT reason 생성에는 currentToNext 구조 변화가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + " TURNING_POINT는 단일 연도의 MIXED 여부나 Match 개수로 생성하지 않고 이전·현재·다음 명리연도의 BIG_FLOW, PRIMARY_ACTIVE_DOMAIN, PRIMARY_PRESSURE_DOMAIN, CROSS_DOMAIN_PATTERN 구조를 Java에서 직접 비교합니다."
        );

        reasons.add(
                "TURNING_POINT 비교에서는 summary 또는 reasons 문자열을 파싱하지 않고 각 deterministic Match의 status와 sourceDomainStatuses 구조만 사용했습니다."
        );

        reasons.add(
                previous.sajuYear()
                        + " -> "
                        + current.sajuYear()
                        + " 구조 변경 Component="
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
                        + " 구조 변경 Component="
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
                            "이전 연도에서 현재 연도로 Major Structural Change가 확인되었고 현재 구조와 다음 명리연도 구조 사이에 deterministic continuity가 확인되어 TRANSITION_ENTRY로 분류했습니다."
                    );

            case TEMPORARY_PIVOT ->
                    reasons.add(
                            "이전 연도에서 현재 연도로 Major Structural Change가 확인되고 현재에서 다음 연도로 다시 Major Structural Change가 확인되었으며 이전 연도와 다음 연도 구조 사이에 deterministic continuity가 확인되어 TEMPORARY_PIVOT으로 분류했습니다."
                    );
        }

        reasons.add(
                "TURNING_POINT의 status는 전환의 강도나 새로운 길흉 점수가 아니며 현재 명리연도 BIG_FLOW의 공식 deterministic status "
                        + current.bigFlow().status()
                        + "를 그대로 보존합니다."
        );

        reasons.add(
                "이 TURNING_POINT는 Java가 계산한 연도 구조 변화만 나타내며 이직·결혼·손실·이별 등 구체적 사건의 발생을 의미하지 않습니다."
        );

        reasons.add(
                "첫 연도와 마지막 연도처럼 이전·다음 연도를 모두 비교할 수 없는 Range Edge에서는 범위 밖 구조를 추정하여 TURNING_POINT를 생성하지 않습니다."
        );

        if (
                previous.yearEvidence()
                        .timelineUncertaintyExists()
                        || current.yearEvidence()
                        .timelineUncertaintyExists()
                        || next.yearEvidence()
                        .timelineUncertaintyExists()
        ) {

            reasons.add(
                    "비교에 사용된 세 명리연도 가운데 하나 이상에 Timeline uncertainty가 존재하므로 TURNING_POINT를 특정 사건 발생이나 확정적인 인생 변화로 해석하지 않습니다."
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
     *
     * Java가 실제 계산한
     * TurningPointType만 서술한다.
     *
     * 새로운 사건이나 원인을 추가하지 않는다.
     */

    private String summaryOf(

            YearStructure current,

            TurningPointType type

    ) {

        Objects.requireNonNull(
                current,
                "TURNING_POINT summary에는 current YearStructure가 필요합니다."
        );

        Objects.requireNonNull(
                type,
                "TURNING_POINT summary에는 TurningPointType이 필요합니다."
        );

        return switch (
                type
                ) {

            case TRANSITION_ENTRY ->
                    current.sajuYear()
                            + "년은 이전 명리연도와 비교해 주요 구조 변화가 확인되고 그 구조가 다음 명리연도에도 일정 수준 유지되는 TRANSITION_ENTRY 구조입니다.";

            case TEMPORARY_PIVOT ->
                    current.sajuYear()
                            + "년은 이전·다음 명리연도와 구분되는 구조가 일시적으로 형성되는 TEMPORARY_PIVOT 구조입니다.";
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
                    status,
                    "TURNING_POINT ComponentSignature에는 status가 필요합니다."
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

            ConsultationCategoryTimelineYearEvidence yearEvidence,

            ConsultationCategoryDeterministicMatch bigFlow,

            ConsultationCategoryDeterministicMatch primaryActive,

            ConsultationCategoryDeterministicMatch primaryPressure,

            ConsultationCategoryDeterministicMatch crossDomain

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

            if (
                    bigFlow.sajuYear() == null
                            || bigFlow.sajuYear()
                            != yearEvidence.sajuYear()
            ) {

                throw new IllegalArgumentException(
                        "TURNING_POINT YearStructure의 BIG_FLOW 연도와 TimelineYearEvidence 연도가 일치해야 합니다. evidenceYear="
                                + yearEvidence.sajuYear()
                                + ", bigFlowYear="
                                + bigFlow.sajuYear()
                );
            }
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