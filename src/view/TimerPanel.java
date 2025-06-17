package view;

import model.*;
import controller.*;
import events.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 타이머 표시 및 제어 패널 - 프로필 변경 실시간 반영 기능 추가
 */
public class TimerPanel extends JPanel implements TimerModel.TimerListener, EventHandler<ProfileChangedEvent> {
    private TimerModel timerModel;
    private TimerController timerController;
    private SettingsController settingsController;
    private EventBus eventBus;  // 이벤트 버스 추가
    
    // UI 컴포넌트
    private JLabel timeLabel;
    private JLabel phaseLabel;
    private JLabel cycleLabel;
    private JProgressBar progressBar;
    
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton resetButton;
    
    private JSpinner workMinutesSpinner;
    private JSpinner breakMinutesSpinner;
    private JCheckBox pomodoroModeCheckBox;
    
    private JPanel controlPanel;
    private JPanel settingsPanel;
    private JPanel displayPanel;
    
    public TimerPanel(TimerModel timerModel, TimerController timerController, SettingsController settingsController) {
        this.timerModel = timerModel;
        this.timerController = timerController;
        this.settingsController = settingsController;
        this.eventBus = SimpleEventBus.getInstance();  // EventBus 인스턴스 가져오기
        
        initializeUI();
        setupEventHandlers();
        updateDisplay();
        
        // 타이머 리스너 등록
        timerModel.addTimerListener(this);
        
        // 프로필 변경 이벤트 구독
        eventBus.subscribe(ProfileChangedEvent.class, this);
        
        System.out.println("✅ TimerPanel에서 ProfileChangedEvent 구독 시작");
    }
    
    /**
     * 프로필 변경 이벤트 처리
     */
    @Override
    public void handle(ProfileChangedEvent event) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 TimerPanel에서 프로필 변경 감지: " + event.getNewProfile().getProfileName());
            
            UserProfile newProfile = event.getNewProfile();
            
            // 타이머 모델 업데이트
            timerModel.setWorkMinutes(newProfile.getWorkMinutes());
            timerModel.setBreakMinutes(newProfile.getBreakMinutes());
            timerModel.setPomodoroMode(newProfile.isPomodoroMode());
            
            // UI 업데이트
            updateDisplay();
            
