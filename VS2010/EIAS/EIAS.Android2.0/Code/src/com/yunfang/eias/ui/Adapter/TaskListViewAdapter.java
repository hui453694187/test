package com.yunfang.eias.ui.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.enumObj.TaskUploadStatusEnum;
import com.yunfang.eias.enumObj.UrgentStatusEnum;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DateTimeUtil;

/*import android.widget.ProgressBar;*/

/**
 * 
 * 项目名称：WaiCai 类名称：TaskDoingListViewAdapter 类描述：待提交模块的ListView所用适配器绑定的ListView项;
 * 创建人：lihc 创建时间：2014-4-17 下午3:07:14
 * 
 * @version
 */
public class TaskListViewAdapter extends ArrayAdapter<TaskInfo> {

	// {{
	/**
	 * 任务信息数据
	 * */
	private ArrayList<TaskInfo> taskInfoes;

	/**
	 * ListView的Item项布局的资源ID
	 * */
	private int itemRID;

	/**
	 * 视图膨胀器
	 * */
	private LayoutInflater mInflater;
	
	private Context context;

	/**
	 * 选中的位置
	 * */
	@SuppressWarnings("unused")
	private int selectedPosition = -1;

	// }}

	public TaskListViewAdapter(Context context, int textViewResourceId, ArrayList<TaskInfo> objects) {
		super(context, textViewResourceId, objects);
		mInflater = LayoutInflater.from(context);
		taskInfoes = objects;
		itemRID = textViewResourceId;
		this.context=context;
	}

	@Override
	public int getCount() {
		return taskInfoes.size();
	}

	@Override
	public TaskInfo getItem(int position) {
		return taskInfoes.get(position);
	}

