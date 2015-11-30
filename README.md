基于Redis统一管理session数据
1.完全释放session相关的JVM内存；
2.session统一存储，当Web container宕机，session数据不再丢失；
3.遵循 Servlet 2.4 规范； 
4.前置HTTP负载均衡设备在进行请求转发时，无需支持会话粘性； 
5.不强依赖于特定 web container
6.不影响应用原有设计，无侵入性。 

技术:序列化使用kryo,代替jdk自带的序列化
           包装HttpServletRequestWrapper
	配置文件如下
	#redis服务器IP
	redisIp=10.19.40.52
	#redis 端口
	redisPort=6379
	#session失效时间
	sessionTimeoutSeconds=1800
	#session与redis 超过时间同步
	sessionIgnoreSeconds=300
后期
	redis集群
	memcached实现
	
	
	
