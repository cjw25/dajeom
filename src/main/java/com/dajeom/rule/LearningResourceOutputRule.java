package com.dajeom.rule;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.OutputCompositeAnalysis;
import com.dajeom.model.ResourceCompositeAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 학습·성장 영역의 인성·식상 복합구조 Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 10차 TenGodCompositeAnalysis
 * 4차 DayMasterStrengthAnalysis
 *
 * =================================================
 *
 * 사용하는 기존 10-3 식상 Pattern:
 *
 * OUTPUT_DEVELOPED
 * EATING_GOD_CENTERED
 * HURTING_OFFICER_CENTERED
 * MIXED_OUTPUT
 * WEAK_DAY_MASTER_EXCESSIVE_DRAIN
 * RESOURCE_CONSTRAINED_OUTPUT
 *
 * =================================================
 *
 * 사용하는 기존 10-6 인성 Pattern:
 *
 * RESOURCE_DEVELOPED
 * DIRECT_RESOURCE_CENTERED
 * INDIRECT_RESOURCE_CENTERED
 * MIXED_RESOURCE
 * WEAK_DAY_MASTER_RESOURCE_SUPPORT
 * STRONG_DAY_MASTER_EXCESSIVE_RESOURCE
 * OUTPUT_CONSTRAINT_CONTEXT
 * OFFICER_GENERATION_CONTEXT
 * WEALTH_PRESSURE_CONTEXT
 *
 * =================================================
 *
 * 18차에서 다시 계산하지 않는 것:
 *
 * - 인성 세력
 * - 식상 세력
 * - 신강신약
 * - 인성/식상 과다 여부
 * - 인성의 식상 제약
 * - 식상의 인성 제약
 * - 관살 생인 Context
 * - 재성 압박 Context
 *
 * 위 내용은 이미 10-3 / 10-6에서
 * deterministic하게 판정된 결과를 사용한다.
 *
 * =================================================
 *
 * 생활영역 의미:
 *
 * 인성:
 * 학습·흡수·정리·내재화 측면의 Context.
 *
 * 식상:
 * 표현·산출·응용·외부 발현 측면의 Context.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 인성 발달
 * -> 공부를 잘한다
 *
 * 식상 발달
 * -> 시험에 합격한다
 *
 * 정인 발달
 * -> 자격증을 딴다
 *
 * 상관 발달
 * -> 창의적 천재다
 *
 * 같은 사건·성과 결과 확정.
 */
