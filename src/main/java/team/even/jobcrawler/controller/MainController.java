package team.even.jobcrawler.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import team.even.jobcrawler.model.dataAnalyzer.DataAnalyzer;
import team.even.jobcrawler.model.db.factory.DistrictDAOFactory;
import team.even.jobcrawler.model.db.factory.JobDataDAOFactory;
import team.even.jobcrawler.model.db.factory.JobKindDAOFactory;
import team.even.jobcrawler.model.db.factory.JobTypesDAOFactory;
import team.even.jobcrawler.model.db.vo.District;
import team.even.jobcrawler.model.db.vo.JobData;
import team.even.jobcrawler.model.db.vo.JobKind;
import team.even.jobcrawler.model.db.vo.JobTypes;
import team.even.jobcrawler.model.service.Service;

@Controller
@RequestMapping("/")
public class MainController
{
	@RequestMapping("/index")
	public String index()
	{
		return "index";
	}

	@RequestMapping("/savedContent")
	public @ResponseBody List<JobTypes> appearJobTypes()
	{
		List<JobTypes> list = (new JobTypesDAOFactory())
				.getJobTypesDAOInstance()
				.findAll();
		return list;
	}
	
	@RequestMapping("/getKind")
	public @ResponseBody List<JobKind> getKind()
	{
		List<JobKind> kindList = (new JobKindDAOFactory())
									.getJobKindDAOInstance()
									.findAll();
		return kindList;
	}
	
	@RequestMapping("/getDistrict")
	public @ResponseBody List<District> getDistrict()
	{
		List<District>  districtList = (new DistrictDAOFactory())
										.getDistrictDAOInstance()
										.findAll();
		return districtList;
	}

	@RequestMapping("/startCrawler")
	public @ResponseBody String startCrawler(@RequestParam(value = "kind")String kind,
			@RequestParam(value = "distinct")String distinct)
	{
		JobTypesDAOFactory factory = new JobTypesDAOFactory();
		List<JobTypes> list = factory.getJobTypesDAOInstance()
				.findByKindandWorkPlace(kind, distinct);
		if(list.isEmpty())
		{
			Service service = Service.getInstance();
			service.start(distinct, kind);
			return "success";
		}
		else
		{
			return "failure";		
		}
	}
	
	@RequestMapping("/getCrawlerStatus")
	public @ResponseBody Map<String, String> getCrawlerStatus()
	{
		Map<String, String> map = new HashMap<String, String>();
		Service service =  Service.getInstance();
		String status = service.getRunStatusCtrl().getStatus();
		int count = service.getRunStatusCtrl().getCount();
		map.put("status", status);
		map.put("count", String.valueOf(count));
		return map;
	}
	
	@RequestMapping("/stopCrawler")
	public @ResponseBody String stopCrawler()
	{
		Service service = Service.getInstance();
		service.stop();
		return "����ֹͣ�����������,���Ե�...\n";
	}
	
	@RequestMapping("/displayData")
	public String displayData()
	{
		return "displayData";
	}
	
	@RequestMapping("/displayData/getData")
	public @ResponseBody List<JobData> display(@RequestParam("kind") String kind,
			@RequestParam("workPlace") String workPlace)
	{
		JobDataDAOFactory factory = new JobDataDAOFactory();
		List<JobData> list = factory.getJobDataDAOInstance()
				.findByKindandWorkPlace(kind, workPlace);
		return list;
	}
	
	@RequestMapping("/removeData")
	public @ResponseBody String removeData(@RequestParam(value="kind")String kind,
			@RequestParam(value="workPlace")String workPlace)
	{
		boolean flag = true;
		JobDataDAOFactory dataFactory = new JobDataDAOFactory();
		flag = dataFactory
				.getJobDataDAOInstance()
				.doDeleteByKindandWorkPlace(kind, workPlace);
		JobTypesDAOFactory typesFactory = new JobTypesDAOFactory();
		flag = typesFactory
				.getJobTypesDAOInstance()
				.doDelete(kind, workPlace);
		if(flag == true)
		{
			return String.valueOf(true);
		}
		else
		{
			return String.valueOf(false);
		}
	}
	
	@RequestMapping("/report")
	public String generateReport()
	{
		return "report";
	}
	
	@RequestMapping("/report/getSalaryData")
	public @ResponseBody Map<String, String> getSalaryData(
			@RequestParam(value = "kind")String kind,
			@RequestParam(value="district")String district)
	{
		Map<String, String> dataMap = new HashMap<String, String>();
		DataAnalyzer analyzer = new DataAnalyzer();
		dataMap = analyzer.analyzeSalary(kind, district);
		
		return dataMap;
	}
	
	@RequestMapping("/report/getExpData")
	public @ResponseBody Map<String, String> getExpData(
			@RequestParam(value = "kind")String kind,
			@RequestParam(value="district")String district)
	{
		Map<String, String> dataMap = new HashMap<String, String>();
		DataAnalyzer analyzer = new DataAnalyzer();
		dataMap = analyzer.analyzeExp(kind, district);
		return dataMap;
	}
	
	@RequestMapping("/report/getAcadeData")
	public @ResponseBody Map<String, String> getAcadeData(
			@RequestParam(value = "kind")String kind,
			@RequestParam(value="district")String district)
	{
		Map<String, String> dataMap = new HashMap<String, String>();
		DataAnalyzer analyzer = new DataAnalyzer();
		dataMap = analyzer.analyzeAcade(kind, district);
		return dataMap;
	}
}