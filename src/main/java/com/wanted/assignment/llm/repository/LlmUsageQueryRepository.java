package com.wanted.assignment.llm.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.assignment.common.constants.Constants;
import com.wanted.assignment.domain.entity.QLlmEntity;
import com.wanted.assignment.domain.entity.QLlmUsageEntity;
import com.wanted.assignment.llm.dto.LlmUsageDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LlmUsageQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  /**
   * LLM 아이디를 기준으로 삭제(isDeleted)
   *
   * @param llmId 삭제할 LLM 아이디
   * @return LLM 삭제 여부
   */
  public boolean deleteLlmUsageByLlmId(Long llmId) {
    QLlmUsageEntity llmUsageEntity = QLlmUsageEntity.llmUsageEntity;
    return jpaQueryFactory
        .update(llmUsageEntity)
        .set(llmUsageEntity.isDeleted, true)
        .set(llmUsageEntity.updatedAt, LocalDateTime.now())
        .where(llmUsageEntity.llmId.eq(llmId)
            .and(llmUsageEntity.isDeleted.eq(false)))
        .execute() > 0;
  }

  /**
   * LLM별 사용량 조회
   *
   * @return LLM별 사용량 정보
   */
  public List<LlmUsageDto.StatsResponse> selectLlmUsageStats() {

    QLlmUsageEntity llmUsageEntity = QLlmUsageEntity.llmUsageEntity;
    QLlmEntity llmEntity = QLlmEntity.llmEntity;

    return jpaQueryFactory
        .select(
            Projections.fields(
                LlmUsageDto.StatsResponse.class,
                llmEntity.id,
                llmEntity.name,
                Expressions.asNumber(
                        llmUsageEntity.usedToken.sum())
                    .castToNum(Long.class).as("totalUsedToken"),
                Expressions.asNumber(
                        llmUsageEntity.usedToken.sum().multiply(llmEntity.pricePerToken))
                    .castToNum(Long.class).as("totalPrice"))
        )
        .from(llmEntity)
        .leftJoin(llmUsageEntity).on(llmEntity.id.eq(llmUsageEntity.llmId)
            .and(llmUsageEntity.isDeleted.eq(false)))
        .where(llmEntity.isDeleted.eq(false))
        .groupBy(llmEntity.id, llmEntity.name)
        .setHint(Constants.HIBERNATE_SQL_COMMENT, "LlmUsageQueryRepository.selectLlmUsageStats")
        .fetch();
  }
}
