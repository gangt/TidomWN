package com.xiangpu.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;

import com.xiangpu.activity.WebMainActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/xiangpu/";

    public static String byte2hex(byte[] bytes) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        char str[] = new char[bytes.length * 2];
        for (int i = 0, k = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            str[k++] = hexDigits[b >>> 4 & 0xf];
            str[k++] = hexDigits[b & 0xf];
        }
        return new String(str);
    }

    public static byte[] hex2byte(String hex) {
        char[] str = hex.toCharArray();
        byte[] res = new byte[str.length / 2];

        for (int i = 0; i != str.length; i += 2) {
            char[] h = {'0', 'x', str[i], str[i + 1]};
            Integer b = Integer.decode(new String(h));
            res[i / 2] = b.byteValue();
        }
        return res;
    }

    public static String remove(String resource, char ch) {
        StringBuffer buffer = new StringBuffer();
        int position = 0;
        char currentChar;

        while (position < resource.length()) {
            currentChar = resource.charAt(position++);
            if (currentChar != ch) buffer.append(currentChar);
        }
        return buffer.toString();
    }

    /**
     * 获取当前应用程序的包名
     *
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public static void goWebMainActivity(Context context, String link, String title) {
        Intent intent = new Intent(context, WebMainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        context.startActivity(intent);
    }

    public static void goWebMainActivity(Context context,String link, String title, String tagPlugin) {
        Intent intent = new Intent(context, WebMainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        intent.putExtra("TagPlugin", tagPlugin);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<? extends Activity> clazz) {
        context.startActivity(new Intent(context, clazz));
    }

    public static Bitmap getUrlToImage(String uri) {
        Bitmap bmp = null;

        try {
            URL url = new URL(uri);

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(1000 * 5);
            conn.connect();

            InputStream in = conn.getInputStream();

            bmp = BitmapFactory.decodeStream(in);

            byte[] data;
            if (bmp == null) {
                data = readStream(in);
                if (data != null) {
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
            }

            in.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public static BitmapDrawable BmpToDraw(Bitmap bmp) {
        if (bmp == null) return null;
        BitmapDrawable bd = new BitmapDrawable(bmp);
        return bd;
    }

    public static Bitmap DrawToBmp(Drawable draw) {
        if (draw == null) return null;

        int w = draw.getIntrinsicWidth() * 2;
        int h = draw.getIntrinsicHeight() * 2;

        Bitmap.Config config = draw.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;

        Bitmap bitmap = Bitmap.createBitmap(w, h, config);

        Canvas canvas = new Canvas(bitmap);
        draw.setBounds(0, 0, w, h);
        // �? drawable 内容画到画布�?
        draw.draw(canvas);

        return bitmap;
    }

    public static boolean getDrawable(String uri, String path) {
        boolean bOk = false;
        try {
            URL url = new URL(uri);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(1000 * 5);
            conn.connect();

            InputStream in = conn.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(in);

            if (bitmap != null) {
                saveBitmapTofile(bitmap, path, uri);
                in.close();
                bOk = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bOk;
    }

    public static Drawable getDrawable(String uri) {
        Drawable draw = null;
        try {
            URL url = new URL(uri);
            URLConnection conn = url.openConnection();
            conn.connect();

            InputStream in = conn.getInputStream();

            //draw = Drawable.createFromStream(in, "set_ssxxc.png");
            //draw = Drawable.createFromResourceStream(MyApplication.getInstance().getResources(), null, in, "src", null);

            if (draw != null) {
                Bitmap bitmap = DrawToBmp(draw);
                saveBitmapTofile(bitmap, SDPATH + "temp.JPEG", uri);
                in.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return draw;
    }

    public static boolean saveBitmapTofile(Bitmap bmp, final String path, final String filename) {
        if (bmp == null) return false;

        CompressFormat format = CompressFormat.PNG;
        int quality = 100;
        OutputStream stream = null;

        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        int pos = filename.lastIndexOf('/');
        String name = filename.substring(pos + 1);

        try {
//                stream = new FileOutputStream(path + name);  
//                
//                bmp.compress(format, quality, stream);  
//                stream.flush();
//                stream.close();


            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(path + name, false));
            bmp.compress(format, quality, bos);
            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

    public static String ImagePath = "HeaderImage";

    public static String saveBitmapToFile(String name, Bitmap bitmap) {
        String path = "";
        String status = Environment.getExternalStorageState();
        FileOutputStream fos = null;
        if (status.equals(Environment.MEDIA_MOUNTED)
                || status.equals(Environment.MEDIA_SHARED)) {

            File div = new File(SDPATH,
                    ImagePath);
            if (!div.exists()) {
                div.mkdirs();
            }

            try {
                File file = new File(div, name + ".jpg");

                fos = new FileOutputStream(file);

                if (bitmap.compress(CompressFormat.JPEG, 100, fos)) {
                    fos.flush();
                    fos.close();
                    path = file.getAbsolutePath();
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return path;
    }

    public static Bitmap loadBitmapFromFile(final String strFileName, final String path) {
        Bitmap bitmap = null;
        if (strFileName == null) return bitmap;

        FileInputStream fis;
        try {
            int pos = strFileName.lastIndexOf('/');
            String name = strFileName.substring(pos + 1);

            fis = new FileInputStream(path + name);
            if (fis != null)
                bitmap = BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap loadBitmapFromFile(final String path) {
        Bitmap bitmap = null;
        if (path == null) return bitmap;

        FileInputStream fis;
        try {

            fis = new FileInputStream(path);
            if (fis != null)
                bitmap = BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap compressImage(Bitmap image) {
        Bitmap bitmap = null;

        if (image == null) return bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这�?100表示不压缩，把压缩后的数据存放到baos�?
        int options = 100;
        while (baos.toByteArray().length / 1024 > 300) {    //循环判断如果压缩后图片是否大�?100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos�?
            options -= 10;//每次都减�?10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream�?
        bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片

        return bitmap;
    }

    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream  
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组  
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, out);
        bm.recycle();
        byte[] result = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }


    public static int StringToInt(String strVal) {
        return Integer.valueOf(strVal).intValue();
    }

    public static String IntToString(int nVal) {
        return String.valueOf(nVal);
    }

    public static long StringToLong(String strVal) {
        long Aphone = Long.valueOf(strVal.trim());
        return Aphone;
        //return Long.valueOf(strVal).longValue();
        //return Long.parseLong(strVal);
    }

    public static String LongToString(long lVal) {
        return String.valueOf(lVal);
    }


    public static String GetImagePath(String url, int nIndex) {
        String name1 = null;
        String name2 = null;
        String name3 = null;
        String name4 = null;
        String name5 = null;
        String name6 = null;
        String name7 = null;
        String name8 = null;
        String name9 = null;

        if (url != null) {

            int p1[] = {0};
            p1[0] = url.indexOf('#');
            if (p1[0] > 0) {
                name1 = url.substring(0, p1[0]);
            }

            int p2[] = {0};
            int p3[] = {0};
            int p4[] = {0};
            int p5[] = {0};
            int p6[] = {0};
            int p7[] = {0};
            int p8[] = {0};
            int p9[] = {0};

            name2 = getname(p1, p2, url);
            name3 = getname(p2, p3, url);
            name4 = getname(p3, p4, url);
            name5 = getname(p4, p5, url);
            name6 = getname(p5, p6, url);
            name7 = getname(p6, p7, url);
            name8 = getname(p7, p8, url);
            name9 = getname(p8, p9, url);

            String name[] = {name1, name2, name3,
                    name4, name5, name6,
                    name7, name8, name9
            };

            return name[nIndex];
        }
        return null;
    }

    private static String getname(int p1[], int p2[], String url) {
        String name = null;
        if (p1[0] > 0) {
            p2[0] = url.indexOf('#', p1[0] < url.length() ? p1[0] + 1 : url.length());
            if (p2[0] > 0) {
                name = url.substring(p1[0] + 1, p2[0]);
                if (name.equals("")) name = null;
            }
        }
        return name;
    }

    public static String GetPolicy(String strPolicy, int nIndex) {
        String name1 = null;
        String name2 = null;
        String name3 = null;
        String name4 = null;
        String name5 = null;
        String name6 = null;
        String name7 = null;

        if (strPolicy != null) {

            int p1[] = {0};
            p1[0] = strPolicy.indexOf('#');
            if (p1[0] > 0) {
                name1 = strPolicy.substring(0, p1[0]);
            }

            int p2[] = {0};
            int p3[] = {0};
            int p4[] = {0};
            int p5[] = {0};
            int p6[] = {0};
            int p7[] = {0};

            name2 = getname(p1, p2, strPolicy);
            name3 = getname(p2, p3, strPolicy);
            name4 = getname(p3, p4, strPolicy);
            name5 = getname(p4, p5, strPolicy);
            name6 = getname(p5, p6, strPolicy);
            name7 = getname(p6, p7, strPolicy);

            String Policy[] = {name1, name2, name3,
                    name4, name5, name6,
                    name7
            };

            return Policy[nIndex];
        }
        return null;

    }

    public static Date getCurDate() {
        Calendar c = Calendar.getInstance();
        Date t = c.getTime();
        return t;
    }

    public static String getCurTime(String fromat) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(fromat);  //yyyy-MM-dd hh:mm:ss
        String time = df.format(date);

        return time;
    }

    public static String getCurTime() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //yyyy-MM-dd hh:mm:ss
        String time = df.format(date);

        return time;
    }

    public static String getMonthDay() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MMdd");  //yyyy-MM-dd hh:mm:ss
        String time = df.format(date);

        return time;
    }

    public static String getCurMonth() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM");  //yyyy-MM-dd hh:mm:ss
        String time = df.format(date);

        return time;
    }

    public static String getyyyyMM(String curData, int month) {
        String yyyyMM = "";

//		Date date=new Date();  
//		SimpleDateFormat df=new SimpleDateFormat("MMdd");  //yyyy-MM-dd hh:mm:ss
//		String time=df.format(date);  

        if (curData != null) {
            String data[] = curData.split("-");

            int curMonth = Integer.parseInt(data[1]);
            int curYear = Integer.parseInt(data[0]);

            if (curMonth - month >= 0) {
                yyyyMM = curYear + "-" + String.valueOf(curMonth + month + 1);
            } else {
                yyyyMM = curYear - 1 + "-" + String.valueOf(12 + curMonth + month + 1);
            }

        }

        return yyyyMM;
    }

    public static int getDays(String d1, String d2) {
        long t2 = getDateSercond(d2);
        long t1 = getDateSercond(d1);

        long t = t2 - t1;

        long sec = t / 1000;

        int h = (int) (sec / 3600);
        int m = (int) (sec % 3600 / 60);
        int s = (int) (sec % 60);
        int day = 0;

        day = h / 24;


        return day;
    }

    public static String getWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today = null;
        if (day == 2) {
            today = "MON";
        } else if (day == 3) {
            today = "TUE";
        } else if (day == 4) {
            today = "WED";
        } else if (day == 5) {
            today = "THU";
        } else if (day == 6) {
            today = "FRI";
        } else if (day == 7) {
            today = "SAT";
        } else if (day == 1) {
            today = "SUN";
        }
        return today;
    }

    //指定日期到今天的时间
    public static synchronized long getDateTimesToToday(String strTime) {
        long sec = getDateSercond(strTime);
        long s = getDateSercond(getCurTime("yyyy-MM-dd HH:mm:ss"));
        sec = sec - s;
        return sec / 1000;//1000 ms
    }

    //日期转秒
    public static synchronized long getDateTimesToSecond(String strTime) {
        long sec = getDateSercond(strTime);
        long s = getDateSercond("1901-01-01 00:00:00");
        sec = sec - s;
        return sec / 1000;//1000 ms
    }

    // 秒（整型）转日期
    public static synchronized Date getSecondTimeToDate(long time) {
        if (time == 0) return null;

        Date dt = null;

        long sec = 0;
        sec = time * 1000 + getDateSercond("1901-01-01 00:00:00");

        SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = Format.format(new Date(sec + 0));
        dt = getStringToDate(strDate);

        return dt;
    }

    //秒（字符�? ）转日期
    public static synchronized Date getSecondTimeToDate(final String time) {
        long sec = Utils.StringToLong(time);

        Date dt = null;

        long s = getDateSercond("1901-01-01 00:00:00");
        sec = sec * 1000 + s;

        SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = Format.format(new Date(sec + 0));
        //dt = Format.parse(Utils.LongToString(sec));
        dt = getStringToDate(strDate);
        return dt;
    }

    public static long getDateSercond(String strDate) {
        long times = 0;

        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            //java.util.Date date = myFormatter.parse(strDate);
            times = myFormatter.parse(strDate).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return times;
    }

    public static String getDateToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //yyyy-MM-dd hh:mm:ss
        String time = df.format(date);
        return time;
    }

    public static Date getStringToDate(String strDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //yyyy-MM-dd hh:mm:ss
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = df.parse(strDate, pos);
        return strtodate;
    }

    //返回格式化的日期
    public static String getDateToString(String format, long time) {
        String result = "";
        try {
            result = new SimpleDateFormat(format).format(new Date(time));
        } catch (Exception e) {

        }

        return result;
    }

    public static boolean isSameDate(Context context) {
        Time time = new Time("GMT+8");
        time.setToNow();
        String date = time.year + "year" + time.month + "month" + time.monthDay + "day";
        String oldDate = SharedPrefUtils.getStringData(context, "date", "");
        if (date.equals(oldDate)) {
            return true;
        } else {
            SharedPrefUtils.saveStringData(context, "date", date);
            return false;
        }
    }

    //写数据到SD中的文件
    public static void writeFileSdcardFileString(String fileName, String write_str) {
        try {

            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //写数据到SD中的文件
    public static int writeFileSdcardFileBytes(final String fileName, byte bytes[]) {

        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            //  byte [] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes.length;
    }

    public static String getTimer(long time) {
        long sec = time / 1000;

        int h = (int) (sec / 3600);
        int m = (int) (sec % 3600 / 60);
        int s = (int) (sec % 60);
        int day = 0;

        if (h > 24) {
            day = h / 24;
            h = h - day * 24;

            return day + "天前";
        } else if (h > 1) {
            return h + "小时�? ";
        } else if (m > 0) {
            return m + "分钟�? ";
        }
        return s + "秒前";
    }


    /**
     * 根据手机的分辨率�? dp 的单�? 转成�? px(像素)
     */
    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率�? px(像素) 的单�? 转成�? dp
     */
    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void clearCache(File directory) {

        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }


    public static void saveBitmap(Bitmap bm, String picName) {
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(String fileName) {
        Bitmap bitmap = null;
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }

            File file = new File(SDPATH, fileName + ".JPEG");
            String path = SDPATH + fileName + ".JPEG";
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
            }
        } catch (Exception e) {
        }
        return bitmap;
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            //dir = new File(SDPATH + dirName);
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(SDPATH + fileName + ".JPEG");
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    /**
     * @param mobilesNO 手机号码
     * @return 判断是否是手机格式（前三位）
     */
    public static boolean isMobileNO(String mobilesNO) {
        /**
         * 移动�?134�?135�?136�?137�?138�?139�?150�?151�?157(TD)�?158�?159�?187�?188
         * 联�?�：130�?131�?132�?152�?155�?156�?185�?186
         * 电信�?133�?153�?180�?189、（1349卫�?�）
         * 总结起来就是第一位必定为1，第二位必定�?3�?5�?8，其他位置的可以�?0-9
         */
        String telRegex = "[1][34578]\\d{9}";  // "[1]"代表�?1位为数字1�?"[358]"代表第二位可以为3�?5�?8中的�?个，"\\d{9}"代表后面是可以是0�?9的数
        if (TextUtils.isEmpty(mobilesNO)) {
            return false;
        } else {
            return mobilesNO.matches(telRegex);
        }
    }

    public static void sleepThread(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * WiFi是否加密
     *
     * @param result
     * @return
     */
    public static boolean isWifiOpen(ScanResult result) {
        if (result.capabilities.toLowerCase().indexOf("wep") != -1 || result.capabilities.toLowerCase().indexOf("wpa") != -1) {
            return false;
        }
        return true;
    }

    public static InetAddress getIntentAddress(Context mContext) throws IOException {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();

        }
    }

    public static boolean isNumeric(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static int getPassWordStatus(String password) {
        int PassWordStatus = 0;
        if (password.length() == 0) {
            PassWordStatus = 0;
        } else if (password.length() < 6) {
            PassWordStatus = 1;
        } else if (password.length() < 10) {
            PassWordStatus = 2;
        } else if (password.length() >= 10) {
            PassWordStatus = 3;
        }
        if (PassWordStatus > 1 && isRuo(password)) {
            PassWordStatus = 1;
        }
        return PassWordStatus;
    }

    private static boolean isRuo(String password) {
        int samesum = 0;
        int samesum2 = 0;
        int samesum3 = 0;
        char[] ss = password.toCharArray();
        for (int i = 1; i < ss.length; i++) {
            if (ss[i] - ss[i - 1] == 1) {
                samesum++;
            }
            if (ss[i] - ss[i - 1] == -1) {
                samesum2++;
            }
            if (ss[i] == ss[i - 1]) {
                samesum3++;
            }
        }
        if (samesum == ss.length - 1 || samesum2 == ss.length - 1
                || samesum3 == ss.length - 1) {
            return true;
        }
        return false;
    }

    /**
     * 设置录像文件名（~/videorecoder/callId/callId_yyyy_MM_dd_HH_mm_ss.MP4�?
     *
     * @param callId
     * @return
     */
    public static String getVideoRecodeName(String callId) {
//		if (!isSD()) {
//			return "noSD";
//		}
        long time = System.currentTimeMillis();
        //File sd = Environment.getExternalStorageDirectory();
        //String path = sd.getPath() + "/Baby/" + callId;

        String path = SDPATH + callId;

        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");// 制定日期的显示格�?
        String name = callId + "_" + sdf.format(new Date(time));
        String filename = file.getPath() + "/" + name + ".MP4";
        return filename;
    }


    public static String getVideoRecodeName2(String callId) {
        String status = Environment.getExternalStorageState();

        FileOutputStream fos = null;
        if (status.equals(Environment.MEDIA_MOUNTED)
                || status.equals(Environment.MEDIA_SHARED)) {

            //File div = new File(Environment.getExternalStorageDirectory(),"/Baby/" + callId);
            File div = new File(SDPATH, callId);

            if (!div.exists()) {
                div.mkdirs();
            }

            long time = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");// 制定日期的显示格�?
            String name = callId + "_" + sdf.format(new Date(time)) + ".MP4";
            File file = new File(div, name);
            return file.getAbsolutePath();
        }
        return null;
    }

    public static String getMakeRecordPath(String makeRecord) {
        String status = Environment.getExternalStorageState();
        FileOutputStream fos = null;
        if (status.equals(Environment.MEDIA_MOUNTED)
                || status.equals(Environment.MEDIA_SHARED)) {

            //File div = new File(Environment.getExternalStorageDirectory(),"/Baby/" + makeRecord);

            File div = new File(SDPATH, makeRecord);

            if (!div.exists()) {
                div.mkdirs();
            }

            long time = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");// 制定日期的显示格�?
            String name = makeRecord + "_" + sdf.format(new Date(time)) + ".png";
            File file = new File(div, name);
            return file.getAbsolutePath();
        }
        return null;
    }

    public static boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            flag = true;
        return flag;
    }

    /**
     * 创建录像根目�? ~/videorecode
     */
    public static File createRecodeFile() {
        if (!isSD()) {
            return null;
        }
        File sd = Environment.getExternalStorageDirectory();
        String path = sd.getPath() + "/videorecode";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        if (file == null) {
            Log.e("Utils", "create Recoding file failed");
        }
        return file;
    }

    /**
     * 判断手机SD卡是否插�?
     *
     * @return
     */
    public static boolean isSD() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)
                || status.equals(Environment.MEDIA_SHARED)) {
            return true;
        } else {
            return false;
        }
    }

    public static String hidePhoneNumber(String number) {
        if (!TextUtils.isEmpty(number) && number.length() > 10) {
            return number.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return null;
    }

}
