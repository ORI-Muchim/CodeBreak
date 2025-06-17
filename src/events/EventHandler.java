package events;

/**
 * 이벤트 핸들러 인터페이스
 * 이벤트를 처리하는 메서드를 정의합니다.
 */
@FunctionalInterface
public interface EventHandler<T extends Event> {
    
    /**
     * 이벤트를 처리합니다.
     * @param event 처리할 이벤트
     */
    void handle(T event);
}
