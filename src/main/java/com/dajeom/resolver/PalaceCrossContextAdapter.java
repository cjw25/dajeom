package com.dajeom.resolver;

import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.EarthlyBranchCombinationIntegratedCandidate;
import com.dajeom.model.EarthlyBranchPairRelation;
import com.dajeom.model.EarthlyBranchTransformationMemberEffect;
import com.dajeom.model.HeavenlyStemCombinationTransformationEvidence;
import com.dajeom.model.HeavenlyStemPairRelation;
import com.dajeom.model.HeavenlyStemTransformationMemberEffect;
import com.dajeom.model.PalaceCrossContext;
import com.dajeom.model.PalaceRelationReference;
import com.dajeom.model.PalaceTransformationReference;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchRelationKind;
import com.dajeom.type.HeavenlyStemRelationKind;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalaceRelationLayer;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 12차-4 Boundary Adapter.
 *
 * 기존:
 *
 * 8차 RelationAnalysis
 * +
 * 9차 CombinationTransformationAnalysis
 *
 * 을:
 *
 * PalaceCrossContext
 *
 * 로 정규화한다.
 *
 * =================================================
 *
 * 원칙
 * =================================================
 *
 * 1.
 * 관계 공식을 다시 계산하지 않는다.
 *
 * 2.
 * RelationAnalysis가 이미 판정한 관계를
 * PalaceRelationReference로 변환한다.
 *
 * 3.
 * 하나의 Pair에 여러 관계가 중첩된 경우
 * 각각 별도의 Reference로 보존한다.
 *
 * 4.
 * 삼합계 / 방합계는 단순 Pair로 축약하지 않고
 * 9차까지 전달된 실제 candidatePositions를 이용해
 * 참여 Palace 집합을 보존한다.
 *
 * 5.
 * 합화 활성 여부와 원기능 감소 여부는
 * 9-9 MemberEffect만을 기준으로 한다.
 *
 * 6.
 * 합이 존재한다는 이유만으로
 * activated=true로 만들지 않는다.
 *
 * 7.
 * 시간 미상에서는 HOUR를 임의 생성하지 않는다.
 *
 * =================================================
 *
 * 주의
 * =================================================
 *
 * EarthlyBranchPairRelation은 8-10의 관계망 요약이다.
 *
 * 따라서 Pair 단위 충/형/파/해/육합은
 * PairRelation에서 변환하고,
 *
 * 삼합/방합처럼 복수 Palace 참여 구조는
 * 9차까지 전달된 candidatePositions를 이용해
 * 그룹 상태를 보존한다.
 *
 * 이 Adapter 자체에서는
 * COMPLETE / HALF / PARTIAL 등의 명리 판정을
 * 다시 수행하지 않는다.
 */
@Component
public class PalaceCrossContextAdapter {

    public PalaceCrossContext resolve(

            RelationAnalysis relationAnalysis,

            CombinationTransformationAnalysis
                    transformationAnalysis

    ) {

        Objects.requireNonNull(
                relationAnalysis,
                "8차 RelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                transformationAnalysis,
                "9차 CombinationTransformationAnalysis가 필요합니다."
        );

        validateIdentity(
                relationAnalysis,
                transformationAnalysis
        );

        List<PalaceRelationReference> relations =
                resolveRelations(
                        relationAnalysis,
                        transformationAnalysis
                );

        List<PalaceTransformationReference>
                transformations =
                resolveTransformations(
                        transformationAnalysis
                );

        return new PalaceCrossContext(
                relations,
                transformations,
                relationAnalysis.completeChart()
        );
    }

    /*
     * =================================================
     * 8차 관계
     * =================================================
     */

