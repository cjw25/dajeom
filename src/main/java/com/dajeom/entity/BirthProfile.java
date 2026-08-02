package com.dajeom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "birth_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BirthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            length = 20
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "gender",
            nullable = false,
            length = 10
    )
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "calendar_type",
            nullable = false,
            length = 10
    )
    private CalendarType calendarType;

    @Column(
            name = "lunar_leap_month",
            nullable = false
    )
    private boolean lunarLeapMonth;

    @Column(
            name = "birth_date",
            nullable = false
    )
    private LocalDate birthDate;

    @Column(name = "birth_time")
    private LocalTime birthTime;

    @Column(
            name = "birth_time_unknown",
            nullable = false
    )
    private boolean birthTimeUnknown;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    private BirthProfile(
            String name,
            Gender gender,
            CalendarType calendarType,
            boolean lunarLeapMonth,
            LocalDate birthDate,
            LocalTime birthTime,
            boolean birthTimeUnknown
    ) {
        this.name = name;
        this.gender = gender;
        this.calendarType = calendarType;
        this.lunarLeapMonth = lunarLeapMonth;
        this.birthDate = birthDate;
        this.birthTime = birthTime;
        this.birthTimeUnknown = birthTimeUnknown;
    }

    public static BirthProfile create(
            String name,
            Gender gender,
            CalendarType calendarType,
            boolean lunarLeapMonth,
            LocalDate birthDate,
            LocalTime birthTime,
            boolean birthTimeUnknown
    ) {
        return new BirthProfile(
                name,
                gender,
                calendarType,
                lunarLeapMonth,
                birthDate,
                birthTime,
                birthTimeUnknown
        );
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}