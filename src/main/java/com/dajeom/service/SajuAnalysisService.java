package com.dajeom.service;

import com.dajeom.calculator.KoreanLunarCalendarCalculator;
import com.dajeom.constant.SajuConstants;
import com.dajeom.dto.AnnualLuckResponse;
import com.dajeom.dto.ConsultationSectionResponse;
import com.dajeom.dto.DecadeLuckResponse;
import com.dajeom.dto.PillarResponse;
import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.engine.AnnualLuckAnalysisEngine;
import com.dajeom.engine.ConsultationAnalysisEngine;
import com.dajeom.engine.DecadeLuckAnalysisEngine;
import com.dajeom.engine.FourPillarsCalculationEngine;
import com.dajeom.engine.InterpretationSignalAnalysisEngine;
import com.dajeom.engine.LuckInteractionAnalysisEngine;
import com.dajeom.engine.NatalChartAdvancedAnalysisEngine;
import com.dajeom.engine.NatalChartAnalysisEngine;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.BirthDateValue;
import com.dajeom.model.ConsultationAnalysis;
import com.dajeom.model.ConsultationSection;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.FourPillars;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.LuckStart;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import com.dajeom.type.CalendarType;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 사주 전체 분석 Application Service.
 *
 * 처리 흐름:
 *
 * Request
 *      ↓
 * 양력/음력 정규화
 *      ↓
 * NormalizedBirthData
 *      ↓
 * FourPillars
 *      ↓
 * 원국 분석
 *      ↓
 * 대운
 *      ↓
 * 세운
 *      ↓
 * 운 상호작용
 *      ↓
 * Interpretation
 *      ↓
 * Consultation
 *      ↓
 * Response
 */
@Service
public class SajuAnalysisService {

    private final KoreanLunarCalendarCalculator
            koreanLunarCalendarCalculator;

    private final FourPillarsCalculationEngine
            fourPillarsCalculationEngine;

    private final NatalChartAnalysisEngine
            natalChartAnalysisEngine;

    private final NatalChartAdvancedAnalysisEngine
            natalChartAdvancedAnalysisEngine;

    private final DecadeLuckAnalysisEngine
            decadeLuckAnalysisEngine;

    private final AnnualLuckAnalysisEngine
            annualLuckAnalysisEngine;

    private final LuckInteractionAnalysisEngine
            luckInteractionAnalysisEngine;

    private final InterpretationSignalAnalysisEngine
            interpretationSignalAnalysisEngine;

    private final ConsultationAnalysisEngine
            consultationAnalysisEngine;

    public SajuAnalysisService(
            KoreanLunarCalendarCalculator
                    koreanLunarCalendarCalculator,
            FourPillarsCalculationEngine
                    fourPillarsCalculationEngine,
            NatalChartAnalysisEngine
                    natalChartAnalysisEngine,
            NatalChartAdvancedAnalysisEngine
                    natalChartAdvancedAnalysisEngine,
            DecadeLuckAnalysisEngine
                    decadeLuckAnalysisEngine,
            AnnualLuckAnalysisEngine
                    annualLuckAnalysisEngine,
            LuckInteractionAnalysisEngine
                    luckInteractionAnalysisEngine,
            InterpretationSignalAnalysisEngine
                    interpretationSignalAnalysisEngine,
            ConsultationAnalysisEngine
                    consultationAnalysisEngine
    ) {

        this.koreanLunarCalendarCalculator =
                koreanLunarCalendarCalculator;

        this.fourPillarsCalculationEngine =
                fourPillarsCalculationEngine;

        this.natalChartAnalysisEngine =
                natalChartAnalysisEngine;

        this.natalChartAdvancedAnalysisEngine =
                natalChartAdvancedAnalysisEngine;

        this.decadeLuckAnalysisEngine =
                decadeLuckAnalysisEngine;

        this.annualLuckAnalysisEngine =
                annualLuckAnalysisEngine;

        this.luckInteractionAnalysisEngine =
                luckInteractionAnalysisEngine;

        this.interpretationSignalAnalysisEngine =
                interpretationSignalAnalysisEngine;

        this.consultationAnalysisEngine =
                consultationAnalysisEngine;
    }

