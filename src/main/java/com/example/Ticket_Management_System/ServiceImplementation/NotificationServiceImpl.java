package com.example.Ticket_Management_System.ServiceImplementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Ticket_Management_System.Repository.NotificationRepository;
import com.example.Ticket_Management_System.Service.NotificationService;
import com.example.Ticket_Management_System.entity.Notification;
import com.example.Ticket_Management_System.entity.UserEntity;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void createNotification(String message, UserEntity recipient) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRecipient(recipient);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUnreadNotifications(UserEntity recipient) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(recipient);
    }

    @Override
    public List<Notification> getAllNotifications(UserEntity recipient) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
