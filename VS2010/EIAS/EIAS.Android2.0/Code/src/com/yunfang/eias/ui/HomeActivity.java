package com.yunfang.eias.ui;

import java.util.HashMap;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.yunfang.eias.R;
import com.yunfang.eias.base.MainService;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.logic.TaskListMenuOperaotr;
import com.yunfang.framework.base.BaseWorkerFragmentActivity;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.ToastUtil;

/*
 * 主界面
 */
public class HomeActivity extends BaseWorkerFragmentActivity implements OnCheckedChangeListener {
	// {{ 任务变量
	/**
	 * 检测版本
	 */
	private final int TASK_CHECK_VERSION = 1;
	// }}

	// {{相关变量

	/**
	 * 当前所在fragment中的名字
	 */
	private int currentPageIndex = 0;

	/**
	 * 记录选项卡以外的fragmentName
	 */
	@SuppressWarnings("unused")
	private String currentFragmentName = "";

	/**
	 * 在屏幕开始碰触的水平位置
	 * */
	private float touchStartX;

	/**
	 * 在屏幕开始碰触的垂直位置
	 * */
	private float touchStartY;

	/**
	 * 移动的X位置
	 */
	public float moveX = 0;

	/**
	 * 移动的Y位置
	 */
	public float moveY = 0;

	/**
	 * 存放当前列表名称与选择列表的选择框列表
	 */
	public HashMap<TaskStatus, TaskListMenuOperaotr> taskListMenuOperaotrs = new HashMap<TaskStatus, TaskListMenuOperaotr>();

	// }}

	// {{ 相关常量

	/**
	 * 退出等待间隔时间
	 */
	long waitTime = 2000;

	/**
	 * 退出等待碰触次数
	 */
	long touchTime = 0;

	/**
	 * 允许在屏幕触发左滑动和右滑动的滑动距离
	 * */
	public final float TOUCH_DISTANCE = 20;

	/**
	 * 可以滑动的总数
	 * */
	private int[] touchIdArray = new int[] { R.id.rb_home, R.id.rb_survey, R.id.rb_submit, R.id.rb_finish, R.id.rb_submiting };

	// }}

	// {{ 界面相关

	/**
	 * 系统头部控件
	 */
	public AppHeader appHeader;

	/**
	 * 主界面下面的单选框
	 */
	private RadioGroup radioGroup;

	/**
	 * 当前所在的Fragment
	 */
	private Fragment currentFragment;

	/**
	 * 当前的布局容器
	 */
	private LinearLayout homeLayout;

	/**
	 * 任务匹配的界面名称
	 */
	public final String taskMatchFragmentName = "TaskMatchFragment";

	// }}

	/**
	 * 
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		case TASK_CHECK_VERSION:
			msg.obj = true;
			break;
		default:
			showToast("没有找到任务执行的操作函数");
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/**
	 * 
	 */
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch (msg.what) {
		case TASK_CHECK_VERSION:
			appHeader.downloadTipsDialog("");
			// appHeader.checkSDCardHasSize();
			break;
		default:
			showToast("没有找到任务执行的操作函数");
			break;
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		homeLayout = (LinearLayout) findViewById(R.id.home_Layout);
		homeLayout.setOnTouchListener(menuBodyOnTouchListener);
		radioGroup.setOnCheckedChangeListener(this);
		changFragment(1);
		initView();
	}

