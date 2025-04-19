package com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO;


import com.MiniLms.LMSBackend.model.ContentModels.SubtopicModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubtopicResponseDTO extends CommonTopicResponseDTO{
    private String topicId;

//    public static SubtopicResponseDTO fromEntity(SubtopicModel subtopicModel) {
//        return SubtopicResponseDTO.builder()
//            .id(subtopicModel.getId())
//            .name(subtopicModel.getName())
//            .resource(subtopicModel.getResource())
//            .topicId(subtopicModel.getTopicId())
//            .build();
//    }
}
