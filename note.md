#**项目笔记**：

后台访问路径：http://localhost:8080/backend/page/login/login.html

前台访问路径：http://localhost:8080/front/page/login.html


==================

**## 功能：**

#### 后台：


[comment]: <> (//)

        登录功能

        员工：
            CRUD
        分类：
            CRUD
        菜品：
            CRUD
            
        

[comment]: <> (//)
        公告字段自动填充：
            ThreadLocal:线程内的局部变量
        异常抛出：
            全局异常抛出
            用户异常抛出

[comment]: <> (//)

        文件上传下载：
            文件上传：
                将本地文件上传到服务器上，可以供其他用户浏览下载
                
                必须采用post方式
                必须采用multipart格式上传
                使用input的file控件
            
            文件下载：
                将文件从服务器传输到本地的过程
    

    不足：
        身份验证、权限判断(可使用 spring security)


#### 前台：
    
    登录功能：
        SMS 短信  验证码登录功能 ： 申请需要有相关资质



还需实现的功能：
```textmate
~~用户退订~~

用户信息页


下单页修改地址
                
缓存:
    套餐缓存
    后台管理端缓存

前端页面图片替换
    favicon.ico
    logo.png
    
```

```text
Mysql主从复制  ==> 实现读写分离

master:
    server-id: 1
    lihaihui
    123456
    
slave:
    server-id: 2
    
```

```text
后台管理模块
前台用户模块
优惠卷模块
评论模块
骑手模块
```

    