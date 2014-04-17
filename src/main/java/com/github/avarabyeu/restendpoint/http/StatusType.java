package com.github.avarabyeu.restendpoint.http;

/**
 * HTTP Response Status Type
 * 
 * @author Andrei Varabyeu
 * 
 */
public enum StatusType {

	/** Informational Response */
	INFORMATIONAL(1),
	/** Successful response */
	SUCCESSFUL(2),
	/** Redirection response */
	REDIRECTION(3),
	/** Client Error Response */
	CLIENT_ERROR(4),
	/** Server Error Response */
	SERVER_ERROR(5);

	/** First Symbol of HTTP response code */
	private final int value;

	private StatusType(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	/**
	 * Obrains {@link StatusType} from HTTP status code. If there are no status
	 * defined throws {@link java.lang.IllegalArgumentException}
	 * 
	 * @param status
	 * @return
	 */
	public static StatusType valueOf(int status) {
		int seriesCode = status / 100;
		for (StatusType series : values()) {
			if (series.value == seriesCode) {
				return series;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + status + "]");
	}

}
