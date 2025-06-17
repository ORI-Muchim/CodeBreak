package view;

import model.*;
import controller.*;
import constants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * 메인 애플리케이션 창
 */
public class MainFrame extends JFrame {
    private TimerModel timerModel;
    private NotificationManager notificationManager;
    private JsonDataManager dataManager;
    
    private TimerPanel timerPanel;
    private SettingsPanel settingsPanel;
    private JTabbedPane tabbedPane;
    private JMenuBar menuBar;
    
    private TimerController timerController;
    private SettingsController settingsController;
    private SystemTrayController trayController;
    
    // UIConstants에서 가져온 상수들 사용
    
    public MainFrame() {
        initializeModels();
        initializeControllers();
        applyInitialProfileSettings();  // 초기 프로필 설정 적용
        initializeUI();
        setupEventHandlers();
        
        // 알림 매니저에 현재 프로필 설정
        notificationManager.setCurrentProfile(settingsController.getCurrentProfile());
        
        // 시스템 트레이 설정
        if (SystemTray.isSupported()) {
            trayController.setupSystemTray();
        }
        
        System.out.println("✅ MainFrame 초기화 완료 - 현재 프로필: " + settingsController.getCurrentProfile().getProfileName());
    }
    
    /**
     * 모델 초기화
     */
    private void initializeModels() {
        timerModel = new TimerModel();
        notificationManager = new NotificationManager();
        dataManager = new JsonDataManager();
    }
    
    /**
     * 컨트롤러 초기화
     */
    private void initializeControllers() {
        timerController = new TimerController(timerModel, notificationManager);
        settingsController = new SettingsController(dataManager);
        trayController = new SystemTrayController(this, timerModel);
        
        // SettingsController와 TimerController 연결
        settingsController.setTimerController(timerController);
    }
    
    /**
     * 🎯 초기 프로필 설정 적용
     */
    private void applyInitialProfileSettings() {
        UserProfile currentProfile = settingsController.getCurrentProfile();
        if (currentProfile != null) {
            System.out.println("\n🚀 앱 시작 시 프로필 적용: " + currentProfile.getProfileName());
            
            // TimerModel에 프로필 설정 적용
            timerModel.setWorkMinutes(currentProfile.getWorkMinutes());
            timerModel.setBreakMinutes(currentProfile.getBreakMinutes());
            timerModel.setPomodoroMode(currentProfile.isPomodoroMode());
            
            // NotificationManager에 프로필 설정 적용
            notificationManager.setSoundEnabled(currentProfile.isSoundEnabled());
            notificationManager.setPopupEnabled(currentProfile.isPopupEnabled());
            notificationManager.setFlashEnabled(currentProfile.isFlashEnabled());
            notificationManager.setSnoozeMinutes(currentProfile.getSnoozeMinutes());
            notificationManager.setCurrentProfile(currentProfile);
            
            // TimerController에도 현재 프로필 설정
            timerController.setCurrentProfile(currentProfile);
            
            System.out.println("✅ 초기 프로필 적용 완료:");
            System.out.println("  - 작업시간: " + currentProfile.getWorkMinutes() + "분");
            System.out.println("  - 휴식시간: " + currentProfile.getBreakMinutes() + "분");
            System.out.println("  - 포모도로 모드: " + (currentProfile.isPomodoroMode() ? "활성" : "비활성"));
            System.out.println("  - 소리 알림: " + (currentProfile.isSoundEnabled() ? "활성" : "비활성"));
        } else {
            System.out.println("⚠️ 현재 프로필이 null입니다.");
        }
    }
    
    /**
     * UI 초기화
     */
    private void initializeUI() {
        setupWindowProperties();
        setupRendering();
        setupLookAndFeel();
        setupLayout();
    }
    
    /**
     * 창 속성 설정
     */
    private void setupWindowProperties() {
        setTitle(UIConstants.APP_TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(UIConstants.DEFAULT_WIDTH, UIConstants.DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(UIConstants.MIN_WIDTH, UIConstants.MIN_HEIGHT));
        setResizable(true);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());
        autoSizeForScreen();
    }
    
