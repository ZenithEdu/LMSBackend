package com.MiniLms.LMSBackend.repository.SecurityRepository;

import com.MiniLms.LMSBackend.model.SecurityModels.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface IRefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteAllByUserId(String userId);
}
