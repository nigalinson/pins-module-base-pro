package com.sloth.functions.layoutmanager.base;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/1/20 16:51
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/1/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class OnRecyclerViewStageChangeListener implements OnRecyclerViewPageChangeListener {

    private int oldPos = -1;

    @Override
    public void onPageChanged(int position) {
        if(oldPos != -1){
            onDownStage(oldPos);
        }
        oldPos = position;

        onStage(oldPos);
    }

    protected abstract void onDownStage(int pos);

    protected abstract void onStage(int pos);

}
