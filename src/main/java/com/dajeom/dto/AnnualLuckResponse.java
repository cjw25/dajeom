package com.dajeom.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;

/**
 * 연간 흐름 API 출력값.
 *
 * =================================================
 * 공개 API 계약
 * =================================================
 *
 * 프론트에 공개:
 *
 * - targetYear
 * - pillar
 * - startDateTime
 * - endDateTime
 *
 * 이 값들은 연간 흐름의:
 *
 * - 대상 연도
 * - 공식 간지
 * - 실제 명리 연도 시작/종료 경계
 *
 * 를 표현하기 위한 구조화 데이터다.
 *
 * =================================================
 * 내부 Evidence
 * =================================================
 *
 * 다음 값은 deterministic 내부 계산 근거다.
 *
 * - stemTenGod
 * - branchMainStemKoreanName
 * - branchMainStemChineseName
 * - branchMainTenGod
 *
 * Java 내부에서는 보존하지만
 * 공개 JSON에는 노출하지 않는다.
 *
 * 프론트가 십성/지장간 근거를 사용해서
 * 별도의 의미를 만들어서는 안 된다.
 */
public record AnnualLuckResponse(

        int targetYear,

        PillarResponse pillar,

        ZonedDateTime startDateTime,

        ZonedDateTime endDateTime,

        /**
         * 연간 천간의 십성.
         *
         * deterministic 내부 Evidence.
         */
        @JsonIgnore
        String stemTenGod,

        /**
         * 연간 지지 본기 천간.
         *
         * deterministic 내부 Evidence.
         */
        @JsonIgnore
        String branchMainStemKoreanName,

        /**
         * 연간 지지 본기 천간 한자.
         *
         * deterministic 내부 Evidence.
         */
        @JsonIgnore
        String branchMainStemChineseName,

        /**
         * 연간 지지 본기의 십성.
         *
         * deterministic 내부 Evidence.
         */
        @JsonIgnore
        String branchMainTenGod

) {
}