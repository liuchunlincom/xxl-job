## 改造内容
  
  1） postgres数据库适配支持
  
       驱动版本：42.2.5
       数据库切换方式：可通过参数（spring.datasource.db-type）控制切换mysql、postgres
  
  2） nacos适配支持
  
        适配方式：引入Springcloud Alibaba(2021.0.1.0)注册中心、配置中心相关依赖
        nacos客户端版本：1.4.2
        配置方式：可通过nacos原生方式支持nacos配置，也可以按照springcloud的方式配置（建议）


## QA：
  
  1） 配置邮件告警后发送邮件出现问题
        
     错误内容：
     
         org.springframework.mail.MailAuthenticationException: Authentication failed; nested exception is javax.mail.AuthenticationFailedException: [EOF]
         	at org.springframework.mail.javamail.JavaMailSenderImpl.doSend(JavaMailSenderImpl.java:440)
         	at org.springframework.mail.javamail.JavaMailSenderImpl.send(JavaMailSenderImpl.java:361)
         	at org.springframework.mail.javamail.JavaMailSenderImpl.send(JavaMailSenderImpl.java:356)
         	at com.xxl.job.admin.core.alarm.impl.EmailJobAlarm.doAlarm(EmailJobAlarm.java:74)
         	at com.xxl.job.admin.core.alarm.JobAlarmer.alarm(JobAlarmer.java:52)
         	at com.xxl.job.admin.core.thread.JobFailMonitorHelper$1.run(JobFailMonitorHelper.java:64)
         	at java.base/java.lang.Thread.run(Thread.java:831)
         Caused by: javax.mail.AuthenticationFailedException: [EOF]
         	at com.sun.mail.smtp.SMTPTransport$Authenticator.authenticate(SMTPTransport.java:947)
         	at com.sun.mail.smtp.SMTPTransport.authenticate(SMTPTransport.java:858)
         	at com.sun.mail.smtp.SMTPTransport.protocolConnect(SMTPTransport.java:762)
         	at javax.mail.Service.connect(Service.java:342)
         	at org.springframework.mail.javamail.JavaMailSenderImpl.connectTransport(JavaMailSenderImpl.java:518)
         	at org.springframework.mail.javamail.JavaMailSenderImpl.doSend(JavaMailSenderImpl.java:437)
         	... 6 common frames omitted
         
     原因分析：
        
        确认密码等配置无误后，查阅资料发现jdk8默认使用IPV6
     
     解决方式：
     
        启动参数新增：-Djava.net.preferIPv4Stack=true
  