package com.example.forum.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String author;
  private String text;
  private LocalDateTime creationDate = LocalDateTime.now();

  @ManyToOne(fetch = FetchType.LAZY)
  private Topic topic;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }
  
}