package com.dajeom.calculator;

import com.dajeom.constant.HiddenStemTable;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 지장간 계산기.
 *
 * 지지에 포함되어 있는
 * 여기 / 중기 / 정기 천간을 조회한다.
 *
 * 지장간의 강도나 영향력 평가는
 * 여기서 하지 않는다.
 *
 * 해당 평가는 이후 Analyzer / Rule에서 수행한다.
 */
@Component
public class HiddenStemCalculator {

    /**
     * 지지를 기준으로 지장간을 구한다.
     */
    public List<HiddenStem> calculate(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        return HiddenStemTable.get(
                branch
        );
    }

    /**
     * Pillar의 지지를 기준으로
     * 지장간을 구한다.
     */
    public List<HiddenStem> calculate(
            Pillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "기둥은 필수입니다."
        );

        return calculate(
                pillar.branch()
        );
    }

    /**
     * 정기(본기) 천간만 반환한다.
     */
    public HeavenlyStem calculateMainStem(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        return HiddenStemTable.getMainStem(
                branch
        );
    }
}