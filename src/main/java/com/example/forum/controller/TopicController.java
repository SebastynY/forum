package com.example.forum.controller;

import com.example.forum.dto.TopicDTO;
import com.example.forum.model.Message;
import com.example.forum.model.Topic;
import com.example.forum.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
  @GetMapping("/topic/{topicId}")
  public ResponseEntity<Topic> getTopicById(@PathVariable UUID topicId) {
    Topic topic = topicService.getTopicById(topicId);
    return ResponseEntity.ok(topic);
  }
  @PostMapping("/topic/{topicId}/message")
  public ResponseEntity<Topic> addMessageToTopic(@PathVariable UUID topicId, @RequestBody Message message) {
    topicService.addMessageToTopic(topicId, message);
    Topic updatedTopic = topicService.getTopicById(topicId);
    return new ResponseEntity<>(updatedTopic, HttpStatus.CREATED);
  }
  @PutMapping("/topic/{topicId}/message")
  public ResponseEntity<Topic> updateMessageInTopic(@PathVariable UUID topicId, @RequestBody Message messageDetails) {
    Topic updatedTopic = topicService.updateMessageInTopic(topicId, messageDetails);
    return new ResponseEntity<>(updatedTopic, HttpStatus.OK);
  }



}