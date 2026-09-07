package com.dajeom.calculation;

import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimCoverageAnalysis;
import com.dajeom.domain.NatalLifeClaimPipelineResult;
import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.OfficerKillingsNodeRef;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationAnalysis;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationStatus;
import com.dajeom.domain.PatternOfficerKillingsYangBladeCompletionAnalysis;
import com.dajeom.domain.PatternOfficerKillingsYangBladeCompletionStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 원국 LifeClaim 생성 전체 순서를 하나로 고정하는 Pipeline.
 *
 * =================================================
 *
 * 최종 순서
 * =================================================
 *
 * NatalLifeClaimCalculator
 *
 * ↓
 *
 * TemperamentLifeClaimCalculator
 *
 * ↓
 *
 * RelationshipLifeClaimCalculator
 *
 * ↓
 *
 * WealthLifeClaimCalculator
 *
 * ↓
 *
 * WorkAchievementLifeClaimCalculator
 *
 * ↓
 *
 * HealthLifestyleLifeClaimCalculator
 *
 * ↓
 *
 * NatalBaseLifeClaimCalculator
 *
 * ↓
 *
 * OfficerKillingsFinalLifeClaimCorrectionCalculator
 *
 * ↓
 *
 * LifeClaimCoverageCalculator
 *
 * =================================================
 *
 * 官殺 관련 최종 원칙
 * =================================================
 *
 * 마지막 단계의
 *
 * PatternOfficerKillingsFinalPurificationAnalysis
 *
 * 가 최종 권위다.
 */
@Component
public class NatalLifeClaimPipelineCalculator {

    private final NatalLifeClaimCalculator
            natalLifeClaimCalculator;

    private final TemperamentLifeClaimCalculator
            temperamentLifeClaimCalculator;

    private final RelationshipLifeClaimCalculator
            relationshipLifeClaimCalculator;

    private final WealthLifeClaimCalculator
            wealthLifeClaimCalculator;

    private final WorkAchievementLifeClaimCalculator
            workAchievementLifeClaimCalculator;

    private final HealthLifestyleLifeClaimCalculator
            healthLifestyleLifeClaimCalculator;

    private final NatalBaseLifeClaimCalculator
            natalBaseLifeClaimCalculator;

    private final OfficerKillingsFinalLifeClaimCorrectionCalculator
            officerKillingsFinalLifeClaimCorrectionCalculator;

    private final LifeClaimCoverageCalculator
            lifeClaimCoverageCalculator;

    public NatalLifeClaimPipelineCalculator(

            NatalLifeClaimCalculator
                    natalLifeClaimCalculator,

            TemperamentLifeClaimCalculator
                    temperamentLifeClaimCalculator,

            RelationshipLifeClaimCalculator
                    relationshipLifeClaimCalculator,

            WealthLifeClaimCalculator
                    wealthLifeClaimCalculator,

            WorkAchievementLifeClaimCalculator
                    workAchievementLifeClaimCalculator,

            HealthLifestyleLifeClaimCalculator
                    healthLifestyleLifeClaimCalculator,

            NatalBaseLifeClaimCalculator
                    natalBaseLifeClaimCalculator,

            OfficerKillingsFinalLifeClaimCorrectionCalculator
                    officerKillingsFinalLifeClaimCorrectionCalculator,

            LifeClaimCoverageCalculator
                    lifeClaimCoverageCalculator

    ) {

        this.natalLifeClaimCalculator =
                Objects.requireNonNull(
                        natalLifeClaimCalculator,
                        "NatalLifeClaimCalculator는 필수입니다."
                );

        this.temperamentLifeClaimCalculator =
                Objects.requireNonNull(
                        temperamentLifeClaimCalculator,
                        "TemperamentLifeClaimCalculator는 필수입니다."
                );

        this.relationshipLifeClaimCalculator =
                Objects.requireNonNull(
                        relationshipLifeClaimCalculator,
                        "RelationshipLifeClaimCalculator는 필수입니다."
                );

        this.wealthLifeClaimCalculator =
                Objects.requireNonNull(
                        wealthLifeClaimCalculator,
                        "WealthLifeClaimCalculator는 필수입니다."
                );

        this.workAchievementLifeClaimCalculator =
                Objects.requireNonNull(
                        workAchievementLifeClaimCalculator,
                        "WorkAchievementLifeClaimCalculator는 필수입니다."
                );

        this.healthLifestyleLifeClaimCalculator =
                Objects.requireNonNull(
                        healthLifestyleLifeClaimCalculator,
                        "HealthLifestyleLifeClaimCalculator는 필수입니다."
                );

        this.natalBaseLifeClaimCalculator =
                Objects.requireNonNull(
                        natalBaseLifeClaimCalculator,
                        "NatalBaseLifeClaimCalculator는 필수입니다."
                );

        this.officerKillingsFinalLifeClaimCorrectionCalculator =
                Objects.requireNonNull(
                        officerKillingsFinalLifeClaimCorrectionCalculator,
                        "OfficerKillingsFinalLifeClaimCorrectionCalculator는 필수입니다."
                );

        this.lifeClaimCoverageCalculator =
                Objects.requireNonNull(
                        lifeClaimCoverageCalculator,
                        "LifeClaimCoverageCalculator는 필수입니다."
                );
    }

