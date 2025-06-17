package events;

import model.TimerModel;

/**
 * 타이머 상태 변경 이벤트
 * 타이머의 상태가 변경되었을 때 발생합니다.
 */
public class TimerStateChangedEvent extends Event {
    private final TimerModel.TimerState oldState;
    private final TimerModel.TimerState newState;
    private final int remainingSeconds;
    private final int currentCycle;
    private final String currentPhase;
    
    public TimerStateChangedEvent(TimerModel.TimerState oldState, TimerModel.TimerState newState, 
                                 int remainingSeconds, int currentCycle, String currentPhase) {
        super("TIMER_STATE_CHANGED");
        this.oldState = oldState;
        this.newState = newState;
        this.remainingSeconds = remainingSeconds;
        this.currentCycle = currentCycle;
        this.currentPhase = currentPhase;
    }
    
    /**
     * 이전 타이머 상태
     * @return 이전 상태
     */
    public TimerModel.TimerState getOldState() {
        return oldState;
    }
    
    /**
     * 새로운 타이머 상태
     * @return 새로운 상태
     */
    public TimerModel.TimerState getNewState() {
        return newState;
    }
    
    /**
     * 남은 시간 (초)
     * @return 남은 시간
     */
    public int getRemainingSeconds() {
        return remainingSeconds;
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
     * 타이머가 시작되었는지 확인
     * @return 시작 여부
     */
    public boolean isStarted() {
        return newState == TimerModel.TimerState.RUNNING;
    }
    
    /**
     * 타이머가 정지되었는지 확인
     * @return 정지 여부
     */
    public boolean isStopped() {
        return newState == TimerModel.TimerState.STOPPED;
    }
    
    /**
     * 타이머가 일시정지되었는지 확인
     * @return 일시정지 여부
     */
    public boolean isPaused() {
        return newState == TimerModel.TimerState.PAUSED;
    }
    
    /**
     * 상태가 실제로 변경되었는지 확인
     * @return 변경 여부
     */
    public boolean isActualStateChange() {
        return oldState != newState;
    }
    
    @Override
    public String toString() {
        return String.format("TimerStateChangedEvent{old=%s, new=%s, remaining=%ds, cycle=%d, phase='%s', timestamp=%d}", 
                           oldState, newState, remainingSeconds, currentCycle, currentPhase, getTimestamp());
    }
}
