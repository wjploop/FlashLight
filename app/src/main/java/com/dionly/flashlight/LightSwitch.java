package com.dionly.flashlight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LightSwitch extends View {

    private Bitmap bg, onBtn, offBtn, curBtn;
    private Paint paint;

    private float bgWidth, bgHeight;
    private float btnWidth, btnHeight;

    private boolean isInit = true;

    private int state = -1;

    private float downX, downY;
    private boolean mToggleOpen = false;

    private float offsetX;


    public LightSwitch(Context context) {
        this(context, null);
    }

    public LightSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        loadBitmap();
    }

    private void loadBitmap() {
        bg = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_bg);
        onBtn = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_btn_on);
        offBtn = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_btn_off);

        curBtn = offBtn;

        bgWidth = bg.getWidth();
        bgHeight = bg.getHeight();

        btnWidth = onBtn.getWidth();
        btnHeight = onBtn.getHeight();



        offsetX = (bgWidth - btnWidth) / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) bgWidth, (int) bgHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bg, 0, 0, paint);

        if (isInit) {
            canvas.drawBitmap(curBtn, offsetX, bgHeight - btnHeight, paint);
            isInit = false;
        } else if (state == MotionEvent.ACTION_DOWN) {
            canvas.drawBitmap(curBtn, offsetX, downY - btnHeight / 2, paint);
        } else if (state == MotionEvent.ACTION_MOVE) {
            canvas.drawBitmap(curBtn, offsetX, downY - btnHeight / 2, paint);
        } else if (state == MotionEvent.ACTION_UP) {
            canvas.drawBitmap(curBtn, offsetX, downY - btnHeight / 2, paint);
        }else{
            canvas.drawBitmap(curBtn, offsetX, bgHeight - btnHeight, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                if (downY > bgHeight - btnHeight / 2) {
                    downY = bgHeight - btnHeight / 2;
                }
                if (downY < btnHeight / 2) {
                    downY = btnHeight / 2;
                }
                state = MotionEvent.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_MOVE:
                state = MotionEvent.ACTION_MOVE;

                float moveY = event.getY();

                downY = moveY;

                if (downY > bgHeight - btnHeight / 2) {
                    downY = bgHeight - btnHeight / 2;
                }
                if (downY < btnHeight / 2) {
                    downY = btnHeight / 2;
                }
                if (downY >= bgHeight / 2) {
                    curBtn = offBtn;
                } else {
                    curBtn = onBtn;
                }
//                Log.d("wolf", "downY=" + downY + curBtn + " init=" + isInit);
                break;
            case MotionEvent.ACTION_UP:

                state = MotionEvent.ACTION_UP;

                downY = event.getY();

                if (downY >= bgHeight - btnHeight / 2) {
                    downY = bgHeight - btnHeight / 2;
                }
                if (downY < btnHeight / 2) {
                    downY = btnHeight / 2;
                }

                if (downY >= bgHeight / 2) {
                    downY = bgHeight - btnHeight / 2;
                    curBtn = offBtn;
                    if (mToggleOpen) {
                        mToggleOpen = false;
                        if (listener != null) {
                            listener.setToggleState(false);
                        }
                    }
                } else {
                    downY = btnHeight - btnHeight / 2;
                    curBtn = onBtn;
                    if (!mToggleOpen) {
                        mToggleOpen = true;
                        if (listener != null) {
                            listener.setToggleState(true);
                        }
                    }
                }

        }

        invalidate();
        return true;
    }

    public onSlideListener listener;

    public interface onSlideListener {
        void setToggleState(boolean open);
    }

    public void setOnSlideListener(onSlideListener listener) {
        this.listener = listener;
    }

}
