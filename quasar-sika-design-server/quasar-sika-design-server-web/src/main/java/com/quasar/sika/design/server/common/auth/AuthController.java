package com.quasar.sika.design.server.common.auth;


import com.quasar.sika.design.server.common.auth.context.AuthFindBackPasswordContext;
import com.quasar.sika.design.server.common.auth.context.AuthRegisterContext;
import com.quasar.sika.design.server.common.auth.factory.AuthFactory;
import com.quasar.sika.design.server.common.auth.pojo.request.*;
import com.quasar.sika.design.server.common.auth.pojo.response.OauthResponse;
import com.quasar.sika.design.server.common.auth.service.AuthService;
import com.quasar.sika.design.server.common.captcha.pojo.request.CaptchaCheckRequest;
import com.quasar.sika.design.server.common.captcha.pojo.request.CaptchaGenerateRequest;
import com.quasar.sika.design.server.common.captcha.service.CaptchaService;
import com.quasar.sika.design.server.common.controller.BaseSikaDesignServerController;
import com.quasar.sika.design.server.common.mail.context.CheckMailCodeContext;
import com.quasar.sika.design.server.common.mail.context.SendMailCodeContext;
import com.quasar.sika.design.server.common.shiro.util.ShiroUtils;
import com.sika.code.basic.errorcode.BaseErrorCodeEnum;
import com.sika.code.result.Result;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>
 * 认证 前端控制器
 * </p>
 *
 * @author daiqi
 * @since 2020-12-26 16:59:13
 */
@RestController(value = "authController")
@RequestMapping("auth")
public class AuthController extends BaseSikaDesignServerController {
    @Autowired
    private AuthService authService;
    @Autowired
    private CaptchaService captchaService;

    /** 忘记密码-----begin */
    /**
     * 忘记密码 - 校验邮箱
     */
    @PostMapping("/check_forget_password_email/anon")
    @ResponseBody
    public Result checkForgetPasswordEmail(@RequestBody AuthForgetPasswordRequest request) {
        return success(authService.checkForgetPasswordEmail(request));
    }

    /**
     * 找回密码 - 校验邮箱
     */
    @PostMapping("/find_back_password/anon")
    @ResponseBody
    public Result findBackPassword(@RequestBody AuthFindBackPasswordContext context) {
        return execute(context);
    }
    /** 忘记密码-----end */
    /** 授权登录-----begin */
    /**
     * 用户登录 - 用户名密码
     */
    @PostMapping("/login_username/anon")
    @ResponseBody
    public Result loginUsername(@RequestBody AuthLoginRequest request) {
        return success(authService.login(request));
    }

    /**
     * 用户登录 - 手机号密码
     */
    @PostMapping("/login_phone/anon")
    @ResponseBody
    public Result loginPhone(@RequestBody AuthLoginPhoneRequest request) {
        return success(authService.loginPhone(request));
    }

    @RequestMapping("/get_captcha_verify_code/anon")
    public void getCaptchaVerifyCode(@RequestBody CaptchaGenerateRequest request) {
        captchaService.generateAndWriteCaptchaVerifyCodeToResponse(response, request);
    }

    /**
     * 校验图片验证码
     */
    @RequestMapping("/check_captcha_verify_code/anon")
    public Result checkCaptchaVerifyCode(@RequestBody CaptchaCheckRequest request) {
        return success(captchaService.checkCaptchaVerifyCode(request));
    }

    /**
     * 用户注册 - 校验邮箱验证码
     */
    @PostMapping("/check_mail_code/anon")
    @ResponseBody
    public Result checkMailCode(@RequestBody CheckMailCodeContext context) {
        return execute(context);
    }

    /**
     * 用户注册 - 校验邮箱验证码
     */
    @PostMapping("/send_mail_code/anon")
    @ResponseBody
    public Result sendUserRegisterMailCode(@RequestBody SendMailCodeContext context) {
        return execute(context);
    }

    /**
     * 用户注册 - 校验邮箱
     */
    @PostMapping("/check_register_email/anon")
    @ResponseBody
    public Result checkRegisterEmail(@RequestBody AuthRegisterRequest request) {
        return success(authService.checkRegisterEmail(request));
    }

    /**
     * 用户注册 - 校验手机号
     */
    @PostMapping("/check_register_phone/anon")
    @ResponseBody
    public Result checkRegisterPhone(@RequestBody AuthRegisterRequest request) {
        return success(authService.checkRegisterPhone(request));
    }

    /**
     * 用户注册 - 校验用户名
     */
    @RequestMapping("/check_register_username/anon")
    @ResponseBody
    public Result checkRegisterUsername(@RequestBody AuthRegisterRequest request) {
        return success(authService.checkRegisterUsername(request));
    }

    /**
     * 前端调用接口获取授权URL，并且重定向到该url
     */
    @RequestMapping("/get_authorize_url/{source}/anon")
    @ResponseBody
    public Result getAuthorizeUrl(@PathVariable("source") String source, String clientUrl) {
        return success(authService.getAuthorizeUrl(source, clientUrl));
    }

