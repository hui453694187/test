package com.yunfang.eias.enumObj;

public enum UrgentStatusEnum {
	//{{ 枚举值
	/**
	 * 常规
	 */
	Normal("一般",0),
	/**
	 * 紧急
	 */
	Urgent("紧急",1);
	//}}
	
	//{{ 属性
	/**
	 *   成员变量名称  
	 */
	private String name;  

	/**
	 *   成员变量值
	 */
	private int index;  
	//}}

	/**
	 * 构造方法  
	 * @param name  枚举项名称
	 * @param index 枚举项数字值
	 */
	private UrgentStatusEnum(String name, int index) {  
		this.name = name;  
		this.index = index;  
	}  

	/**
	 * 通过值得到枚举类型
	 * @param value
	 * @return
	 */
	public static UrgentStatusEnum getEnumByValue(int value){
		UrgentStatusEnum result = null;
		for (UrgentStatusEnum u : UrgentStatusEnum.values()) {  
			if (u.getIndex() == value) {  
				result =u;  
				break;
			}  
		} 
		return result;
	}

	/**
	 * 通过名称得到枚举类型
	 * @param value
	 * @return
	 */	
	public static UrgentStatusEnum getEnumByName(String value){
		UrgentStatusEnum result = null;
		for (UrgentStatusEnum u : UrgentStatusEnum.values()) {  
			if (u.getName().equals(value)) {  
				result =u;   
				break;
			}  
		} 
		return result;
	}
	
	
	/**
	 * 通过枚举项数字值获得枚举项名称
	 * @param index  枚举项数字值
	 * @return
	 */
	public static String getName(int index) {  
		String result = "";
		for (UrgentStatusEnum c : UrgentStatusEnum.values()) {  
			if (c.getIndex() == index) {  
				result = c.name;  
				break; 
			}  
		}  
		return result;  
	}  

	/**
	 * 通过枚举项名称获得枚举项数字值
	 * @param enumName  枚举项名称
	 * @return
	 */
	public static int getValue(String enumName) {  
		int result = -1;
		for (UrgentStatusEnum c : UrgentStatusEnum.values()) {  
			if (c.getName().equals(enumName)) {  
				result = c.index;  
				break; 
			}  
		}  
		return result;  
	}  

	/**  
	 * 获取枚举项名称
	 * @return
	 */
	public String getName() {  
		return name;  
	} 

	/**
	 * 设置枚举项名称
	 * @param name
	 */
	public void setName(String name) {  
		this.name = name;  
	}  

	/**
	 * 获取枚举项数字值
	 * @return
	 */
	public int getIndex() {  
		return index;  
	} 

	/**
	 * 设置枚举项数字值
	 * @return
	 */
	public void setIndex(int index) {  
		this.index = index;  
	}  
}

