package face;

import android.content.Context;
import android.os.PowerManager;
import android.util.Base64;

import com.xiangpu.utils.SharedPrefUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ConUtil {
    public static PowerManager.WakeLock wakeLock = null;

    public static void acquireWakeLock(Context context) {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) (context.getSystemService(Context.POWER_SERVICE));
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
            wakeLock.acquire();
        }
    }

    public static void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    public static String getUUIDString(Context mContext) {
        String KEY_UUID = "key_uuid";
        String uuid = SharedPrefUtils.getStringData(mContext, KEY_UUID, "");
        if (uuid != null && uuid.trim().length() != 0) {
            return uuid;
        }
        uuid = UUID.randomUUID().toString();
        uuid = Base64.encodeToString(uuid.getBytes(), Base64.DEFAULT);
        SharedPrefUtils.saveStringData(mContext, KEY_UUID, "");
        return uuid;
    }

    public static byte[] getFileContent(Context context, int id) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count = -1;
        try {
            inputStream = context.getResources().openRawResource(id);
            while ((count = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, count);
            }
            byteArrayOutputStream.close();
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }

}
