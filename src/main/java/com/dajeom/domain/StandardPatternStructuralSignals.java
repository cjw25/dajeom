package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * 일반 8격 Rule들이 필요로 하는
 * 구조 신호 묶음.
 *
 * =================================================
 *
 * 실제 선택된 격에 필요한 신호만
 * 존재해도 된다.
 *
 * 예:
 *
 * 正財格이면
 * wealth가 존재하면 충분하다.
 *
 * 正官 StructuralSignals까지
 * 억지로 생성할 필요가 없다.
 *
 * =================================================
 *
 * 이 객체 역시 명리 판정을 하지 않는다.
 *
 * 각 Adapter가 생성한 실제 구조 신호를
 * 통합 Calculator로 전달하는 역할만 한다.
 */
public record StandardPatternStructuralSignals(

        Optional<DirectOfficerStructuralSignals>
        directOfficer,

        Optional<WealthPatternStructuralSignals>
        wealth,

        Optional<ResourcePatternStructuralSignals>
        resource,

        Optional<EatingGodPatternStructuralSignals>
        eatingGod,

        Optional<HurtingOfficerPatternStructuralSignals>
        hurtingOfficer,

        Optional<SevenKillingsPatternStructuralSignals>
        sevenKillings

) {

    public StandardPatternStructuralSignals {

        directOfficer =
                directOfficer == null
                        ? Optional.empty()
                        : directOfficer;

        wealth =
                wealth == null
                        ? Optional.empty()
                        : wealth;

        resource =
                resource == null
                        ? Optional.empty()
                        : resource;

        eatingGod =
                eatingGod == null
                        ? Optional.empty()
                        : eatingGod;

        hurtingOfficer =
                hurtingOfficer == null
                        ? Optional.empty()
                        : hurtingOfficer;

        sevenKillings =
                sevenKillings == null
                        ? Optional.empty()
                        : sevenKillings;

        directOfficer.ifPresent(
                Objects::requireNonNull
        );

        wealth.ifPresent(
                Objects::requireNonNull
        );

        resource.ifPresent(
                Objects::requireNonNull
        );

        eatingGod.ifPresent(
                Objects::requireNonNull
        );

        hurtingOfficer.ifPresent(
                Objects::requireNonNull
        );

        sevenKillings.ifPresent(
                Objects::requireNonNull
        );
    }

    public static StandardPatternStructuralSignals empty() {

        return new StandardPatternStructuralSignals(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public DirectOfficerStructuralSignals
    requireDirectOfficer() {

        return directOfficer
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "正官格 판정에 필요한 DirectOfficerStructuralSignals가 없습니다."
                                )
                );
    }

    public WealthPatternStructuralSignals
    requireWealth() {

        return wealth
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "財格 판정에 필요한 WealthPatternStructuralSignals가 없습니다."
                                )
                );
    }

    public ResourcePatternStructuralSignals
    requireResource() {

        return resource
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "印格 판정에 필요한 ResourcePatternStructuralSignals가 없습니다."
                                )
                );
    }

    public EatingGodPatternStructuralSignals
    requireEatingGod() {

        return eatingGod
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "食神格 판정에 필요한 EatingGodPatternStructuralSignals가 없습니다."
                                )
                );
    }

    public HurtingOfficerPatternStructuralSignals
    requireHurtingOfficer() {

        return hurtingOfficer
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "傷官格 판정에 필요한 HurtingOfficerPatternStructuralSignals가 없습니다."
                                )
                );
    }

    public SevenKillingsPatternStructuralSignals
    requireSevenKillings() {

        return sevenKillings
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "七殺格 판정에 필요한 SevenKillingsPatternStructuralSignals가 없습니다."
                                )
                );
    }
}