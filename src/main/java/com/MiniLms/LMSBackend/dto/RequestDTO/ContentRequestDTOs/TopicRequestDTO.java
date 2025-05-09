package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs;


import com.MiniLms.LMSBackend.model.ContentModels.CommonTopicModel;
import com.MiniLms.LMSBackend.model.ContentModels.Resource;
import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;

@EqualsAndHashCode(callSuper = true)
@Data
public class TopicRequestDTO extends CommonTopicRequestDTO{
    public TopicModel toEntity(String subjectId, Resource resource) {
        return TopicModel.builder()
            .name(this.getName())
            .subjectId(subjectId)
            .resource(resource)
            .build();
    }
}
