package model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * JSON 파일 읽기/쓰기를 관리하는 클래스
 * 간단한 JSON 파싱 기능을 직접 구현
 */
public class JsonDataManager {
    private static final String DATA_DIR = "data";
    private static final String PROFILES_FILE = "profiles.json";
    private static final String SETTINGS_FILE = "settings.json";
    
    public JsonDataManager() {
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
            }
        } catch (IOException e) {
            System.err.println("데이터 디렉토리 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 프로필을 JSON 파일에 저장
     */
    public void saveProfiles(List<UserProfile> profiles) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"profiles\": [\n");
            
            for (int i = 0; i < profiles.size(); i++) {
                UserProfile profile = profiles.get(i);
                json.append("    {\n");
                json.append("      \"profileName\": \"").append(escapeJson(profile.getProfileName())).append("\",\n");
                json.append("      \"workMinutes\": ").append(profile.getWorkMinutes()).append(",\n");
                json.append("      \"breakMinutes\": ").append(profile.getBreakMinutes()).append(",\n");
                json.append("      \"pomodoroMode\": ").append(profile.isPomodoroMode()).append(",\n");
                json.append("      \"soundEnabled\": ").append(profile.isSoundEnabled()).append(",\n");
                json.append("      \"popupEnabled\": ").append(profile.isPopupEnabled()).append(",\n");
                json.append("      \"flashEnabled\": ").append(profile.isFlashEnabled()).append(",\n");
                json.append("      \"snoozeMinutes\": ").append(profile.getSnoozeMinutes()).append(",\n");
                json.append("      \"autoStart\": ").append(profile.isAutoStart()).append(",\n");
                json.append("      \"minimizeToTray\": ").append(profile.isMinimizeToTray()).append(",\n");
                
                // 활성화된 알림 유형들
                json.append("      \"enabledNotifications\": [");
                Map<TimerModel.NotificationType, Boolean> notificationSettings = profile.getNotificationSettings();
                List<String> enabledTypes = new ArrayList<>();
                for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
                    if (notificationSettings.getOrDefault(type, false)) {
                        enabledTypes.add(type.name());
                    }
                }
                for (int j = 0; j < enabledTypes.size(); j++) {
                    json.append("\"").append(enabledTypes.get(j)).append("\"");
                    if (j < enabledTypes.size() - 1) json.append(", ");
                }
                json.append("]");
                json.append("\n");
                
                json.append("    }");
                if (i < profiles.size() - 1) json.append(",");
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}");
            
            writeToFile(Paths.get(DATA_DIR, PROFILES_FILE), json.toString());
            
        } catch (Exception e) {
            System.err.println("프로필 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * JSON 파일에서 사용자 프로필 로드
     */
    public List<UserProfile> loadProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        
        try {
            Path profilesPath = Paths.get(DATA_DIR, PROFILES_FILE);
            System.out.println("🔍 프로필 파일 경로: " + profilesPath.toAbsolutePath());
            
            if (!Files.exists(profilesPath)) {
                System.out.println("ℹ️ 프로필 파일이 없어서 기본 프로필들을 생성합니다");
                // 기본 프로필들 생성
                profiles.add(UserProfile.createPomodoroProfile());
                profiles.add(UserProfile.createLongWorkProfile());
                profiles.add(UserProfile.createShortFocusProfile());
                System.out.println("✅ 기본 프로필 3개 생성 완료");
                return profiles;
            }
            
            System.out.println("📖 프로필 파일 읽기 시작...");
            String content = readFromFile(profilesPath);
            System.out.println("📝 파일 내용 길이: " + content.length() + " 문자");
            
            profiles = parseProfilesFromJson(content);
            System.out.println("🔄 파싱 결과: " + profiles.size() + "개 프로필");
            
            if (profiles.isEmpty()) {
                System.out.println("⚠️ 파싱된 프로필이 없어서 기본 프로필들을 생성합니다");
                profiles.add(UserProfile.createPomodoroProfile());
                profiles.add(UserProfile.createLongWorkProfile());
                profiles.add(UserProfile.createShortFocusProfile());
                System.out.println("✅ 기본 프로필 3개 생성 완료");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 로드 실패: " + e.getMessage());
            e.printStackTrace();
            // 에러 시 기본 프로필 반환
            profiles.clear();
            profiles.add(UserProfile.createPomodoroProfile());
            profiles.add(UserProfile.createLongWorkProfile());
            profiles.add(UserProfile.createShortFocusProfile());
            System.out.println("🔧 에러 복구: 기본 프로필 3개 생성");
        }
        
        return profiles;
    }
    
    /**
     * 애플리케이션 설정 저장
     */
    public void saveSettings(Map<String, Object> settings) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            
            int count = 0;
            for (Map.Entry<String, Object> entry : settings.entrySet()) {
                json.append("  \"").append(escapeJson(entry.getKey())).append("\": ");
                
                Object value = entry.getValue();
                if (value instanceof String) {
                    json.append("\"").append(escapeJson((String) value)).append("\"");
                } else if (value instanceof Boolean || value instanceof Number) {
                    json.append(value);
                } else {
                    json.append("\"").append(escapeJson(value.toString())).append("\"");
                }
                
                if (count < settings.size() - 1) json.append(",");
                json.append("\n");
                count++;
            }
            
            json.append("}");
            
            writeToFile(Paths.get(DATA_DIR, SETTINGS_FILE), json.toString());
            
        } catch (Exception e) {
            System.err.println("설정 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 애플리케이션 설정 로드
     */
    public Map<String, Object> loadSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        try {
            Path settingsPath = Paths.get(DATA_DIR, SETTINGS_FILE);
            if (!Files.exists(settingsPath)) {
                return getDefaultSettings();
            }
            
            String content = readFromFile(settingsPath);
            settings = parseSettingsFromJson(content);
            
        } catch (Exception e) {
            System.err.println("설정 로드 실패: " + e.getMessage());
            return getDefaultSettings();
        }
        
        return settings;
    }
    
    /**
     * 기본 설정 반환
     */
    private Map<String, Object> getDefaultSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("selectedProfile", "포모도로");
        settings.put("windowWidth", 400);
        settings.put("windowHeight", 300);
        settings.put("windowX", 100);
        settings.put("windowY", 100);
        settings.put("startMinimized", false);
        return settings;
    }
    
    /**
     * JSON에서 프로필 목록 파싱
     */
    private List<UserProfile> parseProfilesFromJson(String json) {
        List<UserProfile> profiles = new ArrayList<>();
        
        try {
            System.out.println("🔍 JSON 파싱 시작...");
            System.out.println("📋 JSON 내용 미리보기: " + json.substring(0, Math.min(200, json.length())) + "...");
            
            // 간단한 JSON 파싱 (정규식 사용)
            String profilesSection = extractJsonArray(json, "profiles");
            System.out.println("📦 profiles 섹션 길이: " + profilesSection.length());
            System.out.println("📋 profiles 섹션 미리보기: " + profilesSection.substring(0, Math.min(100, profilesSection.length())) + "...");
            
            if (profilesSection.isEmpty()) {
                System.out.println("❌ profiles 섹션이 비어있습니다!");
                return profiles;
            }
            
            String[] profileStrings = splitJsonObjects(profilesSection);
            System.out.println("✂️ 분할된 프로필 객체 수: " + profileStrings.length);
            
            for (int i = 0; i < profileStrings.length; i++) {
                String profileStr = profileStrings[i];
                System.out.println("🔄 프로필 " + (i+1) + " 파싱 시작: " + profileStr.substring(0, Math.min(50, profileStr.length())) + "...");
                
                UserProfile profile = parseProfile(profileStr);
                if (profile != null) {
                    profiles.add(profile);
                    System.out.println("✅ 프로필 파싱 성공: " + profile.getProfileName());
                } else {
                    System.out.println("❌ 프로필 파싱 실패: " + (i+1));
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ JSON 파싱 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return profiles;
    }
    
    /**
     * 개별 프로필 파싱
     */
    private UserProfile parseProfile(String jsonStr) {
        try {
            UserProfile profile = new UserProfile();
            
            profile.setProfileName(extractJsonString(jsonStr, "profileName"));
            profile.setWorkMinutes(extractJsonInt(jsonStr, "workMinutes"));
            profile.setBreakMinutes(extractJsonInt(jsonStr, "breakMinutes"));
            profile.setPomodoroMode(extractJsonBoolean(jsonStr, "pomodoroMode"));
            profile.setSoundEnabled(extractJsonBoolean(jsonStr, "soundEnabled"));
            profile.setPopupEnabled(extractJsonBoolean(jsonStr, "popupEnabled"));
            profile.setFlashEnabled(extractJsonBoolean(jsonStr, "flashEnabled"));
            profile.setSnoozeMinutes(extractJsonInt(jsonStr, "snoozeMinutes"));
            profile.setAutoStart(extractJsonBoolean(jsonStr, "autoStart"));
            profile.setMinimizeToTray(extractJsonBoolean(jsonStr, "minimizeToTray"));
            
            // 알림 유형 파싱
            String notificationsStr = extractJsonArray(jsonStr, "enabledNotifications");
            String[] notificationNames = splitJsonArrayValues(notificationsStr);
            
            // 모든 알림을 비활성화로 시작
            Map<TimerModel.NotificationType, Boolean> notificationSettings = new HashMap<>();
            for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
                notificationSettings.put(type, false);
            }
            
            // JSON에서 가져온 활성화된 알림들을 설정
            for (String name : notificationNames) {
                try {
                    TimerModel.NotificationType type = TimerModel.NotificationType.valueOf(name.trim());
                    notificationSettings.put(type, true);
                } catch (Exception e) {
                    // 잘못된 알림 유형은 무시
                    System.err.println("알 수 없는 알림 유형: " + name);
                }
            }
            
            // 프로필에 알림 설정 적용
            profile.setNotificationSettings(notificationSettings);
            
            return profile;
            
        } catch (Exception e) {
            System.err.println("프로필 파싱 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * JSON에서 설정 파싱
     */
    private Map<String, Object> parseSettingsFromJson(String json) {
        Map<String, Object> settings = new HashMap<>();
        
        // 간단한 키-값 파싱
        String[] lines = json.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.contains(":") && !line.startsWith("{") && !line.startsWith("}")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim().replaceAll("\"", "");
                    String value = parts[1].trim().replaceAll("[,}]", "");
                    
                    // 값 타입 추론
                    if (value.equals("true") || value.equals("false")) {
                        settings.put(key, Boolean.parseBoolean(value));
                    } else if (value.matches("-?\\d+")) {
                        settings.put(key, Integer.parseInt(value));
                    } else {
                        settings.put(key, value.replaceAll("\"", ""));
                    }
                }
            }
        }
        
        return settings;
    }
    
    // 유틸리티 메서드들
    private String extractJsonString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }
    
    private int extractJsonInt(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
    
    private boolean extractJsonBoolean(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? Boolean.parseBoolean(m.group(1)) : false;
    }
    
    private String extractJsonArray(String json, String key) {
        System.out.println("🔍 배열 추출 시작: '" + key + "'");
        
        // "profiles": [ 를 찾습니다
        String startPattern = "\"" + key + "\"\\s*:\\s*\\[";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(startPattern);
        java.util.regex.Matcher m = p.matcher(json);
        
        if (!m.find()) {
            System.out.println("❌ 시작 패턴을 찾을 수 없습니다: " + startPattern);
            return "";
        }
        
        int startIndex = m.end(); // [ 다음 위치
        System.out.println("✅ 시작 위치 발견: " + startIndex);
        
        // 이제 [ 부터 매칭되는 ] 를 찾습니다
        int bracketCount = 1; // 이미 [ 하나를 시작했습니다
        int endIndex = -1;
        
        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') {
                bracketCount++;
            } else if (c == ']') {
                bracketCount--;
                if (bracketCount == 0) {
                    endIndex = i;
                    break;
                }
            }
        }
        
        if (endIndex == -1) {
            System.out.println("❌ 매칭되는 ] 를 찾을 수 없습니다");
            return "";
        }
        
        String result = json.substring(startIndex, endIndex);
        System.out.println("✅ 배열 추출 성공: " + result.length() + " 문자");
        System.out.println("📝 추출된 내용 미리보기: " + result.substring(0, Math.min(100, result.length())) + "...");
        
        return result;
    }
    
    private String[] splitJsonObjects(String jsonArray) {
        System.out.println("✂️ JSON 객체 분할 시작...");
        System.out.println("📜 입력 내용 길이: " + jsonArray.length());
        System.out.println("📜 입력 내용: " + jsonArray.substring(0, Math.min(150, jsonArray.length())) + "...");
        
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);
            current.append(c);
            
            if (c == '{') {
                braceCount++;
                System.out.println("🔍 인덱스 " + i + ": '{' 발견, braceCount = " + braceCount);
            } else if (c == '}') {
                braceCount--;
                System.out.println("🔍 인덱스 " + i + ": '}' 발견, braceCount = " + braceCount);
                
                if (braceCount == 0) {
                    String obj = current.toString().trim();
                    System.out.println("✅ 객체 " + (objects.size() + 1) + " 추출 완료: " + obj.length() + " 문자");
                    System.out.println("📝 객체 내용: " + obj.substring(0, Math.min(100, obj.length())) + "...");
                    objects.add(obj);
                    current = new StringBuilder();
                }
            }
        }
        
        System.out.println("🏁 최종 결과: " + objects.size() + "개 객체 분할 완료");
        
        return objects.toArray(new String[0]);
    }
    
    private String[] splitJsonArrayValues(String arrayContent) {
        if (arrayContent == null || arrayContent.trim().isEmpty()) {
            return new String[0];
        }
        
        String[] values = arrayContent.split(",");
        // 따옴표 제거 및 공백 제거
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim().replaceAll("^\"|\"$", ""); // 양쪽 따옴표 제거
        }
        
        return values;
    }
    
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    private void writeToFile(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    private String readFromFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
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
    
    // ============================================
    // 📤📥 외부 파일 내보내기/불러오기 기능
    // ============================================
    
    /**
     * 📤 프로필을 JSON 파일로 내보내기
     */
    public void exportProfilesToFile(List<UserProfile> profiles, String filePath) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"export_info\": {\n");
        json.append("    \"app_name\": \"CodeBreak\",\n");
        json.append("    \"version\": \"1.0\",\n");
        json.append("    \"export_date\": \"").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("\",\n");
        json.append("    \"profile_count\": ").append(profiles.size()).append("\n");
        json.append("  },\n");
        json.append("  \"profiles\": [\n");
        
        for (int i = 0; i < profiles.size(); i++) {
            UserProfile profile = profiles.get(i);
            json.append("    {\n");
            json.append("      \"profileName\": \"").append(escapeJson(profile.getProfileName())).append("\",\n");
            json.append("      \"workMinutes\": ").append(profile.getWorkMinutes()).append(",\n");
            json.append("      \"breakMinutes\": ").append(profile.getBreakMinutes()).append(",\n");
            json.append("      \"pomodoroMode\": ").append(profile.isPomodoroMode()).append(",\n");
            json.append("      \"soundEnabled\": ").append(profile.isSoundEnabled()).append(",\n");
            json.append("      \"popupEnabled\": ").append(profile.isPopupEnabled()).append(",\n");
            json.append("      \"flashEnabled\": ").append(profile.isFlashEnabled()).append(",\n");
            json.append("      \"snoozeMinutes\": ").append(profile.getSnoozeMinutes()).append(",\n");
            json.append("      \"autoStart\": ").append(profile.isAutoStart()).append(",\n");
            json.append("      \"minimizeToTray\": ").append(profile.isMinimizeToTray()).append(",\n");
            
            // 활성화된 알림 유형들
            json.append("      \"enabledNotifications\": [");
            Map<TimerModel.NotificationType, Boolean> notificationSettings = profile.getNotificationSettings();
            List<String> enabledTypes = new ArrayList<>();
            for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
                if (notificationSettings.getOrDefault(type, false)) {
                    enabledTypes.add(type.name());
                }
            }
            for (int j = 0; j < enabledTypes.size(); j++) {
                json.append("\"").append(enabledTypes.get(j)).append("\"");
                if (j < enabledTypes.size() - 1) json.append(", ");
            }
            json.append("]\n");
            
            json.append("    }");
            if (i < profiles.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}");
        
        writeToFile(Paths.get(filePath), json.toString());
        System.out.println("✅ 프로필을 JSON 파일로 내보내기 완료: " + filePath);
    }
    
    /**
     * 📤 프로필을 텍스트 파일로 내보내기 (사람이 읽기 쉬운 형식)
     */
    public void exportProfilesToTextFile(List<UserProfile> profiles, String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("=== CodeBreak 설정 백업 ===\n");
        content.append("생성일시: ").append(new java.util.Date()).append("\n\n");
        
        content.append("=== 프로필 정보 ===\n");
        for (UserProfile profile : profiles) {
            content.append("프로필: ").append(profile.getProfileName()).append("\n");
            content.append("  작업시간: ").append(profile.getWorkMinutes()).append("분\n");
            content.append("  휴식시간: ").append(profile.getBreakMinutes()).append("분\n");
            content.append("  포모도로 모드: ").append(profile.isPomodoroMode() ? "예" : "아니오").append("\n");
            content.append("  소리 알림: ").append(profile.isSoundEnabled() ? "예" : "아니오").append("\n");
            content.append("  팝업 알림: ").append(profile.isPopupEnabled() ? "예" : "아니오").append("\n");
            content.append("  화면 깜빡임: ").append(profile.isFlashEnabled() ? "예" : "아니오").append("\n");
            content.append("  스누즈 시간: ").append(profile.getSnoozeMinutes()).append("분\n");
            content.append("  자동 시작: ").append(profile.isAutoStart() ? "예" : "아니오").append("\n");
            content.append("  트레이 최소화: ").append(profile.isMinimizeToTray() ? "예" : "아니오").append("\n");
            
            // 알림 유형들
            content.append("  활성화된 알림: ");
            Map<TimerModel.NotificationType, Boolean> notificationSettings = profile.getNotificationSettings();
            List<String> enabledTypes = new ArrayList<>();
            for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
                if (notificationSettings.getOrDefault(type, false)) {
                    enabledTypes.add(type.getName());
                }
            }
            content.append(String.join(", ", enabledTypes)).append("\n");
            content.append("\n");
        }
        
        writeToFile(Paths.get(filePath), content.toString());
        System.out.println("✅ 프로필을 텍스트 파일로 내보내기 완료: " + filePath);
    }
    
    /**
     * 📥 JSON 파일에서 프로필 불러오기
     */
    public List<UserProfile> importProfilesFromJsonFile(String filePath) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new IOException("파일을 찾을 수 없습니다: " + filePath);
        }
        
        String content = readFromFile(Paths.get(filePath));
        System.out.println("📖 JSON 파일 읽기 완료: " + filePath);
        
        // JSON 형식 확인
        if (!content.trim().startsWith("{")) {
            throw new IOException("올바른 JSON 형식이 아닙니다.");
        }
        
        try {
            List<UserProfile> profiles = parseProfilesFromJson(content);
            
            if (profiles.isEmpty()) {
                throw new IOException("불러올 수 있는 프로필이 없습니다.");
            }
            
            System.out.println("✅ JSON 파일에서 " + profiles.size() + "개 프로필 불러오기 완료");
            return profiles;
            
        } catch (Exception e) {
            throw new IOException("JSON 파일 파싱 실패: " + e.getMessage());
        }
    }
    
    /**
     * 📥 텍스트 파일에서 프로필 불러오기 (간단한 형식)
     */
    public List<UserProfile> importProfilesFromTextFile(String filePath) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new IOException("파일을 찾을 수 없습니다: " + filePath);
        }
        
        String content = readFromFile(Paths.get(filePath));
        System.out.println("📖 텍스트 파일 읽기 완료: " + filePath);
        
        List<UserProfile> profiles = new ArrayList<>();
        
        try {
            String[] lines = content.split("\n");
            UserProfile currentProfile = null;
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.startsWith("프로필: ")) {
                    // 이전 프로필 저장
                    if (currentProfile != null) {
                        profiles.add(currentProfile);
                    }
                    
                    // 새 프로필 시작
                    String profileName = line.substring(4).trim();
                    currentProfile = new UserProfile(profileName);
                    
                } else if (currentProfile != null && line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        
                        parseTextProfileProperty(currentProfile, key, value);
                    }
                }
            }
            
            // 마지막 프로필 저장
            if (currentProfile != null) {
                profiles.add(currentProfile);
            }
            
            if (profiles.isEmpty()) {
                throw new IOException("불러올 수 있는 프로필이 없습니다.");
            }
            
            System.out.println("✅ 텍스트 파일에서 " + profiles.size() + "개 프로필 불러오기 완료");
            return profiles;
            
        } catch (Exception e) {
            throw new IOException("텍스트 파일 파싱 실패: " + e.getMessage());
        }
    }
    
    /**
     * 텍스트 파일의 프로필 속성 파싱
     */
    private void parseTextProfileProperty(UserProfile profile, String key, String value) {
        try {
            switch (key) {
                case "작업시간":
                    profile.setWorkMinutes(Integer.parseInt(value.replace("분", "").trim()));
                    break;
                case "휴식시간":
                    profile.setBreakMinutes(Integer.parseInt(value.replace("분", "").trim()));
                    break;
                case "포모도로 모드":
                    profile.setPomodoroMode("예".equals(value) || "true".equalsIgnoreCase(value));
                    break;
                case "소리 알림":
                    profile.setSoundEnabled("예".equals(value) || "true".equalsIgnoreCase(value));
                    break;
                case "팝업 알림":
                    profile.setPopupEnabled("예".equals(value) || "true".equalsIgnoreCase(value));
                    break;
                case "화면 깜빡임":
                    profile.setFlashEnabled("예".equals(value) || "true".equalsIgnoreCase(value));
                    break;
                case "스누즈 시간":
                    profile.setSnoozeMinutes(Integer.parseInt(value.replace("분", "").trim()));
                    break;
                case "자동 시작":
                    profile.setAutoStart("예".equals(value) || "true".equalsIgnoreCase(value));
                    break;
                case "트레이 최소화":
                    profile.setMinimizeToTray("예".equals(value) || "true".equalsIgnoreCase(value));
                    break;
                case "활성화된 알림":
                    parseNotificationTypes(profile, value);
                    break;
                default:
                    System.out.println("⚠️ 알 수 없는 속성: " + key);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ 속성 파싱 실패: " + key + " = " + value + " (" + e.getMessage() + ")");
        }
    }
    
    /**
     * 알림 유형 파싱
     */
    private void parseNotificationTypes(UserProfile profile, String value) {
        Map<TimerModel.NotificationType, Boolean> notificationSettings = new HashMap<>();
        
        // 모든 알림을 비활성화로 시작
        for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
            notificationSettings.put(type, false);
        }
        
        if (value != null && !value.trim().isEmpty()) {
            String[] typeNames = value.split(",");
            for (String typeName : typeNames) {
                String trimmedName = typeName.trim();
                
                // 한글 이름으로 매칭
                for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
                    if (type.getName().equals(trimmedName)) {
                        notificationSettings.put(type, true);
                        break;
                    }
                }
            }
        }
        
        profile.setNotificationSettings(notificationSettings);
    }
    
    /**
     * 🔍 파일 형식 자동 감지 및 불러오기
     */
    public List<UserProfile> importProfilesFromFile(String filePath) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new IOException("파일을 찾을 수 없습니다: " + filePath);
        }
        
        String content = readFromFile(Paths.get(filePath));
        
        // JSON 형식인지 확인
        if (content.trim().startsWith("{") && content.contains("\"profiles\":")) {
            System.out.println("🔍 JSON 형식으로 감지됨");
            return importProfilesFromJsonFile(filePath);
        }
        // 텍스트 형식인지 확인
        else if (content.contains("=== CodeBreak 설정 백업 ===") || content.contains("프로필: ")) {
            System.out.println("🔍 텍스트 형식으로 감지됨");
            return importProfilesFromTextFile(filePath);
        }
        else {
            throw new IOException("지원하지 않는 파일 형식입니다. JSON 또는 CodeBreak 텍스트 형식만 지원됩니다.");
        }
    }
    
    /**
     * 📋 불러온 프로필들의 유효성 검증
     */
    public List<String> validateImportedProfiles(List<UserProfile> profiles) {
        List<String> issues = new ArrayList<>();
        
        for (int i = 0; i < profiles.size(); i++) {
            UserProfile profile = profiles.get(i);
            String prefix = "프로필 " + (i + 1) + " (" + profile.getProfileName() + "): ";
            
            // 이름 검증
            if (profile.getProfileName() == null || profile.getProfileName().trim().isEmpty()) {
                issues.add(prefix + "프로필 이름이 비어있습니다.");
            }
            
            // 작업시간 검증
            if (profile.getWorkMinutes() < 1 || profile.getWorkMinutes() > 999) {
                issues.add(prefix + "작업시간이 유효하지 않습니다 (1-999분): " + profile.getWorkMinutes());
            }
            
            // 휴식시간 검증
            if (profile.getBreakMinutes() < 0 || profile.getBreakMinutes() > 999) {
                issues.add(prefix + "휴식시간이 유효하지 않습니다 (0-999분): " + profile.getBreakMinutes());
            }
            
            // 스누즈시간 검증
            if (profile.getSnoozeMinutes() < 1 || profile.getSnoozeMinutes() > 30) {
                issues.add(prefix + "스누즈시간이 유효하지 않습니다 (1-30분): " + profile.getSnoozeMinutes());
            }
        }
        
        return issues;
    }
}