    private List<PalaceRelationReference> resolveRelations(

            RelationAnalysis relationAnalysis,

            CombinationTransformationAnalysis
                    transformationAnalysis

    ) {

        List<PalaceRelationReference> result =
                new ArrayList<>();

        /*
         * =================================================
         * 천간 관계
         * =================================================
         *
         * 한 Pair에:
         *
         * COMBINATION
         * +
         * CONTROL_ONLY
         *
         * 등이 중첩될 수 있으므로
         * relationKinds 전체를 각각 보존한다.
         */

        for (
                HeavenlyStemPairRelation pair
                : relationAnalysis.heavenlyStemPairRelations()
        ) {

            Set<PalacePosition> positions =
                    Set.of(
                            toPalacePosition(
                                    pair.firstPosition()
                            ),
                            toPalacePosition(
                                    pair.secondPosition()
                            )
                    );

            for (
                    HeavenlyStemRelationKind relationKind
                    : pair.relationKinds()
            ) {

                addRelationIfAbsent(
                        result,
                        new PalaceRelationReference(
                                PalaceRelationLayer.HEAVENLY_STEM,
                                positions,
                                relationKind.name(),
                                relationKind.koreanName()
                        )
                );
            }
        }

        /*
         * =================================================
         * 지지 Pair 관계
         * =================================================
         *
         * THREE_COMBINATION /
         * DIRECTIONAL_COMBINATION은
         * 아래에서 실제 candidatePositions를 사용해
         * 그룹 단위로 추가한다.
         *
         * 따라서 여기서는 Pair로 축약하지 않는다.
         */

        for (
                EarthlyBranchPairRelation pair
                : relationAnalysis.earthlyBranchPairRelations()
        ) {

            Set<PalacePosition> positions =
                    Set.of(
                            toPalacePosition(
                                    pair.firstPosition()
                            ),
                            toPalacePosition(
                                    pair.secondPosition()
                            )
                    );

            for (
                    EarthlyBranchRelationKind relationKind
                    : pair.relationKinds()
            ) {

                if (
                        relationKind.threeCombination()
                                || relationKind.directionalCombination()
                ) {

                    continue;
                }

                addRelationIfAbsent(
                        result,
                        new PalaceRelationReference(
                                PalaceRelationLayer.EARTHLY_BRANCH,
                                positions,
                                relationKind.name(),
                                relationKind.koreanName()
                        )
                );
            }
        }

        /*
         * =================================================
         * 삼합계 관계
         * =================================================
         *
         * 9-8 candidate는
         * 8차에서 시작된 후보가
         * 9-4~9-8을 거쳐 전달된 것이다.
         *
         * 여기서는 합화 성립 여부를 보는 것이 아니라
         * 참여 위치를 Pair로 쪼개지 않기 위해
         * candidatePositions만 사용한다.
         */

        for (
                EarthlyBranchCombinationIntegratedCandidate candidate
                : transformationAnalysis.threeCombinationCandidates()
        ) {

            Set<PalacePosition> positions =
                    toPalacePositions(
                            candidate.candidatePositions()
                    );

            if (
                    positions.size() < 2
            ) {

                continue;
            }

            addRelationIfAbsent(
                    result,
                    new PalaceRelationReference(
                            PalaceRelationLayer.EARTHLY_BRANCH,
                            positions,
                            buildGroupRelationCode(
                                    EarthlyBranchRelationKind
                                            .THREE_COMBINATION,
                                    positions
                            ),
                            EarthlyBranchRelationKind
                                    .THREE_COMBINATION
                                    .koreanName()
                    )
            );
        }

        /*
         * =================================================
         * 방합계 관계
         * =================================================
         */

        for (
                EarthlyBranchCombinationIntegratedCandidate candidate
                : transformationAnalysis
                .directionalCombinationCandidates()
        ) {

            Set<PalacePosition> positions =
                    toPalacePositions(
                            candidate.candidatePositions()
                    );

            if (
                    positions.size() < 2
            ) {

                continue;
            }

            addRelationIfAbsent(
                    result,
                    new PalaceRelationReference(
                            PalaceRelationLayer.EARTHLY_BRANCH,
                            positions,
                            buildGroupRelationCode(
                                    EarthlyBranchRelationKind
                                            .DIRECTIONAL_COMBINATION,
                                    positions
                            ),
                            EarthlyBranchRelationKind
                                    .DIRECTIONAL_COMBINATION
                                    .koreanName()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 9차 합화
     * =================================================
     */

    private List<PalaceTransformationReference>
    resolveTransformations(
            CombinationTransformationAnalysis analysis
    ) {

        List<PalaceTransformationReference> result =
                new ArrayList<>();

        /*
         * =================================================
         * 천간 합화
         * =================================================
         *
         * 9-3의 모든 최종 상태:
         *
         * TRANSFORMED
         * PARTIALLY_TRANSFORMED
         * CONDITIONAL
         * COMBINED_NOT_TRANSFORMED
         * SUPPRESSED
         *
         * 를 모두 보존한다.
         */

        List<HeavenlyStemCombinationTransformationEvidence>
                heavenlyStemEvidences =
                allHeavenlyStemTransformationEvidences(
                        analysis
                );

        for (
                HeavenlyStemCombinationTransformationEvidence evidence
                : heavenlyStemEvidences
        ) {

            Set<PalacePosition> positions =
                    Set.of(
                            toPalacePosition(
                                    evidence.firstPosition()
                            ),
                            toPalacePosition(
                                    evidence.secondPosition()
                            )
                    );

            List<HeavenlyStemTransformationMemberEffect>
                    effects =
                    analysis
                            .effectAnalysis()
                            .heavenlyStemEffects()
                            .stream()
                            .filter(
                                    effect ->
                                            effect.sourceEvidence()
                                                    .equals(
                                                            evidence
                                                    )
                            )
                            .toList();

            boolean activated =
                    effects
                            .stream()
                            .anyMatch(
                                    HeavenlyStemTransformationMemberEffect
                                            ::targetActivated
                            );

            boolean originalFunctionReduced =
                    effects
                            .stream()
                            .anyMatch(
                                    HeavenlyStemTransformationMemberEffect
                                            ::originalFunctionReduced
                            );

            PalaceTransformationReference reference =
                    new PalaceTransformationReference(
                            positions,

                            buildStemTransformationCode(
                                    evidence,
                                    positions
                            ),

                            buildStemTransformationName(
                                    evidence
                            ),

                            evidence
                                    .targetElement()
                                    .getKoreanName(),

                            activated,

                            originalFunctionReduced
                    );

            addTransformationIfAbsent(
                    result,
                    reference
            );
        }

        /*
         * =================================================
         * 지지 합화
         * =================================================
         *
         * SIX / THREE / DIRECTIONAL
         *
         * 모든 후보를 보존한다.
         *
         * effective()만 골라내지 않는다.
         *
         * suppressed / competing / deferred도
         * "후보가 존재했다"는 Evidence이므로
         * Boundary에서 삭제하지 않는다.
         */

        List<EarthlyBranchCombinationIntegratedCandidate>
                branchCandidates =
                allEarthlyBranchCandidates(
                        analysis
                );

        for (
                EarthlyBranchCombinationIntegratedCandidate candidate
                : branchCandidates
        ) {

            Set<PalacePosition> positions =
                    toPalacePositions(
                            candidate.candidatePositions()
                    );

            if (
                    positions.size() < 2
            ) {

                continue;
            }

            List<EarthlyBranchTransformationMemberEffect>
                    effects =
                    analysis
                            .effectAnalysis()
                            .earthlyBranchEffects()
                            .stream()
                            .filter(
                                    effect ->
                                            effect.sourceCandidate()
                                                    .equals(
                                                            candidate
                                                    )
                            )
                            .toList();

            boolean activated =
                    effects
                            .stream()
                            .anyMatch(
                                    EarthlyBranchTransformationMemberEffect
                                            ::targetActivated
                            );

            boolean originalFunctionReduced =
                    effects
                            .stream()
                            .anyMatch(
                                    EarthlyBranchTransformationMemberEffect
                                            ::originalFunctionReduced
                            );

            PalaceTransformationReference reference =
                    new PalaceTransformationReference(
                            positions,

                            buildBranchTransformationCode(
                                    candidate,
                                    positions
                            ),

                            buildBranchTransformationName(
                                    candidate
                            ),

                            candidate
                                    .targetElement()
                                    .getKoreanName(),

                            activated,

                            originalFunctionReduced
                    );

            addTransformationIfAbsent(
                    result,
                    reference
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 천간 합화 Evidence 전체
     * =================================================
     */

    private List<HeavenlyStemCombinationTransformationEvidence>
    allHeavenlyStemTransformationEvidences(
            CombinationTransformationAnalysis analysis
    ) {

        List<HeavenlyStemCombinationTransformationEvidence>
                result =
                new ArrayList<>();

        addUnique(
                result,
                analysis.transformedHeavenlyStemCombinations()
        );

        addUnique(
                result,
                analysis.partiallyTransformedHeavenlyStemCombinations()
        );

        addUnique(
                result,
                analysis.conditionalHeavenlyStemCombinations()
        );

        addUnique(
                result,
                analysis.combinedNotTransformedHeavenlyStemCombinations()
        );

        addUnique(
                result,
                analysis.suppressedHeavenlyStemCombinations()
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 지지 후보 전체
     * =================================================
     */

    private List<EarthlyBranchCombinationIntegratedCandidate>
    allEarthlyBranchCandidates(
            CombinationTransformationAnalysis analysis
    ) {

        List<EarthlyBranchCombinationIntegratedCandidate>
                result =
                new ArrayList<>();

        addUnique(
                result,
                analysis.sixCombinationCandidates()
        );

        addUnique(
                result,
                analysis.threeCombinationCandidates()
        );

        addUnique(
                result,
                analysis.directionalCombinationCandidates()
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Code / Name
     * =================================================
     *
     * 현재 9차 Evidence에는 별도 String ID가 없으므로
     * Boundary 식별자는:
     *
     * 기존 enum type
     * +
     * 실제 참여 Palace
     * +
     * 목표 오행
     *
     * 로 결정론적으로 만든다.
     *
     * 명리 판정 자체를 새로 만드는 것이 아니다.
     */

    private String buildStemTransformationCode(

            HeavenlyStemCombinationTransformationEvidence evidence,

            Set<PalacePosition> positions

    ) {

        return "STEM_"
                + evidence.combinationType().name()
                + "_"
                + joinPositions(
                positions
        )
                + "_TO_"
                + evidence.targetElement().name();
    }

    private String buildStemTransformationName(
            HeavenlyStemCombinationTransformationEvidence evidence
    ) {

        return evidence
                .firstStem()
                .getKoreanName()

                + evidence
                .secondStem()
                .getKoreanName()

                + " 합화 → "

                + evidence
                .targetElement()
                .getKoreanName();
    }

    private String buildBranchTransformationCode(

            EarthlyBranchCombinationIntegratedCandidate candidate,

            Set<PalacePosition> positions

    ) {

        return "BRANCH_"
                + candidate.candidateKind().name()
                + "_"
                + joinPositions(
                positions
        )
                + "_TO_"
                + candidate.targetElement().name();
    }

    private String buildBranchTransformationName(
            EarthlyBranchCombinationIntegratedCandidate candidate
    ) {

        String relationName;

        if (
                candidate
                        .candidateKind()
                        .sixCombination()
        ) {

            relationName =
                    "육합";

        } else if (
                candidate
                        .candidateKind()
                        .threeCombination()
        ) {

            relationName =
                    "삼합계";

        } else {

            relationName =
                    "방합계";
        }

        return relationName
                + " 합화 → "
                + candidate
                .targetElement()
                .getKoreanName();
    }

    private String buildGroupRelationCode(

            EarthlyBranchRelationKind relationKind,

            Set<PalacePosition> positions

    ) {

        return relationKind.name()
                + "_"
                + joinPositions(
                positions
        );
    }

    private String joinPositions(
            Set<PalacePosition> positions
    ) {

        return positions
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                PalacePosition::order
                        )
                )
                .map(
                        Enum::name
                )
                .collect(
                        Collectors.joining(
                                "_"
                        )
                );
    }

    /*
     * =================================================
     * Position Mapping
     * =================================================
     */

    private PalacePosition toPalacePosition(
            VisibleStemPosition position
    ) {

        Objects.requireNonNull(
                position,
                "천간 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    PalacePosition.YEAR;

            case MONTH ->
                    PalacePosition.MONTH;

            case DAY ->
                    PalacePosition.DAY;

            case HOUR ->
                    PalacePosition.HOUR;
        };
    }

    private PalacePosition toPalacePosition(
            EarthlyBranchPosition position
    ) {

        Objects.requireNonNull(
                position,
                "지지 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    PalacePosition.YEAR;

            case MONTH ->
                    PalacePosition.MONTH;

            case DAY ->
                    PalacePosition.DAY;

            case HOUR ->
                    PalacePosition.HOUR;
        };
    }

    private Set<PalacePosition> toPalacePositions(
            List<EarthlyBranchPosition> positions
    ) {

        Objects.requireNonNull(
                positions,
                "지지 위치 목록이 필요합니다."
        );

        Set<PalacePosition> result =
                new LinkedHashSet<>();

        for (
                EarthlyBranchPosition position
                : positions
        ) {

            result.add(
                    toPalacePosition(
                            position
                    )
            );
        }

        return Set.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Deduplication
     * =================================================
     */

    private void addRelationIfAbsent(

            List<PalaceRelationReference> result,

            PalaceRelationReference candidate

    ) {

        boolean exists =
                result
                        .stream()
                        .anyMatch(
                                existing ->
                                        existing.layer()
                                                == candidate.layer()

                                                && existing
                                                .positions()
                                                .equals(
                                                        candidate.positions()
                                                )

                                                && existing
                                                .relationCode()
                                                .equals(
                                                        candidate
                                                                .relationCode()
                                                )
                        );

        if (
                !exists
        ) {

            result.add(
                    candidate
            );
        }
    }

    private void addTransformationIfAbsent(

            List<PalaceTransformationReference> result,

            PalaceTransformationReference candidate

    ) {

        boolean exists =
                result
                        .stream()
                        .anyMatch(
                                existing ->
                                        existing
                                                .positions()
                                                .equals(
                                                        candidate.positions()
                                                )

                                                && existing
                                                .transformationCode()
                                                .equals(
                                                        candidate
                                                                .transformationCode()
                                                )
                        );

        if (
                !exists
        ) {

            result.add(
                    candidate
            );
        }
    }

    private <T> void addUnique(

            List<T> destination,

            List<T> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return;
        }

        for (
                T value
                : source
        ) {

            if (
                    value != null
                            && !destination.contains(
                            value
                    )
            ) {

                destination.add(
                        value
                );
            }
        }
    }

    /*
     * =================================================
     * Identity Validation
     * =================================================
     */

    private void validateIdentity(

            RelationAnalysis relationAnalysis,

            CombinationTransformationAnalysis
                    transformationAnalysis

    ) {

        if (
                transformationAnalysis
                        .relationAnalysis()

                        != relationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "9차 CombinationTransformationAnalysis가 "
                            + "현재 8차 RelationAnalysis를 참조하지 않습니다."
            );
        }

        if (
                transformationAnalysis
                        .dayMaster()

                        != relationAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "8차 관계 분석과 9차 합화 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                transformationAnalysis
                        .completeChart()

                        != relationAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "8차 관계 분석과 9차 합화 분석의 completeChart 상태가 일치하지 않습니다."
            );
        }
    }
}