package com.example.forum.service;

import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.repository.MessageRepository;
import com.example.forum.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {
  private final TopicRepository topicRepository;
  private final MessageRepository messageRepository;

  @Autowired
  public ForumService(TopicRepository topicRepository, MessageRepository messageRepository) {
    this.topicRepository = topicRepository;
    this.messageRepository = messageRepository;
  }

  public List<Topic> getAllTopics() {
    return topicRepository.findAll();
  }

  public List<Message> getMessagesByTopic(Long topicId) {
    Topic topic = topicRepository.findById(topicId)
        .orElseThrow(() -> new RuntimeException("Topic not found"));
    return topic.getMessages();
  }

  public Topic createTopic(String title, String author, String messageText) {
    if (messageText == null || messageText.isEmpty()) {
      throw new IllegalArgumentException("Message text cannot be empty");
    }
    Topic topic = new Topic();
    topic.setTitle(title);
    Message message = new Message();
    message.setAuthor(author);
    message.setText(messageText);
    message.setTopic(topic);
    topic.addMessage(message);
    return topicRepository.save(topic);
  }

  public Message postMessage(Long topicId, String author, String messageText) {
    if (messageText == null || messageText.isEmpty()) {
      throw new IllegalArgumentException("Message text cannot be empty");
    }
    Topic topic = topicRepository.findById(topicId)
        .orElseThrow(() -> new RuntimeException("Topic not found"));
    Message message = new Message();
    message.setAuthor(author);
    message.setText(messageText);
    message.setTopic(topic);
    topic.addMessage(message);
    return messageRepository.save(message);
  }

  public Message editMessage(Long messageId, String newText) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new RuntimeException("Message not found"));
    message.setText(newText);
    return messageRepository.save(message);
  }

  public void deleteMessage(Long messageId) {
    messageRepository.deleteById(messageId);
  }
}

