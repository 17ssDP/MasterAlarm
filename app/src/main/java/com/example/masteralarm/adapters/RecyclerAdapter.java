package com.example.masteralarm.adapters;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.utils.FormatUtils;
import com.example.masteralarm.views.DaySwitch;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import io.reactivex.functions.Consumer;
import me.jfenn.androidutils.DimenUtils;

public class RecyclerAdapter extends RecyclerView.Adapter {

    private MasterAlarm application;
    private RecyclerView recycler;
    private List<AlarmData> alarmData;
    private FragmentManager fragmentManager;
    private int colorForeground = Color.TRANSPARENT;
    private int textColorPrimary = Color.WHITE;
    private int colorAccent = Color.WHITE;
    private MyThread updateAlarm;

    private int expandedPosition = -1;

    public RecyclerAdapter(MasterAlarm alarm, RecyclerView recycler, FragmentManager manager){
        application = alarm;
        this.recycler = recycler;
        alarmData = application.getAlarmData();
        fragmentManager = manager;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private View nameContainer;
        private EditText name;
        private View nameUnderline;
        private Switch enable;
        private TextView time;
        private View extra;
        private AppCompatCheckBox repeat;
        private LinearLayout days;
        private View ringtone;
        private ImageView ringtoneImage;
        private TextView ringtoneText;
        private View vibrate;
        private ImageView vibrateImage;
        private ImageView expandImage;
        private TextView delete;
        private View indicators;
        private ImageView repeatIndicator;
        private ImageView soundIndicator;
        private ImageView vibrateIndicator;

        public ViewHolder(@NonNull View v) {
            super(v);
            nameContainer = v.findViewById(R.id.nameContainer);
            name = v.findViewById(R.id.name);
            nameUnderline = v.findViewById(R.id.underline);
            enable = v.findViewById(R.id.enable);
            time = v.findViewById(R.id.time);
            extra = v.findViewById(R.id.option);
            repeat = v.findViewById(R.id.repeat);
            days = v.findViewById(R.id.days);
            ringtone = v.findViewById(R.id.ringtone);
            ringtoneImage = v.findViewById(R.id.ringtoneImage);
            ringtoneText = v.findViewById(R.id.ringtoneText);
            vibrate = v.findViewById(R.id.vibrate);
            vibrateImage = v.findViewById(R.id.vibrateImage);
            expandImage = v.findViewById(R.id.expandImage);
            delete = v.findViewById(R.id.delete);
            indicators = v.findViewById(R.id.indicators);
            repeatIndicator = v.findViewById(R.id.repeatIndicator);
            soundIndicator = v.findViewById(R.id.soundIndicator);
            vibrateIndicator = v.findViewById(R.id.vibrateIndicator);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder alarmHolder = (ViewHolder) holder;
        final boolean isExpanded = position == expandedPosition;
        AlarmData alarm = getAlarm(position);

        alarmHolder.name.setFocusableInTouchMode(isExpanded);
        alarmHolder.name.setCursorVisible(false);
        alarmHolder.name.clearFocus();
        alarmHolder.nameUnderline.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        alarmHolder.name.setText(alarm.getLabel());
        alarmHolder.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getAlarm(alarmHolder.getAdapterPosition()).setLabel(alarmHolder.name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        alarmHolder.name.setOnClickListener(isExpanded ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmHolder.itemView.callOnClick();
            }
        });

        alarmHolder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                alarmHolder.name.setCursorVisible(hasFocus && alarmHolder.getAdapterPosition() == expandedPosition);
            }
        });

        alarmHolder.enable.setOnCheckedChangeListener(null);
        alarmHolder.enable.setChecked(alarm.isEnable());
        alarmHolder.enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getAlarm(alarmHolder.getAdapterPosition()).setEnable(b);
            }
        });

        alarmHolder.time.setText(FormatUtils.formatShort(application, alarm.getCalendarTime().getTime()));
        alarmHolder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                //用户可以点击时间部件改变设定的时间
                Calendar calendar = application.createTimePicker(view.getContext());
                alarm.setCalendarTime(calendar);
                alarmHolder.time.setText(FormatUtils.formatShort(application,calendar.getTime()));
            }
        });

        alarmHolder.indicators.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        if (isExpanded) {
            alarmHolder.repeat.setOnCheckedChangeListener(null);
            alarmHolder.repeat.setChecked(alarm.isRepeat());
            alarmHolder.repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                    for (int i = 0; i < 7; i++) {
                        alarm.setRepeat(new boolean[]{b,b,b,b,b,b,b});
                    }

                    Transition transition = new AutoTransition();
                    transition.setDuration(150);
                    TransitionManager.beginDelayedTransition(recycler, transition);

                    notifyDataSetChanged();
                }
            });

            alarmHolder.days.setVisibility(alarm.isRepeat() ? View.VISIBLE : View.GONE);

            DaySwitch.OnCheckedChangeListener listener = new DaySwitch.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(DaySwitch daySwitch, boolean b) {
                    AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                    alarm.getRepeat()[alarmHolder.days.indexOfChild(daySwitch)] = b;

                    if (!alarm.isRepeat())
                        notifyItemChanged(alarmHolder.getAdapterPosition());
                }
            };

            for (int i = 0; i < 7; i++) {
                DaySwitch daySwitch = (DaySwitch) alarmHolder.days.getChildAt(i);
                daySwitch.setChecked(alarm.getRepeat()[i]);
                daySwitch.setOnCheckedChangeListener(listener);

                switch (i) {
                    case 0:
                    case 6:
                        daySwitch.setText("S");
                        break;
                    case 1:
                        daySwitch.setText("M");
                        break;
                    case 2:
                    case 4:
                        daySwitch.setText("T");
                        break;
                    case 3:
                        daySwitch.setText("W");
                        break;
                    case 5:
                        daySwitch.setText("F");

                }
            }

            alarmHolder.ringtoneImage.setImageResource(alarm.isHasSound() ? R.drawable.ic_ringtone : R.drawable.ic_ringtone_disabled);
            alarmHolder.ringtoneImage.setAlpha(alarm.isHasSound() ? 1 : 0.333f);
            alarmHolder.ringtoneText.setText(application.getString(R.string.title_sound_none));
            alarmHolder.ringtone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                    alarm.setHasSound(!alarm.isHasSound());

                    alarmHolder.ringtoneImage.setImageResource(alarm.isHasSound() ? R.drawable.ic_ringtone : R.drawable.ic_ringtone_disabled);

                    alarmHolder.ringtoneImage.animate().alpha(alarm.isHasSound() ? 1 : 0.333f).setDuration(250).start();
                }
            });

            AnimatedVectorDrawableCompat vibrateDrawable = AnimatedVectorDrawableCompat.create(application, alarm.isVibrate() ? R.drawable.ic_vibrate_to_none : R.drawable.ic_none_to_vibrate);
            alarmHolder.vibrateImage.setImageDrawable(vibrateDrawable);
            alarmHolder.vibrateImage.setAlpha(alarm.isVibrate() ? 1 : 0.333f);
            alarmHolder.vibrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(updateAlarm == null) {
                        updateAlarm = new MyThread();
                        try {
                            updateAlarm.sleep(5);
                            updateAlarm.run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                    alarm.setVibrate(!alarm.isVibrate());

                    AnimatedVectorDrawableCompat vibrateDrawable = AnimatedVectorDrawableCompat.create(application, alarm.isVibrate() ? R.drawable.ic_none_to_vibrate : R.drawable.ic_vibrate_to_none);
                    if (vibrateDrawable != null) {
                        alarmHolder.vibrateImage.setImageDrawable(vibrateDrawable);
                        vibrateDrawable.start();
                    } else
                        alarmHolder.vibrateImage.setImageResource(alarm.isVibrate() ? R.drawable.ic_vibrate : R.drawable.ic_vibrate_none);

                    alarmHolder.vibrateImage.animate().alpha(alarm.isVibrate() ? 1 : 0.333f).setDuration(250).start();
                    if (alarm.isVibrate())
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                }
            });
        } else {
            alarmHolder.repeatIndicator.setAlpha(alarm.isRepeat() ? 1 : 0.333f);
            alarmHolder.soundIndicator.setAlpha(alarm.isHasSound() ? 1 : 0.333f);
            alarmHolder.vibrateIndicator.setAlpha(alarm.isVibrate() ? 1 : 0.333f);
        }

        alarmHolder.expandImage.animate().rotationX(isExpanded ? 180 : 0).start();
        alarmHolder.delete.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        alarmHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete button listener

                AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                application.deleteAlarm(alarm);
                //delete from database
                LitePal.delete(AlarmData.class, alarm.getId());
            }
        });

        alarmHolder.repeat.setTextColor(textColorPrimary);
        alarmHolder.delete.setTextColor(textColorPrimary);
        alarmHolder.ringtoneImage.setColorFilter(textColorPrimary);
        alarmHolder.vibrateImage.setColorFilter(textColorPrimary);
        alarmHolder.expandImage.setColorFilter(textColorPrimary);
        alarmHolder.repeatIndicator.setColorFilter(textColorPrimary);
        alarmHolder.soundIndicator.setColorFilter(textColorPrimary);
        alarmHolder.vibrateIndicator.setColorFilter(textColorPrimary);
        alarmHolder.nameUnderline.setBackgroundColor(textColorPrimary);

        int visibility = isExpanded ? View.VISIBLE : View.GONE;

        //颜色组件设置
        if (visibility != alarmHolder.extra.getVisibility()) {
            alarmHolder.extra.setVisibility(visibility);
            Aesthetic.Companion.get()
                    .colorPrimary()
                    .take(1)
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), isExpanded ? integer : colorForeground, isExpanded ? colorForeground : integer);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    alarmHolder.itemView.setBackgroundColor((int) animation.getAnimatedValue());
                                }
                            });
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    alarmHolder.itemView.setBackgroundColor(isExpanded ? colorForeground : Color.TRANSPARENT);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                            animator.start();
                        }
                    }).isDisposed();

            ValueAnimator animator = ValueAnimator.ofFloat(isExpanded ? 0 : DimenUtils.dpToPx(2), isExpanded ? DimenUtils.dpToPx(2) : 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewCompat.setElevation(alarmHolder.itemView, (float) animation.getAnimatedValue());
                }
            });
            animator.start();
        } else {
            alarmHolder.itemView.setBackgroundColor(isExpanded ? colorForeground : Color.TRANSPARENT);
            ViewCompat.setElevation(alarmHolder.itemView, isExpanded ? DimenUtils.dpToPx(2) : 0);
        }

        alarmHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                Transition transition = new AutoTransition();
                transition.setDuration(250);
                TransitionManager.beginDelayedTransition(recycler, transition);

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmData.size();
    }

    private AlarmData getAlarm(int position) {
        return alarmData.get(position);
    }

    public void setColorAccent(int colorAccent) {
        this.colorAccent = colorAccent;
        notifyDataSetChanged();
    }

    public void setColorForeground(int colorForeground) {
        this.colorForeground = colorForeground;
        if (expandedPosition > 0)
            notifyItemChanged(expandedPosition);
    }

    public void setTextColorPrimary(int colorTextPrimary) {
        this.textColorPrimary = colorTextPrimary;
        notifyDataSetChanged();
    }

    //更新闹钟信息线程
    class MyThread extends Thread{
        @Override
        public void run() {

        }
        public void update(ViewHolder alarmHolder) {
            AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
            alarm.updateAll("id = ?", "" + alarm.getId());
        }
    }
}