	@Override
	public int getPosition(TaskInfo item) {
		return taskInfoes.indexOf(item);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 设置选中的位置
	 * 
	 * @param position
	 */
	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder;
		final TaskInfo item = taskInfoes.get(position);
		try {
			if (convertView == null) {
				holder = new ViewHolder();
				view = mInflater.inflate(itemRID, null);
				// switch (itemRID) {
				// case R.layout.task_listview_item_todo:// 待领取
				// break;
				// case R.layout.task_listview_item_doing:// 待提交
				// break;
				// case R.layout.task_listview_item_done:// 已完成
				// break;
				// case R.layout.task_listview_item_submiting:
				// break;
				// default:
				// break;
				// }
				holder.raName_Txt = (TextView) view.findViewById(R.id.RAName_Txt);
				holder.status_Txt = (TextView) view.findViewById(R.id.status_Txt);
				holder.fee_Txt = (TextView) view.findViewById(R.id.fee_Txt);
				holder.createtime_Txt = (TextView) view.findViewById(R.id.createtime_Txt);
				holder.TargetAddress = (TextView) view.findViewById(R.id.TargetAddress);
				holder.TaskNum_Txt = (TextView) view.findViewById(R.id.TaskNum_Txt);
				holder.receiveDateTime_Txt = (TextView) view.findViewById(R.id.receiveDateTime_Txt);
				holder.user_Txt = (TextView) view.findViewById(R.id.user_Txt);
				holder.doneTime_Txt = (TextView) view.findViewById(R.id.doneTime_Txt);
				holder.uploadTimes_Txt = (TextView) view.findViewById(R.id.uploadTimes_Txt);
				holder.latestUploadDate_Txt = (TextView) view.findViewById(R.id.latestUploadDate_Txt);
				holder.uploadStatus_Txt = (TextView) view.findViewById(R.id.uploadStatus_Txt);
				holder.defineData_Txt = (TextView) view.findViewById(R.id.defineData_Txt);
				holder.bookedDate_Txt = (TextView) view.findViewById(R.id.bookedDate_Txt);
				holder.bookedTime_Txt = (TextView) view.findViewById(R.id.bookedTime_Txt);
				holder.contactPerson_Txt = (TextView) view.findViewById(R.id.contactPerson_Txt);
				holder.contactTel_Txt = (TextView) view.findViewById(R.id.contactTel_Txt);
				holder.checkTaskInfo = (CheckBox) view.findViewById(R.id.checkTaskInfo);
				holder.txt_submiting = (TextView) view.findViewById(R.id.txt_submiting);
				holder.task_list_view_linelayout = (LinearLayout) view.findViewById(R.id.task_list_view_linelayout);
				holder.rl_done_report_finish = (RelativeLayout) view.findViewById(R.id.rl_done_report_finish);
				holder.doneResource = (TextView) view.findViewById(R.id.doneResource);
				holder.urgent_fee = (TextView) view.findViewById(R.id.urgent_fee);
				holder.liveSearchCharge = (TextView) view.findViewById(R.id.liveSearchCharge);
				holder.rl_urgent_fee = (RelativeLayout) view.findViewById(R.id.rl_urgent_fee);
				// 设置标记
				view.setTag(holder);
			} else {// 重用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			// 小区名称
			if (holder.raName_Txt != null && item.ResidentialArea != null) {
				holder.raName_Txt.setText(item.ResidentialArea);
			}
			// 任务状态
			if (holder.status_Txt != null) {
				if (item.UrgentStatus == UrgentStatusEnum.Urgent) {// 紧急
					holder.status_Txt.setText("急");
					holder.status_Txt.setBackgroundColor(view.getResources().getColor(R.color.statue_active));
				} else {
					holder.status_Txt.setText("普");
					holder.status_Txt.setBackgroundColor(view.getResources().getColor(R.color.statue_normal));
				}
			}
			// 任务收费
			if (holder.fee_Txt != null) {
				if (item.Fee == null || item.Fee.equals(EIASApplication.DefaultNullString)) {
					item.Fee = "";
				}
				if (item.Fee != null && item.Fee.length() > 0) {
					if (item.IsNew) {// 自建任务
						holder.fee_Txt.setText(item.Fee + " -(自建)");
					} else {
						holder.fee_Txt.setText(item.Fee);
					}
				} else {
					if (item.IsNew) {// 自建任务
						holder.fee_Txt.setText("未设置 -(自建)");
					} else {
						holder.fee_Txt.setText("未设置");
					}
				}
			}
			// 创建时间
			if (holder.createtime_Txt != null && item.CreatedDate != null) {
				holder.createtime_Txt.setText(DateTimeUtil.converTime(item.CreatedDate));
			}
			// 地址
			if (holder.TargetAddress != null && item.TargetAddress != null) {
				if (item.BookedRemark == null || item.BookedRemark.trim().equals("")) {
					holder.TargetAddress.setText(item.TargetAddress);
				} else {
					holder.TargetAddress.setText(item.TargetAddress + "(" + item.BookedRemark + ")");
				}
			}
			// 任务编号
			if (holder.TaskNum_Txt != null && item.TaskNum != null) {
				holder.TaskNum_Txt.setText("编号:" + item.TaskNum);
				if(item.Status==TaskStatus.Pause){
					holder.TaskNum_Txt.setTextColor(context.getResources().getColor(R.color.red));
				}
			}
			// 任务领取时间
			if (holder.receiveDateTime_Txt != null && item.ReceiveDate != null) {
				if (item.ReceiveDate == null || item.ReceiveDate.equals("") || item.ReceiveDate.equals(EIASApplication.DefaultNullString)) {
					holder.receiveDateTime_Txt.setText("");
				} else {
					holder.receiveDateTime_Txt.setText(DateTimeUtil.converTime(item.ReceiveDate));
				}
			}
			// 领取人
			if (holder.user_Txt != null && item.User != null) {
				holder.user_Txt.setText(item.User);
			}
			// 完成时间
			if (holder.doneTime_Txt != null && item.DoneDate != null && !item.DoneDate.equals("null")) {
				if (item.DoneDate.equals("") || item.DoneDate.equals(EIASApplication.DefaultNullString)) {
					holder.doneTime_Txt.setText("");
				} else {
					holder.doneTime_Txt.setText(DateTimeUtil.converTime(item.DoneDate));
				}
			}
			// 上传次数
			if (holder.uploadTimes_Txt != null) {
				if (item.UploadTimes != 0) {
					holder.uploadTimes_Txt.setText(String.valueOf(item.UploadTimes));
				} else {
					holder.uploadTimes_Txt.setText("");
				}
			}
			// 最后上传日期
			if (holder.latestUploadDate_Txt != null && item.ReceiveDate != null) {
				if (item.ReceiveDate != null && !item.ReceiveDate.equals("") && item.LatestUploadDate != null && !item.LatestUploadDate.equals("")) {
					holder.latestUploadDate_Txt.setText(DateTimeUtil.UsedTime(item.ReceiveDate, item.LatestUploadDate));
				} else {
					holder.latestUploadDate_Txt.setText("");
				}
			}
			// 上传状态
			if (holder.uploadStatus_Txt != null && item.UploadStatusEnum != null) {
				if (item.UploadStatusEnum != null) {
					if (item.UploadStatusEnum == TaskUploadStatusEnum.Submiting) { // 提交中
						holder.uploadStatus_Txt.setTextColor(Color.RED);
					}
					holder.uploadStatus_Txt.setText(TaskUploadStatusEnum.getName(item.UploadStatusEnum.getIndex()));
				}
			}
			// 勘察表名称
			if (holder.defineData_Txt != null) {
				ResultInfo<DataDefine> dataDefineResult = null;
				if (item.DDID > 0) {
					dataDefineResult = DataDefineWorker.queryDataDefineByDDID(item.DDID);
				}
				if (dataDefineResult != null && dataDefineResult.Success && dataDefineResult.Data != null) {
					holder.defineData_Txt.setText(dataDefineResult.Data.Name);
				} else {
					holder.defineData_Txt.setText("勘察表不存在");
				}
			}
			// 预约日期
			if (holder.bookedDate_Txt != null && item.BookedDate != null) {
				holder.bookedDate_Txt.setText(item.BookedDate);
			}
			// 预约时间
			if (holder.bookedTime_Txt != null && item.BookedTime != null) {
				holder.bookedTime_Txt.setText(item.BookedTime);
			}
			// 预约人
			if (holder.contactPerson_Txt != null && item.ContactPerson != null) {
				holder.contactPerson_Txt.setText(item.ContactPerson);
			}
			// 预约人联系电话
			if (holder.contactTel_Txt != null && item.ContactTel != null) {
				holder.contactTel_Txt.setText(item.ContactTel);
			}
			// 是否被勾选
			if (holder.checkTaskInfo != null) {
				if (item.InworkReportFinish || item.Status == TaskStatus.Submiting) {
					holder.checkTaskInfo.setVisibility(View.GONE);
				} else {
					holder.checkTaskInfo.setVisibility(View.VISIBLE);
					holder.checkTaskInfo.setChecked(item.isChecked);
					holder.checkTaskInfo.setFocusable(false);
					holder.checkTaskInfo.setFocusableInTouchMode(false);
					final LinearLayout layout = (LinearLayout) view.findViewById(R.id.task_listview_item_layout);
					if (layout != null) {
						if (item.isChecked) {
							layout.setBackgroundColor(EIASApplication.getInstance().getResources().getColor(R.color.bg_item_choose));
						} else {
							layout.setBackgroundColor(Color.TRANSPARENT);
						}
						holder.checkTaskInfo.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								CheckBox checkBox = (CheckBox) v;
								item.isChecked = checkBox.isChecked();
								if (item.isChecked) {
									layout.setBackgroundColor(EIASApplication.getInstance().getResources().getColor(R.color.bg_item_choose));
								} else {
									layout.setBackgroundColor(Color.TRANSPARENT);
								}
							}
						});
					}
				}
			}
			// 提交中
			if (holder.txt_submiting != null && holder.task_list_view_linelayout != null) {
				if (item.Status == TaskStatus.Done || item.Status == TaskStatus.Doing) {
					holder.txt_submiting.setVisibility(View.GONE);
					holder.task_list_view_linelayout.setVisibility(View.VISIBLE);
				} else if (item.Status == TaskStatus.Submiting) {
					holder.txt_submiting.setVisibility(View.VISIBLE);
					holder.task_list_view_linelayout.setVisibility(View.GONE);
				}
			}
			// 报告是否完成 资源是否清除 标记
			if (holder.rl_done_report_finish != null && holder.doneResource != null) {
				if (item.Status == TaskStatus.Done && item.InworkReportFinish) {
					holder.rl_done_report_finish.setVisibility(View.VISIBLE);
					if (item.HasResource) {
						holder.doneResource.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.greenblack));
						holder.doneResource.setText("资源文件未清除");
					} else {
						holder.doneResource.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.redblack));
						holder.doneResource.setText("资源文件已被清除");
					}
				}
			}
			// 加急费用
			if (holder.urgent_fee != null && item.UrgentFee >0) {
				if (item.UrgentFee <= 0) {
					holder.urgent_fee.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.gray));
				}
				holder.urgent_fee.setText(String.valueOf(item.UrgentFee));
			}
			// 预收费用
			if (holder.liveSearchCharge != null && item.LiveSearchCharge >0) {
				if (item.LiveSearchCharge <= 0) {
					holder.liveSearchCharge.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.gray));
				}
				holder.liveSearchCharge.setText(String.valueOf(item.LiveSearchCharge));
			}
			// 加急收费金额的容器
			if (holder.rl_urgent_fee != null) {
				if ((item.UrgentFee >0 && item.UrgentFee  > 0) || (item.LiveSearchCharge >0 && item.LiveSearchCharge > 0)) {
					holder.rl_urgent_fee.setVisibility(View.VISIBLE);
				}
			}
			// 勘察使用时间
			if (holder.usedTime_Txt != null) {
				if (item.ReceiveDate != null && item.DoneDate != null && !item.DoneDate.equals("null") && item.ReceiveDate.length() > 0 && item.DoneDate.length() > 0) {
					try {
						holder.usedTime_Txt.setText(DateTimeUtil.UsedTime(item.ReceiveDate, item.DoneDate));
					} catch (Exception e) {
						Log.i("TaskListViewAdapter->usedTime_Txt", e.getMessage());
						e.printStackTrace();
					}
				}
			}
			// 派发时间
			if (holder.deliverytime_Txt != null) {
				try {
					holder.deliverytime_Txt.setText(DateTimeUtil.converTime(item.CreatedDate));
				} catch (Exception e) {
					Log.i("TaskListViewAdapter->deliverytime_Txt", e.getMessage());
				}
			}
		} catch (Exception e) {
			DataLogOperator.other(itemRID + "=>" + item.TaskNum + ">" + e.getMessage());
		}
		return view;
	}
}

