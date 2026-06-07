-- ============================================================
-- AlbaBot — 주요 기능 동작 시나리오 + 쿼리 설계
-- ============================================================
USE albabot;

-- ────────────────────────────────────────────────────────────
-- [시나리오 1] 로그인
-- UserDao.findByEmail → Java BCrypt.matches()로 비밀번호 검증
-- ────────────────────────────────────────────────────────────
SELECT user_id, name, role, region, password_hash,
       preferred_time, is_active
FROM users
WHERE email = 'hwain@example.com';

-- ────────────────────────────────────────────────────────────
-- [시나리오 2] 전체 공고 목록 조회
-- JobDao.getAllJobs
-- ────────────────────────────────────────────────────────────
SELECT *
FROM jobs
ORDER BY job_id ASC;

-- ────────────────────────────────────────────────────────────
-- [시나리오 3] 카테고리 필터링 조회 (서브쿼리)
-- JobService.showJobsByCategory + user_categories 활용
-- ────────────────────────────────────────────────────────────
SELECT *
FROM jobs
WHERE status = 'OPEN'
  AND category IN (
      SELECT category FROM user_categories WHERE user_id = 1)
ORDER BY job_id ASC;

-- ────────────────────────────────────────────────────────────
-- [시나리오 4] 공고 상세 조회 (JOIN)
-- JobDao.getJobById + users JOIN
-- ────────────────────────────────────────────────────────────
SELECT j.*, u.name AS employer_name
FROM jobs j
JOIN users u ON j.employer_id = u.user_id
WHERE j.job_id = 1;

-- ────────────────────────────────────────────────────────────
-- [시나리오 5] 공고 등록 (INSERT)
-- JobDao.insertJob
-- ────────────────────────────────────────────────────────────
INSERT INTO jobs (employer_id, title, category, hourly_wage, location, work_hours, deadline, description, status)
VALUES (4, '신규 카페 오전 알바', '카페/음료', 10300, '서울 강남구 삼성동', '평일 08:00~13:00', '2026-07-01', '오전 시간 가능하신 분', 'OPEN');

-- ────────────────────────────────────────────────────────────
-- [시나리오 6] 중복 지원 확인 + 지원 등록
-- ApplicationDao.hasApplied + ApplicationDao.insertApplication
-- ────────────────────────────────────────────────────────────
-- 6-1. 중복 지원 여부 확인
SELECT COUNT(*) AS cnt
FROM applications
WHERE user_id = 1 AND job_id = 2;

-- 6-2. 지원 등록 (UNIQUE KEY 제약으로 DB 레벨 중복 차단)
INSERT INTO applications (user_id, job_id, status, cover_letter)
VALUES (1, 2, 'PENDING', '평일 오후 가능합니다. 카페 경험 있습니다.');

-- ────────────────────────────────────────────────────────────
-- [시나리오 7] 내 지원 현황 조회 (마이페이지)
-- MypageDao.findApplicationsByUserId
-- ────────────────────────────────────────────────────────────
SELECT a.application_id, a.user_id, a.job_id,
       a.applied_at, a.status, a.cover_letter,
       j.title AS job_title
FROM applications a
JOIN jobs j ON a.job_id = j.job_id
WHERE a.user_id = 1
ORDER BY a.applied_at DESC;

-- ────────────────────────────────────────────────────────────
-- [시나리오 8] 내 선호 카테고리 조회 (마이페이지)
-- MypageDao.findCategoriesByUserId
-- ────────────────────────────────────────────────────────────
SELECT category
FROM user_categories
WHERE user_id = 1;

-- ────────────────────────────────────────────────────────────
-- [시나리오 9] 리뷰 등록 + 공고별 리뷰 목록 조회
-- EvaluationDao.insertEvaluation + EvaluationDao.getEvaluationsByJobId
-- ────────────────────────────────────────────────────────────
-- 9-1. 본인 공고 여부 확인 (본인 공고엔 리뷰 불가)
SELECT employer_id FROM jobs WHERE job_id = 1;

-- 9-2. 리뷰 등록
INSERT INTO evaluations (reviewer_id, reviewee_id, job_id, score, comment, eval_type)
VALUES (1, 4, 1, 5, '근무 환경이 좋고 사장님이 친절합니다.', 'SEEKER_TO_EMPLOYER');

-- 9-3. 공고별 리뷰 목록 조회 (JOIN)
SELECT e.*, u.name AS reviewer_name
FROM evaluations e
JOIN users u ON e.reviewer_id = u.user_id
WHERE e.job_id = 1
ORDER BY e.created_at DESC;

-- ────────────────────────────────────────────────────────────
-- [시나리오 10] 카테고리별 평균 시급 통계 (집계 함수)
-- ────────────────────────────────────────────────────────────
SELECT category,
       COUNT(*)                   AS 공고수,
       ROUND(AVG(hourly_wage), 0) AS 평균시급,
       MAX(hourly_wage)           AS 최고시급
FROM jobs
WHERE status = 'OPEN'
GROUP BY category
ORDER BY 평균시급 DESC;
