package com.dajeom.service;

import com.dajeom.dto.SajuProfileRequest;
import com.dajeom.dto.SajuProfileResponse;
import com.dajeom.entity.BirthProfile;
import com.dajeom.entity.CalendarType;
import com.dajeom.repository.BirthProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class SajuService {

    private final BirthProfileRepository birthProfileRepository;

    @Transactional
    public SajuProfileResponse createProfile(
            SajuProfileRequest request
    ) {
        validateBirthTime(request);

        LocalTime normalizedBirthTime =
                request.birthTimeUnknown()
                        ? null
                        : request.birthTime();

        boolean normalizedLunarLeapMonth =
                request.calendarType() == CalendarType.LUNAR
                        && request.lunarLeapMonth();

        BirthProfile birthProfile =
                BirthProfile.create(
                        request.name().trim(),
                        request.gender(),
                        request.calendarType(),
                        normalizedLunarLeapMonth,
                        request.birthDate(),
                        normalizedBirthTime,
                        request.birthTimeUnknown()
                );

        BirthProfile savedProfile =
                birthProfileRepository.save(birthProfile);

        return convertToResponse(savedProfile);
    }

    @Transactional(readOnly = true)
    public SajuProfileResponse getProfile(
            Long profileId
    ) {
        BirthProfile birthProfile =
                birthProfileRepository.findById(profileId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "사주 기본정보를 찾을 수 없습니다."
                                )
                        );

        return convertToResponse(birthProfile);
    }

    private void validateBirthTime(
            SajuProfileRequest request
    ) {
        if (
                !request.birthTimeUnknown()
                        && request.birthTime() == null
        ) {
            throw new IllegalArgumentException(
                    "출생시간을 입력하거나 시간 모름을 선택해주세요."
            );
        }

        if (
                request.birthTimeUnknown()
                        && request.birthTime() != null
        ) {
            throw new IllegalArgumentException(
                    "시간 모름을 선택한 경우 출생시간을 함께 입력할 수 없습니다."
            );
        }
    }

    private SajuProfileResponse convertToResponse(
            BirthProfile birthProfile
    ) {
        return new SajuProfileResponse(
                birthProfile.getId(),
                birthProfile.getName(),
                birthProfile.getGender(),
                birthProfile.getCalendarType(),
                birthProfile.isLunarLeapMonth(),
                birthProfile.getBirthDate(),
                birthProfile.getBirthTime(),
                birthProfile.isBirthTimeUnknown(),
                birthProfile.getCreatedAt(),
                "사주 기본정보가 저장되었습니다."
        );
    }
}