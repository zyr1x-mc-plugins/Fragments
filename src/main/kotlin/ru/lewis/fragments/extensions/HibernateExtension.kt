package ru.lewis.fragments.extensions

import org.hibernate.Session
import org.hibernate.SessionFactory

/**
 * Выполняет транзакцию и возвращает результат
 */
inline fun <T> SessionFactory.fromTransaction(crossinline block: (Session) -> T): T {
    return openSession().use { session ->
        val transaction = session.beginTransaction()
        try {
            val result = block(session)
            transaction.commit()
            result
        } catch (e: Exception) {
            transaction.rollback()
            throw e
        }
    }
}

/**
 * Выполняет транзакцию без возврата результата
 */
inline fun SessionFactory.inTransaction(crossinline block: (Session) -> Unit) {
    openSession().use { session ->
        val transaction = session.beginTransaction()
        try {
            block(session)
            transaction.commit()
        } catch (e: Exception) {
            transaction.rollback()
            throw e
        }
    }
}