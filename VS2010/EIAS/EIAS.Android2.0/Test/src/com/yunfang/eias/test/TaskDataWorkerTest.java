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
	 * �����������������ݣ����������
	 * */
	public void testFillCompleteTaskDataInfos() {
		ResultInfo<Long> resultInfo = null;
		for (int i = 0; i <= 20; i++) {
			TaskInfo taskInfo = new TaskInfo("U" + TaskOperator.GenProjectID(),
					1, 1, DateUtil.getCurrentTime(), DateUtil.getCurrentTime(),
					2, "1", 0, "������/��������������0��Ժ", "ҵ��", "ҵ���绰", "С������", "¥������",
					"����¥��", "TargetName", "TargetType", "�������", "ί���˵�λ",
					"ί���˲���", "ί��������", "ί������ϵ�绰", "���Ȼ", "�շѽ��", "�վݺ�", "����ʱ��",
					"��ע", null, 0, 1, 1, 1, "����ϴ�ʱ��");

			ArrayList<TaskCategoryInfo> taskCategoryInfoList = new ArrayList<TaskCategoryInfo>();
			TaskCategoryInfo taskCategoryInfo1 = new TaskCategoryInfo(1,
					"��ʶ����1", 1, 1, i, i, "����ʱ��");
			TaskCategoryInfo taskCategoryInfo2 = new TaskCategoryInfo(1,
					"��ʶ����2", 1, 2, i, i, "����ʱ��");
			taskCategoryInfoList.add(taskCategoryInfo1);
			taskCategoryInfoList.add(taskCategoryInfo2);

			ArrayList<TaskDataItem> taskDataItemList = new ArrayList<TaskDataItem>();
			TaskDataItem taskDataItem1 = new TaskDataItem(1, "��������1", "�ɼ�ֵ1",
					1, 1, 1, -1);
			TaskDataItem taskDataItem2 = new TaskDataItem(1, "��������2", "�ɼ�ֵ2",
					1, 1, 1, -1);
			TaskDataItem taskDataItem3 = new TaskDataItem(1, "��������3", "�ɼ�ֵ3",
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
	 * ɾ�����������������(���������
	 * */
	public void testZDeleteCompleteTaskDataInfos() {
		ResultInfo<Integer> resultInfo = TaskDataWorker
				.deleteCompleteTaskDataInfos(1, null);
		System.out.println("testDeleteCompleteTaskDataInfos:" + resultInfo);
		assertEquals(true, resultInfo.Data > 0);
	}

	/**
	 * ���������Ż��ߵ�ַ��ѯ����������Ϣ��TaskInfo��
	 * */
	public void testQueryTaskInfoes() {
		UserInfo userInfo = new UserInfo();
		userInfo.Name = "���Ȼ";
		TaskStatus taskStatus = TaskStatus.getEnumByValue(2);// �����
		ResultInfo<ArrayList<TaskInfo>> resultInfo = TaskDataWorker
				.queryTaskInfoes(1, 10, "1", userInfo, taskStatus);// �����Ż��ߵ�ַ��or��
		System.out.println("testQueryTaskInfo:" + resultInfo);
		System.out.println("һ���鵽��:" + resultInfo.Data.size() + "������");
		assertEquals(true, resultInfo.Data != null);
	}

	/**
	 * ����һ�����������������Ϣ������
	 * */
	public void testUpdateOneTaskCategoryInfo() {
		ResultInfo<TaskCategoryInfo> resultInfo1 = TaskDataWorker
				.queryOneTaskCategoryInfo(1);
		TaskCategoryInfo taskCategoryInfo = resultInfo1.Data;
		taskCategoryInfo.RemarkName = "����Ϊ��ʶ����1";
		ResultInfo<Integer> resultInfo2 = TaskDataWorker
				.updateOneTaskCategoryInfo(taskCategoryInfo);
		System.out.println("testUpdateOneTaskCategoryInfo:" + resultInfo2);
		assertEquals(true, resultInfo2.Data > 0);
	}

	/**
	 * ���ݿ��������������Ϣ���Ӧ������ID(TaskID)��ѯ�ÿ���������Ϣ�����п��������������Ϣ��TaskCategoryInfo��������
	 * */
	public void testQueryTaskCategoryInfoesByTaskID() {

		ResultInfo<ArrayList<TaskCategoryInfo>> resultInfo = TaskDataWorker
				.queryTaskCategories(1, false, false);
		System.out.println("testQueryTaskCategoryInfoesByTaskID:" + resultInfo);
		assertEquals(true, resultInfo.Data != null);
	}

	/**
	 * ����id��ѯ�ÿ�����������������п��������������ݼ�¼��TaskDataItem��������
	 * 
	 * @param taskID
	 *            :����IDֵ
	 * @param categoryID
	 *            :�������ʶ���������ֿ��ظ����е���һ��������ظ�ֵ����ӦTaskCategoryInfos���е�IDֵ
	 * */
	public void testQueryTaskDataItemsByID() {
		ResultInfo<ArrayList<TaskDataItem>> resultInfo = TaskDataWorker
				.queryTaskDataItemsByID(1, 1, 0, null);// (1,1)2��,(1,2)1��
		System.out.println("testQueryTaskDataItemsByID:" + resultInfo);
		assertEquals(true, resultInfo.Data != null);
	}

	/**
	 * ��saveManyTaskCategoryInfo��������߸��¶������������������Ϣ��TaskCategoryInfo�����ݣ���Ҫ����
	 * �˷�������µ�
	 * */
	public void testUpdateManyTaskCategoryInfo() {
		ResultInfo<ArrayList<TaskCategoryInfo>> resultInfo = TaskDataWorker
				.queryTaskCategories(1, true, null);
		ArrayList<TaskCategoryInfo> taskCategoryInfoList = resultInfo.Data;
		for (TaskCategoryInfo taskCategoryInfo : taskCategoryInfoList) {
			taskCategoryInfo.RemarkName = "���ĺ������";
		}
		ResultInfo<Long> resultInfo2 = TaskDataWorker
				.saveManyTaskCategoryInfo(taskCategoryInfoList);
		System.out.println("testUpdateManyTaskCategoryInfo(����):" + resultInfo2);
		assertEquals(true, resultInfo2.Data > 0);
	}

	/**
	 * ��saveManyTaskCategoryInfo��������߸��¶������������������Ϣ��TaskCategoryInfo�����ݣ���Ҫ����
	 * �˷����ǲⱣ���
	 * */
	public void testInsertManyTaskCategoryInfo() {
		ArrayList<TaskCategoryInfo> taskCategoryInfoList = new ArrayList<TaskCategoryInfo>();
		TaskCategoryInfo taskCategoryInfo1 = new TaskCategoryInfo();
		TaskCategoryInfo taskCategoryInfo2 = new TaskCategoryInfo();
		taskCategoryInfoList.add(taskCategoryInfo1);
		taskCategoryInfoList.add(taskCategoryInfo2);
		ResultInfo<Long> resultInfo = TaskDataWorker
				.saveManyTaskCategoryInfo(taskCategoryInfoList);
		System.out.println("testInsertManyTaskCategoryInfo(����):" + resultInfo);
		assertEquals(true, resultInfo.Data > 0);
	}

	/**
	 * ��saveManyTaskDataItem��������߸��¶������������������ݼ�¼��TaskDataItem�����ݣ���Ҫ���� �˷����ǲ���µ�
	 * */
	public void testUpdateManyTaskDataItem() {
		// �ҳ�TaskDataItem
		ResultInfo<ArrayList<TaskDataItem>> resultInfo = TaskDataWorker
				.queryTaskDataItemsByID(1, 1, 0, null);
		ArrayList<TaskDataItem> taskDataItemList = resultInfo.Data;
		for (TaskDataItem taskDataItem : taskDataItemList) {
			taskDataItem.Value = "���ĺ�Ĳɼ�ֵ";
		}
		// ����TaskDataItem
		ResultInfo<Integer> resultInfo2 = TaskDataWorker
				.saveManyTaskDataItem(taskDataItemList);
		System.out.println("testUpdateManyTaskDataItem:" + resultInfo2);
		assertEquals(true, resultInfo2.Data > 0);
	}

	/**
	 * (saveManyTaskDataItem)������߸��¶������������������ݼ�¼��TaskDataItem�����ݣ���Ҫ���� �˷����ǲⱣ���
	 * */
	public void testInsertManyTaskDataItem() {
		ArrayList<TaskDataItem> taskDataItemList = new ArrayList<TaskDataItem>();
		TaskDataItem taskDataItem1 = new TaskDataItem(1, "��������11", "�ɼ�ֵ11", 1,
				1, 1, -1);
		TaskDataItem taskDataItem2 = new TaskDataItem(1, "��������22", "�ɼ�ֵ22", 2,
				1, 1, -1);
		TaskDataItem taskDataItem3 = new TaskDataItem(1, "��������33", "�ɼ�ֵ33", 3,
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
	 * ��������id��ѯ��Ӧ�Ŀ��������������Ϣ��TaskCategoryInfo������
	 * */
	public void testQueryOneTaskCategoryInfo() {
		ResultInfo<TaskCategoryInfo> resultInfo = TaskDataWorker
				.queryOneTaskCategoryInfo(1);
		System.out.println("testQueryOneTaskCategoryInfo:" + resultInfo);
		assertEquals(true, resultInfo.Data != null);
	}

}
