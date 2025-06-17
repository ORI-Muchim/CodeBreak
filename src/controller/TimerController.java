package controller;

import model.*;
import constants.*;

/**
 * 타이머 동작을 제어하는 컨트롤러
 */
public class TimerController {
    private TimerModel timerModel;
    private NotificationManager notificationManager;
    private TimerStatusCache statusCache; // 성능 최적화를 위한 캐시
    
    public TimerController(TimerModel timerModel, NotificationManager notificationManager) {
        this.timerModel = timerModel;
        this.notificationManager = notificationManager;
        this.statusCache = new TimerStatusCache();
    }
    
    /**
     * 타이머 시작 (상태 체크 최적화)
     */
    public void startTimer() {
        if (canStart()) {
            timerModel.startTimer();
        }
    }
    
    /**
     * 타이머 일시정지 (상태 체크 최적화)
     */
    public void pauseTimer() {
        if (isRunning()) {
            timerModel.pauseTimer();
        }
    }
    
    // ============================================
    // 헬퍼 메서드들 - 상태 체크 로직 통합
    // ============================================
    
    private boolean canStart() {
        TimerModel.TimerState state = timerModel.getState();
        return state == TimerModel.TimerState.STOPPED || state == TimerModel.TimerState.PAUSED;
    }
    
    private boolean isRunning() {
        return timerModel.getState() == TimerModel.TimerState.RUNNING;
    }
    
    private boolean isPaused() {
        return timerModel.getState() == TimerModel.TimerState.PAUSED;
    }
    
    /**
     * 타이머 정지
     */
    public void stopTimer() {
        timerModel.stopTimer();
    }
    
    /**
     * 타이머 리셋
     */
    public void resetTimer() {
        timerModel.resetTimer();
    }
    
    /**
     * 휴식 확인 (알림 다이얼로그에서 확인 버튼 클릭 시)
     */
    public void acknowledgeBreak() {
        if (isPomodoroMode()) {
            startNextPomodoroCycle();
        } else {
            resetToWorkPhase();
        }
    }
    
    private boolean isPomodoroMode() {
        return timerModel.isPomodoroMode();
    }
    
    private void startNextPomodoroCycle() {
        // 포모도로 모드에서는 자동으로 다음 페이즈로 전환
        timerModel.startTimer();
    }
    
    private void resetToWorkPhase() {
        // 일반 모드에서는 타이머 리셋
        timerModel.resetTimer();
    }
    
    /**
     * 작업 계속 (알림 다이얼로그에서 계속 작업 버튼 클릭 시)
     */
    public void continueWork() {
        // 현재 사이클을 작업 사이클로 리셋하고 계속 진행
        if (timerModel.isPomodoroMode()) {
            // 작업 시간으로 리셋
            timerModel.stopTimer();
            // 사이클을 짝수로 만들어서 작업 시간이 되도록 함
            if (timerModel.getCurrentCycle() % 2 == 1) {
                // 현재가 휴식 사이클이면 이전 작업 사이클로 되돌림
                // 실제로는 새로운 작업 사이클 시작
            }
            timerModel.resetTimer();
            timerModel.startTimer();
        } else {
            // 일반 모드에서는 그냥 계속
            if (timerModel.getState() == TimerModel.TimerState.PAUSED) {
                timerModel.startTimer();
            }
        }
    }
    
    /**
     * 알림 스누즈 (지정된 시간 후 다시 알림) - 최적화됨
     */
    public void snoozeNotification(TimerModel.NotificationType type) {
        notificationManager.snoozeNotification(type);
        
        // 타이머는 계속 진행
        if (isPaused()) {
            timerModel.startTimer();
        }
    }
    
    /**
     * 작업 시간 설정
     */
    public void setWorkMinutes(int minutes) {
        if (TimerConstants.isValidWorkMinutes(minutes)) {
            timerModel.setWorkMinutes(minutes);
        }
    }
    
    /**
     * 휴식 시간 설정
     */
    public void setBreakMinutes(int minutes) {
        if (TimerConstants.isValidBreakMinutes(minutes)) {
            timerModel.setBreakMinutes(minutes);
        }
    }
    
    /**
     * 포모도로 모드 설정 - 최적화됨
     */
    public void setPomodoroMode(boolean enabled) {
        timerModel.setPomodoroMode(enabled);
        
        // 모드 변경 시 타이머가 실행 중이면 리셋
        if (isRunning()) {
            stopAndResetTimer();
        }
    }
    
    private void stopAndResetTimer() {
        timerModel.stopTimer();
        timerModel.resetTimer();
    }
    
    /**
     * 빠른 타이머 설정 (프리셋) - 최적화됨
     */
    public void setQuickTimer(int workMinutes, int breakMinutes, boolean pomodoroMode) {
        boolean wasRunning = isRunning();
        
        if (wasRunning) {
            timerModel.stopTimer();
        }
        
        applyTimerSettings(workMinutes, breakMinutes, pomodoroMode);
        
        if (wasRunning) {
            timerModel.startTimer();
        }
    }
    
    private void applyTimerSettings(int workMinutes, int breakMinutes, boolean pomodoroMode) {
        timerModel.setWorkMinutes(workMinutes);
        timerModel.setBreakMinutes(breakMinutes);
        timerModel.setPomodoroMode(pomodoroMode);
        timerModel.resetTimer();
    }
    
