package com.wanted.assignment.users.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.assignment.common.constants.Constants;
import com.wanted.assignment.domain.entity.QLlmEntity;
import com.wanted.assignment.domain.entity.QLlmUsageEntity;
import com.wanted.assignment.domain.entity.QUsersEntity;
import com.wanted.assignment.users.dto.UsersDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UsersQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  /**
   * 사용자 목록 조회
   *
   * @param selectRequest 조회할 사용자 정보
   * @return 사용자 목록
   */
  public List<UsersDto.SelectResponse> selectUserList(UsersDto.SelectRequest selectRequest) {

    QUsersEntity usersEntity = QUsersEntity.usersEntity;

    BooleanBuilder builder = new BooleanBuilder();
    if (StringUtils.hasText(selectRequest.getName())) {
      builder.and(usersEntity.name.contains(selectRequest.getName()));
    }
    if (StringUtils.hasText(selectRequest.getEmail())) {
      builder.and(usersEntity.email.contains(selectRequest.getEmail()));
    }
    builder.and(usersEntity.isDeleted.eq(false));

    return jpaQueryFactory
        .select(
            Projections.fields(
                UsersDto.SelectResponse.class,
                usersEntity.id,
                usersEntity.name,
                usersEntity.email
            )
        )
        .from(usersEntity)
        .where(builder)
        .orderBy(usersEntity.id.asc())
        .setHint(Constants.HIBERNATE_SQL_COMMENT, "UsersQueryRepository.selectUserList")
        .fetch();
  }

  /**
   * 사용자 아이디를 기준으로 삭제(isDeleted)
   *
   * @param userId 삭제할 사용자 아이디
   * @return 사용자 삭제 여부
   */
  public boolean deleteUserById(Long userId) {
    QUsersEntity usersEntity = QUsersEntity.usersEntity;
    return jpaQueryFactory
        .update(usersEntity)
        .set(usersEntity.isDeleted, true)
        .set(usersEntity.updatedAt, LocalDateTime.now())
        .where(usersEntity.id.eq(userId)
            .and(usersEntity.isDeleted.eq(false)))
        .execute() > 0;
  }

  /**
   * 특정 사용자별 LLM 사용량 조회
   *
   * @param userId 사용자 아이디
   * @return 특정 사용자별 LLM 사용량 정보 목록
   */
  public List<UsersDto.LlmUsage> selectUserLlmUsage(Long userId) {
    QLlmEntity llmEntity = QLlmEntity.llmEntity;
    QLlmUsageEntity llmUsageEntity = QLlmUsageEntity.llmUsageEntity;
    return jpaQueryFactory
        .select(
            Projections.fields(
                UsersDto.LlmUsage.class,
                llmEntity.id,
                llmEntity.name,
                Expressions.asNumber(
                        llmUsageEntity.usedToken.sum())
                    .castToNum(Long.class).as("totalUsedToken"),
                Expressions.asNumber(
                        llmEntity.pricePerToken.multiply(llmUsageEntity.usedToken.sum()))
                    .castToNum(Long.class).as("totalPrice")
            ))
        .from(llmUsageEntity)
        .join(llmEntity).on(llmEntity.id.eq(llmUsageEntity.llmId))
        .where(llmUsageEntity.userId.eq(userId)
            .and(llmUsageEntity.isDeleted.eq(false))
            .and(llmEntity.isDeleted.eq(false)))
        .groupBy(llmEntity.id, llmEntity.name, llmEntity.pricePerToken)
        .setHint(Constants.HIBERNATE_SQL_COMMENT, "UsersQueryRepository.selectUserLlmUsage")
        .fetch();
  }
}
