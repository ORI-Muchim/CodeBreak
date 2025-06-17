package controller;

import model.*;

/**
 * 🚀 초간편 프로필 관리 헬퍼 클래스
 * 복잡한 설정 없이 몇 줄의 코드로 프로필을 생성하고 관리할 수 있습니다
 */
public class EasyProfileHelper {
    private SettingsController settingsController;
    
    public EasyProfileHelper(SettingsController settingsController) {
        this.settingsController = settingsController;
    }
    
    // ============================================
    // 🎯 원클릭 프로필 생성
    // ============================================
    
    /**
     * 💾 현재 설정을 바로 저장
     * 예: easyHelper.saveNow(); // "내 프로필 1"로 자동 저장
     */
    public UserProfile saveNow() {
        return settingsController.saveCurrentAsNewProfile(null);
    }
    
    /**
     * 💾 현재 설정을 원하는 이름으로 저장
     * 예: easyHelper.saveAs("밤샘 코딩");
     */
    public UserProfile saveAs(String name) {
        return settingsController.saveCurrentAsNewProfile(name);
    }
    
    /**
     * 📋 현재 프로필 복사
     * 예: easyHelper.copyProfile("포모도로 변형");
     */
    public UserProfile copyProfile(String newName) {
        return settingsController.duplicateCurrentProfile(newName);
    }
    
    // ============================================
    // ⚡ 빠른 프로필 생성 (프리셋)
    // ============================================
    
    /**
     * ⚡ 초고속 프로필 생성
     * 예: easyHelper.quick(25, 5); // 25분 작업, 5분 휴식
     */
    public UserProfile quick(int workMinutes, int breakMinutes) {
        return settingsController.createQuickProfile(workMinutes, breakMinutes);
    }
    
    /**
     * ⚡ 이름까지 지정해서 빠른 생성
     * 예: easyHelper.quick(45, 10, "집중 세션");
     */
    public UserProfile quick(int workMinutes, int breakMinutes, String name) {
        return settingsController.createQuickProfile(workMinutes, breakMinutes, name);
    }
    
    // ============================================
    // 🎨 자주 사용하는 프리셋들
    // ============================================
    
    /**
     * 🍅 클래식 포모도로 (25분 작업 + 5분 휴식)
     */
    public UserProfile pomodoro() {
        return quick(25, 5, "클래식 포모도로");
    }
    
    /**
     * 🔥 파워 세션 (50분 작업 + 10분 휴식)
     */
    public UserProfile powerSession() {
        return quick(50, 10, "파워 세션");
    }
    
    /**
     * ⚡ 스프린트 (15분 작업 + 3분 휴식)
     */
    public UserProfile sprint() {
        return quick(15, 3, "스프린트");
    }
    
    /**
     * 🎯 딥워크 (90분 작업 + 15분 휴식)
     */
    public UserProfile deepWork() {
        return quick(90, 15, "딥워크");
    }
    
    /**
     * 📚 학습 세션 (30분 작업 + 10분 휴식)
     */
    public UserProfile study() {
        return quick(30, 10, "학습 세션");
    }
    
    /**
     * 💼 미팅 사이 (10분 작업 + 2분 휴식)
     */
    public UserProfile quickBreak() {
        return quick(10, 2, "미팅 사이");
    }
    
    // ============================================
    // 🔄 프로필 관리
    // ============================================
    
    /**
     * 🔄 현재 설정을 기존 프로필에 업데이트
     * 예: easyHelper.updateProfile("내 프로필");
     */
    public boolean updateProfile(String profileName) {
        UserProfile profile = findProfile(profileName);
        if (profile != null) {
            return settingsController.updateProfileWithCurrentSettings(profile);
        }
        return false;
    }
    
    /**
     * 🔄 현재 프로필에 자동 저장
     */
    public void autoSave() {
        settingsController.autoSaveCurrentProfile();
    }
    
