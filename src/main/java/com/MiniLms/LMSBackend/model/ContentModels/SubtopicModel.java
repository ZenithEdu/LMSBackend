package com.MiniLms.LMSBackend.model.ContentModels;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="subtopic")
public class SubtopicModel extends CommonTopicModel{
    @NotBlank(message = "Subject ID is required")
    private String topicId;
}
