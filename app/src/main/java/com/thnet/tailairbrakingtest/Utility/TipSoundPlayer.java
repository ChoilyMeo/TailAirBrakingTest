package com.thnet.tailairbrakingtest.Utility;

import android.media.MediaPlayer;
import android.util.Log;

import com.thnet.tailairbrakingtest.CustomApplication.WindTestApplication;
import com.thnet.tailairbrakingtest.R;

public class TipSoundPlayer {
    private static TipSoundPlayer instance;
    private static final String TAG = TipSoundPlayer.class.getSimpleName();
    public static final int nVoiceFileNameBegin_ad = R.raw.andingshiyan;//@"\Voice\安定试验.WAV";
    public static final int nVoiceFileNameCompleted_ad = R.raw.andinghege;//@"\Voice\安定合格.WAV";
    public static final int nVoiceFileNameNotCompleted_ad = R.raw.andingbuhege;//@"\Voice\安定不合格.WAV";
    public static final int nVoiceFileNameBegin_gd = R.raw.gandushiyan;//@"\Voice\感度试验.WAV";
    public static final int nVoiceFileNameCompleted_gd = R.raw.ganduhege;//@"\Voice\感度合格.WAV";
    public static final int nVoiceFileNameNotCompleted_gd = R.raw.gandubuhege;//@"\Voice\感度不合格.WAV";
    public static final int nVoiceFileNameBegin_by = R.raw.chixubaoyashiyan;//@"\Voice\持续保压试验.WAV";
    public static final int nVoiceFileNameCompleted_by = R.raw.chixubaoyahege;//@"\Voice\持续保压合格.WAV";
    public static final int nVoiceFileNameNotCompleted_by = R.raw.chixubaoyabuhege;//@"\Voice\持续保压不合格.WAV";
    public static final int nVoiceFileNameBegin_jl = R.raw.jianlueshiyan;//@"\Voice\简略试验.WAV";
    public static final int nVoiceFileNameCompleted_jl = R.raw.jianluewanbi;//@"\Voice\简略完毕.WAV";
    public static final int nVoiceFileNameNotCompleted_jl = R.raw.jianluewanbi;//@"\Voice\简略完毕.WAV";
    public static final int nVoiceFileNameBegin_lx = R.raw.louxieshiyan;//@"\Voice\漏写试验.WAV";
    public static final int nVoiceFileNameCompleted_lx = R.raw.louxiehege;//@"\Voice\漏写合格.WAV";
    public static final int nVoiceFileNameNotCompleted_lx = R.raw.louxiebuhege;//@"\Voice\漏写不合格.WAV";
    public static final int nVoiceFileNameBeginTestWind = R.raw.zhunbeishifeng;//@"\Voice\准备试风.WAV";
    public static final int nVoiceFileNameBegin_klw = R.raw.kelieweishiyankaishi;//@"\Voice\客列尾试验.WAV";
    public static final int nVoiceFileNameCompleted_klw = R.raw.kelieweishiyanhege;//@"\Voice\客列尾试验合格.WAV";
    public static final int nVoiceFileNameNotCompleted_klw = R.raw.kelieweishiyanbuhege;//@"\Voice\客列尾试验不合格.WAV";
    public static final int nVoiceFileNameBegin_yljy = R.raw.chuanganqijiaoyankaishi;//@"\Voice\压力校验试验.WAV";
    public static final int nVoiceFileNameCompleted_yljy = R.raw.chuanganqijiaoyanhege;//@"\Voice\压力校验完毕.WAV";
    public static final int nVoiceFileNameNotCompleted_yljy =R.raw.chuanganqijiaoyanbuhege;// @"\Voice\压力校验完毕.WAV";

    private static MediaPlayer mediaPlayer;

    public static void PlayVoicePrompts(int voiceName){
        try{
            mediaPlayer = MediaPlayer.create(WindTestApplication.getWindTestInstance(), voiceName);
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
