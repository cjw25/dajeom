package com.dajeom.rule;

import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.type.FiveElement;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 건강·생활리듬 영역의
 * 균형 / 소모 / 조정 필요 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 3차 ElementStrengthAnalysis
 * 4차 DayMasterStrengthAnalysis
 * 6차 ClimateHealthAnalysis
 * 7차 UsefulGodAnalysis
 *
 * =================================================
 *
 * 이 Rule의 HEALTH_LIFESTYLE 의미:
 *
 * - 오행 세력 분포
 * - 일간 수용력
 * - 조후 조정 필요
 * - 명리적 병약 구조
 * - 통관 필요
 * - 6차 내부 충돌 / 재검토
 * - 최종 용신·희신·기신 조정 방향
 *
 * =================================================
 *
 * 매우 중요:
 *
 * 여기서 말하는:
 *
 * 병약
 * pathology
 * pressure
 *
 * 는 명리 분석 내부의 구조 용어다.
 *
 * 의료적:
 *
 * - 질병
 * - 장기 이상
 * - 진단
 * - 발병
 * - 치료 필요성
 *
 * 을 의미하지 않는다.
 *
 * =================================================
 *
 * 금지:
 *
 * 木 부족 -> 간 질환
 * 火 부족 -> 심장 질환
 * 水 과다 -> 신장 질환
 *
 * 같은 오행-장기 의료 매핑.
 *
 * 또한:
 *
 * 병약 구조 존재
 * -> 병이 생긴다
 *
 * 로 해석하지 않는다.
 *
 * =================================================
 *
 * 기존 3~7차 결과를 다시 계산하지 않는다.
 */