    /**
     * 🎯 프로필 빠른 전환
     * 예: easyHelper.switchTo("포모도로");
     */
    public boolean switchTo(String profileName) {
        UserProfile profile = findProfile(profileName);
        if (profile != null) {
            settingsController.setCurrentProfile(profile);
            return true;
        }
        return false;
    }
    
    /**
     * 🗑️ 안전한 프로필 삭제
     */
    public boolean deleteProfile(String profileName) {
        UserProfile profile = findProfile(profileName);
        if (profile != null) {
            return settingsController.safeDeleteProfile(profile);
        }
        return false;
    }
    
    // ============================================
    // 🛠️ 유틸리티
    // ============================================
    
    /**
     * 프로필 이름으로 찾기
     */
    private UserProfile findProfile(String name) {
        for (UserProfile profile : settingsController.getAllProfiles()) {
            if (profile.getProfileName().equals(name)) {
                return profile;
            }
        }
        return null;
    }
    
    /**
     * 📊 현재 상태 요약
     */
    public void printStatus() {
        UserProfile current = settingsController.getCurrentProfile();
        System.out.println("📊 현재 프로필: " + current.getProfileName());
        System.out.println("⏰ 작업시간: " + current.getWorkMinutes() + "분");
        System.out.println("☕ 휴식시간: " + current.getBreakMinutes() + "분");
        System.out.println("🔄 포모도로 모드: " + (current.isPomodoroMode() ? "ON" : "OFF"));
        System.out.println("🔔 사용 가능한 프로필: " + settingsController.getAllProfiles().size() + "개");
    }
    
    /**
     * 📝 사용법 도움말
     */
    public void help() {
        System.out.println("🚀 EasyProfileHelper 사용법:");
        System.out.println("");
        System.out.println("💾 현재 설정 저장:");
        System.out.println("  easyHelper.saveNow();                    // 자동 이름으로 저장");
        System.out.println("  easyHelper.saveAs(\"밤샘 코딩\");           // 원하는 이름으로 저장");
        System.out.println("");
        System.out.println("⚡ 빠른 프로필 생성:");
        System.out.println("  easyHelper.quick(25, 5);                 // 25분 작업, 5분 휴식");
        System.out.println("  easyHelper.pomodoro();                   // 클래식 포모도로");
        System.out.println("  easyHelper.powerSession();               // 50분 파워 세션");
        System.out.println("  easyHelper.deepWork();                   // 90분 딥워크");
        System.out.println("");
        System.out.println("🔄 프로필 관리:");
        System.out.println("  easyHelper.switchTo(\"포모도로\");          // 프로필 전환");
        System.out.println("  easyHelper.autoSave();                   // 현재 프로필에 자동 저장");
        System.out.println("  easyHelper.updateProfile(\"내 프로필\");    // 기존 프로필 업데이트");
        System.out.println("");
        System.out.println("📤📥 백업/복원:");
        System.out.println("  easyHelper.exportToJson(\"backup.json\");   // JSON으로 내보내기");
        System.out.println("  easyHelper.exportToText(\"backup.txt\");    // 텍스트로 내보내기");
        System.out.println("  easyHelper.importProfiles(\"backup.json\"); // 프로필 불러오기");
        System.out.println("  easyHelper.replaceAll(\"backup.json\");     // 모든 프로필 교체");
        System.out.println("");
        System.out.println("📊 상태 확인:");
        System.out.println("  easyHelper.printStatus();                // 현재 상태 출력");
    }
    
    // ============================================
    // 📤📥 빠른 백업/복원 기능
    // ============================================
    
