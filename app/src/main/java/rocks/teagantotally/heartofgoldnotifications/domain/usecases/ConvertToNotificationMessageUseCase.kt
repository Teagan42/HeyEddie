package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message

class ConvertToNotificationMessageUseCase(
    private val gson: Gson
) : UseCase<Message, NotificationMessage?>() {
    override suspend fun invoke(params: Message): NotificationMessage? =
        try {
            gson.fromJson<NotificationMessage>(String(params.payload), NotificationMessage::class.java)
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
            null
        }
}