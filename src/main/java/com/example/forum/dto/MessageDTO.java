package com.example.forum.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MessageDTO implements Serializable {
  private UUID id;
  private String text;
  private String author;
  private OffsetDateTime created;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public OffsetDateTime getCreated() {
    return created;
  }

  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }
}
