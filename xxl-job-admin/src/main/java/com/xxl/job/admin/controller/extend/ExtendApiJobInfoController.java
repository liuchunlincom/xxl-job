package com.xxl.job.admin.controller.extend;

import java.util.*;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;

/**
 *
 * @author Rain
 */
@RestController
@RequestMapping("/extend/api/jobinfo")
public class ExtendApiJobInfoController {
	private static Logger logger = LoggerFactory.getLogger(ExtendApiJobInfoController.class);

	@Resource
	private XxlJobService xxlJobService;
	
	@RequestMapping("/pageList")
	@PermissionLimit(limitType = "token")
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
		
		return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
	}

	@RequestMapping("/queryById")
	@PermissionLimit(limitType = "token")
	public ReturnT<XxlJobInfo> queryById(@RequestParam(required = true) int id) {

		return xxlJobService.queryById(id);

	}

	
	@RequestMapping("/add")
	@PermissionLimit(limitType = "token")
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}
	
	@RequestMapping("/update")
	@PermissionLimit(limitType = "token")
	public ReturnT<String> update(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}
	
	@RequestMapping("/remove")
	@PermissionLimit(limitType = "token")
	public ReturnT<String> remove(int id) {
		return xxlJobService.remove(id);
	}

	/**
	 * 停止(正在执行的任务)。
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/kill")
	@PermissionLimit(limitType = "token")
	public ReturnT<String> kill(int id) {

		return xxlJobService.kill(id);


	}

	/**
	 * 终止后续任务.
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/stop")
	@PermissionLimit(limitType = "token")
	public ReturnT<String> pause(int id) {
		return xxlJobService.stop(id);
	}
	
	@RequestMapping("/start")
	@PermissionLimit(limitType = "token")
	public ReturnT<String> start(int id) {
		return xxlJobService.start(id);
	}
	
	@RequestMapping("/trigger")
	@PermissionLimit(limitType = "token")
	public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
		// force cover job param
		if (executorParam == null) {
			executorParam = "";
		}

		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
		return ReturnT.SUCCESS;
	}



	@RequestMapping("/nextTriggerTime")
	@PermissionLimit(limitType = "token")
	public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

		XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
		paramXxlJobInfo.setScheduleType(scheduleType);
		paramXxlJobInfo.setScheduleConf(scheduleConf);

		List<String> result = new ArrayList<>();
		try {
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
				if (lastTime != null) {
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) + e.getMessage());
		}
		return new ReturnT<List<String>>(result);

	}
	
}
