# jKool Streaming RESTFul API

###Why jKool Rest Clients?
This jKool Java Rest Client contains Java helper classes that will help you to get up and running very quickly with the jKool Streaming API. You will need a streaming  “token” in order to stream. This token is associated with a repository assigned to you when you sign-up for jKool. The token is passed in the request header. Other language bindings can be generated with the Swagger Code Generator using the Swagger yaml file found it the "swagger" folder.  Please be aware the the Swagger yaml file is documenting every field that can be passed via Resful Services and the when using the helper classes, many of these fields are automatically populated for you.

###jKool Streaming Concepts and Documentation
You can find very comprehensive documentation on jKool Data Types and Concepts in our jKool Streaming Guide found here: https://www.jkoolcloud.com/download/jkool-model.pdf. But basically, there are four types of data that can be streamed into jKool. They are:
* Events: Represents a basic time series element containing time, message, severity and other fields associated with event.
* Activities: Represents a group of events and other activities (e.g. transactions).
* Snapshots: categorized metrics (name, value, type) at a "point in time". Snapshots can be associated with events, activities.
* Properties: simple metrics (name, value pairs). Properties can be associated with events, activities. 
* In addition to the Streaming Guide, this Git repository contains a Swagger yaml file. Open this file in a Swagger Editor and you will have detailed documentation of each field that comprises the above mentioned data.

###Streaming Examples
To use this sample code please do the following:
* Run mvn install on the project. This will generate a jar file (found in the target directory).
* Import this jar file into your own project in which you wish to stream to jKool. 
* Please see the sample classes and run them in order to get a good understanding on how to use the helper code. You will be doing the following:
* Instantiate the `JKStream` class. You will need to pass it the access token you received when you signed up for jKool. This token will grant you access to stream and also ensure that the data goes to the proper repository.
```java
		JKStream jkSend = new JKStream("yourtoken");
```
* Instantiate the object you wish to stream. Then populate all of the fields you wish to stream. For example:
```java
		Event event = new Event("Casablanca");
		event.setAppl("WebOrders").setServer(InetAddress.getLocalHost().getHostName())
		        .setNetAddr(InetAddress.getLocalHost().getHostAddress()).setDataCenter("DCNY")
		        .setElapsedTimeUsec(TimeUnit.HOURS.toMicros(2)).setLocation("New York, NY")
		        .setMsgText("Casablanca is playing.");

```
Please note that this example code depicts streaming in real-time. Therefore the start date of the event will default to the current date/time and the end date will default to the start date plus the elapsed time. You can however control start/end dates. For example:
```java
		event.setTime(System.currentTimeMillis()).setElapsedTimeUsec(TimeUnit.HOURS.toMicros(2));
```

* Finally, invoke the post method on the `jKoolStream` object, passing it the object you wish to stream. For example:

```java
		JKStream jkSend = new JKStream("yourtoken");
		Event event = new Event("Casablanca");
		event.setAppl("WebOrders").setServer(InetAddress.getLocalHost().getHostName())
			.setNetAddr(InetAddress.getLocalHost().getHostAddress()).setDataCenter("DCNY")
			.setElapsedTimeUsec(TimeUnit.HOURS.toMicros(2)).setLocation("New York, NY")
			.setMsgText("Casablanca is playing.");
		Response response = jkSend.post(event);
		response.close();
```
The Rest Client will properly format the entity into JSON format and stream it to jKool over default `https` protocol.

That's it!! Any problems or concerns, please email us at (`support at jkoolcloud.com`).

###Running jKool Queries
Below is an example of running a JKQL query against a repository associated with a specified access token `yourtoken`:

