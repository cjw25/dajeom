package com.dajeom.rule;

import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.AnnualLuckContextSignal;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 학습·성장 영역의 세운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 14-6 AnnualLuckContextAnalysis
 *
 * =================================================
 *
 * 학습·성장 영역에서 참조하는 세운 십성:
 *
 * DIRECT_RESOURCE
 * INDIRECT_RESOURCE
 * EATING_GOD
 * HURTING_OFFICER
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * AnnualLuckContextSignal은
 * 14-6에서 이미 계산된
 * deterministic Evidence 단위다.
 *
 * 따라서 이 Rule에서는:
 *
 * AnnualLuckContextSignal 1개
 * ->
 * LifeDomainRuleMatch 1개
 *
 * 로 보존한다.
 *
 * =================================================
 *
 * 서로 다른 Annual Signal을:
 *
 * - 합산하지 않는다.
 * - 개수로 방향을 결정하지 않는다.
 * - 하나의 대표 상태로 축약하지 않는다.
 * - 정인과 편인을 합쳐 새 인성 상태를 만들지 않는다.
 * - 식신과 상관을 합쳐 새 식상 상태를 만들지 않는다.
 * - 인성과 식상을 결합하지 않는다.
 *
 * 단순 공존만으로:
 *
 * - 새로운 관계
 * - 새로운 인과
 * - 새로운 순서
 * - 강화
 * - 약화
 * - 상쇄
 * - 복합 상태
 * - 추가 종합 의미
 *
 * 를 생성하지 않는다.
 *
 * =================================================
 *
 * 동일 Annual Signal이
 * 둘 이상의 StructureTenGodType 조회 결과에
 * 포함될 수 있는 경우:
 *
 * Object Identity 기준으로 Match는 하나만 생성한다.
 *
 * 대신 해당 Signal이 어떤 십성 조회에
 * 실제 포함되었는지는 모두 근거에 보존한다.
 *
 * =================================================
 *
 * 십성 의미:
 *
 * DIRECT_RESOURCE / INDIRECT_RESOURCE
 *
 * =
 * 학습·흡수·정리·내재화와 관련하여
 * 참조 가능한 Context.
 *
 * EATING_GOD / HURTING_OFFICER
 *
 * =
 * 표현·산출·응용·외부 발현과 관련하여
 * 참조 가능한 Context.
 *
 * 위 의미 역시 서로 다른 Signal을
 * 합쳐 새로운 결과를 만드는 데 사용하지 않는다.
 *
 * =================================================
 *
 * 14차에서 이미 계산된:
 *
 * - 세운 십성
 * - 원국 × 세운
 * - 격국
 * - 용신
 * - 궁위
 * - 관계
 * - 합화
 * - 가능한 경우 대운 Segment Context
 *
 * 를 18차에서 다시 계산하지 않는다.
 *
 * 실제 관계 계산이 AnnualLuckContextSignal 내부에
 * 이미 포함되어 있다면 해당 계산 결과는 유지한다.
 *
 * 이 Rule이 다른 독립 Signal과 연결하여
 * 추가 관계 의미를 생성하지 않을 뿐이다.
 *
 * =================================================
 *
 * 단일 Signal 상태 변환:
 *
 * SUPPORTED
 * -> SUPPORTIVE
 *
 * QUALIFIED
 * -> CONTEXTUAL
 *
 * WEAKENED
 * -> PRESSURE
 *
 * CONFLICTED
 * -> MIXED
 *
 * 여러 Signal의 상태를 합쳐
 * 새로운 MIXED 등을 만들지 않는다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 인성 세운
 * -> 시험 합격
 *
 * 식상 세운
 * -> 성과 발생
 *
 * 인성 지원
 * -> 자격증 취득
 *
 * 식상 약화
 * -> 학업 실패
 *
 * 같은 실제 사건 결과 확정.
 *
 * =================================================
 *
 * Natal Bundle에는 ANNUAL_CONTEXT가 없으므로
 * 아무 Match도 생성하지 않는다.
 *
 * Timeline Segment Bundle에서만 작동한다.
 */
