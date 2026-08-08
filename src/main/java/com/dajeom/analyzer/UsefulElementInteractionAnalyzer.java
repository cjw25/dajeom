package com.dajeom.analyzer;

import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.ElementCandidateMatch;
import com.dajeom.model.LuckPillarSource;
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
 * - 억부 용신 후보
 * - 희신 후보
 * - 기신 후보
 * - 조후 후보
 * - 통관 후보
 *
 * 중 어디에 해당하는지 분석한다.
 *
 * 이것을 바로 길흉으로 변환하지 않는다.
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

        if (decadeLuckPeriod != null) {

            LuckPillarSource decadeSource =
                    new LuckPillarSource(
                            LuckSourceType.DECADE_LUCK,
                            null,
                            decadeLuckPeriod.order(),
                            decadeLuckPeriod.pillar()
                    );

            addPillarMatches(
                    result,
                    usefulGodAnalysis,
                    decadeSource
            );
        }

        LuckPillarSource annualSource =
                new LuckPillarSource(
                        LuckSourceType.ANNUAL_LUCK,
                        null,
                        annualLuckAnalysis.targetYear(),
                        annualLuckAnalysis.pillar()
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

    private ElementCandidateMatch createMatch(
            UsefulGodAnalysis usefulGodAnalysis,
            LuckPillarSource source,
            PillarPart pillarPart,
            FiveElement element
    ) {

        return new ElementCandidateMatch(
                source,
                pillarPart,
                element,

                usefulGodAnalysis
                        .yongshinCandidates()
                        .contains(
                                element
                        ),

                usefulGodAnalysis
                        .huishinCandidates()
                        .contains(
                                element
                        ),

                usefulGodAnalysis
                        .gishinCandidates()
                        .contains(
                                element
                        ),

                usefulGodAnalysis
                        .temperatureCandidates()
                        .contains(
                                element
                        ),

                usefulGodAnalysis
                        .mediationCandidates()
                        .contains(
                                element
                        )
        );
    }
}