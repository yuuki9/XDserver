package com.egis.xdserver.util;

/**
 * @author 강민아
 * @date 2022. 6. 29.
 * 사용자정의 코드
 */
public enum CODE {

	CAN_NOT_MAKE_PATH("can't_make_path"),
	FAILD_CHECK_ACCOUNT("Faild, can't delete. check account."),
	FAILD_CAN_NOT_DELETE("Faild, can't delete"),
	FAILD_CAN_NOT_EXIST("Faild, not exist"),
	SUCCESS("Success"),
	UPDATE_FILE_DAT("OK : UPDATE_FILE_DAT"),
	CAN_NOT_UPDATE_FILE("NG : CAN_NOT_UPDATE_FILE"),
	NOT_EXIST_LAYER("NG : NOT_EXIST_LAYER"),
	ERROR_PARAM("NG : ERROR_PARAM"),
	NOT_DIRECTORY("NOT_DIRECTORY"),
	NOT_DATA_LENGTH("NOT_DATA_LENGTH")
	;

	private final String message;
	
	CODE(String message) {
		this.message = message;
	}
	
	public String getMessage() { return message; }
	
}
