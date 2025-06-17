package controller;

import model.*;
import constants.UIConstants;
import constants.TimerConstants;
import events.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 🚀 향상된 설정 컨트롤러 - 안정적인 자동 저장과 실시간 동기화 지원
 */
public class SettingsController {
    private JsonDataManager dataManager;
    private List<UserProfile> profiles;
    private UserProfile currentProfile;
    private UserProfile pendingProfile; // 임시 변경사항을 저장할 프로필
    private Map<String, Object> applicationSettings;
    private TimerController timerController;
    private EventBus eventBus;
    
    // 자동 저장 관련
    private ScheduledExecutorService autoSaveScheduler;
    private boolean hasUnsavedChanges = false;
    private long lastChangeTime = 0;
    private static final long AUTO_SAVE_DELAY_MS = 500; // 0.5초 후 자동 저장 (2초 -> 0.5초로 단축)
    
    // 상태 추적
    private final Set<String> changeListeners = new HashSet<>();
    private boolean isLoading = false;
    
    public SettingsController(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        this.eventBus = SimpleEventBus.getInstance();
        
        initializeAutoSave();
        loadSettings();
        
        System.out.println("✅ SettingsController 초기화 완료");
    }
    
    /**
     * 🔄 자동 저장 스케줄러 초기화
     */
    private void initializeAutoSave() {
        autoSaveScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "AutoSave-Thread");
            t.setDaemon(true);
            return t;
        });
        
        // 주기적으로 변경사항 체크하여 자동 저장 (더 빠른 체크 주기)
        autoSaveScheduler.scheduleAtFixedRate(() -> {
            if (hasUnsavedChanges && 
                System.currentTimeMillis() - lastChangeTime >= AUTO_SAVE_DELAY_MS) {
                
                try {
                    performAutoSave();
                } catch (Exception e) {
                    System.err.println("❌ 자동 저장 실패: " + e.getMessage());
                }
            }
        }, 100, 100, TimeUnit.MILLISECONDS); // 0.1초마다 체크 (1초 -> 0.1초로 단축)
        
        System.out.println("🔄 자동 저장 스케줄러 시작");
    }
    
    /**
     * 📂 설정 로드 - 향상된 안전성
     */
    private void loadSettings() {
        System.out.println("\n📂 설정 로드 시작...");
        isLoading = true;
        
        try {
            // 프로필 로드
            profiles = dataManager.loadProfiles();
            System.out.println("✅ 프로필 로드: " + profiles.size() + "개");
            
            // 안전성 체크
            if (profiles.isEmpty()) {
                System.out.println("⚠️ 프로필이 없어서 기본 프로필 생성");
                createDefaultProfiles();
            }
            
            // 애플리케이션 설정 로드
            applicationSettings = dataManager.loadSettings();
            System.out.println("✅ 애플리케이션 설정 로드");
            
            // 현재 프로필 설정
            setCurrentProfileSafely();
            
            // 임시 프로필 초기화 (현재 프로필의 복사본)
            pendingProfile = new UserProfile(currentProfile.getProfileName());
            pendingProfile.copyFrom(currentProfile);
            
            System.out.println("📂 설정 로드 완료!\n");
            
        } catch (Exception e) {
            System.err.println("❌ 설정 로드 중 오류: " + e.getMessage());
            e.printStackTrace();
            handleLoadError();
        } finally {
            isLoading = false;
        }
    }
    
    /**
     * 🛡️ 현재 프로필 안전하게 설정
     */
    private void setCurrentProfileSafely() {
        String selectedProfileName = (String) applicationSettings.get("selectedProfile");
        currentProfile = findProfileByName(selectedProfileName);
        
        if (currentProfile == null) {
            currentProfile = profiles.get(0);
            System.out.println("⚠️ 선택된 프로필을 찾을 수 없어서 첫 번째 프로필 사용: " + currentProfile.getProfileName());
            
            // 애플리케이션 설정 업데이트
            applicationSettings.put("selectedProfile", currentProfile.getProfileName());
        } else {
            System.out.println("✅ 현재 프로필: " + currentProfile.getProfileName());
        }
    }
    
    /**
     * 🔧 기본 프로필 생성
     */
    private void createDefaultProfiles() {
        profiles.add(UserProfile.createPomodoroProfile());
        profiles.add(UserProfile.createLongWorkProfile());
        profiles.add(UserProfile.createShortFocusProfile());
        System.out.println("🔧 기본 프로필 3개 생성 완료");
    }
    
    /**
     * ⚠️ 로드 오류 처리
     */
    private void handleLoadError() {
        System.out.println("🔧 오류 복구 모드 실행");
        
        profiles = new ArrayList<>();
        createDefaultProfiles();
        
        applicationSettings = new HashMap<>();
        applicationSettings.put("selectedProfile", profiles.get(0).getProfileName());
        applicationSettings.put("windowWidth", UIConstants.SETTINGS_DEFAULT_WIDTH);
        applicationSettings.put("windowHeight", UIConstants.SETTINGS_DEFAULT_HEIGHT);
        applicationSettings.put("windowX", UIConstants.DEFAULT_WINDOW_X);
        applicationSettings.put("windowY", UIConstants.DEFAULT_WINDOW_Y);
        
        currentProfile = profiles.get(0);
        
        // 즉시 저장하여 복구된 상태 유지
        forceSave();
        System.out.println("🔧 오류 복구 완료");
    }
    
    /**
     * 🔍 이름으로 프로필 찾기
     */
    private UserProfile findProfileByName(String name) {
        if (name == null) return null;
        
        return profiles.stream()
                .filter(profile -> profile.getProfileName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 💾 자동 저장 실행
     */
    private void performAutoSave() {
        if (!hasUnsavedChanges) return;
        
        System.out.println("💾 자동 저장 실행...");
        
        try {
            // 임시 프로필의 변경사항을 현재 프로필에 적용
            applyPendingChanges();
            
            // 실제 저장
            dataManager.saveProfiles(profiles);
            dataManager.saveSettings(applicationSettings);
            
            hasUnsavedChanges = false;
            System.out.println("✅ 자동 저장 완료");
            
            // 변경 이벤트 발송 (설정 업데이트 알림용)
            if (currentProfile != null) {
                eventBus.publish(new ProfileChangedEvent(currentProfile));
                System.out.println("✅ 설정 업데이트 이벤트 발송");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 자동 저장 실패: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 🔄 임시 변경사항을 현재 프로필에 적용
     */
    private void applyPendingChanges() {
        if (pendingProfile != null && currentProfile != null) {
            currentProfile.copyFrom(pendingProfile);
            System.out.println("🔄 임시 변경사항 적용: " + currentProfile.getProfileName());
        }
    }
    
    /**
     * ⚡ 강제 즉시 저장
     */
    public void forceSave() {
        System.out.println("⚡ 강제 저장 실행");
        
        try {
            applyPendingChanges();
            dataManager.saveProfiles(profiles);
            dataManager.saveSettings(applicationSettings);
            hasUnsavedChanges = false;
            
            System.out.println("✅ 강제 저장 완료");
            
        } catch (Exception e) {
            System.err.println("❌ 강제 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 📝 변경사항 기록 (빠른 자동 저장 트리거)
     */
    private void markChanged() {
        if (isLoading) return; // 로딩 중에는 변경사항으로 간주하지 않음
        
        hasUnsavedChanges = true;
        lastChangeTime = System.currentTimeMillis();
        System.out.println("📝 변경사항 감지 - 빠른 자동 저장 예약 (0.5초 후)");
    }
    
    // ============================================
    // 🎯 프로필 관리 API - 향상된 버전
    // ============================================
    
    /**
     * 🔄 현재 프로필 변경 - 안전한 버전
     */
    public boolean setCurrentProfile(UserProfile profile) {
        if (profile == null || !profiles.contains(profile)) {
            System.out.println("❌ 유효하지 않은 프로필");
            return false;
        }
        
        try {
            UserProfile oldProfile = currentProfile;
            System.out.println("\n🔄 프로필 변경 시작:");
            System.out.println("  이전: " + (oldProfile != null ? oldProfile.getProfileName() : "null"));
            System.out.println("  새 프로필: " + profile.getProfileName());
            
            // 1. 현재 임시 변경사항을 이전 프로필에 저장
            if (currentProfile != null && pendingProfile != null) {
                applyPendingChanges();
                System.out.println("  ✅ 이전 프로필 변경사항 저장");
            }
            
            // 2. 새 프로필로 변경
            currentProfile = profile;
            applicationSettings.put("selectedProfile", profile.getProfileName());
            
            // 3. 새 임시 프로필 생성
            pendingProfile = new UserProfile(profile.getProfileName());
            pendingProfile.copyFrom(profile);
            
            // 4. TimerController에 즉시 적용
            if (timerController != null) {
                timerController.setCurrentProfile(profile);
                
                // TimerModel에도 직접 적용
                TimerModel timerModel = timerController.getTimerModel();
                if (timerModel != null) {
                    timerModel.setWorkMinutes(profile.getWorkMinutes());
                    timerModel.setBreakMinutes(profile.getBreakMinutes());
                    timerModel.setPomodoroMode(profile.isPomodoroMode());
                }
                
                System.out.println("  ✅ TimerController 및 TimerModel 업데이트");
            }
            
            // 5. 변경사항 저장
            markChanged();
            
            // 6. 이벤트 발송 (더 명확하게)
            eventBus.publish(new ProfileChangedEvent(oldProfile, profile));
            System.out.println("  ✅ ProfileChangedEvent 발송 완료");
            
            System.out.println("🔄 프로필 변경 완료\n");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 변경 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * ✏️ 현재 프로필 설정 업데이트 (실시간)
     */
    public void updateCurrentProfileSetting(String settingName, Object value) {
        if (pendingProfile == null) {
            System.out.println("⚠️ 임시 프로필이 null - 건너뜀");
            return;
        }
        
        try {
            System.out.println("✏️ 설정 업데이트: " + settingName + " = " + value);
            
            // 리플렉션 대신 직접 매핑
            switch (settingName) {
                case "workMinutes":
                    pendingProfile.setWorkMinutes((Integer) value);
                    // TimerModel에도 즉시 반영
                    if (timerController != null && timerController.getTimerModel() != null) {
                        timerController.getTimerModel().setWorkMinutes((Integer) value);
                    }
                    break;
                case "breakMinutes":
                    pendingProfile.setBreakMinutes((Integer) value);
                    // TimerModel에도 즉시 반영
                    if (timerController != null && timerController.getTimerModel() != null) {
                        timerController.getTimerModel().setBreakMinutes((Integer) value);
                    }
                    break;
                case "pomodoroMode":
                    pendingProfile.setPomodoroMode((Boolean) value);
                    // TimerModel에도 즉시 반영
                    if (timerController != null && timerController.getTimerModel() != null) {
                        timerController.getTimerModel().setPomodoroMode((Boolean) value);
                    }
                    break;
                case "soundEnabled":
                    pendingProfile.setSoundEnabled((Boolean) value);
                    break;
                case "popupEnabled":
                    pendingProfile.setPopupEnabled((Boolean) value);
                    break;
                case "flashEnabled":
                    pendingProfile.setFlashEnabled((Boolean) value);
                    break;
                case "snoozeMinutes":
                    pendingProfile.setSnoozeMinutes((Integer) value);
                    break;
                case "autoStart":
                    pendingProfile.setAutoStart((Boolean) value);
                    break;
                case "minimizeToTray":
                    pendingProfile.setMinimizeToTray((Boolean) value);
                    break;
                default:
                    System.out.println("⚠️ 알 수 없는 설정: " + settingName);
                    return;
            }
            
            markChanged();
            System.out.println("✅ 설정 업데이트 완료 (실시간 반영)");
            
        } catch (Exception e) {
            System.err.println("❌ 설정 업데이트 실패: " + e.getMessage());
        }
    }
    
    /**
     * 🔔 알림 유형 설정 업데이트
     */
    public void updateNotificationSetting(TimerModel.NotificationType type, boolean enabled) {
        if (pendingProfile == null) return;
        
        System.out.println("🔔 알림 설정: " + type.getName() + " = " + enabled);
        pendingProfile.setNotificationEnabled(type, enabled);
        markChanged();
    }
    
    /**
     * ➕ 새 프로필 추가 - 향상된 버전
     */
    public UserProfile addProfile(String profileName) {
        if (profileName == null || profileName.trim().isEmpty()) {
            System.out.println("❌ 프로필 이름이 비어있음");
            return null;
        }
        
        String trimmedName = profileName.trim();
        
        // 중복 이름 체크
        if (findProfileByName(trimmedName) != null) {
            System.out.println("❌ 중복된 프로필 이름: " + trimmedName);
            return null;
        }
        
        try {
            UserProfile newProfile = new UserProfile(trimmedName);
            
            // 현재 설정을 새 프로필에 복사 (사용자 편의)
            if (pendingProfile != null) {
                newProfile.copyFrom(pendingProfile);
                newProfile.setProfileName(trimmedName); // 이름은 새로 설정한 것으로
            }
            
            // 🔔 새 프로필에는 항상 휴식 알림을 기본 활성화
            newProfile.setNotificationEnabled(TimerModel.NotificationType.REST, true);
            System.out.println("✅ 새 프로필에 휴식 알림 기본 활성화 설정");
            
            // 📱 minimizeToTray 항상 false로 설정
            newProfile.setMinimizeToTray(false);
            System.out.println("✅ 새 프로필에 minimizeToTray false 설정");
            
            profiles.add(newProfile);
            markChanged();
            
            System.out.println("✅ 새 프로필 추가: " + trimmedName);
            return newProfile;
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 추가 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 🗑️ 프로필 삭제 - 안전한 버전
     */
    public boolean deleteProfile(String profileName) {
        if (profiles.size() <= 1) {
            System.out.println("❌ 마지막 프로필은 삭제할 수 없음");
            return false;
        }
        
        UserProfile toDelete = findProfileByName(profileName);
        if (toDelete == null) {
            System.out.println("❌ 삭제할 프로필을 찾을 수 없음: " + profileName);
            return false;
        }
        
        try {
            profiles.remove(toDelete);
            
            // 삭제된 프로필이 현재 프로필이면 다른 프로필로 변경
            if (currentProfile == toDelete) {
                setCurrentProfile(profiles.get(0));
                System.out.println("➡️ 현재 프로필을 변경함: " + currentProfile.getProfileName());
            }
            
            markChanged();
            System.out.println("🗑️ 프로필 삭제 완료: " + profileName);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 삭제 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 📋 프로필 복제
     */
    public UserProfile duplicateProfile(String originalName, String newName) {
        UserProfile original = findProfileByName(originalName);
        if (original == null) {
            System.out.println("❌ 원본 프로필을 찾을 수 없음: " + originalName);
            return null;
        }
        
        UserProfile duplicate = addProfile(newName);
        if (duplicate != null) {
            duplicate.copyFrom(original);
            duplicate.setProfileName(newName);
            
            // 🔔 복제된 프로필에도 휴식 알림 기본 활성화
            duplicate.setNotificationEnabled(TimerModel.NotificationType.REST, true);
            System.out.println("✅ 복제된 프로필에 휴식 알림 기본 활성화 설정");
            
            // 📱 minimizeToTray 항상 false로 설정
            duplicate.setMinimizeToTray(false);
            System.out.println("✅ 복제된 프로필에 minimizeToTray false 설정");
            
            markChanged();
            System.out.println("📋 프로필 복제 완료: " + originalName + " → " + newName);
        }
        
        return duplicate;
    }
    
    // ============================================
    // 📊 상태 조회 API
    // ============================================
    
    public List<UserProfile> getAllProfiles() { 
        return new ArrayList<>(profiles); 
    }
    
    public UserProfile getCurrentProfile() { 
        return currentProfile; 
    }
    
    /**
     * 🔍 현재 진행 중인 변경사항이 있는 프로필 반환 (실시간 반영)
     */
    public UserProfile getCurrentProfileWithPendingChanges() {
        return pendingProfile != null ? pendingProfile : currentProfile;
    }
    
    public boolean hasUnsavedChanges() { 
        return hasUnsavedChanges; 
    }
    
    public Map<String, Object> getApplicationSettings() { 
        return new HashMap<>(applicationSettings); 
    }
    
    /**
     * 🔧 TimerController 설정
     */
    public void setTimerController(TimerController timerController) {
        this.timerController = timerController;
        
        // 현재 프로필을 TimerController에 설정
        if (timerController != null && currentProfile != null) {
            timerController.setCurrentProfile(currentProfile);
        }
    }
    
    /**
     * 📊 현재 프로필을 타이머 모델에 적용
     */
    public void applyProfileToTimer(TimerModel timerModel) {
        if (currentProfile != null && timerModel != null) {
            timerModel.setWorkMinutes(currentProfile.getWorkMinutes());
            timerModel.setBreakMinutes(currentProfile.getBreakMinutes());
            timerModel.setPomodoroMode(currentProfile.isPomodoroMode());
            System.out.println("프로필 '" + currentProfile.getProfileName() + "'을 타이머에 적용");
        }
    }
    
    /**
     * 📊 현재 프로필을 알림 매니저에 적용
     */
    public void applyProfileToNotificationManager(NotificationManager notificationManager) {
        if (currentProfile != null && notificationManager != null) {
            notificationManager.setSoundEnabled(currentProfile.isSoundEnabled());
            notificationManager.setPopupEnabled(currentProfile.isPopupEnabled());
            notificationManager.setFlashEnabled(currentProfile.isFlashEnabled());
            notificationManager.setSnoozeMinutes(currentProfile.getSnoozeMinutes());
            notificationManager.setCurrentProfile(currentProfile);
            System.out.println("프로필 '" + currentProfile.getProfileName() + "'을 알림 매니저에 적용");
        }
    }
    
    /**
     * 💾 현재 타이머 설정을 새 프로필로 저장 (원클릭 저장)
     */
    public UserProfile saveCurrentAsNewProfile(String profileName) {
        if (profileName == null || profileName.trim().isEmpty()) {
            profileName = generateUniqueProfileName(); // 자동 이름 생성
        }
        
        // 현재 타이머 설정을 가져와서 새 프로필 생성
        UserProfile newProfile = new UserProfile(profileName.trim());
        
        if (timerController != null) {
            TimerModel timerModel = timerController.getTimerModel();
            NotificationManager notificationManager = timerController.getNotificationManager();
            
            // 타이머 설정 복사
            newProfile.setWorkMinutes(timerModel.getWorkMinutes());
            newProfile.setBreakMinutes(timerModel.getBreakMinutes());
            newProfile.setPomodoroMode(timerModel.isPomodoroMode());
            
            // 알림 설정 복사
            newProfile.setSoundEnabled(notificationManager.isSoundEnabled());
            newProfile.setPopupEnabled(notificationManager.isPopupEnabled());
            newProfile.setFlashEnabled(notificationManager.isFlashEnabled());
            newProfile.setSnoozeMinutes(notificationManager.getSnoozeMinutes());
            
            // 현재 프로필의 알림 유형 설정도 복사
            if (currentProfile != null) {
                newProfile.setNotificationSettings(currentProfile.getNotificationSettings());
            }
        }
        
        profiles.add(newProfile);
        markChanged();
        
        System.out.println("✅ 새 프로필 '" + profileName + "' 저장 완료!");
        return newProfile;
    }
    
    /**
     * 📋 현재 프로필 복제 (간편 복사)
     */
    public UserProfile duplicateCurrentProfile(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            newName = currentProfile.getProfileName() + " 복사";
            newName = ensureUniqueName(newName);
        }
        
        UserProfile duplicate = new UserProfile(newName.trim());
        duplicate.copyFrom(currentProfile);
        
        // 🔔 복제된 프로필에도 휴식 알림 기본 활성화
        duplicate.setNotificationEnabled(TimerModel.NotificationType.REST, true);
        System.out.println("✅ 복제된 프로필에 휴식 알림 기본 활성화 설정");
        
        // 📱 minimizeToTray 항상 false로 설정
        duplicate.setMinimizeToTray(false);
        System.out.println("✅ 복제된 프로필에 minimizeToTray false 설정");
        
        profiles.add(duplicate);
        markChanged();
        
        System.out.println("✅ 프로필 '" + newName + "' 복제 완료!");
        return duplicate;
    }
    
    /**
     * ⚡ 빠른 프로필 생성 (프리셋 기반)
     */
    public UserProfile createQuickProfile(int workMinutes, int breakMinutes) {
        return createQuickProfile(workMinutes, breakMinutes, null);
    }
    
    public UserProfile createQuickProfile(int workMinutes, int breakMinutes, String customName) {
        String profileName = customName;
        if (profileName == null || profileName.trim().isEmpty()) {
            profileName = generateSmartProfileName(workMinutes, breakMinutes);
        }
        
        UserProfile quickProfile = new UserProfile(profileName);
        quickProfile.setWorkMinutes(workMinutes);
        quickProfile.setBreakMinutes(breakMinutes);
        
        // 스마트 기본 설정
        quickProfile.setPomodoroMode(breakMinutes > 0); // 휴식시간이 있으면 포모도로 모드
        quickProfile.setSoundEnabled(true);
        quickProfile.setPopupEnabled(true);
        quickProfile.setFlashEnabled(false);
        quickProfile.setSnoozeMinutes(Math.max(3, breakMinutes / 2)); // 휴식시간의 절반, 최소 3분
        
        // 작업시간에 따른 스마트 알림 설정
        setupSmartNotifications(quickProfile, workMinutes);
        
        // 🔔 휴식 알림 강제 활성화 (중요!)
        quickProfile.setNotificationEnabled(TimerModel.NotificationType.REST, true);
        System.out.println("✅ 빠른 프로필에 휴식 알림 기본 활성화 설정");
        
        // 📱 minimizeToTray 항상 false로 설정
        quickProfile.setMinimizeToTray(false);
        System.out.println("✅ 빠른 프로필에 minimizeToTray false 설정");
        
        profiles.add(quickProfile);
        markChanged();
        
        System.out.println("⚡ 빠간 프로필 '" + profileName + "' 생성 완료!");
        return quickProfile;
    }
    
    /**
     * 🔄 현재 설정을 기존 프로필에 덮어쓰기
     */
    public boolean updateProfileWithCurrentSettings(UserProfile targetProfile) {
        if (targetProfile == null || !profiles.contains(targetProfile)) {
            return false;
        }
        
        if (timerController != null) {
            TimerModel timerModel = timerController.getTimerModel();
            NotificationManager notificationManager = timerController.getNotificationManager();
            
            // 현재 설정을 타겟 프로필에 저장
            targetProfile.setWorkMinutes(timerModel.getWorkMinutes());
            targetProfile.setBreakMinutes(timerModel.getBreakMinutes());
            targetProfile.setPomodoroMode(timerModel.isPomodoroMode());
            
            targetProfile.setSoundEnabled(notificationManager.isSoundEnabled());
            targetProfile.setPopupEnabled(notificationManager.isPopupEnabled());
            targetProfile.setFlashEnabled(notificationManager.isFlashEnabled());
            targetProfile.setSnoozeMinutes(notificationManager.getSnoozeMinutes());
            
            markChanged();
            System.out.println("🔄 프로필 '" + targetProfile.getProfileName() + "' 업데이트 완료!");
            return true;
        }
        
        return false;
    }
    
    /**
     * 📝 현재 프로필 업데이트 (자동 저장)
     */
    public void autoSaveCurrentProfile() {
        updateProfileWithCurrentSettings(currentProfile);
    }
    
    /**
     * 🗑️ 안전한 프로필 삭제 (기본 프로필 보호)
     */
    public boolean safeDeleteProfile(UserProfile profile) {
        if (profile == null || !profiles.contains(profile)) {
            return false;
        }
        
        // 기본 프로필들은 삭제 방지
        String profileName = profile.getProfileName();
        if (profileName.equals("포모도로") || 
            profileName.equals("장시간 작업") || 
            profileName.equals("단시간 집중")) {
            System.out.println("⚠️ 기본 프로필은 삭제할 수 없습니다: " + profileName);
            return false;
        }
        
        // 마지막 프로필이면 삭제 방지
        if (profiles.size() <= 1) {
            System.out.println("⚠️ 마지막 프로필은 삭제할 수 없습니다.");
            return false;
        }
        
        profiles.remove(profile);
        
        // 삭제된 프로필이 현재 프로필이면 다른 프로필로 변경
        if (currentProfile == profile) {
            setCurrentProfile(profiles.get(0));
            System.out.println("➡️ 현재 프로필을 변경함: " + currentProfile.getProfileName());
        }
        
        markChanged();
        System.out.println("🗑️ 프로필 '" + profileName + "' 삭제 완료!");
        return true;
    }
    
    /**
     * 🔄 설정 초기화
     */
    public void resetToDefaults() {
        System.out.println("\n🔄 설정 초기화 시작...");
        
        try {
            profiles.clear();
            createDefaultProfiles();
            
            currentProfile = profiles.get(0);
            
            applicationSettings.clear();
            applicationSettings.put("selectedProfile", currentProfile.getProfileName());
            applicationSettings.put("windowWidth", UIConstants.SETTINGS_DEFAULT_WIDTH);
            applicationSettings.put("windowHeight", UIConstants.SETTINGS_DEFAULT_HEIGHT);
            applicationSettings.put("windowX", UIConstants.DEFAULT_WINDOW_X);
            applicationSettings.put("windowY", UIConstants.DEFAULT_WINDOW_Y);
            applicationSettings.put("startMinimized", false);
            
            // 임시 프로필 재생성
            pendingProfile = new UserProfile(currentProfile.getProfileName());
            pendingProfile.copyFrom(currentProfile);
            
            forceSave();
            System.out.println("🔄 초기화 완료!\n");
            
        } catch (Exception e) {
            System.err.println("❌ 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 🧹 리소스 정리
     */
    public void shutdown() {
        System.out.println("🧹 SettingsController 종료...");
        
        if (hasUnsavedChanges) {
            System.out.println("💾 종료 전 최종 저장");
            forceSave();
        }
        
        if (autoSaveScheduler != null && !autoSaveScheduler.isShutdown()) {
            autoSaveScheduler.shutdown();
            try {
                if (!autoSaveScheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    autoSaveScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                autoSaveScheduler.shutdownNow();
            }
            System.out.println("🧹 자동 저장 스케줄러 종료");
        }
        
        System.out.println("✅ SettingsController 종료 완료");
    }
    
    /**
     * 기본 알림 설정 (간단한 버전)
     */
    private void setupSmartNotifications(UserProfile profile, int workMinutes) {
        Map<TimerModel.NotificationType, Boolean> settings = new HashMap<>();
        
        // 모든 알림을 기본적으로 비활성화하고 REST만 활성화
        for (TimerModel.NotificationType type : TimerModel.NotificationType.values()) {
            settings.put(type, false);
        }
        settings.put(TimerModel.NotificationType.REST, true); // 휴식 알림만 기본 활성화
        
        profile.setNotificationSettings(settings);
    }
    
    // ============================================
    // 🛠️ 헬퍼 메서드들
    // ============================================
    
    /**
     * 유니크한 프로필 이름 자동 생성
     */
    private String generateUniqueProfileName() {
        String baseName = "내 프로필";
        String candidateName = baseName;
        int counter = 1;
        
        while (findProfileByName(candidateName) != null) {
            candidateName = baseName + " " + counter;
            counter++;
        }
        
        return candidateName;
    }
    
    /**
     * 작업시간과 휴식시간을 기반으로 간단한 프로필 이름 생성
     */
    private String generateSmartProfileName(int workMinutes, int breakMinutes) {
        // 간단하고 예측 가능한 이름 생성
        String baseName = workMinutes + "분 작업";
        return ensureUniqueName(baseName);
    }
    
    /**
     * 이름이 중복되지 않도록 보장
     */
    private String ensureUniqueName(String baseName) {
        String candidateName = baseName;
        int counter = 1;
        
        while (findProfileByName(candidateName) != null) {
            candidateName = baseName + " " + counter;
            counter++;
        }
        
        return candidateName;
    }
    
    /**
     * 📊 디버깅용 상태 출력
     */
    public void printStatus() {
        System.out.println("\n=== 📊 SettingsController 상태 ===");
        System.out.println("프로필 수: " + profiles.size());
        System.out.println("현재 프로필: " + (currentProfile != null ? currentProfile.getProfileName() : "null"));
        System.out.println("임시 프로필: " + (pendingProfile != null ? pendingProfile.getProfileName() : "null"));
        System.out.println("저장되지 않은 변경사항: " + hasUnsavedChanges);
        System.out.println("애플리케이션 설정 수: " + applicationSettings.size());
        System.out.println("===========================================\n");
    }
    
    // ============================================
    // 📤📥 프로필 내보내기/불러오기 기능
    // ============================================
    
    /**
     * 📤 모든 프로필을 JSON 파일로 내보내기
     */
    public void exportProfilesToJsonFile(String filePath) throws Exception {
        try {
            dataManager.exportProfilesToFile(profiles, filePath);
            System.out.println("✅ 프로필 JSON 내보내기 성공: " + filePath);
        } catch (Exception e) {
            System.err.println("❌ 프로필 JSON 내보내기 실패: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 📤 모든 프로필을 텍스트 파일로 내보내기
     */
    public void exportProfilesToTextFile(String filePath) throws Exception {
        try {
            dataManager.exportProfilesToTextFile(profiles, filePath);
            System.out.println("✅ 프로필 텍스트 내보내기 성공: " + filePath);
        } catch (Exception e) {
            System.err.println("❌ 프로필 텍스트 내보내기 실패: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 📥 파일에서 프로필 불러오기 (자동 형식 감지)
     */
    public ImportResult importProfilesFromFile(String filePath) {
        try {
            System.out.println("\n📥 프로필 불러오기 시작: " + filePath);
            
            // 파일에서 프로필 불러오기
            List<UserProfile> importedProfiles = dataManager.importProfilesFromFile(filePath);
            
            if (importedProfiles.isEmpty()) {
                return new ImportResult(false, "불러올 프로필이 없습니다.", 0, 0);
            }
            
            // 프로필 유효성 검증
            List<String> validationIssues = dataManager.validateImportedProfiles(importedProfiles);
            
            if (!validationIssues.isEmpty()) {
                String issues = String.join("\n", validationIssues);
                return new ImportResult(false, "프로필 유효성 검증 실패:\n" + issues, 0, 0);
            }
            
            // 중복 이름 처리
            List<UserProfile> processedProfiles = processImportedProfiles(importedProfiles);
            
            // 프로필 추가
            int addedCount = 0;
            int skippedCount = 0;
            
            for (UserProfile profile : processedProfiles) {
                if (addProfileIfNotExists(profile)) {
                    addedCount++;
                } else {
                    skippedCount++;
                }
            }
            
            if (addedCount > 0) {
                markChanged();
                System.out.println("✅ 프로필 불러오기 완료: " + addedCount + "개 추가, " + skippedCount + "개 건너뜀");
                return new ImportResult(true, "성공적으로 " + addedCount + "개 프로필을 불러왔습니다.", addedCount, skippedCount);
            } else {
                return new ImportResult(false, "새로 추가된 프로필이 없습니다. 모든 프로필이 이미 존재합니다.", 0, skippedCount);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 불러오기 실패: " + e.getMessage());
            return new ImportResult(false, "프로필 불러오기 실패: " + e.getMessage(), 0, 0);
        }
    }
    
    /**
     * 📥 불러온 프로필들의 중복 이름 처리
     */
    private List<UserProfile> processImportedProfiles(List<UserProfile> importedProfiles) {
        List<UserProfile> processedProfiles = new ArrayList<>();
        
        for (UserProfile importedProfile : importedProfiles) {
            String originalName = importedProfile.getProfileName();
            String uniqueName = generateUniqueProfileNameForImport(originalName);
            
            if (!uniqueName.equals(originalName)) {
                System.out.println("📝 프로필 이름 변경: '" + originalName + "' → '" + uniqueName + "'");
                importedProfile.setProfileName(uniqueName);
            }
            
            processedProfiles.add(importedProfile);
        }
        
        return processedProfiles;
    }
    
    /**
     * 📝 불러오기용 유니크한 프로필 이름 생성
     */
    private String generateUniqueProfileNameForImport(String baseName) {
        String candidateName = baseName;
        int counter = 1;
        
        while (findProfileByName(candidateName) != null) {
            candidateName = baseName + " (" + counter + ")";
            counter++;
        }
        
        return candidateName;
    }
    
    /**
     * ➕ 프로필이 존재하지 않으면 추가
     */
    private boolean addProfileIfNotExists(UserProfile profile) {
        if (findProfileByName(profile.getProfileName()) != null) {
            return false; // 이미 존재함
        }
        
        // 🔔 불러온 프로필에는 항상 휴식 알림을 기본 활성화
        profile.setNotificationEnabled(TimerModel.NotificationType.REST, true);
        
        profiles.add(profile);
        return true;
    }
    
    /**
     * 🔄 기존 프로필들을 불러온 프로필들로 교체
     */
    public ImportResult replaceAllProfilesFromFile(String filePath) {
        try {
            System.out.println("\n🔄 모든 프로필 교체 시작: " + filePath);
            
            // 파일에서 프로필 불러오기
            List<UserProfile> importedProfiles = dataManager.importProfilesFromFile(filePath);
            
            if (importedProfiles.isEmpty()) {
                return new ImportResult(false, "불러올 프로필이 없습니다.", 0, 0);
            }
            
            // 프로필 유효성 검증
            List<String> validationIssues = dataManager.validateImportedProfiles(importedProfiles);
            
            if (!validationIssues.isEmpty()) {
                String issues = String.join("\n", validationIssues);
                return new ImportResult(false, "프로필 유효성 검증 실패:\n" + issues, 0, 0);
            }
            
            // 기존 프로필 백업
            List<UserProfile> backupProfiles = new ArrayList<>(profiles);
            
            try {
                // 모든 프로필 교체
                profiles.clear();
                for (UserProfile profile : importedProfiles) {
                    // 🔔 불러온 프로필에는 항상 휴식 알림을 기본 활성화
                    profile.setNotificationEnabled(TimerModel.NotificationType.REST, true);
                    profiles.add(profile);
                }
                
                // 새 현재 프로필 설정
                setCurrentProfile(profiles.get(0));
                
                markChanged();
                
                System.out.println("✅ 모든 프로필 교체 완료: " + importedProfiles.size() + "개 프로필");
                return new ImportResult(true, "성공적으로 " + importedProfiles.size() + "개 프로필로 교체했습니다.", importedProfiles.size(), 0);
                
            } catch (Exception e) {
                // 오류 시 백업 복원
                profiles.clear();
                profiles.addAll(backupProfiles);
                throw e;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 프로필 교체 실패: " + e.getMessage());
            return new ImportResult(false, "프로필 교체 실패: " + e.getMessage(), 0, 0);
        }
    }
    
    /**
     * 📊 불러오기 결과를 담는 클래스
     */
    public static class ImportResult {
        private final boolean success;
        private final String message;
        private final int addedCount;
        private final int skippedCount;
        
        public ImportResult(boolean success, String message, int addedCount, int skippedCount) {
            this.success = success;
            this.message = message;
            this.addedCount = addedCount;
            this.skippedCount = skippedCount;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getAddedCount() { return addedCount; }
        public int getSkippedCount() { return skippedCount; }
    }
}
