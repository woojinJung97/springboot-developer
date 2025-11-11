CREATE TABLE train_reservations (
    train_resv_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    train_no VARCHAR(20),
    train_grade VARCHAR(20),
    depart_station VARCHAR(50),
    arrive_station VARCHAR(50),
    dep_date DATETIME,
    arr_date DATETIME,
    price INT,
    promotion VARCHAR(50),
    reserv_state VARCHAR(20),
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE train_seats (
     seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
     train_resv_id BIGINT,
     seat_info VARCHAR(20),
     FOREIGN KEY (train_resv_id) REFERENCES train_reservations(train_resv_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    rate VARCHAR(20),
    role VARCHAR(20),
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS refresh_token (
     token_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 토큰 기본키
     user_id BIGINT NOT NULL,                     -- users 테이블의 id (FK 아님, 단순 참조)
     token VARCHAR(512) NOT NULL UNIQUE,          -- JWT Refresh Token 값
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 생성일시
);

