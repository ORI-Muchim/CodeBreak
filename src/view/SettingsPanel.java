package view;

import model.*;
import controller.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 🚀 향상된 설정 패널 - 실시간 동기화와 자동 저장 지원
 */
public class SettingsPanel extends JPanel {
    private SettingsController settingsController;
    private NotificationManager notificationManager;
    
    // UI 컴포넌트들
    private JComboBox<UserProfile> profileComboBox;
    private JButton newProfileButton;
    private JButton deleteProfileButton;
    private JButton duplicateProfileButton;
    private JTextField profileNameField;
    
    // 타이머 설정
    private JSpinner workMinutesSpinner;
    private JSpinner breakMinutesSpinner;
    private JCheckBox pomodoroModeCheckBox;
    
    // 알림 설정
    private JCheckBox soundEnabledCheckBox;
    private JCheckBox popupEnabledCheckBox;
    private JCheckBox flashEnabledCheckBox;
    private JSpinner snoozeMinutesSpinner;
    
    // 알림 유형 설정
    private JCheckBox[] notificationTypeCheckBoxes;
    
    // 일반 설정
    private JCheckBox autoStartCheckBox;
    private JCheckBox minimizeToTrayCheckBox;
    private JCheckBox alwaysOnTopCheckBox;
    
    // 상태 표시
    private JLabel statusLabel;
    private JProgressBar saveProgressBar;
    
    // 이벤트 처리
    private boolean isUpdating = false;
    private ScheduledExecutorService uiUpdateScheduler;
    
    public SettingsPanel(SettingsController settingsController, NotificationManager notificationManager) {
        this.settingsController = settingsController;
        this.notificationManager = notificationManager;
        
        initializeUI();
        setupEventHandlers();
        startUIUpdateScheduler();
        refreshAllSettings();
        
        System.out.println("✅ SettingsPanel 초기화 완료");
    }
    
    /**
     * 🎨 UI 초기화
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 249, 250));
        
        // 상단: 상태 패널
        add(createStatusPanel(), BorderLayout.NORTH);
        
        // 중앙: 탭 패널
        JTabbedPane tabbedPane = createTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
        
        // 하단: 액션 패널
        add(createActionPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * 📊 상태 패널 생성
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout(10, 5));
        statusPanel.setBackground(new Color(248, 249, 250));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        statusLabel = new JLabel("✅ 설정이 동기화됨");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        statusLabel.setForeground(Color.BLACK);
        
        saveProgressBar = new JProgressBar();
        saveProgressBar.setIndeterminate(false);
        saveProgressBar.setStringPainted(true);
        saveProgressBar.setString("저장됨");
        saveProgressBar.setValue(100);
        saveProgressBar.setPreferredSize(new Dimension(120, 20));
        
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(saveProgressBar, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    /**
     * 📑 탭 패널 생성
     */
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        tabbedPane.addTab("프로필", createProfilePanel());
        tabbedPane.addTab("타이머", createTimerPanel());
        tabbedPane.addTab("알림", createNotificationPanel());
        tabbedPane.addTab("일반", createGeneralPanel());
        tabbedPane.addTab("테스트", createTestPanel());
        
