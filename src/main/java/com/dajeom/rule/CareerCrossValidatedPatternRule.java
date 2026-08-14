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
 * 직업·사회 영역의
 * 10-8 / 10-9 십성 핵심 Pattern
 * 교차검증 Rule.
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
 * 위 내용을 다시 계산하지 않는다.
 *
 * =================================================
 *
 * 직업·사회 주요 Pattern:
 *
 * EATING_GOD_CONTROLS_SEVEN_KILLINGS
 * 식신제살
 *
 * HURTING_OFFICER_MEETS_DIRECT_OFFICER
 * 상관견관
 *
 * DIRECT_OFFICER_GENERATES_RESOURCE
 * 정관생인
 *
 * SEVEN_KILLINGS_GENERATES_RESOURCE
 * 칠살생인
 *
 * MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS
 * 정관·칠살 혼재
 *
 * WEALTH_GENERATES_DIRECT_OFFICER
 * 재생정관
 *
 * =================================================
 *
 * 중요:
 *
 * 10-8 Pattern 자체와
 * 10-9 교차검증 상태는
 * 서로 다른 정보다.
 *
 * 예:
 *
 * 상관견관 Pattern 존재
 * +
 * 10-9 WEAKENED
 *
 * 이라고 해서:
 *
 * "상관견관이 없다"
 *
 * 로 제거하지 않는다.
 *
 * 원 Pattern과
 * 전체 구조에서의 교차검증 상태를
 * 동시에 보존한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 식신제살
 * -> 승진
 *
 * 정관생인
 * -> 공무원 적합
 *
 * 상관견관
 * -> 직장 문제 발생
 *
 * 관살혼잡
 * -> 이직
 *
 * 같은 실제 직업 사건 확정.
 */
