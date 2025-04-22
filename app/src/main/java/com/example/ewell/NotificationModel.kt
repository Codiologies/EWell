package com.example.ewell

data class NotificationModel(
    val notificationId: String? = null,
    val message: String = "",
    val timestamp: String = "",
    var isRead: Boolean = false
)
