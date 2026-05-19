package albabot_backend;

import java.sql.Connection;
import java.util.List;

import dao.JobDao;
import model.Job;
import dao.ApplicationDao;
import model.Application;

public class Main {
	public static void main(String[] args) {
		DatabaseConnector connector = new DatabaseConnector();
		Connection conn = connector.getConnection();
		JobDao jobDao = new JobDao(connector);
		ApplicationDao appDao = new ApplicationDao(connector);
		
		// 테스트코드 - 구인 글 작성
//		dao.insertJob(new Job(1, "성실하신 분 찾아요", "카페", "xx카페에서 일하실 분 모집합니다."));
//		List<Job> jobs = jobDao.getAllJobs();
		
		// 테스트코드 - 구인 글 수정
//		dao.updateJob(new Job(2, 1, "활기찬 분도 좋아요~", "카페", "oo카페에서 일하실 분 모집합니다."));
//		List<Job> jobs = jobDao.getAllJobs();
		
		// 테스트코드 - 구인 글 삭제
//		jobDao.deleteJob(2);
		
		// 테스트코드 - 업무 지원
		//appDao.insertApplication(new Application(2, 1, "홀 서빙 경력 있습니다."));
	}
}
