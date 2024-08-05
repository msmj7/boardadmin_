CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_index INT NOT NULL,
    post_id BIGINT NOT NULL,
    liked BOOLEAN NOT NULL,
    disliked BOOLEAN NOT NULL
);
