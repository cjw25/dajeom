package com.dajeom.calculation;

import com.dajeom.domain.DaewoonPillar;
import com.dajeom.domain.LifePeriodKey;
import com.dajeom.domain.LifePeriodType;
import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.PeriodMyeongriFacts;
import com.dajeom.domain.TenGod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 대운 간지를 PeriodMyeongriFacts로 변환한다.
 *
 * =================================================
 *
 * 현재 단계의 책임:
 *
 * - 대운 천간 십성 계산
 * - 대운 지지 정기 십성 계산
 * - 5대 십성 Family의 기간 활성화 판정
 *
 * =================================================
 *
 * 아직 여기서 확정하지 않는 것:
 *
 * - resourceWeakened
 * - natalStructureDisruptionActivated
 * - natalStructureDisruptionResolved
 * - activeBindingEstablished
 * - natalDirectionReinforced
 * - natalDirectionChallenged
 *
 * 위 항목들은 단순한 운 간지 존재나 raw 合/沖만으로
 * true로 만들 수 없으므로 전부 false를 유지한다.
 *
 * =================================================
 *
 * PeriodMyeongriFactsAdapter 같은 새 추상화는 추가하지 않는다.
 * 실제 대운 계산선에 필요한 구체 Calculator만 둔다.
 */
@Component
public class DaewoonPeriodMyeongriFactsCalculator {

    private final TenGodCalculator tenGodCalculator;
    private final HiddenStemCalculator hiddenStemCalculator;

