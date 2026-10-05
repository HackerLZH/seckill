let RQ_PREF = "";
if (window.location.hostname === "localhost") {
    RQ_PREF = "http://localhost:8080"
} else {
    RQ_PREF = "/api";
}

const token_key = 'SeckillAuthorization';
const expire_key = 'expire';
const username_key = 'username';
const role_key = 'role';

function checkLogin() {
    // 检查是否存在 SeckillAuthorization 令牌
    const token = localStorage.getItem(token_key);
    if(!token) { // 如果没有令牌，则显示登录模态框
        $('#loginModal').modal('show');
        return null;
    }
    return token;
}

// 显示 Toast 通知
function showToast(title, message, type = 'info') {
    const toastId = 'toast-' + Date.now();
    const typeClass = {
        'danger': 'bg-danger text-white',
        'success': 'bg-success text-white',
        'warning': 'bg-warning',
        'info': 'bg-info text-white'
    }[type] || 'bg-info text-white';

    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center ${typeClass} border-0" role="alert" aria-live="assertive" aria-atomic="true" style="position: fixed; top: 20px; right: 20px; z-index: 9999; min-width: 350px;">
            <div class="d-flex">
                <div class="toast-body">
                    <strong>${title}</strong>
                    <div class="mt-1">${message}</div>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    $('body').append(toastHtml);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();

    // 自动移除 DOM 元素
    toastElement.addEventListener('hidden.bs.toast', function () {
        toastElement.remove();
    });
}

// 页面加载时获取商品数据并恢复登录状态
$(document).ready(function() {
    fetchSeckillProducts();
    restoreLoginState();
    // 页面关闭 / 刷新时停止支付轮询，避免残留请求（覆盖 bfcache 恢复场景）
    window.addEventListener('pagehide', stopPaymentPolling);
    window.addEventListener('beforeunload', stopPaymentPolling);
});

// 检查登录状态并恢复 UI
function restoreLoginState() {
    let token = checkLogin();
    if (!token) {
        return;
    }
    const username = localStorage.getItem(username_key);
    const role = localStorage.getItem(role_key);
    const expireTime = localStorage.getItem(expire_key);
    if (new Date().getTime() > expireTime) {
        // token过期
        clearState();
        return;
    }
    // token 有效，恢复登录状态
    $('#loginBtn').hide();
    $('#usernameDisplay').text(username);
    $('#userMenu').show();
    // 管理员角色显示秒杀管理入口
    if (role === 'A') {
        $('#seckillManageItem').show();
    } else {
        $('#seckillManageItem').hide();
    }
}

// 获取秒杀商品数据
function fetchSeckillProducts() {
    // 这里应该替换为实际的 API 端点
    $.ajax({
        url: `${RQ_PREF}/seckill/products`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            renderProducts(response.data.current, 'current-seckill-products');
            renderProducts(response.data.upcoming, 'upcoming-seckill-products');
        },
        error: function(xhr, status, error) {
            console.error('获取商品数据失败:', error);
        }
    });
}

// 渲染商品列表
function renderProducts(products, containerId) {
    const container = $('#' + containerId);
    container.empty();
    products.forEach(product => {
        const productCard = $('<div>').addClass('col-md-4');
        productCard.html(createProductCardHTML(product, containerId));
        container.append(productCard);
    });
}

// 创建商品卡片 HTML
function createProductCardHTML(product, containerId) {
    if (containerId === 'current-seckill-products') {
        // 当前秒杀商品卡片
        const soldPercent = ((product.originStock - product.availableStock) * 100 / product.originStock).toFixed(1);
        return `
                    <div class="card product-card">
                        <img src="${product.image}" class="card-img-top" alt="${product.name}">
                        <div class="card-body">
                            <h5 class="card-title">${product.name}</h5>
                            <p class="card-text">
                                <span class="original-price">¥${product.price - 2000}</span>
                                <span class="seckill-price ms-2">¥${product.price}</span>
                            </p>
                            <div class="progress progress-thin mb-2">
                                <div class="progress-bar bg-danger" style="width: ${soldPercent}%"></div>
                            </div>
                            <small class="text-muted">已售${soldPercent}%，仅剩${product.availableStock}件</small>
                            <button class="${product.availableStock === 0? 'btn btn-outline-secondary w-100' : 'btn seckill-btn w-100 mt-2'}"
                                onclick="doKill(${product.id}, '${product.name}', '${product.image}', ${product.price})"
                                ${product.availableStock === 0? 'disabled' : ''}>
                            立即秒杀
                            </button>
                        </div>
                    </div>
                `;
    } else {
        // 即将开始的秒杀商品卡片
        return `
                    <div class="card product-card">
                        <div class="card-header bg-light">
                            ${new Date(product.startTime).toLocaleString()} 开始
                        </div>
                        <img src="${product.image}" class="card-img-top" alt="${product.name}">
                        <div class="card-body">
                            <h5 class="card-title">${product.name}</h5>
                            <p class="card-text">
                                <span class="original-price">¥${product.price - 2000}</span>
                                <span class="seckill-price ms-2">¥${product.price}</span>
                            </p>
                            <button class="btn btn-outline-secondary w-100" disabled>即将开始</button>
                        </div>
                    </div>
                `;
    }
}

