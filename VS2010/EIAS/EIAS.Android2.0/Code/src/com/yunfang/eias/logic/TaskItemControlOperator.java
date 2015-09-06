package com.yunfang.eias.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.eias.ui.TaskInfoActivity;
import com.yunfang.framework.maps.BaiduLocationHelper;
import com.yunfang.framework.maps.BaiduLocationHelper.BaiduLoactionOperatorListener;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.StringUtil;
import com.yunfang.framework.view.BaibuMapView;
import com.yunfang.framework.view.BaibuMapView.OperatorListener;

/**
 * 
 * 项目名称：外采系统 类名称：TaskCreateControlOperator 类描述：待领取任务逻辑类 创建人：贺隽 创建时间：2014-4-17
 * 下午5:40:48
 * 
 * @version
 */
public class TaskItemControlOperator {

	/**
	 * 需要用的容器
	 */
	private TaskInfoActivity mActivity;

	/**
	 * 控件加载显示的视图控件
	 */
	private LinearLayout viewLayout;

	/**
	 * 任务对应的勘察表子项数据(某个分类下)
	 */
	private ArrayList<DataFieldDefine> defineItems;

	/**
	 * 任务子项数据(某个分类下)
	 */
	private ArrayList<TaskDataItem> dataItems;

	/**
	 * 构函数
	 * 
	 * @param activity
	 * @param linearLayout
	 */
	public TaskItemControlOperator(TaskInfoActivity activity, LinearLayout linearLayout, Integer taskID, Integer baseCategoryID) {
		this.mActivity = activity;
		this.viewLayout = linearLayout;
		this.taskID = taskID;
		this.baseCategoryID = baseCategoryID;
	}

