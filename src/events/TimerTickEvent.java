package events;

import constants.TimerConstants;

/**
 * 타이머 틱 이벤트
 * 타이머가 1초마다 틱할 때 발생합니다.
 */
public class TimerTickEvent extends Event {
    private final int remainingSeconds;
    private final int totalSeconds;
    private final int currentCycle;
    private final String currentPhase;
    private final boolean isPomodoroMode;
    
    public TimerTickEvent(int remainingSeconds, int totalSeconds, int currentCycle, 
                         String currentPhase, boolean isPomodoroMode) {
        super("TIMER_TICK");
        this.remainingSeconds = remainingSeconds;
        this.totalSeconds = totalSeconds;
        this.currentCycle = currentCycle;
        this.currentPhase = currentPhase;
        this.isPomodoroMode = isPomodoroMode;
    }
    
    /**
     * 남은 시간 (초)
     * @return 남은 시간
     */
    public int getRemainingSeconds() {
        return remainingSeconds;
    }
    
    /**
     * 총 시간 (초)
     * @return 총 시간
     */
    public int getTotalSeconds() {
        return totalSeconds;
    }
    
    /**
     * 현재 사이클
     * @return 현재 사이클 번호
     */
    public int getCurrentCycle() {
        return currentCycle;
    }
    
    /**
     * 현재 페이즈
     * @return 현재 페이즈 문자열
     */
    public String getCurrentPhase() {
        return currentPhase;
    }
    
    /**
     * 포모도로 모드 여부
     * @return 포모도로 모드 여부
     */
    public boolean isPomodoroMode() {
        return isPomodoroMode;
    }
    
    /**
     * 진행률 (0.0 ~ 1.0)
     * @return 진행률
     */
    public double getProgress() {
        if (totalSeconds <= 0) {
            return 1.0;
        }
        return Math.max(0.0, Math.min(1.0, (double) (totalSeconds - remainingSeconds) / totalSeconds));
    }
    
    /**
     * 진행률 (백분율)
     * @return 진행률 백분율 (0 ~ 100)
     */
    public int getProgressPercentage() {
        return (int) Math.round(getProgress() * 100);
    }
    
    /**
     * 포맷팅된 시간 문자열
     * @return MM:SS 형식의 시간 문자열
     */
    public String getFormattedTime() {
        return TimerConstants.formatTime(remainingSeconds);
    }
    
    /**
     * 남은 분
     * @return 남은 분
     */
    public int getRemainingMinutes() {
        return TimerConstants.secondsToMinutes(remainingSeconds);
    }
    
    /**
     * 타이머가 거의 끝나가는지 확인 (30초 미만)
     * @return 거의 끝나가는 여부
     */
    public boolean isAlmostFinished() {
        return remainingSeconds <= 30;
    }
    
    /**
     * 타이머가 막 시작되었는지 확인 (5초 이내)
     * @return 막 시작된 여부
     */
    public boolean isJustStarted() {
        int elapsedSeconds = totalSeconds - remainingSeconds;
        return elapsedSeconds <= 5;
    }
    
    @Override
    public String toString() {
        return String.format("TimerTickEvent{remaining=%ds, total=%ds, cycle=%d, phase='%s', progress=%.1f%%, timestamp=%d}", 
                           remainingSeconds, totalSeconds, currentCycle, currentPhase, 
                           getProgress() * 100, getTimestamp());
    }
}
