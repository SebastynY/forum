package com.example.forum.service;

import com.example.forum.dto.MessageDTO;
import com.example.forum.dto.TopicDTO;
import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.repository.MessageRepository;
import com.example.forum.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ForumeServiceTest {

  @Mock
  private TopicRepository topicRepository;

  @Mock
  private MessageRepository messageRepository;

  @InjectMocks
  private ForumService forumService;

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
    messageDto.setAuthor("Test Author");
    messageDto.setCreated(OffsetDateTime.now());

    topicDto.setMessage(messageDto);

    Topic expectedTopic = new Topic();
    expectedTopic.setTitle("Test Topic");
    expectedTopic.setCreatedAt(OffsetDateTime.now());

    Message message = new Message();
    message.setText(messageDto.getText());
    message.setAuthor(messageDto.getAuthor());
    message.setCreatedAt(messageDto.getCreated());
    expectedTopic.getMessages().add(message);

    when(topicRepository.save(any(Topic.class))).thenReturn(expectedTopic);

    Topic actualTopic = forumService.createTopic(topicDto);

    assertEquals(expectedTopic.getTitle(), actualTopic.getTitle());
    assertEquals(expectedTopic.getMessages().size(), actualTopic.getMessages().size());

    Message actualMessage = actualTopic.getMessages().get(0);
    assertEquals(messageDto.getText(), actualMessage.getText());
    assertEquals(messageDto.getAuthor(), actualMessage.getAuthor());
    assertEquals(messageDto.getCreated(), actualMessage.getCreatedAt());

    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  public void getAllTopics_ReturnsListOfTopics() {
    // Arrange
    List<Topic> expectedTopics = new ArrayList<>();
    expectedTopics.add(new Topic());
    expectedTopics.add(new Topic());

    when(topicRepository.findAll()).thenReturn(expectedTopics);

    // Act
    List<Topic> actualTopics = forumService.getAllTopics();

    // Assert
    assertEquals(expectedTopics.size(), actualTopics.size());
    verify(topicRepository, times(1)).findAll();
  }

  @Test
  public void getTopicById_ValidId_ReturnsTopic() {
    // Arrange
    UUID topicId = UUID.randomUUID();
    Topic expectedTopic = new Topic();
    expectedTopic.setId(topicId);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(expectedTopic));

    // Act
    Topic actualTopic = forumService.getTopicById(topicId);

    // Assert
    assertEquals(expectedTopic.getId(), actualTopic.getId());
    verify(topicRepository, times(1)).findById(topicId);
  }

  @Test
  public void getTopicById_InvalidId_ThrowsException() {
    // Arrange
    UUID topicId = UUID.randomUUID();

    when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> forumService.getTopicById(topicId));
    verify(topicRepository, times(1)).findById(topicId);
  }

  @Test
  public void updateTopic_ValidInput_ReturnsTopic() {
    UUID topicId = UUID.randomUUID();
    TopicDTO topicDto = new TopicDTO();
    topicDto.setId(topicId);
    topicDto.setTopicName("Updated Topic");

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);
    existingTopic.setTitle("Original Topic");

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(topicRepository.save(any(Topic.class))).thenReturn(existingTopic);

    Topic actualTopic = forumService.updateTopic(topicDto);

    assertEquals(topicDto.getTopicName(), actualTopic.getTitle());
    verify(topicRepository, times(1)).findById(topicId);
    verify(topicRepository, times(1)).save(any(Topic.class));
  }

  @Test
  public void addMessageToTopic_ValidInput_ReturnsMessage() {
    UUID topicId = UUID.randomUUID();
    Message message = new Message();
    message.setText("Test Message");
    message.setAuthor("Test Author");

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(topicRepository.save(any(Topic.class))).thenReturn(existingTopic);

    Message actualMessage = forumService.addMessageToTopic(topicId, message);

    assertEquals(message.getText(), actualMessage.getText());
    assertEquals(message.getAuthor(), actualMessage.getAuthor());
    assertNotNull(actualMessage.getCreatedAt());
    assertEquals(existingTopic, actualMessage.getTopic());
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

    Topic existingTopic = new Topic();
    existingTopic.setId(topicId);

    Message existingMessage = new Message();
    existingMessage.setId(messageId);
    existingMessage.setText("Original Message");
    existingMessage.setTopic(existingTopic);

    when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
    when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);

    Topic actualTopic = forumService.updateMessageInTopic(topicId, messageDetails);

    assertEquals(existingTopic, actualTopic);
    assertEquals(messageDetails.getText(), existingMessage.getText());
    verify(topicRepository, times(1)).findById(topicId);
    verify(messageRepository, times(1)).findById(messageId);
    verify(messageRepository, times(1)).save(any(Message.class));
  }

  @Test
  public void deleteMessage_ValidId_DeletesMessage() {
    UUID messageId = UUID.randomUUID();
    Message existingMessage = new Message();
    existingMessage.setId(messageId);

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));

    forumService.deleteMessage(messageId);

    verify(messageRepository, times(1)).findById(messageId);
    verify(messageRepository, times(1)).delete(existingMessage);
  }
}