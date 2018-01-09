package com.michael.takephoto.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;

//带手势操作的imageView

public class CustomPreviewImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "customPreviewImageView";

    //动作状态记录
    private enum MODE {
        NONE, DRAG, ZOOM
    }

    private boolean isMonitorV = false;        //垂直监控

    private boolean isMonitorH = false;        //水平监控

    private boolean isScaleAnim = false;    //是否开启动画

    private ScaleAnimation scaleAnimation;    //缩放动画

    public MODE mode = MODE.NONE;

    private int screenW, screenH;        //屏幕可见宽高

    private int imageW, imageH;            //图片当前宽高

    //位置记录
    private int MAX_W, MAX_H, MIN_W, MIN_H;        //宽高极限值

    private int curTop, curRight, curBottom, curLeft;        //当前图片边框值

    private int defaultTop = -1, defaultRight = -1, defaultBottom = -1, defaultLeft = -1;        //默认边框值

    private Point defaultPoint, currentPoint;        //触摸位置

    private float defaultDistance, newDistance;        //触摸点距离

    private float tml_scale;        //当前缩放比例


    public CustomPreviewImageView(Context context) {
        super(context);
        //initial variable
        defaultPoint = new Point();
        currentPoint = new Point();
    }

    public CustomPreviewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //initial variable
        defaultPoint = new Point();
        currentPoint = new Point();
    }

    /*
     * 初始化屏幕可见区域大小参数
     */
    public void setScreenSize(int width, int height) {
        screenH = height;
        screenW = width;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        //图片默认大小
        imageW = bm.getWidth();
        imageH = bm.getHeight();

        //设定极限值(3倍大小,1/2最小值)
        MAX_H = imageH * 3;
        MAX_W = imageW * 3;

        MIN_H = imageH / 2;
        MIN_W = imageW / 2;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //初始化坐标位置
        if (defaultTop == -1) {
            defaultTop = top;
            defaultLeft = left;
            defaultRight = right;
            defaultBottom = bottom;
        }
    }


    /**
     * 判断手势事件
     * 规则:
     * 单手指操作：ACTION_DOWN---ACTION_MOVE----ACTION_UP
     * 多手指操作：ACTION_DOWN---ACTION_POINTER_DOWN---ACTION_MOVE--ACTION_POINTER_UP---ACTION_UP.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);        //处理按下的动作(第一个动作)
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //判断是否两手操作缩放动作
                onZoomTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);        //拖动
                break;
            case MotionEvent.ACTION_UP:
                //结束动作
                mode = MODE.NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE.NONE;
                //执行缩放动画
                if (isScaleAnim) {
                    actScaleAnim();
                }
                break;
        }

        return true;
    }

    /**
     * 一些动作
     */

    //按下
    private void onTouchDown(MotionEvent event) {
        mode = MODE.DRAG;        //拖拽模式开始

        //初始化坐标
        currentPoint.x = (int) event.getRawX();
        currentPoint.y = (int) event.getRawY();

        defaultPoint.x = (int) event.getX();
        defaultPoint.y = currentPoint.y - getTop();

//		Log.v(TAG,"curPoint:"+currentPoint.toString());
//		Log.v(TAG,"defaultPoint:"+defaultPoint.toString());
    }

    //缩放动作(两手指)
    private void onZoomTouch(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mode = MODE.ZOOM;
            //获取两点距离
            defaultDistance = getDistance(event);
        }
    }


    //触摸拖动响应
    private void onTouchMove(MotionEvent event) {
        Log.v(TAG, "moving mode=" + mode);
        int tmpLeft = 0, tmpRight = 0, tmpTop = 0, tmpBottom = 0;
        if (mode == MODE.DRAG) {        //�϶��¼�

            //test
            //only zoom

            //防止drag越界
//			Log.v(TAG,"Draging");
            //获取拖动的新位置
            tmpLeft = currentPoint.x - defaultPoint.x;
            tmpRight = currentPoint.x + getWidth() - defaultPoint.x;
            tmpTop = currentPoint.y - defaultPoint.y;
            tmpBottom = currentPoint.y + getHeight() - defaultPoint.y;

            //水平判断
            if (isMonitorH) {
                if (tmpLeft >= 0) {
                    tmpLeft = 0;
                    tmpRight = getWidth();
                }
                if (tmpRight <= screenW) {
                    tmpLeft = screenW - getWidth();
                    tmpRight = screenW;
                }
            } else {
                tmpLeft = getLeft();
                tmpRight = getRight();
            }

            //垂直判断
            if (isMonitorV) {
                if (tmpTop >= 0) {
                    tmpTop = 0;
                    tmpBottom = getHeight();
                }
                if (tmpBottom <= screenH) {
                    tmpTop = screenH - getHeight();
                    tmpBottom = screenH;
                }
            } else {
                tmpTop = getTop();
                tmpBottom = getBottom();
            }

            if (isMonitorH || isMonitorV) {
                //根据位置重绘
                layout(tmpLeft, tmpTop, tmpRight, tmpBottom);
            }

            currentPoint.x = (int) event.getRawX();
            currentPoint.y = (int) event.getRawY();

        } else if (mode == MODE.ZOOM) {        //处理缩放
            newDistance = getDistance(event);

            float gapDistance = newDistance - defaultDistance;        //变化的距离

            if (Math.abs(gapDistance) > 5f) {        //大于5就开始计算缩放比例
                tml_scale = newDistance / defaultDistance;

                setScale(tml_scale);

                defaultDistance = newDistance;
            }

        }
    }


    //计算两点距离
    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    //缩放处理
    void setScale(float scale) {
        int disX = (int) (getWidth() * Math.abs(1 - scale)) / 4;            //缩放水平距离
        int disY = (int) (getHeight() * Math.abs(1 - scale)) / 4;            //缩放垂直距离


        if (scale > 1 && getWidth() <= MAX_W) {
            //zoom in 放大
            //计算新的大小
            curLeft = getLeft() - disX;
            curTop = getTop() - disY;
            curRight = getRight() + disX;
            curBottom = getBottom() + disY;

            setFrame(curLeft, curTop, curRight, curBottom);

            //TODO
            //对称，只做一次判断
            if (curTop <= 0 && curBottom >= screenH) {
                //已经超出屏幕范围
                isMonitorV = true;        //垂直监控
            } else {
                isMonitorV = false;
            }
            if (curLeft <= 0 && curRight >= screenW) {
                //已经超出屏幕范围
                isMonitorH = true;        //水平监控
            } else {
                isMonitorH = false;
            }
        } else if (scale < 1 && getWidth() >= MIN_W) {
            //zoom out 缩小
            curLeft = getLeft() + disX;
            curTop = getTop() + disY;
            curRight = getRight() - disX;
            curBottom = getBottom() - disY;

            //上方越界
            if (isMonitorV && curTop > 0) {
                curTop = 0;
                curBottom = getBottom() - 2 * disY;        //以两倍速度溃缩
                if (curBottom < screenH) {
                    curBottom = screenH;
                    isMonitorV = false;
                }
            }

            //下方越界
            if (isMonitorV && curBottom < screenH) {
                curBottom = screenH;
                curTop = getTop() + 2 * disY;
                if (curTop > 0) {
                    curTop = 0;
                    isMonitorV = false;
                }
            }

            //左边越界
            if (isMonitorH && curLeft >= 0) {
                curLeft = 0;
                curRight = getRight() - 2 * disX;
                if (curRight <= screenW) {
                    curRight = screenW;
                    isMonitorH = false;
                }
            }


            //右边越界
            if (isMonitorH && curRight <= screenW) {
                curRight = screenW;
                curLeft = getLeft() + 2 * disX;
                if (curLeft >= 0) {
                    curLeft = 0;
                    isMonitorH = false;
                }
            }

            //
            this.setFrame(curLeft, curTop, curRight, curBottom);
            if (isMonitorH || isMonitorV) {
                //还在范围外
            } else {
                isScaleAnim = true;        //开启动画
            }

        }


    }

    /**
     * 回缩动画处理
     */
    private void actScaleAnim() {

    }

    class AnimAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

    }

}
