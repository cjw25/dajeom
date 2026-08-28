package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationSectionAssemblyViolationType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 하나의 Consultation Section 최종 조립 검증 결과.
 *
 * =================================================
 * 135-4
 * =================================================
 *
 * plannedPackets를 함께 보존한다.
 *
 * Assembly Entry의 packetId를 이용해
 * 해당 Packet의 공식:
 *
 * - timelineYear
 * - timelineStage
 *
 * 를 조회한다.
 *
 * 문자열에서 연도/Stage를 파싱하지 않는다.
 *
 * CURRENT_TIMELINE:
 *
 * 연도
 * -> Stage Header
 * -> 독립 Packet 문단
 *
 * PAST/FUTURE:
 *
 * 연도
 * -> 독립 Packet 문단
 *
 * 새로운 의미 접속사는 생성하지 않는다.
 */
public record ConsultationSectionAssemblyAnalysis(

        ConsultationCategory category,

        List<ConsultationSectionAssemblyEntry> entries,

        List<ConsultationMeaningPacket> plannedPackets,

        Map<ConsultationSectionAssemblyViolationType, List<String>> violations

) {

    /**
     * 기존 3인자 생성자 호환.
     */
    public ConsultationSectionAssemblyAnalysis(

            ConsultationCategory category,

            List<ConsultationSectionAssemblyEntry> entries,

            Map<ConsultationSectionAssemblyViolationType, List<String>> violations

    ) {

        this(
                category,
                entries,
                List.of(),
                violations
        );
    }

    public ConsultationSectionAssemblyAnalysis {

        Objects.requireNonNull(
                category,
                "Section Assembly Analysis category는 필수입니다."
        );

        entries =
                entries == null
                        ? List.of()
                        : entries
                        .stream()
                        .map(
                                entry ->
                                        Objects.requireNonNull(
                                                entry,
                                                "Section Assembly Entry는 null일 수 없습니다."
                                        )
                        )
                        .toList();

        plannedPackets =
                plannedPackets == null
                        ? List.of()
                        : plannedPackets
                        .stream()
                        .map(
                                packet ->
                                        Objects.requireNonNull(
                                                packet,
                                                "Section Assembly planned Packet은 null일 수 없습니다."
                                        )
                        )
                        .toList();

        violations =
                immutableViolations(
                        violations
                );
    }

    public boolean valid() {

        return violations
                .values()
                .stream()
                .allMatch(
                        List::isEmpty
                );
    }

    public boolean hasViolation(
            ConsultationSectionAssemblyViolationType type
    ) {

        if (
                type == null
        ) {

            return false;
        }

        return !violations
                .getOrDefault(
                        type,
                        List.of()
                )
                .isEmpty();
    }

    public List<String> violationPacketIds(
            ConsultationSectionAssemblyViolationType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return violations.getOrDefault(
                type,
                List.of()
        );
    }

    public List<String> violatingPacketIds() {

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (
                List<String> packetIds
                : violations.values()
        ) {

            result.addAll(
                    packetIds
            );
        }

        return List.copyOf(
                result
        );
    }

    public String storyText() {

        requireValid();

        if (
                entries.isEmpty()
        ) {

            return "";
        }

        /*
         * 기존 생성 경로 호환.
         *
         * plannedPackets가 없으면
         * 기존 단순 조립 방식을 유지한다.
         */
        if (
                plannedPackets.isEmpty()
        ) {

            return String.join(
                    "\n\n",
                    entries
                            .stream()
                            .map(
                                    ConsultationSectionAssemblyEntry::text
                            )
                            .toList()
            ).trim();
        }

        Map<String, ConsultationMeaningPacket> packetById =
                packetById();

        List<String> sectionBlocks =
                new ArrayList<>();

        Integer activeYear =
                null;

        ConsultationTimelineUserMeaningPolicy.TimelineStage activeStage =
                null;

        List<String> yearBlocks =
                new ArrayList<>();

        List<String> stageTexts =
                new ArrayList<>();

        for (
                ConsultationSectionAssemblyEntry entry
                : entries
        ) {

            ConsultationMeaningPacket packet =
                    packetById.get(
                            entry.packetId()
                    );

            if (
                    packet == null
            ) {

                throw new IllegalStateException(
                        "Section Assembly Entry에 대응하는 planned Packet이 없습니다. packetId="
                                + entry.packetId()
                );
            }

            Integer entryYear =
                    packet.timelineYear();

            ConsultationTimelineUserMeaningPolicy.TimelineStage entryStage =
                    packet.timelineStage();

            /*
             * =================================================
             * 일반 Packet
             * =================================================
             */
            if (
                    entryYear == null
            ) {

                flushStage(
                        yearBlocks,
                        activeStage,
                        stageTexts
                );

                activeStage =
                        null;

                stageTexts =
                        new ArrayList<>();

                flushYear(
                        sectionBlocks,
                        activeYear,
                        yearBlocks
                );

                activeYear =
                        null;

                yearBlocks =
                        new ArrayList<>();

                sectionBlocks.add(
                        entry.text()
                );

                continue;
            }

            /*
             * =================================================
             * Year 시작 / 변경
             * =================================================
             */
            if (
                    activeYear == null
            ) {

                activeYear =
                        entryYear;

            } else if (
                    !activeYear.equals(
                            entryYear
                    )
            ) {

                flushStage(
                        yearBlocks,
                        activeStage,
                        stageTexts
                );

                activeStage =
                        null;

                stageTexts =
                        new ArrayList<>();

                flushYear(
                        sectionBlocks,
                        activeYear,
                        yearBlocks
                );

                activeYear =
                        entryYear;

                yearBlocks =
                        new ArrayList<>();
            }

            /*
             * =================================================
             * Stage 없는 Timeline Year
             * =================================================
             *
             * PAST/FUTURE.
             */
            if (
                    entryStage == null
            ) {

                flushStage(
                        yearBlocks,
                        activeStage,
                        stageTexts
                );

                activeStage =
                        null;

                stageTexts =
                        new ArrayList<>();

                yearBlocks.add(
                        entry.text()
                );

                continue;
            }

            /*
             * =================================================
             * CURRENT Timeline Stage
             * =================================================
             */
            if (
                    activeStage == null
            ) {

                activeStage =
                        entryStage;

                stageTexts.add(
                        entry.text()
                );

                continue;
            }

            if (
                    activeStage == entryStage
            ) {

                stageTexts.add(
                        entry.text()
                );

                continue;
            }

            /*
             * Stage 변경.
             */
            flushStage(
                    yearBlocks,
                    activeStage,
                    stageTexts
            );

            activeStage =
                    entryStage;

            stageTexts =
                    new ArrayList<>();

            stageTexts.add(
                    entry.text()
            );
        }

        flushStage(
                yearBlocks,
                activeStage,
                stageTexts
        );

        flushYear(
                sectionBlocks,
                activeYear,
                yearBlocks
        );

        return String.join(
                "\n\n",
                sectionBlocks
        ).trim();
    }

    private Map<String, ConsultationMeaningPacket> packetById() {

        LinkedHashMap<String, ConsultationMeaningPacket> result =
                new LinkedHashMap<>();

        for (
                ConsultationMeaningPacket packet
                : plannedPackets
        ) {

            ConsultationMeaningPacket previous =
                    result.putIfAbsent(
                            packet.packetId(),
                            packet
                    );

            if (
                    previous != null
            ) {

                throw new IllegalStateException(
                        "Section Assembly planned Packet ID가 중복되었습니다: "
                                + packet.packetId()
                );
            }
        }

        return Map.copyOf(
                result
        );
    }

    /**
     * Stage는 공식 title()만 사용한다.
     *
     * 별도 사용자 의미 문장을 만들지 않는다.
     */
    private void flushStage(

            List<String> yearBlocks,

            ConsultationTimelineUserMeaningPolicy.TimelineStage stage,

            List<String> stageTexts

    ) {

        if (
                stage == null
                        ||
                        stageTexts == null
                        ||
                        stageTexts.isEmpty()
        ) {

            return;
        }

        String body =
                String.join(
                        "\n\n",
                        stageTexts
                ).trim();

        if (
                body.isBlank()
        ) {

            return;
        }

        yearBlocks.add(
                stage.title()
                        + "\n\n"
                        + body
        );
    }

    private void flushYear(

            List<String> sectionBlocks,

            Integer timelineYear,

            List<String> yearBlocks

    ) {

        if (
                timelineYear == null
                        ||
                        yearBlocks == null
                        ||
                        yearBlocks.isEmpty()
        ) {

            return;
        }

        String body =
                String.join(
                        "\n\n",
                        yearBlocks
                ).trim();

        if (
                body.isBlank()
        ) {

            return;
        }

        sectionBlocks.add(
                timelineYear
                        + "년"
                        + "\n\n"
                        + body
        );
    }

    public List<String> emphasis(
            int maxCount
    ) {

        requireValid();

        if (
                maxCount < 1
        ) {

            return List.of();
        }

        String storyText =
                storyText();

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (
                ConsultationSectionAssemblyEntry entry
                : entries
        ) {

            for (
                    String phrase
                    : entry.emphasis()
            ) {

                if (
                        result.size()
                                >= maxCount
                ) {

                    return List.copyOf(
                            result
                    );
                }

                if (
                        storyText.contains(
                                phrase
                        )
                ) {

                    result.add(
                            phrase
                    );
                }
            }
        }

        return List.copyOf(
                result
        );
    }

    public String summary() {

        if (
                valid()
        ) {

            return "";
        }

        List<String> parts =
                new ArrayList<>();

        for (
                ConsultationSectionAssemblyViolationType type
                : ConsultationSectionAssemblyViolationType.values()
        ) {

            List<String> packetIds =
                    violations.getOrDefault(
                            type,
                            List.of()
                    );

            if (
                    packetIds.isEmpty()
            ) {

                continue;
            }

            parts.add(
                    type.name()
                            + "="
                            + String.join(
                            "|",
                            packetIds
                    )
            );
        }

        return String.join(
                ", ",
                parts
        );
    }

    private void requireValid() {

        if (
                !valid()
        ) {

            throw new IllegalStateException(
                    "검증되지 않은 Section Assembly는 조립할 수 없습니다. category="
                            + category
                            + ", reason="
                            + summary()
            );
        }
    }

    private static Map<
            ConsultationSectionAssemblyViolationType,
            List<String>
            >
    immutableViolations(

            Map<
                    ConsultationSectionAssemblyViolationType,
                    List<String>
                    > source

    ) {

        EnumMap<
                ConsultationSectionAssemblyViolationType,
                List<String>
                > result =
                new EnumMap<>(
                        ConsultationSectionAssemblyViolationType.class
                );

        for (
                ConsultationSectionAssemblyViolationType type
                : ConsultationSectionAssemblyViolationType.values()
        ) {

            List<String> values =
                    source == null
                            ? List.of()
                            : source.getOrDefault(
                            type,
                            List.of()
                    );

            LinkedHashSet<String> normalized =
                    new LinkedHashSet<>();

            for (
                    String raw
                    : values
            ) {

                if (
                        raw == null
                ) {

                    continue;
                }

                String value =
                        raw.trim();

                if (
                        !value.isBlank()
                ) {

                    normalized.add(
                            value
                    );
                }
            }

            result.put(
                    type,
                    List.copyOf(
                            normalized
                    )
            );
        }

        return Map.copyOf(
                result
        );
    }
}