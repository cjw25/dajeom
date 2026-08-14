package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-1.
 *
 * 용신 후보를 계산하기 전에
 * 어떤 용신법이 현재 명식에서
 * 실제로 적용될지를 분리한 결과.
 *
 * =================================================
 *
 * 항상 다음 6개 방법이 정확히 한 번씩 존재한다.
 *
 * BALANCING
 * CLIMATE_CONTROL
 * STRUCTURE
 * REMEDY
 * PASSAGE
 * FOLLOW_STRUCTURE
 *
 * =================================================
 *
 * 여러 CORE 방법이 동시에 존재할 수 있다.
 *
 * 예:
 *
 * 억부 CORE
 * 조후 CORE
 * 병약 CORE
 *
 * 이것을 지금 하나로 선택하지 않는다.
 *
 * 7-2~7-7에서 각각 후보를 만든 뒤
 * 7-8에서 수렴과 충돌을 처리한다.
 */
public record UsefulGodMethodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        ClimateHealthAnalysis climateHealthAnalysis,

        List<UsefulGodMethodEvidence> methods,

        boolean followStructureMode,

        boolean completeChart

) {

    public UsefulGodMethodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                climateHealthAnalysis,
                "6차 분석 결과가 필요합니다."
        );

        methods =
                methods == null
                        ? List.of()
                        : List.copyOf(
                        methods
                );

        if (
                methods.size()
                        != UsefulGodMethodType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "용신법 분석에는 6개 방법이 모두 포함되어야 합니다."
            );
        }

        EnumSet<UsefulGodMethodType> methodTypes =
                EnumSet.noneOf(
                        UsefulGodMethodType.class
                );

        for (
                UsefulGodMethodEvidence evidence
                : methods
        ) {

            if (
                    !methodTypes.add(
                            evidence.methodType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 용신법 Evidence가 중복되었습니다: "
                                + evidence.methodType()
                );
            }
        }

        if (
                climateHealthAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "6차 분석과 7-1 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                climateHealthAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "6차 분석과 7-1 분석의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                climateHealthAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "6차 분석과 7-1 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                climateHealthAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "6차 분석과 7-1 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        UsefulGodMethodEvidence followEvidence =
                methods
                        .stream()
                        .filter(
                                evidence ->
                                        evidence.methodType()
                                                == UsefulGodMethodType.FOLLOW_STRUCTURE
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "종격용신법 Evidence가 없습니다."
                                        )
                        );

        boolean expectedFollowMode =
                followEvidence
                        .applicability()
                        .core();

        if (
                followStructureMode
                        != expectedFollowMode
        ) {

            throw new IllegalArgumentException(
                    "종격 모드와 종격용신법 적용도가 일치하지 않습니다."
            );
        }
    }

    public List<UsefulGodMethodEvidence>
    coreMethods() {

        return methods
                .stream()
                .filter(
                        UsefulGodMethodEvidence
                                ::core
                )
                .toList();
    }

    public List<UsefulGodMethodEvidence>
    activeMethods() {

        return methods
                .stream()
                .filter(
                        UsefulGodMethodEvidence
                                ::active
                )
                .toList();
    }

    public List<UsefulGodMethodEvidence>
    conditionalMethods() {

        return methods
                .stream()
                .filter(
                        UsefulGodMethodEvidence
                                ::conditional
                )
                .toList();
    }

    public List<UsefulGodMethodEvidence>
    inactiveMethods() {

        return methods
                .stream()
                .filter(
                        UsefulGodMethodEvidence
                                ::inactive
                )
                .toList();
    }

    public List<UsefulGodMethodEvidence>
    applicableMethods() {

        return methods
                .stream()
                .filter(
                        UsefulGodMethodEvidence
                                ::applicable
                )
                .toList();
    }

    public List<UsefulGodMethodEvidence>
    meaningfulMethods() {

        return methods
                .stream()
                .filter(
                        UsefulGodMethodEvidence
                                ::meaningful
                )
                .toList();
    }

    public Optional<UsefulGodMethodEvidence>
    methodOf(
            UsefulGodMethodType methodType
    ) {

        if (
                methodType == null
        ) {

            return Optional.empty();
        }

        return methods
                .stream()
                .filter(
                        evidence ->
                                evidence.methodType()
                                        == methodType
                )
                .findFirst();
    }

    public boolean hasCoreMethod() {

        return !coreMethods()
                .isEmpty();
    }

    public boolean hasMultipleCoreMethods() {

        return coreMethods()
                .size() >= 2;
    }

    public Optional<UsefulGodMethodEvidence>
    singleCoreMethod() {

        List<UsefulGodMethodEvidence> core =
                coreMethods();

        if (
                core.size() != 1
        ) {

            return Optional.empty();
        }

        return Optional.of(
                core.get(
                        0
                )
        );
    }

    /**
     * 서로 다른 용신법 결과를
     * 7-8에서 통합할 필요가 있는가.
     */
    public boolean requiresMultiMethodIntegration() {

        return meaningfulMethods()
                .size() >= 2;
    }

    public boolean balancingSuppressedByFollowStructure() {

        return methodOf(
                UsefulGodMethodType.BALANCING
        )
                .map(
                        UsefulGodMethodEvidence
                                ::suppressedByFollowStructure
                )
                .orElse(
                        false
                );
    }

    public boolean climateMethodActive() {

        return methodOf(
                UsefulGodMethodType.CLIMATE_CONTROL
        )
                .map(
                        UsefulGodMethodEvidence
                                ::applicable
                )
                .orElse(
                        false
                );
    }

    public boolean structureMethodActive() {

        return methodOf(
                UsefulGodMethodType.STRUCTURE
        )
                .map(
                        UsefulGodMethodEvidence
                                ::applicable
                )
                .orElse(
                        false
                );
    }

    public boolean remedyMethodActive() {

        return methodOf(
                UsefulGodMethodType.REMEDY
        )
                .map(
                        UsefulGodMethodEvidence
                                ::applicable
                )
                .orElse(
                        false
                );
    }

    public boolean passageMethodActive() {

        return methodOf(
                UsefulGodMethodType.PASSAGE
        )
                .map(
                        UsefulGodMethodEvidence
                                ::applicable
                )
                .orElse(
                        false
                );
    }

    public boolean followMethodActive() {

        return methodOf(
                UsefulGodMethodType.FOLLOW_STRUCTURE
        )
                .map(
                        UsefulGodMethodEvidence
                                ::applicable
                )
                .orElse(
                        false
                );
    }

    public int countApplicability(
            UsefulGodMethodApplicability applicability
    ) {

        if (
                applicability == null
        ) {

            return 0;
        }

        return (int) methods
                .stream()
                .filter(
                        evidence ->
                                evidence.applicability()
                                        == applicability
                )
                .count();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}