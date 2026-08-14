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
 * 직업·사회 영역의 MONTH Palace Context Rule.
 *
 * =================================================
 *
 * Source:
 *
 * 12차 PalaceAnalysis
 *
 * =================================================
 *
 * 공식 위치 Context:
 *
 * MONTH
 * =
 * ENVIRONMENTAL_OPERATION
 * =
 * 환경·사회적 작동
 *
 * =================================================
 *
 * 이 Rule에서 MONTH 궁위는:
 *
 * - 사회적으로 작동하는 환경
 * - 외부 환경과 맞물리는 방식
 * - 역할을 수행하는 생활 기반
 * - 조직·사회적 조건과 접촉하는 위치
 *
 * 를 해석하기 위한 구조적 위치 Context다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * MONTH
 * -> 직장
 *
 * MONTH
 * -> 직업 성공
 *
 * MONTH에 정관
 * -> 승진
 *
 * MONTH에 재성
 * -> 돈 버는 직장
 *
 * MONTH 충
 * -> 이직
 *
 * MONTH 합
 * -> 취업
 *
 * MONTH 합화
 * -> 직업 전환
 *
 * 같은 사건을 생성하지 않는다.
 *
 * =================================================
 *
 * 12운성:
 *
 * PalaceContextEvidence가 이미
 * 해당 위치의 12운성을 보존한다.
 *
 * 여기서는:
 *
 * 장생/건록/제왕
 * -> SUPPORTIVE
 *
 * 병/사/묘/절
 * -> PRESSURE
 *
 * 로 변환하지 않는다.
 *
 * 12운성은 위치 Context의 일부로만 보존한다.
 *
 * =================================================
 *
 * 관계:
 *
 * stemRelations
 * branchRelations
 *
 * 이 존재하더라도
 * 합 = 지원
 * 충 = 부담
 *
 * 으로 단순 변환하지 않는다.
 *
 * 관계의 실제 종류와 중첩은
 * 8차 원국 관계 분석의 책임이다.
 *
 * =================================================
 *
 * 합화:
 *
 * hasTransformations()
 *
 * !=
 *
 * hasActivatedTransformation()
 *
 * !=
 *
 * hasReducedOriginalFunction()
 *
 * 이 세 상태를 반드시 분리한다.
 *
 * 결합 후보 존재만으로
 * 실제 합화 활성이나
 * 원기능 감소를 추론하지 않는다.
 *
 * =================================================
 *
 * 상태:
 *
 * 이 Rule은 항상 CONTEXTUAL이다.
 *
 * MONTH Palace 자체는
 * 직업적 길흉이나 성패가 아니라
 * 직업·사회 해석이 작동하는 위치 Context이기 때문이다.
 *
 * =================================================
 *
 * 기존 Career Rule과의 역할 분리:
 *
 * CareerPrimaryOfficerStructureRule
 * -> 5차 격국 중심축
 *
 * CareerOfficerStructureRule
 * -> 10차 관성 복합구조
 *
 * CareerCrossValidatedPatternRule
 * -> 10차 교차검증 Pattern
 *
 * CareerDecade/Annual/MonthlyContextRule
 * -> 시간층 변화
 *
 * CareerEnvironmentalPalaceContextRule
 * -> 12차 MONTH 환경·사회적 작동 위치 Context
 */
