-- ============================================================
-- AlbaBot — 더미 데이터
-- ============================================================
USE albabot;

-- users (구직자 3명, 고용주 2명)
INSERT INTO users (email, password_hash, name, phone, role, region, preferred_time) VALUES
('hwain@example.com',    SHA2('pass1234',256), '장화인',    '010-1111-2222', 'SEEKER',   '서울 강남구', '주말'),
('chulsoo@example.com',  SHA2('pass1234',256), '김철수',    '010-3333-4444', 'SEEKER',   '서울 마포구', '평일 오후'),
('younghee@example.com', SHA2('pass1234',256), '이영희',    '010-5555-6666', 'SEEKER',   '경기 성남시', '주말'),
('cafe_boss@example.com',SHA2('pass1234',256), '카페사장',  '010-7777-8888', 'EMPLOYER', '서울 강남구', NULL),
('cvs_boss@example.com', SHA2('pass1234',256), '편의점사장','010-9999-0000', 'EMPLOYER', '서울 마포구', NULL);

-- user_categories
INSERT INTO user_categories (user_id, category) VALUES
(1, '카페/음료'), (1, '서빙/홀'),
(2, '편의점'),   (2, '카페/음료'),
(3, '서빙/홀');

-- jobs
INSERT INTO jobs (employer_id, title, category, hourly_wage, location, work_hours, deadline, description) VALUES
(4, '강남 카페 주말 알바',   '카페/음료', 10500, '서울 강남구 역삼동',   '토일 09:00~18:00', '2026-06-15', '친절한 분 환영'),
(4, '강남 카페 평일 알바',   '카페/음료', 10000, '서울 강남구 역삼동',   '평일 13:00~18:00', '2026-06-30', '경력 우대'),
(5, '마포 편의점 야간 알바', '편의점',    11000, '서울 마포구 합정동',   '평일 22:00~06:00', '2026-06-20', '성실한 분'),
(5, '홍대 카페 서빙',        '카페/음료', 10200, '서울 마포구 홍대입구', '주말 10:00~18:00', '2026-06-25', '카페 경험자 우대');

-- applications
INSERT INTO applications (user_id, job_id, status, cover_letter) VALUES
(1, 1, 'ACCEPTED', '카페 알바 경험 6개월 있습니다.'),
(1, 4, 'PENDING',  '홍대 근처라 지원합니다.'),
(2, 3, 'REJECTED', '야간 가능합니다.'),
(3, 1, 'PENDING',  '주말 가능합니다.');

-- evaluations
INSERT INTO evaluations (reviewer_id, reviewee_id, job_id, score, comment, eval_type) VALUES
(4, 1, 1, 5, '매우 성실하고 친절합니다.', 'EMPLOYER_TO_SEEKER'),
(1, 4, 1, 4, '좋은 근무 환경이었습니다.', 'SEEKER_TO_EMPLOYER');

-- blocks
INSERT INTO blocks (blocker_id, blocked_user_id) VALUES (1, 5);

-- notifications
INSERT INTO notifications (user_id, type, message) VALUES
(4, 'APPLICATION_RESULT', '장화인님이 [강남 카페 주말 알바] 공고에 지원했습니다.'),
(1, 'APPLICATION_RESULT', '[강남 카페 주말 알바] 공고에 합격하셨습니다!');
