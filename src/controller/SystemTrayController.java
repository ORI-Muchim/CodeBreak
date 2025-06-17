package controller;

import model.*;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * 시스템 트레이 동작을 관리하는 컨트롤러
 */
public class SystemTrayController {
    private MainFrame mainFrame;
    private TimerModel timerModel;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private PopupMenu popupMenu;
    
    // 메뉴 아이템들
    private MenuItem showMenuItem;
    private MenuItem startStopMenuItem;
    private MenuItem resetMenuItem;
    private Menu profileMenu;
    private MenuItem settingsMenuItem;
    private MenuItem exitMenuItem;
    
    private boolean isSetup = false;
    
    public SystemTrayController(MainFrame mainFrame, TimerModel timerModel) {
        this.mainFrame = mainFrame;
        this.timerModel = timerModel;
        
        if (SystemTray.isSupported()) {
            this.systemTray = SystemTray.getSystemTray();
        }
    }
    
    /**
     * 시스템 트레이 설정
     */
    public void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.err.println("시스템 트레이가 지원되지 않습니다.");
            return;
        }
        
        try {
            // 트레이 아이콘 생성
            Image trayIconImage = createTrayIcon();
            
            // 팝업 메뉴 생성
            createPopupMenu();
            
            // 트레이 아이콘 설정
            trayIcon = new TrayIcon(trayIconImage, "Code ∧ Break", popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Code ∧ Break - " + getStatusText());
            
            // 이벤트 리스너 설정
            setupTrayIconListeners();
            
            // 시스템 트레이에 추가
            systemTray.add(trayIcon);
            
            isSetup = true;
            
            // 타이머 상태 변경 리스너 등록
            timerModel.addTimerListener(new TimerModel.TimerListener() {
                @Override
                public void onTimerTick(int remainingSeconds) {
                    updateTrayTooltip();
                }
                
                @Override
                public void onTimerComplete(TimerModel.NotificationType type) {
                    // 트레이에서도 알림 표시
                    showTrayMessage("휴식 시간!", type.getMessage());
                }
                
                @Override
                public void onTimerStateChanged(TimerModel.TimerState state) {
                    updateTrayMenu();
                    updateTrayTooltip();
                }
            });
            
        } catch (AWTException e) {
            System.err.println("시스템 트레이 설정 실패: " + e.getMessage());
        }
    }
    
    /**
     * 트레이 아이콘 생성
     */
    private Image createTrayIcon() {
        int size = 16; // 트레이 아이콘 크기
        BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        // 안티얼라이싱 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 상태에 따른 아이콘 색상
        Color iconColor;
        switch (timerModel.getState()) {
            case RUNNING:
                iconColor = new Color(60, 179, 113); // 녹색
                break;
            case PAUSED:
                iconColor = new Color(255, 165, 0); // 주황색
                break;
            case STOPPED:
            default:
                iconColor = new Color(128, 128, 128); // 회색
                break;
        }
        
        // 원형 배경
        g2d.setColor(iconColor);
        g2d.fillOval(1, 1, size-2, size-2);
        
        // 테두리
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(1, 1, size-2, size-2);
        
        // 상태 표시 (간단한 기호)
        g2d.setColor(Color.WHITE);
        switch (timerModel.getState()) {
            case RUNNING:
                // 재생 표시 (작은 삼각형)
                int[] xPoints = {size/3, size*2/3, size/3};
                int[] yPoints = {size/4, size/2, size*3/4};
                g2d.fillPolygon(xPoints, yPoints, 3);
                break;
            case PAUSED:
                // 일시정지 표시 (두 개의 세로선)
                g2d.fillRect(size/3, size/4, 2, size/2);
                g2d.fillRect(size*2/3-2, size/4, 2, size/2);
                break;
            case STOPPED:
                // 정지 표시 (작은 사각형)
                g2d.fillRect(size/3, size/3, size/3, size/3);
                break;
        }
        
        g2d.dispose();
        return icon;
    }
    
    /**
     * 팝업 메뉴 생성
     */
    private void createPopupMenu() {
        popupMenu = new PopupMenu();
        
        // 창 표시/숨기기
        showMenuItem = new MenuItem("창 표시");
        showMenuItem.addActionListener(e -> showMainWindow());
        popupMenu.add(showMenuItem);
        
        popupMenu.addSeparator();
        
        // 타이머 제어
        startStopMenuItem = new MenuItem("시작");
        startStopMenuItem.addActionListener(e -> toggleTimer());
        popupMenu.add(startStopMenuItem);
        
        resetMenuItem = new MenuItem("리셋");
        resetMenuItem.addActionListener(e -> resetTimer());
        popupMenu.add(resetMenuItem);
        
        popupMenu.addSeparator();
        
        // 프로필 메뉴 (동적으로 생성)
        profileMenu = new Menu("프로필");
        updateProfileMenu();
        popupMenu.add(profileMenu);
        
        popupMenu.addSeparator();
        
        // 설정
        settingsMenuItem = new MenuItem("설정");
        settingsMenuItem.addActionListener(e -> showSettings());
        popupMenu.add(settingsMenuItem);
        
        popupMenu.addSeparator();
        
        // 종료
        exitMenuItem = new MenuItem("종료");
        exitMenuItem.addActionListener(e -> exitApplication());
        popupMenu.add(exitMenuItem);
        
        // 초기 메뉴 상태 업데이트
        updateTrayMenu();
    }
    
    /**
     * 프로필 메뉴 업데이트
     */
    private void updateProfileMenu() {
        if (profileMenu == null) return;
        
        // 기존 메뉴 아이템 제거
        profileMenu.removeAll();
        
        // 프로필 목록 가져오기
        if (mainFrame.getSettingsController() != null) {
            for (UserProfile profile : mainFrame.getSettingsController().getAllProfiles()) {
                MenuItem profileItem = new MenuItem(profile.getProfileName());
                profileItem.addActionListener(e -> switchToProfile(profile));
                
                // 현재 프로필 표시
                if (profile == mainFrame.getSettingsController().getCurrentProfile()) {
                    profileItem.setLabel("● " + profile.getProfileName());
                }
                
                profileMenu.add(profileItem);
            }
        }
        
        profileMenu.addSeparator();
        
        // 빠른 프리셋
        MenuItem pomodoroItem = new MenuItem("포모도로 (25/5)");
        pomodoroItem.addActionListener(e -> applyPomodoroPreset());
        profileMenu.add(pomodoroItem);
        
        MenuItem longWorkItem = new MenuItem("장시간 작업 (60/10)");
        longWorkItem.addActionListener(e -> applyLongWorkPreset());
        profileMenu.add(longWorkItem);
        
        MenuItem shortFocusItem = new MenuItem("단시간 집중 (15/3)");
        shortFocusItem.addActionListener(e -> applyShortFocusPreset());
        profileMenu.add(shortFocusItem);
    }
    
    /**
     * 트레이 아이콘 이벤트 리스너 설정
     */
    private void setupTrayIconListeners() {
        // 더블클릭으로 창 표시/숨기기
        trayIcon.addActionListener(e -> toggleMainWindow());
        
        // 마우스 클릭 이벤트 (더 세밀한 제어)
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                    // 왼쪽 단일 클릭 - 빠른 상태 확인
                    showQuickStatus();
                }
            }
        });
    }
    
    /**
     * 트레이 메뉴 상태 업데이트
     */
    private void updateTrayMenu() {
        if (!isSetup) return;
        
        SwingUtilities.invokeLater(() -> {
            // 창 표시/숨기기 메뉴
            if (mainFrame.isVisible()) {
                showMenuItem.setLabel("창 숨기기");
            } else {
                showMenuItem.setLabel("창 표시");
            }
            
            // 타이머 제어 메뉴
            switch (timerModel.getState()) {
                case STOPPED:
                    startStopMenuItem.setLabel("시작");
                    startStopMenuItem.setEnabled(true);
                    resetMenuItem.setEnabled(true);
                    break;
                case RUNNING:
                    startStopMenuItem.setLabel("일시정지");
                    startStopMenuItem.setEnabled(true);
                    resetMenuItem.setEnabled(false);
                    break;
                case PAUSED:
                    startStopMenuItem.setLabel("재개");
                    startStopMenuItem.setEnabled(true);
                    resetMenuItem.setEnabled(true);
                    break;
            }
            
            // 프로필 메뉴 업데이트
            updateProfileMenu();
            
            // 트레이 아이콘 업데이트
            if (trayIcon != null) {
                trayIcon.setImage(createTrayIcon());
            }
        });
    }
    
    /**
     * 트레이 툴팁 업데이트
     */
    private void updateTrayTooltip() {
        if (!isSetup || trayIcon == null) return;
        
        SwingUtilities.invokeLater(() -> {
            String tooltip = "Code ∧ Break - " + getStatusText();
            trayIcon.setToolTip(tooltip);
        });
    }
    
    /**
     * 상태 텍스트 가져오기
     */
    private String getStatusText() {
        StringBuilder status = new StringBuilder();
        
        switch (timerModel.getState()) {
            case RUNNING:
                status.append("실행 중 (").append(timerModel.getFormattedTime()).append(")");
                break;
            case PAUSED:
                status.append("일시정지 (").append(timerModel.getFormattedTime()).append(")");
                break;
            case STOPPED:
                status.append("정지");
                break;
        }
        
        status.append(" | ").append(timerModel.getCurrentPhase());
        
        return status.toString();
    }
    
    /**
     * 빠른 상태 표시
     */
    private void showQuickStatus() {
        String message = String.format(
            "상태: %s\n시간: %s\n페이즈: %s\n사이클: %d",
            timerModel.getState().toString(),
            timerModel.getFormattedTime(),
            timerModel.getCurrentPhase(),
            timerModel.getCurrentCycle()
        );
        
        showTrayMessage("현재 상태", message);
    }
    
    /**
     * 메인 창 표시/숨기기 토글
     */
    private void toggleMainWindow() {
        if (mainFrame.isVisible()) {
            mainFrame.setVisible(false);
        } else {
            showMainWindow();
        }
    }
    
    /**
     * 메인 창 표시
     */
    private void showMainWindow() {
        mainFrame.restoreFromTray();
    }
    
    /**
     * 타이머 시작/일시정지 토글
     */
    private void toggleTimer() {
        TimerController controller = mainFrame.getTimerController();
        
        switch (timerModel.getState()) {
            case STOPPED:
            case PAUSED:
                controller.startTimer();
                break;
            case RUNNING:
                controller.pauseTimer();
                break;
        }
    }
    
    /**
     * 타이머 리셋
     */
    private void resetTimer() {
        mainFrame.getTimerController().resetTimer();
    }
    
    /**
     * 프로필 전환
     */
    private void switchToProfile(UserProfile profile) {
        mainFrame.getSettingsController().setCurrentProfile(profile);
        mainFrame.getSettingsController().applyProfileToTimer(timerModel);
        mainFrame.getSettingsController().applyProfileToNotificationManager(mainFrame.getNotificationManager());
        
        showTrayMessage("프로필 변경", "'" + profile.getProfileName() + "' 프로필로 변경되었습니다.");
    }
    
    /**
     * 포모도로 프리셋 적용
     */
    private void applyPomodoroPreset() {
        mainFrame.getTimerController().applyPomodoroPreset();
        showTrayMessage("프리셋 적용", "포모도로 프리셋이 적용되었습니다. (25분/5분)");
    }
    
    /**
     * 장시간 작업 프리셋 적용
     */
    private void applyLongWorkPreset() {
        mainFrame.getTimerController().applyLongWorkPreset();
        showTrayMessage("프리셋 적용", "장시간 작업 프리셋이 적용되었습니다. (60분/10분)");
    }
    
    /**
     * 단시간 집중 프리셋 적용
     */
    private void applyShortFocusPreset() {
        mainFrame.getTimerController().applyShortFocusPreset();
        showTrayMessage("프리셋 적용", "단시간 집중 프리셋이 적용되었습니다. (15분/3분)");
    }
    
    /**
     * 설정 창 표시
     */
    private void showSettings() {
        showMainWindow();
        // 설정 탭으로 전환하는 로직이 필요하면 여기에 추가
    }
    
    /**
     * 애플리케이션 종료
     */
    private void exitApplication() {
        // 확인 다이얼로그 없이 바로 종료 (트레이에서는)
        cleanup();
        System.exit(0);
    }
    
    /**
     * 트레이 아이콘 업데이트 (외부에서 호출)
     */
    public void updateTrayIcon(String status) {
        updateTrayMenu();
        updateTrayTooltip();
    }
    
    /**
     * 트레이 메시지 표시
     */
    public void showTrayMessage(String title, String message) {
        if (isSetup && trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }
    
    /**
     * 트레이 메시지 표시 (메시지 유형 지정)
     */
    public void showTrayMessage(String title, String message, TrayIcon.MessageType messageType) {
        if (isSetup && trayIcon != null) {
            trayIcon.displayMessage(title, message, messageType);
        }
    }
    
    /**
     * 정리 작업
     */
    public void cleanup() {
        if (isSetup && systemTray != null && trayIcon != null) {
            systemTray.remove(trayIcon);
            isSetup = false;
        }
    }
    
    // Getters
    public boolean isSetup() { return isSetup; }
    public TrayIcon getTrayIcon() { return trayIcon; }
}
