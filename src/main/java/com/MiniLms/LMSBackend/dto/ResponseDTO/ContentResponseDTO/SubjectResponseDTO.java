package com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO;

import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
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
public class SubjectResponseDTO extends CommonContentResponseDTO{
    private String description;
    private Set<String> topicIds;

    public static SubjectResponseDTO fromEntity(SubjectModel subject) {
        return SubjectResponseDTO.builder()
            .id(subject.getId())
            .name(subject.getName())
            .description(subject.getDescription())
            .topicIds(subject.getTopicsIds())
            .build();
    }
}
