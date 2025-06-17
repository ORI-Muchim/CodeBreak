package events;

import constants.TimerConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 단순한 이벤트 버스 구현체
 * 순환 의존성을 제거하고 컴포넌트 간 결합도를 낮춥니다.
 */
public class SimpleEventBus implements EventBus {
    
    // 싱글톤 인스턴스
    private static SimpleEventBus instance;
    
    // 이벤트 타입별 핸들러 목록을 저장하는 맵
    private final Map<Class<? extends Event>, List<EventHandler<? extends Event>>> subscribers;
    
    // 디버그 모드 플래그
    private final boolean debugMode;
    
    public SimpleEventBus() {
        this(false);
    }
    
    public SimpleEventBus(boolean debugMode) {
        this.subscribers = new ConcurrentHashMap<>();
        this.debugMode = debugMode;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void publish(Event event) {
        if (event == null) {
            logDebug("이벤트가 null입니다. 무시합니다.");
            return;
        }
        
        logDebug("이벤트 발행: " + event);
        
        Class<? extends Event> eventType = event.getClass();
        List<EventHandler<? extends Event>> handlers = subscribers.get(eventType);
        
        if (handlers == null || handlers.isEmpty()) {
            logDebug("이벤트 타입 " + eventType.getSimpleName() + "에 대한 구독자가 없습니다.");
            return;
        }
        
        // 핸들러 실행
        for (EventHandler<? extends Event> handler : handlers) {
            try {
                ((EventHandler<Event>) handler).handle(event);
                logDebug("핸들러 실행 완료: " + handler.getClass().getSimpleName());
            } catch (Exception e) {
                System.err.println("이벤트 핸들러 실행 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public <T extends Event> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        if (eventType == null || handler == null) {
            logDebug("이벤트 타입 또는 핸들러가 null입니다. 구독을 무시합니다.");
            return;
        }
        
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
        logDebug("핸들러 구독: " + eventType.getSimpleName() + " -> " + handler.getClass().getSimpleName());
    }
    
    @Override
    public <T extends Event> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        if (eventType == null || handler == null) {
            return;
        }
        
        List<EventHandler<? extends Event>> handlers = subscribers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
            logDebug("핸들러 구독 해제: " + eventType.getSimpleName() + " -> " + handler.getClass().getSimpleName());
            
            // 핸들러가 없으면 리스트 제거
            if (handlers.isEmpty()) {
                subscribers.remove(eventType);
            }
        }
    }
    
    @Override
    public void clear() {
        int subscriberCount = subscribers.values().stream()
                .mapToInt(List::size)
                .sum();
        
        subscribers.clear();
        logDebug("모든 구독 해제됨. 총 " + subscriberCount + "개의 핸들러가 해제되었습니다.");
    }
    
    /**
     * 현재 구독자 수 조회
     * @return 총 구독자 수
     */
    public int getSubscriberCount() {
        return subscribers.values().stream()
                .mapToInt(List::size)
                .sum();
    }
    
    /**
     * 특정 이벤트 타입의 구독자 수 조회
     * @param eventType 이벤트 타입
     * @return 구독자 수
     */
    public int getSubscriberCount(Class<? extends Event> eventType) {
        List<EventHandler<? extends Event>> handlers = subscribers.get(eventType);
        return handlers != null ? handlers.size() : 0;
    }
    
    /**
     * 구독된 이벤트 타입 목록 조회
     * @return 이벤트 타입 세트
     */
    public Set<Class<? extends Event>> getSubscribedEventTypes() {
        return new HashSet<>(subscribers.keySet());
    }
    
    /**
     * 이벤트 버스 상태 정보
     * @return 상태 문자열
     */
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append("EventBus 상태:\n");
        status.append("- 총 구독자 수: ").append(getSubscriberCount()).append("\n");
        status.append("- 이벤트 타입 수: ").append(subscribers.size()).append("\n");
        
        if (!subscribers.isEmpty()) {
            status.append("- 구독된 이벤트 타입:\n");
            for (Map.Entry<Class<? extends Event>, List<EventHandler<? extends Event>>> entry : subscribers.entrySet()) {
                status.append("  - ").append(entry.getKey().getSimpleName())
                      .append(": ").append(entry.getValue().size()).append("개 핸들러\n");
            }
        }
        
        return status.toString();
    }
    
    /**
     * 디버그 로그 출력
     * @param message 로그 메시지
     */
    private void logDebug(String message) {
        if (debugMode) {
            System.out.println("[EventBus] " + message);
        }
    }
    
    /**
     * 빌더 패턴을 통한 EventBus 생성
     */
    public static class Builder {
        private boolean debugMode = false;
        
        public Builder debugMode(boolean enabled) {
            this.debugMode = enabled;
            return this;
        }
        
        public SimpleEventBus build() {
            return new SimpleEventBus(debugMode);
        }
    }
    
    /**
     * 빌더 인스턴스 생성
     * @return 빌더 인스턴스
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 싱글톤 인스턴스 반환
     * @return SimpleEventBus 인스턴스
     */
    public static synchronized SimpleEventBus getInstance() {
        if (instance == null) {
            instance = new SimpleEventBus(false);
        }
        return instance;
    }
    
    /**
     * 싱글톤 인스턴스 초기화 (디버그 모드 설정 가능)
     * @param debugMode 디버그 모드
     * @return SimpleEventBus 인스턴스
     */
    public static synchronized SimpleEventBus getInstance(boolean debugMode) {
        if (instance == null) {
            instance = new SimpleEventBus(debugMode);
        }
        return instance;
    }
}
