package rocks.teagantotally.kotqtt.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.Serializable
import java.util.*

@Parcelize
data class Message(
    val topic: String,
    val retain: Boolean = false,
    val qos: QoS = QoS.DEFAULT_QOS,
    val payload: ByteArray = ByteArray(0),
    val date: Date = Date()
) : Parcelable, Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (topic != other.topic) return false
        if (retain != other.retain) return false
        if (qos != other.qos) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topic.hashCode()
        result = 31 * result + retain.hashCode()
        result = 31 * result + qos.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}