package com.MiniLms.LMSBackend.model.SecurityModels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id;

    @NonNull
    @NotBlank
    @Indexed(unique = true)
    private String token;

    @NonNull
    @NotBlank
    @Indexed
    private String userId;  // Reference to User document ID

    @NonNull
    @NotNull
    private Date expiryDate;

    private boolean revoked = false;
}
