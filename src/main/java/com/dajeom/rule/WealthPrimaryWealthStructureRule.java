package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
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
 * 재물 영역의 주격 재성 격국 Context Rule.
 *
 * =================================================
 *
 * Source:
 *
 * 5차 StructureAnalysis
 *
 * =================================================
 *
 * 현재 직접 판정 범위:
 *
 * primaryStandardTenGod:
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 *
 * =================================================
 *
 * 의미:
 *
 * 정재/편재가 주격이라는 것은
 * 원국 전체 구조에서:
 *
 * - 현실적 성과
 * - 자원 관리
 * - 교환과 획득
 * - 결과를 현실화하는 기능
 * - 돈과 자원을 다루는 요구
 *
 * 가 중심축으로 관여한다는
 * 구조 Evidence다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 정재격
 * -> 돈을 잘 번다
 *
 * 편재격
 * -> 사업을 잘한다
 *
 * 재격 성립
 * -> 부자가 된다
 *
 * 재격 파격
 * -> 돈을 잃는다
 *
 * 재격
 * -> 투자에 강하다
 *
 * 같은 실제 재물 결과나 사건을
 * 직접 생성하지 않는다.
 *
 * =================================================
 *
 * 기존 Wealth Rule과의 차이:
 *
 * WealthCapacityRule:
 *
 * TenGodCompositeAnalysis를 통해
 * 재성의 실제 발달도와
 * 일간 수용력, 비겁 압박 등을 본다.
 *
 * WealthCrossValidatedPatternRule:
 *
 * 재성 관련 복합 Pattern의
 * 교차검증 결과를 본다.
 *
 * 이 Rule:
 *
 * 5차 StructureAnalysis에서
 * 실제 주격이 정재/편재로 선택되었는지,
 * 그리고 그 주격이:
 *
 * - 성립
 * - 부담
 * - 손상
 * - 혼잡
 *
 * 중 어떤 상태인지 보존한다.
 *
 * =================================================
 *
 * 상태:
 *
 * SUPPORTIVE
 *
 * 재물운이 좋다는 뜻이 아니다.
 *
 * 재성 주격이 원국의 중심축으로
 * 비교적 성립되어 있다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * PRESSURE
 *
 * 돈을 잃는다는 뜻이 아니다.
 *
 * 재성 중심 구조가
 * 부담 또는 강한 손상을
 * 동반한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * MIXED
 *
 * 재성 중심축의 성립 근거와
 * 부담·손상·혼잡 근거가
 * 함께 존재한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * CONTEXTUAL
 *
 * 재성이 주격으로 관여하지만
 * 구조 방향을 단순하게 확정하기 어려운
 * Context라는 뜻이다.
 *
 * =================================================
 *
 * 보조격:
 *
 * StructureSelectionCandidate의
 * 공식 타입 API를 추측하지 않는다.
 *
 * 따라서 현재는:
 *
 * - 보조격 존재 여부
 * - 보조격 이름
 *
 * 만 Provenance로 보존한다.
 *
 * =================================================
 *
 * 시간 미상:
 *
 * completeChart=false를 그대로 보존한다.
 *
 * 가짜 시주를 만들거나
 * 불확실성을 삭제하지 않는다.
 */
