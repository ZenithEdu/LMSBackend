package com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO;


import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TopicResponseDTO extends CommonTopicResponseDTO{
    private String subjectId;
    private Set<String> subtopicIds;

    public static TopicResponseDTO fromEntity(TopicModel topicModel) {
        return TopicResponseDTO.builder()
            .id(topicModel.getId())
            .name(topicModel.getName())
            .subjectId(topicModel.getSubjectId())
            .subtopicIds(topicModel.getSubtopicIds())
            .build();
    }

}
