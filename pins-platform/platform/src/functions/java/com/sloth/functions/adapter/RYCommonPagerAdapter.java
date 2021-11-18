package com.sloth.functions.adapter;

import android.view.View;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/30 13:28
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/30         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class RYCommonPagerAdapter extends RYBasePagerAdapter<View> {

    @Override
    public View getView(int position) {
        return getData(position);
    }

}