    public DaewoonPeriodMyeongriFactsCalculator(
            TenGodCalculator tenGodCalculator,
            HiddenStemCalculator hiddenStemCalculator
    ) {
        this.tenGodCalculator =
                Objects.requireNonNull(
                        tenGodCalculator,
                        "TenGodCalculator가 필요합니다."
                );

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );
    }

    public List<PeriodMyeongriFacts> calculate(
            NatalMyeongriProfile natalProfile,
            List<DaewoonPillar> daewoonPillars,
            List<LifePeriodKey> periodKeys
    ) {
        Objects.requireNonNull(
                natalProfile,
                "NatalMyeongriProfile이 필요합니다."
        );

        Objects.requireNonNull(
                daewoonPillars,
                "대운 간지 목록이 필요합니다."
        );

        Objects.requireNonNull(
                periodKeys,
                "대운 LifePeriodKey 목록이 필요합니다."
        );

        if (
                daewoonPillars.size()
                        != periodKeys.size()
        ) {
            throw new IllegalArgumentException(
                    "대운 간지와 기간 Key 개수가 일치하지 않습니다. pillars="
                            + daewoonPillars.size()
                            + ", keys="
                            + periodKeys.size()
            );
        }

        if (daewoonPillars.isEmpty()) {
            return List.of();
        }

        List<PeriodMyeongriFacts> result =
                new ArrayList<>();

        for (
                int index = 0;
                index < daewoonPillars.size();
                index++
        ) {
            DaewoonPillar pillar =
                    Objects.requireNonNull(
                            daewoonPillars.get(index),
                            "대운 간지 목록에는 null이 들어갈 수 없습니다."
                    );

            LifePeriodKey period =
                    Objects.requireNonNull(
                            periodKeys.get(index),
                            "대운 기간 Key 목록에는 null이 들어갈 수 없습니다."
                    );

            validatePair(
                    pillar,
                    period
            );

            TenGod stemTenGod =
                    tenGodCalculator.calculate(
                            natalProfile.dayMaster(),
                            pillar.stem()
                    );

            TenGod branchMainTenGod =
                    hiddenStemCalculator
                            .calculateMainTenGod(
                                    natalProfile.dayMaster(),
                                    pillar.branch()
                            );

            TenGodFamily stemFamily =
                    familyOf(
                            stemTenGod
                    );

            TenGodFamily branchFamily =
                    familyOf(
                            branchMainTenGod
                    );

            boolean resourceActivated =
                    activated(
                            TenGodFamily.RESOURCE,
                            stemFamily,
                            branchFamily
                    );

            boolean outputActivated =
                    activated(
                            TenGodFamily.OUTPUT,
                            stemFamily,
                            branchFamily
                    );

            boolean wealthActivated =
                    activated(
                            TenGodFamily.WEALTH,
                            stemFamily,
                            branchFamily
                    );

            boolean authorityActivated =
                    activated(
                            TenGodFamily.AUTHORITY,
                            stemFamily,
                            branchFamily
                    );

            boolean companionActivated =
                    activated(
                            TenGodFamily.COMPANION,
                            stemFamily,
                            branchFamily
                    );

            result.add(
                    new PeriodMyeongriFacts(
                            period,
                            resourceActivated,

                            /*
                             * 財 유입 자체를 財破印으로 승격하지 않는다.
                             */
                            false,

                            outputActivated,
                            wealthActivated,
                            authorityActivated,
                            companionActivated,

                            /*
                             * raw 沖刑破害 != 원국 구조 훼손 확정
                             */
                            false,

                            /*
                             * raw 合/會 != 훼손 해소 확정
                             */
                            false,

                            /*
                             * raw 天干合 != active binding 확정
                             */
                            false,

                            /*
                             * 원국 방향 강화/도전은
                             * 별도 기간 구조 Rule 전까지 미확정.
                             */
                            false,
                            false
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private void validatePair(
            DaewoonPillar pillar,
            LifePeriodKey period
    ) {
        if (
                period.type()
                        != LifePeriodType.DAEWOON
        ) {
            throw new IllegalArgumentException(
                    "대운 Facts에는 DAEWOON LifePeriodKey만 사용할 수 있습니다. type="
                            + period.type()
            );
        }

        String expectedKey =
                "DAEWOON-"
                        + pillar.order();

        if (
                !expectedKey.equals(
                        period.key()
                )
        ) {
            throw new IllegalArgumentException(
                    "대운 간지 순번과 LifePeriodKey가 일치하지 않습니다. expected="
                            + expectedKey
                            + ", actual="
                            + period.key()
            );
        }
    }

    private boolean activated(
            TenGodFamily target,
            TenGodFamily stemFamily,
            TenGodFamily branchFamily
    ) {
        return stemFamily == target
                ||
                branchFamily == target;
    }

    private TenGodFamily familyOf(
            TenGod tenGod
    ) {
        Objects.requireNonNull(
                tenGod,
                "십성은 필수입니다."
        );

        String value =
                String.valueOf(
                                tenGod
                        )
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
                        .replace(" ", "")
                        .replace("-", "_");

        return switch (value) {

            case "BI_GYEON",
                 "COMPANION",
                 "비견",
                 "GEOP_JAE",
                 "ROB_WEALTH",
                 "겁재" ->
                    TenGodFamily.COMPANION;

            case "SIK_SIN",
                 "EATING_GOD",
                 "식신",
                 "SANG_GWAN",
                 "HURTING_OFFICER",
                 "상관" ->
                    TenGodFamily.OUTPUT;

            case "PYEON_JAE",
                 "INDIRECT_WEALTH",
                 "편재",
                 "JEONG_JAE",
                 "DIRECT_WEALTH",
                 "정재" ->
                    TenGodFamily.WEALTH;

            case "PYEON_GWAN",
                 "SEVEN_KILLINGS",
                 "칠살",
                 "편관",
                 "JEONG_GWAN",
                 "DIRECT_OFFICER",
                 "정관" ->
                    TenGodFamily.AUTHORITY;

            case "PYEON_IN",
                 "INDIRECT_RESOURCE",
                 "편인",
                 "JEONG_IN",
                 "DIRECT_RESOURCE",
                 "정인" ->
                    TenGodFamily.RESOURCE;

            default ->
                    throw new IllegalStateException(
                            "지원하지 않는 TenGod 표기입니다: "
                                    + tenGod
                    );
        };
    }

    private enum TenGodFamily {
        COMPANION,
        OUTPUT,
        WEALTH,
        AUTHORITY,
        RESOURCE
    }
}
