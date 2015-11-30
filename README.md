基于Redis统一管理session数据<br/>
1.完全释放session相关的JVM内存； <br/>
2.session统一存储，当Web container宕机，session数据不再丢失； <br/>
3.遵循 Servlet 2.4 规范；  <br/>
4.前置HTTP负载均衡设备在进行请求转发时，无需支持会话粘性；  <br/>
5.不强依赖于特定 web container <br/>
6.不影响应用原有设计，无侵入性。  <br/>

技术:序列化使用kryo,代替jdk自带的序列化 <br/>
           包装HttpServletRequestWrapper <br/>
	配置文件如下 <br/>
	#redis服务器IP <br/>
	redisIp=10.19.40.52 <br/>
	#redis 端口 <br/>
	redisPort=6379 <br/>
	#session失效时间 <br/>
	sessionTimeoutSeconds=1800 <br/>
	#session与redis 超过时间同步 <br/>
	sessionIgnoreSeconds=300 <br/>
后期 <br/>
	redis集群 <br/>
	memcached实现 <br/>
	
	
	
