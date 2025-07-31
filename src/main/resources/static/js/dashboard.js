// dashboard.js - Spring Boot 대시보드 JavaScript
function logoutWithForm() {
    if (!confirm('로그아웃 하시겠습니까?')) {
        return;
    }

    // 동적으로 form 생성하여 로그아웃 요청
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/auth/logout';


    document.body.appendChild(form);
    form.submit();
}

document.addEventListener('DOMContentLoaded', function() {
    console.log('대시보드 초기화 시작');

    // 페이지 로드시 통계 로딩
    loadDashboardStats();

    console.log('대시보드 초기화 완료');
});

// =============================================================================
// 통계 관련 함수들
// =============================================================================

/**
 * 대시보드 통계 로딩
 */
async function loadDashboardStats() {
    try {
        const [accountsRes, groupCodesRes, codesRes, gifticonsRes, couponsRes] = await Promise.all([
            fetch('/api/admin/accounts/count'),
            fetch('/api/admin/group-codes/count'),
            fetch('/api/admin/codes/count'),
            fetch('/api/admin/gifticons/count'),
            fetch('/api/admin/coupons/count')
        ]);

        // 계정 수
        if (accountsRes.ok) {
            const data = await accountsRes.json();
            document.getElementById('totalAccounts').textContent = data.data?.toLocaleString() || '0';
        } else {
            document.getElementById('totalAccounts').textContent = 'N/A';
        }

        // 그룹 코드 수
        if (groupCodesRes.ok) {
            const data = await groupCodesRes.json();
            document.getElementById('totalGroupCodes').textContent = data.data?.toLocaleString() || '0';
        } else {
            document.getElementById('totalGroupCodes').textContent = 'N/A';
        }

        // 코드 수
        if (codesRes.ok) {
            const data = await codesRes.json();
            document.getElementById('totalCodes').textContent = data.data?.toLocaleString() || '0';
        } else {
            document.getElementById('totalCodes').textContent = 'N/A';
        }

        // 기프티콘 수
        if (gifticonsRes.ok) {
            const data = await gifticonsRes.json();
            document.getElementById('totalGifticons').textContent = data.data?.toLocaleString() || '0';
        } else {
            document.getElementById('totalGifticons').textContent = 'N/A';
        }

        // 쿠폰 수
        const couponsElement = document.getElementById('totalCoupons');
        if (couponsElement) {
            if (couponsRes.ok) {
                const data = await couponsRes.json();
                couponsElement.textContent = data.data?.toLocaleString() || '0';
            } else {
                couponsElement.textContent = 'N/A';
            }
        }

    } catch (error) {
        console.error('통계 로딩 실패:', error);
        document.getElementById('totalAccounts').textContent = 'Error';
        document.getElementById('totalGroupCodes').textContent = 'Error';
        document.getElementById('totalCodes').textContent = 'Error';
        document.getElementById('totalGifticons').textContent = 'Error';

        const couponsElement = document.getElementById('totalCoupons');
        if (couponsElement) {
            couponsElement.textContent = 'Error';
        }
    }
}

/**
 * 통계 새로고침
 */
function refreshStats() {
    loadDashboardStats();
}

// =============================================================================
// 계정 관리 함수들
// =============================================================================

/**
 * 계정 목록 로딩
 */
async function loadAccounts() {
    try {
        const response = await fetch('/api/admin/accounts?pageNumber=0&pageSize=50');
        const data = await response.json();

        if (data.resultCode === 200) {
            showAccountsModal(data.data);
        } else {
            alert('계정 목록을 불러오는데 실패했습니다: ' + data.message);
        }
    } catch (error) {
        console.error('계정 로딩 실패:', error);
        alert('계정 목록 로딩 중 오류가 발생했습니다.');
    }
}

/**
 * 계정 검색 모달 열기
 */
