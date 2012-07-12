package org.cronhub.managesystem.commons.thrift.call;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.thrift.gen.ExecutorService;

/**
 * 远程执行者类，供远程执行方法，并返回执行结果，有一个静态方法call，最终关闭远程网络连接
 * @author dd
 *
 */
public class RemoteCaller {
	/***
	 * 这个静态方法是远程调用daemon端的方法，需要传入ip和port，以及一个抽象的IExecuter接口,这个接口中有个传入ExecutorService.Client参数的方法,最后关闭远程连接。
	 * @param ip 远程daemon端的ip
	 * @param port 远程daemon端的port端口
	 * @param executer 抽象的IExecuter接口,这个接口中有个传入ExecutorService.Client参数的方法，最终ExecutorService.Client可以远程调用的各种thrift"生成"的方法
	 * @param defaultReturn 如果执行时失败（因为通信原因等),就返回一个默认的返回值由此参数定义
	 * @return 如果执行成功,返回执行client的最终调用远程方法的返回结果
	 * @throws Exception 如果执行时失败（因为通信原因等)抛出的错误，最终是thrift的"client执行"生成的代码默认抛出的org.apache.thrift.TException
	 */
	public static Object call(String ip,int port,IExecuter executer,Object defaultReturn) throws Exception{
		TTransport transport  = null;
		Object ret = defaultReturn;
		
		try {
	        transport = new TSocket(ip, port);
	        
	        TProtocol protocol = new TBinaryProtocol(transport);
	        ExecutorService.Client client = new ExecutorService.Client(protocol);
	        transport.open();
	        ret = executer.execute(client);
       }catch(Exception e) {
    	   //AppLogger.errorLogger.error(String.format("ip:%s,port:%s cannot ping normally when execute:'%s'!",ip,port,executer.getName()),e);
    	   throw e;
       }finally{
    	   if(null!=transport){
    	   transport.close();
    	   }
       }
       return ret;
	}
}
