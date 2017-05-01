package com.example.mihika.expocr;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.animation.Animator;
import android.animation.ValueAnimator;
//import android.app.Activity;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.*;
import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.LogRecord;

/**
 * This activity implements the pie chart for the Summary page.
 */
public class SummaryActivity extends AppCompatActivity {

    public static final int SELECTED_SEGMENT_OFFSET = 0;
    private static final int PLOT_FINISH = 1;

    public PieChart pie;
    private Handler handler;


    private final String TAG = "SummaryActivity";
    public ArrayList<SegmentFormatter> segmentFormatters = new ArrayList<>();
    public Map<String, Double> percents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case PLOT_FINISH:
                        int count = 0;
                        for (String category : percents.keySet()) {
                            Double percent = percents.get(category);
                            Segment segment = new Segment(category , percent);

                            pie.addSegment(segment, segmentFormatters.get(count % segmentFormatters.size()));
                            count++;
                        }
                        pie.getBorderPaint().setColor(Color.WHITE);
                        pie.getBackgroundPaint().setColor(Color.WHITE);//Color Of Background
                        pie.getTitle().getLabelPaint().setColor(Color.RED);//Color of title
                        pie.getTitle().getLabelPaint().setShadowLayer(2.5f, 2.5f, 2.5f, Color.RED);//Shade of title
                        setupIntroAnimation();
                        break;
                }
            }
        };

        // initialize our XYPlot reference:
        pie = (PieChart) findViewById(R.id.mySimplePieChart);
//        pie.getRenderer(PieRenderer.class).setDonutSize(20/100f,
//                PieRenderer.DonutMode.PERCENT);


        final float padding = PixelUtils.dpToPix(30);
        pie.getPie().setPadding(padding, padding, padding, padding);
        //setupIntroAnimation();

        // detect segment clicks:
        pie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                PointF click = new PointF(motionEvent.getX(), motionEvent.getY());
                if(pie.getPie().containsPoint(click)) {
                    Segment segment = pie.getRenderer(PieRenderer.class).getContainingSegment(click);
                    if(segment != null) {
                        final boolean isSelected = getFormatter(segment).getOffset() != 0;
                        deselectAll();
                        setSelected(segment, !isSelected);
                        pie.redraw();
                    }
                }
                return false;
            }

            private SegmentFormatter getFormatter(Segment segment) {
                return pie.getFormatter(segment, PieRenderer.class);
            }

            private void deselectAll() {
                List<Segment> segments = pie.getRegistry().getSeriesList();
                for(Segment segment : segments) {
                    setSelected(segment, false);
                }
            }

            private void setSelected(Segment segment, boolean isSelected) {
                SegmentFormatter f = getFormatter(segment);
                if(isSelected) {
                    f.setOffset(SELECTED_SEGMENT_OFFSET);
                } else {
                    f.setOffset(0);
                }
            }
        });

        updateDonutText();
        segmentFormatters.add(new SegmentFormatter(this, R.xml.pie_segment_formatter_blue));
        segmentFormatters.add(new SegmentFormatter(this, R.xml.pie_segment_formatter_green));
        segmentFormatters.add(new SegmentFormatter(this, R.xml.pie_segment_formatter_red));
        segmentFormatters.add(new SegmentFormatter(this, R.xml.pie_segment_formatter_yellow));
        segmentFormatters.add(new SegmentFormatter(this, R.xml.pie_segment_formatter_purple));
        plot();
    }

    /**
     * get user expenses from server and plot piechart with returned transactions
     */
    private void plot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                percents = parse(expense_retrieve_all());
                Message msg = new Message();
                msg.what = PLOT_FINISH;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected void updateDonutText() {
    }

    protected void setupIntroAnimation() {

        final PieRenderer renderer = pie.getRenderer(PieRenderer.class);
        // start with a zero degrees pie:

        renderer.setExtentDegs(0);
        // animate a scale value from a starting val of 0 to a final value of 1:
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

        // use an animation pattern that begins and ends slowly:
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scale = valueAnimator.getAnimatedFraction();
                renderer.setExtentDegs(360 * scale);
                pie.redraw();
            }
        });

        animator.setDuration(1500);
        animator.start();
    }

    protected String expense_retrieve_all(){
        String serverUrl = "http://" + ServerUtil.getServerAddress() + "transaction/get_by_receiver";
        String requestBody = "receiver_id=" + MainActivity.getU_id() + "&category=no_payment";

        String text = ServerUtil.sendData(serverUrl, requestBody, "UTF-8");
        Log.d(TAG, text);
        return text;
    }

    protected Map<String, Double> parse(String s) {
        Log.d(TAG, s);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, Double> amounts = new HashMap<>();
        double totalAmount = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String category = jsonObject.getString("Category");
                Double amount = jsonObject.getDouble("amount");
                if (amount > 0) {
                    if (!amounts.containsKey(category)) {
                        amounts.put(category, amount);
                    } else {
                        amounts.put(category, amount + amounts.get(category));
                    }
                    totalAmount += amount;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Map<String, Double> percents = new HashMap<>();
        for (String key : amounts.keySet()) {
            percents.put(key, (BigDecimal.valueOf(amounts.get(key) * 100 / totalAmount).setScale(2, RoundingMode.HALF_UP).doubleValue()));
        }
        if (percents.size() == 0) {
            Map<String, Double> ret = new HashMap<>();
            ret.put("Nothing", 100.0);
            return ret;
        }
        return percents;
    }
}
