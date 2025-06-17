package model;

import constants.TimerConstants;
import java.awt.*;

/**
 * 알림 생성 및 관리를 담당하는 클래스
 */
public class NotificationManager {
    private boolean soundEnabled;
    private boolean popupEnabled;
    private boolean flashEnabled;
    private int snoozeMinutes;
    
    // 현재 프로필 참조 (알림 유형 설정 확인용)
    private UserProfile currentProfile;
    
    public NotificationManager() {
        this.soundEnabled = true;
        this.popupEnabled = true;
        this.flashEnabled = true;
        this.snoozeMinutes = TimerConstants.DEFAULT_SNOOZE_MINUTES;
    }
    
    /**
     * 알림을 표시합니다 (설정에 따라 필터링)
     */
    public void showNotification(TimerModel.NotificationType type) {
        // 알림 유형이 비활성화되어 있으면 표시하지 않음
        if (!isNotificationTypeEnabled(type)) {
            System.out.println("알림 유형 " + type.getName() + "이(가) 비활성화되어 있어 알림을 표시하지 않습니다.");
            return;
        }
        
        // 알림 우선순위 확인
        if (!shouldShowNotification()) {
            return;
        }
        
        System.out.println("알림 표시: " + type.getName() + " - " + type.getMessage());
        
        if (popupEnabled) {
            showPopupNotification(type);
        }
        
        if (soundEnabled) {
            playNotificationSound();
        }
        
        if (flashEnabled) {
            flashScreen();
        }
        
        // 시스템 트레이 알림도 표시
        showSystemTrayNotification(type);
    }
    
    /**
     * 알림 유형이 활성화되어 있는지 확인 (프로필 설정에서)
     */
    public boolean isNotificationTypeEnabled(TimerModel.NotificationType type) {
        if (currentProfile == null) {
            return true; // 프로필이 없으면 기본적으로 활성화
        }
        
        boolean enabled = currentProfile.isNotificationEnabled(type);
        System.out.println("알림 유형 " + type.getName() + " 확인: " + enabled);
        return enabled;
    }
    
    /**
     * 현재 프로필 설정
     */
    public void setCurrentProfile(UserProfile profile) {
        this.currentProfile = profile;
        System.out.println("알림 매니저에 프로필 설정: " + (profile != null ? profile.getProfileName() : "null"));
    }
    
    /**
     * 팝업 알림을 표시합니다
     */
    private void showPopupNotification(TimerModel.NotificationType type) {
        // 이 부분은 View 클래스에서 처리될 예정
        // 여기서는 알림 데이터만 준비
        System.out.println("팝업 알림: " + type.getName() + " - " + type.getMessage());
    }
    
    /**
     * 알림음을 재생합니다
     */
    private void playNotificationSound() {
        try {
            // 기본 시스템 알림음 사용
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            System.err.println("알림음 재생 실패: " + e.getMessage());
        }
    }
    
    /**
     * 화면을 깜빡입니다
     */
    private void flashScreen() {
        try {
            // 화면 깜빡임 효과
            new Thread(() -> {
                try {
                    // 간단한 화면 깜빡임 효과
                    Frame flashFrame = new Frame();
                    flashFrame.setUndecorated(true);
                    flashFrame.setBackground(Color.WHITE);
                    flashFrame.setAlwaysOnTop(true);
                    flashFrame.setOpacity(0.3f);
                    flashFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                    
                    flashFrame.setVisible(true);
                    Thread.sleep(200);
                    flashFrame.setVisible(false);
                    flashFrame.dispose();
                    
                } catch (Exception e) {
                    System.err.println("화면 깜빡임 실패: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            System.err.println("화면 깜빡임 실패: " + e.getMessage());
        }
    }
    
    /**
     * 시스템 트레이 알림을 표시합니다
     */
    private void showSystemTrayNotification(TimerModel.NotificationType type) {
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                TrayIcon[] trayIcons = tray.getTrayIcons();
                
                if (trayIcons.length > 0) {
                    TrayIcon trayIcon = trayIcons[0];
                    trayIcon.displayMessage(
                        "Code ∧ Break",
                        type.getMessage(),
                        TrayIcon.MessageType.INFO
                    );
                }
            } catch (Exception e) {
                System.err.println("시스템 트레이 알림 실패: " + e.getMessage());
            }
        }
    }
    
    /**
     * 스누즈 기능 - 지정된 시간 후에 다시 알림
     */
    public void snoozeNotification(TimerModel.NotificationType type) {
        new Thread(() -> {
            try {
                Thread.sleep(snoozeMinutes * TimerConstants.SECONDS_PER_MINUTE * TimerConstants.MILLISECONDS_PER_SECOND); // 상수를 사용한 시간 변환
                showNotification(type);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * 알림 우선순위를 확인합니다
     */
    public boolean shouldShowNotification() {
        // 현재 활성 창이 풀스크린인지 확인
        try {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gd.getFullScreenWindow() != null) {
                // 풀스크린 모드에서는 덜 방해가 되는 알림만 표시
                return false;
            }
        } catch (Exception e) {
            // 에러 발생시 기본적으로 알림 표시
        }
        
        return true;
    }
    
    // Getters and Setters
    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }
    
    public boolean isPopupEnabled() { return popupEnabled; }
    public void setPopupEnabled(boolean popupEnabled) { this.popupEnabled = popupEnabled; }
    
    public boolean isFlashEnabled() { return flashEnabled; }
    public void setFlashEnabled(boolean flashEnabled) { this.flashEnabled = flashEnabled; }
    
    public int getSnoozeMinutes() { return snoozeMinutes; }
    public void setSnoozeMinutes(int snoozeMinutes) { this.snoozeMinutes = snoozeMinutes; }
}
