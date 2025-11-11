package com.example.appointment.ui;

import com.example.appointment.R;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.uithealthcare.domain.appointment.AppointmentRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ChooseDateActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private AppointmentRequest req;
    private final SimpleDateFormat outFmt =
            new SimpleDateFormat("yyyy-MM-dd", new Locale("vi", "VN"));

    private final Set<CalendarDay> holidays = new HashSet<>();
    private String nameSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_date);

        req = (AppointmentRequest) getIntent().getSerializableExtra(AppointmentRequest.EXTRA);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();

        nameSpecialty = intent.getStringExtra("nameSpecialty");

        calendarView = findViewById(R.id.calendarView);

        // Bắt đầu tuần từ T2
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .commit();

        // Tiêu đề tháng: "Tháng 10"
        calendarView.setTitleFormatter(new TitleFormatter() {
            final SimpleDateFormat mFmt = new SimpleDateFormat("M", new Locale("vi", "VN"));
            @Override public CharSequence format(CalendarDay day) {
                Date d = day.getDate();
                return "Tháng " + mFmt.format(d);
            }
        });

        // Nhãn thứ: CN, T2..T7 (API 1.x truyền vào int: Calendar.SUNDAY..)
        calendarView.setWeekDayFormatter(new WeekDayFormatter() {
            @Override public CharSequence format(int dayOfWeek) {
                switch (dayOfWeek) {
                    case Calendar.SUNDAY:    return "CN";
                    case Calendar.MONDAY:    return "T2";
                    case Calendar.TUESDAY:   return "T3";
                    case Calendar.WEDNESDAY: return "T4";
                    case Calendar.THURSDAY:  return "T5";
                    case Calendar.FRIDAY:    return "T6";
                    case Calendar.SATURDAY:  return "T7";
                }
                return "";
            }
        });

        // Decorators
        calendarView.addDecorator(new SundayDisabledDecorator());
        calendarView.addDecorator(new PastDisabledDecorator());
        seedHolidays();
        calendarView.addDecorator(new HolidayDecorator(holidays,
                ContextCompat.getColor(this, R.color.holidayRed)));

        Drawable todayStroke = ContextCompat.getDrawable(this, R.drawable.bg_today_stroke);
        calendarView.addDecorator(new TodayDecorator(todayStroke));



        // Trả ngày về Activity gọi khi click ngày hợp lệ
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (!selected) return;

            if (SundayDisabledDecorator.isSunday(date)) return;
            if (PastDisabledDecorator.isPast(date)) return;
            if (holidays.contains(date)) return;

            SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = outFmt.format(date.getDate());

            Intent data = new Intent(this, ChooseDoctorActivity.class);

            data.putExtra("selectedDate", formattedDate);
            data.putExtra("nameSpecialty", nameSpecialty);
            data.putExtra(AppointmentRequest.EXTRA, req);
            setResult(RESULT_OK, data);
            startActivity(data);
            finish();
        });
    }

    private void seedHolidays() {
        // Ví dụ 02/09/2025 và 01/01/2025
        holidays.add(fromYMD(2025, 9, 2));
        holidays.add(fromYMD(2025, 1, 1));
    }

    /** Tạo CalendarDay an toàn (tháng truyền vào 1-12) */
    private CalendarDay fromYMD(int year, int month1to12, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month1to12 - 1); // Calendar: 0-11
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return CalendarDay.from(c);
    }

    /* ================= Decorators ================= */

    /** Khóa Chủ nhật */
    static class SundayDisabledDecorator implements DayViewDecorator {
        @Override public boolean shouldDecorate(CalendarDay day) { return isSunday(day); }
        @Override public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.addSpan(new android.text.style.ForegroundColorSpan(0xFF9E9E9E));
        }
        static boolean isSunday(CalendarDay day) {
            Calendar c = Calendar.getInstance();
            c.setTime(day.getDate());
            return c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
        }
    }

    /** Khóa ngày quá khứ */
    static class PastDisabledDecorator implements DayViewDecorator {
        private final CalendarDay today = CalendarDay.today();
        @Override public boolean shouldDecorate(CalendarDay day) { return isPast(day, today); }
        @Override public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.addSpan(new android.text.style.ForegroundColorSpan(0xFF9E9E9E));
        }
        static boolean isPast(CalendarDay day) { return isPast(day, CalendarDay.today()); }
        static boolean isPast(CalendarDay day, CalendarDay today) {
            // so sánh theo yyyy-MM-dd
            Date d1 = normalize(day.getDate());
            Date d2 = normalize(today.getDate());
            return d1.before(d2);
        }
        private static Date normalize(Date d){
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            return c.getTime();
        }
    }

    /** Hôm nay: viền xanh + chấm màu primary */
    static class TodayDecorator implements DayViewDecorator {
        private final CalendarDay today = CalendarDay.today();
        private final Drawable stroke;
        TodayDecorator(Drawable stroke){ this.stroke = stroke; }
        @Override public boolean shouldDecorate(CalendarDay day) { return today.equals(day); }
        @Override public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(6f, 0xFF1976D2));
            if (stroke != null) view.setBackgroundDrawable(stroke);
        }
    }

    /** Nghỉ lễ: chấm đỏ + disable */
    static class HolidayDecorator implements DayViewDecorator {
        private final Set<CalendarDay> days; private final int color;
        HolidayDecorator(Set<CalendarDay> days, int color){ this.days = days; this.color = color; }
        @Override public boolean shouldDecorate(CalendarDay day) { return days.contains(day); }
        @Override public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.addSpan(new DotSpan(6f, color));
            view.addSpan(new android.text.style.ForegroundColorSpan(color));
        }
    }
}
