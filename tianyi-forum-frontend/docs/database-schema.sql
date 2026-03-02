# 天翼论坛 - 数据库建表脚本

-- ==================== 用户表 ====================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（bcrypt加密）',
    role ENUM('user', 'moderator', 'admin') DEFAULT 'user' COMMENT '角色',
    status ENUM('active', 'banned') DEFAULT 'active' COMMENT '状态',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_login_at DATETIME COMMENT '最后登录时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==================== 板块表 ====================
CREATE TABLE boards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '板块ID',
    name VARCHAR(100) NOT NULL COMMENT '板块名称',
    description TEXT COMMENT '板块描述',
    moderator_id BIGINT COMMENT '版主用户ID',
    post_count INT DEFAULT 0 COMMENT '帖子数量',
    sort INT DEFAULT 0 COMMENT '排序权重',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (moderator_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='板块表';

-- ==================== 帖子表 ====================
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '正文内容',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    board_id BIGINT COMMENT '所属板块ID',
    status ENUM('normal', 'pending', 'archived') DEFAULT 'pending' COMMENT '状态：正常/待审/下架',
    is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    is_featured BOOLEAN DEFAULT FALSE COMMENT '是否加精',
    hot_score INT DEFAULT 0 COMMENT '热度分数',
    violation_score INT DEFAULT 0 COMMENT '违规权重',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE SET NULL,
    INDEX idx_author_id (author_id),
    INDEX idx_board_id (board_id),
    INDEX idx_status (status),
    INDEX idx_hot_score (hot_score DESC),
    INDEX idx_violation_score (violation_score DESC),
    INDEX idx_created_at (created_at DESC),
    FULLTEXT INDEX ft_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- ==================== 评论表 ====================
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '所属帖子ID',
    author_id BIGINT NOT NULL COMMENT '评论者ID',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID（用于回复）',
    status ENUM('normal', 'deleted') DEFAULT 'normal' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE SET NULL,
    INDEX idx_post_id (post_id),
    INDEX idx_author_id (author_id),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ==================== 投诉表 ====================
CREATE TABLE complaints (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投诉ID',
    target_type ENUM('post', 'comment', 'user') NOT NULL COMMENT '举报对象类型',
    target_id BIGINT NOT NULL COMMENT '举报对象ID',
    reason VARCHAR(500) NOT NULL COMMENT '投诉原因',
    evidence VARCHAR(500) COMMENT '证据链接（截图等）',
    reporter_id BIGINT NOT NULL COMMENT '举报人ID',
    status ENUM('pending', 'resolved') DEFAULT 'pending' COMMENT '状态',
    handle_note TEXT COMMENT '处理说明',
    handler_id BIGINT COMMENT '处理人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    handled_at DATETIME COMMENT '处理时间',
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (handler_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_target (target_type, target_id),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉表';

-- ==================== 敏感词表 ====================
CREATE TABLE sensitive_words (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '敏感词ID',
    word VARCHAR(100) UNIQUE NOT NULL COMMENT '敏感词',
    level ENUM('高', '中', '低') DEFAULT '中' COMMENT '敏感等级',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_word (word),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- ==================== 管理员操作日志表 ====================
CREATE TABLE admin_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    admin_id BIGINT NOT NULL COMMENT '管理员ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型（ban_user, delete_post等）',
    target_type VARCHAR(50) COMMENT '操作对象类型',
    target_id BIGINT COMMENT '操作对象ID',
    reason TEXT COMMENT '操作原因',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';

-- ==================== 初始数据 ====================

-- 插入管理员账号（密码: admin123，使用 bcrypt 加密）
INSERT INTO users (username, email, password, role, status) VALUES 
('admin', 'admin@tianyi-forum.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye0bXd1YvHn8xFcqkKpXZLPdD1j/WxA1C', 'admin', 'active');

-- 插入示例板块
INSERT INTO boards (name, description, moderator_id, sort) VALUES 
('技术交流', '讨论编程、算法、技术话题', 1, 1),
('灌水乐园', '轻松闲聊，分享生活', 1, 2),
('意见反馈', '对论坛功能的建议和反馈', 1, 3);

-- 插入常见敏感词
INSERT INTO sensitive_words (word, level) VALUES 
('广告', '中'),
('色情', '高'),
('暴力', '高'),
('赌博', '高');
