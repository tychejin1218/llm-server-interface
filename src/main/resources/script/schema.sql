DROP TABLE IF EXISTS llm_usage;
DROP TABLE IF EXISTS llm;
DROP TABLE IF EXISTS users;

-- 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(16) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);
COMMENT ON TABLE users IS '사용자 정보를 저장하는 테이블';
COMMENT ON COLUMN users.id IS '사용자 아아디 (자동 증가)';
COMMENT ON COLUMN users.name IS '사용자 이름 (최대 16자)';
COMMENT ON COLUMN users.email IS '사용자 이메일 주소 (고유값)';
COMMENT ON COLUMN users.password IS '사용자 비밀번호 (암호화)';
COMMENT ON COLUMN users.created_at IS '사용자 추가 시각';
COMMENT ON COLUMN users.updated_at IS '사용자 수정 시각';
COMMENT ON COLUMN users.is_deleted IS '사용자 삭제 여부';

-- LLM 테이블
CREATE TABLE llm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    price_per_token INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);
COMMENT ON TABLE llm IS 'LLM 정보를 저장하는 테이블';
COMMENT ON COLUMN llm.id IS 'LLM 고유 아이디 (자동 증가)';
COMMENT ON COLUMN llm.name IS 'LLM 이름 (최대 20자)';
COMMENT ON COLUMN llm.price_per_token IS '해당 LLM의 토큰당 가격';
COMMENT ON COLUMN llm.created_at IS 'LLM 추가 시각';
COMMENT ON COLUMN llm.updated_at IS 'LLM 수정 시각';
COMMENT ON COLUMN llm.is_deleted IS 'LLM 삭제 여부';

-- LLM 사용량 테이블
CREATE TABLE llm_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    llm_id BIGINT NOT NULL,
    used_token INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (llm_id) REFERENCES llm(id) ON DELETE CASCADE
);
COMMENT ON TABLE llm_usage IS 'LLM 사용량 정보를 저장하는 테이블';
COMMENT ON COLUMN llm_usage.id IS 'LLM 사용량 아이디 (자동 증가)';
COMMENT ON COLUMN llm_usage.user_id IS '사용자 ID';
COMMENT ON COLUMN llm_usage.llm_id IS 'LLM ID';
COMMENT ON COLUMN llm_usage.used_token IS '사용 토큰 수';
COMMENT ON COLUMN llm_usage.created_at IS 'LLM 사용량 추가 시각';
COMMENT ON COLUMN llm_usage.updated_at IS 'LLM 사용량 수정 시각';
COMMENT ON COLUMN llm_usage.is_deleted IS 'LLM 사용량 삭제 여부';
