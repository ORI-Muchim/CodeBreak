package constants;

/**
 * 타이머 관련 상수들을 중앙 관리하는 클래스
 * 타이머 기능의 매직 넘버를 제거하고 설정값을 명확하게 정의합니다.
 */
public final class TimerConstants {
    
    // ============================================
    // ⏰ 기본 타이머 설정
    // ============================================
    
    /** 기본 작업 시간 (분) */
    public static final int DEFAULT_WORK_MINUTES = 25;
    
    /** 기본 휴식 시간 (분) */
    public static final int DEFAULT_BREAK_MINUTES = 5;
    
    /** 기본 스누즈 시간 (분) */
    public static final int DEFAULT_SNOOZE_MINUTES = 5;
    
    // ============================================
    // 📊 타이머 제한값
    // ============================================
    
    /** 최소 작업 시간 (분) */
    public static final int MIN_WORK_MINUTES = 1;
    
    /** 최대 작업 시간 (분) - 3시간 */
    public static final int MAX_WORK_MINUTES = 180;
    
    /** 최소 휴식 시간 (분) */
    public static final int MIN_BREAK_MINUTES = 1;
    
    /** 최대 휴식 시간 (분) - 1시간 */
    public static final int MAX_BREAK_MINUTES = 60;
    
    /** 최소 스누즈 시간 (분) */
    public static final int MIN_SNOOZE_MINUTES = 1;
    
    /** 최대 스누즈 시간 (분) */
    public static final int MAX_SNOOZE_MINUTES = 30;
    
    // ============================================
    // 🔄 시간 변환 상수
    // ============================================
    
    /** 1분당 초 수 */
    public static final int SECONDS_PER_MINUTE = 60;
    
    /** 1초당 밀리초 수 */
    public static final int MILLISECONDS_PER_SECOND = 1000;
    
    /** 타이머 틱 간격 (밀리초) */
    public static final int TIMER_TICK_INTERVAL_MS = 1000;
    
    // ============================================
    // 🎯 프리셋 타이머 설정
    // ============================================
    
    // 포모도로 프리셋
    /** 포모도로 작업 시간 (분) */
    public static final int POMODORO_WORK_MINUTES = 25;
    
    /** 포모도로 휴식 시간 (분) */
    public static final int POMODORO_BREAK_MINUTES = 5;
    
    /** 포모도로 긴 휴식 시간 (분) */
    public static final int POMODORO_LONG_BREAK_MINUTES = 15;
    
    /** 긴 휴식 전 필요한 사이클 수 */
    public static final int CYCLES_BEFORE_LONG_BREAK = 4;
    
    // 장시간 작업 프리셋
    /** 장시간 작업 시간 (분) */
    public static final int LONG_WORK_MINUTES = 60;
    
    /** 장시간 작업 휴식 시간 (분) */
    public static final int LONG_WORK_BREAK_MINUTES = 10;
    
    // 단시간 집중 프리셋
    /** 단시간 집중 작업 시간 (분) */
    public static final int SHORT_FOCUS_WORK_MINUTES = 15;
    
    /** 단시간 집중 휴식 시간 (분) */
    public static final int SHORT_FOCUS_BREAK_MINUTES = 3;
    
    // 커스텀 프리셋 기준값들
    /** 짧은 작업으로 간주하는 최대 시간 (분) */
    public static final int SHORT_WORK_THRESHOLD = 20;
    
    /** 중간 작업으로 간주하는 최대 시간 (분) */
    public static final int MEDIUM_WORK_THRESHOLD = 45;
    
    /** 긴 작업으로 간주하는 최소 시간 (분) */
    public static final int LONG_WORK_THRESHOLD = 46;
    
    // ============================================
    // 📊 통계 및 성과 관련 상수
    // ============================================
    
    /** 생산성 비율 계산시 백분율 변환 */
    public static final double PERCENTAGE_MULTIPLIER = 100.0;
    
    /** 높은 집중도로 간주하는 작업/휴식 비율 */
    public static final double HIGH_FOCUS_RATIO = 10.0;
    
    /** 일반 집중도로 간주하는 작업/휴식 비율 */
    public static final double NORMAL_FOCUS_RATIO = 5.0;
    
    /** 균형 잡힌 것으로 간주하는 작업/휴식 비율 */
    public static final double BALANCED_RATIO = 3.0;
    
    /** 휴식 중심으로 간주하는 작업/휴식 비율 */
    public static final double REST_FOCUSED_RATIO = 1.5;
    
    // ============================================
    // 🔔 알림 관련 상수
    // ============================================
    
    /** 알림 다이얼로그 자동 닫기 시간 (초) */
    public static final int NOTIFICATION_AUTO_CLOSE_SECONDS = 10;
    
    /** 트레이 알림 표시 시간 (밀리초) */
    public static final int TRAY_NOTIFICATION_DURATION_MS = 3000;
    
    /** 스누즈 기본 분할 비율 (휴식시간의 1/2) */
    public static final int SNOOZE_RATIO_DIVISOR = 2;
    
    /** 스누즈 최소 보장 시간 (분) */
    public static final int SNOOZE_MINIMUM_GUARANTEED_MINUTES = 3;
    
    // ============================================
    // 🎵 사운드 관련 상수
    // ============================================
    
    /** 알림 사운드 볼륨 (0.0 ~ 1.0) */
    public static final float NOTIFICATION_SOUND_VOLUME = 0.7f;
    
    /** 사운드 재생 지속시간 (밀리초) */
    public static final int SOUND_DURATION_MS = 2000;
    
