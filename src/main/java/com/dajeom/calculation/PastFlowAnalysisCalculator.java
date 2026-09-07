package com.dajeom.calculation;

import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.PastFlowAnalysis;
import com.dajeom.domain.PeriodLifeClaim;
import com.dajeom.domain.PeriodLifeSignal;
import com.dajeom.domain.PeriodMyeongriFacts;
import com.dajeom.domain.PeriodMyeongriInfluence;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 지나온 흐름 전체 내부 파이프라인 Calculator.
 *
 * =================================================
 *
 * 입력
 *
 * NatalMyeongriProfile
 * +
 * PeriodMyeongriFacts
 *
 * =================================================
 *
 * 내부 처리
 *
 * PeriodMyeongriFacts
 *
 *      ↓
 *
 * PeriodMyeongriInfluenceCalculator
 *
 *      ↓
 *
 * PeriodMyeongriInfluence
 *
 *      ↓
 *
 * PeriodLifeSignalCalculator
 *
 *      ↓
 *
 * PeriodLifeSignal
 *
 *      ↓
 *
 * PastFlowLifeClaimCalculator
 *
 *      ↓
 *
 * PeriodLifeClaim
 *
 * =================================================
 *
 * 출력
 *
 * PastFlowAnalysis
 *
 * =================================================
 *
 * 이 Calculator는 기존 대운 / 세운 / 월운의
 * 천간지지 계산을 하지 않는다.
 *
 * 실제 운 계산 결과를 PeriodMyeongriFacts로
 * 정규화한 이후부터 담당한다.
 *
 * 따라서 기존 운 엔진과 이 계층 사이에는
 * Adapter만 존재하면 된다.
 */
@Component
public class PastFlowAnalysisCalculator {

    private final PeriodMyeongriInfluenceCalculator
            influenceCalculator;

    private final PeriodLifeSignalCalculator
            signalCalculator;

    private final PastFlowLifeClaimCalculator
            claimCalculator;

    public PastFlowAnalysisCalculator(

            PeriodMyeongriInfluenceCalculator
                    influenceCalculator,

            PeriodLifeSignalCalculator
                    signalCalculator,

            PastFlowLifeClaimCalculator
                    claimCalculator

    ) {

        this.influenceCalculator =
                Objects.requireNonNull(
                        influenceCalculator,
                        "PeriodMyeongriInfluenceCalculator는 필수입니다."
                );

        this.signalCalculator =
                Objects.requireNonNull(
                        signalCalculator,
                        "PeriodLifeSignalCalculator는 필수입니다."
                );

        this.claimCalculator =
                Objects.requireNonNull(
                        claimCalculator,
                        "PastFlowLifeClaimCalculator는 필수입니다."
                );
    }

    /**
     * 여러 대운 / 세운 / 월운 Fact를
     * 한 번에 지나온 흐름 분석으로 변환한다.
     */
    public PastFlowAnalysis calculate(

            NatalMyeongriProfile natalProfile,

            List<PeriodMyeongriFacts> facts

    ) {

        Objects.requireNonNull(
                natalProfile,
                "NatalMyeongriProfile은 필수입니다."
        );

        Objects.requireNonNull(
                facts,
                "PeriodMyeongriFacts 목록은 필수입니다."
        );

        /*
         * 입력 Fact가 없다면
         * 원국만으로 과거 시기 사건을 만들어내지 않는다.
         */
        if (facts.isEmpty()) {

            return new PastFlowAnalysis(
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of()
            );
        }

        validateFacts(
                facts
        );

        /*
         * =================================================
         * 1. Facts → Influence
         * =================================================
         */

        List<PeriodMyeongriInfluence> influences =
                influenceCalculator
                        .calculateAll(
                                facts
                        );

        /*
         * =================================================
         * 2. Influence → Signal
         * =================================================
         */

        List<PeriodLifeSignal> signals =
                signalCalculator
                        .calculate(
                                influences
                        );

        /*
         * =================================================
         * 3. Signal + 원국 → Period Claim
         * =================================================
         */

        List<PeriodLifeClaim> claims =
                claimCalculator
                        .calculate(
                                natalProfile,
                                signals
                        );

        /*
         * =================================================
         * 4. 종합 결과
         * =================================================
         */

        return new PastFlowAnalysis(
                facts,
                influences,
                signals,
                claims
        );
    }

    private void validateFacts(
            List<PeriodMyeongriFacts> facts
    ) {

        for (
                PeriodMyeongriFacts fact
                : facts
        ) {

            Objects.requireNonNull(
                    fact,
                    "PeriodMyeongriFacts 목록에 null이 포함될 수 없습니다."
            );
        }
    }
}