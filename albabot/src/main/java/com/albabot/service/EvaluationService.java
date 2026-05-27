package com.albabot.service;

import org.springframework.stereotype.Service;
import com.albabot.dao.EvaluationDao;
import com.albabot.model.Evaluation;

@Service
public class EvaluationService {

    private final EvaluationDao evaluationDao;

    // 생성자 주입
    public EvaluationService(EvaluationDao evaluationDao) {
        this.evaluationDao = evaluationDao;
    }

    public void registerEvaluation(Evaluation eval) {
        // 1. 별점 필수 유효성 검사 (1점 ~ 5점 사이)
        if (eval.getScore() < 1 || eval.getScore() > 5) {
            throw new IllegalArgumentException("평점은 1점부터 5점 사이여야 합니다.");
        }
        
        // 2. 구직자가 사장님 공고에 쓰는 것이므로 기본 evalType을 'SEEKER_TO_EMPLOYER'로 설정
        if (eval.getEvalType() == null) {
            eval.setEvalType("SEEKER_TO_EMPLOYER");
        }

        // 3. DAO 호출하여 최종 DB 저장
        evaluationDao.insertEvaluation(eval);
    }
}