package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.PalaceContextEvidence;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 재물 영역의 Palace 분포 Context Rule.
 *
 * =================================================
 *
 * Source:
 *
 * 12차 PalaceAnalysis
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 특정 Palace를 재물 영역으로
 * 기계적으로 고정하지 않는다.
 *
 * 예:
 *
 * YEAR
 * -> 재물
 *
 * MONTH
 * -> 재물
 *
 * HOUR
 * -> 재물
 *
 * 같은 위치 고정 매핑을 하지 않는다.
 *
 * =================================================
 *
 * 대신 현재 원국의 알려진 Palace 중:
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 *
 * 가 실제 존재하는 위치를 찾고,
 *
 * 해당 위치의 공식 Palace Context를
 * 그대로 재물영역에 연결한다.
 *
 * =================================================
 *
 * 공식 위치 Context:
 *
 * YEAR
 * -> BACKGROUND_FOUNDATION
 *
 * MONTH
 * -> ENVIRONMENTAL_OPERATION
 *
 * DAY
 * -> CORE_LIFE_AXIS
 *
 * HOUR
 * -> EXTENSION_LATER_DEVELOPMENT
 *
 * =================================================
 *
 * 의미:
 *
 * 재성은:
 *
 * - 현실적 성과
 * - 자원
 * - 교환
 * - 획득
 * - 관리
 * - 결과의 현실화
 *
 * 같은 기능을 다룬다.
 *
 * 이 Rule은 그런 기능이
 * 원국의 어느 위치 Context에
 * 실제 배치되어 있는지를 보존한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * YEAR 재성
 * -> 집안이 부유하다
 *
 * MONTH 재성
 * -> 직장에서 돈을 번다
 *
 * DAY 재성
 * -> 배우자가 돈이 많다
 *
 * HOUR 재성
 * -> 말년에 부자가 된다
 *
 * 정재 위치
 * -> 안정적인 소득
 *
 * 편재 위치
 * -> 사업 성공
 *
 * 같은 사건·자산 규모·가족관계를
 * 직접 생성하지 않는다.
 *
 * =================================================
 *
 * 12운성:
 *
 * 해당 Palace의 12운성을
 * Context 일부로 보존한다.
 *
 * 장생/건록/제왕
 * -> SUPPORTIVE
 *
 * 병/사/묘/절
 * -> PRESSURE
 *
 * 같은 길흉 변환을 하지 않는다.
 *
 * =================================================
 *
 * 관계:
 *
 * 해당 위치에 합·충·형·파·해 등이
 * 연결되어 있더라도
 *
 * 관계 존재 자체를:
 *
 * SUPPORTIVE
 * PRESSURE
 *
 * 로 변환하지 않는다.
 *
 * =================================================
 *
 * 합화:
 *
 * Transformation 존재
 * != activated
 * != originalFunctionReduced
 *
 * 를 분리한다.
 *
 * =================================================
 *
 * Match 단위:
 *
 * 재성이 존재하는 Palace마다
 * 별도 Match를 생성한다.
 *
 * 예:
 *
 * MONTH 재성
 * +
 * HOUR 재성
 *
 * 이 동시에 존재하면
 * 두 위치 Context를 하나로 평탄화하지 않고
 * Match 두 개로 보존한다.
 *
 * =================================================
 *
 * 상태:
 *
 * 항상 CONTEXTUAL.
 *
 * Palace 분포는 재물 길흉이 아니라
 * 재성 기능의 위치 Context이기 때문이다.
 *
 * =================================================
 *
 * 시간 미상:
 *
 * knownPositions()에 HOUR가 없으므로
 * HOUR 재성을 추정하지 않는다.
 */
