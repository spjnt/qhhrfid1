package tramais.hnb.hhrfid.util;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pickerview.TimePickerView;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.interfaces.GetOneString;


public class TimeUtil {


    public static String getTime(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date curDate = new Date(System.currentTimeMillis());//获取当前日期
        String str = formatter.format(curDate);
        return str;
    }


    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(String past) {
        if (TextUtils.isEmpty(past)) {
            return " ";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.yyyy_mm_dd);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - Integer.valueOf(past));
            Date today = calendar.getTime();
            return formatter.format(today);
        }

    }

    public static String getFeatDay(int feat) {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.yyyy_mm_dd);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + feat);
        Date today = calendar.getTime();
        return formatter.format(today);
    }



    public static String getFeatureYearOrMonth(String specifiedDay, int farture, int type) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(Constants.yyyy_mm_dd).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        c.setTime(c.getTime());

        int MMOrYY = c.get(type);

        c.set(type, MMOrYY + farture);

        String dayBefore = new SimpleDateFormat(Constants.yyyy_mm_dd).format(c.getTime());
        return dayBefore;
    }

   /* *//*  m 个月后的第 n  天*//*
    public static String getSpecifiedMonthFeature(String specifiedDay, int farture) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(Constants.yyyy_mm_dd).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        c.setTime(c.getTime());
        int year = Calendar.YEAR;
        int month = c.get(Calendar.YEAR);

        c.set(Calendar.MONTH, month + farture);

        String dayBefore = new SimpleDateFormat(Constants.yyyy_mm_dd).format(c.getTime());
        return dayBefore;
    }*/

    public static String getBeginDayofMonth(String time) {
        if (!TextUtils.isEmpty(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.yyyy_mm_dd);
            try {
                Date date = sdf.parse(time);
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(date);
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                Date firstDate = startDate.getTime();

                return sdf.format(firstDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";

    }

    public static void initTimePicker(Context activity, GetOneString getRoom) {
        initTimePicker(activity, 1, 6, getRoom);
    }

    public static void initTimePicker(Context activity, int pastYear, int pastMonth, GetOneString getRoom) {
        Calendar rightNow = Calendar.getInstance();
        Calendar rightNow1 = Calendar.getInstance();
        rightNow1.add(Calendar.MONTH, -pastMonth);
        rightNow1.add(Calendar.YEAR, -pastYear);
        Integer year = rightNow.get(Calendar.YEAR);
        Integer year1 = rightNow1.get(Calendar.YEAR);
        //第一个月从0开始，所以得到月份＋1
        Integer month = rightNow.get(Calendar.MONTH);
        Integer month1 = rightNow1.get(Calendar.MONTH);
        Integer day = rightNow.get(Calendar.DAY_OF_MONTH);
        Integer day1 = rightNow1.get(Calendar.DAY_OF_MONTH);
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.set(year1, month1, day1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(year, month, day);


        //时间选择器
        TimePickerView.Builder builder = new TimePickerView.Builder(activity, ((date, v) -> {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.yyyy_mm_dd);
            getRoom.getString(sdf.format(date));
        }));
        //年月日时分秒 的显示与否，不设置则默认全部显示
        TimePickerView pvTime = builder.setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)

                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();

        pvTime.show();
    }

    public static boolean getTimeCompareSize(String startTime, String endTime) {
        // 年-月-日 时-分
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.yyyy_mm_dd);
        try {
            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间
            // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            if (date2.getTime() >= date1.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

}
