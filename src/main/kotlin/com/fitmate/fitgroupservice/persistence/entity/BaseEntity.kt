package com.fitmate.fitgroupservice.persistence.entity

import com.fitmate.fitgroupservice.common.GlobalStatus
import com.fitmate.fitgroupservice.persistence.converter.BooleanNumberConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
open class BaseEntity(
    @Convert(converter = BooleanNumberConverter::class)
    var state: Boolean,
    @CreatedDate
    @Column(updatable = false)
    @Convert(converter = Jsr310JpaConverters.InstantConverter::class)
    var createdAt: Instant,
    @Column(nullable = false) var createUser: String,
    @LastModifiedDate
    @Convert(converter = Jsr310JpaConverters.InstantConverter::class)
    var updatedAt: Instant? = null,
    @Column
    var updateUser: String? = null
) {

    fun delete() {
        this.state = GlobalStatus.PERSISTENCE_DELETED
        this.updatedAt = Instant.now()
        this.updateUser = this.createUser
    }

    val isDeleted: Boolean
        get() = state
}