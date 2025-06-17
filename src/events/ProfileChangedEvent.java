package events;

import model.UserProfile;

/**
 * 프로필 변경 이벤트
 * 사용자 프로필이 변경되었을 때 발생합니다.
 */
public class ProfileChangedEvent extends Event {
    private final UserProfile oldProfile;
    private final UserProfile newProfile;
    
    public ProfileChangedEvent(UserProfile oldProfile, UserProfile newProfile) {
        super("PROFILE_CHANGED");
        this.oldProfile = oldProfile;
        this.newProfile = newProfile;
    }
    
    public ProfileChangedEvent(UserProfile newProfile) {
        super("PROFILE_CHANGED");
        this.oldProfile = null;
        this.newProfile = newProfile;
    }
    
    /**
     * 이전 프로필
     * @return 이전 프로필 (null일 수 있음)
     */
    public UserProfile getOldProfile() {
        return oldProfile;
    }
    
    /**
     * 새로운 프로필
     * @return 새로운 프로필
     */
    public UserProfile getNewProfile() {
        return newProfile;
    }
    
    /**
     * 프로필이 실제로 변경되었는지 확인
     * @return 변경 여부
     */
    public boolean isActualChange() {
        if (oldProfile == null && newProfile == null) {
            return false;
        }
        if (oldProfile == null || newProfile == null) {
            return true;
        }
        return !oldProfile.getProfileName().equals(newProfile.getProfileName());
    }
    
    @Override
    public String toString() {
        String oldName = oldProfile != null ? oldProfile.getProfileName() : "null";
        String newName = newProfile != null ? newProfile.getProfileName() : "null";
        return String.format("ProfileChangedEvent{old='%s', new='%s', timestamp=%d}", 
                           oldName, newName, getTimestamp());
    }
}