@Component
public class CareerCrossValidatedPatternRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_CROSS_VALIDATED_PATTERN";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .TEN_GOD_COMPOSITE
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
                "CareerCrossValidatedPatternRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerCrossValidatedPatternRule은 CAREER Bundle만 평가할 수 있습니다."
            );
        }

        LifeDomainEvidenceReference<?>
                tenGodReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .TEN_GOD_COMPOSITE
                );

        TenGodCompositeAnalysis analysis =
                tenGodReference
                        .requireSource(
                                TenGodCompositeAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 식신제살
         * =================================================
         *
         * 칠살 압력에 대해 식상의 제어 구조가
         * 전체 구조에서도 지지되는 경우
         * 직업·사회 영역의 SUPPORTIVE Context.
         *
         * 그러나 승진·합격·조직 성공을 뜻하지 않는다.
         */

        addSupportPatternMatch(
                matches,
                tenGodReference,
                analysis,
                TenGodCorePatternType
                        .EATING_GOD_CONTROLS_SEVEN_KILLINGS,
                "칠살의 압력에 식신 계열의 제어 작용이 연결되는 구조가 존재합니다.",
                "EATING_GOD_CONTROLS_SEVEN_KILLINGS"
        );

        /*
         * =================================================
         * 2.
         * 상관견관
         * =================================================
         *
         * 표현·행동 기능과
         * 정관의 규범·역할 기능 사이에
         * 충돌 가능성이 존재하는 Pattern.
         */

        addPressurePatternMatch(
                matches,
                tenGodReference,
                analysis,
                TenGodCorePatternType
                        .HURTING_OFFICER_MEETS_DIRECT_OFFICER,
                "상관의 표현·행동 기능과 정관의 규범·역할 기능 사이에 긴장 구조가 존재합니다.",
                "HURTING_OFFICER_MEETS_DIRECT_OFFICER"
        );

        /*
         * =================================================
         * 3.
         * 정관생인
         * =================================================
         *
         * 역할·책임이 인성의
         * 학습·정리·내재화 기능으로
         * 연결되는 Context.
         *
         * 직업 성취로 자동 승격하지 않는다.
         */

        addContextPatternMatch(
                matches,
                tenGodReference,
                analysis,
                TenGodCorePatternType
                        .DIRECT_OFFICER_GENERATES_RESOURCE,
                "정관의 역할·책임 기능이 인성 계열로 이어지는 구조가 존재합니다.",
                "DIRECT_OFFICER_GENERATES_RESOURCE"
        );

        /*
         * =================================================
         * 4.
         * 칠살생인
         * =================================================
         *
         * 강한 외부 압력 또는 책임이
         * 인성 기능으로 연결되는 Context.
         *
         * 살인상생의 사건 결과를
         * 새로 확정하지 않는다.
         */

        addContextPatternMatch(
                matches,
                tenGodReference,
                analysis,
                TenGodCorePatternType
                        .SEVEN_KILLINGS_GENERATES_RESOURCE,
                "칠살의 압력·책임 기능이 인성 계열로 이어지는 구조가 존재합니다.",
                "SEVEN_KILLINGS_GENERATES_RESOURCE"
        );

        /*
         * =================================================
         * 5.
         * 정관·칠살 혼재
         * =================================================
         *
         * Pattern 자체가 복수 관살 양식의
         * 공존을 의미하므로,
         *
         * 10-9에서 충분히 지지되면
         * MIXED로 보존한다.
         *
         * QUALIFIED / WEAKENED라면
         * 강한 혼재 결론 대신 CONTEXTUAL.
         */

        addMixedPatternMatch(
                matches,
                tenGodReference,
                analysis,
                TenGodCorePatternType
                        .MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS,
                "정관과 칠살의 서로 다른 사회적 역할·압력 양식이 함께 존재합니다.",
                "MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS"
        );

        /*
         * =================================================
         * 6.
         * 재생정관
         * =================================================
         *
         * 재성 기능이 정관 구조를
         * 지원하는 연결.
         *
         * 직업 Domain에서는
         * 자원과 사회적 역할의 연결 Context로 본다.
         */

        addContextPatternMatch(
                matches,
                tenGodReference,
                analysis,
                TenGodCorePatternType
                        .WEALTH_GENERATES_DIRECT_OFFICER,
                "재성 기능이 정관의 역할·책임 구조로 이어지는 Context가 존재합니다.",
                "WEALTH_GENERATES_DIRECT_OFFICER"
        );

        /*
         * =================================================
         * 7.
         * 식상 -> 관살 Family 제어
         * =================================================
         *
         * 10-7의 broader Family 관계.
         *
         * 식신제살 같은 특정 Pattern보다
         * 넓은 범위이므로 별도 보존한다.
         */

        if (
                analysis
                        .outputControlsOfficer()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "식상과 관살 사이에 제어 방향의 Family 관계가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-7 TenGodFamilyChainAnalysis에서 outputControlsOfficer=true입니다.",
                                    "식상 계열의 표현·산출 기능과 관살 계열의 역할·규범 기능 사이에 제어 관계가 존재합니다.",
                                    "이 Family 관계는 EATING_GOD_CONTROLS_SEVEN_KILLINGS 같은 특정 10-8 Pattern보다 넓은 구조입니다.",
                                    "식상 제어 구조의 존재를 직업 성공·조직 적응 결과로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 8.
         * 재성 -> 관살 Family Chain
         * =================================================
         *
         * 특정 재생정관 Pattern보다
         * broader한 재성 -> 관살 연결.
         */

        if (
                analysis
                        .wealthGeneratesOfficer()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "재성과 관살 사이에 생성 방향의 Family Chain이 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-7 TenGodFamilyChainAnalysis에서 wealthGeneratesOfficer=true입니다.",
                                    "재성의 자원·현실 운용 기능이 관살의 역할·책임 기능과 연결됩니다.",
                                    "WEALTH_GENERATES_DIRECT_OFFICER는 정관에 한정된 특정 Pattern이며, 현재 Evidence는 더 넓은 Family 연결입니다.",
                                    "재성의 관살 지원을 승진·직위 획득 같은 실제 사건으로 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 9.
         * 관살 -> 인성 Family Chain
         * =================================================
         */

        if (
                analysis
                        .officerGeneratesResource()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "관살과 인성 사이에 생성 방향의 Family Chain이 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-7 TenGodFamilyChainAnalysis에서 officerGeneratesResource=true입니다.",
                                    "사회적 역할·책임 구조가 학습·정리·내재화 기능과 연결되는 Family Context입니다.",
                                    "DIRECT_OFFICER_GENERATES_RESOURCE와 SEVEN_KILLINGS_GENERATES_RESOURCE는 각각 세부 Pattern으로 별도 보존될 수 있습니다.",
                                    "관인 연결을 자격·직위·조직 성공 결과로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 10.
         * 식상 -> 재성 -> 관살 -> 인성 전체 Chain
         * =================================================
         *
         * 전체 연쇄 자체를
         * 무조건 SUPPORTIVE로 만들지 않는다.
         *
         * 각 구간에 압박·충돌 Pattern이
         * 동시에 존재할 수 있기 때문이다.
         */

        if (
                analysis
                        .fullOutputWealthOfficerResourceChain()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "식상·재성·관살·인성이 이어지는 전체 십성 연쇄 구조가 존재합니다.",
                            List.of(
                                    tenGodReference
                            ),
                            List.of(
                                    "10-7 TenGodFamilyChainAnalysis에서 fullOutputWealthOfficerResourceChain=true입니다.",
                                    "식상 -> 재성 -> 관살 -> 인성의 전체 연쇄를 직업적 성공의 단일 지표로 사용하지 않습니다.",
                                    "직업·사회 영역에서는 표현·성과·자원·책임·학습 기능이 하나의 연쇄에 놓이는 Context로만 보존합니다.",
                                    "상관견관·관살혼재 등 별도 Pressure/Mixed Pattern이 존재하면 동시에 유지합니다."
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
     * SUPPORT 성격 Pattern
     * =================================================
     *
     * SUPPORTED
     * -> SUPPORTIVE
     *
     * CONFLICTED
     * -> MIXED
     *
     * QUALIFIED / WEAKENED
     * -> CONTEXTUAL
     *
     * 원 Pattern 자체는 항상 유지.
     */

    private void addSupportPatternMatch(

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

        LifeDomainSignalStatus status;

        if (
                analysis
                        .isPatternSupported(
                                patternType
                        )
        ) {

            status =
                    LifeDomainSignalStatus
                            .SUPPORTIVE;

        } else if (
                analysis
                        .isPatternConflicted(
                                patternType
                        )
        ) {

            status =
                    LifeDomainSignalStatus
                            .MIXED;

        } else {

            status =
                    LifeDomainSignalStatus
                            .CONTEXTUAL;
        }

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
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
                                "10-9는 격국·신강신약·용신·합화까지 교차검증한 결과입니다.",
                                "원 Pattern 자체와 교차검증 결과를 동시에 보존합니다.",
                                "SUPPORTIVE 상태도 실제 취업·승진·성과 사건을 의미하지 않습니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * PRESSURE 성격 Pattern
     * =================================================
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

        LifeDomainSignalStatus status;

        if (
                analysis
                        .isPatternSupported(
                                patternType
                        )
        ) {

            status =
                    LifeDomainSignalStatus
                            .PRESSURE;

        } else if (
                analysis
                        .isPatternConflicted(
                                patternType
                        )
        ) {

            status =
                    LifeDomainSignalStatus
                            .MIXED;

        } else {

            status =
                    LifeDomainSignalStatus
                            .CONTEXTUAL;
        }

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
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
                                "교차검증에서 SUPPORTED된 부담 Pattern은 직업영역의 PRESSURE로 보존합니다.",
                                "CONFLICTED 상태에서는 반대 방향 Evidence도 함께 존재하므로 MIXED로 유지합니다.",
                                "해당 구조를 실제 직장 갈등·실패·이직 사건으로 확정하지 않습니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * MIXED 성격 Pattern
     * =================================================
     *
     * Pattern 자체가
     * 복수 작동 방식의 병존을 나타낸다.
     *
     * SUPPORTED / CONFLICTED
     * -> MIXED
     *
     * QUALIFIED / WEAKENED
     * -> CONTEXTUAL
     */

    private void addMixedPatternMatch(

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

        boolean materiallyRetained =
                analysis
                        .isPatternSupported(
                                patternType
                        )
                        || analysis
                        .isPatternConflicted(
                                patternType
                        );

        LifeDomainSignalStatus status =
                materiallyRetained
                        ? LifeDomainSignalStatus.MIXED
                        : LifeDomainSignalStatus.CONTEXTUAL;

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
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
                                "정관과 칠살 중 어느 하나를 대표 구조로 임의 선택하지 않습니다.",
                                "Pattern이 전체 구조에서도 유지될 때 복수 역할·압력 양식을 MIXED로 보존합니다.",
                                "혼재 구조를 이직·갈등·직업 불안정 사건으로 직접 환산하지 않습니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * 일반 연결 Context Pattern
     * =================================================
     *
     * SUPPORTED라도 특정 사건 결과를
     * 의미하지 않는 연결형 Pattern은
     * CONTEXTUAL을 유지한다.
     *
     * CONFLICTED일 때만 MIXED.
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
                        LifeDomain.CAREER,
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
                                "연결 Pattern은 SUPPORTED 상태여도 실제 직업 성취 결과로 자동 승격하지 않습니다.",
                                "CONFLICTED 상태에서는 반대 Evidence가 함께 존재하므로 MIXED로 보존합니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * 10-9 상태 문자열
     * =================================================
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
                    "CareerCrossValidatedPatternRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }
}