@Component
public class CareerEnvironmentalPalaceContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_ENVIRONMENTAL_PALACE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.PALACE
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.CAREER;
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
                "CareerEnvironmentalPalaceContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerEnvironmentalPalaceContextRule은 CAREER Bundle만 평가할 수 있습니다."
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

        /*
         * =================================================
         * PALACE Evidence 실제 Source
         * =================================================
         *
         * LifeDomainEvidenceResolver가
         * 12차 PalaceAnalysis 원본 객체를
         * NATAL:PALACE Source로 직접 연결한다.
         */

        PalaceAnalysis palaceAnalysis =
                palaceReference
                        .requireSource(
                                PalaceAnalysis.class
                        );

        /*
         * =================================================
         * MONTH Context
         * =================================================
         */

        PalaceContextEvidence monthContext =
                palaceAnalysis
                        .monthContext();

        validateMonthContext(
                monthContext
        );

        List<String> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 위치 Identity
         * =================================================
         */

        reasons.add(
                "12차 PalaceAnalysis의 MONTH 궁위를 직업·사회 영역의 위치 Context로 사용합니다."
        );

        reasons.add(
                "MONTH 궁위의 공식 위치 Context는 "
                        + monthContext
                        .positionContext()
                        .koreanName()
                        + "입니다."
        );

        reasons.add(
                "MONTH 궁위는 가족관계나 특정 직장을 기계적으로 의미하지 않고 환경·사회적 작동 위치로 사용합니다."
        );

        /*
         * =================================================
         * 실제 Stem / Branch
         * =================================================
         */

        reasons.add(
                "MONTH 궁위 천간은 "
                        + monthContext.stem()
                        + "입니다."
        );

        reasons.add(
                "MONTH 궁위 지지는 "
                        + monthContext.branch()
                        + "입니다."
        );

        /*
         * =================================================
         * 십성
         * =================================================
         *
         * 여기서 십성 자체의 길흉을 다시 판정하지 않는다.
         *
         * 위치에 어떤 십성이 실제 연결되어 있는지만
         * Provenance로 보존한다.
         */

        List<StructureTenGodType> monthTenGodTypes =
                collectTenGodTypes(
                        palaceAnalysis
                );

        if (
                !monthTenGodTypes.isEmpty()
        ) {

            reasons.add(
                    "MONTH 환경·사회적 작동 위치에 연결된 십성 유형: "
                            + joinTenGodTypes(
                            monthTenGodTypes
                    )
            );
        }

        monthContext
                .visibleTenGod()
                .ifPresent(
                        ignored ->
                                reasons.add(
                                        "MONTH 궁위에는 투출된 visible TenGod Evidence가 존재합니다."
                                )
                );

        if (
                !monthContext
                        .hiddenTenGods()
                        .isEmpty()
        ) {

            reasons.add(
                    "MONTH 궁위에는 "
                            + monthContext
                            .hiddenTenGods()
                            .size()
                            + "개의 hidden TenGod Evidence가 존재합니다."
            );
        }

        reasons.add(
                "궁위 십성은 해당 기능이 어느 위치에서 드러나는지를 보조하며 그 자체로 직업·직종·성과를 결정하지 않습니다."
        );

        /*
         * =================================================
         * 12운성
         * =================================================
         *
         * stage 이름은 원본 Context Provenance.
         *
         * 여기서 SUPPORTIVE/PRESSURE로 변환하지 않는다.
         */

        reasons.add(
                "MONTH 궁위의 12운성은 "
                        + monthContext
                        .twelveLifeStage()
                        .name()
                        + "이며 위치 Context의 일부로만 보존합니다."
        );

        reasons.add(
                "12운성 단계만으로 직업적 길흉·성공·실패를 판단하지 않습니다."
        );

        /*
         * =================================================
         * 천간 관계
         * =================================================
         */

        if (
                monthContext
                        .hasStemRelations()
        ) {

            reasons.add(
                    "MONTH 궁위에는 "
                            + monthContext
                            .stemRelations()
                            .size()
                            + "개의 천간 관계 Reference가 연결되어 있습니다."
            );
        }

        /*
         * =================================================
         * 지지 관계
         * =================================================
         */

        if (
                monthContext
                        .hasBranchRelations()
        ) {

            reasons.add(
                    "MONTH 궁위에는 "
                            + monthContext
                            .branchRelations()
                            .size()
                            + "개의 지지 관계 Reference가 연결되어 있습니다."
            );
        }

        if (
                monthContext.hasStemRelations()
                        || monthContext.hasBranchRelations()
        ) {

            reasons.add(
                    "MONTH 궁위 관계 존재 자체를 지원 또는 부담으로 단순 변환하지 않으며 관계 종류와 중첩은 기존 원국 관계 Evidence를 그대로 따릅니다."
            );
        }

        /*
         * =================================================
         * Transformation 후보 존재
         * =================================================
         */

        if (
                monthContext
                        .hasTransformations()
        ) {

            reasons.add(
                    "MONTH 궁위에는 "
                            + monthContext
                            .transformations()
                            .size()
                            + "개의 합화 Context Reference가 연결되어 있습니다."
            );
        }

        /*
         * =================================================
         * 실제 합화 Activation
         * =================================================
         *
         * 후보 존재와 활성 상태를 분리한다.
         */

        if (
                monthContext
                        .hasActivatedTransformation()
        ) {

            reasons.add(
                    "MONTH 궁위에 연결된 합화 Context 중 실제 activated 상태가 존재합니다."
            );

        } else if (
                monthContext
                        .hasTransformations()
        ) {

            reasons.add(
                    "MONTH 궁위에 합화 Context는 존재하지만 activated 상태가 확인된 것은 아닙니다."
            );
        }

        /*
         * =================================================
         * 원기능 감소
         * =================================================
         *
         * 합화 활성과 원기능 감소도 별개다.
         */

        if (
                monthContext
                        .hasReducedOriginalFunction()
        ) {

            reasons.add(
                    "MONTH 궁위의 Transformation Context 중 원래 기능 감소가 명시된 Evidence가 존재합니다."
            );

        } else if (
                monthContext
                        .hasTransformations()
        ) {

            reasons.add(
                    "MONTH 궁위에 Transformation Context가 존재하더라도 원래 기능 감소가 자동으로 성립하는 것은 아닙니다."
            );
        }

        reasons.add(
                "합 존재, 합화 후보, 실제 합화 활성, 원기능 감소를 서로 동일한 상태로 취급하지 않습니다."
        );

        /*
         * =================================================
         * completeChart
         * =================================================
         *
         * MONTH 자체는 시간 미상이어도 존재하지만
         * 원국 전체 완전성은 그대로 남긴다.
         */

        if (
                palaceAnalysis
                        .hourUnknown()
        ) {

            reasons.add(
                    "출생시간 미상으로 HOUR 궁위는 확정되지 않았지만 YEAR·MONTH·DAY 궁위에 가짜 HOUR 정보를 추가하지 않습니다."
            );
        }

        reasons.add(
                "MONTH 궁위 Context는 직업·사회적 활동이 어떤 원국 위치 환경에서 작동하는지 설명하는 구조적 근거이며 실제 취업·퇴사·승진·이직 사건을 생성하지 않습니다."
        );

        /*
         * =================================================
         * Match
         * =================================================
         *
         * Palace Context 자체는 방향 판정이 아니므로
         * CONTEXTUAL 고정.
         */

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
                        RULE_CODE,
                        LifeDomainSignalStatus.CONTEXTUAL,
                        summaryOf(
                                monthContext
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

    /*
     * =================================================
     * MONTH Context 정합성
     * =================================================
     */

    private void validateMonthContext(
            PalaceContextEvidence context
    ) {

        Objects.requireNonNull(
                context,
                "PalaceAnalysis에는 MONTH PalaceContextEvidence가 필요합니다."
        );

        if (
                context.position()
                        != PalacePosition.MONTH
        ) {

            throw new IllegalStateException(
                    "monthContext의 PalacePosition이 MONTH가 아닙니다: "
                            + context.position()
            );
        }

        if (
                context.positionContext()
                        != PalacePositionContextType
                        .ENVIRONMENTAL_OPERATION
        ) {

            throw new IllegalStateException(
                    "MONTH Palace의 공식 Context는 ENVIRONMENTAL_OPERATION이어야 합니다: "
                            + context.positionContext()
            );
        }

        if (
                !context
                        .positionContext()
                        .environmentalOperation()
        ) {

            throw new IllegalStateException(
                    "MONTH Palace Context가 environmentalOperation 상태가 아닙니다."
            );
        }
    }

    /*
     * =================================================
     * MONTH 위치 십성 수집
     * =================================================
     *
     * PalaceTenGodEvidence의 내부 API를
     * 다시 파싱하지 않는다.
     *
     * PalaceAnalysis의 공식 containsTenGod(...)
     * 조회 API를 사용한다.
     */

    private List<StructureTenGodType>
    collectTenGodTypes(
            PalaceAnalysis palaceAnalysis
    ) {

        List<StructureTenGodType> result =
                new ArrayList<>();

        for (
                StructureTenGodType tenGodType
                : StructureTenGodType.values()
        ) {

            if (
                    palaceAnalysis
                            .containsTenGod(
                                    PalacePosition.MONTH,
                                    tenGodType
                            )
            ) {

                result.add(
                        tenGodType
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * TenGod Debug/Provenance 표현
     * =================================================
     *
     * 최종 사용자 출력이 아니다.
     */

    private String joinTenGodTypes(
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
     * 내부 Semantic Summary
     * =================================================
     *
     * 사용자 최종 점사 문장이 아니다.
     *
     * SUPPORTIVE/PRESSURE를 만들지 않고
     * 환경·사회적 작동 위치의 복합 Context만
     * Grounding 후보로 전달한다.
     */

    private String summaryOf(
            PalaceContextEvidence monthContext
    ) {

        boolean relationExists =
                monthContext.hasStemRelations()
                        || monthContext.hasBranchRelations();

        boolean transformationExists =
                monthContext.hasTransformations();

        boolean activatedTransformation =
                monthContext.hasActivatedTransformation();

        boolean reducedOriginalFunction =
                monthContext.hasReducedOriginalFunction();

        if (
                reducedOriginalFunction
        ) {

            return "환경·사회적 작동 위치에 관계 및 기능변화 Context가 관여하며 일부 원래 기능 감소 Evidence까지 존재하지만 이를 직업적 길흉으로 단순화하지 않습니다.";
        }

        if (
                activatedTransformation
        ) {

            return "환경·사회적 작동 위치에 실제 활성된 기능변화 Context가 관여하지만 그 자체로 직업 변화 사건을 의미하지 않습니다.";
        }

        if (
                transformationExists
                        && relationExists
        ) {

            return "환경·사회적 작동 위치에 원국 관계와 합화 후보 Context가 함께 관여하며 실제 작동 방향은 다른 구조 Evidence와 함께 해석해야 합니다.";
        }

        if (
                relationExists
        ) {

            return "환경·사회적 작동 위치가 원국의 다른 위치와 관계망을 형성하고 있어 사회적 역할 환경을 단독 위치가 아닌 연결 구조로 해석해야 합니다.";
        }

        if (
                transformationExists
        ) {

            return "환경·사회적 작동 위치에 합화 Context가 존재하지만 후보 존재와 실제 활성 여부를 분리해서 해석해야 합니다.";
        }

        return "MONTH 궁위가 환경·사회적 작동 위치 Context를 제공하며 직업·사회 해석의 구조적 배경으로 사용됩니다.";
    }

    /*
     * =================================================
     * 단일 PALACE Evidence
     * =================================================
     *
     * 현재 18-2 Resolver는
     * 원국 PalaceAnalysis 하나를
     * NATAL:PALACE로 연결한다.
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
                    "CareerEnvironmentalPalaceContextRule에는 최대 하나의 "
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