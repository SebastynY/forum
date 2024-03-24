package com.example.forum.controller;

import com.example.forum.dto.TopicDTO;
import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.service.ForumService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@Tag(
    name = "ForumController",
    description = "Контроллер для управления темами и сообщениями форума")
public class ForumController {

  @Autowired private ForumService topicService;

  @PostMapping("/topic")
  @ApiOperation(
      value = "Создать новую тему",
      response = Topic.class,
      notes = "Этот метод создает новую тему на форуме с начальным сообщением.")
  public ResponseEntity<Topic> createTopic(@RequestBody TopicDTO topicDto) {
    Topic createdTopic = topicService.createTopic(topicDto);
    return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
  }

  @GetMapping("/topic")
  @ApiOperation(
      value = "Получить все темы",
      response = Topic.class,
      responseContainer = "List",
      notes = "Возвращает список всех тем на форуме.")
  public ResponseEntity<Page<Topic>> getAllTopics(@PageableDefault(size = 5) Pageable pageable) {
    Page<Topic> topics = topicService.getAllTopics(pageable);
    return ResponseEntity.ok(topics);
  }

  @PutMapping("/topic")
  @ApiOperation(
      value = "Обновить тему",
      response = Topic.class,
      notes = "Обновляет детали существующей темы. Требует указания ID темы в DTO.")
  public ResponseEntity<Topic> updateTopic(@RequestBody TopicDTO topicDto) {
    Topic updatedTopic = topicService.updateTopic(topicDto);
    return ResponseEntity.ok(updatedTopic);
  }

  @GetMapping("/topic/{topicId}")
  @ApiOperation(
      value = "Получить тему по ID",
      response = Topic.class,
      notes = "Возвращает тему по уникальному идентификатору.")
  public ResponseEntity<Topic> getTopicById(@PathVariable UUID topicId) {
    Topic topic = topicService.getTopicById(topicId);
    return ResponseEntity.ok(topic);
  }

  @PostMapping("/topic/{topicId}/message")
  @ApiOperation(
      value = "Добавить сообщение в тему",
      response = Topic.class,
      notes = "Добавляет новое сообщение к существующей теме по ID темы.")
  public ResponseEntity<Topic> addMessageToTopic(
      @PathVariable UUID topicId, @RequestBody Message message) {
    topicService.addMessageToTopic(topicId, message);
    Topic updatedTopic = topicService.getTopicById(topicId);
    return new ResponseEntity<>(updatedTopic, HttpStatus.CREATED);
  }

  @PutMapping("/topic/{topicId}/message")
  @ApiOperation(
      value = "Обновить сообщение в теме",
      response = Topic.class,
      notes = "Обновляет существующее сообщение в теме. Требуется ID темы и детали сообщения.")
  public ResponseEntity<Topic> updateMessageInTopic(
      @PathVariable UUID topicId, @RequestBody Message messageDetails) {
    Topic updatedTopic = topicService.updateMessageInTopic(topicId, messageDetails);
    return new ResponseEntity<>(updatedTopic, HttpStatus.OK);
  }

  @DeleteMapping("/message/{messageId}")
  @ApiOperation(
      value = "Удалить сообщение",
      notes = "Удаляет сообщение по его уникальному идентификатору.")
  public ResponseEntity<?> deleteMessage(@PathVariable UUID messageId) {
    topicService.deleteMessage(messageId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/topic/{topicId}/messages")
  @ApiOperation(
      value = "Получить сообщения темы по ID",
      response = Message.class,
      responseContainer = "Page",
      notes = "Возвращает страницу с сообщениями заданной темы, поддерживая пагинацию.")
  public ResponseEntity<Page<Message>> getMessagesByTopicId(
      @PathVariable UUID topicId, @PageableDefault(size = 10) Pageable pageable) {
    Page<Message> messages = topicService.getTopicMessages(topicId, pageable);
    return ResponseEntity.ok(messages);
  }
}
