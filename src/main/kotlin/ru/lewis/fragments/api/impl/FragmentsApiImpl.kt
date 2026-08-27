package ru.lewis.fragments.api.impl

import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.lewis.fragments.api.FragmentEventController
import ru.lewis.fragments.api.FragmentPurchaseController
import ru.lewis.fragments.api.FragmentsApi
import ru.lewis.fragments.api.FragmentsEconomy

@Singleton
class FragmentsApiImpl @Inject constructor(
    override val economy: FragmentsEconomy,
    override val event: FragmentEventController,
    override val purchases: FragmentPurchaseController,
) : FragmentsApi
