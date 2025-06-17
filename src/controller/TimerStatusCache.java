package controller;

import model.*;
import constants.*;

/**
 * 성능 최적화를 위한 타이머 상태 캐시
 * 자주 호출되는 상태 문자열을 캐싱하여 성능 향상
 */
public class TimerStatusCache {
    
    // 상태별 미리 계산된 문자열
    private static final String STATE_RUNNING = "실행 중";
    private static final String STATE_PAUSED = "일시정지";
    private static final String STATE_STOPPED = "정지";
    private static final String MODE_POMODORO = "포모도로";
    private static final String MODE_NORMAL = "일반";
    
    // 캐시된 상태 정보
    private String cachedStatusText;
    private TimerModel.TimerState lastState;
    private boolean lastPomodoroMode;
    private int lastCycle;
    private String lastPhase;
    
    /**
     * 타이머 상태 텍스트 가져오기 (캐싱 적용)
     */
    public String getTimerStatusText(TimerModel model) {
        // 상태가 변경된 경우에만 새로 생성
        if (needsUpdate(model)) {
            cachedStatusText = buildStatusText(model);
            updateCacheKeys(model);
        }
        
        return cachedStatusText;
    }
    
    /**
     * 캐시 업데이트가 필요한지 확인
     */
    private boolean needsUpdate(TimerModel model) {
        return cachedStatusText == null ||
               lastState != model.getState() ||
               lastPomodoroMode != model.isPomodoroMode() ||
               lastCycle != model.getCurrentCycle() ||
               !lastPhase.equals(model.getCurrentPhase());
    }
    
    /**
     * 상태 텍스트 생성 (최적화된 버전)
     */
    private String buildStatusText(TimerModel model) {
        // StringBuilder 대신 String.format 사용으로 더 읽기 쉽게
        return String.format("상태: %s | 모드: %s | 사이클: %d | 페이즈: %s",
            getStateText(model.getState()),
            model.isPomodoroMode() ? MODE_POMODORO : MODE_NORMAL,
            model.getCurrentCycle(),
            model.getCurrentPhase()
        );
    }
    
    /**
     * 상태별 텍스트 반환 (switch 최적화)
     */
    private String getStateText(TimerModel.TimerState state) {
        switch (state) {
            case RUNNING: return STATE_RUNNING;
            case PAUSED: return STATE_PAUSED;
            case STOPPED: return STATE_STOPPED;
            default: return "알 수 없음";
        }
    }
    
    /**
     * 캐시 키 업데이트
     */
    private void updateCacheKeys(TimerModel model) {
        lastState = model.getState();
        lastPomodoroMode = model.isPomodoroMode();
        lastCycle = model.getCurrentCycle();
        lastPhase = model.getCurrentPhase();
    }
    
    /**
     * 캐시 초기화
     */
    public void clearCache() {
        cachedStatusText = null;
        lastState = null;
        lastPhase = null;
    }
}
