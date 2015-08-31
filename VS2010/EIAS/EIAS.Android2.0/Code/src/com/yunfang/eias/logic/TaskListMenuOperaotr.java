package com.yunfang.eias.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.base.MainService;
import com.yunfang.eias.dto.DialogTipsDTO;
import com.yunfang.eias.enumObj.TaskMenuEnum;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.enumObj.TaskUploadStatusEnum;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.eias.ui.HomeActivity;
import com.yunfang.eias.ui.TaskInfoActivity;
import com.yunfang.eias.ui.TaskListFragment;
import com.yunfang.eias.ui.Adapter.TaskUnWriteAdapter;
import com.yunfang.framework.utils.DialogBuilder;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.ToastUtil;
import com.yunfang.framework.utils.WinDisplay;
import com.yunfang.framework.view.CustomMultipleChoiceView;
import com.yunfang.framework.view.CustomMultipleChoiceView.onCancelListener;
import com.yunfang.framework.view.CustomMultipleChoiceView.onSelectedListener;

/**
 * 处理任务列表中长按菜单
 * 
 * @author gorson
 * 
 */
@SuppressLint("NewApi")
public class TaskListMenuOperaotr {
	// {{ 菜单项数据

	/**
	 * 要显示的菜单项
	 */
	private String[] menuItems;

	// }}

	// {{ 变量
	/**
	 * 对话框Titile
	 */
	private String title = "请选择操作";

	/**
	 * 对话框
	 */
	private DialogBuilder dialogBuilder;

	/**
	 * TaskListFragment
	 */
	public TaskListFragment taskListFragment;

	/**
	 * 收费信息的窗口
	 */
	private Dialog feeDialog;

	/**
	 * 设置预约信息窗口
	 */
	private Dialog appointmentDialog;

	/**
	 * 查看任务信息的窗口
	 */
	private Dialog detailDialog;

	/**
	 * 暂停任务的窗口
	 */
	private Dialog pauseDialog;

	/**
	 * 当前选中任务的分类项信息 Key:Android端ID值 Value:分类项名称
	 */
	private LinkedHashMap<String, String> categoryItems;

	/**
	 * 提交任务信息所需适配器
	 */

	private TaskUnWriteAdapter taskunwriteadapter;

	/**
	 * 多选对话框
	 * */
	private PopupWindow stationSelectDialog;

	/**
	 * HomeActivity对象
	 */
	private HomeActivity homeActivity;

	/**
	 * taskInfoActivity对象
	 */
	private TaskInfoActivity taskInfoActivity;

	/**
	 * 当前上下文
	 */
	private Context currentContext;

	// }}

	/**
	 * 构造函数
	 * 
	 * @param listFragment
	 *            :任务列表对象
	 */
	public TaskListMenuOperaotr(TaskListFragment listFragment, HomeActivity activity) {
		taskListFragment = listFragment;
		currentContext = listFragment.getActivity();
		this.homeActivity = activity;
	}

	/**
	 * 构造函数
	 * 
	 * @param listFragment
	 *            :任务列表对象
	 */
	public TaskListMenuOperaotr(TaskInfoActivity activity) {
		this.taskInfoActivity = activity;
	}

