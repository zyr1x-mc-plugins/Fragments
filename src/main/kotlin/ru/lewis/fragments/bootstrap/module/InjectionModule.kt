package ru.lewis.fragments.bootstrap.module

import com.google.inject.AbstractModule
import com.google.inject.Provides
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.Plugin
import ru.lewis.fragments.api.FragmentEventController
import ru.lewis.fragments.api.FragmentPurchaseController
import ru.lewis.fragments.api.FragmentsApi
import ru.lewis.fragments.api.FragmentsEconomy
import ru.lewis.fragments.api.impl.FragmentEventControllerImpl
import ru.lewis.fragments.api.impl.FragmentPurchaseControllerImpl
import ru.lewis.fragments.api.impl.FragmentsApiImpl
import ru.lewis.fragments.model.FragmentsEconomyImpl
import ru.lewis.point.api.PointAPI

class InjectionModule(
    private val plugin: Plugin
) : AbstractModule() {

    override fun configure() {
        bind(FragmentsEconomy::class.java).to(FragmentsEconomyImpl::class.java)
        bind(FragmentEventController::class.java).to(FragmentEventControllerImpl::class.java)
        bind(FragmentPurchaseController::class.java).to(FragmentPurchaseControllerImpl::class.java)
        bind(FragmentsApi::class.java).to(FragmentsApiImpl::class.java)
    }

    @Provides
    fun providePlugin(): Plugin = plugin

    @Provides
    fun MiniMessage(): MiniMessage = MiniMessage.miniMessage()

    @Provides
    fun pointAPI(): PointAPI = PointAPI.get()
}