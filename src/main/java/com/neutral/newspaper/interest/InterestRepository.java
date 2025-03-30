package com.neutral.newspaper.interest;

import com.neutral.newspaper.interest.domain.Interest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByName(String name);
}
