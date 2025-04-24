package com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchCreationResponseDTO {

    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String managerId;
    private Set<BatchSubjectResponse> subjects;
    private List<String> studentId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BatchSubjectResponse {
        private String subjectId;
        private List<SelectedTopicResponse> selectedTopics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectedTopicResponse {
        private String topicId;
        private LocalDate selectedDate;
        private List<SelectedSubtopicResponse> selectedSubtopics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectedSubtopicResponse {
        private String subtopicId;
        private LocalDate selectedDate;
    }
}
