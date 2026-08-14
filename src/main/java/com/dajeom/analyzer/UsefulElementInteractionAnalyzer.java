package com.dajeom.analyzer;

import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.ElementCandidateMatch;
import com.dajeom.model.LuckPillarSource;
import com.dajeom.model.Pillar;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.type.FiveElement;
import com.dajeom.type.LuckSourceType;
import com.dajeom.type.PillarPart;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 대운/세운에서 유입되는 오행이
 * 원국에서 계산된:
 *
 * - 최종 용신
 * - 희신
 * - 기신
 * - 조후용신 후보
 * - 통관용신 후보
 *
 * 중 어디에 해당하는지 분석한다.
 *
 * =================================================
 *
 * 주의:
 *
 * 이것을 바로 길흉으로 변환하지 않는다.
 *
 * =================================================
 *
 * 현재 정밀화 구조:
 *
 * 최종 용신 / 희신 / 기신
 * ->
 * UsefulGodRelationAnalysis
 *
 * 조후용신
 * ->
 * UsefulGodSelectionAnalysis
 *     -> ClimateUsefulGodAnalysis
 *
 * 통관용신
 * ->
 * UsefulGodSelectionAnalysis
 *     -> PassageUsefulGodAnalysis
 *
 * 를 사용한다.
 */
@Component
public class UsefulElementInteractionAnalyzer {

    public List<ElementCandidateMatch> analyze(
            UsefulGodAnalysis usefulGodAnalysis,
            DecadeLuckPeriod decadeLuckPeriod,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        Objects.requireNonNull(
                usefulGodAnalysis,
                "용신 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "세운 분석 결과는 필수입니다."
        );

        List<ElementCandidateMatch> result =
                new ArrayList<>();

        /*
         * =================================================
         * 대운
         * =================================================
         */
        if (
                decadeLuckPeriod != null
        ) {

            /*
             * 현재 DecadeLuckPeriod의 간지는
             * SexagenaryPillar 구조이므로
             * 기존 LuckPillarSource가 사용하는 Pillar로 변환한다.
             */
            Pillar decadePillar =
                    new Pillar(
                            decadeLuckPeriod
                                    .pillar()
                                    .stem(),

                            decadeLuckPeriod
                                    .pillar()
                                    .branch()
                    );

            LuckPillarSource decadeSource =
                    new LuckPillarSource(
                            LuckSourceType.DECADE_LUCK,
                            null,
                            decadeLuckPeriod.sequenceNumber(),
                            decadePillar
                    );

            addPillarMatches(
                    result,
                    usefulGodAnalysis,
                    decadeSource
            );
        }

        /*
         * =================================================
         * 세운
         * =================================================
         */
        Pillar annualPillar =
                new Pillar(
                        annualLuckAnalysis
                                .annualStem(),

                        annualLuckAnalysis
                                .annualBranch()
                );

        LuckPillarSource annualSource =
                new LuckPillarSource(
                        LuckSourceType.ANNUAL_LUCK,
                        null,
                        annualLuckAnalysis.targetYear(),
                        annualPillar
                );

        addPillarMatches(
                result,
                usefulGodAnalysis,
                annualSource
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 하나의 운 기둥에서:
     *
     * 천간 오행
     * 지지 오행
     *
     * 을 각각 검사한다.
     */
    private void addPillarMatches(
            List<ElementCandidateMatch> target,
            UsefulGodAnalysis usefulGodAnalysis,
            LuckPillarSource source
    ) {

        FiveElement stemElement =
                source
                        .pillar()
                        .stem()
                        .getElement();

        FiveElement branchElement =
                source
                        .pillar()
                        .branch()
                        .getElement();

        target.add(
                createMatch(
                        usefulGodAnalysis,
                        source,
                        PillarPart.STEM,
                        stemElement
                )
        );

        target.add(
                createMatch(
                        usefulGodAnalysis,
                        source,
                        PillarPart.BRANCH,
                        branchElement
                )
        );
    }

    /**
     * 하나의 유입 오행이
     * 현재 용신 체계에서 어떤 역할에 해당하는지 판정한다.
     *
     * =================================================
     *
     * 최종 용신 / 희신 / 기신:
     *
     * 7-9 UsefulGodRelationAnalysis를 사용한다.
     *
     * =================================================
     *
     * 조후:
     *
     * 7-3 ClimateUsefulGodAnalysis에서
     * PRIMARY / SECONDARY로 실제 선택된 경우만 true.
     *
     * =================================================
     *
     * 통관:
     *
     * 7-6 PassageUsefulGodAnalysis에서
     * PRIMARY / SECONDARY로 실제 선택된 경우만 true.
     *
     * CONDITIONAL은 확정 후보로 취급하지 않는다.
     */
    private ElementCandidateMatch createMatch(
            UsefulGodAnalysis usefulGodAnalysis,
            LuckPillarSource source,
            PillarPart pillarPart,
            FiveElement element
    ) {

        /*
         * =================================================
         * 7-9
         * 최종 용신 / 희신 / 기신
         * =================================================
         */
        boolean yongshinCandidate =
                usefulGodAnalysis
                        .relationAnalysis()
                        .usefulGodElements()
                        .contains(
                                element
                        );

        boolean huishinCandidate =
                usefulGodAnalysis
                        .relationAnalysis()
                        .favorableGodElements()
                        .contains(
                                element
                        );

        boolean gishinCandidate =
                usefulGodAnalysis
                        .relationAnalysis()
                        .unfavorableGodElements()
                        .contains(
                                element
                        );

        /*
         * =================================================
         * 7-3
         * 조후용신
         * =================================================
         *
         * ClimateUsefulGodAnalysis는
         * 木火土金水 전체 후보를 보존하므로
         * candidateOf() 존재 여부만으로 판단하면 안 된다.
         *
         * 실제 선택된 PRIMARY / SECONDARY인지 확인한다.
         */
        boolean temperatureCandidate =
                usefulGodAnalysis
                        .selectionAnalysis()
                        .climateAnalysis()
                        .candidateOf(
                                element
                        )
                        .map(
                                candidate ->
                                        candidate.selected()
                        )
                        .orElse(
                                false
                        );

        /*
         * =================================================
         * 7-6
         * 통관용신
         * =================================================
         *
         * PassageUsefulGodAnalysis 역시
         * 木火土金水 전체를 보존한다.
         *
         * 실제 선택된 PRIMARY / SECONDARY인지 확인한다.
         */
        boolean mediationCandidate =
                usefulGodAnalysis
                        .selectionAnalysis()
                        .passageAnalysis()
                        .candidateOf(
                                element
                        )
                        .map(
                                candidate ->
                                        candidate.selected()
                        )
                        .orElse(
                                false
                        );

        return new ElementCandidateMatch(
                source,
                pillarPart,
                element,
                yongshinCandidate,
                huishinCandidate,
                gishinCandidate,
                temperatureCandidate,
                mediationCandidate
        );
    }
}