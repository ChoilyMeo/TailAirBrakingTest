package com.thnet.tailairbrakingtest.utility;

import android.media.MediaPlayer;
import android.util.Log;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.R;

public class TipSoundPlayer {
    private static TipSoundPlayer instance;
    private static final String TAG = TipSoundPlayer.class.getSimpleName();
    public static final int VOICE_FILE_NAME_BEGIN_AD = R.raw.andingshiyan;//@"\Voice\安定试验.WAV";
    public static final int VOICE_FILE_NAME_COMPLETED_AD = R.raw.andinghege;//@"\Voice\安定合格.WAV";
    public static final int VOICE_FILE_NAME_NOT_COMPLETED_AD = R.raw.andingbuhege;//@"\Voice\安定不合格.WAV";
    public static final int VOICE_FILE_NAME_BEGIN_GD = R.raw.gandushiyan;//@"\Voice\感度试验.WAV";
    public static final int VOICE_FILE_NAME_COMPLETED_GD = R.raw.ganduhege;//@"\Voice\感度合格.WAV";
    public static final int VOICE_FILE_NAME_NOT_COMPLETED_GD = R.raw.gandubuhege;//@"\Voice\感度不合格.WAV";
    public static final int VOICE_FILE_NAME_BEGIN_BY = R.raw.chixubaoyashiyan;//@"\Voice\持续保压试验.WAV";
    public static final int VOICE_FILE_NAME_COMPLETED_BY = R.raw.chixubaoyahege;//@"\Voice\持续保压合格.WAV";
    public static final int VOICE_FILE_NAME_NOT_COMPLETED_BY = R.raw.chixubaoyabuhege;//@"\Voice\持续保压不合格.WAV";
    public static final int VOICE_FILE_NAME_BEGIN_JL = R.raw.jianlueshiyan;//@"\Voice\简略试验.WAV";
    public static final int VOICE_FILE_NAME_COMPLETED_JL = R.raw.jianluewanbi;//@"\Voice\简略完毕.WAV";
    public static final int VOICE_FILE_NAME_NOT_COMPLETED_JL = R.raw.jianluewanbi;//@"\Voice\简略完毕.WAV";
    public static final int VOICE_FILE_NAME_BEGIN_LX = R.raw.louxieshiyan;//@"\Voice\漏写试验.WAV";
    public static final int VOICE_FILE_NAME_COMPLETED_LX = R.raw.louxiehege;//@"\Voice\漏写合格.WAV";
    public static final int VOICE_FILE_NAME_NOT_COMPLETED_LX = R.raw.louxiebuhege;//@"\Voice\漏写不合格.WAV";
    public static final int VOICE_FILE_NAME_BEGIN_TEST_WIND = R.raw.zhunbeishifeng;//@"\Voice\准备试风.WAV";
    public static final int VOICE_FILE_NAME_END_TEST_WIND = R.raw.shifengwanbi;//@"\Voice\试风完毕.WAV";
    public static final int VOICE_FILE_NAME_PRESSURE_TOO_HIGH = R.raw.yaliguogao;//压力过高;

    private static MediaPlayer mediaPlayer;

    public static void PlayVoicePrompts(int voiceName){
        try{
            mediaPlayer = MediaPlayer.create(WindTestApplication.getWindTestInstance(), voiceName);
            if (mediaPlayer.isPlaying()){
                return;
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
            mediaPlayer.start();
            //mediaPlayer.release();
            //mediaPlayer = null;
        }
        catch (Exception ex){
            Log.e(TAG, "播放提示音异常。");
        }
    }

    private TipSoundPlayer(){ }

    public static TipSoundPlayer getInstance(){
        if (null == instance){
            instance = new TipSoundPlayer();
        }
        return instance;
    }
}
