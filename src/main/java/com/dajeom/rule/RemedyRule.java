package com.dajeom.rule;

import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.PathologyAnalysis;
import com.dajeom.model.PathologyEvidence;
import com.dajeom.model.RemedyPathologyLink;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureDamageSignal;
import com.dajeom.type.ClimateCorrectionTarget;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.RemedyActionType;
import com.dajeom.type.RemedyCandidateRole;
import com.dajeom.type.StructureDamagePatternType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-5.
 *
 * 병(病) 구조를 약(藥) 오행 후보로
 * 변환하는 다점 내부 Rule.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 병 하나 = 약 하나
 *    로 단순 매핑하지 않는다.
 *
 * 2.
 * 한 오행이 여러 병을
 *    동시에 완화할 수 있다.
 *
 * 3.
 * 병을 하나 완화하면서
 *    다른 병을 악화할 수도 있다.
 *
 * 4.
 * 신약/신강 방향과 충돌하면
 *    CONDITIONAL로 남긴다.
 *
 * 5.
 * 조후 방향과 충돌해도
 *    CONDITIONAL로 남긴다.
 *
 * 6.
 * 현재 이미 매우 많은 오행을
 *    무조건 더 쓰지 않는다.
 *
 * 7.
 * 여기서 최종 용신을 결정하지 않는다.
 */
@Component
public class RemedyRule {

    /**
     * 하나의 오행 후보가
     * 현재 병 구조들을 어떤 방식으로
     * 완화할 수 있는지 찾는다.
     */
    public List<RemedyPathologyLink>
    findRemedyLinks(

            FiveElement candidateElement,

            DayMasterElementRole candidateRole,

            PathologyAnalysis pathology,

            StructureAnalysis structure,

            ClimateElementCandidate climateCandidate

    ) {

        Objects.requireNonNull(
                candidateElement,
                "약 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                candidateRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                structure,
                "격국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                climateCandidate,
                "조후 오행 후보가 필요합니다."
        );

        List<RemedyPathologyLink> links =
                new ArrayList<>();

        for (
                PathologyEvidence evidence
                : pathology.evidences()
        ) {

            switch (
                    evidence.patternType()
            ) {

                /*
                 * ========================================
                 * 특정 오행 과다
                 * ========================================
                 */
                case ELEMENT_OVERCONCENTRATION ->
                        addElementConcentrationLinks(
                                links,
                                candidateElement,
                                evidence
                        );

                /*
                 * ========================================
                 * 식상 설기 과다
                 * ========================================
                 */
                case OUTPUT_DRAIN_OVERLOAD ->
                        addOutputDrainLinks(
                                links,
                                candidateRole,
                                evidence
                        );

                /*
                 * ========================================
                 * 재성 부담 과다
                 * ========================================
                 */
                case WEALTH_EXERTION_OVERLOAD ->
                        addWealthPressureLinks(
                                links,
                                candidateRole,
                                evidence
                        );

                /*
                 * ========================================
                 * 관살 압박 과다
                 * ========================================
                 */
                case OFFICER_CONTROL_OVERLOAD ->
                        addOfficerPressureLinks(
                                links,
                                candidateRole,
                                evidence
                        );

                /*
                 * ========================================
                 * 비겁·인성 생조 과다
                 * ========================================
                 */
                case SUPPORT_OVERCONCENTRATION ->
                        addSupportOverconcentrationLinks(
                                links,
                                candidateRole,
                                evidence
                        );

                /*
                 * ========================================
                 * 조후 병
                 * ========================================
                 */
                case EXTREME_COLD ->
                        addClimateLink(
                                links,
                                climateCandidate,
                                evidence,
                                ClimateCorrectionTarget.WARMING,
                                RemedyActionType.WARM_CLIMATE
                        );

                case EXTREME_HEAT ->
                        addClimateLink(
                                links,
                                climateCandidate,
                                evidence,
                                ClimateCorrectionTarget.COOLING,
                                RemedyActionType.COOL_CLIMATE
                        );

                case EXTREME_DRY ->
                        addClimateLink(
                                links,
                                climateCandidate,
                                evidence,
                                ClimateCorrectionTarget.MOISTENING,
                                RemedyActionType.MOISTEN_CLIMATE
                        );

                case EXTREME_WET ->
                        addClimateLink(
                                links,
                                climateCandidate,
                                evidence,
                                ClimateCorrectionTarget.DRYING,
                                RemedyActionType.DRY_CLIMATE
                        );

                case CLIMATE_CORRECTION_CONFLICT ->
                        addClimateBridgeLink(
                                links,
                                climateCandidate,
                                evidence
                        );

                /*
                 * ========================================
                 * 주격 파격
                 * ========================================
                 */
                case PRIMARY_STRUCTURE_DAMAGE ->
                        addStructureDamageLinks(
                                links,
                                candidateRole,
                                evidence,
                                structure
                        );

                /*
                 * ========================================
                 * 주격 불안정
                 * ========================================
                 *
                 * 불안정은 여러 원인의 최종 결과이므로
                 * 이 단계에서 하나의 오행 약으로
                 * 억지로 환원하지 않는다.
                 */
                case PRIMARY_STRUCTURE_INSTABILITY -> {
                    // 6-8 / 6-9에서 종합 검토.
                }
            }
        }

        return List.copyOf(
                links
        );
    }

