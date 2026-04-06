package com.hospital.controller;

import com.hospital.model.ChatMessage;
import com.hospital.model.Doctor;
import com.hospital.model.PatientUser;
import com.hospital.repository.ChatMessageRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PatientUserRepository patientUserRepository;

    // ========== WebSocket Handler ==========
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message) {
        // Save to database
        ChatMessage saved = chatMessageRepository.save(message);
        // Broadcast to everyone in the room
        messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomId(), saved);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        messagingTemplate.convertAndSend("/topic/typing/" + roomId, payload);
    }

    // ========== REST APIs ==========

    // Get chat history for a room
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable String roomId) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
        return ResponseEntity.ok(messages);
    }

    // Mark messages as read
    @Transactional
    @PostMapping("/read/{roomId}")
    public ResponseEntity<?> markAsRead(@PathVariable String roomId, @RequestBody Map<String, String> body) {
        String readerRole = body.get("role");
        chatMessageRepository.markAllAsRead(roomId, readerRole);
        return ResponseEntity.ok(Map.of("message", "Marked as read"));
    }

    // Get all doctors available to chat (for patient)
    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctorsForChat() {
        List<Doctor> doctors = doctorRepository.findByStatus(Doctor.DoctorStatus.ACTIVE);
        return ResponseEntity.ok(doctors);
    }

    // Get all patients available to chat (for doctor)
    @GetMapping("/patients")
    public ResponseEntity<?> getPatientsForChat() {
        List<PatientUser> patients = patientUserRepository.findAll();
        patients.forEach(p -> p.setPassword(null));
        return ResponseEntity.ok(patients);
    }

    // Build room ID from doctor and patient IDs
    @GetMapping("/room")
    public ResponseEntity<?> getRoomId(@RequestParam Long doctorId, @RequestParam Long patientId) {
        String roomId = "doctor_" + doctorId + "_patient_" + patientId;
        return ResponseEntity.ok(Map.of("roomId", roomId));
    }

    // Get unread count
    @GetMapping("/unread/{roomId}")
    public ResponseEntity<?> getUnreadCount(@PathVariable String roomId, @RequestParam String role) {
        long count = chatMessageRepository.countUnreadMessages(roomId, role);
        return ResponseEntity.ok(Map.of("unread", count));
    }
}
