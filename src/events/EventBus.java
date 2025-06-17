package events;

/**
 * 이벤트 버스 인터페이스
 * 이벤트의 발행과 구독을 관리합니다.
 */
public interface EventBus {
    
    /**
     * 이벤트를 발행합니다.
     * @param event 발행할 이벤트
     */
    void publish(Event event);
    
    /**
     * 특정 이벤트 타입에 대한 핸들러를 등록합니다.
     * @param eventType 이벤트 타입 클래스
     * @param handler 이벤트 핸들러
     * @param <T> 이벤트 타입
     */
    <T extends Event> void subscribe(Class<T> eventType, EventHandler<T> handler);
    
    /**
     * 특정 이벤트 타입에서 핸들러 등록을 해제합니다.
     * @param eventType 이벤트 타입 클래스
     * @param handler 해제할 이벤트 핸들러
     * @param <T> 이벤트 타입
     */
    <T extends Event> void unsubscribe(Class<T> eventType, EventHandler<T> handler);
    
    /**
     * 모든 구독을 해제합니다.
     */
    void clear();
}
