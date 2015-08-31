package com.yunfang.eias.utils;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class DensityHelper {

    private  int W=-1;
    private  int H=-1;

    private static DensityHelper densityUtil;
    private Activity context;

    public static DensityHelper getInstance(Activity context){
        if(densityUtil==null){
            densityUtil=new DensityHelper(context);
        }
        if(densityUtil.context==null){
        	densityUtil.context=context;
        }
        return densityUtil;

    }
    
    public void destroy(){
    	context=null;
    }

    /***
     * 单例
     */
    private DensityHelper(Activity context){
    	if(this.context!=null){
    		this.context=null;
    	}
        this.context=context;
    }

    public int getW(){
        if(W==-1){
            this.screenInfo();
        }
        return W;
    }

    public int getH(){
        if(H==-1){
            this.screenInfo();
        }

        return H;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
	public int px2dip( float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

    /**
     * sp转px
     * @param spVal
     * @return
     */
    public int sp2px(float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /***
     * px 转SP  TextView
     * @param pxVal
     * @return
     */
    public int px2sp(float pxVal){
        return (int)(pxVal/context.getResources().getDisplayMetrics().scaledDensity);
    }

    /***
     * 获取当前说及屏幕宽高
     */
    public void screenInfo(){
        Display mDisplay = context.getWindowManager().getDefaultDisplay();
        W = mDisplay.getWidth();
        H = mDisplay.getHeight();
        //System.out.println("("+W+"*"+H+")"+":" + this.px2dip( W)+"*"+this.px2dip(H));
        //Log.d("ljh","("+W+"*"+H+")"+":" + this.px2dip( W)+"*"+this.px2dip(H));
    }


    /***
     *  创建Pop对话框
     * @param context
     * @param layoutId  布局ID
     * @param backGroundId 背景资源ID
     * @param width 宽
     * @param height 高
     * @return
     */
    public PopupWindow createPopWindows(Context context
    		,int layoutId
            ,int backGroundId
            ,int width
            ,int height){

        LayoutInflater mInflater=LayoutInflater.from(context);
        View popView=mInflater.inflate(layoutId,null,false);
        PopupWindow pop=new PopupWindow(popView,width,height);
        pop.setBackgroundDrawable(context.getResources().getDrawable(backGroundId));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        return pop;
    }

}
