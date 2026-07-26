package ru.lewis.fragments.model.event

import jakarta.inject.Singleton
import java.time.Duration
import java.time.Instant

@Singleton
class EventTimer {

    private var start: Instant? = null
    private var end: Instant? = null


    fun start(duration: Duration) {
        start = Instant.now()
        end = start!!.plus(duration)
    }


    fun stop() {
        start = null
        end = null
    }


    fun progress(): Float {
        val start = start ?: return 0f
        val end = end ?: return 0f

        val total = Duration.between(start, end).toMillis()
        val remaining = Duration.between(
            Instant.now(),
            end
        ).toMillis()

        if (total <= 0) {
            return 0f
        }

        return (remaining.toFloat() / total)
            .coerceIn(0f, 1f)
    }


    fun remaining(): Duration {
        val end = end ?: return Duration.ZERO

        return Duration.between(
            Instant.now(),
            end
        ).coerceAtLeast(Duration.ZERO)
    }
}