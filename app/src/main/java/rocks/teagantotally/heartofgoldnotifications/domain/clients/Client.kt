package rocks.teagantotally.heartofgoldnotifications.domain.clients

import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

interface Client : MqttCallbackExtended {
    fun isConnected(): Boolean

    fun connect()

    fun disconnect()

    fun publish(message: Message)

    fun subscribe(topic: String, qosMax: Int)

    fun unsubscribe(topic: String)
}