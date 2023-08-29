package com.sloth.widget.hotarea;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HotAreaProxy {

    private final List<HotAreaClickListener> listeners = new ArrayList<>();
    private final List<HotArea> hotAreaList = new ArrayList<>();

    private final HotAreaTarget target;

    private boolean debugMode = false;
    private Paint debugPaint;
    private boolean drawOriginIconSize;

    private Bitmap icon;
    private Rect src;
    private Rect dest;
    private Paint icPaint;

    public HotAreaProxy(HotAreaTarget target) {
        this.target = target;
        this.target.setWillNotDraw(false);
        this.drawOriginIconSize = false;
    }

    public HotAreaProxy addHotArea(HotArea hotArea){
        hotAreaList.add(hotArea);
        return this;
    }

    public HotAreaProxy addHotArea(HotArea... hotAreas){
        hotAreaList.addAll(Arrays.asList(hotAreas));
        return this;
    }

    public HotAreaProxy addHotArea(Collection<HotArea> hotAreas){
        hotAreaList.addAll(hotAreas);
        return this;
    }

    public void removeHotArea(HotArea hotArea){
        int index = hotAreaList.indexOf(hotArea);
        if(index != -1){
            hotAreaList.remove(index);
        }
    }

    public void clearHotArea(){
        hotAreaList.clear();
    }

    public HotAreaProxy addHotAreaListener(HotAreaClickListener hotAreaClickListener){
        if(!listeners.contains(hotAreaClickListener)){
            listeners.add(hotAreaClickListener);
        }
        return this;
    }

    public void removeHotAreaListener(HotAreaClickListener hotAreaClickListener){
        int index = listeners.indexOf(hotAreaClickListener);
        if(index != -1){
            listeners.remove(index);
        }
    }

    public void clearHotAreaListeners(){
        listeners.clear();
    }

    public void clear(){
        clearHotAreaListeners();
        clearHotArea();
    }

    public void setDrawOriginIconSize(boolean drawOriginIconSize) {
        this.drawOriginIconSize = drawOriginIconSize;
    }

    /**
     * for view
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event){
        return event.getAction() == MotionEvent.ACTION_DOWN && onHitArea(event.getX(), event.getY());
    }

    private boolean onHitArea(float x, float y){
        for(HotArea hotArea: hotAreaList){
            if(hotArea.contains(x, y)){
                triggerClicked(hotArea);
                return true;
            }
        }
        return false;
    }

    private void triggerClicked(HotArea hotArea) {
        for(HotAreaClickListener listener: listeners){
            listener.onHotAreaClicked(hotArea);
        }
    }

    public HotAreaProxy setDebugMode(boolean openDebug){
        debugMode = openDebug;
        return this;
    }

    public boolean isDebugMode(){
        return debugMode;
    }

    public HotAreaProxy triggerDebugMode(){
        debugMode = !debugMode;
        return this;
    }

    public void invalidate(){
        target.invalidate();
    }

    public void setIcon(int res){
        icon = BitmapFactory.decodeResource(target.getResource(), res);
        src = new Rect(0,0,icon.getWidth(), icon.getHeight());
        dest = new Rect(0,0,0, 0);
    }

    public void setIcon(Bitmap ic){
        icon = ic;
        src = new Rect(0,0,icon.getWidth(), icon.getHeight());
        dest = new Rect(0,0,0, 0);
    }

    public void onDraw(Canvas canvas){
        if(icon != null && src != null){
            initIconPaint();
            for(HotArea hotArea: hotAreaList){
                if(drawOriginIconSize){
                    dest.set((int)hotArea.left, (int)hotArea.top, (int)(hotArea.left + src.width()), (int)(hotArea.top + src.height()));
                    canvas.drawBitmap(icon, src, dest, icPaint);
                }else{
                    canvas.drawBitmap(icon, src, hotArea, icPaint);
                }
            }
        }

        if(!debugMode) return;
        initDebugPaint();
        for(HotArea hotArea: hotAreaList){
            canvas.drawRect(hotArea, debugPaint);
        }
    }

    private void initIconPaint() {
        if(icPaint == null){
            icPaint = new Paint();
            icPaint.setColor(Color.parseColor("#ffffff"));
            icPaint.setStyle(Paint.Style.FILL);
        }
    }

    private void initDebugPaint() {
        if(debugPaint == null){
            debugPaint = new Paint();
            debugPaint.setColor(Color.parseColor("#800000FF"));
            debugPaint.setStyle(Paint.Style.FILL);
        }
    }

}
