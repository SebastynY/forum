package com.example.forum.service;

import com.example.forum.dto.TopicDTO;
import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.repository.MessageRepository;
import com.example.forum.repository.TopicRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class ForumService {

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private MessageRepository messageRepository;
  @Transactional
  public Topic createTopic(TopicDTO topicDto) {
    Topic topic = new Topic();
    topic.setTitle(topicDto.getTopicName());
    topic.setCreated(OffsetDateTime.now());

    Message message = new Message();
    message.setText(topicDto.getMessage().getText());
    message.setAuthor(topicDto.getMessage().getAuthor());
    message.setCreated(topicDto.getMessage().getCreated());
    message.setTopic(topic);

    topic.getMessages().add(message);

    return topicRepository.save(topic);
  }
  @Transactional
  public List<Topic> getAllTopics() {
    return topicRepository.findAll();
  }
  @Transactional
  public Topic updateTopic(TopicDTO topicDto) {
    UUID topicId = topicDto.getId();
    Topic topic = topicRepository.findById(topicId).orElseThrow(
        () -> new IllegalArgumentException("Topic not found")
    );

    if (topicDto.getTopicName() != null) {
      topic.setTitle(topicDto.getTopicName());
    }

    return topicRepository.save(topic);
  }
  @Transactional
  public Topic getTopicById(UUID topicId) {
    return topicRepository.findById(topicId).orElseThrow(
        () -> new IllegalArgumentException("Topic not found")
    );
  }
  @Transactional
  public Message addMessageToTopic(UUID topicId, Message message) {
    Topic topic = topicRepository.findById(topicId).orElseThrow(
        () -> new IllegalArgumentException("Topic not found")
    );
    message.setTopic(topic);
    message.setCreated(OffsetDateTime.now());
    topic.getMessages().add(message);
    topicRepository.save(topic);
    return message;
  }

  @Transactional
  public Topic updateMessageInTopic(UUID topicId, Message messageDetails) {
    Topic topic = topicRepository.findById(topicId).orElseThrow(
        () -> new IllegalArgumentException("Topic not found")
    );
    UUID messageId = messageDetails.getId();
    Message messageToUpdate = messageRepository.findById(messageId).orElseThrow(
        () -> new IllegalArgumentException("Message not found")
    );
    if (!messageToUpdate.getTopic().getId().equals(topicId)) {
      throw new IllegalArgumentException("Message does not belong to the topic");
    }
    messageToUpdate.setText(messageDetails.getText());
    messageRepository.save(messageToUpdate);
    return topic;
  }
  @Transactional
  public void deleteMessage(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    messageRepository.delete(message);
  }


}