package com.example.forum.service;

import com.example.forum.dto.TopicDTO;
import com.example.forum.entity.Message;
import com.example.forum.entity.Topic;
import com.example.forum.repository.MessageRepository;
import com.example.forum.repository.TopicRepository;
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
  public Topic createTopic(TopicDTO topicDto) {
    if (topicDto == null || topicDto.getMessage() == null) {
      throw new IllegalArgumentException("Topic and initial message must be provided");
    }

    Topic topic = new Topic();
    topic.setTitle(topicDto.getTopicName());
    topic.setCreated(OffsetDateTime.now());

    Message message = new Message();
    message.setText(topicDto.getMessage().getText());
    message.setAuthor(topicDto.getMessage().getAuthor());
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
  public Topic updateTopic(TopicDTO topicDto) {
    UUID topicId = topicDto.getId();
    Topic topic =
        topicRepository
            .findById(topicId)
            .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

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
        .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
  }

  /**
   * Добавляет сообщение в тему.
   *
   * @param topicId Идентификатор темы, в которую добавляется сообщение.
   * @param message Сообщение для добавления.
   * @return Добавленное сообщение.
   */
  @Transactional
  public Message addMessageToTopic(UUID topicId, Message message) {
    Topic topic =
        topicRepository
            .findById(topicId)
            .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
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
  public Topic updateMessageInTopic(UUID topicId, Message messageDetails) {
    Topic topic =
        topicRepository
            .findById(topicId)
            .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
    UUID messageId = messageDetails.getId();
    Message messageToUpdate =
        messageRepository
            .findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    if (!messageToUpdate.getTopic().getId().equals(topicId)) {
      throw new IllegalArgumentException("Message does not belong to the topic");
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
  public void deleteMessage(UUID messageId) {
    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message not found"));
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
  public Page<Message> getTopicMessages(UUID topicId, Pageable pageable) {
    return messageRepository.findByTopicId(topicId, pageable);
  }
}
