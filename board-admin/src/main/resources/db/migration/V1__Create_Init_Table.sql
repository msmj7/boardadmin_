-- user 테이블
CREATE TABLE user (
    user_index INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);
-- role 테이블
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(20) NOT NULL UNIQUE
);

-- user_role 테이블
CREATE TABLE user_role (
	ur_index INT AUTO_INCREMENT PRIMARY KEY,
    user_index INT NOT NULL,
    role_id INT NOT NULL,
    UNIQUE (user_index, role_id)
);

--board 테이블
CREATE TABLE board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    writer VARCHAR(255) NOT NULL,
    created_date TIMESTAMP,
    modified_date TIMESTAMP
);