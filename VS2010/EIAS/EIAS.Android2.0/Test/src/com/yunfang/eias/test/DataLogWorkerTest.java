package com.yunfang.eias.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;

import com.yunfang.eias.enumObj.OperatorTypeEnum;

import com.yunfang.eias.model.DataLog;
import com.yunfang.eias.tables.DataLogWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * 
 * 项目名称：com.yunfang.eias.test 
 * 类名称：TaskOperatorTest 
 * 类描述：DataLogWorkerTest类单元测试
 * 创建人：贺隽 
 * 创建时间：2014-6-17 上午15:25:51
 * 
 * @version 1.0.0.1
 */
public class DataLogWorkerTest extends AndroidTestCase {

	/**
	 * 测试创建日志
	 * */
	public void testCreatedDataLog() { 
		UserInfo user = new UserInfo();
		user.Name = "魏国秀";
		ResultInfo<Boolean> result = DataLogWorker.createDataLog(user,
				"我只是测试内容", OperatorTypeEnum.TaskCreate);
		System.out.println("testFillCompleteDataDefindInfos:" + result.Data);
		assertEquals(false, result.Data != null);
	}

	/**
	 * 测试查询日志
	 * */
	public void testQueryDataLogs() {
		UserInfo user = new UserInfo();
		user.Name = "魏国秀";
		ResultInfo<ArrayList<DataLog>> result = DataLogWorker.getDataLogs(1,
				20, "测试", user, OperatorTypeEnum.TaskCreate.getIndex());
		System.out.println("testFillCompleteDataDefindInfos:" + result.Data);
		assertEquals(false, result.Data != null && result.Data.size() > 0);
	}
}