    /**
     * 📤 모든 프로필을 JSON으로 내보내기
     * 예: easyHelper.exportToJson("my_backup.json");
     */
    public boolean exportToJson(String filePath) {
        try {
            settingsController.exportProfilesToJsonFile(filePath);
            System.out.println("✅ JSON 내보내기 성공: " + filePath);
            return true;
        } catch (Exception e) {
            System.err.println("❌ JSON 내보내기 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 📤 모든 프로필을 텍스트로 내보내기 (사람이 읽기 쉽지만 불러오기 제한적)
     * 예: easyHelper.exportToText("readable_backup.txt");
     */
    public boolean exportToText(String filePath) {
        try {
            settingsController.exportProfilesToTextFile(filePath);
            System.out.println("✅ 텍스트 내보내기 성공: " + filePath);
            return true;
        } catch (Exception e) {
            System.err.println("❌ 텍스트 내보내기 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 📥 파일에서 프로필 불러오기 (기존 프로필은 유지하고 추가)
     * 예: easyHelper.importProfiles("backup.json");
     */
    public ImportSummary importProfiles(String filePath) {
        try {
            SettingsController.ImportResult result = settingsController.importProfilesFromFile(filePath);
            
            ImportSummary summary = new ImportSummary(
                result.isSuccess(),
                result.getMessage(),
                result.getAddedCount(),
                result.getSkippedCount()
            );
            
            if (result.isSuccess()) {
                System.out.println("✅ 프로필 불러오기 성공: " + result.getAddedCount() + "개 추가");
            } else {
                System.err.println("❌ 프로필 불러오기 실패: " + result.getMessage());
            }
            
            return summary;
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 불러오기 오류: " + e.getMessage());
            return new ImportSummary(false, e.getMessage(), 0, 0);
        }
    }
    
    /**
     * 🔄 기존 모든 프로필을 삭제하고 파일에서 불러온 프로필들로 교체
     * 예: easyHelper.replaceAll("new_profiles.json");
     */
    public ImportSummary replaceAll(String filePath) {
        try {
            SettingsController.ImportResult result = settingsController.replaceAllProfilesFromFile(filePath);
            
            ImportSummary summary = new ImportSummary(
                result.isSuccess(),
                result.getMessage(),
                result.getAddedCount(),
                result.getSkippedCount()
            );
            
            if (result.isSuccess()) {
                System.out.println("✅ 모든 프로필 교체 성공: " + result.getAddedCount() + "개 프로필");
            } else {
                System.err.println("❌ 모든 프로필 교체 실패: " + result.getMessage());
            }
            
            return summary;
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 교체 오류: " + e.getMessage());
            return new ImportSummary(false, e.getMessage(), 0, 0);
        }
    }
    
    /**
     * 📦 현재 모든 프로필을 자동으로 명명된 JSON 파일로 백업
     * 예: easyHelper.quickBackup(); // "codebreak_backup_20250609_143025.json" 생성
     */
    public String quickBackup() {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String fileName = "codebreak_backup_" + timestamp + ".json";
        
        if (exportToJson(fileName)) {
            System.out.println("✅ 빠른 백업 완료: " + fileName);
            return fileName;
        } else {
            System.err.println("❌ 빠른 백업 실패");
            return null;
        }
    }
    
    /**
     * 📦 현재 모든 프로필을 사람이 읽기 쉽은 텍스트 파일로 백업
     * 예: easyHelper.quickTextBackup(); // "codebreak_readable_20250609_143025.txt" 생성
     */
    public String quickTextBackup() {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String fileName = "codebreak_readable_" + timestamp + ".txt";
        
        if (exportToText(fileName)) {
            System.out.println("✅ 빠른 텍스트 백업 완료: " + fileName);
            return fileName;
        } else {
            System.err.println("❌ 빠른 텍스트 백업 실패");
            return null;
        }
    }
    
    /**
     * 📊 불러오기/내보내기 결과를 담는 간단한 클래스
     */
    public static class ImportSummary {
        private final boolean success;
        private final String message;
        private final int addedCount;
        private final int skippedCount;
        
        public ImportSummary(boolean success, String message, int addedCount, int skippedCount) {
            this.success = success;
            this.message = message;
            this.addedCount = addedCount;
            this.skippedCount = skippedCount;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getAddedCount() { return addedCount; }
        public int getSkippedCount() { return skippedCount; }
        
        @Override
        public String toString() {
            return String.format("ImportSummary{success=%s, added=%d, skipped=%d, message='%s'}", 
                success, addedCount, skippedCount, message);
        }
    }
}
