package com.dajeom.rule;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.WealthCompositeAnalysis;
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
 * 재물 영역의 기본 수용력 / 부담 구조 Rule.
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
 * 사용하는 기존 10-4 Pattern:
 *
 * WEALTH_DEVELOPED
 * DIRECT_WEALTH_CENTERED
 * INDIRECT_WEALTH_CENTERED
 * MIXED_WEALTH
 * WEAK_DAY_MASTER_WEALTH_BURDEN
 * COMPANION_PRESSURED_WEALTH
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Rule은:
 *
 * - 재성 세력
 * - 신강신약
 * - 비겁 압박
 *
 * 을 새로 계산하지 않는다.
 *
 * 기존 10차 WealthCompositeAnalysis 결과를
 * 생활영역 Evidence로 해석할 뿐이다.
 *
 * =================================================
 *
 * 또한:
 *
 * 재성 있음
 * ->
 * 돈을 번다
 *
 * 재다신약
 * ->
 * 돈을 잃는다
 *
 * 같은 사건 결과를 만들지 않는다.
 *
 * LifeDomainSignalStatus는
 * 생활영역 Context의 방향만 표현한다.
 */
@Component
public class WealthCapacityRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_CAPACITY";

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
                "WealthCapacityRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCapacityRule은 WEALTH Bundle만 평가할 수 있습니다."
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

        WealthCompositeAnalysis wealthAnalysis =
                tenGodCompositeAnalysis
                        .wealthAnalysis();

        /*
         * =================================================
         * Upstream Identity 검증
         * =================================================
         */

        if (
                wealthAnalysis
                        .dayMasterStrengthAnalysis()
                        != dayMasterStrengthAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-4 WealthCompositeAnalysis와 "
                            + "18차 Bundle의 DayMasterStrengthAnalysis가 일치하지 않습니다."
            );
        }

        if (
                wealthAnalysis.dayMaster()
                        != tenGodCompositeAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-4 WealthCompositeAnalysis와 "
                            + "10차 TenGodCompositeAnalysis의 일간이 일치하지 않습니다."
            );
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 재성 자체가 없는 경우
         * =================================================
         *
         * 재성이 원국에 없다는 사실만으로
         * 재물 능력의 부재를 의미하지 않는다.
         *
         * 운 Context에서 재성 기능이
         * 새로 유입될 수 있으므로
         * 여기서는 Match를 생성하지 않는다.
         */

        if (
                !wealthAnalysis
                        .wealthPresent()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 재성 발달
         * =================================================
         */

        if (
                wealthAnalysis
                        .wealthDeveloped()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "원국에서 재성 기능이 의미 있는 구조로 형성되어 있습니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-4 WealthCompositeAnalysis에서 WEALTH_DEVELOPED Pattern이 의미 있게 성립합니다.",
                                    "재성 Family 세력은 "
                                            + wealthAnalysis
                                            .wealthFamilyPower()
                                            + "입니다.",
                                    "재성 점유율은 "
                                            + wealthAnalysis
                                            .wealthFamilyShareBasisPoints()
                                            + "bp입니다.",
                                    "재성 구조의 존재를 재물 획득 결과로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 정재 중심
         * =================================================
         */

        if (
                wealthAnalysis
                        .directWealthCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "정재 계열이 재성 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-4 WealthCompositeAnalysis에서 DIRECT_WEALTH_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "정재 배분 세력은 "
                                            + wealthAnalysis
                                            .directWealthPower()
                                            + "입니다.",
                                    "정재 출현 횟수는 "
                                            + wealthAnalysis
                                            .directWealthOccurrenceCount()
                                            + "입니다.",
                                    "정재 중심 구조는 재물 운용 방식의 Context이며 실제 수입 사건을 확정하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 편재 중심
         * =================================================
         */

        if (
                wealthAnalysis
                        .indirectWealthCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "편재 계열이 재성 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-4 WealthCompositeAnalysis에서 INDIRECT_WEALTH_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "편재 배분 세력은 "
                                            + wealthAnalysis
                                            .indirectWealthPower()
                                            + "입니다.",
                                    "편재 출현 횟수는 "
                                            + wealthAnalysis
                                            .indirectWealthOccurrenceCount()
                                            + "입니다.",
                                    "편재 중심 구조는 재물 운용 방식의 Context이며 투기·횡재 같은 사건을 자동 추론하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 정재 + 편재 혼재
         * =================================================
         */

        if (
                wealthAnalysis
                        .mixedWealth()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "정재와 편재의 재물 운용 성향이 함께 작동하는 구조입니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-4 WealthCompositeAnalysis에서 MIXED_WEALTH Pattern이 의미 있게 성립합니다.",
                                    "정재와 편재가 모두 존재하는 상태를 단일 재물 성향으로 축약하지 않습니다.",
                                    "정재·편재의 병존은 운 Context와 다른 구조에 따라 서로 다른 방식으로 드러날 수 있습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 재다신약 계열 부담
         * =================================================
         *
         * 10-4에서 이미 재성 구조와
         * 4차 일간 강약을 교차한 결과를 사용한다.
         *
         * 18차에서 신약 기준을 새로 계산하지 않는다.
         */

        if (
                wealthAnalysis
                        .weakDayMasterWealthBurden()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "재성 기능이 존재하지만 일간의 수용력과 비교할 때 부담 Context가 함께 존재합니다.",
                            List.of(
                                    tenGodReference,
                                    dayMasterReference
                            ),
                            List.of(
                                    "10-4 WealthCompositeAnalysis에서 WEAK_DAY_MASTER_WEALTH_BURDEN Pattern이 의미 있게 성립합니다.",
                                    "현재 4차 일간 강약 Level은 "
                                            + wealthAnalysis
                                            .dayMasterStrengthLevel()
                                            .name()
                                            + "입니다.",
                                    "재성의 존재 자체를 손실로 해석하지 않고, 감당력과 함께 봐야 하는 부담 Evidence로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 비겁의 재성 압박
         * =================================================
         */

        if (
                wealthAnalysis
                        .companionPressuredWealth()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "비겁 계열의 힘이 재성 기능에 압박으로 작용하는 구조가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-4 WealthCompositeAnalysis에서 COMPANION_PRESSURED_WEALTH Pattern이 의미 있게 성립합니다.",
                                    "비겁 Family 세력은 "
                                            + wealthAnalysis
                                            .companionFamilyPower()
                                            + "입니다.",
                                    "재성 Family 세력은 "
                                            + wealthAnalysis
                                            .wealthFamilyPower()
                                            + "입니다.",
                                    "비겁의 재성 압박을 실제 금전 손실 사건으로 확정하지 않고 구조적 부담 Context로 유지합니다."
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
                    "WealthCapacityRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }
}