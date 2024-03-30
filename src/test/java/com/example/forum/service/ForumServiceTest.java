package com.example.forum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.forum.dto.MessageDTO;
import com.example.forum.dto.TopicDTO;
import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.entity.User;
import com.example.forum.exception.NotAuthorizedException;
import com.example.forum.repository.MessageRepository;
import com.example.forum.repository.TopicRepository;
import com.example.forum.repository.UserRepository;
import java.time.OffsetDateTime;
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
import org.springframework.data.domain.PageRequest;

public class ForumServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private TopicRepository topicRepository;
  @Mock private MessageRepository messageRepository;

  @InjectMocks private ForumService forumService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void createTopic_ValidInput_ReturnsTopic() {
    TopicDTO topicDto = new TopicDTO();
    topicDto.setTopicName("Test Topic");

    MessageDTO messageDto = new MessageDTO();
    messageDto.setText("Test Message");
    messageDto.setCreated(OffsetDateTime.now());

    topicDto.setMessage(messageDto);

    User user = new User();
    user.setId(1L);
    user.setUsername("Test Author");

    Topic expectedTopic = new Topic();
    expectedTopic.setTitle("Test Topic");
    expectedTopic.setCreated(OffsetDateTime.now());
    expectedTopic.setUser(user);

    Message message = new Message();
    message.setText(messageDto.getText());
    message.setAuthor(user.getUsername());
    message.setCreated(messageDto.getCreated());
    expectedTopic.getMessages().add(message);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(topicRepository.save(any(Topic.class))).thenReturn(expectedTopic);

    Topic actualTopic = forumService.createTopic(topicDto, 1L);

    assertEquals(expectedTopic.getTitle(), actualTopic.getTitle());
    assertEquals(expectedTopic.getMessages().size(), actualTopic.getMessages().size());

    Message actualMessage = actualTopic.getMessages().get(0);
    assertEquals(messageDto.getText(), actualMessage.getText());
    assertEquals(user.getUsername(), actualMessage.getAuthor());
    assertEquals(messageDto.getCreated(), actualMessage.getCreated());

    verify(userRepository, times(1)).findById(1L);
    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  public void getAllTopics_ReturnsPageOfTopics() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Topic> expectedTopics = new PageImpl<>(List.of(new Topic(), new Topic()));

    when(topicRepository.findAll(pageable)).thenReturn(expectedTopics);

    Page<Topic> actualTopics = forumService.getAllTopics(pageable);

    assertEquals(expectedTopics.getSize(), actualTopics.getSize());
    verify(topicRepository, times(1)).findAll(pageable);
  }

  @Test
  public void getTopicById_ValidId_ReturnsTopic() {
    UUID topicId = UUID.randomUUID();
    Topic expectedTopic = new Topic();
    expectedTopic.setId(topicId);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(expectedTopic));

    Topic actualTopic = forumService.getTopicById(topicId);

    assertEquals(expectedTopic.getId(), actualTopic.getId());
    verify(topicRepository, times(1)).findById(topicId);
  }

  @Test
  public void getTopicById_InvalidId_ThrowsException() {
    UUID topicId = UUID.randomUUID();

    when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

    assertThrows(NotAuthorizedException.class, () -> forumService.getTopicById(topicId));
    verify(topicRepository, times(1)).findById(topicId);
  }

  @Test
  public void updateTopic_ValidInput_ReturnsTopic() {
    UUID topicId = UUID.randomUUID();
    TopicDTO topicDto = new TopicDTO();
    topicDto.setId(topicId);
    topicDto.setTopicName("Updated Topic");

    User user = new User();
    user.setId(1L);

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);
    existingTopic.setTitle("Original Topic");
    existingTopic.setUser(user);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(topicRepository.save(any(Topic.class))).thenReturn(existingTopic);

    Topic actualTopic = forumService.updateTopic(topicDto, 1L);

    assertEquals(topicDto.getTopicName(), actualTopic.getTitle());
    verify(topicRepository, times(1)).findById(topicId);
    verify(userRepository, times(1)).findById(1L);
    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  public void updateTopic_UserNotAuthor_ThrowsException() {
    UUID topicId = UUID.randomUUID();
    TopicDTO topicDto = new TopicDTO();
    topicDto.setId(topicId);

    User user = new User();
    user.setId(1L);

    User otherUser = new User();
    otherUser.setId(2L);

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);
    existingTopic.setUser(otherUser);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThrows(NotAuthorizedException.class, () -> forumService.updateTopic(topicDto, 1L));
    verify(topicRepository, times(1)).findById(topicId);
    verify(userRepository, times(1)).findById(1L);
    verify(topicRepository, never()).save(any(Topic.class));
  }

  @Test
  public void addMessageToTopic_ValidInput_ReturnsMessage() {
    UUID topicId = UUID.randomUUID();
    Message message = new Message();
    message.setText("Test Message");

    User user = new User();
    user.setId(1L);
    user.setUsername("Test Author");

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(topicRepository.save(any(Topic.class))).thenReturn(existingTopic);

    Message actualMessage = forumService.addMessageToTopic(topicId, message, 1L);

    assertEquals(message.getText(), actualMessage.getText());
    assertEquals(user.getUsername(), actualMessage.getAuthor());
    assertNotNull(actualMessage.getCreated());
    assertEquals(existingTopic, actualMessage.getTopic());
    verify(userRepository, times(1)).findById(1L);
    verify(topicRepository, times(1)).findById(topicId);
    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  public void updateMessageInTopic_ValidInput_ReturnsTopic() {
    UUID topicId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    Message messageDetails = new Message();
    messageDetails.setId(messageId);
    messageDetails.setText("Updated Message");

    User user = new User();
    user.setId(1L);
    user.setUsername("Test Author");

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);

    Message existingMessage = new Message();
    existingMessage.setId(messageId);
    existingMessage.setText("Original Message");
    existingMessage.setAuthor(user.getUsername());
    existingMessage.setTopic(existingTopic);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);

    Topic actualTopic = forumService.updateMessageInTopic(topicId, messageDetails, 1L);

    assertEquals(existingTopic, actualTopic);
    assertEquals(messageDetails.getText(), existingMessage.getText());
    verify(topicRepository, times(1)).findById(topicId);
    verify(messageRepository, times(1)).findById(messageId);
    verify(userRepository, times(1)).findById(1L);
    verify(messageRepository, times(1)).save(any(Message.class));
  }

  @Test
  public void updateMessageInTopic_UserNotAuthor_ThrowsException() {
    UUID topicId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    Message messageDetails = new Message();
    messageDetails.setId(messageId);

    User user = new User();
    user.setId(1L);
    user.setUsername("Test Author");

    User otherUser = new User();
    otherUser.setId(2L);
    otherUser.setUsername("Other Author");

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);

    Message existingMessage = new Message();
    existingMessage.setId(messageId);
    existingMessage.setAuthor(otherUser.getUsername());
    existingMessage.setTopic(existingTopic);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThrows(
        NotAuthorizedException.class,
        () -> forumService.updateMessageInTopic(topicId, messageDetails, 1L));
    verify(topicRepository, times(1)).findById(topicId);
    verify(messageRepository, times(1)).findById(messageId);
    verify(userRepository, times(1)).findById(1L);
    verify(messageRepository, never()).save(any(Message.class));
  }

  @Test
  public void deleteMessage_ValidId_DeletesMessage() {
    UUID messageId = UUID.randomUUID();
    UUID topicId = UUID.randomUUID();

    User user = new User();
    user.setId(1L);
    user.setUsername("Test Author");

    Topic topic = new Topic();
    topic.setId(topicId);

    Message existingMessage = new Message();
    existingMessage.setId(messageId);
    existingMessage.setAuthor(user.getUsername());
    existingMessage.setTopic(topic);

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(messageRepository.countByTopicId(topicId)).thenReturn(0L);

    forumService.deleteMessage(messageId, 1L);

    verify(messageRepository, times(1)).findById(messageId);
    verify(userRepository, times(1)).findById(1L);
    verify(messageRepository, times(1)).delete(existingMessage);
    verify(messageRepository, times(1)).countByTopicId(topicId);
    verify(topicRepository, times(1)).deleteById(topicId);
  }

  @Test
  public void deleteMessage_UserNotAuthor_ThrowsException() {
    UUID messageId = UUID.randomUUID();

    User user = new User();
    user.setId(1L);
    user.setUsername("Test Author");

    User otherUser = new User();
    otherUser.setId(2L);
    otherUser.setUsername("Other Author");

    Message existingMessage = new Message();
    existingMessage.setId(messageId);
    existingMessage.setAuthor(otherUser.getUsername());

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThrows(NotAuthorizedException.class, () -> forumService.deleteMessage(messageId, 1L));
    verify(messageRepository, times(1)).findById(messageId);
    verify(userRepository, times(1)).findById(1L);
    verify(messageRepository, never()).delete(any(Message.class));
    verify(messageRepository, never()).countByTopicId(any(UUID.class));
    verify(topicRepository, never()).deleteById(any(UUID.class));
  }

  @Test
  public void getMessagesByTopicId_ReturnsPageOfMessages() {
    UUID topicId = UUID.randomUUID();
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Message> expectedMessages = new PageImpl<>(List.of(new Message(), new Message()));

    when(messageRepository.findByTopicId(topicId, pageable)).thenReturn(expectedMessages);

    Page<Message> actualMessages = forumService.getTopicMessage(topicId, pageable);

    assertEquals(expectedMessages.getSize(), actualMessages.getSize());
    verify(messageRepository, times(1)).findByTopicId(topicId, pageable);
  }
}
