/**
 * 项目名：AutomationFrame <br>
 * 包名：com.automation.webframe.web.action.system <br>
 * 文件名：MainAction.java <br>
 * 版本信息：TODO <br>
 * 作者：赵增斌 E-mail：zhaozengbin@gmail.com QQ:4415599 weibo:http://weibo.com/zhaozengbin<br>
 * 日期：2013-6-24-下午2:26:04<br>
 * Copyright (c) 2013 赵增斌-版权所有<br>
 *
 */
package com.automation.webframe.web.action.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.automation.utils.Constant.SuperAdmin;
import com.automation.utils.DateUtils;
import com.automation.utils.MethodUtils;
import com.automation.webframe.annotation.Auth;
import com.automation.webframe.entity.BaseBean.DELETED;
import com.automation.webframe.entity.BaseBean.STATE;
import com.automation.webframe.entity.SysMenu;
import com.automation.webframe.entity.SysMenuBtn;
import com.automation.webframe.entity.SysUser;
import com.automation.webframe.service.SysMenuBtnService;
import com.automation.webframe.service.SysMenuService;
import com.automation.webframe.service.SysUserService;
import com.automation.webframe.utils.HtmlUtils;
import com.automation.webframe.utils.SessionUtils;
import com.automation.webframe.utils.TreeUtils;
import com.automation.webframe.utils.URLUtils;
import com.automation.webframe.web.vo.SiteMainModel;
import com.automation.webframe.web.vo.TreeNode;

/**
 * 
 * 类名称：MainAction <br>
 * 类描述：TODO <br>
 * 创建人：赵增斌 <br>
 * 修改人：赵增斌 <br>
 * 修改时间：2013-6-24 下午2:26:04 <br>
 * 修改备注：TODO <br>
 * 
 */
@Controller
public class MainAction extends BaseAction {

	private final static Logger log = Logger.getLogger(MainAction.class);

	@Autowired(required = false)
	private SysMenuService<SysMenu> sysMenuService;

	@Autowired(required = false)
	private SysUserService<SysUser> sysUserService;

	@Autowired(required = false)
	private SysMenuBtnService<SysMenuBtn> sysMenuBtnService;

