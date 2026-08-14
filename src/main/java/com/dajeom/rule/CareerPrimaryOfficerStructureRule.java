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
 * 직업·사회 영역의 주격 격국 Context Rule.
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
 * - DIRECT_OFFICER
 * - SEVEN_KILLINGS
 *
 * =================================================
 *
 * 의미:
 *
 * 정관/칠살이 주격이라는 것은
 * 원국 전체 구조에서:
 *
 * - 역할
 * - 책임
 * - 규범
 * - 통제
 * - 요구
 * - 사회적 위치와 역할 수행
 *
 * 을 다루는 기능이
 * 중심축으로 관여한다는 구조 Evidence다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 정관격
 * -> 좋은 직업
 *
 * 칠살격
 * -> 힘든 직업
 *
 * 정관격
 * -> 공무원
 *
 * 칠살격
 * -> 군인/경찰
 *
 * 격 성립
 * -> 취업/승진
 *
 * 파격
 * -> 실직
 *
 * 같은 직업 사건이나 직종을
 * 직접 생성하지 않는다.
 *
 * =================================================
 *
 * 10차 Career Rule과의 차이:
 *
 * CareerOfficerStructureRule은
 * TenGodCompositeAnalysis를 통해
 * 관성 전체의 분포·강약·복합 Pattern을 본다.
 *
 * 이 Rule은 5차 StructureAnalysis에서
 * 실제 주격이 무엇으로 선택되었는지와
 * 그 주격의 성립·부담·손상·혼잡 상태를
 * 별도 STRUCTURE Evidence로 보존한다.
 *
 * 즉 서로 대체하지 않는다.
 *
 * =================================================
 *
 * 상태 의미:
 *
 * SUPPORTIVE
 *
 * 직업 성공을 뜻하지 않는다.
 *
 * 정관/칠살 주격이
 * 중심 구조로 성립되어 있다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * PRESSURE
 *
 * 직업 실패를 뜻하지 않는다.
 *
 * 중심 구조가 부담 또는 강한 손상을
 * 동반한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * MIXED
 *
 * 중심축 성립 근거와
 * 손상·부담·혼잡 근거가
 * 함께 존재한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * CONTEXTUAL
 *
 * 정관/칠살이 주격으로 관여하지만
 * 성립 방향을 단순하게 확정하기 어려운
 * 구조 Context라는 뜻이다.
 *
 * =================================================
 *
 * 보조격:
 *
 * StructureAnalysis.secondaryCandidates()의
 * 후보 타입 API를 이 Rule에서 추측하지 않는다.
 *
 * 따라서 현재는:
 *
 * - 보조격 존재 여부
 * - 보조격 이름
 *
 * 만 Reason에 보존한다.
 *
 * 향후 StructureSelectionCandidate의
 * 공식 타입 API를 이용하는 별도 Rule에서
 * 보조격 생활영역 연결을 정밀하게 처리할 수 있다.
 *
 * =================================================
 *
 * 시간 미상:
 *
 * completeChart=false를 그대로 보존한다.
 *
 * 가짜 시주를 만들거나
 * 완전한 원국으로 승격하지 않는다.
 */
