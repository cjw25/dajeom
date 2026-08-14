package com.dajeom.rule;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.OfficerCompositeAnalysis;
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
 * 직업·사회 영역의 관살 구조 Rule.
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
 * 사용하는 기존 10-5 Pattern:
 *
 * OFFICER_DEVELOPED
 * DIRECT_OFFICER_CENTERED
 * SEVEN_KILLINGS_CENTERED
 * MIXED_OFFICER_KILLINGS
 * WEAK_DAY_MASTER_OFFICER_PRESSURE
 * OUTPUT_CONTROL_CONTEXT
 * RESOURCE_MEDIATION_CONTEXT
 * WEALTH_SUPPORT_CONTEXT
 *
 * =================================================
 *
 * 이 Rule은:
 *
 * - 관살 세력
 * - 정관/칠살 중심성
 * - 관살혼잡
 * - 일간 압박
 * - 식상 제어
 * - 인성 중재
 * - 재성의 관살 지원
 *
 * 을 새로 계산하지 않는다.
 *
 * 기존 10-5 OfficerCompositeAnalysis 결과를
 * 직업·사회 Domain의 deterministic Context로
 * 변환할 뿐이다.
 *
 * =================================================
 *
 * 금지:
 *
 * 정관 발달
 * -> 취업한다
 *
 * 관살 발달
 * -> 승진한다
 *
 * 칠살 강함
 * -> 직장 문제가 생긴다
 *
 * 같은 사건 결과 확정.
 */
