# Makefile for Code ∧ Break

# 변수 설정
SRC_DIR = src
BIN_DIR = bin
MAIN_CLASS = CodeBreakApplication

# Java 소스 파일들
JAVA_FILES = $(shell find $(SRC_DIR) -name "*.java")

# 기본 타겟
all: compile

# 컴파일
compile: $(BIN_DIR)
	@echo "Code ∧ Break 컴파일 중..."
	javac -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SRC_DIR)/$(MAIN_CLASS).java $(SRC_DIR)/constants/*.java $(SRC_DIR)/model/*.java $(SRC_DIR)/view/*.java $(SRC_DIR)/controller/*.java $(SRC_DIR)/events/*.java
	@echo "컴파일 완료!"

# bin 디렉토리 생성
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

# 실행
run: compile
	@echo "Code ∧ Break 실행 중..."
	cd $(BIN_DIR) && java $(MAIN_CLASS)

# 디버그 모드로 실행
debug: compile
	@echo "Code ∧ Break 디버그 모드 실행 중..."
	cd $(BIN_DIR) && java $(MAIN_CLASS) --debug

# 최소화 모드로 실행
minimized: compile
	@echo "Code ∧ Break 최소화 모드 실행 중..."
	cd $(BIN_DIR) && java $(MAIN_CLASS) --minimized

# 자동 시작 모드로 실행
auto-start: compile
	@echo "Code ∧ Break 자동 시작 모드 실행 중..."
	cd $(BIN_DIR) && java $(MAIN_CLASS) --auto-start

# 포모도로 프로필로 실행
pomodoro: compile
	@echo "Code ∧ Break 포모도로 모드 실행 중..."
	cd $(BIN_DIR) && java $(MAIN_CLASS) --profile "포모도로" --auto-start

# 정리
clean:
	@echo "정리 중..."
	rm -rf $(BIN_DIR)
	rm -rf data
	@echo "정리 완료!"

# JAR 파일 생성
jar: compile
	@echo "JAR 파일 생성 중..."
	cd $(BIN_DIR) && jar cfe ../CodeBreak.jar $(MAIN_CLASS) .
	@echo "CodeBreak.jar 파일이 생성되었습니다!"

# JAR 실행
run-jar: jar
	@echo "JAR 파일 실행 중..."
	java -jar CodeBreak.jar

# 도움말
help:
	@echo "Code ∧ Break Makefile"
	@echo ""
	@echo "사용 가능한 명령:"
	@echo "  make compile     - 소스 코드 컴파일"
	@echo "  make run         - 애플리케이션 실행"
	@echo "  make debug       - 디버그 모드로 실행"
	@echo "  make minimized   - 최소화 모드로 실행"
	@echo "  make auto-start  - 자동 시작 모드로 실행"
	@echo "  make pomodoro    - 포모도로 모드로 실행"
	@echo "  make jar         - JAR 파일 생성"
	@echo "  make run-jar     - JAR 파일 실행"
	@echo "  make clean       - 생성된 파일들 정리"
	@echo "  make help        - 이 도움말 표시"

# 파일 존재 여부 확인
check:
	@echo "파일 확인 중..."
	@if [ -f "$(SRC_DIR)/$(MAIN_CLASS).java" ]; then \
		echo "✓ 메인 클래스 발견"; \
	else \
		echo "✗ 메인 클래스 없음"; \
	fi
	@if [ -d "$(SRC_DIR)/model" ]; then \
		echo "✓ Model 패키지 발견"; \
	else \
		echo "✗ Model 패키지 없음"; \
	fi
	@if [ -d "$(SRC_DIR)/view" ]; then \
		echo "✓ View 패키지 발견"; \
	else \
		echo "✗ View 패키지 없음"; \
	fi
	@if [ -d "$(SRC_DIR)/controller" ]; then \
		echo "✓ Controller 패키지 발견"; \
	else \
		echo "✗ Controller 패키지 없음"; \
	fi

# .PHONY 타겟들
.PHONY: all compile run debug minimized auto-start pomodoro clean jar run-jar help check
