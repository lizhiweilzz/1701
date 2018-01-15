package cn.smbms.controller;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;

@Controller
@RequestMapping("/user")
public class UserController{
	private Logger logger = Logger.getLogger(UserController.class);
	
	@Resource
	private UserService userService;
	
	@RequestMapping(value="/login.html")
	public String login(){
		logger.debug("UserController welcome SMBMS==================");
		return "login";
	}
	
	@RequestMapping(value="/dologin.html",method=RequestMethod.POST)
	public String doLogin(@RequestParam String userCode,@RequestParam String userPassword,HttpServletRequest request,HttpSession session){
		logger.debug("doLogin====================================");
		//调用service方法，进行用户匹配
		User user = userService.login(userCode,userPassword);
		if(null != user){//登录成功
			//放入session
			session.setAttribute(Constants.USER_SESSION, user);
			//页面跳转（frame.jsp）
			return "redirect:/user/main.html";
			//response.sendRedirect("jsp/frame.jsp");
		}else{
			//页面跳转（login.jsp）带出提示信息--转发
			request.setAttribute("error", "用户名或密码不正确");
			return "login";
		}
	}
	@RequestMapping(value="/main.html")
	public String main(HttpSession session){
		if(session.getAttribute(Constants.USER_SESSION) == null){
			return "redirect:/user/login.html";
		}
		return "frame";
	}
	
	@RequestMapping(value="/exlogin.html",method=RequestMethod.GET)
	public String exLogin(@RequestParam String userCode,@RequestParam String userPassword){
		logger.debug("exLogin====================================");
		//调用service方法，进行用户匹配
		User user = userService.login(userCode,userPassword);
		if(null == user){//登录失败
			throw new RuntimeException("用户名或者密码不正确！");
		}
		return "redirect:/user/main.html";
	}
	
	/*@ExceptionHandler(value={RuntimeException.class})
	public String handlerException(RuntimeException e,HttpServletRequest req){
		req.setAttribute("exception", e);
		return "error";
	}*/
	
	public Object userCodeIsExit(@RequestParam String userCode) {
		logger.debug("userCodeIsExit  userCode: "+userCode);
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isNullOrEmpty(userCode)) {
			resultMap.put("userCode", "exist");
		}else{
			User user = userService.selectUserCodeExist(userCode);
			if (null != user) {
				resultMap.put("userCode", "exist");
			}else{
				resultMap.put("userCode", "noexist");
			}
			
			
		}
		return JSONArray.toJSONString(resultMap);
	}
}
