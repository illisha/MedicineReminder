package com.example.shipon.medicinereminder.activity;

import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.shipon.medicinereminder.Class.AlarmReceiver;
import com.example.shipon.medicinereminder.Class.Medicine;
import com.example.shipon.medicinereminder.Class.MedicinePerRow;
import com.example.shipon.medicinereminder.Class.MyService;
import com.example.shipon.medicinereminder.adopter.MyRecyclerViewAdapter;
import com.example.shipon.medicinereminder.R;
import com.example.shipon.medicinereminder.database.MedicineManagementDatabase;
import com.example.shipon.medicinereminder.fragment.MainFragment;
import com.example.shipon.medicinereminder.fragment.MainFragment.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;

import static com.example.shipon.medicinereminder.adopter.MyRecyclerViewAdapter.MNAME;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, MyRecyclerViewAdapter.ItemClickListener {
    RecyclerView medicineRV;
    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
    TextView time;
    ArrayList<String> name;
    String medicineName = null;
    String dateFormat;
    LinearLayout noMedicine;
    ArrayList<String> medicine = new ArrayList<>();
    int day, month, year, hour, minute;
    boolean doubleBackToExitPressedOnce = false;
    final static int RQS_1 = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        medicineRV = findViewById(R.id.MedicineRV);
        time = findViewById(R.id.Time);
        time.setVisibility(View.GONE);
        noMedicine = findViewById(R.id.NoMedicineLayout);
        noMedicine.setVisibility(View.GONE);
        setCalender();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Alarm();
    }


    public int getMonth(String date) {
        int str1 = date.indexOf("/");
        int str2 = date.lastIndexOf("/");
        String st1 = date.substring(0, str1);
        String st2 = date.substring(str1 + 1, str2);
        return Integer.parseInt(st2);
    }

    public int getDay(String date) {
        int str1 = date.indexOf("/");
        int str2 = date.lastIndexOf("/");
        String st1 = date.substring(0, str1);
        int ind = Integer.valueOf(st1);
        return ind;
    }

    public int getYear(String date) {
        int str1 = date.indexOf("/");
        int str2 = date.lastIndexOf("/");
        String st1 = date.substring(0, str1);
        String st2 = date.substring(str1 + 1, str2);
        String st3 = date.substring(str2 + 1, date.length());
        return Integer.parseInt(st3);
    }

    public int getHour1(String time) {
        int str1 = time.indexOf(":");
        String st1 = time.substring(0, str1);
        int h = Integer.parseInt(st1);
        return h;
    }

    public int getHour(String time) {
        int str1 = time.indexOf(":");
        String st1 = time.substring(0, str1);
        int h = Integer.parseInt(st1);
        // if (h >= 12) h = h % 12;
        return h;
    }

    public int getMin(String time) {
        int str1 = time.indexOf(":");
        String st2 = time.substring(str1 + 1, time.length());
        return Integer.parseInt(st2);
    }

    private void Alarm() {
        MedicineManagementDatabase obj = new MedicineManagementDatabase(this);
        ArrayList<MedicinePerRow> row = obj.retriveAllDateTime();
        for (int i = 0; i < row.size(); ++i) {
            Calendar cal = Calendar.getInstance();
            Calendar current = Calendar.getInstance();
            cal.set(getYear(row.get(i).getMedicineTakenDate())
                    , getMonth(row.get(i).getMedicineTakenDate())
                    , getDay(row.get(i).getMedicineTakenDate())
                    , getHour(row.get(i).getMedicineTime())
                    , getMin(row.get(i).getMedicineTime()), 00);
            if (cal.compareTo(current) <= 0) {

            } else {
                setAlarm(cal);
            }
        }
    /*    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);*/
    }

    private void callMainFragment(String date, String mName) {
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("name", mName);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.MainFragmentLayout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        AlarmReceiver MyService = new AlarmReceiver();
        Intent mServiceIntent = new Intent(getApplicationContext(), MyService.getClass());
        if (!isMyServiceRunning(MyService.getClass())) {
            startService(mServiceIntent);
        }
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    private void setMedicineOnRv(ArrayList<String> mName, String NAME) {
        medicine.clear();
        MedicineManagementDatabase obj = new MedicineManagementDatabase(this);
        ArrayList<Medicine> medicines = obj.retriveAllMedicineInfo();
        ArrayList<String> medicineName = new ArrayList<>();
        for (int i = 0; i < mName.size(); i++) {
            if (!medicineName.contains(mName.get(i).toString())) {
                medicineName.add(mName.get(i).toString());
            }
        }
        if (medicineName.size() == 0) {
            noMedicine.setVisibility(View.VISIBLE);
        } else {
            noMedicine.setVisibility(View.GONE);
        }

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        medicineRV.setLayoutManager(horizontalLayoutManagaer);
        for (int i = 0; i < medicines.size(); i++) {
            medicine.add(medicines.get(i).getMedicineName());
        }
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(MainActivity.this, medicineName, NAME);
        medicineRV.setAdapter(adapter);
        medicineRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onRestart() {
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);

            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        super.onRestart();
    }

    private void setCalender() {
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        final HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .datesNumberOnScreen(7)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {
                int dd = Integer.valueOf(DateFormat.format("dd", date).toString());
                int mm = Integer.valueOf(DateFormat.format("MM", date).toString()) - 1;
                int yy = Integer.valueOf(DateFormat.format("yyyy", date).toString());
                dateFormat = dd + "/" + mm + "/" + yy;

                MedicineManagementDatabase obj = new MedicineManagementDatabase(MainActivity.this);
                name = obj.retriveMedicineNameByDate(dateFormat);

                if (medicineName == null && name.size() != 0) {
                    medicineName = name.get(0).toString();
                } else if (!name.contains(medicineName) && name.size() != 0)
                    medicineName = name.get(0).toString();
                callMainFragment(dateFormat, medicineName);
                setMedicineOnRv(name, medicineName);
            }
        });
    }

    private String getCurrrentTime() {
        String time = null;
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.AM_PM) == Calendar.AM) {
            time = "" + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + " AM";

        } else {
            time = "" + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + " PM";
        }
        return time;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

        } else if (item.getItemId() == R.id.MedicineReviewID) {
            Intent ii = new Intent(this, MedicinePreviewActivity.class);
            startActivity(ii);
        }

        return super.onOptionsItemSelected(item);
    }



    private void setAlarm(Calendar targetCal) {

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), _id, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        intentArray.add(pendingIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onItemClick(View view, int position) {
        medicineName = name.get(position);
        callMainFragment(dateFormat, MNAME);
    }
}
