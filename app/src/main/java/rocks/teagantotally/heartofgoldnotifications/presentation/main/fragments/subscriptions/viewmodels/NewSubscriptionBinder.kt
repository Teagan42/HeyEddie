package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.viewmodels

import android.text.Editable
import android.view.View
import kotlinx.android.synthetic.main.item_add_subscription.view.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.asMessageType
import rocks.teagantotally.heartofgoldnotifications.common.extensions.asQoS
import rocks.teagantotally.heartofgoldnotifications.common.extensions.select
import rocks.teagantotally.heartofgoldnotifications.presentation.common.OptionsMenuCallbacks
import rocks.teagantotally.heartofgoldnotifications.presentation.common.SimpleTextWatcher
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.ConditionalItemBinder

class NewSubscriptionBinder(private val optionsMenuCallbacks: OptionsMenuCallbacks) :
    ConditionalItemBinder<SubscriptionViewModel> {
    override fun canBind(item: SubscriptionViewModel): Boolean =
        item is SubscriptionViewModel.NewSubscription

    override fun getLayoutResourceId(item: SubscriptionViewModel): Int =
        R.layout.item_add_subscription

    override fun bind(item: SubscriptionViewModel, view: View) {
        with(view) {
            (item as? SubscriptionViewModel.NewSubscription)
                ?.let { subscription ->
                    new_subscription_topic.addTextChangedListener(TopicTextWatcher(subscription, optionsMenuCallbacks))
                    subscription.topic
                        ?.let {
                            new_subscription_topic.text =
                                Editable.Factory
                                    .getInstance()
                                    .newEditable(subscription.topic)
                        }

                    with(new_subscription_max_qos) {
                        asQoS {
                            subscription.maxQoS = it
                            optionsMenuCallbacks.invalidateOptionsMenu()
                        }

                        select(subscription.maxQoS) { qosInt, qosString ->
                            qosInt == qosString?.toIntOrNull()
                        }
                        setSelection(subscription.maxQoS ?: 0, true)
                    }

                    with(new_subscription_message_type) {
                        asMessageType {
                            subscription.messageType = it
                            optionsMenuCallbacks.invalidateOptionsMenu()
                        }
                        select(subscription.messageType) { messageType, typeString ->
                            messageType?.name == typeString
                        }
                    }
                }
        }
    }

    private class TopicTextWatcher(
        private val item: SubscriptionViewModel.NewSubscription,
        private val optionsMenuCallbacks: OptionsMenuCallbacks
    ) : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            item.topic = s?.toString()
            optionsMenuCallbacks.invalidateOptionsMenu()
        }
    }
}