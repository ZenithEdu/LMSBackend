package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs;

import com.MiniLms.LMSBackend.model.ContentModels.SubtopicModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class SubtopicRequestDTO extends CommonTopicRequestDTO{
//    public SubtopicModel toEntity(String topicId) {
//        return SubtopicModel.builder()
//            .name(this.getName())
//            .resource(this.getResource())
//            .topicId(topicId)
//            .build();
//    }
}
