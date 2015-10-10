package com.yunfang.eias.tables;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.http.task.UploadLogInfoTask;
import com.yunfang.eias.model.DataLog;
import com.yunfang.framework.db.SQLiteHelper;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * 
 * 项目名称：外采勘察 类名称：DataLogWorker 类描述：日志数据表操作类 创建人：贺隽 创建时间：2014-6-17 上午11:31:47
 * 
 * @version 1.0.0.1
 */
public class DataLogWorker {

	// {{ 插入新的日志

	/**
	 * 创建新的日志
	 * 
	 * @param userID
	 *            ：用户编号
	 * @param content
	 *            :日志内容
	 * @param operatorType
	 *            :日志类型
	 * @return
	 */
	public static ResultInfo<Boolean> createDataLog(final UserInfo currentUser, final String content, final OperatorTypeEnum operatorType) {
		final ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		new Thread() {
			public void run() {
				
				try {
					// 是否成功写入服务器
					Boolean isSuccessSaveInService = false;
					final DataLog log = new DataLog();
					log.UserID = currentUser.Account;
					log.LogContent = content;
					log.OperatorType = operatorType;
					// 有网络的情况下直接将日志信息写入服务端
					if (EIASApplication.IsNetworking) {
						UploadLogInfoTask setLogTask = new UploadLogInfoTask();
						ResultInfo<Boolean> uploadInfo = setLogTask.request(currentUser, log);
						isSuccessSaveInService = (uploadInfo.Data && uploadInfo.Success);
						result.Data = true;
						result.Success = true;
					}
					if (!isSuccessSaveInService) {
						log.ID = (int) log.onInsert();
						if (log.ID > 0) {
							result.Data = true;
							result.Success = true;
						}
					}
				} catch (Exception e) {
					result.Data = false;
					result.Success = false;
					result.Message = e.getMessage();
				}
			}
		}.start();		
		return result;
	}

	// }}

	// {{ 获取日志信息 分页

	/**
	 * 根据任务编号或者地址查询勘察任务信息表（DataLog）
	 * 
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            :每页行数
	 * @param queryStr
	 *            :内容和创建日期
	 * @param currentUser
	 *            :指定查询用户的信息，即当前用户信息
	 * @param OperatorType
	 *            :日志的记录类型 不需要的话可以传 null
	 * @return
	 */
	public static ResultInfo<ArrayList<DataLog>> getDataLogs(int pageIndex, int pageSize, String queryStr, UserInfo currentUser, int logTypeIndex) {
		ResultInfo<ArrayList<DataLog>> resultInfo = new ResultInfo<ArrayList<DataLog>>();
		ArrayList<DataLog> data = null;
		DataLog log = null;
		SQLiteDatabase db = null;
		try {
			data = new ArrayList<DataLog>();
			log = new DataLog();
			db = SQLiteHelper.getReadableDB();
			StringBuilder sqlBuilder = new StringBuilder("from DataLog where UserID='");
			sqlBuilder.append(currentUser.Account + "'");
			if (logTypeIndex != -1) {
				sqlBuilder.append(" AND OperatorType = " + logTypeIndex);
			}

			if (queryStr.trim().length() > 0) {
				sqlBuilder.append(" AND (LogContent");
				sqlBuilder.append(" like '%" + queryStr);
				sqlBuilder.append("%' or CreatedDate");
				sqlBuilder.append(" like '%" + queryStr);
				sqlBuilder.append("%')");
			}

			Cursor cursor = db.rawQuery("select count(1) " + sqlBuilder.toString(), null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					resultInfo.Others = cursor.getInt(0);
				}
			}

			sqlBuilder.append(" order by CreatedDate desc limit " + (pageIndex - 1) * pageSize + "," + pageSize);
			cursor = db.rawQuery("select * " + sqlBuilder.toString(), null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					log = new DataLog();
					log.setValueByCursor(cursor);
					data.add(log);
				}
				resultInfo.Data = data;
			}
			cursor.close();
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Success = false;
			resultInfo.Data = null;// 如果失败，data为null
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	// }}

	// {{ 删除当前用户的日志

	/**
	 * 删除当前用户的日志信息
	 * 
	 * @param userId
	 *            :用户编号 为当前用户的 currentUser.Account;
	 * @return
	 */
	public static ResultInfo<Integer> deleteByUserId(String userId) {
		ResultInfo<Integer> resultInfo = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		// 删除数据影响的行数
		Integer result = 0;
		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();
			result += db.delete(new DataLog().getTableName(), "UserID=?", new String[] { String.valueOf(userId) });
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			// 结束事务，默认是回滚
			db.endTransaction();
			resultInfo.Data = result; // 此rowNum为删除影响的行数
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = result;
			resultInfo.Success = false;
		} finally {
			// db.close();
		}
		return resultInfo;
	}

	// }}
}