    /**
     * 렌더링 설정
     */
    private void setupRendering() {
        setupHighQualityRendering();
    }
    
    /**
     * Look and Feel 설정
     */
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setupUIColors();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            handleLookAndFeelError(e);
        }
    }
    
    /**
     * Look and Feel 오류 처리
     */
    private void handleLookAndFeelError(Exception e) {
        System.err.println(UIConstants.LOOK_AND_FEEL_ERROR_MESSAGE + e.getMessage());
    }
    
    /**
     * 레이아웃 설정
     */
    private void setupLayout() {
        createMenuBar();
        createTabbedPane();
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * 메뉴바 생성
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // 파일 메뉴
        JMenu fileMenu = new JMenu("파일");
        
        JMenuItem exitItem = new JMenuItem("종료");
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);
        
        // 보기 메뉴
        JMenu viewMenu = new JMenu("보기");
        
        JMenuItem alwaysOnTopItem = new JMenuItem("항상 위에");
        alwaysOnTopItem.addActionListener(e -> {
            setAlwaysOnTop(!isAlwaysOnTop());
            alwaysOnTopItem.setText(isAlwaysOnTop() ? "항상 위에 해제" : "항상 위에");
        });
        viewMenu.add(alwaysOnTopItem);
        
        JMenuItem minimizeToTrayItem = new JMenuItem("트레이로 최소화");
        minimizeToTrayItem.addActionListener(e -> minimizeToTray());
        viewMenu.add(minimizeToTrayItem);
        
        // 도움말 메뉴
        JMenu helpMenu = new JMenu("도움말");
        
        JMenuItem aboutItem = new JMenuItem("정보");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * 탭 패널 생성
     */
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        
        // 타이머 패널 - 설정 컨트롤러 전달
        timerPanel = new TimerPanel(timerModel, timerController, settingsController);
        tabbedPane.addTab("타이머", new ImageIcon(), timerPanel, "타이머 제어");
        
        // 설정 패널
        settingsPanel = new SettingsPanel(settingsController, notificationManager);
        tabbedPane.addTab("설정", new ImageIcon(), settingsPanel, "애플리케이션 설정");
        
        // 탭 변경 리스너
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == settingsPanel) {
                settingsPanel.refreshAllSettings();
            }
        });
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 창 닫기 이벤트
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (settingsController.getCurrentProfile().isMinimizeToTray() && SystemTray.isSupported()) {
                    minimizeToTray();
                } else {
                    exitApplication();
                }
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
                if (settingsController.getCurrentProfile().isMinimizeToTray() && SystemTray.isSupported()) {
                    minimizeToTray();
                }
            }
        });
        
        // 타이머 이벤트 리스너
        timerModel.addTimerListener(new TimerModel.TimerListener() {
            @Override
            public void onTimerTick(int remainingSeconds) {
                // 타이틀바에 남은 시간 표시
                if (timerModel.getState() == TimerModel.TimerState.RUNNING) {
                    setTitle(UIConstants.APP_TITLE + " - " + timerModel.getFormattedTime());
                } else {
                    setTitle(UIConstants.APP_TITLE);
                }
            }
            
            @Override
            public void onTimerComplete(TimerModel.NotificationType type) {
                // 알림 다이얼로그 표시
                SwingUtilities.invokeLater(() -> {
                    NotificationDialog dialog = new NotificationDialog(MainFrame.this, type, timerController);
                    dialog.setVisible(true);
                });
            }
            
            @Override
            public void onTimerStateChanged(TimerModel.TimerState state) {
                // 상태에 따라 UI 업데이트
                updateUIForState(state);
            }
        });
    }
    
    /**
     * 상태에 따른 UI 업데이트
     */
    private void updateUIForState(TimerModel.TimerState state) {
        SwingUtilities.invokeLater(() -> {
            switch (state) {
                case RUNNING:
                    if (trayController != null) {
                        trayController.updateTrayIcon("실행 중");
                    }
                    break;
                case PAUSED:
                    if (trayController != null) {
                        trayController.updateTrayIcon("일시정지");
                    }
                    break;
                case STOPPED:
                    setTitle(UIConstants.APP_TITLE);
                    if (trayController != null) {
                        trayController.updateTrayIcon("정지");
                    }
                    break;
            }
        });
    }
    
    /**
     * 트레이로 최소화
     */
    public void minimizeToTray() {
        if (SystemTray.isSupported() && trayController != null) {
            setVisible(false);
            trayController.showTrayMessage(UIConstants.APP_TITLE, UIConstants.TRAY_MINIMIZE_MESSAGE);
        }
    }
    
    /**
     * 트레이에서 복원
     */
    public void restoreFromTray() {
        setVisible(true);
        setState(Frame.NORMAL);
        toFront();
        requestFocus();
    }
    
    /**
     * 애플리케이션 종료
     */
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "정말로 종료하시겠습니까?",
            "종료 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // 설정 저장
            settingsController.forceSave();
            
            // 타이머 정지
            if (timerModel.getState() == TimerModel.TimerState.RUNNING) {
                timerModel.stopTimer();
            }
            
            // 패널 정리
            if (timerPanel != null) {
                timerPanel.cleanup();
            }
            if (settingsPanel != null) {
                settingsPanel.cleanup();
            }
            
            // 설정 컨트롤러 종료
            if (settingsController != null) {
                settingsController.shutdown();
            }
            
            // 시스템 트레이 정리
            if (trayController != null) {
                trayController.cleanup();
            }
            
            System.exit(0);
        }
    }
    
    /**
     * 정보 다이얼로그 표시
     */
    private void showAboutDialog() {
        String message = "<html>" +
                "<h2>Code ∧ Break</h2>" +
                "<p>버전: 1.0</p>" +
                "<p>프로그래머를 위한 건강한 코딩 습관 도구</p>" +
                "<br>" +
                "<p>개발자: 조민형</p>" +
                "<p>2025년 Term Project</p>" +
                "</html>";
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Code ∧ Break 정보",
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(createAppIcon())
        );
    }
    
    /**
     * 애플리케이션 아이콘 생성
     */
    private Image createAppIcon() {
        // 간단한 아이콘 생성
        Image icon = new BufferedImage(UIConstants.APP_ICON_SIZE, UIConstants.APP_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = ((BufferedImage) icon).createGraphics();
        
        // 안티얼라이싱 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 배경 (원형)
        g2d.setColor(new Color(UIConstants.MAIN_COLOR_RGB[0], UIConstants.MAIN_COLOR_RGB[1], UIConstants.MAIN_COLOR_RGB[2]));
        int iconSize = UIConstants.APP_ICON_SIZE - (UIConstants.ICON_PADDING * 2);
        g2d.fillOval(UIConstants.ICON_PADDING, UIConstants.ICON_PADDING, iconSize, iconSize);
        
        // 테두리
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(UIConstants.ICON_BORDER_WIDTH));
        g2d.drawOval(UIConstants.ICON_PADDING, UIConstants.ICON_PADDING, iconSize, iconSize);
        
        // 시계 바늘 (간단하게)
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(UIConstants.ICON_BORDER_WIDTH));
        int center = UIConstants.APP_ICON_SIZE / 2;
        g2d.drawLine(center, center, center, center - 8);  // 시침
        g2d.drawLine(center, center, center + 6, center); // 분침
        
        g2d.dispose();
        return icon;
    }
    
    /**
     * UI 색상 설정으로 가독성 개선
     */
    private void setupUIColors() {
        // 기본 색상 정의
        Color backgroundColor = Color.WHITE;
        Color textColor = Color.BLACK;
        Color selectionColor = new Color(184, 207, 229);
        
        // 기본 컴포넌트 색상 설정
        UIManager.put("Panel.background", backgroundColor);
        UIManager.put("Panel.foreground", textColor);
        
        // 라벨 색상
        UIManager.put("Label.background", backgroundColor);
        UIManager.put("Label.foreground", textColor);
        
        // 버튼 색상 (더 강력하게 설정)
        UIManager.put("Button.background", new Color(245, 245, 245));
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.select", selectionColor);
        UIManager.put("Button.focus", selectionColor);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder());
        UIManager.put("Button.disabledText", new Color(128, 128, 128));
        
        // 버튼 텍스트 강제 설정
        UIManager.put("Button.textForeground", Color.BLACK);
        UIManager.put("Button.textShiftOffset", 0);
        
        // 텍스트 필드 색상
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", textColor);
        UIManager.put("TextField.selectionBackground", selectionColor);
        UIManager.put("TextField.selectionForeground", textColor);
        
        // 콤보박스 색상
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", textColor);
        UIManager.put("ComboBox.selectionBackground", selectionColor);
        UIManager.put("ComboBox.selectionForeground", textColor);
        
        // 체크박스 색상
        UIManager.put("CheckBox.background", backgroundColor);
        UIManager.put("CheckBox.foreground", textColor);
        
        // 탭 패널 색상
        UIManager.put("TabbedPane.background", backgroundColor);
        UIManager.put("TabbedPane.foreground", textColor);
        UIManager.put("TabbedPane.selected", selectionColor);
        UIManager.put("TabbedPane.selectedForeground", textColor);
        
        // 메뉴 색상
        UIManager.put("Menu.background", backgroundColor);
        UIManager.put("Menu.foreground", textColor);
        UIManager.put("MenuItem.background", backgroundColor);
        UIManager.put("MenuItem.foreground", textColor);
        UIManager.put("MenuItem.selectionBackground", selectionColor);
        UIManager.put("MenuItem.selectionForeground", textColor);
        
        // 폰트 크기 설정
        Font defaultFont = new Font("Dialog", Font.PLAIN, (int)UIConstants.DEFAULT_FONT_SIZE);
        Font labelFont = new Font("Dialog", Font.PLAIN, (int)UIConstants.LABEL_FONT_SIZE);
        Font buttonFont = new Font("Dialog", Font.BOLD, (int)UIConstants.BUTTON_FONT_SIZE);
        
        UIManager.put("Label.font", labelFont);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("CheckBox.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);
        UIManager.put("Menu.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
    }
    
    /**
     * 고품질 렌더링 설정
     */
    private void setupHighQualityRendering() {
        // 폰트 안티에일리어싱 설정
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.dpiaware", "true");
        
        // 렌더링 품질 개선
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        System.setProperty("swing.useSystemFontSettings", "true");
    }
    
    /**
     * 화면 크기에 따른 자동 조정
     */
    private void autoSizeForScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // 화면이 작은 경우 조정
        if (screenSize.width < UIConstants.DEFAULT_WIDTH || screenSize.height < UIConstants.DEFAULT_HEIGHT) {
            int width = Math.min(UIConstants.DEFAULT_WIDTH, (int)(screenSize.width * UIConstants.SCREEN_SIZE_RATIO));
            int height = Math.min(UIConstants.DEFAULT_HEIGHT, (int)(screenSize.height * UIConstants.SCREEN_SIZE_RATIO));
            setSize(width, height);
        }
        
        // 화면이 충분히 큰 경우 선택적으로 최대화
        if (screenSize.width >= UIConstants.LARGE_SCREEN_WIDTH_THRESHOLD && screenSize.height >= UIConstants.LARGE_SCREEN_HEIGHT_THRESHOLD) {
            // 대화면에서는 더 큰 기본 크기 사용
            setSize(Math.min(UIConstants.LARGE_SCREEN_MAX_WIDTH, screenSize.width - UIConstants.SCREEN_MARGIN), 
                   Math.min(UIConstants.LARGE_SCREEN_MAX_HEIGHT, screenSize.height - UIConstants.SCREEN_MARGIN));
        }
    }
    
    // Getters
    public TimerModel getTimerModel() { return timerModel; }
    public NotificationManager getNotificationManager() { return notificationManager; }
    public JsonDataManager getDataManager() { return dataManager; }
    public TimerController getTimerController() { return timerController; }
    public SettingsController getSettingsController() { return settingsController; }
}
