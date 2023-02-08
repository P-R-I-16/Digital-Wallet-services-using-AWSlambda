package model;

public class RegisterResponce {
	 
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RegisterResponce(String message) {
		super();
		this.message = message;
	}

	public RegisterResponce() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "RegisterResponce [message=" + message + "]";
	}
	  
}