@Component
public class WealthPalaceDistributionContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_PALACE_DISTRIBUTION_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.PALACE
            );

    private static final Set<StructureTenGodType>
            WEALTH_TYPES =
            Set.of(
                    StructureTenGodType.DIRECT_WEALTH,
                    StructureTenGodType.INDIRECT_WEALTH
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.WEALTH;
    }

    @Override
    public String code() {

        return RULE_CODE;
    }

    @Override
    public Set<LifeDomainEvidenceSourceType>
    supportedSources() {

        return SUPPORTED_SOURCES;
    }

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "WealthPalaceDistributionContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthPalaceDistributionContextRule은 WEALTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                palaceReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.PALACE
                );

        if (
                palaceReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                palaceReference =
                palaceReferenceOptional
                        .orElseThrow();

        PalaceAnalysis palaceAnalysis =
                palaceReference
                        .requireSource(
                                PalaceAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 알려진 Palace만 순회
         * =================================================
         *
         * 시간 미상에서는 HOUR가
         * knownPositions()에 존재하지 않는다.
         */

        for (
                PalacePosition position
                : palaceAnalysis.knownPositions()
        ) {

            List<StructureTenGodType> wealthTypes =
                    collectWealthTypes(
                            palaceAnalysis,
                            position
                    );

            if (
                    wealthTypes.isEmpty()
            ) {

                continue;
            }

            PalaceContextEvidence context =
                    palaceAnalysis
                            .contextOf(
                                    position
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "알려진 Palace 위치에 PalaceContextEvidence가 존재하지 않습니다: "
                                                            + position
                                            )
                            );

            validateContext(
                    position,
                    context
            );

            List<String> reasons =
                    buildReasons(
                            palaceAnalysis,
                            context,
                            wealthTypes
                    );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            summaryOf(
                                    context,
                                    wealthTypes
                            ),
                            List.of(
                                    palaceReference
                            ),
                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        return List.copyOf(
                matches
        );
    }

    /*
     * =================================================
     * 재성 타입 수집
     * =================================================
     *
     * PalaceTenGodEvidence 내부 구조를
     * 문자열로 추측하지 않는다.
     *
     * PalaceAnalysis 공식 containsTenGod(...)
     * API를 사용한다.
     */

    private List<StructureTenGodType>
    collectWealthTypes(

            PalaceAnalysis palaceAnalysis,

            PalacePosition position

    ) {

        List<StructureTenGodType> result =
                new ArrayList<>();

        for (
                StructureTenGodType wealthType
                : WEALTH_TYPES
        ) {

            if (
                    palaceAnalysis
                            .containsTenGod(
                                    position,
                                    wealthType
                            )
            ) {

                result.add(
                        wealthType
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Palace Context 정합성
     * =================================================
     */

    private void validateContext(

            PalacePosition position,

            PalaceContextEvidence context

    ) {

        Objects.requireNonNull(
                position,
                "PalacePosition이 필요합니다."
        );

        Objects.requireNonNull(
                context,
                "PalaceContextEvidence가 필요합니다."
        );

        if (
                context.position()
                        != position
        ) {

            throw new IllegalStateException(
                    "PalacePosition과 PalaceContextEvidence 위치가 일치하지 않습니다. "
                            + "position="
                            + position
                            + ", context="
                            + context.position()
            );
        }

        PalacePositionContextType expectedContext =
                expectedPositionContext(
                        position
                );

        if (
                context.positionContext()
                        != expectedContext
        ) {

            throw new IllegalStateException(
                    "Palace 위치와 공식 PositionContext가 일치하지 않습니다. "
                            + "position="
                            + position
                            + ", expected="
                            + expectedContext
                            + ", actual="
                            + context.positionContext()
            );
        }
    }

    /*
     * =================================================
     * PalacePosition -> 공식 Context
     * =================================================
     */

    private PalacePositionContextType expectedPositionContext(
            PalacePosition position
    ) {

        return switch (
                position
                ) {

            case YEAR ->
                    PalacePositionContextType
                            .BACKGROUND_FOUNDATION;

            case MONTH ->
                    PalacePositionContextType
                            .ENVIRONMENTAL_OPERATION;

            case DAY ->
                    PalacePositionContextType
                            .CORE_LIFE_AXIS;

            case HOUR ->
                    PalacePositionContextType
                            .EXTENSION_LATER_DEVELOPMENT;
        };
    }

    /*
     * =================================================
     * Reason 구성
     * =================================================
     */

    private List<String> buildReasons(

            PalaceAnalysis palaceAnalysis,

            PalaceContextEvidence context,

            List<StructureTenGodType> wealthTypes

    ) {

        List<String> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 위치
         * =================================================
         */

        reasons.add(
                "12차 PalaceAnalysis에서 "
                        + context.position()
                        + " 위치에 재성 계열 Evidence가 존재합니다."
        );

        reasons.add(
                "해당 Palace의 공식 위치 Context는 "
                        + context
                        .positionContext()
                        .koreanName()
                        + "입니다."
        );

        reasons.add(
                "재성이 존재하는 Palace의 실제 위치 Context를 사용하며 특정 Palace를 재물 영역으로 미리 고정하지 않습니다."
        );

        /*
         * =================================================
         * 재성 종류
         * =================================================
         */

        reasons.add(
                "해당 위치의 재성 유형: "
                        + joinTypes(
                        wealthTypes
                )
        );

        if (
                wealthTypes.contains(
                        StructureTenGodType.DIRECT_WEALTH
                )
        ) {

            reasons.add(
                    "정재 Evidence는 현실적 자원과 결과를 비교적 일정한 방식으로 관리하는 기능의 위치 Context로만 사용합니다."
            );
        }

        if (
                wealthTypes.contains(
                        StructureTenGodType.INDIRECT_WEALTH
                )
        ) {

            reasons.add(
                    "편재 Evidence는 여러 자원·기회·교환을 다루는 기능의 위치 Context로만 사용합니다."
            );
        }

        if (
                wealthTypes.size()
                        > 1
        ) {

            reasons.add(
                    "정재와 편재가 같은 Palace에 함께 존재하므로 두 재성 기능을 하나로 삭제하지 않고 공존 Evidence로 보존합니다."
            );
        }

        /*
         * =================================================
         * 위치별 Semantic
         * =================================================
         */

        reasons.add(
                positionMeaning(
                        context.positionContext()
                )
        );

        /*
         * =================================================
         * 천간 / 지지
         * =================================================
         */

        reasons.add(
                "해당 Palace 천간은 "
                        + context.stem()
                        + ", 지지는 "
                        + context.branch()
                        + "입니다."
        );

        /*
         * =================================================
         * Visible / Hidden
         * =================================================
         */

        context
                .visibleTenGod()
                .ifPresent(
                        ignored ->
                                reasons.add(
                                        "해당 Palace에는 visible TenGod Evidence가 존재합니다."
                                )
                );

        if (
                !context
                        .hiddenTenGods()
                        .isEmpty()
        ) {

            reasons.add(
                    "해당 Palace에는 "
                            + context
                            .hiddenTenGods()
                            .size()
                            + "개의 hidden TenGod Evidence가 존재합니다."
            );
        }

        reasons.add(
                "visible/hidden 상태는 재성 기능이 위치에서 드러나는 방식의 Provenance이며 수입 규모나 재물 성과의 우열로 변환하지 않습니다."
        );

        /*
         * =================================================
         * 12운성
         * =================================================
         */

        reasons.add(
                "해당 Palace의 12운성은 "
                        + context
                        .twelveLifeStage()
                        .name()
                        + "이며 위치 Context 일부로만 보존합니다."
        );

        reasons.add(
                "12운성 단계만으로 재물의 길흉·축적·손실을 판단하지 않습니다."
        );

        /*
         * =================================================
         * 천간 / 지지 관계
         * =================================================
         */

        if (
                context.hasStemRelations()
        ) {

            reasons.add(
                    "해당 Palace에는 "
                            + context
                            .stemRelations()
                            .size()
                            + "개의 천간 관계 Reference가 연결되어 있습니다."
            );
        }

        if (
                context.hasBranchRelations()
        ) {

            reasons.add(
                    "해당 Palace에는 "
                            + context
                            .branchRelations()
                            .size()
                            + "개의 지지 관계 Reference가 연결되어 있습니다."
            );
        }

        if (
                context.hasStemRelations()
                        || context.hasBranchRelations()
        ) {

            reasons.add(
                    "재성이 위치한 Palace의 관계 존재 자체를 재물 지원 또는 손실로 단순 변환하지 않습니다."
            );
        }

        /*
         * =================================================
         * Transformation
         * =================================================
         */

        if (
                context.hasTransformations()
        ) {

            reasons.add(
                    "해당 Palace에는 "
                            + context
                            .transformations()
                            .size()
                            + "개의 Transformation Context Reference가 존재합니다."
            );
        }

        if (
                context.hasActivatedTransformation()
        ) {

            reasons.add(
                    "해당 Palace의 Transformation Context 중 실제 activated 상태가 존재합니다."
            );

        } else if (
                context.hasTransformations()
        ) {

            reasons.add(
                    "해당 Palace에 Transformation Context는 존재하지만 실제 활성 상태가 확인된 것은 아닙니다."
            );
        }

        if (
                context.hasReducedOriginalFunction()
        ) {

            reasons.add(
                    "해당 Palace의 Transformation Context 중 원래 기능 감소가 명시된 Evidence가 존재합니다."
            );

        } else if (
                context.hasTransformations()
        ) {

            reasons.add(
                    "Transformation Context 존재만으로 재성의 원래 기능 감소를 추론하지 않습니다."
            );
        }

        reasons.add(
                "결합 존재, 합화 후보, 실제 활성, 원기능 감소를 서로 동일한 상태로 취급하지 않습니다."
        );

        /*
         * =================================================
         * 시간 미상
         * =================================================
         */

        if (
                palaceAnalysis.hourUnknown()
        ) {

            reasons.add(
                    "출생시간 미상으로 HOUR Palace는 분석 대상에서 제외되어 있으며 재성의 HOUR 위치를 추정하지 않습니다."
            );
        }

        /*
         * =================================================
         * 생활 의미 제한
         * =================================================
         */

        reasons.add(
                "이 Match는 재성 기능이 원국의 어느 생활 위치 Context에 배치되어 있는지를 설명하며 재물 규모·소득·투자·사업·자산 사건을 직접 생성하지 않습니다."
        );

        reasons.add(
                "재성의 실제 발달도와 일간 수용력은 WealthCapacityRule이 별도로 담당합니다."
        );

        reasons.add(
                "재성 관련 복합 Pattern은 WealthCrossValidatedPatternRule이 별도로 담당합니다."
        );

        reasons.add(
                "재성이 원국의 중심격으로 선택되었는지는 WealthPrimaryWealthStructureRule이 별도로 담당합니다."
        );

        reasons.add(
                "대운·세운·월운의 재물 기능 변화는 각각의 Wealth temporal Rule이 별도로 담당합니다."
        );

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * 위치 Semantic
     * =================================================
     *
     * 가족관계나 사건으로 변환하지 않는다.
     */

    private String positionMeaning(
            PalacePositionContextType contextType
    ) {

        return switch (
                contextType
                ) {

            case BACKGROUND_FOUNDATION ->
                    "재성 기능이 배경·초기 기반 위치에 관여한다는 구조 Context이며 가족의 재산 수준이나 상속을 의미하지 않습니다.";

            case ENVIRONMENTAL_OPERATION ->
                    "재성 기능이 환경·사회적 작동 위치에 관여한다는 구조 Context이며 특정 직장이나 소득 발생을 의미하지 않습니다.";

            case CORE_LIFE_AXIS ->
                    "재성 기능이 핵심 생활축 위치에 관여한다는 구조 Context이며 배우자·결혼·재산 사건으로 자동 변환하지 않습니다.";

            case EXTENSION_LATER_DEVELOPMENT ->
                    "재성 기능이 확장·후반 전개 위치에 관여한다는 구조 Context이며 미래 자산 증가나 말년 재물 결과를 예언하지 않습니다.";
        };
    }

    /*
     * =================================================
     * 내부 Semantic Summary
     * =================================================
     */

    private String summaryOf(

            PalaceContextEvidence context,

            List<StructureTenGodType> wealthTypes

    ) {

        String function =
                wealthFunction(
                        wealthTypes
                );

        String location =
                switch (
                        context.positionContext()
                        ) {

                    case BACKGROUND_FOUNDATION ->
                            "배경·초기 기반";

                    case ENVIRONMENTAL_OPERATION ->
                            "환경·사회적 작동";

                    case CORE_LIFE_AXIS ->
                            "핵심 생활축";

                    case EXTENSION_LATER_DEVELOPMENT ->
                            "확장·후반 전개";
                };

        if (
                context.hasReducedOriginalFunction()
        ) {

            return location
                    + " 위치에서 "
                    + function
                    + "이 관여하며 Transformation에 따른 원기능 감소 Evidence도 존재하지만 이를 재물 손실 사건으로 단순화하지 않습니다.";
        }

        if (
                context.hasActivatedTransformation()
        ) {

            return location
                    + " 위치에서 "
                    + function
                    + "이 관여하고 실제 활성된 기능변화 Context가 함께 존재합니다.";
        }

        if (
                context.hasStemRelations()
                        || context.hasBranchRelations()
        ) {

            return location
                    + " 위치에서 "
                    + function
                    + "이 원국의 다른 위치와 관계망을 이루며 작동합니다.";
        }

        return location
                + " 위치에서 "
                + function
                + "이 재물영역의 구조 Context로 관여합니다.";
    }

    private String wealthFunction(
            List<StructureTenGodType> wealthTypes
    ) {

        boolean direct =
                wealthTypes.contains(
                        StructureTenGodType.DIRECT_WEALTH
                );

        boolean indirect =
                wealthTypes.contains(
                        StructureTenGodType.INDIRECT_WEALTH
                );

        if (
                direct
                        && indirect
        ) {

            return "안정적인 자원 관리와 다양한 자원·기회를 함께 다루는 기능";
        }

        if (
                direct
        ) {

            return "현실적 자원과 결과를 일정하게 관리하는 기능";
        }

        return "다양한 자원·기회·교환을 다루는 기능";
    }

    /*
     * =================================================
     * Type 표현
     * =================================================
     *
     * 내부 Provenance용.
     */

    private String joinTypes(
            List<StructureTenGodType> types
    ) {

        return String.join(
                ", ",
                types
                        .stream()
                        .map(
                                Enum::name
                        )
                        .toList()
        );
    }

    /*
     * =================================================
     * 단일 PALACE Evidence
     * =================================================
     */

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        List<LifeDomainEvidenceReference<?>>
                evidences =
                bundle.evidencesOf(
                        sourceType
                );

        if (
                evidences.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                evidences.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "WealthPalaceDistributionContextRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(
                evidences.get(
                        0
                )
        );
    }
}