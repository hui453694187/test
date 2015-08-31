package com.yunfang.eias.test;

import java.util.ArrayList;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.framework.model.ResultInfo;
import android.test.AndroidTestCase;

/**   
*    
* 项目名称：WaiCaiTest   
* 类名称：DataDefineWorkerTest   
* 类描述： DataDefineWorker类单元测试  
* 创建人：lihc   
* 创建时间：2014-4-16 上午9:25:51   
* @version        
*/ 
public class DataDefineWorkerTest extends AndroidTestCase {

	//DataDefineWorker dataDefineWorker = new DataDefineWorker();

	long createID=-1;
	
	/**
	 * 填充完成整个勘察表数据（事务操作）
	 * */
	public void testFillCompleteDataDefindInfos() {
		// 准备勘察表的数据
		DataDefine dataDefine = new DataDefine();

		ArrayList<DataCategoryDefine> dataCategoryDefineList = new ArrayList<DataCategoryDefine>();
		DataCategoryDefine dataCategoryDefine = new DataCategoryDefine();
		DataCategoryDefine dataCategoryDefine1 = new DataCategoryDefine();
		

		ArrayList<DataFieldDefine> dataFieldDefineList = new ArrayList<DataFieldDefine>();
		DataFieldDefine dataFieldDefine = new DataFieldDefine();
		DataFieldDefine dataFieldDefine1 = new DataFieldDefine();
		DataFieldDefine dataFieldDefine2 = new DataFieldDefine();
		dataFieldDefineList.add(dataFieldDefine);
		dataFieldDefineList.add(dataFieldDefine1);
		dataCategoryDefine.Fields = dataFieldDefineList;
		dataCategoryDefineList.add(dataCategoryDefine);
		
		dataFieldDefineList = new ArrayList<DataFieldDefine>();
		dataFieldDefineList.add(dataFieldDefine2);
		dataCategoryDefine1.Fields=dataFieldDefineList;
		dataCategoryDefineList.add(dataCategoryDefine1);
		
		dataDefine.Categories = dataCategoryDefineList;
		// 向勘察表表插入数据
		ResultInfo<Long> resultInfo = DataDefineWorker
				.fillCompleteDataDefindInfos(dataDefine);
		createID = resultInfo.Data;
		System.out.println("testFillCompleteDataDefindInfos:"+resultInfo);
		// 断言
		assertEquals(true, resultInfo.Data > 0);
	}
	
	/**
	 * 根据DDID删除完整的勘察表数据
	 * */
	public void testZDeleteCompleteDataDefindInfos() {
		ResultInfo<Integer> resultInfo = DataDefineWorker.deleteCompleteDataDefindInfos((int)createID);
		System.out.println("testDeleteCompleteDataDefindInfos:"+resultInfo);
		//断言
		assertEquals(true, resultInfo.Data>0);
	}
	
	/**
	 * 根据对应后台管理系统中此勘察配置表的ID值或者所在公司的ID值查询勘察配置基本信息表的数据
	 * */
	public void testQueryDataDefineByDDIDOrCompanyID() {
		ResultInfo<ArrayList<DataDefine>> resultInfo = DataDefineWorker.queryDataDefineByDDIDOrCompanyID(1, 0);
		System.out.println("testQueryDataDefineByDDIDOrCompanyID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * 根据CompanyID查询勘察配置基本信息表的数据
	 * */
	public void testQueryDataDefineByCompanyID() {
		ResultInfo<ArrayList<DataDefine>> resultInfo = DataDefineWorker.queryDataDefineByCompanyID(1);
		System.out.println("testQueryDataDefineByCompanyID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * 根据DDID查询勘察配置基本信息表的数据
	 * */
	public void testQueryDataDefineByDDID() {
		ResultInfo<DataDefine> resultInfo = DataDefineWorker.queryDataDefineByDDID(1);
		System.out.println("testQueryDataDefineByDDID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * 根据DDID查询对应的勘察配置表分类项信息表数据
	 * */
	public void testQueryDataCategoryDefineByDDID() {
		ResultInfo<ArrayList<DataCategoryDefine>> resultInfo = DataDefineWorker.queryDataCategoryDefineByDDID(1);
		System.out.println("testQueryDataCategoryDefineByDDID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * 根据ID查询勘察配置表属性信息表的数据
	 * @param dDID：所属勘察配置表的ID，即属性项在后台管理系统中对应的ID值
	 * @param categoryID 属于哪个分类项的属性列表信息
	 * */
	public void testQueryDataFieldDefineByID() {
		ResultInfo<ArrayList<DataFieldDefine>> resultInfo = DataDefineWorker.queryDataFieldDefineByID(1,1);//（1,1）2条，（1,2）1条
		System.out.println("testQueryDataFieldDefineByDDID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}

}
