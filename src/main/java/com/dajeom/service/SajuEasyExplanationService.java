package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuAnalysisResponse.BalanceItemResponse;
import com.dajeom.dto.SajuAnalysisResponse.InterpretationResponse;
import com.dajeom.dto.SajuAnalysisResponse.RelationResponse;
import com.dajeom.dto.SajuAnalysisResponse.ShinsalResponse;
import com.dajeom.dto.SajuAnalysisResponse.StrengthResponse;
import com.dajeom.dto.SajuAnalysisResponse.TenGodBalanceResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SajuEasyExplanationService {

    /**
     * 기존 계산 결과는 그대로 유지한다.
     *
     * summary와 interpretations만
     * 일반 사용자가 이해하기 쉬운 문장으로 교체한다.
     */
    public SajuAnalysisResponse enrich(
            SajuAnalysisResponse source
    ) {
        String easySummary =
                buildEasySummary(
                        source
                );

        List<InterpretationResponse> easyInterpretations =
                buildEasyInterpretations(
                        source
                );

        return new SajuAnalysisResponse(
                source.analysisId(),
                source.profileId(),
                source.status(),
                source.name(),
                source.calendar(),
                source.zodiac(),
                source.dayMaster(),
                easySummary,
                source.pillars(),
                source.elementBalance(),
                source.yinYangBalance(),
                source.tenGodBalance(),
                source.structure(),
                source.strength(),
                source.relations(),
                source.shinsal(),
                source.luck(),
                easyInterpretations,
                source.disclaimer(),
                source.completedAt()
        );
    }

    /**
     * 결과 화면과 영상 첫 장면에서 사용하는
     * 전체 요약 문장이다.
     */
    private String buildEasySummary(
            SajuAnalysisResponse source
    ) {
        String name =
                valueOrDefault(
                        source.name(),
                        "회원"
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

        String strength =
                source.strength() == null
                        ? "-"
                        : valueOrDefault(
                        source.strength()
                                .classification(),
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

        return name
                + "님은 "
                + dominantElement
                + " 기운의 특징인 "
                + elementKeyword(
                dominantElement
        )
                + "이 비교적 잘 드러나는 편입니다. "
                + weakestElement
                + " 기운은 상대적으로 적어 "
                + elementCompensation(
                weakestElement
        )
                + "을 의식하면 균형을 잡는 데 도움이 됩니다. "
                + strengthMeaning(
                strength
        )
                + " 또한 "
                + dominantTenGod
                + "의 작용도 눈에 띕니다. "
                + tenGodMeaning(
                dominantTenGod
        );
    }

    /**
     * 기존의 전문적인 분야별 해설을
     * 쉬운 생활 언어로 다시 구성한다.
     */
    private List<InterpretationResponse> buildEasyInterpretations(
            SajuAnalysisResponse source
    ) {
        List<InterpretationResponse> result =
                new ArrayList<>();

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

        String strength =
                source.strength() == null
                        ? "-"
                        : valueOrDefault(
                        source.strength()
                                .classification(),
                        "-"
                );

        /*
         * 오행에서 가장 강한 기운
         */
        result.add(
                new InterpretationResponse(
                        "오행",
                        "가장 강한 기운 · "
                                + dominantElement,
                        elementStrongMeaning(
                                dominantElement
                        )
                )
        );

        /*
         * 오행에서 상대적으로 적은 기운
         */
        result.add(
                new InterpretationResponse(
                        "오행",
                        "보완하면 좋은 기운 · "
                                + weakestElement,
                        elementWeakMeaning(
                                weakestElement
                        )
                )
        );

        /*
         * 성격과 기본 성향
         */
        result.add(
                new InterpretationResponse(
                        "성격",
                        "타고난 성향",
                        strengthMeaning(
                                strength
                        )
                                + " "
                                + dominantTenGod
                                + "의 특징도 함께 나타날 수 있습니다. "
                                + tenGodMeaning(
                                dominantTenGod
                        )
                )
        );

        /*
         * 직업
         */
        result.add(
                new InterpretationResponse(
                        "직업",
                        "일할 때 드러나는 강점",
                        careerMeaning(
                                dominantElement,
                                dominantTenGod
                        )
                )
        );

        /*
         * 재물
         */
        result.add(
                new InterpretationResponse(
                        "재물",
                        "돈을 다루는 방식",
                        moneyMeaning(
                                dominantTenGod,
                                strength
                        )
                )
        );

        /*
         * 관계와 연애
         */
        result.add(
                new InterpretationResponse(
                        "연애",
                        "사람과 관계를 맺는 방식",
                        relationshipMeaning(
                                dominantTenGod,
                                source.relations()
                        )
                )
        );

        /*
         * 건강
         */
        result.add(
                new InterpretationResponse(
                        "건강",
                        "생활 관리에서 볼 점",
                        "건강운은 질병을 예언하는 뜻이 아닙니다. "
                                + weakestElement
                                + " 기운이 적다는 결과는 "
                                + elementLifestyle(
                                weakestElement
                        )
                                + "을 생활 습관으로 보완해 보라는 참고값으로 이해하는 것이 좋습니다."
                )
        );

        /*
         * 시험과 학업
         */
        result.add(
                new InterpretationResponse(
                        "시험",
                        "공부와 시험 준비 방식",
                        studyMeaning(
                                source.tenGodBalance(),
                                source.strength()
                        )
                )
        );

        /*
         * 창업과 사업
         */
        result.add(
                new InterpretationResponse(
                        "창업",
                        "사업과 독립 활동의 방향",
                        startupMeaning(
                                dominantElement,
                                dominantTenGod,
                                source.strength()
                        )
                )
        );

        /*
         * 대운
         */
        if (
                source.luck() != null
        ) {
            result.add(
                    new InterpretationResponse(
                            "대운",
                            "현재 큰 흐름",
                            "대운은 좋은 시기와 나쁜 시기를 단정하는 표가 아니라, "
                                    + "어떤 주제가 강해지는지를 보는 큰 흐름입니다. 현재 대운은 "
                                    + valueOrDefault(
                                    source.luck()
                                            .currentDecade(),
                                    "확인되지 않음"
                            )
                                    + "이며 "
                                    + valueOrDefault(
                                    source.luck()
                                            .direction(),
                                    "방향 미상"
                            )
                                    + "으로 진행됩니다. 실제 선택에서는 자금, 경력, 건강과 주변 환경을 함께 확인해야 합니다."
                    )
            );
        }

        /*
         * 신살
         */
        List<ShinsalResponse> shinsal =
                source.shinsal() == null
                        ? List.of()
                        : source.shinsal();

        if (
                shinsal.isEmpty()
        ) {
            result.add(
                    new InterpretationResponse(
                            "신살",
                            "신살 보는 방법",
                            "현재 두드러진 주요 신살이 많지 않습니다. "
                                    + "신살은 사건을 확정하는 예언이 아니라 성향과 상황을 보조적으로 설명하는 자료입니다."
                    )
            );

        } else {
            shinsal.stream()
                    .filter(
                            Objects::nonNull
                    )
                    .limit(
                            8
                    )
                    .forEach(
                            item -> result.add(
                                    new InterpretationResponse(
                                            "신살",
                                            valueOrDefault(
                                                    item.name(),
                                                    "주요 신살"
                                            ),
                                            shinsalMeaning(
                                                    item.name(),
                                                    item.description()
                                            )
                                    )
                            )
                    );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 가장 강한 오행의 쉬운 설명
     */
    private String elementStrongMeaning(
            String element
    ) {
        return switch (element) {
            case "목" ->
                    "성장하려는 힘과 새로운 것을 시작하는 태도가 강점입니다. "
                            + "배우고 기획하며 범위를 넓히는 일에서 장점이 나타날 수 있습니다.";

            case "화" ->
                    "표현력과 추진력이 강점입니다. "
                            + "분위기를 만들고 빠르게 행동하며 자신의 존재감을 드러내는 힘이 있습니다.";

            case "토" ->
                    "책임감과 안정감, 꾸준히 유지하는 힘이 강점입니다. "
                            + "맡은 일을 쉽게 놓지 않고 현실적으로 관리하는 편입니다.";

            case "금" ->
                    "분명한 기준과 판단력, 정리하는 능력이 강점입니다. "
                            + "정확성과 원칙이 필요한 상황에서 장점이 나타날 수 있습니다.";

            case "수" ->
                    "상황을 읽는 감각과 유연한 적응력이 강점입니다. "
                            + "정보와 흐름을 파악하고 변화에 맞추는 힘이 있습니다.";

            default ->
                    "가장 강한 기운은 장점으로 활용할 수 있지만, "
                            + "지나치게 한쪽으로 쓰이지 않도록 전체 균형도 함께 보는 것이 좋습니다.";
        };
    }

    /**
     * 상대적으로 부족한 오행의 쉬운 설명
     */
    private String elementWeakMeaning(
            String element
    ) {
        return switch (element) {
            case "목" ->
                    "시작을 오래 고민하기보다 작은 목표부터 바로 실행하고, "
                            + "배움과 계획을 꾸준히 이어가면 도움이 됩니다.";

            case "화" ->
                    "생각과 감정을 말과 행동으로 조금 더 분명하게 표현하고, "
                            + "적절한 활동성을 유지하면 도움이 됩니다.";

            case "토" ->
                    "수면, 식사, 일정처럼 반복되는 생활 리듬을 안정시키고 "
                            + "한 번 정한 습관을 꾸준히 유지하면 도움이 됩니다.";

            case "금" ->
                    "기준과 우선순위를 미리 정하고, "
                            + "불필요한 일과 물건을 정리하는 습관이 도움이 됩니다.";

            case "수" ->
                    "한 가지 방식만 고집하지 않고 상황에 맞춰 생각과 계획을 "
                            + "유연하게 조정하는 연습이 도움이 됩니다.";

            default ->
                    "부족한 기운은 결함이 아니라 생활 습관과 환경을 통해 "
                            + "보완할 수 있는 부분입니다.";
        };
    }

    /**
     * 오행 핵심 단어
     */
    private String elementKeyword(
            String element
    ) {
        return switch (element) {
            case "목" ->
                    "성장하려는 힘과 새로운 것을 시작하는 태도";

            case "화" ->
                    "표현력과 추진력, 빠른 반응";

            case "토" ->
                    "책임감과 안정감, 꾸준히 유지하는 힘";

            case "금" ->
                    "분명한 기준과 판단력, 정리하는 능력";

            case "수" ->
                    "상황을 읽는 감각과 유연한 적응력";

            default ->
                    "여러 기운을 조정하는 능력";
        };
    }

    /**
     * 부족한 오행 보완 방법
     */
    private String elementCompensation(
            String element
    ) {
        return switch (element) {
            case "목" ->
                    "작은 목표부터 바로 시작하고 배움의 흐름을 꾸준히 만드는 것";

            case "화" ->
                    "생각과 감정을 말과 행동으로 조금 더 분명하게 표현하는 것";

            case "토" ->
                    "수면, 식사, 일정처럼 반복되는 생활 리듬을 안정시키는 것";

            case "금" ->
                    "기준과 우선순위를 정하고 불필요한 일을 정리하는 것";

            case "수" ->
                    "한 가지 방식만 고집하지 않고 상황에 맞춰 유연하게 조정하는 것";

            default ->
                    "부족한 부분을 생활 습관과 환경으로 보완하는 것";
        };
    }

    /**
     * 건강 화면에 사용하는 생활 관리 표현
     */
    private String elementLifestyle(
            String element
    ) {
        return switch (element) {
            case "목" ->
                    "규칙적인 스트레칭과 산책, 배움과 계획을 이어가는 습관";

            case "화" ->
                    "적절한 활동과 햇빛, 감정을 건강하게 표현하는 습관";

            case "토" ->
                    "규칙적인 식사와 수면, 무리하지 않는 일정 관리";

            case "금" ->
                    "호흡과 자세를 점검하고 주변 환경을 정리하는 습관";

            case "수" ->
                    "수분 섭취와 충분한 휴식, 감정을 오래 쌓아두지 않는 습관";

            default ->
                    "기본적인 수면, 식사와 운동 리듬";
        };
    }

    /**
     * 신강·신약을 쉬운 말로 변환
     */
    private String strengthMeaning(
            String classification
    ) {
        return switch (classification) {
            case "신강" ->
                    "자기 중심과 버티는 힘이 비교적 강한 편이라 주도성이 장점이 되지만, "
                            + "혼자 감당하거나 고집이 세지지 않도록 균형이 필요합니다.";

            case "신약" ->
                    "주변 상황을 세밀하게 느끼고 배려하는 힘이 있지만, "
                            + "환경의 영향을 많이 받아 쉽게 지칠 수 있으므로 도움과 자원을 잘 활용하는 것이 중요합니다.";

            case "중화" ->
                    "한쪽으로 크게 치우치지 않은 편이라 상황에 맞춰 균형을 잡는 힘이 있지만, "
                            + "운과 환경에 따라 강점이 달라질 수 있습니다.";

            default ->
                    "일간의 힘은 계절과 전체 구조를 함께 봐야 하므로 "
                            + "현재 값은 참고용으로 이해하는 것이 좋습니다.";
        };
    }

    /**
     * 십성을 쉬운 말로 변환
     */
    private String tenGodMeaning(
            String tenGod
    ) {
        return switch (tenGod) {
            case "일원" ->
                    "사주에서 나 자신을 뜻하는 중심 기준입니다.";

            case "비견" ->
                    "독립심과 자기주도성, 동료의식과 경쟁심을 나타냅니다.";

            case "겁재" ->
                    "빠른 추진력과 승부욕, 사람과 자원을 나누는 상황을 나타냅니다.";

            case "식신" ->
                    "기술과 생산성, 표현과 돌봄처럼 결과물을 꾸준히 만들어내는 힘입니다.";

            case "상관" ->
                    "아이디어와 표현력, 문제를 발견하고 기존 틀을 바꾸려는 힘입니다.";

            case "편재" ->
                    "다양한 기회와 사람을 통해 돈과 자원을 움직이는 성향입니다.";

            case "정재" ->
                    "안정적인 수입과 계획적인 소비, 현실적인 관리 성향입니다.";

            case "편관" ->
                    "압박 속에서 책임을 지고 빠르게 대응하는 힘과 긴장감을 나타냅니다.";

            case "정관" ->
                    "규칙과 책임, 조직 생활과 신뢰를 중요하게 보는 성향입니다.";

            case "편인" ->
                    "직관과 독창적인 학습, 한 분야를 깊이 파고드는 사고를 나타냅니다.";

            case "정인" ->
                    "배움과 보호, 자격과 문서를 통해 안정적으로 지식을 받아들이는 힘입니다.";

            default ->
                    "사주에서 사람과 일, 돈과 표현 방식의 역할을 설명하는 요소입니다.";
        };
    }

    /**
     * 직업 해설
     */
    private String careerMeaning(
            String dominantElement,
            String dominantTenGod
    ) {
        String elementCareer =
                switch (dominantElement) {
                    case "목" ->
                            "기획, 교육, 콘텐츠와 성장 산업처럼 새로운 것을 키우는 일과 잘 연결될 수 있습니다.";

                    case "화" ->
                            "홍보, 발표, 디자인과 서비스처럼 표현과 반응 속도가 중요한 일과 잘 연결될 수 있습니다.";

                    case "토" ->
                            "운영, 관리, 회계와 행정처럼 안정성과 지속성이 필요한 일과 잘 연결될 수 있습니다.";

                    case "금" ->
                            "기술, 보안, 품질과 법무처럼 정확한 기준과 판단이 필요한 일과 잘 연결될 수 있습니다.";

                    case "수" ->
                            "연구, 데이터, 상담과 유통처럼 정보와 흐름을 읽는 일과 잘 연결될 수 있습니다.";

                    default ->
                            "강점을 반복해서 활용하고 결과를 쌓을 수 있는 일이 잘 맞습니다.";
                };

        return elementCareer
                + " "
                + dominantTenGod
                + "의 작용도 함께 나타날 수 있습니다. "
                + tenGodMeaning(
                dominantTenGod
        )
                + " 실제 직업 선택에서는 현재 경력, 적성, 근무환경을 함께 판단하는 것이 좋습니다.";
    }

    /**
     * 재물 해설
     */
    private String moneyMeaning(
            String dominantTenGod,
            String strength
    ) {
        String base =
                switch (dominantTenGod) {
                    case "정재" ->
                            "안정적인 수입과 계획적인 소비를 선호하는 편입니다. "
                                    + "예산과 저축 계획을 세울수록 강점이 살아납니다.";

                    case "편재" ->
                            "사람과 기회를 넓게 보고 돈의 흐름을 만드는 감각이 있을 수 있습니다. "
                                    + "다만 지출과 위험 관리가 함께 필요합니다.";

                    case "식신" ->
                            "기술, 생산물과 서비스처럼 직접 만든 결과가 수입으로 연결되기 쉽습니다.";

                    case "상관" ->
                            "아이디어와 차별화된 표현이 수입 기회로 이어질 수 있지만 "
                                    + "수입의 변동성을 관리해야 합니다.";

                    case "비견", "겁재" ->
                            "자기 힘으로 벌고 움직이려는 성향이 강하지만 "
                                    + "동업, 경쟁과 공동 지출에서는 기준을 분명히 하는 것이 좋습니다.";

                    default ->
                            "재물은 한 번의 큰 기회보다 자신의 강점을 꾸준히 "
                                    + "수입 구조로 연결하는 방식이 안정적입니다.";
                };

        if (
                "신약".equals(
                        strength
                )
        ) {
            return base
                    + " 재물 기회가 커질수록 체력과 감당 범위를 함께 확인하는 것이 중요합니다.";
        }

        if (
                "신강".equals(
                        strength
                )
        ) {
            return base
                    + " 실행력은 강점이지만 지나친 확신이나 과감한 지출은 한 번 더 점검하는 것이 좋습니다.";
        }

        return base
                + " 수입과 지출의 균형을 꾸준히 확인하는 방식이 잘 맞습니다.";
    }

    /**
     * 연애와 인간관계 해설
     */
    private String relationshipMeaning(
            String dominantTenGod,
            RelationResponse relations
    ) {
        String base =
                switch (dominantTenGod) {
                    case "비견", "겁재" ->
                            "관계에서 대등함과 자기 의견을 중요하게 여기는 편입니다. "
                                    + "서로의 영역과 주도권을 존중하면 관계가 편안해집니다.";

                    case "식신", "상관" ->
                            "대화와 감정 표현이 관계 만족도에 큰 영향을 줍니다. "
                                    + "솔직함은 장점이지만 표현의 강도를 조절하면 좋습니다.";

                    case "편재", "정재" ->
                            "약속과 현실적인 신뢰를 중요하게 여기는 편입니다. "
                                    + "말보다 행동으로 마음을 보여줄 수 있습니다.";

                    case "편관", "정관" ->
                            "관계를 책임 있게 대하려 하지만 "
                                    + "기준이 높거나 부담을 혼자 떠안을 수 있습니다.";

                    case "편인", "정인" ->
                            "정서적인 이해와 안정감을 중요하게 여기며 "
                                    + "마음을 열기까지 시간이 필요할 수 있습니다.";

                    default ->
                            "서로의 속도와 표현 방식을 확인하며 "
                                    + "관계의 균형을 맞추는 것이 중요합니다.";
                };

        if (
                relations == null
        ) {
            return base;
        }

        int relationCount =
                safeSize(
                        relations.heavenlyStemRelations()
                )
                        + safeSize(
                        relations.earthlyBranchRelations()
                );

        if (
                relationCount > 0
        ) {
            return base
                    + " 원국 안에 관계 변화 요소가 있어 가까운 사이일수록 "
                    + "오해를 오래 두지 않고 바로 확인하는 습관이 도움이 됩니다.";
        }

        return base;
    }

    /**
     * 시험과 학업 해설
     */
    private String studyMeaning(
            TenGodBalanceResponse tenGodBalance,
            StrengthResponse strength
    ) {
        if (
                tenGodBalance == null
                        || tenGodBalance.items() == null
        ) {
            return "이해와 표현을 함께 반복하는 방식이 잘 맞습니다. "
                    + "일정한 학습량, 오답 정리와 실전 문제풀이가 실제 결과에 가장 큰 영향을 줍니다.";
        }

        Map<String, Integer> counts =
                tenGodBalance.items()
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .collect(
                                Collectors.toMap(
                                        BalanceItemResponse::name,
                                        BalanceItemResponse::count,
                                        Integer::sum
                                )
                        );

        int resource =
                counts.getOrDefault(
                        "편인",
                        0
                )
                        + counts.getOrDefault(
                        "정인",
                        0
                );

        int expression =
                counts.getOrDefault(
                        "식신",
                        0
                )
                        + counts.getOrDefault(
                        "상관",
                        0
                );

        if (
                resource > expression
        ) {
            return "내용을 읽고 이해하고 정리하는 힘이 상대적으로 잘 나타납니다. "
                    + "실제 시험에서는 아는 내용을 답으로 꺼내는 연습이 필요하므로 "
                    + "문제풀이와 시간 제한 훈련을 함께 하는 것이 좋습니다.";
        }

        if (
                expression > resource
        ) {
            return "배운 내용을 말이나 글로 표현하고 문제에 적용하는 힘이 잘 나타날 수 있습니다. "
                    + "다만 개념의 빈틈이 생기지 않도록 기본 이론을 반복해서 확인하는 것이 좋습니다.";
        }

        String classification =
                strength == null
                        ? "-"
                        : valueOrDefault(
                        strength.classification(),
                        "-"
                );

        return "이해와 표현을 함께 반복하는 방식이 잘 맞습니다. "
                + "일정한 학습량, 오답 정리와 실전 문제풀이가 실제 결과에 가장 큰 영향을 줍니다. "
                + strengthMeaning(
                classification
        );
    }

    /**
     * 창업과 사업 해설
     */
    private String startupMeaning(
            String dominantElement,
            String dominantTenGod,
            StrengthResponse strength
    ) {
        String usefulGod =
                strength == null
                        ? "-"
                        : valueOrDefault(
                        strength.usefulGod(),
                        "-"
                );

        return careerMeaning(
                dominantElement,
                dominantTenGod
        )
                + " 창업에서는 작은 기능부터 시장 반응을 확인하고 "
                + "매출, 비용과 고객 데이터를 기준으로 단계적으로 확장하는 것이 안전합니다. "
                + "보완에 도움이 되는 기운은 "
                + usefulGod
                + "으로 표시됩니다.";
    }

    /**
     * 신살을 일반인이 이해하기 쉬운 말로 변환한다.
     *
     * 신살만으로 사건, 사고, 이별과 질병을 단정하지 않는다.
     */
    private String shinsalMeaning(
            String name,
            String originalDescription
    ) {
        return switch (
                valueOrDefault(
                        name,
                        ""
                )
                ) {
            case "백호살" ->
                    "이름이 강해 보이지만 사고를 확정하는 뜻은 아닙니다. "
                            + "위기에서 빠르게 판단하고 강하게 대응하는 성향으로 참고합니다.";

            case "역마살" ->
                    "이동과 변화, 여행과 이직, 새로운 환경에서 활동성이 살아나는 흐름입니다.";

            case "화개살" ->
                    "혼자 깊이 생각하고 예술, 연구, 철학, 상담과 기술처럼 "
                            + "한 분야를 파고드는 성향입니다.";

            case "도화살" ->
                    "사람의 시선을 끌고 관계와 표현에서 존재감이 나타나는 흐름입니다.";

            case "장성살" ->
                    "책임을 맡고 사람이나 일을 이끄는 주도성과 리더십을 뜻합니다.";

            case "천을귀인" ->
                    "어려운 상황에서 도움을 받거나 해결의 실마리를 만나는 흐름으로 설명합니다.";

            case "문창귀인" ->
                    "공부와 문서, 글쓰기와 기획, 표현 능력을 보조하는 흐름입니다.";

            case "양인살" ->
                    "결단력과 추진력이 강하지만 감정이나 행동이 너무 급해지지 않도록 "
                            + "조절이 필요한 흐름입니다.";

            case "괴강살" ->
                    "강한 자존감과 독립성, 결단력이 나타날 수 있으며 "
                            + "타인과의 완급 조절이 중요합니다.";

            case "공망" ->
                    "해당 영역이 단순히 없다는 뜻보다 기대한 방식과 다르게 작용하거나 "
                            + "채워가는 과정이 필요하다는 보조 표시입니다.";

            case "원진살" ->
                    "관계에서 마음과 표현이 엇갈려 오해가 생기기 쉬운 흐름을 "
                            + "보조적으로 설명합니다.";

            case "귀문관살" ->
                    "감각과 집중력이 예민할 수 있으나 생각이 많아지거나 "
                            + "긴장이 쌓이지 않도록 관리가 필요한 흐름입니다.";

            default -> {
                if (
                        originalDescription == null
                                || originalDescription.isBlank()
                ) {
                    yield "전체 사주를 보조적으로 설명하는 표식이며 "
                            + "특정 사건을 단정하는 의미는 아닙니다.";
                }

                yield originalDescription
                        + " 다만 이 요소 하나만으로 결과를 단정하지 않습니다.";
            }
        };
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
}