package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.entity.BirthProfile;
import com.dajeom.entity.SajuAnalysis;
import com.dajeom.entity.SajuAnalysisStatus;
import com.dajeom.repository.BirthProfileRepository;
import com.dajeom.repository.SajuAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SajuAnalysisService {

    private final BirthProfileRepository birthProfileRepository;
    private final SajuAnalysisRepository sajuAnalysisRepository;
    private final SajuCalculatorService sajuCalculatorService;

    @Transactional
    public SajuAnalysisResponse createAnalysis(
            SajuAnalysisRequest request
    ) {
        BirthProfile birthProfile =
                birthProfileRepository.findById(
                        request.profileId()
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "사주 기본정보를 찾을 수 없습니다."
                        )
                );

        SajuAnalysis analysis =
                SajuAnalysis.createCalculating(
                        birthProfile
                );

        SajuAnalysis savedAnalysis =
                sajuAnalysisRepository.saveAndFlush(
                        analysis
                );

        try {
            savedAnalysis.updateProgress(30);

            /*
             * 만세력 계산이 끝난 뒤 상태를 완료로 바꾸고
             * 전체 Response를 생성한다.
             */
            savedAnalysis.complete();

            return sajuCalculatorService.calculate(
                    savedAnalysis
            );

        } catch (IllegalArgumentException exception) {
            savedAnalysis.fail();

            throw exception;

        } catch (Exception exception) {
            savedAnalysis.fail();

            throw new IllegalStateException(
                    "사주 만세력 계산 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    @Transactional(readOnly = true)
    public SajuAnalysisResponse getAnalysis(
            Long analysisId
    ) {
        SajuAnalysis analysis =
                sajuAnalysisRepository.findById(
                        analysisId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "사주 분석 결과를 찾을 수 없습니다."
                        )
                );

        if (
                analysis.getStatus()
                        != SajuAnalysisStatus.COMPLETED
        ) {
            throw new IllegalStateException(
                    "사주 분석이 아직 완료되지 않았습니다."
            );
        }

        return sajuCalculatorService.calculate(
                analysis
        );
    }
}