@Component
public class WealthPrimaryWealthStructureRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_PRIMARY_WEALTH_STRUCTURE";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.STRUCTURE
            );

    private static final Set<StructureTenGodType>
            WEALTH_PRIMARY_TYPES =
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
                "WealthPrimaryWealthStructureRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthPrimaryWealthStructureRule은 WEALTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                structureReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.STRUCTURE
                );

        if (
                structureReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                structureReference =
                structureReferenceOptional
                        .orElseThrow();

        StructureAnalysis structureAnalysis =
                structureReference
                        .requireSource(
                                StructureAnalysis.class
                        );

        /*
         * =================================================
         * 일반격 주격만 직접 판정.
         * =================================================
         *
         * 특수격을 임의로 재격으로 환원하지 않는다.
         */

        if (
                !structureAnalysis
                        .primaryIsStandard()
        ) {

            return List.of();
        }

        Optional<StructureTenGodType>
                primaryStandardTenGodOptional =
                structureAnalysis
                        .primaryStandardTenGod();

        if (
                primaryStandardTenGodOptional.isEmpty()
        ) {

            throw new IllegalStateException(
                    "primaryIsStandard=true인데 primaryStandardTenGod이 존재하지 않습니다."
            );
        }

        StructureTenGodType primaryStandardTenGod =
                primaryStandardTenGodOptional
                        .orElseThrow();

        if (
                !WEALTH_PRIMARY_TYPES.contains(
                        primaryStandardTenGod
                )
        ) {

            return List.of();
        }

        LifeDomainSignalStatus status =
                resolveStatus(
                        structureAnalysis
                );

        List<String> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 기본 Identity
         * =================================================
         */

        reasons.add(
                "5차 격국 분석에서 주격은 "
                        + structureAnalysis
                        .primaryNameKorean()
                        + "입니다."
        );

        reasons.add(
                "주격 일반격 십성 유형은 "
                        + primaryStandardTenGod.name()
                        + "입니다."
        );

        reasons.add(
                "최종 격 상태는 "
                        + structureAnalysis
                        .statusKorean()
                        + "입니다."
        );

        reasons.add(
                "최종 격 상태 설명: "
                        + structureAnalysis
                        .statusDescription()
        );

        reasons.add(
                "격국 분석 기준 일간은 "
                        + structureAnalysis
                        .dayMaster()
                        + ", 월지는 "
                        + structureAnalysis
                        .monthBranch()
                        + "입니다."
        );

        /*
         * =================================================
         * 성립
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryEstablished()
        ) {

            reasons.add(
                    "재성 주격은 성립 계열 상태로 판정되었습니다."
            );

        } else {

            reasons.add(
                    "재성 주격이 선택되어 있으나 성립 계열 상태로 확정된 것은 아닙니다."
            );
        }

        /*
         * =================================================
         * 균형 부담
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryBurdened()
        ) {

            reasons.add(
                    "재성 주격은 일간 강약 및 균형 관점에서 부담 조건을 동반합니다."
            );
        }

        /*
         * =================================================
         * 손상
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryDamaged()
        ) {

            reasons.add(
                    "재성 주격에 의미 있는 손상 Evidence가 존재합니다."
            );
        }

        if (
                structureAnalysis
                        .primaryStronglyDamaged()
        ) {

            reasons.add(
                    "재성 주격에 강한 손상 Evidence가 존재합니다."
            );
        }

        /*
         * =================================================
         * 순도 / 혼잡
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryRelativelyPure()
        ) {

            reasons.add(
                    "재성 주격은 상대적으로 순도가 확보된 구조입니다."
            );
        }

        if (
                structureAnalysis
                        .primaryHighlyMixed()
        ) {

            reasons.add(
                    "재성 주격에는 높은 혼잡도 Evidence가 존재합니다."
            );
        }

        /*
         * =================================================
         * 보조격
         * =================================================
         *
         * 이름만 Provenance로 보존한다.
         *
         * 문자열을 파싱해서
         * 십성 유형을 역추론하지 않는다.
         */

        if (
                structureAnalysis
                        .hasSecondaryStructure()
        ) {

            reasons.add(
                    "주격 외에 보조격 후보가 존재합니다."
            );

            if (
                    !structureAnalysis
                            .secondaryNamesKorean()
                            .isEmpty()
            ) {

                reasons.add(
                        "보조격 후보: "
                                + String.join(
                                ", ",
                                structureAnalysis
                                        .secondaryNamesKorean()
                        )
                );
            }
        }

        /*
         * =================================================
         * 특수격 후보 / 추가 검토
         * =================================================
         */

        if (
                structureAnalysis
                        .hasSpecialStructureCandidate()
        ) {

            reasons.add(
                    "일반격 주격과 별개로 특수격 후보가 존재합니다."
            );
        }

        if (
                structureAnalysis
                        .requiresSpecialReview()
        ) {

            reasons.add(
                    "특수구조 추가 검토가 필요한 상태이므로 현재 재성 주격을 절대적인 단일 구조로 확정하지 않습니다."
            );
        }

        /*
         * =================================================
         * 완전성
         * =================================================
         */

        if (
                !structureAnalysis
                        .completeChart()
        ) {

            reasons.add(
                    "원국이 completeChart=false이므로 시간 미상 등으로 인한 구조 불확실성을 그대로 보존합니다."
            );
        }

        /*
         * =================================================
         * 생활영역 의미
         * =================================================
         */

        reasons.add(
                "정재 또는 편재가 주격이라는 사실은 현실적 성과·자원 관리·교환·획득·결과의 현실화 기능이 원국의 중심 구조에 관여한다는 Evidence로 사용합니다."
        );

        reasons.add(
                "재성이 주격이라는 사실만으로 재물 규모·소득 수준·자산 증가·투자 성공 같은 실제 결과를 생성하지 않습니다."
        );

        reasons.add(
                "정재격을 안정적인 부자로, 편재격을 사업가나 투자형으로 자동 변환하지 않습니다."
        );

        reasons.add(
                "격국의 성립·손상·혼잡 상태는 부유함이나 가난함의 판정이 아니라 재물과 현실적 성과를 다루는 중심 구조의 안정성·부담·복합성을 설명하는 Context입니다."
        );

        reasons.add(
                "실제 재물 수용력과 재성 과다 부담, 비겁의 재성 압박 등은 별도의 WealthCapacityRule이 담당합니다."
        );

        reasons.add(
                "재성 관련 복합 Pattern의 교차검증은 별도의 WealthCrossValidatedPatternRule이 담당하며 이 Rule은 5차 주격 정보를 추가 Context로만 보존합니다."
        );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.WEALTH,
                        RULE_CODE,
                        status,
                        summaryOf(
                                structureAnalysis,
                                status
                        ),
                        List.of(
                                structureReference
                        ),
                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * STRUCTURE -> LifeDomainSignalStatus
     * =================================================
     *
     * 재물 길흉 상태가 아니다.
     *
     * 재성 주격이라는 중심 구조가
     * 어떤 상태로 존재하는지를 표현한다.
     */

    private LifeDomainSignalStatus resolveStatus(
            StructureAnalysis analysis
    ) {

        Objects.requireNonNull(
                analysis,
                "StructureAnalysis가 필요합니다."
        );

        boolean established =
                analysis.primaryEstablished();

        boolean burdened =
                analysis.primaryBurdened();

        boolean damaged =
                analysis.primaryDamaged();

        boolean stronglyDamaged =
                analysis.primaryStronglyDamaged();

        boolean highlyMixed =
                analysis.primaryHighlyMixed();

        /*
         * =================================================
         * 강한 손상
         * =================================================
         */

        if (
                stronglyDamaged
        ) {

            return established
                    ? LifeDomainSignalStatus.MIXED
                    : LifeDomainSignalStatus.PRESSURE;
        }

        /*
         * =================================================
         * 일반 손상 / 높은 혼잡
         * =================================================
         */

        if (
                damaged
                        || highlyMixed
        ) {

            return LifeDomainSignalStatus.MIXED;
        }

        /*
         * =================================================
         * 균형 부담
         * =================================================
         */

        if (
                burdened
        ) {

            return established
                    ? LifeDomainSignalStatus.MIXED
                    : LifeDomainSignalStatus.PRESSURE;
        }

        /*
         * =================================================
         * 성립
         * =================================================
         */

        if (
                established
        ) {

            return LifeDomainSignalStatus.SUPPORTIVE;
        }

        return LifeDomainSignalStatus.CONTEXTUAL;
    }

    /*
     * =================================================
     * 내부 의미 Summary
     * =================================================
     *
     * 최종 사용자용 점사 문장이 아니다.
     */

    private String summaryOf(

            StructureAnalysis structureAnalysis,

            LifeDomainSignalStatus status

    ) {

        String center =
                structureAnalysis
                        .primaryStandardTenGod()
                        .map(
                                type ->
                                        switch (
                                                type
                                                ) {

                                            case DIRECT_WEALTH ->
                                                    "현실적인 성과와 자원을 안정적으로 관리하는 기능";

                                            case INDIRECT_WEALTH ->
                                                    "여러 자원과 기회를 다루고 현실적인 결과로 연결하는 기능";

                                            default ->
                                                    "현실적 성과와 자원을 다루는 기능";
                                        }
                        )
                        .orElse(
                                "현실적 성과와 자원을 다루는 기능"
                        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 비교적 성립된 축으로 관여합니다.";

            case PRESSURE ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 관여하지만 부담 또는 손상 조건이 두드러집니다.";

            case MIXED ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 중요한 축으로 관여하면서 성립 근거와 부담·손상·혼잡 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 관여하지만 재물 방향을 단독으로 확정할 수준의 구조로 단순화하지 않습니다.";
        };
    }

    /*
     * =================================================
     * 단일 STRUCTURE Evidence
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
                    "WealthPrimaryWealthStructureRule에는 최대 하나의 "
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