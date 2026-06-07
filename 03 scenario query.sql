-- ============================================================
-- AlbaBot — 주요 기능 동작 시나리오 + 쿼리 설계
-- ============================================================
USE albabot;

-- [시나리오 1] 로그인
-- 이메일 조회 → Java에서 BCrypt.matches()로 비밀번호 검증
SELECT user_id, name, role, region, password_hash
FROM users
WHERE email = 'hwain@example.com'
  AND is_active = 1;

-- [시나리오 2] 게시물 목록 조회 (메인 페이지)
-- 2-1. 전체 공고 조회
SELECT job_id, title, category, hourly_wage, location, work_hours, deadline, status
FROM jobs
WHERE status = 'OPEN'
  AND deadline >= NOW()
ORDER BY created_at DESC;

-- 2-2. 선호 카테고리 기반 필터링 (서브쿼리)
SELECT j.job_id, j.title, j.category, j.hourly_wage, j.location, j.work_hours, j.deadline
FROM jobs j
WHERE j.status = 'OPEN'
  AND j.deadline >= NOW()
  AND j.category IN (
      SELECT category FROM user_categories WHERE user_id = 1)
ORDER BY j.created_at DESC;

-- [시나리오 3] 게시물 상세 조회 (JOIN)
SELECT j.*, u.name AS employer_name
FROM jobs j
JOIN users u ON j.employer_id = u.user_id
WHERE j.job_id = 1;

-- [시나리오 4] 게시물 생성 (트랜잭션)
START TRANSACTION;
INSERT INTO jobs (employer_id, title, category, hourly_wage, location, work_hours, deadline, description)
VALUES (4, '신규 카페 오전 알바', '카페/음료', 10300, '서울 강남구 삼성동', '평일 08:00~13:00', '2026-07-01', '오전 시간 가능하신 분');
COMMIT;

-- [시나리오 5] 업무 지원 (중복 지원 방지)
-- UNIQUE KEY(user_id, job_id) 제약으로 DB 레벨 중복 차단
-- 5-1. 중복 지원 여부 확인
SELECT COUNT(*) AS cnt
FROM applications
WHERE user_id = 1 AND job_id = 2;

-- 5-2. 지원 등록 (트랜잭션)
START TRANSACTION;
INSERT INTO applications (user_id, job_id, cover_letter)
VALUES (1, 2, '평일 오후 가능합니다. 카페 경험 있습니다.');
COMMIT;

-- [시나리오 6] 합격 처리 (트랜잭션)
START TRANSACTION;
UPDATE applications
SET status = 'ACCEPTED'
WHERE application_id = 2;
COMMIT;

-- 합격 여부 확인
SELECT application_id, user_id, job_id, status
FROM applications
WHERE application_id = 2;

-- [시나리오 7] 내 지원 현황 조회 (3테이블 JOIN)
SELECT a.application_id,
       j.title       AS 공고명,
       j.hourly_wage AS 시급,
       j.location    AS 위치,
       a.status      AS 지원상태,
       a.applied_at  AS 지원일시
FROM applications a
JOIN jobs  j ON a.job_id  = j.job_id
JOIN users u ON a.user_id = u.user_id
WHERE a.user_id = 1
ORDER BY a.applied_at DESC;

-- [시나리오 8] 리뷰 등록 & 공고별 리뷰 목록 조회
-- 8-1. 본인 공고 여부 확인
SELECT employer_id FROM jobs WHERE job_id = 1;

-- 8-2. 리뷰 등록
INSERT INTO evaluations (reviewer_id, reviewee_id, job_id, score, comment, eval_type)
VALUES (1, 4, 1, 5, '근무 환경이 좋고 사장님이 친절합니다.', 'SEEKER_TO_EMPLOYER');

-- 8-3. 공고별 리뷰 목록 조회 (JOIN)
SELECT e.score, e.comment, u.name AS reviewer_name, e.created_at
FROM evaluations e
JOIN users u ON e.reviewer_id = u.user_id
WHERE e.job_id = 1
ORDER BY e.created_at DESC;

-- [시나리오 9] 지원자별 현황 조회 (고용주 시점)
SELECT a.application_id,
       u.name         AS 지원자명,
       u.phone        AS 연락처,
       a.cover_letter AS 자기소개,
       a.status       AS 지원상태,
       a.applied_at   AS 지원일시
FROM applications a
JOIN users u ON a.user_id = u.user_id
WHERE a.job_id = 1
ORDER BY a.applied_at ASC;

-- [시나리오 10] 카테고리별 평균 시급 통계 (집계 함수)
SELECT category,
       COUNT(*)                   AS 공고수,
       ROUND(AVG(hourly_wage), 0) AS 평균시급,
       MAX(hourly_wage)           AS 최고시급
FROM jobs
WHERE status = 'OPEN'
GROUP BY category
ORDER BY 평균시급 DESC;
