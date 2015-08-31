package com.yunfang.eias.logic;

import java.util.ArrayList;

import android.content.Context;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.http.task.GetDataDefineDataTask;
import com.yunfang.eias.http.task.GetFinishInworkReportTask;
import com.yunfang.eias.http.task.GetHomeInfoTask;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.UserTaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.NetWorkUtil;

/**
 * 主界面逻辑操作类
 * 
 * @author gorson
 * 
 */
public class HomeOperator {

	/**
	 * 获取用户任务信息
	 * 
	 * @return
	 */
	public static UserTaskInfo getCurrentTaskInfos() {
		UserTaskInfo result = new UserTaskInfo();

		result.NonReceivedNormal = 20;
		result.NonReceivedUrgent = 3;
		result.NonReceivedTotals = result.NonReceivedNormal + result.NonReceivedUrgent;

		result.ReceivedNormal = 15;
		result.ReceivedUrgent = 1;
		result.ReceivedTotals = result.ReceivedNormal + result.ReceivedUrgent;

		return result;
	}

	/**
	 * 获取当前所用的网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetType(Context context) {
		String type = NetWorkUtil.getNetworkType().getName();
		return type;
	}

	/**
	 * 同步可以同步的所有勘察匹配表完整信息：分类项信息、分类项下属性信息列表
	 * 
	 * @param currentUserInfo
	 * @param dataDefines
	 * @return
	 */
	public static ResultInfo<ArrayList<DataDefine>> fillAllDataDefines(UserInfo currentUserInfo, ArrayList<DataDefine> dataDefines) {
		ResultInfo<ArrayList<DataDefine>> result = new ResultInfo<ArrayList<DataDefine>>();
		result.Data = new ArrayList<DataDefine>();
		if (dataDefines != null && dataDefines.size() > 0) {
			ResultInfo<Boolean> tempResult = new ResultInfo<Boolean>();
			for (DataDefine define : dataDefines) {
				tempResult = fillOneDataDefine(currentUserInfo, define);
				if (tempResult.Success && tempResult.Data) {
					result.Message = tempResult.Message;
					result.Data.add(define);
				}
			}
		}
		return result;
	}

	/**
	 * 同步某个勘察匹配表完整信息：分类项信息、分类项下属性信息列表
	 * 
	 * @param currentUserInfo
	 *            :当前用户信息
	 * @param dataDefine
	 *            :当前勘察表信息，拿ID做交互
	 * @return
	 */
	public static ResultInfo<Boolean> fillOneDataDefine(UserInfo currentUserInfo, DataDefine dataDefine) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;

		try {
			GetDataDefineDataTask task = new GetDataDefineDataTask();
			ResultInfo<DataDefine> data = task.request(currentUserInfo, dataDefine);
			if (data.Success && data.Data != null) {
				ResultInfo<Long> fillInfo = DataDefineWorker.fillCompleteDataDefindInfos(data.Data);
				if (fillInfo.Data > 0) {
					result.Data = true;
					DataLogOperator.dataDefineDataSynchronization(dataDefine, "");
				} else {
					result.Data = false;
					result.Success = true;
					result.Message = "勘察表数据写入设备出错";
					DataLogOperator.dataDefineDataSynchronization(dataDefine, result.Message);
				}
			} else {
				result.Data = false;
				result.Success = data.Success;
				result.Message = (data.Message.trim().length() > 0 ? data.Message : "勘察表数据获取失败");
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}

		return result;
	}

	/**
	 * 得到用户信息
	 * 
	 * @return
	 */
	public static UserInfo getUsrInfo() {
		UserInfo info = new UserInfo();

		return info;
	}

	/**
	 * 获取HomeFragment数据信息
	 * 
	 * @param userInfo
	 *            ：当前用户信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ResultInfo<UserTaskInfo> getHomeData(UserInfo userInfo) {
		ResultInfo<UserTaskInfo> result = new ResultInfo<UserTaskInfo>();
		try {
			if (!EIASApplication.IsOffline) {
				GetHomeInfoTask task = new GetHomeInfoTask();
				result = task.request(userInfo);
				if (result.Success && result.Others != null) {
					ArrayList<DataDefine> returnDefines = (ArrayList<DataDefine>) result.Others;
					ResultInfo<ArrayList<DataDefine>> localDefines = DataDefineWorker.queryDataDefineByCompanyID(userInfo.CompanyID);
					ArrayList<DataDefine> defines = new ArrayList<DataDefine>();
					if (localDefines.Success) {
						if (localDefines.Data != null && localDefines.Data.size() > 0) {
							boolean isDeal = false;
							for (DataDefine define : returnDefines) {
								isDeal = false;
								for (DataDefine localDefine : localDefines.Data) {
									if (define.DDID == localDefine.DDID) {
										if (define.Version != localDefine.Version) {
											defines.add(define);
										}
										isDeal = true;
										break;
									}
								}
								if (!isDeal) {
									defines.add(define);
								}
							}
							result.Others = defines;
						}
					}
				}
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = result.Message.length() > 0 ? result.Message : e.getMessage();
		}
		return result;
	}

	/**
	 * 同步已经完成报告的任务信息　
	 */
	public static void synchroReportInfo(final UserInfo userInfo) {
		if (!EIASApplication.IsOffline) {
			new Thread() {
				public void run() {
					try {
						// 获取最后的报告日期同步时间
						ResultInfo<String> date = TaskDataWorker.getLastDateByReport(userInfo);
						if (date.Success) {
							// 获取当前查询结果日期后的报告
							GetFinishInworkReportTask task = new GetFinishInworkReportTask();
							ResultInfo<ArrayList<String>> tasklst = task.request(userInfo, date.Data);
							if (tasklst.Success && tasklst.Data != null && tasklst.Data.size() > 0) {
								for (String item : tasklst.Data) {
									String[] temp = item.split(",");
									if (temp.length == 2) {
										TaskDataWorker.synchroReportInfo(temp[0], temp[1]);
									}
								}
							}
						}
					} catch (Exception e) {
						DataLogOperator.other("synchroReportInfo=>" + e.getMessage());
					}
				}
			}.start();
		}
	}
}
