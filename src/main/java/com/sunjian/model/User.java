package com.sunjian.model;

import lombok.Data;

/**
 * 存储用户信息
 * @author sunjian
 *
 */
@Data
public class User {

	private int id;
	private String userName;
	private String password;
	private String age;
	private String sex;
	private String permission;
	private String isDelete;
	
}
