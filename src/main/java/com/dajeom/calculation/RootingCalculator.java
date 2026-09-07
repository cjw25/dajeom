package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemComponent;
import com.dajeom.domain.PillarPosition;
import com.dajeom.domain.StemGroundAnalysis;
import com.dajeom.domain.StemGroundHit;
import com.dajeom.domain.StemGroundRelationType;
import com.dajeom.rule.HiddenStemSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class RootingCalculator {

    private final HiddenStemSource hiddenStemSource;

    public RootingCalculator(
            HiddenStemSource hiddenStemSource
    ) {

        this.hiddenStemSource =
                Objects.requireNonNull(
                        hiddenStemSource,
                        "HiddenStemSource는 필수입니다."
                );
    }

    public StemGroundAnalysis analyze(

            HeavenlyStem targetStem,

            EarthlyBranch yearBranch,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            Optional<EarthlyBranch> hourBranch

    ) {

        Objects.requireNonNull(
                targetStem,
                "분석 대상 천간은 필수입니다."
        );

        Objects.requireNonNull(
                yearBranch,
                "년지는 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지는 필수입니다."
        );

        Objects.requireNonNull(
                hourBranch,
                "시지 Optional은 필수입니다."
        );

        List<StemGroundHit> hits =
                new ArrayList<>();

        appendHits(
                hits,
                targetStem,
                PillarPosition.YEAR,
                yearBranch
        );

        appendHits(
                hits,
                targetStem,
                PillarPosition.MONTH,
                monthBranch
        );

        appendHits(
                hits,
                targetStem,
                PillarPosition.DAY,
                dayBranch
        );

        hourBranch.ifPresent(
                branch ->
                        appendHits(
                                hits,
                                targetStem,
                                PillarPosition.HOUR,
                                branch
                        )
        );

        return new StemGroundAnalysis(
                targetStem,
                hits
        );
    }

    private void appendHits(

            List<StemGroundHit> result,

            HeavenlyStem targetStem,

            PillarPosition branchPosition,

            EarthlyBranch branch

    ) {

        List<HiddenStemComponent> hiddenStems =
                hiddenStemSource.hiddenStemsOf(
                        branch
                );

        if (
                hiddenStems == null
                        || hiddenStems.isEmpty()
        ) {

            return;
        }

        for (
                HiddenStemComponent component
                : hiddenStems
        ) {

            Objects.requireNonNull(
                    component,
                    "지장간 목록에 null이 포함될 수 없습니다."
            );

            HeavenlyStem hiddenStem =
                    component.stem();

            StemGroundRelationType relationType =
                    relationTypeOf(
                            targetStem,
                            hiddenStem
                    );

            if (relationType == null) {

                continue;
            }

            result.add(
                    new StemGroundHit(
                            targetStem,
                            branchPosition,
                            branch,
                            hiddenStem,
                            component.phase(),
                            relationType
                    )
            );
        }
    }

    private StemGroundRelationType relationTypeOf(

            HeavenlyStem targetStem,

            HeavenlyStem hiddenStem

    ) {

        if (targetStem == hiddenStem) {

            return StemGroundRelationType.EXACT_STEM;
        }

        if (
                elementOf(targetStem)
                        == elementOf(hiddenStem)
        ) {

            return StemGroundRelationType.SAME_ELEMENT_OTHER_STEM;
        }

        return null;
    }

    /**
     * 현재 인수인계에서 확정된
     * 천간-오행 관계만 사용한다.
     *
     * HeavenlyStem의 실제 getter 이름을
     * 추측하지 않기 위해 enum 자체로 판정한다.
     */
    private FiveElement elementOf(
            HeavenlyStem stem
    ) {

        return switch (stem) {

            case JIA, YI ->
                    FiveElement.WOOD;

            case BING, DING ->
                    FiveElement.FIRE;

            case WU, JI ->
                    FiveElement.EARTH;

            case GENG, XIN ->
                    FiveElement.METAL;

            case REN, GUI ->
                    FiveElement.WATER;
        };
    }
}