/**
 * 视图句柄
 * */
class ViewHolder {
	/**
	 * 任务状态
	 */
	TextView status_Txt;

	/**
	 * 任务收费信息
	 */
	TextView fee_Txt;

	/**
	 * 任务在当前设备的创建时间
	 */
	TextView createtime_Txt;

	/**
	 * 地址信息
	 */
	TextView TargetAddress;

	/**
	 * 小区名称
	 */
	TextView raName_Txt;

	/**
	 * 任务编号
	 */

	TextView TaskNum_Txt;
	/**
	 * 任务领取时间
	 */
	TextView receiveDateTime_Txt;

	/**
	 * 任务领取人信息
	 */
	TextView user_Txt;

	/**
	 * 任务完成时间
	 */
	TextView doneTime_Txt;

	/**
	 * 任务花费时间
	 */
	TextView usedTime_Txt;

	/**
	 * 任务提交次数
	 */
	TextView uploadTimes_Txt;

	/**
	 * 提交时间
	 */
	TextView latestUploadDate_Txt;

	/**
	 * 上传状态
	 */
	TextView uploadStatus_Txt;

	/**
	 * 派发时间
	 */
	TextView deliverytime_Txt;

	/**
	 * 勘察表名称
	 */
	TextView defineData_Txt;

	// 新增字段
	/**
	 * 勘察预约日期
	 */
	TextView bookedDate_Txt;

	/**
	 * 勘察预约时间
	 */
	TextView bookedTime_Txt;

	/**
	 * 联系人
	 */
	TextView contactPerson_Txt;

	/**
	 * 联系人电话
	 */
	TextView contactTel_Txt;

	/**
	 * 任务选取复选框
	 */
	CheckBox checkTaskInfo;

	/**
	 * 报告是否完成 资源是否清除 标记
	 */
	RelativeLayout rl_done_report_finish;

	/**
	 * 资源已经被清除
	 */
	TextView doneResource;

	/**
	 * 加急收费金额
	 */
	TextView urgent_fee;

	/**
	 * 预收费用
	 */
	TextView liveSearchCharge;

	/**
	 * 加急收费金额的容器
	 */
	RelativeLayout rl_urgent_fee;

	/**
	 * 提交中
	 */
	TextView txt_submiting;

	/**
	 * 任务其他信息
	 */
	LinearLayout task_list_view_linelayout;

	/*	*//**
	 * 备注
	 */
	/*
	 * TextView bookedRemark_Txt;
	 */
}
