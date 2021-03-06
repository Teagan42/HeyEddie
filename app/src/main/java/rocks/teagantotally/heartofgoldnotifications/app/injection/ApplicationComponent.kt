package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.IO
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationSavedUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.main.injection.MainActivityComponent
import javax.inject.Singleton

@ObsoleteCoroutinesApi
@UseExperimental(ExperimentalCoroutinesApi::class)
@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        NotificationModule::class,
        ConnectionModule::class,
        CoroutineModule::class
    ]
)
interface ApplicationComponent {
    fun inject(service: MqttService)
    fun inject(receiver: MqttService.DismissNotificationReceiver)

    @UI
    fun provideUICoroutineScope(): CoroutineScope

    @IO
    fun provideIOCoroutineScope(): CoroutineScope

    fun provideConnectionConfigurationChangedUseCase(): ClientConfigurationSavedUseCase

    fun provideConnectionConfigurationManager(): ConnectionConfigManager

    fun mainActivityComponentBuilder(): MainActivityComponent.Builder

    fun provideApplicationContext(): Context

    fun provideNotifier(): Notifier
}