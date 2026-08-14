package com.dajeom.type;

import java.util.List;

/**
 * 정밀화 10차-7.
 *
 * Family 단위 연속 생(生) 구조.
 *
 * =================================================
 *
 * 이번 단계에서는 핵심적으로
 *
 * 식상 -> 재성 -> 관살 -> 인성
 *
 * 흐름을 분석한다.
 *
 * =================================================
 *
 * 세부적으로:
 *
 * OUTPUT_WEALTH_OFFICER
 *
 * 식상생재
 * +
 * 재생관
 *
 * =================================================
 *
 * WEALTH_OFFICER_RESOURCE
 *
 * 재생관
 * +
 * 관생인
 *
 * =================================================
 *
 * OUTPUT_WEALTH_OFFICER_RESOURCE
 *
 * 식상
 * -> 재성
 * -> 관살
 * -> 인성
 *
 * 의 전체 연쇄.
 *
 * =================================================
 *
 * 이것만으로:
 *
 * 관인상생
 * 살인상생
 * 재생관격
 *
 * 등을 최종 확정하지 않는다.
 */
public enum TenGodFamilyChainType {

    OUTPUT_WEALTH_OFFICER(
            "식상→재성→관살",
            List.of(
                    DayMasterElementRole.OUTPUT,
                    DayMasterElementRole.WEALTH,
                    DayMasterElementRole.OFFICER
            )
    ),

    WEALTH_OFFICER_RESOURCE(
            "재성→관살→인성",
            List.of(
                    DayMasterElementRole.WEALTH,
                    DayMasterElementRole.OFFICER,
                    DayMasterElementRole.RESOURCE
            )
    ),

    OUTPUT_WEALTH_OFFICER_RESOURCE(
            "식상→재성→관살→인성",
            List.of(
                    DayMasterElementRole.OUTPUT,
                    DayMasterElementRole.WEALTH,
                    DayMasterElementRole.OFFICER,
                    DayMasterElementRole.RESOURCE
            )
    );

    private final String koreanName;

    private final List<DayMasterElementRole> roles;

    TenGodFamilyChainType(
            String koreanName,
            List<DayMasterElementRole> roles
    ) {

        this.koreanName =
                koreanName;

        this.roles =
                List.copyOf(
                        roles
                );
    }

    public String koreanName() {

        return koreanName;
    }

    public List<DayMasterElementRole> roles() {

        return roles;
    }

    public int roleCount() {

        return roles.size();
    }

    public int interactionCount() {

        return Math.max(
                0,
                roles.size() - 1
        );
    }

    public DayMasterElementRole firstRole() {

        return roles.get(0);
    }

    public DayMasterElementRole lastRole() {

        return roles.get(
                roles.size() - 1
        );
    }

    public boolean includes(
            DayMasterElementRole role
    ) {

        return role != null
                && roles.contains(
                role
        );
    }

    public boolean fullFourFamilyChain() {

        return this
                == OUTPUT_WEALTH_OFFICER_RESOURCE;
    }
}