            System.out.println("✅ 타이머 UI 업데이트 완료: " + newProfile.getWorkMinutes() + "분/" + newProfile.getBreakMinutes() + "분");
        });
    }
    
    /**
     * UI 초기화
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 패널 배경색 설정
        setBackground(Color.WHITE);
        
        createDisplayPanel();
        createControlPanel();
        createSettingsPanel();
        
        add(displayPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(settingsPanel, BorderLayout.NORTH);
    }
    
    /**
     * 디스플레이 패널 생성
     */
    private void createDisplayPanel() {
        displayPanel = new JPanel(new BorderLayout(10, 10));
        displayPanel.setBackground(Color.WHITE); // 배경색 명시적 설정
        
        // 시간 표시 라벨
        timeLabel = new JLabel("25:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        timeLabel.setForeground(new Color(70, 130, 180));
        timeLabel.setBackground(Color.WHITE);
        timeLabel.setOpaque(true);
        timeLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // 상태 정보 패널
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusPanel.setBackground(Color.WHITE); // 배경색 설정
        
        phaseLabel = new JLabel("작업 시간", SwingConstants.CENTER);
        phaseLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        phaseLabel.setForeground(Color.BLACK); // 텍스트 색상 명시적 설정
        phaseLabel.setBackground(Color.WHITE);
        phaseLabel.setOpaque(true);
        
        cycleLabel = new JLabel("사이클: 0", SwingConstants.CENTER);
        cycleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cycleLabel.setForeground(new Color(60, 60, 60)); // 보조 텍스트 색상
        cycleLabel.setBackground(Color.WHITE);
        cycleLabel.setOpaque(true);
        
        // 진행 바
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setBackground(Color.LIGHT_GRAY);
        progressBar.setForeground(new Color(70, 130, 180));
        
        statusPanel.add(phaseLabel);
        statusPanel.add(cycleLabel);
        statusPanel.add(progressBar);
        
        displayPanel.add(timeLabel, BorderLayout.CENTER);
        displayPanel.add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 컨트롤 패널 생성
     */
    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(Color.WHITE); // 배경색 설정
        
        // 버튼 생성
        startButton = new JButton("시작");
        startButton.setPreferredSize(new Dimension(80, 35));
        startButton.setBackground(new Color(60, 179, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setOpaque(true);
        startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        pauseButton = new JButton("일시정지");
        pauseButton.setPreferredSize(new Dimension(100, 35));
        pauseButton.setBackground(new Color(255, 165, 0));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.setBorderPainted(false);
        pauseButton.setOpaque(true);
        pauseButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        pauseButton.setEnabled(false);
        
        stopButton = new JButton("정지");
        stopButton.setPreferredSize(new Dimension(80, 35));
        stopButton.setBackground(new Color(220, 20, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setBorderPainted(false);
        stopButton.setOpaque(true);
        stopButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        stopButton.setEnabled(false);
        
        resetButton = new JButton("리셋");
        resetButton.setPreferredSize(new Dimension(80, 35));
        resetButton.setBackground(new Color(128, 128, 128));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setOpaque(true);
        resetButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(resetButton);
    }
    
    /**
     * 설정 패널 생성
     */
    private void createSettingsPanel() {
        settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("타이머 설정"));
        settingsPanel.setBackground(Color.WHITE); // 배경색 설정
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 포모도로 모드 체크박스
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        pomodoroModeCheckBox = new JCheckBox("포모도로 모드", timerModel.isPomodoroMode());
        pomodoroModeCheckBox.setBackground(Color.WHITE);
        pomodoroModeCheckBox.setForeground(Color.BLACK);
        settingsPanel.add(pomodoroModeCheckBox, gbc);
        
        // 작업 시간 설정
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel workLabel = new JLabel("작업 시간 (분):");
        workLabel.setForeground(Color.BLACK); // 텍스트 색상 명시적 설정
        settingsPanel.add(workLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        workMinutesSpinner = new JSpinner(new SpinnerNumberModel(timerModel.getWorkMinutes(), 1, 180, 1));
        workMinutesSpinner.setPreferredSize(new Dimension(80, 25));
        workMinutesSpinner.setBackground(Color.WHITE);
        settingsPanel.add(workMinutesSpinner, gbc);
        
        // 휴식 시간 설정
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel breakLabel = new JLabel("휴식 시간 (분):");
        breakLabel.setForeground(Color.BLACK); // 텍스트 색상 명시적 설정
        settingsPanel.add(breakLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        breakMinutesSpinner = new JSpinner(new SpinnerNumberModel(timerModel.getBreakMinutes(), 1, 60, 1));
        breakMinutesSpinner.setPreferredSize(new Dimension(80, 25));
        breakMinutesSpinner.setBackground(Color.WHITE);
        settingsPanel.add(breakMinutesSpinner, gbc);
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 시작 버튼
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerController.startTimer();
            }
        });
        
        // 일시정지 버튼
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerController.pauseTimer();
            }
        });
        
        // 정지 버튼
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerController.stopTimer();
            }
        });
        
        // 리셋 버튼
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerController.resetTimer();
            }
        });
        
        // 설정 변경 리스너
        workMinutesSpinner.addChangeListener(e -> {
            int newValue = (Integer) workMinutesSpinner.getValue();
            timerModel.setWorkMinutes(newValue);
            
            // 새로운 SettingsController 메서드 사용
            settingsController.updateCurrentProfileSetting("workMinutes", newValue);
            System.out.println("작업시간 " + newValue + "분 저장됨");
        });
        
        breakMinutesSpinner.addChangeListener(e -> {
            int newValue = (Integer) breakMinutesSpinner.getValue();
            timerModel.setBreakMinutes(newValue);
            
            // 새로운 SettingsController 메서드 사용
            settingsController.updateCurrentProfileSetting("breakMinutes", newValue);
            System.out.println("휴식시간 " + newValue + "분 저장됨");
        });
        
        pomodoroModeCheckBox.addActionListener(e -> {
            boolean newValue = pomodoroModeCheckBox.isSelected();
            timerModel.setPomodoroMode(newValue);
            
            // 새로운 SettingsController 메서드 사용
            settingsController.updateCurrentProfileSetting("pomodoroMode", newValue);
            System.out.println("포모도로 모드 " + newValue + " 저장됨");
            
            updateBreakSettingsVisibility();
        });
        
        // 초기 설정
        updateBreakSettingsVisibility();
    }
    
    /**
     * 휴식 시간 설정의 표시 여부 업데이트
     */
    private void updateBreakSettingsVisibility() {
        boolean showBreakSettings = pomodoroModeCheckBox.isSelected();
        
        // 휴식 시간 관련 컴포넌트들의 표시 여부 설정
        Component[] components = settingsPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JLabel) {
                JLabel label = (JLabel) components[i];
                if (label.getText().contains("휴식")) {
                    label.setVisible(showBreakSettings);
                }
            }
        }
        breakMinutesSpinner.setVisible(showBreakSettings);
        
        settingsPanel.revalidate();
        settingsPanel.repaint();
    }
    
    /**
     * 디스플레이 업데이트
     */
    private void updateDisplay() {
        SwingUtilities.invokeLater(() -> {
            // 시간 표시 업데이트
            timeLabel.setText(timerModel.getFormattedTime());
            
            // 페이즈 표시 업데이트
            phaseLabel.setText(timerModel.getCurrentPhase());
            
            // 사이클 표시 업데이트
            cycleLabel.setText("사이클: " + timerModel.getCurrentCycle());
            
            // 진행 바 업데이트
            updateProgressBar();
            
            // 버튼 상태 업데이트
            updateButtonStates();
            
            // 설정 값 업데이트
            updateSettings();
        });
    }
    
    /**
     * 진행 바 업데이트
     */
    private void updateProgressBar() {
        int totalSeconds;
        int elapsedSeconds;
        
        if (timerModel.isPomodoroMode() && timerModel.getCurrentCycle() % 2 == 1) {
            // 휴식 시간
            totalSeconds = timerModel.getBreakMinutes() * 60;
        } else {
            // 작업 시간
            totalSeconds = timerModel.getWorkMinutes() * 60;
        }
        
        elapsedSeconds = totalSeconds - timerModel.getRemainingSeconds();
        int progress = (int) ((double) elapsedSeconds / totalSeconds * 100);
        
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
    }
    
    /**
     * 버튼 상태 업데이트
     */
    private void updateButtonStates() {
        TimerModel.TimerState state = timerModel.getState();
        
        switch (state) {
            case STOPPED:
                startButton.setEnabled(true);
                startButton.setText("시작");
                startButton.setForeground(Color.WHITE); // 텍스트 색상 재설정
                pauseButton.setEnabled(false);
                pauseButton.setForeground(Color.WHITE);
                stopButton.setEnabled(false);
                stopButton.setForeground(Color.WHITE);
                resetButton.setEnabled(true);
                resetButton.setForeground(Color.WHITE);
                
                // 설정 변경 가능
                workMinutesSpinner.setEnabled(true);
                breakMinutesSpinner.setEnabled(true);
                pomodoroModeCheckBox.setEnabled(true);
                break;
                
            case RUNNING:
                startButton.setEnabled(false);
                startButton.setForeground(Color.WHITE);
                pauseButton.setEnabled(true);
                pauseButton.setForeground(Color.WHITE);
                stopButton.setEnabled(true);
                stopButton.setForeground(Color.WHITE);
                resetButton.setEnabled(false);
                resetButton.setForeground(Color.WHITE);
                
                // 설정 변경 불가
                workMinutesSpinner.setEnabled(false);
                breakMinutesSpinner.setEnabled(false);
                pomodoroModeCheckBox.setEnabled(false);
                break;
                
            case PAUSED:
                startButton.setEnabled(true);
                startButton.setText("재개");
                startButton.setForeground(Color.WHITE);
                pauseButton.setEnabled(false);
                pauseButton.setForeground(Color.WHITE);
                stopButton.setEnabled(true);
                stopButton.setForeground(Color.WHITE);
                resetButton.setEnabled(true);
                resetButton.setForeground(Color.WHITE);
                
                // 설정 변경 불가
                workMinutesSpinner.setEnabled(false);
                breakMinutesSpinner.setEnabled(false);
                pomodoroModeCheckBox.setEnabled(false);
                break;
        }
        
        // 모든 버튼의 폰트와 스타일 재설정
        JButton[] buttons = {startButton, pauseButton, stopButton, resetButton};
        for (JButton button : buttons) {
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            button.repaint();
        }
    }
    
    /**
     * 설정 값 업데이트
     */
    private void updateSettings() {
        if (!workMinutesSpinner.getValue().equals(timerModel.getWorkMinutes())) {
            workMinutesSpinner.setValue(timerModel.getWorkMinutes());
        }
        
        if (!breakMinutesSpinner.getValue().equals(timerModel.getBreakMinutes())) {
            breakMinutesSpinner.setValue(timerModel.getBreakMinutes());
        }
        
        if (pomodoroModeCheckBox.isSelected() != timerModel.isPomodoroMode()) {
            pomodoroModeCheckBox.setSelected(timerModel.isPomodoroMode());
            updateBreakSettingsVisibility();
        }
    }
    
    // TimerModel.TimerListener 구현
    @Override
    public void onTimerTick(int remainingSeconds) {
        updateDisplay();
    }
    
    @Override
    public void onTimerComplete(TimerModel.NotificationType type) {
        updateDisplay();
        
        // 완료 효과 (색상 변경)
        SwingUtilities.invokeLater(() -> {
            Color originalColor = timeLabel.getForeground();
            timeLabel.setForeground(Color.RED);
            
            Timer flashTimer = new Timer(500, null);
            flashTimer.addActionListener(e -> {
                timeLabel.setForeground(originalColor);
                ((Timer) e.getSource()).stop();
            });
            flashTimer.start();
        });
    }
    
    @Override
    public void onTimerStateChanged(TimerModel.TimerState state) {
        updateDisplay();
    }
    
    /**
     * 패널 정리
     */
    public void cleanup() {
        // 타이머 리스너 해제
        timerModel.removeTimerListener(this);
        
        // 이벤트 구독 해제
        if (eventBus != null) {
            eventBus.unsubscribe(ProfileChangedEvent.class, this);
            System.out.println("🧹 TimerPanel ProfileChangedEvent 구독 해제");
        }
    }
}
