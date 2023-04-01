# portforward
A port forward tool By Java

# Introduction
A minimalist port forwarding tool made with java


# Usage
```bash
java -jar PortForward.jar -l 0.0.0.0 -p 1234 -t 192.168.1.111 -p 5678

你当前的网络为A，目标网络为C，你有自己的VPS网络为B。他们之间的网络关系如下：
A与C网络不通
A<====>B (A，B网络互通)
B<====>C (B，C网络互通)

你将PortForward.jar作为服务部署在网络B，使用PortForward.jar提供的端口转发能力访问此前无法访问的C网络。
```
The above command means forwarding all requests from local port 1234 to port 5678 of target ip (192.168.1.111).

 * Suppose the following scenario: Your network is not connected to the Target Network
   > That is: You[Network=172.16.18.8] <==Network not working==> Target Network[192.168.1.111]

 * But your network and Server [portforward, Network=200.200.201.2] are connected, and Server and Target Network [192.168.1.111] are connected 
   > That is: You[Network=172.16.18.8] <=====> Server[portforward, Network=200.200.201.2] <=====> Target Network[192.168.1.111]
 
 * Then you can use this tool to use the Server as a springboard to access the unreachable network (Target Network[192.168.1.111])

# todo 
 * Traffic Encryption