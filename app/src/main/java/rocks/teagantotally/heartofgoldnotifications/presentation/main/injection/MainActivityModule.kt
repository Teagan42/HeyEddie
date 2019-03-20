package rocks.teagantotally.heartofgoldnotifications.presentation.main.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.ActivityScope
import rocks.teagantotally.heartofgoldnotifications.data.services.receivers.MqttEventBroadcastReceiver
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StopClient
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivityContract
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivityPresenter

@Module
class MainActivityModule(
    private val view: MainActivityContract.View
) {
    @Provides
    @ActivityScope
    fun provideView(): MainActivityContract.View =
        view

    @Provides
    @ActivityScope
    fun provideMqttEventReeiver(
        mqttEventConsumer: MainActivityContract.Presenter
    ): MqttEventBroadcastReceiver<*> =
        MqttEventBroadcastReceiver(mqttEventConsumer)


    @Provides
    @ActivityScope
    fun providePresenter(
        view: MainActivityContract.View,
        configManager: ConnectionConfigManager,
        startClient: StartClient,
        stopClient: StopClient
    ): MainActivityContract.Presenter =
        MainActivityPresenter(
            view,
            configManager,
            startClient,
            stopClient
        )
}