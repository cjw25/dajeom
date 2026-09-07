package com.dajeom.calculation;

import com.dajeom.domain.LifePeriodType;
import com.dajeom.domain.PeriodMyeongriFacts;

/**
 * 기존 대운 / 세운 / 월운 계산 결과를
 * PeriodMyeongriFacts로 변환하기 위한 Adapter 계약.
 *
 * =================================================
 *
 * 예:
 *
 * DaewoonAnalysis
 *      ↓
 * DaewoonMyeongriFactsAdapter
 *
 * SewoonAnalysis
 *      ↓
 * SewoonMyeongriFactsAdapter
 *
 * WolwoonAnalysis
 *      ↓
 * WolwoonMyeongriFactsAdapter
 *
 * =================================================
 *
 * 이 인터페이스 덕분에
 * 이후 LifeClaim 계층은
 *
 * DaewoonAnalysis
 * SewoonAnalysis
 * WolwoonAnalysis
 *
 * 같은 실제 계산 객체를 직접 알 필요가 없다.
 *
 * =================================================
 *
 * @param <T>
 * 기존 운 계산기가 반환하는 실제 분석 객체 타입.
 */
public interface PeriodMyeongriFactsAdapter<T> {

    /**
     * 이 Adapter가 담당하는 운 단위.
     */
    LifePeriodType periodType();

    /**
     * 기존 운 계산 결과를
     * 정규화된 PeriodMyeongriFacts로 변환한다.
     *
     * 여기서 중요한 것은:
     *
     * raw 천간/지지 존재를 그대로 true로 바꾸는 게 아니다.
     *
     * 기존 명리 Rule을 통과해서
     * 실제로 활성됐다고 판정된 구조만 true로 만들어야 한다.
     */
    PeriodMyeongriFacts adapt(
            T source
    );
}