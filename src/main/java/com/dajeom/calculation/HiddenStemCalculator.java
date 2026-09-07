package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStem;
import com.dajeom.domain.TenGod;
import com.dajeom.rule.SajuRuleBook;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 지장간 계산기.
 *
 * 지장간 자체는 SajuRuleBook의 고정 규칙을 사용한다.
 *
 * 각 지장간이 일간과 어떤 십성 관계인지까지
 * TenGodCalculator를 통해 계산한다.
 */
@Component
public class HiddenStemCalculator {

    private final TenGodCalculator tenGodCalculator;

    public HiddenStemCalculator(
            TenGodCalculator tenGodCalculator
    ) {

        this.tenGodCalculator =
                Objects.requireNonNull(
                        tenGodCalculator,
                        "TenGodCalculator가 필요합니다."
                );
    }

    /**
     * 지지의 지장간 원본만 반환.
     */
    public List<HiddenStem> calculate(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        return SajuRuleBook.hiddenStems(
                branch
        );
    }

    /**
     * 일간 기준으로
     * 지장간 각각의 십성까지 계산한다.
     */
    public List<HiddenStemTenGod> calculate(
            HeavenlyStem dayStem,
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        return SajuRuleBook
                .hiddenStems(
                        branch
                )
                .stream()
                .map(hiddenStem ->
                        new HiddenStemTenGod(
                                hiddenStem,
                                tenGodCalculator.calculate(
                                        dayStem,
                                        hiddenStem.stem()
                                )
                        )
                )
                .toList();
    }

    /**
     * 해당 지지의 정기 기준 대표 십성.
     *
     * 천을귀인 화면에서 지지 바로 위에 표시되는
     * 대표 십성을 계산할 때 사용한다.
     */
    public TenGod calculateMainTenGod(
            HeavenlyStem dayStem,
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        HiddenStem main =
                SajuRuleBook.mainHiddenStem(
                        branch
                );

        return tenGodCalculator.calculate(
                dayStem,
                main.stem()
        );
    }

    /**
     * 지장간 + 해당 지장간의 십성.
     */
    public record HiddenStemTenGod(
            HiddenStem hiddenStem,
            TenGod tenGod
    ) {

        public HiddenStemTenGod {

            Objects.requireNonNull(
                    hiddenStem,
                    "지장간이 필요합니다."
            );

            Objects.requireNonNull(
                    tenGod,
                    "십성이 필요합니다."
            );
        }

        public HeavenlyStem stem() {
            return hiddenStem.stem();
        }

        public HiddenStem.Position position() {
            return hiddenStem.position();
        }

        public String stemHanja() {
            return hiddenStem.stem().hanja();
        }

        public String tenGodKorean() {
            return tenGod.korean();
        }
    }
}