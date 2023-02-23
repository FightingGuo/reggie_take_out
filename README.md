# reggie_take_out

开发环境：IDEA2020.3、jdk1.8、tomcat9（springboot内置）、mysql8、redis6


管理员模块、用户模块，管理员模块包含员工管理、菜品管理和套餐管理，都是简单的增删改。用户 模块包括有腾讯云的短信登录、购物车模块、订单模块。部署了两台服务器一台部署 nginx，通过 nginx 反向代理部署了管理员模块的前端资源，Mysql 主从库的主库，并且通过（ShardingJDBC）实现读写分离。另一台部 署项目项目和 redis 其中项目通过 maven 管理打包，在启动项目时，用 git 从远程库实时拉取最新代码。用 redis 缓存短信验证码并设置过期时间，用 springCache 缓存菜品和套餐信息。


部署环境说明
docker容器化部署:
宿主机（centos7）:192.168.150.3
配置国内阿里云容器镜像
拉取redis、mysql、nginx镜像
根据mysql镜像run两个mysql容器实例(注意映射容器内不同端口), 配置mysql的主从复制
1.分别对两个容器挂载两个容器卷到宿主机的两个不同目录 分别存放mysql的配置文件(conf)、日志文件(log)、数据(data) 
2.配置两个mysqld.cnf配置文件
3.在Master库创建数据同步用户,授予用户 slave REPLICATION SLAVE权限和REPLICATION CLIENT权限，用于在主从库之间同步数据 CREATE USER 'slave'@'%' IDENTIFIED BY '123456';
4.进入Master库  show master status 记录File和Position字段的值
5.进入Slave库 执行change master to master_host='172.18.0.3', master_user='slave', master_password='123456', master_port=3306, master_log_file='master-bin.000001', master_log_pos=617, master_connect_retry=30;
6.start slave;

拉取nginx镜像:部署前端项目、配置反向代理

拉取redis:同样也挂载容器卷到宿主机 指定宿主机上的配置文件启动redis

















