package com.albabot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.albabot.dao.MypageDao;
import com.albabot.model.Application;

@Service
public class MypageService {

    @Autowired
    private MypageDao mypageDao;

    // 내 지원 현황 조회
    public List<Application> getMyApplications(int userId) {
        return mypageDao.findApplicationsByUserId(userId);
    }

    // 내 선호 카테고리 조회
    public List<String> getMyCategories(int userId) {
        return mypageDao.findCategoriesByUserId(userId);
    }
}
