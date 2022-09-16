# reggie_take_out

开发环境：IDEA2020.3、jdk1.8、tomcat9（springboot内置）、mysql8、redis6


管理员模块、用户模块，管理员模块包含员工管理、菜品管理和套餐管理，都是简单的增删改。用户 模块包括有腾讯云的短信登录、购物车模块、订单模块。部署了两台服务器一台部署 nginx，通过 nginx 反向代理部署了管理员模块的前端资源，Mysql 主从库的主库，并且通过（ShardingJDBC）实现读写分离。另一台部 署项目项目和 redis 其中项目通过 maven 管理打包，在启动项目时，用 git 从远程库实时拉取最新代码。用 redis 缓存短信验证码并设置过期时间，用 springCache 缓存菜品和套餐信息。


部署环境说明
服务器:
●192.168.150.3 (服务器A)
Nginx:部署前端项目、配置反向代理
Mysql:主从复制结构中的主库

●192.168.150.4 (服务器B)
jdk:运行Java项目
git:版本控制工具
maven:项目构建工具
jar: Spring Boot项目打成jar包基于内置Tomcat运行
Redis:缓存中间件

