    /**
     * 현재 후보가
     * 이미 존재하는 병 구조 가운데
     * 무엇을 더 악화시킬 가능성이 있는지.
     */
    public List<PathologyPatternType>
    findAggravatedPathologies(

            FiveElement candidateElement,

            DayMasterElementRole candidateRole,

            PathologyAnalysis pathology,

            StructureAnalysis structure,

            ClimateElementCandidate climateCandidate

    ) {

        Objects.requireNonNull(
                candidateElement,
                "약 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                candidateRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                structure,
                "격국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                climateCandidate,
                "조후 후보가 필요합니다."
        );

        List<PathologyPatternType> result =
                new ArrayList<>();

        for (
                PathologyEvidence evidence
                : pathology.evidences()
        ) {

            boolean aggravates =
                    switch (
                            evidence.patternType()
                            ) {

                        case ELEMENT_OVERCONCENTRATION ->
                                evidence
                                        .relatedElements()
                                        .contains(
                                                candidateElement
                                        );

                        case OUTPUT_DRAIN_OVERLOAD ->
                                candidateRole
                                        == DayMasterElementRole.OUTPUT;

                        case WEALTH_EXERTION_OVERLOAD ->
                                candidateRole
                                        == DayMasterElementRole.WEALTH;

                        case OFFICER_CONTROL_OVERLOAD ->
                                candidateRole
                                        == DayMasterElementRole.OFFICER;

                        case SUPPORT_OVERCONCENTRATION ->
                                candidateRole
                                        == DayMasterElementRole.COMPANION

                                        || candidateRole
                                        == DayMasterElementRole.RESOURCE;

                        case EXTREME_COLD ->
                                candidateElement
                                        == FiveElement.WATER;

                        case EXTREME_HEAT ->
                                candidateElement
                                        == FiveElement.FIRE;

                        case EXTREME_DRY ->
                                candidateElement
                                        == FiveElement.FIRE;

                        case EXTREME_WET ->
                                candidateElement
                                        == FiveElement.WATER;

                        case CLIMATE_CORRECTION_CONFLICT ->
                                climateCandidate
                                        .hasConflict();

                        case PRIMARY_STRUCTURE_DAMAGE ->
                                aggravatesPrimaryStructureDamage(
                                        candidateRole,
                                        structure
                                );

                        case PRIMARY_STRUCTURE_INSTABILITY ->
                                false;
                    };

            if (
                    aggravates
                            && !result.contains(
                            evidence.patternType()
                    )
            ) {

                result.add(
                        evidence.patternType()
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 현재 일간 강약 방향과
     * 후보 오행이 충돌하는가.
     *
     * 신약:
     *
     * 식상·재성·관살은 기본적으로
     * 추가 부담 방향.
     *
     * 신강:
     *
     * 비겁·인성은 기본적으로
     * 추가 생조 방향.
     *
     * 중화권은 충돌 없음.
     */
    public boolean hasStrengthDirectionConflict(

            DayMasterStrengthLevel strengthLevel,

            DayMasterElementRole candidateRole

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                candidateRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        if (
                weakSide(
                        strengthLevel
                )
        ) {

            return candidateRole
                    == DayMasterElementRole.OUTPUT

                    || candidateRole
                    == DayMasterElementRole.WEALTH

                    || candidateRole
                    == DayMasterElementRole.OFFICER;
        }

        if (
                strongSide(
                        strengthLevel
                )
        ) {

            return candidateRole
                    == DayMasterElementRole.COMPANION

                    || candidateRole
                    == DayMasterElementRole.RESOURCE;
        }

        return false;
    }

    /**
     * 최종 6-5 후보 역할.
     */
    public RemedyCandidateRole determineRole(

            List<RemedyPathologyLink> links,

            List<PathologyPatternType> aggravatedPathologies,

            PathologyAnalysis pathology,

            ClimateElementPresenceLevel presenceLevel,

            boolean strengthDirectionConflict,

            boolean climateConflict

    ) {

        Objects.requireNonNull(
                links,
                "약 작용 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                aggravatedPathologies,
                "악화 가능 병 구조 목록이 필요합니다."
        );

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 보유 수준이 필요합니다."
        );

        if (
                links.isEmpty()
        ) {

            return RemedyCandidateRole
                    .REJECTED;
        }

        /*
         * ================================================
         * 다른 구조와 직접 충돌
         * ================================================
         */
        if (
                strengthDirectionConflict
                        || climateConflict
        ) {

            return RemedyCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 현재 존재하는 다른 병을
         * 의미 있게 악화할 가능성
         * ================================================
         */
        boolean meaningfulAggravation =
                aggravatedPathologies
                        .stream()
                        .anyMatch(
                                pattern ->
                                        severityOf(
                                                pathology,
                                                pattern
                                        )
                                                .meaningful()
                        );

        if (
                meaningfulAggravation
        ) {

            return RemedyCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 이미 지나치게 많은 오행
         * ================================================
         */
        if (
                presenceLevel.saturated()
        ) {

            return RemedyCandidateRole
                    .CONDITIONAL;
        }

        boolean direct =
                links
                        .stream()
                        .anyMatch(
                                RemedyPathologyLink
                                        ::direct
                        );

        PathologySeverity highestAddressedSeverity =
                links
                        .stream()
                        .map(
                                RemedyPathologyLink
                                        ::pathologySeverity
                        )
                        .max(
                                java.util.Comparator.comparingInt(
                                        PathologySeverity
                                                ::rank
                                )
                        )
                        .orElse(
                                PathologySeverity.NONE
                        );

        int meaningfulPathologyCount =
                (int) links
                        .stream()
                        .filter(
                                RemedyPathologyLink
                                        ::meaningfulPathology
                        )
                        .map(
                                RemedyPathologyLink
                                        ::pathologyPatternType
                        )
                        .distinct()
                        .count();

        /*
         * ================================================
         * 가장 강한 병을 직접 완화
         * ================================================
         */
        if (
                direct

                        && highestAddressedSeverity.rank()
                        >= pathology
                        .overallSeverity()
                        .rank()
        ) {

            if (
                    presenceLevel.high()
            ) {

                return RemedyCandidateRole
                        .SECONDARY;
            }

            return RemedyCandidateRole
                    .PRIMARY;
        }

        /*
         * ================================================
         * 서로 다른 의미 있는 병을
         * 둘 이상 동시에 완화
         * ================================================
         */
        if (
                direct
                        && meaningfulPathologyCount >= 2
        ) {

            if (
                    presenceLevel.high()
            ) {

                return RemedyCandidateRole
                        .SECONDARY;
            }

            return RemedyCandidateRole
                    .PRIMARY;
        }

        return RemedyCandidateRole
                .SECONDARY;
    }

    /**
     * 오행과 일간 사이의
     * 큰 십성 그룹을 계산한다.
     *
     * 기존 Phase4 객체의 구현 세부 API에
     * 의존하지 않도록 FiveElement 관계로
     * 직접 판정한다.
     */
    public DayMasterElementRole determineDayMasterRole(

            FiveElement dayMasterElement,

            FiveElement targetElement

    ) {

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "대상 오행이 필요합니다."
        );

        if (
                dayMasterElement == targetElement
        ) {

            return DayMasterElementRole
                    .COMPANION;
        }

        if (
                produces(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterElementRole
                    .RESOURCE;
        }

        if (
                produces(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterElementRole
                    .OUTPUT;
        }

        if (
                controls(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterElementRole
                    .WEALTH;
        }

        if (
                controls(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterElementRole
                    .OFFICER;
        }

        throw new IllegalStateException(
                "일간과 대상 오행의 관계를 결정할 수 없습니다."
        );
    }

    private void addElementConcentrationLinks(

            List<RemedyPathologyLink> links,

            FiveElement candidateElement,

            PathologyEvidence evidence

    ) {

        if (
                evidence.relatedElements()
                        .isEmpty()
        ) {

            return;
        }

        FiveElement excessElement =
                evidence
                        .relatedElements()
                        .get(
                                0
                        );

        /*
         * 과다 오행이 생하는 오행.
         *
         * 과다 세력을 밖으로 배출하는
         * 설기 방향.
         */
        FiveElement drainElement =
                producedElementOf(
                        excessElement
                );

        if (
                candidateElement == drainElement
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            excessElement,
                            RemedyActionType
                                    .DRAIN_EXCESS_ELEMENT,
                            true,
                            null
                    )
            );
        }

        /*
         * 과다 오행을 직접 극하는 오행.
         */
        FiveElement controller =
                controllerOf(
                        excessElement
                );

        if (
                candidateElement == controller
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            excessElement,
                            RemedyActionType
                                    .CONTROL_EXCESS_ELEMENT,
                            true,
                            null
                    )
            );
        }
    }

    private void addOutputDrainLinks(

            List<RemedyPathologyLink> links,

            DayMasterElementRole candidateRole,

            PathologyEvidence evidence

    ) {

        if (
                candidateRole
                        == DayMasterElementRole.RESOURCE
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            firstRelatedElement(
                                    evidence
                            ),
                            RemedyActionType
                                    .CONTROL_OUTPUT_DRAIN,
                            true,
                            null
                    )
            );

            return;
        }

        if (
                candidateRole
                        == DayMasterElementRole.COMPANION
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            firstRelatedElement(
                                    evidence
                            ),
                            RemedyActionType
                                    .STRENGTHEN_DAY_MASTER,
                            false,
                            null
                    )
            );
        }
    }

