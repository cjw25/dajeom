package com.dajeom.resolver;

import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.TwelveLifeStageAnalysis;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 18차-2.
 *
 * 3~11차 원국 deterministic 분석 객체를
 * LifeDomainEvidenceReference로 연결하는
 * 요청별 Resolver Factory.
 *
 * =================================================
 *
 * 이 Factory가 필요한 이유:
 *
 * LuckIntegratedAnalysis는
 * 16차 통합 운 Anchor이며
 * PalaceAnalysis를 보존하지만,
 *
 * 3~10차 원국 분석 객체 전체를
 * 다시 소유하지 않는다.
 *
 * 따라서 현재 요청에서 이미 계산된:
 *
 * - NatalChartAdvancedAnalysis
 * - TenGodCompositeAnalysis
 *
 * 를 immutable closure로 캡처한다.
 *
 * =================================================
 *
 * 11차 TwelveLifeStageAnalysis는
 * 현재 PalaceAnalysis가 직접 보존하고 있으므로
 * PalaceAnalysis.twelveLifeStageAnalysis()
 * 객체를 그대로 사용한다.
 *
 * =================================================
 *
 * 금지:
 *
 * - 3~11차 재계산
 * - 새로운 분석 객체 생성
 * - 문자열만 복사하고 upstream source 폐기
 * - singleton mutable request state
 */
@Component
public class LifeDomainNatalEvidenceSourceResolverFactory {

    /**
     * =================================================
     * 공식 Factory API
     * =================================================
     */
    public LifeDomainNatalEvidenceSourceResolver create(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "18차 원국 Evidence Adapter에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "18차 원국 Evidence Adapter에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "18차 원국 Evidence Adapter에는 PalaceAnalysis가 필요합니다."
        );

        validateCapturedContext(
                advancedAnalysis,
                tenGodCompositeAnalysis,
                palaceAnalysis
        );

        TwelveLifeStageAnalysis twelveLifeStageAnalysis =
                Objects.requireNonNull(
                        palaceAnalysis.twelveLifeStageAnalysis(),
                        "PalaceAnalysis에 11차 TwelveLifeStageAnalysis가 없습니다."
                );