    public SajuAnalysisResponse analyze(
            SajuAnalysisRequest request
    ) {

        Objects.requireNonNull(
                request,
                "사주 분석 요청은 필수입니다."
        );

        /*
         * 1.
         * 양력/음력을 실제 계산용 양력 날짜로
         * 정규화한다.
         */
        NormalizedBirthData birthData =
                normalizeBirthData(
                        request
                );

        /*
         * 2.
         * 사주 네 기둥.
         */
        FourPillars fourPillars =
                fourPillarsCalculationEngine
                        .calculate(
                                birthData
                        );

        /*
         * 3.
         * 원국 기본 분석.
         */
        NatalChartAnalysis natalChart =
                natalChartAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        /*
         * 4.
         * 격국 / 조후 / 합화 / 통관 /
         * 용신 후보 분석.
         */
        NatalChartAdvancedAnalysis
                advancedAnalysis =
                natalChartAdvancedAnalysisEngine
                        .analyze(
                                natalChart
                        );

        /*
         * 현재 대운 조회 기준시각.
         */
        ZonedDateTime targetDateTime =
                ZonedDateTime.now(
                        birthData.zoneId()
                );

        /*
         * 5.
         * 대운.
         */
        DecadeLuckAnalysis decadeLuckAnalysis =
                decadeLuckAnalysisEngine
                        .analyze(
                                birthData,
                                fourPillars,
                                targetDateTime
                        );

        /*
         * 6.
         * 세운.
         */
        AnnualLuckAnalysis annualLuckAnalysis =
                annualLuckAnalysisEngine
                        .analyze(
                                birthData,
                                fourPillars,
                                request.targetYear()
                        );

        /*
         * 7.
         * 원국 ↔ 대운 ↔ 세운.
         */
        LuckInteractionAnalysis
                luckInteractionAnalysis =
                luckInteractionAnalysisEngine
                        .analyze(
                                advancedAnalysis,
                                decadeLuckAnalysis,
                                annualLuckAnalysis
                        );

        /*
         * 8.
         * 11개 분야 해석 Signal.
         */
        InterpretationSignalAnalysis
                signalAnalysis =
                interpretationSignalAnalysisEngine
                        .analyze(
                                advancedAnalysis,
                                luckInteractionAnalysis
                        );

        /*
         * 9.
         * 사용자용 기본 상담 구조.
         */
        ConsultationAnalysis
                consultationAnalysis =
                consultationAnalysisEngine
                        .analyze(
                                signalAnalysis,
                                luckInteractionAnalysis
                        );

        /*
         * 10.
         * API Response.
         */
        return createResponse(
                birthData,
                fourPillars,
                advancedAnalysis,
                decadeLuckAnalysis,
                annualLuckAnalysis,
                consultationAnalysis,
                request.targetYear()
        );
    }

    /**
     * API 입력값을 실제 계산 가능한
     * NormalizedBirthData로 변환한다.
     */
    private NormalizedBirthData normalizeBirthData(
            SajuAnalysisRequest request
    ) {

        Objects.requireNonNull(
                request.calendarType(),
                "양력/음력 구분은 필수입니다."
        );

        Objects.requireNonNull(
                request.birthDate(),
                "생년월일은 필수입니다."
        );

        Objects.requireNonNull(
                request.gender(),
                "성별은 필수입니다."
        );

        Objects.requireNonNull(
                request.targetYear(),
                "분석 대상 연도는 필수입니다."
        );

        /*
         * 원본 날짜 문자열 파싱.
         *
         * 여기에서는 Gregorian 규칙으로
         * 날짜를 확정하지 않는다.
         */
        BirthDateValue originalBirthDate =
                BirthDateValue.parse(
                        request.birthDate()
                );

        if (
                request.birthTimeUnknown()
                        && request.birthTime() != null
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상인 경우 birthTime은 비워야 합니다."
            );
        }

        if (
                !request.birthTimeUnknown()
                        && request.birthTime() == null
        ) {

            throw new IllegalArgumentException(
                    "출생시간을 알고 있는 경우 birthTime은 필수입니다."
            );
        }

        LocalDate solarBirthDate;

        if (
                request.calendarType()
                        == CalendarType.SOLAR
        ) {

            if (request.leapMonth()) {

                throw new IllegalArgumentException(
                        "양력 입력에는 윤달을 사용할 수 없습니다."
                );
            }

            /*
             * 양력은 Gregorian 규칙으로
             * 그대로 검증한다.
             */
            solarBirthDate =
                    originalBirthDate
                            .toGregorianDate();

        } else {

            /*
             * 음력은 한국 음양력 데이터로
             * 실제 양력 날짜를 얻는다.
             *
             * leapMonth:
             *
             * false = 평달
             * true  = 윤달
             */
            solarBirthDate =
                    koreanLunarCalendarCalculator
                            .toSolarDate(
                                    originalBirthDate,
                                    request.leapMonth()
                            );
        }

        /*
         * 미래 출생일 방지.
         *
         * 반드시 변환 완료된 실제 양력일을 기준으로 한다.
         */
        LocalDate today =
                LocalDate.now(
                        SajuConstants.DEFAULT_ZONE_ID
                );

        if (
                solarBirthDate.isAfter(
                        today
                )
        ) {

            throw new IllegalArgumentException(
                    "생년월일은 미래 날짜일 수 없습니다."
            );
        }

