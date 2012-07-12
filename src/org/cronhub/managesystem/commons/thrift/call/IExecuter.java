package org.cronhub.managesystem.commons.thrift.call;

import org.apache.thrift.TException;
import org.cronhub.managesystem.commons.thrift.gen.ExecutorService;


public interface IExecuter {
	/**
	 * 执行器调用client的各种方法去远程执行，thrift的"client执行"生成的代码默认会抛出org.apache.thrift.TException
	 * @param client org.cronhub.managesystem.commons.thrift.gen.ExecutorService.Client执行器
	 * @return 执行client的方法的返回结果
	 * @throws Exception 抛出的org.apache.thrift.TException，一般为执行时网络通信中断
	 */
	public Object execute(ExecutorService.Client client) throws Exception;
}
