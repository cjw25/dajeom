package com.dajeom.calculation;

import com.dajeom.domain.DayMasterRelationType;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemPhase;
import com.dajeom.domain.MonthCommandDayMasterAnalysis;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateAnalysis;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.MonthCommandStemRelation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 월령을 중심으로 격국 후보를 추출한다.
 *
 * =================================================
 *
 * 원칙:
 *
 * 1. 월령 MAIN은 본래 격국 후보로 항상 보존.
 *
 * 2. 월령 EARLY/MIDDLE은
 *    실제 천간에 투출했을 때만
 *    변화 격국 후보로 올림.
 *
 * 3. 후보 추출과 격국 확정은 분리.
 *
 * 4. MAIN과 다른 지장간이 동시에 투출했다고
 *    임의로 어느 하나를 선택하지 않음.
 *
 * 5. 비견/겁재 월령은
 *    일반 8격으로 억지 분류하지 않고
 *    建祿/月劫 검토용으로 분리.
 *
 * =================================================
 *
 * 숫자 / 가중치 / 다수결 없음.
 */
@Component
public class MonthCommandPatternCandidateCalculator {

    public MonthCommandPatternCandidateAnalysis analyze(
            MonthCommandDayMasterAnalysis monthCommand
    ) {

        Objects.requireNonNull(
                monthCommand,
                "월령-일간 관계 분석은 필수입니다."
        );

        HeavenlyStem dayMaster =
                monthCommand.dayMaster();

        MonthCommandStemRelation mainRelation =
                monthCommand
                        .mainRelation()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "월령 MAIN 지장간 관계가 없습니다."
                                        )
                        );

        MonthCommandPatternCandidate primaryCandidate =
                createCandidate(
                        monthCommand,
                        mainRelation
                );

        /*
         * 월령 MAIN 이외의 지장간 중
         * 실제 천간에 투출한 것만
         * 격국 변화 후보로 보존한다.
         */
        List<MonthCommandPatternCandidate>
                exposedAlternativeCandidates =
                monthCommand
                        .relations()
                        .stream()
                        .filter(
                                relation ->
                                        relation.phase()
                                                != HiddenStemPhase.MAIN
                        )
                        .filter(
                                MonthCommandStemRelation
                                        ::exposed
                        )
                        .map(
                                relation ->
                                        createCandidate(
                                                monthCommand,
                                                relation
                                        )
                        )
                        .toList();

        return new MonthCommandPatternCandidateAnalysis(
                dayMaster,
                monthCommand.monthBranch(),
                primaryCandidate,
                exposedAlternativeCandidates
        );
    }

    private MonthCommandPatternCandidate createCandidate(

            MonthCommandDayMasterAnalysis monthCommand,

            MonthCommandStemRelation relation

    ) {

        HeavenlyStem dayMaster =
                monthCommand.dayMaster();

        HeavenlyStem hiddenStem =
                relation.hiddenStem();

        MonthCommandPatternCandidateType candidateType =
                candidateTypeOf(
                        dayMaster,
                        hiddenStem,
                        relation.relationType()
                );

        return new MonthCommandPatternCandidate(
                dayMaster,
                monthCommand.monthBranch(),
                hiddenStem,
                relation.phase(),
                relation.relationType(),
                candidateType,
                relation.exposureHits()
        );
    }

    /**
     * 오행 생극관계
     * +
     * 일간/대상 천간 음양
     *
     * 으로 정확한 십성 계열을 구분하고
     * 격국 후보 타입으로 변환한다.
     *
     * 기존 TenGodCalculator와 같은
     * 결정론적 십성 원리를 사용하지만
     * 해당 클래스의 알 수 없는 API를
     * 추측해서 의존하지 않는다.
     */
    public MonthCommandPatternCandidateType candidateTypeOf(

            HeavenlyStem dayMaster,

            HeavenlyStem targetStem,

            DayMasterRelationType relationType

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                targetStem,
                "대상 천간은 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "일간-대상 관계는 필수입니다."
        );

        boolean samePolarity =
                isYang(
                        dayMaster
                )
                        ==
                        isYang(
                                targetStem
                        );

        return switch (relationType) {

            /*
             * 인성
             *
             * 같은 음양 → 편인
             * 다른 음양 → 정인
             */
            case GENERATES_DAY_MASTER -> {

                if (samePolarity) {

                    yield MonthCommandPatternCandidateType
                            .INDIRECT_RESOURCE;
                }

                yield MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE;
            }

            /*
             * 비겁
             *
             * 같은 음양 → 비견
             * 다른 음양 → 겁재
             */
            case SAME_ELEMENT -> {

                if (samePolarity) {

                    yield MonthCommandPatternCandidateType
                            .MONTH_COMPANION;
                }

                yield MonthCommandPatternCandidateType
                        .MONTH_ROB_WEALTH;
            }

            /*
             * 식상
             *
             * 같은 음양 → 식신
             * 다른 음양 → 상관
             */
            case GENERATED_BY_DAY_MASTER -> {

                if (samePolarity) {

                    yield MonthCommandPatternCandidateType
                            .EATING_GOD;
                }

                yield MonthCommandPatternCandidateType
                        .HURTING_OFFICER;
            }

            /*
             * 재성
             *
             * 같은 음양 → 편재
             * 다른 음양 → 정재
             */
            case CONTROLLED_BY_DAY_MASTER -> {

                if (samePolarity) {

                    yield MonthCommandPatternCandidateType
                            .INDIRECT_WEALTH;
                }

                yield MonthCommandPatternCandidateType
                        .DIRECT_WEALTH;
            }

            /*
             * 관살
             *
             * 같은 음양 → 편관/칠살
             * 다른 음양 → 정관
             */
            case CONTROLS_DAY_MASTER -> {

                if (samePolarity) {

                    yield MonthCommandPatternCandidateType
                            .SEVEN_KILLINGS;
                }

                yield MonthCommandPatternCandidateType
                        .DIRECT_OFFICER;
            }
        };
    }

    private boolean isYang(
            HeavenlyStem stem
    ) {

        return switch (stem) {

            case JIA,
                 BING,
                 WU,
                 GENG,
                 REN ->
                    true;

            case YI,
                 DING,
                 JI,
                 XIN,
                 GUI ->
                    false;
        };
    }
}