package com.albabot.model;

public enum Role {
    SEEKER,    // 💡 JOBSEEKER를 데이터베이스와 연동되도록 SEEKER로 변경합니다.
    EMPLOYER,
    ADMIN      // DDL에 명시된 관리자 권한까지 미리 추가해두면 안전합니다.
}