drop table if exists `user`;
create table `user` (
    `id` int primary key auto_increment comment '用户id',
    `name` varchar(100) not null comment '用户名',
    `password` varchar(100) not null comment '用户密码',
    `age` int not null default 0 comment '用户年龄',
    `sex` enum('男', '女') default null comment '用户性别',
    `email` varchar(100) default null comment '用户邮箱',
    `phone` varchar(100) default null comment '用户手机号',
    `address` varchar(100) default null comment '用户地址',
    `create_time` datetime not null comment '用户创建时间',
    `is_active` tinyint(1) DEFAULT 1 COMMENT '是否有效(1=是；0=否)',
    unique key `idx_name` (`name`),
    unique key `idx_email` (`email`),
    unique key `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

insert into `user`(`id`, `name`, `password`, `create_time`) values(1, 'admin', 'admin', now());

drop table if exists `goods`;
create table `goods` (
    `id` int primary key auto_increment comment '商品id',
    `name` varchar(100) not null comment '商品名称',
    `price` decimal(10, 2) not null comment '商品价格',
    `stock` int not null comment '商品库存',
    `create_time` datetime not null comment '商品创建时间',
    `is_active` tinyint(1) DEFAULT 1 COMMENT '是否有效(1=是；0=否)',
    unique key `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品表';

insert into `goods`(`id`, `name`, `price`, `stock`, `create_time`) values(1, 'iphone16', 12999.00, 1000, now());

drop table if exists `goods_kill`;
create table `goods_kill` (
    `id` int primary key auto_increment comment '商品秒杀id',
    `goods_id` int not null comment '商品id',
    `stock` int not null comment '秒杀库存',
    `start_time` datetime not null comment '秒杀开始时间',
    `end_time` datetime not null comment '秒杀结束时间',
    `create_time` datetime not null comment '秒杀创建时间',
    `is_active` tinyint(1) DEFAULT 1 COMMENT '是否有效(1=是；0=否)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品秒杀表';

insert into `goods_kill`(`id`, `goods_id`, `stock`, `start_time`, `end_time`, `create_time`) values(1, 1, 100, '1970-01-01 00:00:00', '1970-01-01 00:00:10', now());

drop table if exists `goods_kill_order`;
create table `goods_kill_order` (
    `order_id` varchar(100) primary key comment '订单编号',
    `user_id` int not null comment '用户id',
    -- `goods_id` int not null comment '商品id',
    `kill_id` int not null comment '商品秒杀id',
    `status` tinyint(1) DEFAULT 0 COMMENT '秒杀结果: 0待付款 1已付款 2已取消'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀订单表';