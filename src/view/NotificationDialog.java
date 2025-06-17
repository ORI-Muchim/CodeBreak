package view;

import model.*;
import controller.*;
import constants.TimerConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * 알림 팝업 창
 */
public class NotificationDialog extends JDialog {
    private TimerModel.NotificationType notificationType;
    private TimerController timerController;
    
    private JLabel titleLabel;
    private JLabel messageLabel;
    private JLabel iconLabel;
    private JProgressBar timeoutBar;
    
    private JButton okButton;
    private JButton snoozeButton;
    private JButton ignoreButton;
    // continueButton 제거
    
    private Timer countdownTimer;
    private int timeoutSeconds = TimerConstants.NOTIFICATION_AUTO_CLOSE_SECONDS; // 상수에서 가져온 자동 닫기 시간
    private int remainingTimeout;
    
    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 250;
    
    public NotificationDialog(Frame parent, TimerModel.NotificationType type, TimerController controller) {
        super(parent, "휴식 알림", true);
        this.notificationType = type;
        this.timerController = controller;
        this.remainingTimeout = timeoutSeconds;
        
        initializeUI();
        setupEventHandlers();
        startCountdown();
        
        // 다이얼로그를 화면 중앙에 표시
        setLocationRelativeTo(parent);
        
        // 항상 맨 위에 표시
        setAlwaysOnTop(true);
        
        // 화면 점등 효과
        flashScreen();
        
        // 시스템 소리 재생
        Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * 화면 점등 효과
     */
    private void flashScreen() {
        SwingUtilities.invokeLater(() -> {
            try {
                // 전체 화면 크기 가져오기
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                // 크기를 줄인 점등 효과 위도우 생성
                JWindow flashWindow = new JWindow();
                flashWindow.setBounds(0, 0, screenSize.width, screenSize.height);
                flashWindow.setAlwaysOnTop(true);
                flashWindow.setBackground(new Color(255, 255, 255, 100)); // 반투명 흰색
                
                // 크기를 줄인 패널 생성
                JPanel flashPanel = new JPanel();
                flashPanel.setOpaque(false);
                flashPanel.setBackground(new Color(255, 255, 255, 150));
                flashWindow.setContentPane(flashPanel);
                
                // 점등 애니메이션
                Timer flashTimer = new Timer(50, null);
                final int[] flashCount = {0};
                final int MAX_FLASHES = 6; // 3번 깜빡임 (on/off 반복)
                
                flashTimer.addActionListener(e -> {
                    flashCount[0]++;
                    
                    if (flashCount[0] % 2 == 1) {
                        // 점등 on
                        flashWindow.setVisible(true);
                        flashPanel.setOpaque(true);
                        flashPanel.setBackground(new Color(255, 255, 255, 120));
                    } else {
                        // 점등 off
                        flashWindow.setVisible(false);
                    }
                    
                    if (flashCount[0] >= MAX_FLASHES) {
                        flashTimer.stop();
                        flashWindow.dispose();
                    }
                    
                    flashWindow.repaint();
                });
                
                flashTimer.start();
                
            } catch (Exception e) {
                System.err.println("화면 점등 효과 실패: " + e.getMessage());
            }
        });
    }
    
    /**
     * UI 초기화
     */
    private void initializeUI() {
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        // 메인 패널
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255)); // 연한 파란색 배경
        
        // 헤더 패널 (아이콘 + 제목)
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        
        // 아이콘
        iconLabel = new JLabel(createNotificationIcon(), SwingConstants.CENTER);
        headerPanel.add(iconLabel, BorderLayout.WEST);
        
        // 제목
        titleLabel = new JLabel(notificationType.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // 메시지 패널
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setOpaque(false);
        
        messageLabel = new JLabel("<html><div style='text-align: center; color: black;'>" + 
                                 notificationType.getMessage() + 
                                 "<br><br>잠시 휴식을 취하고 건강을 챙기세요!</div></html>", 
                                 SwingConstants.CENTER);
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK); // 명시적 텍스트 색상 설정
        messageLabel.setOpaque(true);
        messageLabel.setBackground(Color.WHITE);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // 타임아웃 진행바
        timeoutBar = new JProgressBar(0, timeoutSeconds);
        timeoutBar.setValue(remainingTimeout);
        timeoutBar.setStringPainted(true);
        timeoutBar.setString(remainingTimeout + "초 후 자동으로 휴식 시작");
        timeoutBar.setForeground(new Color(255, 165, 0));
        timeoutBar.setBackground(Color.LIGHT_GRAY);
        timeoutBar.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        // 진행바 텍스트 색상 설정
        timeoutBar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        messagePanel.add(timeoutBar, BorderLayout.SOUTH);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        okButton = createStyledButton("확인", new Color(60, 179, 113));
        snoozeButton = createStyledButton("5분 후", new Color(255, 165, 0));
        ignoreButton = createStyledButton("무시", new Color(128, 128, 128));
        // 계속 작업 버튼 제거 (무시와 동일한 기능)
        
