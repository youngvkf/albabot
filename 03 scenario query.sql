-- ============================================================
-- AlbaBot — 주요 기능 동작 시나리오 + 쿼리 설계
-- ============================================================
USE albabot;

-- ────────────────────────────────────────────────────────────
-- [시나리오 1] 로그인
-- 사용자가 이메일/비밀번호 입력 → DB 조회 → 일치 시 로그인 성공
-- ────────────────────────────────────────────────────────────
SELECT user_id, name, role, region
FROM users
WHERE email = 'hwain@example.com'
  AND password_hash = SHA2('pass1234', 256)
  AND is_active = 1;

-- ────────────────────────────────────────────────────────────
-- [시나리오 2] 게시물 목록 조회 (메인 페이지)
-- 최신순 정렬, 선호 카테고리 필터링, 차단 공고 제외
-- ────────────────────────────────────────────────────────────
SELECT j.job_id, j.title, j.category, j.hourly_wage, j.location, j.deadline
FROM jobs j
WHERE j.status = 'OPEN'
  AND j.deadline >= NOW()
  AND j.category IN (
      SELECT category FROM user_categories WHERE user_id = 1)
  AND j.job_id NOT IN (
      SELECT blocked_job_id FROM blocks
      WHERE blocker_id = 1 AND blocked_job_id IS NOT NULL)
  AND j.employer_id NOT IN (
      SELECT blocked_user_id FROM blocks
      WHERE blocker_id = 1 AND blocked_user_id IS NOT NULL)
ORDER BY j.created_at DESC;

-- ────────────────────────────────────────────────────────────
-- [시나리오 3] 게시물 상세 조회
-- ────────────────────────────────────────────────────────────
SELECT j.*, u.name AS employer_name
FROM jobs j
JOIN users u ON j.employer_id = u.user_id
WHERE j.job_id = 1;

-- ────────────────────────────────────────────────────────────
-- [시나리오 4] 게시물 생성 (트랜잭션)
-- ────────────────────────────────────────────────────────────
START TRANSACTION;
INSERT INTO jobs (employer_id, title, category, hourly_wage, location, work_hours, deadline, description)
VALUES (4, '신규 카페 오전 알바', '카페/음료', 10300, '서울 강남구 삼성동', '평일 08:00~13:00', '2026-07-01', '오전 시간 가능하신 분');
COMMIT;

-- ────────────────────────────────────────────────────────────
-- [시나리오 5] 업무 지원 (중복 지원 방지 + 트랜잭션)
-- ────────────────────────────────────────────────────────────
-- 중복 지원 여부 확인
SELECT COUNT(*) AS cnt
FROM applications
WHERE user_id = 1 AND job_id = 2;

-- 지원 등록 + 알림 생성 (트랜잭션)
START TRANSACTION;
INSERT INTO applications (user_id, job_id, cover_letter)
VALUES (1, 2, '평일 오후 가능합니다. 카페 경험 있습니다.');

INSERT INTO notifications (user_id, type, message)
SELECT j.employer_id, 'APPLICATION_RESULT',
       CONCAT((SELECT name FROM users WHERE user_id = 1), '님이 [', j.title, '] 공고에 지원했습니다.')
FROM jobs j WHERE j.job_id = 2;
COMMIT;

-- ────────────────────────────────────────────────────────────
-- [시나리오 6] 합격 처리 + 지원자 알림 (트랜잭션)
-- ────────────────────────────────────────────────────────────
START TRANSACTION;
UPDATE applications SET status = 'ACCEPTED' WHERE application_id = 2;

INSERT INTO notifications (user_id, type, message)
SELECT a.user_id, 'APPLICATION_RESULT',
       CONCAT('[', j.title, '] 공고에 합격하셨습니다!')
FROM applications a
JOIN jobs j ON a.job_id = j.job_id
WHERE a.application_id = 2;
COMMIT;

-- ────────────────────────────────────────────────────────────
-- [시나리오 7] 내 지원 현황 조회 (3테이블 JOIN)
-- ────────────────────────────────────────────────────────────
SELECT a.application_id,
       j.title        AS 공고명,
       j.hourly_wage  AS 시급,
       j.location     AS 위치,
       a.status       AS 지원상태,
       a.applied_at   AS 지원일시
FROM applications a
JOIN jobs  j ON a.job_id  = j.job_id
JOIN users u ON a.user_id = u.user_id
WHERE a.user_id = 1
ORDER BY a.applied_at DESC;

-- ────────────────────────────────────────────────────────────
-- [시나리오 8] 업무 리뷰 별점 작성
-- ────────────────────────────────────────────────────────────
-- 작성 권한 확인 (해당 게시글 user_id와 일치하면 작성 불가)
SELECT employer_id FROM jobs WHERE job_id = 1;

INSERT INTO evaluations (reviewer_id, reviewee_id, job_id, score, comment, eval_type)
VALUES (1, 4, 1, 5, '근무 환경이 좋고 사장님이 친절합니다.', 'SEEKER_TO_EMPLOYER');

-- ────────────────────────────────────────────────────────────
-- [시나리오 9] 부적절 공고 신고 + 사용자 차단
-- ────────────────────────────────────────────────────────────
INSERT INTO reports (reporter_id, target_job_id, reason)
VALUES (1, 3, '허위 시급 정보가 포함된 공고입니다.');

INSERT INTO blocks (blocker_id, blocked_user_id)
VALUES (1, 5);

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

-- ────────────────────────────────────────────────────────────
-- [시나리오 11] 개인화 추천 점수 계산 및 저장
-- 카테고리 일치(40) + 지역 근접(30) + 시급 기준(20) + 신규(10)
-- ────────────────────────────────────────────────────────────
INSERT INTO recommendations (user_id, job_id, score, reason)
SELECT 1, j.job_id,
    (CASE WHEN j.category IN (
         SELECT category FROM user_categories WHERE user_id = 1)
         THEN 40 ELSE 0 END
    + CASE WHEN j.location LIKE CONCAT(
         (SELECT SUBSTRING_INDEX(region,' ',1) FROM users WHERE user_id=1),'%')
         THEN 30 ELSE 0 END
    + CASE WHEN j.hourly_wage >= 10000 THEN 20 ELSE 10 END
    + CASE WHEN j.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 10 ELSE 0 END
    ) AS score,
    '선호 카테고리·지역·시급 기반 추천' AS reason
FROM jobs j
WHERE j.status = 'OPEN'
  AND j.deadline >= NOW()
  AND j.job_id NOT IN (SELECT job_id FROM applications WHERE user_id = 1)
ORDER BY score DESC
LIMIT 10;
