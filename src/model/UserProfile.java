package model;

import constants.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 설정을 저장하고 관리하는 클래스
 */
public class UserProfile {
    private String profileName;
    private int workMinutes;
    private int breakMinutes;
    private boolean pomodoroMode;
    private boolean soundEnabled;
    private boolean popupEnabled;
    private boolean flashEnabled;
    private int snoozeMinutes;
    private boolean autoStart;
    private boolean minimizeToTray;
    
    // 알림 유형별 활성화 상태 (Map으로 변경)
    private Map<TimerModel.NotificationType, Boolean> notificationSettings;
    
    // 기본 생성자
    public UserProfile() {
        this(TimerConstants.DEFAULT_PROFILE_NAME);
    }
    
    // 프로필명을 받는 생성자
    public UserProfile(String profileName) {
        this.profileName = profileName;
        
        // 기본값 설정
        this.workMinutes = TimerConstants.DEFAULT_WORK_MINUTES;
        this.breakMinutes = TimerConstants.DEFAULT_BREAK_MINUTES;
        this.pomodoroMode = true;
        this.soundEnabled = true;
        this.popupEnabled = true;
        this.flashEnabled = true;
        this.snoozeMinutes = TimerConstants.DEFAULT_SNOOZE_MINUTES;
        this.autoStart = false;
        this.minimizeToTray = false; // 기본값을 false로 변경
        
        // 알림 설정 초기화 (기본적으로 모두 비활성화)
        this.notificationSettings = new HashMap<>();
        for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
            notificationSettings.put(type, false); // 기본적으로 비활성화
        }
        
        // 휴식 알림만 기본적으로 활성화
        notificationSettings.put(TimerModel.NotificationType.REST, true);
    }
    
    /**
     * 포모도로 프로필 생성
     */
    public static UserProfile createPomodoroProfile() {
        UserProfile profile = new UserProfile(TimerConstants.POMODORO_PROFILE_NAME);
        profile.setWorkMinutes(TimerConstants.POMODORO_WORK_MINUTES);
        profile.setBreakMinutes(TimerConstants.POMODORO_BREAK_MINUTES);
        profile.setPomodoroMode(true);
        profile.setMinimizeToTray(false); // 명시적으로 false 설정
        return profile;
    }
    
    /**
     * 장시간 작업 프로필 생성
     */
    public static UserProfile createLongWorkProfile() {
        UserProfile profile = new UserProfile(TimerConstants.LONG_WORK_PROFILE_NAME);
        profile.setWorkMinutes(TimerConstants.LONG_WORK_MINUTES);
        profile.setBreakMinutes(TimerConstants.LONG_WORK_BREAK_MINUTES);
        profile.setPomodoroMode(false);
        profile.setMinimizeToTray(false); // 명시적으로 false 설정
        return profile;
    }
    
    /**
     * 단시간 집중 프로필 생성
     */
    public static UserProfile createShortFocusProfile() {
        UserProfile profile = new UserProfile(TimerConstants.SHORT_FOCUS_PROFILE_NAME);
        profile.setWorkMinutes(TimerConstants.SHORT_FOCUS_WORK_MINUTES);
        profile.setBreakMinutes(TimerConstants.SHORT_FOCUS_BREAK_MINUTES);
        profile.setPomodoroMode(true);
        profile.setMinimizeToTray(false); // 명시적으로 false 설정
        return profile;
    }
    
    /**
     * 프로필 유효성 검사 - TimerConstants의 유효성 검사 메서드 활용
     */
    public boolean isValid() {
        return TimerConstants.isValidWorkMinutes(workMinutes) && 
               TimerConstants.isValidBreakMinutes(breakMinutes) && 
               TimerConstants.isValidSnoozeMinutes(snoozeMinutes) && 
               profileName != null && 
               !profileName.trim().isEmpty() &&
               profileName.length() <= TimerConstants.MAX_PROFILE_NAME_LENGTH;
    }
    
    /**
     * 프로필을 다른 프로필로부터 복사
     */
    public void copyFrom(UserProfile other) {
        this.workMinutes = other.workMinutes;
        this.breakMinutes = other.breakMinutes;
        this.pomodoroMode = other.pomodoroMode;
        this.soundEnabled = other.soundEnabled;
        this.popupEnabled = other.popupEnabled;
        this.flashEnabled = other.flashEnabled;
        this.snoozeMinutes = other.snoozeMinutes;
        this.autoStart = other.autoStart;
        this.minimizeToTray = other.minimizeToTray;
        
        // 알림 설정 복사
        this.notificationSettings = new HashMap<>(other.notificationSettings);
    }
    
    /**
     * 특정 알림 유형이 활성화되어 있는지 확인
     */
    public boolean isNotificationEnabled(TimerModel.NotificationType type) {
        Boolean enabled = notificationSettings.get(type);
        boolean result = enabled != null && enabled;
        System.out.println("프로필 '" + profileName + "'에서 알림 " + type.getName() + " 확인: " + result);
        return result;
    }
    
    /**
     * 알림 유형 활성화/비활성화
     */
    public void setNotificationEnabled(TimerModel.NotificationType type, boolean enabled) {
        notificationSettings.put(type, enabled);
        System.out.println("프로필 '" + profileName + "'에서 알림 " + type.getName() + " 설정: " + enabled);
    }
    
    @Override
    public String toString() {
        return profileName + " (작업:" + workMinutes + "분, 휴식:" + breakMinutes + "분)";
    }
    
    // Getters and Setters
    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }
    
    public int getWorkMinutes() { return workMinutes; }
    public void setWorkMinutes(int workMinutes) { this.workMinutes = workMinutes; }
    
    public int getBreakMinutes() { return breakMinutes; }
    public void setBreakMinutes(int breakMinutes) { this.breakMinutes = breakMinutes; }
    
    public boolean isPomodoroMode() { return pomodoroMode; }
    public void setPomodoroMode(boolean pomodoroMode) { this.pomodoroMode = pomodoroMode; }
    
    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }
    
    public boolean isPopupEnabled() { return popupEnabled; }
    public void setPopupEnabled(boolean popupEnabled) { this.popupEnabled = popupEnabled; }
    
    public boolean isFlashEnabled() { return flashEnabled; }
    public void setFlashEnabled(boolean flashEnabled) { this.flashEnabled = flashEnabled; }
    
    public int getSnoozeMinutes() { return snoozeMinutes; }
    public void setSnoozeMinutes(int snoozeMinutes) { this.snoozeMinutes = snoozeMinutes; }
    
    public boolean isAutoStart() { return autoStart; }
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
    
    public boolean isMinimizeToTray() { return minimizeToTray; }
    public void setMinimizeToTray(boolean minimizeToTray) { this.minimizeToTray = minimizeToTray; }
    
    // 알림 설정 관련 getters/setters
    public Map<TimerModel.NotificationType, Boolean> getNotificationSettings() { 
        return new HashMap<>(notificationSettings); 
    }
    
    public void setNotificationSettings(Map<TimerModel.NotificationType, Boolean> settings) { 
        this.notificationSettings = new HashMap<>(settings); 
    }
}
