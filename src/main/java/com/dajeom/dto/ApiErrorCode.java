package com.dajeom.dto;

/**
 * API 오류의 공식 Machine-Readable Code.
 *
 * =================================================
 * 원칙
 * =================================================
 *
 * 프론트에서는:
 *
 * - message
 * - details
 * - HTTP reason phrase
 *
 * 문자열을 분석해서 오류 종류를 추측하지 않는다.
 *
 * errorCode만 공식 분기값으로 사용한다.
 *
 * =================================================
 * HTTP Status와의 관계
 * =================================================
 *
 * HTTP Status
 *
 * =
 * 프로토콜 수준 오류 분류
 *
 * ApiErrorCode
 *
 * =
 * DAJEOM API 내부의 안정적인 오류 계약
 */
public enum ApiErrorCode {

    /**
     * 존재하지 않는 API/정적 리소스.
     */
    RESOURCE_NOT_FOUND,

    /**
     * @Valid Bean Validation 실패.
     */
    REQUEST_VALIDATION_FAILED,

    /**
     * JSON 문법 / enum / 날짜 / 시간 등
     * Request Body 역직렬화 실패.
     */
    REQUEST_BODY_INVALID,

    /**
     * JSON 자체는 읽혔지만
     * 필드 조합이 Domain 입력 계약에 맞지 않음.
     */
    INVALID_REQUEST,

    /**
     * 절입 경계 등에서 정확한 계산을 위해
     * 출생시간이 필수인 경우.
     */
    BIRTH_TIME_REQUIRED,

    /**
     * 존재하지 않는 음력 날짜 / 윤달 조합.
     */
    INVALID_LUNAR_DATE,

    /**
     * 사용자 입력 문제가 아니라
     * Java deterministic 내부 계약 위반.
     */
    DETERMINISTIC_CONTRACT_VIOLATION,

    /**
     * 그 밖의 예상하지 못한 서버 오류.
     */
    INTERNAL_SERVER_ERROR
}