	/**
	 * 获取当前Dialog
	 * 
	 * @return
	 */
	public void showDialog() {
		if (setMenuItems()) {
			if (menuItems != null) {
				dialogBuilder = new DialogBuilder(currentContext);
				dialogBuilder.setTitle(title);
				dialogBuilder.setItems(menuItems, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						menuItemClick(which);
					}
				});
			}
			dialogBuilder.create().show();
		}
	}

	/**
	 * 设置需要显示的任务菜单
	 * 
	 * @return 是否需要弹出选项
	 */
	private Boolean setMenuItems() {
		Boolean result = true;
		ArrayList<String> tempItems = new ArrayList<String>();
		menuItems = null;
		if (taskListFragment.viewModel.taskStatus != null) {
			switch (taskListFragment.viewModel.taskStatus) {
			case Todo:// 待领取
				tempItems.add(TaskMenuEnum.领取任务.toString());
				/* tempItems.add(TaskMenuEnum.领取任务并编辑.toString()); */
				tempItems.add(TaskMenuEnum.查看任务信息.toString());
				break;
			case Doing:// 待提交
				// 多选状态
				if (taskListFragment.viewModel.currentSelectedTask.isChecked) {
					tempItems.add(TaskMenuEnum.提交选中任务.toString());
				} else {
					tempItems.add(TaskMenuEnum.查看任务信息.toString());
					tempItems.add(TaskMenuEnum.预约信息.toString());
					tempItems.add(TaskMenuEnum.编辑内容.toString());
					tempItems.add(TaskMenuEnum.收费信息.toString());
					if (!taskListFragment.viewModel.currentSelectedTask.IsNew) {
						tempItems.add(TaskMenuEnum.暂停任务.toString());
					}
					tempItems.add(TaskMenuEnum.删除本地任务.toString());
					if (EIASApplication.IsNetworking && !EIASApplication.IsOffline) {
						tempItems.add(TaskMenuEnum.提交本地任务.toString());
						tempItems.add(TaskMenuEnum.任务匹配.toString());
					}
					tempItems.add(TaskMenuEnum.复制.toString());
					tempItems.add(TaskMenuEnum.数据检查并导出.toString());
					tempItems.add(TaskMenuEnum.任务数据导入.toString());
				}
				break;
			case Done:// 已完成
				// 多选状态
				if (taskListFragment.viewModel.currentSelectedTask.isChecked) {
					tempItems.add(TaskMenuEnum.重新提交选中任务.toString());
					tempItems.add(TaskMenuEnum.删除选中任务资源文件.toString());
				}
				// 非多选状态
				else {
					tempItems.add(TaskMenuEnum.查看任务信息.toString());
					tempItems.add(TaskMenuEnum.预约信息.toString());
					tempItems.add(TaskMenuEnum.编辑内容.toString());
					tempItems.add(TaskMenuEnum.图片补发.toString());
					//tempItems.add(TaskMenuEnum.收费信息.toString());
					if (EIASApplication.IsNetworking && !EIASApplication.IsOffline) {
						tempItems.add(TaskMenuEnum.重新提交本地任务.toString());
					}
					tempItems.add(TaskMenuEnum.复制.toString());
					tempItems.add(TaskMenuEnum.数据检查并导出.toString());
					tempItems.add(TaskMenuEnum.任务数据导入.toString());
					if (taskListFragment.viewModel.onlyReportFinish) {
						tempItems.add(TaskMenuEnum.显示全部完成任务.toString());
					} else {
						tempItems.add(TaskMenuEnum.只显示完成报告任务.toString());
					}
					tempItems.add(TaskMenuEnum.删除本地任务.toString());
				}
				break;
			case Submiting:
				if (taskListFragment.viewModel.currentSelectedTask.UploadStatusEnum != TaskUploadStatusEnum.Submiting) {
					if (EIASApplication.IsNetworking && !EIASApplication.IsOffline) {
						if (taskListFragment.viewModel.currentSelectedTask.UploadStatusEnum == TaskUploadStatusEnum.SubmitFailure) {
							tempItems.add(TaskMenuEnum.重新提交本地任务.toString());
						}
					}
					tempItems.add(TaskMenuEnum.取消提交任务.toString());
				} else {
					result = false;
				}
			default:
				break;
			}

			if (taskListFragment.viewModel.currentCopiedTask != null && taskListFragment.viewModel.currentSelectedTask != null
					&& taskListFragment.viewModel.currentSelectedTask.DDID == taskListFragment.viewModel.currentCopiedTask.DDID) {
				tempItems.add(TaskMenuEnum.粘贴创建新任务.toString());
				if (taskListFragment.viewModel.currentSelectedTask.ID !=

				taskListFragment.viewModel.currentCopiedTask.ID) {
					tempItems.add(TaskMenuEnum.粘贴.toString());
				}
			}
			menuItems = tempItems.toArray(new String[0]);
		}
		return result;
	}

	/**
	 * 菜单响应事件
	 * 
	 * @param which
	 */
	private void menuItemClick(int index) {
		TaskMenuEnum menu = TaskMenuEnum.valueOf(dialogBuilder.getMenuName(index));
		Integer ddid = taskListFragment.viewModel.currentSelectedTask.DDID;
		String taskNum = taskListFragment.viewModel.currentSelectedTask.TaskNum;
		String fee = taskListFragment.viewModel.currentSelectedTask.Fee;
		switch (menu) {
		case 删除本地任务:
			if (!TaskOperator.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				deleteTaskInfoDialog();
			} else {
				ToastUtil.longShow(taskListFragment.getActivity(), "当前任务正在提交中，将不会清空当前任务!");
			}
			break;
		case 复制:
			showCopyCategoriesDialog();
			break;
		case 收费信息:
			showSetFeeDialog();
			break;
		case 暂停任务:
			if (!TaskOperator.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				showPauseDialog();
			} else {
				ToastUtil.longShow(taskListFragment.getActivity(), "当前任务正在提交中，将不会暂停当前任务!");
			}
			break;
		case 查看任务信息:
			showCurrentSelectedTaskInfo();
			break;
		case 粘贴:
			if (!TaskOperator.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				pastedCategories(false);
			} else {
				ToastUtil.longShow(taskListFragment.getActivity(), "当前任务正在提交中，将不会粘贴到当前任务!");
			}
			break;
		case 粘贴创建新任务:
			pastedCategories(true);
			break;
		case 编辑内容:
			taskListFragment.doSomething("加载中", taskListFragment.TASK_EDIT_TASKINFO);
			break;
		case 领取任务:
			taskListFragment.doSomething("加载中", taskListFragment.TASK_RECEIVETASK);
			break;
		case 提交本地任务:
		case 重新提交本地任务:
			if (!hasNewDataDefines(ddid) && !checkTaskReportFinish(taskListFragment.viewModel.currentSelectedTask)) {
				putTaskInfo(ddid, taskNum, fee);
			}
			break;
		case 提交选中任务:
		case 重新提交选中任务:
			ArrayList<TaskInfo> allTaskInfo = taskListFragment.viewModel.taskInfoes;
			ArrayList<TaskInfo> needSubmitTaskInfo = new ArrayList<TaskInfo>();
			Boolean isSubmit = true;
			// 验证任务是否都可以上传
			for (TaskInfo element : allTaskInfo) {
				if (hasNewDataDefines(element.DDID)) {
					isSubmit = false;
					break;
				}
			}
			// 验证任务是否都可以上传
			if (isSubmit) {
				for (int i = 0; i < allTaskInfo.size(); i++) {
					TaskInfo taskInfo = allTaskInfo.get(i);
					if (taskInfo.isChecked) {
						needSubmitTaskInfo.add(taskInfo);
					}
				}
				commitTasks(needSubmitTaskInfo);
			}
			break;
		case 任务匹配:
			taskListFragment.viewModel.homeActivity.openMatchAty(taskNum, ddid);
			break;
		case 取消提交任务:
			MainService.removeUploadTasks(taskListFragment.viewModel.currentSelectedTask.TaskNum);
			break;
		case 数据检查并导出: {
			if (!hasNewDataDefines(ddid)) {
				taskExportInfo();
			}
			break;
		}
		case 任务数据导入: {
			if (!hasNewDataDefines(ddid)) {
				taskImportInfo();
			}
			break;
		}
		case 图片补发: {
			if (!hasNewDataDefines(ddid)) {
				additionalPictrue();
			}
			break;
		}
		case 预约信息: {
			showAppointmentDialog(TaskMenuEnum.预约信息);
			break;
		}
		case 只显示完成报告任务: {
			queryFinishReport();
			break;
		}
		case 显示全部完成任务: {
			queryFinishTask();
			break;
		}
		case 删除选中任务资源文件: {
			removeTaskResource();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 进入图片补发
	 */
	private void additionalPictrue() {
		taskListFragment.doSomething("", taskListFragment.TASK_ADDITIONAL_PICTRUE);
	}

	/**
	 * 删除本地任务
	 */
	private void deleteTaskInfoDialog() {
		DialogUtil.showConfirmationDialog(currentContext, "您确认要删除编号为[" + taskListFragment.viewModel.currentSelectedTask.TaskNum + "]的任务吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				taskListFragment.doSomething("删除中", taskListFragment.TASK_DELETE);
			}
		});
	}

	/**
	 * 跳去任务无法勘察的操作界面
	 */
	private void showPauseDialog() {
		if (pauseDialog == null) {
			pauseDialog = DialogUtil.commonDialog(currentContext, R.layout.dialog_view_task_pause);
			android.view.WindowManager.LayoutParams params = pauseDialog.getWindow().getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.8));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				pauseDialog.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				pauseDialog.getWindow().setAttributes(params);
				break;
			}
			Button btnConfirm = (Button) pauseDialog.findViewById(R.id.dialog_button_ok);
			Button btnCancel = (Button) pauseDialog.findViewById(R.id.dialog_button_cancel);
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					EditText txtReason = (EditText) pauseDialog.findViewById(R.id.dialog_view_pause_reason);
					pauseDialog.dismiss();
					taskListFragment.viewModel.currentSelectedTask.Remark = txtReason.getText().toString().trim();
					taskListFragment.doSomething("保存任务暂停原因", taskListFragment.TASK_SETPAUSE);
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					pauseDialog.dismiss();
				}
			});
		}
		pauseDialog.show();
	}

	/**
	 * 收费信息
	 * 
	 * @param tempObject
	 */
	private void showSetFeeDialog() {
		if (feeDialog == null) {
			feeDialog = DialogUtil.commonDialog(currentContext, R.layout.dialog_view_fee);
			WindowManager.LayoutParams params = feeDialog.getWindow().getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.8));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				feeDialog.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				feeDialog.getWindow().setAttributes(params);
				break;
			}

			Button btnConfirm = (Button) feeDialog.findViewById(R.id.dialog_button_ok);
			Button btnCancel = (Button) feeDialog.findViewById(R.id.dialog_button_cancel);

			btnConfirm.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (!TaskOperator.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
						EditText txtFee = (EditText) feeDialog.findViewById(R.id.dialog_view_fee_txtFee);
						EditText txtReceiptNo = (EditText) feeDialog.findViewById(R.id.dialog_view_fee_txtReceiptNo);
						taskListFragment.viewModel.currentSelectedTask.ReceiptNo = String.valueOf(txtReceiptNo.getText());
						taskListFragment.viewModel.currentSelectedTask.Fee = txtFee.getText().toString().trim();
						taskListFragment.doSomething("保存任务收费信息", taskListFragment.TASK_SETFEEINFO);
						feeDialog.dismiss();
					} else {
						ToastUtil.longShow(taskListFragment.getActivity(), "当前任务正在提交中，将不能设置任务收费信息!");
					}
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					feeDialog.dismiss();
				}
			});
		}
		if (taskListFragment.viewModel.currentSelectedTask != null) {
			EditText txtFee = (EditText) feeDialog.findViewById(R.id.dialog_view_fee_txtFee);
			EditText txtReceiptNo = (EditText) feeDialog.findViewById(R.id.dialog_view_fee_txtReceiptNo);
			String showReceiptNo = taskListFragment.viewModel.currentSelectedTask.ReceiptNo == null || taskListFragment.viewModel.currentSelectedTask.ReceiptNo.equals("null") ? ""
					: taskListFragment.viewModel.currentSelectedTask.ReceiptNo.toString();
			txtReceiptNo.setText(showReceiptNo);
			String showFee = taskListFragment.viewModel.currentSelectedTask.Fee.equals("null") ? "" : taskListFragment.viewModel.currentSelectedTask.Fee.toString();
			txtFee.setText(showFee);
		}
		feeDialog.show();
	}

	/**
	 * 显示预约信息窗口
	 * 
	 * @param task任务信息
	 */
	private void showAppointmentDialog(final TaskMenuEnum menu) {
		if (appointmentDialog == null) {
			appointmentDialog = DialogUtil.commonDialog(currentContext, R.layout.dialog_view_appointment);
			WindowManager.LayoutParams params = appointmentDialog.getWindow().getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.9));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				appointmentDialog.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				appointmentDialog.getWindow().setAttributes(params);
				break;
			}
		}
		Button btnConfirm = (Button) appointmentDialog.findViewById(R.id.dialog_button_ok);
		Button btnCancel = (Button) appointmentDialog.findViewById(R.id.dialog_button_cancel);
		final EditText dialog_view_appointment_name = (EditText) appointmentDialog.findViewById(R.id.dialog_view_appointment_name);
		final EditText dialog_view_appointment_phone = (EditText) appointmentDialog.findViewById(R.id.dialog_view_appointment_phone);
		final EditText dialog_view_appointment_date = (EditText) appointmentDialog.findViewById(R.id.dialog_view_appointment_date);
		final EditText dialog_view_appointment_time = (EditText) appointmentDialog.findViewById(R.id.dialog_view_appointment_time);
		final EditText dialog_view_appointment_remark = (EditText) appointmentDialog.findViewById(R.id.dialog_view_appointment_remark);
		final DatePicker dialog_view_appointment_date2 = (DatePicker) appointmentDialog.findViewById(R.id.dialog_view_appointment_date2);
		final TimePicker dialog_view_appointment_time2 = (TimePicker) appointmentDialog.findViewById(R.id.dialog_view_appointment_time2);

		// resizePikcer(dialog_view_appointment_date2);//调整datepicker大小
		// resizePikcer(dialog_view_appointment_time2);//调整timepicker大小

		dialog_view_appointment_time2.setIs24HourView(true);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!TaskOperator.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
					taskListFragment.viewModel.currentSelectedTask.ContactPerson = dialog_view_appointment_name.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.ContactTel = dialog_view_appointment_phone.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.BookedDate = dialog_view_appointment_date.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.BookedTime = dialog_view_appointment_time.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.BookedRemark = dialog_view_appointment_remark.getText().toString().trim();
					taskListFragment.doSomething("保存预约信息中。。。", taskListFragment.TASK_APPOINTMENT, menu);
					appointmentDialog.dismiss();
				} else {
					ToastUtil.longShow(taskListFragment.getActivity(), "当前任务正在提交中，将不能设置预约信息!");
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				appointmentDialog.dismiss();
			}
		});

		dialog_view_appointment_time2.setOnTimeChangedListener(new OnTimeChangedListener() {
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				dialog_view_appointment_time.setText(hourOfDay + ":" + minute);
			}

		});

		OnDateChangedListener dateListener = new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				dialog_view_appointment_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
			}
		};

		if (taskListFragment.viewModel.currentSelectedTask != null) {
			TaskInfo task = TaskOperator.getLocTaskInfoCompleteByTaskNum(taskListFragment.viewModel.currentSelectedTask.TaskNum);
			dialog_view_appointment_name.setText(taskListFragment.viewModel.currentSelectedTask.ContactPerson == null ? "" : taskListFragment.viewModel.currentSelectedTask.ContactPerson);
			dialog_view_appointment_phone.setText(taskListFragment.viewModel.currentSelectedTask.ContactTel == null ? "" : taskListFragment.viewModel.currentSelectedTask.ContactTel);
			dialog_view_appointment_date.setText(task.BookedDate);
			dialog_view_appointment_time.setText(task.BookedTime);
			dialog_view_appointment_remark.setText(task.BookedRemark);

			if (task.BookedDate != null && task.BookedDate.length() > 0) {
				String[] date = task.BookedDate.split("-");
				dialog_view_appointment_date2.init(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), dateListener);
			} else {
				final Calendar c = Calendar.getInstance();
				Integer mYear = c.get(Calendar.YEAR);
				Integer mMonth = c.get(Calendar.MONTH);
				Integer mDay = c.get(Calendar.DAY_OF_MONTH);
				dialog_view_appointment_date2.init(mYear, mMonth, mDay, dateListener);
				dialog_view_appointment_date.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
			}
			if (task.BookedTime != null && task.BookedTime.length() > 0) {
				String[] time = task.BookedTime.split(":");
				dialog_view_appointment_time2.setCurrentHour(Integer.parseInt(time[0]));
				dialog_view_appointment_time2.setCurrentMinute(Integer.parseInt(time[1]));
			} else {
				final Calendar c = Calendar.getInstance();
				Integer mHour = c.get(Calendar.HOUR_OF_DAY);
				Integer mMinute = c.get(Calendar.MINUTE);
				dialog_view_appointment_time2.setCurrentHour(mHour);
				dialog_view_appointment_time2.setCurrentMinute(mMinute);
				dialog_view_appointment_time.setText(mHour + ":" + mMinute);
			}
		}
		appointmentDialog.show();
	}

	public void resizePikcer(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}

	/**
	 * 得到viewGroup里面的numberpicker组件
	 * 
	 * @param viewGroup
	 * @return
	 */
	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}
		return npList;
	}

	/*
	 * 调整numberpicker大小
	 */
	private void resizeNumberPicker(NumberPicker np) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 10, 0);
		np.setLayoutParams(params);
	}

	/**
	 * 把复制好的任务勘察信息粘贴到新的指定任务下面
	 */
	private void pastedCategories(final Boolean isCopyToNewTask) {
		if (taskListFragment.viewModel.currentSelectedTask != null) {
			if (!isCopyToNewTask && taskListFragment.viewModel.currentCopiedTask.TaskNum.equals(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				ToastUtil.longShow(currentContext, "不能把任务粘贴给自己");
			} else if (!isCopyToNewTask && taskListFragment.viewModel.currentCopiedTask.DDID !=

			taskListFragment.viewModel.currentSelectedTask.DDID) {
				ToastUtil.longShow(currentContext, "任务所属勘察表不一致，无法继续");
			} else {
				String titleString;

				if (isCopyToNewTask) {
					titleString = "您确认把复制的勘察数据粘贴到新建任务中吗?";
				} else {
					titleString = "您确认粘贴到编号为[" + taskListFragment.viewModel.currentSelectedTask.TaskNum + "]任务中吗";
				}

				DialogUtil.showConfirmationDialog(currentContext, titleString, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

						if (isCopyToNewTask) {
							taskListFragment.doSomething("加载中...",

							taskListFragment.TASK_PASTED_NEWTASKINFO);
						} else {
							taskListFragment.doSomething("粘贴中...",

							taskListFragment.TASK_PASTED_TASKINFO);
						}
					}
				});
			}

		}
	}

	/**
	 * 点击查看任务信息的操作
	 */
	private void showCurrentSelectedTaskInfo() {
		LayoutInflater inflater = LayoutInflater.from(currentContext);
		View dialog_view_detail = inflater.inflate(R.layout.dialog_view_task_detail, null);
		detailDialog = DialogUtil.getDetailDialog(currentContext, dialog_view_detail);
		final TaskInfo taskInfo = taskListFragment.viewModel.currentSelectedTask;
		// TextView dialog_title = (TextView)
		// dialog_view_detail.findViewById(R.id.dialog_title);
		TextView taskNum_txt = (TextView) dialog_view_detail.findViewById(R.id.taskNum_txt);
		TextView address_txt = (TextView) dialog_view_detail.findViewById(R.id.address_txt);
		TextView residentialArea_txt = (TextView) dialog_view_detail.findViewById(R.id.residentialArea_txt);
		TextView building_txt = (TextView) dialog_view_detail.findViewById(R.id.building_txt);
		TextView floor_txt = (TextView) dialog_view_detail.findViewById(R.id.floor_txt);
		TextView targetArea_txt = (TextView) dialog_view_detail.findViewById(R.id.targetArea_txt);
		TextView owner_txt = (TextView) dialog_view_detail.findViewById(R.id.owner_txt);
		TextView ownerTel_txt = (TextView) dialog_view_detail.findViewById(R.id.ownerTel_txt);
		TextView targetName_txt = (TextView) dialog_view_detail.findViewById(R.id.targetName_txt);
		TextView clientUnit_txt = (TextView) dialog_view_detail.findViewById(R.id.clientUnit_txt);
		TextView clientDep_txt = (TextView) dialog_view_detail.findViewById(R.id.clientDep_txt);
		TextView clientName_txt = (TextView) dialog_view_detail.findViewById(R.id.clientName_txt);
		TextView clientTel_txt = (TextView) dialog_view_detail.findViewById(R.id.clientTel_txt);
		TextView remark_txt = (TextView) dialog_view_detail.findViewById(R.id.remark_txt);
		Button btn_confirm = (Button) dialog_view_detail.findViewById(R.id.btn_confirm);

		if (taskNum_txt != null && taskInfo.TaskNum.length() > 0) {
			taskNum_txt.setText("任务编号：" + taskInfo.TaskNum);
		}
		if (address_txt != null && taskInfo.TargetAddress.length() > 0) {
			address_txt.setText("地址：" + taskInfo.TargetAddress);
		}
		if (residentialArea_txt != null && taskInfo.ResidentialArea.length() > 0) {
			residentialArea_txt.setText("小区名称：" + taskInfo.ResidentialArea);
		}
		if (building_txt != null && taskInfo.Building.length() > 0) {
			building_txt.setText("楼栋名称：" + taskInfo.Building);
		}
		if (floor_txt != null && taskInfo.Floor.length() > 0) {
			floor_txt.setText("所在楼层：" + taskInfo.Floor);
		}
		if (targetArea_txt != null && taskInfo.TargetArea.length() > 0) {
			targetArea_txt.setText("建筑面积：" + taskInfo.TargetArea);
		}
		if (owner_txt != null && taskInfo.Owner.length() > 0) {
			owner_txt.setText("所属业主：" + taskInfo.Owner);
		}
		if (ownerTel_txt != null && taskInfo.OwnerTel.length() > 0) {
			ownerTel_txt.setText("业主电话：" + taskInfo.OwnerTel);
		}
		if (targetName_txt != null && taskInfo.TargetName.length() > 0) {
			targetName_txt.setText("目标名称：" + taskInfo.TargetName);
		}
		if (clientUnit_txt != null && taskInfo.ClientUnit.length() > 0) {
			clientUnit_txt.setText("委托人单位：" + taskInfo.ClientUnit);
		}
		if (clientDep_txt != null && taskInfo.ClientDep.length() > 0) {
			clientDep_txt.setText("委托人部门：" + taskInfo.ClientDep);
		}
		if (clientName_txt != null && taskInfo.ClientName.length() > 0) {
			clientName_txt.setText("委托人名称：" + taskInfo.ClientName);
		}
		if (clientTel_txt != null && taskInfo.ClientTel.length() > 0) {
			clientTel_txt.setText("委托人联系电话：" + taskInfo.ClientTel);
		}
		if (remark_txt != null && taskInfo.Remark.length() > 0) {
			remark_txt.setText("标注：" + taskInfo.Remark);
		}

		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				detailDialog.dismiss();
			}
		});

		if (detailDialog.isShowing()) {
			detailDialog.dismiss();
		} else {
			detailDialog.show();
		}
	}

	/**
	 * 数据检查
	 */
	private void taskExportInfo() {
		taskListFragment.doSomething("检查中...", taskListFragment.TASK_EXPORT);
	}

	/**
	 * 跳转到数据导入
	 */
	private void taskImportInfo() {
		taskListFragment.doSomething("", taskListFragment.TASK_IMPORT_GUIDE);
	}

	// {{ showCopyCategoriesDialog

	/**
	 * 执行点击复制后的操作
	 */
	private void showCopyCategoriesDialog() {
		if (taskListFragment.viewModel.currentSelectedTask != null) {
			TaskInfo selectedTask = taskListFragment.viewModel.currentSelectedTask;
			Boolean isCreatedByUser = selectedTask.IsNew;
			selectedTask.Categories = TaskDataWorker.queryTaskCategories(isCreatedByUser ? selectedTask.ID : selectedTask.TaskID, isCreatedByUser, false).Data;

			categoryItems = new LinkedHashMap<String, String>();
			for (TaskCategoryInfo taskCategory : selectedTask.Categories) {
				categoryItems.put(String.valueOf(taskCategory.ID), taskCategory.RemarkName);
			}

			LayoutInflater inflater = LayoutInflater.from(currentContext);
			View view = inflater.inflate(R.layout.dialog_multiplechoice, null);
			CustomMultipleChoiceView mutipleChoiceView = (CustomMultipleChoiceView) view.findViewById(R.id.CustomMultipleChoiceView);
			mutipleChoiceView.setData(categoryItems, null);
			mutipleChoiceView.selectAll();
			mutipleChoiceView.setTitle("选择要复制的分组");
			// 加载适应屏幕的PopupWindow
			loadSuitableDialog(view);
			// 确定
			mutipleChoiceView.setOnSelectedListener(new onSelectedListener() {
				@Override
				public void onSelected(SparseBooleanArray sparseBooleanArray) {
					stationSelectDialog.dismiss();
					// 记录选中的复制项
					HashMap<String, String> selectedItems = new HashMap<String, String>();
					Object[] keys = categoryItems.keySet().toArray();
					for (int i = 0; i < sparseBooleanArray.size(); i++) {
						if (sparseBooleanArray.get(i)) {
							selectedItems.put(keys[i].toString(), categoryItems.get(keys[i]));
						}
					}
					// 保存选中复制的分类
					taskListFragment.viewModel.selectedCategoryItems = selectedItems;
					if (selectedItems.size() > 0) {
						// 点确定之后，把当前要的任务保存
						taskListFragment.viewModel.currentCopiedTask = taskListFragment.viewModel.currentSelectedTask;
						setDoingTaskinfoMenuCopyValue(selectedItems);
						ToastUtil.longShow(currentContext, "复制完成，您可以选择粘贴操作");
					} else {
						taskListFragment.viewModel.currentCopiedTask = null;
						ToastUtil.longShow(currentContext, "没有指定任务复制项");
					}
				}
			});
			// 取消
			mutipleChoiceView.setOnCancelListener(new onCancelListener() {
				@Override
				public void onCancel() {
					stationSelectDialog.dismiss();
				}
			});
		}
		stationSelectDialog.showAtLocation(taskListFragment.mView, Gravity.CENTER, 0, 0);
	}

	/**
	 * 将已完成的复制值赋值给待提交中的复制值
	 */
	private void setDoingTaskinfoMenuCopyValue(HashMap<String, String> selectedItems) {
		TaskListMenuOperaotr taskListMenuOperaotr = taskListFragment.viewModel.homeActivity.taskListMenuOperaotrs.get(TaskStatus.Doing);
		if (taskListMenuOperaotr != null) {
			taskListMenuOperaotr.taskListFragment.viewModel.selectedCategoryItems = selectedItems;
			if (selectedItems.size() > 0) {
				taskListMenuOperaotr.taskListFragment.viewModel.currentCopiedTask = taskListFragment.viewModel.currentSelectedTask;
			}
		}
	}

	/**
	 * 加载适应屏幕的视图
	 * 
	 * @param view
	 *            :绑定的视图
	 */
	private void loadSuitableDialog(View view) {
		// 取得屏幕的宽高
		Point point = WinDisplay.getWidthAndHeight(currentContext);
		switch (EIASApplication.PageSize) {
		case 6:// 手机
			if (categoryItems.size() >= 0 && categoryItems.size() < 6) {
				stationSelectDialog = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			} else {
				stationSelectDialog = new PopupWindow(view, LayoutParams.WRAP_CONTENT, (int) ((point.y) * (0.7)), true);
			}
			break;
		case 15:// 平板
			if (categoryItems.size() >= 0 && categoryItems.size() < 8) {
				stationSelectDialog = new PopupWindow(view, (int) ((point.x) * (0.65)), LayoutParams.WRAP_CONTENT, true);
			} else {
				stationSelectDialog = new PopupWindow(view, (int) ((point.x) * (0.65)), (int) ((point.y) * (0.6)), true);
			}
			break;
		}
	}

	// }}

	// {{ 提交当前任务

	/**
	 * 批量上传任务
	 * 
	 * @param finalIsRepeat
	 *            是否重复
	 * @param ddid
	 *            勘察表ID
	 * @param unWriteItems
	 *            不写入项
	 * @param taskInfo
	 *            任务信息
	 */
	private void commitTasks(final List<TaskInfo> submitTaskInfos) {
		String feeMsg = "";
		String taskReportMsg = "";
		for (TaskInfo taskItem : submitTaskInfos) {
			if (taskItem.Fee == null || taskItem.Fee.equals("") || taskItem.Fee.equals("null")) {
				feeMsg += taskItem.TargetAddress + ",";
			}
			if (taskItem.InworkReportFinish) {
				taskReportMsg += "[" + taskItem.TaskNum + "]";
			}
		}
		if (!taskReportMsg.equals("")) {
			taskListFragment.viewModel.homeActivity.appHeader.showDialog("提示信息", "编号为" + taskReportMsg + "的任务已经完成报告，不能继续提交");
		} else {
			// 判断收费是否存在
			if (!feeMsg.equals("")) {
				// 移除最后一个逗号
				feeMsg = feeMsg.substring(0, feeMsg.length() - 1);
				if (homeActivity != null) {
					currentContext = homeActivity;
				} else if (taskInfoActivity != null) {
					currentContext = taskInfoActivity;
				}
				DialogUtil.showConfirmationDialog(currentContext, feeMsg + " 的收费信息尚未填写,是否确定提交任务?", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (TaskInfo taskInfo : submitTaskInfos) {
							checkTaskFile(taskInfo.DDID, taskInfo.TaskNum);
						}
					}
				});
			} else {
				for (TaskInfo taskInfo : submitTaskInfos) {
					checkTaskFile(taskInfo.DDID, taskInfo.TaskNum);
				}
			}
		}
	}

	/**
	 * 检查并上传任务
	 * 
	 * @param ddid任务使用的勘察表
	 * @param taskNum任务编号
	 * @param fee费用
	 */
	public void putTaskInfo(final Integer ddid, final String taskNum, String fee) {
		// 判断收费是否存在
		if (fee == null || fee.equals("") || fee.equals("null")) {
			currentContext = homeActivity != null ? homeActivity : taskInfoActivity;
			DialogUtil.showConfirmationDialog(currentContext, "收费信息尚未填写，是否确定提交任务?", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					checkTaskFile(ddid, taskNum);
				}
			});
		} else {
			checkTaskFile(ddid, taskNum);
		}
	}

	/**
	 * 上传任务方法
	 * 
	 * @param finalIsRepeat
	 *            是否重复
	 * @param ddid
	 *            勘察表ID
	 * @param unWriteItems
	 *            不写入项
	 * @param taskInfo
	 *            任务信息
	 */
	private void checkTaskFile(Integer ddid, String taskNum) {
		TaskInfo taskInfo = TaskOperator.getLocTaskInfoCompleteByTaskNum(taskNum);
		String taskRoot = EIASApplication.projectRoot + taskInfo.TaskNum;
		// 检测任务信息是否正确
		ArrayList<DialogTipsDTO> taskFormat = TaskOperator.taskCheckFormat(taskInfo, taskRoot);
		// 若任务信息不正确则直接提示并返回
		if (taskFormat != null && taskFormat.size() > 0) {
			if (homeActivity != null) {
				homeActivity.appHeader.showDialogResult("请修正以下信息：", taskFormat, false, null);
			} else if (taskInfoActivity != null) {
				taskInfoActivity.appHeader.showDialogResult("请修正以下信息：", taskFormat, false, null);
			}
		} else {
			ArrayList<TaskCategoryInfo> unWriteItems = TaskOperator.checkHasValue(ddid, taskInfo);
			if (unWriteItems.size() > 0) {
				LayoutInflater inflater = LayoutInflater.from(currentContext);
				View dialog_view_task_submit = inflater.inflate(R.layout.dialog_view_task_submit, null);
				detailDialog = DialogUtil.getDetailDialog(currentContext, dialog_view_task_submit);
				TextView dialog_taskNumInfo = (TextView) dialog_view_task_submit.findViewById(R.id.dialog_taskNumInfo);
				Button btn_confirm = (Button) dialog_view_task_submit.findViewById(R.id.btn_confirm);
				ListView bdialog_tipsinfo = (ListView) dialog_view_task_submit.findViewById(R.id.bdialog_tipsinfo);

				taskunwriteadapter = new TaskUnWriteAdapter(currentContext, unWriteItems);
				bdialog_tipsinfo.setAdapter(taskunwriteadapter);
				dialog_taskNumInfo.setText("任务编号[" + taskInfo.TaskNum + "]小区名:" + taskInfo.ResidentialArea + "地址:" + taskInfo.TargetAddress);
				btn_confirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						detailDialog.dismiss();
					}
				});
				if (detailDialog.isShowing()) {
					detailDialog.dismiss();
				} else {
					detailDialog.show();
				}
			} else {
				taskListFragment.refreshList(taskInfo.TaskNum);
				TaskOperator.taskSubmiting(taskInfo);
			}
		}
	}

	/**
	 * 是否有新的勘察表
	 * 
	 * @param ddid勘察配置表ID
	 * @return
	 */
	private boolean hasNewDataDefines(int ddid) {
		for (DataDefine dataDefine : EIASApplication.currentUpdateDataDefines) {
			if (dataDefine.DDID == ddid) {
				taskInfoActivity.appHeader.showDialog("提示信息", "请同步勘察表[" + dataDefine.Name + "]");
				return true;
			}
		}
		return false;
	}

	/**
	 * 已经完成报告的任务不能提交
	 * 
	 * @param taskInfo
	 * @return
	 */
	private boolean checkTaskReportFinish(TaskInfo taskInfo) {
		if (taskInfo.InworkReportFinish) {
			taskListFragment.viewModel.homeActivity.appHeader.showDialog("提示信息", taskInfo.TaskNum + "已经完成报告的任务不能提交!");
			return true;
		}
		return false;
	}

	// }}

	// {{ 移除任务资源

	/**
	 * 移除任务资源
	 */
	private void removeTaskResource() {
		taskListFragment.doSomething("", taskListFragment.TASK_REMOVE_RESOURCE);
	}

	// }}

	// {{ 查询已经完成报告的任务

	/**
	 * 查询已经完成报告的任务
	 */
	private void queryFinishReport() {
		taskListFragment.viewModel.onlyReportFinish = true;
		taskListFragment.loadData();
	}

	// }}

	// {{差全部已完成任务

	/**
	 * 全部已完成任务
	 */
	private void queryFinishTask() {
		taskListFragment.viewModel.onlyReportFinish = false;
		taskListFragment.loadData();
	}

	// }}
}