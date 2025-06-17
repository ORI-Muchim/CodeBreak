package events;

import model.TimerModel;

/**
 * 타이머 완료 이벤트
 * 타이머가 완료되어 알림이 필요할 때 발생합니다.
 */
public class TimerCompletedEvent extends Event {
    private final TimerModel.NotificationType notificationType;
    private final int completedCycle;
    private final String completedPhase;
    private final boolean isPomodoroMode;
    private final int workMinutes;
    private final int breakMinutes;
    
    public TimerCompletedEvent(TimerModel.NotificationType notificationType, int completedCycle, 
                              String completedPhase, boolean isPomodoroMode, 
                              int workMinutes, int breakMinutes) {
        super("TIMER_COMPLETED");
        this.notificationType = notificationType;
        this.completedCycle = completedCycle;
        this.completedPhase = completedPhase;
        this.isPomodoroMode = isPomodoroMode;
        this.workMinutes = workMinutes;
        this.breakMinutes = breakMinutes;
    }
    
    /**
     * 알림 타입
     * @return 알림 타입
     */
    public TimerModel.NotificationType getNotificationType() {
        return notificationType;
    }
    
    /**
     * 완료된 사이클 번호
     * @return 완료된 사이클
     */
    public int getCompletedCycle() {
        return completedCycle;
    }
    
    /**
     * 완료된 페이즈
     * @return 완료된 페이즈 문자열
     */
    public String getCompletedPhase() {
        return completedPhase;
    }
    
    /**
     * 포모도로 모드 여부
     * @return 포모도로 모드 여부
     */
    public boolean isPomodoroMode() {
        return isPomodoroMode;
    }
    
    /**
     * 작업 시간 (분)
     * @return 작업 시간
     */
    public int getWorkMinutes() {
        return workMinutes;
    }
    
    /**
     * 휴식 시간 (분)
     * @return 휴식 시간
     */
    public int getBreakMinutes() {
        return breakMinutes;
    }
    
    /**
     * 작업 세션이 완료되었는지 확인
     * @return 작업 세션 완료 여부
     */
    public boolean isWorkSessionCompleted() {
        return isPomodoroMode && completedCycle % 2 == 1;
    }
    
    /**
     * 휴식 세션이 완료되었는지 확인
     * @return 휴식 세션 완료 여부
     */
    public boolean isBreakSessionCompleted() {
        return isPomodoroMode && completedCycle % 2 == 0;
    }
    
    /**
     * 알림 메시지 가져오기
     * @return 알림 메시지
     */
    public String getNotificationMessage() {
        return notificationType.getMessage();
    }
    
    /**
     * 알림 이름 가져오기
     * @return 알림 이름
     */
    public String getNotificationName() {
        return notificationType.getName();
    }
    
    /**
     * 완료 통계 정보
     * @return 통계 문자열
     */
    public String getCompletionStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("완료된 사이클: ").append(completedCycle);
        
        if (isPomodoroMode) {
            int workSessions = (completedCycle + 1) / 2;
            int breakSessions = completedCycle / 2;
            stats.append(" (작업: ").append(workSessions)
                 .append("회, 휴식: ").append(breakSessions).append("회)");
        }
        
        return stats.toString();
    }
    
    @Override
    public String toString() {
        return String.format("TimerCompletedEvent{type=%s, cycle=%d, phase='%s', pomodoro=%s, timestamp=%d}", 
                           notificationType.getName(), completedCycle, completedPhase, 
                           isPomodoroMode, getTimestamp());
    }
}
