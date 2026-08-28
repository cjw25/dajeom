package com.dajeom.dto;

import com.dajeom.entity.Gender;
import com.dajeom.type.CalendarType;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationRelationshipStatus;
import com.dajeom.type.ConsultationRelationshipTarget;
import com.dajeom.type.ConsultationWorkStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주 분석 API 입력값.
 *
 * birthDate는 양력/음력을 모두 표현할 수 있도록
 * 문자열 yyyy-MM-dd 형식으로 받는다.
 *
 * 실제 날짜 검증은 달력 종류에 따라
 * SajuAnalysisService에서 수행한다.
 *
 * =================================================
 *
 * 정밀화 16차 운세 기준시점 입력 경계
 *
 * luckReferenceDate:
 *
 * 대운 / 세운 / 월운을 하나의 실제 시간축에서
 * 통합 선택하기 위한 기준 민간력 날짜.
 *
 * null:
 * -> 통합 운세 기준날짜 미지정.
 *
 * =================================================
 *
 * luckReferenceTime:
 *
 * 기준날짜 안의 정확한 시각.
 *
 * null:
 * -> 날짜만 알고 있는 상태.
 *
 * 이 경우 정밀화 16차에서는
 * LuckReferenceContext.dateOnly(...)를 사용해
 * 해당 민간력 날짜 전체의 가능범위를 유지한다.
 *
 * 값 존재:
 * -> LuckReferenceContext.exact(...)를 사용한다.
 *
 * =================================================
 *
 * 중요:
 *
 * targetYear와 luckReferenceDate는
 * 동일 개념이 아니다.
 *
 * targetYear:
 * -> 기존 연도 단위 분석 요청 기준.
 *
 * luckReferenceDate / luckReferenceTime:
 * -> 16차 대운·세운·월운 통합 Timeline 기준점.
 *
 * 따라서:
 *
 * luckReferenceDate.getYear() == targetYear
 *
 * 같은 검증을 하지 않는다.
 *
 * 절입 경계에서는 민간력 연도와 명리연도 경계가
 * 단순 일치하지 않을 수 있기 때문이다.
 *
 * =================================================
 *
 * 정밀화 19차 상담 입력 경계
 *
 * consultationCategories:
 *
 * 사용자가 명시적으로 선택한 상담 분야.
 *
 * 빈 목록:
 * -> 별도 분야 선택 없음.
 *
 * consultationQuestion:
 *
 * 사용자가 자유롭게 입력한 고민/질문.
 *
 * null / blank:
 * -> 별도 질문 없음.
 *
 * =================================================
 *
 * 요청 의미
 *
 * 1.
 * Category 없음
 * +
 * Question 없음
 *
 * -> 종합운 요청
 * -> 11개 ConsultationCategory 전체
 *
 * 2.
 * Category 있음
 *
 * -> 명시적으로 선택한 분야 중심 요청
 *
 * 3.
 * Question 있음
 * +
 * Category 없음
 *
 * -> 자유 질문은 존재하지만
 *    아직 ConsultationCategory 분류가 확정되지 않은 요청
 *
 * 이 DTO에서 자유 질문의 의미를 추측하거나
 * 키워드로 명리 분야를 강제 결정하지 않는다.
 *
 * =================================================
 * 122 API Raw Request Boundary
 * =================================================
 *
 * consultationCategories는
 * constructor 내부에서:
 *
 * - null 제거 금지
 * - 중복 제거
 * - 공식 order 정렬
 *
 * 을 수행한다.
 *
 * 그러나 @Size Bean Validation은
 * Record 생성 이후의 정규화된 값을 검사하므로,
 *
 * 예:
 *
 * 12개 입력
 * +
 * 중복 1개
 *
 * 요청이 11개로 정규화된 뒤
 * @Size(max=11)를 통과할 수 있다.
 *
 * 따라서 원본 List 길이는
 * normalizeCategories(...) 호출 전에
 * 별도로 검사한다.
 */
