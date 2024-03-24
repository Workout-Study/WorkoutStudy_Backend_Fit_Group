package com.fitmate.fitgroupservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fitmate.fitgroupservice.common.GlobalURI
import com.fitmate.fitgroupservice.dto.mate.*
import com.fitmate.fitgroupservice.persistence.entity.BankCode
import com.fitmate.fitgroupservice.persistence.entity.FitGroup
import com.fitmate.fitgroupservice.persistence.entity.FitLeader
import com.fitmate.fitgroupservice.persistence.entity.FitMate
import com.fitmate.fitgroupservice.persistence.repository.BankCodeRepository
import com.fitmate.fitgroupservice.persistence.repository.FitGroupRepository
import com.fitmate.fitgroupservice.persistence.repository.FitLeaderRepository
import com.fitmate.fitgroupservice.persistence.repository.FitMateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FitMateControllerBootTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var fitGroupRepository: FitGroupRepository

    @Autowired
    private lateinit var fitMateRepository: FitMateRepository

    @Autowired
    private lateinit var fitLeaderRepository: FitLeaderRepository

    @Autowired
    private lateinit var bankCodeRepository: BankCodeRepository

    private val requestUserId = "testUserId"
    private val fitGroupName = "헬창들은 일주일에 7번 운동해야죠 스터디"
    private val penaltyAmount = 5000
    private val penaltyAccountBankCode = "090"
    private val penaltyAccount = "3333-03-5367420"
    private val category = 1
    private val introduction = "헬창들은 일주일에 7번은 운동해야한다고 생각합니다 당신도 헬창이 됩시다 근육 휴식따윈 생각도 마십쇼"
    private val cycle = null
    private val frequency = 7
    private val maxFitMate = 20

    private lateinit var bankCode: BankCode
    private lateinit var fitGroup: FitGroup

    @BeforeEach
    fun createTestFitGroup() {
        bankCode = bankCodeRepository.findByCode(penaltyAccountBankCode).get()

        val fitGroup = FitGroup(
            fitGroupName, penaltyAmount, bankCode, penaltyAccount, category, introduction, cycle
                ?: 1, frequency, maxFitMate, "test"
        )

        val savedFitGroup = fitGroupRepository.save(fitGroup)

        val fitLeader = FitLeader(savedFitGroup, requestUserId, "test")

        fitLeaderRepository.save(fitLeader)

        this.fitGroup = savedFitGroup

        for (i in 0..3) {
            fitMateRepository.save(FitMate(fitGroup, requestUserId + i, requestUserId + i))
        }
    }

    @Test
    @DisplayName("[통합][Controller] Fit mate 등록 - 성공 테스트")
    @Throws(Exception::class)
    fun `register fit mate controller success test`() {
        //given
        val registerFitMateRequest = RegisterMateRequest("newTestUserId", fitGroup.id!!)

        //when
        val resultActions = mockMvc.perform(
            post(GlobalURI.MATE_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerFitMateRequest))
                .accept(MediaType.APPLICATION_JSON)
        )
        //then
        resultActions.andExpect(status().isCreated())
            .andDo(print())
    }

    @Test
    @DisplayName("[통합][Controller] Fit mate 목록 조회 - 성공 테스트")
    @Throws(Exception::class)
    fun `get fit mate list controller success test`() {
        //given when
        val resultActions = mockMvc.perform(
            get(GlobalURI.MATE_ROOT + GlobalURI.PATH_VARIABLE_FIT_GROUP_ID_WITH_BRACE, fitGroup.id!!)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
        //then
        resultActions.andExpect(status().isOk())
            .andDo(print())
    }

    @Test
    @DisplayName("[통합][Controller] Fit mate 탈퇴 - 성공 테스트")
    @Throws(Exception::class)
    fun `delete fit mate controller success test`() {
        //given
        fitMateRepository.save(FitMate(fitGroup, requestUserId, requestUserId))

        val deleteMateRequest = DeleteMateRequest(requestUserId)
        //when
        val resultActions = mockMvc.perform(
            delete(GlobalURI.MATE_ROOT + GlobalURI.PATH_VARIABLE_FIT_GROUP_ID_WITH_BRACE, fitGroup.id!!)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteMateRequest))
                .accept(MediaType.APPLICATION_JSON)
        )
        //then
        resultActions.andExpect(status().isOk())
            .andDo(print())
    }
}