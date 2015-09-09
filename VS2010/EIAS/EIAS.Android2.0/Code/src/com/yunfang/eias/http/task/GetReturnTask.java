/**
 * 
 */
package com.yunfang.eias.http.task;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * @author Administrator
 * 
 */
public class GetReturnTask implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	/* 
	 * 
	 */
	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/***
	 * 
	 * @param user
	 * @return
	 */
	public ResultInfo<String> getReturnTadkInfo(UserInfo user) {
		ResultInfo<String> result = new ResultInfo<String>();
		String url = user.LatestServer + "/apis/GetReturnTask";
		Hashtable<String, Object> params = new Hashtable<String, Object>(4);
		params.put("token", user.Token);
		// 封装了一个其请求
		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			// 搜集错误日志
			DataLogOperator.taskHttp("CheckVersionTask=>" + result.Message + "(request)", e.getMessage());
		}

		return result;
	}

	/*
	 * 处理返回的JSON 数据
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<String> getResponseData() {
		ResultInfo<String> result = new ResultInfo<String>();
		try {
			if (mData != null) {
				String resultStr = new String(mData);
				JSONObject resultJsonObj = new JSONObject(resultStr);
				if(resultJsonObj.getString("Success").equals("false")){
					result.Success=false;
				}else{
					result.Data=resultJsonObj.getString("Data");
				}
				result.Message=resultJsonObj.getString("Message");
			}
		} catch (JSONException e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskHttp("GetReturnTask 获取真挺任务编号=>(getResponseData)", e.getMessage());
		}

		return result;
	}

}
