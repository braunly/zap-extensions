package org.zaproxy.zap.extension.soap;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

public class SOAPActionSpoofingActiveScannerTestCase {

	private HttpMessage originalMsg = new HttpMessage();
	private HttpMessage modifiedMsg = new HttpMessage();
	
	@BeforeClass
	public static void setUpBeforeClass() {
		// XXX Does not work with Java 9+ because of used classes (e.g. javax.xml.soap.SOAPException).
		// Ref: https://github.com/zaproxy/zaproxy/issues/4037
		assumeTrue(SystemUtils.IS_JAVA_1_8);
	}

	@Before
	public void setUp() throws HttpMalformedHeaderException{
		/* Original. */
		Sample.setOriginalRequest(originalMsg);
		Sample.setOriginalResponse(originalMsg);
		/* Modified. */
		Sample.setOriginalRequest(modifiedMsg);
		Sample.setByeActionRequest(modifiedMsg);
		Sample.setByeResponse(modifiedMsg);
	}
	

	
	@Test
	public void scanResponseTest() throws Exception {
		SOAPActionSpoofingActiveScanner scanner = new SOAPActionSpoofingActiveScanner();
		
		/* Positive cases. */	
		int result = scanner.scanResponse(modifiedMsg, originalMsg);
		assertTrue(result == SOAPActionSpoofingActiveScanner.SOAPACTION_EXECUTED);
		
		Sample.setOriginalResponse(modifiedMsg);
		result = scanner.scanResponse(modifiedMsg, originalMsg);
		assertTrue(result == SOAPActionSpoofingActiveScanner.SOAPACTION_IGNORED);
		
		/* Negative cases. */
		result = scanner.scanResponse(new HttpMessage(), originalMsg);
		assertTrue(result == SOAPActionSpoofingActiveScanner.EMPTY_RESPONSE);
		
		Sample.setEmptyBodyResponse(modifiedMsg);
		result = scanner.scanResponse(modifiedMsg, originalMsg);
		assertTrue(result == SOAPActionSpoofingActiveScanner.EMPTY_RESPONSE);
		
		Sample.setInvalidFormatResponse(modifiedMsg);
		result = scanner.scanResponse(modifiedMsg, originalMsg);
		assertTrue(result == SOAPActionSpoofingActiveScanner.INVALID_FORMAT);
	}

}