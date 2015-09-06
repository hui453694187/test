/**
 * 
 */
package com.yunfang.eias.logic;

import java.util.ArrayList;
import java.util.List;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.http.task.GetDatadefinesTask;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * 勘察配置信息数据操作
 * @author kevin
 *
 */
public class DatadefinesOperator {
	
	/***
	 * 获取最新的勘察配置表信息
	 * @param userInfo
	 * @return 勘察信息表
	 */
	public static ResultInfo<ArrayList<DataDefine>> getNewestDatadefine(UserInfo userInfo){
		ResultInfo<ArrayList<DataDefine>> result=new ResultInfo<ArrayList<DataDefine>>();
		try{
			if(!EIASApplication.IsOffline){
				
				GetDatadefinesTask getDatadefinesTask=new GetDatadefinesTask();
				result=getDatadefinesTask.request(userInfo);
				
				ArrayList<DataDefine> motroDefines=(ArrayList<DataDefine>)result.Data;
				//int size=motroDefines.size();
				//Log.d("lee","size"+size);
				/*for(DataDefine d:motroDefines){
					Log.d("lee","define:"+d.toString());
				}*/
				List<String> ddidList=new ArrayList<String>();
				//获取本地数据库 勘察表数据
				ResultInfo<ArrayList<DataDefine>> localDefines=DataDefineWorker.queryDataDefineByCompanyID(userInfo.CompanyID);
				
				for(DataDefine localDefine:localDefines.Data){
					boolean isExist=false;
					for(DataDefine motroDefine:motroDefines){
						if(motroDefine.ID==localDefine.DDID){// 本地的DDID 对应远程服务器的 define 的ID 
							isExist=true;
							break;
						}
					}
					if(!isExist){// 远程服务器不存这张勘察表
						ddidList.add(String.valueOf(localDefine.DDID));
					}
				}
				//Log.d("lee","要删除的DDID 个数："+ddidList.size());
				if(ddidList.size()>0){
					DataDefineWorker.deletDataDefneByDDID(ddidList);
				}
				
				
			}
			
			
		}catch(Exception e){
			result.Success = false;
			result.Message = result.Message.length() > 0 ? result.Message : e.getMessage();
		}
		return result;
	}
}