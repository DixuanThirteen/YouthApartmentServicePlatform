CREATE TABLE Users(
    id INT PRIMARY KEY AUTO_INCREMENT,

    username VARCHAR(30) NOT NULL UNIQUE,   -- 用户名  r

    nickname VARCHAR(30) NOT NULL,          -- 昵称   r

    realname VARCHAR(100) NOT NULL,         -- 姓名   r

    password VARCHAR(30) NOT NULL,          -- 密码   r

    idnumber VARCHAR(18) NOT NULL UNIQUE CHECK (idnumber REGEXP '^\\d{17}[0-9Xx]$'),   -- 身份证号  r

    phone CHAR(11) NOT NULL UNIQUE CHECK (phone REGEXP '^[0-9]{11}$'),  -- 电话号码 r

    email VARCHAR(100) UNIQUE CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'), -- 电子邮箱  r
    
    born_date DATE NOT NULL,                -- 出生日期 r
    
    gender ENUM('male', 'female') NOT NULL, -- 性别   r
    
    avatar VARCHAR(255) NOT NULL DEFAULT '/images/DefaultAvatar.png',           -- 头像   r
    
    introduction TEXT,                      -- 个人简介
    
    region VARCHAR(100),                    -- 地区
    
    degree ENUM('below', 'college', 'bachelor', 'master', 'doctorate', 'above'),  -- 学位

    register_date DATE NOT NULL,                     -- 注册时间    r

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP     -- 更新时间
)