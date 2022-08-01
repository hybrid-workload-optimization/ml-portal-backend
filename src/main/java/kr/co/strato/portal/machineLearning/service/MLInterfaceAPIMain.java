package kr.co.strato.portal.machineLearning.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import kr.co.strato.global.util.Base64Util;

public class MLInterfaceAPIMain {

	public static void main(String[] args) {
		String yaml = "apiVersion: batch/v1\r\n"
				+ "kind: Job\r\n"
				+ "metadata:\r\n"
				+ "  name: pi1\r\n"
				+ "spec:\r\n"
				+ "  template:\r\n"
				+ "    spec:\r\n"
				+ "      containers:\r\n"
				+ "      - name: pi\r\n"
				+ "        image: perl\r\n"
				+ "        command: [\"perl\",  \"-Mbignum=bpi\", \"-wle\", \"print bpi(2000)\"]\r\n"
				+ "      restartPolicy: Never\r\n"
				+ "  backoffLimit: 4\r\n"
				+ "---\r\n"
				+ "apiVersion: batch/v1\r\n"
				+ "kind: Job\r\n"
				+ "metadata:\r\n"
				+ "  name: pi2\r\n"
				+ "spec:\r\n"
				+ "  template:\r\n"
				+ "    spec:\r\n"
				+ "      containers:\r\n"
				+ "      - name: pi\r\n"
				+ "        image: perl\r\n"
				+ "        command: [\"perl\",  \"-Mbignum=bpi\", \"-wle\", \"print bpi(2000)\"]\r\n"
				+ "      restartPolicy: Never\r\n"
				+ "  backoffLimit: 4";
		
		String encoding = Base64Util.encode(yaml);
		System.out.println(encoding);
		
		
		/*
		File f = new File("D:\\paas-portal biz\\portal-backend\\src\\main\\resources\\addons\\monitoring-1.23.1\\metrics-server.yaml");
		
		Yaml yaml = new Yaml();
		InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, String> resMap = new HashMap<>();
		
		for(Object object : yaml.loadAll(is)) {
			if(object instanceof Map) {
				Map map = (Map) object;
				String kind = null;
				if(map.get("kind") != null) {
					kind = (String)map.get("kind");
				}
				
				String output = yaml.dump(map);
			    System.out.println(output);
			    
			    System.out.println(map.get("kind"));
			    System.out.println("------------------------------------------");
			} else {
				System.out.println("No Map...!!!");
			}
		}
		*/
		
	}
}
