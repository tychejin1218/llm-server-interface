package com.wanted.assignment.llm.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.assignment.common.constants.Constants;
import com.wanted.assignment.domain.entity.QLlmEntity;
import com.wanted.assignment.llm.dto.LlmDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LlmQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  /**
   * LLM 목록 조회
   *
   * @param selectRequest 조회할 사용자 정보
   * @return 사용자 목록
   */
  public List<LlmDto.SelectResponse> selectLlmList(LlmDto.SelectRequest selectRequest) {

    QLlmEntity llmEntity = QLlmEntity.llmEntity;

    BooleanBuilder builder = new BooleanBuilder();
    if (StringUtils.hasText(selectRequest.getName())) {
      builder.and(llmEntity.name.contains(selectRequest.getName()));
    }
    builder.and(llmEntity.isDeleted.eq(false));

    return jpaQueryFactory
        .select(
            Projections.fields(
                LlmDto.SelectResponse.class,
                llmEntity.id,
                llmEntity.name,
                llmEntity.pricePerToken
            )
        )
        .from(llmEntity)
        .where(builder)
        .orderBy(llmEntity.id.asc())
        .setHint(Constants.HIBERNATE_SQL_COMMENT, "LlmQueryRepository.selectLlmList")
        .fetch();
  }

  /**
   * LLM 수정
   *
   * @param updateRequest 수정할 LLM 정보
   * @return LLM 수정 여부
   */
  public boolean updateLlm(LlmDto.UpdateRequest updateRequest) {
    QLlmEntity llmEntity = QLlmEntity.llmEntity;
    return jpaQueryFactory
        .update(llmEntity)
        .set(llmEntity.name, updateRequest.getName())
        .set(llmEntity.pricePerToken, updateRequest.getPricePerToken())
        .set(llmEntity.updatedAt, LocalDateTime.now())
        .where(llmEntity.id.eq(updateRequest.getId())
            .and(llmEntity.isDeleted.eq(false)))
        .execute() > 0;
  }

  /**
   * LLM 아이디를 기준으로 삭제(isDeleted)
   *
   * @param llmId 삭제할 LLM 아이디
   * @return LLM 삭제 여부
   */
  public boolean deleteLlmById(Long llmId) {
    QLlmEntity llmEntity = QLlmEntity.llmEntity;
    return jpaQueryFactory
        .update(llmEntity)
        .set(llmEntity.isDeleted, true)
        .set(llmEntity.updatedAt, LocalDateTime.now())
        .where(llmEntity.id.eq(llmId)
            .and(llmEntity.isDeleted.eq(false)))
        .execute() > 0;
  }
}
