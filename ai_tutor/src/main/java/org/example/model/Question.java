package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "text_question", nullable = false, columnDefinition = "text")
    private String textQuestion;

    @Column(name = "source", length = 30)
    private String source;

    @Column(name = "language", length = 20)
    private String language;

    public Question() {};

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTextQuestion(String textQuestion) {
        this.textQuestion = textQuestion;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getLanguage() {
        return language;
    }

    public String getTextQuestion() {
        return textQuestion;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", textQuestion='" + textQuestion + '\'' +
                ", source='" + source + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
