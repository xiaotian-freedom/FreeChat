package com.common.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.R;
import com.common.util.CommonUtil;
import com.common.util.DensityUtil;
import com.common.util.PermissionUtil;
import com.common.util.TimeUtil;

import java.io.File;

public class RecordButton extends AppCompatButton {
    public static final int BACK_RECORDING = R.drawable.chat_voice_bg_pressed;
    public static final int BACK_NORMAL = R.drawable.chat_voice_bg_normal;
//    public static final int BACK_IDLE = R.drawable.chat_voice_round_bg;
    public static final int SLIDE_UP_TO_CANCEL = 0;
    public static final int RELEASE_TO_CANCEL = 1;
    public static final int RECORD_AUDIO = 100;
    public static final int WRITE_FILE = 200;
    public static final int READ_FILE = 300;
    private static final int MIN_INTERVAL_TIME = 1000;// 2s
    private static int[] recordImageIds = {R.drawable.chat_icon_voice0,
            R.drawable.chat_icon_voice1, R.drawable.chat_icon_voice2,
            R.drawable.chat_icon_voice3, R.drawable.chat_icon_voice4,
            R.drawable.chat_icon_voice5};
    private String outputPath = null;
    private RecordEventListener recordEventListener;
    private long startTime;
    private Dialog recordIndicator;
    private MediaRecorder recorder;
    private ObtainDecibelThread thread;
    private Handler volumeHandler;

    private View view;
    private ImageView soundView;
    private TextView tipView;
    private TextView tickTime;
    private LinearLayout tickLayout;
    private int status;
    private Context context;

    private boolean recordComplete;

    private OnDismissListener onDismiss = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    public RecordButton(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setSavePath(String path) {
        outputPath = path;
    }

    public void setRecordEventListener(RecordEventListener listener) {
        recordEventListener = listener;
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
        setBackgroundResource(BACK_NORMAL);
        initRecordDialog();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (recordEventListener != null) {
            recordEventListener.checkPermission();
        }
        if (!PermissionUtil.isReadRecord(context)) {
            return false;
        }
        if (outputPath == null)
            return false;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startRecord();
                break;
            case MotionEvent.ACTION_UP:
                setText(R.string.chat_bottom_record_layout_pressToRecord);
                if (status == RELEASE_TO_CANCEL) {
                    cancelRecord();
                } else {
                    finishRecord();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    status = RELEASE_TO_CANCEL;
                } else {
                    status = SLIDE_UP_TO_CANCEL;
                }
                setTextViewByStatus();
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelRecord();
                break;
        }
        return true;
    }

    public int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    private void setTextViewByStatus() {
        if (status == RELEASE_TO_CANCEL) {
            tipView.setTextColor(getColor(R.color.color_r));
            tipView.setText(R.string.chat_record_button_releaseToCancel);
        } else if (status == SLIDE_UP_TO_CANCEL) {
            tipView.setTextColor(getColor(R.color.color_w));
            tipView.setText(R.string.chat_record_button_slideUpToCancel);
        }
    }

    private void startRecord() {
        startTime = System.currentTimeMillis();
        setBackgroundResource(BACK_RECORDING);
        setText(R.string.chat_bottom_record_layout_releaseToSend);
        startRecording();
    }

    private void initRecordDialog() {
        recordIndicator = new Dialog(getContext(),
                R.style.chat_record_button_toast_dialog_style);

        view = inflate(getContext(), R.layout.chat_record_layout, null);
        soundView = (ImageView) view.findViewById(R.id.imageView);
        tipView = (TextView) view.findViewById(R.id.textView);
        tickTime = (TextView) view.findViewById(R.id.tickTime);
        tickLayout = (LinearLayout) view.findViewById(R.id.chat_voice_layout);

        recordIndicator.setContentView(view, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        recordIndicator.setOnDismissListener(onDismiss);

        LayoutParams lp = recordIndicator.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;

        showSoundView(true);
    }

    private void finishRecord() {
        stopRecording();
        recordIndicator.dismiss();
        setBackgroundResource(BACK_NORMAL);
        showSoundView(true);

        if (recordComplete) {
            long intervalTime = System.currentTimeMillis() - startTime;
            if (intervalTime < MIN_INTERVAL_TIME) {
                CommonUtil.showToast(context, R.string.chat_record_button_pleaseSayMore);
                File file = new File(outputPath);
                file.delete();
                return;
            }

//            int sec = Math.round(intervalTime * 1.0f / 1000);
            if (recordEventListener != null) {
                recordEventListener.onFinishedRecord(outputPath, intervalTime);
            }
            recordComplete = false;
        }
    }

    private void cancelRecord() {
        stopRecording();
        setBackgroundResource(BACK_NORMAL);
        recordIndicator.dismiss();
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 显示声音view
     *
     * @param active
     */
    private void showSoundView(boolean active) {
        if (active) {
            ViewGroup.LayoutParams lp = tickLayout.getLayoutParams();
            lp.width = DensityUtil.dip2px(context, 150);
            lp.height = DensityUtil.dip2px(context, 150);
            tickLayout.setLayoutParams(lp);
            soundView.setVisibility(VISIBLE);
            tipView.setVisibility(VISIBLE);
            tickTime.setVisibility(GONE);
        } else {
            ViewGroup.LayoutParams lp = tickLayout.getLayoutParams();
            lp.width = DensityUtil.dip2px(context, 150);
            lp.height = DensityUtil.dip2px(context, 150);
            tickLayout.setLayoutParams(lp);
            soundView.setVisibility(GONE);
            tipView.setVisibility(GONE);
            tickTime.setVisibility(VISIBLE);
        }
    }

    private void startRecording() {
        try {
            if (recorder == null) {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(outputPath);
                recorder.prepare();
            } else {
                recorder.reset();
                recorder.setOutputFile(outputPath);
            }
            recorder.start();
            thread = new ObtainDecibelThread();
            thread.start();
            if (recordEventListener != null) {
                recordEventListener.onStartRecord();
            }
            File file = new File(outputPath);
            if (file.exists()) {
                long length = file.length();
                if (length == 0) {
                    throw new IllegalStateException("生成的录音文件无内容");
                }
            }
            recordComplete = true;
            recordIndicator.show();
        } catch (Exception e) {
            recordComplete = false;
            CommonUtil.showCustomDialog(context, "请在权限管理中开启语音权限", false, true);
        }
    }

    private void stopRecording() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
            } catch (Exception e) {
                // TODO: handle exception
                recorder = null;
            }
        }
    }

    public interface RecordEventListener {
        void onFinishedRecord(String audioPath, long time);

        void onStartRecord();

        void checkPermission();
    }

    int tick = 10;

    private class ObtainDecibelThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                if (TimeUtil.remainTenSeconds(startTime)) {
                    while (tick >= 0) {
                        volumeHandler.sendEmptyMessage(-2);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (tick == 0) break;
                        tick--;
                    }
                }
                if (tick == 0) {
                    volumeHandler.sendEmptyMessage(-1);
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                int x = recorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    int index = (f - 18) / 5;
                    if (index < 0)
                        index = 0;
                    if (index > 5)
                        index = 5;
                    volumeHandler.sendEmptyMessage(index);
                }
            }
        }

    }

    @SuppressLint("HandlerLeak")
    class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                finishRecord();
            } else if (msg.what == -2) {
                if (soundView.getVisibility() == VISIBLE) {
                    showSoundView(false);
                }
                tickTime.setText(String.valueOf(tick));
            } else {
                soundView.setImageResource(recordImageIds[msg.what]);
            }
        }
    }

}
