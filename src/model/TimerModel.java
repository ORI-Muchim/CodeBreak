package model;

import constants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 타이머 상태 및 로직을 관리하는 모델 클래스
 */
public class TimerModel {
    public enum TimerState {
        STOPPED, RUNNING, PAUSED
    }
    
    public enum NotificationType {
        REST("휴식", "잠시 휴식을 취하세요!"),
        STRETCH("스트레칭", "몸을 움직여보세요!"),
        WATER("물 마시기", "물을 마셔주세요!"),
        EYE_REST("눈 휴식", "눈을 쉬게 해주세요!");
        
        private final String name;
        private final String message;
        
        NotificationType(String name, String message) {
            this.name = name;
            this.message = message;
        }
        
        public String getName() { return name; }
        public String getMessage() { return message; }
    }
    
    private TimerState state;
    private int workMinutes;
    private int breakMinutes;
    private int remainingSeconds;
    private int currentCycle;
    private boolean isPomodoroMode;
    private NotificationType currentNotificationType;
    private UserProfile currentProfile; // 현재 프로필 참조 추가
    
    private Timer timer;
    private List<TimerListener> listeners;
    
    // 포모도로 기본 설정은 TimerConstants에서 관리
    
    public TimerModel() {
        this.state = TimerState.STOPPED;
        this.workMinutes = TimerConstants.DEFAULT_WORK_MINUTES;
        this.breakMinutes = TimerConstants.DEFAULT_BREAK_MINUTES;
        this.remainingSeconds = TimerConstants.minutesToSeconds(workMinutes);
        this.currentCycle = 0;
        this.isPomodoroMode = true;
        this.currentNotificationType = NotificationType.REST;
        this.listeners = new ArrayList<>();
    }
    
    public interface TimerListener {
        void onTimerTick(int remainingSeconds);
        void onTimerComplete(NotificationType type);
        void onTimerStateChanged(TimerState state);
    }
    
    public void addTimerListener(TimerListener listener) {
        listeners.add(listener);
    }
    
    public void removeTimerListener(TimerListener listener) {
        listeners.remove(listener);
    }
    
    public void startTimer() {
        if (state == TimerState.RUNNING) return;
        
        setState(TimerState.RUNNING);
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, TimerConstants.TIMER_TICK_INTERVAL_MS, TimerConstants.TIMER_TICK_INTERVAL_MS);
    }
    
    public void pauseTimer() {
        if (state != TimerState.RUNNING) return;
        
        setState(TimerState.PAUSED);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    public void stopTimer() {
        setState(TimerState.STOPPED);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        resetTimer();
    }
    
    public void resetTimer() {
        remainingSeconds = TimerConstants.minutesToSeconds(workMinutes);
        currentCycle = 0;
        notifyTimerTick();
    }
    
    private void tick() {
        remainingSeconds--;
        notifyTimerTick();
        
        if (remainingSeconds <= 0) {
            timerComplete();
        }
    }
    
    private void timerComplete() {
        pauseTimer();
        currentCycle++;
        
        // 포모도로 모드에서는 휴식/작업 교대
        if (isPomodoroMode) {
            if (currentCycle % 2 == 1) {
                // 작업 완료 -> 휴식
                remainingSeconds = TimerConstants.minutesToSeconds(breakMinutes);
                currentNotificationType = getRandomNotificationType();
            } else {
                // 휴식 완룼 -> 작업
                remainingSeconds = TimerConstants.minutesToSeconds(workMinutes);
                currentNotificationType = NotificationType.REST;
            }
        } else {
            // 사용자 정의 모드에서는 항상 휴식 알림
            remainingSeconds = TimerConstants.minutesToSeconds(workMinutes);
            currentNotificationType = getRandomNotificationType();
        }
        
        notifyTimerComplete();
    }
    
    private NotificationType getRandomNotificationType() {
        // 프로필이 설정되어 있으면 활성화된 알림 유형들 중에서만 선택
        if (currentProfile != null) {
            List<NotificationType> enabledTypes = new ArrayList<>();
            for (NotificationType type : NotificationType.values()) {
                if (currentProfile.isNotificationEnabled(type)) {
                    enabledTypes.add(type);
                }
            }
            
            // 활성화된 알림이 있으면 그 중에서 선택
            if (!enabledTypes.isEmpty()) {
                return enabledTypes.get((int) (Math.random() * enabledTypes.size()));
            }
        }
        
        // 프로필이 없거나 활성화된 알림이 없으면 기본적으로 REST 반환
        return NotificationType.REST;
    }
    
    private void setState(TimerState newState) {
        this.state = newState;
        notifyStateChanged();
    }
    
    private void notifyTimerTick() {
        for (TimerListener listener : listeners) {
            listener.onTimerTick(remainingSeconds);
        }
    }
    
    private void notifyTimerComplete() {
        for (TimerListener listener : listeners) {
            listener.onTimerComplete(currentNotificationType);
        }
    }
    
    private void notifyStateChanged() {
        for (TimerListener listener : listeners) {
            listener.onTimerStateChanged(state);
        }
    }
    
    // Getters and Setters
    public TimerState getState() { return state; }
    public int getWorkMinutes() { return workMinutes; }
    public int getBreakMinutes() { return breakMinutes; }
    public int getRemainingSeconds() { return remainingSeconds; }
    public int getCurrentCycle() { return currentCycle; }
    public boolean isPomodoroMode() { return isPomodoroMode; }
    public NotificationType getCurrentNotificationType() { return currentNotificationType; }
    
    public void setWorkMinutes(int workMinutes) {
        this.workMinutes = workMinutes;
        if (state == TimerState.STOPPED) {
            remainingSeconds = TimerConstants.minutesToSeconds(workMinutes);
            notifyTimerTick();
        }
    }
    
    public void setBreakMinutes(int breakMinutes) {
        this.breakMinutes = breakMinutes;
    }
    
    public void setPomodoroMode(boolean pomodoroMode) {
        this.isPomodoroMode = pomodoroMode;
    }
    
    public void setCurrentProfile(UserProfile profile) {
        this.currentProfile = profile;
        System.out.println("TimerModel에 프로필 설정: " + (profile != null ? profile.getProfileName() : "null"));
    }
    
    public UserProfile getCurrentProfile() {
        return currentProfile;
    }

    
    public String getFormattedTime() {
        return TimerConstants.formatTime(remainingSeconds);
    }
    
    public String getCurrentPhase() {
        if (isPomodoroMode) {
            return (currentCycle % 2 == 0) ? "작업 시간" : "휴식 시간";
        } else {
            return "작업 시간";
        }
    }
}
