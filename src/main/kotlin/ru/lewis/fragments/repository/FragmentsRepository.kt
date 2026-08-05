package ru.lewis.fragments.repository

import com.google.inject.ImplementedBy
import ru.lewis.fragments.model.FragmentsUserEntity
import ru.lewis.fragments.repository.impl.HibernateFragmentsRepository
import java.util.UUID

@ImplementedBy(HibernateFragmentsRepository::class)
interface FragmentsRepository {

    fun findById(uuid: UUID): FragmentsUserEntity?

    fun save(entity: FragmentsUserEntity)

    fun delete(entity: FragmentsUserEntity)

    fun getTop(limit: Int): List<FragmentsUserEntity>

    fun findAll(): List<FragmentsUserEntity>
}