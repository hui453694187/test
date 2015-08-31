package com.yunfang.eias.test;
import android.test.AndroidTestCase;

import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.TaskDataWorker;

/**   
*    
* 项目名称：com.yunfang.eias.test
* 类名称：TaskOperatorTest   
* 类描述：TaskOperator类单元测试  
* 创建人：sen
* 创建时间：2014-5-26 上午9:25:51   
* @version        
*/ 
public class TaskOperatorTest extends AndroidTestCase {
	/**
	 * 测试获得一个完整的任务项
	 */
	public void testGetCompleteTaskInfoById(){
		boolean isCreatedByUser = true;
		int copied_task_id =5;
		TaskInfo taskInfo = TaskDataWorker.getCompleteTaskInfoById(copied_task_id, isCreatedByUser).Data;
		System.out.println("testFillCompleteDataDefindInfos:"+taskInfo);
		assertEquals(false,taskInfo==null);
	}
	
	/**
	 *	测试获取当前指定任务可以添加的分类项信息
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
