package com.example.Ticket_Management_System.Service;

import java.util.List;

import com.example.Ticket_Management_System.entity.Notification;
import com.example.Ticket_Management_System.entity.UserEntity;

public interface NotificationService {

    void createNotification(String message, UserEntity recipient);

    List<Notification> getUnreadNotifications(UserEntity recipient);

    List<Notification> getAllNotifications(UserEntity recipient);

    void markAsRead(Long notificationId);
}
