package ru.lewis.fragments.repository

import com.google.inject.ImplementedBy
import ru.lewis.fragments.model.UserEntity
import ru.lewis.fragments.repository.impl.HibernateFragmentsRepository
import java.util.UUID

@ImplementedBy(HibernateFragmentsRepository::class)
interface FragmentsRepository {

    fun findById(uuid: UUID): UserEntity?

    fun save(entity: UserEntity)

    fun delete(entity: UserEntity)

    fun getTop(limit: Int): List<UserEntity>

    fun findAll(): List<UserEntity>
}