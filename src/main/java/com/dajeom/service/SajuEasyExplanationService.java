package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuAnalysisResponse.BalanceItemResponse;
import com.dajeom.dto.SajuAnalysisResponse.DecadeFortuneResponse;
import com.dajeom.dto.SajuAnalysisResponse.InterpretationResponse;
import com.dajeom.dto.SajuAnalysisResponse.RelationResponse;
import com.dajeom.dto.SajuAnalysisResponse.YearFortuneResponse;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SajuEasyExplanationService {

    private static final ZoneId KOREA_ZONE =
            ZoneId.of(
                    "Asia/Seoul"
            );

    private static final int MIN_DEEP_LENGTH = 1800;
    private static final int MAX_DEEP_LENGTH = 2200;

    /**
     * 만세력 계산값은 변경하지 않고
     * 반말 형식의 심층 해설을 결합한다.
     */
    public SajuAnalysisResponse enrich(
            SajuAnalysisResponse source
    ) {
        if (
                source == null
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 결과가 없습니다."
            );
        }

        SajuFacts facts =
                extractFacts(
                        source
                );

        String summary =
                buildSummary(
                        facts
                );

        List<InterpretationResponse> interpretations =
                buildInterpretations(
                        facts
                );

        return new SajuAnalysisResponse(
                source.analysisId(),
                source.profileId(),
                source.status(),
                source.name(),
                source.calendar(),
                source.zodiac(),
                source.dayMaster(),
                summary,
                source.pillars(),
                source.elementBalance(),
                source.yinYangBalance(),
                source.tenGodBalance(),
                source.structure(),
                source.strength(),
                source.relations(),
                source.shinsal(),
                source.luck(),
                interpretations,
                "이 해설은 전통 명리 계산을 바탕으로 성향과 선택의 패턴을 분석한 참고 자료야. "
                        + "질병, 사고, 이혼, 재혼, 소송이나 특정 사건을 확정하는 예언으로 사용하면 안 돼.",
                source.completedAt()
        );
    }

    /**
     * 전체 결과 화면 상단에 표시할 핵심 요약을 만든다.
     */
    private String buildSummary(
            SajuFacts facts
    ) {
        return sanitizeNarrative(
                facts.name()
                        + ", 결론부터 말할게. "
                        + "너는 "
                        + elementCoreTrait(
                        facts.dominantElement()
                )
                        + " 힘이 강하고, "
                        + facts.dominantTenGod()
                        + "의 성향이 반복해서 드러나는 사람이야. "
                        + strengthCoreTrait(
                        facts.strengthClassification()
                )
                        + " 네 장점은 분명해. "
                        + tenGodAdvantage(
                        facts.dominantTenGod()
                )
                        + " 하지만 강한 기운을 계속 같은 방식으로 쓰면 "
                        + tenGodRisk(
                        facts.dominantTenGod()
                )
                        + " 문제가 생기기 쉬워. "
                        + "특히 "
                        + facts.weakestElement()
                        + " 기운이 상대적으로 부족해서 "
                        + elementCompensation(
                        facts.weakestElement()
                )
                        + " 습관을 의식적으로 만들어야 해. "
                        + "이 사주의 핵심은 운이 대신 해결해주는 구조가 아니라 "
                        + "네 판단력과 실행 방식을 제대로 다듬을수록 결과가 커지는 구조라는 거야."
        );
    }

    /**
     * 현대사주와 전통사주 심층 해설을 생성한다.
     */
    private List<InterpretationResponse> buildInterpretations(
            SajuFacts facts
    ) {
        List<InterpretationResponse> result =
                new ArrayList<>();

        for (
                DeepTopic topic : DeepTopic.values()
        ) {
            result.add(
                    new InterpretationResponse(
                            topic.category(),
                            topic.title(
                                    facts.reportYear()
                            ),
                            buildDeepContent(
                                    topic,
                                    facts
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 카테고리별 약 1,800자에서 2,200자의 해설을 만든다.
     */
    private String buildDeepContent(
            DeepTopic topic,
            SajuFacts facts
    ) {
        String content =
                topicOpening(
                        topic,
                        facts
                )
                        + "\n\n"
                        + topicCore(
                        topic,
                        facts
                )
                        + "\n\n"
                        + topicEvidence(
                        topic,
                        facts
                )
                        + "\n\n"
                        + topicReality(
                        topic,
                        facts
                )
                        + "\n\n"
                        + topicAction(
                        topic,
                        facts
                )
                        + "\n\n"
                        + topicLimitation(
                        topic,
                        facts
                );

        return ensureDeepLength(
                sanitizeNarrative(
                        content
                ),
                topic,
                facts
        );
    }

    private String topicOpening(
            DeepTopic topic,
            SajuFacts facts
    ) {
        return switch (
                topic
                ) {
            case BORN_OVERALL ->
                    "너는 단순히 운이 좋다거나 나쁘다고 한 줄로 정리할 수 있는 사람이 아니야. "
                            + "강한 부분은 아주 분명하고 약한 부분도 숨기기 어려운 구조야. "
                            + "그래서 잘 풀릴 때는 속도가 빠르지만, 잘못된 방향으로 확신하면 손해도 오래 끌고 갈 수 있어.";

            case YEAR_OVERALL ->
                    facts.reportYear()
                            + "년은 네가 무엇을 더 벌일지가 아니라 무엇을 끝까지 가져갈지를 정해야 하는 해야. "
                            + "기회가 없어서 답답한 해가 아니라 선택지가 생길수록 기준이 더 중요해지는 해에 가까워.";

            case FUTURE_OVERALL ->
                    "앞으로의 흐름은 갑자기 인생이 뒤집히는 식보다 네가 반복해온 선택의 결과가 점점 크게 드러나는 쪽이야. "
                            + "좋은 습관은 자산이 되고 잘못된 습관은 부담으로 돌아오게 돼.";

            case LOVE_STYLE ->
                    "연애에서 너는 가볍게 만나고 쉽게 정리하는 타입은 아니야. "
                            + "마음을 열기 전까지는 관찰하지만 한번 내 사람이라고 판단하면 책임까지 떠안으려는 성향이 있어.";

            case MEETING_PERSON ->
                    "네가 만나기 쉬운 사람은 단순히 특정 외모나 직업으로 정해지는 게 아니야. "
                            + "네가 어떤 결핍과 욕구를 가지고 있느냐에 따라 반복해서 끌리는 성격 유형이 생기는 거야.";

            case COMPATIBILITY ->
                    "너와 궁합이 좋은 사람은 무조건 착하거나 순한 사람이 아니야. "
                            + "네 강한 부분을 감당하면서도 자기 몫을 스스로 책임지는 사람이 맞아.";

            case PAST ->
                    "과거운을 보면 너는 편하게 보호만 받으며 자란 사람의 방식보다 상황을 빨리 읽고 스스로 판단하는 방식이 강하게 남기 쉬워.";

            case PARENTS ->
                    "부모와의 연은 좋다, 나쁘다로 단순하게 끊을 수 없어. "
                            + "정은 있어도 표현이 어긋날 수 있고, 가까이 있어도 마음의 거리가 생길 수 있는 구조를 함께 봐야 해.";

            case SIBLINGS ->
                    "형제운은 형제가 있느냐보다 경쟁, 비교, 책임 분담이 어떤 방식으로 나타나는지를 봐야 해. "
                            + "형제가 없어도 친구나 동료 관계에서 같은 패턴이 드러날 수 있어.";

            case MONEY ->
                    "금전운부터 명확하게 말하면 돈이 아예 안 붙는 구조라고 볼 수는 없어. "
                            + "문제는 버는 능력과 지키는 능력이 반드시 같은 건 아니라는 거야.";

            case WEALTH ->
                    "재물운은 한 번에 큰돈을 잡느냐보다 들어온 돈을 자산으로 남길 수 있느냐가 핵심이야. "
                            + "네 사주에서는 돈을 움직이는 방식과 책임지는 방식이 재물의 크기를 결정해.";

            case LEGAL ->
                    "관재수가 있다고 무조건 경찰서나 법원에 간다는 뜻은 아니야. "
                            + "말, 계약, 돈, 책임 문제를 감정적으로 처리할 때 분쟁이 공식화될 가능성을 보는 거야.";

            case CURRENT ->
                    "현재운은 네가 지금 어떤 문제를 반복해서 마주치고 있는지를 보면 더 정확해. "
                            + "대운은 사건 날짜가 아니라 삶의 배경과 과제를 보여주는 흐름이야.";

            case FUTURE ->
                    "미래운은 정해진 사건표가 아니야. "
                            + "원국의 성향과 현재 대운이 만나면서 어떤 선택이 더 유리하고 어떤 행동이 손실을 만드는지를 보는 거야.";
        };
    }

    private String topicCore(
            DeepTopic topic,
            SajuFacts facts
    ) {
        String strengthText =
                strengthCoreTrait(
                        facts.strengthClassification()
                );

        String elementText =
                elementCoreTrait(
                        facts.dominantElement()
                );

        String tenGodText =
                tenGodCoreTrait(
                        facts.dominantTenGod()
                );

        return switch (
                topic
                ) {
            case BORN_OVERALL ->
                    "네 원국에서 가장 먼저 보이는 건 "
                            + facts.dominantElement()
                            + " 기운의 우세야. 이 기운은 "
                            + elementText
                            + " 성향으로 나타나. "
                            + "여기에 "
                            + facts.dominantTenGod()
                            + "이 강하게 잡혀 있어서 "
                            + tenGodText
                            + " 행동이 반복되기 쉬워. "
                            + strengthText
                            + " 그래서 남들이 보기에는 버티는 힘과 판단력이 있어 보이지만, "
                            + "실제로는 모든 문제를 혼자 정리하려다가 피로를 쌓을 수 있어. "
                            + "좋은 운이 들어와도 네가 기준 없이 여러 일을 동시에 잡으면 결과가 흩어지고, "
                            + "반대로 한 가지 목표를 정해서 기록과 실적을 남기면 같은 기운이 강한 성취로 바뀌어. "
                            + "너는 운을 기다리는 사람보다 구조를 만들 때 강한 사람이야.";

            case YEAR_OVERALL ->
                    buildYearCore(
                            facts
                    );

            case FUTURE_OVERALL ->
                    "앞으로는 "
                            + cleanDecadeLabel(
                            facts.currentDecade()
                    )
                            + "의 큰 주제와 네 원국의 "
                            + facts.dominantTenGod()
                            + " 성향이 함께 작용해. "
                            + "이 흐름에서는 책임과 선택의 범위가 커질수록 네 실력이 드러나지만, "
                            + "준비되지 않은 확장이나 사람을 믿고 시작한 일은 부담으로 바뀔 수 있어. "
                            + "용신으로 분류된 "
                            + facts.usefulGod()
                            + " 기운은 네가 균형을 잡는 방향이고, 희신인 "
                            + facts.favorableGod()
                            + " 기운은 그 방향을 보조해. "
                            + "기신인 "
                            + facts.unfavorableGod()
                            + "의 성향이 지나치게 강해질 때는 조급함, 과신, 감정적인 결정 같은 형태를 먼저 점검해야 해. "
                            + "미래가 좋아지는 조건은 단순해. 네가 잘하는 일을 반복 가능한 수입과 경력으로 만들고, "
                            + "사람과 돈의 경계를 문서로 분명히 하는 거야.";

            case LOVE_STYLE ->
                    romanceCore(
                            facts
                    );

            case MEETING_PERSON ->
                    "너는 처음부터 지나치게 의존적인 사람보다 자기 색깔과 능력이 있는 사람에게 관심을 느끼기 쉬워. "
                            + "문제는 강한 매력과 안정적인 관계가 같은 것은 아니라는 거야. "
                            + facts.dominantTenGod()
                            + " 성향이 강하면 상대의 추진력이나 독립성을 매력으로 느낄 수 있지만, "
                            + "그 사람이 자기 책임을 지지 않으면 결국 네가 뒤처리를 맡게 돼. "
                            + "반대로 지나치게 수동적인 사람을 만나면 처음에는 편해 보여도 시간이 지나면서 답답함이 커질 수 있어. "
                            + "네가 만나기 쉬운 사람은 말이 화려한 사람보다 상황을 빠르게 움직이는 사람, "
                            + "자신의 문제를 강하게 이야기하는 사람, 네 능력과 책임감을 기대하는 사람일 가능성이 있어. "
                            + "따라서 상대가 얼마나 매력적인가보다 자신의 생활과 돈, 감정을 스스로 관리하는지를 봐야 해.";

            case COMPATIBILITY ->
                    "궁합에서 가장 중요한 건 오행 하나가 맞는지가 아니야. "
                            + "너는 "
                            + facts.dominantElement()
                            + " 기운이 강하기 때문에 네 속도와 기준을 무조건 따라오기만 하는 사람보다 "
                            + facts.weakestElement()
                            + " 기운의 장점을 현실적으로 보여주는 사람이 균형에 도움이 돼. "
                            + elementCompensation(
                            facts.weakestElement()
                    )
                            + " 태도를 가진 사람이 잘 맞을 수 있다는 뜻이야. "
                            + "또한 네가 "
                            + facts.strengthClassification()
                            + " 구조라면 상대가 자기 의견은 분명하되 감정적으로 맞서기만 하지 않는 게 중요해. "
                            + "좋은 궁합은 서로 부족한 부분을 메워주는 관계이면서도 돈과 책임을 한쪽에 몰아주지 않는 관계야. "
                            + "말이 잘 통하는 것보다 갈등이 생겼을 때 어떻게 해결하는지가 더 중요해.";

            case PAST ->
                    "원국의 강한 기운과 "
                            + facts.strengthClassification()
                            + " 판정을 함께 보면 어릴 때부터 상황을 눈치채고 자기 방식으로 버티는 습관이 형성됐을 가능성이 있어. "
                            + "이게 반드시 가정환경이 나빴다는 뜻은 아니야. "
                            + "부모가 바빴거나 감정 표현이 적었거나, 네가 스스로 책임져야 하는 일이 또래보다 빨리 생겼을 수도 있어. "
                            + "관계 요소가 "
                            + facts.totalRelationCount()
                            + "개 확인된다는 점은 가까운 사람 사이에서도 말과 기대가 어긋나는 경험이 반복됐을 가능성을 보여줘. "
                            + "그래서 과거의 너는 도움을 요청하기보다 혼자 해결한 뒤 결과만 보여주는 방식을 선택했을 수 있어. "
                            + "이 습관은 독립성을 키웠지만 감정과 피로를 늦게 알아차리는 약점도 만들었을 가능성이 있어.";

            case PARENTS ->
                    parentCore(
                            facts
                    );

            case SIBLINGS ->
                    siblingCore(
                            facts
                    );

            case MONEY ->
                    moneyCore(
                            facts,
                            false
                    );

            case WEALTH ->
                    moneyCore(
                            facts,
                            true
                    );

            case LEGAL ->
                    legalCore(
                            facts
                    );

            case CURRENT ->
                    "현재 대운은 "
                            + cleanDecadeLabel(
                            facts.currentDecade()
                    )
                            + "으로 계산돼 있고 흐름은 "
                            + facts.luckDirection()
                            + " 방식으로 이어져. "
                            + "이 시기의 핵심은 좋은 일이 생기느냐보다 어떤 책임과 선택이 계속 반복되느냐야. "
                            + "직업에서 역할은 커지는데 보상이 따라오지 않는지, "
                            + "사람 문제 때문에 돈과 시간이 빠져나가는지, "
                            + "새로운 일을 준비하면서 기존 일을 정리하지 못하는지를 봐야 해. "
                            + "현재운이 강하게 작용할수록 네 원국의 "
                            + facts.dominantTenGod()
                            + " 성향도 더 선명해져. "
                            + "장점은 추진력과 집중력으로 나타나지만 단점은 고집과 과부담으로 나타날 수 있어.";

            case FUTURE ->
                    "미래 흐름에서는 갑자기 모든 문제가 사라지는 걸 기대하기보다 네 선택의 질이 높아지는 걸 목표로 잡아야 해. "
                            + "용신 "
                            + facts.usefulGod()
                            + ", 희신 "
                            + facts.favorableGod()
                            + " 방향은 네가 균형을 회복하는 데 도움이 되고, "
                            + "기신 "
                            + facts.unfavorableGod()
                            + "의 성향이 지나치게 강해질 때는 확장보다 정리가 먼저야. "
                            + "앞으로 직업과 돈, 관계에서 기회가 들어올 수는 있어. "
                            + "하지만 사람의 말만 믿고 조건을 확인하지 않거나, "
                            + "기존 기반을 먼저 없앤 뒤 새로운 일을 시작하면 운의 장점을 제대로 쓰기 어려워. "
                            + "미래운의 핵심은 준비된 이동, 기록이 남는 계약, 감당 가능한 확장이야.";
        };
    }

    private String topicEvidence(
            DeepTopic topic,
            SajuFacts facts
    ) {
        return switch (
                topic
                ) {
            case YEAR_OVERALL ->
                    yearEvidence(
                            facts
                    );

            case LOVE_STYLE, MEETING_PERSON, COMPATIBILITY ->
                    "이 판단은 단순히 도화살 하나로 내린 게 아니야. "
                            + "주요 십성인 "
                            + facts.dominantTenGod()
                            + ", 일간의 힘을 나타내는 "
                            + facts.strengthClassification()
                            + ", 원국의 합·충·형·파·해 관계 "
                            + facts.totalRelationCount()
                            + "개를 함께 본 결과야. "
                            + "연애와 궁합은 실제 상대의 성격과 행동에 따라 크게 달라지기 때문에 "
                            + "사주에서는 반복되는 감정 패턴과 관계의 경계를 중심으로 봐야 해.";

            case PARENTS ->
                    "부모 해석에서는 전통적으로 연주와 월주, 인성과 재성, 원국의 관계 구조를 함께 봐. "
                            + "현재 계산에서는 인성 계열이 "
                            + facts.resourceCount()
                            + "개, 재성 계열이 "
                            + facts.wealthCount()
                            + "개로 나타나. "
                            + "다만 성별에 따른 부모 별의 세부 구분과 실제 가족사는 별도 정보가 없기 때문에 "
                            + "부모의 이혼이나 재혼을 사실처럼 확정할 수는 없어.";

            case SIBLINGS ->
                    "형제와 동료를 보는 비견·겁재 계열은 "
                            + facts.peerCount()
                            + "개로 계산돼. "
                            + "이 수치만으로 형제 수나 관계의 결과를 단정하지는 않아. "
                            + "원국의 관계 요소와 함께 경쟁, 협력, 책임 분배의 패턴을 보는 보조 근거로 사용해.";

            case MONEY, WEALTH ->
                    "금전과 재물은 재성만 보지 않아. "
                            + "재성 계열 "
                            + facts.wealthCount()
                            + "개, 돈을 만들어내는 식상 계열 "
                            + facts.outputCount()
                            + "개, 재물을 나누거나 경쟁하게 만드는 비겁 계열 "
                            + facts.peerCount()
                            + "개를 함께 봐야 해. "
                            + "일간의 힘이 "
                            + facts.strengthClassification()
                            + "이라는 점도 돈을 감당하고 관리하는 방식에 영향을 줘.";

            case LEGAL ->
                    "관재수는 관성 계열 "
                            + facts.officerCount()
                            + "개, 표현과 반발을 뜻하는 식상 계열 "
                            + facts.outputCount()
                            + "개, 원국의 관계 요소 "
                            + facts.totalRelationCount()
                            + "개를 함께 참고한 판단이야. "
                            + "신살 하나만으로 소송이나 경찰 문제를 예언하지 않아.";

            default ->
                    evidenceOverview(
                            facts
                    );
        };
    }

    /**
     * 현실에서 나타나기 쉬운 모습을 설명한다.
     *
     * FUTURE_OVERALL과 FUTURE를 서로 다른 case로 분리해서
     * 중복 case 오류가 발생하지 않도록 한다.
     */
    private String topicReality(
            DeepTopic topic,
            SajuFacts facts
    ) {
        return switch (
                topic
                ) {
            case BORN_OVERALL ->
                    "현실에서는 일을 맡으면 끝까지 책임지지만 도움을 요청하는 시점이 늦을 수 있어. "
                            + "사람들은 너를 강하고 판단이 빠른 사람으로 보지만, 실제로는 혼자 계산하고 걱정하는 시간이 길 수 있어. "
                            + "잘 풀릴 때는 기획과 실행이 빠르지만, 지쳤을 때는 관계나 일을 갑자기 끊는 방식으로 반응할 수 있어.";

            case YEAR_OVERALL ->
                    "올해는 공부, 자격, 직업 이동, 새로운 프로젝트처럼 앞으로 남길 수 있는 일을 선택하는 게 중요해. "
                            + "여러 제안이 들어와도 네 경력과 수입에 무엇이 남는지 확인해야 해. "
                            + "단기적인 기분이나 주변 사람의 기대 때문에 일정을 과도하게 벌리면 체력과 돈이 먼저 흔들릴 수 있어.";

            case FUTURE_OVERALL ->
                    "앞으로는 네가 직접 만든 결과물과 경력이 중요한 자산이 돼. "
                            + "이름만 그럴듯한 계획보다 실제 사용자, 고객, 매출, 자격, 포트폴리오처럼 확인 가능한 결과를 남겨야 해. "
                            + "인간관계에서도 친한 사람의 부탁과 네 책임을 분리해야 장기적으로 안정돼.";

            case LOVE_STYLE ->
                    "연애가 잘될 때는 상대를 세심하게 챙기고 실제 행동으로 신뢰를 보여줘. "
                            + "문제가 생기면 네가 먼저 참고 해결하려다가 어느 순간 감정이 한꺼번에 터질 수 있어. "
                            + "상대가 힘들다고 하면 그 사람의 문제까지 네 몫처럼 받아들이는 습관을 조심해야 해.";

            case MEETING_PERSON ->
                    "처음에는 능력 있고 자신감 있는 사람에게 끌릴 수 있어. "
                            + "하지만 시간이 지나면 그 사람이 자신의 약속을 지키는지, 돈과 생활을 관리하는지가 더 중요해져. "
                            + "너에게 지나치게 기대거나 감정적으로 압박하는 사람은 처음의 강한 끌림과 달리 장기적으로 피로를 만들 수 있어.";

            case COMPATIBILITY ->
                    "잘 맞는 관계에서는 서로의 일과 생활을 존중하면서도 중요한 문제를 숨기지 않아. "
                            + "안 맞는 관계에서는 한쪽이 결정하고 다른 쪽이 따라가거나, 돈과 감정의 책임이 한 사람에게 몰려. "
                            + "너는 대화가 부드러운 사람보다 갈등 뒤에 행동을 바꾸는 사람과 더 오래 갈 수 있어.";

            case PAST ->
                    "과거에 누군가에게 충분히 설명받기보다 상황을 보고 스스로 판단했던 경험이 많았다면 지금의 독립성이 더 강해졌을 수 있어. "
                            + "반대로 도움을 받아도 마음 한쪽에서는 결국 네가 책임져야 한다고 느꼈을 가능성도 있어. "
                            + "이 경험이 현재의 과도한 책임감으로 이어지지 않는지 봐야 해.";

            case PARENTS ->
                    "부모와의 관계에서 정은 있지만 대화 방식이 맞지 않거나, 한쪽 부모의 부담을 네가 대신 이해하려는 모습이 나타날 수 있어. "
                            + "부모 사이의 갈등이 있었다면 네가 중간에서 분위기를 살피는 역할을 맡았을 가능성도 있어. "
                            + "다만 실제 이혼이나 재혼 여부는 사주만으로 확인할 수 없어.";

            case SIBLINGS ->
                    "형제나 가까운 동료 사이에서 비교와 경쟁이 생기면 네가 물러서기보다 결과로 증명하려는 태도가 나타날 수 있어. "
                            + "한편 가족 문제에서는 네가 더 책임져야 한다고 생각할 수도 있어. "
                            + "형제 관계가 멀다면 친구나 동업자에게 같은 기대를 걸지 않는지 살펴봐야 해.";

            case MONEY ->
                    "돈이 들어오면 필요한 곳에 빠르게 쓰거나 새로운 계획에 재투자하려는 성향이 나타날 수 있어. "
                            + "문제는 수입이 늘어날 때 지출 기준도 같이 커지는 거야. "
                            + "특히 가까운 사람의 부탁이나 공동 프로젝트는 네 몫과 책임을 흐리게 만들 수 있어.";

            case WEALTH ->
                    "재물이 남는 시기에는 수입이 많아서가 아니라 지출과 부채, 계약을 통제했을 가능성이 커. "
                            + "반대로 돈이 새는 시기에는 한 번의 큰 손실보다 여러 약속과 고정비가 겹치는 모습으로 나타나기 쉬워. "
                            + "재물운을 살리려면 현금흐름을 먼저 보여야 해.";

            case LEGAL ->
                    "현실에서 관재 문제는 계약서 미확인, 빌려준 돈, 온라인 발언, 명예 문제, 감정적인 문자처럼 기록이 남는 다툼으로 시작될 수 있어. "
                            + "억울하다고 바로 강하게 대응하면 작은 갈등이 공식적인 분쟁으로 커질 수 있어. "
                            + "네가 옳더라도 증거가 없으면 불리할 수 있다는 점을 기억해야 해.";

            case CURRENT ->
                    "현재 답답함이 있다면 운이 막혔다고만 보지 마. "
                            + "네가 동시에 너무 많은 책임을 잡고 있거나, 결과가 남지 않는 일에 시간을 쓰고 있을 가능성이 있어. "
                            + "지금은 무엇을 추가할지보다 무엇을 중단하고 정리할지가 중요해.";

            case FUTURE ->
                    "앞으로 기회는 사람, 기술, 공부, 직업 변화의 형태로 들어올 수 있어. "
                            + "그 기회가 진짜인지 판단하려면 보상과 책임, 지속 가능성을 확인해야 해. "
                            + "감정적으로 기존 기반을 먼저 없애지 말고 다음 조건을 확보한 뒤 움직이는 게 맞아.";
        };
    }

    private String topicAction(
            DeepTopic topic,
            SajuFacts facts
    ) {
        return switch (
                topic
                ) {
            case LOVE_STYLE ->
                    "연애에서는 서운함을 참다가 한꺼번에 꺼내지 마. "
                            + "불편한 일이 생기면 사실, 감정, 요구를 나눠서 말해. "
                            + "상대의 말을 믿기 전에 약속한 행동이 반복되는지 최소한 몇 차례 확인해야 해. "
                            + "도와주는 것과 대신 책임지는 것을 구분하는 게 핵심이야.";

            case MEETING_PERSON ->
                    "새로운 사람을 볼 때는 직업이나 외모보다 생활 안정성, 금전 습관, 약속 이행, 갈등 해결 방식을 봐. "
                            + "처음부터 지나치게 개인적인 문제나 돈 이야기를 꺼내는 사람은 천천히 판단해. "
                            + "네가 구해줘야 할 것 같은 감정을 사랑으로 착각하지 않는 게 중요해.";

            case COMPATIBILITY ->
                    "궁합을 확인하려면 세 가지를 보면 돼. "
                            + "상대가 자기 책임을 지는지, 네 경계를 존중하는지, 갈등 뒤에 행동을 수정하는지 확인해. "
                            + "말이 잘 통한다는 이유만으로 돈과 생활 기준까지 맞는다고 생각하면 안 돼.";

            case MONEY, WEALTH ->
                    "돈은 통장 목적을 분리해서 관리해. "
                            + "생활비, 고정비, 비상금, 투자·사업비를 한 계좌에서 섞지 마. "
                            + "사람과 돈이 오갈 때는 금액, 날짜, 조건을 기록해. "
                            + "큰 지출은 바로 결정하지 말고 다음 날에도 같은 판단인지 다시 확인해야 해.";

            case LEGAL ->
                    "분쟁이 생기면 감정적인 통화부터 하지 마. "
                            + "계약서, 입금내역, 문자, 통화기록과 날짜를 먼저 정리해. "
                            + "상대에게 보낼 문장은 사실과 요구사항만 남기고 모욕적인 표현은 빼. "
                            + "법률 판단이 필요한 사안은 전문가의 확인을 우선해야 해.";

            case PARENTS ->
                    "부모 문제를 전부 이해하거나 해결하려 하지 마. "
                            + "부모의 선택과 감정은 부모의 책임이야. "
                            + "네가 할 수 있는 도움의 범위를 정하고 돈이나 생활 문제가 섞일 때는 약속을 구체적으로 남겨야 해.";

            case SIBLINGS ->
                    "형제나 가까운 사람과는 역할과 비용을 미리 정해. "
                            + "가족이라는 이유로 네가 더 많이 부담하는 구조를 당연하게 만들지 마. "
                            + "서운함을 오래 쌓기보다 책임 범위를 숫자와 행동으로 확인하는 게 맞아.";

            case YEAR_OVERALL ->
                    "올해는 최우선 목표를 하나 정해. "
                            + "그 목표가 경력, 자격, 수입 중 무엇을 남기는지 분명히 적어. "
                            + "새로운 제안은 기간, 비용, 보상, 중도 종료 조건을 확인한 뒤 결정해. "
                            + "많이 시작하는 것보다 하나를 끝내는 게 더 중요해.";

            default ->
                    "현실에서 적용할 기준은 간단해. "
                            + "감정이 강할 때 결정을 미루고, 돈과 책임은 기록으로 남기고, "
                            + "새로운 일을 시작하기 전에 기존 자원과 시간부터 계산해. "
                            + "네 강점을 한 번의 열정이 아니라 반복 가능한 습관으로 바꾸는 게 핵심이야.";
        };
    }

    private String topicLimitation(
            DeepTopic topic,
            SajuFacts facts
    ) {
        String timeLimit =
                facts.birthTimeUnknown()
                        ? "출생시간을 모르는 명식이라 시주와 자녀운, 말년운의 세부 판단에는 제한이 있어. "
                        : "";

        return switch (
                topic
                ) {
            case PARENTS ->
                    timeLimit
                            + "부모의 실제 이혼, 별거, 재혼 여부는 사주만으로 확정할 수 없어. "
                            + "여기서는 부모와 감정적으로 연결되는 방식과 가족 안에서 맡기 쉬운 역할만 분석한 거야.";

            case LEGAL ->
                    "관재수는 법적 사건의 확정 예고가 아니야. "
                            + "실제 분쟁 가능성은 계약 내용, 상대방의 행동, 증거와 법률관계에 따라 달라져. "
                            + "중요한 사건은 사주보다 법률 검토가 우선이야.";

            case YEAR_OVERALL ->
                    facts.reportYear()
                            + "년 해설은 한 해 전체의 주제만 본 거야. "
                            + "특정 월에 사건이 생긴다고 예측하지 않았고 실제 결과는 네 준비와 선택에 따라 달라져.";

            case FUTURE, FUTURE_OVERALL ->
                    timeLimit
                            + "미래운은 정해진 사건을 읽는 게 아니라 유리한 선택과 불리한 습관을 구분하는 참고 자료야. "
                            + "몇 년 몇 월에 특정 사건이 발생한다고 단정하지 않아.";

            default ->
                    timeLimit
                            + "이 해설은 원국과 대운의 반복 구조를 바탕으로 한 성향 분석이야. "
                            + "한 가지 십성이나 신살만으로 결론을 내리지 않았고 실제 삶의 결과를 확정하지도 않아.";
        };
    }

    private String ensureDeepLength(
            String source,
            DeepTopic topic,
            SajuFacts facts
    ) {
        StringBuilder builder =
                new StringBuilder(
                        normalizeText(
                                source
                        )
                );

        List<String> supplements =
                List.of(
                        topicSupplement(
                                topic,
                                facts
                        ),

                        "이 카테고리를 볼 때 가장 중요한 건 한 번의 사건보다 반복되는 방식이야. "
                                + "비슷한 문제가 두 번 이상 생겼다면 상대나 환경만 바꾸려고 하지 말고 "
                                + "네가 어떤 순간에 경계를 풀고, 어떤 상황에서 결정을 서두르는지 확인해야 해. "
                                + "사주의 장점은 반복 패턴을 미리 알아차릴 수 있다는 데 있어.",

                        "명리 근거의 우선순위도 분명히 해야 해. "
                                + "원국 전체와 월령, 일간의 세력, 십성의 분포와 합·충 관계를 먼저 보고 "
                                + "신살은 마지막에 보조적으로 사용해. "
                                + "강한 표현 하나보다 같은 의미가 여러 층에서 반복되는지가 더 중요해.",

                        "네가 실제로 변화를 만들려면 판단 기준을 글로 적는 게 좋아. "
                                + "무엇을 얻는지, 무엇을 잃는지, 비용과 책임은 누가 지는지, "
                                + "중간에 문제가 생기면 어떻게 끝낼지를 미리 정해. "
                                + "감정이 가라앉은 뒤에도 같은 선택이라면 그때 움직여.",

                        "좋은 운을 쓴다는 건 무조건 크게 도전하는 게 아니야. "
                                + "감당 가능한 규모로 시작하고 결과를 확인한 뒤 확장하는 게 좋은 운을 현실화하는 방식이야. "
                                + "반대로 준비되지 않은 확장은 좋은 흐름에서도 손실을 만들 수 있어.",

                        "결국 "
                                + topic.title(
                                facts.reportYear()
                        )
                                + "의 핵심은 네가 타고난 기운을 어떤 행동으로 바꾸느냐야. "
                                + "사주는 변명거리가 아니라 기준을 세우는 자료로 써야 해. "
                                + "불리한 성향은 멈추는 장치를 만들고 유리한 성향은 반복 가능한 구조로 바꾸는 게 맞아."
                );

        for (
                String supplement : supplements
        ) {
            if (
                    builder.length()
                            >= MIN_DEEP_LENGTH
            ) {
                break;
            }

            if (
                    supplement == null
                            || supplement.isBlank()
            ) {
                continue;
            }

            builder.append(
                    "\n\n"
            );

            builder.append(
                    sanitizeNarrative(
                            supplement
                    )
            );
        }

        return trimToMaximumLength(
                builder.toString(),
                MAX_DEEP_LENGTH
        );
    }

    private String topicSupplement(
            DeepTopic topic,
            SajuFacts facts
    ) {
        return switch (
                topic
                ) {
            case BORN_OVERALL ->
                    "네 사주의 강점은 버티는 힘과 결과를 만들려는 태도야. "
                            + "하지만 혼자 감당하는 걸 능력이라고 착각하면 사람이 도와줄 기회까지 놓칠 수 있어. "
                            + "도움을 받되 최종 기준은 네가 세우는 방식이 가장 좋아.";

            case YEAR_OVERALL ->
                    facts.reportYear()
                            + "년에는 계획을 늘리기보다 우선순위를 줄여야 해. "
                            + "당장 좋아 보이는 제안보다 끝난 뒤 남는 경력과 수입을 봐. "
                            + "사람이 좋다는 이유만으로 조건이 불분명한 일을 시작하면 안 돼.";

            case FUTURE_OVERALL, FUTURE ->
                    "앞으로의 안정은 우연한 행운보다 네가 가진 기술과 경험을 자산으로 바꾸는 데서 나와. "
                            + "배운 것을 기록하고 결과물로 남기고 반복 가능한 수입 구조를 만들어야 해.";

            case LOVE_STYLE ->
                    "사랑을 많이 주는 것과 관계를 혼자 끌고 가는 건 달라. "
                            + "상대가 네 도움 없이도 자기 생활을 지킬 수 있는 사람인지 봐야 해.";

            case MEETING_PERSON ->
                    "강하게 끌리는 사람일수록 속도를 늦춰. "
                            + "상대가 불편한 상황에서 어떻게 말하고 행동하는지를 보면 관계의 실제 모습이 보여.";

            case COMPATIBILITY ->
                    "좋은 궁합은 싸우지 않는 관계가 아니야. "
                            + "싸운 뒤에 책임을 인정하고 행동을 고칠 수 있는 관계가 좋은 궁합이야.";

            case PAST ->
                    "과거에 만들어진 생존 방식이 지금도 필요한지 점검해야 해. "
                            + "예전에는 혼자 버티는 게 필요했더라도 지금은 협력과 분담이 더 좋은 결과를 만들 수 있어.";

            case PARENTS ->
                    "부모에게 받은 영향은 평생 똑같이 작용하지 않아. "
                            + "부모의 방식과 네 방식을 구분하는 순간부터 가족 문제에 휘둘리는 정도를 줄일 수 있어.";

            case SIBLINGS ->
                    "형제나 가까운 동료와의 관계에서는 공평함보다 합의가 중요해. "
                            + "서로 기대하는 책임이 다르면 미리 말로 정리해야 해.";

            case MONEY ->
                    "금전운을 살리려면 수입이 늘기 전에 지출 기준부터 세워야 해. "
                            + "돈을 벌고 남는 금액이 아니라 먼저 남길 금액을 정한 뒤 쓰는 방식이 맞아.";

            case WEALTH ->
                    "재물은 소유한 금액뿐 아니라 부채와 고정비를 빼고 남는 순자산으로 봐야 해. "
                            + "보이는 매출이나 통장 잔액만으로 재물 상태를 판단하지 마.";

            case LEGAL ->
                    "분쟁에서 중요한 건 누가 더 화가 났는지가 아니라 누가 사실과 증거를 정리했는지야. "
                            + "감정은 줄이고 기록은 늘려야 해.";

            case CURRENT ->
                    "현재운이 답답할수록 모든 것을 바꾸려 하지 마. "
                            + "가장 큰 손실을 만드는 한 가지 습관부터 끊는 게 흐름을 바꾸는 빠른 방법이야.";
        };
    }

    private String buildYearCore(
            SajuFacts facts
    ) {
        YearFortuneResponse yearFortune =
                facts.yearFortune();

        if (
                yearFortune == null
        ) {
            return facts.reportYear()
                    + "년 세운의 상세 간지 정보가 현재 응답에서 확인되지 않았어. "
                    + "그래서 없는 근거를 만들어서 말하지 않을게. "
                    + "올해는 현재 대운과 원국의 "
                    + facts.dominantTenGod()
                    + " 성향을 중심으로 보면 돼. "
                    + "여러 일을 동시에 벌이기보다 실제 결과가 남는 목표를 고르고, "
                    + "사람과 돈이 함께 움직이는 제안은 조건을 먼저 확인해야 해. "
                    + "올해의 핵심은 확장보다 선택과 완성이야.";
        }

        return facts.reportYear()
                + "년은 "
                + valueOrDefault(
                yearFortune.ganZhiKorean(),
                yearFortune.ganZhi()
        )
                + " 세운이고, 네 일간을 기준으로 "
                + valueOrDefault(
                yearFortune.tenGod(),
                "복합적인"
        )
                + " 작용이 들어와. "
                + "이 작용은 "
                + yearTenGodMeaning(
                yearFortune.tenGod()
        )
                + " 주제를 강조해. "
                + "좋게 쓰면 새로운 역할이나 공부, 직업적인 결과를 만들 수 있지만 "
                + "준비되지 않은 상태에서 책임만 늘리면 부담이 커질 수 있어. "
                + "올해는 운이 알아서 해결해주는 해가 아니야. "
                + "무엇을 선택하고 어떤 조건으로 움직이는지가 결과를 크게 가르게 돼. "
                + "특히 사람의 말보다 계약과 보상, 실제로 남는 경력을 확인해야 해.";
    }

    private String yearEvidence(
            SajuFacts facts
    ) {
        if (
                facts.yearFortune() == null
        ) {
            return "올해의 상세 세운 데이터가 응답에서 확인되지 않았기 때문에 "
                    + "원국과 현재 대운에서 반복되는 주제만 사용했어. "
                    + "없는 간지나 십성을 임의로 만들어내지 않았어.";
        }

        return "올해 해설의 근거는 "
                + valueOrDefault(
                facts.yearFortune()
                        .ganZhiKorean(),
                facts.yearFortune()
                        .ganZhi()
        )
                + " 세운과 "
                + valueOrDefault(
                facts.yearFortune()
                        .tenGod(),
                "복합 십성"
        )
                + " 작용이야. "
                + "여기에 원국의 우세 오행 "
                + facts.dominantElement()
                + ", 주요 십성 "
                + facts.dominantTenGod()
                + ", 현재 대운 "
                + cleanDecadeLabel(
                facts.currentDecade()
        )
                + "을 함께 비교했어.";
    }

    private String romanceCore(
            SajuFacts facts
    ) {
        return "네 연애의 시작은 신중하지만 관계가 깊어지면 책임감이 강해지는 편이야. "
                + facts.dominantTenGod()
                + " 성향은 "
                + tenGodRomanceMeaning(
                facts.dominantTenGod()
        )
                + " 모습으로 나타나. "
                + facts.strengthClassification()
                + " 구조는 "
                + strengthRomanceMeaning(
                facts.strengthClassification()
        )
                + " 경향을 더해. "
                + "원국에 관계 변화 요소가 "
                + facts.totalRelationCount()
                + "개 확인되기 때문에 상대가 알아서 이해할 거라고 생각하면 오해가 커질 수 있어. "
                + "너는 사랑을 말보다 행동으로 증명하려는 경향이 있지만, "
                + "상대가 같은 수준의 책임을 지지 않으면 시간이 지나면서 불공평하다는 감정이 쌓일 수 있어. "
                + "연애에서 가장 조심할 건 외로움 때문에 시작하는 관계와 불쌍해서 유지하는 관계야.";
    }

    private String parentCore(
            SajuFacts facts
    ) {
        return "전통적으로 부모와의 연은 연주와 월주, 인성과 재성, 원국의 관계를 함께 봐. "
                + "네 명식에서는 인성 계열이 "
                + facts.resourceCount()
                + "개, 재성 계열이 "
                + facts.wealthCount()
                + "개로 계산돼. "
                + "이 배치는 부모에게서 받는 보호와 현실적인 책임이 한쪽으로 치우치거나 "
                + "표현 방식이 다르게 느껴질 가능성을 보여줄 수 있어. "
                + "부모와 정이 없다는 뜻은 아니야. "
                + "오히려 정이 있기 때문에 기대와 실망도 크게 느낄 수 있어. "
                + "부모 사이의 관계가 불안정했다면 네가 분위기를 살피거나 중간 역할을 맡았을 가능성이 있지만, "
                + "이것만으로 이혼이나 재혼을 확정할 수는 없어. "
                + "같은 집에 살아도 정서적 거리가 있었을 수 있고 한쪽 부모가 역할을 충분히 하지 못했을 수도 있어.";
    }

    private String siblingCore(
            SajuFacts facts
    ) {
        return "비견과 겁재 계열이 "
                + facts.peerCount()
                + "개로 나타나. "
                + "이 기운은 형제, 친구, 동료 사이의 경쟁과 협력을 보여주는 보조 기준이야. "
                + "비겁이 강하면 가까운 사람과 대등하게 지내려는 힘이 있지만 비교와 주도권 문제도 생길 수 있어. "
                + "반대로 비겁이 약하면 형제와 거리가 있거나 각자의 생활이 분명할 수 있고, "
                + "형제 대신 친구나 동료에게 형제 같은 기대를 걸 수 있어. "
                + "네 경우에는 "
                + facts.dominantTenGod()
                + " 성향이 관계의 주도권에 영향을 주기 때문에 "
                + "책임과 비용을 누가 얼마나 맡는지가 관계의 질을 크게 좌우해. "
                + "가족이니까 알아서 해야 한다는 방식은 오래 갈수록 서운함을 만들 수 있어.";
    }

    private String moneyCore(
            SajuFacts facts,
            boolean assetFocus
    ) {
        if (
                assetFocus
        ) {
            return "재물 축적 구조를 보면 재성 계열 "
                    + facts.wealthCount()
                    + "개와 식상 계열 "
                    + facts.outputCount()
                    + "개, 비겁 계열 "
                    + facts.peerCount()
                    + "개의 균형이 중요해. "
                    + "식상은 돈을 만들어내는 기술과 활동을 뜻하고 재성은 실제 돈과 관리, 비겁은 경쟁과 분산을 뜻해. "
                    + "네가 돈을 잘 벌어도 고정비가 커지거나 사람과 공동으로 움직이는 비용이 늘면 재물이 남지 않을 수 있어. "
                    + "반대로 수입이 아주 크지 않아도 지출과 부채를 통제하고 기술을 꾸준히 수입으로 연결하면 재산은 쌓일 수 있어. "
                    + "재물운의 핵심은 한 번의 큰 기회가 아니라 남는 구조를 만드는 거야.";
        }

        return "금전 흐름에서는 재성 계열이 "
                + facts.wealthCount()
                + "개, 식상 계열이 "
                + facts.outputCount()
                + "개로 나타나. "
                + "네가 가진 기술과 결과물을 수입으로 연결할 가능성은 있지만, "
                + "비겁 계열 "
                + facts.peerCount()
                + "개의 작용 때문에 사람과 돈이 섞일 때 분산과 경쟁이 생길 수 있어. "
                + facts.strengthClassification()
                + " 구조에서는 돈을 감당하는 방식도 중요해. "
                + "신강하면 확신으로 큰 결정을 빠르게 내릴 수 있고, 신약하면 기회를 감당하느라 체력과 시간이 먼저 소모될 수 있어. "
                + "돈복이 없다고 볼 구조는 아니지만 친분과 계약을 분리하지 않으면 벌어도 남기 어려울 수 있어.";
    }

    private String legalCore(
            SajuFacts facts
    ) {
        return "관성과 식상의 관계를 보면 규칙과 책임을 받아들이는 방식, "
                + "부당하다고 느꼈을 때 말과 행동으로 대응하는 방식을 볼 수 있어. "
                + "관성 계열은 "
                + facts.officerCount()
                + "개, 식상 계열은 "
                + facts.outputCount()
                + "개로 계산돼. "
                + "식상이 강한데 관성과 충돌하면 규칙이나 권위에 문제를 제기하는 힘이 커질 수 있어. "
                + "이건 부당한 일을 바로잡을 때는 장점이야. "
                + "하지만 화가 난 상태에서 문자나 통화로 강하게 대응하면 작은 문제가 공식적인 분쟁으로 커질 수 있어. "
                + "반대로 관성이 강하면 책임과 규칙을 지나치게 떠안거나 불리한 조건도 참고 버틸 수 있어. "
                + "관재수의 핵심은 겁을 먹는 게 아니라 기록과 절차를 지키는 거야.";
    }

    private String evidenceOverview(
            SajuFacts facts
    ) {
        return "이 판단은 일간 "
                + facts.dayMaster()
                + ", 우세 오행 "
                + facts.dominantElement()
                + ", 부족 오행 "
                + facts.weakestElement()
                + ", 주요 십성 "
                + facts.dominantTenGod()
                + ", 일간 세력 "
                + facts.strengthClassification()
                + ", 합·충·형·파·해 관계 "
                + facts.totalRelationCount()
                + "개를 함께 본 결과야. "
                + "신살 하나만으로 결론을 내리지 않았어.";
    }

    private SajuFacts extractFacts(
            SajuAnalysisResponse source
    ) {
        Map<String, Integer> tenGodCounts =
                createTenGodCountMap(
                        source
                );

        int reportYear =
                Year.now(
                        KOREA_ZONE
                ).getValue();

        YearFortuneResponse yearFortune =
                findYearFortune(
                        source,
                        reportYear
                );

        String dominantElement =
                source.elementBalance() == null
                        ? "-"
                        : valueOrDefault(
                        source.elementBalance()
                                .dominant(),
                        "-"
                );

        String weakestElement =
                source.elementBalance() == null
                        ? "-"
                        : valueOrDefault(
                        source.elementBalance()
                                .weakest(),
                        "-"
                );

        String dominantTenGod =
                source.tenGodBalance() == null
                        ? "-"
                        : valueOrDefault(
                        source.tenGodBalance()
                                .dominant(),
                        "-"
                );

        String strengthClassification =
                source.strength() == null
                        ? "-"
                        : valueOrDefault(
                        source.strength()
                                .classification(),
                        "-"
                );

        String usefulGod =
                source.strength() == null
                        ? "-"
                        : valueOrDefault(
                        source.strength()
                                .usefulGod(),
                        "-"
                );

        String favorableGod =
                source.strength() == null
                        ? "-"
                        : valueOrDefault(
                        source.strength()
                                .favorableGod(),
                        "-"
                );

        String unfavorableGod =
                source.strength() == null
                        ? "-"
                        : valueOrDefault(
                        source.strength()
                                .unfavorableGod(),
                        "-"
                );

        String currentDecade =
                source.luck() == null
                        ? "현재 대운"
                        : valueOrDefault(
                        source.luck()
                                .currentDecade(),
                        "현재 대운"
                );

        String luckDirection =
                source.luck() == null
                        ? "대운 흐름"
                        : valueOrDefault(
                        source.luck()
                                .direction(),
                        "대운 흐름"
                );

        boolean birthTimeUnknown =
                source.calendar() != null
                        && source.calendar()
                        .birthTimeUnknown();

        return new SajuFacts(
                valueOrDefault(
                        source.name(),
                        "너"
                ),
                valueOrDefault(
                        source.dayMaster(),
                        "-"
                ),
                dominantElement,
                weakestElement,
                dominantTenGod,
                strengthClassification,
                usefulGod,
                favorableGod,
                unfavorableGod,
                currentDecade,
                luckDirection,
                relationCount(
                        source.relations()
                ),
                tenGodCounts,
                reportYear,
                yearFortune,
                birthTimeUnknown
        );
    }

    private Map<String, Integer> createTenGodCountMap(
            SajuAnalysisResponse source
    ) {
        Map<String, Integer> result =
                new LinkedHashMap<>();

        if (
                source.tenGodBalance() == null
                        || source.tenGodBalance()
                        .items() == null
        ) {
            return result;
        }

        for (
                BalanceItemResponse item
                : source.tenGodBalance()
                .items()
        ) {
            if (
                    item == null
                            || item.name() == null
                            || item.name().isBlank()
            ) {
                continue;
            }

            result.merge(
                    item.name(),
                    item.count(),
                    Integer::sum
            );
        }

        return Map.copyOf(
                result
        );
    }

    private YearFortuneResponse findYearFortune(
            SajuAnalysisResponse source,
            int year
    ) {
        if (
                source.luck() == null
                        || source.luck()
                        .decadeFortunes() == null
        ) {
            return null;
        }

        return source.luck()
                .decadeFortunes()
                .stream()
                .filter(
                        Objects::nonNull
                )
                .map(
                        DecadeFortuneResponse::yearlyFortunes
                )
                .filter(
                        Objects::nonNull
                )
                .flatMap(
                        List::stream
                )
                .filter(
                        Objects::nonNull
                )
                .filter(
                        fortune ->
                                fortune.year() == year
                )
                .findFirst()
                .orElse(
                        null
                );
    }

    private int relationCount(
            RelationResponse relations
    ) {
        if (
                relations == null
        ) {
            return 0;
        }

        return safeSize(
                relations.heavenlyStemRelations()
        )
                + safeSize(
                relations.earthlyBranchRelations()
        );
    }

    private String elementCoreTrait(
            String element
    ) {
        return switch (
                element
                ) {
            case "목" ->
                    "성장하고 범위를 넓히려는";

            case "화" ->
                    "빠르게 표현하고 움직이려는";

            case "토" ->
                    "안정적인 기반을 만들고 책임지려는";

            case "금" ->
                    "기준을 세우고 정확하게 정리하려는";

            case "수" ->
                    "상황을 읽고 유연하게 움직이려는";

            default ->
                    "상황에 맞춰 여러 기운을 조절하려는";
        };
    }

    private String elementCompensation(
            String element
    ) {
        return switch (
                element
                ) {
            case "목" ->
                    "작은 목표를 정해서 꾸준히 시작하는";

            case "화" ->
                    "생각과 감정을 분명하게 표현하는";

            case "토" ->
                    "수면과 식사, 일정의 리듬을 안정시키는";

            case "금" ->
                    "우선순위와 기준을 정하고 정리하는";

            case "수" ->
                    "한 가지 방식만 고집하지 않고 유연하게 조정하는";

            default ->
                    "생활 습관과 환경의 균형을 맞추는";
        };
    }

    private String strengthCoreTrait(
            String strength
    ) {
        return switch (
                strength
                ) {
            case "신강" ->
                    "스스로 해결하고 끝까지 밀어붙이는 힘이 강해.";

            case "신약" ->
                    "환경과 사람의 영향을 세밀하게 읽는 힘이 있지만 주변 반응에 흔들리기 쉬워.";

            case "중화" ->
                    "상황에 맞게 균형을 조절할 수 있지만 기준이 없으면 결정을 오래 미룰 수 있어.";

            default ->
                    "일간의 힘은 전체 구조와 함께 봐야 해서 한 가지 성향으로만 단정하면 안 돼.";
        };
    }

    private String tenGodCoreTrait(
            String tenGod
    ) {
        return switch (
                tenGod
                ) {
            case "비견" ->
                    "자기 방식으로 결정하고 직접 해결하려는";

            case "겁재" ->
                    "경쟁이 생기면 물러서지 않고 빠르게 움직이는";

            case "식신" ->
                    "기술과 결과물을 꾸준히 만들어내는";

            case "상관" ->
                    "문제점을 빠르게 발견하고 기존 방식을 바꾸려는";

            case "편재" ->
                    "사람과 기회를 넓게 보고 자원을 움직이려는";

            case "정재" ->
                    "현실적인 안정과 계획적인 관리를 중요하게 보는";

            case "편관" ->
                    "압박이 생길수록 강하게 대응하고 책임지려는";

            case "정관" ->
                    "규칙과 책임, 신뢰를 중요하게 보는";

            case "편인" ->
                    "남들이 보지 못하는 부분을 오래 생각하고 깊게 파고드는";

            case "정인" ->
                    "배우고 이해한 내용을 체계적으로 정리하는";

            default ->
                    "여러 성향을 복합적으로 사용하는";
        };
    }

    private String tenGodAdvantage(
            String tenGod
    ) {
        return switch (
                tenGod
                ) {
            case "비견" ->
                    "스스로 결정하고 버티는 힘이 있다는 거야.";

            case "겁재" ->
                    "경쟁과 위기에서 빠르게 움직일 수 있다는 거야.";

            case "식신" ->
                    "꾸준히 기술과 결과를 만들 수 있다는 거야.";

            case "상관" ->
                    "문제를 발견하고 새로운 방식을 제시할 수 있다는 거야.";

            case "편재" ->
                    "사람과 기회를 연결하고 판을 넓힐 수 있다는 거야.";

            case "정재" ->
                    "돈과 생활을 현실적으로 관리할 수 있다는 거야.";

            case "편관" ->
                    "압박 속에서도 결단하고 책임질 수 있다는 거야.";

            case "정관" ->
                    "원칙을 지키며 신뢰를 쌓을 수 있다는 거야.";

            case "편인" ->
                    "독특한 관점과 전문성을 깊게 만들 수 있다는 거야.";

            case "정인" ->
                    "배우고 정리하며 지식을 자산으로 만들 수 있다는 거야.";

            default ->
                    "상황에 맞춰 여러 능력을 조절할 수 있다는 거야.";
        };
    }

    private String tenGodRisk(
            String tenGod
    ) {
        return switch (
                tenGod
                ) {
            case "비견" ->
                    "고집과 독단으로";

            case "겁재" ->
                    "경쟁심과 사람·돈의 분산으로";

            case "식신" ->
                    "안일함과 과도한 책임으로";

            case "상관" ->
                    "강한 말과 규칙 충돌로";

            case "편재" ->
                    "무리한 확장과 지출로";

            case "정재" ->
                    "손해에 대한 불안과 지나친 안정 추구로";

            case "편관" ->
                    "긴장과 압박을 혼자 쌓는 방식으로";

            case "정관" ->
                    "책임을 과도하게 떠안는 방식으로";

            case "편인" ->
                    "생각이 많아 실행을 멈추는 방식으로";

            case "정인" ->
                    "준비만 하고 결과를 미루는 방식으로";

            default ->
                    "한쪽으로 치우친 판단으로";
        };
    }

    private String tenGodRomanceMeaning(
            String tenGod
    ) {
        return switch (
                tenGod
                ) {
            case "비견" ->
                    "연애에서도 대등함과 개인 영역을 중요하게 여기는";

            case "겁재" ->
                    "마음이 생기면 빠르게 가까워지지만 경쟁심과 질투도 커질 수 있는";

            case "식신" ->
                    "말보다 챙겨주는 행동으로 사랑을 보여주는";

            case "상관" ->
                    "감정을 솔직하게 표현하지만 서운할 때 말이 강해질 수 있는";

            case "편재" ->
                    "활동적이고 매력 있는 사람에게 끌리며 만남의 폭도 넓게 보는";

            case "정재" ->
                    "가벼운 관계보다 약속과 생활의 안정을 중요하게 보는";

            case "편관" ->
                    "긴장감과 강한 매력이 있는 관계에 끌릴 수 있는";

            case "정관" ->
                    "책임감과 신뢰가 확인돼야 관계를 깊게 만드는";

            case "편인" ->
                    "자신만의 세계가 있는 사람에게 끌리지만 혼자 결론을 내릴 수 있는";

            case "정인" ->
                    "정서적으로 이해받고 안정감을 느껴야 마음을 여는";

            default ->
                    "상대의 행동을 오래 확인한 뒤 관계를 깊게 만드는";
        };
    }

    private String strengthRomanceMeaning(
            String strength
    ) {
        return switch (
                strength
                ) {
            case "신강" ->
                    "관계를 네 방식으로 이끌려는";

            case "신약" ->
                    "상대의 기분을 지나치게 의식하는";

            case "중화" ->
                    "상황에 맞춰 조율하지만 중요한 기준을 애매하게 둘 수 있는";

            default ->
                    "감정을 오래 생각한 뒤 표현하는";
        };
    }

    private String yearTenGodMeaning(
            String tenGod
    ) {
        return switch (
                valueOrDefault(
                        tenGod,
                        ""
                )
                ) {
            case "비견" ->
                    "독립, 경쟁, 동료 관계와 자기 결정";

            case "겁재" ->
                    "경쟁, 지출, 공동 자원과 빠른 추진";

            case "식신" ->
                    "기술, 생산, 결과물과 생활 안정";

            case "상관" ->
                    "표현, 변화, 문제 제기와 새로운 방식";

            case "편재" ->
                    "기회, 활동, 사람과 돈의 이동";

            case "정재" ->
                    "현실적인 수입, 계약과 관리";

            case "편관" ->
                    "압박, 책임, 도전과 긴장";

            case "정관" ->
                    "직업, 규칙, 평가와 공식적인 책임";

            case "편인" ->
                    "전문성, 직관, 변화된 학습 방식";

            case "정인" ->
                    "공부, 자격, 문서와 보호";

            default ->
                    "선택과 책임";
        };
    }

    private String cleanDecadeLabel(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return "현재 대운";
        }

        String cleaned =
                value.replaceAll(
                                "(?:19|20|21)\\d{2}\\s*[~\\-–—]\\s*(?:19|20|21)\\d{2}",
                                ""
                        )
                        .replaceAll(
                                "\\([^)]*\\)",
                                " "
                        )
                        .replaceAll(
                                "\\s{2,}",
                                " "
                        )
                        .trim();

        return cleaned.isBlank()
                ? "현재 대운"
                : cleaned;
    }

    /**
     * 해설에서 특정 월과 날짜 표현을 제거한다.
     *
     * 현재 연도 자체는 종합운 제목과 해설에 사용할 수 있다.
     */
    private String sanitizeNarrative(
            String value
    ) {
        if (
                value == null
        ) {
            return "";
        }

        return value
                .replaceAll(
                        "((?:19|20|21)\\d{2})\\s*년\\s*\\d{1,2}\\s*월",
                        "$1년의 세부 시기"
                )
                .replaceAll(
                        "(?<!\\d)\\d{1,2}\\s*월\\s*\\d{1,2}\\s*일",
                        "세부 시기"
                )
                .replaceAll(
                        "(?<!\\d)\\d{1,2}\\s*월(?:부터|까지|경|초|중순|말|말경)?",
                        "세부 시기"
                )
                .replaceAll(
                        "\\s{2,}",
                        " "
                )
                .replaceAll(
                        "\\s+([,.!?])",
                        "$1"
                )
                .trim();
    }

    private String normalizeText(
            String value
    ) {
        return value == null
                ? ""
                : value.replace(
                        "\r",
                        ""
                )
                .replaceAll(
                        "[ \\t]{2,}",
                        " "
                )
                .replaceAll(
                        "\\n{3,}",
                        "\n\n"
                )
                .trim();
    }

    private String trimToMaximumLength(
            String value,
            int maximumLength
    ) {
        String normalized =
                normalizeText(
                        value
                );

        if (
                normalized.length()
                        <= maximumLength
        ) {
            return normalized;
        }

        int cutIndex =
                Math.max(
                        normalized.lastIndexOf(
                                ".",
                                maximumLength
                        ),
                        Math.max(
                                normalized.lastIndexOf(
                                        "!",
                                        maximumLength
                                ),
                                normalized.lastIndexOf(
                                        "?",
                                        maximumLength
                                )
                        )
                );

        if (
                cutIndex < maximumLength - 250
        ) {
            cutIndex = maximumLength;
        } else {
            cutIndex += 1;
        }

        return normalized.substring(
                0,
                cutIndex
        ).trim();
    }

    private int safeSize(
            List<?> values
    ) {
        return values == null
                ? 0
                : values.size();
    }

    private String valueOrDefault(
            String value,
            String defaultValue
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return defaultValue;
        }

        return value;
    }

    private enum DeepTopic {

        BORN_OVERALL(
                "현대사주",
                "타고난 종합운"
        ),

        YEAR_OVERALL(
                "현대사주",
                "%d년 종합운"
        ),

        FUTURE_OVERALL(
                "현대사주",
                "앞으로의 종합운"
        ),

        LOVE_STYLE(
                "현대사주",
                "연애 스타일"
        ),

        MEETING_PERSON(
                "현대사주",
                "만나기 쉬운 사람"
        ),

        COMPATIBILITY(
                "현대사주",
                "궁합이 좋은 사람"
        ),

        PAST(
                "전통사주",
                "과거운"
        ),

        PARENTS(
                "전통사주",
                "부모와의 연"
        ),

        SIBLINGS(
                "전통사주",
                "형제운"
        ),

        MONEY(
                "전통사주",
                "금전운"
        ),

        WEALTH(
                "전통사주",
                "재물운"
        ),

        LEGAL(
                "전통사주",
                "관재수"
        ),

        CURRENT(
                "전통사주",
                "현재운"
        ),

        FUTURE(
                "전통사주",
                "미래운"
        );

        private final String category;
        private final String titleFormat;

        DeepTopic(
                String category,
                String titleFormat
        ) {
            this.category = category;
            this.titleFormat = titleFormat;
        }

        public String category() {
            return category;
        }

        public String title(
                int reportYear
        ) {
            if (
                    titleFormat.contains(
                            "%d"
                    )
            ) {
                return String.format(
                        titleFormat,
                        reportYear
                );
            }

            return titleFormat;
        }
    }

    private record SajuFacts(

            String name,

            String dayMaster,

            String dominantElement,

            String weakestElement,

            String dominantTenGod,

            String strengthClassification,

            String usefulGod,

            String favorableGod,

            String unfavorableGod,

            String currentDecade,

            String luckDirection,

            int totalRelationCount,

            Map<String, Integer> tenGodCounts,

            int reportYear,

            YearFortuneResponse yearFortune,

            boolean birthTimeUnknown
    ) {

        int peerCount() {
            return count(
                    "비견"
            )
                    + count(
                    "겁재"
            );
        }

        int outputCount() {
            return count(
                    "식신"
            )
                    + count(
                    "상관"
            );
        }

        int wealthCount() {
            return count(
                    "편재"
            )
                    + count(
                    "정재"
            );
        }

        int officerCount() {
            return count(
                    "편관"
            )
                    + count(
                    "정관"
            );
        }

        int resourceCount() {
            return count(
                    "편인"
            )
                    + count(
                    "정인"
            );
        }

        private int count(
                String key
        ) {
            if (
                    tenGodCounts == null
            ) {
                return 0;
            }

            return tenGodCounts.getOrDefault(
                    key,
                    0
            );
        }
    }
}