function openSearchModal() {
    const modal = createModal('계정 검색', 'search-modal');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="searchForm">
            <input type="text" id="searchKeyword" class="search-input" placeholder="이메일 또는 닉네임 입력...">
            <div class="button-group">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-primary">🔍 검색</button>
            </div>
        </form>
    `;

    document.body.appendChild(modal);

    // 폼 제출 이벤트
    document.getElementById('searchForm').addEventListener('submit', searchAccounts);
}

/**
 * 계정 검색
 */
async function searchAccounts(e) {
    e.preventDefault();

    const keyword = document.getElementById('searchKeyword').value.trim();
    if (!keyword) {
        alert('검색어를 입력해주세요.');
        return;
    }

    try {
        const response = await fetch(`/api/admin/accounts/search?keyword=${encodeURIComponent(keyword)}&pageNumber=0&pageSize=20`);
        const data = await response.json();

        if (data.resultCode === 200) {
            document.querySelector('.modal-overlay').remove();
            showAccountsModal(data.data);
        } else {
            alert('검색 실패: ' + data.message);
        }
    } catch (error) {
        console.error('계정 검색 실패:', error);
        alert('검색 중 오류가 발생했습니다.');
    }
}

/**
 * 계정 모달 표시
 */
function showAccountsModal(accounts) {
    const modal = createModal('계정 목록');
    const body = modal.querySelector('.modal-body');

    if (!accounts || accounts.length === 0) {
        body.innerHTML = '<div class="no-data">표시할 계정이 없습니다.</div>';
    } else {
        const accountCount = `<div style="margin-bottom: 1.5rem; padding: 1rem; background: linear-gradient(135deg, #e8f4fd, #d1ecf1); border-radius: 12px; border-left: 4px solid #17a2b8;">
                <strong style="color: #0c5460; font-size: 1.1rem;">총 ${accounts.length}개의 계정</strong>
            </div>`;

        const accountCards = accounts.map(account => {
            const userInfo = account.user ? `
                    <div class="detail-item">
                        <div class="detail-label">실명</div>
                        <div class="detail-value">${account.user.name || '등록되지 않음'}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">생년월일</div>
                        <div class="detail-value">${account.user.birthDate || '등록되지 않음'}</div>
                    </div>
                ` : `
                    <div class="detail-item">
                        <div class="detail-label">사용자 정보</div>
                        <div class="detail-value" style="color: #6c757d;">미등록</div>
                    </div>
                `;

            const linesInfo = account.lines && account.lines.length > 0 ? `
                    <div class="lines-section">
                        <div class="lines-title">💱 보유 회선 (${account.lines.length}개)</div>
                        ${account.lines.map(line => `
                            <div class="line-card">
                                <div class="line-header">
                                    <span class="phone-number">📞 ${line.phoneNumber}</span>
                                    ${line.plan
                ? `<span class="plan-info">📋 ${line.plan.name} (${line.plan.data === -1 ? '무제한' : line.plan.data.toLocaleString() + 'GB'})</span>`
                : '<span class="plan-info">📋 요금제 미설정</span>'}
                                </div>
                                ${line.countryCode ? `<div style="font-size: 0.9rem; color: #6c757d; margin-top: 0.5rem;">🌍 국가코드: +${line.countryCode}</div>` : ''}
                            </div>
                        `).join('')}
                    </div>
                ` : `
                    <div class="lines-section">
                        <div class="lines-title">💱 보유 회선</div>
                        <div style="padding: 1rem; text-align: center; color: #6c757d; background: #f8f9fa; border-radius: 8px;">
                            회선이 등록되지 않았습니다
                        </div>
                    </div>
                `;

            return `
                    <div class="account-card">
                        <div class="account-header">
                            <div class="account-main-info">
                                <div class="account-id">🆔 ${account.id}</div>
                                <div class="account-name">👤 ${account.nickname || '닉네임 없음'}</div>
                                <a href="mailto:${account.email}" class="account-email">✉️ ${account.email || '이메일 없음'}</a>
                            </div>
                            <div class="account-actions">
                                <button class="btn-danger" onclick="deleteAccount('${account.id}')" title="계정 삭제">🗑️ 삭제</button>
                            </div>
                        </div>

                        <div class="account-details">
                            <div class="detail-item">
                                <div class="detail-label">🏢 제공자</div>
                                <div class="detail-value">${account.provider || '직접 가입'}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">💎 마일리지</div>
                                <div class="detail-value">${account.mileage ? account.mileage.toLocaleString() : '0'} 포인트</div>
                            </div>
                            ${userInfo}
                        </div>

                        ${linesInfo}
                    </div>
                `;
        }).join('');

        body.innerHTML = accountCount + accountCards;
    }

    document.body.appendChild(modal);
}

/**
 * 계정 삭제
 */
async function deleteAccount(accountId) {
    if (!confirm('이 계정을 정말 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.')) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/accounts/${accountId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('계정이 성공적으로 삭제되었습니다.');
            loadAccounts(); // 목록 새로고침
            refreshStats();
        } else {
            alert('삭제 실패: ' + data.message);
        }
    } catch (error) {
        console.error('계정 삭제 실패:', error);
        alert('삭제 중 오류가 발생했습니다: ' + error.message);
    }
}

// =============================================================================
// 그룹 코드 관리 함수들
// =============================================================================

/**
 * 그룹 코드 목록 로딩
 */
async function loadGroupCodes() {
    try {
        const response = await fetch('/api/admin/group-codes?pageNumber=0&pageSize=20');
        const data = await response.json();

        if (data.resultCode === 200) {
            showGroupCodesModal(data.data);
        } else {
            alert('그룹 코드 목록을 불러오는데 실패했습니다: ' + data.message);
        }
    } catch (error) {
        console.error('그룹 코드 로딩 실패:', error);
        alert('그룹 코드 목록 로딩 중 오류가 발생했습니다.');
    }
}

/**
 * 그룹 코드 모달 표시
 */
function showGroupCodesModal(groupCodes) {
    const modal = createModal('그룹 코드 관리');
    const body = modal.querySelector('.modal-body');

    if (!groupCodes || groupCodes.length === 0) {
        body.innerHTML = `
                <div class="no-data">표시할 그룹 코드가 없습니다.</div>
                <div style="text-align: center; margin-top: 2rem;">
                    <button class="btn-success" onclick="openCreateGroupCodeModal()">➕ 새 그룹 코드 추가</button>
                </div>
            `;
    } else {
        const headerSection = `
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; padding: 1rem; background: linear-gradient(135deg, #e8f5e8, #d4edda); border-radius: 12px; border-left: 4px solid #27ae60;">
                    <strong style="color: #155724; font-size: 1.1rem;">📝 총 ${groupCodes.length}개의 그룹 코드</strong>
                    <button class="btn-success" onclick="openCreateGroupCodeModal()">➕ 새 그룹 코드 추가</button>
                </div>
            `;

        const groupCodeCards = groupCodes.map(groupCode => `
                <div class="groupcode-card">
                    <div class="groupcode-header">
                        <div class="groupcode-main-info">
                            <div class="groupcode-id">📋 ${groupCode.groupCode}</div>
                            <div class="groupcode-name">📂 ${groupCode.groupCodeName}</div>
                            <div class="groupcode-desc">📝 ${groupCode.groupCodeDesc || '설명이 없습니다.'}</div>
                        </div>
                        <div class="groupcode-actions">
                            <button class="btn-info" onclick="loadCodesByGroupCode('${groupCode.groupCode}')" title="그룹 코드 보기">👁️ 코드 보기</button>
                            <button class="btn-warning" onclick="openEditGroupCodeModal('${groupCode.groupCode}', '${groupCode.groupCodeName}', '${groupCode.groupCodeDesc || ''}')" title="그룹 코드 수정">✏️ 수정</button>
                            <button class="btn-danger" onclick="deleteGroupCode('${groupCode.groupCode}')" title="그룹 코드 삭제">🗑️ 삭제</button>
                        </div>
                    </div>
                </div>
            `).join('');

        body.innerHTML = headerSection + groupCodeCards;
    }

    document.body.appendChild(modal);
}

// =============================================================================
// 코드 관리 함수들
// =============================================================================

/**
 * 코드 관리 모달 열기
 */
function openCodeManagementModal() {
    const modal = createModal('코드 관리');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
            <div style="margin-bottom: 2rem;">
                <label class="form-label">그룹 코드 선택</label>
                <select id="groupCodeSelect" class="form-select" onchange="loadCodesByGroup()">
                    <option value="">그룹 코드를 선택하세요</option>
                </select>
            </div>
            <div id="codesContainer">
                <div class="no-data">그룹 코드를 선택하면 해당 코드들이 표시됩니다.</div>
            </div>
        `;

    document.body.appendChild(modal);
    loadGroupCodeOptions();
}

/**
 * 그룹 코드 옵션 로딩
 */
async function loadGroupCodeOptions() {
    try {
        const response = await fetch('/api/admin/group-codes?pageNumber=0&pageSize=100');
        const data = await response.json();

        if (data.resultCode === 200) {
            const select = document.getElementById('groupCodeSelect');
            data.data.forEach(groupCode => {
                const option = document.createElement('option');
                option.value = groupCode.groupCode;
                option.textContent = `${groupCode.groupCode} - ${groupCode.groupCodeName}`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('그룹 코드 옵션 로딩 실패:', error);
    }
}

/**
 * 그룹별 코드 로딩
 */
async function loadCodesByGroup() {
    const groupCode = document.getElementById('groupCodeSelect').value;
    const container = document.getElementById('codesContainer');

    if (!groupCode) {
        container.innerHTML = '<div class="no-data">그룹 코드를 선택하면 해당 코드들이 표시됩니다.</div>';
        return;
    }

    try {
        const response = await fetch(`/api/admin/codes?groupCode=${groupCode}&pageNumber=0&pageSize=50`);
        const data = await response.json();

        if (data.resultCode === 200) {
            showCodesInContainer(data.data, groupCode);
        } else {
            container.innerHTML = '<div class="no-data">코드를 불러오는데 실패했습니다.</div>';
        }
    } catch (error) {
        console.error('코드 로딩 실패:', error);
        container.innerHTML = '<div class="no-data">코드 로딩 중 오류가 발생했습니다.</div>';
    }
}

/**
 * 코드 컨테이너에 표시
 */
function showCodesInContainer(codes, groupCode) {
    const container = document.getElementById('codesContainer');

    if (!codes || codes.length === 0) {
        container.innerHTML = `
                <div class="no-data">이 그룹에 등록된 코드가 없습니다.</div>
                <div style="text-align: center; margin-top: 2rem;">
                    <button class="btn-success" onclick="openCreateCodeModal('${groupCode}')">➕ 새 코드 추가</button>
                </div>
            `;
        return;
    }

    const headerSection = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; padding: 1rem; background: linear-gradient(135deg, #fff3cd, #ffeaa7); border-radius: 12px; border-left: 4px solid #f39c12;">
                <strong style="color: #856404; font-size: 1.1rem;">🏷️ 총 ${codes.length}개의 코드</strong>
                <button class="btn-success" onclick="openCreateCodeModal('${groupCode}')">➕ 새 코드 추가</button>
            </div>
        `;

    const codeCards = codes.map(code => `
            <div class="code-card">
                <div class="code-header">
                    <div class="code-main-info">
                        <div class="code-id">🏷️ ${code.groupCode}-${code.code}</div>
                        <div class="code-name">📝 ${code.codeName}</div>
                        ${code.codeNameBrief ? `<div class="code-brief">💬 ${code.codeNameBrief}</div>` : ''}
                        <div class="code-order">📊 순서: ${code.orderNo}</div>
                    </div>
                    <div class="code-actions">
                        <button class="btn-warning" onclick="openEditCodeModal('${code.groupCode}', '${code.code}', '${code.codeName}', '${code.codeNameBrief || ''}', ${code.orderNo})" title="코드 수정">✏️ 수정</button>
                        <button class="btn-danger" onclick="deleteCode('${code.groupCode}', '${code.code}')" title="코드 삭제">🗑️ 삭제</button>
                    </div>
                </div>
            </div>
        `).join('');

    container.innerHTML = headerSection + codeCards;
}

// =============================================================================
// 가격 정책 관리 함수들
// =============================================================================

/**
 * 가격 정책 로딩
 */
async function loadPrices() {
    try {
        const response = await fetch('/api/admin/prices');
        const data = await response.json();

        if (data.resultCode === 200) {
            showPriceModal(data.data);
        } else {
            alert('가격 정책을 불러오는데 실패했습니다: ' + data.message);
        }
    } catch (error) {
        console.error('가격 정책 로딩 실패:', error);
        alert('가격 정책 로딩 중 오류가 발생했습니다.');
    }
}

/**
 * 가격 정책 모달 표시
 */
function showPriceModal(priceData) {
    const modal = createModal('가격 정책 관리');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
            <div class="price-card">
                <h3 style="margin-bottom: 2rem; color: #2c3e50; text-align: center;">💰 가격 정책 설정</h3>
                <form id="priceForm" class="price-form-grid">
                    <div class="form-group">
                        <label class="form-label">💵 최소 가격 (원)</label>
                        <input type="number" id="minimumPrice" class="form-input" value="${priceData?.minimumPrice || 0}" min="0" step="100" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">📊 최소 가격 비율 (%)</label>
                        <input type="number" id="minimumRate" class="form-input" value="${priceData?.minimumRate || 0}" min="0" max="100" step="0.1" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">🏛️ 수수료 (%)</label>
                        <input type="number" id="tax" class="form-input" value="${priceData?.tax || 0}" min="0" max="100" step="0.1" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">🔄 거래 가능 비율 (%)</label>
                        <input type="number" id="availableTradeRate" class="form-input" value="${priceData?.availableTradeRate || 0}" min="0" max="100" step="0.1" required>
                    </div>
                </form>
                <div class="modal-form-actions">
                    <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                    <button type="button" class="btn-success" onclick="updatePrice()">💾 저장</button>
                </div>
            </div>
        `;

    document.body.appendChild(modal);
}

/**
 * 가격 정책 업데이트
 */
async function updatePrice() {
    const minimumPrice = parseInt(document.getElementById('minimumPrice').value);
    const minimumRate = parseFloat(document.getElementById('minimumRate').value);
    const tax = parseFloat(document.getElementById('tax').value);
    const availableTradeRate = parseFloat(document.getElementById('availableTradeRate').value);

    if (isNaN(minimumPrice) || isNaN(minimumRate) || isNaN(tax) || isNaN(availableTradeRate)) {
        alert('모든 필드를 올바르게 입력해주세요.');
        return;
    }

    try {
        const response = await fetch('/api/admin/prices', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                minimumPrice: minimumPrice,
                minimumRate: minimumRate,
                tax: tax,
                availableTradeRate: availableTradeRate
            })
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('가격 정책이 성공적으로 업데이트되었습니다.');
            document.querySelector('.modal-overlay').remove();
        } else {
            alert('업데이트 실패: ' + data.message);
        }
    } catch (error) {
        console.error('가격 정책 업데이트 실패:', error);
        alert('업데이트 중 오류가 발생했습니다: ' + error.message);
    }
}

// =============================================================================
// 기프티콘 관리 함수들
// =============================================================================

/**
 * 기프티콘 목록 로딩
 */
async function loadGifticons() {
    try {
        const response = await fetch('/api/admin/gifticons?pageNumber=0&pageSize=20');
        const data = await response.json();

        if (data.resultCode === 200) {
            showGifticonsModal(data.data);
        } else {
            alert('기프티콘 목록을 불러오는데 실패했습니다: ' + data.message);
        }
    } catch (error) {
        console.error('기프티콘 로딩 실패:', error);
        alert('기프티콘 목록 로딩 중 오류가 발생했습니다.');
    }
}

/**
 * 기프티콘 모달 표시
 */
function showGifticonsModal(gifticons) {
    const modal = createModal('기프티콘 관리');
    const body = modal.querySelector('.modal-body');

    if (!gifticons || gifticons.length === 0) {
        body.innerHTML = `
                <div class="no-data">등록된 기프티콘이 없습니다.</div>
                <div style="text-align: center; margin-top: 2rem;">
                    <button class="btn-success" onclick="openCreateGifticonModal()">➕ 새 기프티콘 추가</button>
                </div>
            `;
    } else {
        const headerSection = `
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; padding: 1rem; background: linear-gradient(135deg, #fdeaea, #f8d7da); border-radius: 12px; border-left: 4px solid #e74c3c;">
                    <strong style="color: #721c24; font-size: 1.1rem;">🎁 총 ${gifticons.length}개의 기프티콘</strong>
                    <button class="btn-success" onclick="openCreateGifticonModal()">➕ 새 기프티콘 추가</button>
                </div>
            `;

        const gifticonCards = gifticons.map(gifticon => `
            <div class="gifticon-card">
                <div class="gifticon-header">
                    <div style="display: flex; gap: 1.5rem; align-items: flex-start;">
                        ${gifticon.imageUrl ? `<img src="${gifticon.imageUrl}" alt="${gifticon.name}" class="gifticon-image">` : '<div class="gifticon-image" style="background: #f8f9fa; display: flex; align-items: center; justify-content: center; color: #6c757d;">이미지 없음</div>'}
                        <div class="gifticon-main-info">
                            <div class="gifticon-id">${gifticon.id}</div>
                            <div class="gifticon-name">${gifticon.name}</div>
                            ${gifticon.description ? `<div class="gifticon-desc">${gifticon.description}</div>` : ''}
                            <div style="display: flex; align-items: center; gap: 0.5rem; margin-top: 0.8rem;">
                                <div class="gifticon-price">${gifticon.price?.toLocaleString() || 0}원</div>
                                ${gifticon.category ? `<div class="gifticon-category" id="category-${gifticon.id}">${gifticon.category}</div>` : ''}
                            </div>
                        </div>
                    </div>
                    <div class="gifticon-actions">
                        <button class="btn-warning" onclick="openEditGifticonModal('${gifticon.id}')" title="기프티콘 수정">수정</button>
                        <button class="btn-danger" onclick="deleteGifticon('${gifticon.id}')" title="기프티콘 삭제">삭제</button>
                    </div>
                </div>
            </div>
        `).join('');

        body.innerHTML = headerSection + gifticonCards;

        // 카테고리 이름 로딩
        gifticons.forEach(gifticon => {
            if (gifticon.category) {
                loadCategoryName(gifticon.id, gifticon.category);
            }
        });
    }

    document.body.appendChild(modal);
}

/**
 * 기프티콘 생성 모달
 */
function openCreateGifticonModal() {
    const modal = createModal('새 기프티콘 추가');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="createGifticonForm" enctype="multipart/form-data">
            <div class="form-group">
                <label class="form-label">기프티콘명</label>
                <input type="text" id="newGifticonName" class="form-input" placeholder="예: 스타벅스 아메리카노" required>
            </div>
            <div class="form-group">
                <label class="form-label">설명</label>
                <textarea id="newGifticonDesc" class="form-textarea" placeholder="기프티콘에 대한 설명을 입력하세요"></textarea>
            </div>
            <div class="form-group">
                <label class="form-label">가격 (원)</label>
                <input type="number" id="newGifticonPrice" class="form-input" placeholder="예: 4500" min="0" step="100" required>
            </div>
            <div class="form-group">
                <label class="form-label">카테고리</label>
                <select id="newGifticonCategory" class="form-select" required>
                    <option value="">카테고리를 선택하세요</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">이미지</label>
                <div class="file-upload">
                    <input type="file" id="newGifticonImage" accept="image/*">
                    <label for="newGifticonImage" class="file-upload-label">
                        <div class="file-upload-text">클릭하여 이미지를 선택하세요</div>
                    </label>
                </div>
            </div>
            <div class="modal-form-actions">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-success">생성</button>
            </div>
        </form>
    `;

    // 카테고리 옵션 로딩
    loadCategoryOptions();

    document.body.appendChild(modal);

    // 파일 선택 시 라벨 업데이트
    document.getElementById('newGifticonImage').addEventListener('change', function() {
        const label = this.nextElementSibling.querySelector('.file-upload-text');
        if (this.files.length > 0) {
            label.textContent = this.files[0].name;
        } else {
            label.textContent = '클릭하여 이미지를 선택하세요';
        }
    });

    // 폼 제출 이벤트
    document.getElementById('createGifticonForm').addEventListener('submit', createGifticon);
}

// =============================================================================
// 쿠폰 관리 함수들
// =============================================================================

/**
 * 쿠폰 목록 로딩
 */
function loadCoupons() {
    const modal = createModal('쿠폰 목록');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; padding: 1rem; background: linear-gradient(135deg, #fff3e0, #ffcc80); border-radius: 12px; border-left: 4px solid #ff9800;">
            <strong style="color: #e65100; font-size: 1.1rem;">🎫 쿠폰 관리</strong>
            <button class="btn-success" onclick="openCreateCouponModal()">➕ 새 쿠폰 추가</button>
        </div>
        <div id="couponsContainer">
            <div class="loading">쿠폰 목록을 불러오는 중...</div>
        </div>
    `;

    document.body.appendChild(modal);

    // 쿠폰 목록 로드
    loadCouponsData();
}

/**
 * 쿠폰 등록 모달 열기
 */
function openCreateCouponModal() {
    const modal = createModal('쿠폰 등록');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="createCouponForm">
            <div class="form-group">
                <label class="form-label">쿠폰 이름</label>
                <input type="text" id="couponName" class="form-input" placeholder="쿠폰 이름을 입력하세요">
            </div>
            <div class="form-group">
                <label class="form-label">🎁 기프티콘 선택</label>
                <select id="gifticonSelect" class="form-select">
                    <option value="">기프티콘을 선택하세요</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">🏷️ 쿠폰 코드</label>
                <select id="couponCodeSelect" class="form-select" required>
                    <option value="">쿠폰 코드를 선택하세요</option>
                </select>
            </div>
            <div class="modal-form-actions">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-success">🎫 등록</button>
            </div>
        </form>
    `;

    document.body.appendChild(modal);

    setTimeout(() => {
        // 기프티콘 목록 로드
        loadGifticonOptions();

        // 쿠폰 코드 옵션 로드 (공통코드 030)
        loadCouponCodeOptions();

        // 폼 제출 이벤트
        const form = document.getElementById('createCouponForm');
        if (form) {
            form.addEventListener('submit', createCoupon);
        }
    }, 0);
}

// =============================================================================
// 룰렛 이벤트 관리 함수들
// =============================================================================

/**
 * 룰렛 이벤트 등록 모달 열기
 */
function openRouletteEventModal() {
    const modal = createModal('룰렛 이벤트 등록');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="rouletteEventForm">
            <div class="form-group">
                <label class="form-label">이벤트 제목</label>
                <input type="text" id="eventTitle" class="form-input" placeholder="예: 설날 특별 룰렛" required>
            </div>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label class="form-label">시작 날짜</label>
                    <input type="datetime-local" id="startDate" class="form-input" required>
                </div>
                <div class="form-group">
                    <label class="form-label">종료 날짜</label>
                    <input type="datetime-local" id="endDate" class="form-input" required>
                </div>
            </div>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label class="form-label">최대 당첨자 수</label>
                    <input type="number" id="maxWinners" class="form-input" min="1" placeholder="예: 100" required>
                </div>
                <div class="form-group">
                    <label class="form-label">당첨 확률 (%)</label>
                    <input type="number" id="winProbability" class="form-input" min="0" max="100" step="0.01" placeholder="예: 15.5" required>
                </div>
            </div>
            
            <div class="form-group">
                <label class="form-label">보상 쿠폰</label>
                <select id="rewardCouponSelect" class="form-select" required>
                    <option value="">보상으로 지급할 쿠폰을 선택하세요</option>
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">활성화 상태</label>
                <select id="isActive" class="form-select">
                    <option value="true">활성화</option>
                    <option value="false">비활성화</option>
                </select>
            </div>
            
            <div class="modal-form-actions">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-success">등록</button>
            </div>
        </form>
    `;

    document.body.appendChild(modal);

    // 현재 시간을 기본값으로 설정
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);

    document.getElementById('startDate').value = now.toISOString().slice(0, 16);
    document.getElementById('endDate').value = tomorrow.toISOString().slice(0, 16);

    // 쿠폰 목록 로드
    loadCouponOptionsForRoulette();

    // 폼 제출 이벤트
    document.getElementById('rouletteEventForm').addEventListener('submit', createRouletteEvent);
}

/**
 * 룰렛 이벤트 목록 로딩
 */
function loadRouletteEvents() {
    const modal = createModal('룰렛 이벤트 목록');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; padding: 1rem; background: linear-gradient(135deg, #f3e5f5, #e1bee7); border-radius: 12px; border-left: 4px solid #9c27b0;">
            <strong style="color: #4a148c; font-size: 1.1rem;">🎲 룰렛 이벤트 관리</strong>
            <button class="btn-success" onclick="openRouletteEventModal()">➕ 새 이벤트 추가</button>
        </div>
        <div id="rouletteEventsContainer">
            <div class="loading">이벤트 목록을 불러오는 중...</div>
        </div>
    `;

    document.body.appendChild(modal);

    // 이벤트 목록 로드
    loadRouletteEventsData();
}

// =============================================================================
// 유틸리티 함수들
// =============================================================================

/**
 * 모달 생성 헬퍼 함수
 */
function createModal(title, additionalClass = '') {
    const modal = document.createElement('div');
    modal.className = `modal-overlay ${additionalClass}`;
    modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3>${title}</h3>
                    <button class="close-btn" onclick="this.closest('.modal-overlay').remove()">✕</button>
                </div>
                <div class="modal-body"></div>
            </div>
        `;
    return modal;
}

// =============================================================================
// 스텁 함수들 구현
// =============================================================================

/**
 * 특정 그룹 코드의 코드들 보기
 */
async function loadCodesByGroupCode(groupCode) {
    try {
        const response = await fetch(`/api/admin/codes?groupCode=${groupCode}&pageNumber=0&pageSize=50`);
        const data = await response.json();

        if (data.resultCode === 200) {
            showCodesModal(data.data, groupCode);
        } else {
            alert('코드 목록을 불러오는데 실패했습니다: ' + data.message);
        }
    } catch (error) {
        console.error('코드 로딩 실패:', error);
        alert('코드 목록 로딩 중 오류가 발생했습니다.');
    }
}

/**
 * 코드 목록 모달 표시
 */
function showCodesModal(codes, groupCode) {
    const modal = createModal(`코드 목록 (${groupCode})`);
    const body = modal.querySelector('.modal-body');

    if (!codes || codes.length === 0) {
        body.innerHTML = `
            <div class="no-data">이 그룹에 등록된 코드가 없습니다.</div>
            <div style="text-align: center; margin-top: 2rem;">
                <button class="btn-success" onclick="openCreateCodeModal('${groupCode}')">➕ 새 코드 추가</button>
            </div>
        `;
    } else {
        const headerSection = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; padding: 1rem; background: linear-gradient(135deg, #fff3cd, #ffeaa7); border-radius: 12px; border-left: 4px solid #f39c12;">
                <strong style="color: #856404; font-size: 1.1rem;">🏷️ 총 ${codes.length}개의 코드</strong>
                <button class="btn-success" onclick="openCreateCodeModal('${groupCode}')">➕ 새 코드 추가</button>
            </div>
        `;

        const codeCards = codes.map(code => `
            <div class="code-card">
                <div class="code-header">
                    <div class="code-main-info">
                        <div class="code-id">🏷️ ${code.groupCode}-${code.code}</div>
                        <div class="code-name">📝 ${code.codeName}</div>
                        ${code.codeNameBrief ? `<div class="code-brief">💬 ${code.codeNameBrief}</div>` : ''}
                        <div class="code-order">📊 순서: ${code.orderNo}</div>
                    </div>
                    <div class="code-actions">
                        <button class="btn-warning" onclick="openEditCodeModal('${code.groupCode}', '${code.code}', '${code.codeName}', '${code.codeNameBrief || ''}', ${code.orderNo})" title="코드 수정">✏️ 수정</button>
                        <button class="btn-danger" onclick="deleteCode('${code.groupCode}', '${code.code}')" title="코드 삭제">🗑️ 삭제</button>
                    </div>
                </div>
            </div>
        `).join('');

        body.innerHTML = headerSection + codeCards;
    }

    document.body.appendChild(modal);
}

/**
 * 그룹 코드 생성 모달
 */
function openCreateGroupCodeModal() {
    const modal = createModal('새 그룹 코드 추가');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="createGroupCodeForm">
            <div class="form-group">
                <label class="form-label">📋 그룹 코드 (3자리)</label>
                <input type="text" id="newGroupCode" class="form-input" placeholder="예: STA" maxlength="3" required>
            </div>
            <div class="form-group">
                <label class="form-label">📂 그룹 코드명</label>
                <input type="text" id="newGroupCodeName" class="form-input" placeholder="예: 거래상태" required>
            </div>
            <div class="form-group">
                <label class="form-label">📝 설명</label>
                <textarea id="newGroupCodeDesc" class="form-textarea" placeholder="그룹 코드에 대한 설명을 입력하세요"></textarea>
            </div>
            <div class="modal-form-actions">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-success">✅ 생성</button>
            </div>
        </form>
    `;

    document.body.appendChild(modal);

    // 폼 제출 이벤트
    document.getElementById('createGroupCodeForm').addEventListener('submit', createGroupCode);
}

/**
 * 그룹 코드 수정 모달
 */
function openEditGroupCodeModal(groupCode, groupCodeName, groupCodeDesc) {
    const modal = createModal('그룹 코드 수정');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="editGroupCodeForm">
            <div class="form-group">
                <label class="form-label">📋 그룹 코드</label>
                <input type="text" id="editGroupCode" class="form-input" value="${groupCode}" readonly style="background-color: #f8f9fa;">
            </div>
            <div class="form-group">
                <label class="form-label">📂 그룹 코드명</label>
                <input type="text" id="editGroupCodeName" class="form-input" value="${groupCodeName}" required>
            </div>
            <div class="form-group">
                <label class="form-label">📝 설명</label>
                <textarea id="editGroupCodeDesc" class="form-textarea">${groupCodeDesc}</textarea>
            </div>
            <div class="modal-form-actions">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-warning">✏️ 수정</button>
            </div>
        </form>
    `;

    document.body.appendChild(modal);

    // 폼 제출 이벤트
    document.getElementById('editGroupCodeForm').addEventListener('submit', (e) => updateGroupCode(e, groupCode));
}

/**
 * 그룹 코드 삭제
 */
async function deleteGroupCode(groupCode) {
    if (!confirm(`그룹 코드 "${groupCode}"를 정말 삭제하시겠습니까?\n이 그룹에 속한 모든 코드도 함께 삭제됩니다.`)) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/group-codes/${groupCode}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('그룹 코드가 성공적으로 삭제되었습니다.');
            loadGroupCodes(); // 목록 새로고침
            refreshStats();
        } else {
            alert('삭제 실패: ' + data.message);
        }
    } catch (error) {
        console.error('그룹 코드 삭제 실패:', error);
        alert('삭제 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 그룹 코드 생성
 */
async function createGroupCode(e) {
    e.preventDefault();

    const groupCode = document.getElementById('newGroupCode').value.trim();
    const groupCodeName = document.getElementById('newGroupCodeName').value.trim();
    const groupCodeDesc = document.getElementById('newGroupCodeDesc').value.trim();

    if (!groupCode || !groupCodeName) {
        alert('그룹 코드와 그룹 코드명은 필수입니다.');
        return;
    }

    if (groupCode.length !== 3) {
        alert('그룹 코드는 정확히 3자리여야 합니다.');
        return;
    }

    try {
        const response = await fetch('/api/admin/group-codes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                groupCode: groupCode,
                groupCodeName: groupCodeName,
                groupCodeDesc: groupCodeDesc
            })
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('그룹 코드가 성공적으로 생성되었습니다.');
            document.querySelector('.modal-overlay').remove();
            loadGroupCodes(); // 목록 새로고침
            refreshStats();
        } else {
            alert('생성 실패: ' + data.message);
        }
    } catch (error) {
        console.error('그룹 코드 생성 실패:', error);
        alert('생성 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 그룹 코드 수정
 */
async function updateGroupCode(e, groupCode) {
    e.preventDefault();

    const groupCodeName = document.getElementById('editGroupCodeName').value.trim();
    const groupCodeDesc = document.getElementById('editGroupCodeDesc').value.trim();

    if (!groupCodeName) {
        alert('그룹 코드명은 필수입니다.');
        return;
    }

    try {
        const response = await fetch(`/api/admin/group-codes/${groupCode}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                groupCode: groupCode,
                groupCodeName: groupCodeName,
                groupCodeDesc: groupCodeDesc
            })
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('그룹 코드가 성공적으로 수정되었습니다.');
            document.querySelector('.modal-overlay').remove();
            loadGroupCodes(); // 목록 새로고침
        } else {
            alert('수정 실패: ' + data.message);
        }
    } catch (error) {
        console.error('그룹 코드 수정 실패:', error);
        alert('수정 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 코드 생성 모달
 */
function openCreateCodeModal(groupCode) {
    const modal = createModal('새 코드 추가');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="createCodeForm">
            <div class="form-group">
                <label class="form-label">📋 그룹 코드</label>
                <input type="text" id="newCodeGroupCode" class="form-input" value="${groupCode}" readonly style="background-color: #f8f9fa;">
            </div>
            <div class="form-group">
                <label class="form-label">🏷️ 코드 (3자리)</label>
                <input type="text" id="newCode" class="form-input" placeholder="예: 001" maxlength="3" required>
            </div>
            <div class="form-group">
                <label class="form-label">📝 코드명</label>
                <input type="text" id="newCodeName" class="form-input" placeholder="예: 거래완료" required>
            </div>
            <div class="form-group">
                <label class="form-label">💬 간략명</label>
                <input type="text" id="newCodeNameBrief" class="form-input" placeholder="예: 완료">
            </div>
            <div class="form-group">
                <label class="form-label">📊 정렬순서</label>
                <input type="number" id="newCodeOrder" class="form-input" value="1" min="0" required>
            </div>
            <div class="modal-form-actions">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-success">✅ 생성</button>
            </div>
        </form>
    `;

    document.body.appendChild(modal);

    // 폼 제출 이벤트
    document.getElementById('createCodeForm').addEventListener('submit', createCode);
}

/**
 * 코드 수정 모달
 */
function openEditCodeModal(groupCode, code, codeName, codeNameBrief, orderNo) {
    const modal = createModal('코드 수정');
    const body = modal.querySelector('.modal-body');

    body.innerHTML = `
        <form id="editCodeForm">
            <div class="form-group">
                <label class="form-label">📋 그룹 코드</label>
                <input type="text" id="editCodeGroupCode" class="form-input" value="${groupCode}" readonly style="background-color: #f8f9fa;">
            </div>
            <div class="form-group">
                <label class="form-label">🏷️ 코드</label>
                <input type="text" id="editCode" class="form-input" value="${code}" readonly style="background-color: #f8f9fa;">
            </div>
            <div class="form-group">
                <label class="form-label">📝 코드명</label>
                <input type="text" id="editCodeName" class="form-input" value="${codeName}" required>
            </div>
            <div class="form-group">
                <label class="form-label">💬 간략명</label>
                <input type="text" id="editCodeNameBrief" class="form-input" value="${codeNameBrief}">
            </div>
            <div class="form-group">
                <label class="form-label">📊 정렬순서</label>
                <input type="number" id="editCodeOrder" class="form-input" value="${orderNo}" min="0" required>
            </div>
            <div class="modal-form-actions">
                <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                <button type="submit" class="btn-warning">✏️ 수정</button>
            </div>
        </form>
    `;

    document.body.appendChild(modal);

    // 폼 제출 이벤트
    document.getElementById('editCodeForm').addEventListener('submit', (e) => updateCode(e, groupCode, code));
}

/**
 * 코드 생성
 */
async function createCode(e) {
    e.preventDefault();

    const groupCode = document.getElementById('newCodeGroupCode').value.trim();
    const code = document.getElementById('newCode').value.trim();
    const codeName = document.getElementById('newCodeName').value.trim();
    const codeNameBrief = document.getElementById('newCodeNameBrief').value.trim();
    const orderNo = parseInt(document.getElementById('newCodeOrder').value);

    if (!code || !codeName) {
        alert('코드와 코드명은 필수입니다.');
        return;
    }

    if (code.length !== 3) {
        alert('코드는 정확히 3자리여야 합니다.');
        return;
    }

    try {
        const response = await fetch('/api/admin/codes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                groupCode: groupCode,
                code: code,
                codeName: codeName,
                codeNameBrief: codeNameBrief,
                orderNo: orderNo
            })
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('코드가 성공적으로 생성되었습니다.');
            document.querySelector('.modal-overlay').remove();
            loadCodesByGroup(); // 목록 새로고침
            refreshStats();
        } else {
            alert('생성 실패: ' + data.message);
        }
    } catch (error) {
        console.error('코드 생성 실패:', error);
        alert('생성 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 코드 수정
 */
async function updateCode(e, groupCode, code) {
    e.preventDefault();

    const codeName = document.getElementById('editCodeName').value.trim();
    const codeNameBrief = document.getElementById('editCodeNameBrief').value.trim();
    const orderNo = parseInt(document.getElementById('editCodeOrder').value);

    if (!codeName) {
        alert('코드명은 필수입니다.');
        return;
    }

    try {
        const response = await fetch(`/api/admin/codes/${groupCode}/${code}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                groupCode: groupCode,
                code: code,
                codeName: codeName,
                codeNameBrief: codeNameBrief,
                orderNo: orderNo
            })
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('코드가 성공적으로 수정되었습니다.');
            document.querySelector('.modal-overlay').remove();
            loadCodesByGroup(); // 목록 새로고침
        } else {
            alert('수정 실패: ' + data.message);
        }
    } catch (error) {
        console.error('코드 수정 실패:', error);
        alert('수정 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 코드 삭제
 */
async function deleteCode(groupCode, code) {
    if (!confirm(`코드 "${groupCode}-${code}"를 정말 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/codes/${groupCode}/${code}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('코드가 성공적으로 삭제되었습니다.');
            loadCodesByGroup(); // 목록 새로고침
            refreshStats();
        } else {
            alert('삭제 실패: ' + data.message);
        }
    } catch (error) {
        console.error('코드 삭제 실패:', error);
        alert('삭제 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 기프티콘 수정 모달
 */
async function openEditGifticonModal(gifticonId) {
    try {
        const response = await fetch(`/api/admin/gifticons/${gifticonId}`);
        const data = await response.json();

        if (data.resultCode !== 200) {
            alert('기프티콘 정보를 불러오는데 실패했습니다.');
            return;
        }

        const gifticon = data.data;
        const modal = createModal('기프티콘 수정');
        const body = modal.querySelector('.modal-body');

        body.innerHTML = `
            <form id="editGifticonForm" enctype="multipart/form-data">
                <input type="hidden" id="editGifticonId" value="${gifticon.id}">
                <div class="form-group">
                    <label class="form-label">기프티콘명</label>
                    <input type="text" id="editGifticonName" class="form-input" value="${gifticon.name}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">설명</label>
                    <textarea id="editGifticonDesc" class="form-textarea">${gifticon.description || ''}</textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">가격 (원)</label>
                    <input type="number" id="editGifticonPrice" class="form-input" value="${gifticon.price}" min="0" step="100" required>
                </div>
                <div class="form-group">
                    <label class="form-label">카테고리</label>
                    <select id="editGifticonCategory" class="form-select" required>
                        <option value="">카테고리를 선택하세요</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">현재 이미지</label>
                    ${gifticon.imageUrl ? `<img src="${gifticon.imageUrl}" alt="${gifticon.name}" style="width: 200px; height: 200px; object-fit: cover; border-radius: 8px; margin-bottom: 1rem;">` : '<div style="color: #6c757d;">이미지가 없습니다.</div>'}
                </div>
                <div class="form-group">
                    <label class="form-label">새 이미지 (선택사항)</label>
                    <div class="file-upload">
                        <input type="file" id="editGifticonImage" accept="image/*">
                        <label for="editGifticonImage" class="file-upload-label">
                            <div class="file-upload-text">클릭하여 새 이미지를 선택하세요</div>
                        </label>
                    </div>
                </div>
                <div class="modal-form-actions">
                    <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                    <button type="submit" class="btn-warning">수정</button>
                </div>
            </form>
        `;

        document.body.appendChild(modal);

        // 카테고리 옵션 로딩
        await loadCategoryOptions('editGifticonCategory', gifticon.category);

        // 파일 선택 이벤트
        document.getElementById('editGifticonImage').addEventListener('change', function() {
            const label = this.nextElementSibling.querySelector('.file-upload-text');
            if (this.files.length > 0) {
                label.textContent = this.files[0].name;
            } else {
                label.textContent = '클릭하여 새 이미지를 선택하세요';
            }
        });

        // 폼 제출 이벤트
        document.getElementById('editGifticonForm').addEventListener('submit', updateGifticon);

    } catch (error) {
        console.error('기프티콘 상세 정보 로딩 실패:', error);
        alert('기프티콘 정보 로딩 중 오류가 발생했습니다.');
    }
}

/**
 * 기프티콘 생성
 */
async function createGifticon(e) {
    e.preventDefault();

    const name = document.getElementById('newGifticonName').value.trim();
    const description = document.getElementById('newGifticonDesc').value.trim();
    const price = parseInt(document.getElementById('newGifticonPrice').value);
    const image = document.getElementById('newGifticonImage').files[0];
    const category = document.getElementById('newGifticonCategory').value.trim();

    if (!name || isNaN(price)) {
        alert('기프티콘명과 가격은 필수입니다.');
        return;
    }

    try {
        const formData = new FormData();
        formData.append('name', name);
        formData.append('description', description);
        formData.append('price', price);
        formData.append('category', category);
        if (image) {
            formData.append('image', image);
        }

        const response = await fetch('/api/admin/gifticons', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('기프티콘이 성공적으로 생성되었습니다.');
            document.querySelector('.modal-overlay').remove();
            loadGifticons(); // 목록 새로고침
            refreshStats();
        } else {
            alert('생성 실패: ' + data.message);
        }
    } catch (error) {
        console.error('기프티콘 생성 실패:', error);
        alert('생성 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 기프티콘 수정
 */
async function updateGifticon(e) {
    e.preventDefault();

    const id = document.getElementById('editGifticonId').value;
    const name = document.getElementById('editGifticonName').value.trim();
    const description = document.getElementById('editGifticonDesc').value.trim();
    const price = parseInt(document.getElementById('editGifticonPrice').value);
    const image = document.getElementById('editGifticonImage').files[0];
    const category = document.getElementById('editGifticonCategory').value;

    if (!name || isNaN(price)) {
        alert('기프티콘명과 가격은 필수입니다.');
        return;
    }

    try {
        const formData = new FormData();
        formData.append('name', name);
        formData.append('description', description);
        formData.append('price', price);
        formData.append('category', category);
        if (image) {
            formData.append('image', image);
        }

        const response = await fetch(`/api/admin/gifticons/${id}`, {
            method: 'PUT',
            body: formData
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('기프티콘이 성공적으로 수정되었습니다.');
            document.querySelector('.modal-overlay').remove();
            loadGifticons(); // 목록 새로고침
        } else {
            alert('수정 실패: ' + data.message);
        }
    } catch (error) {
        console.error('기프티콘 수정 실패:', error);
        alert('수정 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 기프티콘 삭제
 */
async function deleteGifticon(gifticonId) {
    if (!confirm(`기프티콘을 정말 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/gifticons/${gifticonId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();

        if (data.resultCode === 200) {
            alert('기프티콘이 성공적으로 삭제되었습니다.');
            loadGifticons(); // 목록 새로고침
            refreshStats();
        } else {
            alert('삭제 실패: ' + data.message);
        }
    } catch (error) {
        console.error('기프티콘 삭제 실패:', error);
        alert('삭제 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 카테고리 옵션 로딩
 */
async function loadCategoryOptions(selectId = 'newGifticonCategory', selectedValue = '') {
    try {
        const response = await fetch('/api/admin/codes?groupCode=080&pageNumber=0&pageSize=100');
        const data = await response.json();

        if (data.resultCode === 200) {
            const select = document.getElementById(selectId);
            data.data.forEach(category => {
                const option = document.createElement('option');
                option.value = category.code;
                option.textContent = `${category.code} - ${category.codeName}`;
                if (category.code === selectedValue) {
                    option.selected = true;
                }
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('카테고리 옵션 로딩 실패:', error);
    }
}

/**
 * 카테고리 이름 로딩
 */
async function loadCategoryName(gifticonId, categoryCode) {
    try {
        const response = await fetch(`/api/admin/codes/080/${categoryCode}`);
        const data = await response.json();

        if (data.resultCode === 200) {
            const categoryElement = document.getElementById(`category-${gifticonId}`);
            if (categoryElement) {
                categoryElement.textContent = `${categoryCode} - ${data.data.codeName}`;
            }
        }
    } catch (error) {
        console.error('카테고리 이름 로딩 실패:', error);
    }
}

/**
 * 쿠폰 데이터 로드
 */
async function loadCouponsData() {
    try {
        const response = await fetch('/api/admin/coupons?pageNumber=0&pageSize=50');
        const result = await response.json();

        if (result.resultCode === 200) {
            displayCoupons(result.data);
        } else {
            document.getElementById('couponsContainer').innerHTML =
                `<div class="no-data">쿠폰 목록을 불러올 수 없습니다: ${result.message}</div>`;
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('couponsContainer').innerHTML =
            '<div class="no-data">쿠폰 목록을 불러오는 중 오류가 발생했습니다.</div>';
    }
}

/**
 * 쿠폰 목록 표시
 */
function displayCoupons(coupons) {
    const container = document.getElementById('couponsContainer');
    if (!container) return;

    if (!coupons || coupons.length === 0) {
        container.innerHTML = `
            <div class="no-data">등록된 쿠폰이 없습니다.</div>
            <div style="text-align: center; margin-top: 2rem;">
                <button class="btn-success" onclick="openCreateCouponModal()">➕ 새 쿠폰 추가</button>
            </div>
        `;
        return;
    }

    const couponsHtml = coupons.map(coupon => `
        <div class="account-card" style="border-left: 4px solid #ff9800;">
            <div class="account-header">
                <div style="display: flex; gap: 1.5rem; align-items: flex-start;">
                    ${coupon.gifticonImageUrl ?
        `<img src="${coupon.gifticonImageUrl}" alt="${coupon.gifticonName}" style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px;">` :
        '<div style="width: 80px; height: 80px; background: #f8f9fa; display: flex; align-items: center; justify-content: center; border-radius: 8px; color: #6c757d;">이미지<br>없음</div>'
    }
                    <div class="account-main-info">
                        <div class="account-id">🎫 ${coupon.id}</div>
                        <div class="account-name">🎁 ${coupon.gifticonName || '기프티콘 정보 없음'}</div>
                        <div style="color: #ff9800; font-weight: 600; margin-top: 0.5rem;">
                            💰 ${coupon.gifticonPrice ? coupon.gifticonPrice.toLocaleString() : '0'}원
                        </div>
                    </div>
                </div>
                <div class="account-actions">
                    <button class="btn-info" onclick="openEditCouponModal('${coupon.id}')" title="쿠폰 수정">✏️ 수정</button>
                    <button class="btn-danger" onclick="deleteCoupon('${coupon.id}')" title="쿠폰 삭제">🗑️ 삭제</button>
                </div>
            </div>

            <div class="account-details">
                <div class="detail-item">
                    <div class="detail-label">🏷️ 쿠폰 코드</div>
                    <div class="detail-value">${coupon.couponCode || '미설정'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">📅 생성일</div>
                    <div class="detail-value">${new Date(coupon.createdAt).toLocaleString()}</div>
                </div>
            </div>
        </div>
    `).join('');

    container.innerHTML = couponsHtml;
}

/**
 * 기프티콘 옵션 로드
 */
async function loadGifticonOptions() {
    try {
        const response = await fetch('/api/admin/gifticons?pageNumber=0&pageSize=100');
        const result = await response.json();

        if (result.resultCode === 200) {
            const select = document.getElementById('gifticonSelect');
            result.data.forEach(gifticon => {
                const option = document.createElement('option');
                option.value = gifticon.id;
                option.textContent = `${gifticon.name} (${gifticon.price.toLocaleString()}원)`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('기프티콘 옵션 로딩 실패:', error);
    }
}

/**
 * 쿠폰 코드 옵션 로드 (공통코드 030)
 */
async function loadCouponCodeOptions() {
    try {
        const response = await fetch('/api/admin/codes?groupCode=030&pageNumber=0&pageSize=100');
        const result = await response.json();

        if (result.resultCode === 200) {
            const select = document.getElementById('couponCodeSelect');
            result.data.forEach(code => {
                const option = document.createElement('option');
                option.value = code.code;
                option.textContent = `${code.code} - ${code.codeName}`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('쿠폰 코드 옵션 로딩 실패:', error);
    }
}

/**
 * 쿠폰 생성
 */
async function createCoupon(e) {
    e.preventDefault();

    const id = document.getElementById('couponName').value;
    const gifticonId = document.getElementById('gifticonSelect').value;
    const couponCode = document.getElementById('couponCodeSelect').value;

    if (!couponCode) {
        alert('쿠폰 코드는 필수입니다.');
        return;
    }

    const couponData = {
        id: id,
        gifticonId: gifticonId,
        couponCode: couponCode,
    };

    try {
        const response = await fetch('/api/admin/coupons', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(couponData)
        });

        const result = await response.json();

        if (result.resultCode === 200) {
            alert('쿠폰이 성공적으로 등록되었습니다.');
            document.querySelector('.modal-overlay').remove();
            refreshStats();
        } else {
            alert('등록 실패: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('등록 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 쿠폰 수정 모달
 */
async function openEditCouponModal(couponId) {
    try {
        const response = await fetch(`/api/admin/coupons/${couponId}`);
        const result = await response.json();

        if (result.resultCode !== 200) {
            alert('쿠폰 정보를 불러오는데 실패했습니다.');
            return;
        }

        const coupon = result.data;
        const modal = createModal('쿠폰 수정');
        const body = modal.querySelector('.modal-body');

        body.innerHTML = `
            <form id="editCouponForm">
                <input type="hidden" id="editCouponId" value="${coupon.id}">
                
                <div class="form-group">
                    <label class="form-label">🎁 기프티콘 선택</label>
                    <select id="editGifticonSelect" class="form-select" required>
                        <option value="">기프티콘을 선택하세요</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label class="form-label">🏷️ 쿠폰 코드</label>
                    <select id="editCouponCodeSelect" class="form-select" required>
                        <option value="">쿠폰 코드를 선택하세요</option>
                    </select>
                </div>
                
                <div class="modal-form-actions">
                    <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">취소</button>
                    <button type="submit" class="btn-warning">✏️ 수정</button>
                </div>
            </form>
        `;

        document.body.appendChild(modal);

        // 기프티콘 옵션 로드
        await loadEditGifticonOptions(coupon.gifticonId);

        // 쿠폰 코드 옵션 로드
        await loadEditCouponCodeOptions(coupon.couponCode);

        // 폼 제출 이벤트
        document.getElementById('editCouponForm').addEventListener('submit', (e) => updateCoupon(e, couponId));

    } catch (error) {
        console.error('쿠폰 수정 모달 오픈 실패:', error);
        alert('쿠폰 정보 로딩 중 오류가 발생했습니다.');
    }
}

/**
 * 수정용 기프티콘 옵션 로드
 */
async function loadEditGifticonOptions(selectedGifticonId) {
    try {
        const response = await fetch('/api/admin/gifticons?pageNumber=0&pageSize=100');
        const result = await response.json();

        if (result.resultCode === 200) {
            const select = document.getElementById('editGifticonSelect');
            result.data.forEach(gifticon => {
                const option = document.createElement('option');
                option.value = gifticon.id;
                option.textContent = `${gifticon.name} (${gifticon.price.toLocaleString()}원)`;
                if (gifticon.id === selectedGifticonId) {
                    option.selected = true;
                }
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('기프티콘 옵션 로딩 실패:', error);
    }
}

/**
 * 수정용 쿠폰 코드 옵션 로드
 */
async function loadEditCouponCodeOptions(selectedCouponCode) {
    try {
        const response = await fetch('/api/admin/codes?groupCode=030&pageNumber=0&pageSize=100');
        const result = await response.json();

        if (result.resultCode === 200) {
            const select = document.getElementById('editCouponCodeSelect');
            result.data.forEach(code => {
                const option = document.createElement('option');
                option.value = code.code;
                option.textContent = `${code.code} - ${code.codeName}`;
                if (code.code === selectedCouponCode) {
                    option.selected = true;
                }
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('쿠폰 코드 옵션 로딩 실패:', error);
    }
}

/**
 * 쿠폰 수정
 */
async function updateCoupon(e, couponId) {
    e.preventDefault();

    const gifticonId = document.getElementById('editGifticonSelect').value;
    const couponCode = document.getElementById('editCouponCodeSelect').value;

    if (!gifticonId || !couponCode) {
        alert('모든 필수 필드를 입력해주세요.');
        return;
    }

    const couponData = {
        gifticonId: gifticonId,
        couponCode: couponCode
    };

    try {
        const response = await fetch(`/api/admin/coupons/${couponId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(couponData)
        });

        const result = await response.json();

        if (result.resultCode === 200) {
            alert('쿠폰이 성공적으로 수정되었습니다.');
            document.querySelector('.modal-overlay').remove();
            loadCouponsData();
        } else {
            alert('수정 실패: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('수정 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 쿠폰 삭제
 */
async function deleteCoupon(couponId) {
    if (!confirm('이 쿠폰을 정말 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.')) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/coupons/${couponId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const result = await response.json();

        if (result.resultCode === 200) {
            alert('쿠폰이 성공적으로 삭제되었습니다.');
            loadCouponsData();
            refreshStats();
        } else {
            alert('삭제 실패: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('삭제 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 룰렛용 쿠폰 옵션 로드
 */
async function loadCouponOptionsForRoulette() {
    try {
        const response = await fetch('/api/admin/coupons?pageNumber=0&pageSize=100');
        const result = await response.json();

        if (result.resultCode === 200) {
            const select = document.getElementById('rewardCouponSelect');
            result.data.forEach(coupon => {
                const option = document.createElement('option');
                option.value = coupon.id;
                option.textContent = `${coupon.gifticonName} (${coupon.couponCode})`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('쿠폰 옵션 로딩 실패:', error);
    }
}

/**
 * 룰렛 이벤트 생성
 */
async function createRouletteEvent(e) {
    e.preventDefault();

    const title = document.getElementById('eventTitle').value.trim();
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const maxWinners = parseInt(document.getElementById('maxWinners').value);
    const winProbabilityPercent = parseFloat(document.getElementById('winProbability').value);
    const rewardCouponId = document.getElementById('rewardCouponSelect').value;
    const isActive = document.getElementById('isActive').value === 'true';

    if (!title || !startDate || !endDate || !maxWinners || isNaN(winProbabilityPercent) || !rewardCouponId) {
        alert('모든 필드를 올바르게 입력해주세요.');
        return;
    }

    if (new Date(startDate) >= new Date(endDate)) {
        alert('시작 날짜는 종료 날짜보다 이전이어야 합니다.');
        return;
    }

    if (winProbabilityPercent < 0 || winProbabilityPercent > 100) {
        alert('당첨 확률은 0~100 사이의 값이어야 합니다.');
        return;
    }

    // 퍼센트를 소수로 변환
    const winProbabilityDecimal = winProbabilityPercent / 100;

    const eventData = {
        title: title,
        startDate: startDate,
        endDate: endDate,
        maxWinners: maxWinners,
        winProbability: winProbabilityDecimal,
        rewardCouponId: rewardCouponId,
        isActive: isActive
    };

    try {
        const response = await fetch('/api/admin/roulette/events', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(eventData)
        });

        const result = await response.json();

        if (result.resultCode === 200) {
            alert('룰렛 이벤트가 성공적으로 등록되었습니다.');
            document.querySelector('.modal-overlay').remove();
            refreshStats();
        } else {
            alert('등록 실패: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('등록 중 오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 룰렛 이벤트 데이터 로드
 */
async function loadRouletteEventsData() {
    try {
        const response = await fetch('/api/admin/roulette/events?pageNumber=0&pageSize=50');
        const result = await response.json();

        if (result.resultCode === 200) {
            displayRouletteEvents(result.data);
        } else {
            document.getElementById('rouletteEventsContainer').innerHTML =
                `<div class="no-data">이벤트 목록을 불러올 수 없습니다: ${result.message}</div>`;
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('rouletteEventsContainer').innerHTML =
            '<div class="no-data">이벤트 목록을 불러오는 중 오류가 발생했습니다.</div>';
    }
}

/**
 * 룰렛 이벤트 목록 표시
 */
function displayRouletteEvents(events) {
    const container = document.getElementById('rouletteEventsContainer');
    if (!container) return;

    if (!events || events.length === 0) {
        container.innerHTML = `
            <div class="no-data">등록된 룰렛 이벤트가 없습니다.</div>
            <div style="text-align: center; margin-top: 2rem;">
                <button class="btn-success" onclick="openRouletteEventModal()">새 이벤트 추가</button>
            </div>
        `;
        return;
    }

    const eventsHtml = events.map(event => `
        <div class="account-card" style="border-left: 4px solid #9c27b0;">
            <div class="account-header">
                <div class="account-main-info">
                    <div class="account-id">${event.id}</div>
                    <div class="account-name">${event.title}</div>
                </div>
                <div class="account-actions">
                    <button class="btn-warning" onclick="toggleEventStatus('${event.id}', ${!event.isActive})" title="상태 변경">
                        ${event.isActive ? '비활성화' : '활성화'}
                    </button>
                    <button class="btn-danger" onclick="deleteRouletteEvent('${event.id}')" title="이벤트 삭제">삭제</button>
                </div>
            </div>

            <div class="account-details">
                <div class="detail-item">
                    <div class="detail-label">시작 날짜</div>
                    <div class="detail-value">${new Date(event.startDate).toLocaleString()}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">종료 날짜</div>
                    <div class="detail-value">${new Date(event.endDate).toLocaleString()}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">당첨자</div>
                    <div class="detail-value">${event.currentWinners} / ${event.maxWinners} 명</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">당첨 확률</div>
                    <div class="detail-value">${(event.winProbability * 100).toFixed(2)}%</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">보상 쿠폰</div>
                    <div class="detail-value">${event.rewardGifticonName || '미설정'} (${event.rewardCouponName || '미설정'})</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">상태</div>
                    <div class="detail-value">
                        <span style="color: ${event.isActive ? '#28a745' : '#dc3545'}; font-weight: bold;">
                            ${event.isActive ? '활성' : '비활성'}
                        </span>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">생성일</div>
                    <div class="detail-value">${new Date(event.createdAt).toLocaleString()}</div>
                </div>
            </div>
        </div>
    `).join('');

    container.innerHTML = eventsHtml;
}

/**
 * 이벤트 상태 토글
 */
async function toggleEventStatus(eventId, newStatus) {
    try {
        const response = await fetch(`/api/admin/roulette/events/${eventId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ isActive: newStatus })
        });

        const result = await response.json();

        if (result.resultCode === 200) {
            alert('상태가 변경되었습니다.');
            loadRouletteEventsData(); // 목록 새로고침
        } else {
            alert('상태 변경 실패: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('상태 변경 중 오류가 발생했습니다.');
    }
}

/**
 * 룰렛 이벤트 삭제
 */
async function deleteRouletteEvent(eventId) {
    if (!confirm('이 룰렛 이벤트를 정말 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.')) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/roulette/events/${eventId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const result = await response.json();

        if (result.resultCode === 200) {
            alert('룰렛 이벤트가 성공적으로 삭제되었습니다.');
            loadRouletteEventsData(); // 목록 새로고침
            refreshStats(); // 통계 새로고침
        } else {
            alert('삭제 실패: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('삭제 중 오류가 발생했습니다: ' + error.message);
    }
}

