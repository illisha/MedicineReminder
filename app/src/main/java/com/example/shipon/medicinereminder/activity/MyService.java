package com.example.shipon.medicinereminder.activity;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyService extends Service {
    Intent intent;
    private int coef = 0;
    private static MyService inst;
    String response = "Thank you for using FemiTym. Please ensure you send the correct format (FT#ovulation period#day/month/year) e.g FT#04/05/2017. " +
            "Please NOTE that this system works for individuals with regular periods for at least the last 6 months " +
            "and their menstrual cycle period ranges between 26 and 32";
    public static MyService instance() {
        return inst;
    }
    @SuppressWarnings("deprecation")
    public void onStart() {
        super.onStart(intent, 0);
        inst = this;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String status = intent.getStringExtra("msg");
        String number = intent.getStringExtra("num");

        //String sender = intent.getStringExtra("address");

        if(status!=null && status.contains("FT#")) {

            //int duration = Toast.LENGTH_LONG;
            try {
                try {

                    //try{
                    String arrContent[] = status.split("#");
                    //Toast.makeText(getApplicationContext(), arrContent[1]+" "+arrContent[2], Toast.LENGTH_LONG).show();
                    if(arrContent[1].contains("26") || arrContent[1].contains("27") || arrContent[1].contains("28")
                            || arrContent[1].contains("29") || arrContent[1].contains("30") || arrContent[1].contains("31")
                            || arrContent[1].contains("32")){

                        if (arrContent[1].contains("26")) {
                            coef = 0;
                        } else if (arrContent[1].contains("27")) {
                            coef = 1;
                        } else if (arrContent[1].contains("28")) {
                            coef = 2;
                        } else if (arrContent[1].contains("29")) {
                            coef = 3;
                        } else if (arrContent[1].contains("30")) {
                            coef = 4;
                        } else if (arrContent[1].contains("31")) {
                            coef = 5;
                        } else if (arrContent[1].contains("32")) {
                            coef = 6;
                        }


                        String dateContent[] = arrContent[2].split("/");
                        String day = dateContent[0];
                        String month = dateContent[1];
                        String year = dateContent[2];
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d-M-yyyy");
                        //*FT*26*dd/mm/yyyy#
                        //String firstFilter = message;
                        //firstFilter = firstFilter.replaceFirst("/*FT/*", "").replaceAll("#", "");
                        //String strContent = status.replaceAll("message", "");
                        // String dateContent[] = strContent[1].split("/");


                        //Safe days do plus 6 days from first cycle and + 14 days from then, then add an extra 6 days.
                        int NORM_LAST = 6 + coef;
                        String MY_DATE = day + "/" + month + "/" + year;
                        String DATE_NEXT = daysBetween(MY_DATE, 6);
                        String DATE_NEXT_CYCLE = daysBetween(DATE_NEXT, 14);
                        String DATE_NEXT_CYCLE_SIX = daysBetween(DATE_NEXT_CYCLE, NORM_LAST);


                        response = "Thank you for using FemiTym. FemiTym is brought to you by Jeanette & Joy. Your Safe Days are between " + sdf.format(new SimpleDateFormat("d/M/yyyy").parse(MY_DATE))
                                + " to " + sdf.format(new SimpleDateFormat("d/M/yyyy").parse(DATE_NEXT)) + " and between " + sdf.format(new SimpleDateFormat("d/M/yyyy").parse(DATE_NEXT_CYCLE)) + " to "
                                + sdf.format(new SimpleDateFormat("d/M/yyyy").parse(DATE_NEXT_CYCLE_SIX)) + "." +
                                " Please NOTE that FemiTym works for women with regular periods and menstrual cycles ranging between 26 and 32 days. " +
                                "To get auto reminders, call us today on 0707041662!" +
                                "Powered by LevitNudi";

                        //Toast.makeText(getApplicationContext(), "Last coef is " + NORM_LAST, Toast.LENGTH_LONG).show();
                        sendSMS(number, response);

                    }else{
                        response = "Sorry, you've entered the wrong cycle, where cycle should be a number between 26 and 32." +
                                " To proceed, please use the following format FT#Cycle#DD/MM/YYYY";
                        sendSMS(number, response);
                    }

                } catch (ParseException e) {
                    response = "Please ensure your SMS contains the right key word format. To get your possible safe days," +
                            " send SMS to this number in the format FT#PERIOD#DAY/MONTH/YEAR e.g (FT#28#05/04/2017)." +
                            " The date should be the first date of your last menstrual cycle. " +
                            "Please NOTE that this system works for individuals with regular periods for at least the last 6 months" +
                            " and their menstrual cycle that ranges between 26 and 32. Thank you for choosing FemiTym. Powered by levitnudi designs";
                }
            }catch (Exception e){
                response = "Please ensure your SMS contains the right key word format. To get your possible safe days," +
                        " send SMS to this number in the format FT#PERIOD#DAY/MONTH/YEAR e.g (FT#28#05/04/2017)." +
                        " The date should be the first date of your last menstrual cycle. " +
                        "Please NOTE that this system works for individuals with regular periods for at least the last 6 months" +
                        " and their menstrual cycle period ranges between 26 and 32. Thank you for choosing FemiTym. Powered by levitnudi designs";
                sendSMS(number, response);
            }
        }
        return startId;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }



    //Date calculator
    public String daysBetween(String day, int num){
        String dt = day;  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
            c.add(Calendar.DATE, num);  // number of days to add
            dt = sdf.format(c.getTime());  // dt is now the new date
        }catch (Exception e){}
        return dt;
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            PendingIntent pi = PendingIntent.getActivity(this, 0,
                    new Intent(this, MyService.class), 0);
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(msg);
            sms.sendMultipartTextMessage(phoneNo, null, parts, null, null);
            // sms.sendTextMessage(phoneNo, null, msg, pi, null);
            /*for(int i=0; i<100; i++){
            Toast.makeText(getApplicationContext(), "Message Sent to "+phoneNo+" length is "+msg.length(),
                    Toast.LENGTH_LONG);}*/
        } catch (Exception ex) {
            /*Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG);
            ex.printStackTrace();*/
        }
    }

}