package com.project.main.authToken;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {

    @Query(value = """
      select t from AuthToken t 
      inner join t.userAccount u 
      where u.userId = :id and t.expired = false and t.revoked = false
      """)
    List<AuthToken> findAllValidTokenByUser(@Param("id") Long id);

    Optional<AuthToken> findByToken(String token);
}
