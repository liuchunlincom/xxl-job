- 改造内容
  
  1） 数据库适配改造
  
        新增postgres数据库支持，可通过参数（spring.datasource.db-type）控制切换mysql、postgres
  
  2） 支持nacos
  
        引入Springcloud Alibaba(2021.0.1.0),其对应引入客户端版本为1.4.0，可通过nacos原生方式支持nacos配置，也可以按照springcloud的方式配置


- 问题：
  
  1） 配置邮件告警后发送邮件出现问题
        
     问题内容：
     
         javamail could not connect to smtp host
         
     原因分析：
        
        jdk8默认使用IPV6
     
     解决方式：
     
        启动参数新增：-Djava.net.preferIPv4Stack=true
  