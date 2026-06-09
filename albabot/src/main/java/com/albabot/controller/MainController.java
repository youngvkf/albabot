package com.albabot.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.albabot.dao.JobDao;
import com.albabot.dao.EvaluationDao;
import com.albabot.model.Job;
import com.albabot.model.Evaluation;
import com.albabot.model.User;
import com.albabot.service.EvaluationService;
import com.albabot.service.JobService;

@Controller
public class MainController {

    private final JobDao jobDao;
    private final EvaluationDao evaluationDao;
    private final JobService jobService;
    private final EvaluationService evaluationService;

    public MainController(JobDao jobDao, EvaluationDao evaluationDao, JobService jobService, EvaluationService evaluationService) {
        this.jobDao = jobDao;
        this.evaluationDao = evaluationDao;
        this.jobService = jobService;
        this.evaluationService = evaluationService;
    }

    // 메인 페이지 화면 매핑
    @GetMapping("/main")
    public String mainPage(Model model, HttpSession session) {
    	User loginUser = (User) session.getAttribute("loginUser");
    	if (loginUser == null) {
    		return "redirect:/login";
    	}
    	model.addAttribute("loginUser", loginUser);
    	
        model.addAttribute("jobs", jobService.showAllJobs());
        model.addAttribute("selectedCategory", null);
        return "main";
    }

    @GetMapping("/jobs")
    public String viewJobs(@RequestParam(required = false) String category,
    		Model model, HttpSession session) {
    	User loginUser = (User) session.getAttribute("loginUser");
    	model.addAttribute("loginUser", loginUser);
    	
    	 if (category == null || category.isBlank()) {
    	        model.addAttribute("jobs", jobService.showAllJobs());
    	        model.addAttribute("selectedCategory", null);
    	    } else {
    	        model.addAttribute("jobs", jobService.showJobsByCategory(category));
    	        model.addAttribute("selectedCategory", category);
    	    }

    	    return "main";
    }
    
 // 공고 상세 페이지 조회 매핑
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable("id") int jobId, Model model, HttpSession session) {
        
    	User loginUser = (User) session.getAttribute("loginUser");
    	model.addAttribute("loginUser", loginUser); 
    	
        Job job = jobDao.getJobById(jobId); 
        model.addAttribute("job", job);

        // 해당 공고에 달린 후기(리뷰) 목록을 가져와서 detail.html에 던져주기
        List<Evaluation> evaluations = evaluationDao.getEvaluationsByJobId(jobId);
        model.addAttribute("evaluations", evaluations);
        
        // 지원자 수 표기 오류(null명) 방지
        model.addAttribute("applicationCount", jobService.countApplication(jobId)); 
        
        // 기본값을 false로 명시해 두고, 본인 공고일 때만 true로 변경하여 확실한 boolean 값을 전달합니다.
        boolean isEmployerMe = false;
        if (loginUser != null && job != null && loginUser.getUserId() == job.getEmployerId()) {
            isEmployerMe = true;
        }
        model.addAttribute("isEmployerMe", isEmployerMe);         
        // 팀원분들과의 연동을 위해 이미 지원했는지 여부 조건도 우선 false로 안전하게 대입해 둡니다.
        
        model.addAttribute("alreadyApplied", false);
        
        return "detail"; 
    }
    
    @PostMapping("/jobs/{id}/apply")
    public String applyJob(@PathVariable int id,
                           @RequestParam(required = false) String coverLetter,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");

        boolean result = jobService.applicate(id, loginUser, coverLetter);

        if (result) {
            redirectAttributes.addFlashAttribute("successMsg", "지원이 완료되었습니다!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "지원할 수 없는 공고입니다.");
        }

        return "redirect:/jobs/" + id;
    }
    
    @GetMapping("/jobs/new")
    public String newJobForm(Model model, HttpSession session) {
    	model.addAttribute("job", new Job());
    	return "job-form";
    }
    
    @PostMapping("/jobs/new")
    public String createJob(@ModelAttribute Job job, HttpSession session) {
    	User loginUser = (User) session.getAttribute("loginUser");
    	
    	job.setEmployerId(loginUser.getUserId());
    	jobService.addJob(job);
    	
    	return "redirect:/jobs";
    }
    
    @PostMapping("/jobs/{id}/review")
    public String addReview(@PathVariable("id") int jobId, 
                            @RequestParam("score") int score, 
                            @RequestParam("comment") String comment, 
                            HttpSession session) {
        
        // 세션에서 로그인한 유저(작성자) 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login"; // 로그인 안 되어있으면 로그인 페이지로 강제 이동
        }

        // 해당 공고 정보를 가져와서 고용주(수신자) ID 매핑하기
        Job job = jobDao.getJobById(jobId);
        if (job == null) {
            return "redirect:/main";
        }

        // Evaluation 객체 생성 및 데이터 세팅
        Evaluation eval = new Evaluation();
        eval.setJobId(jobId);
        eval.setReviewerId(loginUser.getUserId()); // 구직자 (장화인 ID: 1)
        eval.setRevieweeId(job.getEmployerId());   // 고용주 (카페사장 ID: 4)
        eval.setScore(score);
        eval.setComment(comment);
        eval.setEvalType("SEEKER_TO_EMPLOYER");    // 구직자가 사장님에게 남기는 후기 타입 명시

        // DB에 최종 인서트
        evaluationDao.insertEvaluation(eval);

        // 💡 등록 완료 후 원래 보던 상세 페이지로 다시 새로고침(리다이렉트)
        return "redirect:/jobs/" + jobId;
    }
    
}