    /**
     * 授权登录-配置的授权回调链接为前端-前端再ajax调用该接口 在创建github授权应用时的回调地址应为：http://127.0.0.1:8443/oauth/callback/github
     */
    @RequestMapping("/oauth_login/{source}/anon")
    @ResponseBody
    public Result oauthLogin(@PathVariable("source") String source, AuthCallback callback) {
        return success(authService.oauthLogin(source, callback));
    }

    @RequestMapping("/bind_oauth_user/anon")
    public Result bindOauthUser(@RequestBody BindOauthUserRequest request) {
        return success(authService.bindOauthUser(request));
    }

    @RequestMapping("/render/{source}/anon")
    public ModelAndView renderAuth(@PathVariable("source") String source, String clientUrl) throws IOException {
        return redirect(authService.getAuthorizeUrl(source, clientUrl));
    }

    /**
     * oauth平台中配置的授权回调地址，以本项目为例，在创建github授权应用时的回调地址应为：http://127.0.0.1:8443/oauth/callback/github
     */
    @RequestMapping("/callback/{source}/anon")
    public ModelAndView login(@PathVariable("source") String source, AuthCallback callback, HttpServletRequest request) throws IOException {
        OauthResponse authResponse = authService.oauthLogin(source, callback);
        return redirect(authResponse.getClientUrl() + "?" + "oauthToken=" + authResponse.getOauthToken() + "&source=" + authResponse.getSource());
    }

    @RequestMapping("/do_oauth_login/anon")
    public Result doOauthLogin(@RequestBody AuthOauthLoginRequest request) {
        return success(authService.doOauthLogin(request));
    }

    @RequestMapping("/users/anon")
    @ResponseBody
    public Result users() {
        return success(ShiroUtils.getUserInfo());
    }

    @RequestMapping("/revoke/{source}/{uuid}")
    @ResponseBody
    public Result revokeAuth(@PathVariable("source") String source, @PathVariable("uuid") String uuid) throws IOException {
        AuthRequest authRequest = AuthFactory.getAuthRequest(source.toLowerCase());

        AuthUser user = null;
        if (null == user) {
            return resultGenerator.generateResultError("用户未登录");
        }
        AuthResponse<AuthToken> response = null;
        try {
            response = authRequest.revoke(user.getToken());
            if (response.ok()) {
//                map.remove(user.getUuid());
                return success("用户 [" + user.getUsername() + "] 的 授权状态 已收回！");
            }
            return resultGenerator.generateResultError("用户 [" + user.getUsername() + "] 的 授权状态 收回失败！" + response.getMsg());
        } catch (AuthException e) {
            return resultGenerator.generateResultError(e.getErrorMsg());
        }
    }

    @RequestMapping("/refresh/{source}/{uuid}")
    @ResponseBody
    public Object refreshAuth(@PathVariable("source") String source, @PathVariable("uuid") String uuid) {
        AuthRequest authRequest = AuthFactory.getAuthRequest(source.toLowerCase());

        AuthUser user = null;
        if (null == user) {
            return resultGenerator.generateResultError("用户不存在");
        }
        AuthResponse<AuthToken> response = null;
        try {
            response = authRequest.refresh(user.getToken());
            if (response.ok()) {
                user.setToken(response.getData());
//                map.put(user.getUuid(), user);
                return success("用户 [" + user.getUsername() + "] 的 access token 已刷新！新的 accessToken: " + response.getData().getAccessToken());
            }
            return fail("用户 [" + user.getUsername() + "] 的 access token 刷新失败！" + response.getMsg());
        } catch (AuthException e) {
            return fail(e.getErrorMsg());
        }
    }

    /**
     * 授权登录-----end
     */
    @PostMapping("/register/anon")
    public Result register(@RequestBody AuthRegisterContext context) {
        return super.execute(context);
    }

    @PostMapping("/update_current_password")
    public Result updateCurrentPassword(@RequestBody AuthUpdatePasswordRequest request) {
        return super.success(authService.updateCurrentPassword(request));
    }

    @PostMapping("/update_password")
    public Result updatePassword(@RequestBody AuthUpdatePasswordRequest request) {
        return super.success(authService.updatePassword(request));
    }

    @RequestMapping("/current_user")
    public Result currentUser() {
        return super.success(authService.currentUser());
    }


    @RequestMapping("/logout")
    public Result logout() {
        ShiroUtils.logout();
        return success("退出系统成功");
    }

    /**
     * 未登录
     */
    @RequestMapping("/unLogin/anon")
    @ResponseBody
    public Result unLogin() {
        return fail(BaseErrorCodeEnum.UN_AUTH, BaseErrorCodeEnum.UN_AUTH.getDesc());
    }

    /**
     * 未授权
     */
    @RequestMapping("/unAuth/anon")
    public Result unAuth() {
        return resultGenerator.generateResultError("当前用户没有授权");
    }

    /**
     * token过期
     */
    @RequestMapping("/tokenExpired/anon")
    public Result tokenExpired() {
        return resultGenerator.generateResultError("token过期，请重新登录");
    }

    /**
     * 被挤下线
     */
    @RequestMapping("/downline/anon")
    public Result downline() {
        return resultGenerator.generateResultError("您的账号已在其他地方登录，被挤下线，请重新登录！");
    }

}