	/**
	 * 
	 */
	private void initView() {
		appHeader = new AppHeader(this, R.id.home_title);
		Message msg = new Message();
		msg.what = TASK_CHECK_VERSION;
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 
	 */
	private OnTouchListener menuBodyOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return touchPage(event.getAction(), event.getX(), event.getY(), true);
		}
	};

	/**
	 * 滑动到指定的界面
	 * 
	 * @param action
	 *            ：操作事件类型
	 * @param x
	 *            :x轴的坐标
	 * @param y
	 *            :y轴的坐标
	 * @param defaultResult
	 *            :默认显示的值
	 * @return
	 */
	public boolean touchPage(int action, float x, float y, boolean defaultResult) {
		boolean result = defaultResult;
		int toIndex = currentPageIndex;
		switch (action) {
		// 手指按下
		case MotionEvent.ACTION_DOWN:
			// 记录开始坐标
			touchStartX = x;
			touchStartY = y;
			moveX = 0;
			moveY = 0;
			break;
		// 手指抬起
		case MotionEvent.ACTION_UP:
			// 滑动距离必须是一定范围,X和Y的距离
			moveX = (x - touchStartX > 0 ? x - touchStartX : touchStartX - x);
			moveY = (y - touchStartY > 0 ? y - touchStartY : touchStartY - y);
			if ((moveY * 2 < moveX) && moveX > TOUCH_DISTANCE && moveY > TOUCH_DISTANCE / 2) {
				// 往左滑动
				if (x < touchStartX) {
					toIndex += 1;
				}
				// 往右滑动
				else if (touchStartX < x) {
					toIndex -= 1;
				}

				if (toIndex <= 0) {
					toIndex = touchIdArray.length;
				} else if (toIndex > touchIdArray.length) {
					toIndex = 1;
				}

				((RadioButton) this.findViewById(touchIdArray[toIndex - 1])).setChecked(true);
				result = true;
			}
			break;
		}
		return result;
	}

	/**
	 * 切换当前的fragment
	 * 
	 * @param toIndex
	 *            :要跳去的Fragment的Index
	 */
	public void changFragment(int toIndex) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (currentPageIndex > 0) {
			currentFragment = fm.findFragmentByTag(String.valueOf(currentPageIndex));
			if (currentFragment != null) {
				ft.hide(currentFragment);
			}
		}

		// 任务匹配 无论是那一个都要隐藏
		currentFragment = fm.findFragmentByTag(taskMatchFragmentName);
		if (currentFragment != null) {
			ft.hide(currentFragment);
		}

		currentPageIndex = toIndex;
		currentFragment = fm.findFragmentByTag(String.valueOf(currentPageIndex));
		if (currentFragment == null) {
			TaskListFragment temp = null;
			switch (toIndex) {
			case 1:
				currentFragment = new HomeFragment();

				break;
			case 2: // 待勘察
				temp = new TaskListFragment();
				temp.viewModel.taskStatus = TaskStatus.Todo;
				currentFragment = temp;
				break;
			case 3: // 待提交
				temp = new TaskListFragment();
				// 初始化TaskListFragment中的HomeFragment
				temp.homeFragment = (HomeFragment) fm.findFragmentByTag(String.valueOf(1));
				temp.viewModel.taskStatus = TaskStatus.Doing;
				currentFragment = temp;
				break;
			case 4: // 已完成
				temp = new TaskListFragment();
				temp.viewModel.taskStatus = TaskStatus.Done;
				currentFragment = temp;
				break;
			case 5: // 提交中
				temp = new TaskListFragment();
				temp.viewModel.taskStatus = TaskStatus.Submiting;
				currentFragment = temp;
				break;
			default:
				break;
			}
		}
		if (currentFragment != null && currentFragment.isAdded()) {
			ft.show(currentFragment);
		} else {
			ft.add(R.id.frameLayout, currentFragment, String.valueOf(currentPageIndex));
		}
		ft.commitAllowingStateLoss();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int toIndex = 1;
		for (int i = 0; i < touchIdArray.length; i++) {
			if (touchIdArray[i] == checkedId) {
				toIndex = i + 1;
				break;
			}
		}
		changFragment(toIndex);
	}

	@Override
	protected void onDestroy() {
		try {
			appHeader.unRegisterReceiver();
		} catch (Exception e) {
			DataLogOperator.other("HomeActivity.onDestroy.Exception:" + e.getLocalizedMessage());
		}
		super.onDestroy();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
			if(MainService.getUploadTasks().size() > 0){				
				DialogUtil.showConfirmationDialog(this, "当前有任务还未提交完成，您确认退出吗？", new OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
			}else{
				long currentTime = System.currentTimeMillis();
				if ((currentTime - touchTime) >= waitTime) {
					ToastUtil.longShow(this, "再按一次退出");
					touchTime = currentTime;
				} else {
					finish();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 刷新下拉列表
	 */
	public void setMenuItemsVisibility() {
		appHeader.setMenuItemsVisibility();
	}

	/**
	 * 任务编号
	 * @param taskNum
	 */
	public void openMatchAty(String taskNum,Integer ddid){ 	
		Intent intent = new Intent();
		intent.setClass(this,TaskMatchActivity.class);
		intent.putExtra("taskNum", taskNum);
		intent.putExtra("ddid", ddid);
		startActivity(intent);
	}
}
