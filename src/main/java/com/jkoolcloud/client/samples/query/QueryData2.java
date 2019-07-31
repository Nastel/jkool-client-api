/*
 * Copyright 2014-2019 JKOOL, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jkoolcloud.client.samples.query;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkoolcloud.client.api.service.JKQuery;
import com.jkoolcloud.client.api.utils.JKCmdOptions;

/**************************************************************************************************************************
 * This example demonstrates how to retrieve data from jKool via JKQL using {@code jKoolQuery.call()}
 ***********************************************************************************************************************/

public class QueryData2 {
	public static void main(String[] args) throws ProcessingException {
		try {
			Properties props = new Properties();
			props.setProperty(JKCmdOptions.PROP_URI, JKQuery.JKOOL_QUERY_URL);
			JKCmdOptions options = new JKCmdOptions(QueryData2.class, args, props);
			if (options.usage != null) {
				System.out.println(options.usage);
				System.exit(-1);
			}
			options.print();
			JKQuery jkQuery = new JKQuery(options.token);
			Response res = jkQuery.call(options.query);
			
			int status = res.getStatus();
		    String json = res.readEntity(String.class);
		    if (status == 200) {
		    	System.out.println(String.format("Status: %d\nResponse:\n%s", status, formatJson(json)));
		    } else {
		    	System.out.println(String.format("Status: %d %s", status, json));		    	
		    }
			res.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private static String formatJson(String input) throws IOException {
	      ObjectMapper mapper = new ObjectMapper();
	      Object json = mapper.readValue(input, Object.class);
	      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);	
	}
}