@Component
public class CareerOfficerStructureRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_OFFICER_STRUCTURE";

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
                "CareerOfficerStructureRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerOfficerStructureRule은 CAREER Bundle만 평가할 수 있습니다."
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

        OfficerCompositeAnalysis officerAnalysis =
                tenGodCompositeAnalysis
                        .officerAnalysis();

        /*
         * =================================================
         * Upstream Identity 검증
         * =================================================
         */

        if (
                officerAnalysis
                        .dayMasterStrengthAnalysis()
                        != dayMasterStrengthAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-5 OfficerCompositeAnalysis와 "
                            + "18차 Bundle의 DayMasterStrengthAnalysis가 일치하지 않습니다."
            );
        }

        if (
                officerAnalysis.dayMaster()
                        != tenGodCompositeAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-5 OfficerCompositeAnalysis와 "
                            + "10차 TenGodCompositeAnalysis의 일간이 일치하지 않습니다."
            );
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 관살 자체가 없는 경우
         * =================================================
         *
         * 원국에 관살이 없다는 이유만으로
         * 직업·사회 역할이 없거나
         * 직업운이 약하다고 판정하지 않는다.
         *
         * 식상·인성·재성·격국·용신·운 Context 등
         * 다른 직업 Evidence가 존재할 수 있다.
         */

        if (
                !officerAnalysis
                        .officerPresent()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 관살 발달
         * =================================================
         */

        if (
                officerAnalysis
                        .officerDeveloped()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "원국에서 관살 기능이 의미 있는 구조로 형성되어 있습니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 OFFICER_DEVELOPED Pattern이 의미 있게 성립합니다.",
                                    "관살 Family 세력은 "
                                            + officerAnalysis
                                            .officerFamilyPower()
                                            + "입니다.",
                                    "관살 점유율은 "
                                            + officerAnalysis
                                            .officerFamilyShareBasisPoints()
                                            + "bp입니다.",
                                    "관살 구조의 발달을 취업·승진 같은 실제 직업 사건으로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 정관 중심
         * =================================================
         */

        if (
                officerAnalysis
                        .directOfficerCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "정관 계열이 직업·사회 역할 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 DIRECT_OFFICER_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "정관 배분 세력은 "
                                            + officerAnalysis
                                            .directOfficerPower()
                                            + "입니다.",
                                    "정관 출현 횟수는 "
                                            + officerAnalysis
                                            .directOfficerOccurrenceCount()
                                            + "입니다.",
                                    "정관 중심성을 특정 직업 형태나 안정적 취업 결과로 단정하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 칠살 중심
         * =================================================
         */

        if (
                officerAnalysis
                        .sevenKillingsCentered()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "칠살 계열이 직업·사회 역할 구조의 주요 축으로 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 SEVEN_KILLINGS_CENTERED Pattern이 의미 있게 성립합니다.",
                                    "칠살 배분 세력은 "
                                            + officerAnalysis
                                            .sevenKillingsPower()
                                            + "입니다.",
                                    "칠살 출현 횟수는 "
                                            + officerAnalysis
                                            .sevenKillingsOccurrenceCount()
                                            + "입니다.",
                                    "칠살 중심성을 직업 불안정이나 갈등 사건으로 자동 변환하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 정관 + 칠살 혼재
         * =================================================
         *
         * 관살혼잡을 무조건 흉으로 처리하지 않는다.
         *
         * 서로 다른 관살 작동 방식이
         * 동시에 존재한다는 사실을 MIXED로 보존한다.
         */

        if (
                officerAnalysis
                        .mixedOfficerKillings()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "정관과 칠살의 사회적 역할·압력 구조가 함께 작동합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 MIXED_OFFICER_KILLINGS Pattern이 의미 있게 성립합니다.",
                                    "정관과 칠살의 병존을 하나의 긍정 또는 부정 상태로 축약하지 않습니다.",
                                    "관살 혼재의 실제 작동 방식은 일간 수용력과 식상·인성·재성 Context를 함께 봐야 합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 신약 + 관살 압박
         * =================================================
         *
         * 10-5에서 이미 4차 일간 강약과
         * 관살 구조를 교차한 결과를 사용한다.
         *
         * 18차에서 신강신약 기준을 다시 계산하지 않는다.
         */

        if (
                officerAnalysis
                        .weakDayMasterOfficerPressure()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "관살 기능이 존재하지만 일간의 수용력과 비교할 때 사회적 역할·책임 부담 Context가 함께 존재합니다.",
                            List.of(
                                    tenGodReference,
                                    dayMasterReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 WEAK_DAY_MASTER_OFFICER_PRESSURE Pattern이 의미 있게 성립합니다.",
                                    "현재 4차 일간 강약 Level은 "
                                            + officerAnalysis
                                            .dayMasterStrengthLevel()
                                            .name()
                                            + "입니다.",
                                    "관살 압박을 직장 실패나 갈등 사건으로 확정하지 않고 수용력과 함께 봐야 할 Pressure Evidence로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 식상 제어 Context
         * =================================================
         *
         * 식상이 관살과 관계를 형성하는 구조를
         * 무조건 제살 성공으로 확정하지 않는다.
         */

        if (
                officerAnalysis
                        .outputControlContext()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "식상 계열이 관살 구조를 조절하거나 대응하는 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 OUTPUT_CONTROL_CONTEXT Pattern이 의미 있게 성립합니다.",
                                    "식상 Family 세력은 "
                                            + officerAnalysis
                                            .outputFamilyPower()
                                            + "입니다.",
                                    "식상과 관살의 관계를 자동으로 긍정적인 제살 성립이나 직업 성취 결과로 확정하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 인성 중재 Context
         * =================================================
         *
         * 인성이 관살 압력을 중재할 수 있는
         * 구조적 Context를 보존한다.
         *
         * 살인상생 성립 여부를
         * 이 Rule에서 새로 계산하지 않는다.
         */

        if (
                officerAnalysis
                        .resourceMediationContext()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "인성 계열이 관살 구조를 중재하는 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 RESOURCE_MEDIATION_CONTEXT Pattern이 의미 있게 성립합니다.",
                                    "인성 Family 세력은 "
                                            + officerAnalysis
                                            .resourceFamilyPower()
                                            + "입니다.",
                                    "인성 중재 Context를 자격 취득·승진·조직 적응 같은 실제 사건으로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 재성의 관살 지원 Context
         * =================================================
         *
         * 재성이 관살과 연결되는 구조가 있음을
         * 보존할 뿐,
         *
         * 재생관 완성이나
         * 재물 -> 승진 사건을
         * 여기서 새로 확정하지 않는다.
         */

        if (
                officerAnalysis
                        .wealthSupportContext()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "재성 계열이 관살 기능을 지원하는 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 WEALTH_SUPPORT_CONTEXT Pattern이 의미 있게 성립합니다.",
                                    "재성 Family 세력은 "
                                            + officerAnalysis
                                            .wealthFamilyPower()
                                            + "입니다.",
                                    "재성의 관살 지원 Context를 직업적 성취나 승진 결과로 단정하지 않습니다."
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
                    "CareerOfficerStructureRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }
}