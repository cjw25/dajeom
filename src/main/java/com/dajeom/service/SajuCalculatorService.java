package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuAnalysisResponse.BalanceItemResponse;
import com.dajeom.dto.SajuAnalysisResponse.CalendarResponse;
import com.dajeom.dto.SajuAnalysisResponse.DecadeFortuneResponse;
import com.dajeom.dto.SajuAnalysisResponse.ElementBalanceResponse;
import com.dajeom.dto.SajuAnalysisResponse.HiddenStemResponse;
import com.dajeom.dto.SajuAnalysisResponse.InterpretationResponse;
import com.dajeom.dto.SajuAnalysisResponse.LuckResponse;
import com.dajeom.dto.SajuAnalysisResponse.PillarResponse;
import com.dajeom.dto.SajuAnalysisResponse.RelationItemResponse;
import com.dajeom.dto.SajuAnalysisResponse.RelationResponse;
import com.dajeom.dto.SajuAnalysisResponse.ShinsalResponse;
import com.dajeom.dto.SajuAnalysisResponse.StrengthResponse;
import com.dajeom.dto.SajuAnalysisResponse.StructureResponse;
import com.dajeom.dto.SajuAnalysisResponse.TenGodBalanceResponse;
import com.dajeom.dto.SajuAnalysisResponse.YearFortuneResponse;
import com.dajeom.dto.SajuAnalysisResponse.YinYangBalanceResponse;
import com.dajeom.entity.BirthProfile;
import com.dajeom.entity.CalendarType;
import com.dajeom.entity.Gender;
import com.dajeom.entity.SajuAnalysis;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SajuCalculatorService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private static final String[] STEMS = {
            "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
    };

    private static final String[] STEMS_KOREAN = {
            "갑", "을", "병", "정", "무", "기", "경", "신", "임", "계"
    };

    private static final String[] BRANCHES = {
            "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
    };

    private static final String[] BRANCHES_KOREAN = {
            "자", "축", "인", "묘", "진", "사", "오", "미", "신", "유", "술", "해"
    };

    private static final String[] STEM_ELEMENTS = {
            "목", "목", "화", "화", "토", "토", "금", "금", "수", "수"
    };

    private static final String[] BRANCH_ELEMENTS = {
            "수", "토", "목", "목", "토", "화", "화", "토", "금", "금", "토", "수"
    };

    private static final String[] STEM_YIN_YANG = {
            "양", "음", "양", "음", "양", "음", "양", "음", "양", "음"
    };

    private static final String[] BRANCH_YIN_YANG = {
            "양", "음", "양", "음", "양", "음", "양", "음", "양", "음", "양", "음"
    };

    private static final String[] ZODIACS = {
            "쥐", "소", "호랑이", "토끼", "용", "뱀", "말", "양", "원숭이", "닭", "개", "돼지"
    };

    private static final List<String> ELEMENT_ORDER =
            List.of("목", "화", "토", "금", "수");

    private static final List<String> TEN_GOD_ORDER =
            List.of(
                    "비견", "겁재", "식신", "상관", "편재",
                    "정재", "편관", "정관", "편인", "정인"
            );

    private static final String[] TWELVE_STAGES = {
            "장생", "목욕", "관대", "건록", "제왕", "쇠",
            "병", "사", "묘", "절", "태", "양"
    };

    private static final int[] TWELVE_STAGE_START_BRANCH = {
            11, 6, 2, 9, 2, 9, 5, 0, 8, 3
    };

    private static final boolean[] TWELVE_STAGE_FORWARD = {
            true, false, true, false, true, false, true, false, true, false
    };

    private static final String[] NA_YIN = {
            "해중금", "노중화", "대림목", "노방토", "검봉금",
            "산두화", "간하수", "성두토", "백랍금", "양류목",
            "천중수", "옥상토", "벽력화", "송백목", "장류수",
            "사중금", "산하화", "평지목", "벽상토", "금박금",
            "복등화", "천하수", "대역토", "채천금", "상자목",
            "대계수", "사중토", "천상화", "석류목", "대해수"
    };

    private static final Map<String, List<Integer>> HIDDEN_STEMS = createHiddenStemMap();

    private static final Map<String, String> PRODUCES = Map.of(
            "목", "화",
            "화", "토",
            "토", "금",
            "금", "수",
            "수", "목"
    );

    private static final Map<String, String> CONTROLS = Map.of(
            "목", "토",
            "토", "수",
            "수", "화",
            "화", "금",
            "금", "목"
    );

    private static final Map<String, String> STEM_COMBINATIONS = Map.of(
            "甲己", "갑기합",
            "乙庚", "을경합",
            "丙辛", "병신합",
            "丁壬", "정임합",
            "戊癸", "무계합"
    );

    private static final Map<String, String> STEM_CLASHES = Map.of(
            "甲庚", "갑경충",
            "乙辛", "을신충",
            "丙壬", "병임충",
            "丁癸", "정계충"
    );

    private static final Map<String, String> BRANCH_COMBINATIONS = Map.of(
            "子丑", "자축합",
            "寅亥", "인해합",
            "卯戌", "묘술합",
            "辰酉", "진유합",
            "巳申", "사신합",
            "午未", "오미합"
    );

    private static final Map<String, String> BRANCH_CLASHES = Map.of(
            "子午", "자오충",
            "丑未", "축미충",
            "寅申", "인신충",
            "卯酉", "묘유충",
            "辰戌", "진술충",
            "巳亥", "사해충"
    );

    private static final Map<String, String> BRANCH_HARMS = Map.of(
            "子未", "자미해",
            "丑午", "축오해",
            "寅巳", "인사해",
            "卯辰", "묘진해",
            "申亥", "신해해",
            "酉戌", "유술해"
    );

    private static final Map<String, String> BRANCH_BREAKS = Map.of(
            "子酉", "자유파",
            "丑辰", "축진파",
            "寅亥", "인해파",
            "卯午", "묘오파",
            "巳申", "사신파",
            "未戌", "미술파"
    );

    private static final long[] SOLAR_TERM_MINUTES = {
            0L, 21208L, 42467L, 63836L, 85337L, 107014L,
            128867L, 150921L, 173149L, 195551L, 218072L, 240693L,
            263343L, 285989L, 308563L, 331033L, 353350L, 375494L,
            397447L, 419210L, 440795L, 462224L, 483532L, 504758L
    };

    private static final Instant SOLAR_TERM_BASE =
            LocalDateTime.of(1900, 1, 6, 2, 5)
                    .toInstant(ZoneOffset.UTC);

    private static final double TROPICAL_YEAR_MILLIS = 31_556_925_974.7;

    private final KoreanLunarConverter lunarConverter = new KoreanLunarConverter();

    public SajuAnalysisResponse calculate(SajuAnalysis analysis) {
        BirthProfile profile = analysis.getBirthProfile();

        KoreanLunarConverter.Result calendar = resolveCalendar(profile);

        LocalTime inputTime = profile.isBirthTimeUnknown()
                ? LocalTime.NOON
                : profile.getBirthTime();

        LocalDateTime birthDateTime = LocalDateTime.of(
                calendar.solarDate(),
                inputTime
        );

        Chart chart = calculateChart(
                birthDateTime,
                profile.isBirthTimeUnknown()
        );

        List<PillarResponse> pillars = buildPillarResponses(chart);
        ElementBalanceResponse elementBalance = buildElementBalance(pillars);
        YinYangBalanceResponse yinYangBalance = buildYinYangBalance(pillars);
        TenGodBalanceResponse tenGodBalance = buildTenGodBalance(pillars);
        StrengthResponse strength = buildStrength(chart.dayPillar().stemIndex(), elementBalance);
        StructureResponse structure = buildStructure(chart, calendar, profile.isBirthTimeUnknown());
        RelationResponse relations = buildRelations(chart);
        List<ShinsalResponse> shinsal = buildShinsal(chart);
        LuckResponse luck = buildLuck(profile, chart);
        List<InterpretationResponse> interpretations = buildInterpretations(
                profile,
                chart,
                elementBalance,
                tenGodBalance,
                strength
        );

        CalendarResponse calendarResponse = new CalendarResponse(
                profile.getCalendarType() == CalendarType.SOLAR ? "양력" : "음력",
                calendar.solarDate().toString(),
                calendar.lunarDate()
                        + (calendar.leapMonth() ? " (윤달)" : " (평달)"),
                calendar.leapMonth(),
                profile.isBirthTimeUnknown() ? "시간 모름" : profile.getBirthTime().toString(),
                profile.isBirthTimeUnknown(),
                profile.isBirthTimeUnknown()
                        ? "출생시간 미상 · 시주 제외"
                        : "입력한 한국 표준시 기준",
                "내장 한국 음양력 데이터(1900~2050) · 절입 근사시각 · 야자시 당일 기준"
        );

        String dayMaster =
                STEMS_KOREAN[chart.dayPillar().stemIndex()]
                        + "(" + STEMS[chart.dayPillar().stemIndex()] + ") "
                        + STEM_ELEMENTS[chart.dayPillar().stemIndex()];

        String summary =
                profile.getName()
                        + "님의 일간은 "
                        + dayMaster
                        + "이며, 오행에서는 "
                        + elementBalance.dominant()
                        + " 기운이 가장 두드러지고 "
                        + elementBalance.weakest()
                        + " 기운이 상대적으로 적습니다. "
                        + "오행 개수 기반 보조 판정은 "
                        + strength.classification()
                        + "입니다.";

        return new SajuAnalysisResponse(
                analysis.getId(),
                profile.getId(),
                analysis.getStatus().name(),
                profile.getName(),
                calendarResponse,
                ZODIACS[chart.yearPillar().branchIndex()],
                dayMaster,
                summary,
                pillars,
                elementBalance,
                yinYangBalance,
                tenGodBalance,
                structure,
                strength,
                relations,
                shinsal,
                luck,
                interpretations,
                "이 결과는 내장 데이터와 절기 근사식으로 계산한 만세력 자료입니다. "
                        + "서비스 출시 전 경계 시각 출생자와 음력 윤달 사례는 한국천문연구원 기준 자료로 교차 검증해야 합니다. "
                        + "신강·신약, 용신과 신살은 학파에 따라 해석이 달라질 수 있습니다.",
                analysis.getCompletedAt()
        );
    }

    /**
     * 미래 운세 계산에 필요한 원국·용희기신·현재 대운 자료를 구조화해서 반환한다.
     *
     * SajuForecastService가 SajuAnalysisResponse를 JSON으로 다시 검색하지 않고
     * 현재 만세력 계산 결과를 직접 사용할 수 있도록 제공하는 전용 메서드다.
     */
    public ForecastBaseData calculateForecastBaseData(
            SajuAnalysis analysis
    ) {
        if (analysis == null) {
            throw new IllegalArgumentException("사주 분석 정보가 없습니다.");
        }

        BirthProfile profile = analysis.getBirthProfile();

        if (profile == null) {
            throw new IllegalArgumentException("사주 기본정보를 찾을 수 없습니다.");
        }

        Chart chart = calculateBirthChart(profile);

        List<PillarResponse> pillars = buildPillarResponses(chart);
        ElementBalanceResponse elementBalance = buildElementBalance(pillars);
        StrengthResponse strength = buildStrength(
                chart.dayPillar().stemIndex(),
                elementBalance
        );

        List<String> natalStems = chart.knownPillars()
                .stream()
                .map(item -> item.pillar().stem())
                .toList();

        List<String> natalBranches = chart.knownPillars()
                .stream()
                .map(item -> item.pillar().branch())
                .toList();

        List<ForecastNatalPillarData> natalPillars = chart.knownPillars()
                .stream()
                .map(item -> new ForecastNatalPillarData(
                        item.label(),
                        item.pillar().ganZhi(),
                        item.pillar().ganZhiKorean(),
                        item.pillar().stem(),
                        item.pillar().branch(),
                        STEM_ELEMENTS[item.pillar().stemIndex()],
                        BRANCH_ELEMENTS[item.pillar().branchIndex()],
                        tenGod(
                                chart.dayPillar().stemIndex(),
                                item.pillar().stemIndex(),
                                "일주".equals(item.label())
                        )
                ))
                .toList();

        ForecastLuckData currentDaeun = calculateCurrentForecastLuckData(
                profile,
                chart,
                LocalDate.now(KOREA_ZONE).getYear()
        );

        return new ForecastBaseData(
                profile.getId(),
                chart.dayPillar().stem(),
                chart.dayPillar().branch(),
                STEM_ELEMENTS[chart.dayPillar().stemIndex()],
                List.copyOf(natalStems),
                List.copyOf(natalBranches),
                natalPillars,
                strength.classification(),
                strength.usefulGod(),
                strength.favorableGod(),
                strength.unfavorableGod(),
                currentDaeun,
                profile.isBirthTimeUnknown()
        );
    }

    /**
     * 입춘부터 다음 해 입춘 직전까지를 한 해의 세운 적용 기간으로 계산한다.
     */
    public ForecastPillarData calculateYearForecastPillar(
            SajuAnalysis analysis,
            int year
    ) {
        validateForecastYear(year);

        if (analysis == null || analysis.getBirthProfile() == null) {
            throw new IllegalArgumentException("사주 분석 기본정보가 없습니다.");
        }

        Chart birthChart = calculateBirthChart(
                analysis.getBirthProfile()
        );

        PillarData yearPillar = pillarFromCycleIndex(
                Math.floorMod(year - 4, 60)
        );

        LocalDateTime periodStart = solarTerm(year, 2);
        LocalDateTime periodEnd = solarTerm(year + 1, 2)
                .minusNanos(1);

        return new ForecastPillarData(
                year,
                null,
                "입춘",
                yearPillar.ganZhi(),
                yearPillar.ganZhiKorean(),
                yearPillar.stem(),
                yearPillar.branch(),
                STEM_ELEMENTS[yearPillar.stemIndex()],
                BRANCH_ELEMENTS[yearPillar.branchIndex()],
                tenGod(
                        birthChart.dayPillar().stemIndex(),
                        yearPillar.stemIndex(),
                        false
                ),
                periodStart,
                periodEnd
        );
    }

    /**
     * 특정 양력 월에 시작되는 절기월의 월주와 실제 절입 기간을 계산한다.
     *
     * 1월은 소한, 2월은 입춘, 3월은 경칩 순으로 시작하며
     * 일반 달력의 1일부터 말일까지가 아니라 절기 시각을 기준으로 한다.
     */
    public ForecastPillarData calculateMonthForecastPillar(
            SajuAnalysis analysis,
            int year,
            int month
    ) {
        validateForecastYear(year);

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("조회 월은 1월부터 12월 사이여야 합니다.");
        }

        if (analysis == null || analysis.getBirthProfile() == null) {
            throw new IllegalArgumentException("사주 분석 기본정보가 없습니다.");
        }

        Chart birthChart = calculateBirthChart(
                analysis.getBirthProfile()
        );

        int startTermIndex = (month - 1) * 2;
        LocalDateTime periodStart = solarTerm(
                year,
                startTermIndex
        );

        LocalDateTime nextPeriodStart = month == 12
                ? solarTerm(year + 1, 0)
                : solarTerm(year, startTermIndex + 2);

        LocalDateTime periodEnd = nextPeriodStart.minusNanos(1);

        Chart periodChart = calculateChart(
                periodStart.plusSeconds(1),
                true
        );

        PillarData monthPillar = periodChart.monthPillar();

        return new ForecastPillarData(
                year,
                month,
                solarTermName(startTermIndex),
                monthPillar.ganZhi(),
                monthPillar.ganZhiKorean(),
                monthPillar.stem(),
                monthPillar.branch(),
                STEM_ELEMENTS[monthPillar.stemIndex()],
                BRANCH_ELEMENTS[monthPillar.branchIndex()],
                tenGod(
                        birthChart.dayPillar().stemIndex(),
                        monthPillar.stemIndex(),
                        false
                ),
                periodStart,
                periodEnd
        );
    }

    /**
     * 외부 서비스가 절기 경계 시각을 직접 조회할 때 사용한다.
     */
    public LocalDateTime calculateSolarTerm(
            int year,
            int termIndex
    ) {
        return solarTerm(year, termIndex);
    }

    private Chart calculateBirthChart(
            BirthProfile profile
    ) {
        KoreanLunarConverter.Result calendar = resolveCalendar(profile);

        LocalTime inputTime = profile.isBirthTimeUnknown()
                ? LocalTime.NOON
                : profile.getBirthTime();

        LocalDateTime birthDateTime = LocalDateTime.of(
                calendar.solarDate(),
                inputTime
        );

        return calculateChart(
                birthDateTime,
                profile.isBirthTimeUnknown()
        );
    }

    private ForecastLuckData calculateCurrentForecastLuckData(
            BirthProfile profile,
            Chart chart,
            int targetYear
    ) {
        boolean yearStemYang = "양".equals(
                STEM_YIN_YANG[chart.yearPillar().stemIndex()]
        );

        boolean male = profile.getGender() == Gender.MALE;
        boolean forward = (male && yearStemYang)
                || (!male && !yearStemYang);

        LocalDateTime targetTerm = forward
                ? nextJie(chart.birthDateTime())
                : previousJie(chart.birthDateTime());

        long differenceMinutes = Math.abs(
                Duration.between(
                        chart.birthDateTime(),
                        targetTerm
                ).toMinutes()
        );

        long totalStartMonths = differenceMinutes / 360L;
        int startYears = (int) (totalStartMonths / 12L);
        int firstStartAge = Math.max(1, startYears);

        for (int index = 0; index < 12; index++) {
            int step = forward
                    ? index + 1
                    : -(index + 1);

            PillarData decadePillar = pillarFromCycleIndex(
                    chart.monthPillar().cycleIndex() + step
            );

            int decadeStartAge = firstStartAge + index * 10;
            int decadeEndAge = decadeStartAge + 9;
            int decadeStartYear = profile.getBirthDate().getYear()
                    + decadeStartAge;
            int decadeEndYear = decadeStartYear + 9;

            if (
                    targetYear >= decadeStartYear
                            && targetYear <= decadeEndYear
            ) {
                return new ForecastLuckData(
                        decadePillar.ganZhi(),
                        decadePillar.ganZhiKorean(),
                        decadePillar.stem(),
                        decadePillar.branch(),
                        STEM_ELEMENTS[decadePillar.stemIndex()],
                        BRANCH_ELEMENTS[decadePillar.branchIndex()],
                        tenGod(
                                chart.dayPillar().stemIndex(),
                                decadePillar.stemIndex(),
                                false
                        ),
                        decadeStartAge,
                        decadeEndAge,
                        decadeStartYear,
                        decadeEndYear,
                        forward ? "순행" : "역행"
                );
            }
        }

        return null;
    }

    private void validateForecastYear(
            int year
    ) {
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException(
                    "미래 운세 계산 지원 연도는 1900년부터 2100년까지입니다."
            );
        }
    }

    private String solarTermName(
            int termIndex
    ) {
        return switch (termIndex) {
            case 0 -> "소한";
            case 1 -> "대한";
            case 2 -> "입춘";
            case 3 -> "우수";
            case 4 -> "경칩";
            case 5 -> "춘분";
            case 6 -> "청명";
            case 7 -> "곡우";
            case 8 -> "입하";
            case 9 -> "소만";
            case 10 -> "망종";
            case 11 -> "하지";
            case 12 -> "소서";
            case 13 -> "대서";
            case 14 -> "입추";
            case 15 -> "처서";
            case 16 -> "백로";
            case 17 -> "추분";
            case 18 -> "한로";
            case 19 -> "상강";
            case 20 -> "입동";
            case 21 -> "소설";
            case 22 -> "대설";
            case 23 -> "동지";
            default -> throw new IllegalArgumentException("절기 번호가 올바르지 않습니다.");
        };
    }

    private KoreanLunarConverter.Result resolveCalendar(BirthProfile profile) {
        if (profile.getCalendarType() == CalendarType.LUNAR) {
            return lunarConverter.fromLunar(
                    profile.getBirthDate(),
                    profile.isLunarLeapMonth()
            );
        }

        return lunarConverter.fromSolar(profile.getBirthDate());
    }

    private Chart calculateChart(
            LocalDateTime birthDateTime,
            boolean timeUnknown
    ) {
        LocalDateTime lichun = solarTerm(birthDateTime.getYear(), 2);

        int pillarYear = birthDateTime.isBefore(lichun)
                ? birthDateTime.getYear() - 1
                : birthDateTime.getYear();

        int yearCycleIndex = Math.floorMod(pillarYear - 4, 60);
        PillarData yearPillar = pillarFromCycleIndex(yearCycleIndex);

        int monthOffset = findMonthOffset(birthDateTime, pillarYear);
        int firstMonthStem = ((yearPillar.stemIndex() % 5) * 2 + 2) % 10;
        int monthStem = Math.floorMod(firstMonthStem + monthOffset, 10);
        int monthBranch = Math.floorMod(2 + monthOffset, 12);
        PillarData monthPillar = pillarFromStemBranch(monthStem, monthBranch);

        int dayCycleIndex = dayCycleIndex(birthDateTime.toLocalDate());
        PillarData dayPillar = pillarFromCycleIndex(dayCycleIndex);

        PillarData timePillar = null;
        if (!timeUnknown) {
            int hour = birthDateTime.getHour();
            int timeBranch = Math.floorMod((hour + 1) / 2, 12);
            int firstTimeStem = (dayPillar.stemIndex() % 5) * 2;
            int timeStem = Math.floorMod(firstTimeStem + timeBranch, 10);
            timePillar = pillarFromStemBranch(timeStem, timeBranch);
        }

        return new Chart(
                birthDateTime,
                yearPillar,
                monthPillar,
                dayPillar,
                timePillar,
                timeUnknown
        );
    }

    private int findMonthOffset(
            LocalDateTime birthDateTime,
            int pillarYear
    ) {
        List<LocalDateTime> boundaries = List.of(
                solarTerm(pillarYear, 2),
                solarTerm(pillarYear, 4),
                solarTerm(pillarYear, 6),
                solarTerm(pillarYear, 8),
                solarTerm(pillarYear, 10),
                solarTerm(pillarYear, 12),
                solarTerm(pillarYear, 14),
                solarTerm(pillarYear, 16),
                solarTerm(pillarYear, 18),
                solarTerm(pillarYear, 20),
                solarTerm(pillarYear, 22),
                solarTerm(pillarYear + 1, 0),
                solarTerm(pillarYear + 1, 2)
        );

        for (int index = 0; index < 12; index++) {
            LocalDateTime start = boundaries.get(index);
            LocalDateTime end = boundaries.get(index + 1);

            if (
                    !birthDateTime.isBefore(start)
                            && birthDateTime.isBefore(end)
            ) {
                return index;
            }
        }

        throw new IllegalArgumentException("월주 절입 구간을 계산할 수 없습니다.");
    }

    private LocalDateTime solarTerm(
            int year,
            int termIndex
    ) {
        if (year < 1900 || year > 2101) {
            throw new IllegalArgumentException("절기 계산 지원 연도는 1900년부터 2101년까지입니다.");
        }

        if (termIndex < 0 || termIndex >= SOLAR_TERM_MINUTES.length) {
            throw new IllegalArgumentException("절기 번호가 올바르지 않습니다.");
        }

        double elapsedMillis =
                TROPICAL_YEAR_MILLIS * (year - 1900)
                        + SOLAR_TERM_MINUTES[termIndex] * 60_000.0;

        Instant instant = SOLAR_TERM_BASE.plusMillis(Math.round(elapsedMillis));

        return LocalDateTime.ofInstant(instant, KOREA_ZONE);
    }

    private int dayCycleIndex(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;

        long julianDayNumber =
                day
                        + (153L * m + 2) / 5
                        + 365L * y
                        + y / 4
                        - y / 100
                        + y / 400
                        - 32045;

        return Math.floorMod((int) ((julianDayNumber + 49) % 60), 60);
    }

    private PillarData pillarFromCycleIndex(int cycleIndex) {
        int normalized = Math.floorMod(cycleIndex, 60);

        return new PillarData(
                normalized,
                normalized % 10,
                normalized % 12
        );
    }

    private PillarData pillarFromStemBranch(
            int stemIndex,
            int branchIndex
    ) {
        for (int index = 0; index < 60; index++) {
            if (
                    index % 10 == Math.floorMod(stemIndex, 10)
                            && index % 12 == Math.floorMod(branchIndex, 12)
            ) {
                return pillarFromCycleIndex(index);
            }
        }

        throw new IllegalArgumentException("천간과 지지 조합이 올바르지 않습니다.");
    }

    private List<PillarResponse> buildPillarResponses(Chart chart) {
        List<PillarResponse> responses = new ArrayList<>();

        responses.add(buildPillarResponse("년주", chart.yearPillar(), chart.dayPillar().stemIndex(), false));
        responses.add(buildPillarResponse("월주", chart.monthPillar(), chart.dayPillar().stemIndex(), false));
        responses.add(buildPillarResponse("일주", chart.dayPillar(), chart.dayPillar().stemIndex(), true));

        if (chart.timeUnknown()) {
            responses.add(new PillarResponse(
                    "시주",
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of(),
                    null,
                    null,
                    null,
                    null,
                    "출생시간을 모르므로 시주는 계산에서 제외했습니다."
            ));
        } else {
            responses.add(buildPillarResponse("시주", chart.timePillar(), chart.dayPillar().stemIndex(), false));
        }

        return responses;
    }

    private PillarResponse buildPillarResponse(
            String type,
            PillarData pillar,
            int dayStemIndex,
            boolean dayPillar
    ) {
        List<HiddenStemResponse> hiddenStemResponses =
                HIDDEN_STEMS.getOrDefault(
                                BRANCHES[pillar.branchIndex()],
                                List.of()
                        )
                        .stream()
                        .map(hiddenStemIndex -> new HiddenStemResponse(
                                STEMS[hiddenStemIndex],
                                STEMS_KOREAN[hiddenStemIndex],
                                STEM_ELEMENTS[hiddenStemIndex],
                                STEM_YIN_YANG[hiddenStemIndex],
                                tenGod(dayStemIndex, hiddenStemIndex, false)
                        ))
                        .toList();

        return new PillarResponse(
                type,
                true,
                pillar.ganZhi(),
                pillar.ganZhiKorean(),
                STEMS[pillar.stemIndex()],
                STEMS_KOREAN[pillar.stemIndex()],
                BRANCHES[pillar.branchIndex()],
                BRANCHES_KOREAN[pillar.branchIndex()],
                STEM_ELEMENTS[pillar.stemIndex()],
                BRANCH_ELEMENTS[pillar.branchIndex()],
                STEM_YIN_YANG[pillar.stemIndex()],
                BRANCH_YIN_YANG[pillar.branchIndex()],
                dayPillar
                        ? "일원"
                        : tenGod(dayStemIndex, pillar.stemIndex(), false),
                hiddenStemResponses,
                twelveStage(dayStemIndex, pillar.branchIndex()),
                NA_YIN[pillar.cycleIndex() / 2],
                xunName(pillar.cycleIndex()),
                xunKong(pillar.cycleIndex()),
                null
        );
    }

    private String tenGod(
            int dayStemIndex,
            int targetStemIndex,
            boolean dayMaster
    ) {
        if (dayMaster) {
            return "일원";
        }

        String dayElement = STEM_ELEMENTS[dayStemIndex];
        String targetElement = STEM_ELEMENTS[targetStemIndex];
        boolean samePolarity =
                Objects.equals(
                        STEM_YIN_YANG[dayStemIndex],
                        STEM_YIN_YANG[targetStemIndex]
                );

        if (Objects.equals(dayElement, targetElement)) {
            return samePolarity ? "비견" : "겁재";
        }

        if (Objects.equals(PRODUCES.get(dayElement), targetElement)) {
            return samePolarity ? "식신" : "상관";
        }

        if (Objects.equals(CONTROLS.get(dayElement), targetElement)) {
            return samePolarity ? "편재" : "정재";
        }

        if (Objects.equals(CONTROLS.get(targetElement), dayElement)) {
            return samePolarity ? "편관" : "정관";
        }

        if (Objects.equals(PRODUCES.get(targetElement), dayElement)) {
            return samePolarity ? "편인" : "정인";
        }

        return "";
    }

    private String twelveStage(
            int dayStemIndex,
            int targetBranchIndex
    ) {
        int start = TWELVE_STAGE_START_BRANCH[dayStemIndex];
        boolean forward = TWELVE_STAGE_FORWARD[dayStemIndex];

        int offset = forward
                ? Math.floorMod(targetBranchIndex - start, 12)
                : Math.floorMod(start - targetBranchIndex, 12);

        return TWELVE_STAGES[offset];
    }

    private String xunName(int cycleIndex) {
        int startIndex = (Math.floorMod(cycleIndex, 60) / 10) * 10;
        return pillarFromCycleIndex(startIndex).ganZhi() + "순";
    }

    private String xunKong(int cycleIndex) {
        int group = Math.floorMod(cycleIndex, 60) / 10;

        return switch (group) {
            case 0 -> "戌亥";
            case 1 -> "申酉";
            case 2 -> "午未";
            case 3 -> "辰巳";
            case 4 -> "寅卯";
            case 5 -> "子丑";
            default -> "";
        };
    }

    private ElementBalanceResponse buildElementBalance(List<PillarResponse> pillars) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        ELEMENT_ORDER.forEach(element -> counts.put(element, 0));

        for (PillarResponse pillar : pillars) {
            if (!pillar.known()) {
                continue;
            }

            counts.computeIfPresent(pillar.stemElement(), (key, value) -> value + 1);
            counts.computeIfPresent(pillar.branchElement(), (key, value) -> value + 1);
        }

        int total = counts.values().stream().mapToInt(Integer::intValue).sum();

        List<BalanceItemResponse> items = ELEMENT_ORDER.stream()
                .map(element -> new BalanceItemResponse(
                        element,
                        counts.get(element),
                        percentage(counts.get(element), total)
                ))
                .toList();

        String dominant = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("없음");

        String weakest = counts.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("없음");

        return new ElementBalanceResponse(items, dominant, weakest, total);
    }

    private YinYangBalanceResponse buildYinYangBalance(List<PillarResponse> pillars) {
        int yang = 0;
        int yin = 0;

        for (PillarResponse pillar : pillars) {
            if (!pillar.known()) {
                continue;
            }

            if ("양".equals(pillar.stemYinYang())) {
                yang++;
            } else {
                yin++;
            }

            if ("양".equals(pillar.branchYinYang())) {
                yang++;
            } else {
                yin++;
            }
        }

        int total = yang + yin;
        String balance = yang == yin
                ? "음양 균형"
                : yang > yin
                ? "양 기운 우세"
                : "음 기운 우세";

        return new YinYangBalanceResponse(
                yang,
                yin,
                percentage(yang, total),
                percentage(yin, total),
                balance
        );
    }

    private TenGodBalanceResponse buildTenGodBalance(List<PillarResponse> pillars) {
        Map<String, Integer> counts = TEN_GOD_ORDER.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        ignored -> 0,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        for (PillarResponse pillar : pillars) {
            if (!pillar.known()) {
                continue;
            }

            if (counts.containsKey(pillar.stemTenGod())) {
                counts.computeIfPresent(pillar.stemTenGod(), (key, value) -> value + 1);
            }

            for (HiddenStemResponse hiddenStem : pillar.hiddenStems()) {
                if (counts.containsKey(hiddenStem.tenGod())) {
                    counts.computeIfPresent(hiddenStem.tenGod(), (key, value) -> value + 1);
                }
            }
        }

        int total = counts.values().stream().mapToInt(Integer::intValue).sum();

        List<BalanceItemResponse> items = TEN_GOD_ORDER.stream()
                .map(name -> new BalanceItemResponse(
                        name,
                        counts.get(name),
                        percentage(counts.get(name), total)
                ))
                .toList();

        String dominant = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("없음");

        return new TenGodBalanceResponse(items, dominant);
    }

    private StrengthResponse buildStrength(
            int dayStemIndex,
            ElementBalanceResponse balance
    ) {
        String dayElement = STEM_ELEMENTS[dayStemIndex];
        String resourceElement = findProducingElement(dayElement);
        String outputElement = PRODUCES.get(dayElement);
        String wealthElement = CONTROLS.get(dayElement);
        String officerElement = findControllingElement(dayElement);

        Map<String, Integer> counts = balance.items().stream()
                .collect(Collectors.toMap(
                        BalanceItemResponse::name,
                        BalanceItemResponse::count
                ));

        int supportCount = counts.getOrDefault(dayElement, 0)
                + counts.getOrDefault(resourceElement, 0);

        int score = balance.totalCount() == 0
                ? 0
                : (int) Math.round(supportCount * 100.0 / balance.totalCount());

        String classification;
        if (score >= 60) {
            classification = "신강";
        } else if (score <= 40) {
            classification = "신약";
        } else {
            classification = "중화";
        }

        String usefulGod;
        String favorableGod;
        String unfavorableGod;

        if ("신약".equals(classification)) {
            usefulGod = resourceElement;
            favorableGod = dayElement;
            unfavorableGod = officerElement;
        } else if ("신강".equals(classification)) {
            usefulGod = weakestCandidate(
                    counts,
                    List.of(outputElement, wealthElement, officerElement)
            );
            favorableGod = outputElement;
            unfavorableGod = dayElement;
        } else {
            usefulGod = balance.weakest();
            favorableGod = weakestCandidate(
                    counts,
                    List.of(resourceElement, outputElement)
            );
            unfavorableGod = balance.dominant();
        }

        return new StrengthResponse(
                score,
                classification,
                dayElement,
                List.of(resourceElement, dayElement),
                List.of(outputElement, wealthElement, officerElement),
                usefulGod,
                favorableGod,
                unfavorableGod,
                "표면 오행 개수로 계산한 보조값입니다. 계절 세력·조후·통근·투간을 포함한 전문 용신 판정과 다를 수 있습니다."
        );
    }

    private StructureResponse buildStructure(
            Chart chart,
            KoreanLunarConverter.Result calendar,
            boolean timeUnknown
    ) {
        PillarData fetalOrigin = pillarFromStemBranch(
                chart.monthPillar().stemIndex() + 1,
                chart.monthPillar().branchIndex() + 3
        );

        PillarData fetalBreath = pillarFromStemBranch(
                combinedStemIndex(chart.dayPillar().stemIndex()),
                combinedBranchIndex(chart.dayPillar().branchIndex())
        );

        PillarData lifePalace = null;
        PillarData bodyPalace = null;

        if (!timeUnknown) {
            int hourBranch = chart.timePillar().branchIndex();
            int lunarMonthOffset = calendar.lunarDate().getMonthValue() - 1;

            int lifeBranch = Math.floorMod(2 - lunarMonthOffset - hourBranch, 12);
            int bodyBranch = Math.floorMod(2 + lunarMonthOffset + hourBranch, 12);

            int firstMonthStem = ((chart.yearPillar().stemIndex() % 5) * 2 + 2) % 10;
            int lifeStem = Math.floorMod(firstMonthStem + lifeBranch - 2, 10);
            int bodyStem = Math.floorMod(firstMonthStem + bodyBranch - 2, 10);

            lifePalace = pillarFromStemBranch(lifeStem, lifeBranch);
            bodyPalace = pillarFromStemBranch(bodyStem, bodyBranch);
        }

        return new StructureResponse(
                fetalOrigin.ganZhi(),
                fetalOrigin.ganZhiKorean(),
                NA_YIN[fetalOrigin.cycleIndex() / 2],
                fetalBreath.ganZhi(),
                fetalBreath.ganZhiKorean(),
                NA_YIN[fetalBreath.cycleIndex() / 2],
                lifePalace == null ? null : lifePalace.ganZhi(),
                lifePalace == null ? null : lifePalace.ganZhiKorean(),
                lifePalace == null ? null : NA_YIN[lifePalace.cycleIndex() / 2],
                bodyPalace == null ? null : bodyPalace.ganZhi(),
                bodyPalace == null ? null : bodyPalace.ganZhiKorean(),
                bodyPalace == null ? null : NA_YIN[bodyPalace.cycleIndex() / 2],
                timeUnknown
                        ? "출생시간을 모르므로 명궁과 신궁은 표시하지 않습니다."
                        : "명궁과 신궁은 음력 월과 시지 조합으로 계산한 보조값입니다."
        );
    }

    private RelationResponse buildRelations(Chart chart) {
        List<PillarLabel> pillars = chart.knownPillars();
        List<RelationItemResponse> stemRelations = new ArrayList<>();
        List<RelationItemResponse> branchRelations = new ArrayList<>();

        for (int first = 0; first < pillars.size(); first++) {
            for (int second = first + 1; second < pillars.size(); second++) {
                PillarLabel left = pillars.get(first);
                PillarLabel right = pillars.get(second);
                String members = left.label() + "-" + right.label();

                addPairRelation(
                        stemRelations,
                        STEM_COMBINATIONS,
                        left.pillar().stem(),
                        right.pillar().stem(),
                        "천간합",
                        members
                );

                addPairRelation(
                        stemRelations,
                        STEM_CLASHES,
                        left.pillar().stem(),
                        right.pillar().stem(),
                        "천간충",
                        members
                );

                addPairRelation(
                        branchRelations,
                        BRANCH_COMBINATIONS,
                        left.pillar().branch(),
                        right.pillar().branch(),
                        "지지육합",
                        members
                );

                addPairRelation(
                        branchRelations,
                        BRANCH_CLASHES,
                        left.pillar().branch(),
                        right.pillar().branch(),
                        "지지충",
                        members
                );

                addPairRelation(
                        branchRelations,
                        BRANCH_HARMS,
                        left.pillar().branch(),
                        right.pillar().branch(),
                        "지지해",
                        members
                );

                addPairRelation(
                        branchRelations,
                        BRANCH_BREAKS,
                        left.pillar().branch(),
                        right.pillar().branch(),
                        "지지파",
                        members
                );
            }
        }

        Set<String> branches = pillars.stream()
                .map(item -> item.pillar().branch())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        addTripleRelation(branchRelations, branches, Set.of("申", "子", "辰"), "삼합", "신자진 수국");
        addTripleRelation(branchRelations, branches, Set.of("亥", "卯", "未"), "삼합", "해묘미 목국");
        addTripleRelation(branchRelations, branches, Set.of("寅", "午", "戌"), "삼합", "인오술 화국");
        addTripleRelation(branchRelations, branches, Set.of("巳", "酉", "丑"), "삼합", "사유축 금국");

        addTripleRelation(branchRelations, branches, Set.of("亥", "子", "丑"), "방합", "해자축 수방합");
        addTripleRelation(branchRelations, branches, Set.of("寅", "卯", "辰"), "방합", "인묘진 목방합");
        addTripleRelation(branchRelations, branches, Set.of("巳", "午", "未"), "방합", "사오미 화방합");
        addTripleRelation(branchRelations, branches, Set.of("申", "酉", "戌"), "방합", "신유술 금방합");

        addPunishments(branchRelations, pillars);

        return new RelationResponse(stemRelations, branchRelations);
    }

    private void addPairRelation(
            List<RelationItemResponse> result,
            Map<String, String> relationMap,
            String left,
            String right,
            String category,
            String members
    ) {
        findPair(relationMap, left, right)
                .ifPresent(description -> result.add(
                        new RelationItemResponse(category, members, description)
                ));
    }

    private Optional<String> findPair(
            Map<String, String> relationMap,
            String left,
            String right
    ) {
        String direct = relationMap.get(left + right);
        if (direct != null) {
            return Optional.of(direct);
        }

        return Optional.ofNullable(relationMap.get(right + left));
    }

    private void addTripleRelation(
            List<RelationItemResponse> result,
            Set<String> branches,
            Set<String> target,
            String category,
            String description
    ) {
        if (branches.containsAll(target)) {
            result.add(new RelationItemResponse(
                    category,
                    String.join("", target),
                    description
            ));
        }
    }

    private void addPunishments(
            List<RelationItemResponse> result,
            List<PillarLabel> pillars
    ) {
        Set<String> branches = pillars.stream()
                .map(item -> item.pillar().branch())
                .collect(Collectors.toSet());

        if (branches.containsAll(Set.of("寅", "巳", "申"))) {
            result.add(new RelationItemResponse("삼형", "인·사·신", "인사신 무은지형"));
        }

        if (branches.containsAll(Set.of("丑", "戌", "未"))) {
            result.add(new RelationItemResponse("삼형", "축·술·미", "축술미 지세지형"));
        }

        if (branches.contains("子") && branches.contains("卯")) {
            result.add(new RelationItemResponse("상형", "자·묘", "자묘 무례지형"));
        }

        Map<String, Long> counts = pillars.stream()
                .map(item -> item.pillar().branch())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for (String branch : List.of("辰", "午", "酉", "亥")) {
            if (counts.getOrDefault(branch, 0L) >= 2L) {
                result.add(new RelationItemResponse(
                        "자형",
                        branchKorean(branch),
                        branchKorean(branch) + " 자형"
                ));
            }
        }
    }

    private List<ShinsalResponse> buildShinsal(Chart chart) {
        Map<String, ShinsalResponse> result = new LinkedHashMap<>();
        List<PillarLabel> pillars = chart.knownPillars();

        addBranchBasedStars(result, chart.dayPillar().branch(), "일지 기준", pillars);

        if (!Objects.equals(chart.dayPillar().branch(), chart.yearPillar().branch())) {
            addBranchBasedStars(result, chart.yearPillar().branch(), "년지 기준", pillars);
        }

        for (String target : noblemanTargets(chart.dayPillar().stem())) {
            addStarIfPresent(
                    result,
                    "천을귀인",
                    target,
                    pillars,
                    "어려움 속에서 도움과 해결의 계기를 상징하는 귀인성입니다."
            );
        }

        addStarIfPresent(
                result,
                "문창귀인",
                literaryStarTarget(chart.dayPillar().stem()),
                pillars,
                "학습·문서·표현력·기획 능력과 관련해 참고하는 귀인성입니다."
        );

        addStarIfPresent(
                result,
                "양인살",
                bladeStarTarget(chart.dayPillar().stem()),
                pillars,
                "강한 추진력과 결단력을 나타내지만 과도하면 충돌을 경계합니다."
        );

        if (Set.of("庚辰", "庚戌", "壬辰", "戊戌").contains(chart.dayPillar().ganZhi())) {
            result.put(
                    "괴강살",
                    new ShinsalResponse(
                            "괴강살",
                            "일주",
                            "강한 자존감·결단력·독립성을 상징하는 특수 일주로 봅니다."
                    )
            );
        }

        result.put(
                "공망",
                new ShinsalResponse(
                        "공망",
                        xunKong(chart.dayPillar().cycleIndex()),
                        "일주 기준 공망 지지입니다. 비어 있거나 다른 방식으로 작용할 가능성을 참고합니다."
                )
        );

        addPairShinsal(
                result,
                pillars,
                Map.of(
                        "子未", "자미 원진",
                        "丑午", "축오 원진",
                        "寅酉", "인유 원진",
                        "卯申", "묘신 원진",
                        "辰亥", "진해 원진",
                        "巳戌", "사술 원진"
                ),
                "원진살",
                "관계에서 오해·엇갈림·감정적 불편으로 나타날 수 있는 조합입니다."
        );

        addPairShinsal(
                result,
                pillars,
                Map.of(
                        "子酉", "자유 귀문",
                        "丑午", "축오 귀문",
                        "寅未", "인미 귀문",
                        "卯申", "묘신 귀문",
                        "辰亥", "진해 귀문",
                        "巳戌", "사술 귀문"
                ),
                "귀문관살",
                "예민한 감각·집중력·직관성과 함께 생각이 과도해지는 경향을 참고합니다."
        );

        return new ArrayList<>(result.values());
    }

    private void addBranchBasedStars(
            Map<String, ShinsalResponse> result,
            String baseBranch,
            String basis,
            List<PillarLabel> pillars
    ) {
        String group = branchGroup(baseBranch);

        String peach = switch (group) {
            case "신자진" -> "酉";
            case "인오술" -> "卯";
            case "해묘미" -> "子";
            case "사유축" -> "午";
            default -> null;
        };

        String travel = switch (group) {
            case "신자진" -> "寅";
            case "인오술" -> "申";
            case "해묘미" -> "巳";
            case "사유축" -> "亥";
            default -> null;
        };

        String canopy = switch (group) {
            case "신자진" -> "辰";
            case "인오술" -> "戌";
            case "해묘미" -> "未";
            case "사유축" -> "丑";
            default -> null;
        };

        addStarIfPresent(result, "도화살", peach, pillars, basis + " 도화로 매력·관계·표현력의 작용을 참고합니다.");
        addStarIfPresent(result, "역마살", travel, pillars, basis + " 역마로 이동·변화·활동성의 작용을 참고합니다.");
        addStarIfPresent(result, "화개살", canopy, pillars, basis + " 화개로 몰입·예술·철학적 관심의 작용을 참고합니다.");
    }

    private void addStarIfPresent(
            Map<String, ShinsalResponse> result,
            String name,
            String targetBranch,
            List<PillarLabel> pillars,
            String description
    ) {
        if (targetBranch == null) {
            return;
        }

        String location = pillars.stream()
                .filter(item -> Objects.equals(item.pillar().branch(), targetBranch))
                .map(PillarLabel::label)
                .collect(Collectors.joining(", "));

        if (!location.isBlank()) {
            result.putIfAbsent(
                    name + "-" + location,
                    new ShinsalResponse(name, location, description)
            );
        }
    }

    private void addPairShinsal(
            Map<String, ShinsalResponse> result,
            List<PillarLabel> pillars,
            Map<String, String> combinations,
            String name,
            String description
    ) {
        for (int first = 0; first < pillars.size(); first++) {
            for (int second = first + 1; second < pillars.size(); second++) {
                PillarLabel left = pillars.get(first);
                PillarLabel right = pillars.get(second);

                findPair(combinations, left.pillar().branch(), right.pillar().branch())
                        .ifPresent(detail -> result.putIfAbsent(
                                name + "-" + left.label() + "-" + right.label(),
                                new ShinsalResponse(
                                        name,
                                        left.label() + "-" + right.label(),
                                        detail + " · " + description
                                )
                        ));
            }
        }
    }

    private LuckResponse buildLuck(
            BirthProfile profile,
            Chart chart
    ) {
        boolean yearStemYang = "양".equals(STEM_YIN_YANG[chart.yearPillar().stemIndex()]);
        boolean male = profile.getGender() == Gender.MALE;
        boolean forward = (male && yearStemYang) || (!male && !yearStemYang);

        LocalDateTime targetTerm = forward
                ? nextJie(chart.birthDateTime())
                : previousJie(chart.birthDateTime());

        long differenceMinutes = Math.abs(
                Duration.between(chart.birthDateTime(), targetTerm).toMinutes()
        );

        long totalStartMonths = differenceMinutes / 360L;
        int startYears = (int) (totalStartMonths / 12L);
        int startMonths = (int) (totalStartMonths % 12L);
        int startDays = (int) Math.round((differenceMinutes % 360L) / 12.0);

        LocalDate startDate = chart.birthDateTime().toLocalDate()
                .plusYears(startYears)
                .plusMonths(startMonths)
                .plusDays(startDays);

        int firstStartAge = Math.max(1, startYears);
        int currentYear = LocalDate.now().getYear();
        String currentDecade = "현재 대운 없음";

        List<DecadeFortuneResponse> decades = new ArrayList<>();

        for (int index = 0; index < 8; index++) {
            int step = forward ? index + 1 : -(index + 1);
            PillarData decadePillar = pillarFromCycleIndex(
                    chart.monthPillar().cycleIndex() + step
            );

            int decadeStartAge = firstStartAge + index * 10;
            int decadeEndAge = decadeStartAge + 9;
            int decadeStartYear = profile.getBirthDate().getYear() + decadeStartAge;
            int decadeEndYear = decadeStartYear + 9;
            boolean current = currentYear >= decadeStartYear && currentYear <= decadeEndYear;

            if (current) {
                currentDecade = decadePillar.ganZhiKorean() + " 대운";
            }

            List<YearFortuneResponse> yearlyFortunes = new ArrayList<>();

            for (int year = decadeStartYear; year <= decadeEndYear; year++) {
                PillarData yearPillar = pillarFromCycleIndex(Math.floorMod(year - 4, 60));

                yearlyFortunes.add(new YearFortuneResponse(
                        year,
                        year - profile.getBirthDate().getYear() + 1,
                        yearPillar.ganZhi(),
                        yearPillar.ganZhiKorean(),
                        STEM_ELEMENTS[yearPillar.stemIndex()]
                                + "/"
                                + BRANCH_ELEMENTS[yearPillar.branchIndex()],
                        tenGod(chart.dayPillar().stemIndex(), yearPillar.stemIndex(), false),
                        xunName(yearPillar.cycleIndex()),
                        xunKong(yearPillar.cycleIndex())
                ));
            }

            decades.add(new DecadeFortuneResponse(
                    decadeStartAge,
                    decadeEndAge,
                    decadeStartYear,
                    decadeEndYear,
                    decadePillar.ganZhi(),
                    decadePillar.ganZhiKorean(),
                    STEM_ELEMENTS[decadePillar.stemIndex()]
                            + "/"
                            + BRANCH_ELEMENTS[decadePillar.branchIndex()],
                    tenGod(chart.dayPillar().stemIndex(), decadePillar.stemIndex(), false),
                    xunName(decadePillar.cycleIndex()),
                    xunKong(decadePillar.cycleIndex()),
                    current,
                    yearlyFortunes
            ));
        }

        String startAge = startYears
                + "년 "
                + startMonths
                + "개월 "
                + startDays
                + "일";

        return new LuckResponse(
                forward,
                forward ? "순행" : "역행",
                startAge,
                startDate.toString(),
                profile.isBirthTimeUnknown(),
                currentDecade,
                decades,
                profile.isBirthTimeUnknown()
                        ? "출생시간 미상으로 정오를 사용했으므로 대운 시작 시점은 참고값입니다."
                        : "절입 근사시각과 출생시각의 차이를 3일 1년 방식으로 환산했습니다."
        );
    }

    private LocalDateTime nextJie(LocalDateTime dateTime) {
        List<LocalDateTime> terms = nearbyJieTerms(dateTime.getYear());

        return terms.stream()
                .filter(term -> term.isAfter(dateTime))
                .min(Comparator.naturalOrder())
                .orElseGet(() -> solarTerm(dateTime.getYear() + 1, 0));
    }

    private LocalDateTime previousJie(LocalDateTime dateTime) {
        List<LocalDateTime> terms = nearbyJieTerms(dateTime.getYear());

        return terms.stream()
                .filter(term -> !term.isAfter(dateTime))
                .max(Comparator.naturalOrder())
                .orElseGet(() -> solarTerm(dateTime.getYear() - 1, 22));
    }

    private List<LocalDateTime> nearbyJieTerms(int year) {
        List<LocalDateTime> terms = new ArrayList<>();

        for (int targetYear = year - 1; targetYear <= year + 1; targetYear++) {
            for (int termIndex = 0; termIndex < 24; termIndex += 2) {
                terms.add(solarTerm(targetYear, termIndex));
            }
        }

        return terms;
    }

    private List<InterpretationResponse> buildInterpretations(
            BirthProfile profile,
            Chart chart,
            ElementBalanceResponse elementBalance,
            TenGodBalanceResponse tenGodBalance,
            StrengthResponse strength
    ) {
        String dayElement = STEM_ELEMENTS[chart.dayPillar().stemIndex()];
        String dominantElement = elementBalance.dominant();
        String dominantTenGod = tenGodBalance.dominant();

        return List.of(
                new InterpretationResponse(
                        "성격",
                        "일간과 오행으로 보는 기본 성향",
                        profile.getName()
                                + "님의 일간은 "
                                + STEMS_KOREAN[chart.dayPillar().stemIndex()]
                                + "이며 "
                                + dayElement
                                + "의 성향을 기본으로 가집니다. "
                                + dominantElement
                                + " 기운이 두드러져 "
                                + elementPersonality(dominantElement)
                                + " 성향이 함께 나타날 수 있습니다."
                ),
                new InterpretationResponse(
                        "연애",
                        "관계에서 나타나는 흐름",
                        dominantTenGod
                                + "의 작용이 상대적으로 두드러집니다. "
                                + tenGodRelationship(dominantTenGod)
                                + " 실제 관계에서는 합·충·형·해와 배우자궁을 함께 살펴야 합니다."
                ),
                new InterpretationResponse(
                        "재물",
                        "재물의 관리 방식",
                        "재성은 편재와 정재의 분포로 살펴봅니다. 현재 보조 판정은 "
                                + strength.classification()
                                + "이므로 재물을 얻는 흐름뿐 아니라 일간이 재성을 감당하는 균형도 함께 확인합니다."
                ),
                new InterpretationResponse(
                        "직업",
                        "직업과 사회적 역할",
                        dominantElement
                                + " 기운과 "
                                + dominantTenGod
                                + "이 상대적으로 두드러집니다. "
                                + careerSuggestion(dominantElement)
                ),
                new InterpretationResponse(
                        "건강",
                        "오행 균형 관점의 생활 관리",
                        elementBalance.weakest()
                                + " 기운이 상대적으로 적게 나타납니다. 이는 의학적 진단이 아니라 오행 균형 관점의 참고값입니다."
                ),
                new InterpretationResponse(
                        "시험",
                        "학습과 시험 준비 경향",
                        "인성은 학습과 흡수력, 식상은 표현과 답안 작성 능력과 연결해 봅니다. 실제 결과는 학습량과 문제풀이 정확도가 가장 크게 좌우합니다."
                ),
                new InterpretationResponse(
                        "창업",
                        "사업과 독립 활동의 방향",
                        "창업은 식상·재성·비겁의 균형과 현재 대운을 함께 살펴야 합니다. "
                                + strength.usefulGod()
                                + " 기운을 보완하는 환경을 만들고 자금과 매출 데이터를 기준으로 단계적으로 확장하는 편이 안전합니다."
                )
        );
    }

    private static Map<String, List<Integer>> createHiddenStemMap() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();

        map.put("子", List.of(9));
        map.put("丑", List.of(5, 9, 7));
        map.put("寅", List.of(0, 2, 4));
        map.put("卯", List.of(1));
        map.put("辰", List.of(4, 1, 9));
        map.put("巳", List.of(2, 4, 6));
        map.put("午", List.of(3, 5));
        map.put("未", List.of(5, 3, 1));
        map.put("申", List.of(6, 8, 4));
        map.put("酉", List.of(7));
        map.put("戌", List.of(4, 7, 3));
        map.put("亥", List.of(8, 0));

        return Map.copyOf(map);
    }

    private int combinedStemIndex(int stemIndex) {
        return switch (Math.floorMod(stemIndex, 10)) {
            case 0 -> 5;
            case 5 -> 0;
            case 1 -> 6;
            case 6 -> 1;
            case 2 -> 7;
            case 7 -> 2;
            case 3 -> 8;
            case 8 -> 3;
            case 4 -> 9;
            case 9 -> 4;
            default -> throw new IllegalStateException("천간 합을 계산할 수 없습니다.");
        };
    }

    private int combinedBranchIndex(int branchIndex) {
        return switch (Math.floorMod(branchIndex, 12)) {
            case 0 -> 1;
            case 1 -> 0;
            case 2 -> 11;
            case 11 -> 2;
            case 3 -> 10;
            case 10 -> 3;
            case 4 -> 9;
            case 9 -> 4;
            case 5 -> 8;
            case 8 -> 5;
            case 6 -> 7;
            case 7 -> 6;
            default -> throw new IllegalStateException("지지 합을 계산할 수 없습니다.");
        };
    }

    private List<String> noblemanTargets(String dayStem) {
        return switch (dayStem) {
            case "甲", "戊", "庚" -> List.of("丑", "未");
            case "乙", "己" -> List.of("子", "申");
            case "丙", "丁" -> List.of("亥", "酉");
            case "壬", "癸" -> List.of("卯", "巳");
            case "辛" -> List.of("寅", "午");
            default -> List.of();
        };
    }

    private String literaryStarTarget(String dayStem) {
        return Map.ofEntries(
                Map.entry("甲", "巳"),
                Map.entry("乙", "午"),
                Map.entry("丙", "申"),
                Map.entry("丁", "酉"),
                Map.entry("戊", "申"),
                Map.entry("己", "酉"),
                Map.entry("庚", "亥"),
                Map.entry("辛", "子"),
                Map.entry("壬", "寅"),
                Map.entry("癸", "卯")
        ).get(dayStem);
    }

    private String bladeStarTarget(String dayStem) {
        return Map.ofEntries(
                Map.entry("甲", "卯"),
                Map.entry("乙", "寅"),
                Map.entry("丙", "午"),
                Map.entry("丁", "巳"),
                Map.entry("戊", "午"),
                Map.entry("己", "巳"),
                Map.entry("庚", "酉"),
                Map.entry("辛", "申"),
                Map.entry("壬", "子"),
                Map.entry("癸", "亥")
        ).get(dayStem);
    }

    private String branchGroup(String branch) {
        if ("申子辰".contains(branch)) {
            return "신자진";
        }

        if ("寅午戌".contains(branch)) {
            return "인오술";
        }

        if ("亥卯未".contains(branch)) {
            return "해묘미";
        }

        if ("巳酉丑".contains(branch)) {
            return "사유축";
        }

        return "";
    }

    private String findProducingElement(String targetElement) {
        return PRODUCES.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), targetElement))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
    }

    private String findControllingElement(String targetElement) {
        return CONTROLS.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), targetElement))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
    }

    private String weakestCandidate(
            Map<String, Integer> counts,
            List<String> candidates
    ) {
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }

        return candidates.stream()
                .min(Comparator.comparingInt(element -> counts.getOrDefault(element, 0)))
                .orElse(candidates.get(0));
    }

    private double percentage(int value, int total) {
        if (total <= 0) {
            return 0.0;
        }

        return Math.round(value * 1000.0 / total) / 10.0;
    }

    private String branchKorean(String branch) {
        int index = Arrays.asList(BRANCHES).indexOf(branch);
        return index < 0 ? branch : BRANCHES_KOREAN[index];
    }

    private String elementPersonality(String element) {
        return switch (element) {
            case "목" -> "성장·기획·확장";
            case "화" -> "표현·열정·속도";
            case "토" -> "안정·책임·현실감";
            case "금" -> "판단·원칙·정리";
            case "수" -> "관찰·유연성·사고";
            default -> "균형";
        };
    }

    private String tenGodRelationship(String tenGod) {
        return switch (tenGod) {
            case "비견", "겁재" -> "대등한 관계와 주도권이 중요하게 작용할 수 있습니다.";
            case "식신", "상관" -> "대화와 감정 표현이 관계 만족도에 큰 영향을 줄 수 있습니다.";
            case "편재", "정재" -> "현실적인 신뢰와 약속을 중요하게 여길 가능성이 있습니다.";
            case "편관", "정관" -> "책임감과 관계의 기준을 분명하게 세우려는 경향이 있을 수 있습니다.";
            case "편인", "정인" -> "정서적 이해와 안정감을 중요하게 느낄 가능성이 있습니다.";
            default -> "서로의 속도와 표현 방식을 확인하는 것이 중요합니다.";
        };
    }

    private String careerSuggestion(String element) {
        return switch (element) {
            case "목" -> "기획·교육·콘텐츠·성장 산업처럼 확장성을 다루는 분야를 검토할 수 있습니다.";
            case "화" -> "홍보·디자인·발표·서비스처럼 표현과 반응 속도가 중요한 분야를 검토할 수 있습니다.";
            case "토" -> "운영·관리·부동산·회계처럼 안정성과 지속성이 필요한 분야를 검토할 수 있습니다.";
            case "금" -> "기술·보안·법무·품질관리처럼 기준과 정확성이 중요한 분야를 검토할 수 있습니다.";
            case "수" -> "연구·데이터·상담·유통처럼 정보와 흐름을 다루는 분야를 검토할 수 있습니다.";
            default -> "현재 경력과 강점을 중심으로 현실적인 직무를 비교하는 것이 좋습니다.";
        };
    }

    /**
     * 미래 분석 서비스에 전달하는 원국 기본자료.
     */
    public record ForecastBaseData(
            Long profileId,
            String dayStem,
            String dayBranch,
            String dayElement,
            List<String> natalStems,
            List<String> natalBranches,
            List<ForecastNatalPillarData> natalPillars,
            String strengthClassification,
            String usefulElement,
            String favorableElement,
            String unfavorableElement,
            ForecastLuckData currentDaeun,
            boolean birthTimeUnknown
    ) {
        public ForecastBaseData {
            natalStems = natalStems == null
                    ? List.of()
                    : List.copyOf(natalStems);

            natalBranches = natalBranches == null
                    ? List.of()
                    : List.copyOf(natalBranches);

            natalPillars = natalPillars == null
                    ? List.of()
                    : List.copyOf(natalPillars);
        }
    }

    /**
     * 원국의 개별 기둥 자료.
     */
    public record ForecastNatalPillarData(
            String type,
            String ganji,
            String ganjiKorean,
            String stem,
            String branch,
            String stemElement,
            String branchElement,
            String stemTenGod
    ) {
    }

    /**
     * 현재 대운 자료.
     */
    public record ForecastLuckData(
            String ganji,
            String ganjiKorean,
            String stem,
            String branch,
            String stemElement,
            String branchElement,
            String tenGod,
            int startAge,
            int endAge,
            int startYear,
            int endYear,
            String direction
    ) {
    }

    /**
     * 세운 또는 월운의 기둥과 실제 적용 기간.
     */
    public record ForecastPillarData(
            int year,
            Integer month,
            String solarTermName,
            String ganji,
            String ganjiKorean,
            String stem,
            String branch,
            String stemElement,
            String branchElement,
            String tenGod,
            LocalDateTime periodStart,
            LocalDateTime periodEnd
    ) {
    }

    private record PillarData(
            int cycleIndex,
            int stemIndex,
            int branchIndex
    ) {
        String stem() {
            return STEMS[stemIndex];
        }

        String branch() {
            return BRANCHES[branchIndex];
        }

        String ganZhi() {
            return stem() + branch();
        }

        String ganZhiKorean() {
            return STEMS_KOREAN[stemIndex] + BRANCHES_KOREAN[branchIndex];
        }
    }

    private record PillarLabel(
            String label,
            PillarData pillar
    ) {
    }

    private record Chart(
            LocalDateTime birthDateTime,
            PillarData yearPillar,
            PillarData monthPillar,
            PillarData dayPillar,
            PillarData timePillar,
            boolean timeUnknown
    ) {
        List<PillarLabel> knownPillars() {
            List<PillarLabel> result = new ArrayList<>();
            result.add(new PillarLabel("년주", yearPillar));
            result.add(new PillarLabel("월주", monthPillar));
            result.add(new PillarLabel("일주", dayPillar));

            if (!timeUnknown && timePillar != null) {
                result.add(new PillarLabel("시주", timePillar));
            }

            return result;
        }
    }

    private static final class KoreanLunarConverter {

        private static final int BASE_YEAR = 1900;
        private static final int LAST_YEAR = 2050;
        private static final LocalDate BASE_SOLAR_DATE = LocalDate.of(1900, 1, 31);

        private static final int[] YEAR_DATA = {
                0x830084bd, 0x82c404ae, 0x82c60a57, 0x82fe554d, 0xc2c40d26,
                0x82c60d95, 0x83014655, 0x82c4056a, 0xc2c609ad, 0x8300255d,
                0x82c404ae, 0x83006a5b, 0xc2c40a4d, 0x82c40d25, 0x83005da9,
                0x82c60b55, 0xc2c4056a, 0x83002ada, 0x82c6095d, 0x830074bb,
                0xc2c4049b, 0x82c40a4b, 0x83005b4b, 0x82c406a9, 0xc2c40ad4,
                0x83024bb5, 0x82c402b6, 0x82c6095b, 0xc3002537, 0x82c40497,
                0x82fe6656, 0x82c40e4a, 0xc2c60ea5, 0x830156a9, 0x82c605b5,
                0x82c402b6, 0xc30138ae, 0x82c4092e, 0x83017c8d, 0x82c40c95,
                0xc2c40d4a, 0x83016d8a, 0x82c60b69, 0x82c6056d, 0xc301425b,
                0x82c4025d, 0x82c4092d, 0x83002d2b, 0xc2c40a95, 0x83007d55,
                0x82c40b4a, 0x82c60b55, 0xc3015555, 0x82c604db, 0x82c4025b,
                0x83013857, 0xc2c4052b, 0x83008a9b, 0x82c40695, 0x82c406aa,
                0xc3006aea, 0x82c60ab5, 0x82c404b6, 0x83004aae, 0xc2c60a57,
                0x82c40527, 0x82fe3726, 0x82c60d95, 0xc30076b5, 0x82c4056a,
                0x82c609ad, 0x830054dd, 0xc2c404ae, 0x82c40a4e, 0x83004d4d,
                0x82c40d25, 0xc3008d59, 0x82c40b54, 0x82c60d6a, 0x8301695a,
                0xc2c6095b, 0x82c4049b, 0x83004a9b, 0x82c40a4b, 0xc300ab27,
                0x82c406a5, 0x82c406d4, 0x83026b75, 0xc2c402b6, 0x82c6095b,
                0x830054b7, 0x82c40497, 0xc2c4064b, 0x82fe374a, 0x82c60ea5,
                0x830086d9, 0xc2c605ad, 0x82c402b6, 0x8300596e, 0x82c4092e,
                0xc2c40c96, 0x83004e95, 0x82c40d4a, 0x82c60da5, 0xc3002755,
                0x82c4056c, 0x83027abb, 0x82c4025d, 0xc2c4092d, 0x83005cab,
                0x82c40a95, 0x82c40b4a, 0xc3013b4a, 0x82c60b55, 0x8300955d,
                0x82c404ba, 0xc2c60a5b, 0x83005557, 0x82c4052b, 0x82c40a95,
                0xc3004b95, 0x82c406aa, 0x82c60ad5, 0x830026b5, 0xc2c404b6,
                0x83006a6e, 0x82c60a57, 0x82c40527, 0xc2fe56a6, 0x82c60d93,
                0x82c405aa, 0x83003b6a, 0xc2c6096d, 0x8300b4af, 0x82c404ae,
                0x82c40a4d, 0xc3016d0d, 0x82c40d25, 0x82c40d52, 0x83005dd4,
                0xc2c60b6a, 0x82c6096d, 0x8300255b, 0x82c4049b, 0xc3007a57,
                0x82c40a4b, 0x82c40b25, 0x83015b25, 0xc2c406d4, 0x82c60ada,
                0x830138b6
        };

        public Result fromSolar(LocalDate solarDate) {
            validateSolarRange(solarDate);

            long offset = solarDate.toEpochDay() - BASE_SOLAR_DATE.toEpochDay();
            if (offset < 0) {
                throw new IllegalArgumentException("지원 범위보다 이전 날짜입니다.");
            }

            int lunarYear = BASE_YEAR;
            while (lunarYear <= LAST_YEAR) {
                int yearDays = getYearDays(lunarYear);
                if (offset < yearDays) {
                    break;
                }
                offset -= yearDays;
                lunarYear++;
            }

            if (lunarYear > LAST_YEAR) {
                throw new IllegalArgumentException("지원 범위를 벗어난 날짜입니다.");
            }

            int leapMonth = getLeapMonth(lunarYear);
            int lunarMonth = 1;
            boolean leap = false;

            while (lunarMonth <= 12) {
                int monthDays = getMonthDays(lunarYear, lunarMonth, leap);
                if (offset < monthDays) {
                    break;
                }

                offset -= monthDays;

                if (leapMonth == lunarMonth && !leap) {
                    leap = true;
                } else {
                    leap = false;
                    lunarMonth++;
                }
            }

            int lunarDay = (int) offset + 1;

            return new Result(
                    solarDate,
                    LocalDate.of(lunarYear, lunarMonth, lunarDay),
                    leap
            );
        }

        public Result fromLunar(
                LocalDate lunarDate,
                boolean leapMonth
        ) {
            validateLunarDate(lunarDate, leapMonth);

            long offset = 0;

            for (int year = BASE_YEAR; year < lunarDate.getYear(); year++) {
                offset += getYearDays(year);
            }

            int targetYear = lunarDate.getYear();
            int leap = getLeapMonth(targetYear);

            for (int month = 1; month < lunarDate.getMonthValue(); month++) {
                offset += getMonthDays(targetYear, month, false);

                if (leap == month) {
                    offset += getMonthDays(targetYear, month, true);
                }
            }

            if (leapMonth) {
                offset += getMonthDays(targetYear, lunarDate.getMonthValue(), false);
            }

            offset += lunarDate.getDayOfMonth() - 1L;

            LocalDate solarDate = BASE_SOLAR_DATE.plusDays(offset);
            validateSolarRange(solarDate);

            return new Result(
                    solarDate,
                    lunarDate,
                    leapMonth
            );
        }

        public int getLeapMonth(int year) {
            validateYear(year);
            return (getYearData(year) >> 12) & 0x000F;
        }

        public int getYearDays(int year) {
            validateYear(year);
            return (getYearData(year) >> 17) & 0x01FF;
        }

        public int getMonthDays(
                int year,
                int month,
                boolean leapMonth
        ) {
            validateYear(year);

            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("음력 월은 1부터 12 사이여야 합니다.");
            }

            int yearData = getYearData(year);

            if (leapMonth) {
                if (getLeapMonth(year) != month) {
                    throw new IllegalArgumentException("해당 연도에는 선택한 윤달이 없습니다.");
                }

                return ((yearData >> 16) & 0x01) == 1
                        ? 30
                        : 29;
            }

            return ((yearData >> (12 - month)) & 0x01) == 1
                    ? 30
                    : 29;
        }

        private int getYearData(int year) {
            return YEAR_DATA[year - BASE_YEAR];
        }

        private void validateYear(int year) {
            if (year < BASE_YEAR || year > LAST_YEAR) {
                throw new IllegalArgumentException(
                        "음양력 변환 지원 연도는 "
                                + BASE_YEAR
                                + "년부터 "
                                + LAST_YEAR
                                + "년까지입니다."
                );
            }
        }

        private void validateSolarRange(LocalDate solarDate) {
            if (solarDate == null) {
                throw new IllegalArgumentException("양력 날짜가 없습니다.");
            }

            if (
                    solarDate.isBefore(BASE_SOLAR_DATE)
                            || solarDate.getYear() > LAST_YEAR
            ) {
                throw new IllegalArgumentException(
                        "양력 변환 지원 범위는 1900-01-31부터 2050년까지입니다."
                );
            }
        }

        private void validateLunarDate(
                LocalDate lunarDate,
                boolean leapMonth
        ) {
            if (lunarDate == null) {
                throw new IllegalArgumentException("음력 날짜가 없습니다.");
            }

            validateYear(lunarDate.getYear());

            int monthDays = getMonthDays(
                    lunarDate.getYear(),
                    lunarDate.getMonthValue(),
                    leapMonth
            );

            if (
                    lunarDate.getDayOfMonth() < 1
                            || lunarDate.getDayOfMonth() > monthDays
            ) {
                throw new IllegalArgumentException("존재하지 않는 음력 날짜입니다.");
            }
        }

        public record Result(
                LocalDate solarDate,
                LocalDate lunarDate,
                boolean leapMonth
        ) {
        }
    }
}