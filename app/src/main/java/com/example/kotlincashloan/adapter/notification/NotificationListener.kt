package com.example.kotlincashloan.adapter.notification

import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel

interface NotificationListener {
    fun notificationClickListener(position: Int, item: ResultListNoticeModel)
}