package com.dajeom.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum TwelveShinsalGroup {

    HAE_MYO_MI(
            "亥卯未",
            EarthlyBranch.SHEN,
            EarthlyBranch.HAI,
            EarthlyBranch.MAO,
            EarthlyBranch.WEI
    ),

    IN_O_SUL(
            "寅午戌",
            EarthlyBranch.HAI,
            EarthlyBranch.YIN,
            EarthlyBranch.WU,
            EarthlyBranch.XU
    ),

    SA_YU_CHUK(
            "巳酉丑",
            EarthlyBranch.YIN,
            EarthlyBranch.SI,
            EarthlyBranch.YOU,
            EarthlyBranch.CHOU
    ),

    SIN_JA_JIN(
            "申子辰",
            EarthlyBranch.SI,
            EarthlyBranch.SHEN,
            EarthlyBranch.ZI,
            EarthlyBranch.CHEN
    );

    private final String hanja;
    private final EarthlyBranch gyeopSalStartBranch;
    private final List<EarthlyBranch> members;

    TwelveShinsalGroup(
            String hanja,
            EarthlyBranch gyeopSalStartBranch,
            EarthlyBranch... members
    ) {

        this.hanja =
                Objects.requireNonNull(
                        hanja
                );

        this.gyeopSalStartBranch =
                Objects.requireNonNull(
                        gyeopSalStartBranch
                );

        this.members =
                List.copyOf(
                        Arrays.asList(
                                members
                        )
                );
    }

    public String hanja() {
        return hanja;
    }

    public EarthlyBranch gyeopSalStartBranch() {
        return gyeopSalStartBranch;
    }

    public List<EarthlyBranch> members() {
        return members;
    }

    public boolean contains(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch
        );

        return members.contains(
                branch
        );
    }
}