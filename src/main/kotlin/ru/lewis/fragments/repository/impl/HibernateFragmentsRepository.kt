package ru.lewis.fragments.repository.impl

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.hibernate.SessionFactory
import ru.lewis.fragments.model.FragmentsUserEntity
import ru.lewis.fragments.repository.FragmentsRepository
import ru.lewis.point.api.PointAPI
import java.util.UUID

@Singleton
class HibernateFragmentsRepository @Inject constructor(
    private val pointAPI: PointAPI
) : FragmentsRepository {
    private val sessionFactory: SessionFactory get() = pointAPI.databaseService.sessionFactory

    override fun findById(uuid: UUID): FragmentsUserEntity? =
        sessionFactory.openSession().use { session ->
            session.find(FragmentsUserEntity::class.java, uuid)
        }

    override fun save(entity: FragmentsUserEntity) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.merge(entity)
            session.transaction.commit()
        }
    }

    override fun delete(entity: FragmentsUserEntity) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.remove(entity)
            session.transaction.commit()
        }
    }

    override fun getTop(limit: Int): List<FragmentsUserEntity> =
        sessionFactory.openSession().use { session ->
            session.createQuery(
                "FROM ru.lewis.fragments.model.FragmentsUserEntity ORDER BY count DESC",
                FragmentsUserEntity::class.java
            )
                .setMaxResults(limit)
                .resultList
        }

    override fun findAll(): List<FragmentsUserEntity> =
        sessionFactory.openSession().use { session ->
            val builder = session.criteriaBuilder
            val query = builder.createQuery(FragmentsUserEntity::class.java)
            val root = query.from(FragmentsUserEntity::class.java)
            query.select(root)
            session.createQuery(query).resultList
        }
}