    // ============================================
    // 💾 저장 및 설정 관련 상수
    // ============================================
    
    /** 자동 저장 간격 (초) */
    public static final int AUTO_SAVE_INTERVAL_SECONDS = 30;
    
    /** 설정 백업 보관 개수 */
    public static final int SETTINGS_BACKUP_COUNT = 5;
    
    /** 프로필 최대 개수 */
    public static final int MAX_PROFILE_COUNT = 20;
    
    /** 프로필 이름 최대 길이 */
    public static final int MAX_PROFILE_NAME_LENGTH = 50;
    
    // ============================================
    // 🔤 문자열 포맷 관련 상수
    // ============================================
    
    /** 시간 표시 포맷 (MM:SS) */
    public static final String TIME_FORMAT = "%02d:%02d";
    
    /** 통계 표시 포맷 (소수점 1자리) */
    public static final String STATISTICS_FORMAT = "%.1f";
    
    /** 백분율 표시 포맷 (소수점 1자리 + %) */
    public static final String PERCENTAGE_FORMAT = "%.1f%%";
    
    // ============================================
    // 🏷️ 프로필 기본 이름들
    // ============================================
    
    /** 포모도로 프로필 기본 이름 */
    public static final String POMODORO_PROFILE_NAME = "포모도로";
    
    /** 장시간 작업 프로필 기본 이름 */
    public static final String LONG_WORK_PROFILE_NAME = "장시간 작업";
    
    /** 단시간 집중 프로필 기본 이름 */
    public static final String SHORT_FOCUS_PROFILE_NAME = "단시간 집중";
    
    /** 기본 프로필 이름 */
    public static final String DEFAULT_PROFILE_NAME = "기본 프로필";
    
    /** 내 프로필 기본 이름 */
    public static final String MY_PROFILE_BASE_NAME = "내 프로필";
    
    /** 복사본 접미사 */
    public static final String DUPLICATE_SUFFIX = " 복사본";
    
    // ============================================
    // 🔧 성능 최적화 관련 상수
    // ============================================
    
    /** UI 업데이트 쓰로틀링 간격 (밀리초) */
    public static final int UI_UPDATE_THROTTLE_MS = 100;
    
    /** 이벤트 큐 최대 크기 */
    public static final int EVENT_QUEUE_MAX_SIZE = 1000;
    
    /** 캐시 만료 시간 (밀리초) */
    public static final long CACHE_EXPIRY_MS = 60000; // 1분
    
    // ============================================
    // 🚫 인스턴스 생성 방지
    // ============================================
    
    private TimerConstants() {
        throw new AssertionError("TimerConstants는 인스턴스를 생성할 수 없습니다.");
    }
    
    // ============================================
    // 🛠️ 유틸리티 메서드들
    // ============================================
    
    /**
     * 분을 초로 변환
     * @param minutes 분
     * @return 초
     */
    public static int minutesToSeconds(int minutes) {
        return minutes * SECONDS_PER_MINUTE;
    }
    
    /**
     * 초를 분으로 변환 (내림)
     * @param seconds 초
     * @return 분
     */
    public static int secondsToMinutes(int seconds) {
        return seconds / SECONDS_PER_MINUTE;
    }
    
    /**
     * 시간 포맷팅 (MM:SS)
     * @param totalSeconds 총 초
     * @return 포맷팅된 시간 문자열
     */
    public static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / SECONDS_PER_MINUTE;
        int seconds = totalSeconds % SECONDS_PER_MINUTE;
        return String.format(TIME_FORMAT, minutes, seconds);
    }
    
    /**
     * 작업 시간이 유효한지 검사
     * @param minutes 작업 시간 (분)
     * @return 유효성 여부
     */
    public static boolean isValidWorkMinutes(int minutes) {
        return minutes >= MIN_WORK_MINUTES && minutes <= MAX_WORK_MINUTES;
    }
    
    /**
     * 휴식 시간이 유효한지 검사
     * @param minutes 휴식 시간 (분)
     * @return 유효성 여부
     */
    public static boolean isValidBreakMinutes(int minutes) {
        return minutes >= MIN_BREAK_MINUTES && minutes <= MAX_BREAK_MINUTES;
    }
    
    /**
     * 스누즈 시간이 유효한지 검사
     * @param minutes 스누즈 시간 (분)
     * @return 유효성 여부
     */
    public static boolean isValidSnoozeMinutes(int minutes) {
        return minutes >= MIN_SNOOZE_MINUTES && minutes <= MAX_SNOOZE_MINUTES;
    }
    
    /**
     * 작업 시간에 따른 카테고리 반환
     * @param workMinutes 작업 시간 (분)
     * @return 작업 시간 카테고리
     */
    public static String getWorkTimeCategory(int workMinutes) {
        if (workMinutes <= SHORT_WORK_THRESHOLD) {
            return "짧은 집중";
        } else if (workMinutes <= MEDIUM_WORK_THRESHOLD) {
            return "표준 작업";
        } else {
            return "장시간 작업";
        }
    }
    
    /**
     * 스마트 스누즈 시간 계산
     * @param breakMinutes 휴식 시간
     * @return 추천 스누즈 시간
     */
    public static int calculateSmartSnoozeMinutes(int breakMinutes) {
        int calculated = Math.max(SNOOZE_MINIMUM_GUARANTEED_MINUTES, breakMinutes / SNOOZE_RATIO_DIVISOR);
        return Math.min(calculated, MAX_SNOOZE_MINUTES);
    }
}
