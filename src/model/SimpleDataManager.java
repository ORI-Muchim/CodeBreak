package model;

import constants.TimerConstants;
import constants.UIConstants;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 간단하고 명확한 데이터 저장/로드 관리자
 * 복잡한 JSON 파싱 대신 Properties 파일 형태로 저장
 */
public class SimpleDataManager {
    private static final String DATA_DIR = "data";
    private static final String PROFILES_FILE = "profiles.properties";
    private static final String SETTINGS_FILE = "settings.properties";
    
    public SimpleDataManager() {
        createDataDirectory();
    }
    
    /**
     * 데이터 디렉토리 생성
     */
    private void createDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("✅ 데이터 폴더 생성: " + dataPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("❌ 데이터 디렉토리 생성 실패: " + e.getMessage());
        }
    }
    
    // ============================================
    // 📁 프로필 저장/로드 (단순화된 방식)
    // ============================================
    
    /**
     * 프로필들을 간단한 형태로 저장
     * 각 프로필을 섹션으로 구분해서 저장합니다
     */
    public void saveProfiles(List<UserProfile> profiles) {
        try {
            StringBuilder content = new StringBuilder();
            content.append("# CodeBreak 프로필 설정 파일\n");
            content.append("# 생성일: ").append(new Date()).append("\n\n");
            
            for (int i = 0; i < profiles.size(); i++) {
                UserProfile profile = profiles.get(i);
                content.append("[프로필").append(i).append("]\n");
                content.append("이름=").append(profile.getProfileName()).append("\n");
                content.append("작업시간=").append(profile.getWorkMinutes()).append("\n");
                content.append("휴식시간=").append(profile.getBreakMinutes()).append("\n");
                content.append("포모도로모드=").append(profile.isPomodoroMode()).append("\n");
                content.append("소리알림=").append(profile.isSoundEnabled()).append("\n");
                content.append("팝업알림=").append(profile.isPopupEnabled()).append("\n");
                content.append("화면깜빡임=").append(profile.isFlashEnabled()).append("\n");
                content.append("스누즈시간=").append(profile.getSnoozeMinutes()).append("\n");
                content.append("자동시작=").append(profile.isAutoStart()).append("\n");
                content.append("트레이최소화=").append(profile.isMinimizeToTray()).append("\n");
                
                // 활성화된 알림 유형들 (쉼표로 구분)
                StringBuilder notifications = new StringBuilder();
                Map<TimerModel.NotificationType, Boolean> settings = profile.getNotificationSettings();
                for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
                    if (settings.getOrDefault(type, false)) {
                        if (notifications.length() > 0) notifications.append(",");
                        notifications.append(type.name());
                    }
                }
                content.append("활성알림=").append(notifications.toString()).append("\n");
                content.append("\n");
            }
            
            writeToFile(Paths.get(DATA_DIR, PROFILES_FILE), content.toString());
            System.out.println("✅ 프로필 저장 완료: " + profiles.size() + "개");
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 프로필들을 간단한 형태로 로드
     */
    public List<UserProfile> loadProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        
        try {
            Path profilesPath = Paths.get(DATA_DIR, PROFILES_FILE);
            if (!Files.exists(profilesPath)) {
                System.out.println("ℹ️ 프로필 파일이 없어서 기본 프로필들을 생성합니다");
                return createDefaultProfiles();
            }
            
            String content = readFromFile(profilesPath);
            profiles = parseProfilesFromSimpleFormat(content);
            
            if (profiles.isEmpty()) {
                System.out.println("⚠️ 프로필을 읽을 수 없어서 기본 프로필들을 생성합니다");
                return createDefaultProfiles();
            }
            
            System.out.println("✅ 프로필 로드 완료: " + profiles.size() + "개");
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 로드 실패: " + e.getMessage());
            return createDefaultProfiles();
        }
        
        return profiles;
    }
    
    /**
     * 간단한 형식으로 프로필 파싱
     */
    private List<UserProfile> parseProfilesFromSimpleFormat(String content) {
        List<UserProfile> profiles = new ArrayList<>();
        String[] lines = content.split("\\n");
        UserProfile currentProfile = null;
        
        for (String line : lines) {
            line = line.trim();
            
            // 주석이나 빈 줄 건너뛰기
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            // 새 프로필 섹션 시작
            if (line.startsWith("[프로필") && line.endsWith("]")) {
                if (currentProfile != null && currentProfile.isValid()) {
                    profiles.add(currentProfile);
                }
                currentProfile = new UserProfile();
                continue;
            }
            
            // 속성 파싱
            if (currentProfile != null && line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    parseProfileProperty(currentProfile, key, value);
                }
            }
        }
        
        // 마지막 프로필 추가
        if (currentProfile != null && currentProfile.isValid()) {
            profiles.add(currentProfile);
        }
        
        return profiles;
    }
    
    /**
     * 개별 프로필 속성 파싱
     */
    private void parseProfileProperty(UserProfile profile, String key, String value) {
        try {
            switch (key) {
                case "이름":
                    profile.setProfileName(value);
                    break;
                case "작업시간":
                    profile.setWorkMinutes(Integer.parseInt(value));
                    break;
                case "휴식시간":
                    profile.setBreakMinutes(Integer.parseInt(value));
                    break;
                case "포모도로모드":
                    profile.setPomodoroMode(Boolean.parseBoolean(value));
                    break;
                case "소리알림":
                    profile.setSoundEnabled(Boolean.parseBoolean(value));
                    break;
                case "팝업알림":
                    profile.setPopupEnabled(Boolean.parseBoolean(value));
                    break;
                case "화면깜빡임":
                    profile.setFlashEnabled(Boolean.parseBoolean(value));
                    break;
                case "스누즈시간":
                    profile.setSnoozeMinutes(Integer.parseInt(value));
                    break;
                case "자동시작":
                    profile.setAutoStart(Boolean.parseBoolean(value));
                    break;
                case "트레이최소화":
                    profile.setMinimizeToTray(Boolean.parseBoolean(value));
                    break;
                case "활성알림":
                    parseNotificationSettings(profile, value);
                    break;
                default:
                    System.out.println("⚠️ 알 수 없는 프로필 속성: " + key);
            }
        } catch (Exception e) {
            System.err.println("❌ 속성 파싱 실패 [" + key + "=" + value + "]: " + e.getMessage());
        }
    }
    
    /**
     * 알림 설정 파싱 (쉼표로 구분된 값들)
     */
    private void parseNotificationSettings(UserProfile profile, String value) {
        Map<TimerModel.NotificationType, Boolean> settings = new HashMap<>();
        
        // 모든 알림을 비활성화로 시작
        for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
            settings.put(type, false);
        }
        
        // 쉼표로 구분된 활성 알림들 파싱
        if (!value.isEmpty()) {
            String[] enabledTypes = value.split(",");
            for (String typeName : enabledTypes) {
                try {
                    TimerModel.NotificationType type = TimerModel.NotificationType.valueOf(typeName.trim());
                    settings.put(type, true);
                } catch (Exception e) {
                    System.err.println("⚠️ 알 수 없는 알림 유형: " + typeName);
                }
            }
        }
        
        profile.setNotificationSettings(settings);
    }
    
    // ============================================
    // ⚙️ 설정 저장/로드 (단순화된 방식)
    // ============================================
    
    /**
     * 애플리케이션 설정을 간단한 형태로 저장
     */
    public void saveSettings(Map<String, Object> settings) {
        try {
            StringBuilder content = new StringBuilder();
            content.append("# CodeBreak 애플리케이션 설정\n");
            content.append("# 생성일: ").append(new Date()).append("\n\n");
            
            content.append("[애플리케이션]\n");
            for (Map.Entry<String, Object> entry : settings.entrySet()) {
                content.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            
            writeToFile(Paths.get(DATA_DIR, SETTINGS_FILE), content.toString());
            System.out.println("✅ 설정 저장 완료");
            
        } catch (Exception e) {
            System.err.println("❌ 설정 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 애플리케이션 설정을 간단한 형태로 로드
     */
    public Map<String, Object> loadSettings() {
        try {
            Path settingsPath = Paths.get(DATA_DIR, SETTINGS_FILE);
            if (!Files.exists(settingsPath)) {
                System.out.println("ℹ️ 설정 파일이 없어서 기본 설정을 생성합니다");
                return getDefaultSettings();
            }
            
            String content = readFromFile(settingsPath);
            Map<String, Object> settings = parseSettingsFromSimpleFormat(content);
            
            System.out.println("✅ 설정 로드 완료");
            return settings;
            
        } catch (Exception e) {
            System.err.println("❌ 설정 로드 실패: " + e.getMessage());
            return getDefaultSettings();
        }
    }
    
    /**
     * 간단한 형식으로 설정 파싱
     */
    private Map<String, Object> parseSettingsFromSimpleFormat(String content) {
        Map<String, Object> settings = new HashMap<>();
        String[] lines = content.split("\\n");
        
        for (String line : lines) {
            line = line.trim();
            
            // 주석이나 빈 줄, 섹션 헤더 건너뛰기
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("[")) continue;
            
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    // 값 타입 자동 판별
                    Object parsedValue = parseValue(value);
                    settings.put(key, parsedValue);
                }
            }
        }
        
        return settings;
    }
    
    /**
     * 문자열 값을 적절한 타입으로 변환
     */
    private Object parseValue(String value) {
        // boolean 타입
        if ("true".equals(value) || "false".equals(value)) {
            return Boolean.parseBoolean(value);
        }
        
        // 정수 타입
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 정수가 아니면 문자열로 처리
        }
        
        // 기본적으로 문자열
        return value;
    }
    
    // ============================================
    // 🛠️ 유틸리티 메서드들
    // ============================================
    
    /**
     * 기본 프로필들 생성
     */
    private List<UserProfile> createDefaultProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        profiles.add(UserProfile.createPomodoroProfile());
        profiles.add(UserProfile.createLongWorkProfile());
        profiles.add(UserProfile.createShortFocusProfile());
        return profiles;
    }
    
    /**
     * 기본 설정 반환
     */
    private Map<String, Object> getDefaultSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("selectedProfile", TimerConstants.POMODORO_PROFILE_NAME);
        settings.put("windowWidth", UIConstants.SETTINGS_DEFAULT_WIDTH);
        settings.put("windowHeight", UIConstants.SETTINGS_DEFAULT_HEIGHT);
        settings.put("windowX", UIConstants.DEFAULT_WINDOW_X);
        settings.put("windowY", UIConstants.DEFAULT_WINDOW_Y);
        settings.put("startMinimized", false);
        return settings;
    }
    
    /**
     * 파일에 텍스트 쓰기
     */
    private void writeToFile(Path path, String content) throws IOException {
        Files.write(path, content.getBytes("UTF-8"), 
                   StandardOpenOption.CREATE, 
                   StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    /**
     * 파일에서 텍스트 읽기
     */
    private String readFromFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path), "UTF-8");
    }
    
    // ============================================
    // 🔍 디버깅 및 상태 확인
    // ============================================
    
    /**
     * 현재 저장된 데이터 상태 출력 (디버깅용)
     */
    public void printDataStatus() {
        System.out.println("\n=== 📊 데이터 상태 확인 ===");
        
        Path dataDir = Paths.get(DATA_DIR);
        System.out.println("데이터 폴더: " + dataDir.toAbsolutePath());
        System.out.println("폴더 존재: " + Files.exists(dataDir));
        
        Path profilesFile = Paths.get(DATA_DIR, PROFILES_FILE);
        System.out.println("프로필 파일: " + profilesFile.getFileName());
        System.out.println("파일 존재: " + Files.exists(profilesFile));
        if (Files.exists(profilesFile)) {
            try {
                long size = Files.size(profilesFile);
                System.out.println("파일 크기: " + size + " bytes");
            } catch (IOException e) {
                System.out.println("파일 크기 확인 실패");
            }
        }
        
        Path settingsFile = Paths.get(DATA_DIR, SETTINGS_FILE);
        System.out.println("설정 파일: " + settingsFile.getFileName());
        System.out.println("파일 존재: " + Files.exists(settingsFile));
        if (Files.exists(settingsFile)) {
            try {
                long size = Files.size(settingsFile);
                System.out.println("파일 크기: " + size + " bytes");
            } catch (IOException e) {
                System.out.println("파일 크기 확인 실패");
            }
        }
        
        System.out.println("========================\n");
    }
    
    /**
     * 저장된 프로필 파일 내용을 콘솔에 출력 (디버깅용)
     */
    public void printSavedProfiles() {
        try {
            Path profilesPath = Paths.get(DATA_DIR, PROFILES_FILE);
            if (Files.exists(profilesPath)) {
                System.out.println("\n=== 📁 저장된 프로필 파일 내용 ===");
                String content = readFromFile(profilesPath);
                System.out.println(content);
                System.out.println("==================================\n");
            } else {
                System.out.println("프로필 파일이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            System.err.println("프로필 파일 읽기 실패: " + e.getMessage());
        }
    }
}
