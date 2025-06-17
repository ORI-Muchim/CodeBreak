package constants;

/**
 * UI 관련 상수들을 중앙 관리하는 클래스
 * 매직 넘버를 제거하고 가독성을 향상시킵니다.
 */
public final class UIConstants {
    
    // ============================================
    // 📐 창 크기 관련 상수
    // ============================================
    
    /** 기본 창 너비 */
    public static final int DEFAULT_WIDTH = 1000;
    
    /** 기본 창 높이 */
    public static final int DEFAULT_HEIGHT = 700;
    
    /** 설정 창 기본 너비 */
    public static final int SETTINGS_DEFAULT_WIDTH = 450;
    
    /** 설정 창 기본 높이 */
    public static final int SETTINGS_DEFAULT_HEIGHT = 350;
    
    /** 창 기본 X 위치 */
    public static final int DEFAULT_WINDOW_X = 100;
    
    /** 창 기본 Y 위치 */
    public static final int DEFAULT_WINDOW_Y = 100;
    
    /** 최소 창 너비 */
    public static final int MIN_WIDTH = 800;
    
    /** 최소 창 높이 */
    public static final int MIN_HEIGHT = 600;
    
    // ============================================
    // 🖥️ 대화면 디스플레이 관련 상수
    // ============================================
    
    /** 대화면으로 간주하는 최소 너비 */
    public static final int LARGE_SCREEN_WIDTH_THRESHOLD = 1920;
    
    /** 대화면으로 간주하는 최소 높이 */
    public static final int LARGE_SCREEN_HEIGHT_THRESHOLD = 1080;
    
    /** 대화면에서의 최대 창 너비 */
    public static final int LARGE_SCREEN_MAX_WIDTH = 1200;
    
    /** 대화면에서의 최대 창 높이 */
    public static final int LARGE_SCREEN_MAX_HEIGHT = 900;
    
    /** 화면 가장자리에서의 여백 */
    public static final int SCREEN_MARGIN = 100;
    
    /** 화면 대비 창 크기 비율 (90%) */
    public static final double SCREEN_SIZE_RATIO = 0.9;
    
    // ============================================
    // 🎨 폰트 및 스타일 관련 상수
    // ============================================
    
    /** 기본 폰트 크기 */
    public static final float DEFAULT_FONT_SIZE = 14f;
    
    /** 라벨 폰트 크기 */
    public static final float LABEL_FONT_SIZE = 13f;
    
    /** 버튼 폰트 크기 */
    public static final float BUTTON_FONT_SIZE = 13f;
    
    /** 타이머 디스플레이 폰트 크기 */
    public static final float TIMER_FONT_SIZE = 32f;
    
    // ============================================
    // 🖼️ 아이콘 관련 상수
    // ============================================
    
    /** 애플리케이션 아이콘 크기 */
    public static final int APP_ICON_SIZE = 32;
    
    /** 아이콘 내부 여백 */
    public static final int ICON_PADDING = 2;
    
    /** 아이콘 테두리 두께 */
    public static final int ICON_BORDER_WIDTH = 2;
    
    // ============================================
    // 📏 레이아웃 관련 상수
    // ============================================
    
    /** 컴포넌트 간 기본 간격 */
    public static final int DEFAULT_SPACING = 10;
    
    /** 컴포넌트 간 작은 간격 */
    public static final int SMALL_SPACING = 5;
    
    /** 컴포넌트 간 큰 간격 */
    public static final int LARGE_SPACING = 20;
    
    /** 패널 패딩 */
    public static final int PANEL_PADDING = 15;
    
    // ============================================
    // 🎯 애플리케이션 정보
    // ============================================
    
    /** 애플리케이션 제목 */
    public static final String APP_TITLE = "Code ∧ Break";
    
    /** 애플리케이션 버전 */
    public static final String APP_VERSION = "1.0";
    
    /** 개발자 정보 */
    public static final String DEVELOPER_NAME = "조민형";
    
    // ============================================
    // 🌈 색상 관련 상수 (RGB 값)
    // ============================================
    
    /** 메인 테마 색상 (Steel Blue) */
    public static final int[] MAIN_COLOR_RGB = {70, 130, 180};
    
    /** 선택 영역 색상 (Light Blue) */
    public static final int[] SELECTION_COLOR_RGB = {184, 207, 229};
    
    /** 비활성화 텍스트 색상 (Gray) */
    public static final int[] DISABLED_TEXT_RGB = {128, 128, 128};
    
    /** 백그라운드 색상 (White) */
    public static final int[] BACKGROUND_COLOR_RGB = {255, 255, 255};
    
    /** 텍스트 색상 (Black) */
    public static final int[] TEXT_COLOR_RGB = {0, 0, 0};
    
    /** 버튼 배경 색상 (Light Gray) */
    public static final int[] BUTTON_BACKGROUND_RGB = {245, 245, 245};
    
    // ============================================
    // 🎛️ UI 동작 관련 상수
    // ============================================
    
    /** 더블클릭 감지 시간 간격 (밀리초) */
    public static final int DOUBLE_CLICK_INTERVAL_MS = 300;
    
    /** 툴팁 표시 지연시간 (밀리초) */
    public static final int TOOLTIP_DELAY_MS = 500;
    
    /** 애니메이션 지속시간 (밀리초) */
    public static final int ANIMATION_DURATION_MS = 200;
    
    // ============================================
    // 📝 메시지 텍스트
    // ============================================
    
    /** 종료 확인 메시지 */
    public static final String EXIT_CONFIRMATION_MESSAGE = "정말로 종료하시겠습니까?";
    
    /** 종료 확인 제목 */
    public static final String EXIT_CONFIRMATION_TITLE = "종료 확인";
    
    /** 트레이 최소화 메시지 */
    public static final String TRAY_MINIMIZE_MESSAGE = "트레이로 최소화되었습니다.";
    
    /** Look and Feel 오류 메시지 */
    public static final String LOOK_AND_FEEL_ERROR_MESSAGE = "Look and Feel 설정 실패, 기본 테마를 사용합니다: ";
    
    // ============================================
    // 🔧 시스템 속성 키
    // ============================================
    
    /** macOS 애플리케이션 이름 속성 키 */
    public static final String MACOS_APP_NAME_PROPERTY = "com.apple.mrj.application.apple.menu.about.name";
    
    /** Java headless 모드 속성 키 */
    public static final String JAVA_HEADLESS_PROPERTY = "java.awt.headless";
    
    /** UI 스케일 속성 키 */
    public static final String UI_SCALE_PROPERTY = "sun.java2d.uiScale";
    
    /** 폰트 안티앨리어싱 속성 키 */
    public static final String FONT_ANTIALIASING_PROPERTY = "awt.useSystemAAFontSettings";
    
    /** Swing 텍스트 안티앨리어싱 속성 키 */
    public static final String SWING_ANTIALIASING_PROPERTY = "swing.aatext";
    
    // ============================================
    // 🚫 인스턴스 생성 방지
    // ============================================
    
    private UIConstants() {
        throw new AssertionError("UIConstants는 인스턴스를 생성할 수 없습니다.");
    }
}
