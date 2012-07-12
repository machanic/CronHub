package org.cronhub.managesystem.commons.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

public abstract class IPUtils {  
          
          
        public static Collection<InetAddress> getAllHostAddress() {  
            try {  
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();  
                Collection<InetAddress> addresses = new ArrayList<InetAddress>();  
                  
                while (networkInterfaces.hasMoreElements()) {  
                    NetworkInterface networkInterface = networkInterfaces.nextElement();  
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();  
                    while (inetAddresses.hasMoreElements()) {  
                        InetAddress inetAddress = inetAddresses.nextElement();  
                        addresses.add(inetAddress);  
                    }  
                }  
                  
                return addresses;  
            } catch (SocketException e) {  
                throw new RuntimeException(e.getMessage(), e);  
            }  
        }  
          
        public static Collection<String> getAllNoLoopbackAddresses() {  
            Collection<String> noLoopbackAddresses = new ArrayList<String>();  
            Collection<InetAddress> allInetAddresses = getAllHostAddress();  
              
            for (InetAddress address : allInetAddresses) {  
                if (!address.isLoopbackAddress()) {  
                    noLoopbackAddresses.add(address.getHostAddress());  
                }  
            }  
              
            return noLoopbackAddresses;  
        }  
          
        public static String getFirstNoLoopbackAddress() {  
            Collection<String> allNoLoopbackAddresses = getAllNoLoopbackAddresses();  
            return allNoLoopbackAddresses.iterator().next();  
        }  
        public static void main(String[] args) {
			for(InetAddress addr : getAllHostAddress() ){
				System.out.println(IPUtils.getAllNoLoopbackAddresses());
			}
		}
    }     