@Component
public class CareerPrimaryOfficerStructureRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_PRIMARY_OFFICER_STRUCTURE";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.STRUCTURE
            );

    private static final Set<StructureTenGodType>
            CAREER_PRIMARY_TYPES =
            Set.of(
                    StructureTenGodType.DIRECT_OFFICER,
                    StructureTenGodType.SEVEN_KILLINGS
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
                "CareerPrimaryOfficerStructureRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerPrimaryOfficerStructureRule은 CAREER Bundle만 평가할 수 있습니다."
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
         * 일반격 주격만 현재 직접 판정.
         * =================================================
         *
         * 특수격을 정관/칠살 등 일반격으로
         * 억지 변환하지 않는다.
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
                !CAREER_PRIMARY_TYPES.contains(
                        primaryStandardTenGod
                )
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 구조 상태
         * =================================================
         */

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
         * 성립 상태
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryEstablished()
        ) {

            reasons.add(
                    "주격은 성립 계열 상태로 판정되었습니다."
            );
        } else {

            reasons.add(
                    "주격이 선택되어 있으나 성립 계열 상태로 확정된 것은 아닙니다."
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
                    "주격은 일간 강약 및 균형 관점에서 부담 조건을 동반합니다."
            );
        }

        /*
         * =================================================
         * 파격 / 손상
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryDamaged()
        ) {

            reasons.add(
                    "주격에 의미 있는 손상 Evidence가 존재합니다."
            );
        }

        if (
                structureAnalysis
                        .primaryStronglyDamaged()
        ) {

            reasons.add(
                    "주격에 강한 손상 Evidence가 존재합니다."
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
                    "주격은 상대적으로 순도가 확보된 구조입니다."
            );
        }

        if (
                structureAnalysis
                        .primaryHighlyMixed()
        ) {

            reasons.add(
                    "주격에는 높은 혼잡도 Evidence가 존재합니다."
            );
        }

        /*
         * =================================================
         * 보조격
         * =================================================
         *
         * 후보 이름은 Provenance로만 보존한다.
         *
         * 문자열을 해석해서
         * 십성 종류를 역추론하지 않는다.
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
         * 특수격 후보 / 검토
         * =================================================
         *
         * 현재 주격이 일반격이라도
         * 특수 후보가 남아 있을 수 있다.
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
                    "특수구조 추가 검토가 필요한 상태이므로 현재 일반격 주격을 절대적인 단일 구조로 확정하지 않습니다."
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
                "정관 또는 칠살이 주격이라는 사실은 역할·책임·규범·요구·사회적 역할 수행 기능이 원국의 중심 구조에 관여한다는 Evidence로 사용합니다."
        );

        reasons.add(
                "이 Evidence는 특정 직업·직종·직위·취업·승진·퇴사 같은 실제 사건을 직접 생성하지 않습니다."
        );

        reasons.add(
                "정관격을 좋은 직업으로, 칠살격을 나쁜 직업으로 단순 변환하지 않습니다."
        );

        reasons.add(
                "격국의 성립·손상·혼잡 상태는 직업 성공 또는 실패 확률이 아니라 중심 역할 구조의 안정성·부담·복합성을 설명하기 위한 Context입니다."
        );

        reasons.add(
                "십성 전체 분포와 관성 복합구조는 별도의 CareerOfficerStructureRule 및 CareerCrossValidatedPatternRule이 담당하며 이 Rule은 5차 주격 정보를 중복 계산하지 않고 추가 Context로만 보존합니다."
        );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
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
     * 이 상태는 직업 길흉이 아니다.
     *
     * "정관/칠살 주격이라는 직업 관련 중심 구조가
     * 어떤 상태로 성립하고 있는가"
     *
     * 만 표현한다.
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
         *
         * 성립 근거와 강한 손상이 동시에 있다면
         * 한쪽을 삭제하지 않고 MIXED.
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
         *
         * 주격은 성립하지만 부담까지 있으면
         * SUPPORTIVE로 평탄화하지 않는다.
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
         *
         * SUPPORTIVE는
         * 직업적으로 좋다는 뜻이 아니라
         * 해당 중심 구조의 성립 근거가 있다는 뜻.
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

                                            case DIRECT_OFFICER ->
                                                    "규범·책임·정돈된 역할 수행";

                                            case SEVEN_KILLINGS ->
                                                    "압박·요구·긴장 속에서 역할을 수행하는 기능";

                                            default ->
                                                    "사회적 역할 수행";
                                        }
                        )
                        .orElse(
                                "사회적 역할 수행"
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
                            + "이 관여하지만 직업·사회 방향을 단독으로 확정할 수준의 구조로 단순화하지 않습니다.";
        };
    }

    /*
     * =================================================
     * 단일 STRUCTURE Evidence
     * =================================================
     *
     * 원국 Source이므로 정상 Bundle에서는 최대 하나.
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
                    "CareerPrimaryOfficerStructureRule에는 최대 하나의 "
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