package com.yunfang.eias.test;

import java.util.ArrayList;

import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.DateUtil;

import android.test.AndroidTestCase;

public class TaskDataWorkerTest extends AndroidTestCase {

	// TaskDataWorker TaskDataWorker = new TaskDataWorker();

	/**
	 * 填充完成整个任务数据（事务操作）
	 * */
	public void testFillCompleteTaskDataInfos() {
		ResultInfo<Long> resultInfo = null;
		for (int i = 0; i <= 20; i++) {
			TaskInfo taskInfo = new TaskInfo("U" + TaskOperator.GenProjectID(),
					1, 1, DateUtil.getCurrentTime(), DateUtil.getCurrentTime(),
					2, "1", 0, "北京市/海淀区（车道沟0号院", "业主", "业主电话", "小区名称", "楼栋名称",
					"所在楼层", "TargetName", "TargetType", "建筑面积", "委托人单位",
					"委托人部门", "委托人名称", "委托人联系电话", "孙浩然", "收费金额", "收据号", "创建时间",
					"标注", null, 0, 1, 1, 1, "最后上传时间");

			ArrayList<TaskCategoryInfo> taskCategoryInfoList = new ArrayList<TaskCategoryInfo>();
			TaskCategoryInfo taskCategoryInfo1 = new TaskCategoryInfo(1,
					"标识名称1", 1, 1, i, i, "创建时间");
			TaskCategoryInfo taskCategoryInfo2 = new TaskCategoryInfo(1,
					"标识名称2", 1, 2, i, i, "创建时间");
			taskCategoryInfoList.add(taskCategoryInfo1);
			taskCategoryInfoList.add(taskCategoryInfo2);

			ArrayList<TaskDataItem> taskDataItemList = new ArrayList<TaskDataItem>();
			TaskDataItem taskDataItem1 = new TaskDataItem(1, "属性名称1", "采集值1",
					1, 1, 1, -1);
			TaskDataItem taskDataItem2 = new TaskDataItem(1, "属性名称2", "采集值2",
					1, 1, 1, -1);
			TaskDataItem taskDataItem3 = new TaskDataItem(1, "属性名称3", "采集值3",
					1, 1, 2, -1);
			taskDataItemList.add(taskDataItem1);
			taskDataItemList.add(taskDataItem2);
			taskDataItemList.add(taskDataItem3);

			taskCategoryInfo1.Items.addAll(taskDataItemList);

			taskInfo.Categories = taskCategoryInfoList;

			resultInfo = TaskDataWorker.fillCompleteTaskDataInfos(taskInfo);
			System.out.println("testFillCompleteTaskDataInfos:" + resultInfo);
		}

		assertEquals(true, resultInfo.Data > 0);
	}

	/**
	 * 删除完成整个任务数据(事务操作）
	 * */
	public void testZDeleteCompleteTaskDataInfos() {
		ResultInfo<Integer> resultInfo = TaskDataWorker
				.deleteCompleteTaskDataInfos(1, null);
		System.out.println("testDeleteCompleteTaskDataInfos:" + resultInfo);
		assertEquals(true, resultInfo.Data > 0);
	}

	/**
	 * 根据任务编号或者地址查询勘察任务信息表（TaskInfo）
	 * */
	public void testQueryTaskInfoes() {
		UserInfo userInfo = new UserInfo();
		userInfo.Name = "孙浩然";
		TaskStatus taskStatus = TaskStatus.getEnumByValue(2);// 已完成
		ResultInfo<ArrayList<TaskInfo>> resultInfo = TaskDataWorker
				.queryTaskInfoes(1, 10, "1", userInfo, taskStatus);// 任务编号或者地址（or）
		System.out.println("testQueryTaskInfo:" + resultInfo);
		System.out.println("一共查到了:" + resultInfo.Data.size() + "条数据");
		assertEquals(true, resultInfo.Data != null);
	}

	/**
	 * 更新一条勘察任务分类项信息表数据
	 * */
	public void testUpdateOneTaskCategoryInfo() {
		ResultInfo<TaskCategoryInfo> resultInfo1 = TaskDataWorker
				.queryOneTaskCategoryInfo(1);
		TaskCategoryInfo taskCategoryInfo = resultInfo1.Data;
		taskCategoryInfo.RemarkName = "更改为标识名称1";
		ResultInfo<Integer> resultInfo2 = TaskDataWorker
				.updateOneTaskCategoryInfo(taskCategoryInfo);
		System.out.println("testUpdateOneTaskCategoryInfo:" + resultInfo2);
		assertEquals(true, resultInfo2.Data > 0);
	}

	/**
	 * 根据勘察任务分类项信息表对应的任务ID(TaskID)查询该勘察任务信息下所有勘察任务分类项信息表（TaskCategoryInfo）的数据
	 * */
	public void testQueryTaskCategoryInfoesByTaskID() {

		ResultInfo<ArrayList<TaskCategoryInfo>> resultInfo = TaskDataWorker
				.queryTaskCategories(1, false, false);
		System.out.println("testQueryTaskCategoryInfoesByTaskID:" + resultInfo);
		assertEquals(true, resultInfo.Data != null);
	}

