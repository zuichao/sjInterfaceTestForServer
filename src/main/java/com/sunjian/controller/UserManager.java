package com.sunjian.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sunjian.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * swagger访问地址：http://localhost:8888/swagger-ui.html
 * @author sunjian
 *
 */

@Log4j
@RestController
@Api(value="v1",description="xxxxx系统")
@RequestMapping("v1")
public class UserManager {
	//首先获取一个执行sql语句的对象
    @Autowired
    private SqlSessionTemplate template;

    @ApiOperation(value = "登陆接口",httpMethod = "POST")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Boolean login(HttpServletResponse response, @RequestBody User user){
        int i  = template.selectOne("login",user);
        Cookie cookie = new Cookie("login","true");
        response.addCookie(cookie);
        log.info("查看到的结果是"+i);
        if(i==1){
            return true;
        }

        return false;
    }
    
    @ApiOperation(value="获取单条用户信息",httpMethod="GET")
    @RequestMapping(value="/getOneUserInfo",method=RequestMethod.GET)
    public User getOneUserInfo(HttpServletRequest request,@RequestParam Integer id){
    	//声明一个用户对象
    	User user;
    	boolean x = verifyCookies(request);
    	if (x) {			
    		//获取当前用户表中的用户数
    		int i = template.selectOne("userCount");
    		System.out.println("用户表中的条数："+i);
    		//如果存在用户，获取传入的第几条的用户信息
    		if (i>0 && id <= i) {
    				user = template.selectOne("getOneUserInfo",id);
    		}else {
    			user = null;
    		}
		}else {
			user = null;
		}
    	return user;
    }
    
    @ApiOperation(value = "添加用户接口",httpMethod = "POST")
    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public boolean addUser(HttpServletRequest request,@RequestBody User user){
    	boolean x = verifyCookies(request);
        int result = 0;
        if(x){
            result = template.insert("addUser",user);
        }
        if(result>0){
            log.info("添加用户的数量是:"+result);
            return true;
        }
        return false;
    }

    @ApiOperation(value = "获取用户(列表)信息接口",httpMethod = "POST")
    @RequestMapping(value = "/getUserInfo",method = RequestMethod.POST)
    public List<User> getUserInfo(HttpServletRequest request,@RequestBody User user){
    	boolean x = verifyCookies(request);
        if(x){
            List<User> users = template.selectList("getUserInfo",user);
            log.info("getUserInfo获取到的用户数量是" +users.size());
            return users;
        }else {
            return null;
        }
    }


    @ApiOperation(value = "更新/删除用户接口",httpMethod = "POST")
    @RequestMapping(value = "/updateUserInfo",method = RequestMethod.POST)
    public int updateUser(HttpServletRequest request,@RequestBody User user){
    	boolean x = verifyCookies(request);
        int i = 0;
        if(x) {
            i = template.update("updateUserInfo", user);
        }
        log.info("更新数据的条目数为:" + i);
        return i;

    }
    
    /**
     * 验证请求时携带的cookies信息是否正确
     * 
     * @param request
     * @return
     */
    private Boolean verifyCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(Objects.isNull(cookies)){
            log.info("cookies为空");
            return false;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("login") &&
                    cookie.getValue().equals("true")){
                log.info("cookies验证通过");
                return true;
            }
        }
        return false;
    }

}
