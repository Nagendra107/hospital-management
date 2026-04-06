package com.hospital.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;        // e.g. "doctor_3_patient_7"
    private String senderEmail;
    private String senderName;
    private String senderRole;    // DOCTOR or PATIENT

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean isRead = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime sentAt;

    public ChatMessage() {}

    public ChatMessage(String roomId, String senderEmail, String senderName, String senderRole, String content) {
        this.roomId = roomId;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.content = content;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
