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
* ��Ŀ���ƣ�WaiCaiTest   
* �����ƣ�DataDefineWorkerTest   
* �������� DataDefineWorker�൥Ԫ����  
* �����ˣ�lihc   
* ����ʱ�䣺2014-4-16 ����9:25:51   
* @version        
*/ 
public class DataDefineWorkerTest extends AndroidTestCase {

	//DataDefineWorker dataDefineWorker = new DataDefineWorker();

	long createID=-1;
	
	/**
	 * ������������������ݣ����������
	 * */
	public void testFillCompleteDataDefindInfos() {
		// ׼������������
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
		// �򿱲����������
		ResultInfo<Long> resultInfo = DataDefineWorker
				.fillCompleteDataDefindInfos(dataDefine);
		createID = resultInfo.Data;
		System.out.println("testFillCompleteDataDefindInfos:"+resultInfo);
		// ����
		assertEquals(true, resultInfo.Data > 0);
	}
	
	/**
	 * ����DDIDɾ�������Ŀ��������
	 * */
	public void testZDeleteCompleteDataDefindInfos() {
		ResultInfo<Integer> resultInfo = DataDefineWorker.deleteCompleteDataDefindInfos((int)createID);
		System.out.println("testDeleteCompleteDataDefindInfos:"+resultInfo);
		//����
		assertEquals(true, resultInfo.Data>0);
	}
	
	/**
	 * ���ݶ�Ӧ��̨����ϵͳ�д˿������ñ��IDֵ�������ڹ�˾��IDֵ��ѯ�������û�����Ϣ�������
	 * */
	public void testQueryDataDefineByDDIDOrCompanyID() {
		ResultInfo<ArrayList<DataDefine>> resultInfo = DataDefineWorker.queryDataDefineByDDIDOrCompanyID(1, 0);
		System.out.println("testQueryDataDefineByDDIDOrCompanyID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * ����CompanyID��ѯ�������û�����Ϣ�������
	 * */
	public void testQueryDataDefineByCompanyID() {
		ResultInfo<ArrayList<DataDefine>> resultInfo = DataDefineWorker.queryDataDefineByCompanyID(1);
		System.out.println("testQueryDataDefineByCompanyID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * ����DDID��ѯ�������û�����Ϣ�������
	 * */
	public void testQueryDataDefineByDDID() {
		ResultInfo<DataDefine> resultInfo = DataDefineWorker.queryDataDefineByDDID(1);
		System.out.println("testQueryDataDefineByDDID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * ����DDID��ѯ��Ӧ�Ŀ������ñ��������Ϣ������
	 * */
	public void testQueryDataCategoryDefineByDDID() {
		ResultInfo<ArrayList<DataCategoryDefine>> resultInfo = DataDefineWorker.queryDataCategoryDefineByDDID(1);
		System.out.println("testQueryDataCategoryDefineByDDID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}
	
	/**
	 * ����ID��ѯ�������ñ�������Ϣ�������
	 * @param dDID�������������ñ��ID�����������ں�̨����ϵͳ�ж�Ӧ��IDֵ
	 * @param categoryID �����ĸ�������������б���Ϣ
	 * */
	public void testQueryDataFieldDefineByID() {
		ResultInfo<ArrayList<DataFieldDefine>> resultInfo = DataDefineWorker.queryDataFieldDefineByID(1,1);//��1,1��2������1,2��1��
		System.out.println("testQueryDataFieldDefineByDDID:"+resultInfo);
		assertEquals(true, resultInfo.Data!=null);
	}

}
