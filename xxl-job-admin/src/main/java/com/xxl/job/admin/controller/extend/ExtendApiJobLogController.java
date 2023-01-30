package com.xxl.job.admin.controller.extend;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xxl.job.admin.controller.JobInfoController;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;

/**
 *
 * @author Rain
 */
@RestController
@RequestMapping("/extend/api/joblog")
public class ExtendApiJobLogController {
	private static Logger logger = LoggerFactory.getLogger(ExtendApiJobLogController.class);

	@Resource
	public XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;

	@RequestMapping("/getJobsByGroup")
	@PermissionLimit(limit = false)
	public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup){
		List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
		return new ReturnT<List<XxlJobInfo>>(list);
	}
	
	@RequestMapping("/pageList")
	@PermissionLimit(limit = false)
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										int jobGroup, int jobId, int logStatus, String filterTime) {

		// valid permission
		//JobInfoController.validPermission(request, jobGroup);	// 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup
		
		// parse param
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (filterTime!=null && filterTime.trim().length()>0) {
			String[] temp = filterTime.split(" - ");
			if (temp.length == 2) {
				triggerTimeStart = DateUtil.parseDateTime(temp[0]);
				triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
			}
		}
		
		// page query
		List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		int list_count = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// 总记录数
	    maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
	    maps.put("data", list);  					// 分页列表
		return maps;
	}


	@RequestMapping("/logKill")
	@PermissionLimit(limit = false)
	public ReturnT<String> logKill(int id){
		// base check
		XxlJobLog log = xxlJobLogDao.load(id);
		XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
		if (jobInfo==null) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
			return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
		}

		// request of kill
		ReturnT<String> runResult = null;
		try {
			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
			runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			runResult = new ReturnT<String>(500, e.getMessage());
		}

		if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
			log.setHandleCode(ReturnT.FAIL_CODE);
			log.setHandleMsg( I18nUtil.getString("joblog_kill_log_byman")+":" + (runResult.getMsg()!=null?runResult.getMsg():""));
			log.setHandleTime(new Date());
			XxlJobCompleter.updateHandleInfoAndFinish(log);
			return new ReturnT<String>(runResult.getMsg());
		} else {
			return new ReturnT<String>(500, runResult.getMsg());
		}
	}


}