        buttonPanel.add(okButton);
        buttonPanel.add(snoozeButton);
        buttonPanel.add(ignoreButton);
        
        // 레이아웃 구성
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * 스타일이 적용된 버튼 생성
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(80, 30));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true); // 불투명도 설정
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // 텍스트 색상 강제 설정
        button.putClientProperty("html.disable", Boolean.TRUE);
        
        // 호버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
                button.setForeground(Color.WHITE); // 호버시도 텍스트 색상 유지
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor);
                button.setForeground(Color.WHITE); // 텍스트 색상 유지
            }
        });
        
        return button;
    }
    
    /**
     * 알림 아이콘 생성
     */
    private ImageIcon createNotificationIcon() {
        // 간단한 아이콘 생성 (64x64)
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        // 안티얼라이싱 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 알림 유형에 따른 아이콘 그리기
        switch (notificationType) {
            case REST:
                drawRestIcon(g2d);
                break;
            case STRETCH:
                drawStretchIcon(g2d);
                break;
            case WATER:
                drawWaterIcon(g2d);
                break;
            case EYE_REST:
                drawEyeRestIcon(g2d);
                break;
            default:
                drawDefaultIcon(g2d);
                break;
        }
        
        g2d.dispose();
        return new ImageIcon(icon);
    }
    
    /**
     * 휴식 아이콘 그리기
     */
    private void drawRestIcon(Graphics2D g2d) {
        // 의자 모양
        g2d.setColor(new Color(139, 69, 19)); // 갈색
        g2d.fillRect(20, 35, 25, 20); // 등받이
        g2d.fillRect(15, 50, 35, 5);  // 좌석
        
        // 사람
        g2d.setColor(new Color(255, 220, 177)); // 살색
        g2d.fillOval(25, 15, 15, 15); // 머리
        g2d.setColor(new Color(70, 130, 180)); // 파란색 옷
        g2d.fillRect(28, 30, 9, 15); // 몸
    }
    
    /**
     * 스트레칭 아이콘 그리기
     */
    private void drawStretchIcon(Graphics2D g2d) {
        // 사람 모양 (팔을 벌린)
        g2d.setColor(new Color(255, 220, 177)); // 살색
        g2d.fillOval(27, 10, 10, 10); // 머리
        
        g2d.setColor(new Color(70, 130, 180)); // 파란색 옷
        g2d.fillRect(30, 20, 4, 20); // 몸
        
        // 팔 (벌린 모양)
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(20, 25, 32, 25); // 왼쪽 팔
        g2d.drawLine(44, 25, 32, 25); // 오른쪽 팔
        
        // 다리
        g2d.drawLine(32, 40, 25, 55); // 왼쪽 다리
        g2d.drawLine(32, 40, 39, 55); // 오른쪽 다리
    }
    
    /**
     * 물 마시기 아이콘 그리기
     */
    private void drawWaterIcon(Graphics2D g2d) {
        // 물컵
        g2d.setColor(new Color(173, 216, 230)); // 연한 파란색
        g2d.fillRect(25, 20, 15, 25);
        
        // 컵 테두리
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(25, 20, 15, 25);
        
        // 물
        g2d.setColor(new Color(0, 150, 255));
        g2d.fillRect(27, 30, 11, 13);
        
        // 물방울
        g2d.setColor(new Color(0, 191, 255));
        g2d.fillOval(20, 10, 6, 8);
        g2d.fillOval(38, 12, 4, 6);
    }
    
    /**
     * 눈 휴식 아이콘 그리기
     */
    private void drawEyeRestIcon(Graphics2D g2d) {
        // 얼굴
        g2d.setColor(new Color(255, 220, 177)); // 살색
        g2d.fillOval(20, 20, 24, 24);
        
        // 감은 눈
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc(25, 30, 4, 2, 0, 180); // 왼쪽 눈
        g2d.drawArc(35, 30, 4, 2, 0, 180); // 오른쪽 눈
        
        // Z (잠)
        g2d.setColor(new Color(70, 130, 180));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        g2d.drawString("Z", 48, 20);
        g2d.drawString("z", 45, 15);
        g2d.drawString("z", 42, 12);
    }
    
    /**
     * 기본 아이콘 그리기
     */
    private void drawDefaultIcon(Graphics2D g2d) {
        // 알림 벨
        g2d.setColor(new Color(255, 215, 0)); // 금색
        g2d.fillArc(25, 20, 15, 15, 0, 180); // 벨 몸체
        g2d.fillRect(30, 35, 5, 3); // 벨 손잡이
        
        // 알림 표시
        g2d.setColor(Color.RED);
        g2d.fillOval(38, 18, 8, 8); // 빨간 점
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
        g2d.drawString("!", 41, 24);
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 확인 버튼
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOkAction();
            }
        });
        
        // 스누즈 버튼
        snoozeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSnoozeAction();
            }
        });
        
        // 무시 버튼
        ignoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleIgnoreAction();
            }
        });
        
        // 계속 작업 버튼 제거 (무시와 동일하므로)
        
        // 창 닫기 이벤트 (무시와 동일)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleIgnoreAction();
            }
        });
        
        // ESC 키로 닫기
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleIgnoreAction();
            }
        });
        
        // Enter 키로 확인
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke("ENTER");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKeyStroke, "ENTER");
        getRootPane().getActionMap().put("ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOkAction();
            }
        });
    }
    
    /**
     * 카운트다운 시작
     */
    private void startCountdown() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTimeout--;
                timeoutBar.setValue(remainingTimeout);
                timeoutBar.setString(remainingTimeout + "초 후 자동으로 휴식 시작");
                
                if (remainingTimeout <= 0) {
                    handleTimeoutAction();
                }
            }
        });
        countdownTimer.start();
    }
    
    /**
     * 카운트다운 중지
     */
    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }
    
    /**
     * 확인 버튼 처리
     */
    private void handleOkAction() {
        stopCountdown();
        // 타이머를 휴식 모드로 전환 (포모도로 모드인 경우)
        timerController.acknowledgeBreak();
        closeDialog();
    }
    
    /**
     * 스누즈 버튼 처리
     */
    private void handleSnoozeAction() {
        stopCountdown();
        // 5분 후에 다시 알림
        timerController.snoozeNotification(notificationType);
        closeDialog();
        
        // 스누즈 메시지 표시
        showSnoozeMessage();
    }
    
    /**
     * 무시 버튼 처리
     */
    private void handleIgnoreAction() {
        stopCountdown();
        closeDialog();
    }
    
    /**
     * 타임아웃 처리 - 확인 버튼과 동일하게 처리하여 자동으로 휴식 시작
     */
    private void handleTimeoutAction() {
        stopCountdown();
        // 자동으로 휴식 모드로 전환 (확인 버튼과 동일한 동작)
        timerController.acknowledgeBreak();
        closeDialog();
    }
    
    /**
     * 다이얼로그 닫기
     */
    private void closeDialog() {
        setVisible(false);
        dispose();
    }
    
    /**
     * 스누즈 메시지 표시
     */
    private void showSnoozeMessage() {
        // 작은 알림 윈도우 표시
        JWindow snoozeWindow = new JWindow();
        snoozeWindow.setSize(250, 80);
        snoozeWindow.setLocationRelativeTo(null);
        snoozeWindow.setAlwaysOnTop(true);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(255, 255, 224)); // 연한 노란색
        
        JLabel label = new JLabel("5분 후에 다시 알림이 표시됩니다.", SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        label.setForeground(Color.BLACK); // 텍스트 색상 명시적 설정
        label.setOpaque(true);
        label.setBackground(new Color(255, 255, 224));
        panel.add(label, BorderLayout.CENTER);
        
        snoozeWindow.add(panel);
        snoozeWindow.setVisible(true);
        
        // 3초 후 자동으로 닫기
        Timer hideTimer = new Timer(3000, e -> {
            snoozeWindow.setVisible(false);
            snoozeWindow.dispose();
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
}
