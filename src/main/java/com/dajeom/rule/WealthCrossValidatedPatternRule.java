package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.TenGodCorePatternType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 재물 영역의 10-8 / 10-9
 * 십성 핵심 Pattern 교차검증 Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 10차 TenGodCompositeAnalysis
 *
 * =================================================
 *
 * 10-9는 이미 다음을 교차검증했다.
 *
 * - 10-8 핵심 십성 Pattern
 * - 5차 격국
 * - 4차 신강신약
 * - 7차 용신
 * - 9차 합화
 *
 * 따라서 18차에서는
 * 격국·용신·합화 Alignment를 다시 계산하지 않는다.
 *
 * =================================================
 *
 * 주요 재물 Pattern:
 *
 * COMPANION_CONTESTS_WEALTH
 *
 * 비겁쟁재 계열.
 *
 * WEALTH_HEAVY_WEAK_DAY_MASTER
 *
 * 재다신약 계열.
 *
 * WEALTH_GENERATES_DIRECT_OFFICER
 *
 * 재생정관 계열.
 *
 * =================================================
 *
 * 중요:
 *
 * 10-8 원 Pattern과
 * 10-9 교차검증 상태를 모두 보존한다.
 *
 * 예:
 *
 * COMPANION_CONTESTS_WEALTH
 * 원 Pattern 존재
 * +
 * 10-9 WEAKENED
 *
 * 이라면:
 *
 * "비겁쟁재가 없다"
 *
 * 로 삭제하지 않는다.
 *
 * 원 Pattern은 존재하지만
 * 전체 구조 교차검증에서는 약화된 Context라는
 * 두 사실을 동시에 보존한다.
 *
 * =================================================
 *
 * 이 Rule은:
 *
 * 돈을 번다
 * 돈을 잃는다
 * 투자 성공
 * 재산 손실
 *
 * 같은 실제 사건을 확정하지 않는다.
 */
