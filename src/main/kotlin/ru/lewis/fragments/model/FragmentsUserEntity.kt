package ru.lewis.fragments.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "fragments_users")
open class FragmentsUserEntity(
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    open var uuid: UUID? = null,

    @Column(name = "count", nullable = false)
    open var count: Int = 0
)