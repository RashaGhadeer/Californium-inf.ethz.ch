package ch.inf.vs.californium.coap;

public class CoAP {
	
	/**
	 * CoAP defines four types of messages: Confirmable, Non-confirmable,
	 * Acknowledgement, Reset;
	 */
	public enum Type {
		CON(0), NCON(1), ACK(2), RST(3);
		
		public final int value;
		
		Type(int value) {
			this.value = value;
		}
		
		public static Type valueOf(int value) {
			switch (value) {
				case 0: return CON;
				case 1: return NCON;
				case 2: return ACK;
				case 3: return RST;
				default: throw new IllegalArgumentException("Unknown CoAP type "+value);
			}
		}
	}
	
	public enum Code {
		GET(1), POST(2), PUT(3), DELETE(4);
		
		public final int value;
		
		Code(int value) {
			this.value = value;
		}
		
		public static Code valueOf(int value) {
			switch (value) {
				case 1: return GET;
				case 2: return POST;
				case 3: return PUT;
				case 4: return DELETE;
				default: throw new IllegalArgumentException("Unknwon CoAP request code "+value);
			}
		}
	}
	
	
	public enum ResponseCode {
		// Success
		CREATED(65), DELETED(66), VALID(67), CHANGED(68), CONTENT(69),
		
		// Client error
		BAD_REQUEST(128), UNAUTHORIZED(129), BAD_OPTION(130), FORBIDDEN(131), NOT_FOUND(132),
		METHOD_NOT_ALLOWED(133), NOT_ACCEPTABLE(134), PRECONDITION_FAILED(140),
		REQUEST_ENTITIY_TOO_LARGE(141), UNSUPPORTED_CONTENT_FORMAT(143), 
		
		// Server error
		INTERNAL_SERVER_ERROR(160), NOT_IMPLEMENTED(161), BAD_GATEWAY(162),
		SERVICE_UNAVAILABLE(163), GATEWAY_TIMEOUT(164), PROXY_NOT_SUPPORTED(165);
		
		public final int value;
		
		private ResponseCode(int value) {
			this.value = value;
		}
		
		public static ResponseCode valueOf(int value) {
			switch (value) {
				case 65: return CREATED;
				case 66: return DELETED;
				case 67: return VALID;
				case 68: return CHANGED;
				case 69: return CONTENT;
				case 128: return BAD_REQUEST;
				case 129: return UNAUTHORIZED;
				case 130: return BAD_OPTION;
				case 131: return FORBIDDEN;
				case 132: return NOT_FOUND;
				case 133: return METHOD_NOT_ALLOWED;
				case 134: return NOT_ACCEPTABLE;
				case 140: return PRECONDITION_FAILED;
				case 141: return REQUEST_ENTITIY_TOO_LARGE;
				case 143: return UNSUPPORTED_CONTENT_FORMAT;
				case 160: return INTERNAL_SERVER_ERROR;
				case 161: return NOT_IMPLEMENTED;
				case 162: return BAD_GATEWAY;
				case 163: return SERVICE_UNAVAILABLE;
				case 164: return GATEWAY_TIMEOUT;
				case 165: return PROXY_NOT_SUPPORTED;
				default: throw new IllegalArgumentException("Unknown CoAP response code "+value);
			}
		}
		
	}

	public static class OptionRegistry {
		
		// draft-ietf-core-coap-14
		public static final int RESERVED_0 =      0;
		public static final int IF_MATCH =        1;
		public static final int URI_HOST =        3;
		public static final int ETAG =            4;
		public static final int IF_NONE_MATCH =   5;
		public static final int URI_PORT =        7;
		public static final int LOCATION_PATH =   8;
		public static final int URI_PATH =       11;
		public static final int CONTENT_TYPE =   12;
		public static final int MAX_AGE =        14;
		public static final int URI_QUERY =      15;
		public static final int ACCEPT =         16;
		public static final int LOCATION_QUERY = 20;
		public static final int PROXY_URI =      35;
		public static final int PROXY_SCHEME =   39;
		public static final int RESERVED_1 =    128;
		public static final int RESERVED_2 =    132;
		public static final int RESERVED_3 =    136;
		public static final int RESERVED_4 =    140;
	
		// draft-ietf-core-observe-08
		public static final int OBSERVE = 6;
	
		// draft-ietf-core-block-10
		public static final int BLOCK2 = 23;
		public static final int BLOCK1 = 27;
		public static final int SIZE =   28;
		
		public static class Default {
			public static final long MAX_AGE = 60L;
		}
	}
	
	public class MessageFormat {
		/** number of bits used for the encoding of the CoAP version field */
		public static final int VERSION_BITS     = 2;
		
		/** number of bits used for the encoding of the message type field */
		public static final int TYPE_BITS        = 2;
		
		/** number of bits used for the encoding of the token length field */
		public static final int TOKEN_LENGTH_BITS = 4;

		/** number of bits used for the encoding of the request method/response code field */
		public static final int CODE_BITS = 8;

		/** number of bits used for the encoding of the message ID */
		public static final int MESSAGE_ID_BITS = 16;

		/** number of bits used for the encoding of the option delta field */
		public static final int OPTION_DELTA_BITS = 4;
		
		/** number of bits used for the encoding of the option delta field */
		public static final int OPTION_LENGTH_BITS = 4;
		
		/** One byte which indicates indicates the end of options and the start of the payload. */
		public static final byte PAYLOAD_MARKER = (byte) 0xFF;
		
		/** CoAP version supported by this Californium version */
		public static final int VERSION = 1;
		
		public static final int EMPTY_CODE = 0;
		public static final int REQUEST_CODE_LOWER_BOUND = 1;
		public static final int REQUEST_CODE_UPPER_BOUNT = 31;
		public static final int RESPONSE_CODE_LOWER_BOUND = 64;
		public static final int RESPONSE_CODE_UPPER_BOUND = 191;
	}
}
