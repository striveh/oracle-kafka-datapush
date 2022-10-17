                        kafka推数程序相关解释



系统间调用流程
 ![image](https://user-images.githubusercontent.com/3348645/196103616-a7831146-9db4-4447-9513-2ae643424906.png)


Kafka connect ： 是kafka系统中自带的一个服务框架，专门用于实现从其他数据源导入数据到kafka系统中、也可用于把kafka中的数据导出到其他系统；
 相关资料：http://kafka.apache.org/documentation.html#connect
https://docs.confluent.io/platform/current/connect/userguide.html

具体到数据导入导出的现实，需要根据源系统或者目标系统的情况；在相关生态社区都有一些常用的开源实现，
比如这里：https://docs.confluent.io/home/connect/kafka_connectors.html

FDS 中需要把数据从Oracle数据库同步到 kafka中，用到了kafka-connect-jdbc
https://docs.confluent.io/kafka-connect-jdbc/current/

为了便于使用者操作维护数据的同步，基于Kafka connect rest 接口封装了一个后台程序（push-data），同时在FDS可视化里有对应的界面来完成相关功能的调用；封装的目的是简化配置，标准化操作，屏蔽细节，当然这一切都是基于需求而定制，所以开发人源需要尽多的了解细节以便于开发出满足需求的的功能；该程序基于spring boot 开发，通过http请求与Kafka connect rest服务做交互；

开源实现已经满足了常规的需求，如果有特殊需求可以基于源码修改实现自己想要的效果
比如当前的kafka-connect-jdbc   只支持在新建的任务时设定其同步的开始时间，如果想在现有的任务上也能指定同步的开始时间，则修改其相关逻辑


 ![image](https://user-images.githubusercontent.com/3348645/196103864-493895d7-f5e8-4378-9ac0-14c39236bdbe.png)
 

这里只是举个例子，具体的需求怎么实现还需要综合考虑

以上已经可以完成数据同步的需求，但在数据处理细节上根据需求可能会用到以下服务

schema-registry ：是confluent公司开发开源的一个用于处理kafka数据流转序列化相关问题的应用，主要目的是优化数据传输，把数据定义与数据分开传输。
https://docs.confluent.io/platform/current/schema-registry/index.html
相关学习了解非官方中文连接：
https://www.ignite-service.cn/confluent
https://www.cnblogs.com/laoqing/p/11927958.html
https://blog.51cto.com/zero01/2498682



部署相关

1. 部署kafka-connect-jdbc
   官方编译好的包：https://www.confluent.io/hub/confluentinc/kafka-connect-jdbc
   官方源码：https://github.com/confluentinc/kafka-connect-jdbc
   基于源码做了上诉修改的源程序：kafka-connect-jdbc目录下
   在源码目录下 执行maven 打包命令：
mvn clean install -Dmaven.test.skip=true -f pom.xml
根据实际情况到kafka配置目录下修改connect-distributed.properties配置文件，修改下面配置项
 ![image](https://user-images.githubusercontent.com/3348645/196103750-52856b88-3f05-4e9b-afb1-46e418c75287.png)

拷贝target/components/packages/目录下的zip文件到plugin.path配置的目录下，并解压
执行./bin/connect-distributed.sh ./config/connect-distributed.properties 命令启动
Kafka connect reset 服务(先确保启动了kafka，并且在connect-distributed.properties文件里正确设置了服务地址)

2. 部署push-data应用
使用push-data/doc目录下的sql文件创建应用需要用到的表
修改application.yml配置文件（程序里根据环境定义了文件名称，实际可能并不需要这样：只需要一个application.yml文件即可，根据部署环境配置文件）
主要修改配置项
 
![image](https://user-images.githubusercontent.com/3348645/196103657-49d66918-52f8-4435-bc64-a561a3b6505e.png)


在源码目录下 执行maven 打包命令：
mvn clean install -Dmaven.test.skip=true -f pom.xml
把target目录下生成的DataPush.jar 放到部署服务器上运行即可



如果启用schema-registry服务，则需要在connect-distributed.properties文件里做相应的配置
 ![image](https://user-images.githubusercontent.com/3348645/196103691-c934b27f-92fb-4bc7-9d30-37cec4193797.png)

同时需要下载相应的converter包放到plugin.path目录下
https://www.confluent.io/hub/confluentinc/kafka-connect-avro-converter



