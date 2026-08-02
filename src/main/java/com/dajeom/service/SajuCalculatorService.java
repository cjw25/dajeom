package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuAnalysisResponse.*;
import com.dajeom.entity.BirthProfile;
import com.dajeom.entity.CalendarType;
import com.dajeom.entity.Gender;
import com.dajeom.entity.SajuAnalysis;
import com.github.usingsky.calendar.KoreanLunarCalendar;
import com.nlf.calendar.EightChar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import com.nlf.calendar.eightchar.DaYun;
import com.nlf.calendar.eightchar.LiuNian;
import com.nlf.calendar.eightchar.Yun;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SajuCalculatorService {

    private static final List<String> ELEMENT_ORDER =
            List.of("목", "화", "토", "금", "수");

    private static final List<String> TEN_GOD_ORDER =
            List.of(
                    "비견",
                    "겁재",
                    "식신",
                    "상관",
                    "편재",
                    "정재",
                    "편관",
                    "정관",
                    "편인",
                    "정인"
            );

    private static final Map<String, String> STEM_KOREAN =
            Map.ofEntries(
                    Map.entry("甲", "갑"),
                    Map.entry("乙", "을"),
                    Map.entry("丙", "병"),
                    Map.entry("丁", "정"),
                    Map.entry("戊", "무"),
                    Map.entry("己", "기"),
                    Map.entry("庚", "경"),
                    Map.entry("辛", "신"),
                    Map.entry("壬", "임"),
                    Map.entry("癸", "계")
            );

    private static final Map<String, String> BRANCH_KOREAN =
            Map.ofEntries(
                    Map.entry("子", "자"),
                    Map.entry("丑", "축"),
                    Map.entry("寅", "인"),
                    Map.entry("卯", "묘"),
                    Map.entry("辰", "진"),
                    Map.entry("巳", "사"),
                    Map.entry("午", "오"),
                    Map.entry("未", "미"),
                    Map.entry("申", "신"),
                    Map.entry("酉", "유"),
                    Map.entry("戌", "술"),
                    Map.entry("亥", "해")
            );

    private static final Map<String, String> ZODIAC =
            Map.ofEntries(
                    Map.entry("子", "쥐"),
                    Map.entry("丑", "소"),
                    Map.entry("寅", "호랑이"),
                    Map.entry("卯", "토끼"),
                    Map.entry("辰", "용"),
                    Map.entry("巳", "뱀"),
                    Map.entry("午", "말"),
                    Map.entry("未", "양"),
                    Map.entry("申", "원숭이"),
                    Map.entry("酉", "닭"),
                    Map.entry("戌", "개"),
                    Map.entry("亥", "돼지")
            );

    private static final Map<String, String> STEM_ELEMENT =
            Map.ofEntries(
                    Map.entry("甲", "목"),
                    Map.entry("乙", "목"),
                    Map.entry("丙", "화"),
                    Map.entry("丁", "화"),
                    Map.entry("戊", "토"),
                    Map.entry("己", "토"),
                    Map.entry("庚", "금"),
                    Map.entry("辛", "금"),
                    Map.entry("壬", "수"),
                    Map.entry("癸", "수")
            );

    private static final Map<String, String> BRANCH_ELEMENT =
            Map.ofEntries(
                    Map.entry("寅", "목"),
                    Map.entry("卯", "목"),
                    Map.entry("巳", "화"),
                    Map.entry("午", "화"),
                    Map.entry("辰", "토"),
                    Map.entry("戌", "토"),
                    Map.entry("丑", "토"),
                    Map.entry("未", "토"),
                    Map.entry("申", "금"),
                    Map.entry("酉", "금"),
                    Map.entry("亥", "수"),
                    Map.entry("子", "수")
            );

    private static final Set<String> YANG_STEMS =
            Set.of("甲", "丙", "戊", "庚", "壬");

    private static final Set<String> YANG_BRANCHES =
            Set.of("子", "寅", "辰", "午", "申", "戌");

    private static final Map<String, String> TEN_GOD_KOREAN =
            Map.ofEntries(
                    Map.entry("比肩", "비견"),
                    Map.entry("劫财", "겁재"),
                    Map.entry("劫財", "겁재"),
                    Map.entry("食神", "식신"),
                    Map.entry("伤官", "상관"),
                    Map.entry("傷官", "상관"),
                    Map.entry("偏财", "편재"),
                    Map.entry("偏財", "편재"),
                    Map.entry("正财", "정재"),
                    Map.entry("正財", "정재"),
                    Map.entry("七杀", "편관"),
                    Map.entry("七殺", "편관"),
                    Map.entry("正官", "정관"),
                    Map.entry("偏印", "편인"),
                    Map.entry("正印", "정인"),
                    Map.entry("日主", "일원")
            );

    private static final Map<String, String> TWELVE_STAGE_KOREAN =
            Map.ofEntries(
                    Map.entry("长生", "장생"),
                    Map.entry("長生", "장생"),
                    Map.entry("沐浴", "목욕"),
                    Map.entry("冠带", "관대"),
                    Map.entry("冠帶", "관대"),
                    Map.entry("临官", "건록"),
                    Map.entry("臨官", "건록"),
                    Map.entry("帝旺", "제왕"),
                    Map.entry("衰", "쇠"),
                    Map.entry("病", "병"),
                    Map.entry("死", "사"),
                    Map.entry("墓", "묘"),
                    Map.entry("绝", "절"),
                    Map.entry("絶", "절"),
                    Map.entry("胎", "태"),
                    Map.entry("养", "양"),
                    Map.entry("養", "양")
            );

    private static final Map<String, String> PRODUCES =
            Map.of(
                    "목", "화",
                    "화", "토",
                    "토", "금",
                    "금", "수",
                    "수", "목"
            );

    private static final Map<String, String> CONTROLS =
            Map.of(
                    "목", "토",
                    "토", "수",
                    "수", "화",
                    "화", "금",
                    "금", "목"
            );

    private static final Map<String, String> STEM_COMBINATION =
            Map.of(
                    "甲己", "갑기합",
                    "乙庚", "을경합",
                    "丙辛", "병신합",
                    "丁壬", "정임합",
                    "戊癸", "무계합"
            );

    private static final Map<String, String> STEM_CLASH =
            Map.of(
                    "甲庚", "갑경충",
                    "乙辛", "을신충",
                    "丙壬", "병임충",
                    "丁癸", "정계충"
            );

    private static final Map<String, String> BRANCH_COMBINATION =
            Map.of(
                    "子丑", "자축합",
                    "寅亥", "인해합",
                    "卯戌", "묘술합",
                    "辰酉", "진유합",
                    "巳申", "사신합",
                    "午未", "오미합"
            );

    private static final Map<String, String> BRANCH_CLASH =
            Map.of(
                    "子午", "자오충",
                    "丑未", "축미충",
                    "寅申", "인신충",
                    "卯酉", "묘유충",
                    "辰戌", "진술충",
                    "巳亥", "사해충"
            );

    private static final Map<String, String> BRANCH_HARM =
            Map.of(
                    "子未", "자미해",
                    "丑午", "축오해",
                    "寅巳", "인사해",
                    "卯辰", "묘진해",
                    "申亥", "신해해",
                    "酉戌", "유술해"
            );

    private static final Map<String, String> BRANCH_BREAK =
            Map.of(
                    "子酉", "자유파",
                    "丑辰", "축진파",
                    "寅亥", "인해파",
                    "卯午", "묘오파",
                    "巳申", "사신파",
                    "未戌", "미술파"
            );

    public SajuAnalysisResponse calculate(
            SajuAnalysis analysis
    ) {
        BirthProfile profile =
                analysis.getBirthProfile();

        CalendarCalculation calendarCalculation =
                resolveCalendar(profile);

        LocalTime calculationTime =
                profile.isBirthTimeUnknown()
                        ? LocalTime.NOON
                        : profile.getBirthTime();

        Solar solar =
                Solar.fromYmdHms(
                        calendarCalculation.solarDate().getYear(),
                        calendarCalculation.solarDate().getMonthValue(),
                        calendarCalculation.solarDate().getDayOfMonth(),
                        calculationTime.getHour(),
                        calculationTime.getMinute(),
                        0
                );

        Lunar lunar =
                solar.getLunar();

        EightChar eightChar =
                lunar.getEightChar();

        // 야자시 일주는 당일로 계산하는 방식 2
        eightChar.setSect(2);

        List<PillarResponse> pillars =
                buildPillars(
                        eightChar,
                        profile.isBirthTimeUnknown()
                );

        ElementBalanceResponse elementBalance =
                buildElementBalance(pillars);

        YinYangBalanceResponse yinYangBalance =
                buildYinYangBalance(pillars);

        TenGodBalanceResponse tenGodBalance =
                buildTenGodBalance(pillars);

        StrengthResponse strength =
                buildStrength(
                        eightChar.getDayGan(),
                        elementBalance
                );

        StructureResponse structure =
                buildStructure(
                        eightChar,
                        profile.isBirthTimeUnknown()
                );

        RelationResponse relations =
                buildRelations(pillars);

        List<ShinsalResponse> shinsal =
                buildShinsal(
                        eightChar,
                        pillars
                );

        LuckResponse luck =
                buildLuck(
                        profile,
                        eightChar
                );

        List<InterpretationResponse> interpretations =
                buildInterpretations(
                        profile,
                        eightChar,
                        elementBalance,
                        tenGodBalance,
                        strength
                );

        CalendarResponse calendarResponse =
                new CalendarResponse(
                        profile.getCalendarType() == CalendarType.SOLAR
                                ? "양력"
                                : "음력",
                        calendarCalculation.solarDate().toString(),
                        calendarCalculation.lunarDate(),
                        calendarCalculation.lunarLeapMonth(),
                        profile.isBirthTimeUnknown()
                                ? "시간 모름"
                                : profile.getBirthTime().toString(),
                        profile.isBirthTimeUnknown(),
                        profile.isBirthTimeUnknown()
                                ? "출생시간 미상 · 시주 제외"
                                : "사용자 입력 시간",
                        "절입 시각 기준 · 야자시 방식 2 · 기운 분 단위 방식 2"
                );

        String dayMaster =
                koreanStem(eightChar.getDayGan())
                        + " "
                        + elementOfStem(eightChar.getDayGan());

        String summary =
                profile.getName()
                        + "님의 일간은 "
                        + koreanStem(eightChar.getDayGan())
                        + "("
                        + elementOfStem(eightChar.getDayGan())
                        + ")이며, 오행에서는 "
                        + elementBalance.dominant()
                        + " 기운이 가장 두드러지고 "
                        + elementBalance.weakest()
                        + " 기운이 상대적으로 적습니다. "
                        + "일간 균형 보조 판정은 "
                        + strength.classification()
                        + "입니다.";

        return new SajuAnalysisResponse(
                analysis.getId(),
                profile.getId(),
                analysis.getStatus().name(),
                profile.getName(),
                calendarResponse,
                ZODIAC.getOrDefault(
                        eightChar.getYearZhi(),
                        ""
                ),
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
                "본 결과는 절입 기준 만세력 계산 자료입니다. "
                        + "신강·신약, 용신과 신살은 학파에 따라 해석이 달라질 수 있으므로 "
                        + "오행 균형을 살펴보는 참고 자료로 사용해야 합니다.",
                analysis.getCompletedAt()
        );
    }

    private CalendarCalculation resolveCalendar(
            BirthProfile profile
    ) {
        synchronized (KoreanLunarCalendar.class) {
            KoreanLunarCalendar calendar =
                    KoreanLunarCalendar.getInstance();

            if (
                    profile.getCalendarType()
                            == CalendarType.LUNAR
            ) {
                boolean valid =
                        calendar.setLunarDate(
                                profile.getBirthDate().getYear(),
                                profile.getBirthDate().getMonthValue(),
                                profile.getBirthDate().getDayOfMonth(),
                                profile.isLunarLeapMonth()
                        );

                if (!valid) {
                    throw new IllegalArgumentException(
                            "입력한 음력 생년월일 또는 윤달 정보가 올바르지 않습니다."
                    );
                }

                LocalDate solarDate =
                        LocalDate.of(
                                calendar.getSolarYear(),
                                calendar.getSolarMonth(),
                                calendar.getSolarDay()
                        );

                return new CalendarCalculation(
                        solarDate,
                        formatLunarDate(
                                profile.getBirthDate(),
                                profile.isLunarLeapMonth()
                        ),
                        profile.isLunarLeapMonth()
                );
            }

            boolean valid =
                    calendar.setSolarDate(
                            profile.getBirthDate().getYear(),
                            profile.getBirthDate().getMonthValue(),
                            profile.getBirthDate().getDayOfMonth()
                    );

            if (!valid) {
                throw new IllegalArgumentException(
                        "입력한 양력 생년월일을 음력으로 변환할 수 없습니다."
                );
            }

            LocalDate lunarDate =
                    LocalDate.of(
                            calendar.getLunarYear(),
                            calendar.getLunarMonth(),
                            calendar.getLunarDay()
                    );

            return new CalendarCalculation(
                    profile.getBirthDate(),
                    formatLunarDate(
                            lunarDate,
                            calendar.isIntercalation()
                    ),
                    calendar.isIntercalation()
            );
        }
    }

    private String formatLunarDate(
            LocalDate lunarDate,
            boolean leapMonth
    ) {
        return lunarDate
                + (
                leapMonth
                        ? " (윤달)"
                        : " (평달)"
        );
    }

    private List<PillarResponse> buildPillars(
            EightChar eightChar,
            boolean timeUnknown
    ) {
        List<PillarResponse> pillars =
                new ArrayList<>();

        pillars.add(
                buildPillar(
                        "년주",
                        eightChar.getYear(),
                        eightChar.getYearGan(),
                        eightChar.getYearZhi(),
                        eightChar.getYearShiShenGan(),
                        eightChar.getYearHideGan(),
                        eightChar.getYearShiShenZhi(),
                        eightChar.getYearDiShi(),
                        eightChar.getYearNaYin(),
                        eightChar.getYearXun(),
                        eightChar.getYearXunKong()
                )
        );

        pillars.add(
                buildPillar(
                        "월주",
                        eightChar.getMonth(),
                        eightChar.getMonthGan(),
                        eightChar.getMonthZhi(),
                        eightChar.getMonthShiShenGan(),
                        eightChar.getMonthHideGan(),
                        eightChar.getMonthShiShenZhi(),
                        eightChar.getMonthDiShi(),
                        eightChar.getMonthNaYin(),
                        eightChar.getMonthXun(),
                        eightChar.getMonthXunKong()
                )
        );

        pillars.add(
                buildPillar(
                        "일주",
                        eightChar.getDay(),
                        eightChar.getDayGan(),
                        eightChar.getDayZhi(),
                        eightChar.getDayShiShenGan(),
                        eightChar.getDayHideGan(),
                        eightChar.getDayShiShenZhi(),
                        eightChar.getDayDiShi(),
                        eightChar.getDayNaYin(),
                        eightChar.getDayXun(),
                        eightChar.getDayXunKong()
                )
        );

        if (timeUnknown) {
            pillars.add(
                    new PillarResponse(
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
                    )
            );
        } else {
            pillars.add(
                    buildPillar(
                            "시주",
                            eightChar.getTime(),
                            eightChar.getTimeGan(),
                            eightChar.getTimeZhi(),
                            eightChar.getTimeShiShenGan(),
                            eightChar.getTimeHideGan(),
                            eightChar.getTimeShiShenZhi(),
                            eightChar.getTimeDiShi(),
                            eightChar.getTimeNaYin(),
                            eightChar.getTimeXun(),
                            eightChar.getTimeXunKong()
                    )
            );
        }

        return pillars;
    }

    private PillarResponse buildPillar(
            String type,
            String ganZhi,
            String stem,
            String branch,
            String stemTenGod,
            List<String> hiddenStems,
            List<String> hiddenTenGods,
            String twelveStage,
            String naYin,
            String xun,
            String xunKong
    ) {
        List<HiddenStemResponse> hiddenStemResponses =
                new ArrayList<>();

        for (
                int index = 0;
                index < hiddenStems.size();
                index++
        ) {
            String hiddenStem =
                    hiddenStems.get(index);

            String tenGod =
                    index < hiddenTenGods.size()
                            ? hiddenTenGods.get(index)
                            : "";

            hiddenStemResponses.add(
                    new HiddenStemResponse(
                            hiddenStem,
                            koreanStem(hiddenStem),
                            elementOfStem(hiddenStem),
                            yinYangOfStem(hiddenStem),
                            translateTenGod(tenGod)
                    )
            );
        }

        return new PillarResponse(
                type,
                true,
                ganZhi,
                koreanGanZhi(ganZhi),
                stem,
                koreanStem(stem),
                branch,
                koreanBranch(branch),
                elementOfStem(stem),
                elementOfBranch(branch),
                yinYangOfStem(stem),
                yinYangOfBranch(branch),
                translateTenGod(stemTenGod),
                hiddenStemResponses,
                TWELVE_STAGE_KOREAN.getOrDefault(
                        twelveStage,
                        twelveStage
                ),
                naYin,
                xun,
                xunKong,
                null
        );
    }

    private ElementBalanceResponse buildElementBalance(
            List<PillarResponse> pillars
    ) {
        Map<String, Integer> counts =
                createElementCountMap();

        for (PillarResponse pillar : pillars) {
            if (!pillar.known()) {
                continue;
            }

            counts.computeIfPresent(
                    pillar.stemElement(),
                    (key, value) -> value + 1
            );

            counts.computeIfPresent(
                    pillar.branchElement(),
                    (key, value) -> value + 1
            );
        }

        int total =
                counts.values()
                        .stream()
                        .mapToInt(Integer::intValue)
                        .sum();

        List<BalanceItemResponse> items =
                ELEMENT_ORDER
                        .stream()
                        .map(
                                element -> new BalanceItemResponse(
                                        element,
                                        counts.get(element),
                                        percentage(
                                                counts.get(element),
                                                total
                                        )
                                )
                        )
                        .toList();

        String dominant =
                findElementByCount(
                        counts,
                        Comparator.reverseOrder()
                );

        String weakest =
                findElementByCount(
                        counts,
                        Comparator.naturalOrder()
                );

        return new ElementBalanceResponse(
                items,
                dominant,
                weakest,
                total
        );
    }

    private YinYangBalanceResponse buildYinYangBalance(
            List<PillarResponse> pillars
    ) {
        int yangCount = 0;
        int yinCount = 0;

        for (PillarResponse pillar : pillars) {
            if (!pillar.known()) {
                continue;
            }

            if ("양".equals(pillar.stemYinYang())) {
                yangCount++;
            } else {
                yinCount++;
            }

            if ("양".equals(pillar.branchYinYang())) {
                yangCount++;
            } else {
                yinCount++;
            }
        }

        int total =
                yangCount + yinCount;

        String balance;

        if (yangCount > yinCount) {
            balance = "양 기운 우세";
        } else if (yinCount > yangCount) {
            balance = "음 기운 우세";
        } else {
            balance = "음양 균형";
        }

        return new YinYangBalanceResponse(
                yangCount,
                yinCount,
                percentage(yangCount, total),
                percentage(yinCount, total),
                balance
        );
    }

    private TenGodBalanceResponse buildTenGodBalance(
            List<PillarResponse> pillars
    ) {
        Map<String, Integer> counts =
                TEN_GOD_ORDER
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Function.identity(),
                                        ignored -> 0,
                                        (first, second) -> first,
                                        LinkedHashMap::new
                                )
                        );

        for (PillarResponse pillar : pillars) {
            if (!pillar.known()) {
                continue;
            }

            if (
                    pillar.stemTenGod() != null
                            && counts.containsKey(
                            pillar.stemTenGod()
                    )
            ) {
                counts.compute(
                        pillar.stemTenGod(),
                        (key, value) -> value + 1
                );
            }

            for (
                    HiddenStemResponse hiddenStem
                    : pillar.hiddenStems()
            ) {
                if (
                        hiddenStem.tenGod() != null
                                && counts.containsKey(
                                hiddenStem.tenGod()
                        )
                ) {
                    counts.compute(
                            hiddenStem.tenGod(),
                            (key, value) -> value + 1
                    );
                }
            }
        }

        int total =
                counts.values()
                        .stream()
                        .mapToInt(Integer::intValue)
                        .sum();

        List<BalanceItemResponse> items =
                TEN_GOD_ORDER
                        .stream()
                        .map(
                                name -> new BalanceItemResponse(
                                        name,
                                        counts.get(name),
                                        percentage(
                                                counts.get(name),
                                                total
                                        )
                                )
                        )
                        .toList();

        String dominant =
                counts.entrySet()
                        .stream()
                        .max(
                                Map.Entry.comparingByValue()
                        )
                        .map(Map.Entry::getKey)
                        .orElse("없음");

        return new TenGodBalanceResponse(
                items,
                dominant
        );
    }

    private StrengthResponse buildStrength(
            String dayStem,
            ElementBalanceResponse balance
    ) {
        String dayElement =
                elementOfStem(dayStem);

        String resourceElement =
                findElementProducing(dayElement);

        String outputElement =
                PRODUCES.get(dayElement);

        String wealthElement =
                CONTROLS.get(dayElement);

        String officerElement =
                findElementControlling(dayElement);

        Map<String, Integer> counts =
                balance.items()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        BalanceItemResponse::name,
                                        BalanceItemResponse::count
                                )
                        );

        int supportCount =
                counts.getOrDefault(dayElement, 0)
                        + counts.getOrDefault(
                        resourceElement,
                        0
                );

        int total =
                Math.max(
                        balance.totalCount(),
                        1
                );

        int score =
                (int) Math.round(
                        supportCount
                                * 100.0
                                / total
                );

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
            usefulGod =
                    findWeakestElement(
                            counts,
                            List.of(
                                    outputElement,
                                    wealthElement,
                                    officerElement
                            )
                    );

            favorableGod =
                    outputElement;

            unfavorableGod =
                    dayElement;
        } else {
            usefulGod =
                    balance.weakest();

            favorableGod =
                    findWeakestElement(
                            counts,
                            List.of(
                                    resourceElement,
                                    outputElement
                            )
                    );

            unfavorableGod =
                    balance.dominant();
        }

        return new StrengthResponse(
                score,
                classification,
                dayElement,
                List.of(
                        resourceElement,
                        dayElement
                ),
                List.of(
                        outputElement,
                        wealthElement,
                        officerElement
                ),
                usefulGod,
                favorableGod,
                unfavorableGod,
                "팔자 원국의 표면 오행 개수를 기준으로 계산한 보조 판정입니다. "
                        + "계절 세력·조후·통근·투간을 포함한 전문 용신 판정과는 차이가 있을 수 있습니다."
        );
    }

    private StructureResponse buildStructure(
            EightChar eightChar,
            boolean timeUnknown
    ) {
        String lifePalace =
                timeUnknown
                        ? null
                        : eightChar.getMingGong();

        String bodyPalace =
                timeUnknown
                        ? null
                        : eightChar.getShenGong();

        return new StructureResponse(
                eightChar.getTaiYuan(),
                koreanGanZhi(
                        eightChar.getTaiYuan()
                ),
                eightChar.getTaiYuanNaYin(),
                eightChar.getTaiXi(),
                koreanGanZhi(
                        eightChar.getTaiXi()
                ),
                eightChar.getTaiXiNaYin(),
                lifePalace,
                koreanGanZhi(lifePalace),
                timeUnknown
                        ? null
                        : eightChar.getMingGongNaYin(),
                bodyPalace,
                koreanGanZhi(bodyPalace),
                timeUnknown
                        ? null
                        : eightChar.getShenGongNaYin(),
                timeUnknown
                        ? "출생시간을 모르므로 명궁과 신궁은 표시하지 않습니다."
                        : "출생시간을 기준으로 명궁과 신궁을 계산했습니다."
        );
    }

    private RelationResponse buildRelations(
            List<PillarResponse> pillars
    ) {
        List<PillarResponse> knownPillars =
                pillars.stream()
                        .filter(PillarResponse::known)
                        .toList();

        List<RelationItemResponse> stemRelations =
                new ArrayList<>();

        List<RelationItemResponse> branchRelations =
                new ArrayList<>();

        for (
                int first = 0;
                first < knownPillars.size();
                first++
        ) {
            for (
                    int second = first + 1;
                    second < knownPillars.size();
                    second++
            ) {
                PillarResponse left =
                        knownPillars.get(first);

                PillarResponse right =
                        knownPillars.get(second);

                String memberNames =
                        left.type()
                                + "-"
                                + right.type();

                findPairRelation(
                        STEM_COMBINATION,
                        left.stem(),
                        right.stem()
                ).ifPresent(
                        description -> stemRelations.add(
                                new RelationItemResponse(
                                        "천간합",
                                        memberNames,
                                        description
                                )
                        )
                );

                findPairRelation(
                        STEM_CLASH,
                        left.stem(),
                        right.stem()
                ).ifPresent(
                        description -> stemRelations.add(
                                new RelationItemResponse(
                                        "천간충",
                                        memberNames,
                                        description
                                )
                        )
                );

                findPairRelation(
                        BRANCH_COMBINATION,
                        left.branch(),
                        right.branch()
                ).ifPresent(
                        description -> branchRelations.add(
                                new RelationItemResponse(
                                        "지지육합",
                                        memberNames,
                                        description
                                )
                        )
                );

                findPairRelation(
                        BRANCH_CLASH,
                        left.branch(),
                        right.branch()
                ).ifPresent(
                        description -> branchRelations.add(
                                new RelationItemResponse(
                                        "지지충",
                                        memberNames,
                                        description
                                )
                        )
                );

                findPairRelation(
                        BRANCH_HARM,
                        left.branch(),
                        right.branch()
                ).ifPresent(
                        description -> branchRelations.add(
                                new RelationItemResponse(
                                        "지지해",
                                        memberNames,
                                        description
                                )
                        )
                );

                findPairRelation(
                        BRANCH_BREAK,
                        left.branch(),
                        right.branch()
                ).ifPresent(
                        description -> branchRelations.add(
                                new RelationItemResponse(
                                        "지지파",
                                        memberNames,
                                        description
                                )
                        )
                );
            }
        }

        Set<String> branchSet =
                knownPillars.stream()
                        .map(PillarResponse::branch)
                        .collect(Collectors.toSet());

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("申", "子", "辰"),
                "삼합",
                "신자진 수국"
        );

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("亥", "卯", "未"),
                "삼합",
                "해묘미 목국"
        );

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("寅", "午", "戌"),
                "삼합",
                "인오술 화국"
        );

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("巳", "酉", "丑"),
                "삼합",
                "사유축 금국"
        );

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("亥", "子", "丑"),
                "방합",
                "해자축 수방합"
        );

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("寅", "卯", "辰"),
                "방합",
                "인묘진 목방합"
        );

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("巳", "午", "未"),
                "방합",
                "사오미 화방합"
        );

        addTripleRelation(
                branchRelations,
                branchSet,
                Set.of("申", "酉", "戌"),
                "방합",
                "신유술 금방합"
        );

        addPunishments(
                branchRelations,
                knownPillars
        );

        return new RelationResponse(
                stemRelations,
                branchRelations
        );
    }

    private List<ShinsalResponse> buildShinsal(
            EightChar eightChar,
            List<PillarResponse> pillars
    ) {
        Map<String, ShinsalResponse> result =
                new LinkedHashMap<>();

        String dayStem =
                eightChar.getDayGan();

        String dayBranch =
                eightChar.getDayZhi();

        String yearBranch =
                eightChar.getYearZhi();

        addBranchBasedStars(
                result,
                dayBranch,
                "일지 기준",
                pillars
        );

        if (!Objects.equals(dayBranch, yearBranch)) {
            addBranchBasedStars(
                    result,
                    yearBranch,
                    "년지 기준",
                    pillars
            );
        }

        for (
                String target
                : noblemanTargets(dayStem)
        ) {
            addStarIfPresent(
                    result,
                    "천을귀인",
                    target,
                    pillars,
                    "어려움 속에서 도움이나 해결의 계기를 상징하는 귀인성입니다."
            );
        }

        String literaryTarget =
                literaryStarTarget(dayStem);

        addStarIfPresent(
                result,
                "문창귀인",
                literaryTarget,
                pillars,
                "학습·문서·표현력·기획 능력과 관련해 참고하는 귀인성입니다."
        );

        String bladeTarget =
                bladeStarTarget(dayStem);

        addStarIfPresent(
                result,
                "양인살",
                bladeTarget,
                pillars,
                "강한 추진력과 결단력을 나타내지만 과도하면 충돌과 무리수를 경계합니다."
        );

        if (
                Set.of(
                        "庚辰",
                        "庚戌",
                        "壬辰",
                        "戊戌"
                ).contains(eightChar.getDay())
        ) {
            result.put(
                    "괴강살-" + eightChar.getDay(),
                    new ShinsalResponse(
                            "괴강살",
                            "일주",
                            "강한 자존감·결단력·독립성을 상징하는 특수 일주로 봅니다."
                    )
            );
        }

        if (
                eightChar.getDayXunKong() != null
                        && !eightChar.getDayXunKong().isBlank()
        ) {
            result.put(
                    "공망",
                    new ShinsalResponse(
                            "공망",
                            eightChar.getDayXunKong(),
                            "일주 기준 공망 지지입니다. 해당 기운이 비어 있거나 다른 방식으로 나타날 가능성을 참고합니다."
                    )
            );
        }

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

        return new ArrayList<>(
                result.values()
        );
    }

    private LuckResponse buildLuck(
            BirthProfile profile,
            EightChar eightChar
    ) {
        int gender =
                profile.getGender() == Gender.MALE
                        ? 1
                        : 0;

        // 방식 2: 절입 시각과의 분 차이로 기운 시점 계산
        Yun yun =
                eightChar.getYun(
                        gender,
                        2
                );

        Solar startSolar =
                yun.getStartSolar();

        String startDate =
                String.format(
                        "%04d-%02d-%02d %02d:%02d",
                        startSolar.getYear(),
                        startSolar.getMonth(),
                        startSolar.getDay(),
                        startSolar.getHour(),
                        startSolar.getMinute()
                );

        String startAge =
                yun.getStartYear()
                        + "년 "
                        + yun.getStartMonth()
                        + "개월 "
                        + yun.getStartDay()
                        + "일 "
                        + yun.getStartHour()
                        + "시간";

        int currentYear =
                LocalDate.now().getYear();

        String dayStem =
                eightChar.getDayGan();

        String currentDecade =
                "현재 대운 없음";

        List<DecadeFortuneResponse> decadeResponses =
                new ArrayList<>();

        DaYun[] daYuns =
                yun.getDaYun(9);

        for (DaYun daYun : daYuns) {
            if (daYun.getIndex() < 1) {
                continue;
            }

            String ganZhi =
                    daYun.getGanZhi();

            boolean current =
                    currentYear >= daYun.getStartYear()
                            && currentYear <= daYun.getEndYear();

            if (current) {
                currentDecade =
                        koreanGanZhi(ganZhi)
                                + " 대운";
            }

            List<YearFortuneResponse> yearlyFortunes =
                    Arrays.stream(
                                    daYun.getLiuNian()
                            )
                            .map(
                                    liuNian -> buildYearFortune(
                                            dayStem,
                                            liuNian
                                    )
                            )
                            .toList();

            decadeResponses.add(
                    new DecadeFortuneResponse(
                            daYun.getStartAge(),
                            daYun.getEndAge(),
                            daYun.getStartYear(),
                            daYun.getEndYear(),
                            ganZhi,
                            koreanGanZhi(ganZhi),
                            ganZhiElement(ganZhi),
                            tenGodOfStem(
                                    dayStem,
                                    firstCharacter(ganZhi)
                            ),
                            daYun.getXun(),
                            daYun.getXunKong(),
                            current,
                            yearlyFortunes
                    )
            );
        }

        boolean approximate =
                profile.isBirthTimeUnknown();

        return new LuckResponse(
                yun.isForward(),
                yun.isForward()
                        ? "순행"
                        : "역행",
                startAge,
                startDate,
                approximate,
                currentDecade,
                decadeResponses,
                approximate
                        ? "출생시간이 없어 정오를 기준으로 기운 시점을 계산했으므로 대운 시작 시점은 참고값입니다."
                        : "출생시간과 절입 시각 차이를 분 단위로 반영한 기운 시점입니다."
        );
    }

    private YearFortuneResponse buildYearFortune(
            String dayStem,
            LiuNian liuNian
    ) {
        String ganZhi =
                liuNian.getGanZhi();

        return new YearFortuneResponse(
                liuNian.getYear(),
                liuNian.getAge(),
                ganZhi,
                koreanGanZhi(ganZhi),
                ganZhiElement(ganZhi),
                tenGodOfStem(
                        dayStem,
                        firstCharacter(ganZhi)
                ),
                liuNian.getXun(),
                liuNian.getXunKong()
        );
    }

    private List<InterpretationResponse> buildInterpretations(
            BirthProfile profile,
            EightChar eightChar,
            ElementBalanceResponse elementBalance,
            TenGodBalanceResponse tenGodBalance,
            StrengthResponse strength
    ) {
        String dayElement =
                elementOfStem(
                        eightChar.getDayGan()
                );

        String dominantElement =
                elementBalance.dominant();

        String dominantTenGod =
                tenGodBalance.dominant();

        List<InterpretationResponse> result =
                new ArrayList<>();

        result.add(
                new InterpretationResponse(
                        "성격",
                        "일간과 오행으로 보는 기본 성향",
                        profile.getName()
                                + "님의 일간은 "
                                + koreanStem(eightChar.getDayGan())
                                + "이며 "
                                + dayElement
                                + "의 성향을 기본으로 가집니다. "
                                + dominantElement
                                + " 기운이 강하게 나타나므로 "
                                + elementPersonality(
                                dominantElement
                        )
                                + " 성향이 함께 두드러질 수 있습니다."
                )
        );

        result.add(
                new InterpretationResponse(
                        "연애",
                        "관계에서 나타나는 흐름",
                        "연애와 인간관계에서는 "
                                + dominantTenGod
                                + "의 작용을 먼저 참고할 수 있습니다. "
                                + tenGodRelationship(
                                dominantTenGod
                        )
                                + " 다만 합·충·형·해와 배우자궁을 함께 살펴야 구체적인 관계 흐름을 판단할 수 있습니다."
                )
        );

        result.add(
                new InterpretationResponse(
                        "재물",
                        "재물의 관리 방식",
                        "재성은 편재와 정재의 분포로 살펴봅니다. "
                                + "현재 원국은 "
                                + strength.classification()
                                + " 보조 판정이므로, 재물을 얻는 것뿐 아니라 "
                                + "일간이 재성을 감당할 수 있는 균형을 함께 확인해야 합니다."
                )
        );

        result.add(
                new InterpretationResponse(
                        "직업",
                        "직업과 사회적 역할",
                        dominantElement
                                + " 기운과 "
                                + dominantTenGod
                                + "이 상대적으로 두드러집니다. "
                                + careerSuggestion(
                                dominantElement,
                                dominantTenGod
                        )
                )
        );

        result.add(
                new InterpretationResponse(
                        "건강",
                        "오행 균형 관점의 생활 관리",
                        elementBalance.weakest()
                                + " 기운이 상대적으로 적게 나타납니다. "
                                + "이는 의학적 진단이 아니라 오행 균형 관점의 참고값이며, "
                                + "수면·식사·운동처럼 현실적인 생활 습관을 우선 관리해야 합니다."
                )
        );

        result.add(
                new InterpretationResponse(
                        "시험",
                        "학습과 시험 준비 경향",
                        "인성은 학습과 흡수력, 식상은 표현과 답안 작성 능력과 연결해 봅니다. "
                                + "문창귀인과 대운·세운을 함께 확인하되, 실제 합격 가능성은 "
                                + "학습량과 문제풀이 정확도 같은 현실 조건이 가장 크게 좌우합니다."
                )
        );

        result.add(
                new InterpretationResponse(
                        "창업",
                        "사업과 독립 활동의 방향",
                        "창업은 식상·재성·비겁의 균형과 현재 대운을 함께 살펴야 합니다. "
                                + strength.usefulGod()
                                + " 기운을 보완하는 환경을 만들고, "
                                + "자금·매출·운영 데이터를 기준으로 단계적으로 확장하는 방식이 안전합니다."
                )
        );

        return result;
    }

    private void addBranchBasedStars(
            Map<String, ShinsalResponse> result,
            String baseBranch,
            String basis,
            List<PillarResponse> pillars
    ) {
        String peach =
                switch (branchGroup(baseBranch)) {
                    case "신자진" -> "酉";
                    case "인오술" -> "卯";
                    case "해묘미" -> "子";
                    case "사유축" -> "午";
                    default -> null;
                };

        String travel =
                switch (branchGroup(baseBranch)) {
                    case "신자진" -> "寅";
                    case "인오술" -> "申";
                    case "해묘미" -> "巳";
                    case "사유축" -> "亥";
                    default -> null;
                };

        String canopy =
                switch (branchGroup(baseBranch)) {
                    case "신자진" -> "辰";
                    case "인오술" -> "戌";
                    case "해묘미" -> "未";
                    case "사유축" -> "丑";
                    default -> null;
                };

        addStarIfPresent(
                result,
                "도화살",
                peach,
                pillars,
                basis + " 도화로 매력·관계·표현력의 작용을 참고합니다."
        );

        addStarIfPresent(
                result,
                "역마살",
                travel,
                pillars,
                basis + " 역마로 이동·변화·활동성의 작용을 참고합니다."
        );

        addStarIfPresent(
                result,
                "화개살",
                canopy,
                pillars,
                basis + " 화개로 몰입·예술·철학·종교적 관심의 작용을 참고합니다."
        );
    }

    private void addStarIfPresent(
            Map<String, ShinsalResponse> result,
            String name,
            String targetBranch,
            List<PillarResponse> pillars,
            String description
    ) {
        if (targetBranch == null) {
            return;
        }

        String location =
                findBranchLocations(
                        pillars,
                        targetBranch
                );

        if (location.isBlank()) {
            return;
        }

        result.putIfAbsent(
                name + "-" + location,
                new ShinsalResponse(
                        name,
                        location,
                        description
                )
        );
    }

    private void addPairShinsal(
            Map<String, ShinsalResponse> result,
            List<PillarResponse> pillars,
            Map<String, String> combinations,
            String name,
            String description
    ) {
        List<PillarResponse> known =
                pillars.stream()
                        .filter(PillarResponse::known)
                        .toList();

        for (
                int first = 0;
                first < known.size();
                first++
        ) {
            for (
                    int second = first + 1;
                    second < known.size();
                    second++
            ) {
                PillarResponse left =
                        known.get(first);

                PillarResponse right =
                        known.get(second);

                Optional<String> pair =
                        findPairRelation(
                                combinations,
                                left.branch(),
                                right.branch()
                        );

                pair.ifPresent(
                        detail -> result.putIfAbsent(
                                name
                                        + "-"
                                        + left.type()
                                        + "-"
                                        + right.type(),
                                new ShinsalResponse(
                                        name,
                                        left.type()
                                                + "-"
                                                + right.type(),
                                        detail
                                                + " · "
                                                + description
                                )
                        )
                );
            }
        }
    }

    private List<String> noblemanTargets(
            String dayStem
    ) {
        return switch (dayStem) {
            case "甲", "戊", "庚" ->
                    List.of("丑", "未");

            case "乙", "己" ->
                    List.of("子", "申");

            case "丙", "丁" ->
                    List.of("亥", "酉");

            case "壬", "癸" ->
                    List.of("卯", "巳");

            case "辛" ->
                    List.of("寅", "午");

            default ->
                    List.of();
        };
    }

    private String literaryStarTarget(
            String dayStem
    ) {
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

    private String bladeStarTarget(
            String dayStem
    ) {
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

    private String branchGroup(
            String branch
    ) {
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

    private String findBranchLocations(
            List<PillarResponse> pillars,
            String targetBranch
    ) {
        return pillars.stream()
                .filter(PillarResponse::known)
                .filter(
                        pillar -> Objects.equals(
                                pillar.branch(),
                                targetBranch
                        )
                )
                .map(PillarResponse::type)
                .collect(
                        Collectors.joining(", ")
                );
    }

    private void addTripleRelation(
            List<RelationItemResponse> result,
            Set<String> branchSet,
            Set<String> targets,
            String category,
            String description
    ) {
        if (branchSet.containsAll(targets)) {
            result.add(
                    new RelationItemResponse(
                            category,
                            String.join("", targets),
                            description
                    )
            );
        }
    }

    private void addPunishments(
            List<RelationItemResponse> result,
            List<PillarResponse> pillars
    ) {
        Set<String> branches =
                pillars.stream()
                        .map(PillarResponse::branch)
                        .collect(Collectors.toSet());

        if (
                branches.containsAll(
                        Set.of("寅", "巳", "申")
                )
        ) {
            result.add(
                    new RelationItemResponse(
                            "삼형",
                            "인·사·신",
                            "인사신 무은지형"
                    )
            );
        }

        if (
                branches.containsAll(
                        Set.of("丑", "戌", "未")
                )
        ) {
            result.add(
                    new RelationItemResponse(
                            "삼형",
                            "축·술·미",
                            "축술미 지세지형"
                    )
            );
        }

        if (
                branches.contains("子")
                        && branches.contains("卯")
        ) {
            result.add(
                    new RelationItemResponse(
                            "상형",
                            "자·묘",
                            "자묘 무례지형"
                    )
            );
        }

        Map<String, Long> countMap =
                pillars.stream()
                        .map(PillarResponse::branch)
                        .collect(
                                Collectors.groupingBy(
                                        Function.identity(),
                                        Collectors.counting()
                                )
                        );

        for (
                String selfPunishment
                : List.of("辰", "午", "酉", "亥")
        ) {
            if (
                    countMap.getOrDefault(
                            selfPunishment,
                            0L
                    ) >= 2
            ) {
                result.add(
                        new RelationItemResponse(
                                "자형",
                                koreanBranch(
                                        selfPunishment
                                ),
                                koreanBranch(
                                        selfPunishment
                                ) + " 자형"
                        )
                );
            }
        }
    }

    private Optional<String> findPairRelation(
            Map<String, String> relationMap,
            String left,
            String right
    ) {
        String direct =
                relationMap.get(left + right);

        if (direct != null) {
            return Optional.of(direct);
        }

        return Optional.ofNullable(
                relationMap.get(right + left)
        );
    }

    private String findElementByCount(
            Map<String, Integer> counts,
            Comparator<Integer> comparator
    ) {
        return counts.entrySet()
                .stream()
                .sorted(
                        (
                                left,
                                right
                        ) -> comparator.compare(
                                left.getValue(),
                                right.getValue()
                        )
                )
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("없음");
    }

    private String findWeakestElement(
            Map<String, Integer> counts,
            List<String> candidates
    ) {
        return candidates.stream()
                .min(
                        Comparator.comparingInt(
                                element -> counts.getOrDefault(
                                        element,
                                        0
                                )
                        )
                )
                .orElse(candidates.getFirst());
    }

    private String findElementProducing(
            String targetElement
    ) {
        return PRODUCES.entrySet()
                .stream()
                .filter(
                        entry -> Objects.equals(
                                entry.getValue(),
                                targetElement
                        )
                )
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
    }

    private String findElementControlling(
            String targetElement
    ) {
        return CONTROLS.entrySet()
                .stream()
                .filter(
                        entry -> Objects.equals(
                                entry.getValue(),
                                targetElement
                        )
                )
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
    }

    private Map<String, Integer> createElementCountMap() {
        Map<String, Integer> counts =
                new LinkedHashMap<>();

        for (String element : ELEMENT_ORDER) {
            counts.put(element, 0);
        }

        return counts;
    }

    private double percentage(
            int value,
            int total
    ) {
        if (total <= 0) {
            return 0;
        }

        return Math.round(
                value * 1000.0 / total
        ) / 10.0;
    }

    private String koreanStem(
            String stem
    ) {
        if (stem == null) {
            return null;
        }

        return STEM_KOREAN.getOrDefault(
                stem,
                stem
        );
    }

    private String koreanBranch(
            String branch
    ) {
        if (branch == null) {
            return null;
        }

        return BRANCH_KOREAN.getOrDefault(
                branch,
                branch
        );
    }

    private String koreanGanZhi(
            String ganZhi
    ) {
        if (
                ganZhi == null
                        || ganZhi.length() < 2
        ) {
            return ganZhi;
        }

        return koreanStem(
                firstCharacter(ganZhi)
        ) + koreanBranch(
                secondCharacter(ganZhi)
        );
    }

    private String elementOfStem(
            String stem
    ) {
        return STEM_ELEMENT.getOrDefault(
                stem,
                ""
        );
    }

    private String elementOfBranch(
            String branch
    ) {
        return BRANCH_ELEMENT.getOrDefault(
                branch,
                ""
        );
    }

    private String yinYangOfStem(
            String stem
    ) {
        return YANG_STEMS.contains(stem)
                ? "양"
                : "음";
    }

    private String yinYangOfBranch(
            String branch
    ) {
        return YANG_BRANCHES.contains(branch)
                ? "양"
                : "음";
    }

    private String translateTenGod(
            String tenGod
    ) {
        if (tenGod == null) {
            return null;
        }

        return TEN_GOD_KOREAN.getOrDefault(
                tenGod,
                tenGod
        );
    }

    private String ganZhiElement(
            String ganZhi
    ) {
        if (
                ganZhi == null
                        || ganZhi.length() < 2
        ) {
            return "";
        }

        return elementOfStem(
                firstCharacter(ganZhi)
        )
                + "/"
                + elementOfBranch(
                secondCharacter(ganZhi)
        );
    }

    private String tenGodOfStem(
            String dayStem,
            String targetStem
    ) {
        String dayElement =
                elementOfStem(dayStem);

        String targetElement =
                elementOfStem(targetStem);

        boolean samePolarity =
                Objects.equals(
                        yinYangOfStem(dayStem),
                        yinYangOfStem(targetStem)
                );

        if (
                Objects.equals(
                        dayElement,
                        targetElement
                )
        ) {
            return samePolarity
                    ? "비견"
                    : "겁재";
        }

        if (
                Objects.equals(
                        PRODUCES.get(dayElement),
                        targetElement
                )
        ) {
            return samePolarity
                    ? "식신"
                    : "상관";
        }

        if (
                Objects.equals(
                        CONTROLS.get(dayElement),
                        targetElement
                )
        ) {
            return samePolarity
                    ? "편재"
                    : "정재";
        }

        if (
                Objects.equals(
                        CONTROLS.get(targetElement),
                        dayElement
                )
        ) {
            return samePolarity
                    ? "편관"
                    : "정관";
        }

        if (
                Objects.equals(
                        PRODUCES.get(targetElement),
                        dayElement
                )
        ) {
            return samePolarity
                    ? "편인"
                    : "정인";
        }

        return "";
    }

    private String firstCharacter(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return "";
        }

        return value.substring(0, 1);
    }

    private String secondCharacter(
            String value
    ) {
        if (
                value == null
                        || value.length() < 2
        ) {
            return "";
        }

        return value.substring(1, 2);
    }

    private String elementPersonality(
            String element
    ) {
        return switch (element) {
            case "목" ->
                    "성장·기획·확장";

            case "화" ->
                    "표현·열정·속도";

            case "토" ->
                    "안정·책임·현실감";

            case "금" ->
                    "판단·원칙·정리";

            case "수" ->
                    "관찰·유연성·사고";

            default ->
                    "균형";
        };
    }

    private String tenGodRelationship(
            String tenGod
    ) {
        return switch (tenGod) {
            case "비견", "겁재" ->
                    "대등한 관계와 주도권이 중요하게 작용할 수 있습니다.";

            case "식신", "상관" ->
                    "대화와 감정 표현이 관계 만족도에 큰 영향을 줄 수 있습니다.";

            case "편재", "정재" ->
                    "현실적인 신뢰와 약속을 중요하게 여길 가능성이 있습니다.";

            case "편관", "정관" ->
                    "책임감과 관계의 기준을 분명하게 세우려는 경향이 있을 수 있습니다.";

            case "편인", "정인" ->
                    "정서적 이해와 안정감을 중요하게 느낄 가능성이 있습니다.";

            default ->
                    "서로의 속도와 표현 방식을 확인하는 것이 중요합니다.";
        };
    }

    private String careerSuggestion(
            String element,
            String tenGod
    ) {
        return switch (element) {
            case "목" ->
                    "기획·교육·콘텐츠·성장 산업처럼 확장성을 다루는 분야를 검토할 수 있습니다.";

            case "화" ->
                    "홍보·디자인·발표·서비스처럼 표현과 반응 속도가 중요한 분야를 검토할 수 있습니다.";

            case "토" ->
                    "운영·관리·부동산·회계처럼 안정성과 지속성이 필요한 분야를 검토할 수 있습니다.";

            case "금" ->
                    "기술·보안·법무·품질관리처럼 기준과 정확성이 중요한 분야를 검토할 수 있습니다.";

            case "수" ->
                    "연구·데이터·상담·유통처럼 정보와 흐름을 다루는 분야를 검토할 수 있습니다.";

            default ->
                    tenGod
                            + " 특성을 현실 경력과 함께 비교해보는 것이 좋습니다.";
        };
    }

    private record CalendarCalculation(

            LocalDate solarDate,

            String lunarDate,

            boolean lunarLeapMonth
    ) {
    }
}