@Component
public class LearningResourceOutputRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_RESOURCE_OUTPUT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .TEN_GOD_COMPOSITE,

                    LifeDomainEvidenceSourceType
                            .DAY_MASTER_STRENGTH
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.LEARNING_GROWTH;
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
                "LearningResourceOutputRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningResourceOutputRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        LifeDomainEvidenceReference<?>
                tenGodReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .TEN_GOD_COMPOSITE
                );

        LifeDomainEvidenceReference<?>
                dayMasterReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .DAY_MASTER_STRENGTH
                );

        TenGodCompositeAnalysis tenGodCompositeAnalysis =
                tenGodReference
                        .requireSource(
                                TenGodCompositeAnalysis.class
                        );

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis =
                dayMasterReference
                        .requireSource(
                                DayMasterStrengthAnalysis.class
                        );

        OutputCompositeAnalysis outputAnalysis =
                tenGodCompositeAnalysis
                        .outputAnalysis();

        ResourceCompositeAnalysis resourceAnalysis =
                tenGodCompositeAnalysis
                        .resourceAnalysis();

        /*
         * =================================================
         * Upstream Identity 검증
         * =================================================
         */

        if (
                outputAnalysis
                        .dayMasterStrengthAnalysis()
                        != dayMasterStrengthAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-3 OutputCompositeAnalysis와 "
                            + "18차 Bundle의 DayMasterStrengthAnalysis가 일치하지 않습니다."
            );
        }

        if (
                resourceAnalysis
                        .dayMasterStrengthAnalysis()
                        != dayMasterStrengthAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-6 ResourceCompositeAnalysis와 "
                            + "18차 Bundle의 DayMasterStrengthAnalysis가 일치하지 않습니다."
            );
        }

        if (
                outputAnalysis
                        .tenGodDistributionAnalysis()
                        != resourceAnalysis
                        .tenGodDistributionAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10-3 OutputCompositeAnalysis와 "
                            + "10-6 ResourceCompositeAnalysis가 서로 다른 10-1 분석을 참조합니다."
            );
        }

        if (
                outputAnalysis.dayMaster()
                        != tenGodCompositeAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-3 OutputCompositeAnalysis와 "
                            + "10차 TenGodCompositeAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                resourceAnalysis.dayMaster()
                        != tenGodCompositeAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-6 ResourceCompositeAnalysis와 "
                            + "10차 TenGodCompositeAnalysis의 일간이 일치하지 않습니다."
            );
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 인성 발달
         * =================================================
         *
         * 학습·흡수·정리·내재화 기능을
         * 뒷받침하는 구조 Evidence.
         *
         * 구체적인 성적·합격 결과는 아니다.
         */

        if (
                resourceAnalysis
                        .resourceDeveloped()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.SUPPORTIVE,
                            "인성 계열의 학습·흡수·내재화 기능을 뒷받침하는 구조가 형성되어 있습니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 RESOURCE_DEVELOPED Pattern이 의미 있게 성립합니다.",
                                    "인성 Family 세력은 "
                                            + resourceAnalysis
                                            .resourceFamilyPower()
                                            + "입니다.",
                                    "인성 점유율은 "
                                            + resourceAnalysis
                                            .resourceFamilyShareBasisPoints()
                                            + "bp입니다.",
                                    "인성 발달을 성적·합격·자격 취득 같은 실제 성과로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 2.
         * 정인 중심
         * =================================================
         */

        if (
                resourceAnalysis
                        .directResourceCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "정인 계열이 인성 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 DIRECT_RESOURCE_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "정인 배분 세력은 "
                                            + resourceAnalysis
                                            .directResourcePower()
                                            + "입니다.",
                                    "정인 출현 횟수는 "
                                            + resourceAnalysis
                                            .directResourceOccurrenceCount()
                                            + "입니다.",
                                    "정인 중심성을 특정 학습법·학력·자격 결과로 확정하지 않고 학습 구조의 Context로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * 편인 중심
         * =================================================
         */

        if (
                resourceAnalysis
                        .indirectResourceCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "편인 계열이 인성 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 INDIRECT_RESOURCE_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "편인 배분 세력은 "
                                            + resourceAnalysis
                                            .indirectResourcePower()
                                            + "입니다.",
                                    "편인 출현 횟수는 "
                                            + resourceAnalysis
                                            .indirectResourceOccurrenceCount()
                                            + "입니다.",
                                    "편인 중심성을 독특한 재능이나 특정 학습 성취로 자동 변환하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * 정인 + 편인 혼재
         * =================================================
         */

        if (
                resourceAnalysis
                        .mixedResource()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "정인과 편인의 서로 다른 정보 수용·내재화 양식이 함께 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 MIXED_RESOURCE Pattern이 의미 있게 성립합니다.",
                                    "정인과 편인의 병존을 어느 한쪽의 학습 방식으로 축약하지 않습니다.",
                                    "복수의 인성 작동 양식이 동시에 존재하는 구조를 MIXED Evidence로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 5.
         * 신약 + 인성 보조
         * =================================================
         *
         * 10-6에서 이미 신강신약과
         * 인성 구조를 교차한 결과.
         */

        if (
                resourceAnalysis
                        .weakDayMasterResourceSupport()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.SUPPORTIVE,
                            "인성 계열이 일간의 수용력을 보완하는 학습·성장 지원 Context를 형성합니다.",
                            List.of(
                                    tenGodReference,
                                    dayMasterReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 WEAK_DAY_MASTER_RESOURCE_SUPPORT Pattern이 의미 있게 성립합니다.",
                                    "현재 4차 일간 강약 Level은 "
                                            + resourceAnalysis
                                            .dayMasterStrengthLevel()
                                            .name()
                                            + "입니다.",
                                    "인성의 보조 작용을 시험·학업 성공으로 확정하지 않고 수용력 보완 Evidence로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 6.
         * 신강 + 인성 과다
         * =================================================
         *
         * 인성 자체가 나쁘다는 의미가 아니다.
         *
         * 이미 강한 일간에 인성 기능이 과도하게
         * 중첩되는 부담 Context를 보존한다.
         */

        if (
                resourceAnalysis
                        .strongDayMasterExcessiveResource()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "인성 기능이 일간 상태와 비교할 때 과도하게 중첩되는 학습·성장 부담 Context가 존재합니다.",
                            List.of(
                                    tenGodReference,
                                    dayMasterReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 STRONG_DAY_MASTER_EXCESSIVE_RESOURCE Pattern이 의미 있게 성립합니다.",
                                    "현재 4차 일간 강약 Level은 "
                                            + resourceAnalysis
                                            .dayMasterStrengthLevel()
                                            .name()
                                            + "입니다.",
                                    "인성 과다를 학습 부진 같은 결과로 단정하지 않고 흡수·내재화 기능의 과중 Context로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 7.
         * 식상 발달
         * =================================================
         *
         * 표현·산출·응용·외부 발현 측면의
         * 지원 Evidence.
         */

        if (
                outputAnalysis
                        .outputDeveloped()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.SUPPORTIVE,
                            "식상 계열의 표현·산출·응용 기능을 뒷받침하는 구조가 형성되어 있습니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-3 OutputCompositeAnalysis에서 OUTPUT_DEVELOPED Pattern이 의미 있게 성립합니다.",
                                    "식상 Family 세력은 "
                                            + outputAnalysis
                                            .outputFamilyPower()
                                            + "입니다.",
                                    "식상 점유율은 "
                                            + outputAnalysis
                                            .outputFamilyShareBasisPoints()
                                            + "bp입니다.",
                                    "식상 발달을 시험 합격·성과 창출 같은 실제 결과로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 8.
         * 식신 중심
         * =================================================
         */

        if (
                outputAnalysis
                        .eatingGodCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "식신 계열이 식상 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-3 OutputCompositeAnalysis에서 EATING_GOD_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "식신 배분 세력은 "
                                            + outputAnalysis
                                            .eatingGodPower()
                                            + "입니다.",
                                    "식신 출현 횟수는 "
                                            + outputAnalysis
                                            .eatingGodOccurrenceCount()
                                            + "입니다.",
                                    "식신 중심성을 특정 표현 방식이나 학업 성취로 단정하지 않고 산출 구조의 Context로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 9.
         * 상관 중심
         * =================================================
         */

        if (
                outputAnalysis
                        .hurtingOfficerCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "상관 계열이 식상 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-3 OutputCompositeAnalysis에서 HURTING_OFFICER_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "상관 배분 세력은 "
                                            + outputAnalysis
                                            .hurtingOfficerPower()
                                            + "입니다.",
                                    "상관 출현 횟수는 "
                                            + outputAnalysis
                                            .hurtingOfficerOccurrenceCount()
                                            + "입니다.",
                                    "상관 중심성을 창의성·반항성·시험 결과 등의 단일 성향으로 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 10.
         * 식신 + 상관 혼재
         * =================================================
         */

        if (
                outputAnalysis
                        .mixedOutput()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "식신과 상관의 서로 다른 표현·산출 양식이 함께 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-3 OutputCompositeAnalysis에서 MIXED_OUTPUT Pattern이 의미 있게 성립합니다.",
                                    "식신과 상관의 병존을 하나의 표현 성향으로 축약하지 않습니다.",
                                    "복수의 식상 작동 양식이 함께 존재하는 구조를 MIXED Evidence로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 11.
         * 신약 + 식상 과도 설기
         * =================================================
         */

        if (
                outputAnalysis
                        .weakDayMasterExcessiveDrain()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "표현·산출 기능이 일간의 수용력과 비교할 때 과도한 소모 Context로 작동할 수 있습니다.",
                            List.of(
                                    tenGodReference,
                                    dayMasterReference
                            ),
                            List.of(
                                    "10-3 OutputCompositeAnalysis에서 WEAK_DAY_MASTER_EXCESSIVE_DRAIN Pattern이 의미 있게 성립합니다.",
                                    "현재 4차 일간 강약 Level은 "
                                            + outputAnalysis
                                            .dayMasterStrengthLevel()
                                            .name()
                                            + "입니다.",
                                    "식상의 설기를 성취 실패나 학습 저하로 단정하지 않고 산출 과정의 소모 Pressure로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 12.
         * 인성 -> 식상 제약 Context
         * =================================================
         *
         * 10-3에서 이미 판정한
         * RESOURCE_CONSTRAINED_OUTPUT.
         */

        if (
                outputAnalysis
                        .resourceConstrainedOutput()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "인성 계열과 식상 계열 사이에서 표현·산출 기능이 제약되는 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-3 OutputCompositeAnalysis에서 RESOURCE_CONSTRAINED_OUTPUT Pattern이 의미 있게 성립합니다.",
                                    "인성 Family 세력은 "
                                            + outputAnalysis
                                            .resourceFamilyPower()
                                            + "입니다.",
                                    "식상 Family 세력은 "
                                            + outputAnalysis
                                            .outputFamilyPower()
                                            + "입니다.",
                                    "인성의 식상 제약을 학습 또는 표현 실패로 직접 환산하지 않고 상호 제약 Pressure로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 13.
         * 식상 -> 인성 제약 Context
         * =================================================
         *
         * 10-6에서 이미 판정한
         * OUTPUT_CONSTRAINT_CONTEXT.
         *
         * 위 10-3 Pattern과 별개 Evidence이므로
         * 둘 다 성립하면 둘 다 보존한다.
         */

        if (
                resourceAnalysis
                        .outputConstraintContext()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "식상 계열과 인성 계열 사이에서 학습·내재화 기능이 제약되는 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 OUTPUT_CONSTRAINT_CONTEXT Pattern이 의미 있게 성립합니다.",
                                    "식상 Family 세력은 "
                                            + resourceAnalysis
                                            .outputFamilyPower()
                                            + "입니다.",
                                    "인성 Family 세력은 "
                                            + resourceAnalysis
                                            .resourceFamilyPower()
                                            + "입니다.",
                                    "식상의 인성 제약을 학업 실패로 단정하지 않고 흡수와 산출 사이의 Pressure Evidence로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 14.
         * 인성/식상 상호 제약 동시 성립
         * =================================================
         *
         * 10-3과 10-6이 각각 의미 있는
         * 제약 Context를 동시에 보존한 경우.
         *
         * 어느 한쪽을 삭제하지 않고
         * 상호 긴장 구조를 MIXED로 추가 보존한다.
         */

        if (
                outputAnalysis
                        .resourceConstrainedOutput()

                        && resourceAnalysis
                        .outputConstraintContext()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "학습·내재화와 표현·산출 사이의 상호 제약 구조가 함께 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-3 RESOURCE_CONSTRAINED_OUTPUT과 10-6 OUTPUT_CONSTRAINT_CONTEXT가 동시에 의미 있게 성립합니다.",
                                    "흡수와 산출 중 어느 한쪽의 구조를 삭제하거나 우선한다고 가정하지 않습니다.",
                                    "양쪽의 지원·제약 Evidence를 동시에 보존하여 학습·성장의 복합 Context로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 15.
         * 관살 -> 인성 생성 Context
         * =================================================
         *
         * 제도·책임·규범과 인성의 연결 가능성을
         * 보존하지만,
         *
         * 자격증·시험·학력 결과를
         * 새로 추론하지 않는다.
         */

        if (
                resourceAnalysis
                        .officerGenerationContext()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "관살과 인성이 연결되는 학습·성장 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 OFFICER_GENERATION_CONTEXT Pattern이 의미 있게 성립합니다.",
                                    "관살 Family 세력은 "
                                            + resourceAnalysis
                                            .officerFamilyPower()
                                            + "입니다.",
                                    "인성 Family 세력은 "
                                            + resourceAnalysis
                                            .resourceFamilyPower()
                                            + "입니다.",
                                    "관살 생인 Context를 시험·자격·승진 같은 실제 성과로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 16.
         * 재성 -> 인성 압박 Context
         * =================================================
         */

        if (
                resourceAnalysis
                        .wealthPressureContext()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "재성 계열과 인성 계열 사이에 학습·내재화 기능을 압박하는 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-6 ResourceCompositeAnalysis에서 WEALTH_PRESSURE_CONTEXT Pattern이 의미 있게 성립합니다.",
                                    "재성 Family 세력은 "
                                            + resourceAnalysis
                                            .wealthFamilyPower()
                                            + "입니다.",
                                    "인성 Family 세력은 "
                                            + resourceAnalysis
                                            .resourceFamilyPower()
                                            + "입니다.",
                                    "재성의 인성 압박을 학업 실패나 지식 부족으로 확정하지 않고 구조적 Pressure Evidence로 보존합니다."
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
     * Evidence 조회
     * =================================================
     */

    private LifeDomainEvidenceReference<?>
    requireSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        List<LifeDomainEvidenceReference<?>> evidences =
                bundle
                        .evidencesOf(
                                sourceType
                        );

        if (
                evidences.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "LearningResourceOutputRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }
}