package com.fitmate.fitgroupservice.persistence.entity

import com.fitmate.fitgroupservice.common.GlobalStatus
import com.fitmate.fitgroupservice.dto.group.UpdateFitGroupRequest
import jakarta.persistence.*
import java.time.Instant

@Entity
class FitGroup(
    @Column(nullable = false) var fitGroupName: String,
    @Column(nullable = false) var penaltyAmount: Int,
    @Column(nullable = false)
    var category: Int,
    var introduction: String?,
    @Column(nullable = false)
    // cycle - ( 1:week, 2:month, 3:year )
    var cycle: Int,
    @Column(nullable = false) var frequency: Int,
    @Column(nullable = false) var maxFitMate: Int,
    createUser: String
) : BaseEntity(GlobalStatus.PERSISTENCE_NOT_DELETED, Instant.now(), createUser) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun update(updateFitGroupRequest: UpdateFitGroupRequest) {
        this.fitGroupName = updateFitGroupRequest.fitGroupName
        this.penaltyAmount = updateFitGroupRequest.penaltyAmount
        this.category = updateFitGroupRequest.category
        this.introduction = updateFitGroupRequest.introduction
        this.cycle = updateFitGroupRequest.cycle ?: 1
        this.frequency = updateFitGroupRequest.frequency
        this.maxFitMate = updateFitGroupRequest.maxFitMate
        this.updatedAt = Instant.now()
        this.updateUser = updateFitGroupRequest.requestUserId.toString()
    }
}