        /*
         * =================================================
         * 요청별 immutable Resolver.
         * =================================================
         *
         * Spring singleton에 사용자별 분석 상태를
         * mutable field로 저장하지 않는다.
         */
        return luckIntegratedAnalysis -> {

            Objects.requireNonNull(
                    luckIntegratedAnalysis,
                    "18차 원국 Evidence 연결에는 LuckIntegratedAnalysis가 필요합니다."
            );

            validateLuckIntegratedAnchor(
                    luckIntegratedAnalysis,
                    palaceAnalysis
            );

            List<LifeDomainEvidenceReference<?>>
                    result =
                    new ArrayList<>();

            /*
             * =================================================
             * 3차
             * ElementStrengthAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .ELEMENT_STRENGTH,

                            "NATAL:ELEMENT_STRENGTH",

                            advancedAnalysis
                                    .elementStrengthAnalysis(),

                            "원국 오행 세력 분석",

                            List.of(
                                    "3차 ElementStrengthAnalysis 원본 객체를 그대로 연결합니다.",
                                    "오행의 강약과 분포를 생활영역 Rule의 핵심 구조 Evidence로 사용합니다.",
                                    "18차에서 오행 세력을 다시 계산하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 4차
             * DayMasterStrengthAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .DAY_MASTER_STRENGTH,

                            "NATAL:DAY_MASTER_STRENGTH",

                            advancedAnalysis
                                    .dayMasterStrengthAnalysis(),

                            "원국 신강·신약 분석",

                            List.of(
                                    "4차 DayMasterStrengthAnalysis 원본 객체를 그대로 연결합니다.",
                                    "일간의 강약 상태를 생활영역 핵심 구조 Evidence로 사용합니다.",
                                    "18차에서 신강·신약을 재판정하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 5차
             * StructureAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .STRUCTURE,

                            "NATAL:STRUCTURE",

                            advancedAnalysis
                                    .structureAnalysis(),

                            "원국 격국 분석",

                            List.of(
                                    "5차 StructureAnalysis 원본 객체를 그대로 연결합니다.",
                                    "주격과 복수 격국 후보 및 검토 상태를 기존 분석 그대로 보존합니다.",
                                    "생활영역 단계에서 격국을 하나의 새 점수로 축약하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 6차
             * ClimateHealthAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .CLIMATE_DISEASE_FLOW,

                            "NATAL:CLIMATE_DISEASE_FLOW",

                            advancedAnalysis
                                    .climateHealthAnalysis(),

                            "원국 조후·병약·통관 분석",

                            List.of(
                                    "6차 ClimateHealthAnalysis 원본 객체를 그대로 연결합니다.",
                                    "조후·병약·통관 판단을 기존 deterministic 결과 그대로 사용합니다.",
                                    "18차에서 계절·병약·통관 공식을 다시 계산하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 7차
             * UsefulGodAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .USEFUL_GOD,

                            "NATAL:USEFUL_GOD",

                            advancedAnalysis
                                    .usefulGodAnalysis(),

                            "원국 용신 분석",

                            List.of(
                                    "7차 UsefulGodAnalysis 원본 객체를 그대로 연결합니다.",
                                    "용신·희신·기피 오행과 선택 근거를 복수 후보 상태 그대로 보존합니다.",
                                    "18차에서 새로운 용신을 선택하거나 기존 후보를 단일값으로 강제하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 8차
             * RelationAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .NATAL_RELATION,

                            "NATAL:RELATION",

                            advancedAnalysis
                                    .relationAnalysis(),

                            "원국 천간·지지 관계 분석",

                            List.of(
                                    "8차 RelationAnalysis 원본 객체를 그대로 연결합니다.",
                                    "합·충·형·파·해 및 중첩 관계를 기존 분석 그대로 보존합니다.",
                                    "동일 글자 사이의 복수 관계를 18차에서 임의 제거하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 9차
             * CombinationTransformationAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .TRANSFORMATION,

                            "NATAL:TRANSFORMATION",

                            advancedAnalysis
                                    .combinationTransformationAnalysis(),

                            "원국 합화·변환 분석",

                            List.of(
                                    "9차 CombinationTransformationAnalysis 원본 객체를 그대로 연결합니다.",
                                    "결합 존재, 합화 후보, 실제 활성, 원기능 감소 상태를 분리한 기존 결과를 사용합니다.",
                                    "18차에서 합이 있다는 이유만으로 실제 합화 활성으로 변경하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 10차
             * TenGodCompositeAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .TEN_GOD_COMPOSITE,

                            "NATAL:TEN_GOD_COMPOSITE",

                            tenGodCompositeAnalysis,

                            "원국 십성 복합구조 분석",

                            List.of(
                                    "10차 TenGodCompositeAnalysis 원본 객체를 그대로 연결합니다.",
                                    "십성 분포, 계열 구조, 핵심 Pattern과 교차검증 상태를 함께 보존합니다.",
                                    "생활영역 단계에서 십성 구조를 단순 존재 여부로 다시 축약하지 않습니다."
                            )
                    )
            );

            /*
             * =================================================
             * 11차
             * TwelveLifeStageAnalysis
             * =================================================
             */

            result.add(
                    reference(
                            LifeDomainEvidenceSourceType
                                    .TWELVE_LIFE_STAGE,

                            "NATAL:TWELVE_LIFE_STAGE",

                            twelveLifeStageAnalysis,

                            "원국 12운성 분석",

                            List.of(
                                    "11차 TwelveLifeStageAnalysis 원본 객체를 PalaceAnalysis에서 그대로 연결합니다.",
                                    "시간 미상 원국에서는 기존 THREE_PILLAR_ONLY 범위를 그대로 보존합니다.",
                                    "18차에서 누락된 시주 12운성을 임의 생성하지 않습니다."
                            )
                    )
            );

            return List.copyOf(
                    result
            );
        };
    }

    /*
     * =================================================
     * Capture Context 검증
     * =================================================
     */

    private void validateCapturedContext(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        /*
         * 일간.
         */
        if (
                advancedAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3차 ElementStrengthAnalysis와 12차 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10차 TenGodCompositeAnalysis와 12차 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        /*
         * 시간 미상 / 완전 원국 상태.
         */
        if (
                advancedAnalysis.hasHourPillar()
                        != palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "3~9차 원국 분석과 12차 PalaceAnalysis의 completeChart 상태가 일치하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.completeChart()
                        != palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "10차 TenGodCompositeAnalysis와 12차 PalaceAnalysis의 completeChart 상태가 일치하지 않습니다."
            );
        }

        /*
         * 10차가 현재 5/7/9차 객체를
         * 실제로 참조하는지 검증.
         */
        if (
                tenGodCompositeAnalysis
                        .structureAnalysis()
                        != advancedAnalysis
                        .structureAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차 TenGodCompositeAnalysis가 현재 5차 StructureAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .usefulGodAnalysis()
                        != advancedAnalysis
                        .usefulGodAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차 TenGodCompositeAnalysis가 현재 7차 UsefulGodAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .combinationTransformationAnalysis()
                        != advancedAnalysis
                        .combinationTransformationAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차 TenGodCompositeAnalysis가 현재 9차 CombinationTransformationAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * 9차가 현재 8차 / 3차를 참조하는지 검증.
         */
        if (
                advancedAnalysis
                        .combinationTransformationAnalysis()
                        .relationAnalysis()
                        != advancedAnalysis
                        .relationAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "9차 CombinationTransformationAnalysis가 현재 8차 RelationAnalysis를 참조하지 않습니다."
            );
        }

        if (
                advancedAnalysis
                        .combinationTransformationAnalysis()
                        .elementStrengthAnalysis()
                        != advancedAnalysis
                        .elementStrengthAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "9차 CombinationTransformationAnalysis가 현재 3차 ElementStrengthAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * 11차는 12차 내부에서 보존되는
         * 실제 upstream 객체를 사용한다.
         */
        TwelveLifeStageAnalysis twelveLifeStageAnalysis =
                Objects.requireNonNull(
                        palaceAnalysis.twelveLifeStageAnalysis(),
                        "12차 PalaceAnalysis에 11차 TwelveLifeStageAnalysis가 없습니다."
                );

        if (
                twelveLifeStageAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "11차 TwelveLifeStageAnalysis와 12차 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                twelveLifeStageAnalysis.completeChart()
                        != palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "11차 TwelveLifeStageAnalysis와 12차 PalaceAnalysis의 completeChart 상태가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 16차 Anchor 검증
     * =================================================
     */

    private void validateLuckIntegratedAnchor(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            PalaceAnalysis capturedPalaceAnalysis

    ) {

        if (
                luckIntegratedAnalysis.natalAnalysis()
                        != capturedPalaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainNatalEvidenceSourceResolver가 생성될 때의 원국과 "
                            + "현재 LuckIntegratedAnalysis의 원국이 일치하지 않습니다."
            );
        }

        if (
                luckIntegratedAnalysis.dayMaster()
                        != capturedPalaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "16차 LuckIntegratedAnalysis와 원국 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                luckIntegratedAnalysis.completeChart()
                        != capturedPalaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "16차 LuckIntegratedAnalysis와 원국 PalaceAnalysis의 completeChart 상태가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Reference Factory
     * =================================================
     */

    private <T> LifeDomainEvidenceReference<T> reference(

            LifeDomainEvidenceSourceType sourceType,

            String sourceKey,

            T source,

            String summary,

            List<String> reasons

    ) {

        Objects.requireNonNull(
                sourceType
        );

        return new LifeDomainEvidenceReference<>(
                sourceType,
                sourceType.tier(),
                sourceKey,
                Objects.requireNonNull(
                        source,
                        "LifeDomain Evidence upstream source가 필요합니다."
                ),
                summary,
                reasons
        );
    }
}