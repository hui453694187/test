package com.yunfang.eias.test;
import android.test.AndroidTestCase;

import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.TaskDataWorker;

/**   
*    
* ��Ŀ���ƣ�com.yunfang.eias.test
* �����ƣ�TaskOperatorTest   
* ��������TaskOperator�൥Ԫ����  
* �����ˣ�sen
* ����ʱ�䣺2014-5-26 ����9:25:51   
* @version        
*/ 
public class TaskOperatorTest extends AndroidTestCase {
	/**
	 * ���Ի��һ��������������
	 */
	public void testGetCompleteTaskInfoById(){
		boolean isCreatedByUser = true;
		int copied_task_id =5;
		TaskInfo taskInfo = TaskDataWorker.getCompleteTaskInfoById(copied_task_id, isCreatedByUser).Data;
		System.out.println("testFillCompleteDataDefindInfos:"+taskInfo);
		assertEquals(false,taskInfo==null);
	}
	
	/**
	 *	���Ի�ȡ��ǰָ�����������ӵķ�������Ϣ
	 * */
	public void testGetCanBeAddCategories() {
		/*int copied_task_id = 0;
		boolean isCreatedByUser = false;
		TaskInfo taskInfo = TaskDataWorker.queryTaskInfo(copied_task_id, isCreatedByUser).Data;
		ResultInfo<ArrayList<DataCategoryDefine>> dataCategoryDefine =
				com.yunfang.eias.logic.TaskOperator.getCanBeAddCategories(taskInfo);
		System.out.println("testFillCompleteDataDefindInfos:"+dataCategoryDefine.Data);
		assertEquals(false,dataCategoryDefine.Data.isEmpty());*/
	}
}