	/**
	 * 根据id查询该勘察任务分类项下所有勘察任务属性数据记录表（TaskDataItem）的数据
	 * 
	 * @param taskID
	 *            :任务ID值
	 * @param categoryID
	 *            :分类项标识，用于区分可重复项中的哪一个具体的重复值，对应TaskCategoryInfos表中的ID值
	 * */
	public void testQueryTaskDataItemsByID() {
		ResultInfo<ArrayList<TaskDataItem>> resultInfo = TaskDataWorker
				.queryTaskDataItemsByID(1, 1, 0, null);// (1,1)2条,(1,2)1条
		System.out.println("testQueryTaskDataItemsByID:" + resultInfo);
		assertEquals(true, resultInfo.Data != null);
	}

	/**
	 * （saveManyTaskCategoryInfo）保存或者更新多条勘察任务分类项信息表（TaskCategoryInfo）数据（需要事务）
	 * 此方法测更新的
	 * */
	public void testUpdateManyTaskCategoryInfo() {
		ResultInfo<ArrayList<TaskCategoryInfo>> resultInfo = TaskDataWorker
				.queryTaskCategories(1, true, null);
		ArrayList<TaskCategoryInfo> taskCategoryInfoList = resultInfo.Data;
		for (TaskCategoryInfo taskCategoryInfo : taskCategoryInfoList) {
			taskCategoryInfo.RemarkName = "更改后的名称";
		}
		ResultInfo<Long> resultInfo2 = TaskDataWorker
				.saveManyTaskCategoryInfo(taskCategoryInfoList);
		System.out.println("testUpdateManyTaskCategoryInfo(更新):" + resultInfo2);
		assertEquals(true, resultInfo2.Data > 0);
	}

	/**
	 * （saveManyTaskCategoryInfo）保存或者更新多条勘察任务分类项信息表（TaskCategoryInfo）数据（需要事务）
	 * 此方法是测保存的
	 * */
	public void testInsertManyTaskCategoryInfo() {
		ArrayList<TaskCategoryInfo> taskCategoryInfoList = new ArrayList<TaskCategoryInfo>();
		TaskCategoryInfo taskCategoryInfo1 = new TaskCategoryInfo();
		TaskCategoryInfo taskCategoryInfo2 = new TaskCategoryInfo();
		taskCategoryInfoList.add(taskCategoryInfo1);
		taskCategoryInfoList.add(taskCategoryInfo2);
		ResultInfo<Long> resultInfo = TaskDataWorker
				.saveManyTaskCategoryInfo(taskCategoryInfoList);
		System.out.println("testInsertManyTaskCategoryInfo(保存):" + resultInfo);
		assertEquals(true, resultInfo.Data > 0);
	}

	/**
	 * （saveManyTaskDataItem）保存或者更新多条勘察任务属性数据记录表（TaskDataItem）数据（需要事务） 此方法是测更新的
	 * */
	public void testUpdateManyTaskDataItem() {
		// 找出TaskDataItem
		ResultInfo<ArrayList<TaskDataItem>> resultInfo = TaskDataWorker
				.queryTaskDataItemsByID(1, 1, 0, null);
		ArrayList<TaskDataItem> taskDataItemList = resultInfo.Data;
		for (TaskDataItem taskDataItem : taskDataItemList) {
			taskDataItem.Value = "更改后的采集值";
		}
		// 更新TaskDataItem
		ResultInfo<Integer> resultInfo2 = TaskDataWorker
				.saveManyTaskDataItem(taskDataItemList);
		System.out.println("testUpdateManyTaskDataItem:" + resultInfo2);
		assertEquals(true, resultInfo2.Data > 0);
	}

	/**
	 * (saveManyTaskDataItem)保存或者更新多条勘察任务属性数据记录表（TaskDataItem）数据（需要事务） 此方法是测保存的
	 * */
	public void testInsertManyTaskDataItem() {
		ArrayList<TaskDataItem> taskDataItemList = new ArrayList<TaskDataItem>();
		TaskDataItem taskDataItem1 = new TaskDataItem(1, "属性名称11", "采集值11", 1,
				1, 1, -1);
		TaskDataItem taskDataItem2 = new TaskDataItem(1, "属性名称22", "采集值22", 2,
				1, 1, -1);
		TaskDataItem taskDataItem3 = new TaskDataItem(1, "属性名称33", "采集值33", 3,
				1, 1, -1);
		taskDataItemList.add(taskDataItem1);
		taskDataItemList.add(taskDataItem2);
		taskDataItemList.add(taskDataItem3);
		ResultInfo<Integer> resultInfo = TaskDataWorker
				.saveManyTaskDataItem(taskDataItemList);
		System.out.println("testInsertManyTaskDataItem:" + resultInfo);
		assertEquals(true, resultInfo.Data > 0);
	}

	/**
	 * 根据自增id查询相应的勘察任务分类项信息表（TaskCategoryInfo）数据
	 * */
	public void testQueryOneTaskCategoryInfo() {
		ResultInfo<TaskCategoryInfo> resultInfo = TaskDataWorker
				.queryOneTaskCategoryInfo(1);
		System.out.println("testQueryOneTaskCategoryInfo:" + resultInfo);
		assertEquals(true, resultInfo.Data != null);
	}

}