```java
		JKQuery jkQuery = new JKQuery("yourtoken");
		Response response = jkQuery.call("get number of events for today");
		Map<String, Object> jsonResponse = response.readEntity(Map.class);
		response.close();
```
All returned JKQL responses are JSON.
Developers can also invoke JKQL queries asynchronously using callbacks. See example below:
```java
		// setup jKool WebSocket connection and connect
		JKQueryAsync jkQuery = new JKQueryAsync("yourtoken");
		jkQueryAsync.setConnectionHandler(new MyConnectionHandler());
		
		// setup a default response handler for responses not associated with any specific query
		jkQueryAsync.setDefaultResponseHandler(new MyJKQueryCallback());
		jkQueryAsync.connect(); // connect stream with WebSocket interface
		
		// run query in async mode with a callback
		JKQueryHandle qhandle = jkQueryAsync.callAsync("get events", new MyJKQueryCallback());
		...
		jkQueryAsync.close(); // close connection
```
`MyJKQueryCallback` is called when a result from the query is received. Responses not associated with any specific query are handled by a default response handler specified by `jkQueryAsync.setDefaultResponseHandler(...)` call.
```java
public class MyJKQueryCallback implements JKQueryCallback {
	@Override
	public void handle(JKQueryHandle qhandle, JsonObject response, Throwable ex) {
		System.out.println("response: handle=" + qhandle + ", response=" + response);
		if (ex != null) {
			System.out.println("error: handle=" + qhandle + ", error=" + ex);
		}	
	}
}
```
Connection handler can be used to intercept and handle WebSocket connection events such as open, close, error:
```java
public class MyConnectionHandler implements JKConnectionHandler {
	@Override
	public void error(JKQueryAsync async, Throwable ex) {
		System.err.println("error:: " + async + ", error=" + ex);
		ex.printStackTrace();
	}

	@Override
	public void close(JKQueryAsync async, CloseReason reason) {
		System.out.println("close: " + async + ", reason=" + reason);
	}

	@Override
	public void open(JKQueryAsync async) {
		System.out.println("open: " + async);
	}
}

```
`jkQueryAsync.callAsync()` return a query handler (instance of `JKQueryHandle`) which can be used later to cancel subscriptions.
Cancelling active query subscriptions will attempt to stop any streaming traffic associated with the specific subscription.
Cancellation is also issued asynchronously and any responses that are still in transit will be routed to the default response handler specified by `setDefaultResponseHandler()` call.
```java
		// run query in async mode with a callback
		JKQueryHandle qhandle = jkQueryAsync.callAsync("get number of events for today", new MyJKQueryCallback());
		...
		// attempt to cancel subscription to the query results
		jkQueryAsync.cancelAsync(qhandle);
```

###Important note
This sample code showcases some basic examples of using jKool Rest API. jKool can handle very complex application interactions. For example, it is built with the ability to correlate events and track transactions across multiple applications. This can be used for complex tracking and analytics.

###Streaming with Curl
Data can also be streamed into jKool using Curl. Below is an example:

```java
curl -i -H "Content-Type:application/json" -H "token:YOURTOKEN" -X POST https://data.jkoolcloud.com/JESL/event -d '{"operation":"streamingwithcurl","type":"EVENT","start-time-usec":1457524800000000,"end-time-usec":1457524800000000,"msg-text":"Example Curl Streaming","source-fqn":"APPL=TestingCurl#SERVER=CurlServer100#NETADDR=11.0.0.2#DATACENTER=DC1#GEOADDR=52.52437,13.41053"}'
```

###Streaming with Python
Data can also be streamed into jKool using Python. To do so, you will need to use the Python "Request". Details on the Python Request can be found here - http://docs.python-requests.org/en/latest/user/quickstart/. Below is an example of using it to stream into jKool:

```java
import requests
headers = {'token': 'YOURTOKEN'}
payload={'operation':'streamingwithpython','type':'EVENT','start-time-usec':1457524800000000,'end-time-usec':1457524800000000,'msg-text':'Example Python Streaming','source-fqn':'APPL=TestingCurl#SERVER=CurlServer100#NETADDR=11.0.0.2#DATACENTER=DC1#GEOADDR=52.52437,13.41053'}
r = requests.post('https://data.jkoolcloud.com/JESL/event', headers=headers, json=payload)
```
###Query jKool using Curl

Rest can be used to retrieve data out of jKool. To do this, make use of the jKool Query Language (JKQL). Please see JKQL Documentation here http://www.jkoolcloud.com/download/jKQL%20User%20Guide.pdf. To use JKQL via Restful Services, pass your repository and repository credentials (username and password) in the request header. Then issue the GET request via the following URL https://jkool.jkoolcloud.com/jKool/jkql passing the JKQL in a 'query' parameter. For instance, to get all activities in your repository, do the following in curl: 

```java
curl -i -H "Content-Type:application/json" -H "username:<username>" -H "password:<password>" -H "repositoryId:<repository identifier>" -X GET https://jkool.jkoolcloud.com/jKool/jkql?query=get%20activities
```

