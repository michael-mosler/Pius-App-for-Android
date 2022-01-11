package com.rmkrings.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.model.ScrollMode;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.rmkrings.data.adapter.CalendarDateListAdapter;
import com.rmkrings.data.calendar.Calendar;
import com.rmkrings.data.calendar.DayItem;
import com.rmkrings.fragments.calendar.CalendarViewContainer;
import com.rmkrings.fragments.calendar.DayViewContainer;
import com.rmkrings.fragments.calendar.MonthViewContainer;
import com.rmkrings.helper.Config;
import com.rmkrings.helper.Cache;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.activities.R;
import com.rmkrings.pius_app_for_android;

import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Calendar view which shows a scrollable calendar. Each date may have up to three dots each
 * dot indicating an event scheduled for this date. Tapping on a date shows detailed list
 * of scheduled events.
 */
public class CalendarFragment extends Fragment implements HttpResponseCallback, CalendarViewContainer {

    private ProgressBar mProgressBar;
    private CalendarView calendarView;
    private DayViewContainer selectedDayViewContainer = null;
    private TextView noEventsTextView;

    private CalendarDateListAdapter mCalendarDateListAdapter;

    // Local State
    private final String digestFileName = Config.digestFilename("calendar");
    private final String cacheFileName = Config.cacheFilename("calendar");

    private final Cache cache = new Cache();
    private Calendar calendar;
    private final ArrayList<DayItem> dateList = new ArrayList<>();
    private final HashMap<LocalDate, DayViewContainer> dayViewContainerHashMap = new HashMap<>();
    private FragmentActivity fragmentActivity;

    private final Spanned[] dots = new Spanned[]{
            Html.fromHtml("⚫", Html.FROM_HTML_MODE_LEGACY),
            Html.fromHtml("⚫ ⚫", Html.FROM_HTML_MODE_LEGACY),
            Html.fromHtml("⚫ ⚫ ⚫", Html.FROM_HTML_MODE_LEGACY)
    };
    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final CalendarFragment instance = this;

