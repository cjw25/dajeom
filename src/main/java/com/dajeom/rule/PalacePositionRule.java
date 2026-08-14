package com.dajeom.rule;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 12차-1.
 *
 * 궁위 위치 기준 Rule.
 *
 * =================================================
 *
 * 책임:
 *
 * 1.
 * PalacePosition
 * -> PalacePositionContextType
 *
 * 2.
 * 기존 EarthlyBranchPosition
 * <-> PalacePosition 변환
 *
 * =================================================
 *
 * 왜 EarthlyBranchPosition을 그대로
 * 궁위 타입으로 사용하지 않는가?
 *
 * 12차 궁위는 다음 단계부터:
 *
 * Palace
 * ├─ 천간
 * └─ 지지
 *
 * 전체 기둥을 다룬다.
 *
 * EarthlyBranchPosition은 이름 그대로
 * 지지 관계 분석을 위해 만든 타입이므로,
 * 궁위 Domain까지 그 타입에 종속시키지 않는다.
 *
 * 대신 필요한 Boundary에서만 변환한다.
 */
@Component
public class PalacePositionRule {

    /**
     * =================================================
     * 궁위 -> 위치 Context
     * =================================================
     */
    public PalacePositionContextType contextOf(
            PalacePosition position
    ) {

        Objects.requireNonNull(
                position,
                "궁위 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    PalacePositionContextType
                            .BACKGROUND_FOUNDATION;

            case MONTH ->
                    PalacePositionContextType
                            .ENVIRONMENTAL_OPERATION;

            case DAY ->
                    PalacePositionContextType
                            .CORE_LIFE_AXIS;

            case HOUR ->
                    PalacePositionContextType
                            .EXTENSION_LATER_DEVELOPMENT;
        };
    }

    /**
     * =================================================
     * EarthlyBranchPosition -> PalacePosition
     * =================================================
     *
     * 8차 관계분석과 11차 12운성에서 사용하는
     * 기존 지지 위치 정보를
     * 12차 궁위와 연결할 때 사용한다.
     */
    public PalacePosition fromEarthlyBranchPosition(
            EarthlyBranchPosition position
    ) {

        Objects.requireNonNull(
                position,
                "지지 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    PalacePosition.YEAR;

            case MONTH ->
                    PalacePosition.MONTH;

            case DAY ->
                    PalacePosition.DAY;

            case HOUR ->
                    PalacePosition.HOUR;
        };
    }

    /**
     * =================================================
     * PalacePosition -> EarthlyBranchPosition
     * =================================================
     *
     * 궁위에서 지지 관계·12운성 Evidence를
     * 다시 조회해야 할 때 사용한다.
     */
    public EarthlyBranchPosition toEarthlyBranchPosition(
            PalacePosition position
    ) {

        Objects.requireNonNull(
                position,
                "궁위 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    EarthlyBranchPosition.YEAR;

            case MONTH ->
                    EarthlyBranchPosition.MONTH;

            case DAY ->
                    EarthlyBranchPosition.DAY;

            case HOUR ->
                    EarthlyBranchPosition.HOUR;
        };
    }

    /**
     * =================================================
     * 표시 순서
     * =================================================
     *
     * YEAR -> MONTH -> DAY -> HOUR
     *
     * 중요도 순위가 아니다.
     */
    public int orderOf(
            PalacePosition position
    ) {

        Objects.requireNonNull(
                position,
                "궁위 위치가 필요합니다."
        );

        return position.order();
    }
}