package com.example.forum.repository;

import com.example.forum.entity.Message;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
  Page<Message> findByTopicId(UUID topicId, Pageable pageable);
}