    /**
     * 포모도로 프리셋 적용
     */
    public void applyPomodoroPreset() {
        setQuickTimer(TimerConstants.POMODORO_WORK_MINUTES, TimerConstants.POMODORO_BREAK_MINUTES, true);
    }
    
    /**
     * 장시간 작업 프리셋 적용
     */
    public void applyLongWorkPreset() {
        setQuickTimer(TimerConstants.LONG_WORK_MINUTES, TimerConstants.LONG_WORK_BREAK_MINUTES, false);
    }
    
    /**
     * 단시간 집중 프리셋 적용
     */
    public void applyShortFocusPreset() {
        setQuickTimer(TimerConstants.SHORT_FOCUS_WORK_MINUTES, TimerConstants.SHORT_FOCUS_BREAK_MINUTES, true);
    }
    
    /**
     * 사용자 정의 프리셋 적용
     */
    public void applyCustomPreset(int workMinutes, int breakMinutes) {
        setQuickTimer(workMinutes, breakMinutes, false);
    }
    
    public void setCurrentProfile(UserProfile profile) {
        if (profile != null) {
            System.out.println("🔄 TimerController에 프로필 적용: " + profile.getProfileName());
            
            // 타이머 모델에 프로필 설정 적용
            timerModel.setWorkMinutes(profile.getWorkMinutes());
            timerModel.setBreakMinutes(profile.getBreakMinutes());
            timerModel.setPomodoroMode(profile.isPomodoroMode());
            timerModel.setCurrentProfile(profile);
            
            // 알림 매니저에 프로필 설정 적용
            applyProfileToNotificationManager(profile);
            
            System.out.println("✅ TimerController 프로필 적용 완료: " + profile.getWorkMinutes() + "분/" + profile.getBreakMinutes() + "분");
        }
    }
    
    private void applyProfileToNotificationManager(UserProfile profile) {
        notificationManager.setCurrentProfile(profile);
        notificationManager.setSoundEnabled(profile.isSoundEnabled());
        notificationManager.setPopupEnabled(profile.isPopupEnabled());
        notificationManager.setFlashEnabled(profile.isFlashEnabled());
        notificationManager.setSnoozeMinutes(profile.getSnoozeMinutes());
    }
    
    /**
     * 긴급 휴식 모드 (즉시 휴식 알림 표시) - 최적화됨
     */
    public void triggerEmergencyBreak() {
        timerModel.pauseTimer();
        
        TimerModel.NotificationType notificationType = getRandomEnabledNotificationType();
        notificationManager.showNotification(notificationType);
    }
    
    /**
     * 현재 프로필에서 활성화된 알림 유형 중 랜덤 선택
     */
    private TimerModel.NotificationType getRandomEnabledNotificationType() {
        UserProfile currentProfile = timerModel.getCurrentProfile();
        
        if (currentProfile != null) {
            java.util.List<TimerModel.NotificationType> enabledTypes = new java.util.ArrayList<>();
            for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
                if (currentProfile.isNotificationEnabled(type)) {
                    enabledTypes.add(type);
                }
            }
            
            if (!enabledTypes.isEmpty()) {
                return enabledTypes.get((int) (Math.random() * enabledTypes.size()));
            }
        }
        
        // 프로필이 없거나 활성화된 알림이 없으면 기본적으로 REST 반환
        return TimerModel.NotificationType.REST;
    }
    
    /**
     * 현재 타이머 상태 정보 가져오기 (캐시 사용으로 최적화됨)
     */
    public String getTimerStatusText() {
        return statusCache.getTimerStatusText(timerModel);
    }
    
    /**
     * 통계 정보 계산
     */
    public TimerStatistics getStatistics() {
        return new TimerStatistics(timerModel);
    }
    
    /**
     * 타이머 통계 정보를 담는 내부 클래스
     */
    public static class TimerStatistics {
        private final int totalCycles;
        private final int workMinutes;
        private final int breakMinutes;
        private final boolean pomodoroMode;
        private final String currentPhase;
        
        public TimerStatistics(TimerModel model) {
            this.totalCycles = model.getCurrentCycle();
            this.workMinutes = model.getWorkMinutes();
            this.breakMinutes = model.getBreakMinutes();
            this.pomodoroMode = model.isPomodoroMode();
            this.currentPhase = model.getCurrentPhase();
        }
        
        public int getTotalWorkSessions() {
            return pomodoroMode ? (totalCycles + 1) / 2 : totalCycles;
        }
        
        public int getTotalBreakSessions() {
            return pomodoroMode ? totalCycles / 2 : 0;
        }
        
        public int getEstimatedWorkMinutes() {
            return getTotalWorkSessions() * workMinutes;
        }
        
        public int getEstimatedBreakMinutes() {
            return getTotalBreakSessions() * breakMinutes;
        }
        
        public double getProductivityRatio() {
            int totalWork = getEstimatedWorkMinutes();
            int totalBreak = getEstimatedBreakMinutes();
            int total = totalWork + totalBreak;
            
            return total > 0 ? (double) totalWork / total * TimerConstants.PERCENTAGE_MULTIPLIER : 0.0;
        }
        
        // Getters
        public int getTotalCycles() { return totalCycles; }
        public int getWorkMinutes() { return workMinutes; }
        public int getBreakMinutes() { return breakMinutes; }
        public boolean isPomodoroMode() { return pomodoroMode; }
        public String getCurrentPhase() { return currentPhase; }
    }
    
    // Getters
    public TimerModel getTimerModel() { return timerModel; }
    public NotificationManager getNotificationManager() { return notificationManager; }
}
