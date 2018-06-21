package com.xiangpu.bean;

/**
 * Created by andi on 2017/5/4.
 */

public class RecordTime extends Thread{

        private  IRecordTimerInterface RecordTimeInterface;
        public void setRecordInterface(IRecordTimerInterface RecordTimeInterface){
            this.RecordTimeInterface = RecordTimeInterface;
        }

        private int type=0;

        private int h;
        private int m;
        private int s;
        private boolean bRuning = false;
        public RecordTime(int type,IRecordTimerInterface recordTimeInterface){
            bRuning = true;
            this.type = type;
            setRecordInterface(recordTimeInterface);
        }
        public void stopRecordTime(){
            bRuning = false;
        }

        @Override
        public void run() {
            while(bRuning){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                s++;
                if(RecordTimeInterface!=null){
                    if(s>59){
                        m++;
                        s=0;
                        if(m>59){
                            h++;
                            m=0;
                        }
                    }
                    onRecordTime(h,m,s);
                }
            }
        }

    public void onRecordTime(int h,int m,int s) {
        String hh,mm,ss;
        if(h>9){
            hh = String.valueOf(h);
        }else{
            hh ="0"+String.valueOf(h);
        }
        if(m>9){
            mm = String.valueOf(m);
        }else{
            mm ="0"+String.valueOf(m);
        }
        if(s>9){
            ss = String.valueOf(s);
        }else{
            ss ="0"+String.valueOf(s);
        }

        String strTime = String.valueOf(hh+":"+ mm+":"+ss);

        if(RecordTimeInterface!= null) {
            RecordTimeInterface.onRecordTime(strTime);
        }
    }
}