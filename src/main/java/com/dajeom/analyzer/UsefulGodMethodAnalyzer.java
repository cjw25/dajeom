package com.dajeom.analyzer;

import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodEvidence;
import com.dajeom.rule.UsefulGodMethodRule;
import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.PassageNeedLevel;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-1.
 *
 * 6차 ClimateHealthAnalysis를 받아
 * 어떤 용신법을 현재 명식에 적용할지 결정한다.
 *
 * =================================================
 *
 * 처리
 *
 * 4차 신강신약
 * -> 억부용신법
 *
 * 6-2 조후 필요도
 * -> 조후용신법
 *
 * 5차 격국
 * -> 격국용신법
 *
 * 6-4 병 구조
 * -> 병약용신법
 *
 * 6-6 통관 필요도
 * -> 통관용신법
 *
 * 5차 종격 주격 여부
 * -> 종격용신법
 *
 * =================================================
 *
 * 아직 실제 五行 용신 후보는 생성하지 않는다.
 *
 * 실제 후보 계산은:
 *
 * 7-2 ~ 7-7
 *
 * 에서 각 방법별로 독립 수행한다.
 */
@Component
public class UsefulGodMethodAnalyzer {

    private final UsefulGodMethodRule
            usefulGodMethodRule;

    public UsefulGodMethodAnalyzer(
            UsefulGodMethodRule
                    usefulGodMethodRule
    ) {

        this.usefulGodMethodRule =
                Objects.requireNonNull(
                        usefulGodMethodRule,
                        "UsefulGodMethodRule이 필요합니다."
                );
    }

    public UsefulGodMethodAnalysis analyze(
            ClimateHealthAnalysis climateHealth
    ) {

        Objects.requireNonNull(
                climateHealth,
                "6차 조후·병약·통관 분석 결과가 필요합니다."
        );

        StructureAnalysis structure =
                climateHealth
                        .structureAnalysis();

        DayMasterStrengthLevel strengthLevel =
                structure
                        .dayMasterStrengthAnalysis()
                        .level();

        ClimateAdjustmentNeedLevel climateNeed =
                climateHealth
                        .climateAdjustmentNeedAnalysis()
                        .overallNeed();

        PathologySeverity pathologySeverity =
                climateHealth
                        .pathologyAnalysis()
                        .overallSeverity();

        PassageNeedLevel passageNeed =
                climateHealth
                        .passageNeedAnalysis()
                        .overallNeed();

        /*
         * ================================================
         * 종격 모드
         * ================================================
         *
         * 단순히 5-8에 종격 후보가 존재한다는 뜻이 아니라
         * 5-9에서 실제 PRIMARY로 선택된 경우만
         * 종격 모드로 본다.
         */
        boolean followStructureMode =
                structure
                        .primaryCandidate()
                        .followStructureCandidate();

        List<UsefulGodMethodEvidence> methods =
                new ArrayList<>();

        /*
         * ================================================
         * 1.
         * 억부용신법
         * ================================================
         */
        UsefulGodMethodApplicability balancingApplicability =
                usefulGodMethodRule
                        .determineBalancingApplicability(
                                strengthLevel,
                                followStructureMode
                        );

        methods.add(
                new UsefulGodMethodEvidence(
                        UsefulGodMethodType.BALANCING,
                        balancingApplicability,
                        usefulGodMethodRule
                                .balancingReasons(
                                        strengthLevel,
                                        followStructureMode
                                ),
                        !followStructureMode,
                        followStructureMode
                )
        );

        /*
         * ================================================
         * 2.
         * 조후용신법
         * ================================================
         */
        UsefulGodMethodApplicability climateApplicability =
                usefulGodMethodRule
                        .determineClimateApplicability(
                                climateNeed
                        );

        methods.add(
                new UsefulGodMethodEvidence(
                        UsefulGodMethodType.CLIMATE_CONTROL,
                        climateApplicability,
                        usefulGodMethodRule
                                .climateReasons(
                                        climateNeed
                                ),
                        !climateApplicability.inactive(),
                        false
                )
        );

        /*
         * ================================================
         * 3.
         * 격국용신법
         * ================================================
         */
        UsefulGodMethodApplicability structureApplicability =
                usefulGodMethodRule
                        .determineStructureApplicability(
                                structure,
                                followStructureMode
                        );

        methods.add(
                new UsefulGodMethodEvidence(
                        UsefulGodMethodType.STRUCTURE,
                        structureApplicability,
                        usefulGodMethodRule
                                .structureReasons(
                                        structure,
                                        followStructureMode
                                ),
                        !structureApplicability.inactive()
                                && !followStructureMode,
                        followStructureMode
                )
        );

        /*
         * ================================================
         * 4.
         * 병약용신법
         * ================================================
         */
        UsefulGodMethodApplicability remedyApplicability =
                usefulGodMethodRule
                        .determineRemedyApplicability(
                                pathologySeverity
                        );

        methods.add(
                new UsefulGodMethodEvidence(
                        UsefulGodMethodType.REMEDY,
                        remedyApplicability,
                        usefulGodMethodRule
                                .remedyReasons(
                                        pathologySeverity
                                ),
                        !remedyApplicability.inactive(),
                        false
                )
        );

        /*
         * ================================================
         * 5.
         * 통관용신법
         * ================================================
         */
        UsefulGodMethodApplicability passageApplicability =
                usefulGodMethodRule
                        .determinePassageApplicability(
                                passageNeed
                        );

        methods.add(
                new UsefulGodMethodEvidence(
                        UsefulGodMethodType.PASSAGE,
                        passageApplicability,
                        usefulGodMethodRule
                                .passageReasons(
                                        passageNeed
                                ),
                        !passageApplicability.inactive(),
                        false
                )
        );

        /*
         * ================================================
         * 6.
         * 종격용신법
         * ================================================
         */
        UsefulGodMethodApplicability followApplicability =
                usefulGodMethodRule
                        .determineFollowApplicability(
                                followStructureMode
                        );

        methods.add(
                new UsefulGodMethodEvidence(
                        UsefulGodMethodType.FOLLOW_STRUCTURE,
                        followApplicability,
                        usefulGodMethodRule
                                .followReasons(
                                        followStructureMode
                                ),
                        followStructureMode,
                        false
                )
        );

        /*
         * ================================================
         * 출력 정렬
         * ================================================
         *
         * CORE
         * ACTIVE
         * CONDITIONAL
         * INACTIVE
         *
         * 순으로 정렬한다.
         *
         * 같은 적용도에서는
         * UsefulGodMethodType의 정의 순서를 유지한다.
         *
         * 이 정렬은 용신법 점수가 아니다.
         */
        methods.sort(
                Comparator
                        .comparingInt(
                                (
                                        UsefulGodMethodEvidence evidence
                                ) ->
                                        evidence
                                                .applicability()
                                                .rank()
                        )
                        .reversed()

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .methodType()
                                                .ordinal()
                        )
        );

        return new UsefulGodMethodAnalysis(
                climateHealth.dayMaster(),
                climateHealth.dayMasterElement(),
                climateHealth.monthBranch(),
                climateHealth,
                methods,
                followStructureMode,
                climateHealth.completeChart()
        );
    }
}