        noEventsTextView = view.findViewById(R.id.noEventsText);
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer dayViewContainer, @NonNull CalendarDay calendarDay) {
                dayViewContainer.parent = instance;
                dayViewContainer.calendarDay = calendarDay;
                dayViewContainer.textView.setText(String.format(Locale.GERMAN, "%d", calendarDay.getDate().getDayOfMonth()));
                dayViewContainer.markerView.setTextColor(ContextCompat.getColor(pius_app_for_android.getAppContext(), R.color.colorPiusBlue));
                dayViewContainerHashMap.put(calendarDay.getDate(), dayViewContainer);

                // Number of dots to show. It is limited to three.
                if (calendar != null) {
                    final ArrayList<DayItem> dateList = calendar.filterBy(calendarDay);
                    int nDots = Math.min(dateList.size() - 1, 2);
                    if (nDots >= 0) {
                        dayViewContainer.markerView.setText(dots[nDots]);
                    }
                }

                // Selected date matches container date or if no date is selected container date
                // matches current date.
                if (selectedDayViewContainer != null
                        && dayViewContainer.calendarDay.getDate().isEqual(selectedDayViewContainer.calendarDay.getDate())) {
                    selectedDayViewContainer = dayViewContainer;
                    dayViewContainer.textView.setTextColor(Color.WHITE);
                    dayViewContainer.textView.setBackgroundResource(R.drawable.bg_pius_blue);
                }
                // Current date and another date is selected.
                else if (calendarDay.getDate().isEqual(ChronoLocalDate.from(LocalDate.from(ZonedDateTime.now())))) {
                    dayViewContainer.textView.setTextColor(Color.WHITE);
                    dayViewContainer.textView.setBackgroundResource(R.drawable.bg_red);
                } else {
                    // None of the above, plain view.
                    dayViewContainer.textView.setBackground(null);

                    if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                        if (calendarDay.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                            dayViewContainer.textView.setTextColor(Color.RED);
                        } else if (calendarDay.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
                            dayViewContainer.textView.setTextColor(
                                    ContextCompat.getColor(pius_app_for_android.getAppContext(), R.color.colorDisabled)
                            );
                        } else {
                            dayViewContainer.textView.setTextColor(Color.BLACK);
                        }
                    } else {
                        if (calendarDay.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                            dayViewContainer.textView.setTextColor(getResources().getColor(R.color.colorSundayOut, null));
                        } else {
                            dayViewContainer.textView.setTextColor(Color.LTGRAY);
                        }
                    }
                }
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer monthViewContainer, @NonNull CalendarMonth calendarMonth) {
                monthViewContainer
                        .textView
                        .setTextColor(
                                ContextCompat.getColor(pius_app_for_android.getAppContext(), R.color.colorPiusBlue)
                        );
                monthViewContainer
                        .textView
                        .setText(String.format(
                                Locale.GERMAN,
                                "%s %s",
                                calendarMonth.getYearMonth().getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN),
                                calendarMonth.getYear())
                        );
                monthViewContainer
                        .todayButton
                        .setOnClickListener(v -> today());
                monthViewContainer
                        .searchButton
                        .setOnClickListener(v -> {
                            FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frameLayout, CalendarSearchFragment.newInstance(calendar));
                            transaction.addToBackStack(null);
                            transaction.commit();
                        });
            }
        });

        calendarView.setupAsync(YearMonth.now(), YearMonth.now(), DayOfWeek.of(1));
        calendarView.setHasBoundaries(true);
        calendarView.setScrollMode(ScrollMode.PAGED);
        calendarView.setOrientation(CalendarView.HORIZONTAL);
        calendarView.scrollToMonth(YearMonth.now());

        mProgressBar = view.findViewById(R.id.progressBar);

        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(pius_app_for_android.getAppContext(), LinearLayoutManager.VERTICAL, false);
        mCalendarDateListAdapter = new CalendarDateListAdapter(dateList);
        RecyclerView eventListView = view.findViewById(R.id.eventList);
        eventListView.setLayoutManager(mVerticalLayoutManager);
        eventListView.setAdapter(mCalendarDateListAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity)context;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_calendar);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(3).setChecked(true);

        reload();
    }

    /**
     * Sets new date details for given calendar day. Call this when a date is selected
     * in calendar view.
     * @param calendarDay Selecte calenday day
     */
    private void setDateList(CalendarDay calendarDay) {
        mCalendarDateListAdapter.notifyItemRangeRemoved(0, dateList.size());

        dateList.clear();
        dateList.addAll(calendar.filterBy(calendarDay));

        mCalendarDateListAdapter.notifyItemRangeInserted(0, dateList.size());

        noEventsTextView.setVisibility(
                dateList.size() == 0 ? View.VISIBLE : View.INVISIBLE
        );
    }

    /**
     * Reloads calendar data.
     */
    private void reload() {
        String digest;

        if (cache.fileExists(cacheFileName) && cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName));
            digest = null;
        }

        CalendarLoader calendarLoader = new CalendarLoader();
        calendarLoader.load(this, digest);
    }

    /**
     * Reload data callback. Processes calendar data which has been received from backend.
     * @param responseData Backend response data structure
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        mProgressBar.setVisibility(View.INVISIBLE);

        // Check for errors.
        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Calendar. HTTP Status code %d.", responseData.getHttpStatusCode()));
            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_calendar))
                    .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> getParentFragmentManager().popBackStack())
                    .show();
            return;
        }

        try {
            // When data has changed update cache otherwise use data from cache.
            String data = responseData.getData();
            if (data != null) {
                cache.store(cacheFileName, data);
            } else {
                data = cache.read(cacheFileName);
            }

            JSONObject jsonData = new JSONObject(data);
            calendar = new Calendar(jsonData);

            if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && calendar.getDigest() != null) {
                cache.store(digestFileName, calendar.getDigest());
            }

            YearMonth lastMonth = calendar.getLastMonth();
            calendarView.setupAsync(YearMonth.now(), lastMonth, DayOfWeek.of(1));
            calendarView.notifyCalendarChanged();

            // today();

        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_calendar))
                    .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> getParentFragmentManager().popBackStack())
                    .show();
        }
    }

    /**
     * Go to today's date and show events for this date.
     */
    private void today() {
        calendarView.scrollToMonth(YearMonth.now());
        final DayViewContainer dayViewContainer = dayViewContainerHashMap.get(LocalDate.now());
        onSelectionChanged(dayViewContainer);
    }

    /**
     * Called when selected date in calendar is changed. Selects given container which
     * causes this calendar to be highlighted. A container highlighted before is de-selected.
     * @param dayViewContainer Selected day view container.
     */
    @Override
    public void onSelectionChanged(DayViewContainer dayViewContainer) {
        if (selectedDayViewContainer != null) {
            final LocalDate selectedDate = selectedDayViewContainer.calendarDay.getDate();
            selectedDayViewContainer = null;
            calendarView.notifyDateChanged(selectedDate);
        }

        if (dayViewContainer.calendarDay.getOwner() != DayOwner.THIS_MONTH) {
            calendarView.scrollToMonth(
                    YearMonth.of(
                            dayViewContainer.calendarDay.getDate().getYear(),
                            dayViewContainer.calendarDay.getDate().getMonth()
                    )
            );
        }
        selectedDayViewContainer = dayViewContainer;
        calendarView.notifyDateChanged(selectedDayViewContainer.calendarDay.getDate());

        setDateList(dayViewContainer.calendarDay);
    }

}
