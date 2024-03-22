package com.example.forum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.forum.entity.Message;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
}