function doKill(killId, name, imageUrl, price) {
    let token = checkLogin();
    if (!token) {
        return;
    }
    // 执行秒杀逻辑
    $.ajax({
        url: `${RQ_PREF}/seckill/kill/${killId}`,
        method: 'POST',
        headers: {
            [token_key]: token
        },
        success: function(response) {
            switch(response.code) {
                case '530000':
                    title = '已抢光';
                    message = '哎呀，手慢了！这件宝贝已经被抢光了，再试试其他商品吧~';
                    icon = '<i class="fas fa-truck-loading me-2"></i>';
                    break;
                case '530001':
                    title = '重复抢购';
                    message = '您已经拥有这件宝贝了，遵守规则才能让更多小伙伴参与哦~';
                    icon = '<i class="fas fa-shopping-cart me-2"></i>';
                    break;
                default:
                    if (response.data === undefined) {
                        // 秒杀成功后，显示支付选择对话框
                        // response.data对应后端返回的orderId
                        showPaymentSelection(response, name, imageUrl, price);
                        return;
                    }
                    alert('未知错误:' + response);
                    return;
            }
            // 显示个性化的 Toast 通知
            showToast(title, icon + message, 'danger');
        },
        error: function(xhr, status, error) {
            alert("未知错误");
        }
    });
}