    private void addWealthPressureLinks(

            List<RemedyPathologyLink> links,

            DayMasterElementRole candidateRole,

            PathologyEvidence evidence

    ) {

        if (
                candidateRole
                        == DayMasterElementRole.COMPANION
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            firstRelatedElement(
                                    evidence
                            ),
                            RemedyActionType
                                    .SUPPORT_WEALTH_BEARING_CAPACITY,
                            true,
                            null
                    )
            );

            return;
        }

        if (
                candidateRole
                        == DayMasterElementRole.RESOURCE
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            firstRelatedElement(
                                    evidence
                            ),
                            RemedyActionType
                                    .STRENGTHEN_DAY_MASTER,
                            false,
                            null
                    )
            );
        }
    }

    private void addOfficerPressureLinks(

            List<RemedyPathologyLink> links,

            DayMasterElementRole candidateRole,

            PathologyEvidence evidence

    ) {

        if (
                candidateRole
                        == DayMasterElementRole.RESOURCE
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            firstRelatedElement(
                                    evidence
                            ),
                            RemedyActionType
                                    .TRANSFORM_OFFICER_PRESSURE,
                            true,
                            null
                    )
            );

            return;
        }

        if (
                candidateRole
                        == DayMasterElementRole.COMPANION
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            firstRelatedElement(
                                    evidence
                            ),
                            RemedyActionType
                                    .STRENGTHEN_DAY_MASTER,
                            false,
                            null
                    )
            );

            return;
        }

        /*
         * 식상제살/제관 방향.
         *
         * 다만 신약 일간에서는
         * 식상이 다시 일간을 설기하므로
         * strengthDirectionConflict에 의해
         * CONDITIONAL이 될 수 있다.
         */
        if (
                candidateRole
                        == DayMasterElementRole.OUTPUT
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            firstRelatedElement(
                                    evidence
                            ),
                            RemedyActionType
                                    .CONTROL_OFFICER_PRESSURE,
                            true,
                            null
                    )
            );
        }
    }

    private void addSupportOverconcentrationLinks(

            List<RemedyPathologyLink> links,

            DayMasterElementRole candidateRole,

            PathologyEvidence evidence

    ) {

        if (
                candidateRole
                        == DayMasterElementRole.OUTPUT
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            null,
                            RemedyActionType
                                    .RELEASE_EXCESS_SUPPORT,
                            true,
                            null
                    )
            );

            return;
        }

        if (
                candidateRole
                        == DayMasterElementRole.WEALTH

                        || candidateRole
                        == DayMasterElementRole.OFFICER
        ) {

            links.add(
                    new RemedyPathologyLink(
                            evidence.patternType(),
                            evidence.severity(),
                            null,
                            RemedyActionType
                                    .RELEASE_EXCESS_SUPPORT,
                            false,
                            null
                    )
            );
        }
    }

    private void addClimateLink(

            List<RemedyPathologyLink> links,

            ClimateElementCandidate climateCandidate,

            PathologyEvidence evidence,

            ClimateCorrectionTarget target,

            RemedyActionType actionType

    ) {

        if (
                !climateCandidate
                        .addresses(
                                target
                        )
        ) {

            return;
        }

        links.add(
                new RemedyPathologyLink(
                        evidence.patternType(),
                        evidence.severity(),
                        null,
                        actionType,
                        true,
                        null
                )
        );
    }

    private void addClimateBridgeLink(

            List<RemedyPathologyLink> links,

            ClimateElementCandidate climateCandidate,

            PathologyEvidence evidence

    ) {

        if (
                !climateCandidate
                        .bridgeCandidate()
        ) {

            return;
        }

        links.add(
                new RemedyPathologyLink(
                        evidence.patternType(),
                        evidence.severity(),
                        null,
                        RemedyActionType
                                .BRIDGE_CLIMATE_CONFLICT,
                        false,
                        null
                )
        );
    }

    private void addStructureDamageLinks(

            List<RemedyPathologyLink> links,

            DayMasterElementRole candidateRole,

            PathologyEvidence pathologyEvidence,

            StructureAnalysis structure

    ) {

        if (
                !structure.primaryIsStandard()
        ) {

            return;
        }

        List<StructureDamageSignal> damageSignals =
                structure
                        .primaryCandidate()
                        .standardEvidence()
                        .damageEvidence()
                        .damageSignals();

        for (
                StructureDamageSignal signal
                : damageSignals
        ) {

            if (
                    !signal.meaningful()
            ) {

                continue;
            }

            StructureDamagePatternType pattern =
                    signal.patternType();

            StructureDamageRemedyMapping mapping =
                    structureDamageRemedyMapping(
                            pattern,
                            candidateRole
                    );

            if (
                    mapping == null
            ) {

                continue;
            }

            links.add(
                    new RemedyPathologyLink(
                            pathologyEvidence
                                    .patternType(),
                            pathologyEvidence
                                    .severity(),
                            null,
                            RemedyActionType
                                    .REPAIR_STRUCTURE_DAMAGE,
                            mapping.direct(),
                            pattern
                    )
            );
        }
    }

    private StructureDamageRemedyMapping
    structureDamageRemedyMapping(

            StructureDamagePatternType pattern,

            DayMasterElementRole candidateRole

    ) {

        return switch (
                pattern
                ) {

            /*
             * 비겁탈재
             *
             * 관살로 비겁을 제어.
             * 식상으로 재성 흐름을 보조.
             */
            case WEALTH_CONTESTED_BY_COMPANION -> {

                if (
                        candidateRole
                                == DayMasterElementRole.OFFICER
                ) {

                    yield new StructureDamageRemedyMapping(
                            true
                    );
                }

                if (
                        candidateRole
                                == DayMasterElementRole.OUTPUT
                ) {

                    yield new StructureDamageRemedyMapping(
                            false
                    );
                }

                yield null;
            }

            /*
             * 상관견관
             *
             * 인성으로 상관 조절.
             */
            case DIRECT_OFFICER_HURT_BY_HURTING_OFFICER,
                 HURTING_OFFICER_MEETS_DIRECT_OFFICER -> {

                if (
                        candidateRole
                                == DayMasterElementRole.RESOURCE
                ) {

                    yield new StructureDamageRemedyMapping(
                            true
                    );
                }

                yield null;
            }

            /*
             * 편인도식
             *
             * 재성으로 편인을 제어하는 방향.
             */
            case EATING_GOD_BLOCKED_BY_INDIRECT_RESOURCE -> {

                if (
                        candidateRole
                                == DayMasterElementRole.WEALTH
                ) {

                    yield new StructureDamageRemedyMapping(
                            true
                    );
                }

                yield null;
            }

            /*
             * 재극인
             *
             * 비겁으로 재성 부담을 제어하거나
             * 관성으로 인성 흐름을 보조.
             */
            case RESOURCE_CONTROLLED_BY_WEALTH -> {

                if (
                        candidateRole
                                == DayMasterElementRole.COMPANION
                ) {

                    yield new StructureDamageRemedyMapping(
                            true
                    );
                }

                if (
                        candidateRole
                                == DayMasterElementRole.OFFICER
                ) {

                    yield new StructureDamageRemedyMapping(
                            false
                    );
                }

                yield null;
            }

            /*
             * 재생살
             *
             * 인성화살
             * 또는 식상제살.
             */
            case SEVEN_KILLINGS_STRENGTHENED_BY_WEALTH,
                 SEVEN_KILLINGS_LACKS_REGULATION -> {

                if (
                        candidateRole
                                == DayMasterElementRole.RESOURCE

                                || candidateRole
                                == DayMasterElementRole.OUTPUT
                ) {

                    yield new StructureDamageRemedyMapping(
                            true
                    );
                }

                yield null;
            }
        };
    }

    private boolean aggravatesPrimaryStructureDamage(

            DayMasterElementRole candidateRole,

            StructureAnalysis structure

    ) {

        if (
                !structure.primaryIsStandard()
        ) {

            return false;
        }

        return structure
                .primaryCandidate()
                .standardEvidence()
                .damageEvidence()
                .damageSignals()
                .stream()
                .filter(
                        StructureDamageSignal
                                ::meaningful
                )
                .filter(
                        signal ->
                                signal
                                        .patternType()
                                        .sourceBased()
                )
                .map(
                        signal ->
                                signal
                                        .patternType()
                                        .damagingRole()
                )
                .filter(
                        Objects::nonNull
                )
                .anyMatch(
                        damagingRole ->
                                damagingRole
                                        == candidateRole
                );
    }

    private PathologySeverity severityOf(

            PathologyAnalysis pathology,

            PathologyPatternType pattern

    ) {

        return pathology
                .evidences()
                .stream()
                .filter(
                        evidence ->
                                evidence.patternType()
                                        == pattern
                )
                .map(
                        PathologyEvidence
                                ::severity
                )
                .max(
                        java.util.Comparator.comparingInt(
                                PathologySeverity
                                        ::rank
                        )
                )
                .orElse(
                        PathologySeverity.NONE
                );
    }

    private FiveElement firstRelatedElement(
            PathologyEvidence evidence
    ) {

        if (
                evidence.relatedElements()
                        .isEmpty()
        ) {

            return null;
        }

        return evidence
                .relatedElements()
                .get(
                        0
                );
    }

    private boolean weakSide(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED_WEAK

                || level
                == DayMasterStrengthLevel.WEAK

                || level
                == DayMasterStrengthLevel.EXTREME_WEAK;
    }

    private boolean strongSide(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED_STRONG

                || level
                == DayMasterStrengthLevel.STRONG

                || level
                == DayMasterStrengthLevel.EXTREME_STRONG;
    }

    public FiveElement producedElementOf(
            FiveElement source
    ) {

        Objects.requireNonNull(
                source,
                "기준 오행이 필요합니다."
        );

        return switch (
                source
                ) {

            case WOOD ->
                    FiveElement.FIRE;

            case FIRE ->
                    FiveElement.EARTH;

            case EARTH ->
                    FiveElement.METAL;

            case METAL ->
                    FiveElement.WATER;

            case WATER ->
                    FiveElement.WOOD;
        };
    }

    /**
     * target을 극하는 오행.
     */
    public FiveElement controllerOf(
            FiveElement target
    ) {

        Objects.requireNonNull(
                target,
                "대상 오행이 필요합니다."
        );

        return switch (
                target
                ) {

            case WOOD ->
                    FiveElement.METAL;

            case FIRE ->
                    FiveElement.WATER;

            case EARTH ->
                    FiveElement.WOOD;

            case METAL ->
                    FiveElement.FIRE;

            case WATER ->
                    FiveElement.EARTH;
        };
    }

    private boolean produces(

            FiveElement source,

            FiveElement target

    ) {

        return producedElementOf(
                source
        )
                == target;
    }

    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return controllerOf(
                target
        )
                == source;
    }

    private record StructureDamageRemedyMapping(
            boolean direct
    ) {
    }
}