	/**
	 * 方法：login <br>
	 * 描述：登录页面 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 下午2:29:22 <br>
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping("/login")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> context = getRootMap();
		return forword("login", context);
	}

	/**
	 * 方法：toLogin <br>
	 * 描述：登录 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 下午2:29:33 <br>
	 * 
	 * @param email
	 *            邮箱登录账号
	 * @param pwd
	 *            密码
	 * @param verifyCode
	 *            验证码
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping("/toLogin")
	public void toLogin(String email, String pwd, String verifyCode,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String vcode = SessionUtils.getValidateCode(request);
		SessionUtils.removeValidateCode(request);// 清除验证码，确保验证码只能用一次
		if (StringUtils.isBlank(verifyCode)) {
			sendFailureMessage(response, "验证码不能为空.");
			return;
		}
		// 判断验证码是否正确
		if (!verifyCode.toLowerCase().equals(vcode)) {
			sendFailureMessage(response, "验证码输入错误.");
			return;
		}
		if (StringUtils.isBlank(email)) {
			sendFailureMessage(response, "账号不能为空.");
			return;
		}
		if (StringUtils.isBlank(pwd)) {
			sendFailureMessage(response, "密码不能为空.");
			return;
		}
		String msg = "用户登录日志:";
		SysUser user = sysUserService.queryLogin(email, MethodUtils.MD5(pwd));
		if (user == null) {
			// 记录错误登录日志
			log.debug(msg + "[" + email + "]" + "账号或者密码输入错误.");
			sendFailureMessage(response, "账号或者密码输入错误.");
			return;
		}
		if (STATE.DISABLE.key == user.getState()) {
			sendFailureMessage(response, "账号已被禁用.");
			return;
		}
		// 登录次数加1 修改登录时间
		int loginCount = 0;
		if (user.getLoginCount() != null) {
			loginCount = user.getLoginCount();
		}
		user.setLoginCount(loginCount + 1);
		user.setLoginTime(DateUtils.getDateByString(""));
		sysUserService.update(user);
		// 设置User到Session
		SessionUtils.setUser(request, user);
		// 记录成功登录日志
		log.debug(msg + "[" + email + "]" + "登录成功");
		sendSuccessMessage(response, "登录成功.");
	}

	/**
	 * 退出登录
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionUtils.removeUser(request);
		response.sendRedirect(URLUtils.get("msUrl") + "/login.shtml");
	}

	/**
	 * 方法：getActionBtn <br>
	 * 描述：获取Action下的按钮 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 下午2:29:51 <br>
	 * 
	 * @param url
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@Auth(verifyURL = false)
	@RequestMapping("/getActionBtn")
	public void getActionBtn(String url, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> actionTypes = new ArrayList<String>();
		// 判断是否超级管理员
		if (SessionUtils.isAdmin(request)) {
			result.put("allType", true);
		} else {
			String menuUrl = URLUtils.getReqUri(url);
			menuUrl = StringUtils.remove(menuUrl, request.getContextPath());
			// 获取权限按钮
			actionTypes = SessionUtils.getMemuBtnListVal(request,
					StringUtils.trim(menuUrl));
			result.put("allType", false);
			result.put("types", actionTypes);
		}
		result.put(SUCCESS, true);
		HtmlUtils.writerJson(response, result);
	}

	/**
	 * 方法：modifyPwd <br>
	 * 描述：修改密码 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 下午2:30:06 <br>
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@Auth(verifyURL = false)
	@RequestMapping("/modifyPwd")
	public void modifyPwd(String oldPwd, String newPwd,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SysUser user = SessionUtils.getUser(request);
		if (user == null) {
			sendFailureMessage(response, "对不起,登录超时.");
			return;
		}
		SysUser bean = sysUserService.queryById(user.getId());
		if (bean.getId() == null || DELETED.YES.key == bean.getDeleted()) {
			sendFailureMessage(response, "对不起,用户不存在.");
			return;
		}
		if (StringUtils.isBlank(newPwd)) {
			sendFailureMessage(response, "密码不能为空.");
			return;
		}
		// 不是超级管理员，匹配旧密码
		if (!MethodUtils.ecompareMD5(oldPwd, bean.getPwd())) {
			sendFailureMessage(response, "旧密码输入不匹配.");
			return;
		}
		bean.setPwd(MethodUtils.MD5(newPwd));
		sysUserService.update(bean);
		sendSuccessMessage(response, "Save success.");
	}

	/**
	 * 方法：main <br>
	 * 描述：ilook 首页 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 下午2:30:16 <br>
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@Auth(verifyURL = false)
	@RequestMapping("/main")
	public ModelAndView main(SiteMainModel model, HttpServletRequest request) {
		Map<String, Object> context = getRootMap();
		SysUser user = SessionUtils.getUser(request);
		List<SysMenu> rootMenus = null;
		List<SysMenu> childMenus = null;
		List<SysMenuBtn> childBtns = null;
		// 超级管理员
		if (user != null && SuperAdmin.YES.key == user.getSuperAdmin()) {
			rootMenus = sysMenuService.getRootMenu(null);// 查询所有根节点
			childMenus = sysMenuService.getChildMenu();// 查询所有子节点
		} else {
			rootMenus = sysMenuService.getRootMenuByUser(user.getId());// 根节点
			childMenus = sysMenuService.getChildMenuByUser(user.getId());// 子节点
			childBtns = sysMenuBtnService.getMenuBtnByUser(user.getId());// 按钮操作
			buildData(childMenus, childBtns, request); // 构建必要的数据
		}
		context.put("user", user);
		context.put("menuList", treeMenu(rootMenus, childMenus));
		return forword("main/main", context);
	}

	/**
	 * 方法：treeMenu <br>
	 * 描述：构建树形数据 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 下午2:30:27 <br>
	 * 
	 * @param rootMenus
	 * @param childMenus
	 * @return
	 */
	private List<TreeNode> treeMenu(List<SysMenu> rootMenus,
			List<SysMenu> childMenus) {
		TreeUtils util = new TreeUtils(rootMenus, childMenus);
		return util.getTreeNode();
	}

	/**
	 * 方法：buildData <br>
	 * 描述：构建树形数据 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 下午2:30:34 <br>
	 * 
	 * @param childMenus
	 * @param childBtns
	 * @param request
	 */
	private void buildData(List<SysMenu> childMenus,
			List<SysMenuBtn> childBtns, HttpServletRequest request) {
		// 能够访问的url列表
		List<String> accessUrls = new ArrayList<String>();
		// 菜单对应的按钮
		Map<String, List> menuBtnMap = new HashMap<String, List>();
		for (SysMenu menu : childMenus) {
			// 判断URL是否为空
			if (StringUtils.isNotBlank(menu.getUrl())) {
				List<String> btnTypes = new ArrayList<String>();
				for (SysMenuBtn btn : childBtns) {
					if (menu.getId().equals(btn.getMenuid())) {
						btnTypes.add(btn.getBtnType());
						URLUtils.getBtnAccessUrls(menu.getUrl(),
								btn.getActionUrls(), accessUrls);
					}
				}
				menuBtnMap.put(menu.getUrl(), btnTypes);
				URLUtils.getBtnAccessUrls(menu.getUrl(), menu.getActions(),
						accessUrls);
				accessUrls.add(menu.getUrl());
			}
		}
		SessionUtils.setAccessUrl(request, accessUrls);// 设置可访问的URL
		SessionUtils.setMemuBtnMap(request, menuBtnMap); // 设置可用的按钮
	}
}