// 显示支付方式选择对话框
function showPaymentSelection(orderId, name, imageUrl, price) {
    // 创建支付选择模态框内容
    const modalContent = `
        <div class="modal fade" id="paymentModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">恭喜抢到！请选择支付方式</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="d-flex align-items-center mb-3 p-3 bg-light rounded">
                            <img src="${imageUrl}" alt="商品图片" class="rounded me-3" style="width: 80px; height: 80px; object-fit: cover;">
                            <div>
                                <h6 class="mb-1">${name || '秒杀商品'}</h6>
                                <p class="mb-1 text-muted">订单号: ${orderId}</p>
                                <p class="mb-0 text-danger fw-bold">价格：${price}元</p>
                            </div>
                        </div>
                        <p class="mb-3">请选择支付方式：</p>
                        <div class="d-grid gap-2">
                            <button class="btn btn-primary btn-lg" onclick="initiatePayment('${orderId}', '${encodeURIComponent(name)}', ${price}, 'WECHAT')">
                                <i class="fab fa-weixin"></i> 微信支付
                            </button>
                            <button class="btn btn-success btn-lg" onclick="initiatePayment('${orderId}', '${encodeURIComponent(name)}', ${price}, 'ALIPAY')">
                                <i class="fab fa-alipay"></i> 支付宝支付
                            </button>
                            <button class="btn btn-secondary btn-lg" onclick="laterPayment('${orderId}')">
                                <i class="fas fa-clock"></i> 稍后支付
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    // 移除可能存在的旧模态框
    $('#paymentModal').remove();

    // 添加新模态框到页面
    $('body').append(modalContent);

    // 显示模态框
    $('#paymentModal').modal('show');

    // 监听模态框关闭事件，清理DOM
    $('#paymentModal').on('hidden.bs.modal', function () {
        $(this).remove();
    });
}

// 发起支付
function initiatePayment(orderId, name, price, payType) {
    // 隐藏支付选择模态框
    $('#paymentModal').modal('hide');
    // 构造支付请求
    const paymentRequest = {
        orderId: orderId,
        payType: payType,
        amount: price, // 这里应该是实际商品价格，暂时设为0.01元演示
        body: name
    };

    // 发起统一下单请求
    $.ajax({
        url: `${RQ_PREF}/payment/unified`,
        method: 'POST',
        contentType: 'application/json',
        headers: {
            [token_key]: localStorage.getItem(token_key)
        },
        data: JSON.stringify(paymentRequest),
        success: function(response) {
            if (response.success) {
                // 根据支付类型处理
                if (payType === 'WECHAT') {
                    // 微信支付 - 显示二维码或调用微信支付
                    // TODO 当前仅模拟
                    showToast('🎉 支付成功！', '恭喜您，秒杀商品购买成功！', 'success');
                    return;
                    if (response.qrCodeUrl) {
                        showQRCode(response.qrCodeUrl, orderId, 'WECHAT');
                    } else {
                        // 调用微信支付JSAPI
                        callWechatPay(response.signData);
                    }
                } else if (payType === 'ALIPAY') {
                    // 支付宝支付 - 跳转到支付宝支付页面
                    if (response.qrCodeUrl) {
                        showQRCode(response.qrCodeUrl, orderId, 'ALIPAY');
                    } else {
                        // 调用支付宝支付
                        callAlipay(response.signData);
                    }
                }
            } else {
                alert('支付创建失败：' + response.message);
            }
        },
        error: function(xhr, status, error) {
            const message = xhr.responseJSON ? xhr.responseJSON.message : '支付创建失败，请稍后重试';
            alert(message);
        }
    });
}

// 稍后支付
function laterPayment(orderId) {
    $('#paymentModal').modal('hide');
    showToast('未支付', '已记录您的订单，您可以在购物车查看并完成支付', 'warning');
}

// 全局变量用于存储轮询定时器
let paymentPollTimer = null;

// 显示二维码 + 启动支付状态轮询
function showQRCode(qrCodeUrl, orderId, payType) {
    const qrModalContent = `
        <div class="modal fade" id="qrModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">请扫描二维码支付</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body text-center">
                        <div id="qrcode-container" class="d-flex justify-content-center mb-3"></div>
                        <p>请使用${payType === 'WECHAT' ? '微信' : '<a href="https://u.alipay.cn/_7AkhPZNjSvwrRjSiiuPO9o">支付宝沙箱版</a>'}扫描二维码完成支付</p>
                        <small class="text-muted d-block mt-2">支付结果将在 2 分钟内自动确认</small>
                    </div>
                </div>
            </div>
        </div>
    `;

    $('#qrModal').remove();
    $('body').append(qrModalContent);
    $('#qrModal').modal('show');

    // 使用 QRCode.js 生成二维码
    $('#qrModal').on('shown.bs.modal', function () {
        const qrcodeContainer = document.getElementById('qrcode-container');
        qrcodeContainer.innerHTML = ''; // 清空容器
        new QRCode(qrcodeContainer, {
            text: qrCodeUrl,
            width: 250,
            height: 250,
            colorDark : "#000000",
            colorLight : "#ffffff",
            correctLevel : QRCode.CorrectLevel.H
        });
    });

    // 启动支付状态轮询
    startPaymentPolling(orderId, payType);

    // 监听模态框关闭事件，清理DOM和停止轮询
    $('#qrModal').on('hidden.bs.modal', function () {
        stopPaymentPolling();
        $(this).remove();
    });
}

// 启动支付状态轮询
function startPaymentPolling(orderId, payType) {
    // 停止之前的轮询（如果存在）
    stopPaymentPolling();

    let pollCount = 0;
    const MAX_POLL_COUNT = 40; // 最多轮询40次（约2分钟）

    paymentPollTimer = setInterval(() => {
        if (pollCount >= MAX_POLL_COUNT) {
            clearInterval(paymentPollTimer);
            paymentPollTimer = null;
            $('#qrModal').modal('hide');
            showToast('支付超时', '未在规定时间内完成支付，订单已关闭', 'warning');
            return;
        }

        $.ajax({
            url: `${RQ_PREF}/payment/status/${payType}/${orderId}`,
            method: 'GET',
            headers: {
                [token_key]: localStorage.getItem(token_key)
            },
            success: function(response) {
                if (!response.success) {
                    // 查询失败，继续轮询（可能是网络问题）
                    // console.warn('网络问题请等待');
                    return;
                }

                const tradeStatus = response.message; // 支付宝返回 TRADE_SUCCESS/WAIT_BUYER_PAY 等

                switch(tradeStatus) {
                    case 'TRADE_SUCCESS':
                        clearInterval(paymentPollTimer);
                        paymentPollTimer = null;
                        $('#qrModal').modal('hide');
                        showToast('🎉 支付成功！', '恭喜您，秒杀商品购买成功！', 'success');
                        break;
                    case 'TRADE_FINISHED': // 无状态
                    case 'TRADE_CLOSED': // 无状态
                    case 'WAIT_BUYER_PAY':
                        // 继续轮询，无需提示
                        console.log('等待用户支付...');
                        break;
                    default:
                        console.log('未知支付状态:', tradeStatus);
                }
            },
            error: function(xhr, status, error) {
                // 网络错误，继续轮询
                console.warn('轮询请求失败，继续...', error);
            }
        });

        pollCount++;
    }, 3000); // 每3秒查询一次
}

// 停止支付状态轮询
function stopPaymentPolling() {
    if (paymentPollTimer) {
        clearInterval(paymentPollTimer);
        paymentPollTimer = null;
    }
}

// 调用微信支付
function callWechatPay(signData) {
    alert('即将调用微信支付，请在微信中完成支付');
    // 实际应用中这里会调用微信JSAPI
    // WeixinJSBridge.invoke('getBrandWCPayRequest', signData, function(res){
    //     if(res.err_msg == "get_brand_wcpay_request:ok" ) {
    //         alert('支付成功');
    //     } else {
    //         alert('支付失败');
    //     }
    // });
}

// 调用支付宝支付
function callAlipay(signData) {
    alert('即将跳转到支付宝支付页面');
    // 实际应用中这里会跳转到支付宝支付页面
    // window.location.href = signData.payUrl;
}

// 点击购物车按钮
$('#cartBtn').click(function(e) {
    e.preventDefault();
    const token = checkLogin();
    if (!token) {
        $('#loginModal').modal('show');
        return;
    }
    loadOrders();
    $('#cartModal').modal('show');
});

// 加载订单列表
function loadOrders() {
    const token = localStorage.getItem(token_key);
    if (!token) return;

    $.ajax({
        url: `${RQ_PREF}/seckill/kill/orders`,
        method: 'GET',
        headers: {
            [token_key]: token
        },
        success: function(response) {
            renderOrderList(response);
        },
        error: function(xhr, status, error) {
            $('#order-list-container').html(`
                <div class="text-center py-4">
                    <i class="fas fa-exclamation-circle text-warning fa-3x mb-3"></i>
                    <h6>订单加载失败</h6>
                    <p class="text-muted">请稍后重试</p>
                </div>
            `);
            console.error('加载订单失败:', error);
        }
    });
}

// 渲染订单列表
function renderOrderList(orders) {
    if (!orders || orders.length === 0) {
        $('#order-list-container').html(`
            <div class="text-center py-4">
                <i class="fas fa-shopping-cart text-muted fa-3x mb-3"></i>
                <h6>暂无订单</h6>
                <p class="text-muted">快去参与秒杀吧</p>
            </div>
        `);
        return;
    }

    let html = '<div class="accordion" id="ordersAccordion">';

    orders.data.forEach((order, index) => {
        const orderId = order.orderId;
        const formattedTime = order.createTime ? new Date(order.createTime).toLocaleString() : '--';
        const payTime = order.payTime ? new Date(order.payTime).toLocaleString() : '--';
        const price = order.price ? parseFloat(order.price).toFixed(2) : '0.00';
        const status = order.status || 'WAIT';
        const qrcodeurl = order.qrcodeurl || '';

        let statusBadge = '';
        let actionButton = '';

        switch(status) {
            case 'WAIT':
                statusBadge = '<span class="badge bg-warning text-dark">待支付</span>';
                if (qrcodeurl) {
                    actionButton = `
                        <button class="btn btn-primary btn-sm" onclick="resumePayment('${orderId}', '${encodeURIComponent(order.name)}', ${order.price}, '${qrcodeurl}')">
                            <i class="fas fa-qrcode"></i> 继续支付
                        </button>
                    `;
                } else {
                    actionButton = `<span class="text-muted">等待生成支付二维码...</span>`;
                }
                break;
            case 'PAID':
                statusBadge = '<span class="badge bg-success">已支付</span>';
                actionButton = `<button class="btn btn-outline-success btn-sm" disabled>已支付于 ${payTime}</button>`;
                break;
            case 'CANCEL':
                statusBadge = '<span class="badge bg-secondary">已取消</span>';
                actionButton = `<button class="btn btn-outline-secondary btn-sm" disabled>订单已取消</button>`;
                break;
        }

        html += `
            <div class="accordion-item">
                <h2 class="accordion-header" id="heading${index}">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                        <div class="d-flex justify-content-between align-items-center w-100">
                            <div>
                                <span class="fw-bold">订单 #${orderId}</span>
                                ${statusBadge}
                            </div>
                            <div class="text-muted">${formattedTime}</div>
                        </div>
                    </button>
                </h2>
                <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#ordersAccordion">
                    <div class="accordion-body">
                        <div class="d-flex align-items-center mb-3">
                            <img src="${order.image || 'https://via.placeholder.com/80'}" alt="${order.name}" class="rounded me-3" style="width: Ан80px; height: 80px; object-fit: cover;">
                            <div>
                                <h6 class="mb-1">${order.name || '秒杀商品'}</h6>
                                <p class="mb-1 text-muted">订单号: ${orderId}</p>
                                <p class="mb-0 text-danger fw-bold">价格：¥${price}</p>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <div>
                                <small class="text-muted">创建时间：${formattedTime}</small>
                                ${order.payTime ? `<br><small class="text-muted">支付时间：${payTime}</small>` : ''}
                            </div>
                            <div>${actionButton}</div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    });

    html += '</div>';
    $('#order-list-container').html(html);
}

