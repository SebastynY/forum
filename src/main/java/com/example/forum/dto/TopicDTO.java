  package com.example.forum.dto;
  import com.fasterxml.jackson.annotation.JsonAlias;
  import java.io.Serializable;
  import java.time.OffsetDateTime;
  import java.util.UUID;

  public class TopicDTO implements Serializable {

    private UUID id;

    @JsonAlias({"name"})
    private String topicName;

    private OffsetDateTime createdAt;
    private MessageDTO message;

    public String getTopicName() {
      return topicName;
    }

    public void setTopicName(String topicName) {
      this.topicName = topicName;
    }

    public MessageDTO getMessage() {
      return message;
    }

    public void setMessage(MessageDTO message) {
      this.message = message;
    }

    public UUID getId() {
      return id;
    }

    public OffsetDateTime getCreatedAt() {
      return createdAt;
    }
  }