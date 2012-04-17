package android.TextMessenger.model;

	public class ObjToObsever {
		private Object obj;
		private int type;
		
		public ObjToObsever(Object obj, int msgType) {
			this.obj = obj;
			type = msgType;
		}
		
		public Object getContainedData() {
			return obj;
		}

		
		public int getMessageType() {
			return type;
		}
		
	}
