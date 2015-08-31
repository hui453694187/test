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
 * ��Ŀ���ƣ�com.yunfang.eias.test 
 * �����ƣ�TaskOperatorTest 
 * ��������DataLogWorkerTest�൥Ԫ����
 * �����ˣ����� 
 * ����ʱ�䣺2014-6-17 ����15:25:51
 * 
 * @version 1.0.0.1
 */
public class DataLogWorkerTest extends AndroidTestCase {

	/**
	 * ���Դ�����־
	 * */
	public void testCreatedDataLog() { 
		UserInfo user = new UserInfo();
		user.Name = "κ����";
		ResultInfo<Boolean> result = DataLogWorker.createDataLog(user,
				"��ֻ�ǲ�������", OperatorTypeEnum.TaskCreate);
		System.out.println("testFillCompleteDataDefindInfos:" + result.Data);
		assertEquals(false, result.Data != null);
	}

	/**
	 * ���Բ�ѯ��־
	 * */
	public void testQueryDataLogs() {
		UserInfo user = new UserInfo();
		user.Name = "κ����";
		ResultInfo<ArrayList<DataLog>> result = DataLogWorker.getDataLogs(1,
				20, "����", user, OperatorTypeEnum.TaskCreate.getIndex());
		System.out.println("testFillCompleteDataDefindInfos:" + result.Data);
		assertEquals(false, result.Data != null && result.Data.size() > 0);
	}
}
