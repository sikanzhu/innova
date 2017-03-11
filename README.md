#What is Innova  
Innova is a game server framework.  
It is designed to run a endless world in a huge server cluster.  
Benefit to its unique Area of Interest algorithm, developers can focus on logic, and the framework will do the dirty work, including data transferring, updating and remote process call.  
Innova abstracts the virtual world with static objects and actors, then logic programmers can focus on logistic structure and world localization. While physical structure of servers and localization can be handled by server architects.  
  
========  
#Why a new framework?  
## A good idea make things simple.  
There are many frameworks that enable game developer to make wonderful game rapidly.  
But if you read their features, they always talk about load balance, area of interest, scale-ability, hot reload, fault tolerance and so on.  
We can not deny that these features are very very important. But before the well-known features, why shouldn't we take a look at the architecture.  
A better architecture can achieve these feature much easier, and can be much more convenient for developers to build better games.  

## "Standing on the shoulders of giants."  
Thanks to C/C++, Java, Redis, Jedis, libevent, Google's Protocol Buffer, Apache Foundation, SQLite and many other open source projects, we could have a chance to build a fast and reliable new framework.  
Thanks to Pomelo, Skynet, NoahFrame, KDEngine and many other framework or engines, these fabulous works inspired me a lot and shared a lot of priceless experience.  
Though I'm not an expert of any work above, without the works, I can not come up an idea to create and share Innova.  
  
========  
#Features of Innova <TODO Details should be described in the future>  
* Long Interval Tick and Time Magic.  
Short interval tick limits the scale-ability.  
* State Oriented based on Redis.  
Then the bottleneck can be limited in Redis servers. The best way to improve performance is improving the Redis Servers.  
Utility of Redis and No Overlay of Bounds. Versus BigWorld's Ghost Entity.  
Outdated state data will be ignored. This can save some bandwidth and processing.  
* Unique 5D Area of Interest.  
3D location.  
1D continuous time.  
1D dungeon(instance).  
* General server cluster architecture, then most work is game logic.  
* Asynchronous Operation.  
Every other Agent( a ghost that represents Actor in other Update Server or client ) is "const"(constant), can not be modified directly.  
* Separated Broadcasting and Logic (Pomelo has a different implementation).  
* Deferred Security Logic.  
* Cross platform and easy to deploy for end-user.  
Run on any computer or cluster.  
Suitable for both LAN game or MMO.  
+ Scale-ability and Distribution.  
+ Load balance, Dynamic Deploy and Region.  
+ Redundant and Fault Tolerance.  
+ Hot Reload.  
+ Long time connection.  
+ Circular Buffer.  
+ Interface servers.  
- No reliable broadcast, Where possible, do communication in batches(BigWorld).  
- Server Latency and Priority Queue.  
"*" means this feature is designed and unique.  
"+" means this feature is designed, but other frameworks also have this feature.  
"-" means this feature is not designed.  
  
========  
#Building Innova  
Innova depends on some third-party open source projects. Compiled packages are included in the "thirdparty" folder.  
Innova server is written in Java, and developed with Eclipse.  
Innova Demo is available with Unity 5.6.0b4, and written in C#. <TODO I will make a Unreal Engine 4 Demo in the future.>  
Currently Innova is developed in Windows. While it is implemented in Java and C#, it is very easy to integrate Innova to any other operating system.  
  
To build the server, please use Eclipse to open projects in "server" and "demo/server".  
To build the Innova Demo, please use Unity to open the project in "demo/unity3d/InnovaDemo".  
  
========  
#Running Innova  
Before running, please run "bin/innova_demo_server/create_database.bat" to initialize database.  
Then start Redis by "thirdparty/Redis-x64-3.2.100/start.bat".  
Start server in Eclipse, project "innova_demo_server".  
At last, play with Unity.  
  
========  
#Innova internals  
.\bin..........................compiled Innova Server. The server can also be started by "bin/innova_demo_server/start_innova.bat".  
.\common...................common files used by server and client.  
.\common\protobuf.......protobuf files.  
.\demo.......................demo files.  
.\demo\common...........common files used by demo server and client.  
.\demo\server.............demo server project.  
.\demo\unity3d............demo client project of Unity3D.  
.\doc.........................documents.<TODO need to be organized>  
.\resource..................resources, most of these files are used by server.  
.\server.....................Innova server framework. <TODO I will make it a configurable process in the future.>  
.\thirdparty................third-party dependency.  
  
Database tables of Innova  
<TODO to be explained>  
  
Redis data structures of Innova  
<TODO to be explained>  
  
========  
#Innova Architecture  
<img src="https://github.com/sikanzhu/innova/blob/SikanZhu/doc/innova/A4a%20Architecture%20A.png">
<img src="https://github.com/sikanzhu/innova/blob/SikanZhu/doc/innova/A4b%20Architecture%20B.png">
<img src="https://github.com/sikanzhu/innova/blob/SikanZhu/doc/innova/A4c%20Architecture%20C.png">

========  
#License  
The Innova project is currently available under the 3-Clause BSD License.  