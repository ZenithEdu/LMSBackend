package com.MiniLms.LMSBackend.model.ContentModels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;


import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="topic")
@CompoundIndex(def = "{'name': 1, 'subjectId': 1}", unique = true)
public class TopicModel extends CommonTopicModel{
    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    @Size(max = 50, message = "Maximum of 50 subtopic IDs allowed")
    private Set<@NotBlank(message = "Subtopic ID cannot be blank") String> subtopicIds;

}
