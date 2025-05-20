-- 초기 사용자 데이터 삽입
INSERT INTO users (name, email, password, created_at, updated_at, is_deleted)
VALUES ('김티드', 'kimted@wantedlab.com', '$2a$10$T36keW5PPiwSwCprd7D5EetPn9rh3yTPEBiDOjU9HkbBFeM/YNCXu', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        FALSE),
       ('지티드', 'jited@wantedlab.com', '$2a$10$UUCxFQtTS3Hqz5YnKH7creH3czQqRvSHP28KQyXHaCLtw0gvAUz8a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('이티드', 'leeted@wantedlab.com', '$2a$10$5RyNoJCrYxHcYP9.173AQORm566LTwHTZyJqXFVX7i3q2k/P2HiwS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 초기 LLM 데이터 삽입
INSERT INTO llm (name, price_per_token, created_at, updated_at, is_deleted)
VALUES ('gpt-4o-mini', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('gpt-4o', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('gpt-3.5-turbo', 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 초기 LLM 사용량 데이터 삽입
INSERT INTO llm_usage (user_id, llm_id, used_token, created_at, updated_at, is_deleted)
VALUES (1, 1, 512, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       (1, 2, 512, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       (1, 3, 512, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       (2, 1, 256, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       (3, 2, 1024, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