	// {{ 动态创建控件需要用的布局类型和变量
	/**
	 * 全部填充
	 */
	private LinearLayout.LayoutParams FILL_PARENT = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);

	/**
	 * 按父级大小填充
	 */
	private LinearLayout.LayoutParams WRAP_CONTENT = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);

	/**
	 * 所有动态生成的子项控件列表
	 */
	private Map<String, View> inputMap = new HashMap<String, View>();

	/**
	 * 单选按钮变更判断
	 */
	@SuppressWarnings("unused")
	private Boolean hasChanged = false;

	/**
	 * 任务编号
	 */
	private Integer taskID;

	/**
	 * 分类项ID
	 */
	private Integer baseCategoryID;

	// }}

	// {{ 创建控件

	/**
	 * 显示所有的子项信息
	 * 
	 * @param items
	 *            :勘察表子项列表
	 * @param data
	 *            :任务子项列表
	 */
	public Boolean showItems(ArrayList<DataFieldDefine> items, ArrayList<TaskDataItem> data) {
		Boolean result = false;
		viewLayout.removeAllViews();
		inputMap = new HashMap<String, View>();
		defineItems = items;
		dataItems = new ArrayList<TaskDataItem>();
		if (data != null) {
			dataItems.addAll(data);
		}
		if (ListUtil.hasData(items)) {
			TaskDataItem dataItem = null;
			for (DataFieldDefine item : items) {
				dataItem = null;
				if (ListUtil.hasData(data)) {
					for (TaskDataItem tempDataItem : data) {
						if (item.Name.equals(tempDataItem.Name)) {
							dataItem = tempDataItem;
							data.remove(tempDataItem);
							break;
						}
					}
				}
				String itemValue = "";
				if ((dataItem != null && dataItem.Value != null && !dataItem.Value.equals("null"))) {
					itemValue = dataItem.Value;
				}
				// 新增字段，是否在安卓端展示。
				if (item.ShowInPhone) {
					createControl(item, itemValue);
				}
			}
			result = true;
		}
		return result;
	}

	/**
	 * 创建控件
	 * 
	 * @param item
	 */
	private void createControl(DataFieldDefine item, String dataItemValue) {
		LinearLayout l = new LinearLayout(this.mActivity);
		l.setOrientation(LinearLayout.VERTICAL);
		l.setLayoutParams(FILL_PARENT);
		TextView tv = new TextView(this.mActivity, null);
		tv.setText(item.Name + (item.Required ? "*" : ""));
		if (item.Required) {
			tv.setTextColor(Color.RED);
		}
		tv.setLayoutParams(WRAP_CONTENT);
		l.addView(tv);
		item.Value = (item.Value != null && item.Value.equals("null")) ? "" : item.Value;
		String value = (dataItemValue.length() > 0 ? dataItemValue : item.Value);
		if (item.ItemType != null) {
			switch (item.ItemType) {
			case DropDownList:
				createDropdownBox(item, l, value);
				break;
			case CheckedBoxList:
				createCBO(item, l, value);
				break;
			case Text:
			case CustomerText:
				if (value == null || value.trim().length() == 0) {
					value = item.Content.replaceAll("null", "");
				}
				createTextBox(item, l, value);
				break;
			case DateTimeValue:
				createDateTimeTextBox(item, l, value);
				break;
			case Coordinate:
				createBaiduPositionTextBox(item, l, value);
				break;
			case Map:
				createBaiduMapTextBox(item, l, value);
				break;
			case UserName:
				createUserNameTextBox(item, l, value);
				break;
			case UserTel:
				createUserPhoneTextBox(item, l, value);
				break;
			case Picture:

				break;
			case Audio:

				break;
			case Video:

				break;
			case MultiText:
				createMutilTextBox(item, l, value);
				break;
			default:
				break;
			}
		} else {
			createTextBox(item, l, value);
		}
		viewLayout.addView(l);
	}

	/**
	 * 下拉菜单选中事件
	 */
	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String tmp = arg0.getSelectedItem().toString();
			EditText et = ((EditText) arg0.getTag());

			if (tmp.endsWith("}")) {
				et.setText(tmp.substring(tmp.indexOf("{") + 1, tmp.indexOf("}")));
				et.setVisibility(View.VISIBLE);
				et.setGravity(Gravity.CENTER);
			} else {
				et.setVisibility(View.GONE);
			}
			String inputValue = tmp;
			String inputKey = et.getTag().toString();
			if (!inputValue.equals(EIASApplication.DefaultDropDownListValue)) {
				changeValueEvent(inputValue, inputKey);
			}
			hasChanged = true;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	/**
	 * 创建下拉列表
	 * 
	 * @param f
	 * @param parentLinearLayout
	 * @param value
	 */
	private void createDropdownBox(DataFieldDefine f, LinearLayout parentLinearLayout, final String value) {
		final Spinner sp = new Spinner(this.mActivity);
		final EditText et = new EditText(this.mActivity);
		et.setBackgroundColor(Color.BLUE);
		et.setGravity(Gravity.CENTER);
		sp.setTag(et);
		et.setTag(f.Name);
		sp.setLayoutParams(FILL_PARENT);
		// sp.setGravity(Gravity.CENTER_HORIZONTAL);
		sp.setPrompt(" 请选择 ： ");

		final String[] arr = (EIASApplication.DefaultDropDownListValue + "|#|" + f.Content).split("\\|#\\|");
		ArrayAdapter<Object> ad = new ArrayAdapter<Object>(this.mActivity, android.R.layout.simple_spinner_item, arr);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(ad);

		if (value != null && !value.equals("") && !value.equals(EIASApplication.DefaultDropDownListValue)) {
			String tmp = value.endsWith("}") ? value.substring(0, value.indexOf("{")) : value;
			int position = StringUtil.getIndexForArray(arr, tmp);
			if (position >= 0) {
				sp.setSelection(position);
			}
		}
		sp.setOnItemSelectedListener(itemSelectedListener);

		parentLinearLayout.addView(sp);

		inputMap.put(f.Name, sp);

		et.setLayoutParams(FILL_PARENT);
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String tmp = sp.getSelectedItem().toString();
				if (tmp.endsWith("}")) {
					arr[(Integer) sp.getSelectedItemPosition()] = (tmp.substring(0, tmp.indexOf("{")) + "{" + et.getText().toString().trim() + "}");
				}
			}
		});

		et.setVisibility(View.GONE);
		if ((value + "").endsWith("}")) {
			et.setVisibility(View.VISIBLE);
			et.setText(value.substring(value.indexOf("{") + 1, value.indexOf("}")));
		}
		// 设置默认值
		int i = StringUtil.getIndexForArray(arr, value);
		if (i > 0) {
			sp.setSelection(i, true);
		}
		parentLinearLayout.addView(et);
	}

	/**
	 * 创建多选框
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	private void createCBO(DataFieldDefine f, LinearLayout l, String value) {
		final Button selectBtn = new Button(this.mActivity);
		selectBtn.setGravity(Gravity.LEFT);
		selectBtn.setLayoutParams(FILL_PARENT);
		final String[] arr = f.Content.split("\\|#\\|");

		selectBtn.setTag(f.Name);
		selectBtn.setText(value.replaceAll(" ", ","));
		selectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				Button temp = (Button) arg0;
				String selectedItems = temp.getText().toString();
				final boolean[] checkedItems = new boolean[arr.length];
				int count = arr.length;

				for (int i = 0; i < count; i++) {
					String tmp = arr[i];
					if (tmp.endsWith("}")) {
						tmp = tmp.substring(0, tmp.indexOf("{"));
						if (selectedItems.contains(tmp)) {
							Pattern p = Pattern.compile(tmp + "\\{\\w*\\}");
							Matcher m = p.matcher(selectedItems);
							if (m.find()) {
								arr[i] = m.group();
							}
						}
					}
					checkedItems[i] = selectedItems.contains(tmp);
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("请选择");
				builder.setView(buildMutiChoiseLayout(arr, checkedItems));
				builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int clicked) {
						String strTmp = "";
						for (int i = 0; i < arr.length; i++) {
							Log.i("ME", arr[i] + " selected: " + checkedItems[i]);
							if (checkedItems[i]) {
								if (strTmp.length() > 0)
									strTmp += ",";
								strTmp += arr[i];
							}

						}

						selectBtn.setText(strTmp);
						hasChanged = true;

						String inputValue = strTmp;
						String inputKey = selectBtn.getTag().toString();
						changeValueEvent(inputValue, inputKey);
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		l.addView(selectBtn);
		inputMap.put(f.Name, selectBtn);
	}

	/**
	 * 滚动条
	 * 
	 * @param arrItems
	 * @param checkedItems
	 * @return
	 */
	private ScrollView buildMutiChoiseLayout(final String[] arrItems, final boolean[] checkedItems) {
		ScrollView sv = new ScrollView(this.mActivity);
		LinearLayout linearLayout = new LinearLayout(this.mActivity, null, R.style.controlBase);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(FILL_PARENT);
		for (int i = 0; i < arrItems.length; i++) {
			String item = arrItems[i];
			if (item.endsWith("}")) {
				String otherValue = item.substring(item.indexOf("{") + 1, item.length() - 1);
				LinearLayout childLayout = new LinearLayout(this.mActivity);
				childLayout.setOrientation(LinearLayout.VERTICAL);
				linearLayout.setLayoutParams(FILL_PARENT);
				final CheckBox cb = buildCheckBox(checkedItems, checkedItems[i], item.substring(0, item.indexOf("{")));
				cb.setTag(i);
				cb.setLayoutParams(WRAP_CONTENT);
				childLayout.addView(cb);

				final EditText et = new EditText(this.mActivity);
				et.setLayoutParams(FILL_PARENT);
				et.setText(otherValue);
				et.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						arrItems[(Integer) cb.getTag()] = (cb.getText().toString() + "{" + et.getText().toString().trim() + "}");
					}
				});
				childLayout.addView(et);

				linearLayout.addView(childLayout);
			} else {
				CheckBox cb = buildCheckBox(checkedItems, checkedItems[i], item);
				cb.setTag(i);
				linearLayout.addView(cb);
			}
		}
		sv.addView(linearLayout);

		return sv;
	}

	/**
	 * 提示框中的单选按钮
	 * 
	 * @param checkedItems
	 * @param ischecked
	 * @param item
	 * @return
	 */
	private CheckBox buildCheckBox(final boolean[] checkedItems, boolean ischecked, String item) {
		CheckBox cb = new CheckBox(this.mActivity.getBaseContext());
		cb.setLayoutParams(WRAP_CONTENT);
		cb.setText(item);
		cb.setChecked(ischecked);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkedItems[(Integer) buttonView.getTag()] = buttonView.isChecked();
			}
		});
		return cb;
	}

	/**
	 * 创建文本输入框
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	private void createTextBox(DataFieldDefine f, LinearLayout l, String value) {
		EditText et = new EditText(this.mActivity);
		et.setLayoutParams(FILL_PARENT);
		et.setText(value);
		int intputType = f.InputFormat.equals("N") ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT;
		et.setInputType(intputType);
		String hintString = f.Hint.isEmpty() ? "" : f.Hint.equals("null") ? "" : f.Hint;
		et.setHint(hintString);
		et.addTextChangedListener(watcher);
		l.addView(et);
		inputMap.put(f.Name, et);
		et.setTag(f.Name);
		et.setOnFocusChangeListener(focusListenter);
		if (value != null && value.length() > 0) {
			changeValueEvent(value, f.Name);
		}

	}

	/**
	 * 创建文本输入框
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	private void createMutilTextBox(DataFieldDefine f, LinearLayout l, String value) {
		EditText et = new EditText(this.mActivity);
		et.setLayoutParams(FILL_PARENT);
		et.setGravity(Gravity.LEFT | Gravity.TOP);
		et.setLines(3);// 多行的话显示3行
		et.setText(value);
		String hintString = f.Hint.isEmpty() ? "" : f.Hint.equals("null") ? "" : f.Hint;
		et.setHint(hintString);
		et.addTextChangedListener(watcher);
		l.addView(et);
		inputMap.put(f.Name, et);
		et.setTag(f.Name);
		et.setOnFocusChangeListener(focusListenter);
		if (value != null && value.length() > 0) {
			changeValueEvent(value, f.Name);
		}
	}

	/**
	 * 文本变更
	 */
	private TextWatcher watcher = new TextWatcher() {

		public void afterTextChanged(Editable arg0) {
			hasChanged = true;
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}
	};

	/**
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	private void createDateTimeTextBox(final DataFieldDefine f, LinearLayout l, String value) {
		EditText et = new EditText(this.mActivity);
		et.setLayoutParams(FILL_PARENT);
		et.setText(value);
		et.addTextChangedListener(watcher);
		et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText temp = (EditText) v;
				if (hasFocus) {// 获得焦点
					// 在这里可以对获得焦点进行处理
					if (temp.getText().length() == 0) {
						String dateTimeTemp = DateTimeUtil.getCurrentTime_CN();
						String dateTime = dateTimeTemp.substring(0, dateTimeTemp.lastIndexOf(':'));
						temp.setText(dateTime);

					}
				} else {// 失去焦点
					// 在这里可以对输入的文本内容进行有效的验证
				}
				changeValueEvent(temp.getText().toString(), f.Name);

			}
		});

		l.addView(et);
		inputMap.put(f.Name, et);
		if (value != null && value.length() > 0) {
			changeValueEvent(value, f.Name);
		}
	}

	/**
	 * 填写完内容后失去焦点事件(转用于有焦点的控件EditText)
	 */
	OnFocusChangeListener focusListenter = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				// 此处为失去焦点时的处理内容
				if (v instanceof EditText) {
					EditText et = (EditText) v;
					String inputValue = et.getText().toString().trim();
					String inputKey = et.getTag().toString();
					changeValueEvent(inputValue, inputKey);
				}
			}
		}
	};

	/**
	 * 子项值改变事件
	 * 
	 * @param v
	 *            子项控件
	 */
	private void changeValueEvent(String inputValue, String inputKey) {
		if (inputKey == null || inputKey.equals("") || inputValue == null) {
			return;
		}
		ArrayList<TaskDataItem> result = new ArrayList<TaskDataItem>();
		// 子项中没有则添加
		if (ListUtil.hasData(defineItems)) {
			Boolean addItem = true;
			for (DataFieldDefine defineItem : defineItems) {
				if (defineItem.Name.equals(inputKey)) {
					addItem = true;
					for (TaskDataItem dataItem : dataItems) {
						if (defineItem.Name.equals(dataItem.Name)) {
							addItem = false;
							break;
						}
					}
					if (addItem) {
						result.add(new TaskDataItem(baseCategoryID, defineItem.Name, inputValue, defineItem.IOrder, taskID, defineItem.CategoryID, -1));
					}
					break;
				}
			}
		}
		// 子项中有则修改
		if (result.size() == 0) {
			if (ListUtil.hasData(dataItems)) {
				for (TaskDataItem item : dataItems) {
					if (item.Name.equals(inputKey)) {
						item.Value = inputValue;
						result.add(item);
						break;
					}
				}
			}
		}
		changeItemValue(result);
	}

	// ?

	/**
	 * 修改子项值
	 * 
	 * @param itemName
	 *            子项名称
	 * @param value
	 *            输入的子项值
	 */
	private void changeItemValue(ArrayList<TaskDataItem> updateItems) {
		if (updateItems.size() > 0) {
			ResultInfo<Integer> saveInfo = TaskDataWorker.saveManyTaskDataItem(updateItems);
			if (saveInfo.Success && saveInfo.Data > 0) {
				// 修改子项数量
				int valueCount = 0;
				String bMapDefaultValue = EIASApplication.DefaultBaiduMapTipsValue + EIASApplication.DefaultBaiduMapUnLocTipsValue;
				ArrayList<TaskDataItem> items = dataItems;
				for (TaskDataItem item : items) {
					if (item.Value.trim().length() > 0 && !item.Value.trim().equals(EIASApplication.DefaultHorizontalLineValue) && !item.Value.trim().equals(EIASApplication.DefaultNullString)
							&& !item.Value.trim().equals(bMapDefaultValue)) {
						valueCount += 1;
					}
				}
				mActivity.viewModel.currentCategory.DataDefineFinishCount = valueCount;
				for (TaskCategoryInfo category : mActivity.viewModel.currentTaskCategoryInfos) {
					if (category.ID == mActivity.viewModel.currentCategory.ID) {
						category.DataDefineFinishCount = valueCount;
						break;
					}
				}
			}
		}
	}

	/**
	 * 百度地图 GPS
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	private void createBaiduPositionTextBox(DataFieldDefine f, LinearLayout l, String value) {
		EditText et = new EditText(this.mActivity);
		et.setLayoutParams(FILL_PARENT);
		et.setText(value);
		et.addTextChangedListener(watcher);
		et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				final EditText temp = (EditText) v;
				final String inputValue = temp.getText().toString().trim();
				final String inputKey = temp.getTag().toString();
				if (hasFocus) {// 获得焦点
					// 在这里可以对获得焦点进行处理
					if (temp.getText().length() == 0 || temp.getText().equals("null")) {
						BaiduLocationHelper locationHelper = new BaiduLocationHelper(mActivity, true);
						locationHelper.setOperatorListener(new BaiduLoactionOperatorListener() {
							@Override
							public void onSelected(BDLocation location) {
								temp.setText(location.getLatitude() + "," + location.getLongitude());
								changeValueEvent(inputValue, inputKey);
							}
						});
					}
				} else {// 失去焦点
				}
				changeValueEvent(inputValue, inputKey);
			}
		});
		l.addView(et);
		inputMap.put(f.Name, et);
		et.setTag(f.Name);
	}

	/**
	 * 地图
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	@SuppressWarnings("deprecation")
	private void createBaiduMapTextBox(DataFieldDefine f, LinearLayout l, String value) {
		final Button et = new Button(this.mActivity);
		final DataFieldDefine finalF = f;
		et.setGravity(Gravity.CENTER_HORIZONTAL);
		et.setLayoutParams(FILL_PARENT);
		if (value.length() > 0) {
			et.setText(value);
		} else {
			et.setText(EIASApplication.DefaultBaiduMapUnLocTipsValue);
		}
		et.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Button tempView = (Button) v;
				String latlngString = tempView.getText().toString();
				if (!latlngString.contains(",")) {
					latlngString = "";
				}
				BaibuMapView baiduMap = new BaibuMapView(mActivity, latlngString);
				baiduMap.setAddressControl(true);
				FrameLayout frameLayout = new FrameLayout(mActivity);
				frameLayout.setLayoutParams(FILL_PARENT);
				frameLayout.addView(baiduMap.mView);
				final Dialog dialog = DialogUtil.getAboutDialog(mActivity, frameLayout);
				dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				baiduMap.setOperatorListener(new OperatorListener() {
					@Override
					public void onSelected(BDLocation location) {
						if (location != null) {
							String loglat = location.getLatitude() + "," + location.getLongitude();
							et.setText(loglat);
							String inputValue = loglat;
							String inputKey = finalF.Name.toString();
							if (!inputValue.equals(EIASApplication.DefaultDropDownListValue)) {
								changeValueEvent(inputValue, inputKey);
							}
							if (tempView.getTag() != null) {
								BaibuMapView baiduMap = (BaibuMapView) tempView.getTag();
								LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
								baiduMap.setLatlng(latlng);
							}
						}
						dialog.dismiss();
					}

					@Override
					public void onCancel() {
						dialog.dismiss();
					}

					@Override
					public void onGetGeoCodeResult(GeoCodeResult result) {

					}

					@Override
					public void onMapStatusChange(MapStatus arg0) {

					}
				});
				dialog.show();
			}
		});
		l.addView(et);

		BaibuMapView baiduMap = new BaibuMapView(mActivity, value);
		baiduMap.setshowzoomControls(false);
		baiduMap.setScrollEnable(false);
		baiduMap.setRotateEnable(false);
		baiduMap.setOverlookEnable(false);
		baiduMap.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 300));
		et.setTag(baiduMap);
		l.addView(baiduMap);

		inputMap.put(f.Name, et);
		et.setOnFocusChangeListener(focusListenter);
	}

	/**
	 * 创造用户输入框
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	private void createUserNameTextBox(final DataFieldDefine f, LinearLayout l, String value) {
		EditText et = new EditText(this.mActivity);
		et.setLayoutParams(FILL_PARENT);
		if (value.length() == 0 || value.equals("null")) {
			et.setText(EIASApplication.getCurrentUser().Name);
		} else {
			et.setText(value);
		}
		et.addTextChangedListener(watcher);
		et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText temp = (EditText) v;
				if (hasFocus) {
					if (temp.getText().length() == 0) {
						String userName = EIASApplication.getCurrentUser().Name;
						temp.setText(userName);
					}
				} else {// 失去焦点时保存
				}
				changeValueEvent(temp.getText().toString(), f.Name);
			}
		});
		l.addView(et);
		inputMap.put(f.Name, et);
		if (value == null || value.length() <= 0) {
			changeValueEvent(EIASApplication.getCurrentUser().Name, f.Name);
		}
	}

	/**
	 * 创建电话文本输入框
	 * 
	 * @param f
	 * @param l
	 * @param value
	 */
	private void createUserPhoneTextBox(final DataFieldDefine f, LinearLayout l, String value) {
		EditText et = new EditText(this.mActivity);
		et.setLayoutParams(FILL_PARENT);
		if (value.length() == 0 || value.equals("null")) {
			if (EIASApplication.getCurrentUser().Mobile.length() == 0 || EIASApplication.getCurrentUser().Mobile.equals("null")) {
				et.setText("");
			} else {
				et.setText(EIASApplication.getCurrentUser().Mobile);
			}
		} else {
			et.setText(value);
		}
		et.addTextChangedListener(watcher);
		et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText temp = (EditText) v;
				if (hasFocus) {

					if (temp.getText().length() == 0) {
						String mobile = EIASApplication.getCurrentUser().Mobile;
						temp.setText(mobile);
					}
				}
				// 保存
				String mobile = temp.getText().toString();
				changeValueEvent(mobile, f.Name);

			}
		});
		l.addView(et);
		inputMap.put(f.Name, et);
		if (value == null || value.length() <= 0) {
			changeValueEvent(EIASApplication.getCurrentUser().Mobile, f.Name);
		}
	}

	/**
	 * 获取当前修改的所有子项值
	 * 
	 * @return
	 */
	public ArrayList<TaskDataItem> getInputDatas(Integer taskID, Integer baseCategoryID) {
		ArrayList<TaskDataItem> result = new ArrayList<TaskDataItem>();

		String inputValue;
		Iterator<Entry<String, View>> iter = inputMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, View> entry = (Map.Entry<String, View>) iter.next();
			String key = entry.getKey();
			View controlView = entry.getValue();

			inputValue = "";
			if (controlView instanceof Spinner) {
				Spinner sp = (Spinner) controlView;
				if (!sp.getSelectedItem().toString().equals(EIASApplication.DefaultDropDownListValue)) {
					inputValue = sp.getSelectedItem().toString();
				}
			} else if (controlView instanceof EditText) {
				EditText et = (EditText) controlView;
				inputValue = et.getText().toString().trim();
			} else if (controlView instanceof Button) {
				Button btn = (Button) controlView;
				if (btn.getTag() != null && btn.getTag().toString().trim().length() > 0) {
					inputValue = btn.getTag().toString().trim();
				} else {
					String bMapDefaultValue = EIASApplication.DefaultBaiduMapTipsValue + EIASApplication.DefaultBaiduMapUnLocTipsValue;
					if (!bMapDefaultValue.equals(btn.getText().toString().trim())) {
						inputValue = btn.getText().toString().trim();
					}
				}
			}

			if (ListUtil.hasData(dataItems)) {
				for (TaskDataItem item : dataItems) {
					if (item.Name.equals(key)) {
						item.Value = inputValue;
						result.add(item);
						break;
					}
				}
			}

			if (ListUtil.hasData(defineItems)) {
				Boolean addItem = true;
				for (DataFieldDefine defineItem : defineItems) {
					if (defineItem.Name.equals(key)) {
						addItem = true;
						for (TaskDataItem dataItem : result) {
							if (defineItem.Name.equals(dataItem.Name)) {
								addItem = false;
								break;
							}
						}
						if (addItem) {
							result.add(new TaskDataItem(baseCategoryID, defineItem.Name, inputValue, defineItem.IOrder, taskID, defineItem.CategoryID, -1));
						}
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * 创建图片目录
	 * 
	 * @param taskNum任务编号
	 * @param type
	 *            图片 photo 视频 video 音频
	 * @return
	 */
	public static String mkResourceDir(String taskNum, String type) {
		String dirString = EIASApplication.projectRoot + taskNum + File.separator + type;
		FileUtil.mkDir(dirString);
		return dirString;
	}

	/**
	 * 检测每个子项输入字符的格式
	 * 
	 * @param items
	 *            子项列表
	 * @param showMsgs
	 *            返回的错误信息
	 * @return 是否通过验证
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ResultInfo<Boolean> checkItemDataFormat(ArrayList<TaskDataItem> items, ArrayList<String> showMsgs) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		String showMsg = "";
		// 申请错误提示信息列表
		Pattern pat = Pattern
				.compile("^[\\u4E00-\\u9FFF\\w\\u3000\\u0020\\u002c\\u3002\\u003b\\uff1b\\u002d\\u2014\\u007b\\u007d\\u003a\\u3001\\u002e\\u0028\\u0029\\u005b\\u005d\\u3002\\uff0c\\uff1b\\uff1a\\uff08\\uff09\\u007b\\u007d]+$");
		for (int i = 0; i < items.size(); i++) {
			TaskDataItem item = items.get(i);
			if (item != null && item.Value != null && item.Value != "") {
				// 声明存放找出来的非法字符容器
				ArrayList<String> errorChars = new ArrayList();
				for (int j = 0; j < item.Value.length(); j++) {
					String charValue = String.valueOf(item.Value.charAt(j));
					Matcher matcher = pat.matcher(charValue);
					if (!matcher.matches()) {
						errorChars.add(charValue);
					}
				}
				if (errorChars.size() > 0) {
					String msg = "";
					for (int j = 0; j < errorChars.size(); j++) {
						msg = msg + errorChars.get(j);
					}
					showMsgs.add("[" + item.Name + "]" + "存在非法字符[" + msg + "];");
					showMsg = showMsg + "[" + item.Name + "]" + "存在非法字符[" + msg + "];\n";
				}
			}
		}
		result.Message = showMsg;
		result.Data = showMsgs.size() == 0;
		return result;
	}

	// }}
}
