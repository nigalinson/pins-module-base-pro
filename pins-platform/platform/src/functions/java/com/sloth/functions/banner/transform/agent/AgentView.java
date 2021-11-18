package com.sloth.functions.banner.transform.agent;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 16:18
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface AgentView {

    void play(Rect rect, Animator.AnimatorListener animatorListener, Object... args);

    View view();

}
