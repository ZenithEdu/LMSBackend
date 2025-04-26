package com.MiniLms.LMSBackend.model.BatchModels;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Document(collection = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchModel implements Serializable {

    @Id
    private String id;

    @NotBlank(message = "Batch name cannot be blank")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Manager ID is required")
    private String managerId;  // Refers to User document

    @Builder.Default
    private Set<BatchSubject> subjects = new HashSet<>();

    @Builder.Default
    private Set<String> studentId = new HashSet<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BatchSubject implements Serializable {

        @NotBlank(message = "Subject ID cannot be blank")
        private String subjectId;

        @Builder.Default
        @NotNull
        private Set<SelectedTopic> selectedTopics = new HashSet<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectedTopic implements Serializable {

        @NotBlank(message = "Topic ID cannot be blank")
        private String topicId;

        @NotNull(message = "Selected date is required for topic")
        private LocalDate selectedDate;

        @Builder.Default
        @NotNull
        private Set<SelectedSubtopic> selectedSubtopics = new HashSet<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectedSubtopic implements Serializable {

        @NotBlank(message = "Subtopic ID cannot be blank")
        private String subtopicId;

        @NotNull(message = "Selected date is required for subtopic")
        private LocalDate selectedDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SelectedSubtopic that = (SelectedSubtopic) o;
            return Objects.equals(subtopicId, that.subtopicId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(subtopicId);
        }
    }
}

