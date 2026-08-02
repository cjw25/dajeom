package com.dajeom.config;

import com.dajeom.entity.TarotCard;
import com.dajeom.entity.TarotSpread;
import com.dajeom.entity.TarotSpreadPosition;
import com.dajeom.repository.TarotCardRepository;
import com.dajeom.repository.TarotSpreadPositionRepository;
import com.dajeom.repository.TarotSpreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TarotDataInitializer
        implements ApplicationRunner {

    private static final String LEGACY_SPREAD_NAME =
            "3장 기본 스프레드";

    private final TarotCardRepository tarotCardRepository;
    private final TarotSpreadRepository tarotSpreadRepository;
    private final TarotSpreadPositionRepository tarotSpreadPositionRepository;

    @Override
    @Transactional
    public void run(
            ApplicationArguments args
    ) {
        seedTarotCards();
        deactivateLegacySpread();
        seedTarotSpreads();
    }

    private void deactivateLegacySpread() {
        tarotSpreadRepository
                .findByName(LEGACY_SPREAD_NAME)
                .ifPresent(
                        TarotSpread::deactivate
                );
    }

    private void seedTarotSpreads() {

        for (
                SpreadDefinition definition
                : createSpreadDefinitions()
        ) {
            TarotSpread spread =
                    tarotSpreadRepository
                            .findByName(
                                    definition.name()
                            )
                            .orElseGet(
                                    () -> tarotSpreadRepository.save(
                                            TarotSpread.create(
                                                    definition.name(),
                                                    definition.positions().size(),
                                                    definition.description(),
                                                    definition.layoutCode(),
                                                    definition.sortOrder()
                                            )
                                    )
                            );

            spread.updateDefinition(
                    definition.positions().size(),
                    definition.description(),
                    definition.layoutCode(),
                    definition.sortOrder()
            );

            long currentPositionCount =
                    tarotSpreadPositionRepository
                            .countBySpreadId(
                                    spread.getId()
                            );

            if (currentPositionCount == 0) {
                List<TarotSpreadPosition> positions =
                        definition.positions()
                                .stream()
                                .map(
                                        position ->
                                                TarotSpreadPosition.create(
                                                        spread,
                                                        position.order(),
                                                        position.name(),
                                                        position.description()
                                                )
                                )
                                .toList();

                tarotSpreadPositionRepository
                        .saveAll(positions);
            }
        }
    }

    private List<SpreadDefinition>
    createSpreadDefinitions() {

        return List.of(
                new SpreadDefinition(
                        "오늘의 한 장",
                        "지금 나에게 가장 필요한 메시지와 현실적인 조언을 확인합니다.",
                        "SINGLE",
                        1,
                        List.of(
                                new PositionDefinition(
                                        1,
                                        "오늘의 메시지",
                                        "현재 나에게 가장 필요한 흐름과 행동 조언을 나타냅니다."
                                )
                        )
                ),

                new SpreadDefinition(
                        "과거·현재·미래",
                        "과거의 영향, 현재 상황, 앞으로 이어질 가능성을 순서대로 확인합니다.",
                        "TIMELINE_3",
                        2,
                        List.of(
                                new PositionDefinition(
                                        1,
                                        "과거의 영향",
                                        "현재 상황에 영향을 준 과거의 사건과 감정을 나타냅니다."
                                ),
                                new PositionDefinition(
                                        2,
                                        "현재 상황",
                                        "지금 가장 중요하게 작용하는 감정과 상황을 나타냅니다."
                                ),
                                new PositionDefinition(
                                        3,
                                        "앞으로의 흐름",
                                        "현재 선택이 이어질 경우 나타날 가능성과 방향을 나타냅니다."
                                )
                        )
                ),

                new SpreadDefinition(
                        "상대방의 마음",
                        "나와 상대방의 마음, 관계를 막는 요소와 앞으로의 관계 흐름을 확인합니다.",
                        "RELATIONSHIP_5",
                        3,
                        List.of(
                                new PositionDefinition(
                                        1,
                                        "나의 현재 마음",
                                        "현재 내가 이 관계에서 느끼는 감정과 원하는 방향입니다."
                                ),
                                new PositionDefinition(
                                        2,
                                        "상대방의 현재 마음",
                                        "상대방이 관계를 바라보는 감정과 생각입니다."
                                ),
                                new PositionDefinition(
                                        3,
                                        "관계를 막는 요소",
                                        "두 사람 사이에서 관계의 흐름을 방해하는 문제입니다."
                                ),
                                new PositionDefinition(
                                        4,
                                        "상대방이 원하는 방향",
                                        "상대방이 관계에서 기대하거나 바라는 방향입니다."
                                ),
                                new PositionDefinition(
                                        5,
                                        "앞으로의 관계 흐름",
                                        "현재 상태가 이어질 경우 관계가 움직일 가능성입니다."
                                )
                        )
                ),

                new SpreadDefinition(
                        "양자택일",
                        "두 선택지의 과정과 결과를 비교하고 현재 가장 현실적인 방향을 확인합니다.",
                        "CHOICE_6",
                        4,
                        List.of(
                                new PositionDefinition(
                                        1,
                                        "현재 상황",
                                        "두 선택지 사이에서 고민하게 된 현재 상황입니다."
                                ),
                                new PositionDefinition(
                                        2,
                                        "A를 선택했을 때의 과정",
                                        "첫 번째 선택지를 골랐을 때 경험하게 될 과정입니다."
                                ),
                                new PositionDefinition(
                                        3,
                                        "B를 선택했을 때의 과정",
                                        "두 번째 선택지를 골랐을 때 경험하게 될 과정입니다."
                                ),
                                new PositionDefinition(
                                        4,
                                        "A를 선택했을 때의 결과",
                                        "첫 번째 선택지가 가져올 가능성과 결과입니다."
                                ),
                                new PositionDefinition(
                                        5,
                                        "B를 선택했을 때의 결과",
                                        "두 번째 선택지가 가져올 가능성과 결과입니다."
                                ),
                                new PositionDefinition(
                                        6,
                                        "최종 조언",
                                        "두 선택지를 비교할 때 가장 중요하게 생각해야 할 기준입니다."
                                )
                        )
                ),

                new SpreadDefinition(
                        "켈틱 크로스",
                        "현재 상황의 원인과 주변 영향, 마음속 기대와 앞으로의 흐름을 자세히 확인합니다.",
                        "CELTIC_10",
                        5,
                        List.of(
                                new PositionDefinition(
                                        1,
                                        "현재 상황",
                                        "질문과 가장 직접적으로 연결된 현재의 핵심 상황입니다."
                                ),
                                new PositionDefinition(
                                        2,
                                        "가로막는 문제",
                                        "현재 상황을 어렵게 만들거나 해결을 늦추는 요소입니다."
                                ),
                                new PositionDefinition(
                                        3,
                                        "의식적인 생각",
                                        "내가 겉으로 알고 있거나 분명하게 생각하는 부분입니다."
                                ),
                                new PositionDefinition(
                                        4,
                                        "마음속 깊은 원인",
                                        "겉으로 드러나지 않은 감정과 상황의 근본 원인입니다."
                                ),
                                new PositionDefinition(
                                        5,
                                        "과거의 영향",
                                        "현재 상황에 영향을 주고 있는 과거의 사건과 경험입니다."
                                ),
                                new PositionDefinition(
                                        6,
                                        "가까운 미래",
                                        "현재 흐름이 이어질 경우 가까운 시기에 나타날 변화입니다."
                                ),
                                new PositionDefinition(
                                        7,
                                        "나의 태도",
                                        "현재 상황을 바라보는 나의 자세와 행동 방식입니다."
                                ),
                                new PositionDefinition(
                                        8,
                                        "주변 환경",
                                        "상대방과 주변 사람들이 현재 상황에 미치는 영향입니다."
                                ),
                                new PositionDefinition(
                                        9,
                                        "기대와 두려움",
                                        "내가 바라는 결과와 동시에 걱정하고 있는 부분입니다."
                                ),
                                new PositionDefinition(
                                        10,
                                        "최종 흐름",
                                        "현재 선택과 행동이 이어질 경우 나타날 가능성입니다."
                                )
                        )
                )
        );
    }

    private void seedTarotCards() {

        List<TarotCard> cards =
                new ArrayList<>();

        cards.addAll(
                createMajorArcana()
        );

        cards.addAll(
                createMinorArcana()
        );

        Set<String> existingCodes =
                tarotCardRepository
                        .findAll()
                        .stream()
                        .map(TarotCard::getCode)
                        .collect(Collectors.toSet());

        List<TarotCard> cardsToSave =
                cards.stream()
                        .filter(
                                card ->
                                        !existingCodes.contains(
                                                card.getCode()
                                        )
                        )
                        .toList();

        if (!cardsToSave.isEmpty()) {
            tarotCardRepository
                    .saveAll(cardsToSave);
        }
    }

    private List<TarotCard> createMajorArcana() {

        List<MajorDefinition> definitions =
                List.of(
                        new MajorDefinition(
                                "바보",
                                "The Fool",
                                "새로운 시작, 자유로운 선택, 가능성을 향한 첫걸음을 의미합니다.",
                                "준비되지 않은 시작, 충동, 방향을 잃은 자유를 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "마법사",
                                "The Magician",
                                "의지, 실행력, 재능을 현실로 만드는 능력을 의미합니다.",
                                "실행력 부족, 능력의 잘못된 사용, 자신감 저하를 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "여사제",
                                "The High Priestess",
                                "직관, 내면의 지혜, 아직 드러나지 않은 진실을 의미합니다.",
                                "직관을 무시함, 비밀, 혼란스러운 내면을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "여황제",
                                "The Empress",
                                "풍요, 성장, 돌봄과 창조적인 에너지를 의미합니다.",
                                "과도한 의존, 성장 정체, 돌봄의 불균형을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "황제",
                                "The Emperor",
                                "질서, 책임, 현실적인 통제력과 안정된 기반을 의미합니다.",
                                "통제 과잉, 완고함, 불안정한 권위를 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "교황",
                                "The Hierophant",
                                "전통, 배움, 제도와 믿을 수 있는 조언을 의미합니다.",
                                "낡은 규칙, 맹목적인 믿음, 기존 방식에 대한 의문을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "연인",
                                "The Lovers",
                                "사랑, 관계, 가치관에 따른 중요한 선택을 의미합니다.",
                                "관계 불균형, 갈등, 선택의 어려움을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "전차",
                                "The Chariot",
                                "목표를 향한 전진, 의지와 상황 통제를 의미합니다.",
                                "방향 상실, 조급함, 통제하기 어려운 상황을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "힘",
                                "Strength",
                                "인내, 용기, 감정을 다루는 부드러운 힘을 의미합니다.",
                                "자신감 부족, 감정 억압, 인내의 한계를 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "은둔자",
                                "The Hermit",
                                "내면 탐색, 신중함, 혼자 생각할 시간이 필요함을 의미합니다.",
                                "고립, 지나친 고민, 필요한 조언을 피하는 모습을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "운명의 수레바퀴",
                                "Wheel of Fortune",
                                "변화하는 흐름, 기회와 예상하지 못한 전환점을 의미합니다.",
                                "기회의 지연, 반복되는 문제, 변화에 대한 저항을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "정의",
                                "Justice",
                                "공정한 판단, 책임, 행동에 따른 결과를 의미합니다.",
                                "불공정함, 책임 회피, 판단의 흔들림을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "매달린 사람",
                                "The Hanged Man",
                                "멈춤, 관점의 전환, 서두르지 않는 기다림을 의미합니다.",
                                "정체, 불필요한 희생, 놓아야 할 것을 붙잡는 모습을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "죽음",
                                "Death",
                                "끝과 새로운 시작, 피할 수 없는 변화와 정리를 의미합니다.",
                                "변화를 거부함, 끝내지 못함, 과거에 머무는 모습을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "절제",
                                "Temperance",
                                "균형, 조절, 서로 다른 요소의 조화를 의미합니다.",
                                "불균형, 감정 기복, 조절되지 않는 상황을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "악마",
                                "The Devil",
                                "집착, 유혹, 끊기 어려운 관계나 습관을 의미합니다.",
                                "집착에서 벗어남 또는 숨겨진 의존성을 인식하는 과정을 의미합니다."
                        ),
                        new MajorDefinition(
                                "탑",
                                "The Tower",
                                "갑작스러운 변화, 기존 구조의 붕괴와 진실의 드러남을 의미합니다.",
                                "변화를 피하려 함, 위기가 지연됨, 불안한 구조를 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "별",
                                "The Star",
                                "희망, 치유, 미래에 대한 긍정적인 가능성을 의미합니다.",
                                "희망 저하, 자신감 부족, 회복에 시간이 필요함을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "달",
                                "The Moon",
                                "불확실성, 숨은 감정, 직관과 혼란을 의미합니다.",
                                "두려움, 오해, 사실을 명확히 보기 어려운 상태를 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "태양",
                                "The Sun",
                                "기쁨, 성공, 명확함과 따뜻한 에너지를 의미합니다.",
                                "성공 지연, 과도한 낙관, 기쁨을 충분히 느끼지 못함을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "심판",
                                "Judgement",
                                "각성, 중요한 결론, 과거를 돌아보고 다시 선택함을 의미합니다.",
                                "결정을 미룸, 과거에 대한 후회, 자기 평가의 어려움을 의미할 수 있습니다."
                        ),
                        new MajorDefinition(
                                "세계",
                                "The World",
                                "완성, 성취, 하나의 과정이 성공적으로 마무리됨을 의미합니다.",
                                "미완성, 마무리 지연, 다음 단계로 넘어가지 못함을 의미할 수 있습니다."
                        )
                );

        List<TarotCard> cards =
                new ArrayList<>();

        for (
                int index = 0;
                index < definitions.size();
                index++
        ) {
            MajorDefinition definition =
                    definitions.get(index);

            String code =
                    String.format(
                            "major-%02d",
                            index
                    );

            cards.add(
                    TarotCard.create(
                            code,
                            definition.koreanName(),
                            definition.englishName(),
                            "M",
                            null,
                            String.valueOf(index),
                            "/img/tarot/cards/"
                                    + code
                                    + ".webp",
                            definition.upright(),
                            definition.reversed(),
                            index
                    )
            );
        }

        return cards;
    }

    private List<TarotCard> createMinorArcana() {

        SuitDefinition[] suits = {
                new SuitDefinition(
                        "WAND",
                        "완드",
                        "Wands",
                        "열정·행동·도전"
                ),
                new SuitDefinition(
                        "CUP",
                        "컵",
                        "Cups",
                        "감정·관계·직관"
                ),
                new SuitDefinition(
                        "SWORD",
                        "소드",
                        "Swords",
                        "생각·갈등·결정"
                ),
                new SuitDefinition(
                        "PENTACLE",
                        "펜타클",
                        "Pentacles",
                        "현실·재물·직업"
                )
        };

        RankDefinition[] ranks = {
                new RankDefinition(
                        "ace",
                        "에이스",
                        "Ace",
                        "새로운 가능성과 시작이 열리는 흐름",
                        "시작이 지연되거나 기회를 확신하지 못하는 흐름"
                ),
                new RankDefinition(
                        "02",
                        "2",
                        "Two",
                        "두 선택지와 균형을 맞추는 과정",
                        "선택의 혼란과 균형이 흔들리는 과정"
                ),
                new RankDefinition(
                        "03",
                        "3",
                        "Three",
                        "협력과 확장, 첫 번째 성과",
                        "협력 부족과 계획의 지연"
                ),
                new RankDefinition(
                        "04",
                        "4",
                        "Four",
                        "안정과 기반을 지키는 흐름",
                        "정체 또는 안정에 대한 집착"
                ),
                new RankDefinition(
                        "05",
                        "5",
                        "Five",
                        "갈등과 변화 속에서 배우는 과정",
                        "갈등 회피 또는 문제에서 벗어나는 과정"
                ),
                new RankDefinition(
                        "06",
                        "6",
                        "Six",
                        "회복, 이동, 균형을 되찾는 흐름",
                        "과거에 머무르거나 회복이 지연되는 흐름"
                ),
                new RankDefinition(
                        "07",
                        "7",
                        "Seven",
                        "자신의 위치를 지키고 점검하는 과정",
                        "자신감 저하 또는 잘못된 방어"
                ),
                new RankDefinition(
                        "08",
                        "8",
                        "Eight",
                        "빠른 전개, 집중과 반복을 통한 발전",
                        "진행 지연, 집중력 저하 또는 제한"
                ),
                new RankDefinition(
                        "09",
                        "9",
                        "Nine",
                        "완성을 앞둔 인내와 개인적인 성취",
                        "지침, 불안 또는 지나친 경계"
                ),
                new RankDefinition(
                        "10",
                        "10",
                        "Ten",
                        "하나의 주기가 완성되고 결과가 모이는 흐름",
                        "과도한 부담 또는 끝내야 할 것을 붙잡는 흐름"
                ),
                new RankDefinition(
                        "page",
                        "페이지",
                        "Page",
                        "새로운 소식, 호기심과 배움의 시작",
                        "미숙함, 소통 오류 또는 준비 부족"
                ),
                new RankDefinition(
                        "knight",
                        "나이트",
                        "Knight",
                        "목표를 향해 적극적으로 움직이는 흐름",
                        "조급함, 방향 상실 또는 무리한 행동"
                ),
                new RankDefinition(
                        "queen",
                        "퀸",
                        "Queen",
                        "성숙한 이해, 관리와 수용의 힘",
                        "감정 또는 현실 관리의 불균형"
                ),
                new RankDefinition(
                        "king",
                        "킹",
                        "King",
                        "책임, 통솔력과 성숙한 판단",
                        "통제 과잉, 완고함 또는 책임 회피"
                )
        };

        List<TarotCard> cards =
                new ArrayList<>();

        int sortOrder = 22;

        for (SuitDefinition suit : suits) {
            for (RankDefinition rank : ranks) {

                String code =
                        suit.code()
                                .toLowerCase()
                                + "-"
                                + rank.code();

                cards.add(
                        TarotCard.create(
                                code,
                                suit.koreanName()
                                        + " "
                                        + rank.koreanName(),
                                rank.englishName()
                                        + " of "
                                        + suit.englishName(),
                                "m",
                                suit.code(),
                                rank.code(),
                                "/img/tarot/cards/"
                                        + code
                                        + ".webp",
                                suit.theme()
                                        + " 영역에서 "
                                        + rank.upright()
                                        + "을 의미합니다.",
                                suit.theme()
                                        + " 영역에서 "
                                        + rank.reversed()
                                        + "을 의미할 수 있습니다.",
                                sortOrder
                        )
                );

                sortOrder++;
            }
        }

        return cards;
    }

    private record SpreadDefinition(
            String name,
            String description,
            String layoutCode,
            int sortOrder,
            List<PositionDefinition> positions
    ) {
    }

    private record PositionDefinition(
            int order,
            String name,
            String description
    ) {
    }

    private record MajorDefinition(
            String koreanName,
            String englishName,
            String upright,
            String reversed
    ) {
    }

    private record SuitDefinition(
            String code,
            String koreanName,
            String englishName,
            String theme
    ) {
    }

    private record RankDefinition(
            String code,
            String koreanName,
            String englishName,
            String upright,
            String reversed
    ) {
    }
}