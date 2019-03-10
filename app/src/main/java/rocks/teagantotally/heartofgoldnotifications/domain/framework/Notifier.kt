package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage

interface Notifier {
    fun notify(notification: NotificationMessage)

    fun dismiss(notificationId: Int)
}