@Component
public class WealthCrossValidatedPatternRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_CROSS_VALIDATED_PATTERN";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .TEN_GOD_COMPOSITE
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
                "WealthCrossValidatedPatternRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCrossValidatedPatternRule은 WEALTH Bundle만 평가할 수 있습니다."
            );
        }

        LifeDomainEvidenceReference<?>
                tenGodReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .TEN_GOD_COMPOSITE
                );

        TenGodCompositeAnalysis tenGodCompositeAnalysis =
                tenGodReference
                        .requireSource(
                                TenGodCompositeAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 식상 -> 재성 Family Chain
         * =================================================
         *
         * 10-7 Family Chain 결과.
         *
         * 식상에서 재성으로 흐르는 연결이 존재한다는
         * 구조적 Context만 보존한다.
         *
         * 수입 발생이나 생산성 성과를 뜻하지 않는다.
         */

        if (
                tenGodCompositeAnalysis
                        .outputGeneratesWealth()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "식상에서 재성으로 이어지는 십성 Family Chain이 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-7 TenGodFamilyChainAnalysis에서 outputGeneratesWealth=true입니다.",
                                    "표현·산출 계열과 재성 계열 사이의 생 흐름이 구조적으로 연결되어 있습니다.",
                                    "식상생재 구조의 존재를 실제 수입·매출·재산 증가 사건으로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 2.
         * 비겁쟁재
         * =================================================
         *
         * 원 Pattern 존재 여부와
         * 10-9 최종 상태를 함께 읽는다.
         */

        addPressurePatternMatch(
                matches,
                tenGodReference,
                tenGodCompositeAnalysis,
                TenGodCorePatternType
                        .COMPANION_CONTESTS_WEALTH,
                "비겁과 재성 사이의 경쟁·분산 구조가 재물 영역에 존재합니다.",
                "COMPANION_CONTESTS_WEALTH"
        );

        /*
         * =================================================
         * 3.
         * 재다신약
         * =================================================
         */

        addPressurePatternMatch(
                matches,
                tenGodReference,
                tenGodCompositeAnalysis,
                TenGodCorePatternType
                        .WEALTH_HEAVY_WEAK_DAY_MASTER,
                "재성의 무게와 일간 수용력 사이에 부담 구조가 존재합니다.",
                "WEALTH_HEAVY_WEAK_DAY_MASTER"
        );

        /*
         * =================================================
         * 4.
         * 재생정관
         * =================================================
         *
         * 재성이 관성으로 이어지는 구조는
         * 재물 영역에서 단순 SUPPORTIVE로
         * 승격하지 않는다.
         *
         * 재물 기능이 사회적 역할·책임 축으로
         * 연결되는 Context로 보존한다.
         */

        addContextPatternMatch(
                matches,
                tenGodReference,
                tenGodCompositeAnalysis,
                TenGodCorePatternType
                        .WEALTH_GENERATES_DIRECT_OFFICER,
                "재성 기능이 정관 구조로 이어지는 재생관 Context가 존재합니다.",
                "WEALTH_GENERATES_DIRECT_OFFICER"
        );

        /*
         * =================================================
         * 5.
         * 재성 -> 관살 Family Chain
         * =================================================
         *
         * 10-7 Family Chain의 broader 관계도
         * 10-9 정관 Pattern과 별도로 보존한다.
         */

        if (
                tenGodCompositeAnalysis
                        .wealthGeneratesOfficer()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "재성에서 관살로 이어지는 십성 Family Chain이 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-7 TenGodFamilyChainAnalysis에서 wealthGeneratesOfficer=true입니다.",
                                    "재성의 자원 기능과 관살의 역할·책임 기능이 연쇄 구조로 연결되어 있습니다.",
                                    "이 연결을 재물 증가나 승진 사건으로 자동 변환하지 않습니다.",
                                    "10-9 WEALTH_GENERATES_DIRECT_OFFICER와는 범위가 다르므로 둘이 동시에 존재할 수 있습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 6.
         * 식상 -> 재성 -> 관살 -> 인성 전체 Chain
         * =================================================
         *
         * 전체 순환 Chain이 존재해도
         * 무조건 좋은 구조로 판단하지 않는다.
         *
         * 각 Family의 강약과
         * 10-9 Pattern 상태가 별도로 존재하기 때문이다.
         */

        if (
                tenGodCompositeAnalysis
                        .fullOutputWealthOfficerResourceChain()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "식상·재성·관살·인성이 이어지는 전체 십성 연쇄 Context가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-7 TenGodFamilyChainAnalysis에서 fullOutputWealthOfficerResourceChain=true입니다.",
                                    "식상 -> 재성 -> 관살 -> 인성의 연쇄 구조를 하나의 단순 길한 결과로 축약하지 않습니다.",
                                    "재물 영역에서는 재성이 전체 십성 흐름 속에서 어떤 연결 위치를 차지하는지 보여주는 Context로 사용합니다."
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
     * Pressure Pattern
     * =================================================
     *
     * 10-9 SUPPORTED
     * ->
     * 해당 부담 Pattern이 전체 구조에서도
     * 지지되므로 PRESSURE.
     *
     * CONFLICTED
     * ->
     * Pattern과 반대 Evidence가 공존하므로 MIXED.
     *
     * QUALIFIED / WEAKENED
     * ->
     * Pattern 자체는 삭제하지 않고
     * CONTEXTUAL로 보존.
     */

    private void addPressurePatternMatch(

            List<LifeDomainRuleMatch> matches,

            LifeDomainEvidenceReference<?> tenGodReference,

            TenGodCompositeAnalysis analysis,

            TenGodCorePatternType patternType,

            String summary,

            String patternName

    ) {

        if (
                !analysis
                        .hasMeaningfulCorePattern(
                                patternType
                        )
        ) {

            return;
        }

        LifeDomainSignalStatus status =
                pressurePatternStatus(
                        analysis,
                        patternType
                );

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.WEALTH,
                        RULE_CODE,
                        status,
                        summary,
                        List.of(
                                tenGodReference
                        ),
                        List.of(
                                "10-8 TenGodCorePatternAnalysis에서 "
                                        + patternName
                                        + " Pattern이 의미 있게 존재합니다.",
                                "10-9 교차검증 상태는 "
                                        + validationStatusName(
                                        analysis,
                                        patternType
                                )
                                        + "입니다.",
                                "10-9는 격국·신강신약·용신·합화를 함께 교차검증한 결과입니다.",
                                "원 Pattern과 교차검증 결과를 동시에 보존하며 Pattern을 삭제하지 않습니다.",
                                "이 부담 구조를 실제 금전 손실 사건으로 확정하지 않습니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * Context Pattern
     * =================================================
     *
     * 재생관 같은 연쇄구조는
     * 10-9 SUPPORTED라도 재물 Domain에서
     * 곧바로 SUPPORTIVE로 올리지 않는다.
     *
     * 재물이 다른 Family와 연결되는 방식의
     * Context이기 때문이다.
     *
     * CONFLICTED일 때만 MIXED를 사용한다.
     */

    private void addContextPatternMatch(

            List<LifeDomainRuleMatch> matches,

            LifeDomainEvidenceReference<?> tenGodReference,

            TenGodCompositeAnalysis analysis,

            TenGodCorePatternType patternType,

            String summary,

            String patternName

    ) {

        if (
                !analysis
                        .hasMeaningfulCorePattern(
                                patternType
                        )
        ) {

            return;
        }

        LifeDomainSignalStatus status =
                analysis
                        .isPatternConflicted(
                                patternType
                        )
                        ? LifeDomainSignalStatus.MIXED
                        : LifeDomainSignalStatus.CONTEXTUAL;

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.WEALTH,
                        RULE_CODE,
                        status,
                        summary,
                        List.of(
                                tenGodReference
                        ),
                        List.of(
                                "10-8 TenGodCorePatternAnalysis에서 "
                                        + patternName
                                        + " Pattern이 의미 있게 존재합니다.",
                                "10-9 교차검증 상태는 "
                                        + validationStatusName(
                                        analysis,
                                        patternType
                                )
                                        + "입니다.",
                                "교차검증이 SUPPORTED이더라도 해당 연쇄를 재물 획득 결과로 직접 승격하지 않습니다.",
                                "CONFLICTED 상태에서는 반대 Evidence가 함께 존재하므로 MIXED Context로 보존합니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * Pressure Pattern -> 18차 상태
     * =================================================
     */

    private LifeDomainSignalStatus pressurePatternStatus(

            TenGodCompositeAnalysis analysis,

            TenGodCorePatternType patternType

    ) {

        if (
                analysis
                        .isPatternSupported(
                                patternType
                        )
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        if (
                analysis
                        .isPatternConflicted(
                                patternType
                        )
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        /*
         * QUALIFIED / WEAKENED 또는
         * 방어적 미확정 상태.
         *
         * Pattern 자체는 남기되
         * 강한 Pressure로 확정하지 않는다.
         */
        return LifeDomainSignalStatus
                .CONTEXTUAL;
    }

    /*
     * =================================================
     * 10-9 상태 문자열
     * =================================================
     *
     * TenGodCrossValidationStatus의 별도
     * 표시 accessor를 추측하지 않는다.
     *
     * 현재 TenGodCompositeAnalysis의
     * 공식 boolean API만 사용한다.
     */

    private String validationStatusName(

            TenGodCompositeAnalysis analysis,

            TenGodCorePatternType patternType

    ) {

        if (
                analysis
                        .isPatternSupported(
                                patternType
                        )
        ) {

            return "SUPPORTED";
        }

        if (
                analysis
                        .isPatternQualified(
                                patternType
                        )
        ) {

            return "QUALIFIED";
        }

        if (
                analysis
                        .isPatternWeakened(
                                patternType
                        )
        ) {

            return "WEAKENED";
        }

        if (
                analysis
                        .isPatternConflicted(
                                patternType
                        )
        ) {

            return "CONFLICTED";
        }

        return "UNRESOLVED";
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
                    "WealthCrossValidatedPatternRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }
}