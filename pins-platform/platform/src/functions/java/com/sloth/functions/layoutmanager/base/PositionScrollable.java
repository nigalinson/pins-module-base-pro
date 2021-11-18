package com.sloth.functions.layoutmanager.base;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/4 15:20
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/4         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface PositionScrollable {

    int getCurrentPosition();

    int aimingPosition();

    int distanceToTargetPosition(int targetPosition);

}
