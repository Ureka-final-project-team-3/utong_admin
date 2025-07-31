// login.js - Spring Boot 백오피스 로그인 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // DOM 요소들
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const passwordToggle = document.getElementById('passwordToggle');
    const loginBtn = document.getElementById('loginBtn');
    const btnText = loginBtn.querySelector('.btn-text');
    const loadingSpinner = loginBtn.querySelector('.loading-spinner');
    const rememberCheckbox = document.querySelector('input[name="remember-me"]');

    // 초기화
    initializeLogin();

    // 초기화 함수
    function initializeLogin() {
        // 저장된 로그인 정보 불러오기
        loadSavedCredentials();

        // 이벤트 리스너 등록
        setupEventListeners();

        // URL 파라미터 체크 (메시지 자동 숨김)
        handleUrlMessages();

        console.log('Spring Boot 백오피스 로그인 시스템 초기화 완료');
    }

    // 이벤트 리스너 설정
    function setupEventListeners() {
        // 비밀번호 보기/숨기기 토글
        if (passwordToggle) {
            passwordToggle.addEventListener('click', togglePasswordVisibility);
        }

        // 폼 제출 처리
        loginForm.addEventListener('submit', handleFormSubmit);

        // Enter 키 처리
        usernameInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                passwordInput.focus();
            }
        });

        passwordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                loginForm.dispatchEvent(new Event('submit'));
            }
        });

        // 실시간 유효성 검사
        usernameInput.addEventListener('input', function() {
            clearFieldError(this);
            validateEmailField(this.value);
        });

        passwordInput.addEventListener('input', function() {
            clearFieldError(this);
            validatePasswordField(this.value);
        });

        // 폼 필드 포커스 이벤트
        usernameInput.addEventListener('focus', function() {
            this.select();
        });

        // 키보드 단축키
        document.addEventListener('keydown', handleKeyboardShortcuts);

        // 페이지 가시성 변경 처리
        document.addEventListener('visibilitychange', handleVisibilityChange);
    }

    // 비밀번호 보기/숨기기 토글
    function togglePasswordVisibility() {
        const isPassword = passwordInput.type === 'password';
        passwordInput.type = isPassword ? 'text' : 'password';

        const eyeIcon = passwordToggle.querySelector('.eye-icon');
        if (isPassword) {
            // 비밀번호 보이기 상태
            eyeIcon.innerHTML = `
                <path d="M1 10C1 10 4 4 10 4C16 4 19 10 19 10C19 10 16 16 10 16C4 16 1 10 1 10Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="10" cy="10" r="3" stroke="currentColor" stroke-width="1.5"/>
                <path d="M1 1L19 19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            `;
        } else {
            // 비밀번호 숨기기 상태
            eyeIcon.innerHTML = `
                <path d="M1 10C1 10 4 4 10 4C16 4 19 10 19 10C19 10 16 16 10 16C4 16 1 10 1 10Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="10" cy="10" r="3" stroke="currentColor" stroke-width="1.5"/>
            `;
        }
    }

    // 폼 제출 처리
    function handleFormSubmit(e) {
        // Spring Security가 처리하므로 기본 동작 허용
        // 단, 클라이언트 사이드 유효성 검사는 수행
        const isValid = validateForm();

        if (!isValid) {
            e.preventDefault();
            return false;
        }

        // 로그인 정보 저장 (체크박스가 체크된 경우)
        if (rememberCheckbox && rememberCheckbox.checked) {
            saveCredentials(usernameInput.value);
        } else {
            clearSavedCredentials();
        }

        // 로딩 상태 시작
        setLoadingState(true);

        // Spring Security가 처리하도록 폼 제출 허용
        return true;
    }

    // 폼 유효성 검사
    function validateForm() {
        let isValid = true;

        // 이메일 유효성 검사
        const emailValidation = validateEmailField(usernameInput.value);
        if (!emailValidation.valid) {
            setFieldError(usernameInput, emailValidation.message);
            isValid = false;
        }

        // 비밀번호 유효성 검사
        const passwordValidation = validatePasswordField(passwordInput.value);
        if (!passwordValidation.valid) {
            setFieldError(passwordInput, passwordValidation.message);
            isValid = false;
        }

        return isValid;
    }

    // 이메일 유효성 검사
    function validateEmailField(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!email || email.trim() === '') {
            return { valid: false, message: '이메일을 입력해주세요.' };
        }

        if (!emailRegex.test(email.trim())) {
            return { valid: false, message: '올바른 이메일 형식을 입력해주세요.' };
        }

        return { valid: true };
    }

    // 비밀번호 유효성 검사
    function validatePasswordField(password) {
        if (!password || password.trim() === '') {
            return { valid: false, message: '비밀번호를 입력해주세요.' };
        }

        if (password.length < 4) {
            return { valid: false, message: '비밀번호는 4자 이상이어야 합니다.' };
        }

        return { valid: true };
    }

    // 필드 에러 스타일 설정
    function setFieldError(field, message) {
        field.style.borderColor = '#ef4444';
        field.style.boxShadow = '0 0 0 3px rgba(239, 68, 68, 0.1)';

        // 툴팁이나 에러 메시지 표시 (선택사항)
        showFieldTooltip(field, message);

        // 필드에 포커스
        field.focus();
    }

    // 필드 에러 스타일 제거
    function clearFieldError(field) {
        field.style.borderColor = '';
        field.style.boxShadow = '';
        hideFieldTooltip(field);
    }

    // 필드 툴팁 표시
    function showFieldTooltip(field, message) {
        // 기존 툴팁 제거
        hideFieldTooltip(field);

        const tooltip = document.createElement('div');
        tooltip.className = 'field-tooltip';
        tooltip.textContent = message;
        tooltip.style.cssText = `
            position: absolute;
            bottom: -25px;
            left: 0;
            background: #ef4444;
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            white-space: nowrap;
            z-index: 1000;
            animation: fadeIn 0.2s ease;
        `;

        field.parentElement.style.position = 'relative';
        field.parentElement.appendChild(tooltip);
    }

    // 필드 툴팁 숨김
    function hideFieldTooltip(field) {
        const tooltip = field.parentElement.querySelector('.field-tooltip');
        if (tooltip) {
            tooltip.remove();
        }
    }

    // 로딩 상태 관리
    function setLoadingState(isLoading) {
        loginBtn.disabled = isLoading;

        if (isLoading) {
            btnText.style.display = 'none';
            loadingSpinner.style.display = 'flex';
        } else {
            btnText.style.display = 'block';
            loadingSpinner.style.display = 'none';
        }
    }

    // 로그인 정보 저장
    function saveCredentials(email) {
        if (typeof(Storage) !== "undefined") {
            localStorage.setItem('rememberedEmail', email);
            // 보안상 비밀번호는 저장하지 않음
        }
    }

    // 저장된 로그인 정보 불러오기
    function loadSavedCredentials() {
        if (typeof(Storage) !== "undefined") {
            const savedEmail = localStorage.getItem('rememberedEmail');
            if (savedEmail) {
                usernameInput.value = savedEmail;
                if (rememberCheckbox) {
                    rememberCheckbox.checked = true;
                }
                // 이메일이 저장되어 있으면 비밀번호 필드에 포커스
                setTimeout(() => passwordInput.focus(), 100);
            }
        }
    }

    // 저장된 로그인 정보 삭제
    function clearSavedCredentials() {
        if (typeof(Storage) !== "undefined") {
            localStorage.removeItem('rememberedEmail');
        }
    }

    // URL 메시지 처리
    function handleUrlMessages() {
        // 에러/성공 메시지 자동 숨김 (5초 후)
        const errorMessage = document.querySelector('.error-message');
        const successMessage = document.querySelector('.success-message');

        if (errorMessage && errorMessage.style.display !== 'none') {
            setTimeout(() => {
                errorMessage.style.display = 'none';
            }, 5000);
        }

        if (successMessage && successMessage.style.display !== 'none') {
            setTimeout(() => {
                successMessage.style.display = 'none';
            }, 5000);
        }
    }

    // 키보드 단축키 처리
    function handleKeyboardShortcuts(e) {
        // ESC 키로 메시지 닫기
        if (e.key === 'Escape') {
            const messages = document.querySelectorAll('.message');
            messages.forEach(msg => msg.style.display = 'none');
        }

        // Ctrl + Enter로 로그인
        if (e.ctrlKey && e.key === 'Enter') {
            loginForm.dispatchEvent(new Event('submit'));
        }

        // Alt + U로 사용자명 필드 포커스
        if (e.altKey && e.key === 'u') {
            e.preventDefault();
            usernameInput.focus();
        }

        // Alt + P로 비밀번호 필드 포커스
        if (e.altKey && e.key === 'p') {
            e.preventDefault();
            passwordInput.focus();
        }
    }

    // 페이지 가시성 변경 처리
    function handleVisibilityChange() {
        if (document.hidden) {
            // 페이지가 숨겨질 때 로딩 상태 해제
            setLoadingState(false);
        }
    }

    // CSS 애니메이션 추가
    const style = document.createElement('style');
    style.textContent = `
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .field-tooltip {
            animation: fadeIn 0.2s ease !important;
        }
        
        /* 로딩 상태일 때 폼 비활성화 */
        .login-form.loading input,
        .login-form.loading button:not(.login-btn) {
            pointer-events: none;
            opacity: 0.6;
        }
    `;
    document.head.appendChild(style);

    // 개발 환경에서만 로그 출력
    if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
        console.log('개발 모드: 백오피스 로그인 디버깅 활성화');
        console.log('테스트 계정: admin@naver.com / 1234');

        // 개발용 단축키 (Ctrl + Shift + T로 테스트 계정 자동 입력)
        document.addEventListener('keydown', function(e) {
            if (e.ctrlKey && e.shiftKey && e.key === 'T') {
                usernameInput.value = 'admin@naver.com';
                passwordInput.value = '1234';
                console.log('테스트 계정 자동 입력 완료');
            }
        });
    }
});

// 전역 유틸리티 함수들
window.SpringBootLogin = {
    // CSRF 토큰 가져오기
    getCsrfToken: function() {
        const csrfInput = document.querySelector('input[name="_csrf"]');
        return csrfInput ? csrfInput.value : null;
    },

    // 로그인 상태 확인
    checkLoginStatus: function() {
        // Spring Security 컨텍스트 확인 (실제로는 서버에서 처리)
        return document.body.dataset.authenticated === 'true';
    },

    // 세션 만료 처리
    handleSessionExpiry: function() {
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        window.location.href = '/login?sessionExpired=true';
    },

    // 강제 로그아웃
    logout: function() {
        if (confirm('로그아웃하시겠습니까?')) {
            // Spring Security 로그아웃 엔드포인트 호출
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/logout';

            // CSRF 토큰 추가
            const csrfToken = this.getCsrfToken();
            if (csrfToken) {
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = '_csrf';
                csrfInput.value = csrfToken;
                form.appendChild(csrfInput);
            }

            document.body.appendChild(form);
            form.submit();
        }
    }
};