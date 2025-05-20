package com.wanted.assignment.domain.repository;

import com.wanted.assignment.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends
    JpaRepository<UsersEntity, Long> {

  /**
   * 이메일을 기준으로 사용자가 존재하는지 확인
   *
   * @param email 확인할 사용자 이메일
   * @return 사용자가 존재하면 true, 그렇지 않으면 false
   */
  boolean existsByEmail(String email);
}
