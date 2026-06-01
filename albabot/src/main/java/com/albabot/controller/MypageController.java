
package com.albabot.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
 
import com.albabot.model.Application;
import com.albabot.model.User;
import com.albabot.service.MypageService;
 
import jakarta.servlet.http.HttpSession;
 
@Controller
public class MypageController {
 
    @Autowired
    private MypageService mypageService;
 
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
 
        // 로그인 여부 확인 — 로그인 안 했으면 로그인 페이지로
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
 
        // 내 지원 현황
        List<Application> applications = mypageService.getMyApplications(loginUser.getUserId());
 
        // 내 선호 카테고리
        List<String> categories = mypageService.getMyCategories(loginUser.getUserId());
 
        // 화면에 데이터 전달
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("recentApplications", applications);
        model.addAttribute("categories", categories);
 
        return "mypage";  // templates/mypage.html 연결
    }
}
