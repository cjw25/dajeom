package com.dajeom.rule;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.OfficerCompositeAnalysis;
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
 * 관계 영역의 십성 복합구조 Context Rule.
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
 * 사용하는 기존 분석:
 *
 * 10-4 WealthCompositeAnalysis
 * 10-5 OfficerCompositeAnalysis
 *
 * =================================================
 *
 * 중요:
 *
 * 현재 18차 Evidence에는 Gender가 포함되어 있지 않다.
 *
 * 따라서 이 Rule에서는:
 *
 * 남성 -> 재성 = 배우자성
 * 여성 -> 관살 = 배우자성
 *
 * 같은 성별 기반 배우자성 매핑을 수행하지 않는다.
 *
 * =================================================
 *
 * 재성·관살은 각각:
 *
 * - 관계 속 자원 교환
 * - 책임과 역할
 * - 통제와 부담
 * - 관계에서의 구조 Context
 *
 * 를 읽기 위한 독립 계산 결과로 사용한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * WealthCompositeAnalysis와
 * OfficerCompositeAnalysis에서 각각 계산된 결과는
 * 독립 Match로 유지한다.
 *
 * 두 결과가 동시에 존재한다는 이유만으로:
 *
 * - 새로운 복합 상태
 * - 새로운 관계
 * - 새로운 인과
 * - 강화 또는 약화
 * - 상쇄
 * - 추가 종합 의미
 *
 * 를 생성하지 않는다.
 *
 * 단, 각 upstream Analysis 내부에서
 * Java가 이미 명시적으로 계산한 Pattern은
 * 해당 계산 결과 그대로 사용할 수 있다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 재성 발달 = 배우자복
 * - 관살 발달 = 결혼운
 * - 재다신약 = 관계 실패
 * - 관살압박 = 이별
 *
 * 등의 사건 결과 확정.
 */
@Component
public class RelationshipTenGodContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_TEN_GOD_CONTEXT";

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

        return LifeDomain.RELATIONSHIP;
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
                "RelationshipTenGodContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipTenGodContextRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
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

        OfficerCompositeAnalysis officerAnalysis =
                tenGodCompositeAnalysis
                        .officerAnalysis();

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
                wealthAnalysis.dayMaster()
                        != tenGodCompositeAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-4 WealthCompositeAnalysis와 "
                            + "10차 TenGodCompositeAnalysis의 일간이 일치하지 않습니다."
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
         * 1.
         * 재성 발달 Context
         * =================================================
         *
         * 배우자성으로 확정하지 않는다.
         */

        if (
                wealthAnalysis
                        .wealthDeveloped()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "재성 계열이 관계 영역에서 참조할 수 있는 의미 있는 구조로 형성되어 있습니다.",
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
                                    "현재 18차 Evidence에는 성별 정보가 없으므로 재성을 배우자성으로 확정하지 않습니다.",
                                    "재성 구조는 관계에서 자원·현실적 교환·운용 Context를 읽기 위한 구조로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 2.
         * 관살 발달 Context
         * =================================================
         *
         * 배우자성으로 확정하지 않는다.
         */

        if (
                officerAnalysis
                        .officerDeveloped()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "관살 계열이 관계 영역에서 참조할 수 있는 의미 있는 역할·책임 구조로 형성되어 있습니다.",
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
                                    "현재 18차 Evidence에는 성별 정보가 없으므로 관살을 배우자성으로 확정하지 않습니다.",
                                    "관살 구조는 관계에서 역할·책임·통제·긴장 Context를 읽기 위한 구조로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * 정재 / 편재 혼재
         * =================================================
         *
         * WealthCompositeAnalysis 내부에서
         * Java가 이미 계산한 MIXED_WEALTH Pattern.
         *
         * 관계 방식 자체를 단정하지 않는다.
         */

        if (
                wealthAnalysis
                        .mixedWealth()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "정재와 편재의 서로 다른 재성 작동 양식이 함께 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-4 WealthCompositeAnalysis에서 MIXED_WEALTH Pattern이 의미 있게 성립합니다.",
                                    "정재와 편재의 병존을 관계의 좋고 나쁨으로 변환하지 않습니다.",
                                    "10-4에서 이미 계산된 복수 재성 구조를 해당 계산 범위의 Context로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * 정관 / 칠살 혼재
         * =================================================
         *
         * OfficerCompositeAnalysis 내부에서
         * Java가 이미 계산한
         * MIXED_OFFICER_KILLINGS Pattern.
         *
         * 관살혼잡 자체를
         * 관계 불안정 사건으로 만들지 않는다.
         */

        if (
                officerAnalysis
                        .mixedOfficerKillings()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "정관과 칠살의 서로 다른 역할·압력 양식이 함께 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-5 OfficerCompositeAnalysis에서 MIXED_OFFICER_KILLINGS Pattern이 의미 있게 성립합니다.",
                                    "정관과 칠살의 병존을 배우자 문제나 관계 불안정으로 자동 변환하지 않습니다.",
                                    "10-5에서 이미 계산된 관살 혼재 구조를 해당 계산 범위의 Context로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 5.
         * 신약 + 재성 부담
         * =================================================
         *
         * 10-4 WealthCompositeAnalysis에서
         * 이미 명시적으로 계산된 결과.
         *
         * 이 Rule에서 신약과 재성을
         * 다시 조합해서 계산하지 않는다.
         */

        if (
                wealthAnalysis
                        .weakDayMasterWealthBurden()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "관계에서 현실적 요구나 자원 운용을 감당하는 측면에 부담 Context가 존재할 수 있습니다.",
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
                                    "재성 부담을 금전 문제·배우자 문제 같은 실제 관계 사건으로 확정하지 않습니다.",
                                    "10-4에서 이미 계산된 일간 수용력 대비 재성 부담의 Pressure 상태만 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 6.
         * 신약 + 관살 압박
         * =================================================
         *
         * 10-5 OfficerCompositeAnalysis에서
         * 이미 명시적으로 계산된 결과.
         *
         * 이 Rule에서 신약과 관살을
         * 다시 조합해서 계산하지 않는다.
         */

        if (
                officerAnalysis
                        .weakDayMasterOfficerPressure()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "관계에서 역할·책임·통제 요구를 감당하는 측면에 부담 Context가 존재할 수 있습니다.",
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
                                    "관살 압박을 배우자 갈등·이별 같은 실제 사건으로 확정하지 않습니다.",
                                    "10-5에서 이미 계산된 일간 수용력 대비 관살 Pressure 상태만 보존합니다."
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
                    "RelationshipTenGodContextRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }
}