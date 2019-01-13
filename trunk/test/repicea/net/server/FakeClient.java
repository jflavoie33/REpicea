package repicea.net.server;

import java.net.InetSocketAddress;

import repicea.net.server.ServerConfiguration.Protocol;

public class FakeClient extends BasicClient {

	protected FakeClient(InetSocketAddress socketAddress, Protocol protocol, boolean isAJavaApplication) throws BasicClientException {
		super(socketAddress, 30, protocol, isAJavaApplication);
	}
	
	protected Object sendFakeRequest() throws BasicClientException {
		Double latitude = 46d;
		Double longitude = -71d;
		Double altitude = 300d;

		String request = latitude.toString().concat(longitude.toString()).concat(altitude.toString());
		Object result = processRequest(request);
		return result;
	}	
	
	protected Object createAnArrayList() throws BasicClientException {
		String request = "create;repicea.net.server.FakeArrayList";
		Object result = processRequest(request);
		return result;
	}
	
	protected Object sendTimeToServer() throws BasicClientException {
		long currentTime = System.currentTimeMillis();
		String request = "time".concat(((Long) currentTime).toString());
		Object result = processRequest(request);
		return result;
	}

	protected Object createAVectorWithArguments() throws BasicClientException {
		String request = "create;java.util.Vector;integer3";
		Object result = processRequest(request);
		return result;
	}

	protected Object addThisToArrayList(Object arrayList, String toBeAdded) throws BasicClientException {
		String request = "method;" + arrayList.toString().replace("JavaObject;repicea.net.server.FakeArrayList@", "java.objecthashcode") + ";add;" + toBeAdded;
		Object result = processRequest(request);
		return result;
	}

	protected Object testThisDoubleWrapper(Object arrayList) throws BasicClientException {
		String request = "method;" + arrayList.toString().replace("JavaObject;repicea.net.server.FakeArrayList@", "java.objecthashcode") +
				";processThisDouble;" + "numeric0";
		Object result = processRequest(request);
		return result;
	}

	protected Object createMultipleVectorWithArguments() throws BasicClientException {
		String request = "create;java.util.Vector;integer3,4,5";
		Object result = processRequest(request);
		return result;
	}
}
