package com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO;


import com.MiniLms.LMSBackend.model.ContentModels.Resource;
import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TopicResponseDTO extends CommonTopicResponseDTO{
    private String subjectId;
    public static TopicResponseDTO fromEntity(TopicModel topicModel) {
        return convertToDto(topicModel);
    }
    private static TopicResponseDTO convertToDto(TopicModel topic) {
        return TopicResponseDTO.builder()
            .id(topic.getId())
            .name(topic.getName())
            .subjectId(topic.getSubjectId())
            .resourceResponseDTO(convertResourceToDto(topic.getResource()))
            .build();
    }
    private static ResourceResponseDTO convertResourceToDto(Resource resource) {
        return ResourceResponseDTO.builder()
            .test(resource.getTest())
            .video(resource.getVideo())
            .classPPTUrl(getFileUrl(resource.getClassPPT()))
            .exerciseUrl(getFileUrl(resource.getExercise()))
            .solutionUrl(getFileUrl(resource.getSolution()))
            .build();
    }
    private static String getFileUrl(ObjectId fileId) {
        return fileId != null ? "https://lmsbackend-3l0h.onrender.com/api/content/files/" + fileId.toString() : null;
    }
}