        return new NormalizedBirthData(
                request.calendarType(),
                originalBirthDate,
                solarBirthDate,
                request.birthTime(),
                request.birthTimeUnknown(),
                request.gender(),
                request.leapMonth(),
                SajuConstants.DEFAULT_ZONE_ID
        );
    }

    private SajuAnalysisResponse createResponse(
            NormalizedBirthData birthData,
            FourPillars fourPillars,
            NatalChartAdvancedAnalysis advancedAnalysis,
            DecadeLuckAnalysis decadeLuckAnalysis,
            AnnualLuckAnalysis annualLuckAnalysis,
            ConsultationAnalysis consultationAnalysis,
            int targetYear
    ) {

        return new SajuAnalysisResponse(
                birthData.calendarType(),

                birthData
                        .birthDate()
                        .isoFormat(),

                birthData.solarBirthDate(),

                birthData.birthTime(),

                birthData.birthTimeUnknown(),

                birthData.gender(),

                birthData.leapMonth(),

                targetYear,

                toPillarResponse(
                        fourPillars.yearPillar()
                ),

                toPillarResponse(
                        fourPillars.monthPillar()
                ),

                toPillarResponse(
                        fourPillars.dayPillar()
                ),

                fourPillars.hasHourPillar()
                        ? toPillarResponse(
                        fourPillars.hourPillar()
                )
                        : null,

                fourPillars
                        .dayMaster()
                        .getKoreanName(),

                fourPillars
                        .dayMaster()
                        .getChineseCharacter(),

                advancedAnalysis
                        .coreAnalysis()
                        .strengthAnalysis()
                        .level()
                        .getKoreanName(),

                advancedAnalysis
                        .structureAnalysis()
                        .type()
                        .getKoreanName(),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .yongshinCandidates()
                ),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .huishinCandidates()
                ),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .gishinCandidates()
                ),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .temperatureCandidates()
                ),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .mediationCandidates()
                ),

                toDecadeLuckResponse(
                        decadeLuckAnalysis
                ),

                toAnnualLuckResponse(
                        annualLuckAnalysis
                ),

                consultationAnalysis
                        .sections()
                        .stream()
                        .map(
                                this::toConsultationResponse
                        )
                        .toList(),

                consultationAnalysis
                        .fullNarrationText()
        );
    }

    private PillarResponse toPillarResponse(
            Pillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "간지는 필수입니다."
        );

        return new PillarResponse(
                pillar.koreanName(),
                pillar.chineseName(),

                pillar
                        .stem()
                        .getKoreanName(),

                pillar
                        .stem()
                        .getChineseCharacter(),

                pillar
                        .stem()
                        .getElement(),

                pillar
                        .stem()
                        .getYinYang(),

                pillar
                        .branch()
                        .getKoreanName(),

                pillar
                        .branch()
                        .getChineseCharacter(),

                pillar
                        .branch()
                        .getElement(),

                pillar
                        .branch()
                        .getYinYang()
        );
    }

    private DecadeLuckResponse
    toDecadeLuckResponse(
            DecadeLuckAnalysis analysis
    ) {

        LuckStart luckStart =
                analysis
                        .optionalLuckStart()
                        .orElse(null);

        DecadeLuckPeriod current =
                analysis
                        .optionalCurrentPeriod()
                        .orElse(null);

        return new DecadeLuckResponse(
                analysis
                        .direction()
                        .getKoreanName(),

                analysis
                        .calculationAvailable(),

                luckStart == null
                        ? null
                        : luckStart
                        .startAge()
                        .displayName(),

                luckStart == null
                        ? null
                        : luckStart
                        .startDateTime(),

                current == null
                        ? null
                        : current.order(),

                current == null
                        ? null
                        : toPillarResponse(
                        current.pillar()
                ),

                current == null
                        ? null
                        : current
                        .startAge()
                        .displayName(),

                current == null
                        ? null
                        : current
                        .endAge()
                        .displayName(),

                current == null
                        ? null
                        : current.startDateTime(),

                current == null
                        ? null
                        : current.endDateTime(),

                analysis
                        .isBeforeFirstDecadeLuck(),

                analysis
                        .unavailableReason()
        );
    }

    private AnnualLuckResponse
    toAnnualLuckResponse(
            AnnualLuckAnalysis analysis
    ) {

        return new AnnualLuckResponse(
                analysis.targetYear(),

                toPillarResponse(
                        analysis.pillar()
                ),

                analysis.startDateTime(),

                analysis.endDateTime(),

                analysis
                        .stemTenGod()
                        .getKoreanName(),

                analysis
                        .branchMainStem()
                        .getKoreanName(),

                analysis
                        .branchMainStem()
                        .getChineseCharacter(),

                analysis
                        .branchMainTenGod()
                        .getKoreanName()
        );
    }

    private ConsultationSectionResponse
    toConsultationResponse(
            ConsultationSection section
    ) {

        return new ConsultationSectionResponse(
                section.order(),
                section.category(),
                section.title(),
                section.polarity(),
                section.intensity(),
                section.confidence(),
                section.openingMessage(),
                section.summaryMessage(),
                section.guidanceMessage(),
                section.narrationText(),
                section.basisLabels(),
                section.evidenceCount()
        );
    }

    private List<String> toElementNames(
            List<FiveElement> elements
    ) {

        return elements
                .stream()
                .map(
                        FiveElement::getKoreanName
                )
                .toList();
    }
}