// 恢复支付（从购物车继续）
function resumePayment(orderId, name, price, qrcodeUrl) {
    const decodedName = decodeURIComponent(name);

    // Determine payType from qrcodeUrl
    let payType;
    if (qrcodeUrl.includes('weixin')) {
        payType = 'WECHAT';
    } else if (qrcodeUrl.includes('alipay')) {
        payType = 'ALIPAY';
    } else {
        // Default to WECHAT for unknown cases
        payType = 'WECHAT';
    }

    showQRCode(qrcodeUrl, orderId, payType);
    $('#cartModal').modal('hide');
}

$('#loginForm').submit(function(e) {
    e.preventDefault();

    const username = $("#login-username").val();
    const password = $("#login-password").val();

    $.ajax({
        url: `${RQ_PREF}/auth/login`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            username: username,
            password: password
        }),
        success: function(response) {
            if (response.code === '520002' || response.code === '520003') {
                showToast("登录失败", response.msg, 'danger');
            } else {
                $('#loginModal').modal('hide');
                $('#loginForm')[0].reset();
                $('#loginBtn').hide();
                $('#usernameDisplay').text(response.data.username);
                $('#userMenu').show();
                if (response.data.role === 'A') {
                    $('#seckillManageItem').show();
                } else {
                    $('#seckillManageItem').hide();
                }
                localStorage.setItem(token_key, response.data.token);
                localStorage.setItem(username_key, response.data.username);
                localStorage.setItem(role_key, response.data.role);
                localStorage.setItem(expire_key, new Date(response.data.expireTime).getTime());
            }
        },
        error: function(xhr, status, error) {
            alert('登录失败：' + (xhr.responseJSON ? xhr.responseJSON.message : '未知错误'));
        }
    });

});

