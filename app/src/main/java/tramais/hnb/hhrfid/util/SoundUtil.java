package tramais.hnb.hhrfid.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import tramais.hnb.hhrfid.R;

import static android.content.Context.AUDIO_SERVICE;

public class SoundUtil {

    static HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private static SoundPool soundPool;
    private static float volumnRatio;
    private static AudioManager am;

    public static void initSound(Context context) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(context, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(context, R.raw.serror, 1));
        soundMap.put(3, soundPool.load(context, R.raw.camera_click, 1));
        // 实例化AudioManager对象
        am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public static void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, // 右声道音量
                    1, // 优先级，0为最低
                    0, // 循环次数，0无不循环，-1无永远循环
                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (soundPool != null)
            soundPool.autoPause();
    }
}