@Component
public class HealthLifestyleBalanceRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "HEALTH_LIFESTYLE_BALANCE";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ELEMENT_STRENGTH,

                    LifeDomainEvidenceSourceType
                            .DAY_MASTER_STRENGTH,

                    LifeDomainEvidenceSourceType
                            .CLIMATE_DISEASE_FLOW,

                    LifeDomainEvidenceSourceType
                            .USEFUL_GOD
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.HEALTH_LIFESTYLE;
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
                "HealthLifestyleBalanceRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.HEALTH_LIFESTYLE
        ) {

            throw new IllegalArgumentException(
                    "HealthLifestyleBalanceRule은 HEALTH_LIFESTYLE Bundle만 평가할 수 있습니다."
            );
        }

        LifeDomainEvidenceReference<?>
                elementReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .ELEMENT_STRENGTH
                );

        LifeDomainEvidenceReference<?>
                dayMasterReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .DAY_MASTER_STRENGTH
                );

        LifeDomainEvidenceReference<?>
                climateReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .CLIMATE_DISEASE_FLOW
                );

        LifeDomainEvidenceReference<?>
                usefulGodReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .USEFUL_GOD
                );

        ElementStrengthAnalysis elementStrengthAnalysis =
                elementReference
                        .requireSource(
                                ElementStrengthAnalysis.class
                        );

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis =
                dayMasterReference
                        .requireSource(
                                DayMasterStrengthAnalysis.class
                        );

        ClimateHealthAnalysis climateHealthAnalysis =
                climateReference
                        .requireSource(
                                ClimateHealthAnalysis.class
                        );

        UsefulGodAnalysis usefulGodAnalysis =
                usefulGodReference
                        .requireSource(
                                UsefulGodAnalysis.class
                        );

        /*
         * =================================================
         * Upstream Identity / Chart 검증
         * =================================================
         */

        if (
                dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        != elementStrengthAnalysis
        ) {

            throw new IllegalArgumentException(
                    "4차 DayMasterStrengthAnalysis가 "
                            + "현재 3차 ElementStrengthAnalysis를 참조하지 않습니다."
            );
        }

        if (
                usefulGodAnalysis
                        .climateHealthAnalysis()
                        != climateHealthAnalysis
        ) {

            throw new IllegalArgumentException(
                    "7차 UsefulGodAnalysis가 "
                            + "현재 6차 ClimateHealthAnalysis를 참조하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis.dayMaster()
                        != dayMasterStrengthAnalysis.dayMaster()

                        || elementStrengthAnalysis.dayMaster()
                        != climateHealthAnalysis.dayMaster()

                        || elementStrengthAnalysis.dayMaster()
                        != usefulGodAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3·4·6·7차 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis.dayMasterElement()
                        != dayMasterStrengthAnalysis.dayMasterElement()

                        || elementStrengthAnalysis.dayMasterElement()
                        != climateHealthAnalysis.dayMasterElement()

                        || elementStrengthAnalysis.dayMasterElement()
                        != usefulGodAnalysis.dayMasterElement()
        ) {

            throw new IllegalArgumentException(
                    "3·4·6·7차 분석의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis.completeChart()
                        != dayMasterStrengthAnalysis.completeChart()

                        || elementStrengthAnalysis.completeChart()
                        != climateHealthAnalysis.completeChart()

                        || elementStrengthAnalysis.completeChart()
                        != usefulGodAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "3·4·6·7차 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 오행 세력 분포 Context
         * =================================================
         *
         * strongest / weakestExisting은
         * 단순 상대 분포 정보다.
         *
         * 부족 오행이나 질병 오행으로
         * 재해석하지 않는다.
         */

        Optional<FiveElement> strongestElement =
                elementStrengthAnalysis
                        .strongestElement();

        Optional<FiveElement> weakestExistingElement =
                elementStrengthAnalysis
                        .weakestExistingElement();

        List<String> elementReasons =
                new ArrayList<>();

        elementReasons.add(
                "3차 ElementStrengthAnalysis의 전체 최종 오행 세력은 "
                        + elementStrengthAnalysis.totalFinalPower()
                        + "입니다."
        );

        strongestElement.ifPresent(
                element ->
                        elementReasons.add(
                                "상대적으로 가장 강한 오행은 "
                                        + element.name()
                                        + "이며 세력은 "
                                        + elementStrengthAnalysis
                                        .powerOf(
                                                element
                                        )
                                        + ", 비중은 "
                                        + elementStrengthAnalysis
                                        .sharePercentOf(
                                                element
                                        )
                                        + "%입니다."
                        )
        );

        weakestExistingElement.ifPresent(
                element ->
                        elementReasons.add(
                                "원국에 실제 존재하는 오행 가운데 상대적으로 가장 약한 오행은 "
                                        + element.name()
                                        + "이며 세력은 "
                                        + elementStrengthAnalysis
                                        .powerOf(
                                                element
                                        )
                                        + ", 비중은 "
                                        + elementStrengthAnalysis
                                        .sharePercentOf(
                                                element
                                        )
                                        + "%입니다."
                        )
        );

        elementReasons.add(
                "강·약 오행의 상대 분포를 특정 장기·질환·발병 가능성과 연결하지 않습니다."
        );

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.HEALTH_LIFESTYLE,
                        RULE_CODE,
                        LifeDomainSignalStatus.CONTEXTUAL,
                        "원국의 오행 세력 분포를 생활 균형과 리듬을 해석하기 위한 기본 Context로 사용합니다.",
                        List.of(
                                elementReference
                        ),
                        elementReasons
                )
        );

        /*
         * =================================================
         * 2.
         * 일간 수용력 Context
         * =================================================
         *
         * 신강 = 건강
         * 신약 = 질병
         *
         * 으로 해석하지 않는다.
         */

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.HEALTH_LIFESTYLE,
                        RULE_CODE,
                        LifeDomainSignalStatus.CONTEXTUAL,
                        "일간의 지원력과 압력 관계를 생활상의 수용력·소모 Context로 참조합니다.",
                        List.of(
                                elementReference,
                                dayMasterReference
                        ),
                        List.of(
                                "4차 최종 신강·신약 Level은 "
                                        + dayMasterStrengthAnalysis
                                        .levelKorean()
                                        + "입니다.",
                                "구조 보정 강도 지수는 "
                                        + dayMasterStrengthAnalysis
                                        .strengthBasisPoints()
                                        + "bp입니다.",
                                "지원력은 "
                                        + dayMasterStrengthAnalysis
                                        .supportPower()
                                        + ", 압력은 "
                                        + dayMasterStrengthAnalysis
                                        .pressurePower()
                                        + "입니다.",
                                "신강·신약은 생활리듬의 수용력 Context이며 건강 상태나 질환 여부를 직접 의미하지 않습니다."
                        )
                )
        );

        /*
         * =================================================
         * 3.
         * 극단 신강·신약 구조 Context
         * =================================================
         *
         * extreme=true도 질병 판정이 아니다.
         *
         * 종격 가능성 등이 존재할 수 있으므로
         * 단순 PRESSURE로 변환하지 않는다.
         */

        if (
                dayMasterStrengthAnalysis
                        .extreme()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "일간 강약이 극단 구조 검토 영역에 있어 생활 균형 해석에도 구조적 주의가 필요합니다.",
                            List.of(
                                    dayMasterReference
                            ),
                            List.of(
                                    "4차 DayMasterStrengthAnalysis에서 extreme=true입니다.",
                                    "종격 등 특별 구조 검토 필요 여부는 "
                                            + dayMasterStrengthAnalysis
                                            .requiresFollowStructureReview()
                                            + "입니다.",
                                    "극단 신강·신약 자체를 건강 악화나 질병 발생으로 해석하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * 조후 조정 필요
         * =================================================
         */

        if (
                climateHealthAnalysis
                        .climateAdjustmentRequired()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "한난조습 균형에서 조정이 필요한 생활환경·리듬 Context가 존재합니다.",
                            List.of(
                                    climateReference
                            ),
                            List.of(
                                    "6차 ClimateAdjustmentNeedAnalysis에서 adjustmentRequired=true입니다.",
                                    "월지는 "
                                            + climateHealthAnalysis
                                            .monthBranch()
                                            .name()
                                            + "입니다.",
                                    "조후 조정 필요를 실제 체온 이상이나 특정 질환으로 해석하지 않습니다.",
                                    "생활환경과 리듬의 균형을 검토해야 하는 명리적 Pressure Evidence로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 5.
         * 명리적 병약 구조
         * =================================================
         *
         * Pathology는 명리적 용어.
         * 의료 Pathology가 아니다.
         */

        if (
                climateHealthAnalysis
                        .hasMeaningfulPathology()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "명리적 병약 구조에서 의미 있는 균형 저해 Context가 확인됩니다.",
                            List.of(
                                    climateReference
                            ),
                            List.of(
                                    "6차 PathologyAnalysis에서 meaningful pathology가 존재합니다.",
                                    "여기서 병약은 명리 구조 내부의 균형 저해 개념입니다.",
                                    "의학적 질환·발병·장기 이상을 의미하지 않습니다.",
                                    "실제 건강 상태에 대한 진단이나 예측으로 사용하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 6.
         * 미해결 명리적 병약
         * =================================================
         *
         * 병 구조 존재와
         * Remedy 이후 미해결 상태를
         * 서로 다른 Evidence로 보존.
         */

        if (
                climateHealthAnalysis
                        .hasUnresolvedMeaningfulPathology()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "명리적 병약 구조에 대해 현재 분석 단계에서 충분히 해소되지 않은 균형 부담 Context가 남아 있습니다.",
                            List.of(
                                    climateReference
                            ),
                            List.of(
                                    "6차 RemedyAnalysis 이후에도 unresolved meaningful pathology가 남아 있습니다.",
                                    "병약 존재와 미해결 여부를 하나의 상태로 합치지 않고 별도로 보존합니다.",
                                    "미해결 명리 병약을 실제 질환 지속이나 치료 필요성으로 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 7.
         * 통관 필요
         * =================================================
         */

        if (
                climateHealthAnalysis
                        .meaningfulPassageRequired()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "오행 작용 사이에 의미 있는 통관·중재가 필요한 생활 균형 Context가 존재합니다.",
                            List.of(
                                    climateReference
                            ),
                            List.of(
                                    "6차 PassageNeedAnalysis에서 meaningfulPassageRequired=true입니다.",
                                    "통관 필요를 신체 순환 장애나 의학적 문제로 해석하지 않습니다.",
                                    "오행 관계의 중재 필요성을 생활리듬의 구조적 Pressure Context로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 8.
         * 6차 통합 충돌
         * =================================================
         *
         * 조후 / 병약 / 통관 후보가
         * 한 방향으로 정리되지 않는 상태.
         */

        if (
                climateHealthAnalysis
                        .integrationConflict()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "조후·병약·통관 축의 조정 방향 사이에 통합 충돌 Context가 존재합니다.",
                            List.of(
                                    climateReference
                            ),
                            List.of(
                                    "6-8 ClimateHealthIntegrationAnalysis에서 conflict가 확인됩니다.",
                                    "서로 다른 조정 방향 중 하나를 임의로 제거하지 않습니다.",
                                    "생활 균형 해석에서도 복수 방향의 긴장을 MIXED Evidence로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 9.
         * 6차 교차검증 모순
         * =================================================
         */

        if (
                climateHealthAnalysis
                        .hasCrossValidationContradiction()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "6차 후보 교차검증 과정에서 서로 양립하기 어려운 조정 근거가 함께 존재합니다.",
                            List.of(
                                    climateReference
                            ),
                            List.of(
                                    "6-9 ClimateHealthCrossValidationAnalysis에서 contradiction이 확인됩니다.",
                                    "후보 간 모순을 하나의 오행으로 강제 선택하지 않습니다.",
                                    "생활영역에서도 불확실성과 상충 근거를 그대로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 10.
         * 6차 재검토 필요
         * =================================================
         */

        if (
                climateHealthAnalysis
                        .requiresReview()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "조후·병약·통관 구조가 단순한 하나의 조정 방향으로 축약되지 않는 재검토 상태입니다.",
                            List.of(
                                    climateReference
                            ),
                            List.of(
                                    "6차 최종 requiresReview=true입니다.",
                                    "추가 선택 필요="
                                            + climateHealthAnalysis
                                            .requiresFurtherSelection()
                                            + ", 후보 downgrade="
                                            + climateHealthAnalysis
                                            .hasDowngradedCandidate()
                                            + ", PRIMARY 소실="
                                            + climateHealthAnalysis
                                            .primaryLostAfterValidation()
                                            + ", 특별 구조 검토="
                                            + climateHealthAnalysis
                                            .requiresSpecialStructureReview()
                                            + "입니다.",
                                    "불확실한 후보를 억지로 단일 조정축으로 만들지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 11.
         * 최종 용신 조정축
         * =================================================
         *
         * 용신이 있다는 것은:
         *
         * 실제 생활환경에 용신 오행이
         * 충분히 들어와 있다는 뜻이 아니다.
         *
         * 따라서 SUPPORTIVE로 자동 승격하지 않고
         * 조정 방향 CONTEXTUAL로 보존한다.
         */

        if (
                usefulGodAnalysis
                        .hasUsefulGod()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "생활 균형을 해석할 때 참조할 최종 용신 조정축이 존재합니다.",
                            List.of(
                                    climateReference,
                                    usefulGodReference
                            ),
                            List.of(
                                    "7차 최종 용신 오행은 "
                                            + elementNames(
                                            usefulGodAnalysis
                                                    .usefulGodElements()
                                    )
                                            + "입니다.",
                                    "희신 오행은 "
                                            + elementNames(
                                            usefulGodAnalysis
                                                    .favorableGodElements()
                                    )
                                            + "입니다.",
                                    "기신 오행은 "
                                            + elementNames(
                                            usefulGodAnalysis
                                                    .unfavorableGodElements()
                                    )
                                            + "입니다.",
                                    "용신·희신의 존재 자체를 현재 생활환경이 이미 유리하다는 SUPPORTIVE 결과로 변환하지 않습니다.",
                                    "기신의 존재 역시 질병이나 건강 악화를 의미하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 12.
         * 복수 용신
         * =================================================
         *
         * 복수 용신은 오류나 충돌이 아니다.
         *
         * 하나를 임의 선택하지 않고
         * CONTEXTUAL로 보존한다.
         */

        if (
                usefulGodAnalysis
                        .hasMultipleUsefulGods()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "생활 균형의 최종 용신 조정축이 복수로 유지됩니다.",
                            List.of(
                                    usefulGodReference
                            ),
                            List.of(
                                    "7차 최종 용신이 "
                                            + usefulGodAnalysis
                                            .usefulGodElements()
                                            .size()
                                            + "개 존재합니다.",
                                    "복수 용신은 허용되는 정상 상태입니다.",
                                    "배열 첫 번째 용신을 대표 용신으로 임의 선택하지 않습니다.",
                                    "복수 조정축을 그대로 생활리듬 Context에 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 13.
         * 최종 용신 미확정 / 검토
         * =================================================
         */

        if (
                usefulGodAnalysis
                        .unresolvedUsefulGodSelection()

                        || usefulGodAnalysis
                        .multiplePrimaryReview()

                        || usefulGodAnalysis
                        .requiresReview()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "최종 조정축에 선택 불확실성 또는 추가 검토 Context가 남아 있습니다.",
                            List.of(
                                    usefulGodReference
                            ),
                            List.of(
                                    "주용신 미확정="
                                            + usefulGodAnalysis
                                            .unresolvedUsefulGodSelection()
                                            + "입니다.",
                                    "복수 PRIMARY 검토="
                                            + usefulGodAnalysis
                                            .multiplePrimaryReview()
                                            + "입니다.",
                                    "7차 추가 검토 필요="
                                            + usefulGodAnalysis
                                            .requiresReview()
                                            + "입니다.",
                                    "미확정 상태에서는 특정 오행 하나를 생활 균형의 유일한 해법으로 고정하지 않습니다."
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
                    "HealthLifestyleBalanceRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }

    /*
     * =================================================
     * Element 표시
     * =================================================
     */

    private String elementNames(
            List<FiveElement> elements
    ) {

        if (
                elements == null
                        || elements.isEmpty()
        ) {

            return "없음";
        }

        return elements
                .stream()
                .map(
                        FiveElement::name
                )
                .toList()
                .toString();
    }
}