$('#registerForm').submit(function(e) {
    e.preventDefault();

    const username = $("#register-username").val();
    const password = $("#register-password").val();
    const confirmPassword = $("#register-confirm-password").val();

    // 简单验证密码
    if (password !== confirmPassword) {
        alert('两次输入的密码不一致');
        return;
    }

    if (password.length < 6) {
        alert('密码长度至少 6 个字符');
        return;
    }

    // 发送注册请求
    $.ajax({
        url: `${RQ_PREF}/auth/register`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            username: username,
            password: password
        }),
        success: function(response) {
            alert('注册成功');
            // 关闭注册模态框
            $('#registerModal').modal('hide');
            // 清空表单
            $('#registerForm')[0].reset();
        },
        error: function(xhr, status, error) {
            alert('注册失败：' + (xhr.responseJSON ? xhr.responseJSON.message : '未知错误'));
        }
    });
});

// 登出功能
$('#logoutBtn').click(function(e) {
    e.preventDefault();
    $.ajax({
        url: `${RQ_PREF}/auth/logout`,
        method: 'POST',
        headers: {
            [token_key]: localStorage.getItem(token_key)
        },
        success: function(response) {
            clearState();
        },
        error: function(xhr, status, error) {
            alert('登出失败:' + (xhr.responseJSON ? xhr.responseJSON.message : '未知错误'));
        }
    })
});

function clearState() {
    localStorage.clear();
    // 隐藏用户菜单，显示登录按钮
    $('#userMenu').hide();
    $('#loginBtn').show();
}
