package com.example.forum.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.forum.dto.TopicDTO;
import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.entity.User;
import com.example.forum.service.ForumService;
import com.example.forum.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class ForumControllerTest {

  @Mock private ForumService topicService;

  @Mock private UserService userService;

  @Mock private Principal principal;

  @InjectMocks private ForumController forumController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTopic_shouldReturnCreatedTopic() {
    TopicDTO topicDto = new TopicDTO();
    User user = new User();
    user.setId(1L);
    Topic createdTopic = new Topic();
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(topicService.createTopic(topicDto, user.getId())).thenReturn(createdTopic);

    Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password");
    SecurityContextHolder.getContext().setAuthentication(authentication);

    ResponseEntity<Topic> response = forumController.createTopic(topicDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(createdTopic, response.getBody());
  }

  @Test
  void getAllTopics_shouldReturnPageOfTopics() {
    List<Topic> topicsList = new ArrayList<>();
    Page<Topic> topics = new PageImpl<>(topicsList);
    Pageable pageable = Pageable.unpaged();
    when(topicService.getAllTopics(pageable)).thenReturn(topics);

    ResponseEntity<Page<Topic>> response = forumController.getAllTopics(pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(topics, response.getBody());
  }

  @Test
  void updateTopic_shouldReturnUpdatedTopic() {
    TopicDTO topicDto = new TopicDTO();
    User user = new User();
    user.setId(1L);
    Topic updatedTopic = new Topic();
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(topicService.updateTopic(topicDto, user.getId())).thenReturn(updatedTopic);

    when(principal.getName()).thenReturn("testuser");

    ResponseEntity<Topic> response = forumController.updateTopic(topicDto, principal);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedTopic, response.getBody());
  }

  @Test
  void getTopicById_shouldReturnTopic() {
    UUID topicId = UUID.randomUUID();
    Topic topic = new Topic();
    when(topicService.getTopicById(topicId)).thenReturn(topic);

    ResponseEntity<Topic> response = forumController.getTopicById(topicId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(topic, response.getBody());
  }

  @Test
  void addMessageToTopic_shouldReturnUpdatedTopic() {
    UUID topicId = UUID.randomUUID();
    Message message = new Message();
    User user = new User();
    user.setId(1L);
    Topic updatedTopic = new Topic();
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(topicService.getTopicById(topicId)).thenReturn(updatedTopic);

    when(principal.getName()).thenReturn("testuser");

    ResponseEntity<Topic> response = forumController.addMessageToTopic(topicId, message, principal);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(updatedTopic, response.getBody());
    verify(topicService).addMessageToTopic(topicId, message, user.getId());
  }

  @Test
  void updateMessageInTopic_shouldReturnUpdatedTopic() {
    UUID topicId = UUID.randomUUID();
    Message messageDetails = new Message();
    User user = new User();
    user.setId(1L);
    Topic updatedTopic = new Topic();
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(topicService.updateMessageInTopic(topicId, messageDetails, user.getId()))
        .thenReturn(updatedTopic);

    when(principal.getName()).thenReturn("testuser");

    ResponseEntity<Topic> response =
        forumController.updateMessageInTopic(topicId, messageDetails, principal);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedTopic, response.getBody());
  }

  @Test
  void deleteMessage_shouldReturnNoContent() {
    UUID messageId = UUID.randomUUID();
    User user = new User();
    user.setId(1L);
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

    when(principal.getName()).thenReturn("testuser");

    ResponseEntity<?> response = forumController.deleteMessage(messageId, principal);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(topicService).deleteMessage(messageId, user.getId());
  }

  @Test
  void getMessagesByTopicId_shouldReturnPageOfMessages() {
    UUID topicId = UUID.randomUUID();
    List<Message> messagesList = new ArrayList<>();
    Page<Message> messages = new PageImpl<>(messagesList);
    Pageable pageable = Pageable.unpaged();
    when(topicService.getTopicMessage(topicId, pageable)).thenReturn(messages);

    ResponseEntity<Page<Message>> response =
        forumController.getMessagesByTopicId(topicId, pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(messages, response.getBody());
  }
}
