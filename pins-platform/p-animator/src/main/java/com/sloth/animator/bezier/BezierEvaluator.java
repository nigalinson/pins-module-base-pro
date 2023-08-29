package com.sloth.animator.bezier;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2019-08-30 16:54
 * Description: 贝塞尔曲线插值器
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2019-08-30      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class BezierEvaluator implements TypeEvaluator<Point> {

  /**
   * 贝塞尔曲线中间的转折点
   */
  private final Point controllerPoint1;

    /**
     * 第二个控制点 （如果不为空，则是三阶贝塞尔曲线）
   */
  private Point controllerPoint2;

  public BezierEvaluator(Point controllerPoint) {
    this.controllerPoint1 = controllerPoint;
  }

  public BezierEvaluator(Point controllerPoint, Point controllerPoint2) {
    this.controllerPoint1 = controllerPoint;
    this.controllerPoint2 = controllerPoint2;
  }

  /**
   * @param t          变化值
   * @param startValue 是起始的位置
   * @param endValue   是结束的位置
   * @return 贝塞尔曲线（二阶抛物线）
   * P0起始点 P1控制点 P2终点
   * P = (1-t)^2*P0 + 2*(1-t)*t*P1 + t^2*P2
   */
  @Override
  public Point evaluate(float t, Point startValue, Point endValue) {
    if(controllerPoint2 == null){
      //二阶贝塞尔
      int x = (int) ((1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * controllerPoint1.x + t * t * endValue.x);
      int y = (int) ((1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * controllerPoint1.y + t * t * endValue.y);
      return new Point(x, y);
    }else{
      //三阶阶贝塞尔
      //A*((1-t)^3) + B*3*t*((1-t) ^ 2) + C*(1-t)*(t^2) + D*(t^3)
      int x = (int) ((1-t)*(1-t)*(1-t)*startValue.x + 3*t*(1-t)*(1-t)*controllerPoint1.x + 3*(1-t)*t*t*controllerPoint2.x + t*t*t*endValue.x);
      int y = (int) ((1-t)*(1-t)*(1-t)*startValue.y + 3*t*(1-t)*(1-t)*controllerPoint1.y + 3*(1-t)*t*t*controllerPoint2.y + t*t*t*endValue.y);
      return new Point(x, y);
    }
  }
}
