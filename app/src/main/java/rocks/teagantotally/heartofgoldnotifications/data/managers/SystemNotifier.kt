package rocks.teagantotally.heartofgoldnotifications.data.managers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifAlso
import rocks.teagantotally.heartofgoldnotifications.common.extensions.putInvoker
import rocks.teagantotally.heartofgoldnotifications.common.extensions.unique
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_PUBLISH
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_MESSAGE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_NOTIFICATION_ID
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import java.util.*
import kotlin.coroutines.CoroutineContext

class SystemNotifier(
    private val context: Context,
    private val notificationManager: NotificationManager
) : Notifier, Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.Main) }

    override fun notify(notification: NotificationMessage) {
        createChannel(notification.channel)
        notification.transform(context)
            .let {
                notificationManager.notify(it.first, it.second)
            }
    }

    override fun dismiss(notificationId: Int) =
        notificationManager.cancel(notificationId)

    private fun createChannel(notificationChannel: NotificationMessageChannel) {
        try {
            notificationManager.getNotificationChannel(notificationChannel.id)
        } catch (_: Throwable) {
            null
        } ?: notificationManager.createNotificationChannel(notificationChannel.transform())
    }

    private fun NotificationMessageChannel.transform(): NotificationChannel =
        NotificationChannel(id, name, importance.systemValue)
            .also { channel ->

                channel.enableLights(enableLights)
                if (enableLights) {
                    channel.lightColor = lightColor
                }
                vibrationPattern?.let {
                    channel.vibrationPattern = vibrationPattern
                }
                channel.lockscreenVisibility = visibility.systemValue
                channel.description = description
                channel.name = name
                channel.importance = importance.systemValue
                channel.setSound(null, null)
            }
}

@UseExperimental(ExperimentalCoroutinesApi::class)
@ObsoleteCoroutinesApi
fun NotificationMessage.transform(context: Context): Pair<Int, Notification> =
    Pair(
        notificationId,
        Notification.Builder(context, channel.id)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(autoCancel)
            .setOngoing(onGoing)
            .extend(Notification.WearableExtender())
            .extend(Notification.CarExtender())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setWhen(System.currentTimeMillis())
            .ifAlso({ openApplication }) { builder ->
                Intent(context, MainActivity::class.java)
                    .let {
                        it.putInvoker(SystemNotifier::class)
                        PendingIntent.getActivity(
                            context,
                            Int.unique(),
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                    .let { builder.setContentIntent(it) }
            }
            .ifAlso({ !actions.isNullOrEmpty() }) { builder ->
                actions
                    .forEach { action ->
                        Intent(ACTION_PUBLISH)
                            .putInvoker(SystemNotifier::class)
                            .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                            .putExtra(
                                EXTRA_MESSAGE,
                                Message(
                                    action.topic,
                                    action.payload,
                                    action.qos,
                                    action.retain,
                                    Date()
                                ) as Parcelable
                            )
                            .let {
                                PendingIntent.getBroadcast(
                                    context,
                                    Int.unique(),
                                    it,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            }
                            .let {
                                Notification.Action.Builder(0, action.text, it)
                                    .build()
                            }
                            .let { builder.addAction(it) }
                    }
            }
            .build()
            .also {
                it.flags += if (onGoing) Notification.FLAG_ONGOING_EVENT else Notification.FLAG_AUTO_CANCEL
            }
    )