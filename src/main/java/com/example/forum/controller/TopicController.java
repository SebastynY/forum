package com.example.forum.controller;

import com.example.forum.dto.TopicDTO;
import com.example.forum.model.Topic;
import com.example.forum.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class TopicController {

  @Autowired
  private TopicService topicService;

  @PostMapping("/topic")
  public ResponseEntity<Topic> createTopic(@RequestBody TopicDTO topicDto) {
    Topic createdTopic = topicService.createTopic(topicDto);
    return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
  }
  @GetMapping("/topic")
  public ResponseEntity<List<Topic>> getAllTopics() {
    List<Topic> topics = topicService.getAllTopics();
    return ResponseEntity.ok(topics);
  }
  @PutMapping("/topic")
  public ResponseEntity<Topic> updateTopic(@RequestBody TopicDTO topicDto) {
    Topic updatedTopic = topicService.updateTopic(topicDto);
    return ResponseEntity.ok(updatedTopic);
  }
}