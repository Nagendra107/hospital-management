package com.hospital.repository;

import com.hospital.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.roomId = :roomId AND m.isRead = false AND m.senderRole = :senderRole")
    long countUnreadMessages(String roomId, String senderRole);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.roomId = :roomId AND m.senderRole != :readerRole")
    void markAllAsRead(String roomId, String readerRole);

    @Query("SELECT DISTINCT m.roomId FROM ChatMessage m WHERE m.roomId LIKE %:userId%")
    List<String> findRoomsByUser(String userId);
}
