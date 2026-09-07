package com.dajeom.calculation;

import com.dajeom.domain.LifeCategoryNarrativeDirective;
import com.dajeom.domain.LifeInterpretationCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 최종 7개 장문 풀이의 역할을 구분한다.
 *
 * 같은 LifeClaim이라도
 * 카테고리에 따라 다른 관점으로 설명하게 한다.
 */
@Component
public class LifeCategoryNarrativeDirectiveCatalog {

    public LifeCategoryNarrativeDirective directiveOf(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        return switch (category) {

            case TEMPERAMENT ->

                    new LifeCategoryNarrativeDirective(

                            category,

                            "내면의 판단 방식, 반응 패턴, 표현 방식, "
                                    + "장점과 과도하게 발현될 때의 부담을 중심으로 설명한다.",

                            List.of(
                                    "성격을 한 단어로 단정하지 않는다.",
                                    "장점과 주의점을 함께 설명한다.",
                                    "실제 생활에서 어떻게 발현될 수 있는지를 조건형으로 설명한다."
                            ),

                            List.of(
                                    "MBTI 유형을 임의 추정하지 않는다.",
                                    "정신질환이나 성격장애를 언급하지 않는다.",
                                    "어린 시절 사건을 만들어내지 않는다."
                            )
                    );

            case PAST_FLOW ->

                    new LifeCategoryNarrativeDirective(

                            category,

                            "원국이 보여주는 반복적 삶의 흐름과 대응 방식까지만 설명한다. "
                                    + "실제 연도별 과거 사건은 운 계산 근거가 있을 때만 다룬다.",

                            List.of(
                                    "원국 Claim은 반복 가능한 흐름으로 설명한다.",
                                    "특정 사건 대신 변화에 대응하는 방식과 패턴을 설명한다.",
                                    "향후 대운·세운 Claim이 추가되면 그때 시기 정보를 결합할 수 있도록 작성한다."
                            ),

                            List.of(
                                    "취업·퇴사·이사·결혼·이별 등의 과거 사건을 만들어내지 않는다.",
                                    "몇 살 때 무슨 일이 있었다고 쓰지 않는다.",
                                    "원국만으로 특정 연도를 언급하지 않는다."
                            )
                    );

            case FAMILY_RELATIONSHIPS ->

                    new LifeCategoryNarrativeDirective(

                            category,

                            "가족과 사람 사이에서 나타나는 거리 조절, "
                                    + "책임, 경계, 협력, 의사소통 패턴을 중심으로 설명한다.",

                            List.of(
                                    "관계의 상호작용 방식으로 설명한다.",
                                    "본인이 관계에서 조절할 수 있는 부분을 중심으로 설명한다.",
                                    "독립성과 연결감의 균형을 다룬다."
                            ),

                            List.of(
                                    "부모의 실제 성격을 단정하지 않는다.",
                                    "가족불화·단절·사별을 예측하지 않는다.",
                                    "특정 가족 구성원의 사건을 만들어내지 않는다."
                            )
                    );

            case LOVE_MARRIAGE ->

                    new LifeCategoryNarrativeDirective(

                            category,

                            "연애와 결혼에서의 관계 속도, 친밀감, "
                                    + "경계 설정, 책임 공유, 상대 선택 기준을 중심으로 설명한다.",

                            List.of(
                                    "관계 패턴과 선호 방식으로 설명한다.",
                                    "좋은 관계를 만들기 위한 조절 방향을 포함한다.",
                                    "독립성·책임·감정 교류의 균형을 다룬다."
                            ),

                            List.of(
                                    "결혼 여부를 단정하지 않는다.",
                                    "이혼·외도·재혼을 예측하지 않는다.",
                                    "배우자의 직업·외모·나이·재산을 만들어내지 않는다.",
                                    "원국 Claim만으로 결혼 시기를 제시하지 않는다."
                            )
                    );

            case WEALTH ->

                    new LifeCategoryNarrativeDirective(

                            category,

                            "돈 자체의 많고 적음보다 "
                                    + "자원 획득·관리·소비·위험 감수·성과 추구 방식을 중심으로 설명한다.",

                            List.of(
                                    "재물의 운영 방식과 의사결정 습관으로 설명한다.",
                                    "성과 추구와 안정 기반의 균형을 다룬다.",
                                    "재물 구조의 장점과 무리할 때의 부담을 함께 설명한다."
                            ),

                            List.of(
                                    "부자가 된다고 단정하지 않는다.",
                                    "파산한다고 단정하지 않는다.",
                                    "주식·코인·부동산 매매를 직접 권하지 않는다.",
                                    "구체적인 금액을 제시하지 않는다."
                            )
                    );

            case WORK_ACHIEVEMENT ->

                    new LifeCategoryNarrativeDirective(

                            category,

                            "일을 처리하는 방식, 책임 수용 방식, "
                                    + "성과 생산 방식, 경쟁과 조직 안에서의 역할을 중심으로 설명한다.",

                            List.of(
                                    "특정 직업 하나로 한정하지 않는다.",
                                    "잘 맞는 업무 환경과 운영 방식을 설명한다.",
                                    "성과를 지속하기 위해 필요한 조건을 함께 설명한다."
                            ),

                            List.of(
                                    "특정 회사나 직업을 반드시 해야 한다고 하지 않는다.",
                                    "승진·합격·창업 성공을 예측하지 않는다.",
                                    "연봉이나 사회적 지위를 단정하지 않는다."
                            )
                    );

            case HEALTH_LIFESTYLE ->

                    new LifeCategoryNarrativeDirective(

                            category,

                            "의학적 진단이 아니라 생활 리듬, "
                                    + "회복 습관, 활동량 관리, 스트레스 관리 관점으로만 설명한다.",

                            List.of(
                                    "수면·휴식·활동량·생활 루틴 등의 일반적 자기관리 관점으로 설명한다.",
                                    "명리 구조를 신체 질환과 직접 등치하지 않는다.",
                                    "필요하면 전문 의료 판단과 사주 해석은 별개임을 전제로 한다."
                            ),

                            List.of(
                                    "질병명을 예측하지 않는다.",
                                    "장기·신체 부위의 질환을 단정하지 않는다.",
                                    "수명·사망·사고를 예측하지 않는다.",
                                    "치료나 약물 복용을 사주로 지시하지 않는다."
                            )
                    );
        };
    }
}