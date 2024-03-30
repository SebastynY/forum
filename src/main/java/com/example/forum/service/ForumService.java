package com.example.forum.service;

import com.example.forum.dto.TopicDTO;
import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.entity.User;
import com.example.forum.exception.NotAuthorizedException;
import com.example.forum.repository.MessageRepository;
import com.example.forum.repository.TopicRepository;
import com.example.forum.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Класс сервиса для управления темами и сообщениями форума. Предоставляет функциональность для
 * создания, обновления, получения и удаления тем и сообщений на форуме.
 */
@Service
public class ForumService {

  @Autowired private UserRepository userRepository;

  @Autowired private TopicRepository topicRepository;

  @Autowired private MessageRepository messageRepository;

  /**
   * Создает новую тему на основе предоставленного DTO темы, включая начальное сообщение.
   *
   * @param topicDto DTO, содержащее информацию, необходимую для создания новой темы и ее начального
   *     сообщения.
   * @return Новосозданная сущность темы.
   */
  @Transactional
  public Topic createTopic(TopicDTO topicDto, Long userId) {
    if (topicDto == null || topicDto.getMessage() == null) {
      throw new NotAuthorizedException("Topic and initial message must be provided");
    }
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotAuthorizedException("User not found"));

    Topic topic = new Topic();
    topic.setTitle(topicDto.getTopicName());
    topic.setCreated(OffsetDateTime.now());
    topic.setUser(user);

    Message message = new Message();
    message.setText(topicDto.getMessage().getText());
    message.setAuthor(user.getUsername());
    message.setCreated(
        topicDto.getMessage().getCreated() != null
            ? topicDto.getMessage().getCreated()
            : OffsetDateTime.now());
    message.setTopic(topic);

    topic.getMessages().add(message);

    topic = topicRepository.save(topic);
    return topic;
  }

  /**
   * Получает все темы.
   *
   * @return Список всех тем.
   */
  @Transactional
  public Page<Topic> getAllTopics(Pageable pageable) {
    return topicRepository.findAll(pageable);
  }

  /**
   * Обновляет тему на основе предоставленного DTO темы.
   *
   * @param topicDto DTO темы, содержащее обновленные данные.
   * @return Обновленная сущность темы.
   */
  @Transactional
  public Topic updateTopic(TopicDTO topicDto, Long userId) {
    UUID topicId = topicDto.getId();
    Topic topic =
        topicRepository
            .findById(topicId)
            .orElseThrow(() -> new NotAuthorizedException("Topic not found"));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotAuthorizedException("User not found"));

    if (!topic.getUser().getId().equals(user.getId())) {
      throw new NotAuthorizedException("Only the author can update the topic");
    }

    if (topicDto.getTopicName() != null) {
      topic.setTitle(topicDto.getTopicName());
    }

    return topicRepository.save(topic);
  }

  /**
   * Получает тему по ее идентификатору.
   *
   * @param topicId Идентификатор темы.
   * @return Сущность темы.
   */
  @Transactional
  public Topic getTopicById(UUID topicId) {
    return topicRepository
        .findById(topicId)
        .orElseThrow(() -> new NotAuthorizedException("Topic not found"));
  }

  /**
   * Добавляет сообщение в тему.
   *
   * @param topicId Идентификатор темы, в которую добавляется сообщение.
   * @param message Сообщение для добавления.
   * @param userId Идентификатор пользователя.
   * @return Добавленное сообщение.
   */
  @Transactional
  public Message addMessageToTopic(UUID topicId, Message message, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotAuthorizedException("User not found"));

    Topic topic =
        topicRepository
            .findById(topicId)
            .orElseThrow(() -> new NotAuthorizedException("Topic not found"));

    message.setAuthor(user.getUsername());
    message.setTopic(topic);
    message.setCreated(OffsetDateTime.now());
    topic.getMessages().add(message);
    topicRepository.save(topic);
    return message;
  }

  /**
   * Обновляет сообщение в теме.
   *
   * @param topicId Идентификатор темы, в которой находится сообщение.
   * @param messageDetails Детали сообщения для обновления.
   * @return Тема, в которой было обновлено сообщение.
   */
  @Transactional
  public Topic updateMessageInTopic(UUID topicId, Message messageDetails, Long userId) {
    Topic topic =
        topicRepository
            .findById(topicId)
            .orElseThrow(() -> new NotAuthorizedException("Topic not found"));
    UUID messageId = messageDetails.getId();
    Message messageToUpdate =
        messageRepository
            .findById(messageId)
            .orElseThrow(() -> new NotAuthorizedException("Message not found"));

    if (!messageToUpdate.getTopic().getId().equals(topicId)) {
      throw new NotAuthorizedException("Message does not belong to the topic");
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotAuthorizedException("User not found"));

    if (!messageToUpdate.getAuthor().equals(user.getUsername())) {
      throw new NotAuthorizedException("Only the author can update the message");
    }

    messageToUpdate.setText(messageDetails.getText());
    messageRepository.save(messageToUpdate);
    return topic;
  }

  /**
   * Удаляет сообщение по его идентификатору.
   *
   * @param messageId Идентификатор удаляемого сообщения.
   */
  @Transactional
  public void deleteMessage(UUID messageId, Long userId) {
    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(() -> new NotAuthorizedException("Message not found"));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotAuthorizedException("User not found"));

    if (!message.getAuthor().equals(user.getUsername())) {
      throw new NotAuthorizedException("Only the author can delete the message");
    }

    UUID topicId = message.getTopic().getId();
    messageRepository.delete(message);
    if (messageRepository.countByTopicId(topicId) == 0) {
      topicRepository.deleteById(topicId);
    }
  }

  /**
   * Пагинированный запрос сообщений по идентификатору темы.
   *
   * @param topicId Идентификатор темы, для которой требуются сообщения, в формате UUID.
   * @param pageable Параметры для пагинации и сортировки результатов.
   * @return Страница с сообщениями темы, включающая в себя данные о сообщениях и информацию о
   *     пагинации.
   */
  public Page<Message> getTopicMessage(UUID topicId, Pageable pageable) {
    return messageRepository.findByTopicId(topicId, pageable);
  }
}
