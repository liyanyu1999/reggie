#看到 P85

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
            停售功能未做
        

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
        身份验证、权限判断




#### 前台：
    
    登录功能：
        SMS 短信  验证码登录功能 ： 申请需要有相关资质
