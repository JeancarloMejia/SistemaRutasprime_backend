package com.backend.avance1.repository;

import com.backend.avance1.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<ContactMessage, Long> {

    @Query("SELECT MAX(c.id) FROM ContactMessage c")
    Long findLastId();
    ContactMessage findTopByEmailOrderByCreatedAtDesc(String email);
    Optional<ContactMessage> findByMessageCode(String messageCode);
}