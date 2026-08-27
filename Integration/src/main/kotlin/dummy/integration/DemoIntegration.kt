package dummy.integration

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import ru.lewis.fragments.api.FragmentEventController
import ru.lewis.fragments.api.FragmentsApi
import ru.lewis.fragments.api.FragmentsEconomy
import ru.lewis.fragments.api.event.FragmentBalanceUpdateEvent

/**
 * Demo integration used to prove that a third-party plugin can connect to the Fragments
 * API via the Bukkit ServicesManager and that the public contracts compile.
 *
 * This class is NOT part of the Fragments plugin itself; it is only a compile-time
 * consumer of the API module.
 */
class DemoIntegration : Listener {
    fun loadApi(): FragmentsApi? = FragmentsApi.get()

    fun loadViaServicesManager(): FragmentsApi? =
        Bukkit.getServicesManager().load(FragmentsApi::class.java)

    fun showBalance(player: Player): Int {
        val economy: FragmentsEconomy? = loadApi()?.economy
        return economy?.getFragments(player) ?: -1
    }

    fun controlEvent(controller: FragmentEventController) {
        if (!controller.isRunning()) {
            controller.start()
        }
    }

    @EventHandler
    fun onBalance(event: FragmentBalanceUpdateEvent) {
        val delta = event.delta
        val uniqueId = event.uniqueId
        println("Player $uniqueId balance changed by $delta")
    }
}
