import view.MainFrame;
import constants.UIConstants;
import java.awt.SystemTray;

import javax.swing.*;

/**
 * Code ∧ Break 애플리케이션의 메인 클래스
 * 
 * 프로그래머를 위한 GUI 기반 시간 관리 및 알림 시스템
 * 
 * @author 조민형
 * @version 1.0
 */
public class CodeBreakApplication {
    
    // 상수들은 UIConstants에서 관리
    
    /**
     * 애플리케이션 메인 메서드
     */
    public static void main(String[] args) {
        // 명령행 인수 처리
        ApplicationConfig config = parseCommandLineArguments(args);
        
        // 시스템 속성 설정
        setSystemProperties();
        
        // Swing EDT에서 GUI 초기화
        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication(config);
            } catch (Exception e) {
                handleStartupError(e);
            }
        });
    }
    
    /**
     * 명령행 인수 파싱
     */
    private static ApplicationConfig parseCommandLineArguments(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            switch (arg) {
                case "--minimized":
                case "-m":
                    config.startMinimized = true;
                    break;
                    
                case "--no-tray":
                    config.disableTray = true;
                    break;
                    
                case "--debug":
                case "-d":
                    config.debugMode = true;
                    break;
                    
                case "--profile":
                case "-p":
                    if (i + 1 < args.length) {
                        config.startupProfile = args[++i];
                    }
                    break;
                    
                case "--auto-start":
                    config.autoStartTimer = true;
                    break;
                    
                case "--help":
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                    
                case "--version":
                case "-v":
                    printVersion();
                    System.exit(0);
                    break;
                    
                default:
                    if (arg.startsWith("-")) {
                        System.err.println("알 수 없는 옵션: " + arg);
                        System.err.println("도움말을 보려면 --help를 사용하세요.");
                        System.exit(1);
                    }
                    break;
            }
        }
        
        return config;
    }
    
    /**
     * 시스템 속성 설정
     */
    private static void setSystemProperties() {
        // macOS에서 메뉴바에 애플리케이션 이름 표시
        System.setProperty(UIConstants.MACOS_APP_NAME_PROPERTY, UIConstants.APP_TITLE);
        
        // Windows에서 시스템 트레이 지원 향상
        System.setProperty(UIConstants.JAVA_HEADLESS_PROPERTY, "false");
        
        // 고해상도 디스플레이 지원
        System.setProperty(UIConstants.UI_SCALE_PROPERTY, "1.0");
        
        // 폰트 안티앨리어싱 활성화
        System.setProperty(UIConstants.FONT_ANTIALIASING_PROPERTY, "on");
        System.setProperty(UIConstants.SWING_ANTIALIASING_PROPERTY, "true");
    }
    
    /**
     * 애플리케이션 초기화
     */
    private static void initializeApplication(ApplicationConfig config) {
        // 디버그 모드 설정
        if (config.debugMode) {
            enableDebugMode();
        }
        
        // Look and Feel 설정
        setLookAndFeel();
        
        // 메인 프레임 생성
        MainFrame mainFrame = new MainFrame();
        
        // 시작 설정 적용
        applyStartupConfiguration(mainFrame, config);
        
        // 애플리케이션 표시
        if (!config.startMinimized) {
            mainFrame.setVisible(true);
        } else if (SystemTray.isSupported() && !config.disableTray) {
            // 트레이로 시작
            mainFrame.minimizeToTray();
        } else {
            // 트레이가 지원되지 않으면 일반 창으로 표시
            mainFrame.setVisible(true);
        }
        
        // 성공적인 시작 로그
        logApplicationStart(config);
    }
    
    /**
     * Look and Feel 설정
     */
    private static void setLookAndFeel() {
        try {
            // 시스템 Look and Feel 사용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // UI 개선 설정
            UIManager.put("OptionPane.yesButtonText", "예");
            UIManager.put("OptionPane.noButtonText", "아니오");
            UIManager.put("OptionPane.cancelButtonText", "취소");
            UIManager.put("OptionPane.okButtonText", "확인");
            
            // 폰트 설정 (한글 지원)
            UIManager.put("Label.font", UIManager.getFont("Label.font").deriveFont(12f));
            UIManager.put("Button.font", UIManager.getFont("Button.font").deriveFont(12f));
            UIManager.put("TextField.font", UIManager.getFont("TextField.font").deriveFont(12f));
            
        } catch (Exception e) {
            System.err.println("Look and Feel 설정 실패, 기본 테마를 사용합니다: " + e.getMessage());
        }
    }
    
    /**
     * 시작 설정 적용
     */
    private static void applyStartupConfiguration(MainFrame mainFrame, ApplicationConfig config) {
        // 시작 프로필 설정
        if (config.startupProfile != null) {
            // 지정된 프로필로 변경
            var profiles = mainFrame.getSettingsController().getAllProfiles();
            for (var profile : profiles) {
                if (profile.getProfileName().equalsIgnoreCase(config.startupProfile)) {
                    mainFrame.getSettingsController().setCurrentProfile(profile);
                    mainFrame.getSettingsController().applyProfileToTimer(mainFrame.getTimerModel());
                    break;
                }
            }
        }
        
        // 자동 시작 설정
        if (config.autoStartTimer) {
            SwingUtilities.invokeLater(() -> {
                mainFrame.getTimerController().startTimer();
            });
        }
        
        // 시스템 트레이 비활성화 처리
        if (config.disableTray) {
            // 트레이 기능 비활성화 로직
            System.out.println("시스템 트레이가 비활성화되었습니다.");
        }
    }
    
    /**
     * 디버그 모드 활성화
     */
    private static void enableDebugMode() {
        System.out.println("=== " + UIConstants.APP_TITLE + " Debug Mode ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("System Tray Supported: " + SystemTray.isSupported());
        
        // 추가 디버그 정보
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Memory: " + 
            (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + "MB / " +
            runtime.totalMemory() / 1024 / 1024 + "MB");
    }
    
    /**
     * 시작 에러 처리
     */
    private static void handleStartupError(Exception e) {
        System.err.println("애플리케이션 시작 실패: " + e.getMessage());
        e.printStackTrace();
        
        // 사용자에게 에러 표시
        String errorMessage = UIConstants.APP_TITLE + " 시작 중 오류가 발생했습니다.\n\n" +
                             "오류: " + e.getMessage() + "\n\n" +
                             "프로그램을 다시 시작해보세요.";
        
        JOptionPane.showMessageDialog(
            null,
            errorMessage,
            "시작 오류",
            JOptionPane.ERROR_MESSAGE
        );
        
        System.exit(1);
    }
    
    /**
     * 애플리케이션 시작 로그
     */
    private static void logApplicationStart(ApplicationConfig config) {
        System.out.println(UIConstants.APP_TITLE + " v" + UIConstants.APP_VERSION + " 시작됨");
        
        if (config.debugMode) {
            System.out.println("설정: " + config);
        }
    }
    
    /**
     * 도움말 출력
     */
    private static void printHelp() {
        System.out.println(UIConstants.APP_TITLE + " v" + UIConstants.APP_VERSION);
        System.out.println("프로그래머를 위한 GUI 기반 시간 관리 및 알림 시스템");
        System.out.println();
        System.out.println("사용법: java CodeBreakApplication [옵션]");
        System.out.println();
        System.out.println("옵션:");
        System.out.println("  -m, --minimized        최소화된 상태로 시작");
        System.out.println("  --no-tray              시스템 트레이 비활성화");
        System.out.println("  -d, --debug            디버그 모드 활성화");
        System.out.println("  -p, --profile NAME     지정된 프로필로 시작");
        System.out.println("  --auto-start           타이머 자동 시작");
        System.out.println("  -h, --help             이 도움말 표시");
        System.out.println("  -v, --version          버전 정보 표시");
        System.out.println();
        System.out.println("예제:");
        System.out.println("  java CodeBreakApplication --minimized --profile \"포모도로\"");
        System.out.println("  java CodeBreakApplication --auto-start --debug");
    }
    
    /**
     * 버전 정보 출력
     */
    private static void printVersion() {
        System.out.println(UIConstants.APP_TITLE + " " + UIConstants.APP_VERSION);
        System.out.println("개발자: " + UIConstants.DEVELOPER_NAME);
        System.out.println("빌드: " + getBuildInfo());
    }
    
    /**
     * 빌드 정보 가져오기
     */
    private static String getBuildInfo() {
        // 간단한 빌드 정보
        return java.time.LocalDate.now().toString();
    }
    
    /**
     * 애플리케이션 설정을 담는 내부 클래스
     */
    private static class ApplicationConfig {
        boolean startMinimized = false;
        boolean disableTray = false;
        boolean debugMode = false;
        boolean autoStartTimer = false;
        String startupProfile = null;
        
        @Override
        public String toString() {
            return String.format(
                "ApplicationConfig{startMinimized=%s, disableTray=%s, debugMode=%s, autoStartTimer=%s, startupProfile='%s'}",
                startMinimized, disableTray, debugMode, autoStartTimer, startupProfile
            );
        }
    }
}
