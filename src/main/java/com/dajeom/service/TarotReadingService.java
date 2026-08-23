package com.dajeom.service;

import com.dajeom.dto.TarotReadingRequest;
import com.dajeom.dto.TarotReadingResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TarotReadingService {


    private final Map<Integer, TarotMeaning> meanings =
            new HashMap<>();


    public TarotReadingService() {

        registerCards();
    }


    public TarotReadingResponse createReading(
            TarotReadingRequest request
    ) {

        List<TarotReadingResponse.CardReading>
                readings =
                new ArrayList<>();


        String[] positions = {
                "과거의 흐름",
                "현재의 핵심",
                "앞으로의 흐름"
        };


        for (
                int i = 0;
                i < request.cards().size();
                i++
        ) {

            TarotReadingRequest.SelectedCard
                    selected =
                    request.cards().get(i);


            TarotMeaning meaning =
                    meanings.get(
                            selected.id()
                    );


            if (
                    meaning == null
            ) {

                continue;
            }


            boolean reversed =
                    selected.reversed();


            String keyword =
                    reversed
                            ? meaning.reversedKeyword()
                            : meaning.uprightKeyword();


            String baseMeaning =
                    reversed
                            ? meaning.reversedMeaning()
                            : meaning.uprightMeaning();


            String position =
                    i < positions.length
                            ? positions[i]
                            : "카드의 메시지";


            String interpretation =
                    createPositionInterpretation(
                            position,
                            baseMeaning,
                            request.category()
                    );


            readings.add(
                    new TarotReadingResponse.CardReading(
                            position,
                            meaning.name(),
                            reversed,
                            keyword,
                            interpretation
                    )
            );
        }


        String opening =
                createOpening(
                        request.category(),
                        request.question()
                );


        String summary =
                createSummary(
                        request,
                        readings
                );


        String advice =
                createAdvice(
                        request.category()
                );


        return new TarotReadingResponse(
                opening,
                readings,
                summary,
                advice
        );
    }



    private String createOpening(
            String category,
            String question
    ) {

        String safeCategory =
                category == null
                        || category.isBlank()
                        ? "현재 고민"
                        : category;


        if (
                question == null
                        ||
                        question.isBlank()
        ) {

            return safeCategory
                    + "에 대해 세 장의 카드를 펼쳐보았습니다. "
                    + "각 카드는 과거에서 시작해 현재를 지나 "
                    + "앞으로 이어질 흐름을 보여줍니다.";
        }


        return "\""
                + question
                + "\"라는 질문을 중심으로 "
                + "세 장의 카드를 펼쳐보았습니다. "
                + "하나의 카드만 따로 보기보다 "
                + "세 카드가 이어지는 흐름을 함께 살펴보겠습니다.";
    }



    private String createPositionInterpretation(
            String position,
            String meaning,
            String category
    ) {

        String prefix =
                switch (
                        position
                        ) {

                    case "과거의 흐름" ->
                            "이 카드가 과거 자리에 나타났다는 것은, ";

                    case "현재의 핵심" ->
                            "현재 자리에서 이 카드는, ";

                    case "앞으로의 흐름" ->
                            "앞으로의 자리에서는, ";

                    default ->
                            "";

                };


        return prefix
                + meaning;
    }



    private String createSummary(
            TarotReadingRequest request,
            List<TarotReadingResponse.CardReading>
                    readings
    ) {

        if (
                readings.isEmpty()
        ) {

            return "선택된 카드의 흐름을 확인하지 못했습니다.";
        }


        StringBuilder result =
                new StringBuilder();


        result.append(
                "이번 배열의 핵심은 "
        );


        for (
                int i = 0;
                i < readings.size();
                i++
        ) {

            TarotReadingResponse.CardReading
                    reading =
                    readings.get(i);


            if (
                    i > 0
            ) {

                result.append(" → ");

            }


            result.append(
                    reading.keyword()
            );

        }


        result.append(
                "의 흐름으로 이어진다는 점입니다. "
        );


        result.append(
                "타로는 미래를 확정하는 답이라기보다 "
                        + "현재 어떤 흐름이 강하게 작용하고 있는지 "
                        + "살펴보는 도구로 보는 것이 좋습니다."
        );


        return result.toString();
    }



    private String createAdvice(
            String category
    ) {

        if (
                category == null
        ) {

            return "지금 가장 마음에 남은 카드의 메시지를 현실의 선택과 연결해보세요.";
        }


        return switch (
                category
                ) {

            case "연애" ->
                    "상대방의 마음만 확인하려 하기보다 지금 두 사람 사이에서 실제로 오가는 행동을 함께 살펴보세요.";

            case "재회" ->
                    "다시 만날 가능성뿐 아니라 헤어진 원인이 실제로 달라질 수 있는지도 함께 보는 것이 중요합니다.";

            case "직장" ->
                    "현재의 감정보다 실제 선택 가능한 기회와 위험을 나누어 판단해보세요.";

            case "금전" ->
                    "큰 결정을 서두르기보다 손실을 줄일 수 있는 현실적인 기준을 먼저 세우는 것이 좋습니다.";

            case "인간관계" ->
                    "상대의 의도를 추측하기보다 반복해서 나타나는 행동과 관계의 균형을 살펴보세요.";

            default ->
                    "카드가 보여준 가능성을 참고하되 최종 선택은 현재 상황과 현실적인 조건을 함께 보고 결정하세요.";

        };
    }



    private void registerCards() {

        add(
                0,
                "The Fool",
                "새로운 시작 · 자유 · 가능성",
                "충동 · 준비 부족 · 방향 상실",
                "익숙한 길을 벗어나 새로운 가능성으로 들어가는 흐름입니다. 아직 모든 답이 정해지지 않았지만 그만큼 선택의 여지가 큽니다.",
                "새롭게 시작하고 싶은 마음은 있지만 준비되지 않은 상태에서 성급히 움직일 가능성이 있습니다."
        );


        add(
                1,
                "The Magician",
                "실행력 · 능력 · 시작",
                "자신감 부족 · 과장 · 미숙함",
                "필요한 자원과 능력이 이미 가까이에 있습니다. 생각을 현실적인 행동으로 옮기는 것이 핵심입니다.",
                "능력은 있지만 제대로 사용하지 못하거나 자신보다 상황을 크게 보이고 있을 수 있습니다."
        );


        add(
                2,
                "The High Priestess",
                "직감 · 내면 · 숨겨진 진실",
                "혼란 · 불안 · 직감 무시",
                "겉으로 드러난 말보다 아직 밝혀지지 않은 부분과 자신의 직감을 살펴볼 필요가 있습니다.",
                "불안과 직감을 구분하기 어려운 시기입니다. 추측만으로 결론을 내리지 않는 것이 좋습니다."
        );


        add(
                3,
                "The Empress",
                "풍요 · 관계 성장 · 돌봄",
                "과잉 · 의존 · 정체",
                "관계나 상황이 성장할 수 있는 좋은 기반이 만들어지고 있습니다. 충분히 돌보고 키워갈 가치가 있습니다.",
                "지나친 기대나 의존 때문에 오히려 관계와 상황의 균형이 무너질 수 있습니다."
        );


        add(
                4,
                "The Emperor",
                "안정 · 질서 · 책임",
                "통제 · 완고함 · 압박",
                "감정보다 현실적인 기준과 책임을 세우는 것이 중요한 흐름입니다.",
                "한쪽이 지나치게 상황을 통제하려 하거나 자신의 방식만 고집하고 있을 수 있습니다."
        );


        add(
                5,
                "The Hierophant",
                "전통 · 조언 · 신뢰",
                "고정관념 · 형식 · 반발",
                "검증된 방식이나 경험자의 조언을 참고하면 안정적인 방향을 찾을 가능성이 큽니다.",
                "주변의 기준이나 기존 방식에 지나치게 얽매여 자신의 진짜 생각을 놓치고 있을 수 있습니다."
        );


        add(
                6,
                "The Lovers",
                "사랑 · 선택 · 연결",
                "갈등 · 불균형 · 선택의 어려움",
                "강한 연결과 선택이 함께 나타납니다. 무엇을 원하는지 분명히 하는 것이 중요합니다.",
                "마음과 현실 사이에 차이가 있거나 관계 안에서 중요한 선택을 미루고 있을 가능성이 있습니다."
        );


        add(
                7,
                "The Chariot",
                "전진 · 승리 · 의지",
                "방향 상실 · 조급함 · 충돌",
                "목표를 정하고 집중한다면 상황을 앞으로 끌고 갈 힘이 있습니다.",
                "빨리 결과를 만들려는 마음 때문에 오히려 방향을 잃거나 다른 사람과 충돌할 수 있습니다."
        );


        add(
                8,
                "Strength",
                "인내 · 용기 · 내면의 힘",
                "자신감 저하 · 감정 폭발 · 불안",
                "억지로 밀어붙이는 힘보다 감정을 다루는 차분한 힘이 필요한 시기입니다.",
                "감정을 억누르거나 반대로 한꺼번에 터뜨리는 방식이 문제를 키울 수 있습니다."
        );


        add(
                9,
                "The Hermit",
                "성찰 · 거리두기 · 탐색",
                "고립 · 회피 · 지나친 생각",
                "잠시 속도를 늦추고 스스로의 생각을 정리할 시간이 필요합니다.",
                "혼자 생각하는 시간이 너무 길어지면서 현실적인 행동을 피하고 있을 수 있습니다."
        );


        add(
                10,
                "Wheel of Fortune",
                "변화 · 전환점 · 기회",
                "지연 · 반복 · 통제 불가",
                "예상하지 못한 변화가 새로운 방향을 열 수 있습니다. 흐름이 바뀌는 시점입니다.",
                "비슷한 문제가 반복되거나 원하는 시점과 실제 변화의 시점이 어긋날 수 있습니다."
        );


        add(
                11,
                "Justice",
                "균형 · 판단 · 결과",
                "불공정 · 편향 · 책임 회피",
                "감정보다 사실과 결과를 기준으로 판단해야 하는 흐름입니다.",
                "자신에게 유리한 부분만 보고 있지는 않은지 한 번 더 확인할 필요가 있습니다."
        );


        add(
                12,
                "The Hanged Man",
                "기다림 · 관점 변화 · 멈춤",
                "정체 · 희생 · 미련",
                "지금은 억지로 움직이기보다 다른 관점에서 상황을 볼 필요가 있습니다.",
                "상황이 오래 정체되면서 필요 이상의 희생이나 미련을 붙잡고 있을 수 있습니다."
        );


        add(
                13,
                "Death",
                "종료 · 변화 · 재출발",
                "미련 · 변화 거부 · 정체",
                "하나의 흐름이 끝나면서 새로운 단계가 시작되는 강한 변화의 카드입니다.",
                "이미 달라진 상황을 인정하지 못해 새로운 방향으로 움직이지 못하고 있을 수 있습니다."
        );


        add(
                14,
                "Temperance",
                "조화 · 균형 · 회복",
                "불균형 · 과도함 · 조급함",
                "서로 다른 요소를 천천히 조율하면서 안정적인 흐름으로 돌아갈 수 있습니다.",
                "한쪽으로 지나치게 치우쳐 있어 속도를 조절하고 균형을 되찾을 필요가 있습니다."
        );


        add(
                15,
                "The Devil",
                "집착 · 욕망 · 강한 끌림",
                "해방 · 깨달음 · 관계 정리",
                "강한 끌림이 있지만 동시에 집착이나 의존이 섞여 있는지 확인해야 합니다.",
                "자신을 묶고 있던 관계나 습관에서 벗어나려는 움직임이 나타날 수 있습니다."
        );


        add(
                16,
                "The Tower",
                "충격 · 변화 · 진실",
                "변화 회피 · 불안 · 지연된 충돌",
                "기존의 생각이나 구조가 크게 흔들릴 수 있지만 그 과정에서 감춰진 사실이 드러날 수 있습니다.",
                "문제를 알고 있으면서도 변화를 미루고 있어 긴장이 계속 쌓이고 있을 가능성이 있습니다."
        );


        add(
                17,
                "The Star",
                "희망 · 회복 · 가능성",
                "실망 · 자신감 저하 · 기대 약화",
                "어려움을 지나 다시 희망과 가능성을 볼 수 있는 흐름입니다.",
                "결과가 늦어지면서 기대와 자신감이 약해질 수 있으나 완전히 가능성이 사라진 것은 아닙니다."
        );


        add(
                18,
                "The Moon",
                "불확실성 · 감정 · 숨겨진 것",
                "혼란 해소 · 진실 확인 · 안정",
                "지금은 보이는 것만으로 확신하기 어려운 시기입니다. 감정과 사실을 구분해서 볼 필요가 있습니다.",
                "그동안 불분명했던 부분이 조금씩 드러나면서 혼란에서 벗어날 가능성이 있습니다."
        );


        add(
                19,
                "The Sun",
                "성공 · 밝음 · 확신",
                "지연 · 과신 · 기대 차이",
                "상황이 비교적 명확해지고 긍정적인 결과를 기대할 수 있는 흐름입니다.",
                "기본적인 흐름은 나쁘지 않지만 기대했던 만큼 빠르게 진행되지 않을 수 있습니다."
        );


        add(
                20,
                "Judgement",
                "결정 · 재평가 · 다시 시작",
                "미련 · 자기의심 · 결정 지연",
                "과거를 다시 돌아보고 중요한 결론을 내려야 할 시점이 다가오고 있습니다.",
                "결정을 내려야 한다는 것을 알면서도 과거의 문제나 두려움 때문에 미루고 있을 수 있습니다."
        );


        add(
                21,
                "The World",
                "완성 · 성취 · 다음 단계",
                "미완성 · 지연 · 마무리 부족",
                "하나의 과정이 완성되고 다음 단계로 넘어갈 준비가 되어가는 흐름입니다.",
                "거의 끝에 도달했지만 아직 정리해야 할 문제가 남아 있어 완전한 마무리가 늦어질 수 있습니다."
        );
    }



    private void add(
            int id,
            String name,
            String uprightKeyword,
            String reversedKeyword,
            String uprightMeaning,
            String reversedMeaning
    ) {

        meanings.put(
                id,
                new TarotMeaning(
                        name,
                        uprightKeyword,
                        reversedKeyword,
                        uprightMeaning,
                        reversedMeaning
                )
        );
    }



    private record TarotMeaning(

            String name,

            String uprightKeyword,

            String reversedKeyword,

            String uprightMeaning,

            String reversedMeaning

    ) {
    }
}