@Component
public class LearningAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
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
                "LearningAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningAnnualContextRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                annualReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .ANNUAL_CONTEXT
                );

        /*
         * =================================================
         * Natal Bundle
         * =================================================
         */

        if (
                annualReferenceOptional
                        .isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                annualReference =
                annualReferenceOptional
                        .orElseThrow();

        AnnualLuckContextAnalysis annualContext =
                annualReference
                        .requireSource(
                                AnnualLuckContextAnalysis.class
                        );

        /*
         * =================================================
         * 학습·성장 관련 Annual Signal 조회
         * =================================================
         */

        List<AnnualLuckContextSignal>
                directResourceSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_RESOURCE
                        );

        List<AnnualLuckContextSignal>
                indirectResourceSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_RESOURCE
                        );

        List<AnnualLuckContextSignal>
                eatingGodSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .EATING_GOD
                        );

        List<AnnualLuckContextSignal>
                hurtingOfficerSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .HURTING_OFFICER
                        );

        /*
         * =================================================
         * Identity 중복 제거
         * =================================================
         *
         * 이것은 상태 합성이 아니다.
         *
         * 동일 객체가 여러 십성 조회 결과에
         * 반복 포함되는 경우
         * 같은 upstream Signal을 중복 Match로
         * 생성하지 않기 위한 Identity 정규화다.
         */

        List<AnnualLuckContextSignal>
                learningSignals =
                mergeIdentityUnique(
                        directResourceSignals,
                        indirectResourceSignals,
                        eatingGodSignals,
                        hurtingOfficerSignals
                );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * Annual Signal 1개 -> Match 1개
         * =================================================
         */

        for (
                AnnualLuckContextSignal signal
                : learningSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "AnnualLuckContextAnalysis의 Annual Signal은 null일 수 없습니다."
            );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            signal
                    );

            List<StructureTenGodType>
                    matchedTenGodTypes =
                    matchedTenGodTypes(
                            signal,
                            directResourceSignals,
                            indirectResourceSignals,
                            eatingGodSignals,
                            hurtingOfficerSignals
                    );

            if (
                    matchedTenGodTypes.isEmpty()
            ) {

                throw new IllegalStateException(
                        "학습·성장 Annual Signal의 StructureTenGodType 조회 출처를 확인할 수 없습니다."
                );
            }

            List<String> reasons =
                    baseReasons(
                            annualContext
                    );

            reasons.add(
                    "이 Annual Signal이 실제 포함된 학습·성장 십성 조회 유형은 "
                            + matchedTenGodTypes
                            + "입니다."
            );

            reasons.add(
                    "이 Annual Signal의 원래 상태는 "
                            + signal.status().name()
                            + "입니다."
            );

            reasons.add(
                    "이 Match는 하나의 AnnualLuckContextSignal만 LifeDomain 상태로 변환합니다."
            );

            reasons.add(
                    "다른 Annual Signal의 상태·개수·십성 유형과 합산하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 생성하지 않습니다."
            );

            if (
                    containsResourceType(
                            matchedTenGodTypes
                    )
            ) {

                reasons.add(
                        "이 Signal은 인성 계열 조회에 포함되므로 학습·흡수·정리·내재화 측면에서 참조할 수 있습니다."
                );
            }

            if (
                    containsOutputType(
                            matchedTenGodTypes
                    )
            ) {

                reasons.add(
                        "이 Signal은 식상 계열 조회에 포함되므로 표현·산출·응용·외부 발현 측면에서 참조할 수 있습니다."
                );
            }

            reasons.add(
                    "인성 또는 식상 관련 세운 Context를 시험 합격·자격 취득·창작 성공·업무 성과 같은 실제 결과로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            status,
                            summaryOf(
                                    matchedTenGodTypes,
                                    status
                            ),
                            List.of(
                                    annualReference
                            ),
                            List.copyOf(
                                    reasons
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
     * 공통 세운 Reason
     * =================================================
     */

    private List<String> baseReasons(
            AnnualLuckContextAnalysis annualContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "14-6 AnnualLuckContextAnalysis의 현재 세운 연도는 "
                        + annualContext.targetYear()
                        + "년입니다."
        );

        reasons.add(
                "14차가 이미 계산한 십성·원국 관계·격국·용신·궁위·합화 Context를 18차에서 다시 계산하지 않습니다."
        );

        if (
                !annualContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "대운 Segment Context를 사용할 수 없는 상태이지만 14차 정책에 따라 세운 자체 annualSignals는 그대로 보존하여 사용합니다."
            );
        }

        if (
                annualContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 세운 기간에 대운 교운시점 불확실성이 존재하므로 가능한 대운 Context 중 하나를 임의로 확정하지 않습니다."
            );
        }

        return reasons;
    }

    /*
     * =================================================
     * 단일 Annual Signal 상태 변환
     * =================================================
     */

    private LifeDomainSignalStatus resolveStatus(
            AnnualLuckContextSignal signal
    ) {

        Objects.requireNonNull(
                signal,
                "AnnualLuckContextSignal이 필요합니다."
        );

        if (
                signal
                        .status()
                        .conflicted()
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                signal
                        .status()
                        .weakened()
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        if (
                signal
                        .status()
                        .supported()
        ) {

            return LifeDomainSignalStatus
                    .SUPPORTIVE;
        }

        if (
                signal
                        .status()
                        .qualified()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        throw new IllegalStateException(
                "지원하지 않는 AnnualLuckContextSignal 상태입니다: "
                        + signal.status()
        );
    }

    /*
     * =================================================
     * Signal의 실제 십성 조회 출처
     * =================================================
     */

    private List<StructureTenGodType>
    matchedTenGodTypes(

            AnnualLuckContextSignal signal,

            List<AnnualLuckContextSignal>
                    directResourceSignals,

            List<AnnualLuckContextSignal>
                    indirectResourceSignals,

            List<AnnualLuckContextSignal>
                    eatingGodSignals,

            List<AnnualLuckContextSignal>
                    hurtingOfficerSignals

    ) {

        List<StructureTenGodType> types =
                new ArrayList<>();

        if (
                containsIdentity(
                        directResourceSignals,
                        signal
                )
        ) {

            types.add(
                    StructureTenGodType
                            .DIRECT_RESOURCE
            );
        }

        if (
                containsIdentity(
                        indirectResourceSignals,
                        signal
                )
        ) {

            types.add(
                    StructureTenGodType
                            .INDIRECT_RESOURCE
            );
        }

        if (
                containsIdentity(
                        eatingGodSignals,
                        signal
                )
        ) {

            types.add(
                    StructureTenGodType
                            .EATING_GOD
            );
        }

        if (
                containsIdentity(
                        hurtingOfficerSignals,
                        signal
                )
        ) {

            types.add(
                    StructureTenGodType
                            .HURTING_OFFICER
            );
        }

        return List.copyOf(
                types
        );
    }

    private boolean containsIdentity(

            List<AnnualLuckContextSignal> signals,

            AnnualLuckContextSignal target

    ) {

        for (
                AnnualLuckContextSignal signal
                : signals
        ) {

            if (
                    signal == target
            ) {

                return true;
            }
        }

        return false;
    }

    /*
     * =================================================
     * 십성 Family 확인
     * =================================================
     */

    private boolean containsResourceType(
            List<StructureTenGodType> types
    ) {

        return types.contains(
                StructureTenGodType
                        .DIRECT_RESOURCE
        ) || types.contains(
                StructureTenGodType
                        .INDIRECT_RESOURCE
        );
    }

    private boolean containsOutputType(
            List<StructureTenGodType> types
    ) {

        return types.contains(
                StructureTenGodType
                        .EATING_GOD
        ) || types.contains(
                StructureTenGodType
                        .HURTING_OFFICER
        );
    }

    /*
     * =================================================
     * Summary
     * =================================================
     *
     * 여러 Signal의 의미를 합치지 않는다.
     *
     * 현재 하나의 Signal이 어떤
     * StructureTenGodType 조회에 포함되었고
     * 어떤 상태인지까지만 보존한다.
     */

    private String summaryOf(

            List<StructureTenGodType> matchedTenGodTypes,

            LifeDomainSignalStatus status

    ) {

        return "현재 세운의 학습·성장 관련 Annual Context Signal "
                + matchedTenGodTypes
                + "을 독립적으로 보존합니다. 상태="
                + status.name()
                + "입니다.";
    }

    /*
     * =================================================
     * Object Identity 중복 제거
     * =================================================
     *
     * 서로 다른 Signal을 합성하기 위한 것이 아니다.
     *
     * 동일 upstream Signal 객체의
     * 중복 Match 생성을 방지하기 위한 용도다.
     */

    @SafeVarargs
    private final List<AnnualLuckContextSignal>
    mergeIdentityUnique(
            List<AnnualLuckContextSignal>... signalLists
    ) {

        Set<AnnualLuckContextSignal> unique =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        List<AnnualLuckContextSignal> ordered =
                new ArrayList<>();

        for (
                List<AnnualLuckContextSignal> signals
                : signalLists
        ) {

            if (
                    signals == null
            ) {

                continue;
            }

            for (
                    AnnualLuckContextSignal signal
                    : signals
            ) {

                if (
                        unique.add(
                                signal
                        )
                ) {

                    ordered.add(
                            signal
                    );
                }
            }
        }

        return List.copyOf(
                ordered
        );
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
     *
     * Natal Bundle
     * -> 0개 정상.
     *
     * Segment Bundle
     * -> 1개 정상.
     *
     * 2개 이상
     * -> Evidence 구성 오류.
     */

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        List<LifeDomainEvidenceReference<?>>
                evidences =
                bundle
                        .evidencesOf(
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
                    "LearningAnnualContextRule에는 최대 하나의 "
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