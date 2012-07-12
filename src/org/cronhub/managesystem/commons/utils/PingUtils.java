package org.cronhub.managesystem.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.cronhub.managesystem.commons.logger.AppLogger;
import org.cronhub.managesystem.commons.thrift.call.IExecuter;
import org.cronhub.managesystem.commons.thrift.call.RemoteCaller;
import org.cronhub.managesystem.commons.thrift.gen.ExecutorService.Client;

public class PingUtils {
	static final Map<String,IExecuter> executePool= new HashMap<String,IExecuter>();
	public static boolean ping(final String ip,final int port){
		String pool_key = String.format("%s:%s", ip,port);
		IExecuter pingExecuter = null;
		if(!executePool.containsKey(pool_key)){
		pingExecuter = new IExecuter(){
			@Override
			public Object execute(Client client) {
				boolean ping = false;
				try {
					ping =  client.ping();
				} catch (TException e) {
					AppLogger.errorLogger.error("cannot execute call:ping ip:"+ip+",port:"+port,e);
				}
				return ping;
			}
			};
			executePool.put(pool_key, pingExecuter);
		}else{
			pingExecuter = executePool.get(pool_key);
		}
		Boolean ret= false;
		try {
			ret = Boolean.valueOf(RemoteCaller.call(ip, port, pingExecuter,false).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	public static boolean pingRecall(final String ip,final int port){
		boolean canPingDaemon = ping(ip,port);
		return false;
	}
	public static void main(String[] args) {
		System.out.println(PingUtils.ping("192.168.50.185",2008) == true);
	}
}