public record SajuAnalysisRequest(

        @NotNull(
                message = "양력/음력 구분은 필수입니다."
        )
        CalendarType calendarType,

        @NotBlank(
                message = "생년월일은 필수입니다."
        )
        @Pattern(
                regexp = "^\\d{4}-\\d{2}-\\d{2}$",
                message = "생년월일은 yyyy-MM-dd 형식이어야 합니다."
        )
        String birthDate,

        LocalTime birthTime,

        boolean birthTimeUnknown,

        @NotNull(
                message = "성별은 필수입니다."
        )
        Gender gender,

        /**
         * calendarType = LUNAR일 때:
         *
         * false = 평달
         * true = 윤달
         *
         * SOLAR에서는 반드시 false.
         */
        boolean leapMonth,

        @NotNull(
                message = "분석 대상 연도는 필수입니다."
        )
        Integer targetYear,

        /**
         * =================================================
         * 정밀화 16차 운세 기준날짜
         * =================================================
         *
         * 대운 / 세운 / 월운 후보를
         * 하나의 실제 시간축에서 선택하기 위한 기준날짜.
         *
         * null:
         * -> 기준날짜 미지정.
         *
         * 값 존재 + luckReferenceTime 없음:
         * -> DATE_ONLY 정밀도.
         *
         * 값 존재 + luckReferenceTime 존재:
         * -> EXACT 정밀도.
         *
         * targetYear와 강제로 동일 연도로 맞추지 않는다.
         */
        LocalDate luckReferenceDate,

        /**
         * =================================================
         * 정밀화 16차 운세 기준시각
         * =================================================
         *
         * luckReferenceDate 안의 정확한 민간력 시각.
         *
         * null:
         * -> 날짜만 알고 있는 상태.
         *
         * 값 존재:
         * -> exact reference.
         *
         * 기준날짜 없이 시간만 주는 요청은 허용하지 않는다.
         */
        LocalTime luckReferenceTime,

        /**
         * 사용자가 명시적으로 선택한 상담 분야.
         *
         * 미지정/null은 빈 목록으로 정규화한다.
         *
         * enum 자체의 order 기준으로 정렬하고
         * 중복 Category는 제거한다.
         *
         * 중요:
         *
         * 원본 요청 배열 자체도 최대 11개다.
         *
         * 중복 제거 후 11개 이하가 되더라도
         * 원본 배열이 11개를 초과하면 요청을 거부한다.
         */
        @Size(
                max = 11,
                message = "상담 분야는 최대 11개까지 선택할 수 있습니다."
        )
        List<ConsultationCategory> consultationCategories,

        /**
         * 자유형 상담 질문/고민.
         *
         * 사용자 입력 원문이며
         * 명리 계산 Rule에 직접 사용하지 않는다.
         */
        @Size(
                max = 1000,
                message = "상담 질문은 1000자 이하로 입력해 주세요."
        )
        String consultationQuestion,

        /**
         * 상담 전 관계 대상 Context.
         *
         * 명리 계산에는 사용하지 않고
         * 모든 deterministic Judgment가 확정된 뒤
         * 상담 표현의 호칭/상황 적합성에만 사용한다.
         */
        ConsultationRelationshipTarget relationshipTarget,

        /**
         * 상담 전 현재 관계 상태 Context.
         *
         * 사주 결과를 변경하는 Evidence가 아니다.
         */
        ConsultationRelationshipStatus relationshipStatus,

        /**
         * 상담 전 현재 직업/일 상태 Context.
         *
         * 사주 결과를 변경하는 Evidence가 아니다.
         */
        ConsultationWorkStatus workStatus

) {

        public SajuAnalysisRequest {

                /*
                 * =================================================
                 * 정밀화 16차 기준시점 정합성
                 * =================================================
                 *
                 * 시간만 단독으로 주어지면
                 * 어떤 민간력 날짜인지 알 수 없으므로 허용하지 않는다.
                 */

                if (
                        luckReferenceDate == null
                                &&
                                luckReferenceTime != null
                ) {

                        throw new IllegalArgumentException(
                                "운세 기준시각을 지정하려면 luckReferenceDate도 함께 지정해야 합니다."
                        );
                }

                /*
                 * =================================================
                 * 122 Raw Consultation Category Count Guard
                 * =================================================
                 *
                 * 반드시 normalizeCategories(...)보다 먼저 검사한다.
                 *
                 * Bean Validation은 Record 생성 후
                 * 정규화된 consultationCategories를 보게 되므로
                 * 중복으로 원본 최대 개수를 우회하는 것을 여기서 막는다.
                 */

                validateRawCategoryCount(
                        consultationCategories
                );

                consultationCategories =
                        normalizeCategories(
                                consultationCategories
                        );

                consultationQuestion =
                        normalizeQuestion(
                                consultationQuestion
                        );

                relationshipTarget =
                        relationshipTarget == null
                                ? ConsultationRelationshipTarget.UNSPECIFIED
                                : relationshipTarget;

                relationshipStatus =
                        relationshipStatus == null
                                ? ConsultationRelationshipStatus.UNSPECIFIED
                                : relationshipStatus;

                workStatus =
                        workStatus == null
                                ? ConsultationWorkStatus.UNSPECIFIED
                                : workStatus;
        }

        /*
         * =================================================
         * 정밀화 16차 기준시점 Convenience
         * =================================================
         */

        /**
         * 통합 운세 기준날짜가 존재하는지.
         */
        public boolean hasLuckReferenceDate() {

                return luckReferenceDate
                        != null;
        }

        /**
         * 정확한 기준시각까지 존재하는지.
         */
        public boolean hasLuckReferenceTime() {

                return luckReferenceTime
                        != null;
        }

        /**
         * 날짜와 시각이 모두 존재하는
         * EXACT 기준 요청.
         */
        public boolean exactLuckReferenceRequested() {

                return hasLuckReferenceDate()
                        &&
                        hasLuckReferenceTime();
        }

        /**
         * 날짜만 존재하는
         * DATE_ONLY 기준 요청.
         */
        public boolean dateOnlyLuckReferenceRequested() {

                return hasLuckReferenceDate()
                        &&
                        !hasLuckReferenceTime();
        }

        /**
         * 정밀화 16차 기준점이 아예 없는 상태.
         */
        public boolean luckReferenceUnspecified() {

                return !hasLuckReferenceDate();
        }

        public Optional<LocalDate>
        luckReferenceDateOptional() {

                return Optional.ofNullable(
                        luckReferenceDate
                );
        }

        public Optional<LocalTime>
        luckReferenceTimeOptional() {

                return Optional.ofNullable(
                        luckReferenceTime
                );
        }

        /*
         * =================================================
         * 상담 요청 Convenience
         * =================================================
         */

        public boolean hasSelectedConsultationCategories() {

                return !consultationCategories
                        .isEmpty();
        }

        public boolean hasConsultationQuestion() {

                return consultationQuestion
                        != null;
        }

        public Optional<String>
        consultationQuestionOptional() {

                return Optional.ofNullable(
                        consultationQuestion
                );
        }

        /**
         * 아무 상담 분야도 선택하지 않았고
         * 자유 질문도 없으면 종합운 요청이다.
         */
        public boolean comprehensiveConsultationRequested() {

                return !hasSelectedConsultationCategories()
                        &&
                        !hasConsultationQuestion();
        }

        /**
         * 자유 질문은 있지만
         * 사용자가 상담 분야를 명시하지 않은 상태.
         *
         * 이 경우 DTO 단계에서
         * 임의 Category를 선택하지 않는다.
         */
        public boolean freeFormQuestionRequiresCategoryResolution() {

                return hasConsultationQuestion()
                        &&
                        !hasSelectedConsultationCategories();
        }

        /*
         * =================================================
         * Raw Request Validation
         * =================================================
         */

        private static void validateRawCategoryCount(
                List<ConsultationCategory> source
        ) {

                if (
                        source != null
                                &&
                                source.size() > 11
                ) {

                        throw new IllegalArgumentException(
                                "상담 분야는 최대 11개까지 선택할 수 있습니다."
                        );
                }
        }

        /*
         * =================================================
         * Normalize
         * =================================================
         */

        private static List<ConsultationCategory>
        normalizeCategories(
                List<ConsultationCategory> source
        ) {

                if (
                        source == null
                                ||
                                source.isEmpty()
                ) {

                        return List.of();
                }

                return source
                        .stream()
                        .map(
                                category ->
                                        Objects.requireNonNull(
                                                category,
                                                "상담 분야 목록에는 null을 넣을 수 없습니다."
                                        )
                        )
                        .distinct()
                        .sorted(
                                Comparator.comparingInt(
                                        ConsultationCategory
                                                ::getOrder
                                )
                        )
                        .toList();
        }

        private static String normalizeQuestion(
                String source
        ) {

                if (
                        source == null
                ) {

                        return null;
                }

                String normalized =
                        source.trim();

                return normalized.isBlank()
                        ? null
                        : normalized;
        }
}