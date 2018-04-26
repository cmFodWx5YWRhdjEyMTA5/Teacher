package com.easyschools.teacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class Calender extends Fragment {

    private OnFragmentInteractionListener mListener;
    private CompactCalendarView school_calender;
    private TextView current_month;
    private TextView month_before;
    private TextView month_after;
    private String currentMonthStr = null;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private List<Event> events;
    private SimpleDateFormat sdf;
    private RecyclerView.Adapter eventAdapter;
    private List<EventData> eventsDatas;
    private PopupWindow mPopupWindow;
    private RelativeLayout calender_layout;
    private String token ;
    private Context context ;
    private CircleImageView school_logo;
    public Calender() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calender, container, false);
        apis = new Apis();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        token = settings.getString("API_TOKEN", "");
        calender_layout = v.findViewById(R.id.calender_layout);
        events = new ArrayList<>();
        if (getActivity() != null)
        {
            context = getActivity().getApplicationContext();
        }
        school_logo = v.findViewById(R.id.c);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        month_before = v.findViewById(R.id.before_month);
        month_after = v.findViewById(R.id.after_month);
        current_month = v.findViewById(R.id.month);
        school_calender = v.findViewById(R.id.school_calendar);
        school_calender.setUseThreeLetterAbbreviation(true);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        currentMonthStr = month_date.format(cal.getTime());
        Log.d("Current", currentMonthStr);
        current_month.setText(currentMonthStr);
        switch (currentMonthStr) {
            case "Dec":
                month_after.setText("JAN");
                month_before.setText("NOV");
                current_month.setText("December");
                break;

            case "Nov":
                month_after.setText("DEC");
                month_before.setText("OCT");
                current_month.setText("November");
                break;
            case "Oct":
                month_after.setText("NOV");
                month_before.setText("SEP");
                current_month.setText("October");
                break;
            case "Sep":
                month_after.setText("OCT");
                month_before.setText("AUG");
                current_month.setText("September");
                break;
            case "Aug":
                month_after.setText("SEP");
                month_before.setText("JUL");
                current_month.setText("August");
                break;
            case "Jul":
                month_after.setText("AUG");
                month_before.setText("JUN");
                current_month.setText("July");
                break;
            case "Jun":
                month_after.setText("JUL");
                month_before.setText("MAY");
                current_month.setText("June");
                break;
            case "May":
                month_after.setText("JUN");
                month_before.setText("APR");
                current_month.setText("May");
                break;
            case "Apr":
                month_after.setText("MAY");
                month_before.setText("MAR");
                current_month.setText("April");
                break;
            case "Mar":
                month_after.setText("APR");
                month_before.setText("FEB");
                current_month.setText("March");
                break;
            case "Feb":
                month_after.setText("MAR");
                month_before.setText("JAN");
                current_month.setText("February");
                break;
            case "Jan":
                month_after.setText("FEB");
                month_before.setText("DEC");
                current_month.setText("January");
                break;
        }
        eventsDatas = new ArrayList<>();
        school_calender.animate();
        school_calender.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                int eventSize = school_calender.getEvents(dateClicked).size();
                if (!school_calender.getEvents(dateClicked).isEmpty()) {
                    for (int i = 0; i < eventSize; i++)
                    {
                        Log.d("EVENTS", String.valueOf(school_calender.
                                getEvents(dateClicked).get(i).getData()));

                        String data = String.valueOf(school_calender.
                                getEvents(dateClicked).get(i).getData());
                        Log.d("DKHDK", data.replaceAll("[|?*<\">+\\[\\]/']", " "));
                        String []separated = data.replaceAll("[|?*<\">+\\[\\]/']", " ").split(",");
                        for (String item : separated) {
                            Log.d("kjfij", item);
                        }

                        EventData eventData = new EventData();
                        eventData.setName(separated[1]);
                        eventData.setDay_no((String) DateFormat.format("dd", dateClicked));
                        eventData.setDay_txt((String) DateFormat.format("EEEE", dateClicked));
                        eventData.setDesc(separated[2]);
                        eventData.setTime(separated[0]);
                        eventsDatas.add(eventData);
                    }
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    View customView = inflater.inflate(R.layout.event_pop_up, null);
                    mPopupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    final RecyclerView events_list = customView.findViewById(R.id.events_details);
                    calender_layout.setAlpha(0.2f);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setAnimationStyle(R.anim.slide_up);
                    mPopupWindow.showAtLocation(calender_layout,
                            Gravity.CENTER, 0, 0);
                    mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            calender_layout.setAlpha(1f);
                            eventsDatas.clear();
                        }
                    });
                    eventAdapter = new EventAdapter(eventsDatas, customView.getContext());
                    events_list.setAdapter(eventAdapter);
                    events_list.setLayoutManager(new LinearLayoutManager(context));
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currentMonthStr = (String) DateFormat.format("MMM", firstDayOfNewMonth);
                Log.d("MONTH", currentMonthStr);
                current_month.setText(currentMonthStr);
                switch (currentMonthStr) {
                    case "Dec":
                        month_after.setText("JAN");
                        month_before.setText("NOV");
                        current_month.setText("December");
                        break;

                    case "Nov":
                        month_after.setText("DEC");
                        month_before.setText("OCT");
                        current_month.setText("November");
                        break;
                    case "Oct":
                        month_after.setText("NOV");
                        month_before.setText("SEP");
                        current_month.setText("October");
                        break;
                    case "Sep":
                        month_after.setText("OCT");
                        month_before.setText("AUG");
                        current_month.setText("September");
                        break;
                    case "Aug":
                        month_after.setText("SEP");
                        month_before.setText("JUL");
                        current_month.setText("August");
                        break;
                    case "Jul":
                        month_after.setText("AUG");
                        month_before.setText("JUN");
                        current_month.setText("July");
                        break;
                    case "Jun":
                        month_after.setText("JUL");
                        month_before.setText("MAY");
                        current_month.setText("June");
                        break;
                    case "May":
                        month_after.setText("JUN");
                        month_before.setText("APR");
                        current_month.setText("May");
                        break;
                    case "Apr":
                        month_after.setText("MAY");
                        month_before.setText("MAR");
                        current_month.setText("April");
                        break;
                    case "Mar":
                        month_after.setText("APR");
                        month_before.setText("FEB");
                        current_month.setText("March");
                        break;
                    case "Feb":
                        month_after.setText("MAR");
                        month_before.setText("JAN");
                        current_month.setText("February");
                        break;
                    case "Jan":
                        month_after.setText("FEB");
                        month_before.setText("DEC");
                        current_month.setText("January");
                        break;
                }
            }
        });
        calender();

        return v ;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void calender() {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,apis.getUrl()+lang+"/calendar/getAllEvents?" +
                        "api_token="+token,
                        null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CAL_RES", response.toString());
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject jsonObject1 = dataArray.getJSONObject(i);
                                Log.d("ahja", jsonObject1.toString());
                                String start = jsonObject1.getString("start_at");
                                String end = jsonObject1.getString("end_at");
                                String time = jsonObject1.getString("start_time");
                                String name = jsonObject1.getString("name");
                                String desc = jsonObject1.getString("description");

                                List<String> all = new ArrayList<>();
                                all.add(time);
                                all.add(name);
                                all.add(desc);
                                List<Date> test = getDates(start, end);
                                for (int m = 0; m < test.size(); m++) {
                                    Random rnd = new Random();
                                    int color = Color.argb(255,
                                            rnd.nextInt(256),
                                            rnd.nextInt(256),
                                            rnd.nextInt(256));
                                    long milli = test.get(m).getTime();
                                    events.add(new Event(color, milli, all));
                                }

                            }
                            school_calender.addEvents(events);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CAL_ERROR", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static List<Date> getDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<>();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

}
