package com.example.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.forum.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

}