        return tabbedPane;
    }
    
    /**
     * 👤 프로필 패널 생성
     */
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 프로필 선택 영역
        JPanel selectionPanel = createTitledPanel("프로필 관리", new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JLabel profileLabel = new JLabel("현재 프로필:");
        profileLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        selectionPanel.add(profileLabel);
        
        profileComboBox = new JComboBox<>();
        profileComboBox.setPreferredSize(new Dimension(200, 30));
        profileComboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        selectionPanel.add(profileComboBox);
        
        // 프로필 관리 버튼들
        newProfileButton = createStyledButton("새 프로필", new Color(52, 152, 219), Color.BLACK);
        duplicateProfileButton = createStyledButton("복제", new Color(155, 89, 182), Color.BLACK);
        deleteProfileButton = createStyledButton("삭제", new Color(231, 76, 60), Color.BLACK);
        
        selectionPanel.add(newProfileButton);
        selectionPanel.add(duplicateProfileButton);
        selectionPanel.add(deleteProfileButton);
        
        // 프로필 정보 영역
        JPanel infoPanel = createTitledPanel("프로필 정보", new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 프로필 이름 변경
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("프로필 이름:"), gbc);
        
        gbc.gridx = 1;
        profileNameField = new JTextField(15);
        profileNameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        infoPanel.add(profileNameField, gbc);
        
        // 프리셋 버튼들
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        presetPanel.setBackground(Color.WHITE);
        
        // JButton pomodoroBtn = createStyledButton("포모도로 (25/5)", new Color(46, 204, 113), Color.WHITE);
        // JButton longWorkBtn = createStyledButton("장시간 (60/10)", new Color(52, 152, 219), Color.WHITE);
        // JButton shortFocusBtn = createStyledButton("단시간 (15/3)", new Color(241, 196, 15), Color.BLACK);
        
        // pomodoroBtn.addActionListener(e -> loadPreset(25, 5, true));
        // longWorkBtn.addActionListener(e -> loadPreset(60, 10, false));
        // shortFocusBtn.addActionListener(e -> loadPreset(15, 3, true));
        
        // presetPanel.add(pomodoroBtn);
        // presetPanel.add(longWorkBtn);
        // presetPanel.add(shortFocusBtn);
        
        infoPanel.add(presetPanel, gbc);
        
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * ⏰ 타이머 패널 생성
     */
    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 작업 시간 설정
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel workLabel = new JLabel("작업 시간 (분):");
        workLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(workLabel, gbc);
        
        gbc.gridx = 1;
        workMinutesSpinner = new JSpinner(new SpinnerNumberModel(25, 1, 999, 1));
        workMinutesSpinner.setPreferredSize(new Dimension(80, 30));
        workMinutesSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        panel.add(workMinutesSpinner, gbc);
        
        // 휴식 시간 설정
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel breakLabel = new JLabel("휴식 시간 (분):");
        breakLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(breakLabel, gbc);
        
        gbc.gridx = 1;
        breakMinutesSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 999, 1));
        breakMinutesSpinner.setPreferredSize(new Dimension(80, 30));
        breakMinutesSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        panel.add(breakMinutesSpinner, gbc);
        
        // 포모도로 모드
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        pomodoroModeCheckBox = new JCheckBox("포모도로 모드 (작업-휴식 반복)");
        pomodoroModeCheckBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        pomodoroModeCheckBox.setBackground(Color.WHITE);
        panel.add(pomodoroModeCheckBox, gbc);
        
        return panel;
    }
    
    /**
     * 🔔 알림 패널 생성
     */
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 기본 알림 설정
        JPanel basicPanel = createTitledPanel("기본 알림 설정", new GridLayout(4, 1, 5, 10));
        
        soundEnabledCheckBox = new JCheckBox("🔊 소리 알림");
        popupEnabledCheckBox = new JCheckBox("💬 팝업 알림");
        flashEnabledCheckBox = new JCheckBox("⚡ 화면 깜빡임");
        
        styleCheckBox(soundEnabledCheckBox);
        styleCheckBox(popupEnabledCheckBox);
        styleCheckBox(flashEnabledCheckBox);
        
        JPanel snoozePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        snoozePanel.setBackground(Color.WHITE);
        snoozePanel.add(new JLabel("⏰ 스누즈 시간 (분):"));
        snoozeMinutesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        snoozeMinutesSpinner.setPreferredSize(new Dimension(60, 25));
        snoozePanel.add(snoozeMinutesSpinner);
        
        basicPanel.add(soundEnabledCheckBox);
        basicPanel.add(popupEnabledCheckBox);
        basicPanel.add(flashEnabledCheckBox);
        basicPanel.add(snoozePanel);
        
        // 알림 유형 설정
        JPanel typePanel = createTitledPanel("알림 유형 설정", new GridLayout(0, 1, 5, 8));
        
        TimerModel.NotificationType[] types = TimerModel.NotificationType.values();
        notificationTypeCheckBoxes = new JCheckBox[types.length];
        
        for (int i = 0; i < types.length; i++) {
            JPanel typeItemPanel = new JPanel(new BorderLayout(10, 5));
            typeItemPanel.setBackground(Color.WHITE);
            
            notificationTypeCheckBoxes[i] = new JCheckBox(types[i].getName(), true);
            styleCheckBox(notificationTypeCheckBoxes[i]);
            
            JLabel descLabel = new JLabel(types[i].getMessage());
            descLabel.setForeground(Color.BLACK);
            descLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
            
            typeItemPanel.add(notificationTypeCheckBoxes[i], BorderLayout.WEST);
            typeItemPanel.add(descLabel, BorderLayout.CENTER);
            
            typePanel.add(typeItemPanel);
        }
        
        panel.add(basicPanel, BorderLayout.NORTH);
        panel.add(typePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * ⚙️ 일반 패널 생성
     */
    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 시작 설정
        gbc.gridx = 0; gbc.gridy = 0;
        JPanel startupPanel = createTitledPanel("시작 설정", new GridLayout(2, 1, 5, 10));
        
        autoStartCheckBox = new JCheckBox("🚀 애플리케이션 시작 시 타이머 자동 시작");
        minimizeToTrayCheckBox = new JCheckBox("📱 시스템 트레이로 최소화");
        
        styleCheckBox(autoStartCheckBox);
        styleCheckBox(minimizeToTrayCheckBox);
        
        startupPanel.add(autoStartCheckBox);
        startupPanel.add(minimizeToTrayCheckBox);
        panel.add(startupPanel, gbc);
        
        // 창 설정
        gbc.gridy = 1;
        JPanel windowPanel = createTitledPanel("창 설정", new GridLayout(1, 1, 5, 10));
        
        alwaysOnTopCheckBox = new JCheckBox("📌 항상 맨 위에 표시");
        styleCheckBox(alwaysOnTopCheckBox);
        windowPanel.add(alwaysOnTopCheckBox);
        panel.add(windowPanel, gbc);
        
        // 데이터 관리
        gbc.gridy = 2;
        JPanel dataPanel = createTitledPanel("데이터 관리", new GridLayout(2, 1, 5, 10));
        
        // 첫 번째 줄: 내보내기/불러오기
        JPanel ioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        ioPanel.setBackground(Color.WHITE);
        
        JButton exportButton = createStyledButton("📤 내보내기", new Color(52, 152, 219), Color.BLACK);
        JButton importButton = createStyledButton("📥 불러오기", new Color(46, 204, 113), Color.BLACK);
        
        exportButton.addActionListener(e -> exportSettings());
        importButton.addActionListener(e -> importSettings());
        
        ioPanel.add(exportButton);
        ioPanel.add(importButton);
        
        // 두 번째 줄: 관리 기능
        JPanel managePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        managePanel.setBackground(Color.WHITE);
        
        JButton resetButton = createStyledButton("🔄 초기화", new Color(231, 76, 60), Color.BLACK);
        JButton statusButton = createStyledButton("📊 상태 확인", new Color(155, 89, 182), Color.BLACK);
        
        resetButton.addActionListener(e -> resetSettings());
        statusButton.addActionListener(e -> showStatus());
        
        managePanel.add(resetButton);
        managePanel.add(statusButton);
        
        dataPanel.add(ioPanel);
        dataPanel.add(managePanel);
        panel.add(dataPanel, gbc);
        
        return panel;
    }
    
    /**
     * 🧪 테스트 패널 생성
     */
    private JPanel createTestPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JPanel buttonPanel = createTitledPanel("알림 테스트", new GridLayout(0, 2, 10, 10));
        
        // 각 알림 유형별 테스트 버튼
        for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
            JButton testButton = createStyledButton(
                type.getName() + " 테스트", 
                new Color(46, 204, 113), 
                Color.BLACK
            );
            testButton.addActionListener(e -> testNotification(type));
            buttonPanel.add(testButton);
        }
        
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    /**
     * ⚡ 액션 패널 생성
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JButton saveButton = createStyledButton("💾 즉시 저장", new Color(46, 204, 113), Color.BLACK);
        JButton reloadButton = createStyledButton("🔄 새로고침", new Color(52, 152, 219), Color.BLACK);
        
        saveButton.addActionListener(e -> forceSave());
        reloadButton.addActionListener(e -> refreshAllSettings());
        
        panel.add(reloadButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    /**
     * 🎨 스타일 유틸리티 메서드들
     */
    private JPanel createTitledPanel(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 12),
                Color.BLACK
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 10, 30));
        
        // 호버 효과
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        checkBox.setBackground(Color.WHITE);
        checkBox.setForeground(Color.BLACK);
        checkBox.setFocusPainted(false);
    }
    
    /**
     * 🔧 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 프로필 선택 변경
        profileComboBox.addActionListener(e -> {
            if (!isUpdating) {
                UserProfile selected = (UserProfile) profileComboBox.getSelectedItem();
                if (selected != null) {
                    settingsController.setCurrentProfile(selected);
                    refreshCurrentProfileUI();
                }
            }
        });
        
        // 프로필 이름 변경
        profileNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateProfileName();
            }
        });
        
        profileNameField.addActionListener(e -> updateProfileName());
        
        // 프로필 관리 버튼들
        newProfileButton.addActionListener(e -> createNewProfile());
        duplicateProfileButton.addActionListener(e -> duplicateCurrentProfile());
        deleteProfileButton.addActionListener(e -> deleteCurrentProfile());
        
        // 타이머 설정 변경 (즉시 저장 및 즉시 UI 업데이트 추가)
        workMinutesSpinner.addChangeListener(e -> {
            if (!isUpdating) {
                settingsController.updateCurrentProfileSetting("workMinutes", workMinutesSpinner.getValue());
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        breakMinutesSpinner.addChangeListener(e -> {
            if (!isUpdating) {
                settingsController.updateCurrentProfileSetting("breakMinutes", breakMinutesSpinner.getValue());
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        pomodoroModeCheckBox.addActionListener(e -> {
            if (!isUpdating) {
                settingsController.updateCurrentProfileSetting("pomodoroMode", pomodoroModeCheckBox.isSelected());
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        // 알림 설정 변경 (즉시 저장 및 즉시 UI 업데이트 추가)
        soundEnabledCheckBox.addActionListener(e -> {
            if (!isUpdating) {
                updateNotificationSettings();
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        popupEnabledCheckBox.addActionListener(e -> {
            if (!isUpdating) {
                updateNotificationSettings();
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        flashEnabledCheckBox.addActionListener(e -> {
            if (!isUpdating) {
                updateNotificationSettings();
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        snoozeMinutesSpinner.addChangeListener(e -> {
            if (!isUpdating) {
                updateNotificationSettings();
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        // 알림 유형 체크박스들
        if (notificationTypeCheckBoxes != null) {
            for (int i = 0; i < notificationTypeCheckBoxes.length; i++) {
                final int index = i;
                notificationTypeCheckBoxes[i].addActionListener(e -> {
                    if (!isUpdating) {
                        updateNotificationTypes();
                        // 즉시 저장 추가
                        settingsController.forceSave();
                        // 즉시 UI 업데이트
                        SwingUtilities.invokeLater(this::updateStatus);
                    }
                });
            }
        }
        
        // 일반 설정 변경 (즉시 저장 및 즉시 UI 업데이트 추가)
        autoStartCheckBox.addActionListener(e -> {
            if (!isUpdating) {
                settingsController.updateCurrentProfileSetting("autoStart", autoStartCheckBox.isSelected());
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        minimizeToTrayCheckBox.addActionListener(e -> {
            if (!isUpdating) {
                settingsController.updateCurrentProfileSetting("minimizeToTray", minimizeToTrayCheckBox.isSelected());
                // 즉시 저장 추가
                settingsController.forceSave();
                // 즉시 UI 업데이트
                SwingUtilities.invokeLater(this::updateStatus);
            }
        });
        
        alwaysOnTopCheckBox.addActionListener(e -> {
            if (!isUpdating) {
                updateAlwaysOnTop();
            }
        });
    }
    
    /**
     * ⏰ UI 업데이트 스케줄러 시작
     */
    private void startUIUpdateScheduler() {
        uiUpdateScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "UI-Update-Thread");
            t.setDaemon(true);
            return t;
        });
        
        // 주기적으로 상태 업데이트 (더 빠른 주기로 변경: 0.5초 -> 0.1초)
        uiUpdateScheduler.scheduleAtFixedRate(this::updateStatus, 50, 100, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 🔄 모든 설정 새로고침
     */
    public void refreshAllSettings() {
        isUpdating = true;
        
        try {
            refreshProfileList();
            refreshCurrentProfileUI();
            refreshNotificationSettings();
            refreshGeneralSettings();
            updateStatus();
            
        } catch (Exception e) {
            System.err.println("❌ 설정 새로고침 실패: " + e.getMessage());
        } finally {
            isUpdating = false;
        }
    }
    
    /**
     * 👤 프로필 목록 새로고침
     */
    private void refreshProfileList() {
        profileComboBox.removeAllItems();
        List<UserProfile> profiles = settingsController.getAllProfiles();
        
        for (UserProfile profile : profiles) {
            profileComboBox.addItem(profile);
        }
        
        profileComboBox.setSelectedItem(settingsController.getCurrentProfile());
    }
    
    /**
     * 👤 현재 프로필 UI 새로고침
     */
    private void refreshCurrentProfileUI() {
        UserProfile profile = settingsController.getCurrentProfileWithPendingChanges();
        if (profile == null) return;
        
        profileNameField.setText(profile.getProfileName());
        workMinutesSpinner.setValue(profile.getWorkMinutes());
        breakMinutesSpinner.setValue(profile.getBreakMinutes());
        pomodoroModeCheckBox.setSelected(profile.isPomodoroMode());
    }
    
    /**
     * 🔔 알림 설정 새로고침
     */
    private void refreshNotificationSettings() {
        UserProfile profile = settingsController.getCurrentProfileWithPendingChanges();
        if (profile == null) return;
        
        soundEnabledCheckBox.setSelected(profile.isSoundEnabled());
        popupEnabledCheckBox.setSelected(profile.isPopupEnabled());
        flashEnabledCheckBox.setSelected(profile.isFlashEnabled());
        snoozeMinutesSpinner.setValue(profile.getSnoozeMinutes());
        
        // 알림 유형 체크박스 업데이트
        if (notificationTypeCheckBoxes != null) {
            TimerModel.NotificationType[] types = TimerModel.NotificationType.values();
            for (int i = 0; i < types.length && i < notificationTypeCheckBoxes.length; i++) {
                boolean enabled = profile.isNotificationEnabled(types[i]);
                notificationTypeCheckBoxes[i].setSelected(enabled);
            }
        }
    }
    
    /**
     * ⚙️ 일반 설정 새로고침
     */
    private void refreshGeneralSettings() {
        UserProfile profile = settingsController.getCurrentProfileWithPendingChanges();
        if (profile == null) return;
        
        autoStartCheckBox.setSelected(profile.isAutoStart());
        minimizeToTrayCheckBox.setSelected(profile.isMinimizeToTray());
        
        // 항상 위에 표시는 현재 창 상태에서 가져오기
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            alwaysOnTopCheckBox.setSelected(((JFrame) window).isAlwaysOnTop());
        }
    }
    
    /**
     * 📊 상태 업데이트 (디버깅 로그 추가 및 개선된 반응성)
     */
    private void updateStatus() {
        SwingUtilities.invokeLater(() -> {
            boolean hasChanges = settingsController.hasUnsavedChanges();
            
            // 디버깅 로그 추가
            if (hasChanges) {
                System.out.println("🔍 상태 체크: 저장 진행 중...");
            }
            
            if (hasChanges) {
                statusLabel.setText("⏳ 변경사항 저장 중...");
                statusLabel.setForeground(Color.BLACK);
                saveProgressBar.setIndeterminate(true);
                saveProgressBar.setString("저장 중");
            } else {
                statusLabel.setText("✅ 모든 변경사항 저장됨");
                statusLabel.setForeground(Color.BLACK);
                saveProgressBar.setIndeterminate(false);
                saveProgressBar.setValue(100);
                saveProgressBar.setString("저장됨");
            }
        });
    }
    
    /**
     * 📝 프로필 이름 업데이트
     */
    private void updateProfileName() {
        String newName = profileNameField.getText().trim();
        UserProfile current = settingsController.getCurrentProfile();
        
        if (!newName.isEmpty() && !newName.equals(current.getProfileName())) {
            // 중복 체크
            boolean nameExists = settingsController.getAllProfiles().stream()
                .anyMatch(p -> p != current && p.getProfileName().equals(newName));
            
            if (nameExists) {
                JOptionPane.showMessageDialog(this, 
                    "같은 이름의 프로필이 이미 존재합니다: " + newName, 
                    "중복된 이름", JOptionPane.WARNING_MESSAGE);
                profileNameField.setText(current.getProfileName());
            } else {
                current.setProfileName(newName);
                settingsController.forceSave();
                refreshProfileList();
                updateStatus();
            }
        }
    }
    
    /**
     * 🔔 알림 설정 업데이트
     */
    private void updateNotificationSettings() {
        settingsController.updateCurrentProfileSetting("soundEnabled", soundEnabledCheckBox.isSelected());
        settingsController.updateCurrentProfileSetting("popupEnabled", popupEnabledCheckBox.isSelected());
        settingsController.updateCurrentProfileSetting("flashEnabled", flashEnabledCheckBox.isSelected());
        settingsController.updateCurrentProfileSetting("snoozeMinutes", snoozeMinutesSpinner.getValue());
        
        // NotificationManager에도 즉시 반영
        if (notificationManager != null) {
            notificationManager.setSoundEnabled(soundEnabledCheckBox.isSelected());
            notificationManager.setPopupEnabled(popupEnabledCheckBox.isSelected());
            notificationManager.setFlashEnabled(flashEnabledCheckBox.isSelected());
            notificationManager.setSnoozeMinutes((Integer) snoozeMinutesSpinner.getValue());
        }
        
        // updateStatus() 호출 제거 - 이미 이벤트 핸들러에서 처리함
    }
    
    /**
     * 🔔 알림 유형 업데이트
     */
    private void updateNotificationTypes() {
        if (notificationTypeCheckBoxes == null) return;
        
        TimerModel.NotificationType[] types = TimerModel.NotificationType.values();
        
        for (int i = 0; i < types.length && i < notificationTypeCheckBoxes.length; i++) {
            boolean enabled = notificationTypeCheckBoxes[i].isSelected();
            settingsController.updateNotificationSetting(types[i], enabled);
        }
        
        // updateStatus() 호출 제거 - 이미 이벤트 핸들러에서 처리함
    }
    
    /**
     * 📌 항상 위에 표시 업데이트
     */
    private void updateAlwaysOnTop() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            ((JFrame) window).setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
        }
    }
    
    /**
     * 🎯 프리셋 로드
     */
    private void loadPreset(int workMinutes, int breakMinutes, boolean pomodoroMode) {
        workMinutesSpinner.setValue(workMinutes);
        breakMinutesSpinner.setValue(breakMinutes);
        pomodoroModeCheckBox.setSelected(pomodoroMode);
        
        // 설정 즉시 적용
        settingsController.updateCurrentProfileSetting("workMinutes", workMinutes);
        settingsController.updateCurrentProfileSetting("breakMinutes", breakMinutes);
        settingsController.updateCurrentProfileSetting("pomodoroMode", pomodoroMode);
        
        updateStatus();
    }
    
    /**
     * ➕ 새 프로필 생성
     */
    private void createNewProfile() {
        String name = JOptionPane.showInputDialog(this, 
            "새 프로필 이름을 입력하세요:", 
            "새 프로필 생성", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (name != null && !name.trim().isEmpty()) {
            UserProfile newProfile = settingsController.addProfile(name.trim());
            if (newProfile != null) {
                settingsController.setCurrentProfile(newProfile);
                refreshAllSettings();
                JOptionPane.showMessageDialog(this, 
                    "새 프로필 '" + name + "'이 생성되었습니다.", 
                    "생성 완료", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "프로필 생성에 실패했습니다. 이름이 중복되었을 수 있습니다.", 
                    "생성 실패", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 📋 현재 프로필 복제
     */
    private void duplicateCurrentProfile() {
        UserProfile current = settingsController.getCurrentProfile();
        String newName = JOptionPane.showInputDialog(this, 
            "복제된 프로필의 이름을 입력하세요:", 
            "프로필 복제", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (newName != null && !newName.trim().isEmpty()) {
            UserProfile duplicated = settingsController.duplicateProfile(current.getProfileName(), newName.trim());
            if (duplicated != null) {
                settingsController.setCurrentProfile(duplicated);
                refreshAllSettings();
                JOptionPane.showMessageDialog(this, 
                    "프로필 '" + newName + "'이 복제되었습니다.", 
                    "복제 완료", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "프로필 복제에 실패했습니다.", 
                    "복제 실패", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 🗑️ 현재 프로필 삭제
     */
    private void deleteCurrentProfile() {
        if (settingsController.getAllProfiles().size() <= 1) {
            JOptionPane.showMessageDialog(this, 
                "마지막 프로필은 삭제할 수 없습니다.", 
                "삭제 불가", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        UserProfile current = settingsController.getCurrentProfile();
        int option = JOptionPane.showConfirmDialog(this, 
            "프로필 '" + current.getProfileName() + "'을(를) 삭제하시겠습니까?", 
            "프로필 삭제", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            boolean success = settingsController.deleteProfile(current.getProfileName());
            if (success) {
                refreshAllSettings();
                JOptionPane.showMessageDialog(this, 
                    "프로필이 삭제되었습니다.", 
                    "삭제 완료", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "프로필 삭제에 실패했습니다.", 
                    "삭제 실패", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 🧪 알림 테스트
     */
    private void testNotification(TimerModel.NotificationType type) {
        if (notificationManager != null) {
            notificationManager.showNotification(type);
        }
    }
    
    /**
     * 💾 강제 저장 (개선된 버전)
     */
    private void forceSave() {
        System.out.println("💾 강제 저장 시작...");
        settingsController.forceSave();
        
        // 저장 후 즉시 상태 업데이트 (지연 없이)
        SwingUtilities.invokeLater(() -> {
            updateStatus();
            System.out.println("✅ 강제 저장 완료");
        });
        
        JOptionPane.showMessageDialog(this, 
            "모든 설정이 즉시 저장되었습니다.", 
            "저장 완료", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 📊 상태 확인
     */
    private void showStatus() {
        settingsController.printStatus();
        
        StringBuilder info = new StringBuilder();
        info.append("현재 프로필: ").append(settingsController.getCurrentProfile().getProfileName()).append("\n");
        info.append("총 프로필 수: ").append(settingsController.getAllProfiles().size()).append("\n");
        info.append("저장되지 않은 변경사항: ").append(settingsController.hasUnsavedChanges() ? "있음" : "없음").append("\n");
        info.append("\n프로필 목록:\n");
        
        for (UserProfile profile : settingsController.getAllProfiles()) {
            info.append("  - ").append(profile.getProfileName())
                .append(" (작업: ").append(profile.getWorkMinutes())
                .append("분, 휴식: ").append(profile.getBreakMinutes()).append("분)\n");
        }
        
        JOptionPane.showMessageDialog(this, info.toString(), "상태 정보", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 🔄 설정 초기화
     */
    private void resetSettings() {
        int option = JOptionPane.showConfirmDialog(this, 
            "모든 설정을 초기화하시겠습니까?\n이 작업은 되돌릴 수 없습니다.", 
            "설정 초기화", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            settingsController.resetToDefaults();
            refreshAllSettings();
            JOptionPane.showMessageDialog(this, 
                "모든 설정이 초기화되었습니다.", 
                "초기화 완료", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * 📤 설정 내보내기 (향상된 버전)
     */
    private void exportSettings() {
        // 형식 선택 대화상자
        String[] options = {"JSON 형식 (권장)", "텍스트 형식 (읽기 전용)", "취소"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "어떤 형식으로 내보내시겠습니까?\n\n" +
            "• JSON 형식: 나중에 다시 불러올 수 있습니다 (권장)\n" +
            "• 텍스트 형식: 사람이 읽기 쉽지만 불러오기 제한적",
            "내보내기 형식 선택",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
            return; // 취소
        }
        
        boolean isJsonFormat = (choice == 0);
        String extension = isJsonFormat ? ".json" : ".txt";
        String defaultFileName = "codebreak_profiles_" + 
            new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + extension;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("프로필 내보내기");
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));
        
        // 파일 필터 설정
        if (isJsonFormat) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "JSON 파일 (*.json)", "json"));
        } else {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "텍스트 파일 (*.txt)", "txt"));
        }
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                
                // 확장자 자동 추가
                if (!filePath.toLowerCase().endsWith(extension)) {
                    filePath += extension;
                }
                
                if (isJsonFormat) {
                    settingsController.exportProfilesToJsonFile(filePath);
                } else {
                    settingsController.exportProfilesToTextFile(filePath);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "프로필이 성공적으로 내보내졌습니다.\n\n" +
                    "파일: " + filePath + "\n" +
                    "프로필 수: " + settingsController.getAllProfiles().size() + "개", 
                    "내보내기 완료", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "프로필 내보내기 실패:\n" + e.getMessage(), 
                    "오류", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 📥 설정 불러오기
     */
    private void importSettings() {
        // 불러오기 모드 선택
        String[] modes = {"기존 프로필 유지하고 추가", "모든 프로필 교체", "취소"};
        int mode = JOptionPane.showOptionDialog(
            this,
            "어떤 방식으로 불러오시겠습니까?\n\n" +
            "• 기존 프로필 유지하고 추가: 현재 프로필들을 그대로 두고 새 프로필들을 추가합니다\n" +
            "• 모든 프로필 교체: 현재 프로필들을 모두 삭제하고 불러온 프로필들로 교체합니다",
            "불러오기 모드 선택",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            modes,
            modes[0]
        );
        
        if (mode == 2 || mode == JOptionPane.CLOSED_OPTION) {
            return; // 취소
        }
        
        boolean isAddMode = (mode == 0);
        
        // 파일 선택
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("프로필 불러오기");
        
        // 파일 필터 설정
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "JSON 파일 (*.json)", "json"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "텍스트 파일 (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "모든 지원 파일 (*.json, *.txt)", "json", "txt"));
        fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[2]); // 모든 지원 파일을 기본으로
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                
                // 확인 메시지 (교체 모드인 경우)
                if (!isAddMode) {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "정말로 현재 모든 프로필을 삭제하고 새 프로필들로 교체하시겠습니까?\n\n" +
                        "현재 프로필 수: " + settingsController.getAllProfiles().size() + "개\n" +
                        "이 작업은 되돌릴 수 없습니다.",
                        "프로필 교체 확인",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                // 불러오기 실행
                SettingsController.ImportResult result;
                if (isAddMode) {
                    result = settingsController.importProfilesFromFile(filePath);
                } else {
                    result = settingsController.replaceAllProfilesFromFile(filePath);
                }
                
                // 결과 표시
                if (result.isSuccess()) {
                    refreshAllSettings(); // UI 새로고침
                    
                    String details = "";
                    if (result.getAddedCount() > 0) {
                        details += "추가된 프로필: " + result.getAddedCount() + "개\n";
                    }
                    if (result.getSkippedCount() > 0) {
                        details += "건너뛴 프로필: " + result.getSkippedCount() + "개 (이미 존재)\n";
                    }
                    details += "현재 총 프로필: " + settingsController.getAllProfiles().size() + "개";
                    
                    JOptionPane.showMessageDialog(this,
                        result.getMessage() + "\n\n" + details,
                        "불러오기 완료",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        result.getMessage(),
                        "불러오기 실패",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "프로필 불러오기 중 오류가 발생했습니다:\n" + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 🧹 리소스 정리
     */
    public void cleanup() {
        if (uiUpdateScheduler != null && !uiUpdateScheduler.isShutdown()) {
            uiUpdateScheduler.shutdown();
            try {
                if (!uiUpdateScheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    uiUpdateScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                uiUpdateScheduler.shutdownNow();
            }
        }
        
        System.out.println("✅ SettingsPanel 정리 완료");
    }
}