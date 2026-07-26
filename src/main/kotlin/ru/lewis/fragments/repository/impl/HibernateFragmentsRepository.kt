package ru.lewis.fragments.repository.impl

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.hibernate.SessionFactory
import ru.lewis.fragments.model.UserEntity
import ru.lewis.fragments.repository.FragmentsRepository
import ru.lewis.fragments.service.DatabaseService
import java.util.UUID

@Singleton
class HibernateFragmentsRepository @Inject constructor(
    private val databaseService: DatabaseService
) : FragmentsRepository {
    private val sessionFactory: SessionFactory get() = databaseService.sessionFactory

    override fun findById(uuid: UUID): UserEntity? =
        sessionFactory.openSession().use { session ->
            session.find(UserEntity::class.java, uuid)
        }

    override fun save(entity: UserEntity) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.merge(entity)
            session.transaction.commit()
        }
    }

    override fun delete(entity: UserEntity) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.remove(entity)
            session.transaction.commit()
        }
    }

    override fun getTop(limit: Int): List<UserEntity> =
        sessionFactory.openSession().use { session ->
            session.createQuery(
                "FROM UserEntity ORDER BY count DESC",
                UserEntity::class.java
            )
                .setMaxResults(limit)
                .resultList
        }

    override fun findAll(): List<UserEntity> =
        sessionFactory.openSession().use { session ->
            val builder = session.criteriaBuilder
            val query = builder.createQuery(UserEntity::class.java)
            val root = query.from(UserEntity::class.java)
            query.select(root)
            session.createQuery(query).resultList
        }
}