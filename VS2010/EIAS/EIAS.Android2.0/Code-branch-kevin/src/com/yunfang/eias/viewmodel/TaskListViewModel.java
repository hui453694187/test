package com.yunfang.eias.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.ui.HomeActivity;
import com.yunfang.framework.model.ViewModelBase;

/**   
 *    
 * 项目名称：WaiCai   
 * 类名称：TaskModel   
 * 类描述： 任务的ViewModel
 * 创建人：lihc   
 * 创建时间：2014-4-17 下午2:29:30   
 * @version        
 */ 
public class TaskListViewModel extends ViewModelBase {

	/**
	 * 任务信息
	 * */
	public ArrayList<TaskInfo> taskInfoes = new ArrayList<TaskInfo>();

	/**
	 * 当前页码
	 */
	public int currentIndex=1;

	/**
	 * 每页显示数据
	 */
	public int pageSize = EIASApplication.PageSize;

	/**
	 * 远程总页数
	 */
	public int remoteTotal = 1;

	/**
	 * 本地自建项目
	 */
	public int localTotal = 0;

	/**
	 * 记录列表当前的位置
	 */
	public int listItemCurrentPosition=0;

	/**
	 * HomeActivity
	 */
	public HomeActivity homeActivity;

	/**
	 * 当前任务列表的状态
	 */
	public TaskStatus taskStatus;

	/**
	 * 当前任务信息，点击或长按时获取
	 */
	public TaskInfo currentSelectedTask;

	/**
	 * 被复制的任务信息
	 */
	public TaskInfo currentCopiedTask;

	/**
	 * 被复制的任务分类信息
	 */
	public ArrayList<TaskCategoryInfo> currentCopiedTaskCategories;

	/**
	 * 是否重新加载
	 */
	public boolean reload = true;

	/**
	 * 选中的复制项
	 * */
	public HashMap<String, String> selectedCategoryItems;	
	
	/**
	 * 记录上一个Fragment的名称 用于返回
	 */
	public String beforeFragmentName;
	
	/**
	 * 隐藏断网时的提示信息(’提交中‘提交完成时会一起刷新已完成和提交中,当断网情况下提交中的信息会以通知的形式写出，此时隐藏掉刷新'已完成''待提交'列表中服务器返回的断网信息)
	 */
	public Boolean hideOfflineMsg = false;
	
	/**
	 * 在已完成任务列表中只显示完成报告的任务
	 */
	public Boolean onlyReportFinish = false;

}