    /**
     * 기존 호환용.
     *
     * =================================================
     *
     * node-level 羊刃合殺 판정이 아직 호출부에
     * 연결되지 않은 경우 사용한다.
     *
     * 이 경로에서는 官殺取清을
     * 보수적으로 판정한다.
     *
     * 즉:
     *
     * 官殺混雜이 있는데
     * node-level 최종 결과가 제공되지 않았다면
     *
     * "정리 완료"
     *
     * 로 올리지 않는다.
     *
     * false positive보다 false negative를 택한다.
     */
    public NatalLifeClaimPipelineResult calculate(
            NatalMyeongriProfile profile
    ) {

        Objects.requireNonNull(
                profile,
                "NatalMyeongriProfile은 필수입니다."
        );

        PatternOfficerKillingsFinalPurificationAnalysis
                conservativeFinalPurification =
                conservativeFinalPurification(
                        profile
                );

        return calculate(
                profile,
                conservativeFinalPurification
        );
    }

    /**
     * 최종 권위 官殺 판정을 함께 사용하는
     * 정식 Pipeline.
     *
     * 실제 engine 연결이 완료되면
     * 이 메서드를 사용해야 한다.
     */
    public NatalLifeClaimPipelineResult calculate(

            NatalMyeongriProfile profile,

            PatternOfficerKillingsFinalPurificationAnalysis
                    finalPurification

    ) {

        Objects.requireNonNull(
                profile,
                "NatalMyeongriProfile은 필수입니다."
        );

        Objects.requireNonNull(
                finalPurification,
                "官殺 최종 purification 분석은 필수입니다."
        );

        /*
         * =================================================
         * 1. 원국 핵심 Claim
         * =================================================
         */

        List<LifeClaim> claims =
                natalLifeClaimCalculator
                        .calculate(
                                profile
                        );

        /*
         * =================================================
         * 2. 성향·기질
         * =================================================
         */

        claims =
                temperamentLifeClaimCalculator
                        .enrich(
                                profile,
                                claims
                        );

        /*
         * =================================================
         * 3. 가족·관계 / 연애
         * =================================================
         */

        claims =
                relationshipLifeClaimCalculator
                        .enrich(
                                profile,
                                claims
                        );

        /*
         * =================================================
         * 4. 재물
         * =================================================
         */

        claims =
                wealthLifeClaimCalculator
                        .enrich(
                                profile,
                                claims
                        );

        /*
         * =================================================
         * 5. 일·성취
         * =================================================
         */

        claims =
                workAchievementLifeClaimCalculator
                        .enrich(
                                profile,
                                claims
                        );

        /*
         * =================================================
         * 6. 건강·생활
         * =================================================
         */

        claims =
                healthLifestyleLifeClaimCalculator
                        .enrich(
                                profile,
                                claims
                        );

        /*
         * =================================================
         * 7. 기본 Coverage 보충
         * =================================================
         */

        claims =
                natalBaseLifeClaimCalculator
                        .complete(
                                profile,
                                claims
                        );

        /*
         * =================================================
         * 8. 官殺 최종 교정
         * =================================================
         *
         * 반드시 Base 보충 이후.
         *
         * 앞 단계 어디에서 잘못된
         * 官殺 정리 Claim이 만들어졌더라도
         * 여기서 최종 판정 기준으로 제거/재생성한다.
         */

        claims =
                officerKillingsFinalLifeClaimCorrectionCalculator
                        .correct(
                                claims,
                                finalPurification
                        );

        /*
         * =================================================
         * 9. 최종 Coverage
         * =================================================
         */

        LifeClaimCoverageAnalysis coverage =
                lifeClaimCoverageCalculator
                        .analyze(
                                claims
                        );

        return new NatalLifeClaimPipelineResult(
                claims,
                coverage
        );
    }

    public List<LifeClaim> calculateClaims(
            NatalMyeongriProfile profile
    ) {

        return calculate(
                profile
        ).claims();
    }

    public List<LifeClaim> calculateClaims(

            NatalMyeongriProfile profile,

            PatternOfficerKillingsFinalPurificationAnalysis
                    finalPurification

    ) {

        return calculate(
                profile,
                finalPurification
        ).claims();
    }

    /*
     * =================================================
     * node-level 결과가 아직 전달되지 않은 경우
     * =================================================
     *
     * 官殺混雜을 무리하게 "정리 완료"로 보지 않는다.
     */

    private PatternOfficerKillingsFinalPurificationAnalysis
    conservativeFinalPurification(
            NatalMyeongriProfile profile
    ) {

        boolean mixed =
                profile
                        .officerKillingsPurification()
                        .mixed();

        boolean reviewRequired =
                profile
                        .officerKillingsPurification()
                        .reviewRequired();

        PatternOfficerKillingsYangBladeCompletionAnalysis
                noYangBladeCompletion =
                new PatternOfficerKillingsYangBladeCompletionAnalysis(

                        PatternOfficerKillingsYangBladeCompletionStatus
                                .NO_RETAINED_KILLINGS,

                        Set.<OfficerKillingsNodeRef>of(),

                        Set.<OfficerKillingsNodeRef>of(),

                        Set.<OfficerKillingsNodeRef>of(),

                        Set.<OfficerKillingsNodeRef>of()
                );

        if (!mixed) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    false,

                    PatternOfficerKillingsFinalPurificationStatus
                            .NOT_MIXED,

                    false,

                    false,

                    noYangBladeCompletion
            );
        }

        if (reviewRequired) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    true,

                    PatternOfficerKillingsFinalPurificationStatus
                            .REVIEW_REQUIRED,

                    false,

                    false,

                    noYangBladeCompletion
            );
        }

        /*
         * node-level 권위 판정이 없는 경우
         * mixtureCleared를 임의 인정하지 않는다.
         */
        return new PatternOfficerKillingsFinalPurificationAnalysis(

                true,

                PatternOfficerKillingsFinalPurificationStatus
                        .UNRESOLVED,

                false,

                false,

                noYangBladeCompletion
        );
    }
}